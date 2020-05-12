package com.power.platform.more.suggestion.entity;

import com.power.platform.common.persistence.DataEntity;

/**
 * 客户投资建议Entity
 * @author Mr.Jia
 * @version 2016-05-23
 */
public class Suggestion extends DataEntity<Suggestion> {
	
	private static final long serialVersionUID = 1L;
	private String id;			// 逐渐ID
	private String name;		// 客户联系方式
	
	public Suggestion() {
		super();
	}

	public Suggestion(String id){
		super(id);
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