package com.power.platform.ifcert.type;

/**
 * 
 * class: RegisterDateEnum <br>
 * description: 企业注册时间. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午11:40:37
 */
public enum RegisterDateEnum {

	/**
	 * 企业注册时间，自然人填写-1.
	 */
	REGISTER_DATE_NAGATIVE_1("-1", "自然人填写-1");

	private RegisterDateEnum(String value, String text) {

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
