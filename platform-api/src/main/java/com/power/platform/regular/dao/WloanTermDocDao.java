package com.power.platform.regular.dao;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.regular.entity.WloanTermDoc;

/**
 * 
 * 类: WloanTermDocDao <br>
 * 描述: 定期融资档案DAO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月28日 下午5:43:48
 */
@MyBatisDao
public interface WloanTermDocDao extends CrudDao<WloanTermDoc> {

	/**
	 * 
	 * 方法: isExistWloanTermDocAndWloanTermProject <br>
	 * 描述: 定期项目是否使用融资档案，用于删除的时候判断. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月11日 上午11:29:53
	 * 
	 * @param entity
	 * @return
	 */
	public abstract List<WloanTermDoc> isExistWloanTermDocAndWloanTermProject(WloanTermDoc entity);

}
