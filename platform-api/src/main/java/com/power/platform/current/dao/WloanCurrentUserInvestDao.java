package com.power.platform.current.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.current.entity.WloanCurrentUserInvest;

/**
 * 
 * 类: WloanCurrentUserInvestDao <br>
 * 描述: 活期客户投资记录DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月13日 下午3:37:05
 */
@MyBatisDao
public interface WloanCurrentUserInvestDao extends CrudDao<WloanCurrentUserInvest> {


	/**
	 * 根据用户ID查询已投资成功未放款(按投资日期升序)
	 * @param transfer_person
	 * @return
	 */
	List<WloanCurrentUserInvest> findListOrderBy(@Param("userId") String transfer_person);

	/**
	 * 根据ID更新数据
	 * @param amount
	 * @param state
	 */
	void updateById(@Param("amount")Double amount,@Param("state") String state,@Param("Id") String Id);

	/**
	 * 
	 * 方法: findCurrentUserInvestByState <br>
	 * 描述: 根据数据状态(1：待投资，2：已投资)查询客户活期投资记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月19日 下午5:04:17
	 * 
	 * @param wloanCurrentUserInvest
	 * @return
	 */
	public abstract List<WloanCurrentUserInvest> findCurrentUserInvestByState(WloanCurrentUserInvest wloanCurrentUserInvest);


}
