package com.power.platform.bill.dao;

import com.power.platform.bill.entity.MerchantWithdraw;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 平台商户对账，提现文件DAO接口
 * 
 * @author lance
 * @version 2018-03-08
 */
@MyBatisDao
public interface MerchantWithdrawDao extends CrudDao<MerchantWithdraw> {

}