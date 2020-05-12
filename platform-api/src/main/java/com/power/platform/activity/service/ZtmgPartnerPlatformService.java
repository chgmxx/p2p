package com.power.platform.activity.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.ZtmgPartnerPlatformDao;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

/**
 * 
 * 类: ZtmgPartnerPlatformService <br>
 * 描述: ZTMG合作方信息Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月23日 下午3:05:37
 */
@Service
@Transactional(readOnly = true)
public class ZtmgPartnerPlatformService extends CrudService<ZtmgPartnerPlatform> {

	/**
	 * 合作平台类型：个人电脑.
	 */
	public static final String PARTNER_PLATFORM_TYPE_1 = "1";
	/**
	 * 合作平台类型：移动设备.
	 */
	public static final String PARTNER_PLATFORM_TYPE_2 = "2";

	@Resource
	private ZtmgPartnerPlatformDao ztmgPartnerPlatformDao;

	@Override
	protected CrudDao<ZtmgPartnerPlatform> getEntityDao() {

		return ztmgPartnerPlatformDao;
	}
	
	public String findIdForPartner(String phone){
		String id  = ztmgPartnerPlatformDao.findIdForPartner(phone);
		return id;
	}
	
	
	public List<ZtmgPartnerPlatform> findListForBrokerage(String id){
		List<ZtmgPartnerPlatform> list = ztmgPartnerPlatformDao.findListForBrokerage(id);
		for(ZtmgPartnerPlatform entity:list){
			Map<String,Object> map = ztmgPartnerPlatformDao.findListForBrokerage2(entity.userTransDetail.transId);
			if(map!=null){
				entity.setMoneyToOne(Double.valueOf(map.get("moneyToOne").toString()));
				entity.setUserInfoName(map.get("userInfoName").toString());
			}
		}
		return list;
	}

	public Page<ZtmgPartnerPlatform> findPage1(Page<ZtmgPartnerPlatform> page,
			ZtmgPartnerPlatform ztmgPartnerPlatform) {
		ztmgPartnerPlatform.setPage(page);
		page.setList(ztmgPartnerPlatformDao.findList1(ztmgPartnerPlatform));
		return page;
	}
	
	

}