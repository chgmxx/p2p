package com.power.platform.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.ifcert.status.service.ScatterInvestStatusDataAccessService;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

@Service("projectToOnlineTask")
@Lazy(false)
public class ProjectToOnlineTask {

	private static final Logger logger = LoggerFactory.getLogger(ProjectToOnlineTask.class);

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private ScatterInvestStatusDataAccessService scatterInvestStatusDataAccessService;

	/**
	 * 
	 * 方法: getPublishProjects <br>
	 * 描述: 缓存数据库中，获取存放标的ID的List列表，处理上线逻辑，每30秒执行一次 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月15日 下午9:09:34
	 */
	@Scheduled(cron = "0/30 * * * * ?")
	public void getPublishProjects() {

		String key = "PROJECT:ONLINE:LIST";
		int beginIndex = 0;
		int endIndex = -1;
		Long count = 1L; // 默认删除一个元素
		try {

			logger.info("标的定时上线任务轮询...start...");
			// 判断key是否存在
			if (JedisUtils.exists(key)) {
				// 不存在重复的元素
				List<String> lRangeList = JedisUtils.lRange(key, beginIndex, endIndex);
				WloanTermProject project = null;
				long currentTimeMillis = System.currentTimeMillis();
				for (String element : lRangeList) {
					logger.info("element:{}", element);
					// 查询标的信息
					project = wloanTermProjectService.get(element);
					if (null != project) {
						if (WloanTermProjectService.PUBLISH.equals(project.getState())) {
							// 发布状态的标的，获取标的上线时间跟系统时间比较，是否满足上线
							if (DateUtils.compare_date(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"), DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"))) {
								project.setState(WloanTermProjectService.ONLINE);
								project.setUpdateDate(new Date(currentTimeMillis));
								project.setOnlineDate(new Date(currentTimeMillis));
								wloanTermProjectService.updateProState(project);
								logger.info("轮询发布状态的标的，操作上线完成......");
								// 判断上线日期，是否满足上线，true：删除该元素，false：保留该元素
								Long lRem = JedisUtils.lRem(key, count, element);
								logger.info("lRem:{}", lRem);
							}
						} else {
							// 判断上线日期，是否满足上线，true：删除该元素，false：保留该元素
							Long lRem = JedisUtils.lRem(key, count, element);
							logger.info("lRem:{}", lRem);
						}
					}
				}
			} else {
				logger.info("key not exists....");
			}
			logger.info("标的定时上线任务轮询...end...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 每1小时秒执行一次
	 * 从数据库里拿到发布状态的项目（待上线）
	 */
	// @Scheduled(cron="0 0/30 * * * ?")
	public void getProPublishFromDb() {

		try {
			logger.info("定时器ProjectToOnlineTask查询待上线项目开始");

			WloanTermProject project = new WloanTermProject();
			project.setState(WloanTermProjectService.PUBLISH);
			List<WloanTermProject> projectList = wloanTermProjectService.findList(project);
			List<Object> objProjectList = new ArrayList<Object>();
			for (Object object : projectList) {
				objProjectList.add(object);
			}
			if (projectList != null && projectList.size() > 0) {
				JedisUtils.setObject("waitToOnlineProjectList", objProjectList, 0);
				logger.info("定时器ProjectToOnlineTask查询待上线成功，list > 0, 缓存更新成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("定时器ProjectToOnlineTask查询待上线项目异常");
		}

	}

	/**
	 * 每1分钟执行一次
	 * 从memcache缓存里拿到发布状态的项目（待上线）
	 */
	// @Scheduled(cron = "0 0/1 * * * ?")
	public void changeProState() {

		try {
			logger.info("定时器ProjectToOnlineTask修改待上线项目状态为上线状态开始");
			@SuppressWarnings("unchecked")
			List<Object> objProjectList = (List<Object>) JedisUtils.getObject("waitToOnlineProjectList");
			if (objProjectList != null) {
				logger.info("projectList.size = " + objProjectList.size());
				Iterator<Object> iterator = objProjectList.iterator();
				while (iterator.hasNext()) {
					Object object = iterator.next();

					if (object instanceof WloanTermProject) {
						WloanTermProject project = (WloanTermProject) object;
						Date onlineDate = project.getOnlineDate();
						long onLineDateTime = onlineDate.getTime();
						long nowTime = new Date().getTime();

						// 项目上线日期已到，修改项目为上线状态
						if (nowTime >= onLineDateTime) {
							logger.info("定时器ProjectToOnlineTask:" + project.getName() + "已到上线日期，修改为上线状态开始");
							if (project.getState() == WloanTermProjectService.PUBLISH || project.getState().equals(WloanTermProjectService.PUBLISH)) {
								project.setState(WloanTermProjectService.ONLINE);
								wloanTermProjectService.updateProState(project);
								iterator.remove();
								logger.info("定时器ProjectToOnlineTask 修改项目：" + project.getName() + "为已上线状态成功");
							}
						} else {
							logger.info("定时器ProjectToOnlineTask 项目：" + project.getName() + "待上线，未到上线时间");
						}
					}

				}
				JedisUtils.setObject("waitToOnlineProjectList", objProjectList, 0);
			} else {
				logger.info("定时器ProjectToOnlineTask: 没有待上线项目");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("定时器ProjectToOnlineTask修改待上线项目状态为上线状态异常");
		}
	}
}
