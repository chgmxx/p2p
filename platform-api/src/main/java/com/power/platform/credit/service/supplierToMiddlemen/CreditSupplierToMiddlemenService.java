package com.power.platform.credit.service.supplierToMiddlemen;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;

/**
 * 
 * 类: CreditSupplierToMiddlemenService <br>
 * 描述: 借代中间表Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年1月9日 下午4:45:02
 */
@Service("creditSupplierToMiddlemenService")
public class CreditSupplierToMiddlemenService extends CrudService<CreditSupplierToMiddlemen> {

	@Resource
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;

	@Override
	protected CrudDao<CreditSupplierToMiddlemen> getEntityDao() {

		return creditSupplierToMiddlemenDao;
	}

	/**
	 * 
	 * 方法: insertCreditSupplierToMiddlemen <br>
	 * 描述: 新增借代中间表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月9日 下午4:43:38
	 * 
	 * @param creditSupplierToMiddlemen
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditSupplierToMiddlemen(CreditSupplierToMiddlemen creditSupplierToMiddlemen) {

		int flag = 0;

		creditSupplierToMiddlemen.setCreateDate(new Date()); // 创建时间.
		creditSupplierToMiddlemen.setUpdateDate(new Date()); // 更新时间.
		creditSupplierToMiddlemen.setRemarks("借代关系"); // 备注.

		try {
			flag = creditSupplierToMiddlemenDao.insert(creditSupplierToMiddlemen);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditSupplierToMiddlemen,{" + e.getMessage() + "}");
		}

		return flag;
	}

}