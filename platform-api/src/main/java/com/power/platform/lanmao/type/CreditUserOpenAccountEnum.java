package com.power.platform.lanmao.type;

/**
 * 
 * class: CreditUserOpenAccountEnum <br>
 * description: 借款人-企业绑卡注册-开户状态 <br>
 * author: Roy <br>
 * date: 2019年9月26日 上午10:03:44
 */
public enum CreditUserOpenAccountEnum {

	/**
	 * 0:未开户
	 */
	OPEN_ACCOUNT_STATE_0("0", "未开户"),
	/**
	 * 1:已开户
	 */
	OPEN_ACCOUNT_STATE_1("1", "已开户"),
	/**
	 * 2:审核中
	 */
	OPEN_ACCOUNT_STATE_2("2", "审核中"),
	/**
	 * 3:审核回退
	 */
	OPEN_ACCOUNT_STATE_3("3", "审核回退"),
	/**
	 * 4:审核拒绝
	 */
	OPEN_ACCOUNT_STATE_4("4", "审核拒绝");

	private String value;
	private String text;

	private CreditUserOpenAccountEnum(String value, String text) {

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

		for (CreditUserOpenAccountEnum v : CreditUserOpenAccountEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}
}
