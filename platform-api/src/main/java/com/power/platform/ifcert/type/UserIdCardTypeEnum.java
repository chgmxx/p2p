package com.power.platform.ifcert.type;

/**
 * 
 * class: UserIdCardTypeEnum <br>
 * description: 用户证件类型枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月8日 下午3:18:38
 */
public enum UserIdCardTypeEnum {

	/**
	 * 身份证.
	 */
	USER_ID_CARD_TYPE_IDC("IDC", "身份证"),
	/**
	 * 港澳台居民来往内地通行证.
	 */
	USER_ID_CARD_TYPE_GAT("GAT", "港澳台居民来往内地通行证"),
	/**
	 * 军官证.
	 */
	USER_ID_CARD_TYPE_MILIARY("MILIARY", "军官证"),
	/**
	 * 护照.
	 */
	USER_ID_CARD_TYPE_PASS_PORT("PASS_PORT", "护照");

	private UserIdCardTypeEnum(String value, String text) {

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
