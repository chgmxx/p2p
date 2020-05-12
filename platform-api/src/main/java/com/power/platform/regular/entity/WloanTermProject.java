package com.power.platform.regular.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.data.entity.DataStatistics;
import com.power.platform.sys.entity.Area;

/**
 * 类: WloanTermProject <br>
 * 描述: 定期项目信息Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年1月26日 下午2:35:38
 */
public class WloanTermProject extends DataEntity<WloanTermProject> {

	private static final long serialVersionUID = 1L;
	private String sn; // 编号
	private String name; // 项目名称
	private String subjectId; // 融资主体ID
	private String guaranteeId; // 担保机构ID
	private String docId; // 融资档案表ID
	private String locus; // 所在地
	private Double amount; // 融资金额
	private Double annualRate; // 年化收益
	private Date publishDate; // 发布日期
	private Date onlineDate; // 上线日期
	private Date fullDate; // 满标日期
	private Date loanDate; // 放款日期
	private Date beginDate; // 开始时间
	private Date endDate; // 结束日期
	private Integer span; // 期限
	private Integer spans; // 列表查询用
	private String repayType; // 还款方式1．一次性还本付息 2．分期付息到期还本
	private Double minAmount; // 起投金额
	private Double maxAmount; // 最大投资金额
	private Double stepAmount; // 递增金额
	private Double feeRate; // 手续费
	private Double currentAmount; // 投资虚拟进度
	private Double currentRealAmount; // 投资实际进度
	private String purpose; // 资金用途
	private String projectCase; // 项目情况
	private String state; // 状态 0-撤销；1-草稿；2-审核；3-发布；4-上线；5-满标；6-还款中；7-已还完
	private String imgUrl; // 图片URL
	private String detailImgUrl; // 详细图片URL
	private String guaranteeSn; // 担保函编号
	private Double marginPercentage; // 保证金
	private String guaranteeScheme; // 担保方案
	private String remark; // 备注
	private String createById; // 创建人
	private String updateById; // 更改人
	private Date realLoanDate; // 实际放款日期
	private Date beginRealLoanDate; // 放款开始日期(项目管理搜索)
	private Date endRealLoanDate; // 放款结束日期(项目管理搜索)
	private String contractUrl; // 四方合同存储路径
	private String isCanUseCoupon; // 是否可用抵用券
	private String isCanUsePlusCoupon; // 是否可用加息券

	private WGuaranteeCompany wgCompany; // 担保公司
	private WloanSubject wloanSubject; // 融资主体
	private WloanTermDoc wloanTermDoc; // 融资档案
	private Area area; // 归属区域

	private List<String> stateItem; // 多状态查找参数
	private List<String> projectTypeItem; // 多标的类型查找参数.
	private Double minAnnualRate; // 最小利率
	private Double maxAnnualRate; // 最大利率
	private Double realLoanAmount; // 最大利率

	private Date endEndDate; // 到期结束时间
	/**
	 * data ： 2016-04-26
	 * auther ： Mr.Jia
	 * descript : 添加项目类型、标签字段
	 */
	private String projectType; // 项目类型
	private String label; // 项目标签
	

	/**
	 * 描述: 上线开始时间查询. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月11日 下午2:38:05
	 */
	private String beginTimeFromOnline;
	/**
	 * 描述: 上线结束时间查询. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月11日 下午2:38:05
	 */
	private String endTimeToOnline;
	/**
	 * 描述: 满标(包含还款中和已结束)项目开始时间查询. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月11日 下午2:38:05
	 */
	private String beginTimeFromFull;
	/**
	 * 描述: 满标(包含还款中和已结束)项目结束时间查询. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月11日 下午2:38:05
	 */
	private String endTimeToFull;

	private String isEntrustedPay; // 受托支付标识，0：否，1：是.
	private String projectRepayPlanType; // 项目偿还计划类型，0：旧版，1：新版，默认（旧版）.
	private String isEntrustedWithdraw; // 受托支付提现标识，0：否，1：是.
	private String projectDataId; // 融资项目资料ID.
	private String isReplaceRepay; // 是否代偿还款，0：否，1：是.
	/**
	 * 是否代偿还款，0：否.
	 */
	public static final String IS_REPLACE_REPAY_0 = "0";
	/**
	 * 是否代偿还款，1：是.
	 */
	public static final String IS_REPLACE_REPAY_1 = "1";
	private String replaceRepayId; // 代偿还款人ID.
	private String replaceRepayName;// 代偿企业名称

	private String projectProductType; // 标的产品类型.
	private String borrowerResidence; // 借款方住所.
	private String borrowerElectronicSignUrl; // 借款方电子签章URL.
	private String replaceRepayResidence; // 代偿方住所.
	private String replaceRepayElectronicSignUrl; // 代偿方电子签章URL.
	private String sourceOfRepayment; // 还款来源.
	private String creditUserApplyId; // 借款申请ID.
	private String creditUserApplyName; // 借款申请名称.
	private CreditUserApply creditUserApply; // 借款申请实体类.

	// 借款人姓名（用于Excel导出）.
	private String loanUserName;

	/**
	 * 新增借款方信息披露字段5个.
	 */
	private String businessFinancialSituation; // 经营财务情况.
	private String abilityToRepaySituation; // 还款能力情况.
	private String platformOverdueSituation; // 平台逾期情况.
	private String litigationSituation; // 涉诉情况.
	private String administrativePunishmentSituation; // 受行政处罚情况.
	/**
	 * 借款人基本信息.
	 */
	private ZtmgLoanBasicInfo ztmgLoanBasicInfo;
	
	/**
	 * 数据统计对象.
	 */
	private DataStatistics dataStatistics;

	private Double interestRateIncrease; // 利率增加（加息）.
	private String repaymentGuaranteeMeasures; // 还款保障措施.

	public Double getInterestRateIncrease() {

		return interestRateIncrease;
	}

	public void setInterestRateIncrease(Double interestRateIncrease) {

		this.interestRateIncrease = interestRateIncrease;
	}

	public WloanTermDoc getWloanTermDoc() {

		return wloanTermDoc;
	}

	public Integer getSpans() {

		return spans;
	}

	public void setSpans(Integer spans) {

		this.spans = spans;
	}

	public String getProjectType() {

		return projectType;
	}

	public void setProjectType(String projectType) {

		this.projectType = projectType;
	}
	

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public void setWloanTermDoc(WloanTermDoc wloanTermDoc) {

		this.wloanTermDoc = wloanTermDoc;
	}

	public WloanTermProject() {

		super();
	}

	public WloanTermProject(String id) {

		super(id);
	}

	public Area getArea() {

		return area;
	}

	public String getCreateById() {

		return createById;
	}

	public void setCreateById(String createById) {

		this.createById = createById;
	}

	public String getUpdateById() {

		return updateById;
	}

	public void setUpdateById(String updateById) {

		this.updateById = updateById;
	}

	public void setArea(Area area) {

		this.area = area;
	}

	@ExcelField(title = "项目编号", align = 2, sort = 1)
	public String getSn() {

		return sn;
	}

	public void setSn(String sn) {

		this.sn = sn;
	}

	public WGuaranteeCompany getWgCompany() {

		return wgCompany;
	}

	public void setWgCompany(WGuaranteeCompany wgCompany) {

		this.wgCompany = wgCompany;
	}

	public WloanSubject getWloanSubject() {

		return wloanSubject;
	}

	public void setWloanSubject(WloanSubject wloanSubject) {

		this.wloanSubject = wloanSubject;
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

	public String getGuaranteeId() {

		return guaranteeId;
	}

	public void setGuaranteeId(String guaranteeId) {

		this.guaranteeId = guaranteeId;
	}

	public String getDocId() {

		return docId;
	}

	public void setDocId(String docId) {

		this.docId = docId;
	}

	public String getLocus() {

		return locus;
	}

	public void setLocus(String locus) {

		this.locus = locus;
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

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@ExcelField(title = "放款日期", align = 2, sort = 29)
	public Date getRealLoanDate() {

		return realLoanDate;
	}

	public void setRealLoanDate(Date realLoanDate) {

		this.realLoanDate = realLoanDate;
	}

	public Integer getSpan() {

		return span;
	}
	
	public Date getBeginRealLoanDate() {

		return beginRealLoanDate;
	}

	public void setBeginRealLoanDate(Date beginRealLoanDate) {

		this.beginRealLoanDate = beginRealLoanDate;
	}
	
	public Date getEndRealLoanDate() {

		return endRealLoanDate;
	}

	public void setEndRealLoanDate(Date endRealLoanDate) {

		this.endRealLoanDate = endRealLoanDate;
	}

	@ExcelField(title = "融资期限(天)", align = 2, sort = 30)
	public String getExcelSpan() {

		return String.valueOf(span);
	}

	public void setSpan(Integer span) {

		this.span = span;
	}

	@Length(min = 0, max = 255, message = "还款方式1．一次性还本付息 2．分期付息到期还本长度必须介于 0 和 255 之间")
	public String getRepayType() {

		return repayType;
	}

	@ExcelField(title = "还款方式", align = 2, sort = 35)
	public String getExcelRepayType() {

		int flag = Integer.valueOf(repayType);
		String content = "";
		if (flag == 1) {
			content = "一次性还本付息";
		}
		if (flag == 2) {
			content = "分期付息到期还本";
		}
		return content;
	}

	public void setRepayType(String repayType) {

		this.repayType = repayType;
	}

	public Double getMinAmount() {

		return minAmount;
	}

	@ExcelField(title = "起投金额(元)", align = 2, sort = 40)
	public String getExcelMinAmount() {

		return String.valueOf(minAmount);
	}

	public void setMinAmount(Double minAmount) {

		this.minAmount = minAmount;
	}

	public Double getMaxAmount() {

		return maxAmount;
	}

	@ExcelField(title = "最大投资金额(元)", align = 2, sort = 45)
	public String getExcelMaxAmount() {

		return String.valueOf(maxAmount);
	}

	public void setMaxAmount(Double maxAmount) {

		this.maxAmount = maxAmount;
	}

	public Double getStepAmount() {

		return stepAmount;
	}

	public void setStepAmount(Double stepAmount) {

		this.stepAmount = stepAmount;
	}

	public Double getFeeRate() {

		return feeRate;
	}

	public void setFeeRate(Double feeRate) {

		this.feeRate = feeRate;
	}

	public Double getCurrentAmount() {

		return currentAmount;
	}

	@ExcelField(title = "实际融资金额(元)", align = 2, sort = 50)
	public String getExcelCurrentAmount() {

		return String.valueOf(currentAmount);
	}

	public void setCurrentAmount(Double currentAmount) {

		this.currentAmount = currentAmount;
	}

	public Double getCurrentRealAmount() {

		return currentRealAmount;
	}

	@ExcelField(title = "项目放款金额(元)", align = 2, sort = 55)
	public String getExcelCurrentRealAmount() {

		return String.valueOf(currentRealAmount);
	}

	public void setCurrentRealAmount(Double currentRealAmount) {

		this.currentRealAmount = currentRealAmount;
	}

	@Length(min = 0, max = 255, message = "资金用途长度必须介于 0 和 255 之间")
	public String getPurpose() {

		return purpose;
	}

	public void setPurpose(String purpose) {

		this.purpose = purpose;
	}

	public String getProjectCase() {

		return projectCase;
	}

	public void setProjectCase(String projectCase) {

		this.projectCase = projectCase;
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

	public String getImgUrl() {

		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {

		this.imgUrl = imgUrl;
	}

	public String getDetailImgUrl() {

		return detailImgUrl;
	}

	public void setDetailImgUrl(String detailImgUrl) {

		this.detailImgUrl = detailImgUrl;
	}

	public String getGuaranteeSn() {

		return guaranteeSn;
	}

	public void setGuaranteeSn(String guaranteeSn) {

		this.guaranteeSn = guaranteeSn;
	}

	public Double getMarginPercentage() {

		return marginPercentage;
	}

	public void setMarginPercentage(Double marginPercentage) {

		this.marginPercentage = marginPercentage;
	}

	public String getGuaranteeScheme() {

		return guaranteeScheme;
	}

	public void setGuaranteeScheme(String guaranteeScheme) {

		this.guaranteeScheme = guaranteeScheme;
	}

	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public List<String> getStateItem() {

		return stateItem;
	}

	public void setStateItem(List<String> stateItem) {

		this.stateItem = stateItem;
	}

	public List<String> getProjectTypeItem() {

		return projectTypeItem;
	}

	public void setProjectTypeItem(List<String> projectTypeItem) {

		this.projectTypeItem = projectTypeItem;
	}

	public Double getMinAnnualRate() {

		return minAnnualRate;
	}

	public void setMinAnnualRate(Double minAnnualRate) {

		this.minAnnualRate = minAnnualRate;
	}

	public Double getMaxAnnualRate() {

		return maxAnnualRate;
	}

	public void setMaxAnnualRate(Double maxAnnualRate) {

		this.maxAnnualRate = maxAnnualRate;
	}

	public String getContractUrl() {

		return contractUrl;
	}

	public void setContractUrl(String contractUrl) {

		this.contractUrl = contractUrl;
	}

	public Double getRealLoanAmount() {

		return realLoanAmount;
	}

	public void setRealLoanAmount(Double realLoanAmount) {

		this.realLoanAmount = realLoanAmount;
	}

	public Date getEndEndDate() {

		return endEndDate;
	}

	public void setEndEndDate(Date endEndDate) {

		this.endEndDate = endEndDate;
	}

	public String getIsCanUseCoupon() {

		return isCanUseCoupon;
	}

	public void setIsCanUseCoupon(String isCanUseCoupon) {

		this.isCanUseCoupon = isCanUseCoupon;
	}

	public String getIsCanUsePlusCoupon() {

		return isCanUsePlusCoupon;
	}

	public void setIsCanUsePlusCoupon(String isCanUsePlusCoupon) {

		this.isCanUsePlusCoupon = isCanUsePlusCoupon;
	}

	@ExcelField(title = "融资主体名称", align = 2, sort = 10)
	public String getWloanSubjectCompanyName() {

		return wloanSubject.getCompanyName();
	}

	@ExcelField(title = "借款户", align = 2, sort = 11)
	public String getWloanSubjectLoanPhone() {

		return wloanSubject.getLoanPhone();
	}

	@ExcelField(title = "借款人", align = 2, sort = 12)
	public String getLoanUserName() {

		return loanUserName;
	}

	public void setLoanUserName(String loanUserName) {

		this.loanUserName = loanUserName;
	}

	@ExcelField(title = "法人", align = 2, sort = 13)
	public String getWloanSubjectLoanUser() {

		return wloanSubject.getLoanUser();
	}

	public String getBeginTimeFromOnline() {

		return beginTimeFromOnline;
	}

	public void setBeginTimeFromOnline(String beginTimeFromOnline) {

		this.beginTimeFromOnline = beginTimeFromOnline;
	}

	public String getEndTimeToOnline() {

		return endTimeToOnline;
	}

	public void setEndTimeToOnline(String endTimeToOnline) {

		this.endTimeToOnline = endTimeToOnline;
	}

	public String getBeginTimeFromFull() {

		return beginTimeFromFull;
	}

	public void setBeginTimeFromFull(String beginTimeFromFull) {

		this.beginTimeFromFull = beginTimeFromFull;
	}

	public String getEndTimeToFull() {

		return endTimeToFull;
	}

	public void setEndTimeToFull(String endTimeToFull) {

		this.endTimeToFull = endTimeToFull;
	}

	public String getIsEntrustedPay() {

		return isEntrustedPay;
	}

	public void setIsEntrustedPay(String isEntrustedPay) {

		this.isEntrustedPay = isEntrustedPay;
	}

	public String getProjectRepayPlanType() {

		return projectRepayPlanType;
	}

	public void setProjectRepayPlanType(String projectRepayPlanType) {

		this.projectRepayPlanType = projectRepayPlanType;
	}

	public String getIsEntrustedWithdraw() {

		return isEntrustedWithdraw;
	}

	public void setIsEntrustedWithdraw(String isEntrustedWithdraw) {

		this.isEntrustedWithdraw = isEntrustedWithdraw;
	}

	public String getProjectDataId() {

		return projectDataId;
	}

	public void setProjectDataId(String projectDataId) {

		this.projectDataId = projectDataId;
	}

	public String getIsReplaceRepay() {

		return isReplaceRepay;
	}

	public void setIsReplaceRepay(String isReplaceRepay) {

		this.isReplaceRepay = isReplaceRepay;
	}

	public String getReplaceRepayId() {

		return replaceRepayId;
	}

	public void setReplaceRepayId(String replaceRepayId) {

		this.replaceRepayId = replaceRepayId;
	}

	public String getProjectProductType() {

		return projectProductType;
	}

	public void setProjectProductType(String projectProductType) {

		this.projectProductType = projectProductType;
	}

	public String getBorrowerResidence() {

		return borrowerResidence;
	}

	public void setBorrowerResidence(String borrowerResidence) {

		this.borrowerResidence = borrowerResidence;
	}

	public String getBorrowerElectronicSignUrl() {

		return borrowerElectronicSignUrl;
	}

	public void setBorrowerElectronicSignUrl(String borrowerElectronicSignUrl) {

		this.borrowerElectronicSignUrl = borrowerElectronicSignUrl;
	}

	public String getReplaceRepayResidence() {

		return replaceRepayResidence;
	}

	public void setReplaceRepayResidence(String replaceRepayResidence) {

		this.replaceRepayResidence = replaceRepayResidence;
	}

	public String getReplaceRepayElectronicSignUrl() {

		return replaceRepayElectronicSignUrl;
	}

	public void setReplaceRepayElectronicSignUrl(String replaceRepayElectronicSignUrl) {

		this.replaceRepayElectronicSignUrl = replaceRepayElectronicSignUrl;
	}

	public String getSourceOfRepayment() {

		return sourceOfRepayment;
	}

	public void setSourceOfRepayment(String sourceOfRepayment) {

		this.sourceOfRepayment = sourceOfRepayment;
	}

	public String getCreditUserApplyId() {

		return creditUserApplyId;
	}

	public void setCreditUserApplyId(String creditUserApplyId) {

		this.creditUserApplyId = creditUserApplyId;
	}

	public CreditUserApply getCreditUserApply() {

		return creditUserApply;
	}

	public void setCreditUserApply(CreditUserApply creditUserApply) {

		this.creditUserApply = creditUserApply;
	}

	public String getCreditUserApplyName() {

		return creditUserApplyName;
	}

	public void setCreditUserApplyName(String creditUserApplyName) {

		this.creditUserApplyName = creditUserApplyName;
	}

	public String getReplaceRepayName() {

		return replaceRepayName;
	}

	public void setReplaceRepayName(String replaceRepayName) {

		this.replaceRepayName = replaceRepayName;
	}

	public String getBusinessFinancialSituation() {

		return businessFinancialSituation;
	}

	public void setBusinessFinancialSituation(String businessFinancialSituation) {

		this.businessFinancialSituation = businessFinancialSituation;
	}

	public String getAbilityToRepaySituation() {

		return abilityToRepaySituation;
	}

	public void setAbilityToRepaySituation(String abilityToRepaySituation) {

		this.abilityToRepaySituation = abilityToRepaySituation;
	}

	public String getPlatformOverdueSituation() {

		return platformOverdueSituation;
	}

	public void setPlatformOverdueSituation(String platformOverdueSituation) {

		this.platformOverdueSituation = platformOverdueSituation;
	}

	public String getLitigationSituation() {

		return litigationSituation;
	}

	public void setLitigationSituation(String litigationSituation) {

		this.litigationSituation = litigationSituation;
	}

	public String getAdministrativePunishmentSituation() {

		return administrativePunishmentSituation;
	}

	public void setAdministrativePunishmentSituation(String administrativePunishmentSituation) {

		this.administrativePunishmentSituation = administrativePunishmentSituation;
	}

	public ZtmgLoanBasicInfo getZtmgLoanBasicInfo() {

		return ztmgLoanBasicInfo;
	}

	public void setZtmgLoanBasicInfo(ZtmgLoanBasicInfo ztmgLoanBasicInfo) {

		this.ztmgLoanBasicInfo = ztmgLoanBasicInfo;
	}

	public String getRepaymentGuaranteeMeasures() {

		return repaymentGuaranteeMeasures;
	}

	public void setRepaymentGuaranteeMeasures(String repaymentGuaranteeMeasures) {

		this.repaymentGuaranteeMeasures = repaymentGuaranteeMeasures;
	}

	public DataStatistics getDataStatistics() {
		return dataStatistics;
	}

	public void setDataStatistics(DataStatistics dataStatistics) {
		this.dataStatistics = dataStatistics;
	}
	
	
}