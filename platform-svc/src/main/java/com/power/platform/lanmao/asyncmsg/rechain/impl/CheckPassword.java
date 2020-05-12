package com.power.platform.lanmao.asyncmsg.rechain.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 验证密码
 */
public class CheckPassword implements IResponsibility{

	private static final Logger log = LoggerFactory.getLogger(CheckPassword.class);

	private LmTransactionDao lmTransactionDao;
	public CheckPassword(LmTransactionDao lmTransactionDao) {
		this.lmTransactionDao = lmTransactionDao;
	}
	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		if (input != null && ServiceNameEnum.CHECK_PASSWORD.getValue().equals(input.getServiceName().toUpperCase())) {
			try{
				// TODO do something
				JSONObject json = JSONObject.parseObject(input.getRespData());
				String userId = json.getString("platformUserNo");
				String orderId = json.getString("requestNo");
				if("SUCCESS".equals(json.getString("status"))) {
					LmTransaction lmt = lmTransactionDao.get(orderId);
					if(lmt==null||"".equals(lmt)) {
						//在数据库保存流水数据
						LmTransaction lt = new LmTransaction();
						lt.setPlatformUserNo(userId);
						lt.setServiceName(ServiceNameEnum.CHECK_PASSWORD.getValue());
						lt.setRequestNo(orderId);
						lt.setCode(json.getString("code"));
						lt.setStatus(json.getString("status"));
						lt.setCreateDate(new Date());
						lt.setUpdateDate(new Date());
						lt.setId(orderId);
						lmTransactionDao.insert(lt);
					}else {
						lmt.setCode(json.getString("code"));
						lmt.setStatus(json.getString("status"));
						lmt.setUpdateDate(new Date());
						lmTransactionDao.update(lmt);
					}
				}else {
					LmTransaction lmt = lmTransactionDao.get(orderId);
					if(lmt==null||"".equals(lmt)) {
						//在数据库保存流水数据
						LmTransaction lt = new LmTransaction();
						lt.setPlatformUserNo(userId);
						lt.setServiceName(ServiceNameEnum.CHECK_PASSWORD.getValue());
						lt.setRequestNo(orderId);
						lt.setCode(json.getString("code"));
						lt.setStatus(json.getString("status"));
						lt.setCreateDate(new Date());
						lt.setUpdateDate(new Date());
						lt.setId(orderId);
						lt.setErrorCode(json.getString("errorCode"));
						lt.setErrorMessage(json.getString("errorMessage"));
						lmTransactionDao.insert(lt);
					}else {
						lmt.setCode(json.getString("code"));
						lmt.setStatus(json.getString("status"));
						lmt.setUpdateDate(new Date());
						lmt.setErrorCode(json.getString("errorCode"));
						lmt.setErrorMessage(json.getString("errorMessage"));
						lmTransactionDao.update(lmt);
					}
				}
				log.info("验证密码完成");
				System.out.println("验证密码， todo ..... ");
				return;
			}catch(Exception e){
				throw new NotifyException("验证密码异常");
			}
		}
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
} 
