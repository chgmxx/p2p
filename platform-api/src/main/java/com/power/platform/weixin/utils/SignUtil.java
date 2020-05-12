package com.power.platform.weixin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.SortedMap;

/**
 * 微信验证
 * 
 */

public class SignUtil {
	
	
	/**
	 * 验证签名
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        System.out.println("【content】: " + content);
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("【tmpStr】：" + tmpStr);
        content = null;
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }
	
	/**
	 * @param signature 微信加密签名
	 * @param timestamp tocken
	 * @param timestamp 时间戳
	 * @param nonce 随机数
	 * @return
	 */
	public static boolean validSign(String signature, String tocken, String timestamp, String nonce) {
		String[] paramArr = new String[] { tocken, timestamp, nonce };
		//对token、timestamp、nonce 进行字典排序，并拼接成字符串
		Arrays.sort(paramArr);
		StringBuilder sb = new StringBuilder(paramArr[0]);
		sb.append(paramArr[1]).append(paramArr[2]);
		String ciphertext = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(sb.toString().getBytes());// 对接后的字符串进行sha1加密
			ciphertext = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 将sha1加密后的字符串与  signature 进行比较
		return ciphertext != null ? ciphertext.equals(signature.toUpperCase()) : false;
	}

	private static String byteToStr(byte[] byteArray) {
		String rst = "";
		for (int i = 0; i < byteArray.length; i++) {
			rst += byteToHex(byteArray[i]);
		}
		return rst;
	}
	
	private static String byteToHex(byte b) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(b >>> 4) & 0X0F];
		tempArr[1] = Digit[b & 0X0F];
		String s = new String(tempArr);
		return s;
	}
	
    public static String signature(SortedMap<String,String> items){
    	StringBuilder forSign= new StringBuilder();
    	for(String key:items.keySet()){
    		forSign.append(key).append("=").append(items.get(key)).append("&");
    	}
    	forSign.setLength(forSign.length()-1);
    	String result = encryptSHA1(forSign.toString());
    	return result;
    }
    
    public static String encryptSHA1(String content){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(content.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
	
}

