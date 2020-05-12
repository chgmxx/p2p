package com.power.platform.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: ARateCouponDicDao <br>
 * 描述: 加息券字典数据DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月18日 上午10:21:41
 */
@MyBatisDao
public interface ARateCouponDicDao extends CrudDao<ARateCouponDic> {

	/**
	 * 
	 * 方法: findAllList <br>
	 * 描述: 获取全部字典数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月18日 下午5:15:38
	 * 
	 * @return
	 */
	public abstract List<ARateCouponDic> findAllARateCouponDics();

	
	/**
	 * 根据加息券查询数据
	 * @param amount
	 * @return
	 */
	public abstract ARateCouponDic findByRate(@Param("rate") Double rate);

}