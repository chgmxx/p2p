package com.power.platform.sys.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.CicmorganBankCodeService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.supplierToMiddlemen.CreditSupplierToMiddlemenService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.pay.service.CGBPayService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.entity.UserBankCard;

/**
 * 信贷用户Controller
 * 
 * @author nice
 * @version 2017-03-22
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/userinfo/creditUserInfo")
public class CreditUserInfoController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(CreditUserInfoController.class);

	/**
	 * 2：未开户
	 */
	private static final String BANK_STATE_2 = "2";

	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CicmorganBankCodeService cicmorganBankCodeService;
	@Autowired
	private CGBPayService cGBPayService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Resource
	private CreditSupplierToMiddlemenService creditSupplierToMiddlemenService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;

	@ModelAttribute
	public CreditUserInfo get(@RequestParam(required = false) String id) {

		CreditUserInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditUserInfoService.get(id);
		}
		if (entity == null) {
			entity = new CreditUserInfo();
		}
		return entity;
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditUserInfo creditUserInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditUserInfo> page = creditUserInfoService.findPage(new Page<CreditUserInfo>(request, response), creditUserInfo);
		model.addAttribute("page", page);
		return "modules/credit/userinfo/creditUserInfoList";
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = "applyMoney")
	public String applyMoney(CreditUserInfo creditUserInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		return "modules/sys/applyMoney";
	}

	/**
	 * 
	 * methods: lanMaoCreditUserCompanyInfo <br>
	 * description: 懒猫版本，查询企业开户信息 <br>
	 * author: Roy <br>
	 * date: 2019年9月26日 上午10:08:37
	 * 
	 * @param creditUserInfo
	 * @param model
	 * @return
	 */
	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = "creditUserCompanyInfo")
	public String lanMaoCreditUserCompanyInfo(CreditUserInfo creditUserInfo, Model model) {

		// 借款人银行卡信息
		CgbUserBankCard userBankCard = null;
		if (creditUserInfo != null) {
			userBankCard = cgbUserBankCardService.findByUserId1(creditUserInfo.getId());
			if (userBankCard != null) {
				// state，0：未认证， 1:已认证
				CreditAnnexFile annexFile = new CreditAnnexFile();
				annexFile.setOtherId(creditUserInfo.getId());
				List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFile);
				model.addAttribute("creditAnnexFileList", creditAnnexFileList);
				log.info("fn:creditUserCompanyInfo：\t该借款人开户资料查询成功");
			} else {// 未开户
				userBankCard = new CgbUserBankCard();
				userBankCard.setState(BANK_STATE_2);
				log.info("fn:creditUserCompanyInfo：\t该借款人未开户");
			}

			// 借款人ID查询其融资主体
			WloanSubject wloanSubjectOn = new WloanSubject();
			wloanSubjectOn.setLoanApplyId(creditUserInfo.getId());
			List<WloanSubject> list = wloanSubjectService.findList(wloanSubjectOn);
			if (list != null && list.size() > 0) {
				WloanSubject wloanSubject = list.get(0);
				creditUserInfo.setWloanSubject(wloanSubject);
				model.addAttribute("wloanSubject", wloanSubject);
				log.info("fn:creditUserCompanyInfo：\tSET借款人融资主体信息");
			}
			// SET借款人帐号信息
			userBankCard.setCreditUserInfo(creditUserInfo);
			log.info("fn:creditUserCompanyInfo：\tSET借款人帐号信息");
			// 核心企业
			CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
			creditSupplierToMiddlemen.setSupplierId(creditUserInfo.getId());
			List<CreditSupplierToMiddlemen> creditSupplierToMiddlemens = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(creditSupplierToMiddlemen);
			if (creditSupplierToMiddlemens != null) {
				if (creditSupplierToMiddlemens.size() > 0) {
					CreditSupplierToMiddlemen entity = creditSupplierToMiddlemens.get(0);
					CreditUserInfo middlemenCreditUserInfo = creditUserInfoService.get(entity.getMiddlemenId());
					model.addAttribute("creditUserInfo2", middlemenCreditUserInfo);
					log.info("fn:creditUserCompanyInfo：\tset借款人上游核心企业帐号信息");
				}
			} else {
				model.addAttribute("creditUserInfo2", new CreditUserInfo());
				log.info("fn:creditUserCompanyInfo：\tset借款人上游核心企业帐号信息不存在");
			}

			// 借款人用户信息
			model.addAttribute("creUserInfo", creditUserInfo);
			// 借款人银行卡信息
			model.addAttribute("userBankCard", userBankCard);
			log.info("fn:creditUserCompanyInfo：\t返回借款人银行卡等开户信息 ...");
		}
		return "modules/sys/creditUserCompanyInfoNew";
	}

	/**
	 * 
	 * methods: creditUserCompanyInfo <br>
	 * description: 查询借款人开户信息 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月5日 下午5:31:18
	 * 
	 * @param creditUserInfo
	 * @param model
	 * @return
	 */
	public String creditUserCompanyInfo(CreditUserInfo creditUserInfo, Model model) {

		// 借款人银行卡信息
		CgbUserBankCard userBankCard = null;
		if (creditUserInfo != null) {
			userBankCard = cgbUserBankCardService.findByUserId1(creditUserInfo.getId());
			if (userBankCard != null) {
				// state，0：未认证， 1:已认证
				CreditAnnexFile annexFile = new CreditAnnexFile();
				annexFile.setOtherId(creditUserInfo.getId());
				List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFile);
				model.addAttribute("creditAnnexFileList", creditAnnexFileList);
				log.info("fn:creditUserCompanyInfo：\t该借款人开户资料查询成功");
			} else {// 未开户
				userBankCard = new CgbUserBankCard();
				userBankCard.setState(BANK_STATE_2);
				log.info("fn:creditUserCompanyInfo：\t该借款人未开户");
			}

			// 借款人ID查询其融资主体
			WloanSubject wloanSubjectOn = new WloanSubject();
			wloanSubjectOn.setLoanApplyId(creditUserInfo.getId());
			List<WloanSubject> list = wloanSubjectService.findList(wloanSubjectOn);
			if (list != null && list.size() > 0) {
				WloanSubject wloanSubject = list.get(0);
				creditUserInfo.setWloanSubject(wloanSubject);
				model.addAttribute("wloanSubject", wloanSubject);
				log.info("fn:creditUserCompanyInfo：\tSET借款人融资主体信息");
			}
			// SET借款人帐号信息
			userBankCard.setCreditUserInfo(creditUserInfo);
			log.info("fn:creditUserCompanyInfo：\tSET借款人帐号信息");
			// 核心企业
			CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
			creditSupplierToMiddlemen.setSupplierId(creditUserInfo.getId());
			List<CreditSupplierToMiddlemen> creditSupplierToMiddlemens = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(creditSupplierToMiddlemen);
			if (creditSupplierToMiddlemens != null) {
				if (creditSupplierToMiddlemens.size() > 0) {
					CreditSupplierToMiddlemen entity = creditSupplierToMiddlemens.get(0);
					CreditUserInfo middlemenCreditUserInfo = creditUserInfoService.get(entity.getMiddlemenId());
					model.addAttribute("creditUserInfo2", middlemenCreditUserInfo);
					log.info("fn:creditUserCompanyInfo：\tset借款人上游核心企业帐号信息");
				}
			} else {
				model.addAttribute("creditUserInfo2", new CreditUserInfo());
				log.info("fn:creditUserCompanyInfo：\tset借款人上游核心企业帐号信息不存在");
			}

			// 借款人银行卡信息
			model.addAttribute("userBankCard", userBankCard);
			log.info("fn:creditUserCompanyInfo：\t返回借款人银行卡等开户信息 ...");
		}
		return "modules/sys/creditUserCompanyInfoNew";
	}

	@ResponseBody
	@RequestMapping(value = "companyInfoSave")
	public Map<String, Object> companyInfoSave(CreditUserInfo creditUserInfo, WloanSubject wloanSubject) {

		Map<String, Object> params = new HashMap<String, Object>();
		log.info("fn:companyInfoSave：\t执行企业信息保存操作 ...");

		try {

			/**
			 * 借款企业帐号信息变更
			 */
			if (creditUserInfo != null) {
				if (CreditUserInfo.CREDIT_USER_TYPE_15.equals(creditUserInfo.getCreditUserType())) { // 抵押业务(借款户)
					// 账户类型
					creditUserInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_15); // 抵押业务(借款户)
					creditUserInfo.setAccountType(CreditUserInfoService.ACCOUNT_TYPE1); // 项目产品类型安心投
					creditUserInfo.setOwnedCompany("房产抵押"); // 所属企业备注房产抵押
				} else if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserInfo.getCreditUserType())) { // 供应商(借款户)
					// 账户类型
					creditUserInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_02); // 借款户
					creditUserInfo.setAccountType(CreditUserInfoService.ACCOUNT_TYPE2); // 项目产品类型(供应链)
					CreditSupplierToMiddlemen supplierToMiddlemen = new CreditSupplierToMiddlemen();
					supplierToMiddlemen.setSupplierId(creditUserInfo.getId());
					List<CreditSupplierToMiddlemen> supplierToMiddlemens = creditSupplierToMiddlemenService.findList(supplierToMiddlemen);
					if (supplierToMiddlemens != null) {
						if (supplierToMiddlemens.size() > 0) {
							if (supplierToMiddlemens.get(0) != null) {
								String middlementId = supplierToMiddlemens.get(0).getMiddlemenId();
								CreditUserInfo middlementCreditUserInfo = creditUserInfoService.get(middlementId); // 代偿户
								if (middlementCreditUserInfo != null) {
									creditUserInfo.setOwnedCompany(middlementCreditUserInfo.getEnterpriseFullName()); // 供应商(借款户)所属企业
								} else { // 所属企业是他自己
									creditUserInfo.setOwnedCompany(creditUserInfo.getEnterpriseFullName());
								}
							}
						}
					}
				} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserInfo.getCreditUserType())) { // 代偿户
					creditUserInfo.setOwnedCompany(creditUserInfo.getEnterpriseFullName()); // 核心企业(代偿户)所属企业是它自己
				}
				creditUserInfo.setEnterpriseFullName(creditUserInfo.getEnterpriseFullName()); // 企业名称
				creditUserInfo.setUpdateDate(new Date()); // 更新时间
				if (wloanSubject != null) {
					creditUserInfo.setName(wloanSubject.getAgentPersonName()); // 联系人姓名
					creditUserInfo.setCertificateNo(wloanSubject.getAgentPersonCertNo()); // 联系人身份证号码
				}
				int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
				if (creditUserInfoUpdateFlag == 1) {
					log.info("fn:companyInfoSave：\t企业帐号信息更新成功 ...");
				} else {
					log.info("fn:companyInfoSave：\t企业帐号信息更新成功 ...");
				}

				/**
				 * 查询银行编码对照表中的银行卡信息
				 */
				CicmorganBankCode cicmorganBankCode = new CicmorganBankCode();
				if (wloanSubject != null) {
					cicmorganBankCode = cicmorganBankCodeService.get(wloanSubject.getLoanBankCode()); // 根据银行编码对照表的主键ID，查询银行编码
				}

				/**
				 * 借款企业银行卡信息变更/新增
				 */
				CgbUserBankCard cgbUserBankCard = new CgbUserBankCard();
				cgbUserBankCard.setUserId(creditUserInfo.getId());
				List<CgbUserBankCard> cgbUserBankCards = cgbUserBankCardDao.findCreditList(cgbUserBankCard);
				if (cgbUserBankCards != null) {
					if (cgbUserBankCards.size() > 0) {
						CgbUserBankCard creditUserBank = cgbUserBankCards.get(0);
						creditUserBank.setAccountId(creditUserInfo.getAccountId()); // 账户ID
						creditUserBank.setUserId(creditUserInfo.getId()); // 帐号ID
						if (wloanSubject != null) {
							creditUserBank.setBankAccountNo(wloanSubject.getLoanBankNo()); // 银行账户
							creditUserBank.setBankName(wloanSubject.getLoanBankName());
						}
						creditUserBank.setIsDefault(UserBankCard.DEFAULT_YES); // 默认卡
						if (cicmorganBankCode != null) {
							creditUserBank.setBankNo(cicmorganBankCode.getBankCode()); // 银行编码
						}
						creditUserBank.setUpdateDate(new Date()); // 更新时间
						int creditUserBankUpdateFlag = cgbUserBankCardDao.update(creditUserBank);
						if (creditUserBankUpdateFlag == 1) {
							log.info("fn:companyInfoSave：\t企业银行卡信息更新成功 ...");
						} else {
							log.info("fn:companyInfoSave：\t企业银行卡信息更新失败 ...");
						}
					} else if (cgbUserBankCards.size() == 0) {
						CgbUserBankCard newCgbUserBankCard = new CgbUserBankCard();
						newCgbUserBankCard.setId(IdGen.uuid()); // 主键
						newCgbUserBankCard.setUserId(creditUserInfo.getId()); // 帐号ID
						newCgbUserBankCard.setAccountId(creditUserInfo.getAccountId()); // 账户ID
						if (wloanSubject != null) {
							newCgbUserBankCard.setBankAccountNo(wloanSubject.getLoanBankNo()); // 银行账户
							newCgbUserBankCard.setBankName(wloanSubject.getLoanBankName()); // 银行名称
						}
						if (cicmorganBankCode != null) {
							newCgbUserBankCard.setBankNo(cicmorganBankCode.getBankCode()); // 银行编码
						}
						newCgbUserBankCard.setBeginBindDate(new Date()); // 开始绑卡时间
						newCgbUserBankCard.setIsDefault(UserBankCard.DEFAULT_YES); // 默认银行卡
						newCgbUserBankCard.setState(BANK_STATE_2); // 新增时未开户
						newCgbUserBankCard.setBindDate(new Date()); // 绑卡时间
						newCgbUserBankCard.setCreateDate(new Date()); // 创建时间
						newCgbUserBankCard.setUpdateDate(new Date()); // 更新时间
						int newCgbUserBankCardInsertFlag = cgbUserBankCardDao.insert(newCgbUserBankCard);
						if (newCgbUserBankCardInsertFlag == 1) {
							log.info("fn:companyInfoSave：\t企业银行卡信息新增成功 ...");
						} else {
							log.info("fn:companyInfoSave：\t企业银行卡信息新增失败 ...");
						}
					}
				}

				/**
				 * 借款企业融资主体变更/新增
				 */
				WloanSubject entity = new WloanSubject();
				entity.setLoanApplyId(creditUserInfo.getId());
				List<WloanSubject> subjects = wloanSubjectService.findList(entity);
				if (subjects != null) {
					if (subjects.size() > 0) {
						WloanSubject subject = subjects.get(0);
						if (subject != null) { // 融资主体已存在
							subject.setLoanApplyId(creditUserInfo.getId()); // 借款人ID
							subject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2); // 融资类型企业
							subject.setCompanyName(creditUserInfo.getEnterpriseFullName()); // 企业名称
							if (wloanSubject != null) {
								subject.setLoanUser(wloanSubject.getLoanUser()); // 法人姓名
								subject.setLoanPhone(creditUserInfo.getPhone()); // 借款人帐号
								subject.setLoanIdCard(wloanSubject.getCorporationCertNo()); // 法人证件号码.
								subject.setRegistAddress(wloanSubject.getRegistAddress()); // 注册地
								subject.setCorporationCertType(wloanSubject.getCorporationCertType()); // 法人证件类型
								subject.setCorporationCertNo(wloanSubject.getCorporationCertNo()); // 法人证件号码
								subject.setBusinessLicenseType(wloanSubject.getBusinessLicenseType()); // 证照类型
								subject.setBusinessNo(wloanSubject.getBusinessNo()); // 证照号码
								subject.setTaxCode(wloanSubject.getTaxCode()); // 税务登记证
								subject.setOrganNo(wloanSubject.getOrganNo()); // 组织机构代码
								subject.setLoanBankName(wloanSubject.getLoanBankName()); // 银行名称
								if (cicmorganBankCode != null) {
									subject.setLoanBankCode(cicmorganBankCode.getBankCode()); // 银行编码
								}
								subject.setLoanBankNo(wloanSubject.getLoanBankNo()); // 银行账号
								subject.setLoanBankProvince(wloanSubject.getLoanBankProvince()); // 开户省
								subject.setLoanBankCity(wloanSubject.getLoanBankCity()); // 开户市
								subject.setLoanBankCounty(wloanSubject.getLoanBankCounty()); // 开户县区
								subject.setLoanIssuerName(wloanSubject.getLoanIssuerName()); // 支行名称
								subject.setBankPermitCertNo(wloanSubject.getBankPermitCertNo()); // 核准号(银行开户许可证)
								subject.setLoanIssuer(wloanSubject.getLoanIssuer()); // 支行-联行号
								subject.setAgentPersonName(wloanSubject.getAgentPersonName()); // 联系人姓名
								subject.setAgentPersonPhone(wloanSubject.getAgentPersonPhone()); // 联系人手机号码
								subject.setAgentPersonCertType(wloanSubject.getAgentPersonCertType()); // 联系人证件类型
								subject.setAgentPersonCertNo(wloanSubject.getAgentPersonCertNo()); // 联系人证号码
								subject.setEmail(wloanSubject.getEmail()); // 联系人邮箱
								subject.setUpdateDate(new Date()); // 更新时间
								int subjectUpdateFlag = wloanSubjectDao.update(subject);
								if (subjectUpdateFlag == 1) {
									log.info("fn:companyInfoSave：\t借款企业融资主体更新成功 ...");
								} else {
									log.info("fn:companyInfoSave：\t借款企业融资主体更新失败 ...");
								}
							}
						}
					} else if (subjects.size() == 0) { // 新增借款融资主体
						WloanSubject newSubject = new WloanSubject();
						newSubject.setId(IdGen.uuid()); // 主键
						newSubject.setType(WloanSubjectService.WLOAN_SUBJECT_TYPE_2); // 融资主体类型企业
						newSubject.setLoanApplyId(creditUserInfo.getId()); // 借款人ID
						newSubject.setCompanyName(creditUserInfo.getEnterpriseFullName()); // 企业名称
						newSubject.setLoanPhone(creditUserInfo.getPhone()); // 借款人/操作人手机号码
						newSubject.setLoanBankPhone(creditUserInfo.getPhone()); // 银行预留手机
						newSubject.setLoanIdCard(wloanSubject.getCorporationCertNo()); // 借款人法人身份证号码
						newSubject.setLoanUser(wloanSubject.getLoanUser()); // 法人姓名
						newSubject.setRegistAddress(wloanSubject.getRegistAddress()); // 注册地
						newSubject.setCorporationCertType(wloanSubject.getCorporationCertType()); // 法人证件类型
						newSubject.setCorporationCertNo(wloanSubject.getCorporationCertNo()); // 法人证件号码
						newSubject.setBusinessLicenseType(wloanSubject.getBusinessLicenseType()); // 证照类型
						newSubject.setBusinessNo(wloanSubject.getBusinessNo()); // 证照号码
						newSubject.setTaxCode(wloanSubject.getTaxCode()); // 税务登记证
						newSubject.setOrganNo(wloanSubject.getOrganNo()); // 组织机构代码
						newSubject.setLoanBankName(wloanSubject.getLoanBankName()); // 银行名称
						if (cicmorganBankCode != null) {
							newSubject.setLoanBankCode(cicmorganBankCode.getBankCode()); // 银行编码
						}
						newSubject.setLoanBankNo(wloanSubject.getLoanBankNo()); // 银行账号
						newSubject.setLoanBankProvince(wloanSubject.getLoanBankProvince()); // 开户省
						newSubject.setLoanBankCity(wloanSubject.getLoanBankCity()); // 开户市
						newSubject.setLoanBankCounty(wloanSubject.getLoanBankCounty()); // 开户县区
						newSubject.setLoanIssuerName(wloanSubject.getLoanIssuerName()); // 支行名称
						newSubject.setBankPermitCertNo(wloanSubject.getBankPermitCertNo()); // 核准号(银行开户许可证)
						newSubject.setLoanIssuer(wloanSubject.getLoanIssuer()); // 支行-联行号
						newSubject.setAgentPersonName(wloanSubject.getAgentPersonName()); // 联系人姓名
						newSubject.setAgentPersonPhone(wloanSubject.getAgentPersonPhone()); // 联系人手机号码
						newSubject.setAgentPersonCertType(wloanSubject.getAgentPersonCertType()); // 联系人证件类型
						newSubject.setAgentPersonCertNo(wloanSubject.getAgentPersonCertNo()); // 联系人证号码
						newSubject.setEmail(wloanSubject.getEmail()); // 联系人邮箱
						newSubject.setIsEntrustedPay(WloanSubjectService.IS_ENTRUSTED_PAY_0); // 受托支付：否
						newSubject.setCreateDate(new Date()); // 创建时间
						newSubject.setUpdateDate(new Date()); // 更新时间
						int newSubjectInsertFlag = wloanSubjectDao.insert(newSubject);
						if (newSubjectInsertFlag == 1) {
							log.info("fn:companyInfoSave：\t借款企业融资主体新增成功 ...");
						} else {
							log.info("fn:companyInfoSave：\t借款企业融资主体新增失败 ...");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.put("message", "<b>保存操作执行成功 </b>");
		return params;
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:view")
	@RequestMapping(value = "form")
	public String form(CreditUserInfo creditUserInfo, Model model) {

		model.addAttribute("creditUserInfo", creditUserInfo);
		return "modules/credit/userinfo/creditUserInfoForm";
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:edit")
	@RequestMapping(value = "save")
	public String save(CreditUserInfo creditUserInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditUserInfo)) {
			return form(creditUserInfo, model);
		}
		creditUserInfoService.save(creditUserInfo);
		addMessage(redirectAttributes, "保存信贷用户成功");
		return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?repage";
	}

	@RequiresPermissions("credit:userinfo:creditUserInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditUserInfo creditUserInfo, RedirectAttributes redirectAttributes) {

		creditUserInfoService.delete(creditUserInfo);
		addMessage(redirectAttributes, "删除信贷用户成功");
		return "redirect:" + Global.getAdminPath() + "/credit/userinfo/creditUserInfo/?repage";
	}

}