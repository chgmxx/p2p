package com.power.platform.zdw.pojo;

/**
 * 
 * 类: ZdPreparer <br>
 * 描述: 填表人 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 上午11:27:11
 */
public class ZdPreparer {

	private String timeLimit; // 登记期限，1~360正整数，单位：月
	private String title; // 填表人归档号

	public String getTimeLimit() {

		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {

		this.timeLimit = timeLimit;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

}
