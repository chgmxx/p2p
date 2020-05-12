package com.power.platform.ifcert.type;

/**
 * 
 * class: UserProvinceEnum <br>
 * description: 企业注册省份. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午10:50:23
 */
public enum UserProvinceEnum {
	/**
	 * 企业注册省份，自然人填写-1.
	 */
	USER_PROVINCE_NEGATIVE_1("-1", "自然人填写-1");

	private UserProvinceEnum(String value, String text) {

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
