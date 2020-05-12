package com.power.platform.lanmao.asyncmsg.rechain.impl;

import com.power.platform.lanmao.asyncmsg.rechain.IResponsibility;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 资金回退充值异步通知参数：
	（注：资金回退充值的异步通知是存管系统独立发送的，返回中serviceName对应为BACKROLL_RECHARGE）
 * @author chenhj ant-loiter.com
 *
 */
public class BackRollRecharge implements IResponsibility{

	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		if (input != null && ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue().equals(input.getServiceName().toUpperCase())) {
			try{
				// TODO do something
				System.out.println("资金回退充值异步通知， todo ..... ");
				// 暂不实现
				return ;
			}catch(Exception e){
				throw new NotifyException("资金回退充值异步通知异常");
			}
		}
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}

}
