package com.power.platform.sys.web.credit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import cn.tsign.ching.eSign.SignHelper;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
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
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.utils.AiQinPdfContract;
import com.power.platform.utils.LoanAgreementPdfUtil;
import com.power.platform.utils.LoanPdfContractUtil;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

/**
 * 
 * 类: CreditUserApplyController <br>
 * 描述: 借款申请. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年6月20日 上午10:01:42
 */
@Controller
@RequestMapping(value = "${adminPath}/apply/orderApply")
public class OrderApplyController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(OrderApplyController.class);

	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
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
	private CreditPackService creditPackService;
	@Autowired
	private CreditOrderService creditOrderService;
	@Autowired
	private SendSmsService sendSmsService;
	

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
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney1", "" })
	public String applyMoney1(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		String step = request.getParameter("step");
		if(step==null || "".equals(step)){//正常申请
			model.addAttribute("step", "");
		}else{//显示数据
			model.addAttribute("step", step);
		}
		return "modules/orderApply/applyMoney1";
	}
	//保存当前数据
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney1ToSave", "" })
	public String applyMoney1ToSave(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		String step = request.getParameter("step");
		if(step==null || "".equals(step)){//正常申请
			model.addAttribute("step", "");
		}else{//显示数据
			model.addAttribute("step", step);
		}
		creditUserApplyService.save(creditUserApply);
		//保存数据
		return "modules/orderApply/applyMoney1";
	}
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney2", "" })
	public String applyMoney2(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

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
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_1);// 第一步完成
			// 状态.
			creditUserApply.setState(creditUserApplyService.CREDIT_USER_APPLY_STATE_0);// 草稿
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
		model.addAttribute("creditUser", creditUserInfo);
		model.addAttribute("creditSupplierToMiddlemenList", newList);

		return "modules/orderApply/applyMoney2";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney2ToSave", "" })
	public String applyMoney2ToSave(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		String step = request.getParameter("step");
		model.addAttribute("step", step);
		CreditUserInfo supplyUser = creditUserInfoService.get(creditUserApply.getCreditSupplyId());//供应商
		//保存供应商id
		creditUserApply.setCreditApplyName(supplyUser.getEnterpriseFullName());
		creditUserApplyService.save(creditUserApply);
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
		model.addAttribute("creditUser", creditUserInfo);
		model.addAttribute("creditSupplierToMiddlemenList", newList);
		return "modules/orderApply/applyMoney2";
	}
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney3", "" })
	public String applyMoney3(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		//驳回状态申请改为草稿状态
		if(creditUserApply.getState().equals(CreditUserApplyService.CREDIT_USER_APPLY_STATE_3)){
			creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_0);
			creditUserApplyService.save(creditUserApply);
		}
		String step = request.getParameter("step");
		CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserApply.getReplaceUserId());//核心企业
		CreditUserInfo supplyUser = creditUserInfoService.get(creditUserApply.getCreditSupplyId());//供应商
		model.addAttribute("creditUser", creditUserInfo);
		model.addAttribute("supplyUser", supplyUser);
		if(step!=null){//显示数据
			model.addAttribute("step", step);
			String creditInfoId = creditUserApply.getProjectDataId();
			if(creditInfoId!=null){
				CreditPack creditPack = new CreditPack();
				creditPack.setCreditInfoId(creditInfoId);
				List<CreditPack> list = creditPackDao.findList(creditPack);
				if(list.size()>0){
					creditPack = list.get(0);
					model.addAttribute("creditPack", creditPack);
				}
			}
		}else{//正常申请
			model.addAttribute("step", "");
			//保存申请步骤(完成第二步)
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_2);
			creditUserApply.setCreditApplyName(supplyUser.getEnterpriseFullName());
			creditUserApplyService.save(creditUserApply);
		}
		return "modules/orderApply/applyMoney3";
	}
	
//	@RequiresPermissions("apply:creditUserApply:view")
//	@RequestMapping(value = { "applyMoney3ToSave", "" })
//	public String applyMoney3ToSave(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {
//		String step = request.getParameter("step");
//		String creditUserId = creditUserApply.getReplaceUserId();//核心企业id
//		String supplyUserId = creditUserApply.getCreditSupplyId();//供应商id
//		CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);//供应商
//		CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);//核心企业
//		model.addAttribute("supplyUser", supplyUser);
//		model.addAttribute("creditUser", creditUser);
//		
//		if(step!=null){//显示数据
//			model.addAttribute("step", step);
//			String creditInfoId = creditUserApply.getProjectDataId();
//			CreditPack creditPack = new CreditPack();
//			creditPack.setCreditInfoId(creditInfoId);
//			List<CreditPack> list = creditPackDao.findList(creditPack);
//			if(list.size()>0){
//				creditPack = list.get(0);
//	            creditPack.setName(creditUserApply.getCreditPack().getName());
//	            creditPack.setNo(creditUserApply.getCreditPack().getNo());
//	            creditPack.setMoney(creditUserApply.getCreditPack().getMoney());
//	            creditPack.setType(creditUserApply.getCreditPack().getType());
//	            creditPack.setUserdDate(creditUserApply.getCreditPack().getUserdDate());
//	            creditPack.setSignDate(creditUserApply.getCreditPack().getSignDate());
//	            creditPackService.save(creditPack);
//				
//				model.addAttribute("creditPack", creditPack);
//			}
//		}
//		return "modules/orderApply/applyMoney3";
//	}
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney4", "" })
	public String applyMoney4(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {
		String saveInfo = request.getParameter("saveInfo");
		String step = request.getParameter("step");
		String error = request.getParameter("error");
		String creditUserType = request.getParameter("creditUserType");
		
		if(saveInfo!=null){//保存（第三步）
			//第三步保存信息（合同）
			String creditUserId = creditUserApply.getReplaceUserId();//核心企业id
			String supplyUserId = creditUserApply.getCreditSupplyId();//供应商id
			CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);//供应商
			CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);//核心企业
			String creditInfoId = creditUserApply.getProjectDataId();
			if(creditInfoId!=null){//修改内容
	            CreditPack creditPack = new CreditPack();
	            creditPack.setCreditInfoId(creditInfoId);	
	            List<CreditPack> creditPackList = creditPackService.findList(creditPack);
	            if(creditPackList!=null && creditPackList.size()>0){
	            	creditPack = creditPackList.get(0);
	            	creditPack.setName(creditUserApply.getCreditPack().getName());
		            creditPack.setNo(creditUserApply.getCreditPack().getNo());
		            creditPack.setMoney(creditUserApply.getCreditPack().getMoney());
		            creditPack.setType(creditUserApply.getCreditPack().getType());
		            creditPack.setUserdDate(creditUserApply.getCreditPack().getUserdDate());
		            creditPack.setSignDate(creditUserApply.getCreditPack().getSignDate());
		            creditPackService.save(creditPack);
	            }
	            
//	            if(Integer.parseInt(creditUserApply.getFinancingStep())<3){
//	            	//查询最近一次成功的申请
//	            	List<CreditUserApply> applyAgreements = creditUserApplyService.findListForAgreement(supplyUserId);
//	            	if(applyAgreements!=null && applyAgreements.size()>0){
//	            		String infoAgreementId = applyAgreements.get(0).getProjectDataId();
//	            		CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
//	            		annexFileAgreement.setOtherId(infoAgreementId);
//	            		annexFileAgreement.setType("1");
//	            		List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
//	            		if(annexFileAgreements!=null && annexFileAgreements.size()>0){
//	            			annexFileAgreement = annexFileAgreements.get(0);
//	            			CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
//	            			creditAnnexFile.setOtherId(creditInfoId); // 资料信息ID.
//	            			creditAnnexFile.setUrl(annexFileAgreement.getUrl()); // 图片保存路径.
//	            			creditAnnexFile.setType("1"); // 类型
//	            			String remark = "交易合同";
//	            			creditAnnexFile.setRemark(remark); // 备注.
//	            			int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, 1, IdGen.uuid());
//	            			if (tag == 1) {
//	            				model.addAttribute("annexAgreement", creditAnnexFile);
//	            			} else {
//	            			}
//	            		}
//	            	}
//	            	
//	            }
	            
			}else{//新增合同信息
				//生成借款信息
				String id = IdGen.uuid();
	            CreditInfo creditInfo = new CreditInfo();
	            creditInfo.setId(id);
	            creditInfo.setCreditUserId(supplyUser.getId());
	            creditInfo.setName(supplyUser.getEnterpriseFullName()+DateUtils.getDateStr());//资料名称为姓名+时间戳
	            creditInfo.setCreateDate(new Date());
	            creditInfo.setUpdateDate(new Date());
	            int i = creditInfoDao.insert(creditInfo);
	            if(i>0){
	            	log.info("借款用户资料新增成功"+"["+creditInfo.getName()+"]");
	            }
	            
	            if(Integer.parseInt(creditUserApply.getFinancingStep())<3){
	            	//查询最近一次成功的申请
					List<CreditUserApply> applyAgreements = creditUserApplyService.findListForAgreement(supplyUserId);
					if(applyAgreements!=null && applyAgreements.size()>0){
						String infoAgreementId = applyAgreements.get(0).getProjectDataId();
						CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
						annexFileAgreement.setOtherId(infoAgreementId);
						annexFileAgreement.setType("1");
						List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
						if(annexFileAgreements!=null && annexFileAgreements.size()>0){
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
	            
	          //保存借款资料id
	            creditUserApply.setProjectDataId(id);
	            //保存借款资料名称
	            creditUserApply.setCreditApplyName(creditInfo.getName());
	            creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_3);
	            creditUserApplyService.save(creditUserApply);
	            //新增合同信息
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
			return  "redirect:" + Global.getAdminPath() + "/apply/orderApply/applyMoney3?id="+creditUserApply.getId()+"&step=3";
		}else{
			//下一步
			if(step == "" || step == null || "null".equals(step)){//正常申请
				model.addAttribute("step", "4");
				String creditUserId = creditUserApply.getReplaceUserId();//核心企业id
				String supplyUserId = creditUserApply.getCreditSupplyId();//供应商id
				CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);//供应商
				CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);//核心企业
				model.addAttribute("supplyUser", supplyUser);
				model.addAttribute("creditUser", creditUser);
				if(supplyUser!=null){
					if("11".equals(creditUserType)){//核心企业
						String creditInfoId = creditUserApply.getProjectDataId();
						if(creditInfoId!=null && !"".equals(creditInfoId)){
							CreditPack creditPack = new CreditPack();
							creditPack.setCreditInfoId(creditInfoId);
							List<CreditPack> creditPackList = creditPackService.findList(creditPack);
							if(creditPackList!=null && creditPackList.size()>0){
								creditPack = creditPackList.get(0);
								model.addAttribute("packNo", creditPack.getNo());
							}
						}
					}else{
						String creditInfoId = creditUserApply.getProjectDataId();
						if(creditInfoId!=null && !"".equals(creditInfoId)){
							/**
							 * 订单数据.
							 */
							CreditOrder creditOrder = new CreditOrder();
							creditOrder.setCreditInfoId(creditInfoId);
							List<CreditOrder> creditOrderList = creditOrderService.findList(creditOrder);
							if(creditOrderList.size()>0){
								creditOrder = creditOrderList.get(0);
								CreditAnnexFile creditAnnexFile2 = creditAnnexFileService.get(creditOrder.getAnnexId());
								creditOrder.setUrl(creditAnnexFile2.getUrl());
								model.addAttribute("creditOrder", creditOrder);
							}
//							CreditPack creditPack = new CreditPack();
//							creditPack.setCreditInfoId(creditInfoId);
//							List<CreditPack> creditPackList = creditPackService.findList(creditPack);
//							if(creditPackList!=null && creditPackList.size()>0){
//								creditPack = creditPackList.get(0);
//								model.addAttribute("packNo", creditPack.getNo());
//							}
							
							CreditPack creditPack = new CreditPack();
				            creditPack.setCreditInfoId(creditInfoId);	
				            List<CreditPack> creditPackList = creditPackService.findList(creditPack);
				            if(creditPackList!=null && creditPackList.size()>0){
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
			            	//查询当前申请的合同
		            		CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
		            		annexFileAgreement.setOtherId(creditUserApply.getProjectDataId());
		            		annexFileAgreement.setType("1");
		            		List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
		            		if(annexFileAgreements!=null && annexFileAgreements.size()>0){
		            			annexFileAgreement = annexFileAgreements.get(0);
		            			model.addAttribute("annexAgreement", annexFileAgreement);
		            		}
						}else{
							String id = IdGen.uuid();
							CreditInfo creditInfo = new CreditInfo();
							creditInfo.setId(id);
							creditInfo.setCreditUserId(supplyUser.getId());
							creditInfo.setName(supplyUser.getEnterpriseFullName()+DateUtils.getDateStr());//资料名称为姓名+时间戳
							creditInfo.setCreateDate(new Date());
							creditInfo.setUpdateDate(new Date());
							int i = creditInfoDao.insert(creditInfo);
							if(i>0){
								log.info("借款用户资料新增成功"+"["+creditInfo.getName()+"]");
							}
							
							if(Integer.parseInt(creditUserApply.getFinancingStep())<3){
								//查询最近一次成功的申请
								List<CreditUserApply> applyAgreements = creditUserApplyService.findListForAgreement(supplyUserId);
								if(applyAgreements!=null && applyAgreements.size()>0){
									String infoAgreementId = applyAgreements.get(0).getProjectDataId();
									CreditAnnexFile annexFileAgreement = new CreditAnnexFile();
									annexFileAgreement.setOtherId(infoAgreementId);
									annexFileAgreement.setType("1");
									List<CreditAnnexFile> annexFileAgreements = creditAnnexFileService.findList(annexFileAgreement);
									if(annexFileAgreements!=null && annexFileAgreements.size()>0){
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
							
							//保存借款资料id
							creditUserApply.setProjectDataId(id);
							//保存借款资料名称
							creditUserApply.setCreditApplyName(creditInfo.getName());
							//保存申请步骤(完成第三步)
							creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_3);
							creditUserApplyService.save(creditUserApply);
							
							//新增合同信息
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
			}else{//显示数据
				model.addAttribute("step", step);
				Double voucherSum = 0.0;
				String creditInfoId = creditUserApply.getProjectDataId();//借款信息id
				CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
				creditAnnexFile.setOtherId(creditInfoId);
				List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findList(creditAnnexFile);
				model.addAttribute("creditAnnexFileList", creditAnnexFileList);
				CreditVoucher creditVoucher = new CreditVoucher();
				creditVoucher.setCreditInfoId(creditInfoId);
				List<CreditVoucher> creditVoucherList = creditVoucherService.findList(creditVoucher);
				for(CreditVoucher creditVoucher2:creditVoucherList){
					voucherSum +=Double.parseDouble(creditVoucher2.getMoney());
					CreditAnnexFile creditAnnexFile2 = creditAnnexFileService.get(creditVoucher2.getAnnexId());
					creditVoucher2.setUrl(creditAnnexFile2.getUrl());
				}
				model.addAttribute("creditVoucherList", creditVoucherList);
				model.addAttribute("voucherSum", voucherSum.toString());
				CreditOrder creditOrder = new CreditOrder();
				creditOrder.setCreditInfoId(creditInfoId);
				List<CreditOrder> creditOrderList = creditOrderService.findList(creditOrder);
				if(creditOrderList.size()>0){
					creditOrder = creditOrderList.get(0);
					CreditAnnexFile creditAnnexFile2 = creditAnnexFileService.get(creditOrder.getAnnexId());
					creditOrder.setUrl(creditAnnexFile2.getUrl());
					model.addAttribute("creditOrder", creditOrder);
				}
				if(error!="" &&error !=null && !"null".equals(error)){
					model.addAttribute("error", "上传资料不全，请补齐！");
				}
				
			}
			return "modules/orderApply/applyMoney4";
		}
	}
	
	//第四步提交
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney5", "" })
	public String applyMoney5(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String step = request.getParameter("step");
		String saveInfo = request.getParameter("saveInfo");
		String creditUserType = request.getParameter("creditUserType");
		String error = null;
		//第四步点击保存
		if(saveInfo!=null){//保存
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_4);
			creditUserApplyService.save(creditUserApply);
			return  "redirect:" + Global.getAdminPath() + "/apply/orderApply/applyMoney4?id="+creditUserApply.getId()+"&step=4";
		}else{
			//下一步
			if(step=="" || step == null){//正常申请
				model.addAttribute("step", "");
				//判断资料是否齐全
				Boolean type1 = false;
				Boolean type2 = false;
				Boolean type5 = false;
				Boolean type6 = false;
				CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
				creditAnnexFile.setOtherId(creditUserApply.getProjectDataId());
				List<CreditAnnexFile> creditAnnexFiles = creditAnnexFileService.findList(creditAnnexFile);
				if(creditAnnexFiles!=null && creditAnnexFiles.size()>3){//最少四张
					for(CreditAnnexFile creditAnnexFile2:creditAnnexFiles){
						if("1".equals(creditAnnexFile2.getType())){
							type1 = true;
						}
						if("2".equals(creditAnnexFile2.getType())){
							type2 = true;
						}
						if("5".equals(creditAnnexFile2.getType())){
							type5 = true;
						}
						if("6".equals(creditAnnexFile2.getType())){
							type6 = true;
						}
					}
					if(type1&&type2&&type5&&type6){
						//正常执行
						CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserApply.getReplaceUserId());//核心企业
						CreditUserInfo creditSupply = creditUserInfoService.get(creditUserApply.getCreditSupplyId());//供应商
						String enterpriseFullName = creditUserInfo.getEnterpriseFullName();//核心企业
						String creditSupplyName = creditSupply.getEnterpriseFullName();//供应商
						CreditOrder creditOrder = new CreditOrder();
						creditOrder.setCreditInfoId(creditUserApply.getProjectDataId());
						List<CreditOrder> creditOrderList = creditOrderService.findList(creditOrder);
						if(creditOrderList.size()>0){
							creditOrder = creditOrderList.get(0);
						}
						//向供应商发送邮件
						//测试
//						String toMailAddr = SendMailUtil.toMailAddr;//收件人(吉策（测试）)
						//正式
						String toMailAddr = null;//供应商邮箱
						//查询供应商邮箱
						WloanSubject wloanSubject  = new WloanSubject();
						wloanSubject.setLoanApplyId(creditUserApply.getCreditSupplyId());
						List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
						if(wloanSubjects!=null && wloanSubjects.size()>0){
							wloanSubject = wloanSubjects.get(0);
							String email = wloanSubject.getEmail();
							if(email!=null){
								toMailAddr = email;
							}
						}
						if(toMailAddr!=null){
							String subject = "供应商融资申请提醒";
							String message = "尊敬的"+creditSupplyName+"：您的核心企业"+enterpriseFullName+"，已上传关于合同编号/订单号为"+creditOrder.getNo()+"的相关融资资料，请您登陆中投摩根的供应链融资平台系统填写融资申请，谢谢！【系统发送】";
//							List<String> listS = new ArrayList<String>();
							Boolean sendEmailBoolean = SendMailUtil.sendCommonMailBoolean(toMailAddr,subject, message);
							if(sendEmailBoolean){
								log.info("供应商融资申请提醒邮件发送成功");
							}else{
								log.info("供应商融资申请提醒邮件发送失败");
							}
						}else{
							log.info("供应商无邮箱！");
						}
						
						//向供应商发送短信
						logger.info("【向供应商发送短信】------------------------------------------------");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						sendSmsService.directSendSMS(creditSupply.getPhone(), "尊敬的"+creditSupplyName+"：您的核心企业"+enterpriseFullName+"，已上传关于合同编号/订单号为"+creditOrder.getNo()+"的相关融资资料，请您登陆中投摩根的供应链融资平台系统填写融资申请，谢谢！【系统发送】");
						logger.info("【向供应商发送短信成功】------------------------------------------------");
					}
					//保存申请提交状态
					creditUserApply.setFileConfirm("1");
				}else{
					error = "yes";
				}
				creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_4);
				creditUserApplyService.save(creditUserApply);
				return  "redirect:" + Global.getAdminPath() + "/apply/orderApply/applyMoney4?id="+creditUserApply.getId()+"&step=4&error="+error;
			}else{//显示数据
				model.addAttribute("step", step);
				model.addAttribute("creditUserType", creditUserType);
				//判断供应商还是核心企业
				
				String creditUserId = creditUserApply.getReplaceUserId();//核心企业id
				String supplyUserId = creditUserApply.getCreditSupplyId();//供应商id
				String creditInfoId = creditUserApply.getProjectDataId();//借款信息id
				//发票总额
				String voucherSum = null;
				Double voucherSumD = 0.0;
				CreditVoucher creditVoucher = new CreditVoucher();
				creditVoucher.setCreditInfoId(creditInfoId);
				List<CreditVoucher> creditVouchers = creditVoucherService.findList(creditVoucher);
				if(creditVouchers.size()>0){
					for(CreditVoucher creditVoucher2:creditVouchers){
						voucherSumD+=Double.parseDouble(creditVoucher2.getMoney());
					}
				}
				voucherSum = voucherSumD.toString();
				
				//订单总金额
				String orderSum = null;
				Double orderSumD = 0.0;
				CreditOrder creditOrder = new CreditOrder();
				creditOrder.setCreditInfoId(creditInfoId);
				List<CreditOrder> creditOrders = creditOrderService.findList(creditOrder);
				if(creditOrders.size()>0){
					for(CreditOrder creditOrder2:creditOrders){
						orderSumD+=Double.parseDouble(creditOrder2.getMoney());
					}
				}
				orderSum = orderSumD.toString();
				//供应商在贷总金额
				Double sumMoney = 0.0;
				CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);//核心企业
				CreditUserInfo supplyUser = creditUserInfoService.get(supplyUserId);//供应商
				creditInfoId = creditInfoId.trim();
//					CreditInfo creditInfo = creditInfoDao.get(creditInfoId);//借款资料
				CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
				creditMiddlemenRate.setCreditUserId(creditUserId);
				List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
				
				if(creditUserApply.getSpan()!=null){
					for(CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList){
						if(creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())){
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							model.addAttribute("serviceRate", serviceRate);
						}
					}
				}
//								wloanTermProject.setCreditUserApplyId(creditUserApplyId);
				WloanSubject wloanSubject = new WloanSubject();
				wloanSubject.setLoanApplyId(supplyUserId);
				List<WloanSubject> wloanSubjectList = wloanSubjectService.findList(wloanSubject);
				if(wloanSubjectList.size()>0){
					wloanSubject = wloanSubjectList.get(0);
				}
				WloanTermProject wloanTermProject =  new WloanTermProject();
				wloanTermProject.setSubjectId(wloanSubject.getId());
				List<WloanTermProject> wloanTermProjectList = wloanTermProjectService.findList(wloanTermProject);
				if(wloanTermProjectList.size()>0){
					for(WloanTermProject wloanTermProject2:wloanTermProjectList){
						if("4".equals(wloanTermProject2.getState()) || "5".equals(wloanTermProject2.getState()) || "6".equals(wloanTermProject2.getState())){
							sumMoney+=wloanTermProject2.getAmount();
						}
					}
				}
				model.addAttribute("creditUser", creditUser);
				model.addAttribute("supplyUser", supplyUser);
				model.addAttribute("creditMiddlemenRateList", creditMiddlemenRateList);
				model.addAttribute("orderSum", orderSum);
				model.addAttribute("sumMoney", sumMoney);
				return  "modules/orderApply/applyMoney5";
			}
		}
	}
	
  
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney6", "" })
	public String applyMoney6(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String confirm = request.getParameter("confirm");
		String saveInfo = request.getParameter("saveInfo");
		String step = request.getParameter("step");
		//核心企业保存
		if(saveInfo!=null){
			String[] rates = creditUserApply.getSpan().split(",");
			String span = rates[0];
			String rate = rates[1];
			creditUserApply.setSpan(span);
			creditUserApply.setLenderRate(rate);
			Double creditShareRate = Double.parseDouble(creditUserApply.getShareRate());//核心企业分摊比例
			if(creditShareRate!=100){
				creditUserApplyService.save(creditUserApply);
			}
			return  "redirect:" + Global.getAdminPath() + "/apply/orderApply/applyMoney5?id="+creditUserApply.getId()+"&creditUserType=11&step=5";
		}else if(confirm!=null){
			//核心企业确认申请
			creditUserApply.setModify("1");
			String[] rates = creditUserApply.getSpan().split(",");
			String span = rates[0];
			String rate = rates[1];
			creditUserApply.setSpan(span);
			creditUserApply.setLenderRate(rate);
			creditUserApplyService.save(creditUserApply);
		
			Double creditShareRate = Double.parseDouble(creditUserApply.getShareRate());//核心企业分摊比例
			Double supplyShareRate = 100-creditShareRate;//供应商分摊比例
			
			if(creditShareRate==100){
				//未设置分摊
			}else{
				//向供应商发送短信
				CreditUserInfo supplyUser = creditUserInfoService.get(creditUserApply.getCreditSupplyId());//供应商信息
				CreditUserInfo creditUser = creditUserInfoService.get(creditUserApply.getReplaceUserId());//核心企业信息
				//服务费总金额
				Double financingMoney =  Double.parseDouble(creditUserApply.getAmount()==null?"0":creditUserApply.getAmount());//融资金额
				Double financingRate = Double.parseDouble(creditUserApply.getLenderRate()==null?"0":creditUserApply.getLenderRate());//融资利率
				Double financingSpan = Double.parseDouble(creditUserApply.getSpan()==null?"0":creditUserApply.getSpan());//融资期限
				String serviceRate = null;//服务费率
				if(financingMoney!=null && financingRate!=null &&financingSpan!=null){
					CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
					creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
					List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
					if(creditUserApply.getSpan()!=null){
						for(CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList){
							if(creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())){
								serviceRate = creditMiddlemenRate2.getServiceRate();
							}
						}
					}
					Double interestMoney = (financingMoney*financingRate/36500)*financingSpan;//融资利息
					if(serviceRate!=null){
						Double serviceMoney = (financingMoney*Double.parseDouble(serviceRate)/36500)*financingSpan;//平台服务费
						Double registMoney=0.0;//登记费
						if(financingSpan<=180){
							registMoney = 30.0;
						}else{
							registMoney = 60.0;
						}
						
						Double sumFee = interestMoney+serviceMoney+registMoney;
						sumFee = (double)Math.round(sumFee*100)/100;
						Double creditFee = (double)Math.round((sumFee*creditShareRate/100)*100)/100;//核心企业费用
						Double supplyFee = (double)Math.round((sumFee*supplyShareRate/100)*100)/100;//供应商费用
						creditUserApply.setSumFee(sumFee);
						logger.info("【向供应商发送短信】------------------------------------------------");
						sendSmsService.directSendSMS(supplyUser.getPhone(), "尊敬的"+supplyUser.getEnterpriseFullName()+"：您的核心企业"+creditUser.getEnterpriseFullName()+"，已设置平台服务费分摊，比例为"+creditShareRate.intValue()+":"+supplyShareRate.intValue()+"，核心企业"+creditUser.getEnterpriseFullName()+"承担"+creditFee+"元，您承担"+supplyFee+"元，请知晓，谢谢！【系统发送】");
//						sendSmsService.directSendSMS(supplyUser.getPhone(), "尊敬的"+supplyUser.getEnterpriseFullName()+"：您本次借款需要承担的平台费用为"+supplyFee+"元，请知晓，谢谢！【系统发送】");
						logger.info("【向供应商发送短信成功】------------------------------------------------");
						//向供应商发送邮件
//						String toMailAddr = SendMailUtil.toMailAddr;//收件人(吉策（测试）)
//						String subject = "平台服务费用通知";
//						String message = "您本次借款需要承担的平台费用为"+supplyFee+"元，请知晓，谢谢！【系统发送】";
//						List<String> listS = new ArrayList<String>();
//						Boolean sendEmailBoolean = SendMailUtil.sendCommonMailBoolean(toMailAddr,subject, message);
//						if(sendEmailBoolean){
//							log.info("供应商融资申请提醒邮件发送成功");
//						}else{
//							log.info("供应商融资申请提醒邮件发送失败");
//						}
						String toMailAddr = null;//供应商邮箱
						//查询供应商邮箱
						WloanSubject wloanSubject  = new WloanSubject();
						wloanSubject.setLoanApplyId(creditUserApply.getCreditSupplyId());
						List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
						if(wloanSubjects!=null && wloanSubjects.size()>0){
							wloanSubject = wloanSubjects.get(0);
							String email = wloanSubject.getEmail();
							if(email!=null){
								toMailAddr = email;
							}
						}
						if(toMailAddr!=null){
							String subject = "平台服务费用通知";
							String message = "尊敬的"+supplyUser.getEnterpriseFullName()+"：您的核心企业"+creditUser.getEnterpriseFullName()+"，已设置平台服务费分摊，比例为"+creditShareRate.intValue()+":"+supplyShareRate.intValue()+"，核心企业"+creditUser.getEnterpriseFullName()+"承担"+creditFee+"元，您承担"+supplyFee+"元，请知晓，谢谢！";
//							List<String> listS = new ArrayList<String>();
							Boolean sendEmailBoolean = SendMailUtil.sendCommonMailBoolean(toMailAddr,subject, message);
							if(sendEmailBoolean){
								log.info("供应商融资申请提醒邮件发送成功");
							}else{
								log.info("供应商融资申请提醒邮件发送失败");
							}
						}else{
							log.info("供应商无邮箱！");
						}
					}
				}	
			}
			
			return  "redirect:" + Global.getAdminPath() + "/apply/orderApply/applyMoney6?id="+creditUserApply.getId()+"&step=6";
		}else if(step!=null){
			//显示数据
			model.addAttribute("step", step);
			String creditInfoId = creditUserApply.getProjectDataId();//借款信息id
			CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
			creditAnnexFile.setOtherId(creditInfoId);
			List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findList(creditAnnexFile);
//				model.addAttribute("creditAnnexFileList", creditAnnexFileList);
			for(CreditAnnexFile creditAnnexFile1:creditAnnexFileList){
				if("7".equals(creditAnnexFile1.getType())){
					model.addAttribute("creditAnnexFile", creditAnnexFile1);
				}
			}
			CreditUserInfo supplyUser = creditUserInfoService.get(creditUserApply.getCreditSupplyId()); //供应商.
			model.addAttribute("supplyUser", supplyUser);
			String serviceRate = ""; //服务费率.
			CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
			creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
			List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
			if(creditUserApply.getSpan()!=null){
				for(CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList){
					if(creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())){
						serviceRate = creditMiddlemenRate2.getServiceRate();
					}
				}
			}
			model.addAttribute("serviceRate", serviceRate);
			return "modules/orderApply/applyMoney6";
		}else{
			//供应商提交申请
			String[] rates = creditUserApply.getSpan().split(",");
			String span = rates[0];
			String rate = rates[1];
			creditUserApply.setSpan(span);
			creditUserApply.setLenderRate(rate);
//			creditUserApply.setModify("1");//提交申请后,
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_5);
			creditUserApplyService.save(creditUserApply);
			
			//向核心企业发送通知邮件
			
			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserApply.getReplaceUserId());//核心企业
			CreditUserInfo creditSupply = creditUserInfoService.get(creditUserApply.getCreditSupplyId());//供应商
			String enterpriseFullName = creditUserInfo.getEnterpriseFullName();//核心企业
			String creditSupplyName = creditSupply.getEnterpriseFullName();//供应商
			CreditOrder creditOrder = new CreditOrder();
			creditOrder.setCreditInfoId(creditUserApply.getProjectDataId());
			List<CreditOrder> creditOrderList = creditOrderService.findList(creditOrder);
			if(creditOrderList.size()>0){
				creditOrder = creditOrderList.get(0);
//				CreditAnnexFile creditAnnexFile2 = creditAnnexFileService.get(creditOrder.getAnnexId());
//				creditOrder.setUrl(creditAnnexFile2.getUrl());
			}
			
//			String toMailAddr = SendMailUtil.toMailAddr;//收件人(吉策（测试）)
			String toMailAddr = null;//核心企业邮箱
			//查询核心企业邮箱
			WloanSubject wloanSubject  = new WloanSubject();
			wloanSubject.setLoanApplyId(creditUserApply.getReplaceUserId());
			List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
			if(wloanSubjects!=null && wloanSubjects.size()>0){
				wloanSubject = wloanSubjects.get(0);
				String email = wloanSubject.getEmail();
				if(email!=null){
					toMailAddr = email;
				}
			}
			if(toMailAddr!=null){
				String subject = "供应商融资申请提醒";
				String message = "尊敬的"+enterpriseFullName+"：您的供应商企业"+creditSupplyName+"，已填写关于合同编号/订单号为"+creditOrder.getNo()+"的融资申请，请您登陆中投摩根的供应链融资平台系统确认申请并生成担保函，谢谢！【系统发送】";
//				List<String> listS = new ArrayList<String>();
				Boolean sendEmailBoolean = SendMailUtil.sendCommonMailBoolean(toMailAddr,subject, message);
				if(sendEmailBoolean){
					log.info("供应商融资申请提醒邮件发送成功");
				}else{
					log.info("供应商融资申请提醒邮件发送失败");
				}
			}else{
				log.info("供应商无邮箱！");
			}
			//向核心企业发送短信
			logger.info("【向供应商发送短信】------------------------------------------------");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sendSmsService.directSendSMS(creditUserInfo.getPhone(), "尊敬的"+enterpriseFullName+"：您的供应商企业"+creditSupplyName+"，已提交关于合同编号/订单号为"+creditOrder.getNo()+"的融资申请，请您登陆中投摩根的供应链融资平台系统确认申请并生成担保函，谢谢！【系统发送】");
			logger.info("【向供应商发送短信成功】------------------------------------------------");
			
			return  "redirect:" + Global.getAdminPath() + "/apply/orderApply/applyMoney5?id="+creditUserApply.getId()+"&step=5&creditUserType=02";
		}
	}
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = { "applyMoney7", "" })
	public String applyMoney7(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
		String srcPdfFile = null;
		String pdfStr = null;
		// 主键ID.
		CreditUserApply entityApply = new CreditUserApply();
		entityApply.setCreditSupplyId(creditUserApply.getCreditSupplyId());
		entityApply.setFinancingType(creditUserApplyService.CREDIT_FINANCING_TYPE_2);//订单融资
		entityApply.setBeginCreateDate(DateUtils.getDateOfString("2018-09-27"));
		List<CreditUserApply> list = creditUserApplyService.findList(entityApply);
		if(list!=null && list.size()>1){//不是第一次
			pdfStr = AiQinPdfContract.createOrderApplicationBookPdf(creditUserApply);
			//生成合同
//			pdfStr = CreateSupplyChainPdfContract.CreateApplicationBookPdf(creditUserApply);
			
			creditUserApply.setBorrPurpose(pdfStr);
			
			creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STEP_6);
			
			CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
			creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
			List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
			String servicesRate = ""; // 服务费率.
			if(creditUserApply.getSpan()!=null){
				for(CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList){
					if(creditMiddlemenRate2.getSpan().equals(creditUserApply.getSpan())){
						String serviceRate = creditMiddlemenRate2.getServiceRate();
						servicesRate = serviceRate;
					}
				}
			}
			// 风险禁止性行为提示书.
			String promptBookPdfPath = LoanPdfContractUtil.createPromptBookPdf(creditUserApply, servicesRate);
			creditUserApply.setDeclarationFilePath(promptBookPdfPath);
			
			creditUserApplyService.save(creditUserApply);
			
			
			//生成电子签章
			srcPdfFile = pdfStr;
			int lastF = srcPdfFile.lastIndexOf("\\");
			if(lastF == -1){
				lastF = srcPdfFile.lastIndexOf("//");
			}
			// 最终签署后的PDF文件路径
			String signedFolder = srcPdfFile.substring(0, lastF+1);
			// 最终签署后PDF文件名称
			String signedFileName = srcPdfFile.substring(lastF+1, srcPdfFile.length());
			System.out.println("----<场景演示：使用标准的模板印章签署，签署人之间用文件二进制流传递>----");
			// 初始化项目，做全局使用，只初始化一次即可
			SignHelper.initProject();
			log.info("电子签章初始化成功！");
			
//			// 创建企业客户账号(供应商)
//			String userOrganizeAccountId1;
//			ElectronicSign electronicSign1 = new ElectronicSign();
//			electronicSign1.setUserId(creditUserApply.getCreditSupplyId());
//			List<ElectronicSign> electronicSignsList1 = electronicSignService.findList(electronicSign1);
//			
//			WloanSubject wloanSubject1 = new WloanSubject();
//			wloanSubject1.setLoanApplyId(creditUserApply.getCreditSupplyId());
//			List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubject1);
//			wloanSubject1 = wloanSubjectsList1.get(0);
//			
//			if(electronicSignsList1!=null && electronicSignsList1.size()>0){
//				userOrganizeAccountId1 = electronicSignsList1.get(0).getSignId();
//			}else{
//				
//				userOrganizeAccountId1 = SignHelper.addOrganizeAccountZtmg(wloanSubject1);
//				electronicSign1.setId(IdGen.uuid());
//				electronicSign1.setSignId(userOrganizeAccountId1);
//				electronicSign1.setCreateDate(new Date());
//				electronicSignDao.insert(electronicSign1);
//			}
//			// 创建企业客户账号(核心企业)
//			String userOrganizeAccountId2;
//			ElectronicSign electronicSign2 = new ElectronicSign();
//			electronicSign2.setUserId(creditUserApply.getReplaceUserId());
//			List<ElectronicSign> electronicSignsList2 = electronicSignService.findList(electronicSign2);
//			WloanSubject wloanSubject2 = new WloanSubject();
//			wloanSubject2.setLoanApplyId(creditUserApply.getReplaceUserId());
//			List<WloanSubject> wloanSubjectsList2 = wloanSubjectService.findList(wloanSubject2);
//			wloanSubject2 = wloanSubjectsList2.get(0);
//			if(electronicSignsList2!=null && electronicSignsList2.size()>0){
//				userOrganizeAccountId2 = electronicSignsList2.get(0).getSignId();
//			}else{
//				userOrganizeAccountId2 = SignHelper.addOrganizeAccountZtmg(wloanSubject2);
//				electronicSign2.setId(IdGen.uuid());
//				electronicSign2.setSignId(userOrganizeAccountId2);
//				electronicSign2.setCreateDate(new Date());
//				electronicSignDao.insert(electronicSign2);
//			}
//			
//			// 创建企业印章1
//			AddSealResult userOrganizeSealData1 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId1,wloanSubject1);
//			
//			// 创建企业印章2
//			AddSealResult userOrganizeSealData2 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId2,wloanSubject2);
//			
//			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
//			FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByFile(
//					srcPdfFile, userOrganizeAccountId1,userOrganizeSealData1.getSealData());
//			String serviceId1 = userOrganizeSignResult1.getSignServiceId();
//			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
//			FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2(
//					userOrganizeSignResult1.getStream(), userOrganizeAccountId2,userOrganizeSealData2.getSealData());
//			String serviceId2 = userOrganizeSignResult2.getSignServiceId();
//			// 所有签署完成,将最终签署后的文件流保存到本地
//			if (0 == userOrganizeSignResult2.getErrCode()) {
//				SignHelper.saveSignedByStream(userOrganizeSignResult2.getStream(), signedFolder, signedFileName);
//			}
//			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
//			electronicSignTranstail.setId(IdGen.uuid());
//			electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
//			electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
//			electronicSignTranstail.setSignServiceIdSupply(serviceId1);
//			electronicSignTranstail.setSignServiceIdCore(serviceId2);
//			electronicSignTranstail.setCreateDate(new Date());
//			electronicSignTranstailDao.insert(electronicSignTranstail);
			
			
			// 创建企业客户账号(供应商)
			String userOrganizeAccountId1;
			ElectronicSign electronicSign1 = new ElectronicSign();
			electronicSign1.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
			electronicSign1.setUserId(creditUserApply.getCreditSupplyId());
			List<ElectronicSign> electronicSignsList1 = electronicSignService.findList(electronicSign1);
			
			WloanSubject wloanSubject1 = new WloanSubject();
			wloanSubject1.setLoanApplyId(creditUserApply.getCreditSupplyId());
			List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubject1);
			wloanSubject1 = wloanSubjectsList1.get(0);
			
			if(electronicSignsList1!=null && electronicSignsList1.size()>0){
				userOrganizeAccountId1 = electronicSignsList1.get(0).getSignId();
			}else{
				
				userOrganizeAccountId1 = SignHelper.addOrganizeAccountZtmg(wloanSubject1);
				electronicSign1.setId(IdGen.uuid());
				electronicSign1.setSignId(userOrganizeAccountId1);
				electronicSign1.setCreateDate(new Date());
				electronicSign1.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				electronicSignDao.insert(electronicSign1);
			}
			// 创建企业客户账号(核心企业)
			String userOrganizeAccountId2;
			ElectronicSign electronicSign2 = new ElectronicSign();
			electronicSign2.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
			electronicSign2.setUserId(creditUserApply.getReplaceUserId());
			List<ElectronicSign> electronicSignsList2 = electronicSignService.findList(electronicSign2);
			WloanSubject wloanSubject2 = new WloanSubject();
			wloanSubject2.setLoanApplyId(creditUserApply.getReplaceUserId());
			List<WloanSubject> wloanSubjectsList2 = wloanSubjectService.findList(wloanSubject2);
			wloanSubject2 = wloanSubjectsList2.get(0);
			if(electronicSignsList2!=null && electronicSignsList2.size()>0){
				userOrganizeAccountId2 = electronicSignsList2.get(0).getSignId();
			}else{
				userOrganizeAccountId2 = SignHelper.addOrganizeAccountZtmg(wloanSubject2);
				electronicSign2.setId(IdGen.uuid());
				electronicSign2.setSignId(userOrganizeAccountId2);
				electronicSign2.setCreateDate(new Date());
				electronicSign2.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				electronicSignDao.insert(electronicSign2);
			}
			
			// 创建企业印章1（供应商）
			AddSealResult userOrganizeSealData1 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId1,wloanSubject1);
			
			// 创建企业印章2（核心企业）
			AddSealResult userOrganizeSealData2 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId2,wloanSubject2);
			
			
			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
			FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByFile(
					srcPdfFile, userOrganizeAccountId1,userOrganizeSealData1.getSealData());
			String serviceId1 = userOrganizeSignResult1.getSignServiceId();
			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
			FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2(
					userOrganizeSignResult1.getStream(), userOrganizeAccountId2,userOrganizeSealData2.getSealData());
			String serviceId2 = userOrganizeSignResult2.getSignServiceId();
			// 所有签署完成,将最终签署后的文件流保存到本地
			if (0 == userOrganizeSignResult2.getErrCode()) {
				SignHelper.saveSignedByStream(userOrganizeSignResult2.getStream(), signedFolder, signedFileName);
			}
			
			// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档
//			FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammAQ(srcPdfFile);
//			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档(供应商)
//			FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByStream1FirstAQ(
//					platformSignResult.getStream(), userOrganizeAccountId1,userOrganizeSealData1.getSealData());
//			String serviceId1 = userOrganizeSignResult1.getSignServiceId();
//			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档（核心企业）
//			FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2FirstAQ(
//					userOrganizeSignResult1.getStream(), userOrganizeAccountId2,userOrganizeSealData2.getSealData());
//			String serviceId2 = userOrganizeSignResult2.getSignServiceId();
//			// 所有签署完成,将最终签署后的文件流保存到本地
//			if (0 == userOrganizeSignResult2.getErrCode()) {
//				SignHelper.saveSignedByStream(userOrganizeSignResult2.getStream(), signedFolder, signedFileName);
//			}
			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
			electronicSignTranstail.setId(IdGen.uuid());
			electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
			electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
			electronicSignTranstail.setSignServiceIdSupply(serviceId1);
			electronicSignTranstail.setSignServiceIdCore(serviceId2);
			electronicSignTranstail.setCreateDate(new Date());
			electronicSignTranstailDao.insert(electronicSignTranstail);
			creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_1);
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_7);
			creditUserApplyService.save(creditUserApply);
//		model.addAttribute("srcPdfFile",srcPdfFile);//本地 
			model.addAttribute("srcPdfFile", CreditURLConfig.SRCPDFFILE+srcPdfFile.split("data")[1]);
			
		}else{//第一次申请
//			pdfStr = AiQinPdfContract.createFinancingFrameworkPdf(creditUserApply);
			pdfStr = LoanAgreementPdfUtil.createFinancingFrameworkAgreement(creditUserApply);
			//生成合同
//			pdfStr = CreateSupplyChainPdfContract.CreateApplicationBookPdf(creditUserApply);
			
			creditUserApply.setBorrPurpose(pdfStr);
			
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_6);
			
			creditUserApplyService.save(creditUserApply);
			
			
			//生成电子签章
			srcPdfFile = pdfStr;
			int lastF = srcPdfFile.lastIndexOf("\\");
			if(lastF == -1){
				lastF = srcPdfFile.lastIndexOf("//");
			}
			// 最终签署后的PDF文件路径
			String signedFolder = srcPdfFile.substring(0, lastF+1);
			// 最终签署后PDF文件名称
			String signedFileName = srcPdfFile.substring(lastF+1, srcPdfFile.length());
			System.out.println("----<场景演示：使用标准的模板印章签署，签署人之间用文件二进制流传递>----");
			// 初始化项目，做全局使用，只初始化一次即可
			SignHelper.initProject();
			log.info("电子签章初始化成功！");
			// 创建企业客户账号(供应商)
			String userOrganizeAccountId1;
			ElectronicSign electronicSign1 = new ElectronicSign();
			electronicSign1.setUserId(creditUserApply.getCreditSupplyId());
			electronicSign1.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
			List<ElectronicSign> electronicSignsList1 = electronicSignService.findList(electronicSign1);
			
			WloanSubject wloanSubject1 = new WloanSubject();
			wloanSubject1.setLoanApplyId(creditUserApply.getCreditSupplyId());
			List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubject1);
			wloanSubject1 = wloanSubjectsList1.get(0);
			
			if(electronicSignsList1!=null && electronicSignsList1.size()>0){
				userOrganizeAccountId1 = electronicSignsList1.get(0).getSignId();
			}else{
				
				userOrganizeAccountId1 = SignHelper.addOrganizeAccountZtmg(wloanSubject1);
				electronicSign1.setId(IdGen.uuid());
				electronicSign1.setSignId(userOrganizeAccountId1);
				electronicSign1.setCreateDate(new Date());
				electronicSign1.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				electronicSignDao.insert(electronicSign1);
			}
			// 创建企业客户账号(核心企业)
			String userOrganizeAccountId2;
			ElectronicSign electronicSign2 = new ElectronicSign();
			electronicSign2.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
			electronicSign2.setUserId(creditUserApply.getReplaceUserId());
			List<ElectronicSign> electronicSignsList2 = electronicSignService.findList(electronicSign2);
			WloanSubject wloanSubject2 = new WloanSubject();
			wloanSubject2.setLoanApplyId(creditUserApply.getReplaceUserId());
			List<WloanSubject> wloanSubjectsList2 = wloanSubjectService.findList(wloanSubject2);
			wloanSubject2 = wloanSubjectsList2.get(0);
			if(electronicSignsList2!=null && electronicSignsList2.size()>0){
				userOrganizeAccountId2 = electronicSignsList2.get(0).getSignId();
			}else{
				userOrganizeAccountId2 = SignHelper.addOrganizeAccountZtmg(wloanSubject2);
				electronicSign2.setId(IdGen.uuid());
				electronicSign2.setSignId(userOrganizeAccountId2);
				electronicSign2.setCreateDate(new Date());
				electronicSign2.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				electronicSignDao.insert(electronicSign2);
			}
			
			// 创建企业印章1（供应商）
			AddSealResult userOrganizeSealData1 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId1,wloanSubject1);
			
			// 创建企业印章2（核心企业）
			AddSealResult userOrganizeSealData2 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId2,wloanSubject2);
			
			
			// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档
			FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammAQ(srcPdfFile);
			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档(供应商)
			FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByStream1FirstAQ(
					platformSignResult.getStream(), userOrganizeAccountId1,userOrganizeSealData1.getSealData());
			String serviceId1 = userOrganizeSignResult1.getSignServiceId();
			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档（核心企业）
			FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2FirstAQ(
					userOrganizeSignResult1.getStream(), userOrganizeAccountId2,userOrganizeSealData2.getSealData());
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
			creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_1);
			creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_7);
			creditUserApplyService.save(creditUserApply);
//		model.addAttribute("srcPdfFile",srcPdfFile);//本地 
			model.addAttribute("srcPdfFile", CreditURLConfig.SRCPDFFILE+srcPdfFile.split("data")[1]);
			
		}
		
			
		return "modules/orderApply/applyMoney7";
	}
	

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "loanCreditUserApplyList")
	public String loanCreditUserApplyList(WloanTermProject wloanTermProject, HttpServletRequest request, HttpServletResponse response, Model model) {
		String creditUserId = request.getParameter("creditUserId");//核心企业id
		wloanTermProject.setReplaceRepayId(creditUserId);
		Page<WloanTermProject> page = wloanTermProjectService.findPage(new Page<WloanTermProject>(request, response,5), wloanTermProject);
		List<WloanTermProject> list = page.getList();
		for (WloanTermProject wloanTermProject2 : list) {
			CreditUserApply creditUserApply = creditUserApplyService.get(wloanTermProject2.getCreditUserApplyId());
			if(creditUserApply!=null){
				wloanTermProject2.setCreditUserApplyName(creditUserApply.getCreditApplyName());
			}
			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
			if(creditUserInfo!=null){
				wloanTermProject2.setReplaceRepayName(creditUserInfo.getEnterpriseFullName());
			}
		}
//		CreditUserApply creditUserApply = new CreditUserApply();
//		creditUserApply.setReplaceUserId(id);
//		Page<CreditUserApply> page = creditUserApplyService.findPage(new Page<CreditUserApply>(request, response), creditUserApply);
//		List<CreditUserApply> list = page.getList();
//		for (CreditUserApply userApply : list) {
//			CreditUserInfo creditLoanUserInfo = creditUserInfoService.get(userApply.getCreditSupplyId()); // 借款人信息.
//			userApply.setLoanUserEnterpriseFullName(creditLoanUserInfo.getEnterpriseFullName());
//			CreditUserInfo creditReplaceUserInfo = creditUserInfoService.get(userApply.getReplaceUserId()); // 代偿户信息.
//			userApply.setReplaceUserEnterpriseFullName(creditReplaceUserInfo.getEnterpriseFullName());
////			userApply.setRepayDate(DateUtils.getSpecifiedMonthAfter(userApply.getCreateDate(),Integer.parseInt(userApply.getSpan())));
//		}
		model.addAttribute("page", page);
		model.addAttribute("creditUserId", creditUserId);
		return "modules/sys/loanProjectList";
	}
	
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "repaymentList")
	public String repaymentList(HttpServletRequest request, HttpServletResponse response, Model model) {
			String creditUserId = request.getParameter("creditUserId");
			model.addAttribute("creditUserId", creditUserId);
		return "modules/sys/downloadRepayment";
	}
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "downloadProjectPlan")
	public void downloadProjectPlan(WloanTermProjectPlan wloanTermProjectPlan,HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			List<WloanTermProjectPlan> planList = null;
			String fileName = "还款计划信息" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			String beginDate = request.getParameter("beginDate");//开始时间
			String endDate = request.getParameter("endDate");//结束时间
			String creditUserId = request.getParameter("creditUserId");//核心企业id
			CreditUserInfo creditUser = creditUserInfoService.get(creditUserId);
			if(creditUser!=null){
				CreditSupplierToMiddlemen creditSupplierToMiddlemen = new CreditSupplierToMiddlemen();
				creditSupplierToMiddlemen.setMiddlemenId(creditUser.getId());
				List<CreditSupplierToMiddlemen> creditSupplierToMiddlemenList = creditSupplierToMiddlemenDao.findList(creditSupplierToMiddlemen);
				for(CreditSupplierToMiddlemen creditSupplierToMiddlemen2:creditSupplierToMiddlemenList){
					WloanTermProject wloanTermProject = new WloanTermProject();
					WloanSubject subject = new WloanSubject();
					subject.setLoanApplyId(creditSupplierToMiddlemen2.getSupplierId());
					List<WloanSubject> subjectList = wloanSubjectService.findList(subject);
					if (subjectList != null && subjectList.size() > 0) {
						WloanSubject wloanSubject = subjectList.get(0);
						wloanTermProject.setSubjectId(wloanSubject.getId());
						List<WloanTermProject> projectList = wloanTermProjectService.findList(wloanTermProject);
						if (projectList != null && projectList.size() > 0) {
							for (WloanTermProject loanProject : projectList) {
								// N5.根据项目状态是否为[融资中/已放款]来查询项目还款记录
								if (loanProject.getState().equals(WloanTermProjectService.REPAYMENT) || loanProject.getState().equals(WloanTermProjectService.FINISH)) {

									// N6.根据项目ID查询借款用户的项目还款计划
									WloanTermProjectPlan projectPlan = new WloanTermProjectPlan();
									projectPlan.setWloanTermProject(loanProject);
									projectPlan.setBeginDate(beginDate);
									projectPlan.setEndDate(endDate);
									planList = wloanTermProjectPlanService.findList(projectPlan);
								}
							}
						}
					}
				}
			}
			new ExportExcel("还款计划", WloanTermProjectPlan.class).setDataList(planList).write(response, fileName).dispose();
			log.info("导出还款计划成功");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出还款计划失败");
		}
		
	}
	
	
	/**
	 * 借款申请详情
	 * @param creditUserApply
	 * @param model
	 * @return
	 */
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "form")
	public String form(CreditUserApply creditUserApply, Model model) {
		
		List<CreditVoucher> voucherList = new ArrayList<CreditVoucher>();
		List<CreditAnnexFile> annexFileList = new ArrayList<CreditAnnexFile>();
		List<CreditAnnexFile> creditAnnexFileList = new ArrayList<CreditAnnexFile>();
		CreditUserApply userApply = new CreditUserApply();
        String userApplyId = creditUserApply.getProjectDataId();
        if(userApplyId!=null){
        	userApply = creditUserApplyService.findApplyById(userApplyId);
        	if(userApply!=null){
        		String creditInfoId = userApply.getProjectDataId();
        		if(creditInfoId!=null){
        			voucherList = creditVoucherService.findListByInfoId(creditInfoId);	
        			}
        		}
        	annexFileList = creditAnnexFileService.findCreditAnnexFileList(userApplyId);
        	if(annexFileList!=null && annexFileList.size()>0){
        		for (CreditAnnexFile creditAnnexFile : annexFileList) {
					String type = creditAnnexFile.getType();
					String typeName = "";
					if(type.equals("1")){
						typeName = "合同影印件";
					}else if(type.equals("2")){
						typeName = "订单";
					}else if(type.equals("3")){
						typeName = "发货单";
					}else if(type.equals("4")){
						typeName = "验收单";
					}else if(type.equals("5")){
						typeName = "对账单";
					}else if(type.equals("6")){
						continue;
					}else if(type.equals("7")){
						typeName = "核心企业承诺函";
					}else{
						typeName = "其他";
					}
					creditAnnexFile.setRemark(typeName);
					creditAnnexFile.setUrl(creditAnnexFile.getUrl());
					creditAnnexFileList.add(creditAnnexFile);
				}
        	}
        	}
        	String borrPurpose = userApply.getBorrPurpose();
        	borrPurpose = borrPurpose.split("data")[1];
        	userApply.setBorrPurpose(borrPurpose);
        	model.addAttribute("creditUserApply", userApply);
        	model.addAttribute("voucherList",voucherList);//发票
        	model.addAttribute("annexFileList", creditAnnexFileList);//附件
		
		return "modules/user/creditUserApplyForm";
	}

}