package com.power.platform.sms.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.MD5Util;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.current.entity.WloanCurrentProject;
import com.power.platform.sys.entity.User;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

@Service("sendEmailService")
public class SendEmailService {
		
	@Autowired
	private UserInfoService userInfoService;
	
	public String sendEmail(UserInfo userInfo){
		String emailUrl =Global.getConfig("email.check.html");
		String validateCode = MD5Util.encode2hex(userInfo.getEmail()+userInfo.getId());
		StringBuffer sb = new StringBuffer(
				"点击下面链接激活账号，60分钟内有效生效，否则请重新获取，链接只能使用一次，请尽快激活！</br>");
		sb.append("<a href=" +emailUrl + "?validateCode="); 
		sb.append(validateCode+"&userCode="+userInfo.getId());
		sb.append(">");  
		sb.append(emailUrl);
		sb.append("?validateCode=");
		sb.append(validateCode+"&userCode="+userInfo.getId());
		sb.append("</a>");
		SendMailUtil.sendCommonMail(userInfo.getEmail(), "中投摩根-邮箱绑定!",sb.toString());
		return validateCode;
	}
	
	public String  checkEmail(UserInfo userInfo){
			// 验证链接是否在有效期内（60分钟内有效）
			if (userInfo.getSendemaildate() != null && userInfo.getSendemaildate().getTime() > 0) {
				long sendDate = userInfo.getSendemaildate().getTime();
				long activDate = new Date().getTime();
				if (activDate > sendDate) {
					return "outTime";//超时
				}else{
					return "success";//成功
				}
			}else{
				return "error";
			}
	}
	
	/**
	 * 绑定邮箱发送验证邮件
	 * @param path
	 * @param userInfo
	 * @param email
	 * @throws MessagingException 
	 */
	public void sendMultipartMail(String path, UserInfo userInfo, String email) throws MessagingException {
		
		String validateCode = MD5Util.encode2hex(email);
		
		userInfo.setRealName(userInfo.getRealName() == null ? userInfo.getName() : userInfo.getRealName());
		
		StringBuffer sb = new StringBuffer(
				"点击下面链接激活账号，30分钟内有效生效，否则请重新获取，链接只能使用一次，请尽快激活！</br>");
		sb.append("<a href=" + Global.getConfig("email.send.address") + "/email/activateemail?action=activate&phone="); 
		sb.append(userInfo.getName());
		sb.append("&validateCode=");
		sb.append(validateCode);
		sb.append("&email=");
		sb.append(email);
		sb.append(">" + Global.getConfig("email.send.address") + "/email/activateemail?action=activate&phone=");  
		sb.append(userInfo.getName());
		sb.append("&validateCode=");
		sb.append(validateCode);
		sb.append("&email=");
		sb.append(email);
		sb.append("</a>");

		System.out.println(Global.getConfig("email.send.address") + "...................");
		System.out.println("开始发送邮件。。。。。。。。。。。。。。");
		SendMailUtil.sendCommonMail(email, "中投摩根-邮箱绑定!", sb.toString());
		
		userInfo.setSalt(validateCode);
		
		long currentTime = new Date().getTime() + 1000 * 30 * 60;
		
		userInfo.setSendemaildate( new Date(currentTime) );
		
		userInfoService.updateUserByName( userInfo );
		
		System.out.println("邮件发送完毕。。。。。。。。。。。。。。");
		
	}

	/**
	 * 邮箱激活
	 * @param phone
	 * @param validateCode
	 * @param email
	 * @return 
	 */
	public Map<String, Object> processActivate(String phone, String validateCode, String email)
		throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.setName(phone);
		List<UserInfo> list = userInfoService.findList(userInfo);
	
		Map<String, Object> resultMap = new HashMap<String, Object>();
	
		if (list != null && list.size() > 0) {
			UserInfo existUser = list.get(0);
			
			// 验证链接是否在有效期内（30分钟内有效）
			if (existUser.getSendemaildate() != null && existUser.getSendemaildate().getTime() > 0) {
				long sendDate = existUser.getSendemaildate().getTime();
				long activDate = new Date().getTime();
				
				if (activDate > sendDate) {
					resultMap.put("result", "该链接已过有效期，");
					return resultMap;
				}
			}
				
			//校验是否已经激活过
			if (existUser.getSalt() != null && !existUser.getSalt().equals("")) {
				String salt = existUser.getSalt();
				if (salt.equals(validateCode)) {
					// 激活成功
					existUser.setEmailChecked(2);
					existUser.setEmail(email);
					existUser.setSalt("");
					userInfoService.updateUserByName(existUser);
					resultMap.put("result", "激活成功，");
				} else {
					resultMap.put("result", "错误的激活码，");
				}
			} else if(existUser.getEmailChecked() == 2 || existUser.getSalt() == null){
				resultMap.put("result", "账户已绑定，不要重复点击链接。");
			}
			
		} else {
			resultMap.put("result", "不存在的用户，");
		}
		
		return resultMap;
	}
	
	
	
	/**
	 * 反馈的意见发送到客服邮箱
	 * 
	 * @param user
	 * @param email
	 * @throws Exception
	 */
	public void sendOptionToMail(String email, String subject, String context) {
		StringBuffer sb = new StringBuffer(subject);
		sb.append("</br>");
		sb.append("<div>");
		sb.append(context);
		sb.append("</div>");
		SendMailUtil.sendCommonMail(email, "用户反馈：" + subject, sb.toString());
	}

	
	/**
	 * 发送项目到期提醒email提醒
	 * @param email
	 * @param wloanCurrentProject
	 */
	public void sendProjectMessage(User user,
			WloanCurrentProject wloanCurrentProject) {
		StringBuffer sb = new StringBuffer("<p>尊敬的" + user.getName() + ":</p>");
		sb.append("<p style='margin-left: 12px;'>您好：</p>");
		sb.append("<p style='margin-left: 12px;'>活期编号为：" + wloanCurrentProject.getSn() + "(" + wloanCurrentProject.getName() + ")的项目,");
		sb.append("即将在" + DateUtils.formatDate(wloanCurrentProject.getEndDate(), "yyyy-MM-dd") + "日过期，请尽快处理。</p>" );
		sb.append("<p style='margin-left: 200px;'>赢多多管理员</p>");
		sb.append("<p style='margin-left: 200px;'>" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + "</p>");
		SendMailUtil.sendCommonMail(user.getEmail(), "赢多多-活期项目到期提醒!", sb.toString());
	}
	
	public void sendEmailForLoan(List<String> list) {
		String toMailAddr = null; 
		String cc = null; 
		String subject = null; 
		String message = null; 
//		SendMailUtil.sendWithMsgAndAttachment(toMailAddr, cc,subject, message, list); 
        
//		StringBuffer sb = new StringBuffer("<p>尊敬的" + user.getName() + ":</p>");
//		sb.append("<p style='margin-left: 12px;'>您好：</p>");
//		sb.append("<p style='margin-left: 12px;'>活期编号为：" + wloanCurrentProject.getSn() + "(" + wloanCurrentProject.getName() + ")的项目,");
//		sb.append("即将在" + DateUtils.formatDate(wloanCurrentProject.getEndDate(), "yyyy-MM-dd") + "日过期，请尽快处理。</p>" );
//		sb.append("<p style='margin-left: 200px;'>赢多多管理员</p>");
//		sb.append("<p style='margin-left: 200px;'>" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + "</p>");
//		SendMailUtil.sendCommonMail(user.getEmail(), "赢多多-活期项目到期提醒!", sb.toString());
	}
	
}
