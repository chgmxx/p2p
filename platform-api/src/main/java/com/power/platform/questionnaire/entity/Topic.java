package com.power.platform.questionnaire.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: Topic <br>
 * 描述: 题目Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午3:57:40
 */
public class Topic extends DataEntity<Topic> {

	private static final long serialVersionUID = 1L;
	private String name; // 题目名称
	private String remark; // 备注
	private List<Answer> answerList;
	private Questionnaire questionnaire; // 归属问卷.
	private String questionnaireId; // 问卷题目关联表复合主键id.
	private String topicId; // 问卷题目关联表复合主键id.


	public Topic() {

		super();
	}

	public Topic(String id) {

		super(id);
	}

	@Length(min = 0, max = 255, message = "题目名称长度必须介于 0 和 255 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public List<Answer> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<Answer> answerList) {
		this.answerList = answerList;
	}


	
	public Questionnaire getQuestionnaire() {

		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {

		this.questionnaire = questionnaire;
	}

	public String getQuestionnaireId() {

		return questionnaireId;
	}

	public void setQuestionnaireId(String questionnaireId) {

		this.questionnaireId = questionnaireId;
	}

	public String getTopicId() {

		return topicId;
	}

	public void setTopicId(String topicId) {

		this.topicId = topicId;
	}

}