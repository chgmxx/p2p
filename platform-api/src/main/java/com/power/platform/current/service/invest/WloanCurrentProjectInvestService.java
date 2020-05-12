package com.power.platform.current.service.invest;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.current.dao.invest.WloanCurrentProjectInvestDao;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;

/**
 * 活期项目投资Service
 * @author Mr.Jia
 * @version 2016-01-14
 */
@Service("wloanCurrentProjectInvestService")
public class WloanCurrentProjectInvestService extends CrudService<WloanCurrentProjectInvest> {

	@Resource
	private WloanCurrentProjectInvestDao wloanCurrentProjectInvestDao;
	
	
	public List<WloanCurrentProjectInvest> findAll() {
		return null;
	}

	protected CrudDao<WloanCurrentProjectInvest> getEntityDao() {
		return wloanCurrentProjectInvestDao;
	}
	/**
	 * 根据用户ID查询投资记录(按投资日期升序)
	 */
	public List<WloanCurrentProjectInvest> findListOrderBy(String userId) {
		// TODO Auto-generated method stub
		List<WloanCurrentProjectInvest> loanCurrentProjectInvest = wloanCurrentProjectInvestDao.findListOrderBy(userId);
		return loanCurrentProjectInvest;
	}

	/**
	 * 插入数据[活期项目投资表]
	 */
	@Transactional(readOnly=false,rollbackFor=Exception.class)
	public void insert(WloanCurrentProjectInvest wloanCurrentProjectInvest) {
		// TODO Auto-generated method stub
		 wloanCurrentProjectInvestDao.insert(wloanCurrentProjectInvest);
	}

}