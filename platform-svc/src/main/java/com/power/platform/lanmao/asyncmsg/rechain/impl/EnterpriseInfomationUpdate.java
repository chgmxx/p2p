package com.power.platform.lanmao.asyncmsg.rechain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.trade.service.EnterpriseInformationUpdateNotifyService;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 企业信息修改
 */
public class EnterpriseInfomationUpdate implements IResponsibility {

	private final static Logger logger = LoggerFactory.getLogger(EnterpriseInfomationUpdate.class);

	private EnterpriseInformationUpdateNotifyService enterpriseInformationUpdateNotifyService;

	public EnterpriseInfomationUpdate(EnterpriseInformationUpdateNotifyService enterpriseInformationUpdateNotifyService) {

		this.enterpriseInformationUpdateNotifyService = enterpriseInformationUpdateNotifyService;
	}

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue().equals(input.getServiceName().toUpperCase())) {
			try {
				logger.info("主动异步通知，企业信息修改-todo ......");
				if (enterpriseInformationUpdateNotifyService.enterpriseInformationUpdateNotify(input)) {
					logger.info("主动异步通知，企业信息修改-业务处理成功 ......");
				}
				return;
			} catch (Exception e) {
				throw new NotifyException("企业信息修改，主动异步通知异常 ......");
			}
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
