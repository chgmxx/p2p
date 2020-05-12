/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 代偿还款订单历史Entity
 * 
 * @author lance
 * @version 2019-08-01
 */
public class ZtmgReplaceRepayOrderHistory extends DataEntity<ZtmgReplaceRepayOrderHistory> {

	private static final long serialVersionUID = 1L;
	private String proName; // 项目名称
	private String proSn; // 项目编号
	private String subName; // 融资主体名称
	private String grantAmount; // 放款金额（元）
	private String repayAmount; // 还款金额（元）
	private String repayType; // 还款类型（0：付息，1：还本付息）
	private Date repayDate; // 还款日期
	private Date cancelDate; // 流标日期
	private Date grantDate; // 放款日期
	private String status; // 还款状态（S：成功，AS：受理成功，F：失败）
	private String remark; // 备注
	private String repayPlanId; // 项目还款计划ID.

	private String flag; // 是否代偿切换（0：否，1：是）.
	private String projectSn; // 代偿切换，项目编号.

	public ZtmgReplaceRepayOrderHistory() {

		super();
	}

	public ZtmgReplaceRepayOrderHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "项目名称长度必须介于 0 和 64 之间")
	public String getProName() {

		return proName;
	}

	public void setProName(String proName) {

		this.proName = proName;
	}

	@Length(min = 0, max = 32, message = "项目编号长度必须介于 0 和 32 之间")
	public String getProSn() {

		return proSn;
	}

	public void setProSn(String proSn) {

		this.proSn = proSn;
	}

	@Length(min = 0, max = 64, message = "融资主体名称长度必须介于 0 和 64 之间")
	public String getSubName() {

		return subName;
	}

	public void setSubName(String subName) {

		this.subName = subName;
	}

	@Length(min = 0, max = 64, message = "放款金额（元）长度必须介于 0 和 64 之间")
	public String getGrantAmount() {

		return grantAmount;
	}

	public void setGrantAmount(String grantAmount) {

		this.grantAmount = grantAmount;
	}

	@Length(min = 0, max = 64, message = "还款金额（元）长度必须介于 0 和 64 之间")
	public String getRepayAmount() {

		return repayAmount;
	}

	public void setRepayAmount(String repayAmount) {

		this.repayAmount = repayAmount;
	}

	@Length(min = 0, max = 1, message = "还款类型（0：付息，1：还本付息）长度必须介于 0 和 1 之间")
	public String getRepayType() {

		return repayType;
	}

	public void setRepayType(String repayType) {

		this.repayType = repayType;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRepayDate() {

		return repayDate;
	}

	public void setRepayDate(Date repayDate) {

		this.repayDate = repayDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCancelDate() {

		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {

		this.cancelDate = cancelDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getGrantDate() {

		return grantDate;
	}

	public void setGrantDate(Date grantDate) {

		this.grantDate = grantDate;
	}

	@Length(min = 0, max = 2, message = "还款状态（S：成功，AS：受理成功，F：失败）长度必须介于 0 和 2 之间")
	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public String getRepayPlanId() {

		return repayPlanId;
	}

	public void setRepayPlanId(String repayPlanId) {

		this.repayPlanId = repayPlanId;
	}

	public String getFlag() {

		return flag;
	}

	public void setFlag(String flag) {

		this.flag = flag;
	}

	public String getProjectSn() {

		return projectSn;
	}

	public void setProjectSn(String projectSn) {

		this.projectSn = projectSn;
	}

}