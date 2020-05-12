package com.power.platform.ifcert.type;

/**
 * 
 * class: UserTypeEnum <br>
 * description: 用户类型. <br>
 * author: Roy <br>
 * date: 2019年4月25日 下午3:53:45
 */
public enum UserTypeEnum {

	/**
	 * 自然人.
	 */
	USER_TYPE_1("1", "自然人"),
	/**
	 * 企业.
	 */
	USER_TYPE_2("2", "企业");

	private UserTypeEnum(String value, String text) {

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

	public UserTypeEnum getByValue(String value) {

		for (UserTypeEnum item : UserTypeEnum.values()) {
			if (item.value == value)
				return item;
		}
		return null;
	}

}
