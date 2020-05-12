package com.power.platform.common.utils;

public class Test {

	public static void main(String[] args) {

		String[][] str = new String[2][2];
		str[0][0] = "1";
		str[0][1] = "2";
		str[1][0] = "1";
		str[1][1] = "2";
		for (int i = 0; i < str.length; i++) {
			for (int j = 0; j < str.length; j++) {
				System.err.println(str[i][j]);
			}
		}
		String str1 = "http://192.168.1.52:80/";
		String str2 = "http://192.168.1.52:80/2017/5/23/IMG_65f69afe48c04ef4b8ca245b6b1e1028.png";
		System.out.println(str2.substring(str1.length()));

	}

}
