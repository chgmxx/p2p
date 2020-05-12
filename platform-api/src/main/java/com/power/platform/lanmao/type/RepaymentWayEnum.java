package com.power.platform.lanmao.type;

/**
 * 
 * class: RepaymentWayEnum <br>
 * description: 还款方式 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午8:40:08
 */
public enum RepaymentWayEnum {

	/**
	 * ONE_TIME_SERVICING:一次性还本付息
	 */
	ONE_TIME_SERVICING("ONE_TIME_SERVICING", "一次性还本付息"),
	/**
	 * FIRSEINTREST_LASTPRICIPAL:先息后本
	 */
	FIRSEINTREST_LASTPRICIPAL("FIRSEINTREST_LASTPRICIPAL", "先息后本"),
	/**
	 * FIXED_PAYMENT_MORTGAGE:等额本息
	 */
	FIXED_PAYMENT_MORTGAGE("FIXED_PAYMENT_MORTGAGE", "等额本息"),
	/**
	 * FIXED_BASIS_MORTGAGE:等额本金
	 */
	FIXED_BASIS_MORTGAGE("FIXED_BASIS_MORTGAGE", "等额本金"),
	/**
	 * EQUAL_PRINCIPAL_INTEREST:等本等息
	 */
	EQUAL_PRINCIPAL_INTEREST("EQUAL_PRINCIPAL_INTEREST", "等本等息");

	private String value;
	private String text;

	private RepaymentWayEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

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
	public static String getTextByValue(String value) {

		  for (RepaymentWayEnum v : RepaymentWayEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
