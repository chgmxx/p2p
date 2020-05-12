package com.power.platform.lanmao.asyncmsg.rechain.impl;

import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 交易接口， 验密扣费
 */
public class VerifyDeduct implements IResponsibility{

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		if (input != null && ServiceNameEnum.VERIFY_DEDUCT.getValue().equals(input.getServiceName().toUpperCase())) {
			// TODO do something
			System.out.println("交易接口， 验密扣费， todo ..... ");
			return;
		}
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
}
