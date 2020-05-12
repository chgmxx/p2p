package com.power.platform.lanmao.type;

/**
 * FreezeStatusEnum
 * 打款状态
 * @author fuwei
 *
 */
public enum RemitStatusEnum {

	/**
	 * REMITING：打款中
	 */
	REMITING("REMITING", "打款中"),
	
	/**
	 * INIT ：打款订单已生成
	 */
	INIT("INIT", "打款订单已生成"),
	/**
	 * FAIL：打款失败
	 */
	FAIL("FAIL", "打款失败"),
	/**
	 * SUCCESS：打款成功
	 */
	
	SUCCESS("SUCCESS", "打款成功");

	private String value;
	private String text;

	private RemitStatusEnum(String value, String text) {

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

		  for (RemitStatusEnum  v : RemitStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
