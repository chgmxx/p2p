package com.power.platform.userinfo.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;


/**
 * 客户账户对账Entity
 * @author soler
 * @version 2016-06-23
 */
public class UserCheckAccount extends DataEntity<UserCheckAccount> {
	
	private static final long serialVersionUID = 1L;
	private String phone;		// 手机
	private String realName;		// 姓名
	private Double accountAmount;		// 账户可用余额
	private Double checkAmount;		// 检查可用余额
	private String remark;          //备注信息
	
	public UserCheckAccount() {
		super();
	}

	public UserCheckAccount(String id){
		super(id);
	}

	@Length(min=1, max=64, message="手机长度必须介于 1 和 64 之间")
	@ExcelField(title="手机号", align=2, sort=70)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=64, message="姓名长度必须介于 0 和 64 之间")
	@ExcelField(title="姓名", align=2, sort=40)
	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	@ExcelField(title="账户资产", align=2, sort=80)
	public Double getAccountAmount() {
		return accountAmount;
	}

	public void setAccountAmount(Double accountAmount) {
		this.accountAmount = accountAmount;
	}
	
	@ExcelField(title="对账资产", align=2, sort=90)
	public Double getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(Double checkAmount) {
		this.checkAmount = checkAmount;
	}

	@ExcelField(title="对账内容", align=2, sort=60)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}