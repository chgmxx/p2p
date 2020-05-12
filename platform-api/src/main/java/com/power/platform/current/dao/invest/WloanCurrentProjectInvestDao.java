package com.power.platform.current.dao.invest;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;

/**
 * 活期项目投资DAO接口
 * @author Mr.Jia
 * @version 2016-01-14
 */
@MyBatisDao
public interface WloanCurrentProjectInvestDao extends CrudDao<WloanCurrentProjectInvest> {
	
	/**
	 * 根据用户ID查询投资列表(按投资时间升序)
	 * @param userId
	 * @return
	 */
	List<WloanCurrentProjectInvest> findListOrderBy(@Param("userId") String userId);
	
}