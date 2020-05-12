/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.creditOrder;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;


/**
 * 订单信息Entity
 * @author jice
 * @version 2018-05-23
 */
public class CreditOrder extends DataEntity<CreditOrder> {
	
	private static final long serialVersionUID = 1L;
	private String annexId;		// 附件id
	private String creditInfoId;		// 信息id
	private String packNo;		// 合同编号
	private String no;		// 订单编号
	private String money;		// 订单金额
	private String url;			//订单图片地址
	
	public CreditOrder() {
		super();
	}

	public CreditOrder(String id){
		super(id);
	}

	@Length(min=0, max=64, message="附件id长度必须介于 0 和 64 之间")
	public String getAnnexId() {
		return annexId;
	}

	public void setAnnexId(String annexId) {
		this.annexId = annexId;
	}
	
	@Length(min=0, max=64, message="信息id长度必须介于 0 和 64 之间")
	public String getCreditInfoId() {
		return creditInfoId;
	}

	public void setCreditInfoId(String creditInfoId) {
		this.creditInfoId = creditInfoId;
	}
	
	@Length(min=0, max=255, message="合同编号长度必须介于 0 和 255 之间")
	public String getPackNo() {
		return packNo;
	}

	public void setPackNo(String packNo) {
		this.packNo = packNo;
	}
	
	@Length(min=0, max=255, message="订单编号长度必须介于 0 和 255 之间")
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}