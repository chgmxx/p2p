package com.power.platform.questionnaire.answer;

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
import com.power.platform.questionnaire.dao.AnswerDao;
import com.power.platform.questionnaire.entity.Answer;
import com.power.platform.questionnaire.entity.Topic;
import com.power.platform.questionnaire.entity.TopicToAnswer;
import com.power.platform.questionnaire.service.AnswerService;
import com.power.platform.questionnaire.service.TopicService;

/**
 * 
 * 类: AnswerController <br>
 * 描述: 答案Controller. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午4:23:44
 */
@Controller
@RequestMapping(value = "${adminPath}/questionnaire/answer")
public class AnswerController extends BaseController {

	@Autowired
	private TopicService topicService;
	@Autowired
	private AnswerService answerService;
	@Resource
	private AnswerDao answerDao;

	@ModelAttribute
	public Answer get(@RequestParam(required = false) String id) {

		Answer entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = answerService.get(id);
		}
		if (entity == null) {
			entity = new Answer();
		}
		return entity;
	}

	@RequiresPermissions("questionnaire:answer:view")
	@RequestMapping(value = { "list", "" })
	public String list(Answer answer, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<Answer> page = answerService.findPage(new Page<Answer>(request, response), answer);
		model.addAttribute("page", page);
		return "modules/questionnaire/answerList";
	}

	@RequiresPermissions("questionnaire:answer:view")
	@RequestMapping(value = "form")
	public String form(Answer answer, Model model) {

		model.addAttribute("answer", answer);
		return "modules/questionnaire/answerForm";
	}

	@RequiresPermissions("questionnaire:answer:edit")
	@RequestMapping(value = "save")
	public String save(Answer answer, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, answer)) {
			return form(answer, model);
		}
		answerService.save(answer);
		addMessage(redirectAttributes, "保存答案成功");
		return "redirect:" + Global.getAdminPath() + "/questionnaire/answer/?repage";
	}

	@RequiresPermissions("questionnaire:answer:edit")
	@RequestMapping(value = "delete")
	public String delete(Answer answer, RedirectAttributes redirectAttributes) {

		answerService.delete(answer);
		addMessage(redirectAttributes, "删除答案成功");
		return "redirect:" + Global.getAdminPath() + "/questionnaire/answer/?repage";
	}

	/**
	 * 
	 * 方法: assign <br>
	 * 描述: 已选答案列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午2:59:11
	 * 
	 * @param topic
	 * @param model
	 * @return
	 */
	@RequiresPermissions("questionnaire:answer:edit")
	@RequestMapping(value = "assign")
	public String assign(Topic topic, Model model) {

		List<Answer> answerList = answerDao.findAnswerAssignList(topic.getId());
		Topic entity = topicService.get(topic.getId());
		model.addAttribute("topicName", entity.getName());
		model.addAttribute("answerList", answerList);
		return "modules/questionnaire/answerAssignList";
	}

	/**
	 * 
	 * 方法: assignTopic <br>
	 * 描述: 返回题目及答案已选的题目列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午3:03:27
	 * 
	 * @param entity
	 * @param model
	 * @return
	 */
	@RequiresPermissions("questionnaire:answer:edit")
	@RequestMapping(value = "assignAnswer")
	public String assignAnswer(Topic entity, Model model) {

		List<Answer> answerList = answerDao.findAnswerAssignList(entity.getId());
		model.addAttribute("topicName", topicService.get(entity.getId()).getName());

		// 题目.
		Topic topic = topicService.get(entity.getId());
		model.addAttribute("topic", topic);
		// 已选答案列表.
		model.addAttribute("answerList", answerList);
		return "modules/questionnaire/selectTopicToAnswer";
	}

	/**
	 * 
	 * 方法: topicTree <br>
	 * 描述: 待选答案列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午3:07:19
	 * 
	 * @param topicId
	 * @param response
	 * @return
	 */
	@RequiresPermissions("questionnaire:answer:edit")
	@ResponseBody
	@RequestMapping(value = "answerTree")
	public List<Map<String, Object>> answerTree(String topicId, HttpServletResponse response) {

		List<Map<String, Object>> mapList = Lists.newArrayList();
		// 待选题目.
		List<Answer> answerList = answerDao.findAnswerTreeList(topicId);
		for (Answer answer : answerList) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", answer.getId());
			map.put("name", answer.getName());
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 
	 * 方法: confirmAnswerAssign <br>
	 * 描述: 确认向题目分配答案. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午3:13:35
	 * 
	 * @param entity
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("questionnaire:answer:edit")
	@RequestMapping(value = "confirmAnswerAssign")
	public String confirmAnswerAssign(Topic entity, String[] idsArr, RedirectAttributes redirectAttributes) {

		// 遍历idsArr.
		for (int i = 0; i < idsArr.length; i++) {
			// 为题目分配答案，向题目答案关联表中新增数据.
			TopicToAnswer model = new TopicToAnswer();
			model.setTopicId(entity.getId());
			model.setAnswerId(idsArr[i]);
			model.setCreateBy(SessionUtils.getUser());
			model.setCreateDate(new Date(System.currentTimeMillis() + 1000 * i));
			model.setUpdateBy(SessionUtils.getUser());
			model.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * i));
			model.setRemark("题目答案");
			model.setDelFlag(TopicToAnswer.DEL_FLAG_NORMAL);
			int flag = answerDao.insertTopicAnswer(model);
			if (flag == 1) {
				logger.debug("问卷分配题目成功.");
			} else {
				logger.debug("问卷分配题目失败.");
			}
		}
		return "redirect:" + Global.getAdminPath() + "/questionnaire/answer/assign?id=" + entity.getId();
	}

	/**
	 * 
	 * 方法: deleteTopicAnswer <br>
	 * 描述: 题目中移除答案. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月19日 下午3:16:04
	 * 
	 * @param answer
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("questionnaire:answer:edit")
	@RequestMapping(value = "deleteTopicAnswer")
	public String deleteTopicAnswer(Answer answer, RedirectAttributes redirectAttributes) {

		int flag = answerDao.deleteTopicAnswer(answer);
		if (flag == 1) {
			addMessage(redirectAttributes, "移除答案成功");
		} else {
			addMessage(redirectAttributes, "移除答案失败");
		}
		return "redirect:" + Global.getAdminPath() + "/questionnaire/answer/assign?id=" + answer.getTopicId();
	}

}