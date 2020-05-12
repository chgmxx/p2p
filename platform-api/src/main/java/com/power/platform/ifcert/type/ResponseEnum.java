package com.power.platform.ifcert.type;

/**
 * 
 * class: ResponseEnum <br>
 * description: 响应枚举参数列表. <br>
 * author: Roy <br>
 * date: 2019年4月30日 上午9:56:50
 */
public enum ResponseEnum {

	/**
	 * 数据已成功上报，正在等待处理.
	 */
	RESPONSE_CODE_MSG_0000("0000", "数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态"),
	/**
	 * 成功.
	 */
	RESPONSE_CODE_MSG_00("00", "成功"),
	/**
	 * 处理中.
	 */
	RESPONSE_CODE_MSG_01("01", "处理中"),
	/**
	 * 失败.
	 */
	RESPONSE_CODE_MSG_02("02", "失败"),
	/**
	 * 缺少必要参数.
	 */
	RESPONSE_CODE_MSG_03("03", "缺少必要参数，参数错误"),
	/**
	 * 程序异常.
	 */
	RESPONSE_CODE_MSG_04("04", "程序异常");

	private ResponseEnum(String value, String text) {

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
