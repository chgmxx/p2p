package com.power.platform.cgb.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.common.utils.ZipUtils;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.account.service.BusinessBindCardService;
import com.power.platform.lanmao.account.service.PersonBindCardService;
import com.power.platform.pay.service.CGBPayService;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.utils.Validator;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;
import com.timevale.tgtext.text.pdf.da;

/**
 * 银行存管支付通道
 *
 * @author caozhi
 *
 */
@Component
@Path("/cgbPay")
@Service("cGBPayRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CGBPayRestService {

	private static final Logger LOG = LoggerFactory.getLogger(CGBPayRestService.class);

	@Autowired
	private CGBPayService cGBPayService;
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
	/**
	 * 用户开户PC端
	 * 方法: bindCardWeb <br>
	 * 描述: 实名认证(pc、wap). <br>
	 * 作者: Mr.Cao <br>
	 * 时间: 2016年5月6日 下午21:51:04
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
	// @Path("/accountCreateWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> accountCreateWeb(@FormParam("bankCardNo") String bankCardNo, @FormParam("certNo") String certNo, @FormParam("realName") String realName, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bankCardPhone") String bankCardPhone, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		String userInfoId = JedisUtils.get(token);
		// 重新获取一遍出借人信息(解决一些字段重新赋值的情况).
		UserInfo userInfo = null;
		userInfo = userInfoDao.get(userInfoId);
		if (null == userInfo) {
			userInfo = userInfoDao.getCgb(userInfoId);
		}

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(certNo) || StringUtils.isBlank(realName) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(ip) || StringUtils.isBlank(bankCardPhone)) {
			LOG.info("fn:accountCreateWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */

		if (!FuncUtils.isBankCard(bankCardNo)) {
			LOG.info("fn:accountCreateWeb,开户银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(certNo)) {
			LOG.info("fn:accountCreateWeb,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			result.put("state", "0");
			result.put("message", "PC端开户信息生成成功！");
			Map<?, ?> data = cGBPayService.accountCreateWeb(bankCardNo, certNo, realName, ip, bankCardPhone, token);

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
					log.setRemark(userInfo.getRealName() + "PC端开户信息生成成功");
				else
					log.setRemark(userInfo.getName() + "PC端开户信息生成成功");
				userLogDao.insert(log);
			}

			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:accountCreateWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * @测试接口--cp端 ：个人绑卡注册
	 * @return
	 */
	@POST
	@Path("/accountCreateWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> personalRegisterExpand(@FormParam("bankCardNo") String bankCardNo, @FormParam("certNo") String certNo, @FormParam("realName") String realName, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bankCardPhone") String bankCardPhone, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		String userInfoId = JedisUtils.get(token);
		// 重新获取一遍出借人信息(解决一些字段重新赋值的情况).
		UserInfo userInfo = null;
		userInfo = userInfoDao.get(userInfoId);
		if (null == userInfo) {
			userInfo = userInfoDao.getCgb(userInfoId);
		}

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(certNo) || StringUtils.isBlank(realName) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(ip) || StringUtils.isBlank(bankCardPhone)) {
			LOG.info("fn:accountCreateWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */

		if (!FuncUtils.isBankCard(bankCardNo)) {
			LOG.info("fn:accountCreateWeb,开户银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(certNo)) {
			LOG.info("fn:accountCreateWeb,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			result.put("state", "0");
			result.put("message", "PC端开户信息生成成功！");
			 Map<?, ?> data = cGBPayService.accountCreateWeb(bankCardNo, certNo, realName, ip, bankCardPhone, token);
			//个人绑卡注册
//			Map<String, String> data = personBindCardService.cardRegister(userInfo,bankCardNo, certNo, realName,bankCardPhone);
			//解绑银行卡
//			Map<String, String> data = personBindCardService.untyingCard(userInfo.getId());
			//个人绑卡
//			Map<String, String> data = personBindCardService.changeBankCard(userInfo,bankCardNo, certNo, realName, ip, bankCardPhone, token);
			//修改密码
//			Map<String, String> data = personBindCardService.resetPassword(userInfo.getId());
			//验证密码
//			Map<String, String> data = personBindCardService.checkPassword(userInfo.getId());
			//预留手机号更新
//			Map<String, String> data = personBindCardService.modifyMobileExpand(userInfo.getId());
			//企业绑卡注册
//			String token, 
//			String bizType = "02";
//			String enterpriseFullName= "嘉扬软件股份有限公司";
//			String businessLicenseType= "USCC";
//			String businessLicense= "91110108587665983J";
//			String bankPermitCertNo= "331003175451";
//			String taxRegCertNo= "331003175451";
//			String orgCode= "331003175451";
//			String agentPersonName= "周文敏";
//			String agentPersonPhone= "13001110600";
//			String agentPersonCertType= "IDC";
//			String agentPersonCertNo= "210106199106240912";
//			String corporationName= "周文敏";
//			String corporationCertType= "IDC";
//			String corporationCertNo= "210106199106240912";
//			String bankName= "北京银行";
//			String bankCode= "BJCN";
//			bankCardNo= "6226300007734558";
//			String bankCardName= "北京银行";
//			String bankProvince= "北京市";
//			String bankCity= "海淀区";
//			String issuerName= "北京银行";
//			String issuer= "0001111";
//			String supplierId= "";
//			Map<String, String> data = businessBindCardService.cardRegister(token, bizType, enterpriseFullName, 
//					businessLicenseType, businessLicense, bankPermitCertNo, taxRegCertNo, 
//					orgCode, agentPersonName, agentPersonPhone, agentPersonCertType,
//					agentPersonCertNo, corporationName, corporationCertType, corporationCertNo, 
//					bankName, bankCode, bankCardNo, bankCardName, bankProvince, bankCity, 
//					issuerName, issuer, supplierId);
			//企业绑卡
//			Map<String, String> data = businessBindCardService.bindCard(userInfo.getId(),bankCardNo,bankCode);
			//企业信息修改
//			Map<String, String> data = businessBindCardService.informationUpdate(userInfo.getId());

			
			LOG.debug("data : {}", JSON.toJSONString(data));
			System.out.println("data :"+JSON.toJSONString(data));
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:accountCreateWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 企业开户（PC）
	 * 
	 * @param enterpriseFullName
	 * @param businessLicenseType
	 * @param businessLicense
	 * @param bankPermitCertNo
	 * @param taxRegCertNo
	 * @param orgCode
	 * @param agentPersonName
	 * @param agentPersonPhone
	 * @param agentPersonCertType
	 * @param agentPersonCertNo
	 * @param corporationName
	 * @param corporationCertType
	 * @param corporationCertNo
	 * @param bankCardNo
	 * @param supplierId
	 *            借款户
	 * @param middlemenId
	 *            代偿户
	 * @return
	 */
	@POST
//	@Path("/accountCreateByCompany")
	public Map<String, Object> accountCreateByCompany(@FormParam("token") String token, @FormParam("enterpriseFullName") String enterpriseFullName, @FormParam("businessLicenseType") String businessLicenseType, @FormParam("businessLicense") String businessLicense, @FormParam("bankPermitCertNo") String bankPermitCertNo, @FormParam("taxRegCertNo") String taxRegCertNo, @FormParam("orgCode") String orgCode, @FormParam("agentPersonName") String agentPersonName, @FormParam("agentPersonPhone") String agentPersonPhone, @FormParam("agentPersonCertType") String agentPersonCertType, @FormParam("agentPersonCertNo") String agentPersonCertNo, @FormParam("corporationName") String corporationName, @FormParam("corporationCertType") String corporationCertType,
			@FormParam("corporationCertNo") String corporationCertNo, @FormParam("bankName") String bankName, @FormParam("bankCode") String bankCode, @FormParam("bankCardNo") String bankCardNo, @FormParam("bankCardName") String bankCardName, @FormParam("bankProvince") String bankProvince, @FormParam("bizType") String bizType, @FormParam("bankCity") String bankCity, @FormParam("issuerName") String issuerName, @FormParam("issuer") String issuer, @FormParam("supplierId") String supplierId, @Context HttpServletRequest servletRequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		// // 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(bizType) || StringUtils.isBlank(enterpriseFullName) || StringUtils.isBlank(businessLicenseType) || StringUtils.isBlank(businessLicense) || StringUtils.isBlank(bankPermitCertNo) || StringUtils.isBlank(agentPersonName) || StringUtils.isBlank(agentPersonPhone) || StringUtils.isBlank(agentPersonCertType) || StringUtils.isBlank(agentPersonCertNo) || StringUtils.isBlank(corporationName) || StringUtils.isBlank(corporationCertType) || StringUtils.isBlank(corporationCertNo) || StringUtils.isBlank(bankName) || StringUtils.isBlank(bankCode) || StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(bankCardName) || StringUtils.isBlank(bankProvince) || StringUtils.isBlank(bankCity) || StringUtils.isBlank(issuerName)
				|| StringUtils.isBlank(issuer) || StringUtils.isBlank(supplierId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		/**
		 * 银行卡验证
		 */
		/*
		 * 
		 * if (!FuncUtils.isBankCard(bankCardNo)) {
		 * LOG.info("accountCreateByCompany,银行卡号有误！");
		 * result.put("state", "5");
		 * result.put("message", "银行卡号有误，请核实后重新输入");
		 * result.put("data", null);
		 * return result;
		 * }
		 */

		/**
		 * 身份证号验证
		 */
		/*
		 * if (!Validator.isIdCard(agentPersonCertNo)) {
		 * LOG.info("accountCreateByCompany,联系人身份证号有误！");
		 * result.put("state", "6");
		 * result.put("message", "身份证号有误，请核实后重新输入");
		 * result.put("data", null);
		 * return result;
		 * }
		 * if (!Validator.isIdCard(corporationCertNo)) {
		 * LOG.info("accountCreateByCompany,法人身份证号有误！");
		 * result.put("state", "6");
		 * result.put("message", "身份证号有误，请核实后重新输入");
		 * result.put("data", null);
		 * return result;
		 * }
		 */

		try {
			CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
			// Page<CicmorganBankCode> page = new Page<CicmorganBankCode>();
			cicmorganBankCode = cicmorganBankCodeService.get(bankCode);
			bankCode = cicmorganBankCode.getBankCode();
			result.put("state", "0");
			result.put("message", "PC端开户信息生成成功！");
			Map<?, ?> data = cGBPayService.accountCreateByCompany(token, bizType, enterpriseFullName, businessLicenseType, businessLicense, bankPermitCertNo, taxRegCertNo, orgCode, agentPersonName, agentPersonPhone, agentPersonCertType, agentPersonCertNo, corporationName, corporationCertType, corporationCertNo, bankName, bankCode, bankCardNo, bankCardName, bankProvince, bankCity, issuerName, issuer, supplierId);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("accountCreateByCompany,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	/**
	 * 测试接口--企业绑卡注册
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/accountCreateByCompany")
	public Map<String, Object> enterpriseRegister(@FormParam("token") String token, @FormParam("enterpriseFullName") String enterpriseFullName, @FormParam("businessLicenseType") String businessLicenseType, @FormParam("businessLicense") String businessLicense, @FormParam("bankPermitCertNo") String bankPermitCertNo, @FormParam("taxRegCertNo") String taxRegCertNo, @FormParam("orgCode") String orgCode, @FormParam("agentPersonName") String agentPersonName, @FormParam("agentPersonPhone") String agentPersonPhone, @FormParam("agentPersonCertType") String agentPersonCertType, @FormParam("agentPersonCertNo") String agentPersonCertNo, @FormParam("corporationName") String corporationName, @FormParam("corporationCertType") String corporationCertType,
			@FormParam("corporationCertNo") String corporationCertNo, @FormParam("bankName") String bankName, @FormParam("bankCode") String bankCode, @FormParam("bankCardNo") String bankCardNo, @FormParam("bankCardName") String bankCardName, @FormParam("bankProvince") String bankProvince, @FormParam("bizType") String bizType, @FormParam("bankCity") String bankCity, @FormParam("issuerName") String issuerName, @FormParam("issuer") String issuer, @FormParam("supplierId") String supplierId, @Context HttpServletRequest servletRequest) {

		Map<String, Object> result = new HashMap<String, Object>();
		// // 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(bizType) || StringUtils.isBlank(enterpriseFullName) || StringUtils.isBlank(businessLicenseType) || StringUtils.isBlank(businessLicense) || StringUtils.isBlank(bankPermitCertNo) || StringUtils.isBlank(agentPersonName) || StringUtils.isBlank(agentPersonPhone) || StringUtils.isBlank(agentPersonCertType) || StringUtils.isBlank(agentPersonCertNo) || StringUtils.isBlank(corporationName) || StringUtils.isBlank(corporationCertType) || StringUtils.isBlank(corporationCertNo) || StringUtils.isBlank(bankName) || StringUtils.isBlank(bankCode) || StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(bankCardName) || StringUtils.isBlank(bankProvince) || StringUtils.isBlank(bankCity) || StringUtils.isBlank(issuerName)
				|| StringUtils.isBlank(issuer) || StringUtils.isBlank(supplierId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
			// Page<CicmorganBankCode> page = new Page<CicmorganBankCode>();
			cicmorganBankCode = cicmorganBankCodeService.get(bankCode);
			bankCode = cicmorganBankCode.getBankCode();
			result.put("state", "0");
			result.put("message", "PC端开户信息生成成功！");
//			Map<?, ?> data = cGBPayService.accountCreateByCompany(token, bizType, enterpriseFullName, businessLicenseType, businessLicense, bankPermitCertNo, taxRegCertNo, orgCode, agentPersonName, agentPersonPhone, agentPersonCertType, agentPersonCertNo, corporationName, corporationCertType, corporationCertNo, bankName, bankCode, bankCardNo, bankCardName, bankProvince, bankCity, issuerName, issuer, supplierId);
			Map<?, ?> data = businessBindCardService.cardRegister(token, bizType, enterpriseFullName, businessLicenseType, businessLicense, bankPermitCertNo, taxRegCertNo, orgCode, agentPersonName, agentPersonPhone, agentPersonCertType, agentPersonCertNo, corporationName, corporationCertType, corporationCertNo, bankName, bankCode, bankCardNo, bankCardName, bankProvince, bankCity, issuerName, issuer, supplierId);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("accountCreateByCompany,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * methods: accountCreateByCompanyForErp <br>
	 * description: 借款企业开户信息数据封装 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月8日 下午2:22:29
	 * 
	 * @param id
	 * @param enterpriseFullName
	 * @param businessLicenseType
	 * @param businessLicense
	 * @param bankPermitCertNo
	 * @param taxRegCertNo
	 * @param orgCode
	 * @param agentPersonName
	 * @param agentPersonPhone
	 * @param agentPersonCertType
	 * @param agentPersonCertNo
	 * @param corporationName
	 * @param corporationCertType
	 * @param corporationCertNo
	 * @param bankName
	 * @param bankCode
	 * @param bankCardNo
	 * @param bankCardName
	 * @param bankProvince
	 * @param bizType
	 * @param bankCity
	 * @param issuerName
	 * @param issuer
	 * @param supplierId
	 * @param email
	 * @param registAddress
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/webMemberAccountCreateEnterprise")
	@ResponseBody
	public Map<String, Object> webMemberAccountCreateEnterprise(@RequestBody CreditUserInfo creditUserInfo, @Context HttpServletRequest servletRequest) {

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

			// 封装加密数据，准备跳转至存管行页面
			Map<String, String> data = cGBPayService.webMemberAccountCreateEnterprise(creditUserInfo);
			// 发送邮件，文件列表
			List<File> urlList = new ArrayList<File>();
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
			String zipPath = "/data/upload/zip/" + creditUserInfo.getEnterpriseFullName() + ".zip";
			File file = new File(zipPath);
			// 创建目标文件输出流
			FileOutputStream fos2 = new FileOutputStream(file);
			ZipUtils.toZip(urlList, fos2);
			if (file.exists()) {
				LOG.info("zip file exists ...");
			} else {
				LOG.info("zip file not exists, create it ...");
			}
			String toMailAddr = SendMailUtil.toMailAddr; // 开口存管银行联系人
			String ccs = SendMailUtil.toMailAddrCCS; // ZTMG风控人员
			String cc = null;
			String subject = "中投摩根平台借款企业（" + creditUserInfo.getEnterpriseFullName() + "）用户开户[系统发送]";
			// String message = "您好：附件为借款企业申请开户资料，请查收审核，开户成功请回复邮件[yangxing@cicmorgan.com]谢谢！";
			String message = "您好：附件为借款企业申请开户资料，请查收审核，开户成功请回复邮件[wangyanle@cicmorgan.com]谢谢！";
			List<String> listS = new ArrayList<String>();
			listS.add(zipPath);
			Boolean sendEmailBoolean = SendMailUtil.sendWithMsgAndAttachment(toMailAddr, cc, ccs, subject, message, listS, creditUserInfo.getEnterpriseFullName());
			if (sendEmailBoolean) {
				LOG.info("send email successful ...");
			} else {
				LOG.info("send email failure ...");
			}
			result.put("state", "0");
			result.put("message", "<b>企业用户开户收集信息发送至存管行</b>");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("webMemberAccountCreateEnterprise,系统错误");
			result.put("state", "1");
			result.put("message", "<b>系统错误</b>");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 获取银行编码列表
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/selectCicmorganBankCode")
	public Map<String, Object> selectCicmorganBankCode(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// // 判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
			Page<CicmorganBankCode> page = new Page<CicmorganBankCode>();
			Page<CicmorganBankCode> resultPage = cicmorganBankCodeService.findPage(page, cicmorganBankCode);
			List<CicmorganBankCode> list = resultPage.getList();
			result.put("state", "0");
			result.put("message", "获取银行编码成功！");
			result.put("data", list);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("selectCicmorganBankCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * id获取银行编码
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/selectCicmorganBankCodeById")
	public Map<String, Object> selectCicmorganBankCodeById(@FormParam("token") String token, @FormParam("id") String id) {

		Map<String, Object> result = new HashMap<String, Object>();
		// // 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
			cicmorganBankCode = cicmorganBankCodeService.get(id);
			// List<CicmorganBankCode> list = resultPage.getList();
			result.put("state", "0");
			result.put("message", "id获取银行编码成功！");
			result.put("data", cicmorganBankCode);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("selectCicmorganBankCodeById,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 用户开户H5
	 * 实名认证 H5
	 * 
	 * @param bankCardNo
	 * @param certNo
	 * @param realName
	 * @param from
	 * @param token
	 * @param bankCardPhone
	 * @param servletRequest
	 * @return
	 */
	@POST
	@Path("/accountCreateH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> accountCreateH5(@FormParam("bankCardNo") String bankCardNo, @FormParam("certNo") String certNo, @FormParam("realName") String realName, @FormParam("from") String from, @FormParam("token") String token, @FormParam("bankCardPhone") String bankCardPhone, @FormParam("bizType") String bizType, @Context HttpServletRequest servletRequest) {

		String ip = LLPayUtil.getIpAddr(servletRequest);
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(bankCardNo) || StringUtils.isBlank(certNo) || StringUtils.isBlank(realName) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(ip) || StringUtils.isBlank(bankCardPhone) || StringUtils.isBlank(bizType)) {
			LOG.info("fn:accountCreateWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		/**
		 * 银行卡验证
		 */

		if (!FuncUtils.isBankCard(bankCardNo)) {
			LOG.info("fn:accountCreateWeb,开户银行卡号有误！");
			result.put("state", "5");
			result.put("message", "银行卡号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 身份证号验证
		 */
		if (!Validator.isIdCard(certNo)) {
			LOG.info("fn:accountCreateWeb,身份证号有误！");
			result.put("state", "6");
			result.put("message", "身份证号有误，请核实后重新输入");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取用户信息，拼装请求数据
		 */
		try {
			result.put("state", "0");
			result.put("message", "手机端开户信息生成成功！");
			Map<?, ?> data = cGBPayService.accountCreateH5(bankCardNo, certNo, realName, ip, bankCardPhone, token, bizType, from);
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:accountCreateWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 出借用户更换银行卡信息---PC端
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/changeBankCardWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> changeBankCardWeb(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token)) {
			LOG.info("fn:changeBankCardWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 判断账户余额是否为0,账户有余额时需人工邮件
			 */
			// 获取用户信息
			String jedisUserId = JedisUtils.get(token);

			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoService.getCgb(jedisUserId);
				result.put("state", "0");
				result.put("message", "PC端更换银行卡订单生成成功！");
				Map<?, ?> data = cGBPayService.changeBankCardWeb(user.getId());
				result.put("data", data);
				return result;

			} else {
				LOG.info("fn:changeBankCardWeb,系统超时");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:changeBankCardWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 出借用户更换银行卡信息---H5端
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/changeBankCardH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> changeBankCardH5(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token)) {
			LOG.info("fn:changeBankCardH5,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 判断账户余额是否为0,账户有余额时需人工邮件
			 */
			// 获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoService.getCgb(jedisUserId);
				result.put("state", "0");
				result.put("message", "wap端更换银行卡订单生成成功！");
				Map<?, ?> data = cGBPayService.changeBankCardH5(user.getId());
				result.put("data", data);
				return result;

			} else {
				LOG.info("fn:changeBankCardH5,系统超时");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:changeBankCardH5,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 出借用户更换预留手机号信息---PC端
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/changeBankPhoneWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> changeBankPhoneWeb(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token)) {
			LOG.info("fn:changeBankPhoneWeb,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			// 获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoService.getCgb(jedisUserId);

				/**
				 * 判断是否已经绑定银行卡
				 */
				CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(user.getId());
				if (userBankCard != null) {
					if (userBankCard.getState() == null || userBankCard.getState().equals(CgbUserBankCard.CERTIFY_NO)) {
						LOG.info("fn:changeBankPhoneWeb,未绑定银行卡！");
						result.put("state", "3");
						result.put("message", "未绑定银行卡！");
						result.put("data", null);
						return result;
					}
				} else {
					LOG.info("fn:changeBankPhoneWeb,未绑定银行卡！");
					result.put("state", "3");
					result.put("message", "未绑定银行卡！");
					result.put("data", null);
					return result;
				}

				result.put("state", "0");
				result.put("message", "PC端更换银行卡订单生成成功！");
				Map<?, ?> data = cGBPayService.changeBankPhoneWeb(user.getId());
				result.put("data", data);
				return result;

			} else {
				LOG.info("fn:changeBankPhoneWeb,系统超时");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:changeBankPhoneWeb,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 出借用户更换预留手机号信息---H5
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/changeBankPhoneH5")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> changeBankPhoneH5(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(token)) {
			LOG.info("fn:changeBankPhoneH5,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			// 获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoService.getCgb(jedisUserId);

				/**
				 * 判断是否已经绑定银行卡
				 */
				CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(user.getId());
				if (userBankCard != null) {
					if (userBankCard.getState() == null || userBankCard.getState().equals(CgbUserBankCard.CERTIFY_NO)) {
						LOG.info("fn:changeBankPhoneH5,未绑定银行卡！");
						result.put("state", "3");
						result.put("message", "未绑定银行卡！");
						result.put("data", null);
						return result;
					}
				} else {
					LOG.info("fn:changeBankPhoneH5,未绑定银行卡！");
					result.put("state", "3");
					result.put("message", "未绑定银行卡！");
					result.put("data", null);
					return result;
				}

				result.put("state", "0");
				result.put("message", "H5更换银行卡订单生成成功！");
				Map<?, ?> data = cGBPayService.changeBankPhoneH5(user.getId());
				result.put("data", data);
				return result;

			} else {
				LOG.info("fn:changeBankPhoneH5,系统超时");
				result.put("state", "4");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:changeBankPhoneH5,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

}
