package com.power.platform.lanmao.type;

/**
 * 
 * class: PayTypeEnum <br>
 * description: 网银类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum PayTypeEnum {

	/**
	 * B2C:个人网银
	 */
	B2C("B2C", "个人网银"),
	/**
	 * B2B:企业网银
	 */
	B2B("B2B", "企业网银");

	private String value;
	private String text;

	private PayTypeEnum(String value, String text) {

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

		  for (PayTypeEnum v : PayTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
