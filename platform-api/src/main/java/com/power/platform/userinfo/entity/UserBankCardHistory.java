/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.power.platform.common.persistence.DataEntity;

/**
 * 客户银行卡更换历史Entity
 * 
 * @author Soler
 * @version 2015-12-21
 */
public class UserBankCardHistory extends DataEntity<UserBankCardHistory> {

	private static final long serialVersionUID = 1L;
	private String userId; // 客户ID
	private String realName; // 客户姓名
	private String mobilePhone; // 客户移动电话
	private String identityCardNo; // 身份证号码
	private String oldBankCardNo; // 旧银行卡号码
	private String newBankCardNo; // 新银行卡号码
	private String state; // 状态
	private Date replaceDate; // 更换时间
	private String identityCardForwardSidePicUrl; // 身份证正面照片
	private String identityCardBackSidePicUrl; // 身份证反面照片
	private String identityCardAndPersonPicUrl; // 本人手持身份证照片
	private Date beginReplaceDate; // 开始 更换时间
	private Date endReplaceDate; // 结束 更换时间

	public UserBankCardHistory() {

		super();
	}

	public UserBankCardHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "客户ID长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 64, message = "客户姓名长度必须介于 0 和 64 之间")
	public String getRealName() {

		return realName;
	}

	public void setRealName(String realName) {

		this.realName = realName;
	}

	@Length(min = 0, max = 11, message = "客户移动电话长度必须介于 0 和 11 之间")
	public String getMobilePhone() {

		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {

		this.mobilePhone = mobilePhone;
	}

	@Length(min = 0, max = 64, message = "身份证号码长度必须介于 0 和 64 之间")
	public String getIdentityCardNo() {

		return identityCardNo;
	}

	public void setIdentityCardNo(String identityCardNo) {

		this.identityCardNo = identityCardNo;
	}

	@Length(min = 0, max = 64, message = "旧银行卡号码长度必须介于 0 和 64 之间")
	public String getOldBankCardNo() {

		return oldBankCardNo;
	}

	public void setOldBankCardNo(String oldBankCardNo) {

		this.oldBankCardNo = oldBankCardNo;
	}

	@Length(min = 0, max = 64, message = "新银行卡号码长度必须介于 0 和 64 之间")
	public String getNewBankCardNo() {

		return newBankCardNo;
	}

	public void setNewBankCardNo(String newBankCardNo) {

		this.newBankCardNo = newBankCardNo;
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReplaceDate() {

		return replaceDate;
	}

	public void setReplaceDate(Date replaceDate) {

		this.replaceDate = replaceDate;
	}

	@Length(min = 0, max = 255, message = "身份证正面照片长度必须介于 0 和 255 之间")
	public String getIdentityCardForwardSidePicUrl() {

		return identityCardForwardSidePicUrl;
	}

	public void setIdentityCardForwardSidePicUrl(String identityCardForwardSidePicUrl) {

		this.identityCardForwardSidePicUrl = identityCardForwardSidePicUrl;
	}

	@Length(min = 0, max = 255, message = "身份证反面照片长度必须介于 0 和 255 之间")
	public String getIdentityCardBackSidePicUrl() {

		return identityCardBackSidePicUrl;
	}

	public void setIdentityCardBackSidePicUrl(String identityCardBackSidePicUrl) {

		this.identityCardBackSidePicUrl = identityCardBackSidePicUrl;
	}

	@Length(min = 0, max = 255, message = "本人手持身份证照片长度必须介于 0 和 255 之间")
	public String getIdentityCardAndPersonPicUrl() {

		return identityCardAndPersonPicUrl;
	}

	public void setIdentityCardAndPersonPicUrl(String identityCardAndPersonPicUrl) {

		this.identityCardAndPersonPicUrl = identityCardAndPersonPicUrl;
	}

	public Date getBeginReplaceDate() {

		return beginReplaceDate;
	}

	public void setBeginReplaceDate(Date beginReplaceDate) {

		this.beginReplaceDate = beginReplaceDate;
	}

	public Date getEndReplaceDate() {

		return endReplaceDate;
	}

	public void setEndReplaceDate(Date endReplaceDate) {

		this.endReplaceDate = endReplaceDate;
	}

}