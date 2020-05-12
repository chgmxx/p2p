package com.power.platform.credit.dao.userinfo;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;

/**
 * 
 * class: CreditUserAccountDao <br>
 * description: 企业用户账户DAO. <br>
 * author: Roy <br>
 * date: 2019年10月16日 下午6:29:34
 */
@MyBatisDao
public interface CreditUserAccountDao extends CrudDao<CreditUserAccount> {

	// 变更平台营销款账户可用余额及账户总额
	int updateSysGenerateCreditUserAccount(@Param("id") String id, @Param("sumVoucherAmount") Double sumVoucherAmount);

	List<CreditUserAccount> findAllCreditUserAccountList();

	List<CreditUserAccount> findCreditUserAccountListByMiddlemenId(String middlemenId);

	int updateAmount(@Param("id") String id, @Param("currentAmount") Double currentAmount, @Param("surplusAmount") Double surplusAmount);

}