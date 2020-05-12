package com.power.platform.trandetail.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.trandetail.entity.UserTransDetail;


/**
 * 客户流水记录DAO接口
 * @author soler
 * @version 2015-12-23
 */
@MyBatisDao
public interface UserTransDetailDao extends CrudDao<UserTransDetail> {

	/**
	 * 更新交易记录状态
	 * @param userTransDetail
	 * @return
	 */
	int updateState(UserTransDetail userTransDetail);
	
	/**
	 * 根据交易ID查询交易流水记录
	 * @param tranId
	 * @return
	 */
	UserTransDetail getByTransId(@Param("tranId")String tranId);
	
	List<UserTransDetail> findList1(UserTransDetail userTransDetail);
	
	List<UserTransDetail> findList2(UserTransDetail userTransDetail);

	/**
	 * 数据统计
	 * @param userId
	 * @param trustType
	 * @return
	 */
	Double findCountAmount(@Param("userId")String userId,@Param("trustType") int trustType);
	
}