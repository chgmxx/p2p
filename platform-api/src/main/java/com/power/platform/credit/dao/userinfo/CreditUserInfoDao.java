package com.power.platform.credit.dao.userinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.userinfo.CreditUserInfoDto;

/**
 * 信贷用户DAO接口
 * 
 * @author nice
 * @version 2017-03-22
 */
@MyBatisDao
public interface CreditUserInfoDao extends CrudDao<CreditUserInfo> {

	CreditUserInfo getCreditUserInfoByPhone(@Param("phone") String phone);

	List<CreditUserInfo> findPageByAnnexFile(CreditUserInfo creditUser);

	/**
	 * @Description:JBXT-借款用户信息列表
	 */
	List<CreditUserInfoDto> findCreditUserInfo(CreditUserInfoDto creditUserInfo);

}