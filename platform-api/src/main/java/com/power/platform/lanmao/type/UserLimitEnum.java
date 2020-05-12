package com.power.platform.lanmao.type;

/**
 * 
 * class: UserLimitEnum <br>
 * description: 验证身份证唯一性 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:14:29
 */
public enum UserLimitEnum {

	/**
	 * ID_CARD_NO_UNIQUE:身份证唯一性，固定值：ID_CARD_NO_UNIQUE
	 */
	ID_CARD_NO_UNIQUE("ID_CARD_NO_UNIQUE", "身份证唯一性，固定值：ID_CARD_NO_UNIQUE");

	private String value;
	private String text;

	private UserLimitEnum(String value, String text) {

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

		  for (UserLimitEnum v : UserLimitEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
