package com.power.platform.sys.web.credit;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibm.icu.text.SimpleDateFormat;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.dao.info.CreditInfoDao;
import com.power.platform.credit.dao.pack.CreditPackDao;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.creditOrder.CreditOrder;
import com.power.platform.credit.entity.electronic.ElectronicSign;
import com.power.platform.credit.entity.electronic.ElectronicSignTranstail;
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.creditOrder.CreditOrderService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.credit.service.electronic.ElectronicSignTranstailService;
import com.power.platform.credit.service.info.CreditInfoService;
import com.power.platform.credit.service.middlemen.CreditMiddlemenRateService;
import com.power.platform.credit.service.pack.CreditPackService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.credit.service.voucher.CreditVoucherService;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermProjectPlanPoJo;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.entity.WloanTermProject2;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.utils.LoanAgreementPdfUtil;
import com.power.platform.utils.LoanPdfContractUtil;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

import cn.tsign.ching.eSign.SignHelper;
import cn.tsign.ching.utils.FileHelper;

/**
 * 
 * 类: CreditUserApplyController <br>
 * 描述: 借款申请. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年6月20日 上午10:01:42
 */
@Controller
@RequestMapping(value = "${adminPath}/apply/creditUserApply")
public class CreditUserApplyController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(CreditUserApplyController.class);

	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;
	@Autowired
	private UserBankCardService userBankCardService;
	@Resource
	private UserBankCardDao userBankCardDao;
	@Autowired
	private CreditInfoService creditInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditVoucherService creditVoucherService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	@Autowired
	private CreditInfoDao creditInfoDao;
	@Autowired
	private CreditPackDao creditPackDao;
	@Resource
	private ElectronicSignService electronicSignService;
	@Resource
	private ElectronicSignDao electronicSignDao;
	@Resource
	private ElectronicSignTranstailService electronicSignTranstailService;
	@Resource
	private ElectronicSignTranstailDao electronicSignTranstailDao;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private CreditMiddlemenRateService creditMiddlemenRateService;
	@Autowired
	private CreditOrderService creditOrderService;
	@Autowired
	private CreditPackService creditPackService;
	@Autowired
	private SendSmsService sendSmsService;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;

	@ModelAttribute
	public CreditUserApply get(@RequestParam(required = false) String id) {

		CreditUserApply entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditUserApplyService.get(id);
		}
		if (entity == null) {
			entity = new CreditUserApply();
		}
		return entity;
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditUserApply> page = creditUserApplyService.findPage(new Page<CreditUserApply>(request, response), creditUserApply);
		List<CreditUserApply> list = page.getList();
		for (CreditUserApply entity : list) {
			String replaceUserId = entity.getReplaceUserId(); // 代偿户ID.
			CreditUserInfo creditReplaceUserInfo = creditUserInfoService.get(replaceUserId); // 代偿人信息.
			if (null != creditReplaceUserInfo) {
				entity.setReplaceUserEnterpriseFullName(creditReplaceUserInfo.getEnterpriseFullName());
			}
			String projectDataId = entity.getProjectDataId(); // 资料ID.
			CreditInfo creditInfo = creditInfoService.get(projectDataId);
			if (null != creditInfo) { // 资料信息
				String creditUserId = creditInfo.getCreditUserId(); // 借款人ID.
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId); // 借款人信息.
				if (null != creditUserInfo) {
					entity.setLoanUserId(creditUserInfo.getId());
					entity.setLoanUserPhone(creditUserInfo.getPhone());
					entity.setLoanUserName(creditUserInfo.getName());
					entity.setLoanUserEnterpriseFullName(creditUserInfo.getEnterpriseFullName());
				}
			}
		}
		model.addAttribute("page", page);
		return "modules/credit/userApply/creditUserApplyList";
	}

	// 删除申请
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "deleteApply", "" })
	public String deleteApply(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		if (creditUserApply != null && creditUserApply.getId() != null) {
			creditUserApplyService.delete(creditUserApply);
		}
		String limit = request.getParameter("limit");
		if ("1".equals(limit)) {// 核心企业

			return "redirect:" + Global.getAdminPath() + "/sys/user/project?replaceUserId=" + creditUserApply.getReplaceUserId();
		} else if ("2".equals(limit)) {// 供应商

			return "redirect:" + Global.getAdminPath() + "/sys/user/project?creditSupplyId=" + creditUserApply.getCreditSupplyId();
		}
		return null;
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney1", "" })
	public String applyMoney1(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		String id = request.getParameter("creditUserId");
		CreditUserInfo creditUserInfo = creditUserInfoService.get(id);
		model.addAttribute("creditUser", creditUserInfo);
		return "modules/applyMoney/applyMoney1";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney2", "" })
	public String applyMoney2(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 判断是否显示数据
		String step = request.getParameter("step");
		if (step != null) {// 显示数据
			model.addAttribute("step", step);
		} else {
			model.addAttribute("step", "");
			// 创建日期.
			creditUserApply.setCreateDate(new Date());
			// 更新日期.
			creditUserApply.setUpdateDate(new Date());
			// 备注.
			creditUserApply.setRemarks("核心企业【借款申请】");
			creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_1);// 第一步完成
			// 状态.
			creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_0);// 草稿
			// 是否授权
			creditUserApply.setIsAuthorize("FALSE");
			// 核心企业是否通知供应商
			creditUserApply.setIsNotice("FALSE");
			creditUserApplyService.save(creditUserApply);
		}
		// 查询供应商
		CreditSupplierToMiddlemen entity = new CreditSupplierToMiddlemen();
		entity.setMiddlemenId(creditUserApply.getReplaceUserId());
		List<CreditSupplierToMiddlemen> list = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemensList(entity);

		List<CreditSupplierToMiddlemen> newList = new ArrayList<CreditSupplierToMiddlemen>();
		for (CreditSupplierToMiddlemen creditSupplierToMiddlemen : list) {
			CreditUserInfo supplierUser = creditSupplierToMiddlemen.getSupplierUser();
			if (supplierUser != null) {
				String isCreateBasicInfo = supplierUser.getIsCreateBasicInfo();
				String openAccountState = supplierUser.getOpenAccountState();
				if (null != openAccountState && isCreateBasicInfo != null) {
					if (CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue().equals(openAccountState) && isCreateBasicInfo.equals(CreditUserInfo.IS_CREATE_BASIC_INFO_1)) {
						newList.add(creditSupplierToMiddlemen);
					}
				}
			}
		}

		CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserApply.getReplaceUserId());
		model.addAttribute("creditUser", creditUserInfo);// 核心企业信息
		model.addAttribute("creditSupplierToMiddlemenList", newList);// 供应商列表
		return "modules/applyMoney/applyMoney2";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney3", "" })
	public String applyMoney3(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 确认登录企业的角色
		CreditUserInfo loginCreditUserInfo = null;
		String creditUserId = creditUserApply.getReplaceUserId();
		String supplyUserId = creditUserApply.getCreditSupplyId();
		if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserApply.getCreditUserType())) { // 供应商
			loginCreditUserInfo = creditUserInfoService.get(supplyUserId);// 供应商
		} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserApply.getCreditUserType())) { // 核心企业
			loginCreditUserInfo = creditUserInfoService.get(creditUserId);// 核心企业
		}
		model.addAttribute("loginCreditUserInfo", loginCreditUserInfo);

		// 驳回状态申请改为草稿状态
		if (creditUserApply.getState().equals(CreditUserApplyService.CREDIT_USER_APPLY_STATE_3)) {
			creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_0);
			creditUserApplyService.save(creditUserApply);
		}
		String step = request.getParameter("step");
		CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);// 核心企业
		model.addAttribute("creditUser", creditUserInfo);
		CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);// 供应商
		model.addAttribute("supplyUser", supplyUser);
		if (step != null) {// 显示数据
			model.addAttribute("step", step);
			String creditInfoId = creditUserApply.getProjectDataId();
			if (creditInfoId != null) {
				CreditPack creditPack = new CreditPack();
				creditPack.setCreditInfoId(creditInfoId);
				List<CreditPack> list = creditPackDao.findList(creditPack);
				if (list.size() > 0) {
					creditPack = list.get(0);
					model.addAttribute("creditPack", creditPack);
				}
			}
		} else {// 正常申请
			model.addAttribute("step", "");
			// 保存申请步骤(完成第二步)
			creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_2);
			creditUserApply.setCreditApplyName(supplyUser.getEnterpriseFullName());
			creditUserApplyService.save(creditUserApply);
		}
		return "modules/applyMoney/applyMoney3";
	}

	// 上传资料页面.
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney4", "" })
	public String applyMoney4(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		// Boolean containAgreement = false;
		String saveInfo = request.getParameter("saveInfo");
		if (saveInfo != null) {// 保存
			String creditUserId = creditUserApply.getReplaceUserId();// 核心企业id
			String supplyUserId = creditUserApply.getCreditSupplyId();// 供应商id
			// 确认登录企业的角色
			CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);// 供应商
			CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);// 核心企业
			if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserApply.getCreditUserType())) { // 供应商
				model.addAttribute("loginCreditUserInfo", supplyUser);
			} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserApply.getCreditUserType())) { // 核心企业
				model.addAttribute("loginCreditUserInfo", creditUser);
			}
			if (supplyUser != null) {
				String creditInfoId = creditUserApply.getProjectDataId();
				if (creditInfoId != null) {// 修改内容
					CreditPack creditPack = new CreditPack();
					creditPack.setCreditInfoId(creditInfoId);
					List<CreditPack> creditPackList = creditPackService.findList(creditPack);
					if (creditPackList != null && creditPackList.size() > 0) {
						creditPack = creditPackList.get(0);
						creditPack.setName(creditUserApply.getCreditPack().getName());
						creditPack.setNo(creditUserApply.getCreditPack().getNo());
						creditPack.setMoney(creditUserApply.getCreditPack().getMoney());
						creditPack.setType(creditUserApply.getCreditPack().getType());
						creditPack.setUserdDate(creditUserApply.getCreditPack().getUserdDate());
						creditPack.setSignDate(creditUserApply.getCreditPack().getSignDate());
						creditPackService.save(creditPack);
					}
				} else {
					String id = IdGen.uuid();
					CreditInfo creditInfo = new CreditInfo();
					creditInfo.setId(id);
					creditInfo.setCreditUserId(supplyUser.getId());
					creditInfo.setName(supplyUser.getEnterpriseFullName() + DateUtils.getDateStr());// 资料名称为姓名+时间戳
					creditInfo.setCreateDate(new Date());
					creditInfo.setUpdateDate(new Date());
					int i = creditInfoDao.insert(creditInfo);
					if (i > 0) {
						log.info("借款用户资料新增成功" + "[" + creditInfo.getName() + "]");
					}

					// 添加借款合同
					if (Integer.parseInt(creditUserApply.getFinancingStep()) < 3) {
						// 查询最近一次成功的申请
						List<CreditUserApply> applyAgreements = creditUserApplyService.findListForAgreement(supplyUserId);
						if (applyAgreements != null && applyAgreements.size() > 0) {
							String infoAgreementId = applyAgreements.get(0).getProjectDataId();
							CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
							annexFileAgreement.setOtherId(infoAgreementId);
							annexFileAgreement.setType("1");
							List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
							if (annexFileAgreements != null && annexFileAgreements.size() > 0) {
								annexFileAgreement = annexFileAgreements.get(0);
								CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
								creditAnnexFile.setOtherId(id); // 资料信息ID.
								creditAnnexFile.setUrl(annexFileAgreement.getUrl()); // 图片保存路径.
								creditAnnexFile.setType("1"); // 类型
								String remark = "交易合同";
								creditAnnexFile.setRemark(remark); // 备注.
								int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, 1, IdGen.uuid());
								if (tag == 1) {
									model.addAttribute("annexAgreement", creditAnnexFile);
								} else {
								}
							}
						}
					}

					// 保存借款资料id
					creditUserApply.setProjectDataId(id);
					// 保存借款资料名称
					creditUserApply.setCreditApplyName(creditInfo.getName());
					creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_3);
					creditUserApplyService.save(creditUserApply);
					// 新增合同信息
					CreditPack creditPack = new CreditPack();
					creditPack.setCreditInfoId(id);
					creditPack.setCoreName(creditUser.getEnterpriseFullName());
					creditPack.setLoanName(supplyUser.getEnterpriseFullName());
					creditPack.setName(creditUserApply.getCreditPack().getName());
					creditPack.setNo(creditUserApply.getCreditPack().getNo());
					creditPack.setMoney(creditUserApply.getCreditPack().getMoney());
					creditPack.setType(creditUserApply.getCreditPack().getType());
					creditPack.setUserdDate(creditUserApply.getCreditPack().getUserdDate());
					creditPack.setSignDate(creditUserApply.getCreditPack().getSignDate());
					creditPackService.save(creditPack);

				}
			}
			return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/applyMoney3?id=" + creditUserApply.getId() + "&step=3";
		} else {
			String step = request.getParameter("step");
			String error = request.getParameter("error");
			if (error != null) {
				model.addAttribute("error", "上传资料不全，请补齐！");
			}

			String creditUserId = creditUserApply.getReplaceUserId();// 核心企业id
			String supplyUserId = creditUserApply.getCreditSupplyId();// 供应商id
			// 确认登录企业的角色
			CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);// 供应商
			CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);// 核心企业

			if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserApply.getCreditUserType())) { // 供应商
				model.addAttribute("loginCreditUserInfo", supplyUser);
			} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserApply.getCreditUserType())) { // 核心企业
				model.addAttribute("loginCreditUserInfo", creditUser);
			}
			model.addAttribute("supplyUser", supplyUser);
			model.addAttribute("creditUser", creditUser);

			if (step != null) {// 显示数据
				model.addAttribute("step", step);
				Double voucherSum = 0.0;
				String creditInfoId = creditUserApply.getProjectDataId();// 借款信息id
				CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
				creditAnnexFile.setOtherId(creditInfoId);
				List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findList(creditAnnexFile);
				for (CreditAnnexFile creditAnnexFile2 : creditAnnexFileList) {
					if ("7".equals(creditAnnexFile2.getType())) {
						model.addAttribute("letter", "yes");
					}
					// if("1".equals(creditAnnexFile2.getType())){
					// containAgreement = true;
					// }
				}
				// //未上传合同
				// if(!containAgreement){
				// //查询以往合同影印件
				//
				// //查询最近一次成功的申请
				// CreditUserApply applyAgreement = new CreditUserApply();
				// applyAgreement.setCreditSupplyId(supplyUserId);
				// List<CreditUserApply> applyAgreements =
				// creditUserApplyService.findList(applyAgreement);
				// if(applyAgreements!=null && applyAgreements.size()>0){
				// String infoAgreementId =
				// applyAgreements.get(0).getProjectDataId();
				// CreditAnnexFile creditAgreement = new CreditAnnexFile();
				// creditAgreement.setOtherId(creditInfoId);
				// creditAgreement.setType("1");
				// List<CreditAnnexFile> creditAgreements =
				// creditAnnexFileService.findList(creditAgreement);
				// if(creditAgreements!=null && creditAgreements.size()>0){
				// creditAgreement = creditAgreements.get(0);
				// creditAnnexFileList.add(creditAgreement);
				// }
				// }
				// }
				model.addAttribute("creditAnnexFileList", creditAnnexFileList);
				CreditVoucher creditVoucher = new CreditVoucher();
				creditVoucher.setCreditInfoId(creditInfoId);
				Page<CreditVoucher> page = new Page<CreditVoucher>();
				page.setOrderBy(" a.create_date ASC");
				creditVoucher.setPage(page);
				List<CreditVoucher> creditVoucherList = creditVoucherService.findList(creditVoucher);
				for (CreditVoucher creditVoucher2 : creditVoucherList) {
					voucherSum += Double.parseDouble(creditVoucher2.getMoney());
					CreditAnnexFile creditAnnexFile2 = creditAnnexFileService.get(creditVoucher2.getAnnexId());
					if (null != creditAnnexFile2) {
						creditVoucher2.setUrl(creditAnnexFile2.getUrl());
					}
				}
				model.addAttribute("creditVoucherList", creditVoucherList);
				model.addAttribute("voucherSum", voucherSum.toString());
				// 合同信息回显
				if (creditInfoId != null) {
					CreditPack creditPack = new CreditPack();
					creditPack.setCreditInfoId(creditInfoId);
					List<CreditPack> list = creditPackDao.findList(creditPack);
					if (list.size() > 0) {
						creditPack = list.get(0);
						model.addAttribute("creditPack", creditPack);
						if (creditPack != null) {
							model.addAttribute("packNo", creditPack.getNo());
						}
					}
				}
			} else {// 正常申请
					// 跳转至第4步
				model.addAttribute("step", "4");
				if (supplyUser != null) {
					String creditInfoId = creditUserApply.getProjectDataId();

					if (creditInfoId != null) {// 修改内容
						/**
						 * 展示已上传的资料信息
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditInfoId);
						List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findList(creditAnnexFile);
						for (CreditAnnexFile creditAnnexFile2 : creditAnnexFileList) {
							if ("7".equals(creditAnnexFile2.getType())) {
								model.addAttribute("letter", "yes");
							}
						}
						model.addAttribute("creditAnnexFileList", creditAnnexFileList);
						/**
						 * 当前是第三部编辑状态，跳转至第四步，如果资料ID非NULL，展示发票列表.
						 */
						Double voucherSum = 0.0;
						CreditVoucher creditVoucher = new CreditVoucher();
						creditVoucher.setCreditInfoId(creditInfoId);
						Page<CreditVoucher> page = new Page<CreditVoucher>();
						page.setOrderBy(" a.create_date ASC");
						creditVoucher.setPage(page);
						List<CreditVoucher> creditVoucherList = creditVoucherService.findList(creditVoucher);
						for (CreditVoucher creditVoucher2 : creditVoucherList) {
							voucherSum += Double.parseDouble(creditVoucher2.getMoney());
							CreditAnnexFile creditAnnexFile2 = creditAnnexFileService.get(creditVoucher2.getAnnexId());
							if (null != creditAnnexFile2) {
								creditVoucher2.setUrl(creditAnnexFile2.getUrl());
							}
						}
						// 发票列表
						model.addAttribute("creditVoucherList", creditVoucherList);
						// 发票总额
						model.addAttribute("voucherSum", voucherSum.toString());

						CreditPack creditPack = new CreditPack();
						creditPack.setCreditInfoId(creditInfoId);
						List<CreditPack> creditPackList = creditPackService.findList(creditPack);
						if (creditPackList != null && creditPackList.size() > 0) {
							creditPack = creditPackList.get(0);
							creditPack.setName(creditUserApply.getCreditPack().getName());
							creditPack.setNo(creditUserApply.getCreditPack().getNo());
							creditPack.setMoney(creditUserApply.getCreditPack().getMoney());
							creditPack.setType(creditUserApply.getCreditPack().getType());
							creditPack.setUserdDate(creditUserApply.getCreditPack().getUserdDate());
							creditPack.setSignDate(creditUserApply.getCreditPack().getSignDate());
							creditPackService.save(creditPack);
							model.addAttribute("packNo", creditPack.getNo());
						}
						// 查询当前申请的合同
						CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
						annexFileAgreement.setOtherId(creditUserApply.getProjectDataId());
						annexFileAgreement.setType("1");
						List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
						if (annexFileAgreements != null && annexFileAgreements.size() > 0) {
							annexFileAgreement = annexFileAgreements.get(0);
							model.addAttribute("annexAgreement", annexFileAgreement);
						}

					} else {
						String id = IdGen.uuid();
						CreditInfo creditInfo = new CreditInfo();
						creditInfo.setId(id);
						creditInfo.setCreditUserId(supplyUser.getId());
						creditInfo.setName(supplyUser.getEnterpriseFullName() + DateUtils.getDateStr());// 资料名称为姓名+时间戳
						creditInfo.setCreateDate(new Date());
						creditInfo.setUpdateDate(new Date());
						int i = creditInfoDao.insert(creditInfo);
						if (i > 0) {
							log.info("借款用户资料新增成功" + "[" + creditInfo.getName() + "]");
						}

						if (Integer.parseInt(creditUserApply.getFinancingStep()) < 3) {
							// 添加借款合同
							// 查询最近一次成功的申请
							List<CreditUserApply> applyAgreements = creditUserApplyService.findListForAgreement(supplyUserId);
							if (applyAgreements != null && applyAgreements.size() > 0) {
								String infoAgreementId = applyAgreements.get(0).getProjectDataId();
								CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
								annexFileAgreement.setOtherId(infoAgreementId);
								annexFileAgreement.setType("1");
								List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
								if (annexFileAgreements != null && annexFileAgreements.size() > 0) {
									annexFileAgreement = annexFileAgreements.get(0);
									// creditAnnexFileList.add(creditAgreement);
									CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
									creditAnnexFile.setOtherId(id); // 资料信息ID.
									creditAnnexFile.setUrl(annexFileAgreement.getUrl()); // 图片保存路径.
									creditAnnexFile.setType("1"); // 类型
									String remark = "交易合同";
									creditAnnexFile.setRemark(remark); // 备注.
									// log.info("资料信息ID"+id);
									int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, 1, IdGen.uuid());
									if (tag == 1) {
										// LOG.info("PATH:" + path +
										// ", save success.");
										model.addAttribute("annexAgreement", creditAnnexFile);

									} else {
										// LOG.info("PATH:" + path +
										// ", save failure.");
									}
								}
							}
						}

						// 保存借款资料id
						creditUserApply.setProjectDataId(id);
						// 保存借款资料名称
						creditUserApply.setCreditApplyName(creditInfo.getName());
						// 保存申请步骤(完成第三步)
						creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_3);
						creditUserApplyService.save(creditUserApply);

						// 新增合同信息
						CreditPack creditPack = new CreditPack();
						creditPack.setCreditInfoId(id);
						creditPack.setCoreName(creditUser.getEnterpriseFullName());
						creditPack.setLoanName(supplyUser.getEnterpriseFullName());
						creditPack.setName(creditUserApply.getCreditPack().getName());
						creditPack.setNo(creditUserApply.getCreditPack().getNo());
						creditPack.setMoney(creditUserApply.getCreditPack().getMoney());
						creditPack.setType(creditUserApply.getCreditPack().getType());
						creditPack.setUserdDate(creditUserApply.getCreditPack().getUserdDate());
						creditPack.setSignDate(creditUserApply.getCreditPack().getSignDate());
						creditPackService.save(creditPack);
						model.addAttribute("packNo", creditPack.getNo());
					}
				}
			}
			return "modules/applyMoney/applyMoney4";
		}
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney5", "" })
	public String applyMoney5(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		String step = request.getParameter("step");
		String saveInfo = request.getParameter("saveInfo");
		String creditUserType = request.getParameter("creditUserType"); // 登录用户角色
		String creditUserId = creditUserApply.getReplaceUserId();// 核心企业id
		String supplyUserId = creditUserApply.getCreditSupplyId();// 供应商id
		String creditInfoId = creditUserApply.getProjectDataId();// 借款信息id
		CreditUserInfo loginCreditUserInfo = null;
		if (CreditUserInfo.CREDIT_USER_TYPE_02.equals(creditUserType)) { // 供应商
			loginCreditUserInfo = creditUserInfoService.get(supplyUserId);// 供应商
		} else if (CreditUserInfo.CREDIT_USER_TYPE_11.equals(creditUserType)) { // 核心企业
			loginCreditUserInfo = creditUserInfoService.get(creditUserId);// 核心企业
		}
		model.addAttribute("loginCreditUserInfo", loginCreditUserInfo);
		// 合同信息
		CreditPack pack = null;
		CreditPack creditPack = new CreditPack();
		creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
		List<CreditPack> creditPackList = creditPackService.findList(creditPack);
		if (creditPackList != null && creditPackList.size() > 0) {
			pack = creditPackList.get(0);
			creditUserApply.setPack(pack);
		}
		// 当前系统时间
		creditUserApply.setNowDate(new Date());

		String error = null;
		if (saveInfo != null) {// 保存
			creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_4);
			creditUserApplyService.save(creditUserApply);
			return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/applyMoney4?id=" + creditUserApply.getId() + "&step=4" + "&creditUserType=" + creditUserType;
		} else {
			if (step != null) {// 显示数据
				model.addAttribute("step", step);
			} else {// 正常申请
				model.addAttribute("step", "");
				// 判断资料是否齐全
				Boolean type1 = false;
				Boolean type2 = false;
				Boolean type3 = false;
				Boolean type4 = false;
				Boolean type5 = false;
				Boolean type6 = false;
				Boolean type7 = false;
				CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
				creditAnnexFile.setOtherId(creditUserApply.getProjectDataId());
				List<CreditAnnexFile> creditAnnexFiles = creditAnnexFileService.findList(creditAnnexFile);
				if (creditAnnexFiles != null && creditAnnexFiles.size() > 6) { // 最少7张
					for (CreditAnnexFile creditAnnexFile2 : creditAnnexFiles) {
						if ("1".equals(creditAnnexFile2.getType())) {
							type1 = true;
						}
						if ("2".equals(creditAnnexFile2.getType())) {
							type2 = true;
						}
						if ("3".equals(creditAnnexFile2.getType())) {
							type3 = true;
						}
						if ("4".equals(creditAnnexFile2.getType())) {
							type4 = true;
						}
						if ("5".equals(creditAnnexFile2.getType())) {
							type5 = true;
						}
						if ("6".equals(creditAnnexFile2.getType())) {
							type6 = true;
						}
						if ("7".equals(creditAnnexFile2.getType())) {
							type7 = true;
						}
					}
					if (type1 && type2 && type3 && type4 && type5 && type6 && type7) {
						// 正常执行
						// creditUserApply.setModify("1");
						// 保存申请提交状态
						// creditUserApply.setFileConfirm("1");
						creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_4);
						creditUserApplyService.save(creditUserApply);
					} else {
						// 资料不全
						error = "yes";
						return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/applyMoney4?id=" + creditUserApply.getId() + "&step=4&error=" + error;
					}
				} else {
					// 资料不全
					error = "yes";
					return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/applyMoney4?id=" + creditUserApply.getId() + "&step=4&error=" + error;
				}
			}
		}

		// 发票总额
		double voucherSumD = 0.00D;
		CreditVoucher creditVoucher = new CreditVoucher();
		creditVoucher.setCreditInfoId(creditInfoId);
		List<CreditVoucher> creditVouchers = creditVoucherService.findList(creditVoucher);
		if (creditVouchers.size() > 0) {
			for (CreditVoucher creditVoucher2 : creditVouchers) {
				voucherSumD = NumberUtils.add(voucherSumD, Double.parseDouble(creditVoucher2.getMoney()));
			}
		}

		// 供应商在贷总金额
		Double sumMoney = 0.0;
		CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);// 核心企业
		creditUserApply.setReplaceUserInfo(creditUser);
		CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);// 供应商
		creditUserApply.setSupplyUser(supplyUser);
		creditInfoId = creditInfoId.trim();
		// CreditInfo creditInfo = creditInfoDao.get(creditInfoId);//借款资料
		CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
		creditMiddlemenRate.setCreditUserId(creditUserId);
		List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
		if (creditUserApply.getSpan() != null) {
			for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
				if (creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())) {
					String serviceRate = creditMiddlemenRate2.getServiceRate();
					model.addAttribute("serviceRate", serviceRate);
				}
			}
		}
		WloanSubject wloanSubject = new WloanSubject();
		wloanSubject.setLoanApplyId(supplyUserId);
		List<WloanSubject> wloanSubjectList = wloanSubjectService.findList(wloanSubject);
		if (wloanSubjectList.size() > 0) {
			wloanSubject = wloanSubjectList.get(0);
			creditUserApply.setSupplyLoanSubject(wloanSubject);
		}
		WloanTermProject wloanTermProject = new WloanTermProject();
		wloanTermProject.setSubjectId(wloanSubject.getId());
		List<WloanTermProject> wloanTermProjectList = wloanTermProjectService.findList(wloanTermProject);
		if (wloanTermProjectList.size() > 0) {
			for (WloanTermProject wloanTermProject2 : wloanTermProjectList) {
				if ("4".equals(wloanTermProject2.getState()) || "5".equals(wloanTermProject2.getState()) || "6".equals(wloanTermProject2.getState())) {
					sumMoney += wloanTermProject2.getAmount();
				}
			}
		}

		model.addAttribute("creditUser", creditUser);
		model.addAttribute("supplyUser", supplyUser);
		model.addAttribute("creditMiddlemenRateList", creditMiddlemenRateList);
		model.addAttribute("voucherSum", String.valueOf(voucherSumD));
		model.addAttribute("sumMoney", sumMoney);
		return "modules/applyMoney/applyMoney5";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping("skipApplyMoney6")
	public String skipApplyMoney6(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		try {
			// 供应链融资框架协议/融资申请书
			if (creditUserApply.getBorrPurpose() != null) {
				model.addAttribute("srcPdfFile", CreditURLConfig.SRCPDFFILE + creditUserApply.getBorrPurpose().split("data")[1]);
			} else {
				model.addAttribute("srcPdfFile", ""); // 没有生成
			}
			// 借款人网络借贷风险、禁止性行为及有关事项提示书
			if (creditUserApply.getDeclarationFilePath() != null) {
				model.addAttribute("declarationFilePath", CreditURLConfig.SRCPDFFILE + creditUserApply.getDeclarationFilePath().split("data")[1]);
			} else {
				model.addAttribute("declarationFilePath", ""); // 没有生成
			}
			// 贸易真实性及有关事项提示书
			if (creditUserApply.getZdFilePath() != null) {
				model.addAttribute("zdFilePath", CreditURLConfig.SRCPDFFILE + creditUserApply.getZdFilePath().split("data")[1]);
			} else {
				model.addAttribute("zdFilePath", ""); // 没有生成
			}
			// 授权函
			if (creditUserApply.getShCisFilePath() != null) {
				model.addAttribute("shCisFilePath", CreditURLConfig.SRCPDFFILE + creditUserApply.getShCisFilePath().split("data")[1]);
			} else {
				model.addAttribute("shCisFilePath", ""); // 没有生成
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/applyMoney/applyMoney6";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney6", "" })
	@ResponseBody
	public Map<String, Object> applyMoney6(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String saveInfo = request.getParameter("saveInfo");
			if (saveInfo != null) { // 保存并通知供应商进行授权
				String isNotice = creditUserApply.getIsNotice();
				// 短消息通知供应商进行授权
				String creditSupplyId = creditUserApply.getCreditSupplyId(); // 供应商id
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditSupplyId);
				if (null != creditUserInfo) {
					// 短消息内容，短信通道限制金融理财类的短信不允许发送，这里需要核心企业线下通知供应商进行授权...
					// StringBuffer sms_message = new StringBuffer();
					// sms_message.append("亲爱的供应商您好，核心企业代您发起的一笔融资申请需要进行确认、授权，请登录中投摩根供应链融资平台https://loan.cicmorgan.com进行查看！");
					// 短信
					// weixinSendTempMsgService.ztmgSendRepayRemindMsg(creditUserInfo.getPhone(),
					// sms_message.toString());
					if (!"TRUE".equals(isNotice)) {
						// 邮件主题
						String subject = "供应商授权确认";
						// 邮件内容
						StringBuffer email_message = new StringBuffer();
						email_message.append("亲爱的供应商您好：\n\t您好，核心企业【").append(creditUserInfo.getOwnedCompany()).append("】代您发起的一笔融资申请需要进行确认、授权，请及时登录中投摩根云平台（网址：https://loan.cicmorgan.com）确认相关信息，确保融资申请顺利进行。");
						List<WloanSubject> supplyLoanSubjects = wloanSubjectDao.getByLoanApplyId(creditSupplyId);
						String toMailAddr = null; // 供应商邮箱
						WloanSubject supplyLoanSubject = null;
						if (null != supplyLoanSubjects && supplyLoanSubjects.size() > 0) {
							supplyLoanSubject = supplyLoanSubjects.get(0);
							toMailAddr = supplyLoanSubject.getEmail();
						}
						// 融资主体
						String cc = "liyun@cicmorgan.com"; // 平台技术人员，便于跟踪邮件是否发送
						SendMailUtil.ztmgSendRepayRemindEmailMsg(toMailAddr, cc, subject.toString(), email_message.toString());
					}
				}

				// 保存融资金额等相关信息
				String[] rates = creditUserApply.getSpan().split(",");
				String span = rates[0];
				String rate = rates[1];
				creditUserApply.setSpan(span);
				creditUserApply.setLenderRate(rate);
				creditUserApply.setIsNotice("TRUE"); // 是否通知，TRUE
				creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_5);
				creditUserApplyService.save(creditUserApply);

				result.put("state", "0");
				result.put("is_notice", isNotice);
				result.put("msg", "成功");
				return result;
			} else { // 供应商授权及签署相关协议
				// 是否授权
				String isAuthorize = creditUserApply.getIsAuthorize();
				if ("TRUE".equals(isAuthorize)) {
					result.put("state", "0");
					result.put("is_authorize", isAuthorize);
					result.put("msg", "成功");
					return result;
				}
				String[] rates = creditUserApply.getSpan().split(",");
				String span = rates[0];
				String rate = rates[1];
				String servicesRate = rates[2]; // 服务费率.
				creditUserApply.setSpan(span);
				creditUserApply.setLenderRate(rate);
				// 借款人网络借贷风险、禁止性行为及有关事项提示书
				String promptBookPdfPath = LoanPdfContractUtil.createPromptBookPdf(creditUserApply, servicesRate);
				creditUserApply.setDeclarationFilePath(promptBookPdfPath);
				creditUserApplyService.save(creditUserApply); // 更新，设置借款人网络借贷风险、禁止性行为及有关事项提示书的路径

				/**
				 * 电子签章统一印章生成
				 */
				// 初始化项目，做全局使用，只初始化一次即可
				SignHelper.initProject();
				System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
				/**
				 * 平台
				 */
				String platformUserOrganizeAccountId = ""; // 平台电子签章id
				// 平台融资主体
				WloanSubject platformLoanSubject = new WloanSubject();
				platformLoanSubject.setLoanApplyId("SYS_GENERATE_002"); // 平台营销帐号id
				List<WloanSubject> platformLoanSubjects = wloanSubjectService.findList(platformLoanSubject);
				if (null != platformLoanSubjects && platformLoanSubjects.size() > 0) {
					platformLoanSubject = platformLoanSubjects.get(0);
				}
				// 创建企业客户账号(平台)
				ElectronicSign platformElectronicSign = new ElectronicSign();
				platformElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				platformElectronicSign.setUserId("SYS_GENERATE_002");
				List<ElectronicSign> platformElectronicSigns = electronicSignService.findList(platformElectronicSign);
				if (platformElectronicSigns != null && platformElectronicSigns.size() > 0) {
					// 获取已保存的平台电子签章id
					platformUserOrganizeAccountId = platformElectronicSigns.get(0).getSignId();
				} else {
					// 平台没有保存电子签章id，即重新生成
					platformUserOrganizeAccountId = SignHelper.addOrganizeAccountZtmg(platformLoanSubject);
					platformElectronicSign.setId(IdGen.uuid());
					platformElectronicSign.setSignId(platformUserOrganizeAccountId);
					platformElectronicSign.setCreateDate(new Date());
					platformElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
					electronicSignDao.insert(platformElectronicSign);
				}

				/**
				 * 供应商
				 */
				String userOrganizeAccountId1 = ""; // 供应商电子签章id
				// 供应商融资主体
				WloanSubject wloanSubject1 = new WloanSubject();
				wloanSubject1.setLoanApplyId(creditUserApply.getCreditSupplyId());
				List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubject1);
				if (null != wloanSubjectsList1 && wloanSubjectsList1.size() > 0) {
					wloanSubject1 = wloanSubjectsList1.get(0);
				}
				// 创建企业客户账号(供应商)
				ElectronicSign electronicSign1 = new ElectronicSign();
				electronicSign1.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				electronicSign1.setUserId(creditUserApply.getCreditSupplyId());
				List<ElectronicSign> electronicSignsList1 = electronicSignService.findList(electronicSign1);
				if (electronicSignsList1 != null && electronicSignsList1.size() > 0) {
					// 获取已保存的供应商电子签章id
					userOrganizeAccountId1 = electronicSignsList1.get(0).getSignId();
				} else {
					// 该供应商没有保存电子签章id，即重新生成
					userOrganizeAccountId1 = SignHelper.addOrganizeAccountZtmg(wloanSubject1);
					electronicSign1.setId(IdGen.uuid());
					electronicSign1.setSignId(userOrganizeAccountId1);
					electronicSign1.setCreateDate(new Date());
					electronicSign1.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
					electronicSignDao.insert(electronicSign1);
				}

				/**
				 * 核心企业
				 */
				// 核心企业融资主体
				WloanSubject wloanSubject2 = new WloanSubject();
				wloanSubject2.setLoanApplyId(creditUserApply.getReplaceUserId());
				List<WloanSubject> wloanSubjectsList2 = wloanSubjectService.findList(wloanSubject2);
				if (null != wloanSubjectsList2 && wloanSubjectsList2.size() > 0) {
					wloanSubject2 = wloanSubjectsList2.get(0);
				}
				// 创建企业客户账号(核心企业)
				String userOrganizeAccountId2; // 核心企业电子签章id
				ElectronicSign electronicSign2 = new ElectronicSign();
				electronicSign2.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				electronicSign2.setUserId(creditUserApply.getReplaceUserId());
				List<ElectronicSign> electronicSignsList2 = electronicSignService.findList(electronicSign2);
				if (electronicSignsList2 != null && electronicSignsList2.size() > 0) {
					// 获取已保存的核心企业电子签章id
					userOrganizeAccountId2 = electronicSignsList2.get(0).getSignId();
				} else {
					// 如果没有保存核心企业电子签章id，即重新生成
					userOrganizeAccountId2 = SignHelper.addOrganizeAccountZtmg(wloanSubject2);
					electronicSign2.setId(IdGen.uuid());
					electronicSign2.setSignId(userOrganizeAccountId2);
					electronicSign2.setCreateDate(new Date());
					electronicSign2.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
					electronicSignDao.insert(electronicSign2);
				}

				// 创建平台印章
				AddSealResult platformUserOrganizeSealData = SignHelper.addOrganizeTemplateSealZTMG(platformUserOrganizeAccountId, platformLoanSubject);
				// 创建供应商印章
				AddSealResult userOrganizeSealData1 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId1, wloanSubject1);
				// 创建核心企业印章
				AddSealResult userOrganizeSealData2 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId2, wloanSubject2);

				/**
				 * 签署的合同路径
				 */
				String srcPdfFile = "";
				// 主键ID.
				CreditUserApply entityApply = new CreditUserApply();
				entityApply.setCreditSupplyId(creditUserApply.getCreditSupplyId()); // 供应商id.
				entityApply.setReplaceUserId(creditUserApply.getReplaceUserId()); // 核心企业id.
				entityApply.setFinancingType(CreditUserApplyService.CREDIT_FINANCING_TYPE_1);// 应收账款转让
				entityApply.setBeginCreateDate(DateUtils.getDateOfString("2018-09-27"));
				List<CreditUserApply> list = creditUserApplyService.findList(entityApply);
				if (list != null && list.size() > 1) {// 不是第一次
					/**
					 * 融资申请书，进行签章
					 */
					logger.info("融资申请书，进行签章...start...");
					// 融资申请书（应收账款质押）.
					srcPdfFile = LoanAgreementPdfUtil.createFinancingApplicationBook(creditUserApply);
					creditUserApply.setBorrPurpose(srcPdfFile);
					creditUserApplyService.save(creditUserApply); // 更新，融资申请书路径
					int lastF = srcPdfFile.lastIndexOf("\\");
					if (lastF == -1) {
						lastF = srcPdfFile.lastIndexOf("//");
					}
					// 最终签署后的PDF文件路径
					String signedFolder = srcPdfFile.substring(0, lastF + 1);
					// 最终签署后PDF文件名称
					String signedFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
					// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
					FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByFile(srcPdfFile, userOrganizeAccountId1, userOrganizeSealData1.getSealData());
					String serviceId1 = userOrganizeSignResult1.getSignServiceId();
					// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
					FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2(userOrganizeSignResult1.getStream(), userOrganizeAccountId2, userOrganizeSealData2.getSealData());
					String serviceId2 = userOrganizeSignResult2.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == userOrganizeSignResult2.getErrCode()) {
						SignHelper.saveSignedByStream(userOrganizeSignResult2.getStream(), signedFolder, signedFileName);
					}
					ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
					electronicSignTranstail.setId(IdGen.uuid());
					electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					electronicSignTranstail.setSignServiceIdSupply(serviceId1);
					electronicSignTranstail.setSignServiceIdCore(serviceId2);
					electronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(electronicSignTranstail);
					logger.info("融资申请书，进行签章...end...");
					/**
					 * 应收账款质押登记协议，进行签章
					 */
					logger.info("应收账款质押登记协议，进行签章...start...");
					srcPdfFile = LoanAgreementPdfUtil.createYingShouZhangKuanDengJiXiYi(creditUserApply);
					creditUserApply.setZdFilePath(srcPdfFile);
					creditUserApplyService.save(creditUserApply); // 更新，应收账款质押登记协议
					int lastZdFilePath = srcPdfFile.lastIndexOf("\\");
					if (lastZdFilePath == -1) {
						lastZdFilePath = srcPdfFile.lastIndexOf("//");
					}
					// 最终签署后的PDF文件路径
					String ZdFolder = srcPdfFile.substring(0, lastF + 1);
					// 最终签署后PDF文件名称
					String ZdFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
					// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
					FileDigestSignResult fileDigestSignResult_1 = SignHelper.userOrganizeSignByFile(srcPdfFile, userOrganizeAccountId1, userOrganizeSealData1.getSealData());
					String signServiceId_1 = fileDigestSignResult_1.getSignServiceId();
					// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
					FileDigestSignResult fileDigestSignResult_2 = SignHelper.userOrganizeSignByStream2(fileDigestSignResult_1.getStream(), platformUserOrganizeAccountId, platformUserOrganizeSealData.getSealData());
					String signServiceId_2 = fileDigestSignResult_2.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == fileDigestSignResult_2.getErrCode()) {
						SignHelper.saveSignedByStream(fileDigestSignResult_2.getStream(), ZdFolder, ZdFileName);
					}
					ElectronicSignTranstail platformElectronicSignTranstail = new ElectronicSignTranstail();
					platformElectronicSignTranstail.setId(IdGen.uuid());
					platformElectronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					platformElectronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					platformElectronicSignTranstail.setSignServiceIdSupply(signServiceId_1);
					platformElectronicSignTranstail.setSignServiceIdCore(signServiceId_2);
					platformElectronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(platformElectronicSignTranstail);
					logger.info("应收账款质押登记协议，进行签章...end...");
					/**
					 * 授权函，进行签章
					 */
					logger.info("授权函，进行签章...start...");
					srcPdfFile = LoanAgreementPdfUtil.createShouQuanHan(creditUserApply);
					creditUserApply.setShCisFilePath(srcPdfFile);
					creditUserApplyService.save(creditUserApply); // 更新，授权函-上海资信
					int lastShFilePath = srcPdfFile.lastIndexOf("\\");
					if (lastShFilePath == -1) {
						lastShFilePath = srcPdfFile.lastIndexOf("//");
					}
					// 最终签署后的PDF文件路径
					String ShFolder = srcPdfFile.substring(0, lastF + 1);
					// 最终签署后PDF文件名称
					String ShFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
					// 甲方（供应商）
					FileDigestSignResult fileDigestSignResult_Sh = SignHelper.userOrganizeSignByFile(srcPdfFile, userOrganizeAccountId1, userOrganizeSealData1.getSealData());
					String signServiceId_Sh = fileDigestSignResult_Sh.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == fileDigestSignResult_Sh.getErrCode()) {
						SignHelper.saveSignedByStream(fileDigestSignResult_Sh.getStream(), ShFolder, ShFileName);
					}
					ElectronicSignTranstail shElectronicSignTranstail = new ElectronicSignTranstail();
					shElectronicSignTranstail.setId(IdGen.uuid());
					shElectronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					shElectronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					shElectronicSignTranstail.setSignServiceIdSupply(signServiceId_Sh);
					shElectronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(shElectronicSignTranstail);
					logger.info("授权函，进行签章...end...");
				} else { // 供应链融资框架协议
					/**
					 * 供应链融资框架协议，进行签章
					 */
					logger.info("供应链融资框架协议，进行签章...start...");
					if (CreditUserInfo.XIYUN_ID.equals(creditUserApply.getReplaceUserId())) { // 熙耘借款.
						srcPdfFile = LoanAgreementPdfUtil.createXiYunFinancingFrameworkAgreement(creditUserApply);
						creditUserApply.setBorrPurpose(srcPdfFile);
					} else {
						srcPdfFile = LoanAgreementPdfUtil.createFinancingFrameworkAgreement(creditUserApply);
						creditUserApply.setBorrPurpose(srcPdfFile);
					}
					creditUserApplyService.save(creditUserApply); // 更新，供应链融资框架协议路径
					int lastF = srcPdfFile.lastIndexOf("\\");
					if (lastF == -1) {
						lastF = srcPdfFile.lastIndexOf("//");
					}
					// 最终签署后的PDF文件路径
					String signedFolder = srcPdfFile.substring(0, lastF + 1);
					// 最终签署后PDF文件名称
					String signedFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
					// 甲方签章，供应商
					FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByStream1First(FileHelper.getBytes(srcPdfFile), userOrganizeAccountId1, userOrganizeSealData1.getSealData());
					String serviceId1 = userOrganizeSignResult1.getSignServiceId();
					// 乙方签章，核心企业
					FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2First(userOrganizeSignResult1.getStream(), userOrganizeAccountId2, userOrganizeSealData2.getSealData());
					String serviceId2 = userOrganizeSignResult2.getSignServiceId();
					// 丙方签章，平台
					FileDigestSignResult userOrganizeSignResult3 = SignHelper.userOrganizeSignByStream3First(userOrganizeSignResult2.getStream(), platformUserOrganizeAccountId, platformUserOrganizeSealData.getSealData());
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == userOrganizeSignResult3.getErrCode()) {
						SignHelper.saveSignedByStream(userOrganizeSignResult3.getStream(), signedFolder, signedFileName);
					}
					ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
					electronicSignTranstail.setId(IdGen.uuid());
					electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					electronicSignTranstail.setSignServiceIdSupply(serviceId1);
					electronicSignTranstail.setSignServiceIdCore(serviceId2);
					electronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(electronicSignTranstail);
					logger.info("供应链融资框架协议，进行签章...end...");
					/**
					 * 应收账款质押登记协议，进行签章
					 */
					logger.info("应收账款质押登记协议，进行签章...start...");
					srcPdfFile = LoanAgreementPdfUtil.createYingShouZhangKuanDengJiXiYi(creditUserApply);
					creditUserApply.setZdFilePath(srcPdfFile);
					creditUserApplyService.save(creditUserApply); // 更新，应收账款质押登记协议
					int lastZdFilePath = srcPdfFile.lastIndexOf("\\");
					if (lastZdFilePath == -1) {
						lastZdFilePath = srcPdfFile.lastIndexOf("//");
					}
					// 最终签署后的PDF文件路径
					String ZdFolder = srcPdfFile.substring(0, lastF + 1);
					// 最终签署后PDF文件名称
					String ZdFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
					// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
					FileDigestSignResult fileDigestSignResult_1 = SignHelper.userOrganizeSignByFile(srcPdfFile, userOrganizeAccountId1, userOrganizeSealData1.getSealData());
					String signServiceId_1 = fileDigestSignResult_1.getSignServiceId();
					// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
					FileDigestSignResult fileDigestSignResult_2 = SignHelper.userOrganizeSignByStream2(fileDigestSignResult_1.getStream(), platformUserOrganizeAccountId, platformUserOrganizeSealData.getSealData());
					String signServiceId_2 = fileDigestSignResult_2.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == fileDigestSignResult_2.getErrCode()) {
						SignHelper.saveSignedByStream(fileDigestSignResult_2.getStream(), ZdFolder, ZdFileName);
					}
					ElectronicSignTranstail platformElectronicSignTranstail = new ElectronicSignTranstail();
					platformElectronicSignTranstail.setId(IdGen.uuid());
					platformElectronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					platformElectronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					platformElectronicSignTranstail.setSignServiceIdSupply(signServiceId_1);
					platformElectronicSignTranstail.setSignServiceIdCore(signServiceId_2);
					platformElectronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(platformElectronicSignTranstail);
					logger.info("应收账款质押登记协议，进行签章...end...");
					/**
					 * 授权函，进行签章
					 */
					logger.info("授权函，进行签章...start...");
					srcPdfFile = LoanAgreementPdfUtil.createShouQuanHan(creditUserApply);
					creditUserApply.setShCisFilePath(srcPdfFile);
					creditUserApplyService.save(creditUserApply); // 更新，授权函-上海资信
					int lastShFilePath = srcPdfFile.lastIndexOf("\\");
					if (lastShFilePath == -1) {
						lastShFilePath = srcPdfFile.lastIndexOf("//");
					}
					// 最终签署后的PDF文件路径
					String ShFolder = srcPdfFile.substring(0, lastF + 1);
					// 最终签署后PDF文件名称
					String ShFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
					// 甲方（供应商）
					FileDigestSignResult fileDigestSignResult_Sh = SignHelper.userOrganizeSignByFile(srcPdfFile, userOrganizeAccountId1, userOrganizeSealData1.getSealData());
					String signServiceId_Sh = fileDigestSignResult_Sh.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == fileDigestSignResult_Sh.getErrCode()) {
						SignHelper.saveSignedByStream(fileDigestSignResult_Sh.getStream(), ShFolder, ShFileName);
					}
					ElectronicSignTranstail shElectronicSignTranstail = new ElectronicSignTranstail();
					shElectronicSignTranstail.setId(IdGen.uuid());
					shElectronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					shElectronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					shElectronicSignTranstail.setSignServiceIdSupply(signServiceId_Sh);
					shElectronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(shElectronicSignTranstail);
					logger.info("授权函，进行签章...end...");
				}
				creditUserApply.setIsAuthorize("TRUE"); // 授权成功
				creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_1); // 审核中
				creditUserApply.setModify("1"); // 1：不可编辑
				creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_6); // 第六步
				creditUserApplyService.save(creditUserApply);

				if (creditUserApply.getBorrPurpose() != null) { // 供应链融资框架协议/融资申请书
					model.addAttribute("srcPdfFile", CreditURLConfig.SRCPDFFILE + creditUserApply.getBorrPurpose().split("data")[1]);
				} else {
					model.addAttribute("srcPdfFile", ""); // 没有生成
				}

				/**
				 * 平台费用分摊比例计算
				 */
				Double creditShareRate = Double.parseDouble(creditUserApply.getShareRate());// 核心企业分摊比例
				Double supplyShareRate = 100 - creditShareRate;// 供应商分摊比例
				if (creditShareRate == 100) {
					// 未设置分摊
				} else {
					// 向供应商发送短信
					CreditUserInfo supplyUser = creditUserInfoService.get(creditUserApply.getCreditSupplyId());// 供应商信息
					CreditUserInfo creditUser = creditUserInfoService.get(creditUserApply.getReplaceUserId());// 核心企业信息
					// 服务费总金额
					Double financingMoney = Double.parseDouble(creditUserApply.getAmount() == null ? "0" : creditUserApply.getAmount());// 融资金额
					Double financingRate = Double.parseDouble(creditUserApply.getLenderRate() == null ? "0" : creditUserApply.getLenderRate());// 融资利率
					Double financingSpan = Double.parseDouble(creditUserApply.getSpan() == null ? "0" : creditUserApply.getSpan());// 融资期限
					String serviceRate = null;// 服务费率
					if (financingMoney != null && financingRate != null && financingSpan != null) {
						CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
						creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
						List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
						if (creditUserApply.getSpan() != null) {
							for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
								if (creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())) {
									serviceRate = creditMiddlemenRate2.getServiceRate();
								}
							}
						}
						Double interestMoney = (financingMoney * financingRate / 36500) * financingSpan;// 融资利息
						if (serviceRate != null) {
							Double serviceMoney = (financingMoney * Double.parseDouble(serviceRate) / 36500) * financingSpan;// 平台服务费
							Double registMoney = 0.0;// 登记费
							if (financingSpan <= 180) {
								registMoney = 30.0;
							} else {
								registMoney = 60.0;
							}

							Double sumFee = interestMoney + serviceMoney + registMoney;
							sumFee = (double) Math.round(sumFee * 100) / 100;
							Double creditFee = (double) Math.round((sumFee * creditShareRate / 100) * 100) / 100;// 核心企业费用
							Double supplyFee = (double) Math.round((sumFee * supplyShareRate / 100) * 100) / 100;// 供应商费用
							creditUserApply.setSumFee(sumFee);
							logger.info("短信通知供应商平台费用分摊比例...start...");
							sendSmsService.directSendSMS(supplyUser.getPhone(), "尊敬的" + supplyUser.getEnterpriseFullName() + "：您的核心企业" + creditUser.getEnterpriseFullName() + "，已设置平台服务费分摊，比例为" + creditShareRate.intValue() + ":" + supplyShareRate.intValue() + "，核心企业" + creditUser.getEnterpriseFullName() + "承担" + creditFee + "元，您承担" + supplyFee + "元，请知晓，谢谢！【系统发送】");
							logger.info("短信通知供应商平台费用分摊比例...end...");

							String toMailAddr = null;// 供应商邮箱
							// 查询供应商邮箱
							WloanSubject wloanSubject = new WloanSubject();
							wloanSubject.setLoanApplyId(creditUserApply.getCreditSupplyId());
							List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
							if (wloanSubjects != null && wloanSubjects.size() > 0) {
								wloanSubject = wloanSubjects.get(0);
								String email = wloanSubject.getEmail();
								if (email != null) {
									toMailAddr = email;
								}
							}
							if (toMailAddr != null) {
								// 邮件主题
								String subject = "平台费用分摊比例";
								// 邮件内容
								StringBuffer email_message = new StringBuffer();
								email_message.append("亲爱的供应商您好：\n\t您好，核心企业【").append(creditUser.getEnterpriseFullName()).append("】，已设置平台服务费分摊，比例为").append(creditShareRate.intValue()).append(":").append(supplyShareRate.intValue()).append("，核心企业【").append(creditUser.getEnterpriseFullName()).append("】承担").append(creditFee).append("元，您承担").append(supplyFee).append("元，请知晓，谢谢！");
								String cc = "liyun@cicmorgan.com"; // 平台技术人员，便于跟踪邮件是否发送
								SendMailUtil.ztmgSendRepayRemindEmailMsg(toMailAddr, cc, subject.toString(), email_message.toString());
							} else {
								log.info("供应商无邮箱！");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("msg", "系统异常");
			return result;
		}

		result.put("state", "0");
		result.put("msg", "成功");
		return result;

	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "loanCreditUserApplyList")
	public String loanCreditUserApplyList(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, Model model) {

		String creditUserId = request.getParameter("creditUserId");// 核心企业id
		String creditSupplyId = request.getParameter("creditSupplyId");// 供应商id
		Page<WloanTermProject> page = null;
		if (creditUserId == null || "".equals(creditUserId)) {// 供应商
			if (creditSupplyId != null) {
				// CreditUserInfo creditUserInfo =
				// creditUserInfoService.get(creditSupplyId);
				WloanSubject wloanSubject = new WloanSubject();
				wloanSubject.setLoanApplyId(creditSupplyId);
				List<WloanSubject> list = wloanSubjectService.findList(wloanSubject);
				if (list.size() > 0) {
					wloanSubject = list.get(0);
					String wloanSubjectId = wloanSubject.getId();
					wloanTermProject.setSubjectId(wloanSubjectId);
					page = wloanTermProjectService.findPage(new Page<WloanTermProject>(request, response, 5), wloanTermProject);
				}

			}

		} else {// 核心企业
			wloanTermProject.setReplaceRepayId(creditUserId);
			page = wloanTermProjectService.findPage(new Page<WloanTermProject>(request, response, 5), wloanTermProject);
		}

		List<WloanTermProject> list = page.getList();
		for (WloanTermProject wloanTermProject2 : list) {
			CreditUserApply creditUserApply = creditUserApplyService.get(wloanTermProject2.getCreditUserApplyId());
			if (creditUserApply != null) {
				wloanTermProject2.setCreditUserApplyName(creditUserApply.getCreditApplyName());
			}
			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
			if (creditUserInfo != null) {
				wloanTermProject2.setReplaceRepayName(creditUserInfo.getEnterpriseFullName());
			}
		}
		// CreditUserApply creditUserApply = new CreditUserApply();
		// creditUserApply.setReplaceUserId(id);
		// Page<CreditUserApply> page = creditUserApplyService.findPage(new
		// Page<CreditUserApply>(request, response), creditUserApply);
		// List<CreditUserApply> list = page.getList();
		// for (CreditUserApply userApply : list) {
		// CreditUserInfo creditLoanUserInfo =
		// creditUserInfoService.get(userApply.getCreditSupplyId()); // 借款人信息.
		// userApply.setLoanUserEnterpriseFullName(creditLoanUserInfo.getEnterpriseFullName());
		// CreditUserInfo creditReplaceUserInfo =
		// creditUserInfoService.get(userApply.getReplaceUserId()); // 代偿户信息.
		// userApply.setReplaceUserEnterpriseFullName(creditReplaceUserInfo.getEnterpriseFullName());
		// //
		// userApply.setRepayDate(DateUtils.getSpecifiedMonthAfter(userApply.getCreateDate(),Integer.parseInt(userApply.getSpan())));
		// }
		model.addAttribute("page", page);
		if (creditUserId == null || "".equals(creditUserId)) {
			if (creditSupplyId != null && !"".equals(creditSupplyId)) {
				model.addAttribute("creditSupplyId", creditSupplyId);
			}
		} else {
			model.addAttribute("creditUserId", creditUserId);
		}
		return "modules/sys/loanProjectList";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "repaymentList")
	public String repaymentList(HttpServletRequest request, HttpServletResponse response, Model model) {

		// 核心企业ID.
		String creditUserId = request.getParameter("creditUserId");// 核心企业
		// 供应商ID.
		String creditSupplyId = request.getParameter("creditSupplyId");// 核心企业
		if (creditUserId == null || "".equals(creditUserId)) {
			if (creditSupplyId != null && !"".equals(creditSupplyId)) {
				model.addAttribute("creditSupplyId", creditSupplyId);
			}
		} else {
			model.addAttribute("creditUserId", creditUserId);
		}
		return "modules/sys/downloadRepayment";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "exportCreditUserApplyList", method = RequestMethod.POST)
	public String exportCreditUserApplyList(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response) {

		String creditUserId = request.getParameter("creditUserId");// 核心企业id
		String creditSupplyId = request.getParameter("creditSupplyId");// 供应商id
		List<WloanTermProject> page = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = null;
		if (wloanTermProject.getBeginRealLoanDate() == null && wloanTermProject.getEndRealLoanDate() != null)
			fileName = "截止" + sdf.format(wloanTermProject.getEndRealLoanDate()) + "放款数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		else if (wloanTermProject.getBeginRealLoanDate() != null && wloanTermProject.getEndRealLoanDate() == null)
			fileName = sdf.format(wloanTermProject.getBeginRealLoanDate()) + "之后放款数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		else if (wloanTermProject.getBeginRealLoanDate() != null && wloanTermProject.getEndRealLoanDate() != null)
			fileName = sdf.format(wloanTermProject.getBeginRealLoanDate()) + "-" + sdf.format(wloanTermProject.getEndRealLoanDate()) + "之后放款数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
		else
			fileName = "放款数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";

		try {
			if (creditUserId == null || "".equals(creditUserId)) {// 供应商
				if (creditSupplyId != null) {
					// CreditUserInfo creditUserInfo =
					// creditUserInfoService.get(creditSupplyId);
					WloanSubject wloanSubject = new WloanSubject();
					wloanSubject.setLoanApplyId(creditSupplyId);
					List<WloanSubject> list = wloanSubjectService.findList(wloanSubject);
					if (list.size() > 0) {
						wloanSubject = list.get(0);
						String wloanSubjectId = wloanSubject.getId();
						wloanTermProject.setSubjectId(wloanSubjectId);
						page = wloanTermProjectService.findList(wloanTermProject);
					}

				}

			} else {// 核心企业
				wloanTermProject.setReplaceRepayId(creditUserId);
				page = wloanTermProjectService.findList(wloanTermProject);
			}

			List<WloanTermProject> list = page;
			for (WloanTermProject wloanTermProject2 : list) {
				CreditUserApply creditUserApply = creditUserApplyService.get(wloanTermProject2.getCreditUserApplyId());
				if (creditUserApply != null) {
					wloanTermProject2.setCreditUserApplyName(creditUserApply.getCreditApplyName());
				}
				wloanTermProject2.setPurpose("应收账款转让");
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
				if (creditUserInfo != null) {
					wloanTermProject2.setReplaceRepayName(creditUserInfo.getEnterpriseFullName());
				}
			}
			// CreditUserApply creditUserApply = new CreditUserApply();
			// creditUserApply.setReplaceUserId(id);
			// Page<CreditUserApply> page = creditUserApplyService.findPage(new
			// Page<CreditUserApply>(request, response), creditUserApply);
			// List<CreditUserApply> list = page.getList();
			// for (CreditUserApply userApply : list) {
			// CreditUserInfo creditLoanUserInfo =
			// creditUserInfoService.get(userApply.getCreditSupplyId()); //
			// 借款人信息.
			// userApply.setLoanUserEnterpriseFullName(creditLoanUserInfo.getEnterpriseFullName());
			// CreditUserInfo creditReplaceUserInfo =
			// creditUserInfoService.get(userApply.getReplaceUserId()); //
			// 代偿户信息.
			// userApply.setReplaceUserEnterpriseFullName(creditReplaceUserInfo.getEnterpriseFullName());
			// //
			// userApply.setRepayDate(DateUtils.getSpecifiedMonthAfter(userApply.getCreateDate(),Integer.parseInt(userApply.getSpan())));
			// }
			new ExportExcel("客户基本信息", WloanTermProject2.class).setDataList(page).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出项目管理失败");
		}
		String returnUrl = "";
		if (creditSupplyId != null && !"".equals(creditSupplyId)) {
			returnUrl = "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/loanCreditUserApplyList?creditSupplyId=" + creditSupplyId;
		} else {
			returnUrl = "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/loanCreditUserApplyList??creditUserId=" + creditUserId;
		}
		return returnUrl;
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "downloadProjectPlan")
	public String downloadProjectPlan(WloanTermProjectPlan wloanTermProjectPlan, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		// 核心企业id.
		String creditUserId = request.getParameter("creditUserId");
		// 供应商id.
		String creditSupplyId = request.getParameter("creditSupplyId");
		try {
			String fileName = "还款计划_" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			if (creditUserId != null && !"".equals(creditUserId)) {// 核心企业
				/**
				 * 核心企业还款计划导出.
				 */
				WloanTermProject wloanTermProject = new WloanTermProject();
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
				wloanTermProject.setReplaceRepayId(creditUserId);

				List<String> stateItem = new ArrayList<String>();
				stateItem.add(WloanTermProjectService.REPAYMENT);
				stateItem.add(WloanTermProjectService.FINISH);
				wloanTermProject.setStateItem(stateItem);

				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
				Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
				order_page.setOrderBy(", a.project_id ASC");
				wloanTermProjectPlan.setPage(order_page);
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
				// 重新封装数据.
				List<WloanTermProjectPlanPoJo> pojoList = new ArrayList<WloanTermProjectPlanPoJo>();
				for (WloanTermProjectPlan projectPlan : proPlans) {
					WloanTermProjectPlanPoJo projectPlanPoJo = new WloanTermProjectPlanPoJo();
					projectPlanPoJo.setWloanSubject(projectPlan.getWloanSubject());
					projectPlanPoJo.setWloanTermProject(projectPlan.getWloanTermProject());
					projectPlanPoJo.setPrincipal(projectPlan.getPrincipal()); // 还款类型.
					projectPlanPoJo.setInterest(projectPlan.getInterest()); // 还款金额.
					projectPlanPoJo.setRepaymentDate(projectPlan.getRepaymentDate()); // 还款日期.
					projectPlanPoJo.setState(projectPlan.getState()); // 还款状态.
					pojoList.add(projectPlanPoJo);
				}
				if (pojoList.size() > 0) {
					new ExportExcel("还款计划", WloanTermProjectPlanPoJo.class).setDataList(pojoList).write(response, fileName).dispose();
					return null;
				} else {
					log.info("无还款计划");
					addMessage(redirectAttributes, "暂无还款计划！");
				}
			} else if (creditSupplyId != null && !"".equals(creditSupplyId)) {// 供应商.
				/**
				 * 供应商企业还款计划导出.
				 */
				WloanTermProject wloanTermProject = new WloanTermProject();

				List<String> stateItem = new ArrayList<String>();
				stateItem.add(WloanTermProjectService.REPAYMENT);
				stateItem.add(WloanTermProjectService.FINISH);
				wloanTermProject.setStateItem(stateItem);
				wloanTermProjectPlan.setWloanTermProject(wloanTermProject);

				WloanSubject wloanSubject = new WloanSubject();
				wloanSubject.setLoanApplyId(creditSupplyId);
				wloanTermProjectPlan.setWloanSubject(wloanSubject);
				Page<WloanTermProjectPlan> order_page = new Page<WloanTermProjectPlan>();
				order_page.setOrderBy(", a.project_id ASC");
				wloanTermProjectPlan.setPage(order_page);
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanService.findList(wloanTermProjectPlan);
				// 重新封装数据.
				List<WloanTermProjectPlanPoJo> pojoList = new ArrayList<WloanTermProjectPlanPoJo>();
				for (WloanTermProjectPlan projectPlan : proPlans) {
					WloanTermProjectPlanPoJo projectPlanPoJo = new WloanTermProjectPlanPoJo();
					projectPlanPoJo.setWloanSubject(projectPlan.getWloanSubject());
					projectPlanPoJo.setWloanTermProject(projectPlan.getWloanTermProject());
					projectPlanPoJo.setPrincipal(projectPlan.getPrincipal()); // 还款类型.
					projectPlanPoJo.setInterest(projectPlan.getInterest()); // 还款金额.
					projectPlanPoJo.setRepaymentDate(projectPlan.getRepaymentDate()); // 还款日期.
					projectPlanPoJo.setState(projectPlan.getState()); // 还款状态.
					pojoList.add(projectPlanPoJo);
				}
				if (pojoList.size() > 0) {
					new ExportExcel("还款计划", WloanTermProjectPlanPoJo.class).setDataList(pojoList).write(response, fileName).dispose();
					return null;
				} else {
					log.info("无还款计划");
					addMessage(redirectAttributes, "暂无还款计划！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出还款计划失败");
		}

		String returnUrl = "";
		if (creditUserId != null && !"".equals(creditUserId)) {
			returnUrl = "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/repaymentList?creditUserId=" + creditUserId;
		} else if (creditSupplyId != null && !"".equals(creditSupplyId)) {
			returnUrl = "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/repaymentList?creditSupplyId=" + creditSupplyId;
		}
		return returnUrl;
	}

	/**
	 * 借款申请详情
	 * 
	 * @param creditUserApply
	 * @param model
	 * @return
	 */
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "form")
	public String form(CreditUserApply creditUserApply, Model model) {

		List<CreditVoucher> voucherList = new ArrayList<CreditVoucher>();
		List<CreditOrder> creditOrderList = new ArrayList<CreditOrder>();
		List<CreditAnnexFile> annexFileList = new ArrayList<CreditAnnexFile>();
		List<CreditAnnexFile> creditAnnexFileList = new ArrayList<CreditAnnexFile>();
		// CreditUserApply userApply = new CreditUserApply();
		CreditOrder creditOrder = new CreditOrder();

		// String userApplyId = creditUserApply.getProjectDataId();
		// if(userApplyId!=null){
		// userApply = creditUserApplyService.findApplyById(userApplyId);
		if (creditUserApply != null) {
			String creditInfoId = creditUserApply.getProjectDataId();
			if (creditInfoId != null) {
				voucherList = creditVoucherService.findListByInfoId(creditInfoId);
				creditOrder.setCreditInfoId(creditInfoId);
				creditOrderList = creditOrderService.findList(creditOrder);
				if (creditOrderList != null && creditOrderList.size() > 0) {
					creditOrder = creditOrderList.get(0);

				}
			}
		}
		annexFileList = creditAnnexFileService.findCreditAnnexFileList(creditUserApply.getProjectDataId());
		if (annexFileList != null && annexFileList.size() > 0) {
			for (CreditAnnexFile creditAnnexFile : annexFileList) {
				String type = creditAnnexFile.getType();
				String typeName = "";
				if (type.equals("1")) {
					typeName = "合同影印件";
				} else if (type.equals("2")) {
					typeName = "订单";
					creditOrder.setUrl(creditAnnexFile.getUrl());
				} else if (type.equals("3")) {
					typeName = "发货单";
				} else if (type.equals("4")) {
					typeName = "验收单";
				} else if (type.equals("5")) {
					typeName = "对账单";
				} else if (type.equals("6")) {
					continue;
				} else if (type.equals("7")) {
					typeName = "核心企业承诺函";
				} else {
					typeName = "其他";
				}
				creditAnnexFile.setRemark(typeName);
				creditAnnexFileList.add(creditAnnexFile);
				model.addAttribute("creditOrder", creditOrder);
			}
		}
		// 融资框架协议/融资申请书
		String borrPurpose = creditUserApply.getBorrPurpose();
		if (borrPurpose != null) {
			if (borrPurpose.indexOf("data") != -1) {
				borrPurpose = borrPurpose.split("data")[1];
				creditUserApply.setBorrPurpose(borrPurpose);
			}
		}
		// 借款人网络借贷风险、禁止性行为及有关事项提示书
		String declarationFilePath = creditUserApply.getDeclarationFilePath();
		if (declarationFilePath != null) {
			if (declarationFilePath.indexOf("data") != -1) {
				declarationFilePath = declarationFilePath.split("data")[1];
				creditUserApply.setDeclarationFilePath(declarationFilePath);
			}
		}
		// 应收账款质押登记协议
		String zdFilePath = creditUserApply.getZdFilePath();
		if (zdFilePath != null) {
			if (zdFilePath.indexOf("data") != -1) {
				zdFilePath = zdFilePath.split("data")[1];
				creditUserApply.setZdFilePath(zdFilePath);
			}
		}
		// 授权函
		String shCisFilePath = creditUserApply.getShCisFilePath();
		if (shCisFilePath != null) {
			if (shCisFilePath.indexOf("data") != -1) {
				shCisFilePath = shCisFilePath.split("data")[1];
				creditUserApply.setShCisFilePath(shCisFilePath);
			}
		}
		CreditPack creditPack = new CreditPack();
		creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
		List<CreditPack> creditPackList = creditPackService.findList(creditPack);
		if (creditPackList != null && creditPackList.size() > 0) {
			creditPack = creditPackList.get(0);
			creditUserApply.setCreditPack(creditPack);
		}
		// 核心企业服务费率
		CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
		creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
		List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
		if (creditUserApply.getSpan() != null) {
			for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
				if (creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())) {
					String serviceRate = creditMiddlemenRate2.getServiceRate();
					model.addAttribute("serviceRate", serviceRate);
				}
			}
		}
		model.addAttribute("creditUserApply", creditUserApply);
		model.addAttribute("voucherList", voucherList);// 发票
		model.addAttribute("annexFileList", creditAnnexFileList);// 附件

		return "modules/user/creditUserApplyForm";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "checkLoanApply")
	public String checkLoanApply(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		if (creditUserApply != null) {
			if (StringUtils.isBlank(creditUserApply.getId())) {
				CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserApply.getCreditSupplyId()); // 借款户/供应商.
				if (creditUserInfo != null) {
					creditUserApply.setCreditApplyName(creditUserInfo.getEnterpriseFullName() + "_借款申请_" + System.currentTimeMillis());
				}
				// 创建日期.
				creditUserApply.setCreateDate(new Date());
				// 更新日期.
				creditUserApply.setUpdateDate(new Date());
				// 备注.
				creditUserApply.setRemarks("借款户【借款申请】");
				// 步骤1.
				creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_1);
				// 状态0草稿.
				creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_0);
				creditUserApplyService.save(creditUserApply);
			}
		}

		model.addAttribute("creditUserApply", creditUserApply);
		return "modules/applyMoney/checkLoanApply";
	}

}