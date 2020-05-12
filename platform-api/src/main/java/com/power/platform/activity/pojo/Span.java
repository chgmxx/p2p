package com.power.platform.activity.pojo;

public class Span {

	private String id;
	private String name;

	/**
	 * 标题: 构造器 <br>
	 * 描述: 构造器 <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月18日 下午4:40:19
	 */
	public Span() {

		super();
	}

	/**
	 * 标题: 构造器 <br>
	 * 描述: 构造器 <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月18日 下午3:54:40
	 * 
	 * @param id
	 * @param name
	 * @param useable
	 */
	public Span(String id, String name) {

		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

}
