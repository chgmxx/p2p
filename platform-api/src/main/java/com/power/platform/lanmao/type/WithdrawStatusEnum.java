package com.power.platform.lanmao.type;

/**
 * 
 * class: WithdrawStatusEnum <br>
 * description: 提现交易状态 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum WithdrawStatusEnum {

	/**
	 * CONFIRMING:待确认
	 */
	CONFIRMING("CONFIRMING", "待确认"),
	/**
	 * ACCEPT:已受理
	 */
	ACCEPT("ACCEPT", "已受理"),
	/**
	 * REMITING:出款中
	 */
	REMITING("REMITING", "出款中"),
	/**
	 * SUCCESS:提现成功
	 */
	SUCCESS("SUCCESS", "提现成功"),
	/**
	 * FAIL:提现失败
	 */
	FAIL("FAIL", "提现失败"),
	/**
	 * ACCEPT_FAIL:受理失败
	 */
	ACCEPT_FAIL("ACCEPT_FAIL", "受理失败"),;

	private String value;
	private String text;

	private WithdrawStatusEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

	public static String getTextByValue(String value) {

		  for (WithdrawStatusEnum v : WithdrawStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
