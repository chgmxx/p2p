package com.power.platform.zdw.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class OCRUtils {

	private static final Logger log = LoggerFactory.getLogger(OCRUtils.class);

	/**
	 * 测试提交验证码图片
	 * 
	 * @param args
	 */
	public static String commitCat(String type, String src, File img) {

		String param = String.format("type=%s&src=%s", type, src);
		ByteArrayOutputStream baos = null;
		String answer = null;
		try {
			BufferedImage image = ImageIO.read(img);
			baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			baos.flush();
			byte[] data = baos.toByteArray();
			baos.close();
			// String url =
			// "http://10.10.202.21:8080/api/ocr/v1/identify/captchaIdentify?waitFlg=true";
			String url = PropertiesUtil.getProps("ocr.url");
			String html = httpPostImage(url, param, data);
			log.info("result:" + html);
			JSONObject job = JSON.parseObject(html);
			if (job.getBoolean("success")) {
				JSONObject resultJob = job.getJSONObject("result");
				answer = resultJob.getString("answer");
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			answer = null;
		}
		return answer;
	}

	/**
	 * 答题
	 * 
	 * @param url
	 *            请求URL
	 * @param param
	 *            请求参数，如：username=test&password=1
	 * @param data
	 *            图片二进制流
	 * @return 平台返回结果XML样式
	 * @throws IOException
	 */
	public static String httpPostImage(String url, String param, byte[] data) throws IOException {

		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------------" + System.currentTimeMillis();
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		OutputStream out = null;

		u = new URL(url);

		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setReadTimeout(30000);
		// 此值与timeout参数相关，如果timeout参数是90秒，这里就是95000，建议多5秒
		con.setConnectTimeout(30000);// 设定30s
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		out = con.getOutputStream();

		for (String paramValue : param.split("[&]")) {
			out.write(boundarybytesString.getBytes("UTF-8"));
			String paramString = "Content-Disposition: form-data; name=\"" + paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
			out.write(paramString.getBytes("UTF-8"));
		}
		out.write(boundarybytesString.getBytes("UTF-8"));
		String paramString = "Content-Disposition: form-data; name=\"captcha\"; filename=\"123.jpg\"\r\nContent-Type: image/gif\r\n\r\n";
		out.write(paramString.getBytes("UTF-8"));
		out.write(data);
		String tailer = "\r\n--" + boundary + "--\r\n";
		out.write(tailer.getBytes("UTF-8"));
		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
