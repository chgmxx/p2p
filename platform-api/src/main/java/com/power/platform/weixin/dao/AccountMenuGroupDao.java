package com.power.platform.weixin.dao;



import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.AccountMenuGroup;


@MyBatisDao
public interface AccountMenuGroupDao extends CrudDao<AccountMenuGroup>{

	public void updateMenuGroupDisable();
	
	public void updateMenuGroupEnable(@Param("gid")String gid);
}

