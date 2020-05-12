package com.power.platform.lanmao.type;

/**
 * 
 * class: FileTypeEnum  <br>
 * description: 对账文件类型 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum FileTypeEnum {

	/**
	 * RECHARGE:充值
	 */
	RECHARGE("RECHARGE", "充值"),
	/**
	 * WITHDRAW:提现
	 */
	WITHDRAW("WITHDRAW", "提现"),
	/**
	 * COMMISSION:佣金
	 */
	COMMISSION("COMMISSION", "佣金"),
	/**
	 * TRANSACTION:交易处理
	 */
	TRANSACTION("TRANSACTION", "交易处理"),
	/**
	 * BACKROLL_RECHARGE:资金回退充值
	 */
	BACKROLL_RECHARGE("BACKROLL_RECHARGE", "资金回退充值"),
	/**
	 * USER:客户信息更新
	 */
	USER("USER", "客户信息更新"),
	/**
	 * ADJUST_BALANCE:调整平台垫资额度
	 */
	ADJUST_BALANCE("ADJUST_BALANCE", "调整平台垫资额度"),
	/**
	 * ALLBALANCE:用户余额
	 */
	ALLBALANCE("ALLBALANCE", "用户余额");
	
	private String value;
	private String text;

	private FileTypeEnum(String value, String text) {

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

		  for (FileTypeEnum v : FileTypeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
