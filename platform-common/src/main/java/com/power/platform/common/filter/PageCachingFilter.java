/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.common.filter;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

import com.power.platform.common.utils.SpringContextHolder;

/**
 * 页面高速缓存过滤器
 * @author ThinkGem
 * @version 2013-8-5
 */
public class PageCachingFilter extends SimplePageCachingFilter {
	
	private static CacheManager cacheManager = ((CacheManager)SpringContextHolder.getBean("cacheManager"));

	 
	protected CacheManager getCacheManager() {
		return cacheManager; 
	}
	
}
