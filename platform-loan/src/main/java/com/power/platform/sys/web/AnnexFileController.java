package com.power.platform.sys.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.common.config.Global;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;

@Controller
@RequestMapping(value= "${adminPath}/sys/annexfile")
public class AnnexFileController extends BaseController {
	
	@Autowired
	private AnnexFileService annexFileService;
	
	/**
	 * 跳转上传附件页面---融资
	 * @param returnUrl 回调地址
	 * @param otherId  其他表主键 （如有id则不需要传此字段）
	 * @param dictType 字典类型
	 * @param id 附件表id
	 * @return
	 */
	@RequestMapping(value = "form")
	public String form(AnnexFile annexFile, Model model,String returnUrl,String otherId,String dictType,String id,String title) {
				if (StringUtils.isNotBlank(id)){
					annexFile = annexFileService.get(id);
				}else{
					annexFile =new AnnexFile();
					annexFile.setOtherId(otherId);
				}
			 
			annexFile.setReturnUrl(returnUrl);	
			annexFile.setDictType(dictType);
			annexFile.setTitle(title);
			model.addAttribute("annexFile", annexFile);
		return "modules/sys/annexFileForm";
	}
	
	/**
	 * 跳转上传附件页面---风控
	 * @param returnUrl 回调地址
	 * @param otherId  其他表主键 （如有id则不需要传此字段）
	 * @param dictType 字典类型
	 * @param id 附件表id
	 * @return
	 */
	@RequestMapping(value = "formRiskManagement")
	public String formRiskManagement(AnnexFile annexFile, Model model,String returnUrl,String otherId,String dictType,String id,String title) {
				if (StringUtils.isNotBlank(id)){
					annexFile = annexFileService.get(id);
				}else{
					annexFile =new AnnexFile();
					annexFile.setOtherId(otherId);
				}
			 
			annexFile.setReturnUrl(returnUrl);	
			annexFile.setDictType(dictType);
			annexFile.setTitle(title);
			model.addAttribute("annexFile", annexFile);
		return "modules/sys/annexFileRiskManagement";
	}
	
	/**
	 * 跳转上传附件页面---风控(签约)
	 * @param returnUrl 回调地址
	 * @param otherId  其他表主键 （如有id则不需要传此字段）
	 * @param dictType 字典类型
	 * @param id 附件表id
	 * @return
	 */
	@RequestMapping(value = "formSign")
	public String formSign(AnnexFile annexFile, Model model,String returnUrl,String otherId,String dictType,String id,String title) {
				if (StringUtils.isNotBlank(id)){
					annexFile = annexFileService.get(id);
				}else{
					annexFile =new AnnexFile();
					annexFile.setOtherId(otherId);
				}
			System.out.println("--========================="+returnUrl);
			annexFile.setReturnUrl(returnUrl);	
			annexFile.setDictType(dictType);
			annexFile.setTitle(title);
			model.addAttribute("annexFile", annexFile);
		return "modules/sys/annexFileSign";
	}
	
	@RequestMapping(value = "save")
	public String save(AnnexFile annexFile, Model model,RedirectAttributes redirectAttributes) {
			annexFile.setCurrentUser(SessionUtils.getUser());
			annexFileService.save(annexFile);
			model.addAttribute("annexFile", annexFile);
			addMessage(redirectAttributes, "上传成功");
		return "redirect:"+Global.getAdminPath()+annexFile.getReturnUrl();
	}
	
	@ResponseBody
	@RequestMapping(value = "checkForm")
	public String form(AnnexFile annexFile, Model model,String value,String otherId) {
		annexFile.setType(value);
		annexFile.setOtherId(otherId);
		Integer count=annexFileService.findCount(annexFile);
		if(null!=count&&count>0){
			return "1";
		}else{
			return "0";
		}
	}
	 
	@RequestMapping(value = "delete")
	public String delete(AnnexFile annexFile,String returnUrl, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		try {
			//annexFileService.delete(annexFile);
			annexFileService.deleteAnnexFile(annexFile.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		addMessage(redirectAttributes, (isRe!=null&&isRe?"":"删除")+"成功");
		return "redirect:"+Global.getAdminPath()+returnUrl;
	}
	
}
