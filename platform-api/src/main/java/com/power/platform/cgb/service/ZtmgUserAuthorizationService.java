package com.power.platform.cgb.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 
 * 类: ZtmgUserAuthorizationService <br>
 * 描述: 中投摩根用户授权信息Service. <br>
 * 作者: Mr.li <br>
 * 时间: 2018年11月26日 下午1:32:16
 */
@Service("ztmgUserAuthorizationService")
@Transactional(readOnly = false)
public class ZtmgUserAuthorizationService extends CrudService<ZtmgUserAuthorization> {

	@Resource
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;

	@Override
	protected CrudDao<ZtmgUserAuthorization> getEntityDao() {

		return ztmgUserAuthorizationDao;
	}

}