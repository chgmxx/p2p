package com.power.platform.activity.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.power.platform.activity.pojo.Span;
import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: AVouchersDic <br>
 * 描述: 抵用券/代金券字典数据Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月19日 上午9:25:21
 */
public class AVouchersDic extends DataEntity<AVouchersDic> {

	private static final long serialVersionUID = 1L;
	/**
	 * 状态：'1'：未使用，可以变更及删除，'2'：使用中，不可变更及删除
	 */
	private String state;
	/**
	 * 逾期天数(单位：天)
	 */
	private Integer overdueDays;
	/**
	 * 代金券/抵用券的金额
	 */
	private Double amount;
	/**
	 * 抵用券金额，用于展示.
	 */
	private String amountStr;
	/**
	 * 起投金额(投资多少钱可使用的代金券)
	 */
	private Double limitAmount;
	/**
	 * 起投金额，用于展示.
	 */
	private String limitAmountStr;

	public String getAmountStr() {

		return amountStr;
	}

	public void setAmountStr(String amountStr) {

		this.amountStr = amountStr;
	}

	public String getLimitAmountStr() {

		return limitAmountStr;
	}

	public void setLimitAmountStr(String limitAmountStr) {

		this.limitAmountStr = limitAmountStr;
	}

	/**
	 * 项目期限集合.
	 */
	private String spans;
	/**
	 * 拥有期限列表.
	 */
	private List<Span> spanList = Lists.newArrayList();

	public AVouchersDic() {

		super();
	}

	public AVouchersDic(String id) {

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

	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
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

	public List<Span> getSpanList() {

		return spanList;
	}

	public void setSpanList(List<Span> spanList) {

		this.spanList = spanList;
	}

	@JsonIgnore
	public List<String> getSpanIdList() {

		List<String> spanIdList = Lists.newArrayList();
		for (Span span : spanList) {
			spanIdList.add(span.getId());
		}
		return spanIdList;
	}

	public void setSpanIdList(List<String> spanIdList) {

		spanList = Lists.newArrayList();
		for (String spanId : spanIdList) {
			Span span = new Span();
			span.setId(spanId);
			spanList.add(span);
		}
	}

}