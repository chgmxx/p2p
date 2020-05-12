package com.power.platform.lanmao.asyncmsg.rechain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.trade.service.LanMaoUserPreTenderTransactionService;
import com.power.platform.lanmao.type.ConfirmTradeTypEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 用户预处理
 */
public class UserPreTransaction implements IResponsibility {

	private static final Logger log = LoggerFactory.getLogger(UserPreTransaction.class);

	private LanMaoUserPreTenderTransactionService lanMaoUserPreTenderTransactionService;

	public UserPreTransaction(LanMaoUserPreTenderTransactionService lanMaoUserPreTenderTransactionService) {

		this.lanMaoUserPreTenderTransactionService = lanMaoUserPreTenderTransactionService;
	}

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.USER_PRE_TRANSACTION.getValue().equals(input.getServiceName().toUpperCase())) {
			log.info("用户预处理，TODO ......");
			JSONObject respDataJsonObject = JSONObject.parseObject(input.getRespData());
			String bizType = respDataJsonObject.getString("bizType"); // bizType：预处理业务类型，确定此次业务逻辑
			if (ConfirmTradeTypEnum.TENDER.getValue().equals(bizType)) { // 出借
				log.info("出借用户预处理，...start...");
				if (lanMaoUserPreTenderTransactionService.userPreTenderTransactionNotify(input)) {
					log.info("出借用户预处理，业务处理成功...end...");
				}
			}
			return;
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
