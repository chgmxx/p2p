package com.power.platform.lanmao.type;

/**
 * 
 * class: InOutTypeEnum <br>
 * description: 收支类型 <br>
 * author: Roy <br>
 * date: 2019年10月12日 下午2:39:23
 */
public enum InOutTypeEnum {

	/**
	 * IN:收入
	 */
	IN("IN", "收入"),
	/**
	 * OUT:支出
	 */
	OUT("OUT", "支出");

	private String value;
	private String text;

	private InOutTypeEnum(String value, String text) {

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

		for (InOutTypeEnum v : InOutTypeEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}

}
