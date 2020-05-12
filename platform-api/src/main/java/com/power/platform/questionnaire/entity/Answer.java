package com.power.platform.questionnaire.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: Answer <br>
 * 描述: 答案Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午4:04:46
 */
public class Answer extends DataEntity<Answer> {

	private static final long serialVersionUID = 1L;
	private String name; // 答案
	private String remark; // 备注
	private Topic topic; // 归属题目.
	private String topicId; // 题目答案关联表复合主键id.
	private String answerId; // 题目答案关联表复合主键id.
	private Integer score; // 分值.

	public Answer() {

		super();
	}

	public Answer(String id) {

		super(id);
	}

	@Length(min = 0, max = 256, message = "答案长度必须介于 0 和 256 之间")
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

	public Topic getTopic() {

		return topic;
	}

	public void setTopic(Topic topic) {

		this.topic = topic;
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}


}