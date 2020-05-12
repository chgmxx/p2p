package com.power.platform.pay.cash.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.pay.cash.entity.UserCash;

/**
 * 客户提现记录DAO接口
 * 
 * @author soler
 * @version 2015-12-23
 */
@MyBatisDao
public interface UserCashDao extends CrudDao<UserCash> {

	/**
	 * 
	 * 方法: findExcelReportList <br>
	 * 描述: 财务需求，提现. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月9日 上午10:16:17
	 * 
	 * @param entity
	 * @return
	 */
	public List<UserCash> findExcelReportList(UserCash entity);

	/**
	 * 更新交易状态
	 * 
	 * @param userRecharge
	 * @return
	 */
	int updateState(UserCash userCash);

	List<UserCash> getCashCount(UserCash userCash);

	/**
	 * 
	 * methods: findNumberOfWithdrawal <br>
	 * description: 查找用户提现次数. <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月26日 下午1:35:31
	 * 
	 * @param userCash
	 * @return
	 */
	List<UserCash> findNumberOfWithdrawal(UserCash userCash);

	/**
	 * 单表根据id查询提现数据
	 * 
	 * @param orderId
	 * @return
	 */
	public UserCash getInfoById(@Param("id") String orderId);

	List<UserCash> getCreditUserCashCount(UserCash userCash);

}