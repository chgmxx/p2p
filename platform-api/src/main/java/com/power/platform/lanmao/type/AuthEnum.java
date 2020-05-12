package com.power.platform.lanmao.type;

/**
 * 
 * class: AuthEnum <br>
 * description: 用户授权列表 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:19:44
 */
public enum AuthEnum {

	/**
	 * TENDER:授权出借
	 */
	TENDER("TENDER", "授权出借"),
	/**
	 * REPAYMENT:授权还款
	 */
	REPAYMENT("REPAYMENT", "授权还款"),
	/**
	 * CREDIT_ASSIGNMENT:授权债权认购
	 */
	CREDIT_ASSIGNMENT("CREDIT_ASSIGNMENT", "授权债权认购"),
	/**
	 * COMPENSATORY:自动代偿
	 */
	COMPENSATORY("COMPENSATORY", "自动代偿");;

	private String value;
	private String text;

	private AuthEnum(String value, String text) {

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

		  for (AuthEnum v : AuthEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
