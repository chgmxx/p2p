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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Component
@Path("/callbackAutorization")
@Service("callbackAutorizationService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CallbackAutorizationService {

	private static final Logger LOG = LoggerFactory.getLogger(CallbackAutorizationService.class);

	@Autowired
	private NewRechargeService newRechargeService;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private UserRechargeService userRechargeService;
	@Autowired
	private UserTransDetailService userTransDetailService;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private UserInfoDao userInfoDao;
	@Resource
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;

	/**
	 * 商户ID
	 */
	private static final String MERCHANT_ID = Global.getConfig("merchantId");

	/**
	 * 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	 */
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	/**
	 * 商户自己的RSA私钥
	 */
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	/**
	 * 
	 * 方法: callbackBorrowingWebAuthorization <br>
	 * 描述: 借款端（PC端WEB）用户授权接口回调通知地址. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月26日 下午1:03:40
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/callbackBorrowingWebAuthorization")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, String> callbackBorrowingWebAuthorization(@FormParam("tm") String tm, @FormParam("data") String data) throws IOException {

		String jsonRet;
		Map<String, String> result = new HashMap<String, String>();
		try {
			// 对通知数据进行解密.
			jsonRet = APIUtils.decryptDataBySSL(tm, data, merchantRsaPrivateKey);
			Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
			});

			// 网贷平台唯一的用户编码
			String userId = (String) map.get("userId");
			LOG.info("fn:borrowingWebAuthorization-failReason(用户编码)\t" + userId);
			// 失败原因
			String failReason = (String) map.get("failReason");
			LOG.info("fn:borrowingWebAuthorization-failReason(失败原因)\t" + failReason);
			// 返回状态：S-成功；F-失败
			String status = (String) map.get("status");
			LOG.info("fn:borrowingWebAuthorization-status(状态)\t" + status);
			// 此处可传以上一个或多个值，传多个值，用“,”英文半角逗号分隔；用户请求的授权列表
			String grantList = (String) map.get("grantList");
			LOG.info("fn:borrowingWebAuthorization-grantList(授权列表)\t" + grantList);
			// 授权金额列表，单位：万/笔。此处可传以上一个或多个值，传多个值用“,”英文半角逗号分隔；
			String grantAmountList = (String) map.get("grantAmountList");
			LOG.info("fn:borrowingWebAuthorization-grantAmountList(授权金额列表)\t" + grantAmountList);
			// 授权期限列表，单位：年，此处可传以上一个或多个值，传多个值用“,”英文半角逗号分隔；
			String grantTimeList = (String) map.get("grantTimeList");
			LOG.info("fn:borrowingWebAuthorization-grantTimeList(授权期限列表)\t" + grantTimeList);
			// 签名结果
			String signature = (String) map.get("signature");
			LOG.info("fn:borrowingWebAuthorization-signature(签名)\t" + signature);
			map.remove("signature");
			// 校验验密.
			boolean verifyRet = APIUtils.verify(merchantRsaPublicKey, signature, map, "RSA");
			if (verifyRet) { // 验密成功.
				if (status.equals("S")) {
					LOG.info("fn:borrowingWebAuthorization-status(状态)：" + status + " = 成功");

					ZtmgUserAuthorization entity = new ZtmgUserAuthorization();
					entity.setUserId(userId);
					List<ZtmgUserAuthorization> list = ztmgUserAuthorizationDao.findList(entity);
					if (list != null) {
						if (list.size() == 0) { // 新增.
							ZtmgUserAuthorization model = new ZtmgUserAuthorization();
							model.setId(IdGen.uuid());
							model.setUserId(userId);
							model.setMerchantId(MERCHANT_ID);
							model.setFailReason(failReason);
							model.setStatus(status);
							model.setGrantList(grantList);
							model.setGrantAmountList(grantAmountList);
							model.setGrantTimeList(grantTimeList);
							model.setSignature(signature);
							model.setCreateDate(new Date());
							model.setUpdateDate(new Date());
							model.setRemarks("用户新增授权信息");
							int flag = ztmgUserAuthorizationDao.insert(model);
							if (flag == 1) {
								LOG.info("fn:borrowingWebAuthorization-用户新增授权信息成功 ...");
							}
						} else if (list.size() > 0) {
							ZtmgUserAuthorization ztmgUserAuthorization = list.get(0);
							if (ztmgUserAuthorization != null) { // 更新.
								ztmgUserAuthorization.setUserId(userId);
								ztmgUserAuthorization.setMerchantId(MERCHANT_ID);
								ztmgUserAuthorization.setFailReason(failReason);
								ztmgUserAuthorization.setStatus(status);
								ztmgUserAuthorization.setGrantList(grantList);
								ztmgUserAuthorization.setGrantAmountList(grantAmountList);
								ztmgUserAuthorization.setGrantTimeList(grantTimeList);
								ztmgUserAuthorization.setSignature(signature);
								ztmgUserAuthorization.setUpdateDate(new Date());
								ztmgUserAuthorization.setRemarks("用户变更授权信息");
								int flag = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
								if (flag == 1) {
									LOG.info("fn:borrowingWebAuthorization-用户变更授权信息成功 ...");
								}
							}
						}

					}

					/**
					 * 接收通知成功，通知对方服务器不在发送通知.
					 */
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					return result;
				} else if (status.equals("F")) {
					LOG.info("fn:borrowingWebAuthorization-status(状态)：" + status + " = 失败");
					/**
					 * 接收通知成功，通知对方服务器不在发送通知.
					 */
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					return result;
				}
			} else { // 验密失败.
				LOG.info("fn:borrowingWebAuthorization-验密失败，请联系资金存管方，定位失败原因.");
				/**
				 * 接收通知成功，通知对方服务器不在发送通知.
				 */
				result.put("respCode", "00");
				result.put("respMsg", "成功");
				String jsonString = JSON.toJSONString(result);
				result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 授权回调接口
	 * 
	 * @param tm
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/autorizationWebNotify")
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
				String userId = (String) map.get("userId");
				String status = (String) map.get("status");
				System.out.println("授权回调用户ID" + userId);
				UserInfo userInfo = userInfoService.get(userId);
				LOG.info("查询用户信息");
				if (userInfo == null) {
					userInfo = userInfoService.getCgb(userId);
				}
				if (userInfo != null) {
					if (status.equals("S")) {
						// 更新用户信息表 授权字段
						userInfo.setAutoState("1");// 已授权
					} else {
						userInfo.setAutoState("0");// 未授权
					}
					// 更新用户信息表 授权字段
					LOG.info("用户授权状态变更开始");
					userInfoDao.update(userInfo);
					LOG.info("用户授权状态已变更结束");
					result.put("respCode", "00");
					result.put("respMsg", "成功");
					String jsonString = JSON.toJSONString(result);
					// 对返回对方服务器消息进行加密
					result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
					// 返回参数对方服务器，不在发送请求
					return result;
				} else {
					CreditUserInfo creditUserInfo = creditUserInfoService.get(userId);
					if (creditUserInfo != null) {
						if (status.equals("S")) {
							creditUserInfo.setAutoState("1");
						} else {
							creditUserInfo.setAutoState("0");
						}
						// 更新用户信息表 授权字段
						LOG.info("用户授权状态变更开始");
						userInfoService.save(userInfo);
						LOG.info("用户授权状态已变更结束");
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					} else {
						LOG.info("未查询到借款用户信息");
						result.put("respCode", "00");
						result.put("respMsg", "成功");
						String jsonString = JSON.toJSONString(result);
						// 对返回对方服务器消息进行加密
						result = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
						// 返回参数对方服务器，不在发送请求
						return result;
					}
				}

			} else {
				System.out.println("333333333333333");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
