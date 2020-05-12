/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.regular.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.entity.WloanTermProjectDto;

/**
 * 定期项目信息DAO接口
 * 
 * @author jiajunfeng
 * @version 2015-12-28
 */
@MyBatisDao
public interface WloanTermProjectDao extends CrudDao<WloanTermProject> {

	// 标的融资进度更新
	int updateCurrentAmountAndCurrentRealAmountById(@Param("id") String id, @Param("investAmount") Double investAmount);

	int updateStateById(WloanTermProject project);

	// 国家应急数据中心，围绕散标-散标信息，第二阶段-增量.
	List<String> findScatterInvestListZ();

	// 国家应急数据中心，围绕散标-借款户creId列表，第二阶段-增量.
	List<String> findCreUserListZ();

	// 国家应急数据中心，围绕散标-融资主体subId列表，第二阶段-增量.
	List<String> findSubjectListZ();

	// 围绕散标，国家应急数据中心，存量（2019-03-01 00:00:00）before借款户subId列表.
	List<String> findSubjectListC();

	// 围绕散标，国家应急数据中心，存量（2019-03-01 00:00:00）before借款户creId列表.
	List<String> findCreUserListC();

	// 围绕散标，国家应急数据中心，存量（2019-03-01 00:00:00）before散标proId列表.
	List<String> findScatterInvestList();

	/**
	 * 
	 * methods: findProSnExist <br>
	 * description: 查找项目编号是否存在. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月10日 上午9:46:09
	 * 
	 * @param entity
	 * @return
	 */
	public List<WloanTermProject> findProSnExist(WloanTermProject entity);

	void updateProState(WloanTermProject wloanTermProject);

	/**
	 * 根据状态以及当前时间查询符合上线的定期项目
	 * 
	 * @param state
	 *            定期项目状态
	 * @return 符合上线的定期项目集合
	 */
	public List<WloanTermProject> findListByStateAndDate(String state);

	/**
	 * 修改项目状态
	 * 定时任务使用
	 */
	public void updateWloanTermProjectState(@Param("id") String id, @Param("state") String state);

	public List<WloanTermProject> findListByCompanyId(@Param("companyId") String companyId);

	List<WloanTermProject> findExcelReportList(WloanTermProject entity);

	// 网贷协会
	public Map<String, String> selectForWDXHOne();// 累计借贷金额(元) 累计借款笔数

	public Map<String, String> selectForWDXHTwo();// 累计借贷余额 累计借贷余额笔数 当前借款人数

	public Map<String, String> selectForWDXHThree();// 累计借款人数

	public Map<String, String> selectForWDXHFour();// 累计投资人数

	public Map<String, String> selectForWDXHFive();// 当前投资人数

	/**
	 * @Description:jbxt-供应链标的列表
	 */
	List<WloanTermProjectDto> findProjectDtoList(WloanTermProjectDto wloanTermProject);

	/**
	 * @Description:数据中心：利用订单Id查询标的
	 */
	WloanTermProject getWloanTermProject(@Param("orderId") String orderId);

	/**
	 * @Description:数据中心：推送散标信息.(根据时间区间，获取需要推送标的)
	 */
	List<String> findScatterInvest(@Param("startTime") String startTime,@Param("endTime") String endTime);

	/**
	 * @Description:数据统计：利用企业Id查询统计的数据
	 */
	WloanTermProject searchData(@Param("creditUserId") String creditUserId, @Param("entTimeStr")String entTimeStr);
	/**
	 * @Description:数据统计
	 */
	WloanTermProject searchAxtData(@Param("entTimeStr")String entTimeStr);
	/**
	 * @Description:区间数据统计
	 */
	WloanTermProject searchIntervalAxtData(@Param("startTimeStr")String startTimeStr, @Param("entTimeStr")String entTimeStr);
	/**
	 * @Description:区间数据统计
	 */
	WloanTermProject searchIntervalData(@Param("creditUserId")String creditUserId, @Param("startTimeStr")String startTimeStr, @Param("entTimeStr")String entTimeStr);

	/**
	 * @Description:统计平均数据
	 */
	WloanTermProject searchAverageData(@Param("entTimeStr")String entTimeStr);

	List<WloanTermProject> searchDistinct(@Param("entTimeStr")String entTimeStr);

	List<WloanTermProject> searchBySubjectId(@Param("subjectId")String subjectId, @Param("entTimeStr")String entTimeStr);
}