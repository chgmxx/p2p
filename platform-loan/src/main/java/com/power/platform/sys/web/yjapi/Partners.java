package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: Partners <br>
 * 描述: 合作伙伴. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:46:09
 */
public class Partners {

	// 股东.
	private String StockName;
	// 股东类型.
	private String StockType;
	// 出资比例.
	private String StockPercent;
	// 认缴出资额.
	private String ShouldCapi;
	// 认缴出资时间.
	private String ShoudDate;
	// 认缴出资方式.
	private String InvestType;
	// 实缴出资方式.
	private String InvestName;
	// 实缴出资额.
	private String RealCapi;
	// 实缴时间.
	private String CapiDate;

	public String getStockName() {

		return StockName;
	}

	public void setStockName(String stockName) {

		StockName = stockName;
	}

	public String getStockType() {

		return StockType;
	}

	public void setStockType(String stockType) {

		StockType = stockType;
	}

	public String getStockPercent() {

		return StockPercent;
	}

	public void setStockPercent(String stockPercent) {

		StockPercent = stockPercent;
	}

	public String getShouldCapi() {

		return ShouldCapi;
	}

	public void setShouldCapi(String shouldCapi) {

		ShouldCapi = shouldCapi;
	}

	public String getShoudDate() {

		return ShoudDate;
	}

	public void setShoudDate(String shoudDate) {

		ShoudDate = shoudDate;
	}

	public String getInvestType() {

		return InvestType;
	}

	public void setInvestType(String investType) {

		InvestType = investType;
	}

	public String getInvestName() {

		return InvestName;
	}

	public void setInvestName(String investName) {

		InvestName = investName;
	}

	public String getRealCapi() {

		return RealCapi;
	}

	public void setRealCapi(String realCapi) {

		RealCapi = realCapi;
	}

	public String getCapiDate() {

		return CapiDate;
	}

	public void setCapiDate(String capiDate) {

		CapiDate = capiDate;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("StockName:" + StockName + ",");
		bufferStr.append("StockType:" + StockType + ",");
		bufferStr.append("StockPercent:" + StockPercent + ",");
		bufferStr.append("ShouldCapi:" + ShouldCapi + ",");
		bufferStr.append("ShoudDate:" + ShoudDate + ",");
		bufferStr.append("InvestType:" + InvestType + ",");
		bufferStr.append("InvestName:" + InvestName + ",");
		bufferStr.append("RealCapi:" + RealCapi + ",");
		bufferStr.append("CapiDate:" + CapiDate);
		return bufferStr.toString();
	}

}
