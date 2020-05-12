package com.power.platform.credit.service.censusinfo;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.censusinfo.CreditCensusInfoDao;
import com.power.platform.credit.entity.censusinfo.CreditCensusInfo;

/**
 * 信贷人口普查Service
 * 
 * @author nice
 * @version 2017-03-23
 */
@Service("creditCensusInfoService")
public class CreditCensusInfoService extends CrudService<CreditCensusInfo> {

	@Resource
	private CreditCensusInfoDao creditCensusInfoDao;
	
	/**
	 * 户口信息传递文本表单字段3个(String类型的参数).
	 */
	public static final int IS_TEXT_FORM_FIELD_1 = 1;
	public static final int IS_TEXT_FORM_FIELD_2 = 2;
	
	@Override
	protected CrudDao<CreditCensusInfo> getEntityDao() {

		// TODO Auto-generated method stub
		return creditCensusInfoDao;
	}
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditCensusInfo(CreditCensusInfo creditCensusInfo,
			Map<String, String> map) {
		// TODO Auto-generated method stub
		creditCensusInfo.setCreateDate(new Date());
		creditCensusInfo.setUpdateDate(new Date());
		creditCensusInfo.setRemark("户口信息");
		int i = creditCensusInfoDao.insert(creditCensusInfo);
		return i;
	}
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateCreditCensusInfo(CreditCensusInfo creditCensusInfo,
			Map<String, String> map) {
		// TODO Auto-generated method stub
		creditCensusInfo.setId(map.get("censusId"));
		creditCensusInfo.setUpdateDate(new Date());
		int i = creditCensusInfoDao.update(creditCensusInfo);
		return i;
	}
	
	/**
	 * 用于后台查询
	 */
	public Page<CreditCensusInfo> findPage1(Page<CreditCensusInfo> page, CreditCensusInfo entity) {
		entity.setPage(page);
		page.setList(creditCensusInfoDao.findList1(entity));
		return page;
	}

}