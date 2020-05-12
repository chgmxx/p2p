package com.power.platform.ifcert.type;

/**
 * 
 * class: RepayTypeEnum <br>
 * description: 平台还款类型枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月13日 下午5:59:58
 */
public enum RepayTypeEnum {

	/**
	 * 付息.
	 */
	REPAY_TYPE_0("0", "付息"),
	/**
	 * 还本付息.
	 */
	REPAY_TYPE_1("1", "还本付息");

	private RepayTypeEnum(String value, String text) {

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
