package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: OriginalName <br>
 * 描述: 原来的名字. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:44:28
 */
public class OriginalName {

	// 曾用名.
	private String Name;
	// 变更日期.
	private String ChangeDate;

	public String getName() {

		return Name;
	}

	public void setName(String name) {

		Name = name;
	}

	public String getChangeDate() {

		return ChangeDate;
	}

	public void setChangeDate(String changeDate) {

		ChangeDate = changeDate;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("Name:" + Name + ",");
		bufferStr.append("ChangeDate:" + ChangeDate);
		return bufferStr.toString();
	}

}
