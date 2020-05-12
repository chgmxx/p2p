package com.power.platform.lanmao.type;

/**
 * 
 * 提现类型
 * @author fuwei
 *
 */
public enum WithdrawFormEnum {

	/**
	 * IMMEDIATE:直接提现
	 */
	IMMEDIATE("IMMEDIATE", "直接提现"),
	/**
	 * CONFIRMED:待确认提现
	 */
	CONFIRMED("CONFIRMED", "待确认提现");

	private String value;
	private String text;

	private WithdrawFormEnum(String value, String text) {

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

		  for (WithdrawFormEnum v : WithdrawFormEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
