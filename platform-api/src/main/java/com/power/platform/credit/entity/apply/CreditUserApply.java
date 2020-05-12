package com.power.platform.credit.entity.apply;

import java.util.Date;
import java.util.List;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucherInfoDetail;
import com.power.platform.regular.entity.WloanSubject;

/**
 * 类: CreditUserApply <br>
 * 描述: 借款申请Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年12月15日 下午4:53:26
 */
public class CreditUserApply extends DataEntity<CreditUserApply> {

	private static final long serialVersionUID = 1L;
	// 代偿户ID（核心企业）.
	private String replaceUserId;
	// 借款资料ID.
	private String projectDataId;
	// 借款供应商ID.
	private String creditSupplyId;
	// 借款申请名称.
	private String creditApplyName;
	// 借款金额.
	private String amount;
	// 借款期限.
	private String span;
	// 年化率.
	private String lenderRate;
	// 借款用途.
	private String borrPurpose;
	// 状态.
	private String state;

	// 还款日
	private Date repayDate;

	/**
	 * 查询条件.
	 */
	// 创建日期，开始.
	private Date beginCreateDate;
	// 创建日期，结束.
	private Date endCreateDate;

	// 代偿户信息（核心企业）.
	private CreditUserInfo replaceUserInfo;
	// 借款资料信息.
	private CreditInfo projectDataInfo;

	/**
	 * 页面展示字段.
	 */
	// 借款人ID.
	private String loanUserId;
	// 借款人电话.
	private String loanUserPhone;
	// 借款人姓名.
	private String loanUserName;
	// 借款户企业名称.
	private String loanUserEnterpriseFullName;
	// 代偿户企业名称.
	private String replaceUserEnterpriseFullName;

	/**
	 * 过期字段.
	 */
	// 借款人.
	private String creditUserId;
	// 抵押物.
	private String collateralId;
	// 基本信息.
	private String basicId;
	// 借款人帐号.
	private CreditUserInfo creditUserInfo;
	// 项目上线日期.
	private Date onlineDate;

	private CreditPack creditPack;

	private CreditSupplierToMiddlemen creditSupplierToMiddlemen;// 中间表

	private CreditUserInfo supplyUser;// 供应商

	private CreditUserInfo creditUser;// 核心企业

	private List<String> stateItem; // 多状态查找参数

	// 订单融资增加字段
	private String financingType;// 融资类型
	private String financingStep;// 融资步骤
	private String modify;// 1到4步是否可以编辑 1不能编辑，只能查看
	private String financingConfirm;// 融资申请确认
	private String fileConfirm;// 核心企业提交材料

	private String shareRate;// 分摊比例

	private String voucherState;// 是否申请发票 1、申请中 2、申请通过 3、已过期

	private CreditVoucherInfoDetail creditVoucherInfoDetail;// 开票记录

	private Double sumFee;// 服务费总金额

	private String declarationFilePath; // 声明文件路径.

	private String isAuthorize; // 是否授权，TRUE/FALSE
	private String isNotice; // 是否通知，TRUE/FALSE
	private String shCisFilePath; // 授权在上海资信上传该次融资申请的相关资料，授权书
	private String zdFilePath; // 授权在中登网进行应收账款质押登记，授权书

	private String creditUserType; // 账户类型
	private CreditPack pack; // 合同信息
	private Date nowDate; // 当前时间
	private WloanSubject supplyLoanSubject; // 供应商融资主体

	public String getFinancingType() {

		return financingType;
	}

	public String getFinancingStep() {

		return financingStep;
	}

	public void setFinancingStep(String financingStep) {

		this.financingStep = financingStep;
	}

	public void setFinancingType(String financingType) {

		this.financingType = financingType;
	}

	public List<String> getStateItem() {

		return stateItem;
	}

	public void setStateItem(List<String> stateItem) {

		this.stateItem = stateItem;
	}

	public Date getRepayDate() {

		return repayDate;
	}

	public void setRepayDate(Date repayDate) {

		this.repayDate = repayDate;
	}

	public CreditUserApply() {

		super();
	}

	public CreditUserApply(String id) {

		super(id);
	}

	public String getCreditSupplyId() {

		return creditSupplyId;
	}

	public void setCreditSupplyId(String creditSupplyId) {

		this.creditSupplyId = creditSupplyId;
	}

	public String getReplaceUserId() {

		return replaceUserId;
	}

	public void setReplaceUserId(String replaceUserId) {

		this.replaceUserId = replaceUserId;
	}

	public String getProjectDataId() {

		return projectDataId;
	}

	public void setProjectDataId(String projectDataId) {

		this.projectDataId = projectDataId;
	}

	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

	public String getSpan() {

		return span;
	}

	public void setSpan(String span) {

		this.span = span;
	}

	public String getLenderRate() {

		return lenderRate;
	}

	public void setLenderRate(String lenderRate) {

		this.lenderRate = lenderRate;
	}

	public String getBorrPurpose() {

		return borrPurpose;
	}

	public void setBorrPurpose(String borrPurpose) {

		this.borrPurpose = borrPurpose;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public Date getBeginCreateDate() {

		return beginCreateDate;
	}

	public void setBeginCreateDate(Date beginCreateDate) {

		this.beginCreateDate = beginCreateDate;
	}

	public Date getEndCreateDate() {

		return endCreateDate;
	}

	public void setEndCreateDate(Date endCreateDate) {

		this.endCreateDate = endCreateDate;
	}

	public CreditUserInfo getReplaceUserInfo() {

		return replaceUserInfo;
	}

	public void setReplaceUserInfo(CreditUserInfo replaceUserInfo) {

		this.replaceUserInfo = replaceUserInfo;
	}

	public CreditInfo getProjectDataInfo() {

		return projectDataInfo;
	}

	public void setProjectDataInfo(CreditInfo projectDataInfo) {

		this.projectDataInfo = projectDataInfo;
	}

	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	public String getCollateralId() {

		return collateralId;
	}

	public void setCollateralId(String collateralId) {

		this.collateralId = collateralId;
	}

	public String getBasicId() {

		return basicId;
	}

	public void setBasicId(String basicId) {

		this.basicId = basicId;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

	public Date getOnlineDate() {

		return onlineDate;
	}

	public void setOnlineDate(Date onlineDate) {

		this.onlineDate = onlineDate;
	}

	public String getLoanUserId() {

		return loanUserId;
	}

	public void setLoanUserId(String loanUserId) {

		this.loanUserId = loanUserId;
	}

	public String getLoanUserPhone() {

		return loanUserPhone;
	}

	public void setLoanUserPhone(String loanUserPhone) {

		this.loanUserPhone = loanUserPhone;
	}

	public String getLoanUserName() {

		return loanUserName;
	}

	public void setLoanUserName(String loanUserName) {

		this.loanUserName = loanUserName;
	}

	public String getLoanUserEnterpriseFullName() {

		return loanUserEnterpriseFullName;
	}

	public void setLoanUserEnterpriseFullName(String loanUserEnterpriseFullName) {

		this.loanUserEnterpriseFullName = loanUserEnterpriseFullName;
	}

	public String getReplaceUserEnterpriseFullName() {

		return replaceUserEnterpriseFullName;
	}

	public void setReplaceUserEnterpriseFullName(String replaceUserEnterpriseFullName) {

		this.replaceUserEnterpriseFullName = replaceUserEnterpriseFullName;
	}

	public CreditPack getCreditPack() {

		return creditPack;
	}

	public void setCreditPack(CreditPack creditPack) {

		this.creditPack = creditPack;
	}

	public String getCreditApplyName() {

		return creditApplyName;
	}

	public void setCreditApplyName(String creditApplyName) {

		this.creditApplyName = creditApplyName;
	}

	public CreditSupplierToMiddlemen getCreditSupplierToMiddlemen() {

		return creditSupplierToMiddlemen;
	}

	public void setCreditSupplierToMiddlemen(CreditSupplierToMiddlemen creditSupplierToMiddlemen) {

		this.creditSupplierToMiddlemen = creditSupplierToMiddlemen;
	}

	public CreditUserInfo getSupplyUser() {

		return supplyUser;
	}

	public void setSupplyUser(CreditUserInfo supplyUser) {

		this.supplyUser = supplyUser;
	}

	public CreditUserInfo getCreditUser() {

		return creditUser;
	}

	public void setCreditUser(CreditUserInfo creditUser) {

		this.creditUser = creditUser;
	}

	public String getModify() {

		return modify;
	}

	public void setModify(String modify) {

		this.modify = modify;
	}

	public String getFinancingConfirm() {

		return financingConfirm;
	}

	public void setFinancingConfirm(String financingConfirm) {

		this.financingConfirm = financingConfirm;
	}

	public String getFileConfirm() {

		return fileConfirm;
	}

	public void setFileConfirm(String fileConfirm) {

		this.fileConfirm = fileConfirm;
	}

	public String getShareRate() {

		return shareRate;
	}

	public void setShareRate(String shareRate) {

		this.shareRate = shareRate;
	}

	public String getVoucherState() {

		return voucherState;
	}

	public void setVoucherState(String voucherState) {

		this.voucherState = voucherState;
	}

	public CreditVoucherInfoDetail getCreditVoucherInfoDetail() {

		return creditVoucherInfoDetail;
	}

	public void setCreditVoucherInfoDetail(CreditVoucherInfoDetail creditVoucherInfoDetail) {

		this.creditVoucherInfoDetail = creditVoucherInfoDetail;
	}

	public Double getSumFee() {

		return sumFee;
	}

	public void setSumFee(Double sumFee) {

		this.sumFee = sumFee;
	}

	/**
	 * 导出数据专用
	 */
	@ExcelField(title = "资料名称", align = 2, sort = 1)
	public String projectDataInfoName() {

		return projectDataInfo.getName();
	}

	@ExcelField(title = "企业名称", align = 2, sort = 2)
	public String creditUserEnterpriseFullName() {

		return creditUser.getEnterpriseFullName();
	}

	@ExcelField(title = "企业类型", align = 2, sort = 3)
	public String creditUserCreditUserType() {

		String type = creditUser.getCreditUserType();
		if ("02".equals(type)) {
			return "供应商";
		} else if ("11".equals(type)) {
			return "核心企业";
		}
		return "无";
	}

	@ExcelField(title = "服务费金额", align = 2, sort = 4)
	public String getSumFeeExport() {

		return sumFee != null ? sumFee.toString() : "";
	}

	@ExcelField(title = "申请时间", align = 2, sort = 5)
	public Date getExportCreateDate() {

		return this.getCreateDate();
	}

	@ExcelField(title = "修改时间", align = 2, sort = 6)
	public Date getExportUpdateDate() {

		return this.getUpdateDate();
	}

	@ExcelField(title = "抬头", align = 2, sort = 7)
	public String creditVoucherInfoDetailTitle() {

		return creditVoucherInfoDetail.getTitle();
	}

	@ExcelField(title = "税号", align = 2, sort = 8)
	public String creditVoucherInfoDetailNumber() {

		return creditVoucherInfoDetail.getNumber();
	}

	@ExcelField(title = "地址", align = 2, sort = 9)
	public String creditVoucherInfoDetailAddr() {

		return creditVoucherInfoDetail.getAddr();
	}

	@ExcelField(title = "电话", align = 2, sort = 10)
	public String creditVoucherInfoDetailPhone() {

		return creditVoucherInfoDetail.getPhone();
	}

	@ExcelField(title = "开户行", align = 2, sort = 11)
	public String creditVoucherInfoDetailBankName() {

		return creditVoucherInfoDetail.getBankName();
	}

	@ExcelField(title = "开户账号", align = 2, sort = 12)
	public String creditVoucherInfoDetailBankNo() {

		return creditVoucherInfoDetail.getBankNo();
	}

	@ExcelField(title = "发票收件人姓名", align = 2, sort = 13)
	public String creditVoucherInfoDetailToName() {

		return creditVoucherInfoDetail.getToName();
	}

	@ExcelField(title = "发票收件人电话", align = 2, sort = 14)
	public String creditVoucherInfoDetailToPhone() {

		return creditVoucherInfoDetail.getToPhone();
	}

	@ExcelField(title = "发票收件人地址", align = 2, sort = 14)
	public String creditVoucherInfoDetailToAddr() {

		return creditVoucherInfoDetail.getToAddr();
	}

	@ExcelField(title = "状态", align = 2, sort = 16)
	public String creditVoucherInfoDetailState() {

		String state = creditVoucherInfoDetail.getState();
		if ("1".equals(state)) {
			return "未开票";
		} else if ("2".equals(state)) {
			return "已开票";
		}
		return "无";
	}

	public String getDeclarationFilePath() {

		return declarationFilePath;
	}

	public void setDeclarationFilePath(String declarationFilePath) {

		this.declarationFilePath = declarationFilePath;
	}

	public String getIsAuthorize() {

		return isAuthorize;
	}

	public void setIsAuthorize(String isAuthorize) {

		this.isAuthorize = isAuthorize;
	}

	public String getIsNotice() {

		return isNotice;
	}

	public void setIsNotice(String isNotice) {

		this.isNotice = isNotice;
	}

	public String getShCisFilePath() {

		return shCisFilePath;
	}

	public void setShCisFilePath(String shCisFilePath) {

		this.shCisFilePath = shCisFilePath;
	}

	public String getZdFilePath() {

		return zdFilePath;
	}

	public void setZdFilePath(String zdFilePath) {

		this.zdFilePath = zdFilePath;
	}

	public String getCreditUserType() {

		return creditUserType;
	}

	public void setCreditUserType(String creditUserType) {

		this.creditUserType = creditUserType;
	}

	public CreditPack getPack() {

		return pack;
	}

	public void setPack(CreditPack pack) {

		this.pack = pack;
	}

	public Date getNowDate() {

		return nowDate;
	}

	public void setNowDate(Date nowDate) {

		this.nowDate = nowDate;
	}

	public WloanSubject getSupplyLoanSubject() {

		return supplyLoanSubject;
	}

	public void setSupplyLoanSubject(WloanSubject supplyLoanSubject) {

		this.supplyLoanSubject = supplyLoanSubject;
	}

}