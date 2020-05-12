package com.power.platform.ifcert.type;

/**
 * 
 * class: UserFundEnum <br>
 * description: 注册资本. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午10:44:39
 */
public enum UserFundEnum {

	/**
	 * 注册资本，自然人填写-1.
	 */
	USER_FUND_NEGATIVE_1("-1", "自然人填写-1");

	private UserFundEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	private String value;
	private String text;

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

}
