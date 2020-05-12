package com.power.platform.lanmao.rw.pojo;
/**
 * 快捷银行对象
* @author ant-loiter
*
*/
public class SwiftVO {
	private String bankNo;
	private Integer maxByEachStroke; // 每笔最大转账金额
	private Integer maxByEachDay; // 每天最大转账金额
	private Integer min = 5;  // 最少充值金额，>= 5
	public SwiftVO() {};
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public Integer getMaxByEachStroke() {
		return maxByEachStroke;
	}
	public void setMaxByEachStroke(Integer maxByEachStroke) {
		this.maxByEachStroke = maxByEachStroke;
	}
	public Integer getMaxByEachDay() {
		return maxByEachDay;
	}
	public void setMaxByEachDay(Integer maxByEachDay) {
		this.maxByEachDay = maxByEachDay;
	}
	public Integer getMin() {
		return min;
	}
	public void setMin(Integer min) {
		this.min = min;
	}
}
