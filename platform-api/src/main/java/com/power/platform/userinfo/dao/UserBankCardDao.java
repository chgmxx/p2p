/**
 * 工程: user-svc <br>
 * 标题: UserBankCardDao.java <br>
 * 描述: 用户银行卡DAO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月18日 下午3:05:24 <br>
 * 版权: Copyright 2015 1000CHI Software Technology Co.,Ltd. <br>
 * All rights reserved.
 *
 */

package com.power.platform.userinfo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserBankCard;

/**
 * 类: UserBankCardDao <br>
 * 描述: 用户银行卡DAO. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月18日 下午3:05:24
 */
@MyBatisDao
public interface UserBankCardDao extends CrudDao<UserBankCard> {

	/**
	 * 
	 * 方法: getUserBankCardByCreditUserIdAndState <br>
	 * 描述: 获取借款人开户银行卡信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月22日 下午1:33:13
	 * 
	 * @param userBankCard
	 * @return
	 */
	public UserBankCard getUserBankCardByCreditUserIdAndState(UserBankCard userBankCard);

	/**
	 * 
	 * 方法: physicallyDeleted <br>
	 * 描述: 物理删除客户银行卡信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月24日 下午5:21:30
	 * 
	 * @param entity
	 * @return
	 */
	public int physicallyDeleted(UserBankCard entity);

	/**
	 * 
	 * 方法: getUserBankCardByUserId <br>
	 * 描述: 获取客户银行卡信息，根据客户ID. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月21日 下午2:48:30
	 * 
	 * @param userId
	 * @return
	 */
	public UserBankCard getUserBankCardByUserId(@Param("userId") String userId);

	/**
	 * 
	 * 方法: queryUserBankCardByWhere <br>
	 * 描述: 查询封装(where). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月19日 上午11:01:28
	 * 
	 * @param userBankCard
	 * @return
	 */
	List<UserBankCard> queryUserBankCardByWhere(UserBankCard userBankCard);

	/**
	 * 用户银行卡信息查询
	 * 
	 * @param orderId
	 * @return
	 */
	public UserBankCard getInfoById(@Param("id") String orderId);

}
