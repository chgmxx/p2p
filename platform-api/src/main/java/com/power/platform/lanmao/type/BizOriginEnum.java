package com.power.platform.lanmao.type;

/**
 * 
 * class: BizOriginEnum <br>
 * description: 业务来源 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:58:09
 */
public enum BizOriginEnum {

	/**
	 * DISPERSION:散标
	 */
	DISPERSION("DISPERSION", "散标");

	private String value;
	private String text;

	private BizOriginEnum(String value, String text) {

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

		for (BizOriginEnum v : BizOriginEnum.values()) {
			if (v.getValue().equals(value)) {
				return v.getText();
			}
		}
		return "未知结果";
	}
}
