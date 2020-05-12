package com.power.platform.lanmao.trade.pojo;

import java.io.Serializable;

/**
 * 
 * class: CancelPreTransaction <br>
 * description: 预处理取消参数列表 <br>
 * author: Roy <br>
 * date: 2019年9月22日 上午11:15:22
 */
public class CancelPreTransaction implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;
	private String requestNo;
	private String preTransactionNo;
	private String amount;

	public String getRequestNo() {

		return requestNo;
	}

	public void setRequestNo(String requestNo) {

		this.requestNo = requestNo;
	}

	public String getPreTransactionNo() {

		return preTransactionNo;
	}

	public void setPreTransactionNo(String preTransactionNo) {

		this.preTransactionNo = preTransactionNo;
	}

	public String getAmount() {

		return amount;
	}

	public void setAmount(String amount) {

		this.amount = amount;
	}

}
