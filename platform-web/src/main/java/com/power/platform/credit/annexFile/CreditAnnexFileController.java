package com.power.platform.credit.annexFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.ZipUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.collateral.CreditCollateralInfoService;
import com.power.platform.credit.service.pack.CreditPackService;
import com.power.platform.credit.service.voucher.CreditVoucherService;

/**
 * 
 * 类: CreditAnnexFileController <br>
 * 描述: 信贷附件. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年5月12日 上午9:23:51
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/annexFile")
public class CreditAnnexFileController extends BaseController {

	/**
	 * 6:发票.
	 */
	private final static String CREDIT_ANNEX_FILE_TYPE = "6";

	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditCollateralInfoService creditCollateralInfoService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private CreditPackService creditPackService;
	@Autowired
	private CreditVoucherService creditVoucherService;

	@ModelAttribute
	public CreditAnnexFile get(@RequestParam(required = false) String id) {

		CreditAnnexFile entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditAnnexFileService.get(id);
		}
		if (entity == null) {
			entity = new CreditAnnexFile();
		}
		return entity;
	}

	@RequiresPermissions("credit:annexFile:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditAnnexFile creditAnnexFile, HttpServletRequest request, HttpServletResponse response, Model model, String creditUserApplyId) throws Exception {

		// 借款申请.
		CreditUserApply creditUserApply = creditUserApplyService.get(creditUserApplyId);

		Page<CreditAnnexFile> page = creditAnnexFileService.findPage(new Page<CreditAnnexFile>(request, response), creditAnnexFile);
		List<CreditAnnexFile> list = page.getList();
		// 压缩文件，一键下载用
		List<File> fileList = new ArrayList<File>();
		if (null != creditUserApply) {

			CreditPack creditPack = new CreditPack();
			creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
			// 查询借款合同信息
			List<CreditPack> creditPackList = creditPackService.findList(creditPack);
			if (creditPackList != null && creditPackList.size() > 0) {
				model.addAttribute("pack", creditPackList.get(0));
			} else {
				model.addAttribute("pack", creditPack);
			}

			if (StringUtils.isBlank(creditUserApply.getDeclarationFilePath())) {
				model.addAttribute("declarationFilePath", "");
			} else {
				File file = new File(creditUserApply.getDeclarationFilePath());
				if (file.exists()) {
					logger.info("file exists ...");
					fileList.add(new File(creditUserApply.getDeclarationFilePath()));
				} else {
				}
				model.addAttribute("declarationFilePath", creditUserApply.getDeclarationFilePath().split("data")[1]);
			}
			model.addAttribute("creditUserApplyUpdateDate", creditUserApply.getUpdateDate());
		} else {
			model.addAttribute("pack", new CreditPack());
			model.addAttribute("declarationFilePath", "");
			model.addAttribute("creditUserApplyUpdateDate", "");
		}
		int isUpload = 0;
		if (list != null) {
			for (CreditAnnexFile annexFile : list) {

				if (CREDIT_ANNEX_FILE_TYPE.equals(annexFile.getType())) {
					
					CreditVoucher creditVoucher = new CreditVoucher();
					creditVoucher.setAnnexId(annexFile.getId());
					List<CreditVoucher> creditVoucherList = creditVoucherService.findList(creditVoucher );
					if (creditVoucherList != null && creditVoucherList.size() > 0) {
						annexFile.setCreditVoucher(creditVoucherList.get(0));
					}
				}

				
				// 7：付款承诺书.
				if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7.equals(annexFile.getType())) {
					// 付款承诺书线上签订20190227，判断是否是此时间之后签署.
					if (!DateUtils.compare_date(DateUtils.formatDateTime(annexFile.getCreateDate()), "2019-02-27 00:00:00")) {
						annexFile.setBookByCommitment_B(true);
						File file = new File("/data" + annexFile.getUrl());
						if (file.exists()) {
							logger.info("file exists ...");
						} else {
							logger.info("file not exists, create it ...");
							continue;
						}
						fileList.add(file);
					}
				} else {
					File file = new File("/data/upload/image/" + annexFile.getUrl());
					if (file.exists()) {
						logger.info("file exists ...");
					} else {
						logger.info("file not exists, create it ...");
						continue;
					}
					fileList.add(file);
				}
			}
		}

		if (fileList != null && fileList.size() > 0) {
			String downLoad = System.currentTimeMillis() + ".zip";
			String downLoadAll = Global.getConfig("credit_annexFile") + downLoad;
			File file = new File(downLoadAll);
			FileOutputStream fos2 = new FileOutputStream(file);
			ZipUtils.toZip(fileList, fos2);
			model.addAttribute("downLoadAll", downLoad);
			/*if (file.exists()) {
				logger.info("file exists ...");
			} else {
				logger.info("file not exists, create it ...");
				model.addAttribute("downLoadAll", downLoad);
			}*/
		}

		model.addAttribute("model.addAttribute", request.getAttribute("creditInfoName"));
		model.addAttribute("page", page);
		model.addAttribute("creditInfoId", creditAnnexFile.getOtherId());
		model.addAttribute("isUpload", isUpload);
		System.out.println("LIST==========" + creditAnnexFile.getOtherId() + "***********" + isUpload);
		System.out.println("NAME==========" + creditAnnexFile.getRemark());
		return "modules/credit/annexfile/creditAnnexFileList";
	}

	@RequiresPermissions("credit:annexFile:view")
	@RequestMapping(value = "form")
	public String form(CreditAnnexFile creditAnnexFile, Model model) {

		model.addAttribute("creditInfoId", creditAnnexFile.getOtherId());

		// 附件表.
		CreditAnnexFile annexFile = new CreditAnnexFile();
		annexFile.setDictType("credit_info_type"); // 资料类别.
		annexFile.setOtherId(creditAnnexFile.getOtherId()); // otherId
		annexFile.setTitle("上传中等网动产登记查询结果"); // 名称.
		annexFile.setReturnUrl("/credit/annexFile/list?otherId=" + creditAnnexFile.getOtherId()); // 回调URL.
		model.addAttribute("creditAnnexFile", annexFile);
		return "modules/credit/annexfile/creditAnnexFileForm";

	}

	@RequiresPermissions("credit:annexFile:edit")
	@RequestMapping(value = "save")
	public String save(CreditAnnexFile creditAnnexFile, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, creditAnnexFile)) {
			return form(creditAnnexFile, model);
		}
		creditAnnexFile.setType("7");
		creditAnnexFile.setRemark("中等网动产登记查询结果");
		creditAnnexFileService.save(creditAnnexFile);
		addMessage(redirectAttributes, "保存中等网动产登记查询结果附件成功");
		return "redirect:" + Global.getAdminPath() + creditAnnexFile.getReturnUrl();
	}

	@RequiresPermissions("credit:annexFile:edit")
	@RequestMapping(value = "delete")
	public String delete(CreditAnnexFile creditAnnexFile, RedirectAttributes redirectAttributes) {

		creditAnnexFileService.delete(creditAnnexFile);
		addMessage(redirectAttributes, "删除中等网动产登记查询结果成功");
		return "redirect:" + Global.getAdminPath() + "/credit/annexFile/?repage";
	}

}