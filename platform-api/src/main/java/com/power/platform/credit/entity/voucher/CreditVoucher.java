/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.voucher;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;

/**
 * 发票Entity
 * 
 * @author jice
 * @version 2018-03-14
 */
public class CreditVoucher extends DataEntity<CreditVoucher> {

	private static final long serialVersionUID = 1L;
	private String annexId; // creditAnnexFile的id
	private String creditInfoId;// 信息id
	private String packNo; // 合同编号
	private String no; // 发票编号
	private String money; // 发票金额
	private String url;// 发票图片路径

	private CreditAnnexFile annexFile;

	private String code; // 发票代码.
	private Date issueDate; // 开票日期.

	public CreditVoucher() {

		super();
	}

	public CreditVoucher(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "creditAnnexFile的id长度必须介于 0 和 64 之间")
	public String getCreditInfoId() {

		return creditInfoId;
	}

	public void setCreditInfoId(String creditInfoId) {

		this.creditInfoId = creditInfoId;
	}

	@Length(min = 0, max = 64, message = "creditAnnexFile的id长度必须介于 0 和 64 之间")
	public String getAnnexId() {

		return annexId;
	}

	public void setAnnexId(String annexId) {

		this.annexId = annexId;
	}

	@Length(min = 0, max = 255, message = "发票编号长度必须介于 0 和 255 之间")
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

	public String getPackNo() {

		return packNo;
	}

	public void setPackNo(String packNo) {

		this.packNo = packNo;
	}

	public CreditAnnexFile getAnnexFile() {

		return annexFile;
	}

	public void setAnnexFile(CreditAnnexFile annexFile) {

		this.annexFile = annexFile;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public String getCode() {

		return code;
	}

	public void setCode(String code) {

		this.code = code;
	}

	public Date getIssueDate() {

		return issueDate;
	}

	public void setIssueDate(Date issueDate) {

		this.issueDate = issueDate;
	}

}