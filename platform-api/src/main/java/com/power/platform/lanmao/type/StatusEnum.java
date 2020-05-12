package com.power.platform.lanmao.type;

/**
 * 
 * class: StatusEnum <br>
 * description: 提现拦截状态 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum StatusEnum {

	/**
	 * SUCCESS:拦截成功
	 */
	SUCCESS("SUCCESS", "拦截成功"),
	/**
	 * NEW_SWIFT:拦截失败
	 */
	FAIL("FAIL", "拦截失败");

	private String value;
	private String text;

	private StatusEnum(String value, String text) {

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

		  for (StatusEnum v : StatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
