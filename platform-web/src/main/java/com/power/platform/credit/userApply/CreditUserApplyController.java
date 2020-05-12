package com.power.platform.credit.userApply;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucherInfoDetail;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.info.CreditInfoService;
import com.power.platform.credit.service.middlemen.CreditMiddlemenRateService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.credit.service.voucher.CreditVoucherInfoDetailService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.dao.UserBankCardDao;
import com.power.platform.userinfo.service.UserBankCardService;

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
	private UserBankCardService userBankCardService;
	@Resource
	private UserBankCardDao userBankCardDao;
	@Autowired
	private CreditInfoService creditInfoService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditVoucherInfoDetailService creditVoucherInfoDetailService;
	@Autowired
	private CreditMiddlemenRateService creditMiddlemenRateService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	
	
	

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

		Page<CreditUserApply> pageModel = new Page<CreditUserApply>(request, response);
		pageModel.setOrderBy("a.state ASC, a.create_date DESC");
		Page<CreditUserApply> page = creditUserApplyService.findPage(pageModel, creditUserApply);
		List<CreditUserApply> list = page.getList();
		for (CreditUserApply entity : list) {
			CreditUserInfo replaceUserInfo = entity.getReplaceUserInfo();
			if (replaceUserInfo != null) {
				replaceUserInfo.setPhone(CommonStringUtils.mobileEncrypt(replaceUserInfo.getPhone()));
			}
			String replaceUserId = entity.getReplaceUserId(); // 代偿户ID.
			CreditUserInfo creditReplaceUserInfo = creditUserInfoService.get(replaceUserId); // 代偿人信息.
			if (null != creditReplaceUserInfo) {
				entity.setReplaceUserEnterpriseFullName(creditReplaceUserInfo.getEnterpriseFullName());
			}
			
			CreditUserInfo creditUserInfo = creditUserInfoService.get(entity.getCreditSupplyId()); // 借款人信息.
			if (null != creditUserInfo) {
				entity.setLoanUserId(creditUserInfo.getId());
				entity.setLoanUserPhone(CommonStringUtils.mobileEncrypt(creditUserInfo.getPhone()));
				entity.setLoanUserName(creditUserInfo.getName());
				entity.setLoanUserEnterpriseFullName(creditUserInfo.getEnterpriseFullName());
			}
			// 资料信息
//			CreditInfo creditInfo = creditInfoService.get(entity.getProjectDataId());
			if(entity.getBorrPurpose()!=null && -1!=(entity.getBorrPurpose().indexOf("data"))){
				entity.setBorrPurpose(entity.getBorrPurpose().split("data")[1]);
			}
			if(entity.getDeclarationFilePath()!=null && -1!=(entity.getDeclarationFilePath().indexOf("data"))){
				entity.setDeclarationFilePath(entity.getDeclarationFilePath().split("data")[1]);
			}
		}
		model.addAttribute("page", page);

		CreditUserInfo userInfo = new CreditUserInfo();
		userInfo.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_11);
		List<CreditUserInfo> middlemenList = creditUserInfoService.findList(userInfo);
		model.addAttribute("middlemenList", middlemenList);
		return "modules/credit/userApply/creditUserApplyList";
	}

	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "form")
	public String form(CreditUserApply creditUserApply, Model model) {

		model.addAttribute("creditUserApply", creditUserApply);
		return "modules/apply/creditUserApplyForm";
	}

	/**
	 * 
	 * 方法: pass <br>
	 * 描述: 审核. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月15日 下午7:56:57
	 * 
	 * @param creditUserApply
	 * @param status
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("apply:creditUserApply:edit")
	@RequestMapping(value = "audit")
	public String pass(CreditUserApply creditUserApply, String status, Model model, RedirectAttributes redirectAttributes) {

		if (status.equals(CreditUserApplyService.CREDIT_USER_APPLY_STATE_2)) {
			log.info("状态，" + status + "：审核通过");
			
		} else if (status.equals(CreditUserApplyService.CREDIT_USER_APPLY_STATE_3)) {
			log.info("状态，" + status + "：审核驳回");
			//融资类型
			if(null==creditUserApply.getFinancingStep()){
				//应收账款转让
				
				//申请步骤改为第五步
				creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STATE_5);
				
				//置空  modify
				creditUserApply.setModify(null);
			}else {
				if(creditUserApply.getFinancingType().equals(CreditUserApplyService.CREDIT_FINANCING_TYPE_1)){
					//应收账款转让
					
					//申请步骤改为第五步
					creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STATE_5);
					
					//置空  modify
					creditUserApply.setModify(null);
					
				}else if(creditUserApply.getFinancingType().equals(CreditUserApplyService.CREDIT_FINANCING_TYPE_2)){
					//订单融资
					
					//申请步骤改为第六步
					creditUserApply.setFinancingStep(CreditUserApplyService.CREDIT_USER_APPLY_STATE_4);
					
					creditUserApply.setShareRate(null);
					creditUserApply.setSpan(null);
					creditUserApply.setAmount(null);
					
					//置空  modify fileConfirm
					creditUserApply.setModify(null);
					creditUserApply.setFileConfirm(null);
					
					//删除承诺函
					String creditInfoId = creditUserApply.getProjectDataId();
					if(creditInfoId!=null){
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditInfoId);
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7);
						List<CreditAnnexFile> creditAnnexFiles = creditAnnexFileService.findList(creditAnnexFile);
						if(creditAnnexFiles!=null && creditAnnexFiles.size()>0){
							for(CreditAnnexFile creFile:creditAnnexFiles){
								creditAnnexFileService.deleteCreditAnnexFileById(creFile.getId());
							}
						}
						
					}
				}
				
			}
			
			//删除合同
			creditUserApply.setBorrPurpose(null);
		}
		

		creditUserApply.setUpdateBy(SessionUtils.getUser());
		creditUserApply.setUpdateDate(new Date());
		creditUserApply.setState(status);
		creditUserApplyService.save(creditUserApply);
		return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/?repage";
	}

	/**
	 * 
	 * 方法: refused <br>
	 * 描述: 申请失败. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 上午9:45:52
	 * 
	 * @param creditUserApply
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("apply:creditUserApply:edit")
	@RequestMapping(value = "refused")
	public String refused(CreditUserApply creditUserApply, Model model, RedirectAttributes redirectAttributes) {

		// 更新状态为申请失败.
		creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_3);
		creditUserApplyService.save(creditUserApply);
		addMessage(redirectAttributes, "申请拒绝");
		return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/?repage";
	}

	@RequiresPermissions("apply:creditUserApply:edit")
	@RequestMapping(value = "save")
	public String save(CreditUserApply creditUserApply, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditUserApply)) {
			return form(creditUserApply, model);
		}
		creditUserApplyService.save(creditUserApply);
		addMessage(redirectAttributes, "保存借款申请成功");
		return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/?repage";
	}

	@RequiresPermissions("apply:creditUserApply:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditUserApply creditUserApply, RedirectAttributes redirectAttributes) {

		creditUserApplyService.delete(creditUserApply);
		addMessage(redirectAttributes, "删除借款申请成功");
		return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/?repage";
	}
	
	@RequiresPermissions("apply:creditUserApply:edit")
	@RequestMapping(value = "creditVoucherApplyList")
	public String creditVoucherApplyList(CreditUserApply creditUserApply, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditUserApply> page = creditUserApplyService.findVoucherApplyPage(new Page<CreditUserApply>(request, response), creditUserApply);
//		Page<CreditUserApply> page = creditUserApplyService.findPage(new Page<CreditUserApply>(request, response), creditUserApply);
		List<CreditUserApply> list = page.getList();
		for (CreditUserApply entity : list) {
			String replaceUserId = entity.getReplaceUserId(); // 代偿户ID.
			CreditUserInfo creditReplaceUserInfo = creditUserInfoService.get(replaceUserId); // 代偿人信息.
			if (null != creditReplaceUserInfo) {
				entity.setCreditUser(creditReplaceUserInfo);
			}
			String projectDataId = entity.getProjectDataId(); // 资料ID.
			CreditInfo creditInfo = creditInfoService.get(projectDataId);
			if (null != creditInfo) { // 资料信息
				entity.setProjectDataInfo(creditInfo);
				//开票记录
				CreditVoucherInfoDetail creditVoucherInfoDetail = new CreditVoucherInfoDetail();
				creditVoucherInfoDetail.setApplyId(entity.getId());
				List<CreditVoucherInfoDetail> creditVoucherInfoDetails = creditVoucherInfoDetailService.findList(creditVoucherInfoDetail);
				if(creditVoucherInfoDetails!=null && creditVoucherInfoDetails.size()>0){
					creditVoucherInfoDetail = creditVoucherInfoDetails.get(0);
					creditVoucherInfoDetail.setNumber(CommonStringUtils.idEncrypt(creditVoucherInfoDetail.getNumber()));
					creditVoucherInfoDetail.setPhone(CommonStringUtils.mobileEncrypt(creditVoucherInfoDetail.getPhone()));
					creditVoucherInfoDetail.setBankNo(CommonStringUtils.idEncrypt(creditVoucherInfoDetail.getBankNo()));
					creditVoucherInfoDetail.setToName(CommonStringUtils.replaceNameX(creditVoucherInfoDetail.getToName()));
					creditVoucherInfoDetail.setToPhone(CommonStringUtils.mobileEncrypt(creditVoucherInfoDetail.getToPhone()));
					entity.setCreditVoucherInfoDetail(creditVoucherInfoDetail);
				}
			}
			//服务费总金额
			Double financingMoney = 0D;
			if (entity.getAmount() != null) {
				int indexOf = entity.getAmount().indexOf(",");
				if (indexOf == -1) {
					financingMoney =  NumberUtils.scaleDouble(entity.getAmount()==null?0D:Double.valueOf(entity.getAmount()));//融资金额
				} else {
					String[] split = entity.getAmount().split(",");
					StringBuffer sBuffer = new StringBuffer();
					for (int i = 0; i < split.length; i++) {
						sBuffer.append(split[i]);
					}
					financingMoney =  NumberUtils.scaleDouble(entity.getAmount()==null?0D:Double.valueOf(sBuffer.toString()));//融资金额
				}
			} else {
				financingMoney =  NumberUtils.scaleDouble(0D);//融资金额
			}
			Double financingRate = NumberUtils.scaleDouble(entity.getLenderRate()==null?0D:Double.valueOf(entity.getLenderRate()));//融资利率
			Double financingSpan = NumberUtils.scaleDouble(entity.getSpan()==null?0D:Double.valueOf(entity.getSpan()));//融资期限
			String serviceRate = null;//服务费率
			if(financingMoney!=null && financingRate!=null &&financingSpan!=null){
				CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
				creditMiddlemenRate.setCreditUserId(replaceUserId);
				List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
				if(entity.getSpan()!=null){
					for(CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList){
						if(creditMiddlemenRate2.getSpan().equals(entity.getSpan())){
							serviceRate = creditMiddlemenRate2.getServiceRate();
						}
					}
				}
				Double interestMoney = (financingMoney*financingRate/36500)*financingSpan;//融资利息
				if(serviceRate!=null){
					Double serviceMoney = (financingMoney*Double.parseDouble(serviceRate)/36500)*financingSpan;//平台服务费
					Double registMoney=0.0;//登记费
					if(financingSpan<=120){
						registMoney = 30.0;
					}else{
						registMoney = 60.0;
					}
					
					Double sumFee = interestMoney+serviceMoney+registMoney;
					entity.setSumFee((double)Math.round(sumFee*100)/100);
				}
			}
		}
		model.addAttribute("page", page);
		return "modules/credit/userApply/creditVoucherApplyList";
	}
	
	/**
	 * 
	 * 方法: creditVoucherApplyOK <br>
	 * 描述: 开票. <br>
	 * 
	 * @param creditUserApply
	 * @param status
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("apply:creditUserApply:edit")
	@RequestMapping(value = "creditVoucherApplyOK")
	public String creditVoucherApplyOK(CreditUserApply creditUserApply,  Model model, RedirectAttributes redirectAttributes) {

		creditUserApply.setVoucherState("2");
		creditUserApplyService.save(creditUserApply);
		//开票记录
		CreditVoucherInfoDetail creditVoucherInfoDetail = new CreditVoucherInfoDetail();
		creditVoucherInfoDetail.setApplyId(creditUserApply.getId());
		List<CreditVoucherInfoDetail> creditVoucherInfoDetails = creditVoucherInfoDetailService.findList(creditVoucherInfoDetail);
		if(creditVoucherInfoDetails!=null && creditVoucherInfoDetails.size()>0){
			creditVoucherInfoDetail = creditVoucherInfoDetails.get(0);
			creditVoucherInfoDetail.setState(CreditVoucherInfoDetailService.STATE2);
			creditVoucherInfoDetailService.save(creditVoucherInfoDetail);
		}
		return "redirect:" + Global.getAdminPath() + "/apply/creditUserApply/creditVoucherApplyList/?repage";
	}
	
	
	@RequiresPermissions("apply:creditUserApply:view")
	@RequestMapping(value = "downloadVoucherInfoList")
	public void downloadVoucherInfoList(CreditUserApply creditUserApply,HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			String fileName = "申请列表" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<CreditUserApply> page = creditUserApplyService.findVoucherApplyPage(new Page<CreditUserApply>(request, response), creditUserApply);
//			Page<CreditUserApply> page = creditUserApplyService.findPage(new Page<CreditUserApply>(request, response), creditUserApply);
			List<CreditUserApply> list = page.getList();
			for (CreditUserApply entity : list) {
				String replaceUserId = entity.getReplaceUserId(); // 代偿户ID.
				CreditUserInfo creditReplaceUserInfo = creditUserInfoService.get(replaceUserId); // 代偿人信息.
				if (null != creditReplaceUserInfo) {
					entity.setCreditUser(creditReplaceUserInfo);
				}
				String projectDataId = entity.getProjectDataId(); // 资料ID.
				CreditInfo creditInfo = creditInfoService.get(projectDataId);
				if (null != creditInfo) { // 资料信息
					entity.setProjectDataInfo(creditInfo);
					//开票记录
					CreditVoucherInfoDetail creditVoucherInfoDetail = new CreditVoucherInfoDetail();
					creditVoucherInfoDetail.setApplyId(entity.getId());
					List<CreditVoucherInfoDetail> creditVoucherInfoDetails = creditVoucherInfoDetailService.findList(creditVoucherInfoDetail);
					if(creditVoucherInfoDetails!=null && creditVoucherInfoDetails.size()>0){
						creditVoucherInfoDetail = creditVoucherInfoDetails.get(0);
						entity.setCreditVoucherInfoDetail(creditVoucherInfoDetail);
					}
				}
				//服务费总金额
				Double financingMoney =  Double.parseDouble(entity.getAmount()==null?"0":entity.getAmount());//融资金额
				Double financingRate = Double.parseDouble(entity.getLenderRate()==null?"0":entity.getLenderRate());//融资利率
				Double financingSpan = Double.parseDouble(entity.getSpan()==null?"0":entity.getSpan());//融资期限
				String serviceRate = null;//服务费率
				if(financingMoney!=null && financingRate!=null &&financingSpan!=null){
					CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
					creditMiddlemenRate.setCreditUserId(replaceUserId);
					List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
					if(entity.getSpan()!=null){
						for(CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList){
							if(creditMiddlemenRate2.getSpan().equals(entity.getSpan())){
								serviceRate = creditMiddlemenRate2.getServiceRate();
							}
						}
					}
					Double interestMoney = (financingMoney*financingRate/36500)*financingSpan;//融资利息
					if(serviceRate!=null){
						Double serviceMoney = (financingMoney*Double.parseDouble(serviceRate)/36500)*financingSpan;//平台服务费
						Double registMoney=0.0;//登记费
						if(financingSpan<=120){
							registMoney = 30.0;
						}else{
							registMoney = 60.0;
						}
						
						Double sumFee = interestMoney+serviceMoney+registMoney;
						entity.setSumFee((double)Math.round(sumFee*100)/100);
					}
				}
			}
//			model.addAttribute("page", page);
			if(list!=null && list.size()>0){
				new ExportExcel("申请列表", CreditUserApply.class).setDataList(list).write(response, fileName).dispose();
				log.info("导出申请列表成功");
			}else{
				log.info("无申请");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出申请列表失败");
		}
		
	}


}