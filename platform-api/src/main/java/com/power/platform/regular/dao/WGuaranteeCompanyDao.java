package com.power.platform.regular.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.regular.entity.WGuaranteeCompany;

/**
 * 担保机构Dao层接口
 * @author lc
 *
 */
@MyBatisDao
public interface WGuaranteeCompanyDao extends CrudDao<WGuaranteeCompany> {

}
