package com.power.platform.credit.dao.creditOrder;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.creditOrder.CreditOrder;

/**
 * 订单信息DAO接口
 * 
 * @author jice
 * @version 2018-05-23
 */
@MyBatisDao
public interface CreditOrderDao extends CrudDao<CreditOrder> {

	/**
	 * 
	 * 方法: findByCreditInfoIdList <br>
	 * 描述: 订单清单列表. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月25日 下午1:58:07
	 * 
	 * @param creditInfoId
	 * @return
	 */
	List<CreditOrder> findByCreditInfoIdList(@Param("creditInfoId") String creditInfoId);
}