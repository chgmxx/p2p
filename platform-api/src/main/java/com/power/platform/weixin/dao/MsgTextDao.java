package com.power.platform.weixin.dao;


import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.weixin.entity.MsgText;


@MyBatisDao
public interface MsgTextDao extends CrudDao<MsgText>{

/*	public MsgText getRandomMsg(String inputCode);
	public MsgText getRandomMsg2();*/
	
	public MsgText getMsgTextByInputCode(@Param("inputcode")String inputcode);
	
	public MsgText getMsgTextByBaseId(@Param("id")String id);

}