package com.power.platform.ifcert.type;

/**
 * 
 * class: ProductStatusEnum <br>
 * description: 散标流转状态. <br>
 * 0-初始公布／1-满标（截标）／2-流标（弃标）／3-还款结束／4-逾期／5-还款中／6-筹标中／8-坏帐／9-放款/10-线下销账。<br>
 * 说明：网贷机构如果有新增状态需求，可以与应急中心沟通，确认后可以扩展<br>
 * author: Roy <br>
 * date: 2019年5月10日 下午5:09:27
 */
public enum ProductStatusEnum {

	/**
	 * 初始公布
	 */
	PRODUCT_STATUS_0("0", "初始公布"),
	/**
	 * 满标（截标）
	 */
	PRODUCT_STATUS_1("1", "满标（截标）"),
	/**
	 * 流标（弃标）
	 */
	PRODUCT_STATUS_2("2", "流标（弃标）"),
	/**
	 * 还款结束
	 */
	PRODUCT_STATUS_3("3", "还款结束"),
	/**
	 * 逾期
	 */
	PRODUCT_STATUS_4("4", "逾期"),
	/**
	 * 还款中
	 */
	PRODUCT_STATUS_5("5", "还款中"),
	/**
	 * 筹标中
	 */
	PRODUCT_STATUS_6("6", "筹标中"),
	/**
	 * 坏帐
	 */
	PRODUCT_STATUS_8("8", "坏帐"),
	/**
	 * 放款
	 */
	PRODUCT_STATUS_9("9", "放款"),
	/**
	 * 线下销账
	 */
	PRODUCT_STATUS_10("10", "线下销账");

	private ProductStatusEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	private String value;
	private String text;

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

}
