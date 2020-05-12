package com.power.platform.cms.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 * 类: CmsPojo <br>
 * 描述: 内容管理POJO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月24日 下午4:52:39
 */
public class CmsPojo {

	private String id;
	private String sources;
	private Date sourcesDate;
	private Date createDate;
	private String title;
	private String head;
	private String text;
	private Integer state;
	private Integer type;
	private String imgPath;
	private String label;
	private Integer orderSum;

	public String getImgPath() {

		return imgPath;
	}

	public void setImgPath(String imgPath) {

		this.imgPath = imgPath;
	}

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getSources() {

		return sources;
	}

	public void setSources(String sources) {

		this.sources = sources;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getSourcesDate() {

		return sourcesDate;
	}

	public void setSourcesDate(Date sourcesDate) {

		this.sourcesDate = sourcesDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCreateDate() {

		return createDate;
	}

	public void setCreateDate(Date createDate) {

		this.createDate = createDate;
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getHead() {

		return head;
	}

	public void setHead(String head) {

		this.head = head;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}

	public Integer getState() {

		return state;
	}

	public void setState(Integer state) {

		this.state = state;
	}

	public Integer getType() {

		return type;
	}

	public void setType(Integer type) {

		this.type = type;
	}

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public Integer getOrderSum() {

		return orderSum;
	}

	public void setOrderSum(Integer orderSum) {

		this.orderSum = orderSum;
	}

}
