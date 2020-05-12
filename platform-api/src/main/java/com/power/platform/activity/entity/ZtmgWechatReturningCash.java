package com.power.platform.activity.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: ZtmgWechatReturningCash <br>
 * 描述: 微信返现Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年9月8日 上午10:27:19
 */
public class ZtmgWechatReturningCash extends DataEntity<ZtmgWechatReturningCash> {

	private static final long serialVersionUID = 1L;
	private String user_id; // user_id
	private String accountId; // account_id
	private Double payAmount; // pay_amount
	private String mobilePhone; // mobile_phone
	private String realName; // real_name
	private Date createDate;
	private Date updateDate;
	private UserInfo userInfo; // 客户信息.
	private Date beginDate; // 开始日期.
	private Date endDate; // 结束日期.
	private String state;// 状态
	
	public static final String STATE_DONING = "0"; //处理中
	public static final String STATE_SUCCESS = "1"; //成功
	public static final String STATE_FAIL = "2"; //失败

	public ZtmgWechatReturningCash() {

		super();
	}

	public ZtmgWechatReturningCash(String id) {

		super(id);
	}

	@ExcelField(title = "ID", type = 1, align = 2, sort = 1)
	public String getId() {

		return id;
	}

	@Length(min = 0, max = 64, message = "user_id长度必须介于 0 和 64 之间")
	public String getUser_id() {

		return user_id;
	}

	public void setUser_id(String user_id) {

		this.user_id = user_id;
	}

	@Length(min = 0, max = 64, message = "account_id长度必须介于 0 和 64 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	public Double getPayAmount() {

		return payAmount;
	}

	@ExcelField(title = "金额(元)", align = 2, sort = 40)
	public String getPayAmountStr() {

		return String.valueOf(payAmount);
	}

	public void setPayAmount(Double amount) {

		this.payAmount = amount;
	}

	@Length(min = 0, max = 64, message = "mobile_phone长度必须介于 0 和 64 之间")
	@ExcelField(title = "用户名", align = 2, sort = 20)
	public String getMobilePhone() {

		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {

		this.mobilePhone = mobilePhone;
	}

	@Length(min = 0, max = 64, message = "real_name长度必须介于 0 和 64 之间")
	@ExcelField(title = "姓名", align = 2, sort = 30)
	public String getRealName() {

		return realName;
	}

	public void setRealName(String realName) {

		this.realName = realName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "充值时间", align = 2, sort = 50)
	public Date getCreateDate() {

		return createDate;
	}

	public void setCreateDate(Date createDate) {

		this.createDate = createDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDate() {

		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {

		this.updateDate = updateDate;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public Date getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;
	}

	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	@ExcelField(title = "备注", align = 2, sort = 60)
	public String getRemarks() {

		return "虚拟充值";
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	

}