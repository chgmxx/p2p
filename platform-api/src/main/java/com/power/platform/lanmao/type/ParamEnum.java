package com.power.platform.lanmao.type;

/**
 * 
 * class: ParamEnum <br>
 * description: 参数类型 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:05:28
 */
public enum ParamEnum {

	/**
	 * I:整数，十亿以内
	 */
	PARAM_I("I", "整数，十亿以内"),
	/**
	 * A:金额：单位元，精确到到小数点后两位，十亿以内
	 */
	PARAM_A("A", "金额：单位元，精确到到小数点后两位，十亿以内"),
	/**
	 * D:日期：使用YYYYMMDD（如20141213）的格式。时区采用北京时间（GMT+8:00）
	 */
	PARAM_D("D", "日期：使用YYYYMMDD（如20141213）的格式。时区采用北京时间（GMT+8:00）"),
	/**
	 * T:日期时间：使用yyyyMMddHHmmss（如20141213123456）的格式。时区采用北京时间（GMT+8:00）
	 */
	PARAM_T("T", "日期时间：使用yyyyMMddHHmmss（如20141213123456）的格式。时区采用北京时间（GMT+8:00）"),
	/**
	 * S:字符串：任意合法的字符串（英文，符号，中文等）
	 */
	PARAM_S("S", "字符串：任意合法的字符串（英文，符号，中文等）"),
	/**
	 * E:枚举值：见具体参数描述
	 */
	PARAM_E("E", "枚举值：见具体参数描述"),
	/**
	 * F:浮点数：不超过10亿，小数点后最多7位
	 */
	PARAM_F("F", "浮点数：不超过10亿，小数点后最多7位"),
	/**
	 * B:布尔：true为是；false为否
	 */
	PARAM_B("B", "布尔：true为是；false为否"),
	/**
	 * C:复合类型：数组内部嵌套键值对
	 */
	PARAM_C("C", "复合类型：数组内部嵌套键值对"),
	/**
	 * O:简单对象：嵌套键值对
	 */
	PARAM_O("O", "简单对象：嵌套键值对");

	private String value;
	private String text;

	private ParamEnum(String value, String text) {

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

		  for (ParamEnum v : ParamEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }

}
