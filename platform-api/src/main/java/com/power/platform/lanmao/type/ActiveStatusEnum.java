package com.power.platform.lanmao.type;

/**
 * 
 * class: ActiveStatusEnum <br>
 * description: 用户状态 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:24:22
 */
public enum ActiveStatusEnum {

	/**
	 * ACTIVATED：可用
	 */
	ACTIVATED("ACTIVATED", "可用"),
	/**
	 * DEACTIVATED：不可用
	 */
	DEACTIVATED("DEACTIVATED", "不可用");

	private String value;
	private String text;

	private ActiveStatusEnum(String value, String text) {

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

		  for (ActiveStatusEnum  v : ActiveStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
