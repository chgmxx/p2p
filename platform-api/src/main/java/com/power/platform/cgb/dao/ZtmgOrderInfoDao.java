package com.power.platform.cgb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 中投摩根业务订单信息DAO接口
 * 
 * @author lance
 * @version 2018-02-06
 */
@MyBatisDao
public interface ZtmgOrderInfoDao extends CrudDao<ZtmgOrderInfo> {

	/**
	 * 
	 * 方法: findByOrderId <br>
	 * 描述: 根据订单号查询订单信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年2月6日 下午6:16:06
	 * 
	 * @param entity
	 * @return
	 */
	ZtmgOrderInfo findByOrderId(ZtmgOrderInfo entity);

	/**
	 * 
	 * @Title: findZtmgOrderInfo
	 * @Description:定时执行推送散标结束信息
	 * @Author: yangzf 
	 * @param @param starStr
	 * @param @param endStr
	 * @param @return
	 * @return List<ZtmgOrderInfo>
	 * @DateTime 2019年7月23日  下午4:37:58
	 */
	List<ZtmgOrderInfo> findZtmgOrderInfo(@Param("starStr")String starStr,@Param("endStr") String endStr);
}