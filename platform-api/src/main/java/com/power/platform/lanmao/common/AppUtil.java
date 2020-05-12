package com.power.platform.lanmao.common;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.bank.BankEnum;
import com.power.platform.common.utils.bank.BankUtils;
import com.power.platform.lanmao.type.BankCodeEnum;

/**
 * 
 * @author Iren08
 * @date 2017年3月8日 下午12:51:48
 * @version 1.0
 */
public class AppUtil {

	private final static Logger logger = LoggerFactory.getLogger(AppUtil.class);

	/**
	 * 平台编号
	 */
	private static final String PLATFORM_NO = Global.getConfigLanMao("platformNo");
	/**
	 * 平台私钥
	 */
	private static final String PRIVATE_KEY = Global.getConfigLanMao("privateKey");
	/**
	 * 证书序号，用于多证书密钥切换，默认值为1
	 */
	private static final String KEY_SERIAL = Global.getConfigLanMao("keySerial");

	/**
	 * 
	 * methods: lmGeneratePostParam <br>
	 * description: 签名 <br>
	 * author: Roy <br>
	 * date: 2019年9月23日 下午10:07:56
	 * 
	 * @param serviceName
	 * @param reqDataMap
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static Map<String, String> lmGeneratePostParam(String serviceName, Map<String, Object> reqDataMap) throws GeneralSecurityException {

		Map<String, String> result = new HashMap<String, String>();

		logger.debug("PLATFORM_NO:{},KEY_SERIAL:{},PRIVATE_KEY:{}", PLATFORM_NO, KEY_SERIAL, PRIVATE_KEY);

		String reqData = JSONObject.toJSON(reqDataMap).toString();
		logger.debug("reqData:{}", reqData);

		PrivateKey privateKey = SignatureUtils.getRsaPkcs8PrivateKey(Base64.decodeBase64(PRIVATE_KEY));
		byte[] sign = SignatureUtils.sign(SignatureAlgorithm.SHA1WithRSA, privateKey, reqData);

		// 拼装参数
		result.put("serviceName", serviceName);
		result.put("platformNo", PLATFORM_NO);
		result.put("reqData", reqData);
		result.put("keySerial", KEY_SERIAL);
		result.put("sign", Base64.encodeBase64String(sign));

		return result;
	}

	/**
	 * 
	 * methods: generatePostParam <br>
	 * description: 生成参数签名加密 <br>
	 * author: Roy <br>
	 * date: 2019年9月20日 上午9:44:10
	 * 
	 * @param serviceName
	 * @param reqDataMap
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static Map<String, String> generatePostParam(String serviceName, Map<String, String> reqDataMap) throws GeneralSecurityException {

		Map<String, String> result = new HashMap<String, String>();

		logger.debug("PRIVATE_KEY:{}", PRIVATE_KEY);

		String reqData = JSON.toJSONString(reqDataMap);
		logger.debug("请求参数reqData:{}", reqData);

		PrivateKey privateKey = SignatureUtils.getRsaPkcs8PrivateKey(Base64.decodeBase64(PRIVATE_KEY));
		byte[] sign = SignatureUtils.sign(SignatureAlgorithm.SHA1WithRSA, privateKey, reqData);
		logger.debug("PLATFORM_NO:{},KEY_SERIAL:{}", PLATFORM_NO, KEY_SERIAL);
		// 拼装网关参数
		result.put("serviceName", serviceName);
		result.put("platformNo", PLATFORM_NO);
		result.put("reqData", reqData.replace("\\", ""));
		result.put("keySerial", KEY_SERIAL);
		result.put("sign", Base64.encodeBase64String(sign));

		return result;
	}

	public static String CheckStringByDefault(String param, String defValue) {

		if (StringUtils.isBlank(param)) {
			return defValue;
		} else {
			return param;
		}
	}
	
	/**
	 * 参数签名
	 * @param signMap
	 * @return
	 */
	public static String signParam(Map<String, Object> signMap) {
		String _sign = "";
		String signMapTOString = "";
		try{
			// 开始生成签名
			signMapTOString = JSON.toJSONString(signMap);
			logger.debug("请求参数reqData:" + signMapTOString);
			PrivateKey privateKey = SignatureUtils.getRsaPkcs8PrivateKey(Base64.decodeBase64(Global.getConfigLanMao("privateKey")));
			byte[] sign = SignatureUtils.sign(SignatureAlgorithm.SHA1WithRSA, privateKey, signMapTOString);
			 _sign =  Base64.encodeBase64String(sign);
			 logger.debug("参数签名为:" + _sign);
		}catch(Exception e) {
			logger.error("计算签名有错误: {}", e.getMessage());
		}
		return _sign;
	}
	
	/**
	 * 参数签名
	 * @param signMap
	 * @return
	 */
	public static String signParamStr(String signMap) {
		String _sign = "";
		String signMapTOString = "";
		try{
			// 开始生成签名
			signMapTOString = signMap;
			logger.debug("请求参数reqData:" + signMapTOString);
			PrivateKey privateKey = SignatureUtils.getRsaPkcs8PrivateKey(Base64.decodeBase64(Global.getConfigLanMao("privateKey")));
			byte[] sign = SignatureUtils.sign(SignatureAlgorithm.SHA1WithRSA, privateKey, signMapTOString);
			 _sign =  Base64.encodeBase64String(sign);
			 logger.debug("参数签名为:" + _sign);
		}catch(Exception e) {
			logger.error("计算签名有错误: {}", e.getMessage());
		}
		return _sign;
	}
	
	/**
	 * lanmao 通知消息验签
	 * @param sign
	 * @param respData
	 * @return
	 */
	public static boolean checkLanMaoNotifySign (String sign, String respData) {
		// 验签
		PublicKey publicKey;
		boolean verify = false;
		try {
			publicKey = SignatureUtils.getRsaX509PublicKey(Base64
					.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			verify = SignatureUtils.verify(
					SignatureAlgorithm.SHA1WithRSA, publicKey, respData,
					Base64.decodeBase64(sign));
			return verify;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return verify;
	}
	
	/**
	 * 
	 * @param bankCardNo
	 * @return
	 */
	public static Map<String, String> getBankCodeByBankNo(String bankCardNo) {
		String jsonStr = BankUtils.getCardDetail(bankCardNo);
		JSONObject json = JSONObject.parseObject(jsonStr);
		String validated = json.getString("validated");
		Map<String,String> banCode = new HashMap<String,String> ();
		if("true".equals(validated)) {
			String baseBankCode = json.get("bank").toString();
			String baseBankName = BankEnum.getTextByValue(baseBankCode);	
			String lanmaoBankCode = BankCodeEnum.getTextByText(baseBankName);
			banCode.put("BankName", baseBankName);
			banCode.put("baseBankCode", baseBankCode);
			banCode.put("lanmaoBankCode", lanmaoBankCode);
		}
		return banCode;

	}
	

}
