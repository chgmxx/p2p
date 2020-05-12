package com.power.platform.lanmao.type;

/**
 * 
 * class: UserDeviceEnum <br>
 * description: 终端类型 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:02:48
 */
public enum UserDeviceEnum {

	/**
	 * PC:PC端
	 */
	PC("PC", "PC端"),
	/**
	 * MOBILE:移动端
	 */
	MOBILE("MOBILE", "移动端");

	private String value;
	private String text;

	private UserDeviceEnum(String value, String text) {

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

		  for (UserDeviceEnum v : UserDeviceEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
