package com.power.platform.lanmao.type;

/**
 * 
 * class: CheckEnum <br>
 * description: 鉴权通过类型 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:08:23
 */
public enum CheckEnum {

	/**
	 * FULL_CHECKED:四要素鉴权认证通过
	 */
	FULL_CHECKED("FULL_CHECKED", "四要素鉴权认证通过"),
	/**
	 * NOT_AUTH:未鉴权
	 */
	NOT_AUTH("NOT_AUTH", "未鉴权"),
	/**
	 * AUDIT_AUTH:特殊用户认证
	 */
	AUDIT_AUTH("AUDIT_AUTH", "特殊用户认证"),
	/**
	 * PART_CHECKED:企业用户认证
	 */
	PART_CHECKED("PART_CHECKED", "企业用户认证");

	private String value;
	private String text;

	private CheckEnum(String value, String text) {

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

		  for (CheckEnum v : CheckEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
