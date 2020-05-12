package com.power.platform.lanmao.type;

/**
 * 
 * class: BizTypeEnum <br>
 * description: 交易类型 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:48:28
 */
public enum BizTypeEnum {

	/**
	 * TENDER:出借
	 */
	TENDER("TENDER", "出借"),
	/**
	 * REPAYMENT：还款
	 */
	REPAYMENT("REPAYMENT", "还款"),
	/**
	 * CREDIT_ASSIGNMENT:债权认购
	 */
	CREDIT_ASSIGNMENT("CREDIT_ASSIGNMENT", "债权认购"),
	/**
	 * PLATFORM_INDEPENDENT_PROFIT:独立分润
	 */
	PLATFORM_INDEPENDENT_PROFIT("PLATFORM_INDEPENDENT_PROFIT", "独立分润"),
	/**
	 * COMPENSATORY:直接代偿
	 */
	COMPENSATORY("COMPENSATORY", "直接代偿"),
	/**
	 * INDIRECT_COMPENSATORY:间接代偿
	 */
	INDIRECT_COMPENSATORY("INDIRECT_COMPENSATORY", "间接代偿"),
	/**
	 * MARKETING:平台营销款
	 */
	MARKETING("MARKETING", "平台营销款"),
	/**
	 * PLATFORM_SERVICE_DEDUCT:收费
	 */
	PLATFORM_SERVICE_DEDUCT("PLATFORM_SERVICE_DEDUCT", "收费"),
	/**
	 * FUNDS_TRANSFER:平台资金划拨
	 */
	FUNDS_TRANSFER("FUNDS_TRANSFER", "平台资金划拨"),
	/**
	 * PRINCIPAL:平台出借人收回本金
	 */
	PRINCIPAL("PRINCIPAL", "本金"),
	/**
	 * INCOME:平台出借人收回利息
	 */
	INCOME("INCOME", "利息");

	private String value;
	private String text;

	private BizTypeEnum(String value, String text) {

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

		for (BizTypeEnum v : BizTypeEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}
}
