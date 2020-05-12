package com.power.platform.ifcert.type;

/**
 * 
 * class: SecurityTypeEnum <br>
 * description: 担保方式类型枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月9日 上午9:20:51
 */
public enum SecurityTypeEnum {

	/**
	 * 没有担保方式
	 */
	SECURITY_TYPE_NEGATIVE_1("-1", "没有担保方式"),
	/**
	 * 第三方担保
	 */
	SECURITY_TYPE_5("5", "第三方担保");

	private SecurityTypeEnum(String value, String text) {

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
