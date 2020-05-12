package com.power.platform.lanmao.type;

/**
 * 
 * class: CompensatoryEnum <br>
 * description: 代偿业务类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum CompensatoryEnum {

	/**
	 * COMPENSATORY:普通代偿
	 */
	COMPENSATORY("COMPENSATORY", "普通代偿");

	private String value;
	private String text;

	private CompensatoryEnum(String value, String text) {

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

		  for (CompensatoryEnum v : CompensatoryEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
