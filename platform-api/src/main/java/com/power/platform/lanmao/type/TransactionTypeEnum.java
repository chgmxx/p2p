package com.power.platform.lanmao.type;

/**
 * 
 * class: TransactionTypeEnum  <br>
 * description: 交易查询类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum TransactionTypeEnum {

	/**
	 * RECHARGE:充值
	 */
	RECHARGE("RECHARGE", "充值"),
	/**
	 * WITHDRAW:提现
	 */
	WITHDRAW("WITHDRAW", "提现"),
	/**
	 * PRETRANSACTION:交易预处理
	 */
	PRETRANSACTION("PRETRANSACTION", "交易预处理"),
	/**
	 * TRANSACTION:交易确认
	 */
	TRANSACTION("TRANSACTION", "交易确认"),
	/**
	 * FREEZE:冻结
	 */
	FREEZE("FREEZE", "冻结"),
	/**
	 * DEBENTURE_SALE:债权出让
	 */
	DEBENTURE_SALE("DEBENTURE_SALE", "债权出让"),
	/**
	 *CANCEL_PRETRANSACTION:取消预处理
	 */
	CANCEL_PRETRANSACTION("CANCEL_PRETRANSACTION", "取消预处理"),
	/**
	 * UNFREEZE:解冻
	 */
	UNFREEZE("UNFREEZE", "解冻"),
	/**
	 * INTERCEPT_WITHDRAW:提现拦截
	 */
	INTERCEPT_WITHDRAW("INTERCEPT_WITHDRAW", "提现拦截"),
	
	/**
	 * ADJUST_URGENT_BALANCE:调整平台垫资额度
	 */
	ADJUST_URGENT_BALANCE("ADJUST_URGENT_BALANCE", "调整平台垫资额度");
	
	private String value;
	private String text;

	private TransactionTypeEnum(String value, String text) {

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

		  for (TransactionTypeEnum v : TransactionTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
