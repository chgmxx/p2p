/**
 * 银行托管-流水-Entity.
 */
package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.userinfo.entity.UserInfo;

import javax.validation.constraints.NotNull;

/**
 * 银行托管-流水-Entity.
 * 
 * @author lance
 * @version 2017-10-26
 */
public class CgbUserTransDetail extends DataEntity<CgbUserTransDetail> {

	private static final long serialVersionUID = 1L;
	public String transId; // trans_id
	private String accountId; // account_id
	private String userId; // user_id
	private Date transDate; // trans_date
	private Integer trustType; // trust_type
	private Double amount; // amount
	private String amountStr; // 金额.
	private Double avaliableAmount; // avaliable_amount
	private String avaliableAmountStr; // 可用余额.
	private Integer inOutType; // in_out_type
	private Integer state; // state
	private Date beginTransDate; // 开始 trans_date
	private Date endTransDate; // 结束 trans_date
	private String trustTypeStr; //
	private String stateStr; // 状态字符
	private List<Integer> transtypes; // 多个类型查找
	private String trustTypeName;

	private UserInfo userInfo;
	private CgbUserAccount userAccountInfo;
	private CreditUserInfo creditUserInfo;

	/**
	 * 交易流水单选按钮事件类型，1：出借人，2：借款人.
	 */
	private String transDetailRadioType;

	/**
	 * 1：出借人.
	 */
	public static final String TRANS_DETAIL_RADIO_TYPE_1 = "1";
	/**
	 * 2：借款人.
	 */
	public static final String TRANS_DETAIL_RADIO_TYPE_2 = "2";

	private List<Integer> states;

	public CgbUserTransDetail() {

		super();
	}

	public CgbUserTransDetail(String id) {

		super(id);
	}

	@ExcelField(title = "出借人帐号", align = 2, sort = 10)
	private String getUserName() {

		if (userInfo != null) {
			return userInfo.getName();
		}
		return "";
	}

	@ExcelField(title = "出借人姓名", align = 2, sort = 11)
	private String getUserRealName() {

		if (userInfo != null) {
			return userInfo.getRealName();
		}
		return "";
	}

	@ExcelField(title = "借款人帐号", align = 2, sort = 15)
	private String getCreditUserPhone() {

		if (creditUserInfo != null) {
			return creditUserInfo.getPhone();
		}
		return "";
	}

	@ExcelField(title = "借款人姓名", align = 2, sort = 16)
	private String getCreditUserName() {

		if (creditUserInfo != null) {
			return creditUserInfo.getName();
		}
		return "";
	}

	@Length(min = 0, max = 64, message = "交易id长度必须介于 0 和 64 之间")
	@ExcelField(title = "订单号", align = 2, sort = 5)
	public String getTransId() {

		return transId;
	}

	public void setTransId(String transId) {

		this.transId = transId;
	}

	@Length(min = 0, max = 64, message = "账户id长度必须介于 0 和 64 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	@Length(min = 1, max = 64, message = "用户id长度必须介于 1 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@NotNull(message = "交易日期不能为空")
	@ExcelField(title = "交易日期", align = 2, sort = 20)
	public Date getTransDate() {

		return transDate;
	}

	public void setTransDate(Date transDate) {

		this.transDate = transDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getBeginTransDate() {

		return beginTransDate;
	}

	public void setBeginTransDate(Date beginTransDate) {

		this.beginTransDate = beginTransDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndTransDate() {

		return endTransDate;
	}

	public void setEndTransDate(Date endTransDate) {

		this.endTransDate = endTransDate;
	}

	public Integer getTrustType() {

		return trustType;
	}

	public void setTrustType(Integer trustType) {

		this.trustType = trustType;
	}

	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public Double getAvaliableAmount() {

		return avaliableAmount;
	}

	public void setAvaliableAmount(Double avaliableAmount) {

		this.avaliableAmount = avaliableAmount;
	}

	public Integer getInOutType() {

		return inOutType;
	}

	public void setInOutType(Integer inOutType) {

		this.inOutType = inOutType;
	}

	public Integer getState() {

		return state;
	}

	public void setState(Integer state) {

		this.state = state;
	}

	public void setTrustTypeStr(String trustTypeStr) {

		this.trustTypeStr = trustTypeStr;
	}

	@ExcelField(title = "交易类型", align = 2, sort = 25)
	public String getTrustTypeStr() {

		return trustTypeStr;
	}

	@ExcelField(title = "收支类型", align = 2, sort = 30)
	public String getInOutTypeStr() {

		if (inOutType.equals(1)) {
			return "收入";
		} else if (inOutType.equals(2)) {
			return "支出";
		}
		return "";
	}

	@ExcelField(title = "状态", align = 2, sort = 45)
	public String getStateStr() {

		return stateStr;
	}

	@ExcelField(title = "备注", align = 2, sort = 50)
	public String getRmarksStr() {

		return remarks;
	}

	public void setStateStr(String stateStr) {

		this.stateStr = stateStr;
	}

	public List<Integer> getTranstypes() {

		return transtypes;
	}

	public void setTranstypes(List<Integer> transtypes) {

		this.transtypes = transtypes;
	}

	public String getTrustTypeName() {

		return trustTypeName;
	}

	public void setTrustTypeName(String trustTypeName) {

		this.trustTypeName = trustTypeName;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public CgbUserAccount getUserAccountInfo() {

		return userAccountInfo;
	}

	public void setUserAccountInfo(CgbUserAccount userAccountInfo) {

		this.userAccountInfo = userAccountInfo;
	}

	@ExcelField(title = "金额", align = 2, sort = 35)
	public String getAmountStr() {

		return amountStr;
	}

	public void setAmountStr(String amountStr) {

		this.amountStr = amountStr;
	}

	@ExcelField(title = "可用余额", align = 2, sort = 40)
	public String getAvaliableAmountStr() {

		return avaliableAmountStr;
	}

	public void setAvaliableAmountStr(String avaliableAmountStr) {

		this.avaliableAmountStr = avaliableAmountStr;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

	public String getTransDetailRadioType() {

		return transDetailRadioType;
	}

	public void setTransDetailRadioType(String transDetailRadioType) {

		this.transDetailRadioType = transDetailRadioType;
	}

	public List<Integer> getStates() {

		return states;
	}

	public void setStates(List<Integer> states) {

		this.states = states;
	}

}