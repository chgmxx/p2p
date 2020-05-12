package com.power.platform.jbsys.type;

/**
 * 
 * class: FromEnum <br>
 * description: 请求来源枚举类. <br>
 * author: Roy <br>
 * date: 2019年6月9日 上午11:59:11
 */
public enum FromEnum {

	/**
	 * JB-尖兵系统.
	 */
	FROM_ENUM_JB("JB", "尖兵系统");

	private FromEnum(String value, String text) {

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
