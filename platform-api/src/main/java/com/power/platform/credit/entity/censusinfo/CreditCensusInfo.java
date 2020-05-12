/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.censusinfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;


/**
 * 信贷人口普查Entity
 * @author nice
 * @version 2017-03-23
 */
public class CreditCensusInfo extends DataEntity<CreditCensusInfo> {
	
	private static final long serialVersionUID = 1L;
	private String creditUserId;		// 用户ID
	private String creditAnnexId;		// 附件表ID
	private String remark;		// 备注
	
	private CreditUserInfo creditUserInfo;
	private CreditAnnexFile creditAnnexFile;
	
	private List<String> imgList;
	
	public CreditCensusInfo() {
		super();
	}

	public CreditCensusInfo(String id){
		super(id);
	}

	@Length(min=0, max=64, message="用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {
		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {
		this.creditUserId = creditUserId;
	}
	
	@Length(min=0, max=64, message="附件表ID长度必须介于 0 和 64 之间")
	public String getCreditAnnexId() {
		return creditAnnexId;
	}

	public void setCreditAnnexId(String creditAnnexId) {
		this.creditAnnexId = creditAnnexId;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<String> getImgList() {
		return imgList;
	}

	public void setImgList(List<String> imgList) {
		this.imgList = imgList;
	}

	public CreditUserInfo getCreditUserInfo() {
		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {
		this.creditUserInfo = creditUserInfo;
	}

	public CreditAnnexFile getCreditAnnexFile() {
		return creditAnnexFile;
	}

	public void setCreditAnnexFile(CreditAnnexFile creditAnnexFile) {
		this.creditAnnexFile = creditAnnexFile;
	}
	
	
}