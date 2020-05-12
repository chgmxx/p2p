package com.power.platform.zdw.utils;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	public static final String UTF_8 = "UTF-8";
	public static final String GBK = "GBK";
	public static String defaultImageFormat = "jpg";
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 生成文件
	 *
	 * @param fileContent
	 *            文件的内容
	 * @param filePath
	 *            文件路径；
	 * @param fileName
	 *            文件的文件名；
	 * @param isappendContent
	 *            是否追加
	 */
	public static void writeStrings(String fileContent, String filePath, String fileName, boolean isappendContent) {

		try {
			// 创建文件夹
			File file = new File(filePath);
			if (!file.exists() || !file.isDirectory()) {
				makeDir(file);
			}
			FileUtils.write(new File(file, fileName), fileContent, UTF_8, isappendContent);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 生成文件
	 *
	 * @param fileContent
	 *            文件的内容
	 * @param fileName
	 *            文件的文件名；
	 * @param isWriteNewContent
	 *            覆盖原文件 或追加新内容
	 */
	public static void writeFile(String fileContent, String dir, String fileName, boolean isWriteNewContent, InputStream in) {

		File file = new File(dir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		try {
			if (isWriteNewContent) {

				FileUtils.write(new File(file, fileName), fileContent, UTF_8, false);

			} else {

				FileUtils.copyInputStreamToFile(in, new File(file, fileName));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件
	 *
	 * @param src
	 * @return
	 */
	public static String read(File src) {

		StringBuffer res = new StringBuffer();
		String line = null;
		try {
			FileInputStream fis = new FileInputStream(src);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					String frist = line.substring(0, 1);
					if (!frist.equals("#")) {
						res.append(line + "\n").toString();
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.toString();
	}

	/**
	 * 读取文件
	 *
	 * @param src
	 * @return
	 */
	public static String read(File src, String charsetName) {

		StringBuffer res = new StringBuffer();
		String line = null;
		try {
			FileInputStream fis = new FileInputStream(src);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, charsetName));
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					String frist = line.substring(0, 1);
					if (!frist.equals("#")) {
						res.append(line + "\n").toString();
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.toString();
	}

	/**
	 * 读取文件
	 *
	 * @param src
	 * @return
	 */
	public static List<String> reads(File src) {

		List<String> res = new ArrayList<String>();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(src));
			while ((line = reader.readLine()) != null) {
				if (!line.trim().equals("")) {
					String frist = line.substring(0, 1);
					if (!frist.equals("#")) {
						// res.append(line + "\n").toString();
						res.add(line.trim());
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 重命名文件夹
	 *
	 * @param src
	 * @param dest
	 * @return
	 */
	public static boolean renameToNewFile(String src, String dest) {

		File srcDir = new File(src);
		boolean isOk = false;
		File newFile = new File(dest);
		isOk = srcDir.renameTo(newFile);
		logger.info("rename " + src + " to + " + dest + " is OK ? :" + isOk);
		return isOk;
	}

	/**
	 * @param path
	 * @param oldname
	 * @param newname
	 * @return
	 */
	public static boolean renameToNewFile(String path, String oldname, String newname) {

		File srcDir = new File(path + File.separator + oldname);
		boolean isOk = false;
		// srcDir = srcDir.getParentFile().getParentFile();
		// tmp.renameTo(new File(tmp.getParent(), tmp.getName() + "_fin"));
		// if (srcDir.exists() || srcDir.isDirectory()) {
		isOk = srcDir.renameTo(new File(path + File.separator + newname));
		// }

		logger.info("renameToNewFile is OK ? :" + isOk);
		return isOk;
	}

	/**
	 * 页面截图
	 *
	 * @param fileName
	 *            保存图片名称
	 * @param savePath
	 *            保存路径
	 * @param x
	 *            起始x坐标
	 * @param y
	 *            起始y坐标
	 * @param width
	 *            截图宽度
	 * @param height
	 *            截图高度
	 * @throws Exception
	 * @author wangyepeng
	 */
	public static void snapShot(String fileName, String savePath, int x, int y, int width, int height) throws Exception {

		// 拷贝屏幕到一个BufferedImage对象screenshot
		BufferedImage screenshot = (new Robot()).createScreenCapture(new Rectangle(x, y, width, height));
		// 根据文件前缀变量和文件格式变量，自动生成文件名
		String name = savePath + "\\" + fileName + "." + defaultImageFormat;
		// 输出的文件及路径
		File sf = new File(name);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		System.out.print("Save File " + name);
		// 将screenshot对象写入图像文件
		ImageIO.write(screenshot, defaultImageFormat, sf);
		System.out.print("..Finished!\n");
	}

	/**
	 * 合并图片
	 *
	 * @param file1
	 * @param file2
	 * @param toFile
	 * @throws IOException
	 */
	public static void mergeImage(File file1, File file2, File toFile) {

		try {
			BufferedImage image1 = ImageIO.read(file1);
			BufferedImage image2 = ImageIO.read(file2);
			BufferedImage combined = new BufferedImage(image1.getWidth() + image2.getWidth(), image1.getHeight() > image2.getHeight() ? image1.getHeight() : image2.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics g = combined.getGraphics();
			g.drawImage(image1, 0, 0, null);
			g.drawImage(image2, image1.getWidth(), 0, null);
			ImageIO.write(combined, "jpg", toFile);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static File makeDir(File dir) throws IOException {

		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static File makeDir(String dir) throws IOException {

		return makeDir(new File(dir));
	}

	public static File createFile(File file) throws IOException {

		if (!file.exists() || file.isDirectory()) {
			makeDir(file.getParentFile());
		}
		return file;
	}

	public static File createFile(String file) throws IOException {

		return createFile(new File(file));
	}

	/**
	 * 邮政银行图片降噪
	 *
	 * @param file
	 * @param savePath
	 * @return
	 */
	public static File reduceNoise(File file, String savePath) {

		try {
			/**
			 * 定义一个RGB的数组，因为图片的RGB模式是由三个 0-255来表示的 比如白色就是(255,255,255)
			 */
			int[] rgb = new int[3];
			/**
			 * 用来处理图片的缓冲流
			 */
			BufferedImage bi = null;
			try {
				/**
				 * 用ImageIO将图片读入到缓冲中
				 */
				bi = ImageIO.read(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**
			 * 得到图片的长宽
			 */
			int width = bi.getWidth();
			int height = bi.getHeight();
			int minx = bi.getMinX();
			int miny = bi.getMinY();
			logger.info("正在处理：" + file.getName());
			/**
			 * 这里是遍历图片的像素，因为要处理图片的背色，所以要把指定像素上的颜色换成目标颜色 这里 是一个二层循环，遍历长和宽上的每个像素
			 */
			for (int i = minx; i < width; i++) {
				for (int j = miny; j < height; j++) {
					// System.out.print(bi.getRGB(jw, ih));
					/**
					 * 得到指定像素（i,j)上的RGB值，
					 */
					int pixel = bi.getRGB(i, j);
					/**
					 * 分别进行位操作得到 r g b上的值
					 */
					rgb[0] = (pixel & 0xff0000) >> 16;
					rgb[1] = (pixel & 0xff00) >> 8;
					rgb[2] = (pixel & 0xff);
					/**
					 * 进行换色操作，我这里是要把黑色换成背景色，那么就判断图片中rgb值是否黑色范围的像素
					 */
					if (rgb[0] < 50 && rgb[1] < 50 && rgb[2] < 50) {
						/**
						 * 这里是判断通过，则把该像素换成背景色
						 */
						bi.setRGB(i, j, 0xDAD9FB);
					}

				}
			}
			logger.info("\t处理完毕：" + file.getName());

			/**
			 * 将缓冲对象保存到新文件中
			 */
			FileOutputStream ops = new FileOutputStream(new File(savePath + "/fin_" + file.getName()));
			ImageIO.write(bi, "jpg", ops);
			ops.flush();
			ops.close();
			return new File(savePath + "/fin_" + file.getName());
		} catch (Exception e) {
			return file;
		}

	}

	/**
	 * 格式化
	 *
	 * @param jsonStr
	 * @return
	 * @author wangyepeng
	 */
	public static String formatJson(String jsonStr) {

		if (null == jsonStr || "".equals(jsonStr)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		char current = '\0';
		int indent = 0;
		for (int i = 0; i < jsonStr.length(); i++) {
			last = current;
			current = jsonStr.charAt(i);
			switch (current) {
				case '{':
				case '[':
					sb.append(current);
					sb.append('\n');
					indent++;
					addIndentBlank(sb, indent);
					break;
				case '}':
				case ']':
					sb.append('\n');
					indent--;
					addIndentBlank(sb, indent);
					sb.append(current);
					break;
				case ',':
					sb.append(current);
					if (last != '\\') {
						sb.append('\n');
						addIndentBlank(sb, indent);
					}
					break;
				default:
					sb.append(current);
			}
		}

		return sb.toString();
	}

	/**
	 * 添加space
	 *
	 * @param sb
	 * @param indent
	 * @author wangyepeng
	 */
	private static void addIndentBlank(StringBuilder sb, int indent) {

		for (int i = 0; i < indent; i++) {
			sb.append('\t');
		}
	}

}
