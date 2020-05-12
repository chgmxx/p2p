package com.power.platform.lanmao.asyncmsg.rechain.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IdcardUtils;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.asyncmsg.rechain.IResponsibility;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteRecordDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.CgbBigrechargeWhiteRecord;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 个人绑卡
 */
public class PersonalBindBankCardExpand implements IResponsibility{

	private static final Logger log = LoggerFactory.getLogger(PersonalBindBankCardExpand.class);
	private UserInfoDao userInfoDao;

	private CgbUserAccountDao cgbUserAccountDao;

	private CgbUserBankCardDao cgbUserBankCardDao;

	private LmTransactionDao lmTransactionDao;
	
	private CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao;
	
	private LanMaoWhiteListAddDataService whiteListAddDataService;
	
	public PersonalBindBankCardExpand(UserInfoDao userInfoDao, 
							CgbUserAccountDao cgbUserAccountDao,
							CgbUserBankCardDao cgbUserBankCardDao,
							LmTransactionDao lmTransactionDao,
							CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao,
							LanMaoWhiteListAddDataService whiteListAddDataService) {
		this.userInfoDao = userInfoDao;
		this.cgbUserAccountDao = cgbUserAccountDao; 
		this.cgbUserBankCardDao = cgbUserBankCardDao;
		this.lmTransactionDao = lmTransactionDao;
		this.cgbBigrechargeWhiteRecordDao = cgbBigrechargeWhiteRecordDao;
		this.whiteListAddDataService = whiteListAddDataService;
	}
	@Override
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		if (input != null && ServiceNameEnum.PERSONAL_BIND_BANKCARD_EXPAND.getValue().equals(input.getServiceName().toUpperCase())) {
			try{
				log.info("个人绑卡---------异步通知开始");
				// TODO do something
				JSONObject json = JSONObject.parseObject(input.getRespData());
				String userId = json.getString("platformUserNo");
				String orderId = json.getString("requestNo");
				if("SUCCESS".equals(json.getString("status"))) {
					
					UserInfo userInfo = userInfoDao.getCgb(userId);
					if(null==userInfo) {
						log.info("用户不存在-----userId="+userId+"------异步通知");
						return;
					}
					if("2".equals(userInfo.getCgbBindBankCardState())) {
						//说明回调已经做处理了。
						log.info("说明已经执行，不需要再异步通知");
						return;
					}
					
					String bankAccountNo = null;
					CgbBigrechargeWhiteRecord whiteRecord = new CgbBigrechargeWhiteRecord();
					whiteRecord.setRequestNo(orderId);
					List<CgbBigrechargeWhiteRecord> whiteRecordList = cgbBigrechargeWhiteRecordDao.findList(whiteRecord);
					if(whiteRecordList!=null && whiteRecordList.size() != 0) {
						CgbBigrechargeWhiteRecord white = whiteRecordList.get(0);
						bankAccountNo = white.getBankNo();
					}
					
					CgbUserBankCard userBankCard = new CgbUserBankCard();
					userBankCard.setUserId(userInfo.getId());
					List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
					if (list != null && list.size()!=0) {
						// N2.唯一的银行卡
						CgbUserBankCard cbc = list.get(0);
//						if(UserBankCard.CERTIFY_YES.equals(cbc.getState())) {
//							//表示已经绑卡成功，--解决异步通知多次发送
//							log.info("说明已经执行，不需要再异步通知-----state="+cbc.getState()+"------异步通知");
//							return;
//						}
//						log.info("说明未执行，走异步通知-----state="+cbc.getState()+"------异步通知");
						cbc.setAccountId(userInfo.getAccountId());
						cbc.setUserId(userInfo.getId());
						if(bankAccountNo!=null&&!"".equals(bankAccountNo)) {
							cbc.setBankAccountNo(bankAccountNo);
						}
						cbc.setBankCardSign(json.getString("bankcardNo"));
						cbc.setBankCardPhone(json.getString("mobile"));
						cbc.setBankNo(json.getString("bankcode").toString());
						cbc.setBankName(BankCodeEnum.getTextByValue(json.getString("bankcode")));
						cbc.setBeginBindDate(new Date());
						cbc.setState(CgbUserBankCard.CERTIFY_YES);
						cbc.setBindDate(new Date());
						cbc.setUpdateDate(new Date());
						int i = cgbUserBankCardDao.update(cbc);
						log.info("银行卡信息更新:{}", i == 1 ? "成功" : "失败");
					}
					// N3.用户信息保存
//					userInfo.setRealName(json.getString("realName"));// 真实姓名
//					userInfo.setCertificateNo(json.getString("idCardNo"));// 身份证号
//					userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);
					userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_YES);
					userInfo.setBindBankCardState(UserInfo.BIND_CARD_YES);
//					String birthday = IdcardUtils.getBirthByIdCard(json.getString("idCardNo"));
//					userInfo.setBirthday(birthday.substring(4, birthday.length())); // 客户生日设置.
					int j = userInfoDao.update(userInfo);
					log.info("用户信息更新:{}",j == 1 ? "成功" : "失败");
					// 针对老客户需要重新开户 新增账户表数据
					if (cgbUserAccountDao.getUserAccountInfo(userInfo.getId()) == null) {
						// 生成客户账户
						CgbUserAccount userAccountInfo = new CgbUserAccount();
						System.out.println("用户注册插入客户表完成");
						userAccountInfo.setId(userInfo.getAccountId());
						userAccountInfo.setUserId(userInfo.getId());
						userAccountInfo.setTotalAmount(0d);
						userAccountInfo.setTotalInterest(0d);
						userAccountInfo.setAvailableAmount(0d);
						userAccountInfo.setFreezeAmount(0d);
						userAccountInfo.setRechargeAmount(0d);
						userAccountInfo.setRechargeCount(0);
						userAccountInfo.setCashAmount(0d);
						userAccountInfo.setCashCount(0);
						userAccountInfo.setCurrentAmount(0d);
						userAccountInfo.setRegularDuePrincipal(0d);
						userAccountInfo.setRegularDueInterest(0d);
						userAccountInfo.setRegularTotalAmount(0d);
						userAccountInfo.setRegularTotalInterest(0d);
						userAccountInfo.setCurrentTotalAmount(0d);
						userAccountInfo.setCurrentTotalInterest(0d);
						userAccountInfo.setCurrentYesterdayInterest(0d);
						userAccountInfo.setReguarYesterdayInterest(0d);
						// 同时生成客户账户
						cgbUserAccountDao.insert(userAccountInfo);
					}
					LmTransaction lm = new LmTransaction();
					lm.setServiceName(ServiceNameEnum.PERSONAL_BIND_BANKCARD_EXPAND.getValue());
					lm.setPlatformUserNo(userId);
					lm.setRequestNo(orderId);
					lm.setId(IdGen.uuid());
					lm.setCreateDate(new Date());
					lm.setUpdateDate(new Date());
					lm.setCode(json.getString("code"));
					lm.setStatus(json.getString("status"));
						//鉴权通过类型
					lm.setAccessType(json.getString("accessType"));
					lm.setErrorCode(json.getString("errorCode"));
					lm.setErrorMessage(json.getString("errorMessage"));
					int m = lmTransactionDao.insert(lm);
					log.info("个人绑卡流水号插入:{}", m == 1 ? "成功" : "失败");
					
					
					/**
					 * @author fuwei
					 *         添加白名单
					 */
					LanMaoWhiteList whiteList = new LanMaoWhiteList();
					whiteList.setRequestNo(json.getString("requestNo"));
					whiteList.setPlatformUserNo(json.getString("platformUserNo"));
					if(list.size()!=0) {
						//银行卡号明文
						whiteList.setBankcardNo(list.get(0).getBankAccountNo());
					}
					whiteList.setUserRole(UserRoleEnum.INVESTOR.getValue());
					Map<String, Object> result = whiteListAddDataService.whiteListAdd(whiteList,orderId);
					if (result.get("code").equals("调用成功") && result.get("status").equals("处理成功")) {
						log.info("添加白名单成功");
					} else {
						log.info("添加白名单失败:" + result.get("errorMessage"));
					}
					// 鉴权通过类型---1.四要素鉴权认证通过 2.未鉴权 3.特殊用户认证 4.企业用户认证 
//					String accessType = json.getString("accessType");
					
				}else {
					LmTransaction lm = new LmTransaction();
					lm.setServiceName(ServiceNameEnum.PERSONAL_BIND_BANKCARD_EXPAND.getValue());
					lm.setPlatformUserNo(userId);
					lm.setRequestNo(orderId);
					lm.setId(IdGen.uuid());
					lm.setCreateDate(new Date());
					lm.setUpdateDate(new Date());
					lm.setCode(json.getString("code"));
					lm.setStatus(json.getString("status"));
					//鉴权通过类型
					lm.setAccessType(json.getString("accessType"));
					lm.setErrorCode(json.getString("errorCode"));
					lm.setErrorMessage(json.getString("errorMessage"));
					int m = lmTransactionDao.insert(lm);
					log.info("个人绑卡流水号插入:{}", m == 1 ? "成功" : "失败");
				}
				log.info("个人绑卡完成------异步通知");
				System.out.println("个人绑卡ok，  ..... ");
				return;
			}catch(Exception e){
				throw new NotifyException("个人绑卡异常------异步通知");
			}
		}
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}