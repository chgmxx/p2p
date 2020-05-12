/**
 * Copyright &copy; 2012-2016 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.voucher;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.voucher.CreditVoucher;

/**
 * 发票DAO接口
 * 
 * @author jice
 * @version 2018-03-14
 */
@MyBatisDao
public interface CreditVoucherDao extends CrudDao<CreditVoucher> {

	/**
	 * 
	 * 方法: findByCreditInfoIdList <br>
	 * 描述: 所属借款资料清单列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月16日 下午5:14:42
	 * 
	 * @param creditInfoId
	 * @return
	 */
	List<CreditVoucher> findByCreditInfoIdList(@Param("creditInfoId") String creditInfoId);

	/**
	 * 
	 * 方法: invoiceTotalAmount <br>
	 * 描述: 借款申请发票应收账款转让总金额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月16日 下午4:37:05
	 * 
	 * @param creditInfoId
	 * @return
	 */
	Double invoiceTotalAmount(@Param("creditInfoId") String creditInfoId);

	/**
	 * 借款端ERP根据申请查询发票
	 * @param creditInfoId
	 * @return
	 */
	List<CreditVoucher> findListByInfoId(@Param("creditInfoId") String creditInfoId);

	/**
	 *中登网根据标的id查询发票信息
	 */
	List<CreditVoucher> findCreditVoucher(@Param("projectId")String projectId);

}