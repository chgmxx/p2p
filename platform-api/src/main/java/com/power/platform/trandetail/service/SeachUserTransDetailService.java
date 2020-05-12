package com.power.platform.trandetail.service;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.common.exception.WinException;
import com.power.platform.pay.config.PartnerConfig;
import com.power.platform.pay.conn.HttpRequestSimple;
import com.power.platform.pay.utils.ClientHttp;
import com.power.platform.pay.utils.ClientParams;
import com.power.platform.pay.utils.ClientResult;
import com.power.platform.sys.utils.HttpUtil;

@Service("seachUserTransDetailService")
@Transactional(readOnly = false)
public class SeachUserTransDetailService {

	
	//商户号
    private static final String merchantId ="M20000005552";
	//存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW/obfwr0k0VlXNLg/JQrFwWGYetJXyy9n4iOt5ysW21xljaxvnRPLkuR/Wbx3Yvs1T7m0zOhWi+gsg/86R7HVjZMFnosiBj3xgSLftCJPiImTiY04AroJb35guqbokFudSBjObcWru5vjwzdPng4FMKSv5hH7LCZlrbxpOxf9TQIDAQAB";
	 
	//商户自己的RSA私钥
    private static final String merchantRsaPrivateKey ="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJcQeyf2K3Uz1r9ob4jj9nZ1OqLAfMa4gI6oGxbJw67TIwk1vWpcuN8JhVA7JKwpMP6NmzZWehJNnZl9UguV47X/Oym61anVGgp7po8rG4GLN2Lp0ls4+D/J/VhlisSODy0DughnC9GDPXiEJqofUdqQ1I66WCRuEemigzLzhhk3AgMBAAECgYBfmwGmr+ifG3jM2QbFxyijndvHRzVw+zH5lzDVwkoDKgMhgA5p81bZaYgi19uEzekBIZPa1u4ZCWA11ReI14suFi0+KZp+6xsmrxKM9/bumfL8SEZB8VPqyfZEp2T9wor/GeG84C0QBwVJw+9ohQuWDEvXJzdt7d/tpapAFcMQEQJBAOobFW/xCvLWhkzybxA0nUuutIJ4MukGWbyL5DMCVlVoGulW1JqknFejADu0QLWBprO/da9/nW/d4dSPdh7EmA8CQQClMTxQOL/O6e3+1mrVaTGBSazScnF9v7gCac2IzXCWjNqV+O0wsaRUSbQAuE1A4RnpIYtOVxqPSqROgVCPYwRZAkBtIqMjxGMuQgPp6zsLevu5RICyMgbJy0QaObzwaq6EsjuZe/kw/nxD/qElNCrWctKcCS172yox9GZLCXYvccbRAkBqvBJVXyWH4xx0wUQMGkjzWZBB9dIgxwR1Arnbv6oUjHQb3Ngc01rzXx/gKzU3S3q7aIIjHyK5HShm1SUhUIAZAkBrH4D29Sale/Ux3DewosfLEJRiITToDVhNw3x4iRY8qoAjpxb99uZ2TpYcpn4nit9wjk+sPWEXhaEXJwq2zhYr​";
	
	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> seachTransDetail(String orderId) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
        params.put("service", "p2p.trade.order.search");
        params.put("method", "RSA");
        params.put("merchantId", merchantId);
        params.put("source", "1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime =sdf.format(new Date());
        params.put("requestTime", requestTime);
        params.put("version", "1.0.0");
        params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
        //生成签名
        String sign = APIUtils.createSign(merchantRsaPrivateKey, params,"RSA");
      	params.put("signature", sign);
      	String jsonString =JSON.toJSONString(params);
      	System.out.println("单笔交易[请求参数]"+jsonString);
      	//加密
      	Map<String, String> encryptRet =APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
      	encryptRet.put("merchantId", merchantId);
        //返回订单信息
        System.out.println(encryptRet);
        
        String url = "http://fsgw.hkmdev.firstpay.com/phoenixFS-fsgw/gateway";
        
        //发送请求
        
        Map<String, String> result = new HashMap<String, String>();
        String respo = HttpUtil.sendPost(url, encryptRet);
        System.out.println("返回结果报文"+respo);
        JSONObject jsonObject = JSONObject.parseObject(respo);
        String respTm = (String) jsonObject.get("tm");
        String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm,respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {});
        
		System.out.println("解密结果:"+maps);
		return maps;
	}
	
}
