package com.power.platform.bill.dao;

import com.power.platform.bill.entity.MerchantRecharge;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 平台商户对账文件DAO接口
 * 
 * @author lance
 * @version 2018-03-02
 */
@MyBatisDao
public interface MerchantRechargeDao extends CrudDao<MerchantRecharge> {

}