package com.power.platform.zdw.type;

/**
 * 
 * 类: PledgorTypeEnum <br>
 * 描述: 出质人类型 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 下午8:21:37
 */
public enum PledgorTypeEnum {

	/**
	 * 01-金融机构
	 */
	PLEDGOR_TYPE_01("01", "金融机构"),
	/**
	 * 02-企业
	 */
	PLEDGOR_TYPE_02("02", "企业"),
	/**
	 * 03-机关事业单位
	 */
	PLEDGOR_TYPE_03("03", "机关事业单位"),
	/**
	 * 04-个人
	 */
	PLEDGOR_TYPE_04("04", "个人"),
	/**
	 * 06-个体工商户
	 */
	PLEDGOR_TYPE_06("06", "个体工商户"),
	/**
	 * 09-其他
	 */
	PLEDGOR_TYPE_09("09", "其他");

	private PledgorTypeEnum(String value, String text) {

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
