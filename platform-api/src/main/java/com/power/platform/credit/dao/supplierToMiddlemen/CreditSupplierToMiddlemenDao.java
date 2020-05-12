package com.power.platform.credit.dao.supplierToMiddlemen;

import java.util.List;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;

/**
 * 
 * 类: CreditSupplierToMiddlemenDao <br>
 * 描述: 借代中间表DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年1月9日 下午4:36:37
 */
@MyBatisDao
public interface CreditSupplierToMiddlemenDao extends CrudDao<CreditSupplierToMiddlemen> {

	/**
	 * 
	 * 方法: findCreditSupplierToMiddlemens <br>
	 * 描述: 查询借代关系表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月10日 上午10:31:48
	 * 
	 * @param entity
	 * @return
	 */
	List<CreditSupplierToMiddlemen> findCreditSupplierToMiddlemens(CreditSupplierToMiddlemen entity);

	List<CreditSupplierToMiddlemen> findCreditSupplierToMiddlemensList(CreditSupplierToMiddlemen entity);

}