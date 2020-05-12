package com.power.platform.userinfo.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;


/**
 * 订单对账Entity
 * @author yb
 * @version 2017-09-04
 */
public class UserCheckOrder extends DataEntity<UserCheckOrder> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 姓名
	private String phone;		// 手机号
	private String sn;		// 订单号
	private String amount;		// 金额
	private Date orderDate;		// 订单时间
	private String state;		// 订单状态
	private String remarks; //备注
	private String type;//交易类型
	
	public UserCheckOrder() {
		super();
	}

	public UserCheckOrder(String id){
		super(id);
	}

	@Length(min=0, max=64, message="姓名长度必须介于 0 和 64 之间")
	@ExcelField(title="姓名", align=2, sort=40)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=64, message="手机长度必须介于 1 和 64 之间")
	@ExcelField(title="手机号", align=2, sort=70)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=64, message="订单号长度必须介于 0 和 64 之间")
	@ExcelField(title="订单号", align=2, sort=80)
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@ExcelField(title="订单金额", align=2, sort=80)
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="订单时间", type=0, align=1, sort=90)
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	@Length(min=0, max=11, message="订单状态长度必须介于 0 和 11 之间")
	@ExcelField(title="订单状态", align=2, sort=80)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Length(min=0, max=255)
	@ExcelField(title="备注", align=1, sort=900)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Length(min=0, max=255)
	@ExcelField(title="交易类型", align=1, sort=900)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}