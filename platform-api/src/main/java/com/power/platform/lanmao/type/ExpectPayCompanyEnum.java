package com.power.platform.lanmao.type;

/**
 * 
 * class: ExpectPayCompanyEnum  <br>
 * description: 支付公司 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum ExpectPayCompanyEnum {
	
	/**
	 * QIANBAO: 钱宝支付
	 */
	QIANBAO("QIANBAO", "钱宝支付"),
	/**
	 * YEEPAY:易宝支付
	 */
	YEEPAY("YEEPAY", "易宝支付"),
	/**
	 * UCFPAY:先锋支付
	 */
	UCFPAY("UCFPAY", "先锋支付"),
	/**
	 * LIANLIAN:连连支付
	 */
	LIANLIAN("LIANLIAN", "连连支付"),
	/**
	 * FUIOU:富友支付
	 */
	FUIOU("FUIOU", "富友支付"),
	/**
	 * ALLINPAY:通联支付
	 */
	ALLINPAY("ALLINPAY", "通联支付"),
	/**
	 * CHINAGPAY:爱农支付
	 */
	CHINAGPAY("CHINAGPAY", "爱农支付"),
	/**
	 * BAOFOO:快钱支付
	 */
	BAOFOO("BAOFOO", "快钱支付"),
	/**
	 * BILL99:通联支付
	 */
	BILL99("BILL99", "通联支付"),
	/**
	 * SUMAPAY:丰付
	 */
	SUMAPAY("SUMAPAY", "丰付"),
	/**
	 * REAPAL:融宝支付
	 */
	REAPAL("REAPAL", "融宝支付"),
	/**
	 * REAPAL:国付宝
	 */
	GOPAY("GOPAY", "国付宝"),
	/**
	 * REAPAL:京东支付
	 */
	JDPAY("JDPAY", "京东支付"),
	/**
	 * UMPAY:联动优势
	 */
	UMPAY("UMPAY", "联动优势"),
	/**
	 * HELIPAY:合利宝
	 */
	HELIPAY("HELIPAY", "合利宝"),
	/**
	 * KFTPAY:快付通
	 */
	KFTPAY("KFTPAY", "快付通"),
	/**
	 * NEWPAY:新生支付
	 */
	NEWPAY("NEWPAY", "新生支付"),
	/**
	 *BFBPAY:邦付宝
	 */
	BFBPAY("NEWPAY", "邦付宝"),
	/**
	 * EPAY:网易支付
	 */
	ACCEPT_FAIL("EPAY", "网易支付"),;

	private String value;
	private String text;

	private ExpectPayCompanyEnum(String value, String text) {

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

		  for (ExpectPayCompanyEnum v : ExpectPayCompanyEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
