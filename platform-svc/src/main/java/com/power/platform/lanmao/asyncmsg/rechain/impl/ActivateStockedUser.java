package com.power.platform.lanmao.asyncmsg.rechain.impl;

import com.power.platform.lanmao.rw.pojo.NotifyVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.lanmao.account.service.ActivateStockedUserNotifyService;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.rw.pojo.NotifyException;

/**
 * 会员激活
 */
public class ActivateStockedUser implements IResponsibility {

	private static final Logger logger = LoggerFactory.getLogger(ActivateStockedUser.class);

	private ActivateStockedUserNotifyService activateStockedUserNotifyService;

	public ActivateStockedUser(ActivateStockedUserNotifyService activateStockedUserNotifyService) {

		this.activateStockedUserNotifyService = activateStockedUserNotifyService;
	}

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue().equals(input.getServiceName().toUpperCase())) {
			try {
				logger.info("会员激活， todo ...start...");
				activateStockedUserNotifyService.processingNotice(input);
				logger.info("会员激活， todo ...end...");
				return;
			} catch (Exception e) {
				throw new NotifyException("会员激活异常");
			}
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
