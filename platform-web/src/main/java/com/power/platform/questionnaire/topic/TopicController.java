package com.power.platform.questionnaire.topic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.questionnaire.dao.TopicDao;
import com.power.platform.questionnaire.entity.Questionnaire;
import com.power.platform.questionnaire.entity.QuestionnaireToTopic;
import com.power.platform.questionnaire.entity.Topic;
import com.power.platform.questionnaire.service.QuestionnaireService;
import com.power.platform.questionnaire.service.TopicService;

/**
 * 题目Controller
 * 
 * @author nice
 * @version 2017-04-18
 */
@Controller
@RequestMapping(value = "${adminPath}/questionnaire/topic")
public class TopicController extends BaseController {

	@Autowired
	private QuestionnaireService questionnaireService;
	@Autowired
	private TopicService topicService;
	@Resource
	private TopicDao topicDao;

	@ModelAttribute
	public Topic get(@RequestParam(required = false) String id) {

		Topic entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = topicService.get(id);
		}
		if (entity == null) {
			entity = new Topic();
		}
		return entity;
	}

	@RequiresPermissions("topic:view")
	@RequestMapping(value = { "list", "" })
	public String list(Topic topic, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<Topic> page = topicService.findPage(new Page<Topic>(request, response), topic);
		model.addAttribute("page", page);
		return "modules/questionnaire/topicList";
	}

	@RequiresPermissions("topic:view")
	@RequestMapping(value = "form")
	public String form(Topic topic, Model model) {

		model.addAttribute("topic", topic);
		return "modules/questionnaire/topicForm";
	}

	@RequiresPermissions("topic:edit")
	@RequestMapping(value = "save")
	public String save(Topic topic, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, topic)) {
			return form(topic, model);
		}
		topicService.save(topic);
		addMessage(redirectAttributes, "保存题目成功");
		return "redirect:" + Global.getAdminPath() + "/questionnaire/topic/?repage";
	}

	@RequiresPermissions("topic:edit")
	@RequestMapping(value = "delete")
	public String delete(Topic topic, RedirectAttributes redirectAttributes) {

		topicService.delete(topic);
		addMessage(redirectAttributes, "删除题目成功");
		return "redirect:" + Global.getAdminPath() + "/questionnaire/topic/?repage";
	}

	/**
	 * 
	 * 方法: assign <br>
	 * 描述: 已选题目列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午12:54:46
	 * 
	 * @param questionnaire
	 * @param model
	 * @return
	 */
	@RequiresPermissions("topic:edit")
	@RequestMapping(value = "assign")
	public String assign(Questionnaire questionnaire, Model model) {

		List<Topic> topicList = topicDao.findTopicAssignList(questionnaire.getId());
		Questionnaire entity = questionnaireService.get(questionnaire.getId());
		model.addAttribute("questionnaireName", entity.getName());
		model.addAttribute("topicList", topicList);
		return "modules/questionnaire/topicAssignList";
	}

	/**
	 * 
	 * 方法: assignTopic <br>
	 * 描述: 返回问卷及问卷已选的题目列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午12:53:18
	 * 
	 * @param entity
	 * @param model
	 * @return
	 */
	@RequiresPermissions("topic:edit")
	@RequestMapping(value = "assignTopic")
	public String assignTopic(Questionnaire entity, Model model) {

		List<Topic> topicList = topicDao.findTopicAssignList(entity.getId());
		model.addAttribute("questionnaireName", questionnaireService.get(entity.getId()).getName());
		// 问卷.
		Questionnaire questionnaire = questionnaireService.get(entity.getId());
		model.addAttribute("questionnaire", questionnaire);
		// 已选题目列表.
		model.addAttribute("topicList", topicList);
		return "modules/questionnaire/selectQuestionnaireToTopic";
	}

	/**
	 * 
	 * 方法: topicTree <br>
	 * 描述: 待选题目列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 上午11:59:10
	 * 
	 * @param questionnaireId
	 * @param response
	 * @return
	 */
	@RequiresPermissions("topic:edit")
	@ResponseBody
	@RequestMapping(value = "topicTree")
	public List<Map<String, Object>> topicTree(String questionnaireId, HttpServletResponse response) {

		List<Map<String, Object>> mapList = Lists.newArrayList();
		// 待选题目.
		List<Topic> topicList = topicDao.findTopicTreeList(questionnaireId);
		for (Topic topic : topicList) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", topic.getId());
			map.put("name", topic.getName());
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 
	 * 方法: topicAssign <br>
	 * 描述: 确认向问卷分配题目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午1:00:33
	 * 
	 * @param entity
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("topic:edit")
	@RequestMapping(value = "confirmTopicAssign")
	public String confirmTopicAssign(Questionnaire entity, String[] idsArr, RedirectAttributes redirectAttributes) {

		// 遍历idsArr.
		for (int i = 0; i < idsArr.length; i++) {
			// 为问卷分配题目，向问卷题目关联表中新增数据.
			QuestionnaireToTopic model = new QuestionnaireToTopic();
			model.setQuestionnaireId(entity.getId());
			model.setTopicId(idsArr[i]);
			model.setCreateBy(SessionUtils.getUser());
			model.setCreateDate(new Date(System.currentTimeMillis() + 1000 * i));
			model.setUpdateBy(SessionUtils.getUser());
			model.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * i));
			model.setRemark("问卷题目");
			model.setDelFlag(QuestionnaireToTopic.DEL_FLAG_NORMAL);
			int flag = topicDao.insertQuestionnaireTopic(model);
			if (flag == 1) {
				logger.debug("问卷分配题目成功.");
			} else {
				logger.debug("问卷分配题目失败.");
			}
		}
		return "redirect:" + Global.getAdminPath() + "/questionnaire/topic/assign?id=" + entity.getId();
	}

	/**
	 * 
	 * 方法: deleteQuestionnaireTopic <br>
	 * 描述: 问卷中移除题目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午3:14:01
	 * 
	 * @param topic
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("topic:edit")
	@RequestMapping(value = "deleteQuestionnaireTopic")
	public String deleteQuestionnaireTopic(Topic topic, RedirectAttributes redirectAttributes) {

		int flag = topicDao.deleteQuestionnaireTopic(topic);
		if (flag == 1) {
			addMessage(redirectAttributes, "移除题目成功");
		} else {
			addMessage(redirectAttributes, "移除题目失败");
		}
		return "redirect:" + Global.getAdminPath() + "/questionnaire/topic/assign?id=" + topic.getQuestionnaireId();
	}

}