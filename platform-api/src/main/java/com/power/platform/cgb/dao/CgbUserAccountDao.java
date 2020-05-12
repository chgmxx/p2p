/**
 * 银行托管-账户-DAO接口.
 */
package com.power.platform.cgb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 银行托管-账户-DAO接口.
 * 
 * @author lance
 * @version 2017-10-26
 */
@MyBatisDao
public interface CgbUserAccountDao extends CrudDao<CgbUserAccount> {

	// 懒猫版本2.0，出借人收取本金
	int updatePrincipalById(@Param("principal") Double principal, @Param("oldAvailableAmount") Double oldAvailableAmount, @Param("id") String id);

	// 懒猫版本2.0，出借人收取利息
	int updateIncomeById(@Param("income") Double income, @Param("oldAvailableAmount") Double oldAvailableAmount, @Param("id") String id);

	// 懒猫版本，出借人流标账户变更，仅限流标操作
	int updateMiscarryById(@Param("investInterest") Double investInterest, @Param("investAmount") Double investAmount, @Param("id") String id);
	
	// 懒猫版本，账户出借更新（仅限出借）
	int updateTenderById(@Param("investInterest") Double investInterest, @Param("voucherAmount") Double voucherAmount, @Param("investAmount") Double investAmount, @Param("realInvestAmount") Double realInvestAmount, @Param("id") String id);

	// 账户出借更新--流标金额变更
	int updateCancelById(@Param("investInterest") Double investInterest, @Param("investAmount") Double investAmount, @Param("id") String id);

	// 收回利息-客户账户变更.
	int updateTakeBackInterest(@Param("interest") Double interest, @Param("id") String id);

	/**
	 * 根据用户id查找用户账户信息
	 * 
	 * @param userid
	 * @return
	 */
	public CgbUserAccount getUserAccountInfo(@Param("userid") String userid);

	/**
	 * 可用余额为0
	 * 
	 * @param entity
	 * @return
	 */
	public List<CgbUserAccount> findAmountList0(CgbUserAccount entity);

	/**
	 * 可用余额不为0
	 * 
	 * @param entity
	 * @return
	 */
	public List<CgbUserAccount> findAmountList1(CgbUserAccount entity);
}