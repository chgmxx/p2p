package com.power.platform.credit.entity.ztmgLoanBasicInfo;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

/**
 * 借款人股东信息Entity
 * 
 * @author Roy
 * @version 2018-05-02
 */
/**
 * 
 * 类: ZtmgLoanShareholdersInfo <br>
 * 描述: 借款人股东信息Entity. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月2日 上午8:59:17
 */
public class ZtmgLoanShareholdersInfo extends DataEntity<ZtmgLoanShareholdersInfo> {

	private static final long serialVersionUID = 1L;
	private String loanBasicId; // 借款人基本信息ID
	private String shareholdersType; // 股东类型
	private String shareholdersCertType; // 证件类型
	private String shareholdersName; // 股东名称
	private String remark; // 备注

	public ZtmgLoanShareholdersInfo() {

		super();
	}

	public ZtmgLoanShareholdersInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "借款人基本信息ID长度必须介于 0 和 64 之间")
	public String getLoanBasicId() {

		return loanBasicId;
	}

	public void setLoanBasicId(String loanBasicId) {

		this.loanBasicId = loanBasicId;
	}

	@Length(min = 0, max = 32, message = "股东类型长度必须介于 0 和 32 之间")
	public String getShareholdersType() {

		return shareholdersType;
	}

	public void setShareholdersType(String shareholdersType) {

		this.shareholdersType = shareholdersType;
	}

	@Length(min = 0, max = 32, message = "证件类型长度必须介于 0 和 32 之间")
	public String getShareholdersCertType() {

		return shareholdersCertType;
	}

	public void setShareholdersCertType(String shareholdersCertType) {

		this.shareholdersCertType = shareholdersCertType;
	}

	@Length(min = 0, max = 32, message = "股东名称长度必须介于 0 和 32 之间")
	public String getShareholdersName() {

		return shareholdersName;
	}

	public void setShareholdersName(String shareholdersName) {

		this.shareholdersName = shareholdersName;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

}
