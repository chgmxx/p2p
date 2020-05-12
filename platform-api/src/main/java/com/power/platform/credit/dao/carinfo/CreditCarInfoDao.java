package com.power.platform.credit.dao.carinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.carinfo.CreditCarInfo;

/**
 * 
 * 类: CreditCarInfoDao <br>
 * 描述: 信贷车产DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 下午4:33:24
 */
@MyBatisDao
public interface CreditCarInfoDao extends CrudDao<CreditCarInfo> {

	List<CreditCarInfo> getCreditCarInfoList(String creditUserId);

	int deleteCarInfoById(String id);

	List<CreditCarInfo> findList1(CreditCarInfo entity);

}