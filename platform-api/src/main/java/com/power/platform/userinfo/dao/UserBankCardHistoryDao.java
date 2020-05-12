/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.userinfo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.userinfo.entity.UserBankCardHistory;

/**
 * 
 * 类: UserBankCardHistoryDao <br>
 * 描述: 客户银行卡更换历史DAO接口 <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月21日 下午5:01:02
 */
@MyBatisDao
public interface UserBankCardHistoryDao extends CrudDao<UserBankCardHistory> {

	/**
	 * 
	 * 方法: checkUserBankCardHistoryExist <br>
	 * 描述: 判断客户是否在重复提交更换银行卡操作. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月21日 下午8:31:49
	 * 
	 * @param userId
	 * @param state
	 * @return
	 */
	public List<UserBankCardHistory> checkUserBankCardHistoryExist(@Param("userId") String userId, @Param("state") String state);

}