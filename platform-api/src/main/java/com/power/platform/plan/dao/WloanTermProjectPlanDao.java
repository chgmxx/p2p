package com.power.platform.plan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermProjectPlanDto;

@MyBatisDao
public interface WloanTermProjectPlanDao extends CrudDao<WloanTermProjectPlan> {

	// 落单标识，批量还款交易在存管行完成落单，防止操作人员二次还款及页面隐藏按钮操作
	int modifyProjectPlanOrderStatus(@Param("orderStatus") String orderStatus, @Param("id") String projectPlanId);

	// 查询项目还款计划，查询条件为项目编号及还款日期.
	WloanTermProjectPlan findProPlanByProSnAndRepaymentDate(WloanTermProjectPlan proPlan);

	// 围绕散标-增量-借款人还本付息交易流水.
	List<WloanTermProjectPlan> findCrePayPrincipalAndInterestListZ(WloanTermProjectPlan proPlan);

	// 围绕散标，存量借款人还本付息记录.
	List<WloanTermProjectPlan> findCrePayPrincipalAndInterestList(WloanTermProjectPlan proPlan);

	/**
	 * 
	 * 方法: findProPlansByProId <br>
	 * 描述: 根据项目ID查询项目还款计划列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年11月28日 上午11:56:04
	 * 
	 * @param projectId
	 * @return
	 */
	List<WloanTermProjectPlan> findProPlansByProId(@Param("projectId") String projectId);

	int modifyWLoanTermProjectPlanState(WloanTermProjectPlan entity);

	/**
	 * 
	 * 方法: modifyWLoanTermProjectPlanDelFlag <br>
	 * 描述: 项目还款计划逻辑删除. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年10月23日 下午5:06:33
	 * 
	 * @param projectId
	 * @return
	 */
	int modifyWLoanTermProjectPlanDelFlag(String projectId);

	/**
	 * 
	 * 方法: getBySubOrderId <br>
	 * 描述: subOrderId查询项目还款信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月28日 上午10:44:00
	 * 
	 * @param subOrderId
	 * @return
	 */
	WloanTermProjectPlan getBySubOrderId(@Param("subOrderId") String subOrderId);

	public void deleteByProjectId(String projectId);

	public List<WloanTermProjectPlan> findListRefund(@Param("state") String state);

	public void updateWloanTermProjectPlanState(@Param("id") String id, @Param("state") String state);

	List<WloanTermProjectPlanDto> findPlanList(@Param("projectId") String projectId);
}
