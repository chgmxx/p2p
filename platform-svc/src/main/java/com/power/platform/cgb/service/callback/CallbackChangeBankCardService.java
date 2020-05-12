package com.power.platform.cgb.service.callback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.common.config.Global;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.service.CGBPayService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;

@Path("/callbackchange")
@Service("callbackChangeBankCardService")
@Produces(MediaType.APPLICATION_JSON)
public class CallbackChangeBankCardService {

	private static final Logger LOG = LoggerFactory
			.getLogger(CallbackChangeBankCardService.class);
	
	@Autowired
	private CGBPayService cGBPayService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserBankCardDao userBankCardDao;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	
	
	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");
	 
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");
    
    /**
     * 更换银行卡回调
     * @param tm
     * @param data
     * @return
     * @throws IOException
     */
	@POST
	@Path("/changeBankCardWebNotify")
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
				 System.out.println("0000000000000000");
				 //银行卡绑定状态变更
				 String orderId = (String)map.get("orderId");
				 String userId = (String)map.get("userId");
				 String status = (String)map.get("status");
				 LOG.info("更换银行卡订单编号为"+(String)map.get("orderId"));
				 LOG.info("订单号"+orderId+"状态为"+status);
					 if(status.equals("S")){
						 CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userId);
						 if(userBankCard == null){
							 LOG.info("未查询到ID为"+orderId+"的银行卡开户订单");
							 //接收通知成功，通知对方服务器不在发送通知
							 result.put("respCode","00");
							 result.put("respMsg","成功");
							 String jsonString =JSON.toJSONString(result);
							 //对返回对方服务器消息进行加密
							 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
							 //返回参数对方服务器，不在发送请求
							 return result;
						 }
						 String bankName = "";
						 LOG.info("手机号"+(String)map.get("phone")+"bankcardNo"+(String)map.get("bankCardNo")+"bankcode"+(String)map.get("bankCode"));
						 userBankCard.setState(UserBankCard.CERTIFY_YES); 
						 userBankCard.setBankAccountNo((String)map.get("bankCardNo"));
						 userBankCard.setBankCardPhone((String)map.get("phone"));
						 userBankCard.setBankNo((String)map.get("bankCode"));
						 //根据银行卡编码查询银行卡名称
						 CicmorganBankCode code = new CicmorganBankCode();
						 code.setBankCode((String)map.get("bankCode"));
						 List<CicmorganBankCode> list = cicmorganBankCodeService.findList1(code);
						 if(list!=null && list.size()>0){
							 bankName = list.get(0).getBankName();
						 }
						 userBankCard.setBankName(bankName);
						 userBankCard.setUpdateDate(new Date());
						 cgbUserBankCardDao.update(userBankCard);
						 //接收通知成功，通知对方服务器不在发送通知
						 result.put("respCode","00");
						 result.put("respMsg","成功");
						 String jsonString =JSON.toJSONString(result);
						 //对返回对方服务器消息进行加密
						 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						 //返回参数对方服务器，不在发送请求
						 return result;
					 }else if(status.equals("F")){
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
				 System.out.println("333333333333333");
					return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * 企业开户银行回调
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/accountCreateByCompany")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> accountCreateByCompany(@FormParam("tm") String tm,
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
				 System.out.println("0000000000000000");
				 //银行卡绑定状态变更
				 String userId = (String)map.get("userId");
				 String status = (String)map.get("status");
				 String bizType = (String)map.get("bizType");
				 LOG.info("用户编码"+userId+"状态为"+status);
					 if(status.equals("S")){
						 //更新借款端银行卡账户状态
						 CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId1(userId);
						 if(userBankCard == null){
							 LOG.info("未查询到用户ID为"+userId+"的银行卡开户订单");
							 //接收通知成功，通知对方服务器不在发送通知
							 result.put("respCode","00");
							 result.put("respMsg","成功");
							 String jsonString =JSON.toJSONString(result);
							 //对返回对方服务器消息进行加密
							 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
							 //返回参数对方服务器，不在发送请求
							 return result;
						 }
						 userBankCard.setState(UserBankCard.CERTIFY_YES); 
						 userBankCard.setUpdateDate(new Date());
//						 cgbUserBankCardService.save(userBankCard);
						 cgbUserBankCardDao.update(userBankCard);
						 //接收通知成功，通知对方服务器不在发送通知
						 result.put("respCode","00");
						 result.put("respMsg","成功");
						 String jsonString =JSON.toJSONString(result);
						 //对返回对方服务器消息进行加密
						 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						 //返回参数对方服务器，不在发送请求
						 return result;
					 }else if(status.equals("F")){
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
				 System.out.println("333333333333333");
					return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	
    /**
     * 更换预留手机号回调
     * @param tm
     * @param data
     * @return
     * @throws IOException
     */
	@POST
	@Path("/changeBankPhoneWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> notifychangeBankPhone(@FormParam("tm") String tm,
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
				 System.out.println("0000000000000000");
				 //银行卡绑定状态变更
				 String orderId = (String)map.get("orderId");
				 String userId = (String)map.get("userId");
				 String status = (String)map.get("status");
				 LOG.info("更换银行卡订单编号为"+(String)map.get("orderId"));
				 LOG.info("订单号"+orderId+"状态为"+status);
					 if(status.equals("S")){
						 CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userId);
						 if(userBankCard == null){
							 LOG.info("未查询到ID为"+orderId+"的银行卡开户订单");
							 //接收通知成功，通知对方服务器不在发送通知
							 result.put("respCode","00");
							 result.put("respMsg","成功");
							 String jsonString =JSON.toJSONString(result);
							 //对返回对方服务器消息进行加密
							 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
							 //返回参数对方服务器，不在发送请求
							 return result;
						 }
						 LOG.info("新手机号"+(String)map.get("phone"));
						 userBankCard.setBankCardPhone((String)map.get("phone"));
						 userBankCard.setUpdateDate(new Date());
						 cgbUserBankCardDao.update(userBankCard);
						 //接收通知成功，通知对方服务器不在发送通知
						 result.put("respCode","00");
						 result.put("respMsg","成功");
						 String jsonString =JSON.toJSONString(result);
						 //对返回对方服务器消息进行加密
						 result =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						 //返回参数对方服务器，不在发送请求
						 return result;
					 }else if(status.equals("F")){
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
				 System.out.println("333333333333333");
					return result;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
