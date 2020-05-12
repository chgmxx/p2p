package com.power.platform.questionnaire.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: Questionnaire <br>
 * 描述: 问卷Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年4月18日 下午2:59:17
 */
public class Questionnaire extends DataEntity<Questionnaire> {

	private static final long serialVersionUID = 1L;
	private String name; // 试卷名称.
	private String state; // 状态('1'：可用，'2'：不可用).
	private String remark; // 备注.

	public Questionnaire() {

		super();
	}

	public Questionnaire(String id) {

		super(id);
	}

	@Length(min = 0, max = 128, message = "试卷名称长度必须介于 0 和 128 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

}