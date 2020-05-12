/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.common.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 发送电子邮件
 */
public class SendMailUtil {

	private static final Logger logger = LoggerFactory.getLogger(SendMailUtil.class);

	/**
	 * 中投摩根新邮箱.
	 */
	private static final String ZTMG_FROM_ADDR = "zhongtoumogen@cicmorgan.com";
	/**
	 * 中投摩根新邮箱帐号.
	 */
	private static final String ZTMG_USERNAME = "zhongtoumogen@cicmorgan.com";
	/**
	 * 中投摩根新邮箱密码.
	 */
	private static final String ZTMG_PASSWORD = "Ztmg2019";

	// private static final String smtphost = "192.168.1.70";
	private static final String from = "yingduoduokefu@163.com";
	private static final String fromName = "中投摩根";
	private static final String charSet = "utf-8";
	private static final String username = "yingduoduokefu@163.com";
	private static final String password = "vltdrbkgvwkfyokb";

	// private static final String fromWF = "yangxing@cicmorgan.com";
	private static final String fromWF = "wangyanle@cicmorgan.com";
	private static final String charSetWF = "utf-8";
	// private static final String usernameWF = "yangxing@cicmorgan.com";
	// private static final String passwordWF = "123456Yx";
	private static final String usernameWF = "wangyanle@cicmorgan.com";
	private static final String passwordWF = "Wyl0701_";

	// 收件人
	// 测试
	 public static final String toMailAddr = "liyun@cicmorgan.com"; // 收件人，海口联合农商银行运营审核人员的邮箱地址
	public static final String toMailAddrCC = "zhouwenmin@cicmorgan.com";// 抄送人
	 public static final String toMailAddrCCS = "zhouwenmin@cicmorgan.com"; // 抄送人，平台风控专员
	// 正式
//	public static final String toMailAddr = "ubcg2@unitedbank.cn"; // 收件人，海口联合农商银行运营审核人员的邮箱地址
//	public static final String toMailAddrCCS = "wangyanle@cicmorgan.com"; // 抄送人，平台风控专员
	// public static final String toMailAddr = "cgyy@unitedbank.cn";//收件人
	// public static final String toMailAddrCC = "lindanya@unitedbank.cn";//抄送人
	// public static final String toMailAddrCCS = "yangxing@cicmorgan.com";//抄送人

	// 还款计划发送核心企业
	public static final String toMailAddrCore = "jice@cicmorgan.com";// 收件人
	// public static final String toMailAddrCCCore = "yangxing@cicmorgan.com";// 抄送人
	public static final String toMailAddrCCCore = "wangyanle@cicmorgan.com";// 抄送人

	private static Map<String, String> hostMap = new HashMap<String, String>();
	static {
		// 126
		hostMap.put("smtp.126", "smtp.126.com");
		// qq
		hostMap.put("smtp.qq", "smtp.qq.com");

		// 163
		hostMap.put("smtp.163", "smtp.163.com");

		// sina
		hostMap.put("smtp.sina", "smtp.sina.com.cn");

		// tom
		hostMap.put("smtp.tom", "smtp.tom.com");

		// 263
		hostMap.put("smtp.263", "smtp.263.net");

		// yahoo
		hostMap.put("smtp.yahoo", "smtp.mail.yahoo.com");

		// hotmail
		hostMap.put("smtp.hotmail", "smtp.live.com");

		// aliyun
		hostMap.put("smtp.mxhichina", "smtp.mxhichina.com");

		// gmail
		hostMap.put("smtp.gmail", "smtp.gmail.com");
		hostMap.put("smtp.port.gmail", "465");

		// cicmorgan
		hostMap.put("cicmorgan.com", "smtp.exmail.qq.com");
		hostMap.put("smtp.exmail.qq.com", "25");

	}

	public static String getHost(String email) throws Exception {

		Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
		Matcher matcher = pattern.matcher(email);
		String key = "unSupportEmail";
		if (matcher.find()) {
			key = "smtp.163";
		}
		if (email.split("@")[1].equals("cicmorgan.com")) {
			key = "cicmorgan.com";
		}
		if (hostMap.containsKey(key)) {
			return hostMap.get(key);
		} else {
			throw new Exception("unSupportEmail");
		}
	}

	public static int getSmtpPort(String email) throws Exception {

		Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
		Matcher matcher = pattern.matcher(email);
		String key = "unSupportEmail";
		if (matcher.find()) {
			key = "smtp.port." + matcher.group(1);
		}
		if (email.split("@")[1].equals("cicmorgan.com")) {
			key = "smtp.exmail.qq.com";
		}
		if (hostMap.containsKey(key)) {
			return Integer.parseInt(hostMap.get(key));
		} else {
			return 25;
		}
	}

	/**
	 * 发送模板邮件
	 * 
	 * @param toMailAddr
	 *            收信人地址
	 * @param subject
	 *            email主题
	 * @param templatePath
	 *            模板地址
	 * @param map
	 *            模板map
	 */
	public static void sendFtlMail(String toMailAddr, String subject, String templatePath, Map<String, Object> map) {

		Template template = null;
		Configuration freeMarkerConfig = null;
		HtmlEmail hemail = new HtmlEmail();
		try {
			hemail.setHostName(getHost(from));
			hemail.setSmtpPort(getSmtpPort(from));
			hemail.setCharset(charSet);
			hemail.addTo(toMailAddr);
			hemail.setFrom(from, fromName);
			hemail.setAuthentication(username, password);
			hemail.setSubject(subject);
			freeMarkerConfig = new Configuration();
			freeMarkerConfig.setDirectoryForTemplateLoading(new File(getFilePath()));
			// 获取模板
			template = freeMarkerConfig.getTemplate(getFileName(templatePath), new Locale("Zh_cn"), "UTF-8");
			// 模板内容转换为string
			String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
			System.out.println(htmlText);
			hemail.setMsg(htmlText);
			hemail.send();
			System.out.println("email send true!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("email send error!");
		}
	}

	/**
	 * 发送普通邮件
	 * 
	 * @param toMailAddr
	 *            收信人地址
	 * @param subject
	 *            email主题
	 * @param message
	 *            发送email信息
	 */
	public static void sendCommonMail(String toMailAddr, String subject, String message) {

		HtmlEmail hemail = new HtmlEmail();
		try {
			hemail.setHostName(getHost(from));
			hemail.setSmtpPort(getSmtpPort(from));
			hemail.setCharset(charSet);
			hemail.addTo(toMailAddr);
			hemail.setFrom(from, fromName);
			hemail.setAuthentication(username, password);
			hemail.setSubject(subject);
			hemail.setMsg(message);
			hemail.send();
			System.out.println("email send true!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("email send error!");
		}

	}

	/**
	 * 发送普通邮件(带返回值)
	 * 
	 * @param toMailAddr
	 *            收信人地址
	 * @param subject
	 *            email主题
	 * @param message
	 *            发送email信息
	 */
	public static Boolean sendCommonMailBoolean(String toMailAddr, String subject, String message) {

		HtmlEmail hemail = new HtmlEmail();
		try {
			hemail.setHostName(getHost(from));
			hemail.setSmtpPort(getSmtpPort(from));
			hemail.setCharset(charSet);
			hemail.addTo(toMailAddr);
			hemail.setFrom(from, fromName);
			hemail.setAuthentication(username, password);
			hemail.setSubject(subject);
			hemail.setMsg(message);
			hemail.send();
			System.out.println("email send true!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("email send error!");
			return false;
		}

	}

	/**
	 * 
	 * 方法: ztmgSendRepayRemindEmailMsg <br>
	 * 描述: 发送本月项目还款日(T-7、T-5、T-2)还款邮件提醒. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月11日 下午3:34:29
	 * 
	 * @param toMailAddr
	 * @param cc
	 * @param subject
	 * @param message
	 */
	public static void ztmgSendRepayRemindEmailMsg(String toMailAddr, String cc, String subject, String message) {

		HtmlEmail hemail = new HtmlEmail();
		try {
			String host = getHost(ZTMG_FROM_ADDR);
			logger.info("host:{}", host);
			int smtpPort = getSmtpPort(ZTMG_FROM_ADDR);
			logger.info("smtpPort:{}", smtpPort);
			hemail.setHostName(host);
			hemail.setSmtpPort(smtpPort);
			hemail.setCharset(charSet);
			if (toMailAddr != null) { // 收件人.
				hemail.addTo(toMailAddr);
			}
			if (cc != null) { // 抄送人.
				hemail.addCc(cc);
			}
			hemail.setFrom(ZTMG_FROM_ADDR, fromName);
			hemail.setAuthentication(ZTMG_USERNAME, ZTMG_PASSWORD);
			hemail.setSubject(subject);
			hemail.setMsg(message);
			hemail.send();
			logger.info("email send true!");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("email send error!");
		}
	}

	/**
	 * 发送带有附件的邮件. 支持一个/多个接收方,一个/多个抄送方,一个/多个秘密抄送方.
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean sendWithMsgAndAttachment(String toMailAddr, String cc, String ccs, String subject, String message, List<String> list, String enterpriseFullName) {

		boolean result = false;

		try {
			MultiPartEmail email = new MultiPartEmail();
			email.setSmtpPort(getSmtpPort(fromWF));
			EmailAttachment attachment = new EmailAttachment();
			email.setHostName(getHost(fromWF));
			email.setAuthentication(usernameWF, passwordWF);
			email.setCharset(charSetWF);
			// email.setSocketConnectionTimeout(timeout);
			email.setFrom(fromWF);
			if (toMailAddr != null) {
				email.addTo(toMailAddr);
			}
			// if (tos != null) {
			// email.addTo(tos);
			// }
			if (cc != null) {
				email.addCc(cc);
			}
			if (ccs != null) {
				email.addCc(ccs);
			}
			// if (ccss != null) {
			// email.addCc(ccss);
			// }
			// if (bcc != null) {
			// email.addBcc(bcc);
			// }
			// if (bccs != null) {
			// email.addBcc(bccs);
			// }
			if (subject != null) {
				email.setSubject(subject);
			}
			if (message != null) {
				email.setMsg(message);
			}
			// if(list!=null && list.size()>0){
			// for(int i=0;i<list.size();i++){
			// attachment.setPath(list.get(i));
			// attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// }
			// }
			// if (localAttachmentPath != null) {
			attachment.setPath(list.get(0));
			attachment.setName(MimeUtility.encodeText(enterpriseFullName) + ".zip");
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// }
			// if (remoteAttachmentPath != null) {
			// attachment.setURL(new URL(remoteAttachmentPath));
			// attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// }

			// String remoteAttachmentPath = list.get(0);
			// attachment.setURL(new URL(remoteAttachmentPath));
			// attachment.setDisposition(EmailAttachment.ATTACHMENT);

			// if(attachmentName != null) {
			// attachment.setName(attachmentName);
			// }
			// if(attachDescription != null) {
			// attachment.setDescription(attachDescription);
			// }
			email.attach(attachment);
			if (email.send() != null) {
				System.out.println("发送邮件成功");
				result = true;
			} else {
				System.out.println("发送邮件失败");
			}
		} catch (EmailException e) {
			e.printStackTrace();
			System.out.println("发送邮件失败: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("发送邮件失败: " + e);
		}
		return result;
	}

	/**
	 * 发送带有附件的邮件. 支持一个/多个接收方,一个/多个抄送方,一个/多个秘密抄送方.
	 * 还款计划发送给核心企业
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean sendWithMsgAndAttachmentToCore(String toMailAddr, String cc, String subject, String message, String path, String fileName) {

		boolean result = false;

		try {
			MultiPartEmail email = new MultiPartEmail();
			email.setSmtpPort(getSmtpPort(from));
			EmailAttachment attachment = new EmailAttachment();
			email.setHostName(getHost(from));
			email.setAuthentication(username, password);
			email.setCharset(charSet);
			// email.setSocketConnectionTimeout(timeout);
			email.setFrom(from);
			if (toMailAddr != null) {
				email.addTo(toMailAddr);
			}
			// if (tos != null) {
			// email.addTo(tos);
			// }
			if (cc != null) {
				email.addCc(cc);
			}
			// if (ccs != null) {
			// email.addCc(ccs);
			// }
			// if (ccss != null) {
			// email.addCc(ccss);
			// }
			// if (bcc != null) {
			// email.addBcc(bcc);
			// }
			// if (bccs != null) {
			// email.addBcc(bccs);
			// }
			if (subject != null) {
				email.setSubject(subject);
			}
			if (message != null) {
				email.setMsg(message);
			}
			// if(list!=null && list.size()>0){
			// for(int i=0;i<list.size();i++){
			// attachment.setPath(list.get(i));
			// attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// }
			// }
			// if (localAttachmentPath != null) {
			attachment.setPath(path);
			attachment.setName(MimeUtility.encodeText(fileName));
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// }
			// if (remoteAttachmentPath != null) {
			// attachment.setURL(new URL(remoteAttachmentPath));
			// attachment.setDisposition(EmailAttachment.ATTACHMENT);
			// }

			// String remoteAttachmentPath = list.get(0);
			// attachment.setURL(new URL(remoteAttachmentPath));
			// attachment.setDisposition(EmailAttachment.ATTACHMENT);

			// if(attachmentName != null) {
			// attachment.setName(attachmentName);
			// }
			// if(attachDescription != null) {
			// attachment.setDescription(attachDescription);
			// }
			email.attach(attachment);
			if (email.send() != null) {
				System.out.println("发送邮件成功");
				result = true;
			} else {
				System.out.println("发送邮件失败");
			}
		} catch (EmailException e) {
			e.printStackTrace();
			System.out.println("发送邮件失败: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("发送邮件失败: " + e);
		}
		return result;
	}

	public static String getHtmlText(String templatePath, Map<String, Object> map) {

		Template template = null;
		String htmlText = "";
		try {
			Configuration freeMarkerConfig = null;
			freeMarkerConfig = new Configuration();
			freeMarkerConfig.setDirectoryForTemplateLoading(new File(getFilePath()));
			// 获取模板
			template = freeMarkerConfig.getTemplate(getFileName(templatePath), new Locale("Zh_cn"), "UTF-8");
			// 模板内容转换为string
			htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
			System.out.println(htmlText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlText;
	}

	private static String getFilePath() {

		String path = getAppPath(SendMailUtil.class);
		path = path + File.separator + "mailtemplate" + File.separator;
		path = path.replace("\\", "/");
		System.out.println(path);
		return path;
	}

	private static String getFileName(String path) {

		path = path.replace("\\", "/");
		System.out.println(path);
		return path.substring(path.lastIndexOf("/") + 1);
	}

	// @SuppressWarnings("unchecked")
	public static String getAppPath(Class<?> cls) {

		// 检查用户传入的参数是否为空
		if (cls == null)
			throw new java.lang.IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {// 否则按照包名的组成部分，将包名转换为路径
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1)
			realPath = realPath.substring(pos + 5);
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		/*------------------------------------------------------------ 
		 ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径 
		  中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要 
		  的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的 
		  中文及空格路径 
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("realPath----->" + realPath);
		return realPath;
	}

	public static void main(String[] args) {

		// sendCommonMail("lichen1638@sina.cn", "hahaha", "test content");
		// sendWithMsgAndAttachment("jice@cicmorgan.com","hahaha","test content",list);
	}

}