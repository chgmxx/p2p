package com.power.platform.lanmao.type;

/**
 * 
 * class: SwiftRouteEnum <br>
 * description: 快捷路由 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum SwiftRouteEnum {

	/**
	 * ORIGINAL_SWIFT:原快捷
	 */
	ORIGINAL_SWIFT("ORIGINAL_SWIFT", "原快捷"),
	/**
	 * NEW_SWIFT:新标准快捷
	 */
	NEW_SWIFT("NEW_SWIFT", "新标准快捷");

	private String value;
	private String text;

	private SwiftRouteEnum(String value, String text) {

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

		  for (SwiftRouteEnum v : SwiftRouteEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
