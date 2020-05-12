package com.power.platform.ifcert.type;

/**
 * 
 * class: LoanTypeEnum <br>
 * description: 借款类型枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月8日 下午5:34:50
 */
public enum LoanTypeEnum {

	/**
	 * 信用标
	 */
	LOAN_TYPE_1("1", "信用标"),
	/**
	 * 抵押标
	 */
	LOAN_TYPE_2("2", "抵押标"),
	/**
	 * 担保标
	 */
	LOAN_TYPE_3("3", "担保标"),
	/**
	 * 流转标
	 */
	LOAN_TYPE_4("4", "流转标"),
	/**
	 * 净值标
	 */
	LOAN_TYPE_5("5", "净值标"),
	/**
	 * 信用+抵押
	 */
	LOAN_TYPE_6("6", "信用+抵押"),
	/**
	 * 信用+担保
	 */
	LOAN_TYPE_7("7", "信用+担保"),
	/**
	 * 净值+抵押
	 */
	LOAN_TYPE_8("8", "净值+抵押"),
	/**
	 * 净值+信用
	 */
	LOAN_TYPE_9("9", "净值+信用");

	private LoanTypeEnum(String value, String text) {

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
