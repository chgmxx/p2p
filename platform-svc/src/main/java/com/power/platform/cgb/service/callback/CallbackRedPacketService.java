package com.power.platform.cgb.service.callback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.ZtmgWechatReturningCashDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.ZtmgWechatReturningCash;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.ZtmgWechatReturningCashService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 返利回调API
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/callbackredpacket")
@Service("callbackRedPacketService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackRedPacketService {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(CallbackRedPacketService.class);
	
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private ZtmgWechatReturningCashService ztmgWechatReturningCashService;
	@Autowired
	private ZtmgWechatReturningCashDao ztmgWechatReturningCashDao;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	

	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");
	 
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");
	
	/**
	 * 返利回调接口---erp
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/redPacketWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> notify(@FormParam("tm") String tm,
			@FormParam("data") String data)
			throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String,String>();

		try {
			//对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data,
					merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet,
					new TypeReference<Map<String, String>>() {
					});
			String signRet = (String) map.get("signature");
			map.remove("signature");
			
			//校验验密
			boolean verifyRet = APIUtils.verify(
					merchantRsaPublicKey, signRet, map, "RSA");
			//验密成功，进行业务处理
			if (verifyRet) {
				 String orderId = (String)map.get("orderId");
				 String status = (String)map.get("status");
				 LOG.info("返利订单编号为"+orderId);
				 LOG.info("订单状态为"+status);
				 if(status.equals("S")){
					 //根据订单号查询返现
					 ZtmgWechatReturningCash returnCash = ztmgWechatReturningCashService.get(orderId);
					 if(returnCash!=null){
						 Double amount = returnCash.getPayAmount();
						 //N1.更新返现订单状态
						 returnCash.setState(ZtmgWechatReturningCash.STATE_SUCCESS);
						 int i = ztmgWechatReturningCashDao.update(returnCash);
						 if(i>0){
							 LOG.info("返利订单:"+orderId+"状态更新为[成功]");
							 //更新用户账户
							 CgbUserAccount accountInfo = cgbUserAccountDao.getUserAccountInfo(returnCash.getUser_id());
								if (accountInfo != null) {
									LOG.info("用户【" + returnCash.getUser_id() + "】返现前账户金额为" + accountInfo.getTotalAmount());
									LOG.info("用户【" + returnCash.getUser_id() + "】返现前可用金额为" + accountInfo.getAvailableAmount());
									accountInfo.setTotalAmount(NumberUtils.scaleDouble(accountInfo.getTotalAmount() + amount));
									accountInfo.setAvailableAmount(NumberUtils.scaleDouble(accountInfo.getAvailableAmount() + amount));
									LOG.info("用户【" + returnCash.getUser_id() + "】返现后账户金额为" + accountInfo.getTotalAmount());
									LOG.info("用户【" + returnCash.getUser_id() + "】返现后可用金额为" + accountInfo.getAvailableAmount());
									int j = cgbUserAccountDao.update(accountInfo);
									if (j > 0) {
										// 保存客户流水记录
										LOG.info(this.getClass().getName() + "——————保存客户投资流水记录开始");
										CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
										userTransDetail.setId(IdGen.uuid()); // 主键ID.
										userTransDetail.setTransId(returnCash.getId()); // 客户返现记录ID.
										userTransDetail.setUserId(returnCash.getUser_id()); // 客户账号ID.
										userTransDetail.setAccountId(accountInfo.getId()); // 客户账户ID.
										userTransDetail.setTransDate(new Date()); // 交易时间.
										userTransDetail.setTrustType(UserTransDetailService.trust_type7); // 活动返现.
										userTransDetail.setAmount(amount); // 返现金额.
										userTransDetail.setAvaliableAmount(accountInfo.getAvailableAmount()); // 当前可用余额.
										userTransDetail.setInOutType(UserTransDetailService.in_type); // 投资支出.
										userTransDetail.setRemarks("连连账户迁移"); // 备注信息.
										userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
										int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
										if (userTransDetailFlag > 0) {
											LOG.info("用户【" + returnCash.getUser_id() + "】插入交易流水成功");
										}
									}
								}
								//接收通知成功，通知对方服务器不在发送通知
								 result.put("respCode","00");
								 result.put("respMsg","成功");
								 String jsonString =JSON.toJSONString(result);
								 //对返回对方服务器消息进行加密
								 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
								 //返回参数对方服务器，不在发送请求
								 return result;
						 }
					 }else{
						 LOG.info("未查询到订单号为"+orderId+"交易流水");
						 //接收通知成功，通知对方服务器不在发送通知
						 result.put("respCode","00");
						 result.put("respMsg","成功");
						 String jsonString =JSON.toJSONString(result);
						 //对返回对方服务器消息进行加密
						 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						 //返回参数对方服务器，不在发送请求
						 return result;
					 }
				 }else if(status.equals("F")){
					 LOG.info("返现状态为失败");
					 //根据订单号查询返现
					 ZtmgWechatReturningCash returnCash = ztmgWechatReturningCashService.get(orderId);
					 if(returnCash!=null){
						 returnCash.setState(ZtmgWechatReturningCash.STATE_FAIL);
						 int i = ztmgWechatReturningCashDao.update(returnCash); 
						 if(i>0){
							 LOG.info("返利订单:"+orderId+"状态更新为[失败]");
						 }
					 }
					 //接收通知成功，通知对方服务器不在发送通知
					 result.put("respCode","00");
					 result.put("respMsg","成功");
					 String jsonString =JSON.toJSONString(result);
					 //对返回对方服务器消息进行加密
					 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					 //返回参数对方服务器，不在发送请求
					 return result;
				 }
			}else {
					return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 返利回调接口---web
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/redPacketWebNotify2_2_1")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> notify2_2_1(@FormParam("tm") String tm,
			@FormParam("data") String data)
			throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String,String>();

		try {
			//对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data,
					merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet,
					new TypeReference<Map<String, String>>() {
					});
			String signRet = (String) map.get("signature");
			map.remove("signature");
			
			//校验验密
			boolean verifyRet = APIUtils.verify(
					merchantRsaPublicKey, signRet, map, "RSA");
			//验密成功，进行业务处理
			if (verifyRet) {
				 String orderId = (String)map.get("orderId");
				 String status = (String)map.get("status");
				 LOG.info("返利订单编号为"+orderId);
				 LOG.info("订单状态为"+status);
				 String accKey = "";
				 String lockAccValue = "";//账户锁
				 if(status.equals("S")){
					 //根据订单号查询抵用券
					 AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(orderId);
					 if(aUserAwardsHistory!=null){
						 if(aUserAwardsHistory.getValue()!=null){
							 Double voucherAmount = Double.valueOf(aUserAwardsHistory.getValue()); 
							 LOG.info("订单"+orderId+"抵用券金额为"+voucherAmount+"元");
							 UserInfo user = userInfoDao.getCgb(aUserAwardsHistory.getUserId());
							 if(user!=null){
								 CgbUserAccount userAccount = cgbUserAccountDao.get(user.getAccountId());
								 if(userAccount!=null){
										    LOG.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录开始");
											CgbUserTransDetail userTransDetail = cgbUserTransDetailDao.getByTransId(orderId);
											if(userTransDetail!=null){
												userTransDetail.setState(UserTransDetailService.tran_type2);
												int userVoucherDetailFlag = cgbUserTransDetailDao.update(userTransDetail);
												LOG.info("更新状态结果==="+userVoucherDetailFlag);
												if (userVoucherDetailFlag == 1) {
													LOG.info(this.getClass().getName() + "——————客户使用抵用券流水记录状态更新成功");
												}
											}else{
												LOG.info("未查询到返利订单"+orderId);
											}
								 }
							 }
						 }
						 //接收通知成功，通知对方服务器不在发送通知
						 result.put("respCode","00");
						 result.put("respMsg","成功");
						 String jsonString =JSON.toJSONString(result);
						 //对返回对方服务器消息进行加密
						 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						 //返回参数对方服务器，不在发送请求
						 return result; 
					 }else{
						 LOG.info("未查询到订单号为"+orderId+"抵用券记录");
						 //接收通知成功，通知对方服务器不在发送通知
						 result.put("respCode","00");
						 result.put("respMsg","成功");
						 String jsonString =JSON.toJSONString(result);
						 //对返回对方服务器消息进行加密
						 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						 //返回参数对方服务器，不在发送请求
						 return result;
					 }
				 }else if(status.equals("F")){
					 LOG.info("抵用券返利状态为失败");
					 //根据订单号查询抵用券
					 AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(orderId);
					 if(aUserAwardsHistory!=null){
						    Double voucherAmount = Double.valueOf(aUserAwardsHistory.getValue()); 
							//删除原来已使用的抵用券
							int delUserOldVoucher = aUserAwardsHistoryDao.deleteBy(aUserAwardsHistory.getId());
							if(delUserOldVoucher>0){
								LOG.info("[抵用券]原抵用券已删除");
							}
							AUserAwardsHistory newUserVoucher = new AUserAwardsHistory();
							newUserVoucher.setId(String.valueOf(IdGen.randomLong()));
							newUserVoucher.setAwardId(aUserAwardsHistory.getAwardId());
							newUserVoucher.setCreateDate(aUserAwardsHistory.getCreateDate());
							newUserVoucher.setUserId(aUserAwardsHistory.getUserId());
							newUserVoucher.setOverdueDate(aUserAwardsHistory.getOverdueDate());
							newUserVoucher.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
							newUserVoucher.setType("1");// 类型为:抵用劵
							newUserVoucher.setValue(aUserAwardsHistory.getValue());
							newUserVoucher.setSpans(aUserAwardsHistory.getSpans());
							newUserVoucher.setRemark(aUserAwardsHistory.getRemark());
							//新增一张等面值的抵用券
							int newUserVoucherReturn = aUserAwardsHistoryDao.insert(newUserVoucher);
							if(newUserVoucherReturn>0){
								LOG.info("[抵用券]新抵用券已添加");
							}
							//删除原来处理中的交易流水
							CgbUserTransDetail userTransDetail = cgbUserTransDetailDao.getByTransId(orderId);
							if(userTransDetail!=null){
								LOG.info("删除处理中的交易流水["+orderId+"]");
								userTransDetail.setState(UserTransDetailService.tran_type3);
								int i = cgbUserTransDetailDao.update(userTransDetail);
								if(i>0){
									LOG.info("删除处理中的交易流水成功");
								}
							}
							UserInfo user = userInfoDao.getCgb(aUserAwardsHistory.getUserId());
							if(user!=null){
								CgbUserAccount userAccount = cgbUserAccountDao.get(user.getAccountId());
								accKey = "ACC"+userAccount.getId();
								lockAccValue = JedisUtils.lockWithTimeout(accKey, 1000, 1000);//账户锁
								if(lockAccValue!=null && !lockAccValue.equals("")){
									LOG.info("账户余额变更开始====>>>");
									LOG.info("账户总额变更前为"+userAccount.getTotalAmount()+"可用余额变更前为"+userAccount.getAvailableAmount());
									userAccount.setTotalAmount(userAccount.getTotalAmount() - voucherAmount); // 账户总额
									userAccount.setAvailableAmount(userAccount.getAvailableAmount() - voucherAmount);// 可用余额
									int updateAccount = cgbUserAccountService.updateUserAccountInfo(userAccount);
									if (updateAccount == 1) {
										LOG.info(this.getClass().getName() + "——————更改账户信息成功");
										if(JedisUtils.releaseLock(accKey, lockAccValue)){
											LOG.info("账户锁已释放");
											LOG.info("=======账户已解锁====="+System.currentTimeMillis());
										}
									}
								}
							}
		
					 }
					 //接收通知成功，通知对方服务器不在发送通知
					 result.put("respCode","00");
					 result.put("respMsg","成功");
					 String jsonString =JSON.toJSONString(result);
					 //对返回对方服务器消息进行加密
					 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					 //返回参数对方服务器，不在发送请求
					 return result;
				 }
			}else {
					return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
