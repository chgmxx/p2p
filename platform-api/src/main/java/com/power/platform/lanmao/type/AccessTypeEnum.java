package com.power.platform.lanmao.type;

/**
 * 
 * class: AccessTypeEnum  <br>
 * description: 鉴权通过类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum AccessTypeEnum {

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

	private AccessTypeEnum(String value, String text) {

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

		  for (AccessTypeEnum v : AccessTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
