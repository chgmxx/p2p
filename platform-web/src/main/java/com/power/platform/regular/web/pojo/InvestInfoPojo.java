package com.power.platform.regular.web.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.utils.excel.annotation.ExcelField;

public class InvestInfoPojo {

	// 姓名.
	private String name;
	// 移动电话.
	private String mobilePhone;
	// 身份证号码.
	private String idCardCode;
	// 出借金额.
	private String investAmount;
	// 出借时间.
	private Date investDateTime;

	@ExcelField(title = "姓名", align = 2, sort = 10)
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@ExcelField(title = "电话", align = 2, sort = 20)
	public String getMobilePhone() {

		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {

		this.mobilePhone = mobilePhone;
	}

	@ExcelField(title = "身份证号", align = 2, sort = 30)
	public String getIdCardCode() {

		return idCardCode;
	}

	public void setIdCardCode(String idCardCode) {

		this.idCardCode = idCardCode;
	}

	@ExcelField(title = "出借金额", align = 2, sort = 40)
	public String getInvestAmount() {

		return investAmount;
	}

	public void setInvestAmount(String investAmount) {

		this.investAmount = investAmount;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
	@ExcelField(title = "出借时间", align = 2, sort = 50)
	public Date getInvestDateTime() {

		return investDateTime;
	}

	public void setInvestDateTime(Date investDateTime) {

		this.investDateTime = investDateTime;
	}

}
