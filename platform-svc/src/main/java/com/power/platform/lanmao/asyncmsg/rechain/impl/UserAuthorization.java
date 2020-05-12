package com.power.platform.lanmao.asyncmsg.rechain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.trade.service.UserAuthorizationNotifyService;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 交易接口， 用户授权
 */
public class UserAuthorization implements IResponsibility {

	private final static Logger logger = LoggerFactory.getLogger(UserAuthorization.class);

	private UserAuthorizationNotifyService userAuthorizationNotifyService;

	public UserAuthorization(UserAuthorizationNotifyService userAuthorizationNotifyService) {

		this.userAuthorizationNotifyService = userAuthorizationNotifyService;
	}

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.USER_AUTHORIZATION.getValue().equals(input.getServiceName().toUpperCase())) {
			System.out.println("用户授权，TODO ..... ");
			if (userAuthorizationNotifyService.userAuthorizationNotify(input)) {
				logger.info("用户授权异步通知业务处理成功 .......");
			}
			return;
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
