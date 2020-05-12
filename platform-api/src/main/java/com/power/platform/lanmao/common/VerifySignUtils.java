package com.power.platform.lanmao.common;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.config.Global;

/**
 * 
 * class: VerifySignUtils <br>
 * description: 验证签名 <br>
 * author: Roy <br>
 * date: 2019年9月24日 上午9:14:10
 */
public class VerifySignUtils {

	private final static Logger logger = LoggerFactory.getLogger(VerifySignUtils.class);

	/**
	 * 懒猫公钥
	 */
	private static final String LM_PUBLIC_KEY = Global.getConfigLanMao("lmPublicKey");

	/**
	 * 
	 * methods: verifySign <br>
	 * description: 验证签名 <br>
	 * author: Roy <br>
	 * date: 2019年9月24日 上午9:13:59
	 * 
	 * @param response
	 * @param responseData
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySign(CloseableHttpResponse response, String responseData) throws Exception {

		Map<String, Object> respMap = JSON.parseObject(responseData);
		// 接口返回code!=0 || status!=SUCCESS时，不做验签处理
		if (!"0".equals(respMap.get("code")) || !"SUCCESS".equals(respMap.get("status"))) {
			logger.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
			return false;
		}

		// 校验返回报文
		String returnSign = "";
		Header[] headers = response.getHeaders("sign");
		if (headers != null && headers.length > 0) {
			Header header = headers[0];
			returnSign = header.getValue();
		}

		byte[] keyByte = Base64.decodeBase64(LM_PUBLIC_KEY);
		byte[] signByte = Base64.decodeBase64(returnSign);
		try {
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(keyByte);

			boolean b = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, responseData, signByte);
			if (!b) {
				logger.info("验签失败--returnSign="+returnSign);
				logger.info("验签失败--responseData="+responseData);
				// throw new Exception("验签失败，sign与respData不匹配");
				return false;
			}

			logger.info("sign success ...");
			return true;
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeySpecException("验签错误，生成商户公钥失败", e);
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException("验签错误" + e.getMessage(), e);
		} catch (GeneralSecurityException e) {
			throw new GeneralSecurityException("验签错误" + e.getMessage(), e);
		}
	}
}
