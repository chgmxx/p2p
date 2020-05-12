package com.power.platform.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.MsgBaseDao;
import com.power.platform.weixin.entity.MsgBase;

@Transactional(readOnly = false)
@Service("msgBaseService")
public class MsgBaseService extends CrudService<MsgBase> {
	
	@Autowired
	private MsgBaseDao msgTextDao;
	 
	protected CrudDao<MsgBase> getEntityDao() {
		return msgTextDao;
	}
	
}

