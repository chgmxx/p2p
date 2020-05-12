/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.addressinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.addressinfo.CreditAddressInfo;


/**
 * 信贷家庭住址DAO接口
 * @author nice
 * @version 2017-03-23
 */
@MyBatisDao
public interface CreditAddressInfoDao extends CrudDao<CreditAddressInfo> {

	List<CreditAddressInfo> findList1(CreditAddressInfo entity);
	
}