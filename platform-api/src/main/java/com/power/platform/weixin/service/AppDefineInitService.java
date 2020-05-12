package com.power.platform.weixin.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.power.platform.weixin.api.process.MpAccount;
import com.power.platform.weixin.api.process.WxMemoryCacheClient;
import com.power.platform.weixin.dao.AccountDao;
import com.power.platform.weixin.entity.Account;
 
/**
 * 系统启动时自动加载，把公众号信息加入到缓存中
 */
public class AppDefineInitService implements SpringBeanDefineService {

	@Autowired
	private AccountDao accountDao;
	
	public void initApplicationCacheData() {
		Account account = accountDao.getSingleAccount();
		if(account !=null ){
			MpAccount mpAcount = new MpAccount();
			mpAcount.setAccount(account.getAccount());
			mpAcount.setAppid(account.getAppid());
			mpAcount.setAppsecret(account.getAppsecret());
			mpAcount.setToken(account.getToken());
			mpAcount.setMsgcount(account.getMsgcount());
			mpAcount.setUrl(account.getUrl());
			WxMemoryCacheClient.addMpAccount(mpAcount);	
		}
		
	}
	
}
