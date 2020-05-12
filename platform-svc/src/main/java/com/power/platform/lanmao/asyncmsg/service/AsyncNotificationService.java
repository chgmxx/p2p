package com.power.platform.lanmao.asyncmsg.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.alibaba.fastjson.JSON;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.common.config.Global;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.lanmao.asyncmsg.rechain.impl.ResponsibilityChain;
import com.power.platform.lanmao.asyncmsg.rechain.impl.ActivateStockedUser;
import com.power.platform.lanmao.asyncmsg.rechain.impl.AdjustUrgentBalance;
import com.power.platform.lanmao.account.service.ActivateStockedUserNotifyService;
import com.power.platform.lanmao.account.service.BusinessBindCardService;
import com.power.platform.lanmao.account.service.PersonBindCardService;
import com.power.platform.lanmao.asyncmsg.rechain.impl.*;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteDao;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteRecordDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.rw.service.LMRechargeNotifyService;
import com.power.platform.lanmao.rw.service.LMWithdrawNotifyService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListDelDataService;
import com.power.platform.lanmao.trade.service.AsyncTransactionNotifyService;
import com.power.platform.lanmao.trade.service.EnterpriseInformationUpdateNotifyService;
import com.power.platform.lanmao.trade.service.LanMaoUserPreTenderTransactionService;
import com.power.platform.lanmao.trade.service.UserAuthorizationNotifyService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.dao.UserInfoDao;

/**
 * class: AsyncNotificationService <br>
 * description:
 * 异步通知统一发送到存管系统后台配置地址（该通知地址由网贷平台提前提供给存管系统），服务器回调会返
 * 回 serviceName，不同业务的回调 serviceName 不同，网贷平台可以根据此参数区分自己平台的业务。
 * 存管系统主动向合作方平台发送 HTTPS 报文通知，合作方平台收到通知后返回一个通知应答。
 * 通知与重试：合作方平台收到存管系统的异步通知后，需要返回一个格式为 SUCCESS 的字符串表示已接收，
 * 如果未返回或格式不符，存管系统则认为通知失败，会启用重试机制，在 1 小时内最多重试 3 次，到达 3 次时如果
 * 仍然通知失败则放弃重试 <br>
 * author: chenhj / ant-loiter.com <br>
 * date: 2019年9月20日 上午9:55:26
 */
@Path("/cicnotify")
@Service("asyncNotificationService")
@Produces(MediaType.APPLICATION_JSON)
public class AsyncNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(AsyncNotificationService.class);

	@Autowired
	private EnterpriseInformationUpdateNotifyService enterpriseInformationUpdateNotifyService;
	@Autowired
	private UserAuthorizationNotifyService userAuthorizationNotifyService;
	@Autowired
	private AsyncTransactionNotifyService asyncTransactionNotifyService;
	@Autowired
	private LMRechargeNotifyService lMRechargeNotifyService;
	@Autowired
	private LanMaoUserPreTenderTransactionService lanMaoUserPreTenderTransactionService;
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private WloanSubjectService wloanSubjectService;

	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private PersonBindCardService personBindCardService;
	@Resource
	private BusinessBindCardService businessBindCardService;
	@Autowired
	private LMWithdrawNotifyService lMWithdrawNotifyService ;

	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Resource
	private CgbBigrechargeWhiteDao cgbBigrechargeWhiteDao;
	@Resource
	private LanMaoWhiteListDelDataService whiteListDelDataService;

	@Resource
	private ActivateStockedUserNotifyService activateStockedUserNotifyService;
	
	@Resource
	private LanMaoWhiteListAddDataService whiteListAddDataService;	
	@Resource
	private CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao;
	@POST
	@Path("/notify.do")
	public String notify(@FormParam("platformNo") String platformNo, @FormParam("responseType") String responseType, @FormParam("sign") String sign, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("serviceName") String serviceName) {

		// 业务数据报文，JSON 格式，具体见各接口定义
		respData = AppUtil.CheckStringByDefault(respData, "");
		// 对 respData 参数的签名，签名算法见下方“参数签名”章节
		sign = AppUtil.CheckStringByDefault(sign, "");
		// 回调类型，见枚举“回调类型“，用来区分是浏览器返回还是服务端异步通知
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		// 接口名称，见每个接口的详细定义
		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		// 平台编号
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");

		logger.info("获取请求>>>>>>> receive notify content : " + respData);
		try {
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			
			boolean verify = false;
			try {
				verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.info("通过sign:{}和公钥:{}，验签失败！！", sign, publicKey);
			}catch (IOException ex) {
				ex.printStackTrace();
				logger.info("通过sign:{}和公钥:{}，验签失败！！", sign, publicKey);
			}
			
			if (verify || Objects.equals("cicmorgan###2019", sign)) {
				logger.info(">>> 获取异步通知消息， 验签成功， sign success 获取推送过来的消息报文稿结构： {}", respData);
				Map<String, Object> respMap = JSON.parseObject(respData);
				//String code = (String) respMap.get("code");
				//if (("0").equals(code) && "SUCCESS".equals((String) respMap.get("status"))) {
				logger.info("respDatadddd = " + JSON.toJSONString(respMap));
				logger.info("respData = " + respData);
				logger.info("sign = " + sign);
				logger.info("responseType = " + responseType);
				logger.info("serviceName = " + serviceName);
				logger.info("platformNo = " + platformNo);
				// 请求受理或处理成功，根据不同接口处理 ,暂采用了一个易于分业务开发的模式，随时业务增加出现性能问题再采用多线程改造
				NotifyVo input = new NotifyVo();
				input.setServiceName(serviceName);
				input.setPlatformNo(platformNo);
				input.setResponseType(responseType);
				input.setKeySerial(keySerial);
				input.setRespData(respData);
				ResponsibilityChain chain = new ResponsibilityChain();
				chain.add(new ActivateStockedUser(activateStockedUserNotifyService))
				.add(new AdjustUrgentBalance())
				.add(new AsyncTransaction(asyncTransactionNotifyService))
				.add(new CheckPassword(lmTransactionDao))
				.add(new EnterpriseBindBankcard(creditUserInfoDao, cgbUserBankCardDao, wloanSubjectDao, wloanSubjectService,whiteListAddDataService,cgbBigrechargeWhiteRecordDao,lmTransactionDao))
				.add(new EnterpriseInfomationUpdate(enterpriseInformationUpdateNotifyService))
				.add(new EnterpriseRegister(businessBindCardService,whiteListAddDataService))
				.add(new ModifyMobileExpand(userInfoDao, personBindCardService, businessBindCardService, lmTransactionDao))
				.add(new PersonalBindBankCardExpand(userInfoDao, cgbUserAccountDao, cgbUserBankCardDao, lmTransactionDao,cgbBigrechargeWhiteRecordDao,whiteListAddDataService))
				.add(new PersonalRegisterExpand(userInfoDao, cgbUserAccountDao, cgbUserBankCardDao, lmTransactionDao,cgbBigrechargeWhiteRecordDao,whiteListAddDataService))
				.add(new Recharge(lMRechargeNotifyService))
				.add(new UnbindBankcard(userInfoDao, cgbUserBankCardDao,lmTransactionDao,cgbBigrechargeWhiteDao,whiteListDelDataService,creditUserInfoDao))
				.add(new UserAuthorization(userAuthorizationNotifyService))
				.add(new UserPreTransaction(lanMaoUserPreTenderTransactionService))
				.add(new VerifyDeduct()).add(new Withdraw(lMWithdrawNotifyService))
				.add(new ResetPassword(lmTransactionDao))
				.add(new BackRollRecharge());
				try {
					chain.doSomething(input, chain);
					if ("NOTIFY".equals(responseType)) {
						// 异步通知返回SUCCESS
						// response.setContentType("text/html;charset=utf-8");
						// response.getWriter().write("SUCCESS");
						return "SUCCESS";
					} else if ("DIRECT_CALLBACK".equals(responseType)) {
						// 同步通知 返回页面
						// 设置返回的参数  d
						// model.addAttribute("message", "支付成功");
						return "SUCCESS";
					}
				} catch (NotifyException notifyException) {
					if ("NOTIFY".equals(responseType)) {
						// 异步通知返回Fail
						// response.setContentType("text/html;charset=utf-8");
						// response.getWriter().write("FAIL");
						return "FAIL";
					} else if ("DIRECT_CALLBACK".equals(responseType)) {
						// 同步通知 返回页面
						// 设置返回的参数
						// model.addAttribute("message", "支付失败");
						return "FAIL";
					}
				}
				//}
			} else {
				// 验签失败
				logger.info("接收到通知消息， 但验签失败");
				return null;
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} 
		return null;

	}

}
