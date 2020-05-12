package com.power.platform.credit.service.addressinfo;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.addressinfo.CreditAddressInfoDao;
import com.power.platform.credit.entity.addressinfo.CreditAddressInfo;

/**
 * 信贷家庭住址Service
 * 
 * @author nice
 * @version 2017-03-23
 */
@Service("creditAddressInfoService")
public class CreditAddressInfoService extends CrudService<CreditAddressInfo> {

	/**
	 * 现住址信息传递文本表单字段3个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_3 = 3;
	public static final int IS_TEXT_FORM_FIELD_4 = 4;
	@Resource
	private CreditAddressInfoDao creditAddressInfoDao;
	
	@Override
	protected CrudDao<CreditAddressInfo> getEntityDao() {

		// TODO Auto-generated method stub
		return creditAddressInfoDao;
	}
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditAddressInfo(CreditAddressInfo creditAddressInfo,
			Map<String, String> map) {
		// TODO Auto-generated method stub
		String[] areas = map.get("area").split("--");
		String province = areas[0];
		String city = areas[1];
		creditAddressInfo.setCreditUserId(creditAddressInfo.getCreditUserId());
		creditAddressInfo.setAreaProvince(province);
		creditAddressInfo.setAreaCity(city);
		creditAddressInfo.setAddress(map.get("address"));
		creditAddressInfo.setCreateDate(new Date());
		creditAddressInfo.setUpdateDate(new Date());
		creditAddressInfo.setRemark("现住址信息");
		int i = creditAddressInfoDao.insert(creditAddressInfo);
		return i;
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditAddressInfo(CreditAddressInfo creditAddressInfo,
			Map<String, String> map) {
		// TODO Auto-generated method stub
		String[] areas = map.get("area").split("--");
		String province = areas[0];
		String city = areas[1];
		creditAddressInfo.setId(map.get("addressId"));
		creditAddressInfo.setAreaProvince(province);
		creditAddressInfo.setAreaCity(city);
		creditAddressInfo.setAddress(map.get("address"));
		creditAddressInfo.setUpdateDate(new Date());
		int i= creditAddressInfoDao.update(creditAddressInfo);
		return i;
	}
	
	/**
	 * 用于后台查询
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<CreditAddressInfo> findPage1(Page<CreditAddressInfo> page, CreditAddressInfo entity) {
		entity.setPage(page);
		page.setList(creditAddressInfoDao.findList1(entity));
		return page;
	}

}