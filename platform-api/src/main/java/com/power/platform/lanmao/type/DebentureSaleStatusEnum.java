package com.power.platform.lanmao.type;

/**
 * DebentureSaleStatusEnum
 * 出借转让状态
 * @author fuwei
 *
 */
public enum DebentureSaleStatusEnum {

	/**
	 * ONSALE：出让中
	 */
	ONSALE("ONSALE", "出让中"),
	/**
	 * COMPLETED：已结束
	 */
	COMPLETED("COMPLETED", "已结束");

	private String value;
	private String text;

	private DebentureSaleStatusEnum(String value, String text) {

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

		  for (DebentureSaleStatusEnum  v : DebentureSaleStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
