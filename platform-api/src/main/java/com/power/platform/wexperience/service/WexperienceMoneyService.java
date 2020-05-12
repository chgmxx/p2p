package com.power.platform.wexperience.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.wexperience.dao.WexperienceMoneyDao;
import com.power.platform.wexperience.entity.WexperienceMoney;

/**
 * 体验金信息Service
 * @author Mr.Jia
 * @version 2016-01-25
 */
@Service("wexperienceMoneyService")
@Transactional(readOnly = true)
public class WexperienceMoneyService extends CrudService<WexperienceMoney> {

	public static final String WEXPERIENCE_STATE_1 = "1";		// 体验金状态 1、可用
	public static final String WEXPERIENCE_STATE_2 = "2";		// 体验金状态 2、已使用
	public static final String WEXPERIENCE_STATE_3 = "3";		// 体验金状态 3、已过期
	
	// 代金券来源(come_from)
	public static final String NO_SAVE 			= "1";			// 老数据无记录
	public static final String INVITEFRIENDS 	= "2";			// 邀请好友送本人
	public static final String OPENACCOUNT 		= "3";			// 开通第三方送本人
	public static final String BID 				= "4";			// 投资送本人
	
	@Resource
	private WexperienceMoneyDao wexperienceMoneyDao;

	protected CrudDao<WexperienceMoney> getEntityDao() {
		return wexperienceMoneyDao;
	}

}