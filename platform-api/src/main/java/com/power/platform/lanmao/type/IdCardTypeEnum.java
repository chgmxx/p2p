package com.power.platform.lanmao.type;

/**
 * 
 * class: IdCardTypeEnum <br>
 * description: 证件类型 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:05:08
 */
public enum IdCardTypeEnum {

	/**
	 * PRC_ID:身份证
	 */
	PRC_ID("PRC_ID", "身份证"),
	/**
	 * PASSPORT:护照
	 */
	PASSPORT("PASSPORT", "护照"),
	/**
	 * COMPATRIOTS_CARD:港澳台通行证
	 */
	COMPATRIOTS_CARD("COMPATRIOTS_CARD", "港澳台通行证"),
	/**
	 * PERMANENT_RESIDENCE:外国人永久居留证
	 */
	PERMANENT_RESIDENCE("PERMANENT_RESIDENCE", "外国人永久居留证");

	private String value;
	private String text;

	private IdCardTypeEnum(String value, String text) {

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

		for (IdCardTypeEnum v : IdCardTypeEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}

}
