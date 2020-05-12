package com.power.platform.zdw.entity;

/**
 * 
 * class: User <br>
 * description: 中登网帐密. <br>
 * author: Roy <br>
 * date: 2019年7月10日 上午11:24:55
 */
public class User {

	private String userName;
	private String password;

	public User(String userName, String password) {

		this.userName = "cicmorgan456"; // 帐号.
		this.password = "welovecicmorgan0605"; // 密码.
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

		return "user [userName=" + userName + ", password=" + password + ", getUserName()=" + getUserName() + ", getPassword()=" + getPassword() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
