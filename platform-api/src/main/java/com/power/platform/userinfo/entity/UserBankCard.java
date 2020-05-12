package com.power.platform.userinfo.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

import javax.validation.constraints.NotNull;

/**
 * 
 * class: UserBankCard <br>
 * description: 连连客户银行卡信息. <br>
 * author: Mr.Roy <br>
 * date: 2018年12月11日 下午3:44:38
 */
public class UserBankCard extends DataEntity<UserBankCard> {

	private static final long serialVersionUID = 1L;
	private String bankAccountNo; // 银行卡号码
	private String userId; // 客户账号ID
	private String accountId; // 客户账户ID
	private String bankNo; // 银行代码
	private Date bindDate; // 绑卡时间
	private String state; // 状态：0：未认证，1：已认证， 4：审核中【银行审核】
	private String isDefault; // 是否默认银行卡：1：默认，2：已变更
	private Date beginBindDate; // 开始 绑卡时间
	private Date endBindDate; // 结束 绑卡时间
	private String delFlag; // 删除标记

	public static final String DEFAULT_YES = "2";// 是默认卡
	public static final String DEFAULT_NO = "1";// 不是默认卡

	//state
	public static final String CERTIFY_YES = "1"; // 已认证（已开户）
	public static final String CERTIFY_NO = "0"; // 未认证（未开户）
	public static final String IN_AUDIT = "4"; // 银行审核中（未开户）
	
	private UserInfo userInfo; // 客户信息.
	private String bankCardPhone;// 银行预留手机号
	private String bankName; // 银行名称
	private CreditUserInfo creditUserInfo;// 借款用户

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public UserBankCard() {

		super();
	}

	public UserBankCard(String id) {

		super(id);
	}

	@Length(min = 1, max = 50, message = "银行卡号码长度必须介于 1 和 50 之间")
	public String getBankAccountNo() {

		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {

		this.bankAccountNo = bankAccountNo;
	}

	@Length(min = 1, max = 32, message = "客户账号ID长度必须介于 1 和 32 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 1, max = 32, message = "客户账户ID长度必须介于 1 和 32 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	@Length(min = 1, max = 10, message = "银行代码长度必须介于 1 和 10 之间")
	public String getBankNo() {

		return bankNo;
	}

	public void setBankNo(String bankNo) {

		this.bankNo = bankNo;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message = "绑卡时间不能为空")
	public Date getBindDate() {

		return bindDate;
	}

	public void setBindDate(Date bindDate) {

		this.bindDate = bindDate;
	}

	/**
	 * 返回: the state <br>
	 * 
	 * 时间: 2015年12月19日 下午12:01:24
	 */
	public String getState() {

		return state;
	}

	/**
	 * 参数: state the state to set <br>
	 * 
	 * 时间: 2015年12月19日 下午12:01:24
	 */
	public void setState(String state) {

		this.state = state;
	}

	/**
	 * 返回: the isDefault <br>
	 * 
	 * 时间: 2015年12月19日 下午12:01:24
	 */
	public String getIsDefault() {

		return isDefault;
	}

	/**
	 * 参数: isDefault the isDefault to set <br>
	 * 
	 * 时间: 2015年12月19日 下午12:01:24
	 */
	public void setIsDefault(String isDefault) {

		this.isDefault = isDefault;
	}

	public Date getBeginBindDate() {

		return beginBindDate;
	}

	public void setBeginBindDate(Date beginBindDate) {

		this.beginBindDate = beginBindDate;
	}

	public Date getEndBindDate() {

		return endBindDate;
	}

	public void setEndBindDate(Date endBindDate) {

		this.endBindDate = endBindDate;
	}

	/**
	 * 返回: the delFlag <br>
	 * 
	 * 时间: 2015年12月19日 下午12:12:28
	 */
	public String getDelFlag() {

		return delFlag;
	}

	/**
	 * 参数: delFlag the delFlag to set <br>
	 * 
	 * 时间: 2015年12月19日 下午12:12:28
	 */
	public void setDelFlag(String delFlag) {

		this.delFlag = delFlag;
	}

	public String getBankCardPhone() {

		return bankCardPhone;
	}

	public void setBankCardPhone(String bankCardPhone) {

		this.bankCardPhone = bankCardPhone;
	}

	public String getBankName() {

		return bankName;
	}

	public void setBankName(String bankName) {

		this.bankName = bankName;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

}