package com.power.platform.plan.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.plan.dao.WloanTermWrepayDao;
import com.power.platform.plan.entity.WloanTermWrepay;

@Service("wloanTermWrepayService")
public class WloanTermWrepayService extends CrudService<WloanTermWrepay>  {
	
	@Resource
	private WloanTermWrepayDao wloanTermWrepayDao;
	
	protected CrudDao<WloanTermWrepay> getEntityDao() {
		return wloanTermWrepayDao;
	}

}
