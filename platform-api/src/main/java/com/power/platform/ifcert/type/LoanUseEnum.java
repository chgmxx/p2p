package com.power.platform.ifcert.type;

/**
 * 
 * class: LoanUseEnum <br>
 * description: 借款用途枚举类. <br>
 * author: Roy <br>
 * date: 2019年5月8日 下午3:43:14
 */
public enum LoanUseEnum {

	/**
	 * 个人消费
	 */
	LOAN_USE_1("1", "个人消费"),
	/**
	 * 中小企业（企业方借款用途，如：开分公司，采购等）
	 */
	LOAN_USE_2("2", "中小企业（企业方借款用途，如：开分公司，采购等）"),
	/**
	 * 房地产（房地产开发、房地产公司运营等）
	 */
	LOAN_USE_3("3", "房地产（房地产开发、房地产公司运营等）"),
	/**
	 * 金融市场
	 */
	LOAN_USE_4("4", "金融市场"),
	/**
	 * 交通
	 */
	LOAN_USE_5("5", "交通"),
	/**
	 * 农业
	 */
	LOAN_USE_6("6", "农业"),
	/**
	 * 其它
	 */
	LOAN_USE_7("7", "其它"),
	/**
	 * 个人买房、租房、装修
	 */
	LOAN_USE_8("8", "个人买房、租房、装修"),
	/**
	 * 个人买车
	 */
	LOAN_USE_9("9", "个人买车"),
	/**
	 * 个人借款其它用途
	 */
	LOAN_USE_10("10", "个人借款其它用途"),
	/**
	 * 批发和零售业
	 */
	LOAN_USE_11("11", "批发和零售业"),
	/**
	 * 建筑业
	 */
	LOAN_USE_12("12", "建筑业"),
	/**
	 * 租赁和商务服务业
	 */
	LOAN_USE_13("13", "租赁和商务服务业"),
	/**
	 * 制造业
	 */
	LOAN_USE_14("14", "制造业"),
	/**
	 * 信息传输、软件和信息技术服务业
	 */
	LOAN_USE_15("15", "信息传输、软件和信息技术服务业"),
	/**
	 * 住宿和餐饮业
	 */
	LOAN_USE_16("16", "住宿和餐饮业"),
	/**
	 * 科学研究和技术服务业
	 */
	LOAN_USE_17("17", "科学研究和技术服务业"),
	/**
	 * 文化、体育和娱乐业
	 */
	LOAN_USE_18("18", "文化、体育和娱乐业"),
	/**
	 * 居民服务、修理和其他服务业
	 */
	LOAN_USE_19("19", "居民服务、修理和其他服务业"),
	/**
	 * 卫生和社会工作
	 */
	LOAN_USE_20("20", "卫生和社会工作"),
	/**
	 * 公共管理、社会保障和社会组织
	 */
	LOAN_USE_21("21", "公共管理、社会保障和社会组织"),
	/**
	 * 水利、环境和公共设施管理业
	 */
	LOAN_USE_22("22", "水利、环境和公共设施管理业"),
	/**
	 * 教育
	 */
	LOAN_USE_23("23", "教育"),
	/**
	 * 采矿业
	 */
	LOAN_USE_24("24", "采矿业"),
	/**
	 * 电力、热力、燃气及水生产和供应业
	 */
	LOAN_USE_25("25", "电力、热力、燃气及水生产和供应业");

	private LoanUseEnum(String value, String text) {

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
