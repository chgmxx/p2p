package com.power.platform.weixin.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.AccountMenu;


@MyBatisDao
public interface AccountMenuDao extends CrudDao<AccountMenu>{

	public List<AccountMenu> parentMenuList(AccountMenu accountMenu);

	public List<AccountMenu> listWxMenus(@Param("gid")String gid);
}