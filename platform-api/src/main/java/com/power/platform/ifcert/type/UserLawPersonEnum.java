package com.power.platform.ifcert.type;

/**
 * 
 * class: UserLawpersOnEnum <br>
 * description: 法人代表姓名. <br>
 * author: Roy <br>
 * date: 2019年4月29日 上午10:33:25
 */
public enum UserLawPersonEnum {

	/**
	 * 法定代表人，自然人填写-1.
	 */
	USER_LAW_PERSON_NEGATIVE_1("-1", "自然人填写-1");

	private UserLawPersonEnum(String value, String text) {

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
