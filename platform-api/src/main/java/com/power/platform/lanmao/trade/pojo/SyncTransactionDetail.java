package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;

/**
 * 
 * class: SyncTransactionDetail <br>
 * description: 单笔交易业务明细 <br>
 * author: Roy <br>
 * date: 2019年9月22日 下午12:23:53
 */
public class SyncTransactionDetail implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String bizType; // 见【业务类型】枚举
	private String freezeRequestNo; // 预处理请求流水号；若出款方为平台功能账户，不进行预处理冻结金额，直接从平台功能账户划拨资金到平台收款方用户，则不传该参数。
	private String sourcePlatformUserNo; // 出款方用户编号
	private String targetPlatformUserNo; // 收款方用户编号
	private String amount; // 交易金额 （有利息时为本息和， amount为本金和利息之和）
	private String income; // 利息 （income为利息， 本金= amount — income）
	private String share; // 债权份额（债权认购且需校验债权关系的必传）
	private String customDefine; // 网贷平台自定义参数，平台交易时传入的自定义参数
	private String remark; // 备注

	public String getBizType() {

		return bizType;
	}

	public void setBizType(String bizType) {

		this.bizType = bizType;
	}

	public String getFreezeRequestNo() {

		return freezeRequestNo;
	}

	public void setFreezeRequestNo(String freezeRequestNo) {

		this.freezeRequestNo = freezeRequestNo;
	}

	public String getSourcePlatformUserNo() {

		return sourcePlatformUserNo;
	}

	public void setSourcePlatformUserNo(String sourcePlatformUserNo) {

		this.sourcePlatformUserNo = sourcePlatformUserNo;
	}

	public String getTargetPlatformUserNo() {

		return targetPlatformUserNo;
	}

	public void setTargetPlatformUserNo(String targetPlatformUserNo) {

		this.targetPlatformUserNo = targetPlatformUserNo;
	}

	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

	public String getIncome() {

		return income;
	}

	public void setIncome(String income) {

		this.income = income;
	}

	public String getShare() {

		return share;
	}

	public void setShare(String share) {

		this.share = share;
	}

	public String getCustomDefine() {

		return customDefine;
	}

	public void setCustomDefine(String customDefine) {

		this.customDefine = customDefine;
	}

	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

}
