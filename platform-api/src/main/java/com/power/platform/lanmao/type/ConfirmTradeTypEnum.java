package com.power.platform.lanmao.type;

/**
 * 
 * class: ConfirmTradeTypEnum <br>
 * description: 预处理业务类型 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:42:47
 */
public enum ConfirmTradeTypEnum {

	/**
	 * TENDER:出借
	 */
	TENDER("TENDER", "出借"),
	/**
	 * REPAYMENT:还款
	 */
	REPAYMENT("REPAYMENT", "还款"),
	/**
	 * CREDIT_ASSIGNMENT:债权认购
	 */
	CREDIT_ASSIGNMENT("CREDIT_ASSIGNMENT", "债权认购"),
	/**
	 * COMPENSATORY:代偿
	 */
	COMPENSATORY("COMPENSATORY", "代偿");

	private String value;
	private String text;

	private ConfirmTradeTypEnum(String value, String text) {

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

		  for (ConfirmTradeTypEnum v : ConfirmTradeTypEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
