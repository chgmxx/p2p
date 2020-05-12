package com.power.platform.activity.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.activity.entity.Brokerage;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: BrokerageDao <br>
 * 描述: 佣金接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 上午9:46:21
 */
@MyBatisDao
public interface BrokerageDao extends CrudDao<Brokerage> {

	/**
	 * 
	 * 方法: findBrokerageList <br>
	 * 描述: 佣金列表接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月25日 下午12:05:34
	 * 
	 * @param entity
	 * @return
	 */
	public List<Brokerage> findBrokerageList(Brokerage entity);

	/**
	 * 
	 * 方法: brokerageTotalAmount <br>
	 * 描述: 邀请好友投资佣金总额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月24日 下午5:12:15
	 * 
	 * @param userId
	 * @return
	 */
	public double brokerageTotalAmount(@Param("userId") String userId);
	
	public int insertBrokerage(@Param("id") String id,@Param("userId") String userId,@Param("fromUserId") String fromUserId,@Param("amount") Double amount,@Param("createDate") String createDate,@Param("updateDate") String updateDate);
	
	/**
	 * 方法: brokerageTotalAmount <br>
	 * 描述: 邀请好友投资佣金总额(端午活动). <br>
	 * @param userId
	 * @return
	 */
	public double brokerageTotalAmountBote(@Param("userId") String userId);
	
}
