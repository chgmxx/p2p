package com.power.platform.activity.dao;

import org.apache.ibatis.annotations.Param;

import com.power.platform.activity.entity.WeixinShareDetails;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: WeixinShareDetailsDao <br>
 * 描述: 钱多多大联盟，微信分享关注公众号奖励. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 上午9:48:10
 */
@MyBatisDao
public interface WeixinShareDetailsDao extends CrudDao<WeixinShareDetails> {

	/**
	 * 
	 * 方法: queryAmount <br>
	 * 描述: 邀请公众号好友获得奖励. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月25日 下午3:49:22
	 * 
	 * @param userId
	 * @return
	 */
	double queryAmount(@Param("userId") String userId);

	/**
	 * 
	 * 方法: queryUsers <br>
	 * 描述: 公众号推广粉丝数. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月25日 下午3:48:47
	 * 
	 * @param userId
	 * @return
	 */
	int queryUsers(@Param("userId") String userId);

}
