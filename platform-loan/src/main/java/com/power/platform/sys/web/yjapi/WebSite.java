package com.power.platform.sys.web.yjapi;

/**
 * 
 * 类: WebSite <br>
 * 描述: 网站信息. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年8月23日 下午1:11:15
 */
public class WebSite {

	// 网站名称.
	private String Name;
	// 网站地址.
	private String Url;

	public String getName() {

		return Name;
	}

	public void setName(String name) {

		Name = name;
	}

	public String getUrl() {

		return Url;
	}

	public void setUrl(String url) {

		Url = url;
	}

	@Override
	public String toString() {

		StringBuffer bufferStr = new StringBuffer();
		bufferStr.append("Name:" + Name + ",");
		bufferStr.append("Url:" + Url);
		return bufferStr.toString();
	}
}
