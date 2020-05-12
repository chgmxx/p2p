package com.power.platform.credit.dao.apply;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.apply.CreditUserApply;

/**
 * 借款申请DAO接口
 * 
 * @author nice
 * @version 2017-06-14
 */
@MyBatisDao
public interface CreditUserApplyDao extends CrudDao<CreditUserApply> {

	/**
	 * 
	 * 方法: findListByFinancingType <br>
	 * 描述: 借款申请列表-按融资类型加以区分. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月25日 下午4:26:53
	 * 
	 * @param entity
	 * @return
	 */
	List<CreditUserApply> findListByFinancingType(CreditUserApply entity);

	CreditUserApply queryCreditUserApplyById(String id);

	/**
	 * 借款端ERP申请详情
	 * @param userApplyId
	 * @return
	 */
	CreditUserApply findApplById(@Param("userApplyId") String userApplyId);

	/**
	 * 借款端ERP申请详情
	 * @param userApplyId
	 * @return
	 */
	CreditUserApply findApplyById(@Param("userApplyId") String userApplyId);
	
	/**
	 * 查找申请发票列表
	 * @param userApplyId
	 * @return
	 */
	List<CreditUserApply> findVoucherApplyList(CreditUserApply creditUserApply);
	
	/**
	 * 借款端查询合同用
	 * @param supplyId
	 * @return
	 */
	List<CreditUserApply> findListForAgreement(String supplyId);
}