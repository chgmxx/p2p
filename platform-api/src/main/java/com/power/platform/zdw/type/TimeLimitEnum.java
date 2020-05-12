package com.power.platform.zdw.type;

/**
 * 
 * 类: TimeLimitEnum <br>
 * 描述: 登记期限 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 下午9:07:22
 */
public enum TimeLimitEnum {

	/**
	 * 0.5-六个月
	 */
	TIME_LIMIT_0_5("0.5", "六个月"),
	/**
	 * 1.0-1年
	 */
	TIME_LIMIT_1_0("1.0", "1年"),
	/**
	 * 2.0-2年
	 */
	TIME_LIMIT_2_0("2.0", "2年"),
	/**
	 * 3.0-3年
	 */
	TIME_LIMIT_3_0("3.0", "3年"),
	/**
	 * 4.0-4年
	 */
	TIME_LIMIT_4_0("4.0", "4年"),
	/**
	 * 5.0-5年
	 */
	TIME_LIMIT_5_0("5.0", "5年");

	private TimeLimitEnum(String value, String text) {

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
