package com.power.platform.zdw.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ReadColorTest {

	/**
	 * 读取一张图片的RGB值
	 * 
	 * @throws Exception
	 */
	public static Integer getImagePixel(File file) throws Exception {

		int[] rgb = new int[3];
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		int count = 0;
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				if (rgb[0] == 255 && rgb[1] == 255 && rgb[2] == 255) {
					count++;
				}
			}
		}

		return count;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// System.out.println(rc.getImagePixel("D:/image/1513230611782.jpg"));
	}

}