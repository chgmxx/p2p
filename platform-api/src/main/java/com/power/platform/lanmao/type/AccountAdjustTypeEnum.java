package com.power.platform.lanmao.type;

/**
 * AccountAdjustTypeEnum
 * 垫资额度调整类型
 * @author fuwei
 *
 */
public enum AccountAdjustTypeEnum {

	/**
	 * ADJUST_INCREASE ：垫资账户调增
	 */
	ADJUST_INCREASE ("ADJUST_INCREASE ", "出让中"),
	/**
	 * ADJUST_REDUCE：垫资账户调减
	 */
	ADJUST_REDUCE("ADJUST_REDUCE", "垫资账户调减");

	private String value;
	private String text;

	private AccountAdjustTypeEnum(String value, String text) {

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

		  for (AccountAdjustTypeEnum  v : AccountAdjustTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
