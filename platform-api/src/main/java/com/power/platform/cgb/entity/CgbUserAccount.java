/**
 * 银行托管-账户-Entity.
 */
package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 银行托管-账户-Entity.
 * 
 * @author lance
 * @version 2017-10-26
 */
public class CgbUserAccount extends DataEntity<CgbUserAccount> {

	private static final long serialVersionUID = 1L;
	private String userId; // user_id
	private Double totalAmount; // 账户总额
	private String totalAmountStr; // 账户总额
	private Double availableAmount; // 可用金额
	private String availableAmountStr; // 可用金额
	private Double cashAmount; // 提现金额
	private String cashAmountStr; // 提现金额
	private Integer cashCount; // 提现总额
	private Double rechargeAmount; // 充值总额
	private String rechargeAmountStr; // 充值总额
	private Integer rechargeCount; // 充值总次数
	private Double freezeAmount; // 冻结金额
	private String freezeAmountStr; // 冻结金额
	private Double totalInterest; // 总收益
	private String totalInterestStr; // 总收益
	private Double currentAmount; // 活期投资金额
	private Double regularDuePrincipal; // 定期待收本金
	private String regularDuePrincipalStr; // 定期待收本金
	private Double regularDueInterest; // 定期待收收益
	private String regularDueInterestStr; // 定期待收收益
	private Double regularTotalAmount; // 定期投资总金额
	private String regularTotalAmountStr; // 定期投资总金额
	private Double regularTotalInterest; // 定期累计收益
	private String regularTotalInterestStr; // 定期累计收益
	private Double currentTotalInterest; // 活期总收益
	private Double currentTotalAmount; // 活期累计投资金额
	private Double currentYesterdayInterest; // 活期昨日收益
	private Double reguarYesterdayInterest; // 定期昨日收益
	private String reguarYesterdayInterestStr; // 定期昨日收益
	private Double commission; // 客户总佣金
	private String commissionStr; // 客户总佣金

	private UserInfo userInfo; // 用户
	
	private String canUseAmount; //是否有可用余额

	public CgbUserAccount() {

		super();
	}

	public CgbUserAccount(String id) {

		super(id);
	}

	@Length(min = 1, max = 64, message = "user_id长度必须介于 1 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}
	
	
	/**
	 * 导出专用
	 */
	@ExcelField(title = "姓名", align = 2, sort = 10)
	public String getUserName() {
		return userInfo.getRealName();
	}
	
	@ExcelField(title = "手机号", align = 2, sort = 10)
	public String getUserPhone() {
		return userInfo.getName();
	}

	@ExcelField(title = "账户总额", align = 2, sort = 10)
	public Double getTotalAmount() {

		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {

		this.totalAmount = totalAmount;
	}

	@ExcelField(title = "可用余额", align = 2, sort = 10)
	public Double getAvailableAmount() {

		return availableAmount;
	}

	public void setAvailableAmount(Double availableAmount) {

		this.availableAmount = availableAmount;
	}

	@ExcelField(title = "提现总额", align = 2, sort = 10)
	public Double getCashAmount() {

		return cashAmount;
	}

	public void setCashAmount(Double cashAmount) {

		this.cashAmount = cashAmount;
	}

	public Integer getCashCount() {

		return cashCount;
	}

	public void setCashCount(Integer cashCount) {

		this.cashCount = cashCount;
	}

	@ExcelField(title = "充值总额", align = 2, sort = 10)
	public Double getRechargeAmount() {

		return rechargeAmount;
	}

	public void setRechargeAmount(Double rechargeAmount) {

		this.rechargeAmount = rechargeAmount;
	}

	public Integer getRechargeCount() {

		return rechargeCount;
	}

	public void setRechargeCount(Integer rechargeCount) {

		this.rechargeCount = rechargeCount;
	}

	@ExcelField(title = "冻结金额", align = 2, sort = 10)
	public Double getFreezeAmount() {

		return freezeAmount;
	}

	public void setFreezeAmount(Double freezeAmount) {

		this.freezeAmount = freezeAmount;
	}

	public Double getTotalInterest() {

		return totalInterest;
	}

	public void setTotalInterest(Double totalInterest) {

		this.totalInterest = totalInterest;
	}

	public Double getCurrentAmount() {

		return currentAmount;
	}

	public void setCurrentAmount(Double currentAmount) {

		this.currentAmount = currentAmount;
	}

	@ExcelField(title = "定期待收本金", align = 2, sort = 10)
	public Double getRegularDuePrincipal() {

		return regularDuePrincipal;
	}

	public void setRegularDuePrincipal(Double regularDuePrincipal) {

		this.regularDuePrincipal = regularDuePrincipal;
	}

	@ExcelField(title = "定期待收收益", align = 2, sort = 10)
	public Double getRegularDueInterest() {

		return regularDueInterest;
	}

	public void setRegularDueInterest(Double regularDueInterest) {

		this.regularDueInterest = regularDueInterest;
	}

	@ExcelField(title = "定期投资总额", align = 2, sort = 10)
	public Double getRegularTotalAmount() {

		return regularTotalAmount;
	}

	public void setRegularTotalAmount(Double regularTotalAmount) {

		this.regularTotalAmount = regularTotalAmount;
	}

	@ExcelField(title = "定期累计收益", align = 2, sort = 10)
	public Double getRegularTotalInterest() {

		return regularTotalInterest;
	}

	public void setRegularTotalInterest(Double regularTotalInterest) {

		this.regularTotalInterest = regularTotalInterest;
	}

	public Double getCurrentTotalInterest() {

		return currentTotalInterest;
	}

	public void setCurrentTotalInterest(Double currentTotalInterest) {

		this.currentTotalInterest = currentTotalInterest;
	}

	public Double getCurrentTotalAmount() {

		return currentTotalAmount;
	}

	public void setCurrentTotalAmount(Double currentTotalAmount) {

		this.currentTotalAmount = currentTotalAmount;
	}

	public Double getCurrentYesterdayInterest() {

		return currentYesterdayInterest;
	}

	public void setCurrentYesterdayInterest(Double currentYesterdayInterest) {

		this.currentYesterdayInterest = currentYesterdayInterest;
	}

	public Double getReguarYesterdayInterest() {

		return reguarYesterdayInterest;
	}

	public void setReguarYesterdayInterest(Double reguarYesterdayInterest) {

		this.reguarYesterdayInterest = reguarYesterdayInterest;
	}

	public Double getCommission() {

		return commission;
	}

	public void setCommission(Double commission) {

		this.commission = commission;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public String getTotalAmountStr() {

		return totalAmountStr;
	}

	public void setTotalAmountStr(String totalAmountStr) {

		this.totalAmountStr = totalAmountStr;
	}

	public String getAvailableAmountStr() {

		return availableAmountStr;
	}

	public void setAvailableAmountStr(String availableAmountStr) {

		this.availableAmountStr = availableAmountStr;
	}

	public String getCashAmountStr() {

		return cashAmountStr;
	}

	public void setCashAmountStr(String cashAmountStr) {

		this.cashAmountStr = cashAmountStr;
	}

	public String getRechargeAmountStr() {

		return rechargeAmountStr;
	}

	public void setRechargeAmountStr(String rechargeAmountStr) {

		this.rechargeAmountStr = rechargeAmountStr;
	}

	public String getFreezeAmountStr() {

		return freezeAmountStr;
	}

	public void setFreezeAmountStr(String freezeAmountStr) {

		this.freezeAmountStr = freezeAmountStr;
	}

	public String getTotalInterestStr() {

		return totalInterestStr;
	}

	public void setTotalInterestStr(String totalInterestStr) {

		this.totalInterestStr = totalInterestStr;
	}

	public String getRegularDuePrincipalStr() {

		return regularDuePrincipalStr;
	}

	public void setRegularDuePrincipalStr(String regularDuePrincipalStr) {

		this.regularDuePrincipalStr = regularDuePrincipalStr;
	}

	public String getRegularDueInterestStr() {

		return regularDueInterestStr;
	}

	public void setRegularDueInterestStr(String regularDueInterestStr) {

		this.regularDueInterestStr = regularDueInterestStr;
	}

	public String getRegularTotalAmountStr() {

		return regularTotalAmountStr;
	}

	public void setRegularTotalAmountStr(String regularTotalAmountStr) {

		this.regularTotalAmountStr = regularTotalAmountStr;
	}

	public String getRegularTotalInterestStr() {

		return regularTotalInterestStr;
	}

	public void setRegularTotalInterestStr(String regularTotalInterestStr) {

		this.regularTotalInterestStr = regularTotalInterestStr;
	}

	public String getReguarYesterdayInterestStr() {

		return reguarYesterdayInterestStr;
	}

	public void setReguarYesterdayInterestStr(String reguarYesterdayInterestStr) {

		this.reguarYesterdayInterestStr = reguarYesterdayInterestStr;
	}

	public String getCommissionStr() {

		return commissionStr;
	}

	public void setCommissionStr(String commissionStr) {

		this.commissionStr = commissionStr;
	}

	public String getCanUseAmount() {
		return canUseAmount;
	}

	public void setCanUseAmount(String canUseAmount) {
		this.canUseAmount = canUseAmount;
	}

	
}