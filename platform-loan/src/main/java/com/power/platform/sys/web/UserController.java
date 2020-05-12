/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.sys.web;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.RandomUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ImportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucherInfo;
import com.power.platform.credit.entity.voucher.CreditVoucherInfoDetail;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.info.CreditInfoService;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.credit.service.voucher.CreditVoucherInfoDetailService;
import com.power.platform.credit.service.voucher.CreditVoucherInfoService;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sms.dao.SmsMsgHistoryDao;
import com.power.platform.sms.entity.SmsMsgHistory;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.sms.service.SmsRejectService;
import com.power.platform.sys.service.SystemService;

/**
 * 用户Controller
 * 
 * @author ThinkGem
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * 平台编号
	 */
	private static final String PLATFORM_NO = Global.getConfigLanMao("platformNo");

	@Autowired
	private SystemService systemService;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CreditUserAccountService creditUserAccountService;
	@Autowired
	private NewRechargeService newRechargeService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditInfoService creditInfoService;
	@Autowired
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;
	@Resource
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private CreditVoucherInfoService creditVoucherInfoService;
	@Autowired
	private CreditVoucherInfoDetailService creditVoucherInfoDetailService;
	@Autowired
	private CreditUserAccountDao creditUserAccountDao;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;
	@Autowired
	private SmsRejectService smsRejectService;
	@Autowired
	private SendSmsService sendSmsService;
	@Resource
	private SmsMsgHistoryDao smsMsgHistoryDao;
	@Resource(name = "transactionManager")
	private DataSourceTransactionManager transactionManager;
	@Resource
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;

	@ModelAttribute
	public CreditUserInfo get(@RequestParam(required = false) String id) {

		CreditUserInfo user = new CreditUserInfo();
		if (StringUtils.isNotBlank(id)) {
			user = creditUserInfoDao.get(id);
		}
		// user.setCurrentUser(SessionUtils.getUser());
		return user;
	}

	/**
	 * 企业信息
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "creditUserInfo")
	public String CreditUserInfo(HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/sys/creditUserInfo";
	}

	/**
	 * 账户信息
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "account")
	public String account(CreditUserInfo user, Model model) {

		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo = creditUserInfoDao.get(user.getId());
		if (userInfo != null) {
			CgbUserBankCard userBankCard = cgbUserBankCardService.findByUserId1(userInfo.getId());
			CreditUserAccount creditUserAccount = creditUserAccountService.get(userInfo.getAccountId());
			if (userBankCard != null) {
				userBankCard.setBankAccountNo(FuncUtils.hiddenBankCard(userBankCard.getBankAccountNo()));
				creditUserAccount.setAvailableAmountStr(new DecimalFormat("0.00").format(creditUserAccount.getAvailableAmount()));
				userInfo.setCgbUserBankCard(userBankCard);
				userInfo.setCreditUserAccount(creditUserAccount);
			}
		}

		model.addAttribute("userInfo", userInfo);

		// 用户授权信息展示.
		ZtmgUserAuthorization entity = new ZtmgUserAuthorization();
		entity.setUserId(userInfo.getId());
		List<ZtmgUserAuthorization> list = ztmgUserAuthorizationDao.findList(entity);
		if (list != null) {
			if (list.size() > 0) { // 获取用户授权信息.
				ZtmgUserAuthorization ztmgUserAuthorization = list.get(0);
				// 授权状态.
				String status = ztmgUserAuthorization.getStatus();
				if ("S".equals(status)) {
					status = "授权成功";
				} else if ("F".equals(status)) {
					status = "授权失败";
				} else {
					status = "授权处理中";
				}
				ztmgUserAuthorization.setStatus(status);
				// 授权列表.
				String newGrantList = "";
				String grantList = ztmgUserAuthorization.getGrantList();
				String[] splitGrantList = grantList.split(",");
				for (int i = 0; i < splitGrantList.length; i++) {
					if (i >= 1) {
						newGrantList = newGrantList.concat(",");
					}
					if ("SHARE_PAYMENT".equals(splitGrantList[i])) {
						newGrantList = newGrantList.concat("免密缴费");
					}
					if ("REPAY".equals(splitGrantList[i])) {
						newGrantList = newGrantList.concat("免密还款");
					}
					if ("REPAYMENT".equals(splitGrantList[i])) {
						newGrantList = newGrantList.concat("授权还款");
					}
				}
				ztmgUserAuthorization.setGrantList(newGrantList);
				// 授权截至期限，日期转换
				if (PLATFORM_NO.equals(ztmgUserAuthorization.getMerchantId())) { // 懒猫系统商户编号
					try {
						if (null != ztmgUserAuthorization.getGrantTimeList()) {
							Date failTimeDate = DateUtils.parseDate(ztmgUserAuthorization.getGrantTimeList(), "yyyyMMdd");
							ztmgUserAuthorization.setGrantTimeList(DateUtils.formatDate(failTimeDate, "yyyy年MM月dd日"));
						} else {
							ztmgUserAuthorization.setGrantTimeList("");
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				// 授权金额列表.
				String newGrantAmountList = "";
				if (null != ztmgUserAuthorization.getGrantAmountList()) {
					String grantAmountList = ztmgUserAuthorization.getGrantAmountList();
					String[] splitGrantAmountList = grantAmountList.split(",");
					for (int i = 0; i < splitGrantAmountList.length; i++) {
						if (i >= 1) {
							newGrantAmountList = newGrantAmountList.concat(",");
						}
						if (i == 0) {
							newGrantAmountList = newGrantAmountList.concat(splitGrantAmountList[i]).concat("万元/笔");
						} else {
							newGrantAmountList = newGrantAmountList.concat(splitGrantAmountList[i]).concat("万元/笔");
						}
					}
					ztmgUserAuthorization.setGrantAmountList(newGrantAmountList);
				} else {
					ztmgUserAuthorization.setGrantAmountList("");
				}
				// 中投摩根用户授权信息.
				model.addAttribute("ztmgUserAuthorization", ztmgUserAuthorization);
			} else { // 该用户没有进行授权.
				// 中投摩根用户授权信息.
				model.addAttribute("ztmgUserAuthorization", null);
			}
		}

		return "modules/user/account_home";
	}

	/**
	 * 借款申请列表
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "project")
	public String project(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		String error = request.getParameter("error");
		if ("yes".equals(error)) {
			model.addAttribute("error", "发票信息不全，请补充完整！");
		}
		CreditUserInfo userInfo = null;
		if (creditUserApply.getReplaceUserId() != null) {
			userInfo = creditUserInfoService.get(creditUserApply.getReplaceUserId()); // 当前登录用户
			model.addAttribute("limit", "1");// 核心企业
		} else if (creditUserApply.getCreditSupplyId() != null) {
			userInfo = creditUserInfoService.get(creditUserApply.getCreditSupplyId()); // 当前登录用户
			model.addAttribute("limit", "2");// 供应商
		}
		if (userInfo != null) {
			model.addAttribute("userInfo", userInfo);
			WloanSubject wloanSubject = new WloanSubject();
			wloanSubject.setLoanApplyId(userInfo.getId());
			List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
			if (wloanSubjects != null && wloanSubjects.size() > 0) {
				wloanSubject = wloanSubjects.get(0);
				model.addAttribute("wloanSubject", wloanSubject);
			}
			CreditVoucherInfo creditVoucherInfo = new CreditVoucherInfo();
			creditVoucherInfo.setUserId(userInfo.getId());
			List<CreditVoucherInfo> creditVoucherInfos = creditVoucherInfoService.findList(creditVoucherInfo);
			if (creditVoucherInfos != null && creditVoucherInfos.size() > 0) {
				creditVoucherInfo = creditVoucherInfos.get(0);
				model.addAttribute("creditVoucherInfo", creditVoucherInfo);
			}

		}
		Page<CreditUserApply> page = creditUserApplyService.findPage(new Page<CreditUserApply>(request, response), creditUserApply);
		List<CreditUserApply> list = page.getList();
		for (CreditUserApply entity : list) {
			// 处理开票过期的申请
			String voucherState = entity.getVoucherState();
			if (voucherState == null) {
				Date before = entity.getCreateDate();
				Double days = DateUtils.getDistanceOfTwoDate(before, new Date());
				if (days >= 90) {
					entity.setVoucherState("3");
					creditUserApplyService.save(entity);
				}
			}

			String replaceUserId = entity.getReplaceUserId(); // 代偿户ID.
			String loanUserId = entity.getCreditSupplyId();// 融资方ID
			CreditUserInfo creditReplaceUserInfo = creditUserInfoService.get(replaceUserId); // 代偿人信息.
			if (null != creditReplaceUserInfo) {
				entity.setReplaceUserEnterpriseFullName(creditReplaceUserInfo.getEnterpriseFullName());
			}
			CreditUserInfo creditUserInfo = creditUserInfoService.get(loanUserId); // 借款人信息.
			if (null != creditUserInfo) {
				entity.setLoanUserId(creditUserInfo.getId());
				entity.setLoanUserPhone(creditUserInfo.getPhone());
				entity.setLoanUserName(creditUserInfo.getName());
				entity.setLoanUserEnterpriseFullName(creditUserInfo.getEnterpriseFullName());
			}
		}

		model.addAttribute("page", page);
		if (!StringUtils.isBlank(creditUserApply.getReplaceUserId())) {
			model.addAttribute("id", creditUserApply.getReplaceUserId());
		} else {
			model.addAttribute("id", creditUserApply.getCreditSupplyId());
		}
		return "modules/user/creditUserApplyList";
	}

	/**
	 * 申请开票
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "creditVoucherApply")
	public String creditVoucherApply(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		String id = request.getParameter("id");
		creditUserApply = creditUserApplyService.get(id);
		String title = null; // 抬头
		String number = null; // 发票税号
		String addr = null; // 地址
		String phone = null; // 电话
		String bankName = null; // 开户行
		String bankNo = null; // 开户账号
		String toName = null; // 收件人姓名
		String toPhone = null; // 收件人电话
		String toAddr = null; // 收件人地址
		String state = CreditUserApplyService.VOUCHER_STATE1; // 申请状态 申请中
		String error = null;

		WloanSubject wloanSubject = new WloanSubject();
		wloanSubject.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
		if (wloanSubjects != null && wloanSubjects.size() > 0) {
			wloanSubject = wloanSubjects.get(0);
			title = wloanSubject.getCompanyName();
			number = wloanSubject.getBusinessNo();
			addr = wloanSubject.getRegistAddress();

		}
		CreditVoucherInfo creditVoucherInfo = new CreditVoucherInfo();
		creditVoucherInfo.setUserId(creditUserApply.getReplaceUserId());
		List<CreditVoucherInfo> creditVoucherInfos = creditVoucherInfoService.findList(creditVoucherInfo);
		if (creditVoucherInfos != null && creditVoucherInfos.size() > 0) {
			creditVoucherInfo = creditVoucherInfos.get(0);
			phone = creditVoucherInfo.getPhone();
			bankName = creditVoucherInfo.getBankName();
			bankNo = creditVoucherInfo.getBankNo();
			toName = creditVoucherInfo.getToName();
			toPhone = creditVoucherInfo.getToPhone();
			toAddr = creditVoucherInfo.getToAddr();
		}
		if (title == null || number == null || addr == null || phone == null || bankName == null || bankNo == null || toName == null || toPhone == null || toAddr == null || "".equals(title) || "".equals(number) || "".equals(addr) || "".equals(phone) || "".equals(bankName) || "".equals(bankNo) || "".equals(toName) || "".equals(toPhone) || "".equals(toAddr)) {
			error = "yes";
		} else {
			// 保存开票记录
			CreditVoucherInfoDetail creditVoucherInfoDetail = new CreditVoucherInfoDetail();
			creditVoucherInfoDetail.setApplyId(id);
			creditVoucherInfoDetail.setTitle(title);
			creditVoucherInfoDetail.setNumber(number);
			creditVoucherInfoDetail.setAddr(addr);
			;
			creditVoucherInfoDetail.setPhone(phone);
			creditVoucherInfoDetail.setBankName(bankName);
			creditVoucherInfoDetail.setBankNo(bankNo);
			creditVoucherInfoDetail.setToName(toName);
			creditVoucherInfoDetail.setToPhone(toPhone);
			creditVoucherInfoDetail.setToAddr(toAddr);
			creditVoucherInfoDetail.setState(state);
			creditVoucherInfoDetailService.save(creditVoucherInfoDetail);

			creditUserApply.setVoucherState(CreditUserApplyService.VOUCHER_STATE1);// 申请中
			creditUserApplyService.save(creditUserApply);
		}

		return "redirect:" + Global.getAdminPath() + "/sys/user/project?replaceUserId=" + creditUserApply.getReplaceUserId() + "&error=" + error;
	}

	/**
	 * 开票信息
	 * 
	 * @param wloanTermProject
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "creditVoucherInfo")
	public Map<String, String> creditVoucherInfo(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		Map<String, String> map = new HashMap<String, String>();
		String id = request.getParameter("id");// 发票信息id
		String userId = request.getParameter("userId");// 供应商id
		String phone = request.getParameter("phone");// 电话
		String bankName = request.getParameter("bankName");// 开户行
		String bankNo = request.getParameter("bankNo");// 开户账号
		String toName = request.getParameter("toName");// 收件人姓名
		String toPhone = request.getParameter("toPhone");// 收件人电话
		String toAddr = request.getParameter("toAddr");// 收件人地址

		// 变更发票信息
		CreditVoucherInfo creditVoucherInfo = creditVoucherInfoService.get(id);
		if (creditVoucherInfo == null) {
			creditVoucherInfo = new CreditVoucherInfo();
		}
		creditVoucherInfo.setUserId(userId);
		creditVoucherInfo.setPhone(phone);
		creditVoucherInfo.setBankName(bankName);
		creditVoucherInfo.setBankNo(bankNo);
		creditVoucherInfo.setToName(toName);
		creditVoucherInfo.setToPhone(toPhone);
		creditVoucherInfo.setToAddr(toAddr);
		creditVoucherInfoService.save(creditVoucherInfo);

		// 变更申请中的发票信息
		CreditVoucherInfoDetail creditVoucherInfoDetail = new CreditVoucherInfoDetail();
		creditVoucherInfoDetail.setState(CreditVoucherInfoDetailService.STATE1);// 申请中
		List<CreditVoucherInfoDetail> creditVoucherInfoDetails = creditVoucherInfoDetailService.findList(creditVoucherInfoDetail);
		if (creditVoucherInfoDetails != null && creditVoucherInfoDetails.size() > 0) {
			for (CreditVoucherInfoDetail creditVoucherInfoDetail2 : creditVoucherInfoDetails) {
				creditVoucherInfoDetail2.setPhone(phone);
				creditVoucherInfoDetail2.setBankName(bankName);
				creditVoucherInfoDetail2.setBankNo(bankNo);
				creditVoucherInfoDetail2.setToName(toName);
				creditVoucherInfoDetail2.setToAddr(toAddr);
				creditVoucherInfoDetail2.setToPhone(toPhone);
				creditVoucherInfoDetailService.save(creditVoucherInfoDetail2);
			}
		}
		map.put("state", "0");
		return map;
	}

	/**
	 * 供应商列表
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "supplier")
	public String project(CreditSupplierToMiddlemen creditSupplierToMiddlemen, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditSupplierToMiddlemen> page = creditSupplierToMiddlemenService.findPage(new Page<CreditSupplierToMiddlemen>(request, response, 5), creditSupplierToMiddlemen);

		List<CreditSupplierToMiddlemen> list = page.getList();
		for (CreditSupplierToMiddlemen supplierToMiddlemen : list) {
			String supplierId = supplierToMiddlemen.getSupplierId();
			WloanSubject loanSubject = new WloanSubject();
			loanSubject.setLoanApplyId(supplierId);
			List<WloanSubject> wloanSubjectList = wloanSubjectService.findList(loanSubject);
			if (wloanSubjectList != null && wloanSubjectList.size() > 0) {
				supplierToMiddlemen.setWloanSubject(wloanSubjectList.get(0));
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("id", creditSupplierToMiddlemen.getMiddlemenId());
		return "modules/user/creditSupplierList";
	}

	/**
	 * 
	 * methods: addSupplier <br>
	 * description: 添加功能供应商. <br>
	 * author: Roy <br>
	 * date: 2019年4月9日 上午9:51:07
	 * 
	 * @param userInfo
	 *            供应商帐号信息
	 * @param request
	 *            请求域
	 * @param response
	 *            响应域
	 * @param model
	 *            Model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "addSupplier")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> addSupplier(CreditUserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String middlementId = request.getParameter("middlemenId");
			logger.info("middlementId = {}", middlementId);
			// N1.判断必要参数是否为空
			if (StringUtils.isBlank(userInfo.getPhone()) || StringUtils.isBlank(userInfo.getPwd()) || StringUtils.isBlank(middlementId) || StringUtils.isBlank(userInfo.getEnterpriseFullName())) {
				result.put("state", "2");
				result.put("message", "缺少必要参数");
				return result;
			}
			logger.info("phone = {},pwd = {},enterpriseFullName = {}", userInfo.getPhone(), userInfo.getPwd(), userInfo.getEnterpriseFullName());
			// N2.判断是否已经注册
			// 改版逻辑：支持添加供应商，但是保持供应商用户的唯一性.
			CreditUserInfo isRegisterUser = new CreditUserInfo();
			isRegisterUser.setPhone(StringUtils.replaceBlanK(userInfo.getPhone()));
			List<CreditUserInfo> isRegisterUsers = creditUserInfoDao.findList(isRegisterUser);
			if (isRegisterUsers != null && isRegisterUsers.size() > 0) { // 已注册的供应商逻辑处理.
				// 仅添加核心企业与供应商的关联关系表.
				if (isRegisterUsers.size() > 1) { // 异常用户.
					result.put("state", "3");
					result.put("message", "该供应商已注册 ‘>=’2个帐号主体，请您联系客服处理.");
					return result;
				}
				// 已注册用户帐号主体.
				CreditUserInfo registerCreditUserInfo = isRegisterUsers.get(0);

				CreditSupplierToMiddlemen isExistCTM = new CreditSupplierToMiddlemen();
				isExistCTM.setSupplierId(registerCreditUserInfo.getId());
				isExistCTM.setMiddlemenId(middlementId);
				// 判断供应商与核心企业是否已经建立关联关系.
				List<CreditSupplierToMiddlemen> isExistCTMs = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(isExistCTM);
				if (isExistCTMs != null && isExistCTMs.size() > 0) {
					result.put("state", "3");
					result.put("message", "该供应商已添加，请勿重复添加.");
					return result;
				} else {
					// 建立核心企业与供应商关联关系表.
					CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
					creditSupplierToMiddlemen.setId(IdGen.uuid());
					creditSupplierToMiddlemen.setSupplierId(registerCreditUserInfo.getId());
					creditSupplierToMiddlemen.setMiddlemenId(middlementId);
					int n = creditSupplierToMiddlemenService.insertCreditSupplierToMiddlemen(creditSupplierToMiddlemen);
					if (n > 0) {
						logger.info("核心企业与借款户供应商之间的关联关系表新增成功...");
					}
				}

				// 响应.
				result.put("state", "0");
				result.put("message", "供应商新增成功");
				result.put("phone", registerCreditUserInfo.getPhone());
				result.put("userId", registerCreditUserInfo.getId());
			} else {
				// 注册并建立核心企业与供应商的关联关系表.
				// N3.开始注册
				String userAccountId = String.valueOf(IdGen.randomLong());
				userInfo.setId(IdGen.uuid()); // 用户唯一标识主键.
				userInfo.setAccountId(userAccountId); // 用户账户主键.
				userInfo.setPhone(StringUtils.replaceBlanK(userInfo.getPhone())); // 手机号码空格回测换行符处理.
				userInfo.setPwd(EncoderUtil.encrypt(userInfo.getPwd())); // 密码Md5加密.
				userInfo.setRegisterDate(new Date()); // 添加供应商时间/注册时间.
				userInfo.setCreditScore("0");
				userInfo.setEnterpriseFullName(StringUtils.replaceBlanK(userInfo.getEnterpriseFullName()));
				userInfo.setState(CreditUserInfo.CREDIT_USER_NORMAL);
				userInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_02);
				// 新增借款户，初始化默认开户状态，0：未开户
				userInfo.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_0.getValue()); // 未开户
				userInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2); // 未完善基本信息
				int insertCreditUserInfoFlag = creditUserInfoDao.insert(userInfo);
				if (insertCreditUserInfoFlag == 1) { // 用户帐号信息新增成功.
					logger.info("借款户供应商帐号新增成功...");
					// N4.新增借款用户账户
					CreditUserAccount userAccount = new CreditUserAccount();
					userAccount.setId(userAccountId);
					userAccount.setCreditUserId(userInfo.getId());
					userAccount.setTotalAmount(0d);
					userAccount.setAvailableAmount(0d);
					userAccount.setRechargeAmount(0d);
					userAccount.setWithdrawAmount(0d);
					userAccount.setRepayAmount(0d);
					userAccount.setSurplusAmount(0d);
					userAccount.setFreezeAmount(0d);
					int j = creditUserAccountDao.insert(userAccount);
					if (j > 0) { // 用户账户信息新增成功.
						logger.info("借款户供应商账户新增成功...");
						// N5.新增借款户代偿户关系
						CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
						creditSupplierToMiddlemen.setId(IdGen.uuid());
						creditSupplierToMiddlemen.setSupplierId(userInfo.getId());
						creditSupplierToMiddlemen.setMiddlemenId(middlementId);
						int n = creditSupplierToMiddlemenService.insertCreditSupplierToMiddlemen(creditSupplierToMiddlemen);
						if (n > 0) {
							logger.info("核心企业与借款户供应商之间的关联关系表新增成功...");
							WloanSubject wloanSubject = new WloanSubject();
							wloanSubject.setId(IdGen.uuid());
							wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2); // 融资主体类型，2：企业.
							wloanSubject.setLoanApplyId(userInfo.getId());
							wloanSubject.setCompanyName(userInfo.getEnterpriseFullName());// 供应商名称
							wloanSubject.setIsEntrustedPay(WloanSubjectService.IS_ENTRUSTED_PAY_0); // 是否受托支付，0：否.
							wloanSubject.setCreateDate(new Date());
							wloanSubject.setUpdateDate(new Date());
							int m = wloanSubjectDao.insert(wloanSubject);
							if (m > 0) { // 供应商融资主体新增成功.
								logger.info("借款户供应商融资主体新增成功...");
							}
						}

						// 响应.
						result.put("state", "0");
						result.put("message", "供应商新增成功");
						result.put("phone", userInfo.getPhone());
						result.put("userId", userInfo.getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static boolean isMobile(String str) {

		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 
	 * methods: addSupplierList <br>
	 * description: 批量添加供应商. <br>
	 * author: Roy <br>
	 * date: 2019年4月9日 下午3:42:31
	 * 
	 * @param creditUser
	 *            用户帐号信息
	 * @param file
	 *            文件
	 * @param redirectAttributes
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "addSupplierList")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> addSupplierList(CreditUserInfo creditUser, @RequestParam("file") MultipartFile file, HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		// IP.
		String middlementId = (String) request.getParameter("middlementId");

		logger.info("middlementId = {}", middlementId);

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些.
		TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态.

		try {
			ImportExcel ie = new ImportExcel(file, 1, 0);
			for (int i = 1; i < ie.getLastDataRowNum(); i++) {
				Row row = ie.getRow(i);
				StringBuffer rowDateSb = new StringBuffer();
				for (int j = 0; j < ie.getLastCellNum(); j++) {
					Object val = ie.getCellValue(row, j);
					if (val instanceof String) { // String类型不作处理.
					}
					if (val instanceof Integer) { // Integer类型处理.
						val = new DecimalFormat("0").format(val);
					}
					if (val instanceof Double) { // Double类型处理.
						val = new DecimalFormat("0").format(val);
					}
					if (val instanceof Float) { // Float类型处理.
						val = new DecimalFormat("0").format(val);
					}
					rowDateSb.append(val).append("|");
				}
				String rowDateStr = rowDateSb.toString();
				List<String> asList = Arrays.asList(rowDateStr.split("\\|"));

				// 空行，进行下一次循环.
				if (asList.size() == 0) {
					continue;
				}

				String phone = StringUtils.replaceBlanK(asList.get(0));
				if (!isMobile(phone)) {
					// 响应.
					result.put("state", "1");
					result.put("message", "第" + (i + 1) + "条,手机号码格式不对");
					// 手动回滚事务.
					transactionManager.rollback(status);
					return result;
				}
				String enterpriseFullName = asList.get(1);
				String pwd = asList.get(2);
				CreditUserInfo creditUserInfo = new CreditUserInfo();
				creditUserInfo.setPhone(phone);
				List<CreditUserInfo> isExistUser = creditUserInfoDao.findList(creditUserInfo);
				if (isExistUser != null && isExistUser.size() > 0) { // 已注册的供应商逻辑处理.
					// 仅添加核心企业与供应商的关联关系表.
					if (isExistUser.size() > 1) { // 异常用户.
						result.put("state", "1");
						result.put("message", "第" + (i + 1) + "条，该供应商已注册 ‘>=’2个帐号主体，请您联系客服处理.");
						transactionManager.rollback(status);
						return result;
					}
					// 已注册用户帐号主体.
					CreditUserInfo registerCreditUserInfo = isExistUser.get(0);
					CreditSupplierToMiddlemen isExistCTM = new CreditSupplierToMiddlemen();
					isExistCTM.setSupplierId(registerCreditUserInfo.getId());
					isExistCTM.setMiddlemenId(middlementId);
					// 判断供应商与核心企业是否已经建立关联关系.
					List<CreditSupplierToMiddlemen> isExistCTMs = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(isExistCTM);
					if (isExistCTMs != null && isExistCTMs.size() > 0) {
						result.put("state", "3");
						result.put("message", "第" + (i + 1) + "条，该供应商已添加，请勿重复添加.");
						transactionManager.rollback(status);
						return result;
					} else {
						// 建立核心企业与供应商关联关系表.
						CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
						creditSupplierToMiddlemen.setId(IdGen.uuid());
						creditSupplierToMiddlemen.setSupplierId(registerCreditUserInfo.getId());
						creditSupplierToMiddlemen.setMiddlemenId(middlementId);
						int n = creditSupplierToMiddlemenService.insertCreditSupplierToMiddlemen(creditSupplierToMiddlemen);
						if (n > 0) {
							logger.info("核心企业与借款户供应商之间的关联关系表新增成功...");
						}
					}
				} else {
					// 注册并建立核心企业与供应商的关联关系表.
					// N3.开始注册
					String userAccountId = String.valueOf(IdGen.randomLong());
					creditUser.setId(IdGen.uuid()); // 用户唯一标识主键.
					creditUser.setAccountId(userAccountId); // 用户账户主键.
					creditUser.setPhone(phone); // 手机号码空格回测换行符处理.
					creditUser.setPwd(EncoderUtil.encrypt(pwd)); // 密码Md5加密.
					creditUser.setRegisterDate(new Date()); // 添加供应商时间/注册时间.
					creditUser.setCreditScore("0");
					creditUser.setEnterpriseFullName(StringUtils.replaceBlanK(enterpriseFullName));
					creditUser.setState(CreditUserInfo.CREDIT_USER_NORMAL);
					creditUser.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_02);
					// 新增借款户，初始化默认开户状态，0：未开户
					creditUser.setOpenAccountState(CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_0.getValue()); // 未开户
					creditUser.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_2); // 未完善基本信息
					int insertCreditUserInfoFlag = creditUserInfoDao.insert(creditUser);
					if (insertCreditUserInfoFlag == 1) { // 用户帐号信息新增成功.
						logger.info("借款户供应商帐号新增成功...");
						// N4.新增借款用户账户
						CreditUserAccount userAccount = new CreditUserAccount();
						userAccount.setId(userAccountId);
						userAccount.setCreditUserId(creditUser.getId());
						userAccount.setTotalAmount(0d);
						userAccount.setAvailableAmount(0d);
						userAccount.setRechargeAmount(0d);
						userAccount.setWithdrawAmount(0d);
						userAccount.setRepayAmount(0d);
						userAccount.setSurplusAmount(0d);
						userAccount.setFreezeAmount(0d);
						int j = creditUserAccountDao.insert(userAccount);
						if (j > 0) { // 用户账户信息新增成功.
							logger.info("借款户供应商账户新增成功...");
							// N5.新增借款户代偿户关系
							CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
							creditSupplierToMiddlemen.setId(IdGen.uuid());
							creditSupplierToMiddlemen.setSupplierId(creditUser.getId());
							creditSupplierToMiddlemen.setMiddlemenId(middlementId);
							int n = creditSupplierToMiddlemenService.insertCreditSupplierToMiddlemen(creditSupplierToMiddlemen);
							if (n > 0) {
								logger.info("核心企业与借款户供应商之间的关联关系表新增成功...");
								WloanSubject wloanSubject = new WloanSubject();
								wloanSubject.setId(IdGen.uuid());
								wloanSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2); // 融资主体类型，2：企业.
								wloanSubject.setLoanApplyId(creditUser.getId());
								wloanSubject.setCompanyName(creditUser.getEnterpriseFullName());// 供应商名称
								wloanSubject.setIsEntrustedPay(WloanSubjectService.IS_ENTRUSTED_PAY_0); // 是否受托支付，0：否.
								wloanSubject.setCreateDate(new Date());
								wloanSubject.setUpdateDate(new Date());
								int m = wloanSubjectDao.insert(wloanSubject);
								if (m > 0) { // 供应商融资主体新增成功.
									logger.info("借款户供应商融资主体新增成功...");
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 手动回滚事物
			transactionManager.rollback(status);
			// return "redirect:" + Global.getAdminPath() + "/sys/user/supplier?middlemenId=" + creditUser.getId();
		}

		// 事务提交.
		transactionManager.commit(status);
		// 响应.
		result.put("state", "0");
		result.put("message", "批量添加供应商成功");
		return result;

	}

	/**
	 * 供应商列表
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "repayment")
	public String repayment(HttpServletRequest request, HttpServletResponse response, Model model) {

		String wloanTermProjectId = request.getParameter("id");// 项目id
		List<Map<String, Object>> repayList = new ArrayList<Map<String, Object>>();
		int totalPeriods = 0;
		WloanTermProject loanProject = wloanTermProjectService.get(wloanTermProjectId);
		// N5.根据项目状态是否为[融资中/已放款]来查询项目还款记录
		if (loanProject.getState().equals(WloanTermProjectService.ONLINE) || loanProject.getState().equals(WloanTermProjectService.FULL) || loanProject.getState().equals(WloanTermProjectService.REPAYMENT) || loanProject.getState().equals(WloanTermProjectService.FINISH)) {

			Double dueRepayAmount = 0d;
			// N6.根据项目ID查询借款用户的项目还款计划
			WloanTermProjectPlan projectPlan = new WloanTermProjectPlan();
			projectPlan.setWloanTermProject(loanProject);
			List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findList(projectPlan);
			if (planList != null && planList.size() > 0) {
				totalPeriods = planList.size();
				for (int i = 0; i < planList.size(); i++) {
					WloanTermProjectPlan plan = planList.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("planId", plan.getId());
					map.put("projectName", plan.getWloanTermProject().getName());
					map.put("projectId", plan.getWloanTermProject().getId());
					map.put("repayAmount", NumberUtils.scaleDouble(plan.getInterest()));// 本期应还
					map.put("repayDate", DateUtils.formatDateTime(plan.getRepaymentDate()));
					map.put("planState", plan.getState());
					map.put("loanAmount", loanProject.getAmount());// 借款金额
					map.put("periods", i + 1);// 第N期数
					map.put("loanDate", DateUtils.formatDateTime(loanProject.getCreateDate()));// 借款时间
					if (plan.getState().equals("1")) {
						dueRepayAmount = dueRepayAmount + plan.getInterestTrue();
					}
					map.put("totalPeriods", totalPeriods);
					map.put("dueRepayAmount", NumberUtils.scaleDouble(dueRepayAmount));
					repayList.add(map);
				}
			}
		}

		model.addAttribute("repayList", repayList);
		return "modules/sys/loanRepaymentList";
	}

	/**
	 * 修改手机号
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "modifyPhone")
	public Map<String, Object> modifyPhone(CreditUserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		// IP.
		// String ip = (String) request.getAttribute("ip");
		// ip.replace("_", ".");
		// String ip = "127.0.0.1";// 借款端
		Map<String, Object> result = new HashMap<String, Object>();

		String phone = request.getParameter("phone");
		String newPhone = request.getParameter("newPhone");

		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(newPhone)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断是否已经注册
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		List<CreditUserInfo> creditUserInfos = creditUserInfoDao.findList(userInfo);
		if (creditUserInfos != null && creditUserInfos.size() > 0) {
			credituser = creditUserInfos.get(0);
			credituser.setPhone(newPhone);
			creditUserInfoService.save(credituser);
			result.put("state", "0");
			result.put("message", "修改手机号成功");
			return result;
		} else {
			result.put("state", "1");
			result.put("message", "该用户未注册");
			return result;
		}

	}

	/**
	 * 修改密码
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "modifyPwd")
	public Map<String, Object> modifyPwd(CreditUserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		// IP.
		// String ip = (String) request.getAttribute("ip");
		// ip.replace("_", ".");
		// String ip = "127.0.0.1";// 借款端
		Map<String, Object> result = new HashMap<String, Object>();
		String phone = request.getParameter("phone");
		String oldPwd = request.getParameter("oldPwd");
		String newPwd = request.getParameter("newPwd");
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// N2.判断是否已经注册
		CreditUserInfo credituser = new CreditUserInfo();
		credituser.setPhone(phone);
		credituser.setPwd(EncoderUtil.encrypt(oldPwd));
		List<CreditUserInfo> returnuser = creditUserInfoDao.findList(credituser);
		if (returnuser != null && returnuser.size() > 0) {
			credituser = returnuser.get(0);
			credituser.setPwd(EncoderUtil.encrypt(newPwd));
			creditUserInfoService.save(credituser);
			result.put("state", "0");
			result.put("message", "密码修改成功");
			return result;
		} else {
			result.put("state", "3");
			result.put("message", "密码错误");
			return result;
		}
	}

	/**
	 * 发送验证码
	 * 
	 * @param creditUserApply
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "sendMessage")
	public Map<String, Object> sendMessage(CreditUserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		// IP.
		// String ip = (String) request.getAttribute("ip");
		// ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		String phone = request.getParameter("phone");
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(phone)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		// if (!validate(ip,phone)) {
		// LOG.info("fn:sendSmsCode,短信发送过于频繁，请稍后操作！");
		// result.put("state", "6");
		// result.put("message", "短信发送过于频繁，请稍后操作！");
		// result.put("data", null);
		// return result;
		// }

		/**
		 * 发送SMS CODE.
		 */
		try {

			// 验证码.
			String code = RandomUtil.generateRandomDigitalString(6);
			// 发送短信验证码，返回状态.
			String smsState = sendSmsService.sendSmsCode(phone, new String[] { phone, code });
			if (!"0".equals((((smsState.split(","))[1]).split("\n"))[0])) {
				logger.info("fn:sendSmsCode,短信验证码发送失败,发送返回报告:" + smsState);
				result.put("state", "5");
				result.put("message", "短信验证码发送失败！");
				result.put("data", null);
				return result;
			} else {
				// 缓存中保存客户短信验证码.客户手机号码：为Key.
				JedisUtils.set(phone, code, 60);

				// 短信消息验证码历史.
				SmsMsgHistory smsMsgHistory = new SmsMsgHistory();
				smsMsgHistory.setId(IdGen.uuid());
				smsMsgHistory.setPhone(phone);
				smsMsgHistory.setValidateCode(code);
				smsMsgHistory.setMsgContent(sendSmsService.getSmsTemplateContent(new String[] { phone, code }));
				smsMsgHistory.setCreateTime(new Date());
				smsMsgHistory.setType(Integer.valueOf(1));
				// smsMsgHistory.setIp(ip);
				int flag = smsMsgHistoryDao.insert(smsMsgHistory);
				if (flag == 1) {
					logger.info("fn:sendSmsCode,保存消息短信验证码成功！");
				} else {
					logger.info("fn:sendSmsCode,保存消息短信验证码失败！");
				}
				logger.info("fn:sendSmsCode,短信验证码发送成功,发送返回报告:" + smsState);
				result.put("state", "0");
				result.put("message", "短信验证码发送成功！");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:sendSmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * 描述: 校验短信验证码. <br>
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "verifyMessage")
	public Map<String, Object> verifyMessage(CreditUserInfo userInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		String phone = request.getParameter("phone");
		String smsCode = request.getParameter("smsCode");
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(smsCode)) {
			logger.info("fn:verifySmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {

			/**
			 * 缓存中获取短信验证码.
			 */

			String cachSmsCode = JedisUtils.get(phone);

			// 空指针异常.
			if (null == cachSmsCode) {
				logger.info("verifyMessage,缓存中验证码不存在！");
				result.put("state", "5");
				result.put("message", "缓存中验证码不存在！");
				result.put("data", null);
				return result;
			}

			// 判断用户输入的验证码与缓存中的验证码是否相同.
			if (cachSmsCode.equals(smsCode)) {
				logger.info("verifyMessage,校验手机短信验证码成功！");
				result.put("state", "0");
				result.put("message", "校验手机短信验证码成功！");
				result.put("data", null);
				return result;
			} else {
				logger.info("verifyMessage,校验手机短信验证码失败！");
				result.put("state", "6");
				result.put("message", "校验手机短信验证码失败！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("verifyMessage,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/*
	 * private synchronized boolean validate(String ip, String phone) throws Exception {
	 * 
	 * boolean isValid = true;
	 * Map<String, String> sendIPMap = JedisUtils.getMap(ip);
	 * if (sendIPMap == null) {
	 * isValid = true;
	 * } else {
	 * int times = Integer.valueOf(sendIPMap.get("times"));
	 * if (times >= 20) {
	 * isValid = false;
	 * List<SmsRejectHistory> smsRejectHistorys = smsRejectService.getByIP(ip);
	 * SmsRejectHistory smsRejectHistory;
	 * if (smsRejectHistorys != null && smsRejectHistorys.size() > 0) {
	 * smsRejectHistory = smsRejectHistorys.get(0);
	 * smsRejectHistory.setTimes(smsRejectHistory.getTimes() + 1);
	 * smsRejectService.save(smsRejectHistory);
	 * } else {
	 * smsRejectHistory = new SmsRejectHistory();
	 * smsRejectHistory.setCreateTime(new Date());
	 * smsRejectHistory.setPhone(phone);
	 * smsRejectHistory.setIp(ip);
	 * smsRejectHistory.setTimes(times);
	 * smsRejectHistory.setType("1");
	 * smsRejectService.save(smsRejectHistory);
	 * }
	 * 
	 * return isValid;
	 * }
	 * }
	 * 
	 * Map<String, String> sendPhoneMap = JedisUtils.getMap(phone + "times");
	 * if (sendPhoneMap == null) {
	 * isValid = true;
	 * } else {
	 * int times = Integer.valueOf(sendPhoneMap.get("times"));
	 * if (times >= 10) {
	 * isValid = false;
	 * SmsRejectHistory smsRejectHistory = smsRejectService.getByPhone(phone);
	 * if (smsRejectHistory != null) {
	 * smsRejectHistory.setTimes(smsRejectHistory.getTimes() + 1);
	 * smsRejectService.save(smsRejectHistory);
	 * } else {
	 * smsRejectHistory = new SmsRejectHistory();
	 * smsRejectHistory.setCreateTime(new Date());
	 * smsRejectHistory.setPhone(phone);
	 * smsRejectHistory.setIp(ip);
	 * smsRejectHistory.setTimes(times);
	 * smsRejectHistory.setType("0");
	 * smsRejectService.save(smsRejectHistory);
	 * }
	 * return isValid;
	 * }
	 * }
	 * return isValid;
	 * }
	 */
}
