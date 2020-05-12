/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.electronic;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;


/**
 * 电子签章明细Entity
 * @author jice
 * @version 2018-03-19
 */
public class ElectronicSignTranstail extends DataEntity<ElectronicSignTranstail> {
	
	private static final long serialVersionUID = 1L;
	private String coreId;					// 核心企业id
	private String supplyId;				// 供应商id
	private String signServiceIdSupply;		// 供应商服务id
	private String signServiceIdCore;		// 核心企业服务id
	
	private String investUserId;//投资用户id
	private String signServiceIdUser;//投资用户服务id
	
	
	public ElectronicSignTranstail() {
		super();
	}

	public ElectronicSignTranstail(String id){
		super(id);
	}

	@Length(min=0, max=64, message="供应商服务id长度必须介于 0 和 64 之间")
	public String getSignServiceIdSupply() {
		return signServiceIdSupply;
	}

	public void setSignServiceIdSupply(String signServiceIdSupply) {
		this.signServiceIdSupply = signServiceIdSupply;
	}
	
	@Length(min=0, max=64, message="核心企业服务id长度必须介于 0 和 64 之间")
	public String getSignServiceIdCore() {
		return signServiceIdCore;
	}

	public void setSignServiceIdCore(String signServiceIdCore) {
		this.signServiceIdCore = signServiceIdCore;
	}

	public String getCoreId() {
		return coreId;
	}

	public void setCoreId(String coreId) {
		this.coreId = coreId;
	}

	public String getSupplyId() {
		return supplyId;
	}

	public void setSupplyId(String supplyId) {
		this.supplyId = supplyId;
	}

	public String getInvestUserId() {
		return investUserId;
	}

	public void setInvestUserId(String investUserId) {
		this.investUserId = investUserId;
	}

	public String getSignServiceIdUser() {
		return signServiceIdUser;
	}

	public void setSignServiceIdUser(String signServiceIdUser) {
		this.signServiceIdUser = signServiceIdUser;
	}
	
}