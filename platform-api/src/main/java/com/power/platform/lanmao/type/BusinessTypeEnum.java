package com.power.platform.lanmao.type;

/**
 * 
 * class: BusinessTypeEnum <br>
 * description: 业务类型 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:56:21
 */
public enum BusinessTypeEnum {

	/**
	 * TENDER:出借确认（放款）
	 */
	TENDER("TENDER", "出借确认（放款）"),
	/**
	 * REPAYMENT:还款确认
	 */
	REPAYMENT("REPAYMENT", "还款确认"),
	/**
	 * CREDIT_ASSIGNMENT:债权认购确认
	 */
	CREDIT_ASSIGNMENT("CREDIT_ASSIGNMENT", "债权认购确认"),
	/**
	 * COMPENSATORY:代偿确认
	 */
	COMPENSATORY("COMPENSATORY", "代偿确认"),
	/**
	 * COMPENSATORY_REPAYMENT:还代偿款确认
	 */
	COMPENSATORY_REPAYMENT("COMPENSATORY_REPAYMENT", "还代偿款确认"),
	/**
	 * MARKETING:营销红包
	 */
	MARKETING("MARKETING", "营销红包"),
	/**
	 * INTEREST:派息
	 */
	INTEREST("INTEREST", "派息"),
	/**
	 * INTEREST_REPAYMENT:还派息款
	 */
	INTEREST_REPAYMENT("INTEREST_REPAYMENT", "还派息款"),
	/**
	 * COMMISSION:佣金
	 */
	COMMISSION("COMMISSION", "佣金"),
	/**
	 * PROFIT:关联分润
	 */
	PROFIT("PROFIT", "关联分润"),
	/**
	 * DEDUCT:平台服务费
	 */
	DEDUCT("DEDUCT", "平台服务费"),
	/**
	 * FUNDS_TRANSFER:平台资金划拨
	 */
	FUNDS_TRANSFER("FUNDS_TRANSFER", "平台资金划拨"),
	/**
	 * FIDUCIARY_PAYMENT:受托支付
	 */
	FIDUCIARY_PAYMENT("FIDUCIARY_PAYMENT", "受托支付");

	private String value;
	private String text;

	private BusinessTypeEnum(String value, String text) {

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

		  for (BusinessTypeEnum v : BusinessTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
