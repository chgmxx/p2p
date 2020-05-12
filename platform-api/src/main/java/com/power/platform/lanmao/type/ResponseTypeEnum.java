package com.power.platform.lanmao.type;

/**
 * 
 * class: ResponseTypeEnum <br>
 * description: 回调类型 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:05:14
 */
public enum ResponseTypeEnum {

	/**
	 * CALLBACK:浏览器返回
	 */
	CALLBACK("CALLBACK", "浏览器返回"),
	/**
	 * NOTIFY:服务器异步通知
	 */
	NOTIFY("NOTIFY", "服务器异步通知");

	private String value;
	private String text;

	private ResponseTypeEnum(String value, String text) {

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

		  for (ResponseTypeEnum v : ResponseTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
