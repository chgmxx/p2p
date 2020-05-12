package com.power.platform.activity.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.RedPacket;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;



@Service("redPacketService")
public class RedPacketService {

	private static final Logger LOG = LoggerFactory.getLogger(RedPacketService.class);
	
	//商户号
    private static final String merchantId = Global.getConfigUb("UB_MERCHANT_ID");
	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfigUb("UB_MERCHANT_RSA_PUBLIC_KEY");
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKey = Global.getConfigUb("UB_MERCHANT_RSA_PRIVATE_KEY");
    
    private static final String PAY_USER = Global.getConfigUb("PAY_USER");
    
	/**
	 * 状态，1：未使用，可以变更及删除.
	 */
	public static final String A_VOUCHERS_DIC_STATE_1 = "1";

	/**
	 * 状态，2：使用中，不可变更及删除.
	 */
	public static final String A_VOUCHERS_DIC_STATE_2 = "2";
	
	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	
	/**
	 * 返利调用API----erp用
	 * @param user
	 * @param bizType
	 * @param amount
	 * @return
	 */
	public Map<String, String> giveRedPacket(String phone, String bizType, Double amount, String orderId) throws WinException, Exception{
		
		/*
		 * 根据手机号查询用户
		 */
		UserInfo user = userInfoDao.getUserInfoByPhone(phone);
		
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String subOrderId = UUID.randomUUID().toString().replace("-", "");
        Double a = 100*NumberUtils.scaleDouble(amount);
        BigDecimal redAmount = new BigDecimal(a);
		map.put("amount", redAmount.toString());
		map.put("bizType", bizType);
		map.put("payUserId", PAY_USER);
		map.put("receiveUserId", user.getId());
		map.put("subOrderId", subOrderId);
		rpOrderList.add(map);
		
		

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
        params.put("subOrderList",  JSONArray.toJSONString(rpOrderList));
        params.put("currency", "CNY");
        params.put("service", "p2p.trade.batchtransfer.create");
        params.put("method", "RSA");
        params.put("merchantId", merchantId);
        params.put("source", "1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime =sdf.format(new Date());
        params.put("requestTime", requestTime);
        params.put("version", "1.0.0");
        params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
        params.put("callbackUrl", ServerURLConfig.BACK_REDPACKET_URL);
        //生成签名
        String sign = APIUtils.createSign(merchantRsaPrivateKey, params,"RSA");
      	params.put("signature", sign);
      	String jsonString =JSON.toJSONString(params);
      	System.out.println("返利[请求参数]"+jsonString);
      	//加密
      	Map<String, String> encryptRet =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
      	encryptRet.put("merchantId", merchantId);
        //返回订单信息
        System.out.println(encryptRet);
        
        String url = ServerURLConfig.CGB_URL;
        
        String result = HttpUtil.sendPost(url, encryptRet);
        System.out.println("返回结果报文"+result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String respTm = (String) jsonObject.get("tm");
        String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {});
        
		System.out.println("解密结果:"+maps);
		
		return maps;
	}
	
	/**
	 * 返利调用API----web用
	 * @param user
	 * @param bizType
	 * @param amount
	 * @return
	 */
	//商户号
    private static final String merchantIdWeb = Global.getConfig("merchantId");
	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKeyWeb = Global.getConfig("merchantRsaPublicKey");
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKeyWeb = Global.getConfig("merchantRsaPrivateKey");
    
    private static final String PAY_USERWeb = Global.getConfig("payUserId");
	

	public Map<String, String> giveRedPacketWeb(String phone, String bizType, Double amount, String orderId) throws WinException, Exception{
		
		/*
		 * 根据手机号查询用户
		 */
		UserInfo user = userInfoDao.getUserInfoByPhone(phone);
		
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		//String subOrderId = UUID.randomUUID().toString().replace("-", "");
        Double a = 100*NumberUtils.scaleDouble(amount);
        BigDecimal redAmount = new BigDecimal(a);
		map.put("amount", redAmount.toString());
		map.put("bizType", bizType);
		map.put("payUserId", PAY_USERWeb);
		map.put("receiveUserId", user.getId());
		map.put("subOrderId", orderId);
		rpOrderList.add(map);
		
		

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
        params.put("subOrderList",  JSONArray.toJSONString(rpOrderList));
        params.put("currency", "CNY");
        params.put("service", "p2p.trade.batchtransfer.create");
        params.put("method", "RSA");
        params.put("merchantId", merchantIdWeb);
        params.put("source", "1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime =sdf.format(new Date());
        params.put("requestTime", requestTime);
        params.put("version", "1.0.0");
        params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
        params.put("callbackUrl", ServerURLConfig.BACK_REDPACKET_URL2_2_1);
        //生成签名
        String sign = APIUtils.createSign(merchantRsaPrivateKeyWeb, params,"RSA");
      	params.put("signature", sign);
      	String jsonString =JSON.toJSONString(params);
      	System.out.println("返利[抵用券][请求参数]"+jsonString);
      	//加密
      	Map<String, String> encryptRet =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKeyWeb);
      	encryptRet.put("merchantId", merchantIdWeb);
        //返回订单信息
        System.out.println(encryptRet);
        
        String url = ServerURLConfig.CGB_URL;
        
        String result = HttpUtil.sendPost(url, encryptRet);
        System.out.println("返回结果报文"+result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String respTm = (String) jsonObject.get("tm");
        String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKeyWeb);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {});
        
		System.out.println("解密结果:"+maps);
		
		return maps;
	}
}
