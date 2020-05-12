package com.power.platform.lanmao.asyncmsg.rechain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.rw.service.LMWithdrawNotifyService;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 提现
 */
public class Withdraw implements IResponsibility{
	private Logger logger = LoggerFactory.getLogger(Withdraw.class);
	private LMWithdrawNotifyService lMWithdrawNotifyService ;
	public Withdraw(LMWithdrawNotifyService lMWithdrawNotifyService) {
		this.lMWithdrawNotifyService = lMWithdrawNotifyService;
	}
	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		if (input != null && ServiceNameEnum.WITHDRAW.getValue().equals(input.getServiceName().toUpperCase())) {
			// TODO do something
			logger.debug("收到异步通知， 开始处理提现业务， ..... ");
			String result = lMWithdrawNotifyService.pressLMRechargeNotify(input);
			logger.debug("提现异步通知处理结束，{}", result);
			return;
		}
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
