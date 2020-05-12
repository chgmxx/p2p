package com.power.platform.lanmao.type;

/**
 * 
 * class: UserTypeEnum <br>
 * description: 用户类型 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:09:00
 */
public enum UserTypeEnum {
	

	/**
	 * PERSONAL:个人用户
	 */
	PERSONAL("PERSONAL", "个人用户"),
	/**
	 * ORGANIZATION:企业用户
	 */
	ORGANIZATION("ORGANIZATION", "企业用户");

	private String value;
	private String text;

	private UserTypeEnum(String value, String text) {

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

		  for (UserTypeEnum v : UserTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
