/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.censusinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.censusinfo.CreditCensusInfo;


/**
 * 信贷人口普查DAO接口
 * @author nice
 * @version 2017-03-23
 */
@MyBatisDao
public interface CreditCensusInfoDao extends CrudDao<CreditCensusInfo> {

	List<CreditCensusInfo> findList1(CreditCensusInfo entity);
	
}