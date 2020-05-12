package com.power.platform.plan.entity;

import java.util.Date;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;

public class WloanTermProjectPlan extends DataEntity<WloanTermProjectPlan> {

	private static final long serialVersionUID = 1L;

	// 落单标识，用于标记该笔交易已在存管行完成还款操作，隐藏页面还款按钮，防止操作人员二次点击
	private String orderStatus; // TRUE、FALSE
	// 子订单号.
	private String subOrderId;
	// 项目ID.
	private String projectId;

	private WloanTermProject wloanTermProject; // 定期项目主体

	private Date repaymentDate;// 还款时间

	private String principal;// 类型 1还本付息 0 只还利息

	private Double interest;// 应还金额

	private Double interestTrue;// 没有四舍五入的还款金额

	private String state;

	private String repaymentState; // 1:显示还款 2：不显示

	private String beginDate;// 开始时间
	private String endDate;// 结束时间

	// 融资主体.
	private WloanSubject wloanSubject;
	// 借款人信息.
	private CreditUserInfo creditUserInfo;

	private Date beginRepaymentDate;// 开始时间
	private Date endRepaymentDate;// 结束时间

	// private String creditFee;//核心企业分摊金额
	// private String supplyFee;//供应商分摊金额
	private String shareFee;// 分摊金额

	// 区分还款中的和历史还款的.
	private String repayPlanRadioType;

	/**
	 * 1：还款中的记录.
	 */
	public static final String REPAY_PLAN_RADIO_TYPE_1 = "1";

	/**
	 * 2：历史还款记录.
	 */
	public static final String REPAY_PLAN_RADIO_TYPE_2 = "2";

	public String getRepayPlanRadioType() {

		return repayPlanRadioType;
	}

	public void setRepayPlanRadioType(String repayPlanRadioType) {

		this.repayPlanRadioType = repayPlanRadioType;
	}

	public String getProjectId() {

		return projectId;
	}

	public void setProjectId(String projectId) {

		this.projectId = projectId;
	}

	public String getSubOrderId() {

		return subOrderId;
	}

	public void setSubOrderId(String subOrderId) {

		this.subOrderId = subOrderId;
	}

	public String getRepaymentState() {

		return repaymentState;
	}

	public void setRepaymentState(String repaymentState) {

		this.repaymentState = repaymentState;
	}

	public String getState() {

		return state;
	}

	@ExcelField(title = "状态", align = 2, sort = 35)
	public String getStateStr() {

		String str = "";
		if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1.equals(state)) {
			str = "未还";
		} else if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2.equals(state)) {
			str = "已还";
		} else {
			str = "失败";
		}

		return str;
	}

	public void setState(String state) {

		this.state = state;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

	public String getPrincipal() {

		return principal;
	}

	@ExcelField(title = "还款类型", align = 2, sort = 20)
	public String getExcelPrincipal() {

		String content = "";
		if (Integer.valueOf(principal) == 1) {
			content = "还本";
		}
		if (Integer.valueOf(principal) == 0) {
			content = "付息";
		}
		return content;
	}

	public void setPrincipal(String principal) {

		this.principal = principal;
	}

	@ExcelField(title = "还款金额", align = 2, sort = 25)
	public String getExcelInterest() {

		return String.valueOf(interest);
	}

	public Double getInterest() {

		return interest;
	}

	public void setInterest(Double interest) {

		this.interest = interest;
	}

	@ExcelField(title = "还款日期", align = 2, sort = 30)
	public Date getRepaymentDate() {

		return repaymentDate;
	}

	public void setRepaymentDate(Date repaymentDate) {

		this.repaymentDate = repaymentDate;
	}

	public Double getInterestTrue() {

		return interestTrue;
	}

	public void setInterestTrue(Double interestTrue) {

		this.interestTrue = interestTrue;
	}

	@ExcelField(title = "项目名称", align = 2, sort = 10)
	public String projectName() {

		return wloanTermProject.getName();
	}

	@ExcelField(title = "项目编号", align = 2, sort = 15)
	public String projectSn() {

		return wloanTermProject.getSn();
	}

	// @ExcelField(title = "借款户", align = 2, sort = 17)
	public String projectCreditUserPhone() {

		return creditUserInfo.getPhone();
	}

	// @ExcelField(title = "借款人", align = 2, sort = 18)
	public String projectCreditUserName() {

		return creditUserInfo.getName();
	}

	// @ExcelField(title = "法人", align = 2, sort = 19)
	public String projectLoanUser() {

		return wloanSubject.getLoanUser();
	}

	public WloanSubject getWloanSubject() {

		return wloanSubject;
	}

	public void setWloanSubject(WloanSubject wloanSubject) {

		this.wloanSubject = wloanSubject;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

	public String getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(String beginDate) {

		this.beginDate = beginDate;
	}

	public String getEndDate() {

		return endDate;
	}

	public void setEndDate(String endDate) {

		this.endDate = endDate;
	}

	public Date getBeginRepaymentDate() {

		return beginRepaymentDate;
	}

	public void setBeginRepaymentDate(Date beginRepaymentDate) {

		this.beginRepaymentDate = beginRepaymentDate;
	}

	public Date getEndRepaymentDate() {

		return endRepaymentDate;
	}

	public void setEndRepaymentDate(Date endRepaymentDate) {

		this.endRepaymentDate = endRepaymentDate;
	}

	/**
	 * 导出数据专用
	 */
	@ExcelField(title = "融资主体", align = 2, sort = 16)
	public String projectCompanyName() {

		return wloanSubject.getCompanyName();
	}

	@ExcelField(title = "实际融资金额(元)", align = 2, sort = 50)
	public String getExcelCurrentAmount() {

		return String.valueOf(wloanTermProject.getCurrentAmount());
	}

	// @ExcelField(title = "核心企业分摊金额(元)", align = 2, sort = 51)
	// public String getCreditFee() {
	// return creditFee;
	// }
	//
	// public void setCreditFee(String creditFee) {
	// this.creditFee = creditFee;
	// }
	// @ExcelField(title = "供应商分摊金额(元)", align = 2, sort = 52)
	// public String getSupplyFee() {
	// return supplyFee;
	// }
	//
	// public void setSupplyFee(String supplyFee) {
	// this.supplyFee = supplyFee;
	// }

	@ExcelField(title = "分摊金额(元)", align = 2, sort = 51)
	public String getShareFee() {

		return shareFee;
	}

	public void setShareFee(String shareFee) {

		this.shareFee = shareFee;
	}

	public String getOrderStatus() {

		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {

		this.orderStatus = orderStatus;
	}

}
