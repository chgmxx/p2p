package com.power.platform.current.service.moment;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.current.dao.moment.WloanCurrentMomentInvestDao;
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;


/**
 * 投资用户剩余资金信息Service
 * @author Mr.Jia
 * @version 2016-01-14
 */
@Service("wloanCurrentMomentInvestService")
@Transactional(readOnly=true)
public class WloanCurrentMomentInvestService extends CrudService<WloanCurrentMomentInvest> {
	
	/**
	 * 投资切分状态
	 */
	public static final String WLOAN_CURRENT_MOMENT_INVEST_STATE_WAIT 	= "1";		// 待投资
	public static final String WLOAN_CURRENT_MOMENT_INVEST_STATE_FINISH = "2";		// 已投资
	public static final String WLOAN_CURRENT_MOMENT_INVEST_STATE_SALED 	= "3";		// 已赎回
	
	@Resource 
	private WloanCurrentMomentInvestDao wloanCurrentMomentInvestDao;
	
	@Override
	protected CrudDao<WloanCurrentMomentInvest> getEntityDao() {
		return wloanCurrentMomentInvestDao;
	}
	
	/**
	 * 根据用户ID查询投资用户剩余资金信息
	 * @param transfer_person
	 * @return
	 */
	public List<WloanCurrentMomentInvest> findListByUserId(
			String transfer_person) {
		return wloanCurrentMomentInvestDao.findListByUserId(transfer_person);
	}

}
