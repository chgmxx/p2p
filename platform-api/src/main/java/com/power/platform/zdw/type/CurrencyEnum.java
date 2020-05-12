package com.power.platform.zdw.type;

/**
 * 
 * 类: CurrencyEnum <br>
 * 描述: 币种 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 下午9:28:12
 */
public enum CurrencyEnum {

	/**
	 * CNY-人民币
	 */
	CURRENCY_CNY("CNY", "人民币"),
	/**
	 * USD-美元
	 */
	CURRENCY_USD("USD", "美元"),
	/**
	 * EUR-欧元
	 */
	CURRENCY_EUR("EUR", "欧元"),
	/**
	 * GBP-个人
	 */
	CURRENCY_GBP("GBP", "个人"),
	/**
	 * JPY-日元
	 */
	CURRENCY_JPY("JPY", "日元"),
	/**
	 * HKY-港币
	 */
	CURRENCY_HKY("HKY", "港币"),
	/**
	 * OTH-其他币种
	 */
	CURRENCY_OTH("OTH", "其他币种");

	private CurrencyEnum(String value, String text) {

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
