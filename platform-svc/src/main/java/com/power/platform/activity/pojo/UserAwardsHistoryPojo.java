package com.power.platform.activity.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * 类: UserAwardsHistoryPojo <br>
 * 描述: 客户优惠券奖励历史POJO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年5月10日 下午10:02:58
 */
public class UserAwardsHistoryPojo {

	/**
	 * ID.ime 
	 */
	private String id;
	/**
	 * 获取日期.
	 */
	private Date getDate;
	/**
	 * 过期日期.
	 */
	private Date overdueDate;
	/**
	 * 状态(客户奖励状态)：1：可用，未使用，2：已使用，3：逾期的，过期的.
	 */
	private String state;
	/**
	 * 奖励类型：1：抵用券/代金券，2：加息券.
	 */
	private String type;
	/**
	 * 加息券息率.
	 */
	private Double value;
	/**
	 * 起投金额(投资多少钱可使用的代金券)
	 */
	private Double limitAmount;
	
	/**
	 *抵用券期限使用范围
	 */
	private String spans;

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getGetDate() {

		return getDate;
	}

	public void setGetDate(Date getDate) {

		this.getDate = getDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getOverdueDate() {

		return overdueDate;
	}

	public void setOverdueDate(Date overdueDate) {

		this.overdueDate = overdueDate;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public Double getValue() {

		return value;
	}

	public void setValue(Double value) {

		this.value = value;
	}

	public Double getLimitAmount() {

		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {

		this.limitAmount = limitAmount;
	}

	public String getSpans() {
		return spans;
	}

	public void setSpans(String spans) {
		this.spans = spans;
	}

}
