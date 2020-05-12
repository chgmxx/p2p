package com.power.platform.task;

import org.springframework.beans.factory.annotation.Autowired;

import com.power.platform.regular.service.WloanTermProjectService;


public class RegularWloanTermProjectTask{

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	
	/**
	 * 定期投资项目
	 * 定时上线任务调度
	 */
	public void regularWloanTermProjectTask() {
		wloanTermProjectService.wloanTermProjectOnLineTask(WloanTermProjectService.PUBLISH, WloanTermProjectService.ONLINE);
	}
	
}
