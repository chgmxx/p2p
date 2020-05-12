package com.power.platform.weixin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.AccountFans;

@MyBatisDao
public interface AccountFansDao extends CrudDao<AccountFans>{

	public AccountFans getLastOpenId();

	public void addList(@Param("list")List<AccountFans> list);
	
	public AccountFans getByOpenId(@Param("openId")String openId);
}