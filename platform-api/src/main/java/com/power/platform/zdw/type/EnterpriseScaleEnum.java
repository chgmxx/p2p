package com.power.platform.zdw.type;

/**
 * 
 * 类: EnterpriseScaleEnum <br>
 * 描述: 企业规模 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 上午11:08:31
 */
public enum EnterpriseScaleEnum {

	/**
	 * 10-大型企业
	 */
	ENTERPRISE_SCALE_10("10", "大型企业"),
	/**
	 * 20-中型企业
	 */
	ENTERPRISE_SCALE_20("20", "中型企业"),
	/**
	 * 30-小型企业
	 */
	ENTERPRISE_SCALE_30("30", "小型企业"),
	/**
	 * 40-微型企业
	 */
	ENTERPRISE_SCALE_40("40", "微型企业");

	private EnterpriseScaleEnum(String value, String text) {

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
