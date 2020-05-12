/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.houseinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.addressinfo.CreditAddressInfo;
import com.power.platform.credit.entity.houseinfo.CreditHouseInfo;


/**
 * 信贷房产信息DAO接口
 * @author nice
 * @version 2017-03-23
 */
@MyBatisDao
public interface CreditHouseInfoDao extends CrudDao<CreditHouseInfo> {

	List<CreditHouseInfo> findList1(CreditHouseInfo creditHouseInfo);
	
}