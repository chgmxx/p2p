package com.power.platform.current.dao.moment;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;

/**
 * 投资用户剩余资金信息DAO接口
 * @author Mr.Jia
 * @version 2016-01-14
 */
@MyBatisDao
public interface WloanCurrentMomentInvestDao extends CrudDao<WloanCurrentMomentInvest> {

	/**
	 * 根据用户ID查询投资用户剩余资金信息
	 * @param transfer_person
	 * @return
	 */
	List<WloanCurrentMomentInvest> findListByUserId(@Param("userId") String transfer_person);
	
}