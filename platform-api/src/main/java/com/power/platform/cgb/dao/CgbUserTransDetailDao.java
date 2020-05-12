/**
 * 银行托管-流水-DAO接口.
 */
package com.power.platform.cgb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 银行托管-流水-DAO接口.
 * 
 * @author lance
 * @version 2017-10-26
 */
@MyBatisDao
public interface CgbUserTransDetailDao extends CrudDao<CgbUserTransDetail> {

	// 出借人平台流水（单表查询）.
	List<CgbUserTransDetail> findInvTransDetailList(CgbUserTransDetail cutd);

	// 围绕散标-增量-借款用户（放款）流水.
	List<CgbUserTransDetail> findTransactCreUserGrantListZ(CgbUserTransDetail cutd);

	// 围绕散标-增量-借款用户（充值、提现）流水.
	List<CgbUserTransDetail> findTransactCreditUserInfoListZ(CgbUserTransDetail cutd);

	// 围绕散标-2019-03-01 00:00:00 before存量借款用户（放款）流水.
	List<CgbUserTransDetail> findTransactCreUserGrantList(CgbUserTransDetail cutd);

	// 围绕散标-2019-03-01 00:00:00 before存量借款用户（充值、提现）流水.
	List<CgbUserTransDetail> findTransactCreditUserInfoList(CgbUserTransDetail cutd);

	// 围绕出借人-2019-03-01 00:00:00 before存量出借用户（充值、提现）流水.
	List<CgbUserTransDetail> findLendParticularsInvUserTransListC(CgbUserTransDetail cutd);

	// 围绕出借人-2019-03-01 00:00:00 after增量出借用户（充值、提现）流水.
	List<CgbUserTransDetail> findLendParticularsInvUserTransListZ(CgbUserTransDetail cutd);

	// 围绕出借人-2019-03-01 00:00:00 before存量出借用户（充值、提现）流水,帐号余额不为0，投资明细.
	List<CgbUserTransDetail> findTransactUserInfoList2(CgbUserTransDetail cutd);

	// 围绕出借人-2019-03-01 00:00:00-存量-出借人（出借返现）流水-投资明细.
	List<CgbUserTransDetail> findLendParticularsInvCashBackC(CgbUserTransDetail cutd);

	/**
	 * 更新交易记录状态
	 * 
	 * @param userTransDetail
	 * @return
	 */
	int updateState(CgbUserTransDetail userTransDetail);

	CgbUserTransDetail getByTransId(@Param("tranId") String tranId);

	List<CgbUserTransDetail> findCreditList(CgbUserTransDetail entity);

	List<CgbUserTransDetail> findList1(CgbUserTransDetail cgbUserTransDetail);

	String inOutCount(CgbUserTransDetail cgbUserTransDetail);
}