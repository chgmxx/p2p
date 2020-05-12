package com.power.platform.lanmao.type;

/**
 * 
 * class: ProjectStatusEnum  <br>
 * description: 标的状态 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum ProjectStatusEnum {

	/**
	 * COLLECTING:募集中
	 */
	COLLECTING("COLLECTING", "募集中"),
	/**
	 * REPAYING:还款中
	 */
	REPAYING("REPAYING", "还款中"),
	/**
	 * FINISH:已截标
	 */
	FINISH("FINISH", "已截标"),
	/**
	 * MISCARRY流标
	 */
	MISCARRY("MISCARRY", "流标")
	;
	
	private String value;
	private String text;

	private ProjectStatusEnum(String value, String text) {

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

		  for (ProjectStatusEnum v : ProjectStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
