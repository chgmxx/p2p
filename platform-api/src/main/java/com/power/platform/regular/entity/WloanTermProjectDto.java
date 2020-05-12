package com.power.platform.regular.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.sys.entity.Area;

public class WloanTermProjectDto extends DataEntity<WloanTermProjectDto> {

	private static final long serialVersionUID = 1L;
	private String sn; // 编号
	private String name; // 项目名称
	private String subjectId; // 融资主体ID
	private String docId; // 融资档案表ID
	private Double amount; // 融资金额
	private Double annualRate; // 年化收益
	private Date publishDate; // 发布日期
	private Date onlineDate; // 上线日期
	private Date fullDate; // 满标日期
	private Date loanDate; // 流标日期
	private Date realLoanDate; // 真实放款日期
	private Integer span; // 期限
	private String state; // 状态 0-撤销；1-草稿；2-审核；3-发布；4-上线；5-满标；6-还款中；7-已还完
	private String projectProductType; // 标的产品类型.

	private String companyName; // 公司名称

	private Double interestRateIncrease; // 利率增加（加息）.

	private String creditUserApplyId; // 借款申请ID.

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getRealLoanDate() {

		return realLoanDate;
	}

	public void setRealLoanDate(Date realLoanDate) {

		this.realLoanDate = realLoanDate;
	}

	public String getCreditUserApplyId() {

		return creditUserApplyId;
	}

	public void setCreditUserApplyId(String creditUserApplyId) {

		this.creditUserApplyId = creditUserApplyId;
	}

	public Double getInterestRateIncrease() {

		return interestRateIncrease;
	}

	public void setInterestRateIncrease(Double interestRateIncrease) {

		this.interestRateIncrease = interestRateIncrease;
	}

	public String getProjectProductType() {

		return projectProductType;
	}

	public void setProjectProductType(String projectProductType) {

		this.projectProductType = projectProductType;
	}

	public WloanTermProjectDto() {

		super();
	}

	public WloanTermProjectDto(String id) {

		super(id);
	}

	@ExcelField(title = "项目编号", align = 2, sort = 1)
	public String getSn() {

		return sn;
	}

	public void setSn(String sn) {

		this.sn = sn;
	}

	@ExcelField(title = "项目名称", align = 2, sort = 5)
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getSubjectId() {

		return subjectId;
	}

	public void setSubjectId(String subjectId) {

		this.subjectId = subjectId;
	}

	public String getDocId() {

		return docId;
	}

	public void setDocId(String docId) {

		this.docId = docId;
	}

	public Double getAmount() {

		return amount;
	}

	@ExcelField(title = "融资金额(元)", align = 2, sort = 15)
	public String getExcelAmount() {

		return String.valueOf(amount);
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public Double getAnnualRate() {

		return annualRate;
	}

	@ExcelField(title = "年化收益率(%)", align = 2, sort = 20)
	public String getExcelAnnualRate() {

		return String.valueOf(annualRate);
	}

	public void setAnnualRate(Double annualRate) {

		this.annualRate = annualRate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getPublishDate() {

		return publishDate;
	}

	public void setPublishDate(Date publishDate) {

		this.publishDate = publishDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ExcelField(title = "上线日期", align = 2, sort = 25)
	public Date getOnlineDate() {

		return onlineDate;
	}

	public void setOnlineDate(Date onlineDate) {

		this.onlineDate = onlineDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getFullDate() {

		return fullDate;
	}

	public void setFullDate(Date fullDate) {

		this.fullDate = fullDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ExcelField(title = "流标日期", align = 2, sort = 28)
	public Date getLoanDate() {

		return loanDate;
	}

	public void setLoanDate(Date loanDate) {

		this.loanDate = loanDate;
	}

	public Integer getSpan() {

		return span;
	}

	@ExcelField(title = "融资期限(天)", align = 2, sort = 30)
	public String getExcelSpan() {

		return String.valueOf(span);
	}

	public void setSpan(Integer span) {

		this.span = span;
	}

	@Length(min = 0, max = 255, message = "状态 0-撤销；1-草稿；2-审核；3-发布；4-上线；5-满标；6-还款中；7-已还完长度必须介于 0 和 255 之间")
	public String getState() {

		return state;
	}

	@ExcelField(title = "项目状态", align = 2, sort = 60)
	public String getExcelState() {

		String content = "";
		if (state.equals("0")) {
			content = "撤销";
		}
		if (state.equals("1")) {
			content = "草稿";
		}
		if (state.equals("2")) {
			content = "审核中";
		}
		if (state.equals("3")) {
			content = "发布中";
		}
		if (state.equals("4")) {
			content = "投标中";
		}
		if (state.equals("5")) {
			content = "  已满标";
		}
		if (state.equals("6")) {
			content = "还款中";
		}
		if (state.equals("7")) {
			content = "已结束";
		}
		if (state.equals("8")) {
			content = "流标";
		}
		return content;
	}

	public void setState(String state) {

		this.state = state;
	}

	public String getCompanyName() {

		return companyName;
	}

	public void setCompanyName(String companyName) {

		this.companyName = companyName;
	}

}