package com.power.platform.pay.recharge.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.regular.entity.WloanTermInvest;

/**
 * 
 * 类: UserRechargeDao <br>
 * 描述: 客户充值记录DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年10月10日 下午1:12:06
 */
@MyBatisDao
public interface UserRechargeDao extends CrudDao<UserRecharge> {

	/**
	 * 
	 * 方法: findExcelReportList <br>
	 * 描述: 财务需求，充值. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月10日 下午1:11:40
	 * 
	 * @param entity
	 * @return
	 */
	public List<UserRecharge> findExcelReportList(UserRecharge entity);

	/**
	 * 
	 * 方法: findUserRechargeTotalAmount <br>
	 * 描述: 查询统计客户充值总额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月25日 上午10:47:39
	 * 
	 * @param userRecharge
	 * @return
	 */
	public abstract Double findUserRechargeTotalAmount(WloanTermInvest wloanTermInvest);

	/**
	 * 更新交易状态
	 * 
	 * @param userRecharge
	 * @return
	 */
	int updateState(UserRecharge userRecharge);

	/**
	 * 
	 * 方法: getUncompleteRecharge <br>
	 * 描述: 获取参数时间之前的未完成充值操作. <br>
	 * 时间: 2015年12月21日 下午2:48:30
	 * 
	 * @param date
	 * @return
	 */
	public List<UserRecharge> getUncompleteRecharge(@Param("date") Date date);

	public UserRecharge getById(@Param("id") String orderId);

}