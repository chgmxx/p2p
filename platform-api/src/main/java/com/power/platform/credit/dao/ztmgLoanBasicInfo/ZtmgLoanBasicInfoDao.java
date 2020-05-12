package com.power.platform.credit.dao.ztmgLoanBasicInfo;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;

/**
 * 
 * 类: ZtmgLoanBasicInfoDao <br>
 * 描述: 借款人基本信息DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年4月28日 上午10:35:20
 */
@MyBatisDao
public interface ZtmgLoanBasicInfoDao extends CrudDao<ZtmgLoanBasicInfo> {

	int ztmgLoanBasicInfoUpdate(ZtmgLoanBasicInfo ztmgLoanBasicInfo);

	ZtmgLoanBasicInfo findByCreditUserId(ZtmgLoanBasicInfo ztmgLoanBasicInfo);

}