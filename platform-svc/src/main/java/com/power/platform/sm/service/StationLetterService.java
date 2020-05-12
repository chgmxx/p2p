package com.power.platform.sm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 站内信
 */
@Component
@Path("/station")
@Service("stationLetterService")
@Produces(MediaType.APPLICATION_JSON)
public class StationLetterService {
	private static final Logger logger = LoggerFactory.getLogger(StationLetterService.class);
	
	@Autowired
	private StationLettersService stationLettersService;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 获取站内信列表
	 * @param from
	 * @param token
	 * @param state
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/stationList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getStationLetterList(@FormParam("from") String from, @FormParam("token") String token, @FormParam("state") String state,
			@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(token) || StringUtils.isBlank(state) || StringUtils.isBlank(pageNo) 
				|| StringUtils.isBlank(from) || StringUtils.isBlank(pageSize)) {
			logger.info("fn:getStationLetterList,缺少必要参数！");
			result.put("state", "1");
			result.put("message", "缺少必要参数！");
			return result;
		}
		
		try {
			String jedisUserId = JedisUtils.get(token);
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if(userInfo==null){
				userInfo = userInfoService.get(jedisUserId);
			}
			
			StationLetter letter = new StationLetter();
			letter.setUserId(userInfo.getId());
			if (state == "1") {			// 未读信件
				letter.setState("1");
			} else if(state == "2"){	// 已读信件
				letter.setState("2");
			} else {
				letter.setState(null);
			}
			
			Page<StationLetter> page = new Page<StationLetter>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.send_time DESC");
			page = stationLettersService.findPage(page, letter);
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if (page.getList() != null && page.getList().size() > 0) {
				for (StationLetter stationLetter : page.getList()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("id", stationLetter.getId());
					map.put("userId", stationLetter.getUserId());
					map.put("letterType", stationLetter.getLetterType());
					map.put("title", stationLetter.getTitle());
					map.put("body", stationLetter.getBody());
					map.put("state", stationLetter.getState());
					map.put("sendTime", DateUtils.formatDate(stationLetter.getSendTime(),"yyyy-MM-dd"));
					list.add(map);
				}
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("pageNo", pageNo);									
			data.put("pageSize", pageSize);
			data.put("count", String.valueOf(page.getCount()));
			data.put("lastPage", String.valueOf(page.getLast()));
			data.put("letters", list);
			
			result.put("state", "0");
			result.put("message", "信息查询成功");
			result.put("data", data);
			
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
			logger.info("fn:getStationLetterList,系统异常！");
		}
		return result;
	}
	
	/**
	 * 站内信是否未读
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/letterState")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getLetterState(@FormParam("from") String from, @FormParam("token") String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(token) ) {
			logger.info("fn:getStationLetterList,缺少必要参数！");
			result.put("state", "1");
			result.put("message", "缺少必要参数！");
			return result;
		}
		
		try {
			String jedisUserId = JedisUtils.get(token);
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if(userInfo==null){
				userInfo = userInfoService.get(jedisUserId);
			}
			
			StationLetter letter = new StationLetter();
			letter.setUserId(userInfo.getId());
			letter.setState("1");// 未读信件
			Map<String, Object> data = new HashMap<String, Object>();
			List<StationLetter> list = stationLettersService.findList(letter);
			if (list != null && list.size() > 0) {
				data.put("letterState", "1");//有未读消息
			}else{
				data.put("letterState", "0");//没有未读消息
			}
			result.put("state", "0");
			result.put("message", "信息查询成功");
			result.put("data", data);
			
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
			logger.info("fn:getStationLetterList,系统异常！");
		}
		return result;
	}
	
	
	/**
	 * 站内信详细信息
	 * @param from
	 * @param token
	 * @param letterId
	 * @return
	 */
	@POST
	@Path("/letterInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getStationLetterInfo(@FormParam("from") String from, @FormParam("token") String token, @FormParam("letterId") String letterId) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(letterId)) {
			logger.info("fn:getStationLetterInfo,缺少必要参数！");
			result.put("state", "1");
			result.put("message", "缺少必要参数！");
			return result;
		}
		
		try {
			StationLetter letter = new StationLetter();
			letter.setId(letterId);
			letter = stationLettersService.get(letter);
			Map<String, String> map = new HashMap<String, String>();
			if(letter != null){
				if(letter.getState().equals(StationLettersService.LETTER_STATE_UNREAD)){
					List<StationLetter> list = new ArrayList<StationLetter>();
					letter.setState(StationLettersService.LETTER_STATE_READ);
					list.add(letter);
					stationLettersService.updateByUserId(list);
				}
				map.put("id", letter.getId());
				map.put("userId", letter.getUserId());
				map.put("letterType", letter.getLetterType());
				map.put("title", letter.getTitle());
				map.put("body", letter.getBody());
				map.put("state", letter.getState());
				map.put("sendTime", DateUtils.formatDate(letter.getSendTime(),"yyyy-MM-dd"));
			}
			result.put("state", "0");
			result.put("message", "信息查询成功");
			result.put("data", map);
			
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
			logger.info("fn:getStationLetterInfo,系统异常！");
		}
		return result;
	}
	
	
	
	
	/**
	 * 修改用户站内信为已读（一键已读）
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/changeLetterState")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> changeLetterState(@FormParam("from") String from, @FormParam("token") String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
			logger.info("fn:getStationLetterList,缺少必要参数！");
			result.put("state", "1");
			result.put("message", "缺少必要参数！");
			return result;
		}
		
		try {
			String jedisUserId = JedisUtils.get(token);
			UserInfo userInfo = userInfoService.getCgb(jedisUserId);
			if(userInfo==null){
				userInfo = userInfoService.get(jedisUserId);
			}
			
			StationLetter letter = new StationLetter();
			letter.setUserId(userInfo.getId());
			letter.setState("1");
			List<StationLetter> list = stationLettersService.findList(letter);
			int flag = 0;
			if(list != null && list.size() > 0){
				flag = stationLettersService.updateByUserId(list);
			}
			
			result.put("state", "0");
			result.put("message", "信息修改成功");
			result.put("data", flag);
			
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
			logger.info("fn:getStationLetterList,系统异常！");
		}
		return result;
	}
	
	
}
