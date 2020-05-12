package com.power.platform.credit.dao.companyInfo;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.companyInfo.CreditCompanyInfo;

/**
 * 
 * 类: CreditCompanyInfoDao <br>
 * 描述: 个人信贷公司信息DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月30日 上午11:47:29
 */
@MyBatisDao
public interface CreditCompanyInfoDao extends CrudDao<CreditCompanyInfo> {

	List<CreditCompanyInfo> getCreditCompanyInfoList(String creditUserId);

	int deleteCompanyInfoById(String id);

}