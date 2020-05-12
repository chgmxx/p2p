package com.power.platform.lanmao.asyncmsg.rechain;

import com.power.platform.lanmao.rw.pojo.NotifyVo;
 
/**
 * 采用责任链模式来处理懒猫的通知消息
 * author: chenhj www.ant-loiter.com
 */
public interface IResponsibility { 
	// 处理逻辑的方法, 以服务名当为判断条件
	void doSomething(NotifyVo input, IResponsibility responsibility);
}