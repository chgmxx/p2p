package com.power.platform.cgb.type;

/**
 * 
 * class: StatusEnum <br>
 * description: 状态枚举类. <br>
 * author: Roy <br>
 * date: 2019年8月1日 下午6:18:03
 */
public enum StatusEnum {

	/**
	 * S：成功.
	 */
	STATUS_ENUM_S("S", "成功"),
	/**
	 * AS：受理成功.
	 */
	STATUS_ENUM_AS("AS", "受理成功"),
	/**
	 * F：失败.
	 */
	STATUS_ENUM_F("F", "失败");

	private StatusEnum(String value, String text) {

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
