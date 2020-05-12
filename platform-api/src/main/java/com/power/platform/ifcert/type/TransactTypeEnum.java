package com.power.platform.ifcert.type;

/**
 * 
 * class: TransactTypeEnum <br>
 * description: 交易类型枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月16日 上午9:36:30
 */
public enum TransactTypeEnum {

	// 没有此类业务，填-1.
	TRANSACT_TYPE_NAGETIVE_1("-1", "没有此类业务"),

	// 出借类.
	/**
	 * 出借（出借人购买散标产生的初始债权交易流水）
	 */
	TRANSACT_TYPE_2("2", "出借（出借人购买散标产生的初始债权交易流水）"),
	/**
	 * 收回本金（出借方收回本金）
	 */
	TRANSACT_TYPE_8("8", "收回本金"),
	/**
	 * 收回利息（出借方收回利息，不包含48-罚息）
	 */
	TRANSACT_TYPE_9("9", "收回利息"),
	/**
	 * 出借红包（出借人使用红包）/满减（出借人出借金额达到一定金额，实际支付金额减免一定的金额）
	 */
	TRANSACT_TYPE_10("10", "出借红包"),
	/**
	 * 成功转让（包括部分成功转让，与17-承接相对应）
	 */
	TRANSACT_TYPE_11("11", "成功转让"),
	/**
	 * 承接（与11-成功转让相对应）
	 */
	TRANSACT_TYPE_17("17", "承接"),
	/**
	 * 提现服务费
	 */
	TRANSACT_TYPE_23("23", "提现服务费"),
	/**
	 * 转让服务费
	 */
	TRANSACT_TYPE_40("40", "转让服务费"),
	/**
	 * 加息券金额
	 */
	TRANSACT_TYPE_41("41", "加息券金额"),
	/**
	 * 出借返现（出借人成功出借之后平台返回现金到出借人账户作为奖励）
	 */
	TRANSACT_TYPE_44("44", "出借返现"),
	/**
	 * 出借服务费（收回利息的部分不包括“出借服务费”）
	 */
	TRANSACT_TYPE_46("46", "出借服务费"),
	/**
	 * 罚息（借款人逾期后支付给出借人的罚息金额）
	 */
	TRANSACT_TYPE_48("48", "罚息"),
	/**
	 * 补偿金（借款人提前还款支付给出借人的费用）
	 */
	TRANSACT_TYPE_49("49", "补偿金"),

	// 借款类.
	/**
	 * 放款（借款人实际到账金额，不包括所有服务费金额）
	 */
	TRANSACT_TYPE_1("1", "放款"),
	/**
	 * 担保手续费（借款人支付给第三方担保公司的实际担保费用）
	 */
	TRANSACT_TYPE_3("3", "担保手续费"),
	/**
	 * 借款服务费（部分企业也叫其为手续费，即为平台收取的相关费用，不包括担保手续费等支付给第三方平台的其它费用）
	 */
	TRANSACT_TYPE_4("4", "借款服务费"),
	/**
	 * 充值
	 */
	TRANSACT_TYPE_6("6", "充值"),
	/**
	 * 提现
	 */
	TRANSACT_TYPE_7("7", "提现"),
	/**
	 * 还款本金（借款人按还款计划偿还的实际本金）
	 */
	TRANSACT_TYPE_18("18", "还款本金"),
	/**
	 * 还款利息（借款人按还款计划偿还的实际利息）
	 */
	TRANSACT_TYPE_19("19", "还款利息"),
	/**
	 * 第三方推荐费
	 */
	TRANSACT_TYPE_29("29", "第三方推荐费"),
	/**
	 * 逾期服务费(逾期后收取的服务费，包括催收服务费、罚息、逾期滞纳金等相关服务费)
	 */
	TRANSACT_TYPE_32("32", "逾期服务费"),
	/**
	 * 借款红包（借款人使用红包金额，用于替借款人还款）
	 */
	TRANSACT_TYPE_39("39", "借款红包"),
	/**
	 * 放款给消费商户（如部分平台借款人在医美、电商等借款消费的场景，借款人在接收到出借人的借款时直接打款给消费商户，
	 * useridcardhash 推送消费商户在企业平台的唯一标示）
	 */
	TRANSACT_TYPE_45("45", "放款给消费商户"),

	// 代偿类.
	/**
	 * 代偿金额–逾期第三方担保公司代偿
	 */
	TRANSACT_TYPE_27("27", "代偿金额"),
	/**
	 * 代偿金收回–逾期第三方担保公司代偿收回（第三方担保机构/保险公司收回，此时需要上报对应18 和19 的流水数据）
	 */
	TRANSACT_TYPE_30("30", "代偿金收回"),
	/**
	 * 代偿金额–逾期平台代偿
	 */
	TRANSACT_TYPE_37("37", "代偿金额"),
	/**
	 * 代偿金收回–逾期平台代偿收回（平台自己收回，此时需要上报对应18 和19 的流水数据）
	 */
	TRANSACT_TYPE_47("47", "代偿金收回"),
	/**
	 * 线下代偿收回（第三方担保公司或平台代偿线下收回）
	 */
	TRANSACT_TYPE_50("50", "线下代偿收回");

	private TransactTypeEnum(String value, String text) {

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
