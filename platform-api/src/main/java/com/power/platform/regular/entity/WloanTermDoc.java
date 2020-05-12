package com.power.platform.regular.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;

import java.util.Date;

/**
 * 
 * 类: WloanTermDoc <br>
 * 描述: 定期融资档案Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月28日 下午5:44:29
 */
public class WloanTermDoc extends DataEntity<WloanTermDoc> {

	private static final long serialVersionUID = 1L;
	private String name; // 名称
	private Date beginCreateDate; // 开始 创建时间
	private Date endCreateDate; // 结束 创建时间
	private Date beginUpdateDate; // 开始 更新时间
	private Date endUpdateDate; // 结束 更新时间

	private WloanTermProject wloanTermProject; // 定期项目.

	public WloanTermDoc() {

		super();
	}

	public WloanTermDoc(String id) {

		super(id);
	}

	@Length(min = 0, max = 255, message = "名称长度必须介于 0 和 255 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public Date getBeginCreateDate() {

		return beginCreateDate;
	}

	public void setBeginCreateDate(Date beginCreateDate) {

		this.beginCreateDate = beginCreateDate;
	}

	public Date getEndCreateDate() {

		return endCreateDate;
	}

	public void setEndCreateDate(Date endCreateDate) {

		this.endCreateDate = endCreateDate;
	}

	public Date getBeginUpdateDate() {

		return beginUpdateDate;
	}

	public void setBeginUpdateDate(Date beginUpdateDate) {

		this.beginUpdateDate = beginUpdateDate;
	}

	public Date getEndUpdateDate() {

		return endUpdateDate;
	}

	public void setEndUpdateDate(Date endUpdateDate) {

		this.endUpdateDate = endUpdateDate;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

}