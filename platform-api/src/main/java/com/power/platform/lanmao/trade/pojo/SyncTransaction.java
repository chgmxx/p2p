package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * class: SyncTransaction <br>
 * description: 单笔交易参数列表 <br>
 * author: Roy <br>
 * date: 2019年9月22日 下午12:22:30
 */
public class SyncTransaction implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String requestNo; // 请求流水号
	private String tradeType; // 见【交易类型】枚举类
	private String projectNo; // 标的号
	private String saleRequestNo; // 债权出让请求流水号
	private List<SyncTransactionDetail> details; // 业务明细

	public String getRequestNo() {

		return requestNo;
	}

	public void setRequestNo(String requestNo) {

		this.requestNo = requestNo;
	}

	public String getTradeType() {

		return tradeType;
	}

	public void setTradeType(String tradeType) {

		this.tradeType = tradeType;
	}

	public String getProjectNo() {

		return projectNo;
	}

	public void setProjectNo(String projectNo) {

		this.projectNo = projectNo;
	}

	public String getSaleRequestNo() {

		return saleRequestNo;
	}

	public void setSaleRequestNo(String saleRequestNo) {

		this.saleRequestNo = saleRequestNo;
	}

	public List<SyncTransactionDetail> getDetails() {

		return details;
	}

	public void setDetails(List<SyncTransactionDetail> details) {

		this.details = details;
	}

}
