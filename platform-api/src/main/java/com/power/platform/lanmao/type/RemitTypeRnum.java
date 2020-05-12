package com.power.platform.lanmao.type;

/**
 * 
 * class: RemitTypeRnum <br>
 * description: 出款类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:56:14
 */
public enum RemitTypeRnum {

	/**
	 * NORMAL:T+1 出款,T+1 当天到账
	 */
	NORMAL("NORMAL", "T+1 出款,T+1 当天到账"),
	/**
	 * URGENT:实时 D0 出款,实时到账
	 */
	URGENT("URGENT", "实时 D0 出款,实时到账"),
	/**
	 * D:普通 D0 出款,当天到账
	 */
	NORMAL_URGENT("NORMAL_URGENT", "普通 D0 出款,当天到账");

	private String value;
	private String text;

	private RemitTypeRnum(String value, String text) {

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

		  for (RemitTypeRnum v : RemitTypeRnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }

}
