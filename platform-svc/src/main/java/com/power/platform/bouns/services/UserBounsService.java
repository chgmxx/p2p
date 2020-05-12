package com.power.platform.bouns.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.service.ActivityRestService;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;



/**
 * 用户积分系统接口信息
 * @author Mr.Jia
 * @version 2016-12-14
 */
@Component
@Path("/bouns")
@Service("userBounsService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserBounsService {
	
	
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Autowired
	private UserInfoService userInfoService;
	
	
	private static final Logger LOG = LoggerFactory.getLogger(ActivityRestService.class);
	
	/**
	 * 用户积分账户页面详细信息接口
	 * 如果用户第一次使用积分系统，创建积分账户
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/userBouns")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userBounsInfo(@FormParam("from")String from, @FormParam("token")String token){
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userInfo.getId());
					if (userBounsPoint != null) {
						map.put("name", userInfo.getName());
						map.put("score", userBounsPoint.getScore());
						map.put("createDate", userBounsPoint.getCreateDate());
					}
				}
			} else {
				throw new Exception();
			}
			
			result.put("state","0");
			result.put("message", "用户积分信息查询成功");
			result.put("data", map);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}

	
	
	/**
	 * 用户积分系统积分历史明细列表信息
	 * @param from
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @param bounsType
	 * @return
	 */
	@POST
	@Path("/userBounsHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userBounshistoryInfo( @FormParam("from")String from, @FormParam("token")String token,
			@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize,
			@FormParam("bounsType") String bounsType ){
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
				result.put("state", "1");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			Page<UserBounsHistory> page = new Page<UserBounsHistory>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.create_date DESC");
			
			UserBounsHistory userBounsHistory = new UserBounsHistory();
			if("1".equals(bounsType)){
				List<String> typeList = new ArrayList<String>();
				typeList.add("0");
				typeList.add("1");
				typeList.add("2");
				typeList.add("3");
				typeList.add("6");
				typeList.add("7");
				userBounsHistory.setTypeList(typeList);
			}else if("2".equals(bounsType)){
				List<String> typeList = new ArrayList<String>();
				typeList.add("4");
				typeList.add("5");
				userBounsHistory.setTypeList(typeList);
			}
			
			List<UserBounsHistory> listHistory = null;
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			UserBounsPoint userBounsPoint = new UserBounsPoint();
			Map<String, Object> data = new HashMap<String, Object>();
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					userBounsHistory.setUserId(userInfo.getId());
					Page<UserBounsHistory> bounsHistorys = userBounsHistoryService.findPage(page, userBounsHistory);
					listHistory = bounsHistorys.getList();
					for (int i = 0; i < listHistory.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						userBounsHistory = listHistory.get(i);
					
						map.put("number", pageNo.equals("1")? i + 1 : Integer.parseInt(pageSize) * (Integer.parseInt(pageNo) - 1) + (i + 1));//序号
						map.put("score",  userBounsHistory.getCurrentAmount() == null ? "---" : userBounsHistory.getCurrentAmount());//剩余积分
						map.put("id", userBounsHistory.getId());
						map.put("amount", userBounsHistory.getAmount());
						map.put("createDate", DateUtils.formatDateTime(userBounsHistory.getCreateDate()));
						map.put("bounsType", userBounsHistory.getBounsType());
						
						list.add(map);
					}
					
					data.put("userBounsHistory", list);
					data.put("pageNo", bounsHistorys.getPageNo());
					data.put("pageSize", bounsHistorys.getPageSize());
					data.put("totalCount", bounsHistorys.getCount());
					data.put("last", bounsHistorys.getLast());
					data.put("pageCount", bounsHistorys.getLast());
				}
			}
			
			result.put("state","0");
			result.put("message", "用户积分历史明细查询成功");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}
	
	@POST
	@Path("/getUserFriendsBouns")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUserFriendsBouns(@FormParam("token") String token, @FormParam("from") String from) {
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getUserBrokerage,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		
		try {

			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoService.getCgb(jedisUserId);
				if(user==null){
					user = userInfoService.get(jedisUserId);
				}
				if (null != user) {
					/**
					 * 数据域.
					 */
					Map<String, Object> data = new HashMap<String, Object>();
					Double bounsTotalAmount = userBounsHistoryService.bounsTotalAmount(user);
					data.put("bounsTotalAmount", bounsTotalAmount);
					
					
					LOG.info("getUserFriendsBouns,获取客户返利积分成功！");
					result.put("state", "0");
					result.put("message", "获取客户返利积分成功！");
					result.put("data", data);
					return result;
				} else {
					LOG.info("getUserFriendsBouns,客户账号信息为NULL！");
					result.put("state", "5");
					result.put("message", "客户账号信息为NULL！");
					result.put("data", null);
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getUserFriendsBouns,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
		
	}
}
