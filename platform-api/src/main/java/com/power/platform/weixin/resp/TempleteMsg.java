package com.power.platform.weixin.resp;

import java.util.HashMap;
import java.util.Map;

public class TempleteMsg {
	private String touser;
	// 模板id
	private String template_id;
	// 点击详情url
	private String  url;
	// 消息头颜色
	private String topcolor;
	//封装模板列属性
	private Map<String, BaseTemplete> data = new HashMap<String,BaseTemplete>();
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTopcolor() {
		return topcolor;
	}
	public void setTopcolor(String topcolor) {
		this.topcolor = topcolor;
	}
	
	public Map<String, BaseTemplete> getData() {
		return data;
	}
	public void setData(Map<String, BaseTemplete> data) {
		this.data = data;
	}
}
