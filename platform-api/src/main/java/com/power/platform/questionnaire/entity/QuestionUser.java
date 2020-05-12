package com.power.platform.questionnaire.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 客户问卷Entity
 * @author nice
 * @version 2017-04-19
 */
public class QuestionUser extends DataEntity<QuestionUser> {
	
	private static final long serialVersionUID = 1L;
	private UserInfo userInfo;		// user_id
	private String topicId;		// topic_id
	private String answerId;		// answer_id
	private String remark;		// 备注
	
	public QuestionUser() {
		super();
	}

	public QuestionUser(String id){
		super(id);
	}

	public UserInfo getUser() {
		return userInfo;
	}

	public void setUser(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	@Length(min=0, max=64, message="topic_id长度必须介于 0 和 64 之间")
	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	
	@Length(min=0, max=64, message="answer_id长度必须介于 0 和 64 之间")
	public String getAnswerId() {
		return answerId;
	}

	public void setAnswerId(String answerId) {
		this.answerId = answerId;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}