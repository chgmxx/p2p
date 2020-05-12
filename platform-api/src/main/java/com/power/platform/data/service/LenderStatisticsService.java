package com.power.platform.data.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.data.dao.LenderStatisticsDao;
import com.power.platform.data.entity.LenderStatistics;

@Service("lenderStatisticsService")
public class LenderStatisticsService extends CrudService<LenderStatistics>  {

	@Resource
	private LenderStatisticsDao lenderStatisticsDao;
	@Override
	protected CrudDao<LenderStatistics> getEntityDao() {
		
		return lenderStatisticsDao;
	}
	public int insert(LenderStatistics ls) {
		return lenderStatisticsDao.insert(ls);
	}

}
