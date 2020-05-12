package com.power.platform.credit.dao.familyinfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.familyinfo.CreditFamilyInfo;

/**
 * 
 * 类: CreditFamilyInfoDao <br>
 * 描述: 信贷家庭信息DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 上午9:00:15
 */
@MyBatisDao
public interface CreditFamilyInfoDao extends CrudDao<CreditFamilyInfo> {

	List<CreditFamilyInfo> getCreditFamilyInfoList(String creditUserId);

	int deleteFamilyInfoById(String id);

	List<CreditFamilyInfo> findList1(CreditFamilyInfo entity);

}