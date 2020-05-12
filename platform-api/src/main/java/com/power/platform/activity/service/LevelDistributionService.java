package com.power.platform.activity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

@Service("levelDistributionService")
@Transactional(readOnly = true)
public class LevelDistributionService extends CrudService<LevelDistribution> {

	@Autowired
	private LevelDistributionDao levelDistributionDao;

	/**
	 * 方法: findLevelDistributionPage <br>
	 * 描述: 接口提供分页列表数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月25日 上午10:17:49
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<LevelDistribution> findLevelDistributionPage(Page<LevelDistribution> page, LevelDistribution entity) {

		entity.setPage(page);
		page.setList(levelDistributionDao.findLevelDistributionList(entity));
		return page;
	}

	@Override
	protected CrudDao<LevelDistribution> getEntityDao() {

		return levelDistributionDao;
	}

	/**
	 * 
	 * @param levelDistribution
	 * @return
	 */
	public List<LevelDistribution> findListByParentId(
			String userId) {
		// TODO Auto-generated method stub
		return levelDistributionDao.findListByParentId(userId);
	}
}
