package com.power.platform.appversion;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.power.platform.bouns.services.UserAwardService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.common.utils.ZipUtils;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.service.CGBPayService;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.NewInvestService;
import com.power.platform.regular.service.UserInvestWebService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.type.UserStateType;
import com.power.platform.sys.type.UserType;
import com.power.platform.userinfo.dao.RegistUserDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.RegistService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Path("/app")
@Service("appVersionService")
@Produces(MediaType.APPLICATION_JSON)
public class AppVersionService {

	@Autowired
	private CGBPayService cGBPayService;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private NewInvestService newInvestService;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private RegistUserDao registUserDao;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private RegistService registService;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private UserAccountInfoDao userAccountInfoDao;
	@Autowired
	private UserAwardService userAwardService;
	@Autowired
	private UserInvestWebService userInvestWebService;
	@Autowired
	private ElectronicSignService electronicSignService;
	@Autowired
	private ElectronicSignDao electronicSignDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private ElectronicSignTranstailDao electronicSignTranstailDao;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private UserCashService userCashService;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;

	private static final String PAY_USER = Global.getConfig("payUserId");

	private static final Logger logger = Logger.getLogger(AppVersionService.class);

	/**
	 * 融资类型，1：应收账款.
	 */
	private static final String FINANCING_TYPE_1 = "1";
	/**
	 * 融资类型，2：订单融资.
	 */
	private static final String FINANCING_TYPE_2 = "2";

	@POST
	@Path("/appVersion")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> appVersion(@FormParam("from") String from) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空更新说明：
		if (StringUtils.isBlank(from)) {
			result.put("state", "1");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<String> featureList = new ArrayList<String>();
			featureList.add("1、快捷充值功能上线");
			featureList.add("2、修复部分已知bug");
//			featureList.add("3、新增更换银行预留手机号功能");
			// featureList.add("2、优选产品排序规则优化；");
			// featureList.add("3、修复bug，优化用户体验；");
			data.put("appversion", "2.5.0");
			data.put("systemMaintenance", "FALSE"); // 系统维护，开关，已弃用
			data.put("versionCode", 37);
			data.put("url", "https://www.cicmorgan.com/upload/cicMorGan-web-release_250_jiagu_sign.apk");
			data.put("isForce", "0"); // 0：强制更新；1：非强制更新；
			data.put("featureList", featureList);
			data.put("isOnline", "1"); // 0：系统维护；1：系统正常；
			data.put("isAnnualReportOnline", "1"); // 1：年报上线，2：年报下线.
			data.put("updateTime", "2019/11/05 18:00 - 11/07 24:00");// 0正在上线；1非上线
			result.put("state", "0");
			result.put("data", data);
			result.put("message", "获取app版本信息成功");
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
		}
		return result;
	}

	@POST
	@Path("/appIosVersion")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> appIosVersion(@FormParam("from") String from) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "1");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<String> featureList = new ArrayList<String>();
			featureList.add("1、快捷充值功能上线");
			featureList.add("2、修复部分已知bug");
//			featureList.add("1、完成海口联合农商银行存管接口2.0升级");
//			featureList.add("2、新增解绑和重新绑卡功能");
//			featureList.add("3、新增更换银行预留手机号功能");
			// featureList.add("1、标的加息展示优化；");
			// featureList.add("2、优选产品排序规则优化；");
			data.put("appversion", "2.5.0");
			data.put("systemMaintenance", "FALSE");  // 系统维护，开关，已弃用
			data.put("isForce", "0"); // 0：强制更新；1：非强制更新；
			data.put("featureList", featureList);
			data.put("isOnline", "1"); // 0：系统维护；1：系统正常；
			data.put("isAnnualReportOnline", "2"); // 1：年报上线，2：年报下线.
			data.put("updateTime", "2019/10/15 18:00 - 10/17 24:00");// 0正在上线；1非上线
			result.put("state", "0");
			result.put("data", data);
			result.put("message", "获取app版本信息成功");
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 修改企业用户信息
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/updateenterprise")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateenterprise(@FormParam("id") String id, @FormParam("enterpriseFullName") String enterpriseFullName, @FormParam("businessLicenseType") String businessLicenseType, @FormParam("businessLicense") String businessLicense, @FormParam("bankPermitCertNo") String bankPermitCertNo, @FormParam("taxRegCertNo") String taxRegCertNo, @FormParam("orgCode") String orgCode, @FormParam("agentPersonName") String agentPersonName, @FormParam("agentPersonPhone") String agentPersonPhone, @FormParam("agentPersonCertType") String agentPersonCertType, @FormParam("agentPersonCertNo") String agentPersonCertNo, @FormParam("corporationName") String corporationName, @FormParam("corporationCertType") String corporationCertType, @FormParam("corporationCertNo") String corporationCertNo, @FormParam("bankName") String bankName, @FormParam("bankCode") String bankCode,
			@FormParam("bankCardNo") String bankCardNo, @FormParam("bankCardName") String bankCardName, @FormParam("bankProvince") String bankProvince, @FormParam("bizType") String bizType, @FormParam("bankCity") String bankCity, @FormParam("issuerName") String issuerName, @FormParam("issuer") String issuer, @FormParam("supplierId") String supplierId, @FormParam("email") String email, @FormParam("registAddress") String registAddress) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// 压缩附件
			CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
			creditAnnexFile.setOtherId(id);
			List<CreditAnnexFile> list = creditAnnexFileService.findList(creditAnnexFile);
			List<File> urlList = new ArrayList<File>();
			if (list != null && list.size() > 0) {
				for (CreditAnnexFile annexFile : list) {
					if (annexFile.getType().equals("8") || annexFile.getType().equals("9") || annexFile.getType().equals("10")) {
						File file = new File("/data/upload/image/" + annexFile.getUrl());
						urlList.add(file);
					}
				}
			}
			if (urlList.size() < 3) {
				result.put("state", "2");
				result.put("message", "开户资料不齐！");
				return result;
			} else {
				String zipPath = "/data/upload/zip/" + enterpriseFullName + ".zip";
				FileOutputStream fos2 = new FileOutputStream(new File(zipPath));
				ZipUtils.toZip(urlList, fos2);
				String toMailAddr = SendMailUtil.toMailAddr; // 收件人海口存管行（林丹雅）
				// String cc = SendMailUtil.toMailAddrCC;//抄送
				String ccs = SendMailUtil.toMailAddrCCS; // 抄送邮件至ZTMG风控人员
				String cc = null;// 抄送
				// String ccs = null;//抄送
				// String cc = null;//抄送
				String subject = "中投摩根平台借款企业（" + enterpriseFullName + "）修改开户资料申请【系统发送】";
//				String message = "您好：附件为借款企业id:(" + id + ")修改开户申请资料，请查收审核，开户成功请回复邮件[yangxing@cicmorgan.com]谢谢。";
				String message = "您好：附件为借款企业id:(" + id + ")修改开户申请资料，请查收审核，开户成功请回复邮件[wangyanle@cicmorgan.com]谢谢。";
				List<String> listS = new ArrayList<String>();
				listS.add(zipPath);
				Boolean sendEmailBoolean = SendMailUtil.sendWithMsgAndAttachment(toMailAddr, cc, ccs, subject, message, listS, enterpriseFullName);
				if (sendEmailBoolean == true) {
					CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
					cicmorganBankCode = cicmorganBankCodeService.get(bankCode);
					bankCode = cicmorganBankCode.getBankCode();
					result.put("state", "0");
					result.put("message", "修改企业用户信息订单生成成功！");
					Map<?, ?> data = cGBPayService.updateenterprise(id, bizType, enterpriseFullName, businessLicenseType, businessLicense, bankPermitCertNo, taxRegCertNo, orgCode, agentPersonName, agentPersonPhone, agentPersonCertType, agentPersonCertNo, corporationName, corporationCertType, corporationCertNo, bankName, bankCode, bankCardNo, bankCardName, bankProvince, bankCity, issuerName, issuer, supplierId, email, registAddress);
					result.put("data", data);
					return result;
				} else {
					System.out.println("邮件发送失败");
					result.put("state", "3");
					result.put("message", "邮件发送失败");
					return result;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: p2pMemberAccountUpdateEnterprise <br>
	 * description: 企业用户信息修改 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月9日 下午9:28:51
	 * 
	 * @param creditUserInfo
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/p2pMemberAccountUpdateEnterprise")
	@ResponseBody
	public Map<String, Object> p2pMemberAccountUpdateEnterprise(@RequestBody CreditUserInfo creditUserInfo, @Context HttpServletRequest servletRequest) {

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

			// 压缩附件
			CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
			creditAnnexFile.setOtherId(creditUserInfo.getId());
			List<CreditAnnexFile> list = creditAnnexFileService.findList(creditAnnexFile);
			if (list.size() < 3) {
				result.put("state", "2");
				result.put("message", "<b>开户资料不齐</b>");
				return result;
			}

			// 向存管行发起企业用户信息修改请求
			Map<String, String> data = cGBPayService.p2pMemberAccountUpdateEnterprise(creditUserInfo);

			// 返回码说明
			String respMsg = data.get("respMsg");
			// 公共响应参数，两位响应码
			String respCode = data.get("respCode");
			if (null != respCode) {
				if ("00".equals(respCode)) { // 成功
					// 发送邮件
					// 文件资料列表添加
					List<File> urlList = new ArrayList<File>();
					if (list != null) {
						for (CreditAnnexFile annexFile : list) {
							if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_8.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_9.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_10.equals(annexFile.getType())) {
								File file = new File("/data/upload/image/" + annexFile.getUrl());
								if (file.exists()) {
									logger.info("file exists ...");
									urlList.add(file);
								} else {
									logger.info("file not exists, create it ...");
									continue;
								}
							}
						}
					}
					String zipPath = "/data/upload/zip/" + creditUserInfo.getEnterpriseFullName() + ".zip";
					File file = new File(zipPath);
					// 创建目标文件输出流
					FileOutputStream fos2 = new FileOutputStream(file);
					ZipUtils.toZip(urlList, fos2);
					if (file.exists()) {
						logger.info("zip file exists ...");
					} else {
						logger.info("zip file not exists, create it ...");
					}
					String toMailAddr = SendMailUtil.toMailAddr; // 开口存管银行联系人
					String ccs = SendMailUtil.toMailAddrCCS; // ZTMG风控人员
					String cc = null;
					String subject = "中投摩根平台借款企业（" + creditUserInfo.getEnterpriseFullName() + "）用户信息修改[系统发送]";
//					String message = "您好：附件为借款企业申请开户资料，请查收审核，开户成功请回复邮件[yangxing@cicmorgan.com]谢谢！";
					String message = "您好：附件为借款企业申请开户资料，请查收审核，开户成功请回复邮件[wangyanle@cicmorgan.com]谢谢！";
					List<String> listS = new ArrayList<String>();
					listS.add(zipPath);
					Boolean sendEmailBoolean = SendMailUtil.sendWithMsgAndAttachment(toMailAddr, cc, ccs, subject, message, listS, creditUserInfo.getEnterpriseFullName());
					if (sendEmailBoolean) {
						logger.info("send email successful ...");
						result.put("state", "0");
						result.put("message", "<b>" + respMsg + "</b>");
						return result;
					} else {
						logger.info("send email failure ...");
					}
					result.put("state", "0");
					result.put("message", "<b>" + respMsg + "</b>");
					return result;
				} else {
					// 页面消息提示
					result.put("state", "0");
					result.put("message", "<b>" + respMsg + "</b>");
					return result;
				}
			} else {
				// 公共响应参数，两位响应码
				String respSubCode = data.get("respSubCode");
				if (null != respSubCode) {
					if ("000000".equals(respSubCode)) { // 成功
						// 发送邮件
						// 文件资料列表添加
						List<File> urlList = new ArrayList<File>();
						if (list != null) {
							for (CreditAnnexFile annexFile : list) {
								if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_8.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_9.equals(annexFile.getType()) || CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_10.equals(annexFile.getType())) {
									File file = new File("/data/upload/image/" + annexFile.getUrl());
									if (file.exists()) {
										logger.info("file exists ...");
										urlList.add(file);
									} else {
										logger.info("file not exists, create it ...");
										continue;
									}
								}
							}
						}
						String zipPath = "/data/upload/zip/" + creditUserInfo.getEnterpriseFullName() + ".zip";
						File file = new File(zipPath);
						// 创建目标文件输出流
						FileOutputStream fos2 = new FileOutputStream(file);
						ZipUtils.toZip(urlList, fos2);
						if (file.exists()) {
							logger.info("zip file exists ...");
						} else {
							logger.info("zip file not exists, create it ...");
						}
						String toMailAddr = SendMailUtil.toMailAddr; // 开口存管银行联系人
						String ccs = SendMailUtil.toMailAddrCCS; // ZTMG风控人员
						String cc = null;
						String subject = "中投摩根平台借款企业（" + creditUserInfo.getEnterpriseFullName() + "）用户信息修改[系统发送]";
//						String message = "您好：附件为借款企业申请开户资料，请查收审核，开户成功请回复邮件[yangxing@cicmorgan.com]谢谢！";
						String message = "您好：附件为借款企业申请开户资料，请查收审核，开户成功请回复邮件[wangyanle@cicmorgan.com]谢谢！";
						List<String> listS = new ArrayList<String>();
						listS.add(zipPath);
						Boolean sendEmailBoolean = SendMailUtil.sendWithMsgAndAttachment(toMailAddr, cc, ccs, subject, message, listS, creditUserInfo.getEnterpriseFullName());
						if (sendEmailBoolean) {
							logger.info("send email successful ...");
							result.put("state", "0");
							result.put("message", "<b>" + respMsg + "</b>");
							return result;
						} else {
							logger.info("send email failure ...");
						}
						result.put("state", "0");
						result.put("message", "<b>" + respMsg + "</b>");
						return result;
					} else {
						// 页面消息提示
						result.put("state", "0");
						result.put("message", "<b>" + respMsg + "</b>");
						return result;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("webMemberAccountCreateEnterprise,系统错误");
			result.put("state", "1");
			result.put("message", "<b>系统错误</b>");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 更新老数据新增字段
	 * 
	 * @return
	 */
	@POST
	@Path("/updateUserInfoInvite")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateUserInfoInvite() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			result.put("state", "0");
			result.put("message", "更新老数据新增字段！");
			userInfoService.updateUserInfoInvite();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 更新生产环境借款人信息字段
	 * 
	 * @return
	 */
	@POST
	@Path("/updateCreditUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateCreditUserInfo() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			result.put("state", "0");
			result.put("message", "更新老数据新增字段！");
			creditUserInfoService.updateCreditUserInfo();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: cancelInvest <br>
	 * description: 取消投资. <br>
	 * author: Roy <br>
	 * date: 2019年3月7日 上午9:29:30
	 * 
	 * @param orderId
	 * @return
	 */
	@POST
	@Path("/cancelInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> cancelInvest(@FormParam("orderId") String orderId) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(orderId)) {
			result.put("state", "1");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			Map<String, String> cancelInvest = newInvestService.cancelInvest(orderId);
			if (cancelInvest.get("respCode") != null) {
				if ("00".equals(cancelInvest.get("respCode"))) { // 成功.
					logger.info("取消投资成功，资金存管系统将用户冻结资金解冻，退回投资用户。");
					WloanTermInvest invest = wloanTermInvestService.get(orderId);
					if (null != invest) {
						if (invest.getWloanTermProject() != null) {
							WloanTermProject project = wloanTermProjectDao.get(invest.getWloanTermProject().getId());
							if (null != project) {
								// 取消出借，短消息提醒.
								weixinSendTempMsgService.sendCancelInvestMsg(invest, project);
							}
						}
					}
				}
			}

			result.put("state", "0");
			result.put("message", "取消出借，程序执行完成！");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * <世界杯活动>出借排行榜
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/worldCupInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> worldCupInvest(@FormParam("from") String from, @FormParam("date") String date) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> investList = new ArrayList<Map<String, Object>>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(date)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {

			String beginBeginDate = date + " 00:00:00";
			String endBeginDate = date + " 23:59:59";
			Map<String, Object> data = new HashMap<String, Object>();
			WloanTermInvest wloanTermInvest = new WloanTermInvest();
			wloanTermInvest.setBeginBeginDate(DateUtils.parseDate(beginBeginDate));
			wloanTermInvest.setEndBeginDate(DateUtils.parseDate(endBeginDate));
			List<WloanTermInvest> list = wloanTermInvestDao.findWorldCupInvest(wloanTermInvest);
			for (WloanTermInvest invest : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("phone", Util.hideString(invest.getUserInfo().getName(), 3, 4));
				map.put("amount", NumberUtils.scaleDouble(invest.getAmount()));
				map.put("investDate", df.format(invest.getBeginDate()));
				investList.add(map);
			}
			data.put("investList", investList);// 查询成功！
			result.put("state", "0");
			result.put("message", "查询成功！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;

	}

	/**
	 * 出借用户信息迁移
	 * 
	 * @return
	 */
	@POST
	@Path("/importUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> importUserInfo() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// N1.查询所有出借用户
			List<UserInfo> list = userInfoService.findLLUserList();
			if (list != null && list.size() > 0) {
				int i = 0;
				for (UserInfo userInfo : list) {
					// N2.进行用户信息迁移
					userInfoService.importUserInfo(userInfo, "01");
					System.out.println(i++ + "迁移成功");
				}
			}
			result.put("state", "0");
			result.put("message", "出借用户信息迁移成功");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 借款用户信息迁移
	 * 
	 * @return
	 */
	@POST
	@Path("/importCreditUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> importCreditUserInfo() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// N1.查询所有连连正在还款中的借款用户
			WloanTermProject oldProject = new WloanTermProject();
			oldProject.setEndTimeToOnline("2017-12-23 00:00:00");
			oldProject.setState(WloanTermProjectService.REPAYMENT);
			List<WloanTermProject> list = wloanTermProjectService.findList(oldProject);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					WloanSubject creditUser = list.get(i).getWloanSubject();
					String phone = creditUser.getCashierBankPhone() + "L" + i;
					// 先根据手机号查询是否已经注册,已注册则不进行注册流程,直接更改融资主体中LoanApplyId字段
					UserInfo entity = new UserInfo();
					entity.setName(phone);
					List<UserInfo> userList = userInfoService.findList(entity);
					if (userList != null && userList.size() > 0) {
						UserInfo user = userList.get(0);
						creditUser.setLoanApplyId(user.getId());
						int q = wloanSubjectDao.update(creditUser);
						if (q > 0) {
							System.out.println("[已注册]更新借款用户ID字段成功");
						}
					} else {

						// 借款用户注册(借款类型用户信息)
						UserInfo userInfo = new UserInfo();
						userInfo.setId(String.valueOf(IdGen.randomLong()));
						userInfo.setName(phone);
						userInfo.setRealName(creditUser.getCashierUser());
						userInfo.setPwd(EncoderUtil.encrypt("123456a"));
						userInfo.setRecomType(0);
						userInfo.setUserType(UserType.LOAN);// 借款用户
						userInfo.setState(UserStateType.FORBIDDEN);
						userInfo.setRegisterFrom(StringUtils.toInteger(1));
						userInfo.setCreateDate(new Date());
						userInfo.setEmailChecked(UserInfo.BIND_EMAIL_NO);// 邮箱未验证
						userInfo.setCertificateChecked(UserInfo.CERTIFICATE_YES);// 实名认证
						userInfo.setCertificateNo(creditUser.getCashierIdCard());// 身份证号
						userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);// 绑定银行卡
						userInfo.setRegisterDate(new Date());
						userInfo.setLastLoginDate(new Date());
						userInfo.setAccountId(String.valueOf(IdGen.randomLong()));
						userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO); // 未开通银行存管
						int a = registUserDao.insert(userInfo);
						if (a > 0) {
							CgbUserAccount userAccountInfo = new CgbUserAccount();
							System.out.println("借款用户注册插入客户表完成");
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
							userAccountInfo.setUserInfo(userInfo);
							// 同时生成客户账户
							int b = cgbUserAccountDao.insert(userAccountInfo);
							if (b > 0) {
								Map<String, String> resultMaps = userInfoService.importCreditUserInfo(userInfo, "02", creditUser.getCashierBankPhone());
								if (resultMaps.get("respSubCode").equals("000000")) {
									creditUser.setLoanApplyId(userInfo.getId());
									int c = wloanSubjectDao.update(creditUser);
									if (c > 0) {
										System.out.println("[注册后]更新借款用户ID字段成功");

									}
								}
							}
						}
					}
				}
			}
			result.put("state", "0");
			result.put("message", "借款用户信息迁移成功");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 标的信息迁移
	 * 
	 * @return
	 */
	@POST
	@Path("/importBidInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> importBidInfo() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// N1.查询连连定期项目数据(2017-12-23之前)
			WloanTermProject oldProject = new WloanTermProject();
			oldProject.setEndTimeToOnline("2017-12-23 00:00:00");
			oldProject.setState(WloanTermProjectService.REPAYMENT);
			List<WloanTermProject> list = wloanTermProjectService.findList(oldProject);
			if (list != null && list.size() > 0) {
				for (WloanTermProject project : list) {
					if (project.getId().equals("1abecf140a8b41148d9bb36d898c77e5")) {
						System.out.println("fgrgdf");
					}
					// N2.标的信息迁移
					Map<String, String> resultMaps = wloanTermProjectService.importBidInfo(project);
					if (resultMaps.get("respSubCode").equals("000000")) {
						// 更新项目的是否代偿字段 代偿人ID
						project.setIsReplaceRepay(WloanTermProject.IS_REPLACE_REPAY_1);
						project.setReplaceRepayId(PAY_USER);
						int pro = wloanTermProjectDao.update(project);
						if (pro > 0) {
							System.out.println("标的信息代偿人字段更新成功");
						}
					} else if (resultMaps.get("respSubCode").equals("200107")) {
						// 更新项目的是否代偿字段 代偿人ID
						project.setIsReplaceRepay(WloanTermProject.IS_REPLACE_REPAY_1);
						project.setReplaceRepayId(PAY_USER);
						int pro = wloanTermProjectDao.update(project);
						if (pro > 0) {
							System.out.println("标的信息代偿人字段更新成功");
						}
					}
				}
			}
			result.put("state", "0");
			result.put("message", "出借用户信息迁移");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 投资单信息迁移(API)
	 * 
	 * @return
	 */
	@POST
	@Path("/investOrderImport")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> investOrderImport() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// N1.查询连连定期项目数据(2017-12-23之前)
			WloanTermProject oldProject = new WloanTermProject();
			oldProject.setEndTimeToOnline("2017-12-23 00:00:00");
			oldProject.setState(WloanTermProjectService.REPAYMENT);
			List<WloanTermProject> list = wloanTermProjectService.findList(oldProject);
			if (list != null && list.size() > 0) {
				for (WloanTermProject project : list) {
					// 查询该项目投资成功的记录
					WloanTermInvest invest = new WloanTermInvest();
					invest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
					invest.setWloanTermProject(project);
					List<WloanTermInvest> investList = wloanTermInvestDao.findList(invest);
					System.out.println("项目ID为" + project.getId() + ",共有投资笔数为" + investList.size());
					if (investList != null && investList.size() > 0) {
						for (WloanTermInvest wloanTermInvest : investList) {
							Map<String, String> resultMaps = wloanTermProjectService.orderImport(wloanTermInvest);
							if (resultMaps.get("respSubCode").equals("000000")) {
								// 用户账户 待收本金+ 待收收益+ 账户总额+
								Double investAmount = wloanTermInvest.getAmount();
								Double investRate = 0d;
								// 根据出借Id查询未还利息
								WloanTermUserPlan userPlan = new WloanTermUserPlan();
								userPlan.setWloanTermInvest(wloanTermInvest);
								userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
								List<WloanTermUserPlan> planList = wloanTermUserPlanDao.findList(userPlan);
								if (planList != null && planList.size() > 0) {
									for (WloanTermUserPlan wloanTermUserPlan : planList) {
										if (wloanTermUserPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2)) {
											investRate = NumberUtils.scaleDouble(investRate) + NumberUtils.scaleDouble(wloanTermUserPlan.getInterest());
										} else if (wloanTermUserPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1)) {
											investRate = NumberUtils.scaleDouble(investRate) + NumberUtils.scaleDouble(wloanTermUserPlan.getInterest() - investAmount);
										}
									}
								}
								System.out.println("未还利息为" + investRate);

								CgbUserAccount cgbAccount = cgbUserAccountDao.getUserAccountInfo(wloanTermInvest.getUserId());
								if (cgbAccount != null) {
									logger.info("存管宝账户变更前账户总额为" + cgbAccount.getTotalAmount() + "&待收本金为" + cgbAccount.getRegularDuePrincipal() + "&待收收益为" + cgbAccount.getRegularDueInterest());
									System.out.println("存管宝账户变更前账户总额为" + cgbAccount.getTotalAmount() + "&待收本金为" + cgbAccount.getRegularDuePrincipal() + "&待收收益为" + cgbAccount.getRegularDueInterest());
									cgbAccount.setRegularDuePrincipal(NumberUtils.scaleDouble(cgbAccount.getRegularDuePrincipal()) + investAmount);
									cgbAccount.setRegularDueInterest(NumberUtils.scaleDouble(cgbAccount.getRegularDueInterest()) + NumberUtils.scaleDouble(investRate));
									cgbAccount.setTotalAmount(NumberUtils.scaleDouble(cgbAccount.getTotalAmount()) + NumberUtils.scaleDouble(investAmount) + NumberUtils.scaleDouble(investRate));
									logger.info("存管宝账户变更后账户总额为" + NumberUtils.scaleDouble(cgbAccount.getTotalAmount()) + "&待收本金为" + NumberUtils.scaleDouble(cgbAccount.getRegularDuePrincipal()) + "&待收收益为" + NumberUtils.scaleDouble(cgbAccount.getRegularDueInterest()));
									System.out.println("存管宝账户变更后账户总额为" + NumberUtils.scaleDouble(cgbAccount.getTotalAmount()) + "&待收本金为" + NumberUtils.scaleDouble(cgbAccount.getRegularDuePrincipal()) + "&待收收益为" + NumberUtils.scaleDouble(cgbAccount.getRegularDueInterest()));
									int i = cgbUserAccountDao.update(cgbAccount);
									if (i > 0) {
										System.out.println("存管宝账户已更新");
										UserAccountInfo userAccount = userAccountInfoDao.getUserAccountInfo(wloanTermInvest.getUserId());
										if (userAccount != null) {
											logger.info("连连账户变更前账户总额为" + userAccount.getTotalAmount() + "&待收本金为" + userAccount.getRegularDuePrincipal() + "&待收收益为" + userAccount.getRegularDueInterest());
											System.out.println("连连账户变更前账户总额为" + userAccount.getTotalAmount() + "&待收本金为" + userAccount.getRegularDuePrincipal() + "&待收收益为" + userAccount.getRegularDueInterest());
											userAccount.setRegularDuePrincipal(NumberUtils.scaleDouble(userAccount.getRegularDuePrincipal()) - NumberUtils.scaleDouble(investAmount));
											userAccount.setRegularDueInterest(NumberUtils.scaleDouble(userAccount.getRegularDueInterest()) - NumberUtils.scaleDouble(investRate));
											userAccount.setTotalAmount(NumberUtils.scaleDouble(userAccount.getTotalAmount()) - NumberUtils.scaleDouble(investAmount) - NumberUtils.scaleDouble(investRate));
											logger.info("连连账户变更后账户总额为" + NumberUtils.scaleDouble(userAccount.getTotalAmount()) + "&待收本金为" + NumberUtils.scaleDouble(userAccount.getRegularDuePrincipal()) + "&待收收益为" + NumberUtils.scaleDouble(userAccount.getRegularDueInterest()));
											System.out.println("连连账户变更后账户总额为" + NumberUtils.scaleDouble(userAccount.getTotalAmount()) + "&待收本金为" + NumberUtils.scaleDouble(userAccount.getRegularDuePrincipal()) + "&待收收益为" + NumberUtils.scaleDouble(userAccount.getRegularDueInterest()));
											int j = userAccountInfoDao.update(userAccount);
											if (j > 0) {
												System.out.println("连连账户已更新");
											}
										}
									}
								}
							}
						}
					}
				}
			}
			result.put("state", "0");
			result.put("message", "投资单信息迁移");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 更新用户兑奖记录状态字段以适应新的状态规则
	 * 
	 * @return
	 */
	@POST
	@Path("/updateUserAwardInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateUserAwardInfo() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			result.put("state", "0");
			result.put("message", "更新用户兑奖记录状态字段");
			userAwardService.updateUserAwardInfo();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 合同电子签章
	 * 
	 * @return
	 */
	@POST
	@Path("/updateUserInvestSign")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> updateUserInvestSign() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			WloanTermInvest invest = new WloanTermInvest();
			invest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
			invest.setBeginBeginDate(sdf.parse("2018-07-02 21:56:59"));
			invest.setEndBeginDate(sdf.parse("2018-07-17 20:00:00"));
			List<WloanTermInvest> investList = wloanTermInvestDao.findList(invest);
			logger.info("共查出未有电子签章的个数为" + investList.size());
			if (investList != null && investList.size() > 0) {
				for (WloanTermInvest wloanTermInvest : investList) {
					synchronized (this) {
						if (wloanTermInvest.getContractPdfPath() != null) {
							String pdfPath = wloanTermInvest.getContractPdfPath();
							pdfPath = "/data" + pdfPath;
							logger.info("开始签署电子签章" + wloanTermInvest.getContractPdfPath() + "===========>>>");
							System.out.println("开始签署电子签章" + wloanTermInvest.getContractPdfPath());

							if (wloanTermInvest.getWloanTermProject().getId() != null) {
								WloanTermProject project = wloanTermProjectDao.get(wloanTermInvest.getWloanTermProject().getId());
								UserInfo user = userInfoService.getCgb(wloanTermInvest.getUserId());
								if (project != null) {

									if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链项目.
										CreditUserApply creditUserApply = creditUserApplyService.get(project.getCreditUserApplyId()); // 借款申请.
										if (creditUserApply != null) {
											String financingType = creditUserApply.getFinancingType(); // 融资类型.
											if (FINANCING_TYPE_1.equals(financingType)) {// 应收账款转让

												// 生成电子签章
												System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
												userInvestWebService.createElectronicSign(pdfPath, user, project.getCreditUserApplyId(), project.getProjectProductType());
												wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
												wloanTermInvestDao.update(wloanTermInvest);
											} else if (FINANCING_TYPE_2.equals(financingType)) {// 订单融资
												System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
												userInvestWebService.createElectronicSign(pdfPath, user, project.getCreditUserApplyId(), project.getProjectProductType());
												wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
												wloanTermInvestDao.update(wloanTermInvest);
											} else {// 应收账款让
												// 生成电子签章
												System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
												userInvestWebService.createElectronicSign(pdfPath, user, project.getCreditUserApplyId(), project.getProjectProductType());
												wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
												wloanTermInvestDao.update(wloanTermInvest);
											}
										}
									} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
										// 生成电子签章
										System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
										userInvestWebService.createElectronicSign(pdfPath, user, project.getWloanSubject().getLoanApplyId(), project.getProjectProductType());
										wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
										wloanTermInvestDao.update(wloanTermInvest);
									}
								}
							}
						}

					}
				}
			}
			result.put("state", "0");
			result.put("message", "更新合同电子签章");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 个人还款计划,合同电子签章
	 * 
	 * @return
	 */
	@POST
	@Path("/updateUserContract")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> updateUserContract(@FormParam("investId") String investId) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(investId)) {
				result.put("state", "2");
				result.put("message", "缺少必要参数");
				return result;
			}
			WloanTermInvest userInvest = wloanTermInvestDao.get(investId);
			if (userInvest != null) {
				WloanTermProject project = wloanTermProjectDao.get(userInvest.getWloanTermProject().getId());
				UserInfo userInfo = userInfoService.getCgb(userInvest.getUserId());
				if (project != null) {
					logger.info("个人还款计划,合同生成开始=========>>>" + investId);
					userInvestWebService.createUserSelfPlanAndContract(project, userInvest, userInfo);
					logger.info("<<<==================个人还款计划,合同生成结束");
				}
			} else {
				result.put("state", "3");
				result.put("message", "未查询到投资记录");
				return result;
			}
			result.put("state", "0");
			result.put("message", "个人还款计划,合同电子签章生成成功");
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 更新用户提现记录状态字段
	 * 
	 * @return
	 */
	@POST
	@Path("/updateUserCashState")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateUserCashState() {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			result.put("state", "0");
			result.put("message", "更新用户兑奖记录状态字段");
			userCashService.updateUserCashState();
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

}
