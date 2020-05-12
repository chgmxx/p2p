package com.power.platform.zdw.entity;

/**
 * 
 * class: Search <br>
 * description: 中登网-查询接口-查询条件. <br>
 * author: Roy <br>
 * date: 2019年7月10日 上午11:25:28
 */
public class Search {

	private String userName;
	private String password;
	private String guarantor; // 

	public Search(String userName, String password) {

		this.userName = "cicmorgan456"; // 帐号.
		this.password = "welovecicmorgan0605"; // 密码.
	}

	public String getGuarantor() {

		return guarantor;
	}

	public void setGuarantor(String guarantor) {

		this.guarantor = guarantor;
	}

	public String getUserName() {

		return userName;
	}

	public void setUserName(String userName) {

		this.userName = userName;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	@Override
	public String toString() {

		return "Search [userName=" + userName + ", password=" + password + ", guarantor=" + guarantor + ", getGuarantor()=" + getGuarantor() + ", getUserName()=" + getUserName() + ", getPassword()=" + getPassword() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
