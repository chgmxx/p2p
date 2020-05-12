package com.power.platform.zdw.pojo;

/**
 * 
 * 类: ZdProperty <br>
 * 描述: 质押财产 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 下午2:07:31
 */
public class ZdProperty {

	private String mainCurrencyCategory; // 主合同币种
	private String mainPrice; // 主合同金额
	private String pledgeCurrencyCategory; // 质押合同币种
	private String pledgePrice; // 质押合同金额
	private String pledgePropertyDetails; // 质押财产描述

	public String getMainCurrencyCategory() {

		return mainCurrencyCategory;
	}

	public void setMainCurrencyCategory(String mainCurrencyCategory) {

		this.mainCurrencyCategory = mainCurrencyCategory;
	}

	public String getMainPrice() {

		return mainPrice;
	}

	public void setMainPrice(String mainPrice) {

		this.mainPrice = mainPrice;
	}

	public String getPledgeCurrencyCategory() {

		return pledgeCurrencyCategory;
	}

	public void setPledgeCurrencyCategory(String pledgeCurrencyCategory) {

		this.pledgeCurrencyCategory = pledgeCurrencyCategory;
	}

	public String getPledgePrice() {

		return pledgePrice;
	}

	public void setPledgePrice(String pledgePrice) {

		this.pledgePrice = pledgePrice;
	}

	public String getPledgePropertyDetails() {

		return pledgePropertyDetails;
	}

	public void setPledgePropertyDetails(String pledgePropertyDetails) {

		this.pledgePropertyDetails = pledgePropertyDetails;
	}

}
