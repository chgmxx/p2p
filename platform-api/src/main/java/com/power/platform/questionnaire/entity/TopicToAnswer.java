package com.power.platform.questionnaire.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: TopicToAnswer <br>
 * 描述: 题目与答案关联表Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月19日 下午2:44:28
 */
public class TopicToAnswer extends DataEntity<TopicToAnswer> {

	private static final long serialVersionUID = 1L;
	private String topicId; // 题目答案关联表复合主键id..
	private String answerId; // 题目答案关联表复合主键id..
	private String remark; // 备注.

	public TopicToAnswer() {

		super();
	}

	public TopicToAnswer(String id) {

		super(id);
	}

	public String getTopicId() {

		return topicId;
	}

	public void setTopicId(String topicId) {

		this.topicId = topicId;
	}

	public String getAnswerId() {

		return answerId;
	}

	public void setAnswerId(String answerId) {

		this.answerId = answerId;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

}