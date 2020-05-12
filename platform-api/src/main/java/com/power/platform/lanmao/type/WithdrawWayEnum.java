package com.power.platform.lanmao.type;

/**
 * 
 * class: WithdrawWayEnum <br>
 * description: 提现方式 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午10:01:38
 */
public enum WithdrawWayEnum {

	/**
	 * NORMAL:正常提现T+1提现）T+1天到账
	 */
	NORMAL("NORMAL", "正常提现T+1提现）T+1天到账"),
	/**
	 * URGENT:加急D0提现，实时到账
	 */
	URGENT("URGENT", "加急D0提现，实时到账"),
	/**
	 * NORMAL_URGENT:智能D0提现，当天到账
	 */
	NORMAL_URGENT("NORMAL_URGENT", "智能D0提现，当天到账");

	private String value;
	private String text;

	private WithdrawWayEnum(String value, String text) {

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

		  for (WithdrawWayEnum v : WithdrawWayEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
