package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: Industry <br>
 * 描述: 行业. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:30:06
 */
public class Industry {

	// 行业门类code.
	private String IndustryCode;
	// 行业门类描述.
	private String Industry;
	// 行业大类code.
	private String SubIndustryCode;
	// 行业大类描述.
	private String SubIndustry;
	// 行业中类code.
	private String MiddleCategoryCode;
	// 行业中类描述.
	private String MiddleCategory;
	// 行业小类code.
	private String SmallCategoryCode;
	// 行业小类描述.
	private String SmallCategory;

	public String getIndustryCode() {

		return IndustryCode;
	}

	public void setIndustryCode(String industryCode) {

		IndustryCode = industryCode;
	}

	public String getIndustry() {

		return Industry;
	}

	public void setIndustry(String industry) {

		Industry = industry;
	}

	public String getSubIndustryCode() {

		return SubIndustryCode;
	}

	public void setSubIndustryCode(String subIndustryCode) {

		SubIndustryCode = subIndustryCode;
	}

	public String getSubIndustry() {

		return SubIndustry;
	}

	public void setSubIndustry(String subIndustry) {

		SubIndustry = subIndustry;
	}

	public String getMiddleCategoryCode() {

		return MiddleCategoryCode;
	}

	public void setMiddleCategoryCode(String middleCategoryCode) {

		MiddleCategoryCode = middleCategoryCode;
	}

	public String getMiddleCategory() {

		return MiddleCategory;
	}

	public void setMiddleCategory(String middleCategory) {

		MiddleCategory = middleCategory;
	}

	public String getSmallCategoryCode() {

		return SmallCategoryCode;
	}

	public void setSmallCategoryCode(String smallCategoryCode) {

		SmallCategoryCode = smallCategoryCode;
	}

	public String getSmallCategory() {

		return SmallCategory;
	}

	public void setSmallCategory(String smallCategory) {

		SmallCategory = smallCategory;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("IndustryCode:" + IndustryCode + ",");
		bufferStr.append("Industry:" + Industry + ",");
		bufferStr.append("SubIndustryCode:" + SubIndustryCode + ",");
		bufferStr.append("SubIndustry:" + SubIndustry + ",");
		bufferStr.append("MiddleCategoryCode:" + MiddleCategoryCode + ",");
		bufferStr.append("MiddleCategory:" + MiddleCategory + ",");
		bufferStr.append("SmallCategoryCode:" + SmallCategoryCode + ",");
		bufferStr.append("SmallCategory:" + SmallCategory);
		return bufferStr.toString();
	}

}
