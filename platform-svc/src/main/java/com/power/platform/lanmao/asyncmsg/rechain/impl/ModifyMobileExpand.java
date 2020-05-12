package com.power.platform.lanmao.asyncmsg.rechain.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.lanmao.account.service.BusinessBindCardService;
import com.power.platform.lanmao.account.service.PersonBindCardService;
import com.power.platform.lanmao.asyncmsg.rechain.IResponsibility;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.type.AccessTypeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 预留手机号更新
 */
public class ModifyMobileExpand implements IResponsibility{

	private static final Logger log = LoggerFactory.getLogger(ModifyMobileExpand.class);


	private UserInfoDao userInfoDao;

	private PersonBindCardService personBindCardService;

	private BusinessBindCardService businessBindCardService;

	private LmTransactionDao lmTransactionDao;
	
	public ModifyMobileExpand(UserInfoDao userInfoDao,
			PersonBindCardService personBindCardService,
			BusinessBindCardService businessBindCardService,
			LmTransactionDao lmTransactionDao) {
		this.userInfoDao = userInfoDao;
		this.personBindCardService = personBindCardService;
		this.businessBindCardService = businessBindCardService;
		this.lmTransactionDao = lmTransactionDao;
	}
	
	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		
		if (input != null && ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue().equals(input.getServiceName().toUpperCase())) {
			try{	
				// TODO do something
				JSONObject json = JSONObject.parseObject(input.getRespData());
				String userId = json.getString("platformUserNo");
				String orderId = json.getString("requestNo");
				System.out.println("json=="+json.toJSONString());
				boolean flag  = false;
				log.info("预留手机号---------异步通知开始");
				if ("SUCCESS".equals(json.getString("status"))) { // 处理业务
					//.预留手机号更新会做四要素鉴权，四要素鉴权通过才能成功修改手机号。
					if(AccessTypeEnum.FULL_CHECKED.getValue().equals(json.getString("accessType"))) {
						// 重新获取一遍出借人信息(解决一些字段重新赋值的情况).
						UserInfo userInfo = userInfoDao.getCgb(userId);
						if(userInfo==null) {
							log.debug("平台没有这个出借人用户编号: userId="+userId);
						}else {
							personBindCardService.callbackModifyMobileExpand(json, userInfo);
						}
					}else { //四要素鉴权不通过
						LmTransaction lm = new LmTransaction();
						lm.setServiceName(ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue());
						lm.setPlatformUserNo(userId);
						lm.setRequestNo(orderId);
						List<LmTransaction> ltList = lmTransactionDao.findList(lm);
						if(ltList!=null||ltList.size()!=0) {
							LmTransaction lt = ltList.get(0);
							lt.setUpdateDate(new Date());
							lt.setCode(json.getString("code"));
							lt.setStatus(json.getString("status"));
							//鉴权通过类型
							lt.setAccessType(json.getString("accessType"));
							lt.setErrorCode(json.getString("errorCode"));
							lt.setErrorMessage(json.getString("errorMessage"));
							int m = lmTransactionDao.update(lt);
							if (m > 0) {
								log.info("失败---预留手机号更新流水号修改生成成功------异步通知");
							}
						}
					}
				}else {
					LmTransaction lm = new LmTransaction();
					lm.setServiceName(ServiceNameEnum.MODIFY_MOBILE_EXPAND.getValue());
					lm.setPlatformUserNo(userId);
					lm.setRequestNo(orderId);
					List<LmTransaction> ltList = lmTransactionDao.findList(lm);
					if(ltList!=null||ltList.size()!=0) {
						LmTransaction lt = ltList.get(0);
						lt.setUpdateDate(new Date());
						lt.setCode(json.getString("code"));
						lt.setStatus(json.getString("status"));
						//鉴权通过类型
						lt.setAccessType(json.getString("accessType"));
						lt.setErrorCode(json.getString("errorCode"));
						lt.setErrorMessage(json.getString("errorMessage"));
						int m = lmTransactionDao.update(lt);
						if (m > 0) {
							log.info("失败---预留手机号更新流水号修改成功------异步通知");
						}
					}
				}
				log.info("预留手机号更新完成------异步通知");
				System.out.println("预留手机号更新， todo ..... ");
				return;
			}catch(Exception e){
				throw new NotifyException("预留手机号更新异常------异步通知");
			}
		} 
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
