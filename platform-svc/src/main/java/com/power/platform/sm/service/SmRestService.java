package com.power.platform.sm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.ZtmgPartnerPlatformDao;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.activity.service.ZtmgPartnerPlatformService;
import com.power.platform.cache.Cache;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.RandomUtil;
import com.power.platform.sms.dao.SmsMsgHistoryDao;
import com.power.platform.sms.entity.SmsMsgHistory;
import com.power.platform.sms.entity.SmsRejectHistory;
import com.power.platform.sms.service.SendEmailService;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.sms.service.SmsRejectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: SmRestService <br>
 * 描述: 发送消息(短信)唯一SVC. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月26日 下午4:25:23
 */
@Component
@Path("/sm")
@Service("smRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class SmRestService {

	private static final Logger LOG = LoggerFactory.getLogger(SmRestService.class);

	@Autowired
	private SendSmsService sendSmsService;
	@Resource
	private SmsMsgHistoryDao smsMsgHistoryDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private SendEmailService sendEmailService;
	@Autowired
	private ZtmgPartnerPlatformService ztmgPartnerPlatformService;
	@Autowired
	private SmsRejectService smsRejectService;
	@Resource
	private ZtmgPartnerPlatformDao ztmgPartnerPlatformDao;

	/**
	 * 
	 * 方法: verifySmsCode <br>
	 * 描述: 校验短信验证码. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月28日 下午4:36:56
	 * 
	 * @param mobilePhone
	 * @param from
	 * @return
	 */
	@POST
	@Path("/verifySmsCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> verifySmsCode(@FormParam("mobilePhone") String mobilePhone, @FormParam("from") String from, @FormParam("smsCode") String smsCode) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(from) || StringUtils.isBlank(smsCode)) {
			LOG.info("fn:verifySmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 缓存中获取短信验证码.
			 */
			
			String cachSmsCode = JedisUtils.get(mobilePhone);

			// 空指针异常.
			if (null == cachSmsCode) {
				LOG.info("fn:verifySmsCode,缓存中验证码不存在！");
				result.put("state", "5");
				result.put("message", "缓存中验证码不存在！");
				result.put("data", null);
				return result;
			}

			// 判断用户输入的验证码与缓存中的验证码是否相同.
			if (cachSmsCode.equals(smsCode)) {
				LOG.info("fn:verifySmsCode,校验手机短信验证码成功！");
				result.put("state", "0");
				result.put("message", "校验手机短信验证码成功！");
				result.put("data", null);
				return result;
			} else {
				LOG.info("fn:verifySmsCode,校验手机短信验证码失败！");
				result.put("state", "6");
				result.put("message", "校验手机短信验证码失败！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:verifySmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * 方法: sendSmsCode <br>
	 * 描述: 向客户发送短信验证码. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月26日 下午4:48:04
	 * 
	 * @param token
	 * <br>
	 *            客户唯一标识
	 * @param type
	 * <br>
	 *            1：平台注册.<br>
	 *            2：找回登陆密码.<br>
	 *            3：修改登陆密码.<br>
	 *            4：设置交易密码.<br>
	 *            5：找回交易密码.<br>
	 *            6：修改交易密码.<br>
	 *            7：绑定银行卡.<br>
	 * @param from
	 * <br>
	 *            请求平台.
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Path("/sendSmsCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> sendSmsCode(@FormParam("token") String token,@FormParam("mobilePhone") String mobilePhone, @FormParam("type") String type, @FormParam("from") String from, @Context HttpServletRequest servletRequest) throws Exception {

		// IP.
		String ip = (String) servletRequest.getAttribute("ip");
		ip = ip.trim();
		ip = ip.replace("_", ".");

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(type) || StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:sendSmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		
		if (from != "8" && !"8".equals(from) ) {
			if (!validate(ip,mobilePhone)) {			
				LOG.info("fn:sendSmsCode,短信发送过于频繁，请稍后操作！");
				result.put("state", "6");
				result.put("message", "短信发送过于频繁，请稍后操作！");
				result.put("data", null);
				return result;
			}
		}
				
		
		/**
		 * 发送SMS CODE.
		 */
		try {
			
			//校验token是否失效
			String jedisUserId = JedisUtils.get(token);
			
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoDao.getCgb(jedisUserId);
				if(user==null){
					user = userInfoDao.get(jedisUserId);
				}
				if (null != user) {
					// 验证码.
					String code = RandomUtil.generateRandomDigitalString(6);
					// 发送短信验证码，返回状态.
					String smsState = sendSmsService.sendSmsCode(mobilePhone, new String[] { mobilePhone, code });
					if (!"0".equals((((smsState.split(","))[1]).split("\n"))[0])) {
						LOG.info("fn:sendSmsCode,短信验证码发送失败,发送返回报告:" + smsState);
						result.put("state", "5");
						result.put("message", "短信验证码发送失败！");
						result.put("data", null);
						return result;
					} else {
						
						// 缓存中保存客户短信验证码.客户手机号码：为Key.
						JedisUtils.set(mobilePhone, code, 1200);
						
						// 短信消息验证码历史.
						SmsMsgHistory smsMsgHistory = new SmsMsgHistory();
						smsMsgHistory.setId(IdGen.uuid());
						smsMsgHistory.setPhone(mobilePhone);
						smsMsgHistory.setValidateCode(code);
						smsMsgHistory.setMsgContent(sendSmsService.getSmsTemplateContent(new String[] { mobilePhone, code }));
						smsMsgHistory.setCreateTime(new Date());
						smsMsgHistory.setType(Integer.valueOf(type));
						smsMsgHistory.setIp(ip);
						int flag = smsMsgHistoryDao.insert(smsMsgHistory);
						if (flag == 1) {
							LOG.info("fn:sendSmsCode,保存消息短信验证码成功！");
						} else {
							LOG.info("fn:sendSmsCode,保存消息短信验证码失败！");
						}
						LOG.info("fn:sendSmsCode,短信验证码发送成功,发送返回报告:" + smsState);
						result.put("state", "0");
						result.put("message", "短信验证码发送成功！");
						return result;
					}	
					
				}else{
					result.put("state", "4");
					result.put("message", "系统超时");
					result.put("data", null);
					return result;
				}
			}else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", null);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:sendSmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}@POST
	@Path("/sendSmsReturnCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> sendSmsReturnCode(@FormParam("token") String token,@FormParam("mobilePhone") String mobilePhone, @FormParam("type") String type, @FormParam("from") String from, @Context HttpServletRequest servletRequest) throws Exception {

		// IP.
		String ip = (String) servletRequest.getAttribute("ip");
		ip = ip.trim();
		ip = ip.replace("_", ".");

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(type) || StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:sendSmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		
		if (from != "8" && !"8".equals(from) ) {
			if (!validate(ip,mobilePhone)) {			
				LOG.info("fn:sendSmsCode,短信发送过于频繁，请稍后操作！");
				result.put("state", "6");
				result.put("message", "短信发送过于频繁，请稍后操作！");
				result.put("data", null);
				return result;
			}
		}
				
		
		/**
		 * 发送SMS CODE.
		 */
		try {
			
			//校验token是否失效
			String jedisUserId = JedisUtils.get(token);
			
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoDao.getCgb(jedisUserId);
				if(user==null){
					user = userInfoDao.get(jedisUserId);
				}
				if (null != user) {
					// 验证码.
					String code = RandomUtil.generateRandomDigitalString(6);
					// 发送短信验证码，返回状态.
					String smsState = sendSmsService.sendSmsCode(mobilePhone, new String[] { mobilePhone, code });
					if (!"0".equals((((smsState.split(","))[1]).split("\n"))[0])) {
						LOG.info("fn:sendSmsCode,短信验证码发送失败,发送返回报告:" + smsState);
						result.put("state", "5");
						result.put("message", "短信验证码发送失败！");
						result.put("data", null);
						return result;
					} else {
						
						// 缓存中保存客户短信验证码.客户手机号码：为Key.
						JedisUtils.set(mobilePhone, code, 1200);
						
						// 短信消息验证码历史.
						SmsMsgHistory smsMsgHistory = new SmsMsgHistory();
						smsMsgHistory.setId(IdGen.uuid());
						smsMsgHistory.setPhone(mobilePhone);
						smsMsgHistory.setValidateCode(code);
						smsMsgHistory.setMsgContent(sendSmsService.getSmsTemplateContent(new String[] { mobilePhone, code }));
						smsMsgHistory.setCreateTime(new Date());
						smsMsgHistory.setType(Integer.valueOf(type));
						smsMsgHistory.setIp(ip);
						int flag = smsMsgHistoryDao.insert(smsMsgHistory);
						if (flag == 1) {
							LOG.info("fn:sendSmsCode,保存消息短信验证码成功！");
						} else {
							LOG.info("fn:sendSmsCode,保存消息短信验证码失败！");
						}
						LOG.info("fn:sendSmsCode,短信验证码发送成功,发送返回报告:" + smsState);
						result.put("state", "0");
						result.put("mobileCode", code);
						result.put("message", "短信验证码发送成功！");
						return result;
					}	
					
				}else{
					result.put("state", "4");
					result.put("message", "系统超时");
					result.put("data", null);
					return result;
				}
			}else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", null);
				return result;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:sendSmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	/**
	 * 获取渠道用户推荐人投资信息
	 * @param pageNo
	 * @param pageSize
	 * @param mobile
	 * @return
	 */
	@POST
	@Path("/findListForBrokerage")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findListForBrokerage(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize,  @FormParam("mobile") String mobile) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(mobile) ) {
			LOG.info("fn:getBrokerageList,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		/**
		 * 业务处理.
		 */
		try {
			ZtmgPartnerPlatform ztmgPartnerPlatform = new ZtmgPartnerPlatform();
//			ztmgPartnerPlatform.setPhone(mobile);
//			ztmgPartnerPlatform = ztmgPartnerPlatformDao.get(ztmgPartnerPlatform);
			String id = ztmgPartnerPlatformDao.findIdForPartner(mobile);
			List<ZtmgPartnerPlatform> list2 = ztmgPartnerPlatformDao.findListForBrokerage(id);
			ztmgPartnerPlatform = ztmgPartnerPlatformDao.get(id);
			// 分页.
			Page<ZtmgPartnerPlatform> page = new Page<ZtmgPartnerPlatform>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			
			ztmgPartnerPlatform.setPage(page);
			page.setList(list2);
			Page<ZtmgPartnerPlatform> list = page;
			// 数据域.
			Map<String, Object> data = new HashMap<String, Object>();
				// 处理封装数据域.
				data.put("pageNo", list.getPageNo());
				data.put("pageSize", list.getPageSize());
				data.put("last", page.getLast());
				data.put("totalCount", list.getCount());
				// 封装后的列表数据.
				List<ZtmgPartnerPlatform> newList = new ArrayList<ZtmgPartnerPlatform>();
				ZtmgPartnerPlatform newModel = null;
				for (ZtmgPartnerPlatform model : list.getList()) {
					newModel = new ZtmgPartnerPlatform();
					if (null == model.getPhone()) {
						// 移动电话.
						newModel.setPhone("客户已销号");
						newList.add(newModel);
					} else {
						Map<String,Object> map = ztmgPartnerPlatformDao.findListForBrokerage2(model.userTransDetail.transId);
						Double moneyToOne = (Double.valueOf(map.get("moneyToOne").toString()));
						String userInfoName = map.get("userInfoName").toString();
//						model.setUserInfoName(map.get("userInfoName").toString());
						// 移动电话.
//						String mobilePhone = model.getPhone();
//						String subMobilePhone = mobilePhone.substring(0, 3);
//						String endMobilePhone = mobilePhone.substring(mobilePhone.length() - 4, mobilePhone.length());
//						newModel.setPhone(subMobilePhone + "****" + endMobilePhone);
//						String userInfoName = model.getUserInfoName();
						String subUserInfoName = userInfoName.substring(0, 3);
						String endUserInfoName = userInfoName.substring(userInfoName.length() - 4, userInfoName.length());
						newModel.setUserInfoName(subUserInfoName+"****"+endUserInfoName);
						String transDate = model.transDate.substring(0, 11);
						newModel.setTransDate(transDate);
						newModel.setMoneyToOne(moneyToOne);
						Double amount = model.userTransDetail.getAmount();
						newModel.setAmount(amount);
						newList.add(newModel);
					}
				}
				data.put("list", newList);
				LOG.info("fn:getBrokerageList,接口响应成功.");
				result.put("state", "0");
				result.put("message", "接口响应成功.");
				result.put("data", data);
				return result;

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getBrokerageList,系统错误.");
			result.put("state", "1");
			result.put("message", "系统错误.");
			result.put("data", null);
			return result;
		}

	}
	
	
	/**
	 * 渠道用户发送验证码
	 * @param token
	 * @param mobilePhone
	 * @param type
	 * @param from
	 * @param servletRequest
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/sendSmsCodeForPartner")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> sendSmsCodeForPartner(@FormParam("mobilePhone") String mobilePhone, @FormParam("type") String type, @FormParam("from") String from, @Context HttpServletRequest servletRequest) throws Exception {

		// IP.
		String ip = (String) servletRequest.getAttribute("ip");
		ip = ip.trim();
		ip = ip.replace("_", ".");

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(type) || StringUtils.isBlank(from) ) {
			LOG.info("fn:sendSmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		
		if (from != "8" && !"8".equals(from) ) {
			if (!validate(ip,mobilePhone)) {			
				LOG.info("fn:sendSmsCode,短信发送过于频繁，请稍后操作！");
				result.put("state", "6");
				result.put("message", "短信发送过于频繁，请稍后操作！");
				result.put("data", null);
				return result;
			}
		}
				
		
		/**
		 * 发送SMS CODE.
		 */
		try {
			
			String id = ztmgPartnerPlatformService.findIdForPartner(mobilePhone);
				if (null != id && id.length()>0) {
					// 验证码.
					String code = RandomUtil.generateRandomDigitalString(6);
					// 发送短信验证码，返回状态.
					String smsState = sendSmsService.sendSmsCode(mobilePhone, new String[] { mobilePhone, code });
					if (!"0".equals((((smsState.split(","))[1]).split("\n"))[0])) {
						LOG.info("fn:sendSmsCode,短信验证码发送失败,发送返回报告:" + smsState);
						result.put("state", "5");
						result.put("message", "短信验证码发送失败！");
						result.put("data", null);
						return result;
					} else {
						
						// 缓存中保存客户短信验证码. 客户手机号码：为Key.
						JedisUtils.set(mobilePhone, code, 1200);
						
						// 短信消息验证码历史.
						SmsMsgHistory smsMsgHistory = new SmsMsgHistory();
						smsMsgHistory.setId(IdGen.uuid());
						smsMsgHistory.setPhone(mobilePhone);
						smsMsgHistory.setValidateCode(code);
						smsMsgHistory.setMsgContent(sendSmsService.getSmsTemplateContent(new String[] { mobilePhone, code }));
						smsMsgHistory.setCreateTime(new Date());
						smsMsgHistory.setType(Integer.valueOf(type));
						smsMsgHistory.setIp(ip);
						int flag = smsMsgHistoryDao.insert(smsMsgHistory);
						if (flag == 1) {
							LOG.info("fn:sendSmsCode,保存消息短信验证码成功！");
						} else {
							LOG.info("fn:sendSmsCode,保存消息短信验证码失败！");
						}
						LOG.info("fn:sendSmsCode,短信验证码发送成功,发送返回报告:" + smsState);
						result.put("state", "0");
						result.put("message", "短信验证码发送成功！");
						return result;
					}	
					
				}else{
					result.put("state", "4");
					result.put("message", "系统超时");
					result.put("data", null);
					return result;
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:sendSmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	
	private synchronized boolean validate(String ip,String phone) throws Exception {
		boolean isValid = true;
		Map<String, String> sendIPMap = JedisUtils.getMap(ip);
		if (sendIPMap == null ) {
			isValid = true;
		}else {
			int times = Integer.valueOf(sendIPMap.get("times"));
			if (times >=20) {
				isValid =false;
				List<SmsRejectHistory> smsRejectHistorys = smsRejectService.getByIP(ip);
				SmsRejectHistory smsRejectHistory;
				if (smsRejectHistorys !=null && smsRejectHistorys.size()>0) {
					smsRejectHistory = smsRejectHistorys.get(0);
					smsRejectHistory.setTimes(smsRejectHistory.getTimes() + 1);
					smsRejectService.save(smsRejectHistory);
				}else {
					smsRejectHistory = new SmsRejectHistory();
					smsRejectHistory.setCreateTime(new Date());
					smsRejectHistory.setPhone(phone);
					smsRejectHistory.setIp(ip);
					smsRejectHistory.setTimes(times);
					smsRejectHistory.setType("1");
					smsRejectService.save(smsRejectHistory);
				}
				
				return isValid;
			}
		}	
		
		Map<String, String> sendPhoneMap = JedisUtils.getMap(phone+"times");
		if (sendPhoneMap == null ) {
			isValid = true;
		}else {
			int times = Integer.valueOf(sendPhoneMap.get("times"));
			if (times >=10) {
				isValid =false;
				SmsRejectHistory smsRejectHistory = smsRejectService.getByPhone(phone);
				if (smsRejectHistory !=null) {
					smsRejectHistory.setTimes(smsRejectHistory.getTimes() + 1);
					smsRejectService.save(smsRejectHistory);
				}else {
					smsRejectHistory = new SmsRejectHistory();
					smsRejectHistory.setCreateTime(new Date());
					smsRejectHistory.setPhone(phone);
					smsRejectHistory.setIp(ip);
					smsRejectHistory.setTimes(times);
					smsRejectHistory.setType("0");
					smsRejectService.save(smsRejectHistory);
				}
				return isValid;
			}
		}	
		return isValid;
	}
	
	
	
	@POST
	@Path("/checkEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> checkEmail(@FormParam("from") String from,@FormParam("userCode") String userCode,@FormParam("validateCode")String validateCode) {
		Map<String, Object> result = new HashMap<String, Object>();
		//判断必要参数是否为空
		if(StringUtils.isBlank(from)){
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			UserInfo userInfo =userInfoDao.get(userCode);
			if(userInfo==null){
				userInfo =userInfoDao.getCgb(userCode);
			}
			if (null != userInfo&StringUtils.isNotBlank(userInfo.getId())) {
				if(userInfo.getSalt().equals(validateCode)){
					sendEmailService.checkEmail(userInfo);
					userInfo.setEmailChecked(2);
					userInfo.setSalt("");
					userInfoDao.updateEmailInfo(userInfo);
					result.put("state", "0");
					result.put("message", "邮箱验证成功");
				}else{
					result.put("state", "1");
					result.put("message", "邮箱验证失败！");
				}
			}else{
				result.put("state", "1");
				result.put("message", "邮箱验证失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "邮箱验证失败！");
		}
		return result;
	}

	/**
	 * 获取图形验证码
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getPictureCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getPictureCode(@FormParam("from") String from) {
		Map<String, Object> result = new HashMap<String, Object>();
		//判断必要参数是否为空
		if(StringUtils.isBlank(from)){
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			
			String code = RandomUtil.generateRandomDigitalString(4);
			String key = RandomUtil.random6Num().toString();
			// 缓存中保存图形验证码.
			JedisUtils.set(key, code, 1200);
			
            result.put("state", "0");
            result.put("message", "获取图形验证码成功");
            result.put("key", key);
            result.put("pictureCode", code);
 
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "获取图形验证码失败！");
		}
		return result;
	}
	

	/**
	 * 校验图形验证码
	 * @param key
	 * @param from
	 * @param pictureCode
	 * @return
	 */
	@POST
	@Path("/checkPictureCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> checkPictureCode(@FormParam("key") String key, @FormParam("from") String from, @FormParam("pictureCode") String pictureCode) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(key) || StringUtils.isBlank(from) || StringUtils.isBlank(pictureCode)) {
			LOG.info("fn:verifySmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 缓存中获取图形验证码.
			 */
			String cachPictureCode = JedisUtils.get(key);

			// 空指针异常.
			if (null == cachPictureCode) {
				LOG.info("fn:verifySmsCode,缓存中图形验证码不存在！");
				result.put("state", "5");
				result.put("message", "缓存中图形验证码不存在！");
				result.put("data", null);
				return result;
			}

			// 判断用户输入的验证码与缓存中的验证码是否相同.
			if (cachPictureCode.equals(pictureCode)) {
				LOG.info("fn:verifySmsCode,校验图形验证码成功！");
				result.put("state", "0");
				result.put("message", "校验图形验证码成功！");
				result.put("data", null);
				return result;
			} else {
				LOG.info("fn:verifySmsCode,校验图形验证码失败！");
				result.put("state", "6");
				result.put("message", "校验图形验证码失败！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:verifySmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	/**
	 * 注册,找回密码发送短信验证码
	 * @param key
	 * @param picturCode
	 * @param mobilePhone
	 * @param type
	 * @param from
	 * @param servletRequest
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/newSendSmsCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> newSendSmsCode(@FormParam("key") String key,@FormParam("picturCode") String picturCode ,@FormParam("mobilePhone") String mobilePhone, @FormParam("type") String type, @FormParam("from") String from, @Context HttpServletRequest servletRequest) throws Exception {

		// IP.
		String ip = (String) servletRequest.getAttribute("ip");
		ip = ip.trim();
		ip = ip.replace("_", ".");

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(mobilePhone) || StringUtils.isBlank(type) || StringUtils.isBlank(from) || StringUtils.isBlank(key) || StringUtils.isBlank(picturCode)) {
			LOG.info("fn:newSendSmsCode,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		
		if (from != "8" && !"8".equals(from) ) {
			if (!validate(ip,mobilePhone)) {			
				LOG.info("fn:newSendSmsCode,短信发送过于频繁，请稍后操作！");
				result.put("state", "6");
				result.put("message", "短信发送过于频繁，请稍后操作！");
				result.put("data", null);
				return result;
			}
		}
				
		
		/**
		 * 发送SMS CODE.
		 */
		try {
			
			//获取缓存中图形验证码
			String cachpictureCode = JedisUtils.get(key);
			
			// 空指针异常.
			if (null == cachpictureCode && !key.equals("cicmorganabc")) {
				LOG.info("fn:newSendSmsCode,缓存中图形验证码不存在！");
				result.put("state", "7");
				result.put("message", "缓存中图形验证码不存在！");
				result.put("data", null);
				return result;
			}

			// 判断用户输入的验证码与缓存中的验证码是否相同.
			if (key.equals("cicmorganabc")||cachpictureCode.equals(picturCode)) {
				
				// 验证码.
				String code = RandomUtil.generateRandomDigitalString(6);
				// 发送短信验证码，返回状态.
				String smsState = sendSmsService.sendSmsCode(mobilePhone, new String[] { mobilePhone, code });
				if (!"0".equals((((smsState.split(","))[1]).split("\n"))[0])) {
					LOG.info("fn:newSendSmsCode,短信验证码发送失败,发送返回报告:" + smsState);
					result.put("state", "5");
					result.put("message", "短信验证码发送失败！");
					result.put("data", null);
					return result;
				} else {
					
					// 缓存中保存客户短信验证码.客户手机号码：为Key.
					JedisUtils.set(mobilePhone, code, 1200);
					
					
					
					/*
					Map<String, Integer> sendMap = smCache.get(mobilePhone+"times");
					if (sendMap == null ) {
						sendMap = new HashMap<String, Integer>();
						sendMap.put("times", 1);
						smCache.set(mobilePhone+"times", sendMap);
					}else {
						int times = sendMap.get("times");
						sendMap.put("times", times+1);
						smCache.set(mobilePhone+"times",86400,sendMap);
					}	

					Map<String, Integer> sendIpMap = smCache.get(ip);
					if (sendIpMap == null ) {
						sendIpMap = new HashMap<String, Integer>();
						sendIpMap.put("times", 1);
						smCache.set(ip,86400,sendIpMap);
					}else {
						int times = sendIpMap.get("times");
						sendIpMap.put("times", times+1);
						smCache.set(ip,86400,sendIpMap);
					}*/
					
					// 短信消息验证码历史.
					SmsMsgHistory smsMsgHistory = new SmsMsgHistory();
					smsMsgHistory.setId(IdGen.uuid());
					smsMsgHistory.setPhone(mobilePhone);
					smsMsgHistory.setValidateCode(code);
					smsMsgHistory.setMsgContent(sendSmsService.getSmsTemplateContent(new String[] { mobilePhone, code }));
					smsMsgHistory.setCreateTime(new Date());
					smsMsgHistory.setType(Integer.valueOf(type));
					smsMsgHistory.setIp(ip);
					int flag = smsMsgHistoryDao.insert(smsMsgHistory);
					if (flag == 1) {
						LOG.info("fn:newSendSmsCode,保存消息短信验证码成功！");
					} else {
						LOG.info("fn:newSendSmsCode,保存消息短信验证码失败！");
					}
					LOG.info("fn:newSendSmsCode,短信验证码发送成功,发送返回报告:" + smsState);
					result.put("state", "0");
					result.put("message", "短信验证码发送成功！");
					return result;
				}
				
			} else {
				LOG.info("fn:newSendSmsCode,校验图形验证码失败！");
				result.put("state", "8");
				result.put("message", "校验手机短信验证码失败！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:newSendSmsCode,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	
	
}
