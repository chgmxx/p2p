package com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo;

import java.util.List;

import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;

/**
 * 
 * 类: ZtmgLoanApplyAndBasicInfoPojo <br>
 * 描述: 融资主体联动借款申请和基本信息. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月5日 下午3:50:16
 */
public class ZtmgLoanApplyAndBasicInfoPojo {

	// 借款申请列表.
	private List<CreditUserApply> creditUserApplys;

	// 借款人基本信息.
	private ZtmgLoanBasicInfo ztmgLoanBasicInfo;

	public List<CreditUserApply> getCreditUserApplys() {

		return creditUserApplys;
	}

	public void setCreditUserApplys(List<CreditUserApply> creditUserApplys) {

		this.creditUserApplys = creditUserApplys;
	}

	public ZtmgLoanBasicInfo getZtmgLoanBasicInfo() {

		return ztmgLoanBasicInfo;
	}

	public void setZtmgLoanBasicInfo(ZtmgLoanBasicInfo ztmgLoanBasicInfo) {

		this.ztmgLoanBasicInfo = ztmgLoanBasicInfo;
	}

}
