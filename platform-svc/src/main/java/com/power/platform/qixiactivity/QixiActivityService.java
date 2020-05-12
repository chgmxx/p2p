package com.power.platform.qixiactivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.utils.DateUtil;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.pay.utils.BankCodeUtils;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao; 
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;


@Path("/qixi")
@Service("qixiActivityService")
@Produces(MediaType.APPLICATION_JSON)
public class QixiActivityService {

	Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	
	/**
	 * Qixi活动页面数据请求
	 * @param from
	 * @return
	 */
	@POST
	@Path("/index")
	public Map<String, Object> index(@FormParam("from") String from, @FormParam("token") String token ) {
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 1、判断是否存在token
		 * 2、根据token获取用户信息
		 * 3、根据用户信息获取用户投资信息
		 * 4、根据投资信息得到用户奖励级别
		 */
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "参数token为空");
			result.put("data", 0);
			return result;
		}
		
		try {
			// 获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			long steps = 0L;
			if (null != principal) {
				UserInfo userInfo = userInfoDao.get(principal.getUserInfo().getId());
				if (userInfo != null) {
					WloanTermInvest invest = new WloanTermInvest();
					invest.setUserInfo(userInfo);
					invest.setBeginBeginDate(QixiGetStep.getDateOfString("2016-08-04 00:00:00"));
					invest.setEndBeginDate(QixiGetStep.getDateOfString("2016-08-31 23:59:59"));
					List<WloanTermInvest> investsList = wloanTermInvestService.findList(invest);
					
					
					// 开始计算里程数
					if(investsList != null && investsList.size() > 0){
						for (int i = 0; i < investsList.size(); i++) {
							invest = investsList.get(i);
							// 里程数计算公式
							// 里程 = 投资金额*该项目月份数，如8月4日投资人A投资30天项目20000元，8月6日投资180天项目10000元，则A的奔跑里程为：20000*1+10000*6=80000
							steps += invest.getAmount() * ( invest.getWloanTermProject().getSpan() / 30 );
						}
					}
					
					// 根据里程数获得页面对应的点
					Integer step = QixiGetStep.getSterp(steps);
					result.put("state", "0");
					result.put("message", "查询成功");
					result.put("data", step);
				}
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", 0);
		}
		
		return result;
	}
}
