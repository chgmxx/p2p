package com.power.platform.ifcert.type;

/**
 * 
 * class: CountriesEnum <br>
 * description: 国别枚举. <br>
 * author: Roy <br>
 * date: 2019年4月25日 下午3:09:14
 */
public enum CountriesEnum {

	/**
	 * 平台无国别字段.
	 */
	COUNTRIES_NEGATIVE_1("-1", "平台无国别字段"),
	/**
	 * 中国大陆.
	 */
	COUNTRIES_1("1", "中国大陆"),
	/**
	 * 中国港澳台.
	 */
	COUNTRIES_2("2", "中国港澳台"),
	/**
	 * 国外.
	 */
	COUNTRIES_3("3", "国外");

	/**
	 * 
	 * title: constructor <br>
	 * description: 枚举类构造方法赋值. <br>
	 * author: Roy <br>
	 * date: 2019年4月25日 下午3:03:47
	 * 
	 * @param value
	 * @param text
	 */
	private CountriesEnum(String value, String text) {

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

	public CountriesEnum getByValue(String value) {

		for (CountriesEnum item : CountriesEnum.values()) {
			if (item.value == value)
				return item;
		}
		return null;
	}
}
