package com.power.platform.credit.dao.coinsuranceinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.coinsuranceinfo.CreditCoinsuranceInfo;

/**
 * 
 * 类: CreditCoinsuranceInfoDao <br>
 * 描述: 信贷联保DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 下午5:40:51
 */
@MyBatisDao
public interface CreditCoinsuranceInfoDao extends CrudDao<CreditCoinsuranceInfo> {

	List<CreditCoinsuranceInfo> getCreditCoinsuranceInfoList(String creditUserId);

	int deleteCoinsuranceInfoById(String id);

	List<CreditCoinsuranceInfo> findList1(CreditCoinsuranceInfo entity);

}