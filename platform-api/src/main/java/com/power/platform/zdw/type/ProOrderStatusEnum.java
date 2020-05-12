package com.power.platform.zdw.type;

/**
 * 
 * class: ProOrderStatusEnum <br>
 * description: 散标订单状态枚举类. <br>
 * author: Roy <br>
 * date: 2019年7月13日 上午11:23:46
 */
public enum ProOrderStatusEnum {

	PRO_ORDER_STATUS_00("00", "登记成功"), PRO_ORDER_STATUS_01("01", "等待登记"), PRO_ORDER_STATUS_02("02", "登记失败");

	private ProOrderStatusEnum(String value, String text) {

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
