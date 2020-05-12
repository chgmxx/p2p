package com.power.platform.current.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 活期融资资金池Entity
 * @author Mr.Jia
 * @version 2016-01-12
 */
public class WloanCurrentPool extends DataEntity<WloanCurrentPool> {
	
	private static final long serialVersionUID = 1L;
	private String name;					// 产品名称
	private Double surplusAmount;			// 资金池剩余金额
	private Double amount;					// 资金池总金额
	private Double annualRate;				// 年化收益
	private Double minAmount;				// 最小金额
	private Double maxAmount;				// 最大金额
	private Double stepAmount;				// 递增金额
	private String remark;					// 备注
	
	public WloanCurrentPool() {
		super();
	}

	public WloanCurrentPool(String id){
		super(id);
	}

	@Length(min=0, max=255, message="产品名称长度必须介于 0 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Double getSurplusAmount() {
		return surplusAmount;
	}

	public void setSurplusAmount(Double surplusAmount) {
		this.surplusAmount = surplusAmount;
	}
	
	public Double getAnnualRate() {
		return annualRate;
	}

	public void setAnnualRate(Double annualRate) {
		this.annualRate = annualRate;
	}
	
	public Double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(Double minAmount) {
		this.minAmount = minAmount;
	}
	
	public Double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(Double maxAmount) {
		this.maxAmount = maxAmount;
	}
	
	public Double getStepAmount() {
		return stepAmount;
	}

	public void setStepAmount(Double stepAmount) {
		this.stepAmount = stepAmount;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
}