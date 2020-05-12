package com.power.platform.pay.cash.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserInfo;

import javax.validation.constraints.NotNull;

/**
 * 客户提现记录Entity
 * 
 * @author soler
 * @version 2015-12-23
 */
public class UserCash extends DataEntity<UserCash> {

	private static final long serialVersionUID = 1L;
	private String accountId; // 账户主键
	private String sn; // 流水号
	private String userId; // 账号id
	private String bank; // 银行编码
	private String bankAccount; // 银行卡号
	private Double amount; // 提现金额
	private Double feeAmount; // 手续费
	private String feeAccount; // 手续费扣除账户
	private Date beginDate; // 开始时间
	private String ip; // 发起订单ip
	private Integer state; // 提现状态
	private Date endDate; // 提现完成日期
	private Integer from; // 访问来源
	private String brabankName; // 开户支行
	private String cityCode; // 开户城市
	private Date beginBeginDate; // 开始 begin_date
	private Date endBeginDate; // 结束 begin_date
	private Date beginEndDate; // 开始 end_date
	private Date endEndDate; // 结束 end_date
	private UserInfo userInfo; // 客户信息.

	public static final Integer CASH_APPLY = 0;// 预提现订单 
	public static final Integer CASH_INIT = 1;// 提现处理中
	public static final Integer CASH_DOING = 2;// 提交提现订单成功
	public static final Integer CASH_VERIFY_FAIL = 3;// 审核失败
	public static final Integer CASH_SUCCESS = 4;// 到账成功
	public static final Integer CASH_FAIL = 5;// 到账失败
	public static final Integer CASH_FREEZE = 6; // ，这时冻结了提现金额

	public UserCash() {

		super();
	}

	public UserCash(String id) {

		super(id);
	}

	@ExcelField(title = "用户名", align = 2, sort = 10)
	private String getName() {

		return userInfo.getName();
	}

	@ExcelField(title = "姓名", align = 2, sort = 20)
	private String getRealName() {

		return userInfo.getRealName();
	}

	public String getBrabankName() {

		return brabankName;
	}

	public void setBrabankName(String brabankName) {

		this.brabankName = brabankName;
	}

	public String getCityCode() {

		return cityCode;
	}

	public void setCityCode(String cityCode) {

		this.cityCode = cityCode;
	}

	@Length(min = 1, max = 32, message = "account_id长度必须介于 1 和 32 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	@Length(min = 1, max = 32, message = "sn长度必须介于 1 和 32 之间")
	@ExcelField(title = "订单号", align = 2, sort = 30)
	public String getSn() {

		return sn;
	}

	public void setSn(String sn) {

		this.sn = sn;
	}

	@Length(min = 1, max = 20, message = "user_id长度必须介于 1 和 20 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 255, message = "bank长度必须介于 0 和 255 之间")
	public String getBank() {

		return bank;
	}

	public void setBank(String bank) {

		this.bank = bank;
	}

	@Length(min = 1, max = 255, message = "bank_account长度必须介于 1 和 255 之间")
	public String getBankAccount() {

		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {

		this.bankAccount = bankAccount;
	}

	public Double getAmount() {

		return amount;
	}

	@ExcelField(title = "金额(元)", align = 2, sort = 50)
	public String getAmountStr() {

		return String.valueOf(amount);
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public Double getFeeAmount() {

		return feeAmount;
	}

	@ExcelField(title = "手续费(元)", align = 2, sort = 60)
	public String getFeeAmountStr() {

		return String.valueOf(feeAmount);
	}

	public void setFeeAmount(Double feeAmount) {

		this.feeAmount = feeAmount;
	}

	@Length(min = 0, max = 255, message = "fee_account长度必须介于 0 和 255 之间")
	public String getFeeAccount() {

		return feeAccount;
	}

	public void setFeeAccount(String feeAccount) {

		this.feeAccount = feeAccount;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message = "begin_date不能为空")
	@ExcelField(title = "提现时间", align = 2, sort = 40)
	public Date getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;
	}

	@Length(min = 0, max = 255, message = "ip长度必须介于 0 和 255 之间")
	public String getIp() {

		return ip;
	}

	public void setIp(String ip) {

		this.ip = ip;
	}

	@Length(min = 1, max = 11, message = "state长度必须介于 1 和 11 之间")
	public Integer getState() {

		return state;
	}

	public void setState(Integer state) {

		this.state = state;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message = "end_date不能为空")
	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	@Length(min = 1, max = 2, message = "from长度必须介于 1 和 2 之间")
	public Integer getFrom() {

		return from;
	}

	public void setFrom(Integer from) {

		this.from = from;
	}

	public Date getBeginBeginDate() {

		return beginBeginDate;
	}

	public void setBeginBeginDate(Date beginBeginDate) {

		this.beginBeginDate = beginBeginDate;
	}

	public Date getEndBeginDate() {

		return endBeginDate;
	}

	public void setEndBeginDate(Date endBeginDate) {

		this.endBeginDate = endBeginDate;
	}

	public Date getBeginEndDate() {

		return beginEndDate;
	}

	public void setBeginEndDate(Date beginEndDate) {

		this.beginEndDate = beginEndDate;
	}

	public Date getEndEndDate() {

		return endEndDate;
	}

	public void setEndEndDate(Date endEndDate) {

		this.endEndDate = endEndDate;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	@ExcelField(title = "手续费(元)", align = 2, sort = 60)
	public String getRemarks() {

		return "提现";
	}

}