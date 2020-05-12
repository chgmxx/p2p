/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.entity.annexfile;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucher;

/**
 * 信贷附件Entity
 * 
 * @author nice
 * @version 2017-03-23
 */
public class CreditAnnexFile extends DataEntity<CreditAnnexFile> {

	private static final long serialVersionUID = 1L;
	private String otherId; // 外键id
	private String url; // 图片
	/**
	 * 类型（在其他表中标识类型含义）1 合同影印件 2 订单(ERP系统截图) 3 发货单 4 验收单 5 对账单 6 发票 7 承诺函 8 营业执照
	 * 9 银行开户许可证(一张) 10 法人身份证 11 申请书 30 核心企业图片
	 */
	private String type;
	private String state; // 审核状态(1：审核中，2：通过，3：未通过)
	private String remark; // 备注
	private CreditUserInfo creditUserInfo; // 信贷客户信息.
	private List<String> typeList;

	private String returnUrl;

	private String dictType;

	private String title;

	private boolean bookByCommitment_B = false;

	/**
	 * 发票信息.
	 */
	private CreditVoucher creditVoucher;

	public CreditVoucher getCreditVoucher() {

		return creditVoucher;
	}

	public void setCreditVoucher(CreditVoucher creditVoucher) {

		this.creditVoucher = creditVoucher;
	}

	public CreditAnnexFile() {

		super();
	}

	public CreditAnnexFile(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "外键id长度必须介于 0 和 64 之间")
	public String getOtherId() {

		return otherId;
	}

	public void setOtherId(String otherId) {

		this.otherId = otherId;
	}

	@Length(min = 0, max = 5000, message = "图片长度必须介于 0 和 5000 之间")
	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	@Length(min = 0, max = 10, message = "类型（在其他表中标识类型含义）长度必须介于 0 和 10 之间")
	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public CreditUserInfo getCreditUserInfo() {

		return creditUserInfo;
	}

	public void setCreditUserInfo(CreditUserInfo creditUserInfo) {

		this.creditUserInfo = creditUserInfo;
	}

	public List<String> getTypeList() {

		return typeList;
	}

	public void setTypeList(List<String> typeList) {

		this.typeList = typeList;
	}

	public String getReturnUrl() {

		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {

		this.returnUrl = returnUrl;
	}

	public String getDictType() {

		return dictType;
	}

	public void setDictType(String dictType) {

		this.dictType = dictType;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public boolean isBookByCommitment_B() {

		return bookByCommitment_B;
	}

	public void setBookByCommitment_B(boolean bookByCommitment_B) {

		this.bookByCommitment_B = bookByCommitment_B;
	}

}