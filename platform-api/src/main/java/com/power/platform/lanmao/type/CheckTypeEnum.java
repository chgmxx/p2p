package com.power.platform.lanmao.type;

/**
 * 
 * class: CheckEnum <br>
 * description: 开户鉴权验证类型 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:08:23
 */
public enum CheckTypeEnum {

	/**
	 * LIMIT:强制四要素
	 */
	LIMIT("LIMIT", "强制四要素");

	private String value;
	private String text;

	private CheckTypeEnum(String value, String text) {

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

		  for (CheckTypeEnum v : CheckTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
