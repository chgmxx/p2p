package com.power.platform.lanmao.trade.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.IdGen;
import com.power.platform.lanmao.dao.AsyncTransactionLogDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.AsyncTransactionLog;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.ProjectStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 
 * class: AsyncTransactionNotifyService <br>
 * description: 批量交易异步通知业务处理 <br>
 * author: Roy <br>
 * date: 2019年10月6日 下午5:43:31
 */
@Service("asyncTransactionNotifyService")
public class AsyncTransactionNotifyService {

	private final static Logger logger = LoggerFactory.getLogger(AsyncTransactionNotifyService.class);

	@Autowired
	private AsyncTransactionLogDao asyncTransactionLogDao;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private LanMaoProjectService lanMaoProjectService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;

	/**
	 * 
	 * methods: asyncTransactionNotify <br>
	 * description: 批量交易异步通知业务处理 <br>
	 * author: Roy <br>
	 * date: 2019年10月6日 下午6:17:55
	 * 
	 * @param input
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean asyncTransactionNotify(NotifyVo input) {

		boolean flag = false;
		try {
			// 业务数据报文，JSON格式，具体见各接口定义
			JSONObject jsonObject = JSONObject.parseObject(input.getRespData());
			String detailsStr = jsonObject.getString("details");
			JSONArray details = JSONArray.fromObject(detailsStr);
			List list = (List) JSONArray.toCollection(details, AsyncTransactionLog.class);
			Iterator it = list.iterator();
			AsyncTransactionLog atlQuery = null;
			long currentTimeMillis = System.currentTimeMillis();
			String userPlanId = ""; // 用户还款计划主键
			while (it.hasNext()) {
				currentTimeMillis = currentTimeMillis + 1000;
				AsyncTransactionLog atlNext = (AsyncTransactionLog) it.next(); // 交易订单明细
				atlQuery = new AsyncTransactionLog();
				atlQuery.setAsyncRequestNo(atlNext.getAsyncRequestNo()); // 交易明细订单号
				List<AsyncTransactionLog> atls = asyncTransactionLogDao.findList(atlQuery);
				for (AsyncTransactionLog atl : atls) {
					userPlanId = atl.getFreezeRequestNo();
					atl.setBizType(atlNext.getBizType()); // 交易类型
					atl.setBizOrigin(atlNext.getBizOrigin()); // 业务来源
					atl.setCreateTime(atlNext.getCreateTime()); // 交易发起时间
					atl.setTransactionTime(atlNext.getTransactionTime()); // 交易完成时间
					atl.setStatus(atlNext.getStatus()); // 交易状态
					atl.setErrorCode(atlNext.getErrorCode()); // 错误码
					atl.setErrorMessage(atlNext.getErrorMessage()); // 错误码描述
					atl.setUpdateDate(new Date(currentTimeMillis));
					int updateAtlFlag = asyncTransactionLogDao.update(atl);
					logger.info("批量交易日志更新:{}", updateAtlFlag == 1 ? "成功" : "失败");
				}
			}
			// 查询当前用户还款计划批次的还款类型，1：还本付息，2：付息，针对还本付息操作标的截标
			LmTransaction lt = null;
			WloanTermUserPlan userPlan = wloanTermUserPlanDao.get(userPlanId);
			if (null != userPlan) {
				if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(userPlan.getPrincipal())) { // 还本付息
					// 标的信息
					WloanTermProject project = wloanTermProjectService.get(userPlan.getProjectId());
					if (null != project) {
						String sn = project.getSn();
						String requestNo = IdGen.uuid();
						String borrowersSourcePlatformUserNo = "";
						WloanSubject subject = wloanSubjectService.get(project.getSubjectId()); // 融资主体
						if (null != subject) {
							borrowersSourcePlatformUserNo = subject.getLoanApplyId(); // 借款人
						}
						Map<String, Object> modifyProjectMap = lanMaoProjectService.modifyProject(requestNo, sn, ProjectStatusEnum.FINISH.getValue());
						if (BusinessStatusEnum.SUCCESS.getValue().equals(modifyProjectMap.get("status")) && "0".equals(modifyProjectMap.get("code"))) {
							logger.info("最后一期还本付息，标的生命周期结束，截标成功......");
							// 平台留存
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
							lt.setBatchNo(requestNo); // 批次号
							lt.setRequestNo(requestNo);
							lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
							lt.setCode(modifyProjectMap.get("code").toString());
							lt.setStatus(modifyProjectMap.get("status").toString());
							lt.setRemarks("最后一期还本付息，标的生命周期结束，截标");
							lt.setProjectNo(sn);// 标的编号
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertLmtFlag = lmTransactionDao.insert(lt);
							logger.info("最后一期还本付息，标的生命周期结束，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
						} else {
							logger.info("最后一期还本付息，标的生命周期结束，截标成功......");
							// 平台留存
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
							lt.setBatchNo(requestNo); // 批次号
							lt.setRequestNo(requestNo);
							lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
							lt.setCode(modifyProjectMap.get("code").toString());
							lt.setStatus(modifyProjectMap.get("status").toString());
							lt.setRemarks("最后一期还本付息，标的生命周期结束，截标");
							lt.setProjectNo(sn);// 标的编号
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertLmtFlag = lmTransactionDao.insert(lt);
							logger.info("最后一期还本付息，标的生命周期结束，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
						}
					}
				}
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	}
}
