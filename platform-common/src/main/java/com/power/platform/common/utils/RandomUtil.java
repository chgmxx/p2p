package com.power.platform.common.utils;

import java.util.Random;

/**
 * 随机数工具类
 * 
 * @author 潘明洋
 *
 */
public class RandomUtil {

	private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";// 随机产生的字符串

	private Random random = new Random();

	// 随机生成6位数验证码
	public static Integer random6Num() {
		return (int) (Math.random() * 900000 + 100000);
	}

	// 随机生成8位密码
	public String randomPWD() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			String rand = String.valueOf(getRandomString(random
					.nextInt(randString.length())));
			sb.append(rand);
		}
		return sb.toString();
	}

	public String getRandomString(int num) {
		return String.valueOf(randString.charAt(num));
	}

	public static String generateRandomDigitalString(int count) {

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < count; i++) {
			buffer.append((int) (Math.random() * 10));
		}

		return buffer.toString();
	}
}
