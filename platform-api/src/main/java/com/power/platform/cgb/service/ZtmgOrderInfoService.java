package com.power.platform.cgb.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.cgb.entity.ZtmgOrderInfo;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;

/**
 * 中投摩根业务订单信息Service
 * 
 * @author lance
 * @version 2018-02-06
 */
@Service("ztmgOrderInfoService")
@Transactional(readOnly = false)
public class ZtmgOrderInfoService extends CrudService<ZtmgOrderInfo> {

	/**
	 * 回调状态，S:成功.
	 */
	public static final String STATUS_S = "S";

	/**
	 * 回调状态，F:失败.
	 */
	public static final String STATUS_F = "F";

	/**
	 * 回调状态，AS:受理成功.
	 */
	public static final String STATUS_AS = "AS";

	/**
	 * 还款状态，1：未还.
	 */
	public static final String STATE_1 = "1";

	/**
	 * 还款状态，2：已还.
	 */
	public static final String STATE_2 = "2";

	/**
	 * 类型，1：借款户.
	 */
	public static final String TYPE_1 = "1";

	/**
	 * 类型，2：代偿户.
	 */
	public static final String TYPE_2 = "2";

	@Resource
	private ZtmgOrderInfoDao ztmgOrderInfoDao;

	@Override
	protected CrudDao<ZtmgOrderInfo> getEntityDao() {

		return ztmgOrderInfoDao;
	}

}