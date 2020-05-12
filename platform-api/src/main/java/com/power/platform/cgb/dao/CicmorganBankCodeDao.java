/**
 * 银行编码对照DAO接口.
 */
package com.power.platform.cgb.dao;

import java.util.List;

import com.power.platform.cgb.entity.CicmorganBankCode;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 银行编码对照DAO接口.
 * 
 * @author lance
 * @version 2017-11-28
 */
@MyBatisDao
public interface CicmorganBankCodeDao extends CrudDao<CicmorganBankCode> {

	List<CicmorganBankCode> findList1(CicmorganBankCode code);

}