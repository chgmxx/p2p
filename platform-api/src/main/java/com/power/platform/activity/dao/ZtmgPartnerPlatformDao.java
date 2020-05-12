package com.power.platform.activity.dao;

import java.util.List;
import java.util.Map;

import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;

/**
 * 
 * 类: ZtmgPartnerPlatformDao <br>
 * 描述: ZTMG合作方信息DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月23日 下午3:03:59
 */
@MyBatisDao
public interface ZtmgPartnerPlatformDao extends CrudDao<ZtmgPartnerPlatform> {

	public abstract ZtmgPartnerPlatform getEntityByPlatformCode(String refer);
	
	public ZtmgPartnerPlatform getZtmgPartnerPlatformByPlatformCode(String refer);
	
	public List<ZtmgPartnerPlatform> findListForBrokerage(String id);
	
	public Map<String,Object> findListForBrokerage2(String transId);
	
	public String findIdForPartner(String phone);

	public List<ZtmgPartnerPlatform> findList1(ZtmgPartnerPlatform ztmgPartnerPlatform);

}