package com.power.platform.ifcert.type;

/**
 * 
 * class: UserSexEnum <br>
 * description: 用户性别. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午11:44:22
 */
public enum UserSexEnum {
	/**
	 * 企业填写-1.
	 */
	USER_SEX_NAGATIVE_1("-1", "企业填写-1"),
	/**
	 * 女.
	 */
	USER_SEX_0("0", "女"),
	/**
	 * 男.
	 */
	USER_SEX_1("1", "男");

	private UserSexEnum(String value, String text) {

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
