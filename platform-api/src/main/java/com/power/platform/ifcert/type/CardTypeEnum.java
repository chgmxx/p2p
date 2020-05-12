package com.power.platform.ifcert.type;

/**
 * 
 * class: CardTypeEnum <br>
 * description: 证件类型. <br>
 * author: Roy <br>
 * date: 2019年4月28日 下午3:49:26
 */
public enum CardTypeEnum {

	/**
	 * 身份证；
	 */
	CARD_TYPE_1("1", "身份证"),
	/**
	 * 护照；
	 */
	CARD_TYPE_2("2", "护照"),
	/**
	 * 军官证；
	 */
	CARD_TYPE_3("3", "军官证"),
	/**
	 * 台湾居民来往大陆通行证；
	 */
	CARD_TYPE_4("4", "台湾居民来往大陆通行证"),
	/**
	 * 港澳居民来往内地通行证；
	 */
	CARD_TYPE_5("5", "港澳居民来往内地通行证"),
	/**
	 * 外国人永久居留身份证；
	 */
	CARD_TYPE_6("6", "外国人永久居留身份证"),
	/**
	 * 三证合一证/五证合一证/工商注册号等机构证件类型；
	 */
	CARD_TYPE_7("7", "三证合一证/五证合一证/工商注册号等机构证件类型");

	private CardTypeEnum(String value, String text) {

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
