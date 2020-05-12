package com.power.platform.pay.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Service;
import com.power.platform.common.utils.StringUtils;

@Path("/backto")
@Service("backUrlService")
@Produces(MediaType.APPLICATION_JSON)
public class BackUrlService {
		
	@POST
	@Path("/backto")
	@Produces(MediaType.APPLICATION_JSON)
	public  Map<String, Object> backto(@FormParam("from")String from){
		Map<String, Object> result = new HashMap<String, Object>();
		//判断必要参数是否为空
		if(StringUtils.isBlank(from)){
			result.put("state", "1");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
				Map<String, Object> data = new HashMap<String, Object>();
				List<String> featureList = new ArrayList<String>();
				data.put("appversion", "1.0.0");
				data.put("featureList", featureList);
				result.put("state", "0");
				result.put("data", data);
				result.put("message", "获取app版本信息成功");
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
		}
		return result;
	}
}
