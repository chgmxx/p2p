package com.power.platform.sys.web.yjapi;

import java.util.List;

/**
 * 
 * 类: ContactInfo <br>
 * 描述: 联系信息. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 上午11:13:59
 */
public class ContactInfo {

	// 联系电话.
	private String PhoneNumber;
	// 联系邮箱.
	private String Email;
	// 网站信息.
	private List<WebSite> WebSite;

	public String getPhoneNumber() {

		return PhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {

		PhoneNumber = phoneNumber;
	}

	public String getEmail() {

		return Email;
	}

	public void setEmail(String email) {

		Email = email;
	}

	public List<WebSite> getWebSite() {

		return WebSite;
	}

	public void setWebSite(List<WebSite> webSite) {

		WebSite = webSite;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("WebSite:" + WebSite + ",");
		bufferStr.append("PhoneNumber:" + PhoneNumber + ",");
		bufferStr.append("Email:" + Email);
		return bufferStr.toString();
	}

}
