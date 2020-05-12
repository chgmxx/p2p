/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.audit;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 借款审核信息Entity
 * 
 * @author Roy
 * @version 2019-01-16
 */
public class CreditAuditInfo extends DataEntity<CreditAuditInfo> {

	private static final long serialVersionUID = 1L;
	private String zipUrl; // 压缩包路径
	private String actionMessage; // 当前操作说明

	/**
	 * 记录，2：通过/3：驳回，两种状态.
	 */
	private String status;

	public CreditAuditInfo() {

		super();
	}

	public CreditAuditInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 128, message = "压缩包路径长度必须介于 0 和 128 之间")
	public String getZipUrl() {

		return zipUrl;
	}

	public void setZipUrl(String zipUrl) {

		this.zipUrl = zipUrl;
	}

	@Length(min = 0, max = 256, message = "当前操作说明长度必须介于 0 和 256 之间")
	public String getActionMessage() {

		return actionMessage;
	}

	public void setActionMessage(String actionMessage) {

		this.actionMessage = actionMessage;
	}

	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

}