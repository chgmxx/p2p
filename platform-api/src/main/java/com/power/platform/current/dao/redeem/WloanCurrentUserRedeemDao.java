/**
 */
package com.power.platform.current.dao.redeem;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.current.entity.redeem.WloanCurrentUserRedeem;


/**
 * 活期赎回DAO接口
 * @author yb
 * @version 2016-01-13
 */
@MyBatisDao
public interface WloanCurrentUserRedeemDao extends CrudDao<WloanCurrentUserRedeem> {

	/**
	 * 根据用户ID查询赎回申请金额
	 * @param userId
	 * @param state
	 * @return
	 */
	Double findRedeem(@Param("userId")String userId,@Param("state") Integer state);
	
}