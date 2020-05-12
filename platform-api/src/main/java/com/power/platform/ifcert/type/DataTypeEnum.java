package com.power.platform.ifcert.type;

/**
 * 
 * class: DataTypeEnum <br>
 * description: 接口数据类型枚举. <br>
 * author: Roy <br>
 * date: 2019年5月7日 下午2:29:08
 */
public enum DataTypeEnum {

	/**
	 * 调试数据.
	 */
	DATA_TYPE_0("0", "调试数据"),
	/**
	 * 正式数据.
	 */
	DATA_TYPE_1("1", "正式数据");

	private DataTypeEnum(String value, String text) {

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
