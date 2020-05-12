package com.power.platform.lanmao.asyncmsg.rechain.impl;

import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.IResponsibility;
import java.util.List;
import java.util.ArrayList;

/**
 * 将各个责任链的实现类，组织成一个完整的责任链模型
 */
public class ResponsibilityChain implements IResponsibility {
 
	// 将那保存完整责任链表中的实现类对象
	private List<IResponsibility> responsibilieyList = new ArrayList<IResponsibility>();
 
    // 索引
	private int index = 0;
 
	// 添加责任对象
	public ResponsibilityChain add(IResponsibility responsibility) {
		responsibilieyList.add(responsibility);
		return this;
	}
 
	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		// 所有遍历完了，直接返回
		if (index == responsibilieyList.size()) return;
		// 获取当前责任对象
		IResponsibility currentResponsibility = responsibilieyList.get(index);
		// 修改索引值，以便下次回调获取下个节点，达到遍历效果
		index++;
		// 调用当前责任对象处理方法
		currentResponsibility .doSomething(input, this);
	}
 
}