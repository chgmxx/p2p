package com.power.platform.weixin.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.weixin.dao.ZtmgWechatRelationDao;
import com.power.platform.weixin.entity.ZtmgWechatRelation;

/**
 * 
 * 类: ZtmgWechatRelationService <br>
 * 描述: 中投摩根，客户资料与微信资料关系建立Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月8日 下午4:23:50
 */
@Service
@Transactional(readOnly = true)
public class ZtmgWechatRelationService extends CrudService<ZtmgWechatRelation> {

	/**
	 * 1：关注成功.
	 */
	public static final String FOCUS_STATE_1 = "1";
	/**
	 * 2：取消关注.
	 */
	public static final String FOCUS_STATE_2 = "2";
	/**
	 * 3：于账户已绑定
	 */
	public static final String BIND_STATE_3 = "3";

	@Resource
	private ZtmgWechatRelationDao ztmgWechatRelationDao;

	@Override
	protected CrudDao<ZtmgWechatRelation> getEntityDao() {

		return ztmgWechatRelationDao;
	}

}