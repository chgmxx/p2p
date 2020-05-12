package com.power.platform.lanmao.type;

/**
 * 
 * class: PlatformNoEnum  <br>
 * description: 平台账户编号 <br>
 * author: Mr.fu <br>
 * date: 2019年9月19日 下午8:55:26
 */
public enum PlatformNoEnum {

	/**
	 * SYS_GENERATE_000:平台总账户(系统自动生成平台用户编号)
	 */
	SYS_GENERATE_000("SYS_GENERATE_000", "平台总账户(系统自动生成平台用户编号)"),
	/**
	 * SYS_GENERATE_002:平台营销款账户(系统自动生成平台用户编号)
	 */
	SYS_GENERATE_002("SYS_GENERATE_002", "平台营销款账户(系统自动生成平台用户编号)"),
	/**
	 * SYS_GENERATE_004:平台收入账户(系统自动生成平台用户编号)
	 */
	SYS_GENERATE_004("SYS_GENERATE_004", "平台收入账户(系统自动生成平台用户编号)"),
	/**
	 * SYS_GENERATE_005:平台派息账户(系统自动生成平台用户编号)
	 */
	SYS_GENERATE_005("SYS_GENERATE_005", "平台派息账户(系统自动生成平台用户编号)"),
	/**
	 * SYS_GENERATE_007:平台垫资账户(系统自动生成平台用户编号)
	 */
	SYS_GENERATE_007("SYS_GENERATE_007", "平台垫资账户(系统自动生成平台用户编号)");
	
	private String value;
	private String text;

	private PlatformNoEnum(String value, String text) {

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

		  for (PlatformNoEnum v : PlatformNoEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
