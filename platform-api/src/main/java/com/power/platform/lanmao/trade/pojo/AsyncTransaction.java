package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * class: AsyncTransaction <br>
 * description: 批量交易参数列表 <br>
 * author: Roy <br>
 * date: 2019年9月23日 上午10:04:20
 */
public class AsyncTransaction implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String batchNo; // 批次号
	private List<SyncTransaction> bizDetails; // 交易明细

	public String getBatchNo() {

		return batchNo;
	}

	public void setBatchNo(String batchNo) {

		this.batchNo = batchNo;
	}

	public List<SyncTransaction> getBizDetails() {

		return bizDetails;
	}

	public void setBizDetails(List<SyncTransaction> bizDetails) {

		this.bizDetails = bizDetails;
	}

}
