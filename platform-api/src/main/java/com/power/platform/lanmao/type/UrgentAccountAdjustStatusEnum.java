package com.power.platform.lanmao.type;

/**
 * UrgentAccountAdjustStatusEnum
 * 交易处理状态
 * @author fuwei
 *
 */
public enum UrgentAccountAdjustStatusEnum {

	/**
	 * ACCEPT：已受理
	 */
	ACCEPT("ACCEPT", "已受理"),
	/**
	 * ACCEPT_FAIL：受理失败
	 */
	ACCEPT_FAIL("ACCEPT_FAIL", "受理失败"),
	/**
	 * REMITING：转账中
	 */
	REMITING("REMITING", "转账中"),
	/**
	 * SUCCESS：转账成功
	 */
	SUCCESS("SUCCESS", "转账成功"),
	/**
	 * FAIL：转账失败
	 */
	FAIL("FAIL", "转账失败");

	private String value;
	private String text;

	private UrgentAccountAdjustStatusEnum(String value, String text) {

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

		  for (UrgentAccountAdjustStatusEnum  v : UrgentAccountAdjustStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
