package com.power.platform.ifcert.type;

/**
 * 
 * class: InfTypeEnum <br>
 * description: 接口类型对照表枚举. <br>
 * author: Roy <br>
 * date: 2019年5月7日 下午2:24:34
 */
public enum InfTypeEnum {

	/**
	 * 用户接口.
	 */
	INF_TYPE_1("1", "用户接口"),
	/**
	 * 散标接口.
	 */
	INF_TYPE_2("2", "散标接口"),
	/**
	 * 散标状态接口.
	 */
	INF_TYPE_6("6", "散标状态接口"),
	/**
	 * 还款计划接口.
	 */
	INF_TYPE_81("81", "还款计划接口"),
	/**
	 * 初始债权接口.
	 */
	INF_TYPE_82("82", "初始债权接口"),
	/**
	 * 转让信息接口.
	 */
	INF_TYPE_83("83", "转让信息接口"),
	/**
	 * 转让状态接口.
	 */
	INF_TYPE_84("84", "转让状态接口"),
	/**
	 * 承接转让接口.
	 */
	INF_TYPE_85("85", "承接转让接口"),
	/**
	 * 交易流水接口.
	 */
	INF_TYPE_4("4", "交易流水接口"),
	/**
	 * 产品信息接口.
	 */
	INF_TYPE_86("86", "产品信息接口"),
	/**
	 * 产品配置接口.
	 */
	INF_TYPE_87("87", "产品配置接口"),
	/**
	 * 投资明细接口.
	 */
	INF_TYPE_88("88", "投资明细接口");

	private InfTypeEnum(String value, String text) {

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

	/**
	 * 
	 * methods: getTextByValue <br>
	 * description: 获取text根据value. <br>
	 * author: Roy <br>
	 * date: 2019年7月26日 上午11:23:24
	 * 
	 * @param valueCode
	 * @return
	 */
	public static String getTextByValue(String valueCode) {

		String text = "";
		for (InfTypeEnum ite : InfTypeEnum.values()) {
			if (valueCode.equals(ite.getValue())) {
				text = ite.getText();
				return text;
			}
		}
		return text;
	}

}
