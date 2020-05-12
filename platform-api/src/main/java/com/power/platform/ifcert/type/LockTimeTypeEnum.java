package com.power.platform.ifcert.type;

/**
 * 
 * class: LockTimeTypeEnum <br>
 * description: 不允许债权妆转让的锁定截至时间. <br>
 * author: Roy <br>
 * date: 2019年5月14日 下午5:14:47
 */
public enum LockTimeTypeEnum {

	/**
	 * 不允许债权装让.
	 */
	LOCK_TIME_TYPE_NEGATIVE_1("-1", "不允许债权转让");

	private LockTimeTypeEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	private String value;
	private String text;

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

}
