package com.power.platform.ifcert.type;

/**
 * 
 * class: UserAddressEnum <br>
 * description: 企业注册地址. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午11:36:19
 */
public enum UserAddressEnum {

	/**
	 * 企业注册地址，自然人填写-1.
	 */
	USER_ADDRESS_NAGATIVE_1("-1", "自然人填写-1");

	private UserAddressEnum(String value, String text) {

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
