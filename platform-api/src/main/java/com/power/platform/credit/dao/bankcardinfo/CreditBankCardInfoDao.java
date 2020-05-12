/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.bankcardinfo;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.bankcardinfo.CreditBankCardInfo;


/**
 * 信贷银行卡DAO接口
 * @author nice
 * @version 2017-03-23
 */
@MyBatisDao
public interface CreditBankCardInfoDao extends CrudDao<CreditBankCardInfo> {
	
}