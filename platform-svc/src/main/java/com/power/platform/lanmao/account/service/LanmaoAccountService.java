package com.power.platform.lanmao.account.service;

import java.io.File;
import java.io.FileOutputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.service.CGBPayRestService;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.common.utils.ZipUtils;
import com.power.platform.common.utils.bank.BankEnum;
import com.power.platform.common.utils.bank.BankUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteDao;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteRecordDao;
import com.power.platform.lanmao.dao.CreditUserAuditInfoDao;
import com.power.platform.lanmao.entity.CgbBigrechargeWhite;
import com.power.platform.lanmao.entity.CgbBigrechargeWhiteRecord;
import com.power.platform.lanmao.trade.service.ActivateStockedUserService;
import com.power.platform.lanmao.type.AccessTypeEnum;
import com.power.platform.lanmao.type.AuditStatusEnum;
import com.power.platform.lanmao.type.AuthEnum;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.lanmao.type.WhiteStatusEnum;
import com.power.platform.pay.service.CGBPayService;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.utils.Validator;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListDelDataService;

@Component
@Path("/lanmaoAccount")
@Service("lanmaoAccountService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class LanmaoAccountService {

	private static final Logger LOG = LoggerFactory.getLogger(CGBPayRestService.class);
	private static final String PLATFORM_NO = Global.getConfigLanMao("platformNo");

	@Autowired
	private CGBPayService cGBPayService;
	@Resource
	private CgbBigrechargeWhiteDao whiteDao;
	@Resource
	private CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserLogDao userLogDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private PersonBindCardService personBindCardService;
	@Resource
	private BusinessBindCardService businessBindCardService;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;
	@Autowired
	private CreditUserAuditInfoDao creditUserAuditInfoDao;
	@Autowired
	private CgbUserBankCardDao cgbUserBankCardDao;

	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private LanMaoWhiteListAddDataService whiteListAddDataService;
	@Autowired
	private LanMaoWhiteListDelDataService whiteListDelDataService;
	@Autowired
	private CgbBigrechargeWhiteDao cgbBigrechargeWhiteDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;
	@Autowired
	private ActivateStockedUserService activateStockedUserService;

	/**
	 * 
	 * methods: redirectEnterpriseRegister <br>
	 * description: 企业绑卡注册，同步回调 <br>
	 * author: Roy <br>
	 * date: 2019年9月27日 上午9:34:15
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectEnterpriseRegister")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectEnterpriseRegister(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		try {
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			if (verify) {

				JSONObject jsonObject = JSONObject.parseObject(respData);
				if ("SUCCESS".equals(jsonObject.getString("status"))) { // 处理业务
					LOG.info("企业绑卡注册-同步回调-start...");
					String platformUserNo = jsonObject.getString("platformUserNo");

					CreditUserInfo creditUserInfo = creditUserInfoService.get(platformUserNo);
					String enterpriseFullName = "";
					String phone = "";
					if (null != creditUserInfo) {
						enterpriseFullName = creditUserInfo.getEnterpriseFullName();
						phone = creditUserInfo.getPhone();
					}

					// 压缩附件
					CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
					creditAnnexFile.setOtherId(platformUserNo);
					List<CreditAnnexFile> list = creditAnnexFileService.findList(creditAnnexFile);
					List<File> urlList = new ArrayList<File>(); // 文件列表
					if (list != null && list.size() > 0) {
						for (CreditAnnexFile annexFile : list) {
							if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_8.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_9.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_10.equals(annexFile.getType())) {
								File file = new File("/data/upload/image/" + annexFile.getUrl());
								if (file.exists()) {
									LOG.info("file exists ...");
									urlList.add(file);
								} else {
									LOG.info("file not exists, create it ...");
									continue;
								}
							}
						}
					}

					if (list != null) {
						for (CreditAnnexFile annexFile : list) {
							if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_8.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_9.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_10.equals(annexFile.getType())) {
								File file = new File("/data/upload/image/" + annexFile.getUrl());
								if (file.exists()) {
									LOG.info("file exists ...");
									urlList.add(file);
								} else {
									LOG.info("file not exists, create it ...");
									continue;
								}
							}
						}
					}
					String zipPath = "/data/upload/zip/" + enterpriseFullName != null ? enterpriseFullName : "" + "附件.zip";
					File file = new File(zipPath);
					if (file.exists()) {
						LOG.info("zip file exists ...");
					} else {
						LOG.info("zip file not exists, create it ...");
					}
					// 创建目标文件输出流
					FileOutputStream fos2 = new FileOutputStream(file);
					if (urlList.size() > 0) {

						ZipUtils.toZip(urlList, fos2);

						String toMailAddr = SendMailUtil.toMailAddr; // 开口存管银行联系人
						String ccs = SendMailUtil.toMailAddrCCS; // ZTMG风控人员
						String cc = null;

						String subject = "企业会员审核";
						StringBuffer message = new StringBuffer();
						message.append("1）中投摩根+").append(Global.getConfigLanMao("platformNo")).append("\n"); // 1）平台名称+平台编号
						message.append("2）"); // 2）平台会员编号+注册手机号码+企业名称
						if (!StringUtils.isBlank(platformUserNo)) {
							message.append(platformUserNo);
						}
						if (!StringUtils.isBlank(phone)) {
							message.append("+").append(phone);
						}
						if (!StringUtils.isBlank(enterpriseFullName)) {
							message.append("+").append(enterpriseFullName);
						}

						List<String> listS = new ArrayList<String>();
						listS.add(zipPath);
						Boolean sendEmailBoolean = SendMailUtil.sendWithMsgAndAttachment(toMailAddr, cc, ccs, subject, message.toString(), listS, creditUserInfo.getEnterpriseFullName());
						if (sendEmailBoolean) {
							LOG.info("send email successful ...");
						} else {
							LOG.info("send email failure ...");
						}
					} else {
						LOG.info("附加资料信息没有找到，不进行邮件发送......");
					}

					LOG.info("企业绑卡注册-同步回调-end...");
					// 页面响应.
					response.sendRedirect(RedirectUrlConfig.ENTERPRISE_REGISTER_RETURN_URL + "?id=" + platformUserNo);
					return;
				} else {
					LOG.info("企业绑卡注册-同步回调-start...");
					String platformUserNo = jsonObject.getString("platformUserNo");
					LOG.info("企业绑卡注册-业务处理失败啊......");
					LOG.info("企业绑卡注册-同步回调-end...");
					// 页面响应.
					response.sendRedirect(RedirectUrlConfig.ENTERPRISE_REGISTER_RETURN_URL + "?id=" + platformUserNo);
					return;
				}

				// // 借款人ID.
				// String platformUserNo = "";
				// CreditUserInfo entity = null;
				// JSONObject jsonObject = JSONObject.parseObject(respData);
				// if ("SUCCESS".equals(jsonObject.getString("status"))) { // 处理业务
				// LOG.debug("企业绑卡注册-redirect-start...");
				// platformUserNo = jsonObject.getString("platformUserNo");
				// entity = creditUserInfoService.get(platformUserNo);
				// if (null != entity) {
				// if (AuditStatusEnum.PASSED.getValue().equals(jsonObject.getString("auditStatus"))) {
				// entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue());
				// } else if (AuditStatusEnum.AUDIT.getValue().equals(jsonObject.getString("auditStatus"))) {
				// entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_2.getValue());
				// } else if (AuditStatusEnum.BACK.getValue().equals(jsonObject.getString("auditStatus"))) {
				// entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_3.getValue());
				// } else if (AuditStatusEnum.REFUSED.getValue().equals(jsonObject.getString("auditStatus"))) {
				// entity.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_4.getValue());
				// }
				// entity.setUpdateDate(new Date());
				// int updateCreditUserInfoFlag = creditUserInfoDao.update(entity);
				// LOG.info("借款人用户开户状态变更:{}", updateCreditUserInfoFlag == 1 ? "成功" : "失败");
				// /**
				// * 用户授权
				// */
				// ZtmgUserAuthorization creUserAuthorization = new ZtmgUserAuthorization();
				// creUserAuthorization.setUserId(platformUserNo);
				// List<ZtmgUserAuthorization> authorizationList = ztmgUserAuthorizationDao.findList(creUserAuthorization);
				// if (authorizationList != null && authorizationList.size() > 0) {
				// ZtmgUserAuthorization ztmgUserAuthorization = authorizationList.get(0);
				// ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
				// ztmgUserAuthorization.setStatus("S");
				// ztmgUserAuthorization.setSignature(sign);
				// ztmgUserAuthorization.setGrantAmountList(jsonObject.getString("amount")); // 授权金额
				// ztmgUserAuthorization.setGrantTimeList(jsonObject.getString("failTime")); // 授权截至期限
				// ztmgUserAuthorization.setUpdateDate(new Date());
				// ztmgUserAuthorization.setRemarks("变更授权信息");
				// int updateCreUserAuthorization = ztmgUserAuthorizationDao.update(ztmgUserAuthorization);
				// LOG.info("变更授权信息:{}", updateCreUserAuthorization == 1 ? "成功" : "失败");
				// } else {
				// ZtmgUserAuthorization ztmgUserAuthorization = new ZtmgUserAuthorization();
				// ztmgUserAuthorization.setId(IdGen.uuid());
				// ztmgUserAuthorization.setUserId(platformUserNo);
				// ztmgUserAuthorization.setMerchantId(platformNo);
				// ztmgUserAuthorization.setStatus("S");
				// ztmgUserAuthorization.setSignature(sign);
				// ztmgUserAuthorization.setGrantList(AuthEnum.REPAYMENT.getValue());
				// ztmgUserAuthorization.setGrantAmountList(jsonObject.getString("amount"));
				// ztmgUserAuthorization.setGrantTimeList(jsonObject.getString("failTime"));
				// ztmgUserAuthorization.setCreateDate(new Date());
				// ztmgUserAuthorization.setUpdateDate(new Date());
				// ztmgUserAuthorization.setRemarks("新增授权信息");
				// int insertCreUserAuthorization = ztmgUserAuthorizationDao.insert(ztmgUserAuthorization);
				// LOG.info("新增授权信息:{}", insertCreUserAuthorization == 1 ? "成功" : "失败");
				// }
				// /**
				// * 开户审核信息留存
				// */
				// CreditUserAuditInfo creditUserAuditInfo = new CreditUserAuditInfo();
				// creditUserAuditInfo.setId(platformUserNo);
				// creditUserAuditInfo.setPlatformUserNo(platformUserNo);
				// creditUserAuditInfo.setAuditStatus(jsonObject.getString("auditStatus"));
				// creditUserAuditInfo.setUserRole(jsonObject.getString("userRole"));
				// creditUserAuditInfo.setBankcardNo(jsonObject.getString("bankcardNo"));
				// creditUserAuditInfo.setBankcode(jsonObject.getString("bankcode"));
				// creditUserAuditInfo.setRemark(jsonObject.getString("remark"));
				// creditUserAuditInfo.setCode(jsonObject.getString("code"));
				// creditUserAuditInfo.setStatus(jsonObject.getString("status"));
				// creditUserAuditInfo.setErrorCode(jsonObject.getString("errorCode"));
				// creditUserAuditInfo.setErrorMessage(jsonObject.getString("errorMessage"));
				// creditUserAuditInfo.setCreateDate(new Date());
				// creditUserAuditInfo.setUpdateDate(new Date());
				// int insertCreditUserAuditFlag = creditUserAuditInfoDao.insert(creditUserAuditInfo);
				// LOG.info("开户审核记录新增:{}", insertCreditUserAuditFlag == 1 ? "成功" : "失败");
				// /**
				// * @author fuwei
				// * 添加白名单
				// */
				// LanMaoWhiteList whiteList = new LanMaoWhiteList();
				// whiteList.setRequestNo(jsonObject.getString("requestNo"));
				// whiteList.setPlatformUserNo(jsonObject.getString("platformUserNo"));
				// whiteList.setBankcardNo(jsonObject.getString("bankcardNo"));
				// whiteList.setUserRole(UserRoleEnum.BORROWERS.getValue());
				// Map<String, Object> result = whiteListAddDataService.whiteListAdd(whiteList);
				// if (result.get("code").equals("调用成功") && result.get("status").equals("处理成功")) {
				// LOG.info("添加白名单成功");
				// } else {
				// LOG.info("添加白名单失败：" + result.get("errorMessage"));
				// }
				//
				// }
				// }
			} else {
				LOG.info("企业绑卡注册-验签失败......");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 
	 * methods: enterpriseRegister <br>
	 * description: 企业绑卡注册 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * author: Roy <br>
	 * date: 2019年9月26日 上午10:22:23
	 * 
	 * @param creditUserInfo
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/enterpriseRegister")
	@ResponseBody
	public Map<String, Object> enterpriseRegister(@RequestBody CreditUserInfo creditUserInfo, @Context HttpServletRequest servletRequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		if (creditUserInfo != null) {
			if (StringUtils.isBlank(creditUserInfo.getId()) || StringUtils.isBlank(creditUserInfo.getCreditUserType())) {
				result.put("state", "2");
				result.put("message", "<b>缺少必要参数</b>");
				return result;
			} else if (StringUtils.isBlank(creditUserInfo.getEnterpriseFullName())) {
				result.put("state", "2");
				result.put("message", "<b>缺少必要参数</b>");
				return result;
			} else if (creditUserInfo.getWloanSubject() != null) {
				if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanUser()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getRegistAddress())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getCorporationCertType()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getCorporationCertNo())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getBusinessLicenseType()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getBusinessNo())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if ("BLC".equals(creditUserInfo.getWloanSubject().getBusinessLicenseType())) {
					if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getTaxCode()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getOrganNo())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					}
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankName()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankCode())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankNo()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankProvince())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankCity()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankCounty())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanIssuerName()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getBankPermitCertNo())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanIssuer()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonName())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonPhone()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonCertType())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonCertNo()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getEmail())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				}
			} else {
				result.put("state", "2");
				result.put("message", "<b>缺少必要参数</b>");
				return result;
			}
		} else {
			result.put("state", "2");
			result.put("message", "<b>缺少必要参数</b>");
			return result;
		}
		try {
			// 开户资料漏传判断
			CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
			creditAnnexFile.setOtherId(creditUserInfo.getId());
			List<CreditAnnexFile> list = creditAnnexFileService.findList(creditAnnexFile);
			if (list.size() < 3) {
				result.put("state", "2");
				result.put("message", "<b>开户资料不齐，请您完善附加信息</b>");
				return result;
			}

			Map<String, String> data = businessBindCardService.cardRegister(creditUserInfo);

//			CgbBigrechargeWhite white2 = new CgbBigrechargeWhite();
//			white2.setBankNo(creditUserInfo.getWloanSubject().getLoanBankNo());
//			List<CgbBigrechargeWhite> whites = whiteDao.findList(white2);
//			if (whites.size() > 0) {
//				for (CgbBigrechargeWhite changeWhite : whites) {
//					CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//					white.setId(changeWhite.getId());
//					white.setPlatformId(PLATFORM_NO);
//					white.setRealName(creditUserInfo.getName());
//					white.setUserId(creditUserInfo.getId());
//					white.setBankCode(null);
//					white.setBankNo(creditUserInfo.getWloanSubject().getLoanBankNo());
//					white.setStatus(WhiteStatusEnum.GRAY);
//					white.setUserRole(UserRoleEnum.BORROWERS.getValue());
//					white.setOperationDesc(null);
//					white.setUpdateDate(new Date());
//					int whiteFlag = whiteDao.update(white);
//					LOG.info("修改白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//				}
//			} else {
//				CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//				white.setId(IdGen.uuid());
//				white.setPlatformId(PLATFORM_NO);
//				white.setRealName(creditUserInfo.getName());
//				white.setUserId(creditUserInfo.getId());
//				white.setBankCode(BankCodeEnum.getTextByText(null));
//				white.setBankNo(creditUserInfo.getWloanSubject().getLoanBankNo());
//				white.setStatus(WhiteStatusEnum.GRAY);
//				white.setUserRole(UserRoleEnum.BORROWERS.getValue());
//				white.setOperationDesc(null);
//				white.setCreateDate(new Date());
//				white.setUpdateDate(null);
//				int whiteFlag = whiteDao.insert(white);
//				LOG.info("添加白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//			}

			result.put("state", "0");
			result.put("message", "<b>企业绑卡注册信息发送至存管行</b>");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("企业绑卡注册，系统错误...");
			result.put("state", "1");
			result.put("message", "<b>系统错误</b>");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * methods: enterpriseBindBankCard <br>
	 * description: 企业绑卡 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * 
	 * @return
	 */
	@POST
	@Path("/enterpriseBindBankCard")
	@ResponseBody
	public Map<String, Object> enterpriseBindBankCard(@FormParam("from") String from, @FormParam("creditUserId") String creditUserId, @FormParam("bankcardNo") String bankcardNo, @FormParam("bankName") String bankName, @FormParam("bankcardId") String bankcardId, @Context HttpServletRequest servletRequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(creditUserId) || StringUtils.isBlank(bankcardNo) || StringUtils.isBlank(bankName) || StringUtils.isBlank(bankcardId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {

			// 获取银行编码
			CicmorganBankCode cicmorganBankCode = cicmorganBankCodeService.get(bankcardId);
			String bankCode = "";
			if (null != cicmorganBankCode) {
				bankCode = cicmorganBankCode.getBankCode();
			}
			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
			if (null != creditUserInfo) {
				/**
				 * 借款企业银行卡信息变更/新增
				 */
				CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
				cgbUserBankCard.setUserId(creditUserInfo.getId());
				List<CgbUserBankCard> cgbUserBankCards = cgbUserBankCardDao.findCreditList(cgbUserBankCard);
				if (cgbUserBankCards != null) {
					CgbUserBankCard creditUserBank = null;
					if (cgbUserBankCards.size() > 0) {
						creditUserBank = cgbUserBankCards.get(0);
						if (CgbUserBankCard.CERTIFY_YES.equals(creditUserBank.getState())) {
							result.put("state", "2");
							result.put("message", "该企业已绑卡，请您先去解绑银行卡");
							result.put("data", null);
							return result;
						}
						creditUserBank.setBankAccountNo(bankcardNo); // 银行账户
						creditUserBank.setBankName(bankName); // 银行名称
						creditUserBank.setIsDefault(UserBankCard.DEFAULT_YES); // 默认卡
						creditUserBank.setState(CgbUserBankCard.CERTIFY_NO); // 0：未认证，1：已认证
						creditUserBank.setBankNo(bankCode); // 银行编码
						creditUserBank.setUpdateDate(new Date()); // 更新时间
						int creditUserBankUpdateFlag = cgbUserBankCardDao.update(creditUserBank);
						LOG.info("企业绑卡，银行卡信息更新:{}", creditUserBankUpdateFlag == 1 ? "成功" : "失败");
					}
				}
				/**
				 * 借款企业融资主体变更/新增
				 */
				WloanSubject entity = new WloanSubject();
				entity.setLoanApplyId(creditUserInfo.getId());
				List<WloanSubject> subjects = wloanSubjectService.findList(entity);
				if (subjects != null) {
					WloanSubject subject = null;
					if (subjects.size() > 0) {
						subject = subjects.get(0);
						if (subject != null) { // 融资主体已存在
							subject.setLoanBankName(bankName); // 银行名称
							subject.setLoanBankCode(bankCode); // 银行编码
							subject.setLoanBankNo(bankcardNo); // 银行账号
							subject.setUpdateDate(new Date()); // 更新时间
							int subjectUpdateFlag = wloanSubjectDao.update(subject);
							LOG.info("企业绑卡，借款企业融资主体更新:{}", subjectUpdateFlag == 1 ? "成功" : "失败");
						}
					}
				}
//				CgbBigrechargeWhite white2 = new CgbBigrechargeWhite();
//				white2.setBankNo(bankcardNo);
//				List<CgbBigrechargeWhite> whites = whiteDao.findList(white2);
//				if (whites.size() > 0) {
//					for (CgbBigrechargeWhite changeWhite : whites) {
//						CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//						white.setId(changeWhite.getId());
//						white.setPlatformId(PLATFORM_NO);
//						white.setRealName(creditUserInfo.getName());
//						white.setUserId(creditUserInfo.getId());
//						white.setBankCode(bankCode);
//						white.setBankNo(bankcardNo);
//						white.setStatus(WhiteStatusEnum.GRAY);
//						white.setUserRole(UserRoleEnum.BORROWERS.getValue());
//						white.setOperationDesc(null);
//						white.setUpdateDate(new Date());
//						int whiteFlag = whiteDao.update(white);
//						LOG.info("修改白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//					}
//				} else {
//					CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//					white.setId(IdGen.uuid());
//					white.setPlatformId(PLATFORM_NO);
//					white.setRealName(creditUserInfo.getName());
//					white.setUserId(creditUserInfo.getId());
//					white.setBankCode(bankCode);
//					white.setBankNo(bankcardNo);
//					white.setStatus(WhiteStatusEnum.GRAY);
//					white.setUserRole(UserRoleEnum.BORROWERS.getValue());
//					white.setOperationDesc(null);
//					white.setCreateDate(new Date());
//					white.setUpdateDate(null);
//					int whiteFlag = whiteDao.insert(white);
//					LOG.info("添加白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//				}
				
			}

			Map<String, String> data = businessBindCardService.enterpriseBindBankCard(creditUserInfo, bankcardNo, bankCode);

			result.put("state", "0");
			result.put("message", "<b>企业绑卡信息发送至存管行</b>");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("企业绑卡，系统错误...");
			result.put("state", "1");
			result.put("message", "<b>系统错误</b>");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * methods: redirectEnterpriseBindBankCard <br>
	 * description: 企业绑卡，同步回调 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectEnterpriseBindBankCard")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectEnterpriseBindBankCard(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		try {
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			if (verify) {

				JSONObject jsonObject = JSONObject.parseObject(respData);
				String platformUserNo = jsonObject.getString("platformUserNo");
				if ("SUCCESS".equals(jsonObject.getString("status"))) { // 处理业务
					LOG.info("企业绑卡-同步回调-成功...");
				} else {
					LOG.info("企业绑卡-同步回调-失败...");
				}
				// 页面响应.
				response.sendRedirect(RedirectUrlConfig.ENTERPRISE_BIND_BANKCARD_REDIRECT_URL + "?id=" + platformUserNo);
				return;
			} else {
				LOG.info("企业绑卡-验签失败......");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 
	 * methods: enterpriseInformationUpdate <br>
	 * description: 企业信息修改 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * 
	 * @return
	 */
	@POST
	@Path("/enterpriseInformationUpdate")
	@ResponseBody
	public Map<String, Object> enterpriseInformationUpdate(@RequestBody CreditUserInfo creditUserInfo, @Context HttpServletRequest servletRequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (creditUserInfo != null) {
				if (StringUtils.isBlank(creditUserInfo.getId()) || StringUtils.isBlank(creditUserInfo.getCreditUserType())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (StringUtils.isBlank(creditUserInfo.getEnterpriseFullName())) {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				} else if (creditUserInfo.getWloanSubject() != null) {
					if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanUser()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getRegistAddress())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getCorporationCertType()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getCorporationCertNo())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getBusinessLicenseType()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getBusinessNo())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if ("BLC".equals(creditUserInfo.getWloanSubject().getBusinessLicenseType())) {
						if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getTaxCode()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getOrganNo())) {
							result.put("state", "2");
							result.put("message", "<b>缺少必要参数</b>");
							return result;
						}
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankName()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankCode())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankNo()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankProvince())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankCity()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanBankCounty())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanIssuerName()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getBankPermitCertNo())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getLoanIssuer()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonName())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonPhone()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonCertType())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					} else if (StringUtils.isBlank(creditUserInfo.getWloanSubject().getAgentPersonCertNo()) || StringUtils.isBlank(creditUserInfo.getWloanSubject().getEmail())) {
						result.put("state", "2");
						result.put("message", "<b>缺少必要参数</b>");
						return result;
					}
				} else {
					result.put("state", "2");
					result.put("message", "<b>缺少必要参数</b>");
					return result;
				}
			} else {
				result.put("state", "2");
				result.put("message", "<b>缺少必要参数</b>");
				return result;
			}

			Map<String, String> data = businessBindCardService.enterpriseInformationUpdate(creditUserInfo);

			result.put("state", "0");
			result.put("message", "<b>企业绑卡信息发送至存管行</b>");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("企业信息修改，系统错误 ......");
			result.put("state", "1");
			result.put("message", "<b>企业信息修改，系统错误 ......</b>");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * methods: redirectEnterpriseInformationUpdate <br>
	 * description: 企业信息修改 --回调 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectEnterpriseInformationUpdate")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectEnterpriseInformationUpdate(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		try {
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			if (verify) {
				LOG.info("企业信息修改-同步回调...start...");
				JSONObject json = JSONObject.parseObject(respData);
				LOG.info("企业信息修改，同步通知参数:{}", json.toString());
				String platformUserNo = ""; // 平台会员编号
				String requestNo = ""; // 请求流水号
				String reviewStatus = ""; // 提交后的审核状态
				CreditUserInfo creditUserInfo = null;
				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					requestNo = json.getString("requestNo"); // 请求流水号
					platformUserNo = json.getString("platformUserNo"); // 平台会员编号
					reviewStatus = json.getString("reviewStatus"); // 平台会员编号

					creditUserInfo = creditUserInfoService.get(platformUserNo);
					if (null != creditUserInfo) {
						creditUserInfo.setAutoState(reviewStatus);
						creditUserInfo.setUpdateDate(new Date());
						int updateCreditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
						LOG.info("借款人企业信息修改状态变更:{}", updateCreditUserInfoFlag == 1 ? "成功" : "失败");
					}

					// 邮件标题
					StringBuffer subject = new StringBuffer();
					subject.append(platformNo).append("+").append(platformUserNo).append("企业会员修改"); // 平台编号+平台会员编号
					// 邮件正文
					StringBuffer message = new StringBuffer();
					message.append(DateUtils.getDateStr()).append("+").append(requestNo).append("\n");// 提交日期+请求流水号
					message.append("修改前的内容-修改后的内容，麻烦平台风控专员跟企业进行确认后编辑补发...");
					String toMailAddr = SendMailUtil.toMailAddr; // 存管银行运营人员
					String cc = SendMailUtil.toMailAddrCCS; // 平台风控人员
					SendMailUtil.ztmgSendRepayRemindEmailMsg(toMailAddr, cc, subject.toString(), message.toString());
					LOG.info("企业信息修改-同步回调-邮件发送成功......");
				} else {
					LOG.info("企业信息修改-同步回调-业务处理失败......");
				}
				LOG.info("企业信息修改-同步回调...end...");
				// 页面响应.
				response.sendRedirect(RedirectUrlConfig.ENTERPRISE_REGISTER_RETURN_URL + "?id=" + platformUserNo);
				return;
			} else {
				LOG.info("企业信息修改-同步回调-验签失败......");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 根据银行卡号查询银行
	 * 
	 * @param cardNo
	 *            银行卡卡号
	 * @return {"bank":"CMB","validated":true,"cardType":"DC","key":"(卡号)","messages":[],"stat":"ok"}
	 * @return
	 */
	@POST
	@Path("/getCardDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getCardDetail(@FormParam("from") String cardNo) {

		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(cardNo)) {
			LOG.info("fn:getCardDetail,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			String jsonStr = BankUtils.getCardDetail(cardNo);
			JSONObject json = JSONObject.parseObject(jsonStr);
			String validated = json.getString("validated");
			if ("true".equals(validated)) {
				String bankName = BankEnum.getTextByValue(json.get("bank").toString());
				String bankcode = BankCodeEnum.getTextByText(bankName);
				if ("".equals(bankcode)) {
					// 说明绑定的银行卡不在存管行规定的银行
					LOG.info("fn:getCardDetail,绑定的银行卡不在存管行规定的银行里！");
					result.put("state", "2");
					result.put("message", "绑定的银行卡不在存管行规定的银行里！");
					result.put("data", null);
					System.out.println("绑定的银行卡不在存管行规定的银行里");
					return result;
				}
				LOG.info("fn:getCardDetail,绑定的银行卡符合要求！");
				result.put("state", "0");
				result.put("message", "绑定的银行卡符合要求！");
				result.put("data", null);
				System.out.println(json.get("bank"));
				return result;
			} else {
				// 说明绑定的银行卡不在存管行规定的银行
				LOG.info("fn:getCardDetail,银行卡号不正确！");
				result.put("state", "2");
				result.put("message", "银行卡号不正确！");
				result.put("data", null);
				System.out.println("银行卡号不正确");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getCardDetail,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * methods: personalRegisterExpand <br>
	 * description: 个人绑卡注册 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param bankCardNo
	 * <br>
	 *            银行卡号
	 * @param from
	 * <br>
	 *            请求平台.
	 * @param certNo
	 * <br>
	 *            身份证号
	 * @param realName
	 * <br>
	 *            真实姓名
	 * @param bankCardPhone
	 * <br>
	 *            银行预留手机号.
	 * @return
	 */
	@POST
	@Path("/personalRegisterExpand")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> personalRegisterExpand(@FormParam("bankCardNo") String bankCardNo, @FormParam("certNo") String certNo, @FormParam("realName") String realName, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bankCardPhone") String bankCardPhone, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		String userInfoId = JedisUtils.get(token);
		if (StringUtils.isBlank(userInfoId)) {
			LOG.info("fn:personalRegisterExpand,系统超时");
			result.put("state", "4");
			result.put("message", "系统超时！");
			result.put("data", null);
			return result;
		}
		UserInfo userInfo = userInfoDao.getCgb(userInfoId);

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(certNo) || StringUtils.isBlank(realName) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(ip) || StringUtils.isBlank(bankCardPhone)) {
			LOG.info("fn:personalRegisterExpand,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */

		if (!FuncUtils.isBankCard(bankCardNo)) {
			LOG.info("fn:personalRegisterExpand,开户银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(certNo)) {
			LOG.info("fn:personalRegisterExpand,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			System.out.println("yyy------个人绑卡注册开始");
			// Map<?, ?> data = cGBPayService.accountCreateWeb(bankCardNo, certNo, realName, ip, bankCardPhone, token);
			// 个人绑卡注册
			if (userInfo != null) {
				userInfo.setRealName(realName);// 真实姓名
				userInfo.setCertificateNo(certNo);// 身份证号
				int j = userInfoDao.update(userInfo);
				LOG.info("用户信息修改成功:{}", j == 1 ? "成功" : "失败");
			}
			String orderId = IdGen.uuid();
			Map<String, String> data = personBindCardService.cardRegister(userInfo, bankCardNo, certNo, realName, bankCardPhone, orderId);

			if (userInfo != null) {
				UserLog log = new UserLog();
				log.setId(String.valueOf(IdGen.randomLong()));
				log.setUserId(userInfo.getId());
				if (userInfo.getRealName() != null)
					log.setUserName(userInfo.getRealName() + userInfo.getName());
				else
					log.setUserName(userInfo.getName());
				log.setType("2");
				log.setCreateDate(new Date());
				if (userInfo.getRealName() != null)
					log.setRemark(userInfo.getRealName() + "个人绑卡注册请求发送成功");
				else
					log.setRemark(userInfo.getName() + "个人绑卡注册请求发送成功");
				userLogDao.insert(log);

//				CgbBigrechargeWhite white2 = new CgbBigrechargeWhite();
//				white2.setBankNo(bankCardNo);
//				List<CgbBigrechargeWhite> whites = whiteDao.findList(white2);
//				if (whites.size() > 0) {
//					for (CgbBigrechargeWhite changeWhite : whites) {
//						CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//						white.setId(changeWhite.getId());
//						white.setPlatformId(PLATFORM_NO);
//						white.setRealName(userInfo.getName());
//						white.setUserId(userInfo.getId());
//						white.setBankCode(data.get("bankcode"));
//						white.setBankNo(bankCardNo);
//						white.setStatus(WhiteStatusEnum.GRAY);
//						white.setUserRole(UserRoleEnum.INVESTOR.getValue());
//						white.setOperationDesc(null);
//						white.setUpdateDate(new Date());
//						int whiteFlag = whiteDao.update(white);
//						LOG.info("修改白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//					}
//				} else {
//					CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//					white.setId(IdGen.uuid());
//					white.setPlatformId(PLATFORM_NO);
//					white.setRealName(userInfo.getName());
//					white.setUserId(userInfo.getId());
//					white.setBankCode(BankCodeEnum.getTextByText(null));
//					white.setBankCode(data.get("bankcode"));
//					white.setBankNo(bankCardNo);
//					white.setStatus(WhiteStatusEnum.GRAY);
//					white.setUserRole(UserRoleEnum.INVESTOR.getValue());
//					white.setOperationDesc(null);
//					white.setCreateDate(new Date());
//					white.setUpdateDate(null);
//					int whiteFlag = whiteDao.insert(white);
//					LOG.info("添加白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//				}

				CgbBigrechargeWhiteRecord white = new CgbBigrechargeWhiteRecord();
				white.setId(IdGen.uuid());
				white.setRequestNo(orderId);
				white.setPlatformId(PLATFORM_NO);
				white.setRealName(userInfo.getRealName());
				white.setUserId(userInfo.getId());
//				white.setBankCode(BankCodeEnum.getTextByText(null));
				white.setBankCode(data.get("bankcode"));
				white.setBankNo(bankCardNo);
				white.setStatus(WhiteStatusEnum.GRAY);
				white.setUserRole(UserRoleEnum.INVESTOR.getValue());
				white.setOperationDesc(null);
				white.setCreateDate(new Date());
				white.setUpdateDate(new Date());
				white.setDescription("记录绑卡注册白名单：灰度");
				int whiteFlag = cgbBigrechargeWhiteRecordDao.insert(white);
				LOG.info("记录个人绑卡注册白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
				
				result.put("state", "0");
				result.put("message", "个人绑卡注册请求发送成功！");
				System.out.println("yyy------个人绑卡注册结束");
				LOG.debug("data : {}", JSON.toJSONString(data));
				System.out.println("data :" + JSON.toJSONString(data));
				result.put("data", data);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "token 失效！");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:personalRegisterExpand,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: redirectPersonalRegisterExpand <br>
	 * description: 个人绑卡注册，同步回调 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectPersonalRegisterExpand")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectPersonalRegisterExpand(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) throws Exception {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		System.out.println("-serviceName-=" + serviceName);
		System.out.println("-platformNo-=" + platformNo);
		System.out.println("-userDevice-=" + userDevice);
		System.out.println("-responseType-=" + responseType);
		System.out.println("-keySerial-=" + keySerial);
		System.out.println("-respData-=" + respData);
		try {
			System.out.println("yyy------个人绑卡注册回调开始");
			LOG.debug("个人绑卡注册-redirect-start...");
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			System.out.println("verify==" + verify);
			if (verify) {
				// 借款人ID.
				JSONObject json = JSONObject.parseObject(respData);
				String userId = json.getString("platformUserNo");
				String orderId = json.getString("requestNo");
				System.out.println("-userId-=" + userId);
				System.out.println("-orderId-=" + orderId);
				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					// 审核通过 ---没有审核中这个状态
					if ("PASSED".equals(json.getString("auditStatus"))) {
						LOG.info("个人绑卡注册-同步回调-成功...");
					} else { // 审核拒绝或回退
						LOG.info("个人绑卡注册-同步回调-审核拒绝...");
					}
				} else {
					LOG.info("个人绑卡注册-同步回调-失败...");
				}
				// 页面响应. TODO
				response.sendRedirect(RedirectUrlConfig.ACCOUNT_HOME_URL + "?id=" + userId);

			}
			System.out.println("yyy------个人绑卡注册回调结束");
			LOG.debug("个人绑卡注册-redirect-end...");
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("验签错误，生成商户公钥失败", e);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("验签错误" + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new GeneralSecurityException("验签错误" + e.getMessage(), e);
		}
		return;
	}

	/**
	 * 
	 * methods: borrowersUnbindBankcard <br>
	 * description: 借款人解绑银行卡 <br>
	 * author: Roy <br>
	 * date: 2019年10月19日 下午2:18:16
	 * 
	 * @param from
	 * @param creditUserId
	 * @return
	 */
	@POST
	@Path("/borrowersUnbindBankcard")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> borrowersUnbindBankcard(@FormParam("from") String from, @FormParam("creditUserId") String creditUserId) {

		Map<String, Object> result = new HashMap<String, Object>();
		CgbUserBankCard userBankCard = null;
		try {
			if (StringUtils.isBlank(from) || StringUtils.isBlank(creditUserId)) {
				LOG.info("借款人解绑银行卡，缺少必要参数，from:{}，creditUserId:{}", from, creditUserId);
				result.put("state", "2");
				result.put("message", "缺少必要参数！");
				result.put("data", null);
				return result;
			}

			// 判断银行卡是否已解绑
			// 解绑银行卡，处理银行卡字段state，0：已解绑（未认证），1：已绑卡（已认证）
			userBankCard = new CgbUserBankCard();
			userBankCard.setUserId(creditUserId);
			List<CgbUserBankCard> list = cgbUserBankCardDao.findCreditList(userBankCard);
			String orderId = "";
			if (null != list) {
				CgbUserBankCard bankCard = null;
				for (int i = 0; i < list.size(); i++) {
					bankCard = list.get(i); // 有且仅有一条银行卡记录
					if (i == 0) {
						if (CgbUserBankCard.CERTIFY_NO.equals(bankCard.getState())) {
							result.put("state", "2");
							result.put("message", "已解绑，勿重复操作");
							result.put("data", null);
							return result;
						}
						String userName = null;
						CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId); // 借款人
						if (creditUserInfo != null) {
							userName = creditUserInfo.getEnterpriseFullName();
						}
						CgbBigrechargeWhiteRecord white = new CgbBigrechargeWhiteRecord();
						white.setId(IdGen.uuid());
						white.setRequestNo(orderId);
						white.setPlatformId(PLATFORM_NO);
						white.setRealName(userName);
						white.setUserId(creditUserId);
						white.setBankCode(bankCard.getBankNo());
						white.setBankNo(bankCard.getBankAccountNo());
						white.setStatus(WhiteStatusEnum.GRAY);
						white.setUserRole(UserRoleEnum.BORROWERS.getValue());
						white.setOperationDesc(null);
						white.setCreateDate(new Date());
						white.setUpdateDate(new Date());
						white.setDescription("记录企业解绑白名单：灰度");
						int whiteFlag = cgbBigrechargeWhiteRecordDao.insert(white);
						LOG.info("记录企业解绑白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
					}
				}
			}

			Map<String, String> data = businessBindCardService.untyingCard(creditUserId,orderId);
			
			result.put("state", "0");
			result.put("message", "借款人解绑银行卡，数据签名成功");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "借款人解绑银行卡，系统异常");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * methods: untyingCard <br>
	 * description: 解绑银行卡 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * 
	 * @param token
	 * <br>
	 *            token
	 ** @param from
	 * <br>
	 *            1:出借人解绑银行卡 ，2：借款人解绑银行卡
	 * @return
	 */
	@POST
	@Path("/untyingCard")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> untyingCard(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:untyingCard,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		if (!"1".equals(from) && !"2".equals(from)) {
			LOG.info("fn:untyingCard,参数错误！");
			result.put("state", "2");
			result.put("message", "参数错误！");
			result.put("data", null);
			return result;
		}
		String userName = null;
		String userId = null;
		try {
			String userInfoId = JedisUtils.get(token);
			if (StringUtils.isBlank(userInfoId)) {
				LOG.info("fn:untyingCard,系统超时");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}
			if ("1".equals(from)) {
				UserInfo userInfo = userInfoDao.getCgb(userInfoId);
				userName = userInfo.getRealName();
				userId = userInfo.getId();
				
				String orderId = IdGen.uuid();
				CgbUserBankCard userBankCard = new CgbUserBankCard();
				userBankCard.setUserId(userId);
				userBankCard.setState(CgbUserBankCard.CERTIFY_YES);
				List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
				String bankNo = null;
				String bankAccountNo = null;
				if (list != null&& list.size()!=0) {
					//存管保数据唯一
					CgbUserBankCard cubc = list.get(0);
					bankAccountNo = cubc.getBankAccountNo();
					bankNo = cubc.getBankNo();
				}
				CgbBigrechargeWhiteRecord white = new CgbBigrechargeWhiteRecord();
				white.setId(IdGen.uuid());
				white.setRequestNo(orderId);
				white.setPlatformId(PLATFORM_NO);
				white.setRealName(userName);
				white.setUserId(userId);
//				white.setBankCode(BankCodeEnum.getTextByText(null));
				white.setBankCode(bankNo);
				white.setBankNo(bankAccountNo);
				white.setStatus(WhiteStatusEnum.GRAY);
				white.setUserRole(UserRoleEnum.INVESTOR.getValue());
				white.setOperationDesc(null);
				white.setCreateDate(new Date());
				white.setUpdateDate(new Date());
				white.setDescription("记录个人解绑白名单：灰度");
				int whiteFlag = cgbBigrechargeWhiteRecordDao.insert(white);
				LOG.info("记录解绑白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
				
				
				// 出借人解绑银行卡--TODO 要修改回调地址
				Map<String, String> data = personBindCardService.untyingCard(userInfo.getId(),orderId);
				LOG.debug("data : {}", JSON.toJSONString(data));
				System.out.println("data :" + JSON.toJSONString(data));
				result.put("data", data);
			} else {
				// 借款人
				CreditUserInfo creditUserInfo = creditUserInfoService.get(userInfoId); // 借款人
				if (creditUserInfo != null) {
//					userName = creditUserInfo.getEnterpriseFullName();
//					userId = creditUserInfo.getId();
//					// 借款人解绑银行卡--TODO 要修改回调地址
//					Map<String, String> data = businessBindCardService.untyingCard(userInfoId);
//					LOG.debug("data : {}", JSON.toJSONString(data));
//					System.out.println("data :" + JSON.toJSONString(data));
//
//					result.put("data", data);
				} else {
					LOG.info("平台没有这个用户编号: userId=" + userInfoId + "------异步通知");
					LOG.info("fn:untyingCard,token 失效，操作超时！");
					result.put("state", "3");
					result.put("message", "token 失效，操作超时！");
					result.put("data", null);
					return result;
				}
			}
//			CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//			white.setUserId(userInfoId);
//			List<CgbBigrechargeWhite> reBigrechargeWhites = cgbBigrechargeWhiteDao.findList(white);
//			if (reBigrechargeWhites.size() > 0) {
//				for (CgbBigrechargeWhite white2 : reBigrechargeWhites) {
//					white.setBankCode(white2.getBankCode());
//					white.setBankNo(white2.getBankNo());
//					white.setRealName(white2.getRealName());
//					white.setId(white2.getId());
//					white.setPlatformId(white2.getPlatformId());
//					white.setOperationDesc(white2.getOperationDesc());
//					white.setUpdateDate(new Date());
//					white.setStatus(WhiteStatusEnum.GRAY);
//					int whiteFlag = cgbBigrechargeWhiteDao.update(white);
//					LOG.info("修改白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//				}
//			}
			
			
			result.put("state", "0");
			result.put("message", "解绑银行卡请求发送成功！");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:untyingCard,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: redirectUntyingCard <br>
	 * description: 解绑银行卡，同步回调 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectUntyingCard")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectUntyingCard(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) throws Exception {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		try {
			LOG.debug("解绑银行卡-redirect-start...");
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			if (verify) {
				// 借款人ID.
				JSONObject json = JSONObject.parseObject(respData);
				String userId = json.getString("platformUserNo");
				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					UserInfo userInfo = userInfoDao.getCgb(userId);
					if (userInfo != null) {
						LOG.info("出借人解绑银行卡，同步回调，userId:{}", userId);
						// 页面响应
						response.sendRedirect(RedirectUrlConfig.ACCOUNT_HOME_URL + "?id=" + userId);
					} else {
						// 借款人
						CreditUserInfo creditUserInfo = creditUserInfoService.get(userId); // 借款人
						if (creditUserInfo != null) {
							LOG.info("借款人解绑银行卡，同步回调，creditUserId:{}", userId);
						}
						// 页面响应
						response.sendRedirect(RedirectUrlConfig.UNBIND_BANKCARD_REDIRECT_URL + "?id=" + userId);
					}
				}
			}
			LOG.debug("解绑银行卡-redirect-end...");
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("验签错误，生成商户公钥失败", e);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("验签错误" + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new GeneralSecurityException("验签错误" + e.getMessage(), e);
		}
		return;
	}

	/**
	 * 
	 * methods: changeBankCard <br>
	 * description: 个人绑卡 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param bankCardNo
	 * <br>
	 *            银行卡号
	 * @param from
	 * <br>
	 *            请求平台.
	 * @param certNo
	 * <br>
	 *            身份证号
	 * @param realName
	 * <br>
	 *            真实姓名
	 * @param bankCardPhone
	 * <br>
	 *            银行预留手机号.
	 * @return
	 */
	@POST
	@Path("/changeBankCard")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> changeBankCard(@FormParam("bankCardNo") String bankCardNo, @FormParam("certNo") String certNo, @FormParam("realName") String realName, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bankCardPhone") String bankCardPhone, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		String userInfoId = JedisUtils.get(token);
		if (StringUtils.isBlank(userInfoId)) {
			LOG.info("fn:changeBankCard,系统超时");
			result.put("state", "4");
			result.put("message", "系统超时！");
			result.put("data", null);
			return result;
		}
		UserInfo userInfo = userInfoDao.getCgb(userInfoId);

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(ip) || StringUtils.isBlank(bankCardPhone)) {
			LOG.info("fn:changeBankCard,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */

		if (!FuncUtils.isBankCard(bankCardNo)) {
			LOG.info("fn:changeBankCard,开户银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 身份证号验证---个人绑卡不校验
		 */
		// if (!Validator.isIdCard(certNo)) {
		// LOG.info("fn:changeBankCard,身份证号有误！");
		// result.put("state", "6");
		// result.put("message", "身份证号有误，请核实后重新输入");
		// result.put("data", null);
		// return result;
		// }
		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			String orderId = IdGen.uuid();
			// 个人绑卡
			Map<String, String> data = personBindCardService.changeBankCard(userInfo, bankCardNo, certNo, realName, bankCardPhone,orderId);
			if (userInfo != null) {
				UserLog log = new UserLog();
				log.setId(String.valueOf(IdGen.randomLong()));
				log.setUserId(userInfo.getId());
				if (userInfo.getRealName() != null)
					log.setUserName(userInfo.getRealName() + userInfo.getName());
				else
					log.setUserName(userInfo.getName());
				log.setType("2");
				log.setCreateDate(new Date());
				if (userInfo.getRealName() != null)
					log.setRemark(userInfo.getRealName() + "个人绑卡请求请求已经发送成功");
				else
					log.setRemark(userInfo.getName() + "个人绑卡请求请求已经发送成功");
				userLogDao.insert(log);

				String jsonStr = BankUtils.getCardDetail(bankCardNo);
				JSONObject json = JSONObject.parseObject(jsonStr);
				String validated = json.getString("validated");
				/**
				 * @author fuwei
				 *         添加白名单：灰度
				 */
//				CgbBigrechargeWhite white2 = new CgbBigrechargeWhite();
//				white2.setBankNo(bankCardNo);
//				List<CgbBigrechargeWhite> whites = whiteDao.findList(white2);
//				if (whites.size() > 0) {
//					for (CgbBigrechargeWhite changeWhite : whites) {
//						CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//						white.setId(changeWhite.getId());
//						white.setPlatformId(PLATFORM_NO);
//						white.setRealName(userInfo.getName());
//						white.setUserId(userInfo.getId());
//						if ("true".equals(validated)) {
//							String bankName = BankEnum.getTextByValue(bankCardNo);
//							white.setBankCode(BankCodeEnum.getTextByText(bankName));
//						} else {
//							white.setBankCode(BankCodeEnum.getTextByText(null));
//						}
//						white.setBankNo(bankCardNo);
//						white.setStatus(WhiteStatusEnum.GRAY);
//						white.setUserRole(UserRoleEnum.INVESTOR.getValue());
//						white.setOperationDesc(null);
//						white.setUpdateDate(new Date());
//						int whiteFlag = whiteDao.update(white);
//						LOG.info("修改白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//					}
//				} else {
//					CgbBigrechargeWhite white = new CgbBigrechargeWhite();
//					white.setId(IdGen.uuid());
//					white.setPlatformId(PLATFORM_NO);
//					white.setRealName(userInfo.getName());
//					white.setUserId(userInfo.getId());
//					white.setBankCode(BankCodeEnum.getTextByText(null));
//					if ("true".equals(validated)) {
//						String bankName = BankEnum.getTextByValue(bankCardNo);
//						white.setBankCode(BankCodeEnum.getTextByText(bankName));
//					} else {
//						white.setBankCode(BankCodeEnum.getTextByText(null));
//					}
//					white.setBankNo(bankCardNo);
//					white.setStatus(WhiteStatusEnum.GRAY);
//					white.setUserRole(UserRoleEnum.INVESTOR.getValue());
//					white.setOperationDesc(null);
//					white.setCreateDate(new Date());
//					white.setUpdateDate(null);
//					int whiteFlag = whiteDao.insert(white);
//					LOG.info("添加白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
//				}
				
				CgbBigrechargeWhiteRecord white = new CgbBigrechargeWhiteRecord();
				white.setId(IdGen.uuid());
				white.setRequestNo(orderId);
				white.setPlatformId(PLATFORM_NO);
				white.setRealName(userInfo.getRealName());
				white.setUserId(userInfo.getId());
				if ("true".equals(validated)) {
					String bankName = BankEnum.getTextByValue(bankCardNo);
					white.setBankCode(BankCodeEnum.getTextByText(bankName));
				} else {
					white.setBankCode(BankCodeEnum.getTextByText(null));
				}
				white.setBankNo(bankCardNo);
				white.setStatus(WhiteStatusEnum.GRAY);
				white.setUserRole(UserRoleEnum.INVESTOR.getValue());
				white.setOperationDesc(null);
				white.setCreateDate(new Date());
				white.setUpdateDate(new Date());
				white.setDescription("记录个人绑卡白名单：灰度");
				int whiteFlag = cgbBigrechargeWhiteRecordDao.insert(white);
				LOG.info("记录个人绑卡白名单：灰度", whiteFlag == 1 ? "成功" : "失败");
				
				result.put("state", "0");
				result.put("message", "个人绑卡请求请求已经发送成功！");
				LOG.debug("data : {}", JSON.toJSONString(data));
				System.out.println("data :" + JSON.toJSONString(data));
				result.put("data", data);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "token失效，操作超时！");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:changeBankCard,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: redirectChangeBankCard <br>
	 * description: 个人绑卡，同步回调 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectChangeBankCard")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectChangeBankCard(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) throws Exception {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		try {
			LOG.debug("个人绑卡-redirect-start...");
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			if (verify) {
				// 借款人ID.
				JSONObject json = JSONObject.parseObject(respData);
				String userId = json.getString("platformUserNo");
				String orderId = json.getString("requestNo");

				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					LOG.info("个人绑卡成功");
				} else {
					LOG.info("个人绑卡失败");
				}
				// 页面响应. TODO
				response.sendRedirect(RedirectUrlConfig.ACCOUNT_HOME_URL + "?id=" + userId);

			}
			LOG.debug("个人绑卡-redirect-end...");
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("验签错误，生成商户公钥失败", e);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("验签错误" + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new GeneralSecurityException("验签错误" + e.getMessage(), e);
		}
		return;
	}

	/**
	 * 
	 * methods: modifyMobileExpand <br>
	 * description: 预留手机号更新 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 ** 
	 * @param from
	 * <br>
	 *            1:出借人解绑银行卡 ，2：借款人解绑银行卡
	 * @return
	 */
	@POST
	@Path("/modifyMobileExpand")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyMobileExpand(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:modifyMobileExpand,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		if (!"1".equals(from) && !"2".equals(from)) {
			LOG.info("fn:modifyMobileExpand,参数错误！");
			result.put("state", "2");
			result.put("message", "参数错误！");
			result.put("data", null);
			return result;
		}

		try {
			String userInfoId = JedisUtils.get(token);
			if (StringUtils.isBlank(userInfoId)) {
				LOG.info("fn:modifyMobileExpand,系统超时");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}
			if ("1".equals(from)) {
				UserInfo userInfo = userInfoDao.getCgb(userInfoId);
				// 出借人预留手机号更新--TODO 要修改回调地址
				Map<String, String> data = personBindCardService.modifyMobileExpand(userInfo.getId());
				LOG.debug("data : {}", JSON.toJSONString(data));
				System.out.println("data :" + JSON.toJSONString(data));
				result.put("data", data);
			} else {
				CreditUserInfo creditUserInfo = creditUserInfoService.get(userInfoId); // 借款人
				if (creditUserInfo != null) {
					// 借款人预留手机号更新--TODO 要修改回调地址
					Map<String, String> data = businessBindCardService.modifyMobileExpand(userInfoId);
					LOG.debug("data : {}", JSON.toJSONString(data));
					System.out.println("data :" + JSON.toJSONString(data));
					result.put("data", data);
				} else {
					LOG.info("平台没有这个用户编号: userId=" + userInfoId + "------异步通知");
					result.put("state", "3");
					result.put("message", "平台没有这个用户编号！");
					result.put("data", null);
					return result;
				}
			}
			result.put("state", "0");
			result.put("message", "预留手机号更新请求发送成功！");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:modifyMobileExpand,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: redirectModifyMobileExpand <br>
	 * description: 预留手机号更新，同步回调 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectModifyMobileExpand")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectModifyMobileExpand(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) throws Exception {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		LOG.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		LOG.info("keySerial:{},sign{}", keySerial, sign);
		try {
			LOG.debug(" 预留手机号更新-redirect-start...");
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			if (verify) {
				// 借款人ID.
				JSONObject json = JSONObject.parseObject(respData);
				String userId = json.getString("platformUserNo");
				String orderId = json.getString("requestNo");
				System.out.println("json==" + json.toJSONString());
				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					// .预留手机号更新会做四要素鉴权，四要素鉴权通过才能成功修改手机号。
					if (AccessTypeEnum.FULL_CHECKED.getValue().equals(json.getString("accessType"))) {
						UserInfo userInfo = userInfoDao.getCgb(userId);
						if (userInfo == null) {
							LOG.debug("平台没有这个出借人用户编号: userId=" + userId);
						} else {
							LOG.info(" 预留手机号更新成功");
						}
						// 页面响应.
						response.sendRedirect(RedirectUrlConfig.ACCOUNT_HOME_URL + "?id=" + userId);
					} else { // 四要素鉴权不通过
						LOG.info(" 预留手机号更新--四要素鉴权不通过");
					}
				} else {
					LOG.info(" 预留手机号更新失败");
				}
			}
			LOG.debug(" 预留手机号更新-redirect-end...");
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("验签错误，生成商户公钥失败", e);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("验签错误" + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new GeneralSecurityException("验签错误" + e.getMessage(), e);
		}
		return;
	}

	/**
	 * 
	 * methods: activateStockedUser <br>
	 * description: 会员激活 <br>
	 * author: Roy <br>
	 * date: 2019年10月23日 上午11:41:46
	 * 
	 * @param from
	 * @param platformUserNo
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/activateStockedUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> activateStockedUser(@FormParam("from") String from, @FormParam("platformUserNo") String platformUserNo, @FormParam("userRole") String userRole, @Context HttpServletRequest servletRequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(from) || StringUtils.isBlank(platformUserNo) || StringUtils.isBlank(userRole)) {
				LOG.info("借款人会员激活，缺少必要参数，from:{}，platformUserNo:{}，userRole:{}", from, platformUserNo, userRole);
				result.put("state", "2");
				result.put("message", "缺少必要参数！");
				result.put("data", null);
				return result;
			}
			// 见【用户授权列表】；此处可传多个值，传多个值时用“,”英文半角逗号分隔
			StringBuffer authListBuffer = new StringBuffer();
			if (UserRoleEnum.INVESTOR.getValue().equals(userRole)) {
				authListBuffer.append(AuthEnum.TENDER.getValue());
			} else if (UserRoleEnum.BORROWERS.getValue().equals(userRole)) {
				authListBuffer.append(AuthEnum.REPAYMENT.getValue());
			}
			Map<String, String> data = activateStockedUserService.activateStockedUser(platformUserNo, authListBuffer.toString(), RedirectUrlConfig.ACTIVATE_STOCKED_USER);
			result.put("state", "0");
			result.put("message", "<b>企业绑卡注册信息发送至存管行</b>");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "<b>系统异常</b>");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * methods: memberActivation <br>
	 * description: 会员激活 <br>
	 * 是否幂等性：否，接口模式：网关，异步通知：是 <br>
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param from
	 * <br>
	 *            1：出借人激活，2：借款人激活
	 * @return
	 */
	@POST
	@Path("/memberActivation")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> memberActivation(@FormParam("from") String from, @FormParam("token") String token, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:memberActivation,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 获取用户信息，拼装请求数据
		 */
		if ("1".equals(from.trim()) || "2".equals(from.trim())) {
			try {
				Map<String, String> data = new HashMap<String, String>();
				String userInfoId = JedisUtils.get(token);
				if (StringUtils.isBlank(userInfoId)) {
					LOG.info("fn:memberActivation,系统超时");
					result.put("state", "4");
					result.put("message", "系统超时！");
					result.put("data", null);
					return result;
				}
				LOG.info("userInfoId  == " + userInfoId);
				String tmp_userID = "";
				if ("1".equals(from.trim())) { // 表示出借人
					UserInfo userInfo = userInfoDao.getCgb(userInfoId);
					tmp_userID = userInfo.getId();
				} else if ("2".equals(from.trim())) {
					CreditUserInfo cUserInfo = creditUserInfoDao.get(userInfoId);
					if (cUserInfo == null) {
						LOG.info("fn:memberActivation,平台没有这个用户编号！");
						result.put("state", "2");
						result.put("message", "平台没有这个用户编号！");
						result.put("data", null);
						return result;
					}
					tmp_userID = cUserInfo.getId();
				}

				// 会员激活--
				data = personBindCardService.activateStockedUser(tmp_userID);
				LOG.debug("data : {}", JSON.toJSONString(data));
				System.out.println("data :" + JSON.toJSONString(data));
				result.put("data", data);

				result.put("state", "0");
				result.put("message", "会员激活已经发送成功！");
				LOG.debug("data : {}", JSON.toJSONString(data));
				System.out.println("data :" + JSON.toJSONString(data));
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				LOG.info("fn:memberActivation,系统错误！");
				result.put("state", "1");
				result.put("message", "系统错误！");
				result.put("data", null);
				return result;
			}
		} else {
			LOG.info("fn:memberActivation,参数错误！");
			result.put("state", "2");
			result.put("message", "参数错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: redirectMemberActivation <br>
	 * description: 会员激活，同步回调，只做页面跳转，不做逻辑处理 <br>
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectMemberActivation")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void redirectMemberActivation(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) throws Exception {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		try {
			LOG.info("会员激活-redirect-...start...");
			LOG.info("serviceName:{}", serviceName);
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			boolean verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
			JSONObject json = JSONObject.parseObject(respData);
			String platformUserNo = json.getString("platformUserNo");
			if (verify) {
				UserInfo userInfo = null;
				CreditUserInfo creditUserInfo = null;
				LOG.info("会员激活，平台用户编号:{}", platformUserNo);
				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					String auditStatus = json.getString("auditStatus");
					String bankcardNo = json.getString("bankcardNo");
					String bankcode = json.getString("bankcode");
					LOG.info("会员激活，同步回调，auditStatus:{}，bankcardNo:{}，bankcode:{}", auditStatus, bankcardNo, bankcode);
				} else {
					String errorCode = json.getString("errorCode");
					String errorMessage = json.getString("errorMessage");
					LOG.info("会员激活处理失败，errorCode:{}，errorMessage:{}", errorCode, errorMessage);
				}
				userInfo = userInfoDao.getCgb(platformUserNo);
				if (userInfo != null) { // 出借人
					response.sendRedirect(RedirectUrlConfig.LOGIN_URL);
				} else {
					creditUserInfo = creditUserInfoService.get(platformUserNo);
					if (creditUserInfo != null) { // 借款人
						response.sendRedirect(RedirectUrlConfig.UNBIND_BANKCARD_REDIRECT_URL + "?id=" + platformUserNo);
					}
				}
				LOG.debug(" 会员激活-redirect-end...");
			}
			LOG.info("会员激活-redirect-...end...");
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("验签错误，生成商户公钥失败", e);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("验签错误" + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new GeneralSecurityException("验签错误" + e.getMessage(), e);
		}
		return;
	}

	// @Autowired
	// private LanMaoReconciliationService reconciliationService;
	//
	// @POST
	// @Path("/memberActivation")
	// @Produces(MediaType.APPLICATION_JSON)
	// public Map<String, Object> memberActivation(){
	//
	// Map<String, Object> result = new HashMap<String, Object>();
	// String dateStr = "20191011";
	// try {
	// result = reconciliationService.confirmCheckFile(dateStr);
	// result.put("state", "0");
	// result.put("message", "会员激活已经发送成功！");
	// return result;
	// } catch (Exception e) {
	// e.printStackTrace();
	// LOG.info("fn:memberActivation,系统错误！");
	// result.put("state", "1");
	// result.put("message", "系统错误！");
	// result.put("data", null);
	// return result;
	// }
	//
	// }

}
