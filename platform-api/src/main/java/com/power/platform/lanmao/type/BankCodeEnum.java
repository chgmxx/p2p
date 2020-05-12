package com.power.platform.lanmao.type;

/**
 * 
 * class: BankCodeEnum <br>
 * description: 银行编码 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午8:48:54
 */
public enum BankCodeEnum {

	/**
	 * ABOC:中国农业银行
	 */
	ABOC("ABOC", "中国农业银行"),
	/**
	 * BKCH:中国银行
	 */
	BKCH("BKCH", "中国银行"),
	/**
	 * CIBK:中信银行
	 */
	CIBK("CIBK", "中信银行"),
	/**
	 * EVER:中国光大银行
	 */
	EVER("EVER", "中国光大银行"),
	/**
	 * FJIB：兴业银行
	 */
	FJIB("FJIB", "兴业银行"),
	/**
	 * GDBK:广发银行
	 */
	GDBK("GDBK", "广发银行"),
	/**
	 * HXBK：华夏银行
	 */
	HXBK("HXBK", "华夏银行"),
	/**
	 * ICBK:中国工商银行
	 */
	ICBK("ICBK", "中国工商银行"),
	/**
	 * MSBC:中国民生银行
	 */
	MSBC("MSBC", "中国民生银行"),
	/**
	 * PCBC:中国建设银行
	 */
	PCBC("PCBC", "中国建设银行"),
	/**
	 * PSBC：中国邮政储蓄银行
	 */
	PSBC("PSBC", "中国邮政储蓄银行"),
	/**
	 * SZDB:平安银行
	 */
	SZDB("SZDB", "平安银行"),
	/**
	 * SPDB：浦发银行
	 */
	SPDB("SPDB", "浦发银行"),
	/**
	 * BJCN:北京银行
	 */
	BJCN("BJCN", "北京银行"),
	/**
	 * CMBC:招商银行
	 */
	CMBC("CMBC", "招商银行"),
	/**
	 * COMM:交通银行
	 */
	COMM("COMM", "交通银行"),
	/**
	 * HKBC:海口联合农商银行
	 */
	HKBC("HKBC", "海口联合农商银行");

	private String value;
	private String text;

	private BankCodeEnum(String value, String text) {

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

		  for (BankCodeEnum v : BankCodeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
	public static String getTextByText(String text) {

		  for (BankCodeEnum v : BankCodeEnum.values()) {
		   if (v.getText().equals(text)) {
		    return v.getValue();
		   }
		  }
		  return "";
	}
}
