package com.power.platform.regular.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.CacheUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.regular.dao.WGuaranteeCompanyDao;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.utils.WGuaranteeCompanyUtil;

/**
 * 担保机构Service interface.
 * @author lc
 *
 */
@Service("wGuaranteeCompanyService")
@Transactional(readOnly=true)
public class WGuaranteeCompanyService  extends CrudService<WGuaranteeCompany> {
	
	@Resource
	private WGuaranteeCompanyDao wGuaranteeCompanyDao;

	@Override
	protected CrudDao<WGuaranteeCompany> getEntityDao() {
		return wGuaranteeCompanyDao;
	}
	
	/**
	 * 从缓存里获得担保公司集合
	 * @param wloanTermProjectId 定期项目Id
	 * @return 担保公司集合
	 */
	public List<WGuaranteeCompany> findWGuaranteeCompanyListByCache() {
		List<WGuaranteeCompany> list = new ArrayList<WGuaranteeCompany>();
		WGuaranteeCompany wGuaranteeCompany = new WGuaranteeCompany();
		list = wGuaranteeCompanyDao.findList(wGuaranteeCompany);
		return list;
	}

	/**
	 * 新增/修改担保公司
	 * 并刷新缓存
	 * @param wGuaranteeCompany
	 */
	public void saveWGuaranteeCompany(WGuaranteeCompany wGuaranteeCompany) {
		
		if (StringUtils.isBlank(wGuaranteeCompany.getId())){
			wGuaranteeCompany.preInsert();
			wGuaranteeCompanyDao.insert(wGuaranteeCompany);
		} else{
			wGuaranteeCompany.preUpdate();
			wGuaranteeCompanyDao.update(wGuaranteeCompany);
		}
		@SuppressWarnings("unchecked")
		List<WGuaranteeCompany> cacheList = (List<WGuaranteeCompany>)CacheUtils.get(WGuaranteeCompanyUtil.WLOAN_CACHE ,WGuaranteeCompanyUtil.WGURANTEECOMPANY_CACHE_LIST);
		if(cacheList!=null){
			CacheUtils.remove(WGuaranteeCompanyUtil.WLOAN_CACHE ,WGuaranteeCompanyUtil.WGURANTEECOMPANY_CACHE_LIST);
		} 
		WGuaranteeCompanyUtil.getWGuaranteeCompany();		
	}
	
	/**
	 * 删除担保公司
	 * 并刷新缓存
	 * @param wGuaranteeCompany
	 */
	public void deleteWGuaranteeCompany(WGuaranteeCompany wGuaranteeCompany) {
		wGuaranteeCompanyDao.delete(wGuaranteeCompany);
		@SuppressWarnings("unchecked")
		List<WGuaranteeCompany> cacheList = (List<WGuaranteeCompany>)CacheUtils.get(WGuaranteeCompanyUtil.WLOAN_CACHE ,WGuaranteeCompanyUtil.WGURANTEECOMPANY_CACHE_LIST);
		if(cacheList!=null){
			CacheUtils.remove(WGuaranteeCompanyUtil.WLOAN_CACHE ,WGuaranteeCompanyUtil.WGURANTEECOMPANY_CACHE_LIST);
		} 
		WGuaranteeCompanyUtil.getWGuaranteeCompany();
	}
}
