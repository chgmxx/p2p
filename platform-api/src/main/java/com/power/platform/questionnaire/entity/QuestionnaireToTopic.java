package com.power.platform.questionnaire.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: QuestionnaireToTopic <br>
 * 描述: 问卷与题目关联表Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月19日 下午1:23:17
 */
public class QuestionnaireToTopic extends DataEntity<QuestionnaireToTopic> {

	private static final long serialVersionUID = 1L;
	private String questionnaireId; // 问卷题目关联表复合主键id..
	private String topicId; // 问卷题目关联表复合主键id..
	private String remark; // 备注.

	public QuestionnaireToTopic() {

		super();
	}

	public QuestionnaireToTopic(String id) {

		super(id);
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

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

}