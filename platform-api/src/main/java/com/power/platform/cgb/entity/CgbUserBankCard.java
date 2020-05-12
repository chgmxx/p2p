/**
 * 银行托管-银行卡-Entity.
 */
package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.userinfo.entity.UserInfo;

import javax.validation.constraints.NotNull;

/**
 * 银行托管-银行卡-Entity.
 * 
 * @author lance
 * @version 2017-10-26
 */
public class CgbUserBankCard extends DataEntity<CgbUserBankCard> {

	private static final long serialVersionUID = 1L;
	private String bankAccountNo; // 银行卡号码
	private String userId; // 客户账号ID
	private String accountId; // 客户账户ID
	private String bankNo; // 银行代码
	private Date bindDate; // 绑卡时间
	private String state; // 状态：0：未认证，1：已认证
	private String isDefault; // 是否默认银行卡：1：默认
	private String bankCardPhone; // 银行预留手机
	private String bankName; // 银行名称
	private Date beginBindDate; // 开始 绑卡时间
	private Date endBindDate; // 结束 绑卡时间

	private UserInfo userInfo; // 平台投资用户.
	private CreditUserInfo creditUserInfo; // 借款用户.

	private String bankCardRadioType; // 1：出借人，2：借款人.

	public static final String CERTIFY_UPDATE_FAIL = "3"; // 认证失败（已开户）
	public static final String CERTIFY_FAIL = "2"; // 认证失败（未开户）
	/**
	 * 懒猫，1：已绑卡.（包含了个人/企业已绑卡注册，二次个人/企业绑卡成功）
	 */
	public static final String CERTIFY_YES = "1"; // 已绑卡.
	/**
	 * 懒猫，0：未绑卡（包含了个人/企业未绑卡注册，银行卡解绑成功等）
	 */
	public static final String CERTIFY_NO = "0"; // 未绑卡.

	/**
	 * 出借人.
	 */
	public static final String BANK_CARD_RADIO_TYPE_1 = "1";
	/**
	 * 借款人.
	 */
	public static final String BANK_CARD_RADIO_TYPE_2 = "2";

	private String bankCardSign; // 银行卡密文保存

	public CgbUserBankCard() {

		super();
	}

	public CgbUserBankCard(String id) {

		super(id);
	}

	@Length(min = 1, max = 50, message = "银行卡号码长度必须介于 1 和 50 之间")
	public String getBankAccountNo() {

		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {

		this.bankAccountNo = bankAccountNo;
	}

	@Length(min = 1, max = 64, message = "客户账号ID长度必须介于 1 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 1, max = 64, message = "客户账户ID长度必须介于 1 和 64 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	@Length(min = 0, max = 10, message = "银行代码长度必须介于 0 和 10 之间")
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

	@Length(min = 0, max = 1, message = "状态：0：未认证，1：已认证长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 1, message = "是否默认银行卡：1：默认长度必须介于 0 和 1 之间")
	public String getIsDefault() {

		return isDefault;
	}

	public void setIsDefault(String isDefault) {

		this.isDefault = isDefault;
	}

	@Length(min = 0, max = 11, message = "银行预留手机长度必须介于 0 和 11 之间")
	public String getBankCardPhone() {

		return bankCardPhone;
	}

	public void setBankCardPhone(String bankCardPhone) {

		this.bankCardPhone = bankCardPhone;
	}

	@Length(min = 0, max = 255, message = "银行名称长度必须介于 0 和 255 之间")
	public String getBankName() {

		return bankName;
	}

	public void setBankName(String bankName) {

		this.bankName = bankName;
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

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

	public String getBankCardRadioType() {

		return bankCardRadioType;
	}

	public void setBankCardRadioType(String bankCardRadioType) {

		this.bankCardRadioType = bankCardRadioType;
	}

	public String getBankCardSign() {

		return bankCardSign;
	}

	public void setBankCardSign(String bankCardSign) {

		this.bankCardSign = bankCardSign;
	}

}