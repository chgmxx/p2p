package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: Employees <br>
 * 描述: 员工. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:28:20
 */
public class Employees {

	// 姓名.
	private String Name;
	// 职位.
	private String Job;

	public String getName() {

		return Name;
	}

	public void setName(String name) {

		Name = name;
	}

	public String getJob() {

		return Job;
	}

	public void setJob(String job) {

		Job = job;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("Name:" + Name + ",");
		bufferStr.append("Job:" + Job);
		return bufferStr.toString();
	}

}
