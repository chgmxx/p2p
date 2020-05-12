package com.power.platform.bouns.dao;

import java.util.List;

import com.power.platform.bouns.entity.UserSigned;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: UserSignedDao <br>
 * 描述: 客户签到DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年12月13日 下午3:53:56
 */
@MyBatisDao
public interface UserSignedDao extends CrudDao<UserSigned> {

	/**
	 * 
	 * 方法: findExists <br>
	 * 描述: 查询客户在指定的时间范围内是否存在签到记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年12月14日 上午10:35:21
	 * 
	 * @return
	 */
	public abstract List<UserSigned> findExists(UserSigned entity);

}