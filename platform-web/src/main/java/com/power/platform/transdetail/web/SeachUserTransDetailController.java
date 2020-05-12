/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.transdetail.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.common.config.Global;
import com.power.platform.common.web.BaseController;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.service.UserCheckAccountService;
import com.power.platform.userinfo.service.UserCheckOrderService;

/**
 * 客户流水记录Controller
 * @author soler
 * @version 2015-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/transdetail/seachUserTransDetail")
public class SeachUserTransDetailController extends BaseController {

	
	/**
	 * 商户自己的RSA公钥.
	 */
	private static final String MERCHANT_RSA_PUBLIC_KEY = Global.getConfigUb("UB_MERCHANT_RSA_PUBLIC_KEY");
	/**
	 * 商户自己的RSA私钥.
	 */
	private static final String MERCHANT_RSA_PRIVATE_KEY = Global.getConfigUb("UB_MERCHANT_RSA_PRIVATE_KEY");
	/**
	 * 测试环境网关地址.
	 */
	private static final String HOST = Global.getConfigUb("UB_HOST");
	/**
	 * 商户号.
	 */
	private static final String MERCHANT_ID = Global.getConfigUb("UB_MERCHANT_ID");
	
	@Autowired
	private UserTransDetailService userTransDetailService;
	@Autowired
	private UserCheckAccountService userCheckAccountService;
	@Autowired
	private UserCheckOrderService userCheckOrderService;
	
	@ModelAttribute
	public UserTransDetail get(@RequestParam(required=false) String id) {
		UserTransDetail entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userTransDetailService.get(id);
		}
		if (entity == null){
			entity = new UserTransDetail();
		}
		return entity;
	}
	
	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(UserTransDetail userTransDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		//Page<UserTransDetail> page = userTransDetailService.findPage(new Page<UserTransDetail>(request, response), userTransDetail); 
		//model.addAttribute("page", page);
		return "modules/transdetail/seachTransDetail";
	}

	/**
	 * 查询订单
	 * @param userTransDetail
	 * @param model
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	@RequiresPermissions("transdetail:userTransDetail:view")
	@RequestMapping(value = "seach")
	public String form(UserTransDetail userTransDetail, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException, Exception {
		String orderId = userTransDetail.getTransId();
		System.out.println(orderId);
		
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
        params.put("service", "p2p.trade.order.search");
        params.put("method", "RSA");
        params.put("merchantId", MERCHANT_ID);
        params.put("source", "1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime =sdf.format(new Date());
        params.put("requestTime", requestTime);
        params.put("version", "1.0.0");
        params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
        //生成签名
        String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params,"RSA");
      	params.put("signature", sign);
      	String jsonString =JSON.toJSONString(params);
      	System.out.println("单笔交易[请求参数]"+jsonString);
      	//加密
      	Map<String, String> encryptRet =APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
      	encryptRet.put("merchantId", MERCHANT_ID);
        //返回订单信息
        System.out.println(encryptRet);
        
        //发送请求
        
        String respo = HttpUtil.sendPost(HOST, encryptRet);
        System.out.println("返回结果报文"+respo);
        JSONObject jsonObject = JSONObject.parseObject(respo);
        String respTm = (String) jsonObject.get("tm");
        String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm,respData, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {});
        
		System.out.println("解密结果:"+maps);
		UserTransDetail transDetail = new UserTransDetail();
		if(maps.get("respSubCode").equals("000000")){
			String transId = maps.get("orderId");
			String amount =  maps.get("amount");
			String bizType = maps.get("bizType");
			String status = maps.get("status");
			String transTypeName = bizType.equals("1000")?"充值":(bizType.equals("2000")?"提现":(bizType.equals("3100")?"投资":(bizType.equals("5000")?"放款":(bizType.equals("7000")?"还款":(bizType.equals("8000")?"返利":"其他")))));
			transDetail.setTransId(transId);
			transDetail.setAmount(Double.valueOf(amount)/100);
			transDetail.setTrustTypeStr(transTypeName);
			transDetail.setStateStr(status.equals("I")?"处理中":(status.equals("S")?"成功":"失败"));
			model.addAttribute("userTransDetail", transDetail);
			model.addAttribute("userTransDetail", transDetail);
			return "modules/transdetail/resultTransDetail";
		}else{
			addMessage(redirectAttributes, "订单不存在");
			return "redirect:" + Global.getAdminPath() + "/transdetail/seachUserTransDetail/list?repage";
		}
	}


}