package com.power.platform.activity.service;
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

import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.service.WloanTermInvestService;

@Component
@Path("/volunteer")
@Service("volunteerOfferService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class VolunteerOfferService {
	private static final Logger LOG = LoggerFactory.getLogger(VolunteerOfferService.class);
	@Autowired 
	
	private WloanTermInvestService  wloanTermInvestService;
	
	
	/**
	 * 
	 * 方法: volunteerOfferActive<br>
	 * 描述: 公益捐助 <br>
	 * 作者: Mr.彦.赵 <br>
	 * 时间: 2017年4月28日 上午10:43:16
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getVolunteerOfferList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String,Object> volunteerOfferActive(@FormParam("from")String from){
		// 客户端响应结果集
		Map<String, Object> result = new HashMap<String, Object>();
		//数据域
		Map<String, Object> data = new HashMap<String, Object>();
		
		// 判断必要参数是否为空
		if(StringUtils.isBlank(from)){
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		List<Map<String, Object>> list;
		try {
			List<WloanTermInvest> volunteer = wloanTermInvestService.volunterrList();
			list = new ArrayList<Map<String, Object>>();
			int count=500;
			
			if (null != volunteer && volunteer.size() > 0) {
				
				for (int i = 0; i < 15; i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					WloanTermInvest wloanTermInvest = volunteer.get(i);
					map.put("name", Util.hideString(wloanTermInvest.getUserInfo().getName(), 3, 4)); //用户名
					list.add(map);
				}
			}
			count=count+volunteer.size();
			data.put("list", list);
			data.put("totalAmount", count); // 总金额
			result.put("state","0");
			result.put("message", "信息展示成功");
			result.put("data", data);
			
		} catch (Exception e) {
			result.put("message", "系统异常");
			result.put("state", "2");
			result.put("data", null);
			return result;
		}
		return result;
	}
	public static Logger getLog() {
		return LOG;
	}
}
