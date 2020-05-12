package com.power.platform.lanmao.type;

/**
 * FreezeStatusEnum
 * 冻结状态
 * @author fuwei
 *
 */
public enum FreezeStatusEnum {

	/**
	 * FREEZED：尚有冻结
	 */
	FREEZED("FREEZED", "尚有冻结"),
	/**
	 * FAIL：失败
	 */
	FAIL("FAIL", "失败"),
	/**
	 * INIT ：初始化
	 */
	INIT("INIT", "初始化"),
	/**
	 * ERROR：异常
	 */
	ERROR("ERROR", "异常"),
	/**
	 * UNFREEZED：已解冻
	 */
	UNFREEZED("UNFREEZED", "已解冻");

	private String value;
	private String text;

	private FreezeStatusEnum(String value, String text) {

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

		  for (FreezeStatusEnum  v : FreezeStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
