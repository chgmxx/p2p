package com.power.platform.common.utils;

import java.util.Calendar;

import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * 类: WebSiteFileMethods <br>
 * 描述: 赢多多理财平台文件处理. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月22日 下午2:35:02
 */
public class WebSiteFileMethods {

	/**
	 * 
	 * 方法: createFilePath <br>
	 * 描述: 创建文件路径. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2015年12月22日 下午2:34:47
	 * 
	 * @param userId
	 * @param file
	 * @return
	 */
	public static String createFilePath(String userId, MultipartFile file) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		StringBuffer filePath = new StringBuffer();
		filePath.append(userId);
		filePath.append("/");
		filePath.append(calendar.get(Calendar.YEAR));
		filePath.append("/");
		filePath.append(calendar.get(Calendar.MONTH));
		filePath.append("/");
		filePath.append(calendar.get(Calendar.DAY_OF_MONTH));
		filePath.append("/");
		filePath.append(calendar.get(Calendar.MILLISECOND) + file.getOriginalFilename());
		return filePath.toString();
	}

	/**
	 * 
	 * 方法: createPdfName <br>
	 * 描述: 创建PDF名称. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月7日 下午3:10:09
	 * 
	 * @param mobilePhone
	 * @return
	 */
	public static String createPdfName(String mobilePhone) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		StringBuffer filePath = new StringBuffer();
		filePath.append(calendar.get(Calendar.YEAR));
		filePath.append(calendar.get(Calendar.MONTH));
		filePath.append(calendar.get(Calendar.DAY_OF_MONTH));
		filePath.append(calendar.get(Calendar.MILLISECOND));
		filePath.append("-");
		filePath.append(mobilePhone);
		return filePath.toString();
	}

}
