/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.houseinfo;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.sys.entity.Area;

/**
 * 信贷房产信息Entity
 * @author nice
 * @version 2017-03-23
 */
public class CreditHouseInfo extends DataEntity<CreditHouseInfo> {
	
	private static final long serialVersionUID = 1L;
	private String creditUserId;		// 用户ID
	private String creditAnnexId;		// 附件ID
	private String areaProvince;		// 房产地址省
	private String areaCity;    // 房产地址市
	private String address;		// 具体住址位置
	private String remark;		// 备注
	
	private Area province;	
	private Area city;	
	
	private List<String> imgList;
	
	private CreditUserInfo creditUserInfo;
	private CreditAnnexFile creditAnnexFile;
	
	public CreditHouseInfo() {
		super();
	}

	public CreditHouseInfo(String id){
		super(id);
	}

	@Length(min=0, max=64, message="用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {
		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {
		this.creditUserId = creditUserId;
	}
	
	@Length(min=0, max=64, message="附件ID长度必须介于 0 和 64 之间")
	public String getCreditAnnexId() {
		return creditAnnexId;
	}

	public void setCreditAnnexId(String creditAnnexId) {
		this.creditAnnexId = creditAnnexId;
	}
	
	@Length(min=0, max=255, message="具体住址位置长度必须介于 0 和 255 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getAreaProvince() {
		return areaProvince;
	}

	public void setAreaProvince(String areaProvince) {
		this.areaProvince = areaProvince;
	}

	public String getAreaCity() {
		return areaCity;
	}

	public void setAreaCity(String areaCity) {
		this.areaCity = areaCity;
	}

	public Area getProvince() {
		return province;
	}

	public void setProvince(Area province) {
		this.province = province;
	}

	public Area getCity() {
		return city;
	}

	public void setCity(Area city) {
		this.city = city;
	}

	public CreditUserInfo getCreditUserInfo() {
		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {
		this.creditUserInfo = creditUserInfo;
	}

	public List<String> getImgList() {
		return imgList;
	}

	public void setImgList(List<String> imgList) {
		this.imgList = imgList;
	}

	public CreditAnnexFile getCreditAnnexFile() {
		return creditAnnexFile;
	}

	public void setCreditAnnexFile(CreditAnnexFile creditAnnexFile) {
		this.creditAnnexFile = creditAnnexFile;
	}
	
	
}