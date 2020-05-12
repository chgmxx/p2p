package com.power.platform.current.service;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.current.dao.WloanCurrentPoolDao;
import com.power.platform.current.entity.WloanCurrentPool;

/**
 * 活期融资资金池Service
 * 
 * @author Mr.Jia
 * @version 2016-01-12
 */
@Service("wloanCurrentPoolService")
public class WloanCurrentPoolService extends CrudService<WloanCurrentPool> {

	private static final Logger logger = Logger.getLogger(WloanCurrentPoolService.class);

	@Resource
	private WloanCurrentPoolDao wloanCurrentPoolDao;

	 
	@Transactional(readOnly = false)
	public int updateWloanCurrentPool(WloanCurrentPool wloanCurrentPool) {

		int flag = 0;
		try {
			flag = wloanCurrentPoolDao.update(wloanCurrentPool);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateWloanCurrentPool,{更新保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	 
	protected CrudDao<WloanCurrentPool> getEntityDao() {

		return wloanCurrentPoolDao;
	}


	/**
	 * 获取活期资金池可投份额(site项目首页用)
	 */
	 
	@Transactional(readOnly = true)
	public String getSurPlusAMount(WloanCurrentPool wloanCurrentPool) {

		String surPlusAmount = "";
		List<WloanCurrentPool> list = wloanCurrentPoolDao.findList(wloanCurrentPool);
		if (list != null && list.size() > 0) {
			wloanCurrentPool = list.get(0);
			Double surAmount = NumberUtils.scaleDouble(wloanCurrentPool.getSurplusAmount() == null ? 0.00 : wloanCurrentPool.getSurplusAmount());
			DecimalFormat df = new DecimalFormat("###,###,###.00");
			surPlusAmount = df.format(surAmount);
		}
		return surPlusAmount;
	}

}