package com.power.platform.lanmao.rw.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.lanmao.rw.utils.ResultVO;
import com.power.platform.lanmao.rw.utils.ResultVOUtil;
import com.power.platform.lanmao.type.RechargeWayEnum;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.HttpUtils;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.RechargeVo;

@Path("/tore")
@Service("testRechargeService") 
@Produces(MediaType.APPLICATION_JSON)
public class TestRechargeService {


	private static final Logger logger = LoggerFactory.getLogger(TestRechargeService.class);
	
	@GET
	@Path("/recharge")
	public ResultVO<RechargeVo> testRecharge() { 
		RechargeVo rv = new RechargeVo();
		rv.setRequestNo(IdGen.uuid());
		rv.setAmount(0.01);
		rv.setPlatformUserNo("3415696228584974258");
		rv.setRechargeWay(RechargeWayEnum.WEB.getValue());
		rv.setRedirectUrl("https://www.ant-loiter.com"); 
		
		RechargeService rs = new RechargeService();
		return null; 
		
	}
	
	public void randInt() {
		
	}
	
	@GET
	@Path("/bigrecharge")
	public void test(String name, String bankcode) throws Exception {
		String requestNo = IdGen.uuid();
		CloseableHttpResponse response = null;String lmresult = "";
		// 定义reqData参数集合
		Map<String, Object> reqDataMap = new HashMap<String, Object>();
//		reqDataMap.put("requestNo", requestNo); // Y
		reqDataMap.put("accountNo", "8981999900000000013"); // Y专户号：8981999900000000013
		reqDataMap.put("tranDate", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		reqDataMap.put("tranTime", DateUtils.formatDate(new Date(), "yyyyMMdd HH:mm:ss"));
		double doub = (double)(1+Math.random()*1000);
		logger.info("用户 {}， 充值： {},元", name, doub);
		reqDataMap.put("tranAmt", doub); // N
		reqDataMap.put("tfrAcctName", name);
		reqDataMap.put("tfrAcctNo", bankcode);
		// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
		reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

		Map<String, String> requestParam = AppUtil.lmGeneratePostParam("INSERT_ACCOUNTING_HISTORY", reqDataMap);
		logger.debug("request json:{}", JSON.toJSONString(requestParam));

		List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
		BasicNameValuePair n1 = new BasicNameValuePair("serviceName", "INSERT_ACCOUNTING_HISTORY");
		BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestParam.get("platformNo"));
		BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestParam.get("reqData"));
		BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestParam.get("keySerial"));
		BasicNameValuePair n5 = new BasicNameValuePair("sign", requestParam.get("sign"));
		formParams.add(n1);
		formParams.add(n2);
		formParams.add(n3);
		formParams.add(n4);
		formParams.add(n5);
//		response = HttpUtils.httpPostWithPAaram("https://xwbk.lanmaoly.com/bha-neo-app/lanmaotech/tranHisDetail/insert", formParams);
//		lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		Map<String, String> header = new HashMap<>();
		String responseStr = HttpUtil.sendPost("https://hk.lanmaoly.com/bha-neo-app/lanmaotech/tranHisDetail/insert", requestParam, header, "utf-8");
		logger.debug("lmresult:{}", responseStr);
	}
}
