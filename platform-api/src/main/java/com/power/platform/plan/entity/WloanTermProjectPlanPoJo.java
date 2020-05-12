package com.power.platform.plan.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;

public class WloanTermProjectPlanPoJo extends DataEntity<WloanTermProjectPlanPoJo> {

	private static final long serialVersionUID = 1L;

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

	@ExcelField(title = "状态", align = 2, sort = 50)
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

	@ExcelField(title = "还款类型", align = 2, sort = 35)
	public String getExcelPrincipal() {

		String content = "";
		if (Integer.valueOf(principal) == 1) {
			content = "还本付息";
		}
		if (Integer.valueOf(principal) == 0) {
			content = "付息";
		}
		return content;
	}

	public void setPrincipal(String principal) {

		this.principal = principal;
	}

	@ExcelField(title = "还款金额", align = 2, sort = 40)
	public String getExcelInterest() {

		return NumberUtils.scaleDoubleStr(interest);
	}

	public Double getInterest() {

		return interest;
	}

	public void setInterest(Double interest) {

		this.interest = interest;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title = "还款日期", align = 2, sort = 45)
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

	@ExcelField(title = "项目名称", align = 2, sort = 15)
	public String projectName() {

		return wloanTermProject.getName();
	}

	@ExcelField(title = "项目编号", align = 2, sort = 20)
	public String projectSn() {

		return wloanTermProject.getSn();
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title = "流标日期", align = 2, sort = 25)
	public Date projectLoanDate() {

		return wloanTermProject.getLoanDate();
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title = "放款日期", align = 2, sort = 26)
	public Date projectRealLoanDate() {

		return wloanTermProject.getRealLoanDate();
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

	@ExcelField(title = "借款人", align = 2, sort = 10)
	public String wloanSubjectName() {

		return wloanSubject.getCompanyName();
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

	@ExcelField(title = "融资金额/放款金额(元)", align = 2, sort = 30)
	public String getExcelCurrentAmount() {

		return String.valueOf(wloanTermProject.getCurrentAmount());
	}

}
