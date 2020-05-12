package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: ChangeRecords <br>
 * 描述: 更改记录 <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:08:24
 */
public class ChangeRecords {

	// 变更事项.
	private String ProjectName;
	// 变更前内容.
	private String BeforeContent;
	// 变更后内容.
	private String AfterContent;
	// 变更日期.
	private String ChangeDate;

	public String getProjectName() {

		return ProjectName;
	}

	public void setProjectName(String projectName) {

		ProjectName = projectName;
	}

	public String getBeforeContent() {

		return BeforeContent;
	}

	public void setBeforeContent(String beforeContent) {

		BeforeContent = beforeContent;
	}

	public String getAfterContent() {

		return AfterContent;
	}

	public void setAfterContent(String afterContent) {

		AfterContent = afterContent;
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
		bufferStr.append("ProjectName:" + ProjectName + ",");
		bufferStr.append("BeforeContent:" + BeforeContent + ",");
		bufferStr.append("AfterContent:" + AfterContent + ",");
		bufferStr.append("ChangeDate:" + ChangeDate);
		return bufferStr.toString();
	}

}
