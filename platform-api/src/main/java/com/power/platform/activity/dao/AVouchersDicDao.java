package com.power.platform.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: AVouchersDicDao <br>
 * 描述: 抵用券/代金券字典数据DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月19日 上午9:35:52
 */
@MyBatisDao
public interface AVouchersDicDao extends CrudDao<AVouchersDic> {

	/**
	 * 
	 * 方法: findAllList <br>
	 * 描述: 获取全部字典数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月18日 下午5:15:38
	 * 
	 * @return
	 */
	public abstract List<AVouchersDic> findAllAVouchersDics();

	/**
	 * 根据金额查找对应抵用劵信息
	 * @param voucher
	 * @return
	 */
	public abstract AVouchersDic findByVoucher(@Param("amount")
			Double voucher);

}