package com.power.platform.activity.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: ARateCouponDic <br>
 * 描述: 加息券字典数据Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月18日 上午9:53:36
 */
public class ARateCouponDic extends DataEntity<ARateCouponDic> {

	private static final long serialVersionUID = 1L;
	/**
	 * '1'：未使用，可以变更及删除，'2'：使用中，不可变更及删除
	 */
	private String state;
	/**
	 * 逾期天数(单位：天)
	 */
	private Integer overdueDays;
	/**
	 * 利率(加息)
	 */
	private Double rate;
	/**
	 * 起投金额(投资多少钱可使用的加息券)
	 */
	private Double limitAmount;

	public ARateCouponDic() {

		super();
	}

	public ARateCouponDic(String id) {

		super(id);
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	public Integer getOverdueDays() {

		return overdueDays;
	}

	public void setOverdueDays(Integer overdueDays) {

		this.overdueDays = overdueDays;
	}

	public Double getRate() {

		return rate;
	}

	public void setRate(Double rate) {

		this.rate = rate;
	}

	public Double getLimitAmount() {

		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {

		this.limitAmount = limitAmount;
	}

}