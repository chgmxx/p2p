package com.power.platform.demo.service;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;



@Path("/demo")
@Service("demoRestService")
@Produces(MediaType.APPLICATION_JSON)
public class DemoRestService {
	
	@POST
	@Path("/findUser")
	public  Map<String, Object> findUserInfo(@FormParam("name")String name){
		System.out.println(name);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", "0");
		result.put("msg", "123");
		return result;
	}
}
