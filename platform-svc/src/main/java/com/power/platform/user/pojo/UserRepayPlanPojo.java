package com.power.platform.user.pojo;

/**
 * 
 * 类: UserRepayPlanInfo <br>
 * 描述: 用户还款计划详情. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年2月1日 上午11:24:31
 */
public class UserRepayPlanPojo {

	/**
	 * 项目名称.
	 */
	private String projectName;

	/**
	 * 项目编号.
	 */
	private String projectSn;

	/**
	 * 还款日期.
	 */
	private String repaymentDate;

	/**
	 * 现在还款金额.
	 */
	private Double nowRepayAmount;

	/**
	 * 剩余还款金额.
	 */
	private Double remainingRepayAmount;

	/**
	 * 还款状态（1：初始化，2：还款中，3：还款成功，4：还款失败，5：流标）.
	 */
	private String status;

	/**
	 * 还款类型（0：付息，1：还本付息）.
	 */
	private String type;

	public String getProjectName() {

		return projectName;
	}

	public void setProjectName(String projectName) {

		this.projectName = projectName;
	}

	public String getProjectSn() {

		return projectSn;
	}

	public void setProjectSn(String projectSn) {

		this.projectSn = projectSn;
	}

	public String getRepaymentDate() {

		return repaymentDate;
	}

	public void setRepaymentDate(String repaymentDate) {

		this.repaymentDate = repaymentDate;
	}

	public Double getNowRepayAmount() {

		return nowRepayAmount;
	}

	public void setNowRepayAmount(Double nowRepayAmount) {

		this.nowRepayAmount = nowRepayAmount;
	}

	public Double getRemainingRepayAmount() {

		return remainingRepayAmount;
	}

	public void setRemainingRepayAmount(Double remainingRepayAmount) {

		this.remainingRepayAmount = remainingRepayAmount;
	}

	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

}
