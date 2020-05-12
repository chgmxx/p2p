package com.power.platform.regular.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.regular.entity.WloanTermInvest;

/**
 * 
 * 类: WloanTermInvestDao <br>
 * 描述: 定期融资投资表DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月4日 下午7:43:03
 */
@MyBatisDao
public interface WloanTermInvestDao extends CrudDao<WloanTermInvest> {

	// 围绕散标-出借人用户增量数据.
	List<String> findIfCertUserInfoListZ();
	// 围绕散标，2019-03-01 00:00:00 before存量用户信息.
	List<String> findUserInfoList();

	// 已结束项目，用户累计收益.
	Double findSumInterestByInvest(WloanTermInvest invest);

	/**
	 * 
	 * methods: findSumAmountByInvest <br>
	 * description: 查询用户出借金额. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月24日 上午9:18:48
	 * 
	 * @param invest
	 * @return
	 */
	Double findSumAmountByInvest(WloanTermInvest invest);

	/**
	 * 
	 * methods: findCountNumByInvest <br>
	 * description: 查询用户出借笔数. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月23日 下午6:05:12
	 * 
	 * @param invest
	 * @return
	 */
	Integer findCountNumByInvest(WloanTermInvest invest);

	/**
	 * 
	 * methods: findTotalInterestByPlatform <br>
	 * description: 查找平台为用户赚取总收益. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月23日 上午10:54:08
	 * 
	 * @param invest
	 * @return
	 */
	Double findTotalInterestByPlatform(WloanTermInvest invest);

	/**
	 * 
	 * methods: findInvestTotalAmountByUserId <br>
	 * description: 查询用户出借总额. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月7日 上午11:59:50
	 * 
	 * @param invest
	 * @return
	 */
	public Double findInvestTotalAmountByUserId(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findContractPdfPathAnIsNull <br>
	 * 描述: 查询合同路径为Null的数据. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月26日 下午3:56:56
	 * 
	 * @return
	 */
	List<WloanTermInvest> findContractPdfPathAnIsNull();

	/**
	 * 
	 * 方法: findContractPdfPathAnEmptyString <br>
	 * 描述: 查询合同路径为空串的数据. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月26日 下午2:45:13
	 * 
	 * @return
	 */
	List<WloanTermInvest> findContractPdfPathAnEmptyString();

	/**
	 * 
	 * 方法: findAllUserInvestPeopleTotalCount <br>
	 * 描述: 全部用户投资人数. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 下午3:29:24
	 * 
	 * @param invest
	 * @return
	 */
	public Integer findAllUserInvestPeopleTotalCount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findNewUserInvestPeopleTotalCount <br>
	 * 描述: 新用户投资人数. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 下午3:30:02
	 * 
	 * @param invest
	 * @return
	 */
	public Integer findNewUserInvestPeopleTotalCount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findOldUserInvestPeopleTotalCount <br>
	 * 描述: 老用户投资人数. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 下午3:30:15
	 * 
	 * @param invest
	 * @return
	 */
	public Integer findOldUserInvestPeopleTotalCount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findAllUserInvestTotalCount <br>
	 * 描述: 全部用户投资人次. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 下午3:08:36
	 * 
	 * @param invest
	 * @return
	 */
	public Integer findAllUserInvestTotalCount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findNewUserInvestTotalCount <br>
	 * 描述: 新用户投资人次. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 下午3:09:05
	 * 
	 * @param invest
	 * @return
	 */
	public Integer findNewUserInvestTotalCount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findOldUserInvestTotalCount <br>
	 * 描述: 老用户投资人次. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 下午3:09:27
	 * 
	 * @param invest
	 * @return
	 */
	public Integer findOldUserInvestTotalCount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findAllUserFinancingTotalAmount <br>
	 * 描述: 全部用户融资总额. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 上午10:04:56
	 * 
	 * @param invest
	 * @return
	 */
	public Double findAllUserFinancingTotalAmount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findNewUserFinancingTotalAmount <br>
	 * 描述: 新用户融资总额. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 上午10:05:20
	 * 
	 * @param invest
	 * @return
	 */
	public Double findNewUserFinancingTotalAmount(WloanTermInvest invest);

	/**
	 * 
	 * 方法: findOldUserFinancingTotalAmount <br>
	 * 描述: 老用户融资总额. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月14日 上午10:05:36
	 * 
	 * @param invest
	 * @return
	 */
	public Double findOldUserFinancingTotalAmount(WloanTermInvest invest);

	List<WloanTermInvest> findLoanUserInvestList();

	Integer findNowLoanUserInfoNumbers();

	List<WloanTermInvest> findLoanUserInfoTotalNumbers();

	List<WloanTermInvest> findLoanUserInfoTotalNumbersByMonth(@Param("endTime") String endTime);

	Double findInterestTotalAmount();

	Double findInterestTotalAmountByMonth(@Param("endTime") String endTime);

	Double findInvestTotalAmount();

	Double findInvestTotalAmountByMonth(String endTime);

	/**
	 * 
	 * 方法: findProjectInvestNumbers <br>
	 * 描述: 项目投资笔数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月19日 下午3:39:15
	 * 
	 * @param projectId
	 * @return
	 */
	public List<WloanTermInvest> findProjectInvestNumbers(@Param("projectId") String projectId);

	/**
	 * 
	 * 方法: volunterrList <br>
	 * 描述: 公益活动捐献 <br>
	 * 作者: Mr.彦.赵 <br>
	 * 时间: 2017年4月28日 上午11:58:56
	 * 
	 * 
	 * @return
	 */
	public List<WloanTermInvest> volunterrList();

	/**
	 * 
	 * 方法: findWloanTermInvestExists <br>
	 * 描述: 查询客户的所有投资记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年12月16日 上午9:38:56
	 * 
	 * @param userId
	 * @return
	 */
	public List<WloanTermInvest> findWloanTermInvestExists(@Param("userId") String userId);

	/**
	 * 
	 * 方法: findStatisticalOldUserList <br>
	 * 描述: 运营统计老用户投资记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年12月16日 上午9:38:01
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public List<WloanTermInvest> findStatisticalOldUserList(WloanTermInvest wloanTermInvest);

	/**
	 * 
	 * 方法: getSpetInvestInfo <br>
	 * 描述: 获取2016年9月份客服投资信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月30日 下午3:03:30
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public List<WloanTermInvest> getSpetInvestInfo(WloanTermInvest wloanTermInvest);

	/**
	 * 
	 * 方法: findStatisticalNewUserList <br>
	 * 描述: 运营统计新用户投资记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月13日 上午10:03:59
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public List<WloanTermInvest> findStatisticalNewUserList(WloanTermInvest wloanTermInvest);

	/**
	 * 
	 * 方法: findStatisticalAllList <br>
	 * 描述: 运营统计新老用户投资记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月12日 下午4:30:49
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public List<WloanTermInvest> findStatisticalAllList(WloanTermInvest wloanTermInvest);

	/**
	 * 定期投资总额（包括已完成项目）
	 * 
	 * @param userId
	 * @return
	 */
	Double selectAmountTotle(@Param("userId") String userId);

	/**
	 * 根据用户ID查询定期待收收益
	 * 
	 * @param userId
	 * @return
	 */
	Double selectToBePrincipal(@Param("userId") String userId);

	/**
	 * 根据用户ID查询定期投资已收收益
	 * 
	 * @param userId
	 * @return
	 */
	Double selectBeInterest(@Param("userId") String userId);

	/**
	 * 根据用户ID查询定期投资待收收益
	 * 
	 * @param userId
	 * @return
	 */
	Double selectToBeInterest(@Param("userId") String userId);

	/**
	 * 查询某一投资项目某个人投资金额
	 * 
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public Double findInvestAmountByProjectAndUser(@Param("projectId") String projectId, @Param("userId") String userId, @Param("Id") String Id);

	public Double findInvestAmountByProjectAndUser1(@Param("projectId") String projectId, @Param("userId") String userId);

	/**
	 * 根据用户ID，开始时间，结束时间查询累计投资金额
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	Double countAmount(@Param("userId") String userId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

	/**
	 * 用户投资排行
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	List<WloanTermInvest> ranList(@Param("userId") String userId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

	/**
	 * 查询
	 * 
	 * @param invest
	 * @return
	 */
	public List<WloanTermInvest> findCheckInvest();

	/**
	 * 世界杯活动<出借排行榜>
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	List<WloanTermInvest> findWorldCupInvest(WloanTermInvest wloanTermInvest);

	/**
	 * 根据项目ID查询出借总额
	 */
	Double getInvestTotalAmount(@Param("projectId") String projectId);

	/**
	 * 根据项目ID查询所有投资记录
	 * 
	 * @param projectId
	 * @return
	 */
	List<WloanTermInvest> findListByProjectId(@Param("projectId") String projectId);
	/**
	 * 出借人信息统计查询
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	List<WloanTermInvest> findInvestPage(WloanTermInvest wloanTermInvest);
	

	List<WloanTermInvest> findInvest(@Param("beginInvestDate") String beginInvestDate, @Param("endInvestDate") String endInvestDate);

}