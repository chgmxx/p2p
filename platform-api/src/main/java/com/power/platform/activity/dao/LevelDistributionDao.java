package com.power.platform.activity.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: LevelDistributionDao <br>
 * 描述: 三级分销DAO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 上午9:47:35
 */
@MyBatisDao
public interface LevelDistributionDao extends CrudDao<LevelDistribution> {

	/**
	 * 
	 * 方法: myRateIncreasesTeamMembers <br>
	 * 描述: 我的加息团成员. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月30日 上午10:25:06
	 * 
	 * @param entity
	 * @return
	 */
	public abstract List<LevelDistribution> myRateIncreasesTeamMembers(LevelDistribution entity);

	/**
	 * 
	 * 方法: findLevelDistributionList <br>
	 * 描述: 接口三级分销列表展示. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月25日 上午10:15:51
	 * 
	 * @param entity
	 * @return
	 */
	public List<LevelDistribution> findLevelDistributionList(LevelDistribution entity);

	/**
	 * 
	 * 方法: getInviteFriends <br>
	 * 描述: 你已邀请好友多少人(三级分销). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月24日 下午4:43:38
	 * 
	 * @param userId
	 * @return
	 */
	public abstract int getInviteFriends(@Param("userId") String userId);

	/**
	 * 
	 * 方法: queryUserWbidSumAmount <br>
	 * 描述: 邀请好友投资总额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月26日 上午9:47:16
	 * 
	 * @param userId
	 * @return
	 */
	public double queryUserWbidSumAmount(String userId);

	List<Map<String, Object>> queryUserWbidAmount(@Param("userId") String userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

	int countByExample(@Param("userId") String userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

	List<Map<String, Object>> notQueryUserWbidAmount(@Param("userId") String userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

	int notCountByExample(@Param("userId") String userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

	LevelDistribution selectByUserId(@Param("userId") String userId);

	/**
	 * 根据用户ID查询被邀请人列表
	 * 
	 * @param userId
	 * @param i
	 * @param pageSize
	 * @return
	 */
	List<Map<String, Object>> findListByUserId(@Param("userId") String userId, @Param("offset") int offset, @Param("pageSize") int pageSize);

	/**
	 * 根据用户ID查询被邀请人总数
	 * 
	 * @param userId
	 * @param i
	 * @param pageSize
	 * @return
	 */
	int countByUserId(@Param("userId") String userId, @Param("offset") int offset, @Param("pageSize") int pageSize);
	/**
	 * 根据用户ID查询活动期间的被邀请人总数
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>>  findListByUserIdParent(@Param("userId") String userId);
	
	
	/**
	 * 根据用户ID查询被邀请人列表
	 * 
	 * @param userId
	 * @param i
	 * @param pageSize
	 * @return
	 */
	
	int countByUserIdParent(@Param("userId") String userId);
	public double queryUserWbidSumAmountBote(String userId);

	public List<LevelDistribution> findListByParentId(@Param("userId")String parentId);
	
}