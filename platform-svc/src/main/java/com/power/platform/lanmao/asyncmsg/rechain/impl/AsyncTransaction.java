package com.power.platform.lanmao.asyncmsg.rechain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.trade.service.AsyncTransactionNotifyService;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 批量交易
 */
public class AsyncTransaction implements IResponsibility {

	private final static Logger logger = LoggerFactory.getLogger(AsyncTransaction.class);

	private AsyncTransactionNotifyService asyncTransactionNotifyService;

	public AsyncTransaction(AsyncTransactionNotifyService asyncTransactionNotifyService) {

		this.asyncTransactionNotifyService = asyncTransactionNotifyService;
	}

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.ASYNC_TRANSACTION.getValue().equals(input.getServiceName().toUpperCase())) {
			logger.info("批量交易，TODO ......");
			if (asyncTransactionNotifyService.asyncTransactionNotify(input)) {
				logger.info("批量交易，主动异步通知业务处理成功 ......");
			}
			return;
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
