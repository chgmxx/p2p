package com.power.platform.credit.service.houseinfo;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.houseinfo.CreditHouseInfoDao;
import com.power.platform.credit.entity.addressinfo.CreditAddressInfo;
import com.power.platform.credit.entity.houseinfo.CreditHouseInfo;

/**
 * 信贷房产信息Service
 * 
 * @author nice
 * @version 2017-03-23
 */
@Service("creditHouseInfoService")
public class CreditHouseInfoService extends CrudService<CreditHouseInfo> {

	/**
	 * 房产信息传递文本表单字段3个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_3 = 3;
	public static final int IS_TEXT_FORM_FIELD_4 = 4;
	@Resource
	private CreditHouseInfoDao creditHouseInfoDao;
	
	
	@Override
	protected CrudDao<CreditHouseInfo> getEntityDao() {

		// TODO Auto-generated method stub
		return creditHouseInfoDao;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditHouseInfo(CreditHouseInfo creditHouseInfo,
			Map<String, String> map) {
		// TODO Auto-generated method stub
		String[] areas = map.get("area").split("--");
		String province = areas[0];
		String city = areas[1];
		creditHouseInfo.setAreaProvince(province);
		creditHouseInfo.setAreaCity(city);
		creditHouseInfo.setAddress(map.get("address"));
		creditHouseInfo.setCreateDate(new Date());
		creditHouseInfo.setUpdateDate(new Date());
		creditHouseInfo.setRemark("房产信息");
		int i = creditHouseInfoDao.insert(creditHouseInfo);
		return i;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditHouseInfo(CreditHouseInfo creditHouseInfo,
			Map<String, String> map) {
		// TODO Auto-generated method stub
		String[] areas = map.get("area").split("--");
		String province = areas[0];
		String city = areas[1];
		creditHouseInfo.setId(map.get("houseId"));
		creditHouseInfo.setAreaProvince(province);
		creditHouseInfo.setAreaCity(city);
		creditHouseInfo.setAddress(map.get("address"));
		creditHouseInfo.setUpdateDate(new Date());
		int i = creditHouseInfoDao.update(creditHouseInfo);
		return i;
	}
	
	/**
	 * 用于后台查询
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<CreditHouseInfo> findPage1(Page<CreditHouseInfo> page,
			CreditHouseInfo entity) {
		// TODO Auto-generated method stub
		entity.setPage(page);
		page.setList(creditHouseInfoDao.findList1(entity));
		return page;
	}
}