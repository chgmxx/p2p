package com.power.platform.data.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;

/**
 * 类: LenderStatistics <br>
 * 描述: 出借端对象. <br>
 * 作者: Yangzf <br>
 * 时间: 2019年11月19日 上午11:52:19
 */
public class LenderStatistics extends DataEntity<LenderStatistics> {

	private static final long serialVersionUID = 1L;
	private String lenderId; // 出借人Id
	private String registrationTime; // 注册时间
	private String firstLendingTime; // 首次出借时间
	private String totalInterestReceived; // 累计已收利息
	private String annualizedRate; // 年化收益率
	
	private String gylBalance; // 供应链待收余额
	private String axtBalance; // 安心投待收余额
	private String totalBalance; // 合计待收余额
	
	private String lenderName;
	private String lenderPhone;
	
	public LenderStatistics() {

		super();
	}

	public LenderStatistics(String id) {

		super(id);
	}

	@ExcelField(title = "出借人Id", align = 2, sort = 5)
	public String getLenderId() {
		return lenderId;
	}

	public void setLenderId(String lenderId) {
		this.lenderId = lenderId;
	}

	@ExcelField(title = "注册时间", align = 2, sort = 10)
	public String getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(String registrationTime) {
		this.registrationTime = registrationTime;
	}

	@ExcelField(title = "首次出借时间", align = 2, sort = 15)
	public String getFirstLendingTime() {
		return firstLendingTime;
	}

	public void setFirstLendingTime(String firstLendingTime) {
		this.firstLendingTime = firstLendingTime;
	}

	@ExcelField(title = "累计已收利息", align = 2, sort = 20)
	public String getTotalInterestReceived() {
		return totalInterestReceived;
	}

	public void setTotalInterestReceived(String totalInterestReceived) {
		this.totalInterestReceived = totalInterestReceived;
	}

	@ExcelField(title = "年化收益率", align = 2, sort = 25)
	public String getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(String annualizedRate) {
		this.annualizedRate = annualizedRate;
	}
	@ExcelField(title = "供应链待收余额", align = 2, sort = 28)
	public String getGylBalance() {
		return gylBalance;
	}

	public void setGylBalance(String gylBalance) {
		this.gylBalance = gylBalance;
	}
	@ExcelField(title = "安心投待收余额", align = 2, sort = 30)
	public String getAxtBalance() {
		return axtBalance;
	}

	public void setAxtBalance(String axtBalance) {
		this.axtBalance = axtBalance;
	}
	@ExcelField(title = "合计待收余额", align = 2, sort = 35)
	public String getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(String totalBalance) {
		this.totalBalance = totalBalance;
	}
	@ExcelField(title = "出借人姓名", align = 2, sort = 6)
	public String getLenderName() {
		return lenderName;
	}

	public void setLenderName(String lenderName) {
		this.lenderName = lenderName;
	}
	@ExcelField(title = "出借人手机号", align = 2, sort = 7)
	public String getLenderPhone() {
		return lenderPhone;
	}

	public void setLenderPhone(String lenderPhone) {
		this.lenderPhone = lenderPhone;
	}

}