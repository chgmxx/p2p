package com.power.platform.activity.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.power.platform.activity.pojo.Span;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.userinfo.entity.UserInfo;

public class UserVouchersHistory extends DataEntity<UserVouchersHistory> {

	private static final long serialVersionUID = 1L;
	private String awardId; // 抵用券ID
	private String userId; // 客户账号ID
	private Date overdueDate; // 逾期日期
	private String state; // 状态
	private String type; // 类型
	private String bidId; // 投资ID
	private String value; // value
	private String remark;
	// 逾期天数.
	private Integer overdueDays;
	// 起投金额.
	private Double limitAmount;
	// 起投金额-展示.
	private String limitAmountStr;

	// 充值原因.
	private String rechargeReason;

	public String getRechargeReason() {

		return rechargeReason;
	}

	public void setRechargeReason(String rechargeReason) {

		this.rechargeReason = rechargeReason;
	}

	public Integer getOverdueDays() {

		return overdueDays;
	}

	public void setOverdueDays(Integer overdueDays) {

		this.overdueDays = overdueDays;
	}

	public Double getLimitAmount() {

		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {

		this.limitAmount = limitAmount;
	}

	public String getLimitAmountStr() {

		return limitAmountStr;
	}

	public void setLimitAmountStr(String limitAmountStr) {

		this.limitAmountStr = limitAmountStr;
	}

	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	private AVouchersDic vouchersDic; // 抵用券字典数据.
	private List<AVouchersDic> vouchersDics; // 抵用券全部字典数据.
	private UserInfo userInfo; // 客户账号信息.
	private WloanTermProject wloanTermProject; // 投资项目信息.

	// 项目期限集合.
	private String spans;

	private List<Span> spanList = Lists.newArrayList(); // 拥有期限列表.

	public UserVouchersHistory() {

		super();
	}

	public UserVouchersHistory(String id) {

		super(id);
	}

	public List<Span> getSpanList() {

		return spanList;
	}

	public void setSpanList(List<Span> spanList) {

		this.spanList = spanList;
	}

	@JsonIgnore
	public List<String> getSpanIdList() {

		List<String> spanIdList = Lists.newArrayList();
		for (Span span : spanList) {
			spanIdList.add(span.getId());
		}
		return spanIdList;
	}

	public void setSpanIdList(List<String> spanIdList) {

		spanList = Lists.newArrayList();
		for (String spanId : spanIdList) {
			Span span = new Span();
			span.setId(spanId);
			spanList.add(span);
		}
	}

	@Length(min = 0, max = 64, message = "抵用券ID长度必须介于 0 和 64 之间")
	public String getAwardId() {

		return awardId;
	}

	public void setAwardId(String awardId) {

		this.awardId = awardId;
	}

	@Length(min = 0, max = 64, message = "客户账号ID长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 32, message = "姓名长度必须介于 0 和 32 之间")
	@ExcelField(title = "姓名", align = 2, sort = 10)
	public String getUserRealName() {

		return userInfo.getRealName();
	}

	@Length(min = 1, max = 32, message = "手机号码长度必须介于 1 和 32 之间")
	@ExcelField(title = "手机号码", align = 2, sort = 20)
	public String getName() {

		return userInfo.getName();
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ExcelField(title = "获取日期", align = 2, sort = 60)
	public Date getCreateDate() {

		return createDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ExcelField(title = "逾期日期", align = 2, sort = 70)
	public Date getOverdueDate() {

		return overdueDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ExcelField(title = "更新日期", align = 2, sort = 80)
	public Date getUpdateDate() {

		return updateDate;
	}

	public void setOverdueDate(Date overdueDate) {

		this.overdueDate = overdueDate;
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 1, message = "类型长度必须介于 0 和 1 之间")
	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	@Length(min = 0, max = 64, message = "投资ID长度必须介于 0 和 64 之间")
	public String getBidId() {

		return bidId;
	}

	public void setBidId(String bidId) {

		this.bidId = bidId;
	}

	@Length(min = 0, max = 64, message = "value长度必须介于 0 和 64 之间")
	@ExcelField(title = "抵用券(RMB)", align = 2, sort = 30)
	public String getValue() {

		return value;
	}

	@ExcelField(title = "状态", align = 2, sort = 40)
	public String getStateString() {

		String message = "";
		if ("1".equals(state)) {
			message = "可用，未使用";
		} else if ("2".equals(state)) {
			message = "已使用";
		} else if ("3".equals(state)) {
			message = "逾期的，过期的";
		}
		return message;
	}

	@ExcelField(title = "投资项目", align = 2, sort = 50)
	public String getProjectName() {

		return wloanTermProject.getName();
	}

	public void setValue(String value) {

		this.value = value;
	}

	public AVouchersDic getVouchersDic() {

		return vouchersDic;
	}

	public void setVouchersDic(AVouchersDic vouchersDic) {

		this.vouchersDic = vouchersDic;
	}

	public List<AVouchersDic> getVouchersDics() {

		return vouchersDics;
	}

	public void setVouchersDics(List<AVouchersDic> vouchersDics) {

		this.vouchersDics = vouchersDics;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

	public String getSpans() {

		return spans;
	}

	public void setSpans(String spans) {

		this.spans = spans;
	}

}