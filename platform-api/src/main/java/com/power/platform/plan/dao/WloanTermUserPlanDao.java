package com.power.platform.plan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.plan.entity.WloanTermUserPlan;

@MyBatisDao
public interface WloanTermUserPlanDao extends CrudDao<WloanTermUserPlan> {

	// 倒序查询客户未还款的还款计划
	List<WloanTermUserPlan> findInvUserPlanByUserId(WloanTermUserPlan wloanTermUserPlan);

	// 查询未还款的userId列表
	List<String> findInvUserIdByUserPlans();

	/**
	 * 
	 * methods: findSumPrincipalByPlan <br>
	 * description: 查询用户本金. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月24日 上午11:00:14
	 * 
	 * @param wloanTermUserPlan
	 * @return
	 */
	Double findSumPrincipalByPlan(WloanTermUserPlan wloanTermUserPlan);

	/**
	 * 
	 * methods: findSumInterestByPlan <br>
	 * description: 查询用户利息或本息. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月24日 上午10:41:38
	 * 
	 * @param wloanTermUserPlan
	 * @return
	 */
	Double findSumInterestByPlan(WloanTermUserPlan wloanTermUserPlan);

	/**
	 * 
	 * 方法: findUserRepayPlanStatistical <br>
	 * 描述: 查找客户还款计划信息统计. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月31日 上午10:44:21
	 * 
	 * @param wloanTermUserPlan
	 * @return
	 */
	public List<WloanTermUserPlan> findUserRepayPlanStatistical(WloanTermUserPlan wloanTermUserPlan);

	/**
	 * 
	 * 方法: deleteByProjectId <br>
	 * 描述: 删除项目客户还款计划. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年10月23日 下午5:45:23
	 * 
	 * @param projectId
	 */
	public void deleteByProjectId(String projectId);

	/**
	 * 
	 * 方法: modifyWloanTermUserPlanState <br>
	 * 描述: 更新客户还款计划状态. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月29日 上午9:46:24
	 * 
	 * @param entity
	 * @return
	 */
	int modifyWloanTermUserPlanState(WloanTermUserPlan entity);

	public void updateWloanTermUserPlanStateByProjectId(@Param("wloanTermProjectId") String wloanTermProjectId, @Param("state") String state);

	public void updateWloanTermUserPlanStateById(@Param("id") String id, @Param("state") String state);

	public double findCurrentTotal(WloanTermUserPlan wloanTermUserPlan);

	public Double getWaitRepayMoney(WloanTermUserPlan wloanTermUserPlan);

	// 每期的回款金额
	public List<WloanTermUserPlan> findinterestCount(WloanTermUserPlan wloanTermUserPlan);

	// 每期的回款金额新版
	public List<WloanTermUserPlan> findNewInterestCount(WloanTermUserPlan wloanTermUserPlan);

	/**
	 * 
	 * 方法: findUserRepayPlans <br>
	 * 描述: 根据项目ID+还款日期，查找每期的还款计划. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月12日 下午9:14:12
	 * 
	 * @param wloanTermUserPlan
	 * @return
	 */
	public List<WloanTermUserPlan> findUserRepayPlans(WloanTermUserPlan wloanTermUserPlan);

	/**
	 * 删除客户还款计划
	 * 
	 * @param wloanTermInvestId
	 * @return
	 */
	public int deleteByWloanTermInvestId(@Param("wloanTermInvestId") String wloanTermInvestId);

	/**
	 * 账户对账专用 <勿动>
	 * 
	 * @param userPlan
	 * @return
	 */
	public List<WloanTermUserPlan> findUserPlan(WloanTermUserPlan userPlan);

	/**
	 * 
	 * 方法: findDueDateList <br>
	 * 描述: 客户还款计划列表，便于客服跟踪到期项目的还款客户. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年6月13日 下午2:59:41
	 * 
	 * @param wloanTermUserPlan
	 * @return
	 */
	public List<WloanTermUserPlan> findDueDateList(WloanTermUserPlan wloanTermUserPlan);

	/**
	 * 
	 * 出借人付款还本存量
	 */
	public List<WloanTermUserPlan> findUserPlanList(WloanTermUserPlan wloanTermUserPlan);

	// 增量-出借人还本付息-投资明细.
	public List<WloanTermUserPlan> findUserPlanListZ(WloanTermUserPlan wloanTermUserPlan);

	// 采用标的id补推还本付息流水.
	public List<WloanTermUserPlan> fillPushUserPlanList(WloanTermUserPlan wloanTermUserPlan);

	List<WloanTermUserPlan> findPlan(@Param("userId")String userId, @Param("startRepaymentDate")String startRepaymentDate, @Param("endRepaymentDate")String endRepaymentDate);

}
