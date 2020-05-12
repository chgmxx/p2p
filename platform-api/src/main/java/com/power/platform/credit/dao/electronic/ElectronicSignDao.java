/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.credit.dao.electronic;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.credit.entity.electronic.ElectronicSign;


/**
 * 电子签章DAO接口
 * @author jice
 * @version 2018-03-20
 */
@MyBatisDao
public interface ElectronicSignDao extends CrudDao<ElectronicSign> {
	
}