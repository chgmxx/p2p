package com.power.platform.lanmao.asyncmsg.rechain.impl;

import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.account.service.BusinessBindCardService;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.type.UserRoleEnum;

/**
 * 企业绑卡注册
 */
public class EnterpriseRegister implements IResponsibility {

	private static final Logger log = LoggerFactory.getLogger(EnterpriseRegister.class);

	private BusinessBindCardService businessBindCardService;

	private LanMaoWhiteListAddDataService whiteListAddDataService;
	public EnterpriseRegister(BusinessBindCardService businessBindCardService,LanMaoWhiteListAddDataService whiteListAddDataService) {
		this.businessBindCardService = businessBindCardService;
		this.whiteListAddDataService = whiteListAddDataService;
	}
	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void doSomething(NotifyVo input, IResponsibility responsibility) {

		if (input != null && ServiceNameEnum.ENTERPRISE_REGISTER.getValue().equals(input.getServiceName().toUpperCase())) {
			log.info("企业绑卡注册，todo ......");
			JSONObject respDataJsonObject = JSONObject.parseObject(input.getRespData());
			if ("SUCCESS".equals(respDataJsonObject.getString("status"))) {
				if (businessBindCardService.enterpriseRegisterNotify(input)) {
					log.info("企业绑卡注册，业务处理成功 ......");
				}
			}
			return;
		}
		// 当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}