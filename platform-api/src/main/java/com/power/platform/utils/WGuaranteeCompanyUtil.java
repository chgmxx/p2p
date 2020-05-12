package com.power.platform.utils;

import java.util.ArrayList;
import java.util.List;

import com.power.platform.common.utils.CacheUtils;
import com.power.platform.common.utils.SpringContextHolder;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.service.WGuaranteeCompanyService;

/**
 * 担保公司缓存管理
 * @author lc 
 *
 */
public class WGuaranteeCompanyUtil {
	
	private static WGuaranteeCompanyService wGuaranteeCompanyService =SpringContextHolder.getBean("wGuaranteeCompanyService");
	public static final String WLOAN_CACHE="wloan";
	public static final String WGURANTEECOMPANY_CACHE_LIST = "wGuaranteeCompanyCache_list";
	
	/**
	 * 获取担保公司缓存
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<WGuaranteeCompany> getWGuaranteeCompany(){
		List<WGuaranteeCompany> list = new ArrayList<WGuaranteeCompany>();
		list =(List<WGuaranteeCompany>) CacheUtils.get(WLOAN_CACHE,WGURANTEECOMPANY_CACHE_LIST);
		if(list==null){
			WGuaranteeCompany entity =new WGuaranteeCompany();
			list =wGuaranteeCompanyService.findList(entity);
			CacheUtils.put(WLOAN_CACHE,WGURANTEECOMPANY_CACHE_LIST, list);
		}
		return list;
	}

 
	 
}
