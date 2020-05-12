package com.power.platform.lanmao.type;

/**
 * 
 * class: RechargeWayEnum   <br>
 * description: 支付方式 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum RechargeWayEnum {

	/**
	 * WEB:网银
	 */
	WEB("WEB", "网银"),
	/**
	 * SWIFT:快捷支付
	 */
	SWIFT("SWIFT", "快捷支付"),
	/**
	 * BANK:银行转账充值,仅适用迁移场景,调用单笔充值订单查询接口返回
	 */
	BANK("BANK", "银行转账充值,仅适用迁移场景,调用单笔充值订单查询接口返回"),
	/**
	 * BACKROLL:资金回退充值
	 */
	BACKROLL("BACKROLL", "资金回退充值")
	;
	
	private String value;
	private String text;

	private RechargeWayEnum(String value, String text) {

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

		  for (RechargeWayEnum v : RechargeWayEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
