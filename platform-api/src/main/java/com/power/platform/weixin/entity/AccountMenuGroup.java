package com.power.platform.weixin.entity;

import com.power.platform.common.persistence.DataEntity;

public class AccountMenuGroup extends DataEntity<AccountMenuGroup>{

	private String name;
	private Integer enable;


	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}

	public Integer getEnable(){
		return enable;
	}
	public void setEnable(Integer enable){
		this.enable = enable;
	}
 
}

