package com.power.platform.lanmao.type;

/**
 * 
 * class: ProductTypeEnum <br>
 * description: 产品类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum ProductTypeEnum {

	/**
	 * AUTOMATICPRODUCT:自动投标类产品
	 */
	AUTOMATICPRODUCT("AUTOMATICPRODUCT", "自动投标类产品"),
	/**
	 * COMMONPRODUCT:普通投标类产品
	 */
	COMMONPRODUCT("COMMONPRODUCT", "普通投标类产品");

	private String value;
	private String text;

	private ProductTypeEnum(String value, String text) {

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

		  for (ProductTypeEnum v : ProductTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
