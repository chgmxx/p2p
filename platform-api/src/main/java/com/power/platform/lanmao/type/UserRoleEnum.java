package com.power.platform.lanmao.type;

/**
 * 
 * class: UserRoleEnum <br>
 * description: 用户角色 <br>
 * author: Roy <br>
 * date: 2019年9月18日 下午9:01:09
 */
public enum UserRoleEnum {
	/**
	 * GUARANTEECORP:担保机构
	 */
	GUARANTEECORP("GUARANTEECORP", "担保机构"),
	/**
	 * INVESTOR:出借人
	 */
	INVESTOR("INVESTOR", "出借人"),
	/**
	 * BORROWERS:借款人
	 */
	BORROWERS("BORROWERS", "借款人"),
	/**
	 * COLLABORATOR:合作机构
	 */
	COLLABORATOR("COLLABORATOR", "合作机构"),
	/**
	 * SUPPLIER:供应商
	 */
	SUPPLIER("SUPPLIER", "供应商"),
	/**
	 * PLATFORM_MARKETING:平台营销款账户
	 */
	PLATFORM_MARKETING("PLATFORM_MARKETING", "平台营销款账户"),
	/**
	 * PLATFORM_INCOME:平台收入账户
	 */
	PLATFORM_INCOME("PLATFORM_INCOME", "平台收入账户"),
	/**
	 * PLATFORM_INTEREST:平台派息账户
	 */
	PLATFORM_INTEREST("PLATFORM_INTEREST", "平台派息账户"),
	/**
	 * PLATFORM_FUNDS_TRANSFER:平台总账户
	 */
	PLATFORM_FUNDS_TRANSFER("PLATFORM_FUNDS_TRANSFER", "平台总账户"),
	/**
	 * PLATFORM_URGENT:平台垫资账户
	 */
	PLATFORM_URGENT("PLATFORM_URGENT", "平台垫资账户");

	private String value;
	private String text;

	private UserRoleEnum(String value, String text) {

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

		  for (UserRoleEnum v : UserRoleEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
