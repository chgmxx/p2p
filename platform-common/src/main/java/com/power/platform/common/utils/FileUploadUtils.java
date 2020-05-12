package com.power.platform.common.utils;

import java.io.*;
import java.util.*;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * 类: FileUploadUtils <br>
 * 描述: 文件上传工具类. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月24日 下午1:42:50
 */
public class FileUploadUtils {

	/**
	 * 
	 * 方法: uploadStream <br>
	 * 描述: 上传IO流. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月27日 上午9:20:28
	 * 
	 * @param fileName
	 *            文件名 <br/>
	 * @param filePath
	 *            文件储存路径 <br/>
	 * @param inStream
	 *            文件输入流 <br/>
	 * @return
	 */
	public static boolean uploadStream(String fileName, String filePath, InputStream inStream) {

		boolean result = false;

		// 新文件路径.
		StringBuffer path = new StringBuffer();

		// Java Calendar 类的时间操作.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		if ((filePath == null) || (filePath.trim().length() == 0)) {
			return result;
		}
		OutputStream outStream = null;
		try {

			// 基目录.
			path.append(filePath);
			File dir = new File(path.toString());
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// 年(目录).
			int year = calendar.get(Calendar.YEAR);
			path.append(File.separator);
			path.append(year);
			File yearDir = new File(path.toString());
			if (!yearDir.exists()) {
				yearDir.mkdirs();
			}

			// 月(目录).
			int month = calendar.get(Calendar.MONTH);
			month = month + 1;
			path.append(File.separator);
			path.append(month);
			File monthDir = new File(path.toString());
			if (!monthDir.exists()) {
				monthDir.mkdirs();
			}

			// 日(目录).
			int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
			path.append(File.separator);
			path.append(day_of_month);
			File dayDir = new File(path.toString());
			if (!dayDir.exists()) {
				dayDir.mkdirs();
			}

			// 带文件的全路径.
			path.append(File.separator);
			path.append(fileName);

			File outputFile = new File(path.toString());
			boolean isFileExist = outputFile.exists();
			boolean canUpload = true;
			if (isFileExist) {
				canUpload = outputFile.delete();
			}
			if (canUpload) {
				int available = 0;
				outStream = new BufferedOutputStream(new FileOutputStream(outputFile), 2048);
				byte[] buffer = new byte[2048];
				while ((available = inStream.read(buffer)) > 0) {
					if (available < 2048) {
						outStream.write(buffer, 0, available);
					} else {
						outStream.write(buffer, 0, 2048);
					}
				}
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * 方法: createFilePath <br>
	 * 描述: 创建文件目录及文件名. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月24日 下午3:22:13
	 * 
	 * @param fileFormat
	 * @return
	 */
	public static String createFilePath(String fileFormat) {

		// Java Calendar 类的时间操作.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		// StringBuffer是线程安全的，效率低于StringBuilder.
		StringBuffer filePath = new StringBuffer();

		// 年(目录).
		int year = calendar.get(Calendar.YEAR);
		filePath.append(year);
		filePath.append(File.separator);

		// 月(目录).
		int month = calendar.get(Calendar.MONTH);
		month = month + 1;
		filePath.append(month);
		filePath.append(File.separator);

		// 日(目录).
		int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
		filePath.append(day_of_month);
		filePath.append(File.separator);

		// 文件名(IMG_20170324_151727).
		filePath.append("IMG_");
		// 年月日_时分秒(20170324_154233)，这种图片的命名规则只适合单张并不适用与多张图片的命名.
		// String string = (new
		// SimpleDateFormat("yyyyMMdd_HHmmssSSS")).format(calendar.getTime());
		// 使用UUID为图片命名.
		String uuid = IdGen.uuid();
		filePath.append(uuid);
		// ..
		filePath.append(".");
		// 文件格式.
		filePath.append(fileFormat);

		return filePath.toString();
	}

	/**
	 * 
	 * 方法: GetImageStr <br>
	 * 描述: 将图片文件转化为字节数组字符串，并对其进行Base64编码处理. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月22日 下午4:38:26
	 * 
	 * @return
	 */
	public static String GetImageStr() {

		String imageFile = "D://test.jpg"; // 待处理的图片.
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imageFile);
			System.err.println("图片大小：" + in.available());
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码.
		BASE64Encoder encoder = new BASE64Encoder();
		// 返回Base64编码过的字节数组字符串.
		return encoder.encode(data);
	}

	/**
	 * 
	 * 方法: GenerateImage <br>
	 * 描述: 对字节数组字符串进行Base64解码并生成图片. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月22日 下午4:53:24
	 * 
	 * @param imgStr
	 * @return
	 */
	public static boolean GenerateImage(String imgStr) { // 对字节数组字符串进行Base64解码并生成图片

		// 图像数据为空.
		if (imgStr == null)
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码.
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) { // 调整异常数据.
					b[i] += 256;
				}
			}
			String imgFilePath = "D://new.jpg"; // 新生成的图片.
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String[] args) {

		// String imageStr = GetImageStr();
		// System.err.println("Base64编码后的字节数组字符串大小：" + imageStr.length());
		// System.out.println("Base64编码后的字节数组字符串：" + imageStr);
		// boolean flag = GenerateImage(imageStr);
		// System.out.println("Base64编码后的字节数组字符串重新转换成图片，是否成功：" + flag);

	}
}
