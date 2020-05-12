package com.power.platform.lanmao.type;

/**
 * 
 * class: UniversalCodeEnum <br>
 * description: 通用返回码 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午8:24:50
 */
public enum UniversalCodeEnum {

	/**
	 * 0:调用成功
	 */
	UNIVERSAL_CODE_0("0", "调用成功"),
	/**
	 * 1:调用失败，失败原因请查看【调用失败 错误码】
	 */
	UNIVERSAL_CODE_1("1", "调用失败，失败原因请查看【调用失败 错误码】");

	private String value;
	private String text;

	private UniversalCodeEnum(String value, String text) {

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

		for (UniversalCodeEnum v : UniversalCodeEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}
}
