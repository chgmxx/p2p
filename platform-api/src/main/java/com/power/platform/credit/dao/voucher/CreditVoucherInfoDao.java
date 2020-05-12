/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.voucher;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.voucher.CreditVoucherInfo;


/**
 * 发票信息DAO接口
 * @author jice
 * @version 2018-06-20
 */
@MyBatisDao
public interface CreditVoucherInfoDao extends CrudDao<CreditVoucherInfo> {
	
}