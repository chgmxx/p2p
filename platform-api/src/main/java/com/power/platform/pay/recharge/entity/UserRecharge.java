package com.power.platform.pay.recharge.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserInfo;

import javax.validation.constraints.NotNull;

/**
 * 类: UserRecharge <br>
 * 描述: 客户充值记录Entity. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年6月7日 上午10:13:59
 */
public class UserRecharge extends DataEntity<UserRecharge> {

	private static final long serialVersionUID = 1L;
	private String accountId; // account_id
	private String sn; // sn
	private String userId; // user_id
	private String bank; // bank
	private String bankAccount; // bank_account
	private Double amount; // amount
	private Double feeAmount; // fee_amount
	private String feeAccount; // fee_account
	private Date beginDate; // begin_date
	private String ip; // ip
	private Integer state; // state
	private Date endDate; // end_date
	private Integer platForm; // plat_form
	private Integer from; // from
	private Date beginBeginDate; // 开始 begin_date
	private Date endBeginDate; // 结束 begin_date
	private Date beginEndDate; // 开始 end_date
	private Date endEndDate; // 结束 end_date
	private UserInfo userInfo; // 客户信息.

	public String phone;// 客户手机号

	public static final Integer RECHARGE_INIT = 1;// 充值初始化
	public static final Integer RECHARGE_DOING = 2;// 充值申请中
	public static final Integer RECHARGE_SUCCESS = 3;// 充值成功
	public static final Integer RECHARGE_FAIL = 4;// 充值失败

	public static final Integer RECHARGE_GATEWAY = 0;// 网银充值
	public static final Integer RECHARGE_WEB = 1;// 认证支付
	public static final Integer RECHARGE_WAP = 2;// wap支付
	public static final Integer RECHARGE_ANDROID = 3;// 安卓充值
	public static final Integer RECHARGE_IOS = 4;// ios充值
	public static final Integer RECHARGE_LARGE = 5;// ios充值
	public static final Integer OFFLINE_RECHARGE = 6; // 转账充值（PC端WEB）.

	private String userTypeStr; // 用户类型（借款人/出借人）.
	private String formatAmountStr; // 格式化充值金额.
	private String creditUserId; // 借款人ID.

	public UserRecharge() {

		super();
	}

	public UserRecharge(String id) {

		super(id);
	}

	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	@Length(min = 1, max = 32, message = "account_id长度必须介于 1 和 32 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	@ExcelField(title = "手机号码", align = 2, sort = 10)
	private String getName() {

		return userInfo.getName();
	}

	@ExcelField(title = "姓名", align = 2, sort = 20)
	private String getRealName() {

		return userInfo.getRealName();
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
	@ExcelField(title = "银行卡号", align = 2, sort = 40)
	public String getBankAccount() {

		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {

		this.bankAccount = bankAccount;
	}

	public Double getAmount() {

		return amount;
	}

	public String getAmountStr() {

		return String.valueOf(amount);
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	public Double getFeeAmount() {

		return feeAmount;
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
	@ExcelField(title = "充值时间", align = 2, sort = 50)
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

	@ExcelField(title = "状态", align = 2, sort = 70)
	public String getStateStr() {

		String content = "";
		if (state == 1) {
			content = "初始化";
		} else if (state == 2) {
			content = "申请中";
		} else if (state == 3) {
			content = "成功";
		} else if (state == 4) {
			content = "失败";
		}
		return content;
	}

	@ExcelField(title = "备注", align = 2, sort = 80)
	public String getRemarksStr() {

		String content = "充值";
		return content;
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

	@Length(min = 1, max = 11, message = "plat_form长度必须介于 1 和 11 之间")
	public Integer getPlatForm() {

		return platForm;
	}

	public void setPlatForm(Integer platForm) {

		this.platForm = platForm;
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

	@ExcelField(title = "用户类别", align = 2, sort = 5)
	public String getUserTypeStr() {

		return userTypeStr;
	}

	public void setUserTypeStr(String userTypeStr) {

		this.userTypeStr = userTypeStr;
	}

	@ExcelField(title = "金额(元)", align = 2, sort = 60)
	public String getFormatAmountStr() {

		return formatAmountStr;
	}

	public void setFormatAmountStr(String formatAmountStr) {

		this.formatAmountStr = formatAmountStr;
	}

	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

}