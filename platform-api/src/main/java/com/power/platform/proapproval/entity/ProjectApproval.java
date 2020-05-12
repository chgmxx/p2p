package com.power.platform.proapproval.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.regular.entity.WloanTermProject;

/**
 * 项目审批entity
 * 
 * @author Jia
 *
 */
public class ProjectApproval extends DataEntity<ProjectApproval> {

	private static final long serialVersionUID = 1L;

	private WloanTermProject wloanTermProject; // 定期项目主体

	private String rcerLoanTerms; // 风控专员，放款条件

	private String rcerImplement; // 风控专员，落实情况

	private String rcerUser; // 风控专员

	private String rclerkUser; // 风控文员

	private String financeOption; // 财务人员意见

	private String financeUser; // 财务人员

	private String adminOption; // 总经理意见

	private String adminUser; // 总经理

	private String state; // 审批状态

	private String refuseUser; // 拒绝人

	private Date refuseDate; // 拒绝时间
	
	private Date rclerkUpdateDate;	// 文员修改时间
	
	private Date financeUpdateDate;	// 财务修改时间
	
	private String rcerManagerOption; //风控经理意见

	private String rcerManagerUser; // 风控经理
	
	private Date rcerManagerUpdateDate;	// 财务修改时间
	
	
	public String getRcerManagerOption() {
		return rcerManagerOption;
	}

	public void setRcerManagerOption(String rcerManagerOption) {
		this.rcerManagerOption = rcerManagerOption;
	}

	public String getRcerManagerUser() {
		return rcerManagerUser;
	}

	public void setRcerManagerUser(String rcerManagerUser) {
		this.rcerManagerUser = rcerManagerUser;
	}

	public Date getRcerManagerUpdateDate() {
		return rcerManagerUpdateDate;
	}

	public void setRcerManagerUpdateDate(Date rcerManagerUpdateDate) {
		this.rcerManagerUpdateDate = rcerManagerUpdateDate;
	}

	public Date getRclerkUpdateDate() {
		return rclerkUpdateDate;
	}

	public void setRclerkUpdateDate(Date rclerkUpdateDate) {
		this.rclerkUpdateDate = rclerkUpdateDate;
	}

	public Date getFinanceUpdateDate() {
		return financeUpdateDate;
	}

	public void setFinanceUpdateDate(Date financeUpdateDate) {
		this.financeUpdateDate = financeUpdateDate;
	}

	public String getRefuseUser() {

		return refuseUser;
	}

	public void setRefuseUser(String refuseUser) {

		this.refuseUser = refuseUser;
	}

	public Date getRefuseDate() {

		return refuseDate;
	}

	public void setRefuseDate(Date refuseDate) {

		this.refuseDate = refuseDate;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

	@ExcelField(title = "放款条件", align = 2, sort = 80)
	public String getRcerLoanTerms() {

		return rcerLoanTerms;
	}

	public void setRcerLoanTerms(String rcerLoanTerms) {

		this.rcerLoanTerms = rcerLoanTerms;
	}

	@ExcelField(title = "落实情况", align = 2, sort = 90)
	public String getRcerImplement() {

		return rcerImplement;
	}

	public void setRcerImplement(String rcerImplement) {

		this.rcerImplement = rcerImplement;
	}

	public String getRcerUser() {

		return rcerUser;
	}

	public void setRcerUser(String rcerUser) {

		this.rcerUser = rcerUser;
	}

	public String getRclerkUser() {

		return rclerkUser;
	}

	public void setRclerkUser(String rclerkUser) {

		this.rclerkUser = rclerkUser;
	}

	@ExcelField(title = "财务意见", align = 2, sort = 100)
	public String getFinanceOption() {

		return financeOption;
	}

	public void setFinanceOption(String financeOption) {

		this.financeOption = financeOption;
	}

	public String getFinanceUser() {

		return financeUser;
	}

	public void setFinanceUser(String financeUser) {

		this.financeUser = financeUser;
	}

	@ExcelField(title = "总经理意见", align = 2, sort = 110)
	public String getAdminOption() {

		return adminOption;
	}

	public void setAdminOption(String adminOption) {

		this.adminOption = adminOption;
	}

	public String getAdminUser() {

		return adminUser;
	}

	public void setAdminUser(String adminUser) {

		this.adminUser = adminUser;
	}

	@ExcelField(title = "流转节点", align = 2, sort = 120)
	public String getNodeInfo() {

		if (state.equals("1")) {
			return "风控专员";
		} else if (state.equals("2")) {
			return "风控文员";
		} else if (state.equals("3")) {
			return "财务";
		} else if (state.equals("4")) {
			return "总经理";
		} else
			return "";
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@ExcelField(title = "借款人", align = 2, sort = 10)
	public String getCompanyName() {

		return wloanTermProject.getWloanSubjectCompanyName();
	}

	@ExcelField(title = "项目名称", align = 2, sort = 20)
	public String getWloanTermProjectName() {

		return wloanTermProject.getName();
	}

	@ExcelField(title = "项目编号", align = 2, sort = 30)
	public String getWloanTermProjectNo() {

		return wloanTermProject.getSn();
	}

	@ExcelField(title = "融资金额", align = 2, sort = 40)
	public String getWloanTermProjectAmount() {

		return wloanTermProject.getAmount().toString();
	}

	@ExcelField(title = "融资期限", align = 2, sort = 50)
	public String getWloanTermProjectSpan() {

		return wloanTermProject.getSpan().toString();
	}

	@ExcelField(title = "手续费", align = 2, sort = 60)
	public String getWloanTermProjectFeeRate() {

		return wloanTermProject.getFeeRate().toString();
	}

	@ExcelField(title = "保证金", align = 2, sort = 70)
	public String getWloanTermProjectMarginPercentage() {

		return wloanTermProject.getMarginPercentage().toString();
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "放款日期", align = 2, sort = 80)
	public Date getLoanDate() {

		return wloanTermProject.getLoanDate();
	}

}
