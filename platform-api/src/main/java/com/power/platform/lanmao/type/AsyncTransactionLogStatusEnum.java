package com.power.platform.lanmao.type;

/**
 * 
 * class: AsyncTransactionLogStatusEnum <br>
 * description: 批量交易明细订单状态 <br>
 * author: Roy <br>
 * date: 2019年10月6日 下午5:09:32
 */
public enum AsyncTransactionLogStatusEnum {
	/**
	 * INIT:处理中
	 */
	INIT("INIT", "处理中"),
	/**
	 * SUCCESS:处理成功
	 */
	SUCCESS("SUCCESS", "处理成功"),
	/**
	 * FAIL:处理失败
	 */
	FAIL("FAIL", "处理失败");

	private String value;
	private String text;

	private AsyncTransactionLogStatusEnum(String value, String text) {

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

		for (AsyncTransactionLogStatusEnum v : AsyncTransactionLogStatusEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}
}
