package com.power.platform.credit.dao.basicinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.basicinfo.CreditBasicInfo;

/**
 * 
 * 类: CreditBasicInfoDao <br>
 * 描述: 信贷基本信息DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月29日 下午3:00:00
 */
@MyBatisDao
public interface CreditBasicInfoDao extends CrudDao<CreditBasicInfo> {

	/**
	 * 
	 * 方法: getCreditBasicInfo <br>
	 * 描述: 通过用户id获取基本信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月29日 下午2:59:36
	 * 
	 * @param creditUserId
	 * @return
	 */
	List<CreditBasicInfo> getCreditBasicInfo(String creditUserId);

	int deleteBasicInfoById(String id);

	List<CreditBasicInfo> findByUserId(String creditUserId);

}