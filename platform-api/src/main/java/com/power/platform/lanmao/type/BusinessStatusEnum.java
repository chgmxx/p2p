package com.power.platform.lanmao.type;

/**
 * 
 * class: BusinessStatusEnum <br>
 * description: 业务处理状态 <br>
 * author: Roy <br>
 * date: 2019年9月27日 下午6:27:00
 */
public enum BusinessStatusEnum {

	/**
	 * INIT:处理失败
	 */
	INIT("INIT", "处理失败"),
	/**
	 * SUCCESS:处理成功
	 */
	SUCCESS("SUCCESS", "处理成功");

	private String value;
	private String text;

	private BusinessStatusEnum(String value, String text) {

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

		for (BusinessStatusEnum v : BusinessStatusEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}
}
