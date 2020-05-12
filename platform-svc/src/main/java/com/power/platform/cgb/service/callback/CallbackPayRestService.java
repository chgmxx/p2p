package com.power.platform.cgb.service.callback;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IdcardUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
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

@Path("/callback")
@Service("callbackRestService")
@Produces(MediaType.APPLICATION_JSON)
public class CallbackPayRestService {

	private static final Logger LOG = LoggerFactory.getLogger(CallbackPayRestService.class);

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
	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;

	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	//个人绑卡注册回调
	@POST
	@Path("/accountCreateWebNotify")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> notify(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();

		try {
			// 对通知数据进行解密
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String signRet = (String) map.get("signature");
			map.remove("signature");

			// 校验验密
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signRet, map, "RSA");
			// 验密成功，进行业务处理
			if (verifyRet) {
				System.out.println("0000000000000000");
				// 银行卡绑定状态变更
				String orderId = (String) map.get("orderId");
				String status = (String) map.get("status");
				String bizType = (String) map.get("bizType");
				LOG.info("银行卡订单编号为" + (String) map.get("orderId"));
				LOG.info("订单号" + orderId + "状态为" + status);
				if (status.equals("S")) {
					CgbUserBankCard userBankCard = cgbUserBankCardService.getInfoById(orderId);
					if (userBankCard == null) {
						LOG.info("未查询到ID为" + orderId + "的银行卡开户订单");
						// 接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					}
					LOG.info("手机号" + (String) map.get("phone") + "bankcardNo" + (String) map.get("bankCardNo") + "bankcode" + (String) map.get("bankCode") + "用户类型" + bizType);
					userBankCard.setState(UserBankCard.CERTIFY_YES);
					userBankCard.setBankAccountNo((String) map.get("bankCardNo"));
					userBankCard.setBankCardPhone((String) map.get("phone"));
					userBankCard.setBankNo((String) map.get("bankCode"));
					userBankCard.setBankName((String) map.get("bankName"));
					userBankCard.setUpdateDate(new Date());
					cgbUserBankCardService.save(userBankCard);
					if (bizType.equals("01")) {
						LOG.info("投资用户");
						UserInfo user = userInfoService.get(userBankCard.getUserId());
						if (user == null) {
							user = userInfoService.getCgb(userBankCard.getUserId());
						}
						if (user != null) {
							user.setRealName((String) map.get("realName"));
							user.setCgbBindBankCardState(2);
							String birthday = IdcardUtils.getBirthByIdCard(user.getCertificateNo());
							user.setBirthday(birthday.substring(4, birthday.length())); // 客户生日设置.
							userInfoDao.update(user);
						}
					}
					CreditUserInfo creditUserInfo = creditUserInfoService.get(userBankCard.getUserId());
					if (creditUserInfo != null) {
						LOG.info("借款用户信息更新");
						creditUserInfo.setCertificateNo((String) map.get("certNo"));
						creditUserInfo.setName((String) map.get("realName"));
						creditUserInfoService.save(creditUserInfo);
					}
					// 接收通知成功，通知对方服务器不在发送通知
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					// 对返回对方服务器消息进行加密
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// 返回参数对方服务器，不在发送请求
					return result;
				} else if (status.equals("F")) {
					CgbUserBankCard userBankCard = cgbUserBankCardService.getInfoById(orderId);
					if (userBankCard == null) {
						LOG.info("未查询到ID为" + orderId + "的银行卡开户订单");
						// 接收通知成功，通知对方服务器不在发送通知
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					}
					userBankCard.setState(UserBankCard.CERTIFY_NO);
					userBankCard.setBankAccountNo((String) map.get("bankCardNo"));
					userBankCard.setBankCardPhone((String) map.get("phone"));
					userBankCard.setBankNo((String) map.get("bankCode"));
					userBankCard.setBankName((String) map.get("bankName"));
					userBankCard.setUpdateDate(new Date());
					cgbUserBankCardService.save(userBankCard);
					// 接收通知成功，通知对方服务器不在发送通知
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					// 对返回对方服务器消息进行加密
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// 返回参数对方服务器，不在发送请求
					return result;
				}

			} else {
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
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/accountCreateByCompany")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> accountCreateByCompany(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();

		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String signRet = (String) map.get("signature");
			map.remove("signature");

			// 校验验密.
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signRet, map, "RSA");
			// 验密成功，进行业务处理.
			if (verifyRet) {
				LOG.info(this.getClass() + "fn:accountCreateByCompany，验密成功");
				// 银行卡绑定状态变更
				String userId = (String) map.get("userId");
				String status = (String) map.get("status");
				String bizType = (String) map.get("bizType");
				LOG.info("用户编码：" + userId + "，状态：" + status + "，用户类型（01：投资用户，02：借款用户）：" + bizType);
				if (status.equals("S")) {
					CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
					cgbUserBankCard.setUserId(userId);
					// 更新借款端银行卡账户状态
					CgbUserBankCard userBankCard = cgbUserBankCardDao.getUserBankCardByCreditUserIdAndState(cgbUserBankCard);
					if (userBankCard != null) {
						userBankCard.setState(CgbUserBankCard.CERTIFY_YES);
						userBankCard.setUpdateDate(new Date());
						int updateFlag = cgbUserBankCardDao.update(userBankCard);
						if (updateFlag == 1) { // 开户成功，银行卡已认证.
							LOG.info("fn:accountCreateByCompany：\t开户成功，银行卡更新成功");
							ZtmgLoanBasicInfo ztmgLoanBasicInfo = new ZtmgLoanBasicInfo();
							ztmgLoanBasicInfo.setCreditUserId(userId);
							// 根据借款人查询借款人基本信息.
							ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
							if (ztmgLoanBasicInfoEntity != null) { // 更新基本信息
								// 融资主体.
								WloanSubject wloanSubject = new WloanSubject();
								wloanSubject.setLoanApplyId(userId);
								List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
								if (wloanSubjects != null) {
									if (wloanSubjects.size() > 0) {
										wloanSubject = wloanSubjects.get(0);
										ztmgLoanBasicInfoEntity.setCompanyName(wloanSubject.getCompanyName()); // 公司名称.
										ztmgLoanBasicInfoEntity.setOperName(wloanSubject.getLoanUser()); // 公司法定代表人.
										ztmgLoanBasicInfoEntity.setRegisteredAddress(wloanSubject.getRegistAddress()); // 公司注册地址.
										int ztmgLoanBasicInfoEntityUpdate = ztmgLoanBasicInfoDao.update(ztmgLoanBasicInfoEntity);
										if (ztmgLoanBasicInfoEntityUpdate == 1) {
											LOG.info("fn:accountCreateByCompany：\t开户成功，基本信息更新成功");
											// 更新借款人帐号信息，是否完善基本信息字段.
											CreditUserInfo creditUserInfo = creditUserInfoService.get(userId);
											creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2);
											int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
											if (creditUserInfoUpdateFlag == 1) {
												LOG.info("fn:accountCreateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息成功");
												// 接收通知成功，通知对方服务器不在发送通知.
												result.put("respCode", "00");
												result.put("respMsg", "成功");
												String jsonString = JSON.toJSONString(result);
												// 对返回对方服务器消息进行加密.
												result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
												// 返回参数对方服务器，不在发送请求.
												return result;
											} else {
												LOG.info("fn:accountCreateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息失败");
											}
										} else {
											LOG.info("fn:accountCreateByCompany：\t开户成功，基本信息更新失败");
										}
									}
								}
							} else {
								// 融资主体.
								WloanSubject wloanSubject = new WloanSubject();
								wloanSubject.setLoanApplyId(userId);
								List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
								if (wloanSubjects != null) {
									if (wloanSubjects.size() > 0) {
										wloanSubject = wloanSubjects.get(0);
										ztmgLoanBasicInfo.setId(IdGen.uuid()); // 主键.
										ztmgLoanBasicInfo.setCompanyName(wloanSubject.getCompanyName()); // 公司名称.
										ztmgLoanBasicInfo.setOperName(wloanSubject.getLoanUser()); // 公司法定代表人.
										ztmgLoanBasicInfo.setRegisteredAddress(wloanSubject.getRegistAddress()); // 公司注册地址.
										int insertFlag_1 = ztmgLoanBasicInfoDao.insert(ztmgLoanBasicInfo);
										if (insertFlag_1 == 1) {
											LOG.info("fn:accountCreateByCompany：\t开户成功，新增基本信息成功");
											// 更新借款人帐号信息，是否完善基本信息字段.
											CreditUserInfo creditUserInfo = creditUserInfoService.get(userId);
											creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2);
											int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
											if (creditUserInfoUpdateFlag == 1) {
												LOG.info("fn:accountCreateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息成功");
												// 接收通知成功，通知对方服务器不在发送通知.
												result.put("respCode", "00");
												result.put("respMsg", "成功");
												String jsonString = JSON.toJSONString(result);
												// 对返回对方服务器消息进行加密.
												result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
												// 返回参数对方服务器，不在发送请求.
												return result;
											} else {
												LOG.info("fn:accountCreateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息失败");
											}
										} else {
											LOG.info("fn:accountCreateByCompany：\t开户成功，新增基本信息失败");
										}
									}
								}
							}
						} else {
							LOG.info("fn:accountCreateByCompany：\t开户成功，银行卡更新失败");
						}
					}

					// 接收通知成功，通知对方服务器不在发送通知.
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					// 对返回对方服务器消息进行加密.
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// 返回参数对方服务器，不在发送请求.
					return result;
				} else if (status.equals("F")) { // 审核失败
					CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
					cgbUserBankCard.setUserId(userId);
					// 更新借款端银行卡账户状态
					CgbUserBankCard userBankCard = cgbUserBankCardDao.getUserBankCardByCreditUserIdAndState(cgbUserBankCard);
					if (userBankCard != null) {
						userBankCard.setState(CgbUserBankCard.CERTIFY_FAIL);
						userBankCard.setUpdateDate(new Date());
						int updateFlag = cgbUserBankCardDao.update(userBankCard);
						if (updateFlag == 1) { // 开户失败，银行卡未认证.
							LOG.info("fn:accountCreateByCompany：\t开户失败，银行卡更新成功");
						} else {
							LOG.info("fn:accountCreateByCompany：\t开户失败，银行卡更新失败");
						}
					}

					// 接收通知成功，通知对方服务器不在发送通知
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					// 对返回对方服务器消息进行加密
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// 返回参数对方服务器，不在发送请求
					return result;
				}
			} else {
				LOG.info(this.getClass() + "fn:accountCreateByCompany，验密失败，联系开发人员");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 企业信息修改回调
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/accountUpdateByCompany")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> accountUpdateByCompany(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();

		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});
			String signRet = (String) map.get("signature");
			map.remove("signature");

			// 校验验密.
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signRet, map, "RSA");
			// 验密成功，进行业务处理.
			if (verifyRet) {
				LOG.info(this.getClass() + "fn:accountUpdateByCompany，验密成功");
				// 银行卡绑定状态变更.
				String userId = (String) map.get("userId");
				String status = (String) map.get("status");
				LOG.info("用户编码:" + userId + "，状态:" + status);
				if (status.equals("S")) {
					CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
					cgbUserBankCard.setUserId(userId);
					// 更新借款端银行卡账户状态
					CgbUserBankCard userBankCard = cgbUserBankCardDao.getUserBankCardByCreditUserIdAndState(cgbUserBankCard);
					if (userBankCard != null) {
						userBankCard.setState(UserBankCard.CERTIFY_YES);
						userBankCard.setUpdateDate(new Date());
						int updateFlag = cgbUserBankCardDao.update(userBankCard);
						if (updateFlag == 1) { // 开户成功，银行卡已认证.
							LOG.info("fn:accountUpdateByCompany：\t开户成功，银行卡更新成功");
							ZtmgLoanBasicInfo ztmgLoanBasicInfo = new ZtmgLoanBasicInfo();
							ztmgLoanBasicInfo.setCreditUserId(userId);
							// 根据借款人查询借款人基本信息.
							ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
							if (ztmgLoanBasicInfoEntity != null) { // 更新基本信息
								// 融资主体.
								WloanSubject wloanSubject = new WloanSubject();
								wloanSubject.setLoanApplyId(userId);
								List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
								if (wloanSubjects != null) {
									if (wloanSubjects.size() > 0) {
										wloanSubject = wloanSubjects.get(0);
										ztmgLoanBasicInfoEntity.setCompanyName(wloanSubject.getCompanyName()); // 公司名称.
										ztmgLoanBasicInfoEntity.setOperName(wloanSubject.getLoanUser()); // 公司法定代表人.
										ztmgLoanBasicInfoEntity.setRegisteredAddress(wloanSubject.getRegistAddress()); // 公司注册地址.
										int ztmgLoanBasicInfoEntityUpdate = ztmgLoanBasicInfoDao.update(ztmgLoanBasicInfoEntity);
										if (ztmgLoanBasicInfoEntityUpdate == 1) {
											LOG.info("fn:accountUpdateByCompany：\t开户成功，基本信息更新成功");
											// 更新借款人帐号信息，是否完善基本信息字段.
											CreditUserInfo creditUserInfo = creditUserInfoService.get(userId);
											creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2);
											int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
											if (creditUserInfoUpdateFlag == 1) {
												LOG.info("fn:accountUpdateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息成功");
												// 接收通知成功，通知对方服务器不在发送通知.
												result.put("respCode", "00");
												result.put("respMsg", "成功");
												String jsonString = JSON.toJSONString(result);
												// 对返回对方服务器消息进行加密.
												result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
												// 返回参数对方服务器，不在发送请求.
												return result;
											} else {
												LOG.info("fn:accountUpdateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息失败");
											}
										} else {
											LOG.info("fn:accountUpdateByCompany：\t开户成功，基本信息更新失败");
										}
									}
								}
							} else {
								// 融资主体.
								WloanSubject wloanSubject = new WloanSubject();
								wloanSubject.setLoanApplyId(userId);
								List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
								if (wloanSubjects != null) {
									if (wloanSubjects.size() > 0) {
										wloanSubject = wloanSubjects.get(0);
										ztmgLoanBasicInfo.setId(IdGen.uuid()); // 主键.
										ztmgLoanBasicInfo.setCompanyName(wloanSubject.getCompanyName()); // 公司名称.
										ztmgLoanBasicInfo.setOperName(wloanSubject.getLoanUser()); // 公司法定代表人.
										ztmgLoanBasicInfo.setRegisteredAddress(wloanSubject.getRegistAddress()); // 公司注册地址.
										int insertFlag_1 = ztmgLoanBasicInfoDao.insert(ztmgLoanBasicInfo);
										if (insertFlag_1 == 1) {
											LOG.info("fn:accountUpdateByCompany：\t开户成功，新增基本信息成功");
											// 更新借款人帐号信息，是否完善基本信息字段.
											CreditUserInfo creditUserInfo = creditUserInfoService.get(userId);
											creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2);
											int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
											if (creditUserInfoUpdateFlag == 1) {
												LOG.info("fn:accountUpdateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息成功");
												// 接收通知成功，通知对方服务器不在发送通知.
												result.put("respCode", "00");
												result.put("respMsg", "成功");
												String jsonString = JSON.toJSONString(result);
												// 对返回对方服务器消息进行加密.
												result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
												// 返回参数对方服务器，不在发送请求.
												return result;
											} else {
												LOG.info("fn:accountUpdateByCompany：\t开户成功，更新借款人帐号-是否完善基本字段信息失败");
											}
										} else {
											LOG.info("fn:accountUpdateByCompany：\t开户成功，新增基本信息失败");
										}
									}
								}
							}
						} else {
							LOG.info("fn:accountUpdateByCompany：\t开户成功，银行卡更新失败");
						}
					}
				} else if (status.equals("F")) {
					CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
					cgbUserBankCard.setUserId(userId);
					// 更新借款端银行卡账户状态
					CgbUserBankCard userBankCard = cgbUserBankCardDao.getUserBankCardByCreditUserIdAndState(cgbUserBankCard);
					if (userBankCard != null) {
						userBankCard.setState(CgbUserBankCard.CERTIFY_UPDATE_FAIL);
						userBankCard.setUpdateDate(new Date());
						int updateFlag = cgbUserBankCardDao.update(userBankCard);
						if (updateFlag == 1) { // 修改开户审核失败，银行卡未认证.
							LOG.info("fn:accountUpdateByCompany：\t开户审核失败，银行卡更新成功");
						} else {
							LOG.info("fn:accountUpdateByCompany：\t开户审核失败，银行卡更新失败");
						}
					}

					// 接收通知成功，通知对方服务器不在发送通知.
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					// 对返回对方服务器消息进行加密.
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// 返回参数对方服务器，不在发送请求.
					return result;
				}
			} else {
				LOG.info(this.getClass() + "fn:accountUpdateByCompany，验密失败，联系开发人员");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
