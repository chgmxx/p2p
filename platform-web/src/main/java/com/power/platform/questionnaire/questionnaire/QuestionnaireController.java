package com.power.platform.questionnaire.questionnaire;

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
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.questionnaire.entity.Questionnaire;
import com.power.platform.questionnaire.service.QuestionnaireService;

/**
 * 
 * 类: QuestionnaireController <br>
 * 描述: 问卷Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午3:03:24
 */
@Controller
@RequestMapping(value = "${adminPath}/questionnaire")
public class QuestionnaireController extends BaseController {

	@Autowired
	private QuestionnaireService questionnaireService;

	@ModelAttribute
	public Questionnaire get(@RequestParam(required = false) String id) {

		Questionnaire entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = questionnaireService.get(id);
		}
		if (entity == null) {
			entity = new Questionnaire();
		}
		return entity;
	}

	@RequiresPermissions("questionnaire:view")
	@RequestMapping(value = { "list", "" })
	public String list(Questionnaire questionnaire, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<Questionnaire> page = questionnaireService.findPage(new Page<Questionnaire>(request, response), questionnaire);
		model.addAttribute("page", page);
		return "modules/questionnaire/questionnaireList";
	}

	@RequiresPermissions("questionnaire:view")
	@RequestMapping(value = "form")
	public String form(Questionnaire questionnaire, Model model) {

		model.addAttribute("questionnaire", questionnaire);
		return "modules/questionnaire/questionnaireForm";
	}

	@RequiresPermissions("questionnaire:edit")
	@RequestMapping(value = "save")
	public String save(Questionnaire questionnaire, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, questionnaire)) {
			return form(questionnaire, model);
		}
		questionnaireService.save(questionnaire);
		addMessage(redirectAttributes, "保存问卷成功");
		return "redirect:" + Global.getAdminPath() + "/questionnaire/?repage";
	}

	@RequiresPermissions("questionnaire:edit")
	@RequestMapping(value = "delete")
	public String delete(Questionnaire questionnaire, RedirectAttributes redirectAttributes) {

		questionnaireService.delete(questionnaire);
		addMessage(redirectAttributes, "删除问卷成功");
		return "redirect:" + Global.getAdminPath() + "/questionnaire/?repage";
	}

}