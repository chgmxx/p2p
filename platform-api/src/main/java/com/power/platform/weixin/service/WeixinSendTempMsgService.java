package com.power.platform.weixin.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.more.stationletter.dao.StationLetterDao;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.pay.cash.entity.UserCash;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.sms.service.SendSmsService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.entity.ZtmgWechatRelation;
import com.power.platform.weixin.resp.BaseTemplete;
import com.power.platform.weixin.resp.TempleteMsg;
import com.power.platform.weixin.service.ZtmgWechatRelationService;
import com.power.platform.weixin.utils.WeixinUtil;

/**
 * 微信发送模板消息公共类
 * 
 * @author Jia
 *
 */
@Service
public class WeixinSendTempMsgService {

	private static Logger logger = Logger.getLogger(WeixinSendTempMsgService.class);

	/**
	 * 1：付息.
	 */
	public static final String IS_REPAY_TYPE_1 = "1";
	/**
	 * 2：还本.
	 */
	public static final String IS_REPAY_TYPE_2 = "2";

	/**
	 * 风控负责人.
	 */
	public static final String ZTMG_SEND_WARN_INFO_MSG_1 = "1";
	/**
	 * 财务负责人.
	 */
	public static final String ZTMG_SEND_WARN_INFO_MSG_2 = "2";

	// 还款模板跳转地址
	private final static String REPAY_INFO_URL = "";
	// 投资模板跳转地址
	private final static String INVEST_INFO_URL = "";
	// 充值模板跳转地址
	private final static String RECHARGE_INFO_URL = "";
	// 提现模板跳转地址
	private final static String CASH_INFO_URL = "";

	@Autowired
	private ZtmgWechatRelationService ztmgWechatRelationService;
	@Autowired
	private SendSmsService sendSmsService;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private StationLettersService stationLettersService;
	@Resource
	private StationLetterDao stationLetterDao;

	/**
	 * 
	 * 方法: ztmgSendRepayRemindMsg <br>
	 * 描述: 中投摩根发送催收还款(T-7、T-5、T-2)日，短消息提醒. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月11日 下午1:58:03
	 * 
	 * @param mobilePhone
	 * @param message
	 */
	public void ztmgSendRepayRemindMsg(String mobilePhone, String message) {

		try {
			// 发送短信消息提醒.
			logger.info(this.getClass() + "-START-短消息接受人-" + mobilePhone);
			// logger.info(this.getClass() + "手机-" + mobilePhone + "-短消息-" +
			// message);
			String result = sendSmsService.directSendSMS(mobilePhone, message);
			logger.info(this.getClass() + "-短信发送结果-" + result);
			logger.info(this.getClass() + "-END-");
		} catch (Exception e) {
			logger.info(this.getClass() + "-" + e.getMessage() + "-");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: ztmgSendWarnInfoMsg <br>
	 * 描述: 还款时借款户及代偿户账户余额不足时发送短消息提醒. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月18日 上午9:15:18
	 * 
	 * @param mobilePhone
	 *            短消息接受人的手机
	 * @param name
	 *            短消息接受人姓名
	 * @param companyName
	 *            还款企业
	 * @param type
	 *            短消息接受人的类型
	 */
	public void ztmgSendWarnInfoMsg(String mobilePhone, String name, String companyName, String type) {

		try {
			// 发送短信消息提醒.
			logger.info(this.getClass() + "-短消息接受人-" + mobilePhone);
			logger.info(this.getClass() + "-START-");
			if (type.equals(ZTMG_SEND_WARN_INFO_MSG_1)) { // 风控短消息提醒.
				String result = sendSmsService.directSendSMS(mobilePhone, "尊敬的风控同事" + name + "，" + companyName + "借款账户余额不足，无法正常还款，请及时告知借款人充值还款！");
				logger.info(this.getClass() + "-短信发送结果-" + result);
			} else if (type.equals(ZTMG_SEND_WARN_INFO_MSG_2)) { // 财务短消息提醒.
				String result = sendSmsService.directSendSMS(mobilePhone, "尊敬的财务同事" + name + "，" + companyName + "借款账户余额不足，无法正常还款，请及时告知借款人充值还款！");
				logger.info(this.getClass() + "-短信发送结果-" + result);
			}
			logger.info(this.getClass() + "-END-");
		} catch (Exception e) {
			logger.info(this.getClass() + "-" + e.getMessage() + "-");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: cgbSendGrantInfoMsg <br>
	 * 描述: 放款短消息发送. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月9日 下午9:35:27
	 * 
	 * @param creditUserInfo
	 * @param currentAmount
	 */
	public void cgbSendGrantInfoMsg(CreditUserInfo creditUserInfo, Double currentAmount) {

		try {
			// 发送短信消息提醒.
			logger.info("fn：cgbSendGrantInfoMsg，短信，【客户：" + creditUserInfo.getId() + "】");
			logger.info("fn：cgbSendGrantInfoMsg，付息-短信-START，【客户：" + creditUserInfo.getPhone() + "】");
			String result = sendSmsService.directSendSMS(creditUserInfo.getPhone(), "到账通知：您今日收到放款金额共" + NumberUtils.scaleDoubleStr(currentAmount) + "元。");
			logger.info("fn：cgbSendGrantInfoMsg，付息，【短信发送结果：" + result + "】");
			logger.info("fn：cgbSendGrantInfoMsg，付息-短信-END，【客户：" + creditUserInfo.getPhone() + "】");
		} catch (Exception e) {
			logger.info("fn：cgbSendGrantInfoMsg，Exception：微信或者短信发送失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: cgbSendBidCancelMsg <br>
	 * 描述: 流标-发送短消息. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年7月31日 下午3:11:21
	 * 
	 * @param invest
	 */
	public void cgbSendBidCancelMsg(WloanTermInvest invest) {

		try {
			WloanTermProject wloanTermProject = invest.getWloanTermProject();
			UserInfo userInfo = invest.getUserInfo();
			StringBuffer msg = new StringBuffer();
			msg.append("尊敬的用户，您");
			msg.append(DateUtils.formatDate(invest.getBeginDate(), "yyyy年MM月dd日 HH:mm:ss"));
			msg.append("投资的");
			msg.append(wloanTermProject.getName());
			msg.append("项目");
			msg.append(NumberUtils.scaleDoubleStr(invest.getAmount()));
			msg.append("元，因募集期内未满标，导致此标的进行流标退款，请您知晓。您可以前往中投摩根APP或者官网再次进行出借。");
			// 发送短信消息提醒.
			logger.info("fn：cgbSendBidCancelMsg，短信，【客户：" + userInfo.getId() + "】");
			logger.info("fn：cgbSendBidCancelMsg，付息-短信-START，【客户：" + userInfo.getName() + "】");
			String result = sendSmsService.directSendSMS(userInfo.getName(), msg.toString());
			logger.info("fn：cgbSendBidCancelMsg，付息，【短信发送结果：" + result + "】");
			logger.info("fn：cgbSendBidCancelMsg，付息-短信-END，【客户：" + userInfo.getName() + "】");
		} catch (Exception e) {
			logger.info("fn：cgbSendBidCancelMsg，Exception：短信发送失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: cgbSendRepayInfoMsg <br>
	 * 描述: 存管保，还款，发送微信及短信和站内消息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月15日 下午12:47:15
	 * 
	 * @param wloanTermUserPlan
	 *            客户还款计划详情
	 * @param type
	 *            1：付息，2：还本
	 */
	public void cgbSendRepayInfoMsg(WloanTermUserPlan wloanTermUserPlan, String type) {

		try {
			WloanTermProject project = wloanTermUserPlan.getWloanTermProject();

			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(REPAY_INFO_URL);
			temp.setTemplate_id(WeixinUtil.weixinBackMoneyModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();

			// 模版消息封装-客户还款提示语.
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			if (IS_REPAY_TYPE_1.equals(type)) { // 付息.
				firstbasetempBaseTemplete.setValue("您好，您的出借项目还息啦！");
			} else if (IS_REPAY_TYPE_2.equals(type)) { // 还本.
				firstbasetempBaseTemplete.setValue("您好，您的出借项目还本啦！");
			}
			data.put("first", firstbasetempBaseTemplete);

			// 模版消息封装-还款的项目名称及编号.
			StringBuffer projectName = new StringBuffer();
			if (project != null) {
				projectName.append(project.getName()).append("[").append(project.getSn()).append("]");
			} else {
				projectName.append("");
			}
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(projectName.toString());
			data.put("keyword1", wloannametemp);

			// 模版消息封装-还款金额.
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			if (IS_REPAY_TYPE_1.equals(type)) { // 付息.
				if (wloanTermUserPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2)) { // 付息.
					// 付息总额.
					Double repayAmount = wloanTermUserPlan.getInterest();
					wloanratetemp.setValue(String.format("%.2f", repayAmount));
				} else if (wloanTermUserPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1)) { // 还本付息.
					// 还本付息总额.
					Double repayAmount = wloanTermUserPlan.getInterest();
					// 投资金额.
					Double investAmount = wloanTermUserPlan.getWloanTermInvest().getAmount();
					// 还息.
					Double repayInterest = repayAmount - investAmount;
					wloanratetemp.setValue(String.format("%.2f", repayInterest));
				}
			} else if (IS_REPAY_TYPE_2.equals(type)) { // 还本.
				// 投资金额.
				Double investAmount = wloanTermUserPlan.getWloanTermInvest().getAmount();
				wloanratetemp.setValue(String.format("%.2f", investAmount));
			}
			data.put("keyword2", wloanratetemp);

			// 模版消息封装-剩余还款金额.
			Double waitmoney = wloanTermUserPlanDao.getWaitRepayMoney(wloanTermUserPlan);
			BaseTemplete wloandatetemp = new BaseTemplete();
			wloandatetemp.setColor("#173177");
			wloandatetemp.setValue(String.format("%.2f", waitmoney));
			data.put("keyword3", wloandatetemp);

			// 模版消息封装-备注.
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("还款已到您的账户，请注意查收！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户，如果绑定，则发送微信模板消息.
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(wloanTermUserPlan.getUserInfo().getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);
			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				if (IS_REPAY_TYPE_1.equals(type)) { // 付息.
					logger.info("fn：cgbSendRepayInfoMsg，付息-微信-START，【客户：" + weixinUser.getUserId() + "】");
					temp.setTouser(weixinUser.getOpenId());
					String outputStr = JSON.toJSONString(temp);
					String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
					com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
					logger.info("fn：cgbSendRepayInfoMsg，付息，【微信发送结果：" + result + "】");
					logger.info("fn：cgbSendRepayInfoMsg，付息-微信-END，【客户：" + weixinUser.getUserId() + "】");
				} else if (IS_REPAY_TYPE_2.equals(type)) { // 还本.
					logger.info("fn：cgbSendRepayInfoMsg，还本-微信-START，【客户：" + weixinUser.getUserId() + "】");
					temp.setTouser(weixinUser.getOpenId());
					String outputStr = JSON.toJSONString(temp);
					String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
					com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
					logger.info("fn：cgbSendRepayInfoMsg，还本，【微信发送结果：" + result + "】");
					logger.info("fn：cgbSendRepayInfoMsg，还本-微信-END，【客户：" + weixinUser.getUserId() + "】");
				}
			}

			// 发送短信消息提醒.
			logger.info("fn：cgbSendRepayInfoMsg，短信，【客户：" + wloanTermUserPlan.getUserInfo().getId() + "】");
			if (wloanTermUserPlan.getUserInfo().getId() != null) {
				UserInfo userInfo = userInfoDao.get(wloanTermUserPlan.getUserInfo().getId());
				if (null == userInfo) {
					userInfo = userInfoDao.getCgb(wloanTermUserPlan.getUserInfo().getId());
				}
				if (userInfo != null) {
					if (IS_REPAY_TYPE_1.equals(type)) { // 付息.
						logger.info("fn：cgbSendRepayInfoMsg，付息-短信-START，【客户：" + userInfo.getName() + "】");
						if (wloanTermUserPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2)) { // 付息.
							// 付息总额.
							Double repayAmount = wloanTermUserPlan.getInterest();
							wloanratetemp.setValue(String.format("%.2f", repayAmount));
							String result = "受监管要求，现取消除验证码之外的所有短信";
//							String result = sendSmsService.directSendSMS(userInfo.getName(), "到账通知：您今日收到出借利息共" + NumberUtils.scaleDoubleStr(repayAmount) + "元。");
							logger.info("fn：cgbSendRepayInfoMsg，付息，【短信发送结果：" + result + "】");
							logger.info("fn：cgbSendRepayInfoMsg，付息-短信-END，【客户：" + userInfo.getName() + "】");
							// 站内消息.
							StationLetter entity = new StationLetter();
							entity.setId(IdGen.uuid());
							entity.setUserId(userInfo.getId());
							entity.setLetterType(StationLettersService.LETTER_TYPE_REPAY);
							entity.setTitle("【中投摩根】到账通知：");
							entity.setBody("您今日收到出借利息共" + NumberUtils.scaleDoubleStr(repayAmount) + "元。");
							entity.setState(StationLettersService.LETTER_STATE_UNREAD);
							entity.setSendTime(new Date());
							int stationLetterFlag = stationLettersService.insertStationLetter(entity);
							if (stationLetterFlag == 1) {
								logger.info("fn：cgbSendRepayInfoMsg，站内消息发送成功！");
							} else {
								logger.info("fn：cgbSendRepayInfoMsg，站内消息发送失败！");
							}
						} else if (wloanTermUserPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1)) { // 还本付息.
							// 还本付息总额.
							Double repayAmount = wloanTermUserPlan.getInterest();
							// 投资金额.
							Double investAmount = wloanTermUserPlan.getWloanTermInvest().getAmount();
							// 还息.
							Double repayInterest = repayAmount - investAmount;
							wloanratetemp.setValue(String.format("%.2f", repayInterest));
							String result = "受监管要求，现取消除验证码之外的所有短信";
//							String result = sendSmsService.directSendSMS(userInfo.getName(), "到账通知：您今日收到出借利息共" + NumberUtils.scaleDoubleStr(repayInterest) + "元。");
							logger.info("fn：cgbSendRepayInfoMsg，付息，【短信发送结果：" + result + "】");
							logger.info("fn：cgbSendRepayInfoMsg，付息-短信-END，【客户：" + userInfo.getName() + "】");
							// 站内消息.
							StationLetter entity = new StationLetter();
							entity.setId(IdGen.uuid());
							entity.setUserId(userInfo.getId());
							entity.setLetterType(StationLettersService.LETTER_TYPE_REPAY);
							entity.setTitle("【中投摩根】到账通知：");
							entity.setBody("您今日收到出借利息共" + NumberUtils.scaleDoubleStr(repayInterest) + "元。");
							entity.setState(StationLettersService.LETTER_STATE_UNREAD);
							entity.setSendTime(new Date());
							int stationLetterFlag = stationLettersService.insertStationLetter(entity);
							if (stationLetterFlag == 1) {
								logger.info("fn：cgbSendRepayInfoMsg，站内消息发送成功！");
							} else {
								logger.info("fn：cgbSendRepayInfoMsg，站内消息发送失败！");
							}
						}
					} else if (IS_REPAY_TYPE_2.equals(type)) { // 还本.
						logger.info("fn：cgbSendRepayInfoMsg，还本-短信-START，【客户：" + userInfo.getName() + "】");
						// 投资金额.
						Double investAmount = wloanTermUserPlan.getWloanTermInvest().getAmount();
						String result = "受监管要求，现取消除验证码之外的所有短信";
//						String result = sendSmsService.directSendSMS(userInfo.getName(), "到账通知:您今日收到出借本金共" + NumberUtils.scaleDoubleStr(investAmount) + "元。");
						logger.info("fn：cgbSendRepayInfoMsg，还本，【短信发送结果：" + result + "】");
						logger.info("fn：cgbSendRepayInfoMsg，还本-短信-END，【客户：" + userInfo.getName() + "】");
						// 站内消息.
						StationLetter entity = new StationLetter();
						entity.setId(IdGen.uuid());
						entity.setUserId(userInfo.getId());
						entity.setLetterType(StationLettersService.LETTER_TYPE_REPAY);
						entity.setTitle("【中投摩根】到账通知：");
						entity.setBody("您今日收到出借本金共" + NumberUtils.scaleDoubleStr(investAmount) + "元。");
						entity.setState(StationLettersService.LETTER_STATE_UNREAD);
						entity.setSendTime(new Date());
						int stationLetterFlag = stationLettersService.insertStationLetter(entity);
						if (stationLetterFlag == 1) {
							logger.info("fn：cgbSendRepayInfoMsg，站内消息发送成功！");
						} else {
							logger.info("fn：cgbSendRepayInfoMsg，站内消息发送失败！");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("Exception：微信或者短信发送失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 还款发送模板消息
	 */
	public void sendRepayInfoMsg(WloanTermUserPlan wloanTermUserPlan) {

		try {
			WloanTermProject loanProject = wloanTermUserPlan.getWloanTermProject();
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(REPAY_INFO_URL);
			temp.setTemplate_id(WeixinUtil.weixinBackMoneyModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// 剩余还款金额 waitmoney
			Double waitmoney = 0.00d;

			waitmoney = wloanTermUserPlanDao.getWaitRepayMoney(wloanTermUserPlan);

			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			if (wloanTermUserPlan.getPrincipal().equals("2")) {
				firstbasetempBaseTemplete.setValue("您好，您的出借项目还息啦！");
			} else {
				firstbasetempBaseTemplete.setValue("您好，您的出借项目还本付息啦！");
			}
			data.put("first", firstbasetempBaseTemplete);
			// 项目名称
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(loanProject.getName());
			data.put("keyword1", wloannametemp);
			// 还款金额
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue(String.format("%.2f", wloanTermUserPlan.getInterest()));
			data.put("keyword2", wloanratetemp);
			// 剩余还款金额
			BaseTemplete wloandatetemp = new BaseTemplete();
			wloandatetemp.setColor("#173177");
			wloandatetemp.setValue(String.format("%.2f", waitmoney));
			data.put("keyword3", wloandatetemp);

			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("还款已到您的账户，请注意查收！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(wloanTermUserPlan.getUserInfo().getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);

			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【还款发送微信开始】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【还款发送微信结束】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
			}

			// 发送短信消息提醒
			System.out.println("发送还款短信 的用户id 为" + wloanTermUserPlan.getUserInfo().getId());
			if (wloanTermUserPlan.getUserInfo().getId() != null) {// 如果没有微信则发送短信
				UserInfo userInfo = userInfoDao.get(wloanTermUserPlan.getUserInfo().getId());
				if (userInfo == null) {
					userInfo = userInfoDao.getCgb(wloanTermUserPlan.getUserInfo().getId());
				}
				if (userInfo != null) {
					logger.info("【发送短信开始】【用户：" + userInfo.getName() + "】--------------------------------------------------");
					if (wloanTermUserPlan.getPrincipal().equals("2")) {
						String result = sendSmsService.directSendSMS(userInfo.getName(), "到账通知:您今日收到出借利息共" + wloanTermUserPlan.getInterest() + "元。");
						logger.info("【result】" + result);
					} else {
						String result = sendSmsService.directSendSMS(userInfo.getName(), "到账通知:您今日收到出借本息共" + wloanTermUserPlan.getInterest() + "元。");
						logger.info("【result】" + result);
					}
					logger.info("【发送短信结束】【用户：" + userInfo.getName() + "】--------------------------------------------------");
				}
			}
		} catch (Exception e) {
			logger.info("微信或者短信发送失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * methods: sendCancelInvestMsg <br>
	 * description: 取消投资. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年3月6日 下午2:07:05
	 * 
	 * @param invest
	 * @param project
	 */
	public void sendCancelInvestMsg(WloanTermInvest invest, WloanTermProject project) {

		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(INVEST_INFO_URL);// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinBidSuccessModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue("未出借成功，现已将资金退回您的账户。");
			data.put("first", firstbasetempBaseTemplete);
			// 项目名称
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(project.getName());
			data.put("keyword1", wloannametemp);
			// 年化收益
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue(project.getAnnualRate() + "%");
			data.put("keyword2", wloanratetemp);
			// 项目期限
			BaseTemplete wloandatetemp = new BaseTemplete();
			wloandatetemp.setColor("#173177");
			wloandatetemp.setValue(project.getSpan() + "天");
			data.put("keyword3", wloandatetemp);
			// 投资金额
			BaseTemplete wloanpaytemp = new BaseTemplete();
			wloanpaytemp.setColor("#173177");
			wloanpaytemp.setValue(invest.getAmount() + "");
			data.put("keyword4", wloanpaytemp);
			// 投资金额所得利息
			BaseTemplete bidinterest = new BaseTemplete();
			bidinterest.setColor("#173177");
			bidinterest.setValue(invest.getInterest() + "");
			data.put("keyword5", bidinterest);
			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("如您有任何问题，欢迎来电询问。客服电话：400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(invest.getUserInfo().getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);

			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户投资成功发送微信开始】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户投资成功发送微信结束】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
			}

			if (invest.getUserInfo().getId() != null) {// 如果没有微信则发送短信
				UserInfo userInfo = userInfoDao.get(invest.getUserInfo().getId());
				if (userInfo == null) {
					userInfo = userInfoDao.getCgb(invest.getUserInfo().getId());
				}
				if (userInfo != null) {
					logger.info("【用户投资成功开始发送短信】------------------------------------------------");
					// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sendSmsService.directSendSMS(userInfo.getName(), "尊敬的用户，您出借的" + project.getName() + "项目，出借资金" + invest.getAmount() + "元，未出借成功！现已将资金退回您的账户。");
					logger.info("【用户投资成功结束发送微信】------------------------------------------------");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用户投资成功发送消息
	 */
	public void sendInvestInfoMsg(WloanTermInvest invest, WloanTermProject project) {

		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(INVEST_INFO_URL);// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinBidSuccessModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue("您已成功出借，项目满标后开始计息。");
			data.put("first", firstbasetempBaseTemplete);
			// 项目名称
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(project.getName());
			data.put("keyword1", wloannametemp);
			// 年化收益
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue(project.getAnnualRate() + "%");
			data.put("keyword2", wloanratetemp);
			// 项目期限
			BaseTemplete wloandatetemp = new BaseTemplete();
			wloandatetemp.setColor("#173177");
			wloandatetemp.setValue(project.getSpan() + "天");
			data.put("keyword3", wloandatetemp);
			// 投资金额
			BaseTemplete wloanpaytemp = new BaseTemplete();
			wloanpaytemp.setColor("#173177");
			wloanpaytemp.setValue(invest.getAmount() + "");
			data.put("keyword4", wloanpaytemp);
			// 投资金额所得利息
			BaseTemplete bidinterest = new BaseTemplete();
			bidinterest.setColor("#173177");
			bidinterest.setValue(invest.getInterest() + "");
			data.put("keyword5", bidinterest);
			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("如您有任何问题，欢迎来电询问。客服电话：400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(invest.getUserInfo().getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);

			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户投资成功发送微信开始】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户投资成功发送微信结束】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
			}

			if (invest.getUserInfo().getId() != null) {// 如果没有微信则发送短信
				UserInfo userInfo = userInfoDao.get(invest.getUserInfo().getId());
				if (userInfo == null) {
					userInfo = userInfoDao.getCgb(invest.getUserInfo().getId());
				}
				if (userInfo != null) {
					logger.info("【用户投资成功开始发送短信】------------------------------------------------");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String result = "受监管要求，现取消除验证码之外的所有短信";
					// sendSmsService.directSendSMS(userInfo.getName(), "尊敬的用户，中投摩根提醒您，您于" + sdf.format(invest.getBeginDate()) + "成功向" + project.getName() + "项目，出借资金" + NumberUtils.scaleDoubleStr(invest.getAmount()) + "元。");
					logger.info("【用户投资成功结束发送微信】---" + result + "---" + sdf.format(invest.getBeginDate()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 充值短信、微信提醒
	 * 
	 * @param recharge
	 */
	public void sendUserRechargeMsg(UserRecharge userRecharge) {

		// 充值成功发送微信或者短信提醒
		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(RECHARGE_INFO_URL);// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinNetSaveModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue("尊敬的用户，您已成功充值。");
			data.put("first", firstbasetempBaseTemplete);
			UserInfo userInfo = userInfoDao.get(userRecharge.getUserId());
			if (userInfo == null) {
				userInfo = userInfoDao.getCgb(userRecharge.getUserId());
			}
			CgbUserBankCard bankCard = cgbUserBankCardService.findByUserId(userRecharge.getUserId());
			// 账户号
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(Util.hideString(userInfo.getName(), 3, 4));
			data.put("keyword1", wloannametemp);
			// 充值金额
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue(new DecimalFormat("0.00").format(userRecharge.getAmount()));
			data.put("keyword2", wloanratetemp);
			// 充值渠道
			BaseTemplete wloandatetemp = new BaseTemplete();
			wloandatetemp.setColor("#173177");
			wloandatetemp.setValue("网上充值");
			data.put("keyword3", wloandatetemp);
			// 充值银行
			BaseTemplete wloanpaytemp = new BaseTemplete();
			wloanpaytemp.setColor("#173177");
			wloanpaytemp.setValue(bankCard.getBankName());
			data.put("keyword4", wloanpaytemp);
			// 充值状态
			BaseTemplete bidinterest = new BaseTemplete();
			bidinterest.setColor("#173177");
			bidinterest.setValue("充值成功");
			data.put("keyword5", bidinterest);
			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("如您有任何问题，欢迎来电询问。客服电话：400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(userRecharge.getUserId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);

			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户充值成功发送微信开始】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户充值成功发送微信结束】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
			}

			if (userInfo != null) {
				logger.info("【用户充值成功开始发送短信】------------------------------------------------");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String result = "受监管要求，现取消除验证码之外的所有短信";
//				sendSmsService.directSendSMS(userInfo.getName(), "尊敬的用户，中投摩根提醒您：您的账户" + Util.hideString(userInfo.getName(), 3, 4) + "于" + sdf.format(userRecharge.getBeginDate()) + "成功充值" + new DecimalFormat("0.00").format(userRecharge.getAmount()) + "元！");
				logger.info("【用户充值成功结束发送微信】---" + result + "---" + sdf.format(userRecharge.getBeginDate()));
			}
		} catch (Exception e) {
			logger.info("【微信提醒接口失败】");
			e.printStackTrace();
		}
	}

	/**
	 * 提现短信、微信提醒
	 * 
	 * @param recharge
	 */
	public void sendCashSuccessMsg(UserCash userCash) {

		// 提现成功发送微信或者短信提醒
		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(CASH_INFO_URL);// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinCashModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue("尊敬的用户，您的提现操作已成功。");
			data.put("first", firstbasetempBaseTemplete);
			UserInfo userInfo = userInfoDao.get(userCash.getUserId());
			if (userInfo == null) {
				userInfo = userInfoDao.getCgb(userCash.getUserId());
			}
			// 银行卡号
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(Util.hideString(userCash.getBankAccount(), 4, 6));
			data.put("keyword1", wloannametemp);
			// 提现金额
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue(new DecimalFormat("0.00").format(userCash.getAmount()));
			data.put("keyword2", wloanratetemp);
			// 提现时间
			BaseTemplete wloandatetemp = new BaseTemplete();
			wloandatetemp.setColor("#173177");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			wloandatetemp.setValue(sdf.format(userCash.getBeginDate()));
			data.put("keyword3", wloandatetemp);
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("您的提现申请已提交，我们已把转账申请提交第三方处理，请您注意查收！如有到账问题请联系客服400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(userCash.getUserId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);
			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户提现成功发送微信开始】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户提现成功发送微信结束】【用户：" + weixinUser.getUserId() + "】-----------------------------------------------");
			}

			if (userInfo != null) {
				logger.info("【用户充值成功开始发送短信】------------------------------------------------");
				String result = "受监管要求，现取消除验证码之外的所有短信";
//				sendSmsService.directSendSMS(userInfo.getName(), "提现通知：您" + new DecimalFormat("0.00").format(userCash.getAmount()) + "元提现申请已转交第三方处理，请您注意查收！如有到账问题请联系客服400-666-9068！");
				logger.info("【用户充值成功结束发送微信】---" + result + "---");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 密码重置提醒
	 * 
	 * @param pwdType
	 * @param userInfo
	 */
	public void sendUpdatePwdMsg(String pwdType, UserInfo userInfo) {

		// 提现成功发送微信或者短信提醒
		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl(CASH_INFO_URL);// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinCashModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue(pwdType + "密码重置通知");
			data.put("first", firstbasetempBaseTemplete);
			// 密码类型
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(pwdType + "密码");
			data.put("keyword1", wloannametemp);
			// 设置状态
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue("设置成功");
			data.put("keyword2", wloanratetemp);
			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("如您有任何问题，欢迎来电询问。客服电话：400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(userInfo.getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);
			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户修改" + pwdType + "密码成功发送微信开始】【用户：" + userInfo.getName() + "】-----------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户修改" + pwdType + "密码成功发送微信结束】【用户：" + userInfo.getName() + "】-----------------------------------------------");
			}

			if (userInfo != null) {
				logger.info("【用户修改" + pwdType + "密码成功开始发送短信】------------------------------------------------");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
				sendSmsService.directSendSMS(userInfo.getName(), "密码重置提醒：您的账户" + Util.hideString(userInfo.getName(), 3, 4) + "于" + sdf.format(new Date()) + "修改了" + pwdType + "密码，如有疑问请联系客服400-666-9068！");
				logger.info("【用户修改" + pwdType + "密码成功结束发送微信】------------------------------------------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绑定微信提醒
	 * 
	 * @param name
	 * @param bindType
	 *            (1-绑定，2-解绑)
	 */
	public void sendBindWeixinMsg(UserInfo userInfo) {

		// 绑定、解绑微信成功发送微信或者短信提醒
		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl("");// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinBandSuccessModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue("您好！您的微信号与平台账号绑定成功");
			data.put("first", firstbasetempBaseTemplete);
			// 用户名
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(Util.hideString(userInfo.getName(), 3, 4));
			data.put("keyword1", wloannametemp);
			// 用户类型
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue("个人用户");
			data.put("keyword2", wloanratetemp);
			// 绑定时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			BaseTemplete bindTime = new BaseTemplete();
			bindTime.setColor("#173177");
			bindTime.setValue(sdf.format(new Date()));
			data.put("keyword3", bindTime);
			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("如您有任何问题，欢迎来电询问。客服电话：400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(userInfo.getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);
			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户" + userInfo.getName() + "绑定微信成功,开始发送微信模板消息】------------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户" + userInfo.getName() + "绑定微信成功,结束发送微信模板消息】------------------------------------------------");
			}

			if (userInfo != null) {
				logger.info("【用户" + userInfo.getName() + "绑定微信成功,开始发送短信】------------------------------------------------");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
				sendSmsService.directSendSMS(userInfo.getName(), "微信绑定提醒：您的账户" + Util.hideString(userInfo.getName(), 3, 4) + "于" + sdf1.format(new Date()) + "绑定了微信号，如有疑问请联系客服400-666-9068！");
				logger.info("【用户" + userInfo.getName() + "绑定微信成功,结束发送短信】------------------------------------------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解绑微信提醒
	 * 
	 * @param name
	 */
	public void sendUnBindWeixinMsg(UserInfo userInfo) {

		// 绑定、解绑微信成功发送微信或者短信提醒
		try {
			TempleteMsg temp = new TempleteMsg();
			temp.setUrl("");// 无跳转
			temp.setTemplate_id(WeixinUtil.weixinUnBandSuccessModelId);
			Map<String, BaseTemplete> data = new HashMap<String, BaseTemplete>();
			// first封装
			BaseTemplete firstbasetempBaseTemplete = new BaseTemplete();
			firstbasetempBaseTemplete.setColor("#173177");
			firstbasetempBaseTemplete.setValue("您好！您的微信号与平台账号解绑成功");
			data.put("first", firstbasetempBaseTemplete);
			// 用户名
			BaseTemplete wloannametemp = new BaseTemplete();
			wloannametemp.setColor("#173177");
			wloannametemp.setValue(Util.hideString(userInfo.getName(), 3, 4));
			data.put("keyword1", wloannametemp);
			// 用户类型
			BaseTemplete wloanratetemp = new BaseTemplete();
			wloanratetemp.setColor("#173177");
			wloanratetemp.setValue("解除绑定");
			data.put("keyword2", wloanratetemp);
			// 绑定时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			BaseTemplete bindTime = new BaseTemplete();
			bindTime.setColor("#173177");
			bindTime.setValue(sdf.format(new Date()));
			data.put("keyword3", bindTime);
			// 备注
			BaseTemplete remark = new BaseTemplete();
			remark.setColor("#173177");
			remark.setValue("如您有任何问题，欢迎来电询问。客服电话：400-666-9068！");
			data.put("remark", remark);
			temp.setData(data);

			// 查询用户是否绑定微信账户,如果绑定,则发送微信模板消息
			ZtmgWechatRelation weixinUser = new ZtmgWechatRelation();
			weixinUser.setUserId(userInfo.getId());
			List<ZtmgWechatRelation> list = ztmgWechatRelationService.findList(weixinUser);
			if (list != null && list.size() > 0) {
				weixinUser = list.get(0);
				logger.info("【用户" + userInfo.getName() + "解绑微信成功,开始发送微信模板消息】------------------------------------------------");
				temp.setTouser(weixinUser.getOpenId());
				String outputStr = JSON.toJSONString(temp);
				String requestUrl = WeixinUtil.send_templete_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken());
				com.alibaba.fastjson.JSONObject result = WeixinUtil.httpRequest(requestUrl, "POST", outputStr);
				logger.info("【微信发送结果】" + result);
				logger.info("【用户" + userInfo.getName() + "解绑微信成功,结束发送微信模板消息】------------------------------------------------");
			}

			if (userInfo != null) {
				logger.info("【用户" + userInfo.getName() + "解绑微信成功,开始发送短信】------------------------------------------------");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
				sendSmsService.directSendSMS(userInfo.getName(), "微信解绑提醒：您的账户" + Util.hideString(userInfo.getName(), 3, 4) + "于" + sdf1.format(new Date()) + "解除绑定了微信号，如有疑问请联系客服400-666-9068！");
				logger.info("【用户" + userInfo.getName() + "解绑微信成功,结束发送短信】------------------------------------------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信返现短信通知
	 * 尊敬的用户，中投摩根提醒您：您的账户138XXXXX于2016-01-01收到理财利息500元！
	 * 只针对 手机号为 18606314538 的用户
	 * 
	 * @param name
	 */
	public void sendWecatReturnCashInfo(String iphone, Double amount) {

		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sendSmsService.directSendSMS(iphone, "尊敬的用户，中投摩根提醒您：您的账户 " + Util.hideString(iphone, 3, 4) + "于" + sdf1.format(new Date()) + "收到理财利息" + amount + "元");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用户出借失败发送消息
	 * 
	 * @param invest
	 * @param project
	 */
	public void sendInvestFailMsg(WloanTermInvest invest, WloanTermProject project) {

		try {
			if (invest.getUserInfo().getId() != null) {// 发送短信
				UserInfo userInfo = userInfoDao.get(invest.getUserInfo().getId());
				if (userInfo == null) {
					userInfo = userInfoDao.getCgb(invest.getUserInfo().getId());
				}
				if (userInfo != null) {
					logger.info("【用户投资失败开始发送短信】------------------------------------------------");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sendSmsService.directSendSMS(userInfo.getName(), "尊敬的用户，中投摩根提醒您：因 " + project.getName() + "项目已满标,导致您于" + sdf.format(invest.getBeginDate()) + "出借" + invest.getAmount() + "元失败,请您知晓!");
					logger.info("【用户投资失败结束发送微信】------------------------------------------------");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用户投资成功发送消息----满标时短信通知
	 */
	public void newSendInvestInfoMsg(WloanTermInvest invest, WloanTermProject project) {

		try {
			if (invest.getUserInfo().getId() != null) {// 如果没有微信则发送短信
				UserInfo userInfo = userInfoDao.get(invest.getUserInfo().getId());
				if (userInfo == null) {
					userInfo = userInfoDao.getCgb(invest.getUserInfo().getId());
				}
				if (userInfo != null) {
					logger.info("【项目满标开始发送短信】------------------------------------------------");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String result = "受监管要求，现取消除验证码之外的所有短信";
//					sendSmsService.directSendSMS(userInfo.getName(), "尊敬的用户，中投摩根提醒您：您于" + sdf.format(invest.getBeginDate()) + "出借" + project.getName() + "项目已经满标，并开始计息！");
					logger.info("【项目满标结束发送短信】---" + result + "---" + sdf.format(invest.getBeginDate()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送注册成功短信
	 * 
	 * @param iphone
	 */
	public void sendRegistMsg(String iphone) {

		try {
			sendSmsService.directSendSMS(iphone, "恭喜您注册成功，现送您【600元抵扣券】，可登录APP在【我的-优惠券】中查看");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: cgbSendUserInfoMsg <br>
	 * 描述: 抵用券放款消息发送. <br>
	 * 
	 * @param userInfo
	 * @param amount
	 */
	public void cgbSendUserInfoMsg(UserInfo userInfo, Double amount, String proName, String proSn) {

		try {
			// 发送短信消息提醒.
			logger.info("fn：cgbSendUserInfoMsg，短信，【客户：" + userInfo.getId() + "】");
			logger.info("fn：cgbSendUserInfoMsg，抵用券放款-短信-START，【客户：" + userInfo.getName() + "】");
			String result = sendSmsService.directSendSMS(userInfo.getName(), "到账通知：您出借的" + proName + "（" + proSn + "）项目已经满标，收到出借红包返利共" + NumberUtils.scaleDoubleStr(amount) + "元。");
			logger.info("fn：cgbSendUserInfoMsg，抵用券放款，【短信发送结果：" + result + "】");
			logger.info("fn：cgbSendUserInfoMsg，抵用券放款-短信-END，【客户：" + userInfo.getName() + "】");
		} catch (Exception e) {
			logger.info("fn：cgbSendUserInfoMsg，Exception：微信或者短信发送失败！");
			e.printStackTrace();
		}
	}

}
