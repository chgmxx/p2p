package com.power.platform.credit.dao.collateral;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.collateral.CreditCollateralInfo;


/**
 * 抵押物信息DAO接口
 * @author nice
 * @version 2017-05-10
 */
@MyBatisDao
public interface CreditCollateralInfoDao extends CrudDao<CreditCollateralInfo> {
	
}