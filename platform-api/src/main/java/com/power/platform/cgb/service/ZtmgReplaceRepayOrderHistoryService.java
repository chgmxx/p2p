/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.cgb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.ZtmgReplaceRepayOrderHistoryDao;
import com.power.platform.cgb.entity.ZtmgReplaceRepayOrderHistory;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 
 * class: ZtmgReplaceRepayOrderHistoryService <br>
 * description: 代偿还款订单历史Service. <br>
 * author: Roy <br>
 * date: 2019年8月1日 下午6:24:24
 */
@Service("ztmgReplaceRepayOrderHistoryService")
@Transactional(readOnly = false)
public class ZtmgReplaceRepayOrderHistoryService extends CrudService<ZtmgReplaceRepayOrderHistory> {

	@Autowired
	private ZtmgReplaceRepayOrderHistoryDao ztmgReplaceRepayOrderHistoryDao;

	@Override
	protected CrudDao<ZtmgReplaceRepayOrderHistory> getEntityDao() {

		return ztmgReplaceRepayOrderHistoryDao;
	}

}