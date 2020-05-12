package com.power.platform.weixin.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.MsgNews;

@MyBatisDao
public interface MsgNewsDao extends CrudDao<MsgNews>{

	public List<MsgNews> getRandomMsgByContent(@Param("inputcode")String inputcode ,@Param("num")Integer num);
	
	public List<MsgNews> listMsgNewsByBaseId(@Param("ids")String[] ids);
}