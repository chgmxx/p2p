package com.power.platform.activity.dao;

import java.util.List;

import com.power.platform.activity.entity.ZtmgWechatReturningCash;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: ZtmgWechatReturningCashDao <br>
 * 描述: 微信返现DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年9月8日 上午10:30:29
 */
@MyBatisDao
public interface ZtmgWechatReturningCashDao extends CrudDao<ZtmgWechatReturningCash> {

	/**
	 * 
	 * 方法: findExcelReportList <br>
	 * 描述: 微信返现，财务需求，虚拟充值. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月8日 上午10:40:55
	 * 
	 * @param entity
	 * @return
	 */
	public List<ZtmgWechatReturningCash> findExcelReportList(ZtmgWechatReturningCash entity);

}