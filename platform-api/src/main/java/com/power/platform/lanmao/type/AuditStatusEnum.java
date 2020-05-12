package com.power.platform.lanmao.type;

/**
 * 
 * class: AuditStatusEnum <br>
 * description: 审核状态 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午9:39:32
 */
public enum AuditStatusEnum {

	/**
	 * AUDIT：审核中
	 */
	AUDIT("AUDIT", "审核中"),
	/**
	 * PASSED：审核通过
	 */
	PASSED("PASSED", "审核通过"),
	/**
	 * BACK：审核回退
	 */
	BACK("BACK", "审核回退"),
	/**
	 * REFUSED:审核拒绝
	 */
	REFUSED("REFUSED", "审核拒绝");

	private String value;
	private String text;

	private AuditStatusEnum(String value, String text) {

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

		  for (AuditStatusEnum  v : AuditStatusEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
