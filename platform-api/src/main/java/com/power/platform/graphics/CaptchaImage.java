package com.power.platform.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * 验证码
 * @author xuning
 * @date 2015年2月5日 下午3:04:28
 */
public class CaptchaImage {

	public static final String CAPTCHA_CODE_KEY = "CAPTCHA_CODE_KEY";// 放到session中的key
	private Random random = new Random();
	private String randString = "123456789";// 随机产生的字符串

	private int width = 80;// 图片宽
	private int height = 26;// 图片高
	private int lineSize = 40;// 干扰线数量
	private int stringNum = 4;// 随机产生字符数量
	private int fontSize = 18;//字体大小
	private int xPosition = 13;//呈现String位置的x坐标
	private int yPosition = 16;//呈现String位置的y坐标

	public CaptchaImage() {

	}

	public CaptchaImage(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public CaptchaImage(int width, int height, int fontSize, int charCount) {
		this.width = width;
		this.height = height;
		this.lineSize = fontSize;
		this.stringNum = charCount;
	}
	
	/**
	 * @param width  宽
	 * @param height 高
	 * @param lineSize  干扰线条数
	 * @param charCount 字符数量
	 * @param fontSize 字体大小
	 * @param xPositioin 字符x坐标偏移
	 * @param yPosition 字符y坐标偏移
	 */
	public CaptchaImage(int width, int height, int lineSize, int charCount, int fontSize, int xPositioin, int yPosition) {
		this.width = width;
		this.height = height;
		this.lineSize = lineSize;
		this.stringNum = charCount;
		this.fontSize = fontSize;
		this.xPosition = xPositioin;
		this.yPosition = yPosition;
	}
	

	/**
	 * 生成随机图片
	 */
	public void buildImage(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		// BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
		g.setColor(getRandColor(110, 133));
		// 绘制干扰线
		for (int i = 0; i <= lineSize; i++) {
			drowLine(g);
		}
		// 绘制随机字符
		String randomString = "";
		for (int i = 1; i <= stringNum; i++) {
			randomString = drowString(g, randomString, i);
		}
		session.removeAttribute(CAPTCHA_CODE_KEY);
		session.setAttribute(CAPTCHA_CODE_KEY, randomString);
		g.dispose();
		try {
			ImageIO.write(image, "JPEG", response.getOutputStream());// 将内存中的图片通过流动形式输出到客户端
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 获得字体
	 */
	private Font getFont() {
		return new Font("Fixedsys", Font.CENTER_BASELINE, fontSize);
	}

	/*
	 * 获得颜色
	 */
	private Color getRandColor(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc - 16);
		int g = fc + random.nextInt(bc - fc - 14);
		int b = fc + random.nextInt(bc - fc - 18);
		return new Color(r, g, b);
	}

	/*
	 * 绘制字符串
	 */
	private String drowString(Graphics g, String randomString, int i) {
		g.setFont(getFont());
		g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
				.nextInt(121)));
		String rand = String.valueOf(getRandomString(random.nextInt(randString
				.length())));
		randomString += rand;
		g.translate(random.nextInt(3), random.nextInt(3));
		g.drawString(rand, xPosition * i, yPosition);
		return randomString;
	}

	/*
	 * 绘制干扰线
	 */
	private void drowLine(Graphics g) {
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		int xl = random.nextInt(13);
		int yl = random.nextInt(15);
		g.drawLine(x, y, x + xl, y + yl);
	}

	/*
	 * 获取随机的字符
	 */
	public String getRandomString(int num) {
		return String.valueOf(randString.charAt(num));
	}
}