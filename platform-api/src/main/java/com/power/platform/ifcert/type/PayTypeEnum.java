package com.power.platform.ifcert.type;

/**
 * 
 * class: PayTypeEnum <br>
 * description: 还款类型枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月8日 下午5:11:04
 */
public enum PayTypeEnum {

	/**
	 * 等额本息
	 */
	PAY_TYPE_1("1", "等额本息"),
	/**
	 * 等额本金
	 */
	PAY_TYPE_2("2", "等额本金"),
	/**
	 * 先息后本（包括两种还款类型， a 按月付息到期还本； b 前期按月付息，后期按月还本付息）
	 */
	PAY_TYPE_3("3", "先息后本（包括两种还款类型， a：按月付息到期还本； b：前期按月付息，后期按月还本付息）"),
	/**
	 * 一次性还本付息
	 */
	PAY_TYPE_4("4", "一次性还本付息"),
	/**
	 * 其它按月还本付息
	 */
	PAY_TYPE_5("5", "其它按月还本付息"),
	/**
	 * 等本等息
	 */
	PAY_TYPE_6("6", "等本等息"),
	/**
	 * 其他
	 */
	PAY_TYPE_7("7", "其他");

	private PayTypeEnum(String value, String text) {

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
