package com.power.platform.regular.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.web.BaseController;
import com.power.platform.regular.entity.WloanTermDoc;
import com.power.platform.regular.service.WloanTermDocService;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;

/**
 * 
 * 类: WloanTermDocController <br>
 * 描述: 定期融资档案Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月28日 下午7:19:37
 */
@Controller
@RequestMapping(value = "${adminPath}/wloan_term_doc/wloanTermDoc")
public class WloanTermDocController extends BaseController {

	/**
	 * 附件Service.
	 */
	@Autowired
	private AnnexFileService annexFileService;

	/**
	 * 定期融资档案Service.
	 */
	@Autowired
	private WloanTermDocService wloanTermDocService;

	@ModelAttribute
	public WloanTermDoc get(@RequestParam(required = false) String id) {

		WloanTermDoc entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermDocService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermDoc();
		}
		return entity;
	}

	@RequiresPermissions("wloan_term_doc:wloanTermDoc:view")
	@RequestMapping(value = { "list", "" })
	public String list(WloanTermDoc wloanTermDoc, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<WloanTermDoc> page = wloanTermDocService.findPage(new Page<WloanTermDoc>(request, response), wloanTermDoc);
		model.addAttribute("page", page);
		return "modules/regular/wloan_term_doc/wloanTermDocList";
	}

	/**
	 * 
	 * 方法: manageForm <br>
	 * 描述: 定期融资档案管理. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午4:51:23
	 * 
	 * @param wloanTermDoc
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "manageForm")
	public String manageForm(WloanTermDoc wloanTermDoc, Model model) {

		// 1>-定期融资档案表.
		model.addAttribute("wloanTermDoc", wloanTermDoc);
		// 2>-附件表.
		AnnexFile annexFile = new AnnexFile();
		annexFile.setDictType(WloanTermDocService.WLOAN_TERM_DOC_DIC_TYPE); // 资料类别.
		annexFile.setOtherId(wloanTermDoc.getId()); // 定期融资档案主键ID.
		annexFile.setTitle(wloanTermDoc.getName()); // 名称.
		annexFile.setReturnUrl("/wloan_term_doc/wloanTermDoc/manageForm?id=" + wloanTermDoc.getId()); // 回调URL.
		model.addAttribute("annexFile", annexFile);
		// 3>-定期融资档案附件列表.
		List<AnnexFile> annexFiles = annexFileService.findAnnexFilesByWloanTermDoc(annexFile);
		for (AnnexFile aFile : annexFiles) {
			aFile.setDictType(WloanTermDocService.WLOAN_TERM_DOC_DIC_TYPE); // 资料类别.
			aFile.setOtherId(wloanTermDoc.getId()); // 定期融资档案主键ID.
			aFile.setTitle(wloanTermDoc.getName()); // 名称.
			aFile.setReturnUrl("/wloan_term_doc/wloanTermDoc/manageForm?id=" + wloanTermDoc.getId()); // 回调URL.
		}
		model.addAttribute("annexFiles", annexFiles);
		return "modules/regular/wloan_term_doc/wloanTermDocManageForm";
	}

	/**
	 * 
	 * 方法: addForm <br>
	 * 描述: 添加. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 上午10:43:45
	 * 
	 * @param wloanTermDoc
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_doc:wloanTermDoc:view")
	@RequestMapping(value = "addForm")
	public String addForm(WloanTermDoc wloanTermDoc, Model model) {

		model.addAttribute("wloanTermDoc", wloanTermDoc);
		return "modules/regular/wloan_term_doc/wloanTermDocAddForm";
	}

	/**
	 * 
	 * 方法: updateForm <br>
	 * 描述: 修改. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 上午10:45:03
	 * 
	 * @param wloanTermDoc
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_doc:wloanTermDoc:view")
	@RequestMapping(value = "updateForm")
	public String updateForm(WloanTermDoc wloanTermDoc, Model model) {

		model.addAttribute("wloanTermDoc", wloanTermDoc);
		return "modules/regular/wloan_term_doc/wloanTermDocUpdateForm";
	}

	/**
	 * 
	 * 方法: viewForm <br>
	 * 描述: 查看. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 下午7:46:09
	 * 
	 * @param wloanTermDoc
	 * @param model
	 * @return
	 */
	@RequiresPermissions("wloan_term_doc:wloanTermDoc:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(WloanTermDoc wloanTermDoc, Model model) {

		AnnexFile annexFile = new AnnexFile();
		annexFile.setDictType(WloanTermDocService.WLOAN_TERM_DOC_DIC_TYPE); // 资料类别.
		annexFile.setOtherId(wloanTermDoc.getId()); // 定期融资档案主键ID.
		annexFile.setTitle(wloanTermDoc.getName()); // 名称.
		annexFile.setReturnUrl("/wloan_term_doc/wloanTermDoc/manageForm?id=" + wloanTermDoc.getId()); // 回调URL.
		model.addAttribute("annexFile", annexFile);
		List<AnnexFile> annexFiles = annexFileService.findAnnexFilesByWloanTermDoc(annexFile);
		model.addAttribute("annexFiles", annexFiles);
		model.addAttribute("wloanTermDoc", wloanTermDoc);
		return "modules/regular/wloan_term_doc/wloanTermDocViewForm";
	}

	/**
	 * 
	 * 方法: addSave <br>
	 * 描述: 添加保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 上午10:45:38
	 * 
	 * @param wloanTermDoc
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_term_doc:wloanTermDoc:edit")
	@RequestMapping(value = "addSave")
	public String addSave(WloanTermDoc wloanTermDoc, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermDoc)) {
			return addForm(wloanTermDoc, model);
		}
		// 新增定期融资档案.
		wloanTermDoc.setId(IdGen.uuid());
		wloanTermDoc.setCreateBy(SessionUtils.getUser());
		wloanTermDoc.setCreateDate(new Date());
		wloanTermDoc.setUpdateBy(SessionUtils.getUser());
		wloanTermDoc.setUpdateDate(new Date());
		int flag = wloanTermDocService.insertWloanTermDoc(wloanTermDoc);
		if (flag == 1) {
			addMessage(redirectAttributes, "新增定期融资档案保存成功");
		} else {
			addMessage(redirectAttributes, "新增定期融资档案保存失败");
		}
		return "redirect:" + Global.getAdminPath() + "/wloan_term_doc/wloanTermDoc/?repage";
	}

	/**
	 * 
	 * 方法: updateSave <br>
	 * 描述: 修改保存. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月29日 上午10:49:15
	 * 
	 * @param wloanTermDoc
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("wloan_term_doc:wloanTermDoc:edit")
	@RequestMapping(value = "updateSave")
	public String updateSave(WloanTermDoc wloanTermDoc, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, wloanTermDoc)) {
			return addForm(wloanTermDoc, model);
		}
		// 新增定期融资档案.
		wloanTermDoc.setUpdateBy(SessionUtils.getUser());
		wloanTermDoc.setUpdateDate(new Date());
		int flag = wloanTermDocService.updateWloanTermDoc(wloanTermDoc);
		if (flag == 1) {
			addMessage(redirectAttributes, "修改定期融资档案保存成功");
		} else {
			addMessage(redirectAttributes, "修改定期融资档案保存失败");
		}
		return "redirect:" + Global.getAdminPath() + "/wloan_term_doc/wloanTermDoc/?repage";
	}

	@RequiresPermissions("wloan_term_doc:wloanTermDoc:edit")
	@RequestMapping(value = "delete")
	public String delete(WloanTermDoc wloanTermDoc, RedirectAttributes redirectAttributes) {

		// 判断融资项目是否使用到融资档案.
		List<WloanTermDoc> list = wloanTermDocService.isExistWloanTermDocAndWloanTermProject(wloanTermDoc);
		if (list != null && list.size() > 0) {
			addMessage(redirectAttributes, "当前融资档案已被使用");
		} else {
			wloanTermDocService.delete(wloanTermDoc);
			addMessage(redirectAttributes, "删除定期融资档案成功");
		}

		return "redirect:" + Global.getAdminPath() + "/wloan_term_doc/wloanTermDoc/?repage";
	}

}