package com.power.platform.lanmao.type;

/**
 * 
 * class: ProjectTypeEnum  <br>
 * description: 标的类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum ProjectTypeEnum {

	/**
	 * STANDARDPOWDER:普通标的
	 */
	STANDARDPOWDER("STANDARDPOWDER", "普通标的"),
	/**
	 * FIDUCIARYPOWDER:受托支付标的
	 */
	FIDUCIARYPOWDER("FIDUCIARYPOWDER", "受托支付标的");
	
	private String value;
	private String text;

	private ProjectTypeEnum(String value, String text) {

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

		  for (ProjectTypeEnum v : ProjectTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
