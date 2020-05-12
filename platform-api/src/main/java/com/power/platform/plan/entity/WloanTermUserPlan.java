package com.power.platform.plan.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 类: WloanTermUserPlan <br>
 * 描述: 客户还款计划. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年6月13日 上午10:45:17
 */
public class WloanTermUserPlan extends DataEntity<WloanTermUserPlan> {

	private static final long serialVersionUID = 1L;

	private WloanTermProject wloanTermProject; // 定期项目主体

	private String projectId; // 项目ID.

	private String userId; // 客户帐号ID.

	private String wloanTermInvestId; // 客户投资ID.

	private Date repaymentDate;// 还款时间

	private String principal; // 类型 1还本付息 0 只还利息

	private Double interest; // 应还金额，利息四舍五入后

	private Double interestTrue; // 应还金额，利息四舍五入前

	private UserInfo userInfo;

	private String state; // 1：初始化 2：正在还款 3：已经还款

	private WloanTermInvest wloanTermInvest; // 投资

	private Date beginDate; // 查询还款日期，开始时间.

	private Date endDate; // 查询还款日期，结束时间.

	private Integer dayI;

	public WloanTermInvest getWloanTermInvest() {

		return wloanTermInvest;
	}

	public void setWloanTermInvest(WloanTermInvest wloanTermInvest) {

		this.wloanTermInvest = wloanTermInvest;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

	public String getProjectId() {

		return projectId;
	}

	public void setProjectId(String projectId) {

		this.projectId = projectId;
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public String getWloanTermInvestId() {

		return wloanTermInvestId;
	}

	public void setWloanTermInvestId(String wloanTermInvestId) {

		this.wloanTermInvestId = wloanTermInvestId;
	}

	public String getPrincipal() {

		return principal;
	}

	@ExcelField(title = "还款类型", align = 2, sort = 120)
	public String getExcelPrincipal() {

		String content = "";
		if (principal.equals("1")) {
			content = "本息";
		}
		if (principal.equals("2")) {
			content = "利息";
		}
		return content;
	}

	public void setPrincipal(String principal) {

		this.principal = principal;
	}

	public Double getInterest() {

		return interest;
	}

	@ExcelField(title = "投资金额", align = 2, sort = 80)
	public String getExcelAmount() {

		return wloanTermInvest.getAmount().toString();
	}

	@ExcelField(title = "还款金额", align = 2, sort = 90)
	public String getExcelInterest() {

		return interest.toString();
	}

	public void setInterest(Double interest) {

		this.interest = interest;
	}

	public Double getInterestTrue() {

		return interestTrue;
	}

	public void setInterestTrue(Double interestTrue) {

		this.interestTrue = interestTrue;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "还款日期", align = 2, sort = 150)
	public Date getRepaymentDate() {

		return repaymentDate;
	}

	public void setRepaymentDate(Date repaymentDate) {

		this.repaymentDate = repaymentDate;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public String getState() {

		return state;
	}

	@ExcelField(title = "还款状态", align = 2, sort = 180)
	public String getExcelState() {

		String content = "";
		if (state.equals("1")) {
			content = "初始化";
		}
		if (state.equals("2")) {
			content = "正在还款";
		}
		if (state.equals("3")) {
			content = "还款成功";
		}
		if (state.equals("4")) {
			content = "还款失败";
		}

		return content;
	}

	public void setState(String state) {

		this.state = state;
	}

	@ExcelField(title = "项目名称", align = 2, sort = 30)
	public String getProjectName() {

		return wloanTermProject.getName();
	}

	@ExcelField(title = "投资用户", align = 2, sort = 60)
	public String getInvestUserPhone() {

		return userInfo.getName();
	}

	@ExcelField(title = "真实姓名", align = 2, sort = 70)
	public String getInvestUserName() {

		return userInfo.getRealName();
	}

	public Date getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;
	}

	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	public Integer getDayI() {

		return dayI;
	}

	public void setDayI(Integer dayI) {

		this.dayI = dayI;
	}

}
