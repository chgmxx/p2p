package com.power.platform.user;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.dao.ZtmgPartnerPlatformDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.sys.type.UserStateType;
import com.power.platform.sys.type.UserType;
import com.power.platform.userinfo.dao.RegistUserDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.dao.UserLogDao;
import com.power.platform.userinfo.dao.UserLoginDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Service("commonRestService")
@Produces(MediaType.APPLICATION_JSON)
public class CommonRestService {

	private static final Logger LOG = LoggerFactory.getLogger(CommonRestService.class);
	@Autowired
	private RegistUserDao registUserDao;
	@Autowired
	private UserAccountInfoDao userAccountInfoDao;
	@Autowired
	private UserLoginDao userLoginDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private LevelDistributionDao levelDistributionDao;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Resource
	private ZtmgPartnerPlatformDao ztmgPartnerPlatformDao;
	@Resource
	private UserBounsPointDao userBounsPointDao;
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Resource
	private UserBounsHistoryService userBounsHistoryService;
	@Resource
	private CgbUserAccountService cgbUserAccountService;
	@Resource
	private UserVouchersHistoryDao userVouchersHistoryDao;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Resource
	private StationLettersService stationLettersService;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Autowired
	private UserLogDao userLogDao;
	/*
	 * 银行存管
	 */
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;

	/**
	 * 客户类型，1：普通客户.
	 */
	public static final String CUSTOMER_TYPE_1 = "1";

	/**
	 * 客户类型，2：合伙人.
	 */
	public static final String CUSTOMER_TYPE_2 = "2";

	/**
	 * 用户注册
	 * 
	 * @param from
	 * @param name
	 * @param pwd
	 * @return
	 * @throws ParseException
	 */
	@POST
	@Path("/regist")
	public Map<String, Object> regist(@FormParam("from") String from, @FormParam("name") String name, @FormParam("pwd") String pwd, @FormParam("userNo") String userNo, @FormParam("refer") String refer, @FormParam("recommendMobilePhone") String recommendMobilePhone, @Context HttpServletRequest servletrequest) throws ParseException {

		// 推荐人手机号码，推荐人ID.
		String recommendId = "";
		// 邀请链接，推荐人ID.
		String refereesId = "";
		// IP.
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(name) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		UserInfo user = new UserInfo();
		user.setName(name);
		List<UserInfo> returnuser = registUserDao.findList(user);
		if (returnuser != null && returnuser.size() > 0) {
			result.put("state", "1");
			result.put("message", "该用户已注册");
			return result;
		}

		// 注册客户账号信息.

		// 设定客户推荐人及推荐人手机号
		UserInfo userInfo = new UserInfo();
		if (recommendMobilePhone != null && recommendMobilePhone.length() > 0) {
			UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendMobilePhone);
			if (null != recommendUserInfo) {
				recommendId = recommendUserInfo.getId();
				userInfo.setRecommendUserId(recommendUserInfo.getId());
				userInfo.setRecommendUserPhone(recommendMobilePhone);

			}
		}
		if (refer != null && refer.length() > 0 && !refer.equals("null")) {
			ZtmgPartnerPlatform entity = ztmgPartnerPlatformDao.getZtmgPartnerPlatformByPlatformCode(refer);
			if (entity != null) {
				userInfo.setRecommendUserId(entity.getId());
			}
		}

		// 推荐人优先级：recommendMobilePhone>userNo || refer
		// if(recommendMobilePhone!=null && recommendMobilePhone.length()>0){
		// UserInfo recommendUserInfo =
		// userInfoDao.getUserInfoByPhone(recommendMobilePhone);
		// if (null != recommendUserInfo) {
		// userInfo.setRecommendUserId(recommendUserInfo.getId());
		// } else if(refer != null && refer.length() > 0 &&
		// !refer.equals("null")){
		// ZtmgPartnerPlatform entity =
		// ztmgPartnerPlatformDao.getZtmgPartnerPlatformByPlatformCode(refer);
		// if (entity != null) {
		// userInfo.setRecommendUserId(entity.getId());
		// }
		// }else if(userNo != null && userNo.length() > 0){
		// UserInfo recommendUserInfo2 = userInfoDao.getUserInfoByPhone(userNo);
		// if (recommendUserInfo2 != null) {
		// userInfo.setRecommendUserId(recommendUserInfo2.getId());
		// }
		// }
		// }else{
		// if(refer != null && refer.length() > 0 && !refer.equals("null")){
		// ZtmgPartnerPlatform entity =
		// ztmgPartnerPlatformDao.getZtmgPartnerPlatformByPlatformCode(refer);
		// if (entity != null) {
		// userInfo.setRecommendUserId(entity.getId());
		// }
		// }else if(userNo != null && userNo.length() > 0){
		// UserInfo recommendUserInfo2 = userInfoDao.getUserInfoByPhone(userNo);
		// if (recommendUserInfo2 != null) {
		// userInfo.setRecommendUserId(recommendUserInfo2.getId());
		// }
		// }
		// }
		userInfo.setId(String.valueOf(IdGen.randomLong()));
		userInfo.setName(name);
		userInfo.setPwd(EncoderUtil.encrypt(pwd));
		userInfo.setRecomType(0);
		userInfo.setUserType(UserType.BID);// 投资用户
		userInfo.setState(UserStateType.NORMAL);
		userInfo.setRegisterFrom(StringUtils.toInteger(from));
		Date date = new Date();
		userInfo.setCreateDate(date);
		userInfo.setEmailChecked(UserInfo.BIND_EMAIL_NO);// 邮箱未验证
		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_NO);// 实名认证
		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);// 绑定银行卡
		userInfo.setLastLoginIp(ip);
		userInfo.setRegisterDate(date);
		userInfo.setLastLoginDate(date);
		userInfo.setAccountId(String.valueOf(IdGen.randomLong()));
		userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO); // 未开通银行存管
		int a = registUserDao.insert(userInfo);

		// 如果recommendUserId非空，则生成三级关系.

		if (userInfo.getRecommendUserPhone() != null && userInfo.getRecommendUserPhone().length() > 0) {
			addRelationship(recommendId, refereesId, userInfo.getId());
		}

		// 注册用户银行托管保账户
		CgbUserAccount userAccountInfo = new CgbUserAccount();
		if (a > 0) {
			System.out.println("用户注册插入客户表完成");
			userAccountInfo.setId(userInfo.getAccountId());
			userAccountInfo.setUserId(userInfo.getId());
			userAccountInfo.setTotalAmount(0d);
			userAccountInfo.setTotalInterest(0d);
			userAccountInfo.setAvailableAmount(0d);
			userAccountInfo.setFreezeAmount(0d);
			userAccountInfo.setRechargeAmount(0d);
			userAccountInfo.setRechargeCount(0);
			userAccountInfo.setCashAmount(0d);
			userAccountInfo.setCashCount(0);
			userAccountInfo.setCurrentAmount(0d);
			userAccountInfo.setRegularDuePrincipal(0d);
			userAccountInfo.setRegularDueInterest(0d);
			userAccountInfo.setRegularTotalAmount(0d);
			userAccountInfo.setRegularTotalInterest(0d);
			userAccountInfo.setCurrentTotalAmount(0d);
			userAccountInfo.setCurrentTotalInterest(0d);
			userAccountInfo.setCurrentYesterdayInterest(0d);
			userAccountInfo.setReguarYesterdayInterest(0d);
			userAccountInfo.setUserInfo(userInfo);
			// 同时生成客户账户
			cgbUserAccountDao.insert(userAccountInfo);
		}

		// 注册成功送500积分
		UserBounsPoint userBounsPoint1 = new UserBounsPoint();
		userBounsPoint1.setId(IdGen.uuid());
		userBounsPoint1.setUserId(userInfo.getId());
		userBounsPoint1.setScore(500);
		userBounsPoint1.setCreateDate(new Date());
		userBounsPoint1.setUpdateDate(new Date());
		userBounsPoint1.setUserInfo(userInfo);
		userBounsPointDao.insert(userBounsPoint1);
		// 添加账户积分历史明细
		UserBounsHistory userBounsHistory = new UserBounsHistory();
		Double userBouns = 500d;

		userBounsHistory.setId(IdGen.uuid());
		userBounsHistory.setUserId(userInfo.getId());
		userBounsHistory.setAmount(userBouns);
		userBounsHistory.setCreateDate(new Date());
		userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST);
		userBounsHistory.setTransId(userBounsPoint1.getId());
		userBounsHistoryService.insert(userBounsHistory);

		/**
		 * // 积分商城，用户邀请好友完成注册，推荐人获得100积分.
		 * // 推荐人ID.
		 * */
		if (recommendMobilePhone != null && recommendMobilePhone.length() > 0) {
			UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendMobilePhone);
			if (null != recommendUserInfo) {
				recommendId = recommendUserInfo.getId();
				UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(recommendId);
				userBounsPoint.setScore(userBounsPoint.getScore() + 100);
				userBounsPointDao.update(userBounsPoint);

				// 添加账户积分历史明细
				UserBounsHistory userBounsHistory1 = new UserBounsHistory();
				userBounsHistory1.setId(IdGen.uuid());
				userBounsHistory1.setUserId(userInfo.getId());
				userBounsHistory1.setAmount(100D);
				userBounsHistory1.setCreateDate(new Date());
				userBounsHistory1.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REQUEST);
				userBounsHistory1.setTransId(userBounsPoint.getId());
				userBounsHistoryService.insert(userBounsHistory1);
			}
		}

		// 美特好(660)活动期间发放抵用劵
		// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String beginDate = "2018-01-15 00:00:00";
		// String endDate = "2018-01-28 23:59:59";
		// Date now = new Date();
		// if(refer!=null){
		// if(refer.equals("008")){
		// if(now.before(sdf1.parse(endDate)) &&
		// now.after(sdf1.parse(beginDate))){
		// List<Double> voucherListBote = new ArrayList<Double>();
		// voucherListBote.add(100d);
		// voucherListBote.add(100d);
		// voucherListBote.add(100d);
		// voucherListBote.add(50d);
		// voucherListBote.add(50d);
		// voucherListBote.add(50d);
		// voucherListBote.add(50d);
		// voucherListBote.add(20d);
		// voucherListBote.add(20d);
		// voucherListBote.add(20d);
		// voucherListBote.add(20d);
		// voucherListBote.add(20d);
		// voucherListBote.add(10d);
		// voucherListBote.add(10d);
		// voucherListBote.add(10d);
		// voucherListBote.add(10d);
		// voucherListBote.add(10d);
		// voucherListBote.add(10d);
		// addVouchers(userInfo.getId(), voucherListBote);
		// }
		// }
		// }

		/*
		 * try {
		 * if(now.before( sdf1.parse(endDate)) &&
		 * now.after(sdf1.parse(beginDate))){
		 * List<Double> voucherListBote = new ArrayList<Double>();
		 * voucherListBote.add(100d);
		 * voucherListBote.add(100d);
		 * voucherListBote.add(100d);
		 * voucherListBote.add(100d);
		 * voucherListBote.add(100d);
		 * addVouchers(userInfo.getId(), voucherListBote);
		 * }
		 * } catch (ParseException e1) {
		 * // TODO Auto-generated catch block
		 * e1.printStackTrace();
		 * }
		 */

		// 发放抵用券600
		List<Double> voucherList = new ArrayList<Double>();
		// 300元抵用券
		voucherList.add(300d);
		voucherList.add(100d);
		voucherList.add(100d);
		addVouchers(userInfo.getId(), voucherList, "90,120,180,360");
		voucherList = new ArrayList<Double>();
		voucherList.add(50d);
		voucherList.add(10d);
		voucherList.add(20d);
		voucherList.add(20d);
		addVouchers(userInfo.getId(), voucherList, UserVouchersHistoryService.SPAN_1);

		// 缓存客户信息.
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		String token = EncoderUtil.encrypt(sdf.format(time) + userInfo.getId()).replace("+", "a");
		// 设置缓存
		JedisUtils.set(token, userInfo.getId(), 1200);
		result.put("state", "0");
		result.put("message", "注册成功");
		result.put("token", token);
		result.put("username", userInfo.getName());
		return result;
	}

	/**
	 * 用户登录
	 * 
	 * @param from
	 * @param mobile
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("/login")
	public Map<String, Object> login(@FormParam("from") String from, @FormParam("mobile") String mobile, @FormParam("pwd") String pwd, @Context HttpServletRequest servletrequest) {

		// 缓存中登录用户的token信息（key=userId，value=token）
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobile) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setName(mobile);
		userInfo.setPwd(EncoderUtil.encrypt(pwd));

		try {
			List<UserInfo> userList = userLoginDao.findList(userInfo);
			if (userList != null && userList.size() > 0) {
				UserInfo user = userList.get(0);
				if (Integer.valueOf(UserType.BID) == user.getUserType()) {
					// 用户状态正常
					if (user.getState() == UserStateType.NORMAL) {
						// 修改最后登录时间和登录IP
						user.setId(user.getId());
						user.setLastLoginDate(new Date());
						user.setLastLoginIp(ip);
						userLoginDao.update(user);

						// 生成token
						String token = EncoderUtil.encrypt(sdf.format(time) + user.getId()).replace("+", "a");

						Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
						// 系统没有登录用户（一般不会进该方法）
						if (cacheLoginedUser == null) {
							cacheLoginedUser = new HashMap<String, String>();
						}
						String isexitToken = cacheLoginedUser.get(user.getId());
						if (isexitToken != null && isexitToken != "") {
							// 不等于null 获取到原来的token，并且移除
							JedisUtils.del(isexitToken);
						}
						cacheLoginedUser.put(user.getId(), token);
						JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);

						//查询用户是否开户绑卡
						if(user.getCertificateChecked()==2&&user.getCgbBindBankCardState()==2) {
							CgbUserBankCard userBankCard = new CgbUserBankCard();
							userBankCard.setUserId(user.getId());
							List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
							if (list != null&& list.size()!=0) {
								CgbUserBankCard ubc = list.get(0);
								if(ubc!=null) {
									String bankAccountNo = ubc.getBankAccountNo();
									if(bankAccountNo.contains("*")) {
										//截取银行卡4个尾数
										String no = bankAccountNo.substring(bankAccountNo.length()-4, bankAccountNo.length());
										//配置文件里的名为包含--说明用户绑的就是这张银行卡。
										String cardNo = Global.getBankCardNo(user.getId());
										if(cardNo !=null && cardNo.contains(no)) {
											ubc.setBankCardSign(ubc.getBankAccountNo());
											ubc.setBankAccountNo(cardNo);
											ubc.setUpdateDate(new Date());
											int i = cgbUserBankCardDao.update(ubc);
											LOG.info("银行卡信息更新:{}", i == 1 ? "成功" : "失败");
										}
									}
								}
							}
						}
						
						// 设置缓存
						String a = JedisUtils.set(token, user.getId(), 1200);
						result.put("isActivate", user.getIsActivate());
						result.put("state", "0");
						result.put("message", "登录成功");
						result.put("token", token);
						result.put("username", user.getRealName() == null ? user.getName() : user.getRealName());
					} else if (user.getState() == UserStateType.DELETED) {
						System.out.println("用户已经被注销!");
						result.put("state", "1");
						result.put("message", "用户异常");
						result.put("token", "");
					} else {
						System.out.println("用户被禁用!");
						System.out.println("用户已经被注销!");
						result.put("state", "1");
						result.put("message", "用户异常");
						result.put("token", "");
					}
				} else {
					System.out.println("非投资用户!");
					System.out.println("用户已经被注销!");
					result.put("state", "1");
					result.put("message", "用户异常");
					result.put("token", "");
				}
			} else {
				result.put("state", "2");
				result.put("message", "用户名或密码错误");
				result.put("token", "");
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "用户异常");
		}

		return result;
	}

	/**
	 * 忘记登陆密码接口
	 * 
	 * @param from
	 * @param pass
	 * @param token
	 * @return
	 */
	@POST
	@Path("/forgetPassword")
	public Map<String, Object> forgetPassword(@FormParam("from") String from, @FormParam("pwd") String pwd, @FormParam("name") String name) {

		Map<String, Object> result = new HashMap<String, Object>();

		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");

		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pwd) || StringUtils.isBlank(name)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			UserInfo userInfo = new UserInfo();
			userInfo = userInfoDao.getUserInfoByPhone(name);

			if (userInfo != null) {
				userInfo.setPwd(EncoderUtil.encrypt(pwd));
				int i = userInfoDao.update(userInfo);
				if (i > 0) {
					String token = EncoderUtil.encrypt(sdf.format(time) + userInfo.getId()).replace("+", "a");
					// 设置缓存
					JedisUtils.set(token, userInfo.getId(), 1200);
					// 返回结果集
					result.put("state", "0");
					result.put("message", "重设登陆密码成功");
					Map<String, String> map = new HashMap<String, String>();
					map.put("token", token);
					result.put("data", map);
				}
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "退出登录失败");
			result.put("data", null);
		}

		return result;

	}

	/**
	 * 
	 * 方法: addRelationship <br>
	 * 描述: 建立三级分销关系. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月15日 上午11:49:29
	 * 
	 * @param recommendId
	 *            注册页面，推荐人
	 * @param refereesId
	 *            邀请链接，邀请人
	 * @param registuserId
	 *            注册用户
	 */
	public void addRelationship(String recommendId, String refereesId, String registuserId) {

		// 推荐人不能是自己.
		if (recommendId.equals(registuserId) || refereesId.equals(registuserId)) {
			LOG.info("fn:registUser,{三级雇佣关系建立条件不足}");
		} else {

			// 通过注册页面，输入推荐人手机号.
			if (!StringUtils.isBlank(recommendId)) {
				// 在三级佣金表中查询推荐人的上线userId.
				LevelDistribution threeLevelModel = levelDistributionDao.selectByUserId(recommendId);
				LevelDistribution record = new LevelDistribution();
				record.setId(String.valueOf(IdGen.randomLong())); // ID.
				record.setUserId(registuserId); // 当前客户(三级客户).
				record.setParentId(recommendId); // 推荐人(二级客户).
				if (threeLevelModel != null) {
					record.setGrandpaId(threeLevelModel.getParentId()); // 推荐人上线(一级客户).
				}
				record.setInviteCode(recommendId); // 推广方式(手机号码优先级大于邀请码).
				record.setType(CUSTOMER_TYPE_1); // 默认客户类型为普通客户.
				record.setCreateDate(new Date());
				int flag = levelDistributionDao.insert(record);
				if (flag == 1) {
					LOG.info("fn:registUser,{三级雇佣关系建立成功}");
				} else {
					LOG.info("fn:registUser,{三级雇佣关系建立失败}");
				}
			} else {
				// 通过邀请链接，进行注册.
				if (!StringUtils.isBlank(refereesId)) {
					// 在三级佣金表中查询推荐人的上线userId.
					LevelDistribution threeLevelModel = levelDistributionDao.selectByUserId(refereesId);
					LevelDistribution record = new LevelDistribution();
					record.setId(String.valueOf(IdGen.randomLong())); // ID.
					record.setUserId(registuserId); // 当前客户(三级客户).
					record.setParentId(refereesId); // 推荐人(二级客户).
					if (threeLevelModel != null) {
						record.setGrandpaId(threeLevelModel.getParentId()); // 推荐人上线(一级客户).
					}
					record.setInviteCode(refereesId); // 推广方式(手机号码优先级大于邀请码).
					record.setType(CUSTOMER_TYPE_1); // 默认客户类型为普通客户.
					record.setCreateDate(new Date());
					int flag = levelDistributionDao.insert(record);
					if (flag == 1) {
						LOG.info("fn:registUser,{三级雇佣关系建立成功}");
					} else {
						LOG.info("fn:registUser,{三级雇佣关系建立失败}");
					}
				}
			}
		}
	}

	/**
	 * 邮箱激活方法
	 * 
	 * @param from
	 * @param checkCode
	 * @param userId
	 * @return
	 */
	@POST
	@Path("/checkEmailCode")
	public Map<String, Object> checkEmailCode(@FormParam("from") String from, @FormParam("checkCode") String checkCode, @FormParam("userId") String userId, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(checkCode) || StringUtils.isBlank(userId)) {
			result.put("state", "2");
			result.put("message", "缺少参数");
			return result;
		}

		try {
			UserInfo referUser = userInfoDao.get(userId);
			if (referUser == null) {
				referUser = userInfoDao.getCgb(userId);
			}
			int flag = 0;
			if (referUser != null) {
				if (checkCode.equals(referUser.getSalt())) {
					referUser.setEmailChecked(UserInfo.BIND_EMAIL_YES);
					flag = userInfoDao.update(referUser);

					if (token != null && token.length() > 0) {
						// 获取缓存
						String jedisUserId = JedisUtils.get(token);
						UserInfo user = userInfoDao.get(jedisUserId);
						if (user == null) {
							user = userInfoDao.getCgb(jedisUserId);
						}
						if (userId.equals(user.getId())) {
							// 设置缓存
							JedisUtils.set(token, user.getId(), 1200);

						}
					}

				} else {
					throw new Exception("校验码校验失败");
				}
			} else {
				throw new Exception("用户信息查找失败");
			}

			if (flag > 0) {
				result.put("state", "0");
				result.put("message", "邮箱验证成功");
			} else {
				throw new Exception("邮箱验证失败");
			}

		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", e.getMessage());
		}
		return result;
	}

	/**
	 * 发放抵用劵
	 * 
	 * @param userId
	 * @param voucherList
	 */
	public void addVouchers(String userId, List<Double> voucherList, String spans) {

		// 先查询是否有对应金额的抵用劵
		if (voucherList != null && voucherList.size() > 0) {
			for (Double voucher : voucherList) {
				AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
				AVouchersDic aVouchersDic = aVouchersDicDao.findByVoucher(voucher);
				if (aVouchersDic != null) {
					aUserAwardsHistory.setId(String.valueOf(IdGen.randomLong()));
					aUserAwardsHistory.setAwardId(aVouchersDic.getId());
					aUserAwardsHistory.setCreateDate(new Date());
					aUserAwardsHistory.setUserId(userId);
					aUserAwardsHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), aVouchersDic.getOverdueDays()));
					aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
					aUserAwardsHistory.setType("1");// 类型为:抵用劵
					aUserAwardsHistory.setValue(voucher.toString());
					aUserAwardsHistory.setSpans(spans);
					;
					int i = aUserAwardsHistoryDao.insert(aUserAwardsHistory);
					if (i > 0) {
						LOG.info("{用户ID为}" + userId + "发放{" + voucher + "}元抵用劵成功");
					}
				} else {
					LOG.info("{尚未添加}" + voucher + "元面额的抵用劵");
				}
			}
		}
	}

	/**
	 * 
	 * 方法: registByAddVouchers <br>
	 * 描述: 注册送合计600元抵用券. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年7月26日 上午9:22:28
	 * 
	 * @param userId
	 */
	public void registByAddVouchers(String userId) {

		String vouchers_300_id = "8a6787f318b64a61a30503ac66a7546b";
		String vouchers_100_id = "582446293230686208";
		String vouchers_50_id = "582446134660829184";
		String vouchers_20_id = "684576261951066112";
		String vouchers_10_id = "582445912538877952";

		int a = 0;
		// 一张300元抵用券.
		for (int i = 0; i < 1; i++) {
			UserVouchersHistory vouchersHistory = new UserVouchersHistory();
			AVouchersDic vouchersDic = aVouchersDicDao.get(vouchers_300_id);
			vouchersHistory.setId(IdGen.uuid());
			vouchersHistory.setAwardId(vouchersDic.getId());
			vouchersHistory.setUserId(userId);
			vouchersHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
			vouchersHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
			vouchersHistory.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
			vouchersHistory.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
			vouchersHistory.setCreateDate(new Date());
			vouchersHistory.setUpdateDate(new Date());
			vouchersHistory.setRemark(vouchersDic.getRemarks());
			vouchersHistory.setSpans(vouchersDic.getSpans());
			vouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
			vouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
			int flag = userVouchersHistoryDao.insert(vouchersHistory);
			if (flag == 1) {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵成功");
			} else {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵失败");
			}
		}

		// 两张100元抵用券.
		for (int i = 0; i < 2; i++) {
			UserVouchersHistory vouchersHistory = new UserVouchersHistory();
			AVouchersDic vouchersDic = aVouchersDicDao.get(vouchers_100_id);
			vouchersHistory.setId(IdGen.uuid());
			vouchersHistory.setAwardId(vouchersDic.getId());
			vouchersHistory.setUserId(userId);
			vouchersHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
			vouchersHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
			vouchersHistory.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
			vouchersHistory.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
			int x = ++a;
			vouchersHistory.setCreateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setRemark(vouchersDic.getRemarks());
			vouchersHistory.setSpans(vouchersDic.getSpans());
			vouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
			vouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
			int flag = userVouchersHistoryDao.insert(vouchersHistory);
			if (flag == 1) {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵成功");
			} else {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵失败");
			}
		}

		// 一张50元抵用券.
		for (int i = 0; i < 1; i++) {
			UserVouchersHistory vouchersHistory = new UserVouchersHistory();
			AVouchersDic vouchersDic = aVouchersDicDao.get(vouchers_50_id);
			vouchersHistory.setId(IdGen.uuid());
			vouchersHistory.setAwardId(vouchersDic.getId());
			vouchersHistory.setUserId(userId);
			vouchersHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
			vouchersHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
			vouchersHistory.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
			vouchersHistory.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
			int x = ++a;
			vouchersHistory.setCreateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setRemark(vouchersDic.getRemarks());
			vouchersHistory.setSpans(vouchersDic.getSpans());
			vouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
			vouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
			int flag = userVouchersHistoryDao.insert(vouchersHistory);
			if (flag == 1) {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵成功");
			} else {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵失败");
			}
		}

		// 两张20元抵用券.
		for (int i = 0; i < 2; i++) {
			UserVouchersHistory vouchersHistory = new UserVouchersHistory();
			AVouchersDic vouchersDic = aVouchersDicDao.get(vouchers_20_id);
			vouchersHistory.setId(IdGen.uuid());
			vouchersHistory.setAwardId(vouchersDic.getId());
			vouchersHistory.setUserId(userId);
			vouchersHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
			vouchersHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
			vouchersHistory.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
			vouchersHistory.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
			int x = ++a;
			vouchersHistory.setCreateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setRemark(vouchersDic.getRemarks());
			vouchersHistory.setSpans(vouchersDic.getSpans());
			vouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
			vouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
			int flag = userVouchersHistoryDao.insert(vouchersHistory);
			if (flag == 1) {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵成功");
			} else {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵失败");
			}
		}

		// 一张10元抵用券.
		for (int i = 0; i < 1; i++) {
			UserVouchersHistory vouchersHistory = new UserVouchersHistory();
			AVouchersDic vouchersDic = aVouchersDicDao.get(vouchers_10_id);
			vouchersHistory.setId(IdGen.uuid());
			vouchersHistory.setAwardId(vouchersDic.getId());
			vouchersHistory.setUserId(userId);
			vouchersHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
			vouchersHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
			vouchersHistory.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
			vouchersHistory.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
			int x = ++a;
			vouchersHistory.setCreateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * x));
			vouchersHistory.setRemark(vouchersDic.getRemarks());
			vouchersHistory.setSpans(vouchersDic.getSpans());
			vouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
			vouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
			int flag = userVouchersHistoryDao.insert(vouchersHistory);
			if (flag == 1) {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵成功");
			} else {
				LOG.info("用户ID为{" + userId + "}发放{" + NumberUtils.scaleDoubleStr(vouchersDic.getAmount()) + "}元抵用劵失败");
			}
		}

	}

	/**
	 * 用户注册---前端加密
	 * 
	 * @param from
	 * @param name
	 * @param pwd
	 * @return
	 * @throws ParseException
	 */
	@POST
	@Path("/newRegist")
	public Map<String, Object> newRegist(@FormParam("from") String from, @FormParam("name") String name, @FormParam("pwd") String pwd, @FormParam("userNo") String userNo, @FormParam("refer") String refer, @FormParam("recommendMobilePhone") String recommendMobilePhone, @Context HttpServletRequest servletrequest) throws ParseException {

		// 推荐人手机号码，推荐人ID.
		String recommendId = "";
		// 邀请链接，推荐人ID.
		String refereesId = "";
		// IP.
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(name) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		UserInfo user = new UserInfo();
		user.setName(name);
		List<UserInfo> returnuser = registUserDao.findList(user);
		if (returnuser != null && returnuser.size() > 0) {
			result.put("state", "1");
			result.put("message", "该用户已注册");
			return result;
		}

		// 注册客户账号信息.

		// 设定客户推荐人及推荐人手机号
		UserInfo userInfo = new UserInfo();
		if (recommendMobilePhone != null && recommendMobilePhone.length() > 0) {
			UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendMobilePhone);
			if (null != recommendUserInfo) {
				recommendId = recommendUserInfo.getId();
				userInfo.setRecommendUserId(recommendUserInfo.getId());
				userInfo.setRecommendUserPhone(recommendMobilePhone);

			}
		}
		if (refer != null && refer.length() > 0 && !refer.equals("null")) {
			ZtmgPartnerPlatform entity = ztmgPartnerPlatformDao.getZtmgPartnerPlatformByPlatformCode(refer);
			if (entity != null) {
				userInfo.setRecommendUserId(entity.getId());
			}
		}

		userInfo.setId(String.valueOf(IdGen.randomLong()));
		userInfo.setName(name);
		userInfo.setPwd(pwd);
		userInfo.setRecomType(0);
		userInfo.setUserType(UserType.BID);// 投资用户
		userInfo.setState(UserStateType.NORMAL);
		userInfo.setRegisterFrom(StringUtils.toInteger(from));
		Date date = new Date();
		userInfo.setCreateDate(date);
		userInfo.setEmailChecked(UserInfo.BIND_EMAIL_NO);// 邮箱未验证
		userInfo.setCertificateChecked(UserInfo.CERTIFICATE_YES);// 实名认证-否
		userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);// 绑定银行卡
		userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO);// 设为未绑定银行卡
		userInfo.setLastLoginIp(ip);
		userInfo.setRegisterDate(date);
		userInfo.setLastLoginDate(date);
		userInfo.setIsActivate("TRUE");// 懒猫2.0迁移，会员激活标识，TRUE、FALSE
		userInfo.setAccountId(String.valueOf(IdGen.randomLong()));
		userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO); // 未开通银行存管
		int a = registUserDao.insert(userInfo);

		// 如果recommendUserId非空，则生成三级关系.

		if (userInfo.getRecommendUserPhone() != null && userInfo.getRecommendUserPhone().length() > 0) {
			addRelationship(recommendId, refereesId, userInfo.getId());
		}

		// 注册用户银行托管保账户
		CgbUserAccount userAccountInfo = new CgbUserAccount();
		if (a > 0) {
			System.out.println("用户注册插入客户表完成");
			userAccountInfo.setId(userInfo.getAccountId());
			userAccountInfo.setUserId(userInfo.getId());
			userAccountInfo.setTotalAmount(0d);
			userAccountInfo.setTotalInterest(0d);
			userAccountInfo.setAvailableAmount(0d);
			userAccountInfo.setFreezeAmount(0d);
			userAccountInfo.setRechargeAmount(0d);
			userAccountInfo.setRechargeCount(0);
			userAccountInfo.setCashAmount(0d);
			userAccountInfo.setCashCount(0);
			userAccountInfo.setCurrentAmount(0d);
			userAccountInfo.setRegularDuePrincipal(0d);
			userAccountInfo.setRegularDueInterest(0d);
			userAccountInfo.setRegularTotalAmount(0d);
			userAccountInfo.setRegularTotalInterest(0d);
			userAccountInfo.setCurrentTotalAmount(0d);
			userAccountInfo.setCurrentTotalInterest(0d);
			userAccountInfo.setCurrentYesterdayInterest(0d);
			userAccountInfo.setReguarYesterdayInterest(0d);
			userAccountInfo.setUserInfo(userInfo);
			// 同时生成客户账户
			cgbUserAccountDao.insert(userAccountInfo);
		}

		// 注册成功送500积分
		UserBounsPoint userBounsPoint1 = new UserBounsPoint();
		userBounsPoint1.setId(IdGen.uuid());
		userBounsPoint1.setUserId(userInfo.getId());
		userBounsPoint1.setScore(500);
		userBounsPoint1.setCreateDate(new Date());
		userBounsPoint1.setUpdateDate(new Date());
		userBounsPoint1.setUserInfo(userInfo);
		userBounsPointDao.insert(userBounsPoint1);
		// 添加账户积分历史明细
		UserBounsHistory userBounsHistory = new UserBounsHistory();
		Double userBouns = 500d;

		userBounsHistory.setId(IdGen.uuid());
		userBounsHistory.setUserId(userInfo.getId());
		userBounsHistory.setAmount(userBouns);
		userBounsHistory.setCreateDate(new Date());
		userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST);
		userBounsHistory.setTransId(userBounsPoint1.getId());
		userBounsHistory.setCurrentAmount(userBounsPoint1.getScore().toString());// 设置剩余积分
		userBounsHistoryService.insert(userBounsHistory);

		/**
		 * // 积分商城，用户邀请好友完成注册，推荐人获得100积分.
		 * // 推荐人ID.
		 * */
		// if (recommendMobilePhone != null && recommendMobilePhone.length() > 0) {
		// UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendMobilePhone);
		// if (null != recommendUserInfo) {
		// recommendId = recommendUserInfo.getId();
		// UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(recommendId);
		// userBounsPoint.setScore(userBounsPoint.getScore() + 100);
		// userBounsPointDao.update(userBounsPoint);
		//
		// // 添加账户积分历史明细
		// UserBounsHistory userBounsHistory1 = new UserBounsHistory();
		// userBounsHistory1.setId(IdGen.uuid());
		// userBounsHistory1.setUserId(recommendId);
		// userBounsHistory1.setAmount(100D);
		// userBounsHistory1.setCreateDate(new Date());
		// userBounsHistory1.setBounsType(UserBounsHistoryService.
		// BOUNS_TYPE_REQUEST);
		// userBounsHistory1.setTransId(userBounsPoint.getId());
		// userBounsHistoryService.insert(userBounsHistory1);
		// }
		// }

		// 注册送合计600元抵用券.
		registByAddVouchers(userInfo.getId());

		// 发送注册成功短信提醒
		weixinSendTempMsgService.sendRegistMsg(name);
		// 发送站内信
		StationLetter letter = new StationLetter();
		letter.setUserId(userInfo.getId());
		letter.setLetterType(StationLettersService.LETTER_TYPE_REGIST);
		letter.setTitle("恭喜您注册成功，现送您【600元抵扣券】，可登录APP在【我的-优惠券】中查看");
		letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
		letter.setState(StationLettersService.LETTER_STATE_UNREAD);
		letter.setSendTime(new Date());
		stationLettersService.save(letter);

		// 缓存客户信息.
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		String token = EncoderUtil.encrypt(sdf.format(time) + userInfo.getId()).replace("+", "a");
		// 设置缓存
		JedisUtils.set(token, userInfo.getId(), 1200);
		result.put("state", "0");
		result.put("message", "注册成功");
		result.put("token", token);
		result.put("username", userInfo.getName());
		return result;
	}

	/**
	 * 用户登录---前端加密
	 * 
	 * @param from
	 * @param mobile
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("/newLogin")
	public Map<String, Object> newLogin(@FormParam("from") String from, @FormParam("mobile") String mobile, @FormParam("pwd") String pwd, @Context HttpServletRequest servletrequest) {

		// 缓存中登录用户的token信息（key=userId，value=token）
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(mobile) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setName(mobile);
		userInfo.setPwd(pwd);

		try {
			List<UserInfo> userList = userLoginDao.findList(userInfo);
			if (userList != null && userList.size() > 0) {
				UserInfo user = userList.get(0);
				if (Integer.valueOf(UserType.BID) == user.getUserType()) {
					// 用户状态正常
					if (user.getState() == UserStateType.NORMAL) {

						StringBuffer keyBuffer = new StringBuffer();
						keyBuffer.append("ERROR_COUNT_KEY:").append(mobile);
						String failedCount = JedisUtils.get(keyBuffer.toString());
						if (failedCount != null) {
							if (Integer.valueOf(failedCount) >= 5) {
								result.put("state", "2");
								result.put("message", "该帐号已被禁止登陆，请30分钟后重新尝试。");
								result.put("token", "");
								return result;
							}
						}

						// 修改最后登录时间和登录IP
						user.setId(user.getId());
						user.setLastLoginDate(new Date());
						user.setLastLoginIp(ip);
						userLoginDao.update(user);


	                       UserLog userLog = new UserLog();
	                       userLog.setId(String.valueOf(IdGen.randomLong()));
	                       userLog.setUserId(user.getId());
	                       if(user.getRealName()!=null)
	                        userLog.setUserName(user.getRealName()+user.getName());
	                       else
	                    	userLog.setUserName(user.getName());  
	                       userLog.setType("1");
	                       userLog.setCreateDate(new Date());
	                       if(user.getRealName()!=null)
	                        userLog.setRemark(user.getRealName()+"登录系统");
	                       else
	                    	userLog.setRemark(user.getName()+"登录系统");  
	                       userLogDao.insert(userLog);

						// 生成token
						String token = EncoderUtil.encrypt(sdf.format(time) + user.getId()).replace("+", "a");

						Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
						// 系统没有登录用户（一般不会进该方法）
						if (cacheLoginedUser == null) {
							cacheLoginedUser = new HashMap<String, String>();
						}
						String isexitToken = cacheLoginedUser.get(user.getId());
						if (isexitToken != null && isexitToken != "") {
							// 不等于null 获取到原来的token，并且移除
							JedisUtils.del(isexitToken);
						}
						cacheLoginedUser.put(user.getId(), token);
						JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);

						// 设置缓存
						String a = JedisUtils.set(token, user.getId(), 1200);
						//查询用户是否开户绑卡
						if(user.getCertificateChecked()==2&&user.getCgbBindBankCardState()==2) {
							CgbUserBankCard userBankCard = new CgbUserBankCard();
							userBankCard.setUserId(user.getId());
							List<CgbUserBankCard> list = cgbUserBankCardDao.findList(userBankCard);
							if (list != null&& list.size()!=0) {
								CgbUserBankCard ubc = list.get(0);
								if(ubc!=null) {
									String bankAccountNo = ubc.getBankAccountNo();
									if(bankAccountNo.contains("*")) {
										//截取银行卡4个尾数
										String no = bankAccountNo.substring(bankAccountNo.length()-4, bankAccountNo.length());
										//配置文件里的名为包含--说明用户绑的就是这张银行卡。
										String cardNo = Global.getBankCardNo(user.getId());
										if(cardNo !=null && cardNo.contains(no)) {
											ubc.setBankCardSign(ubc.getBankAccountNo());
											ubc.setBankAccountNo(cardNo);
											ubc.setUpdateDate(new Date());
											int i = cgbUserBankCardDao.update(ubc);
											LOG.info("银行卡信息更新:{}", i == 1 ? "成功" : "失败");
										}
									}
								}
							}
						}
						
						//用户激活情况
						result.put("isActivate", user.getIsActivate());
						result.put("certificateChecked", user.getCertificateChecked() == null ? "1" : user.getCertificateChecked());
						result.put("state", "0");
						result.put("message", "登录成功");   
						result.put("token", token);
						result.put("username", user.getRealName() == null ? user.getName() : user.getRealName());
					} else if (user.getState() == UserStateType.DELETED) {
						System.out.println("用户已经被注销!");
						result.put("state", "1");
						result.put("message", "用户异常");
						result.put("token", "");
					} else {
						System.out.println("用户被禁用!");
						System.out.println("用户已经被注销!");
						result.put("state", "1");
						result.put("message", "用户异常");
						result.put("token", "");
					}
				} else {
					System.out.println("非投资用户!");
					System.out.println("用户已经被注销!");
					result.put("state", "1");
					result.put("message", "用户异常");
					result.put("token", "");
				}
			} else {
				StringBuffer keyBuffer = new StringBuffer();
				keyBuffer.append("ERROR_COUNT_KEY:").append(mobile);
				LOG.info("set {}", keyBuffer.toString());
				JedisUtils.increaseFailedLoginCounter(keyBuffer.toString(), 1800);

				String failedCount = JedisUtils.get(keyBuffer.toString());
				result.put("state", "2");
				if (Integer.valueOf(failedCount) > 5) {
					result.put("message", "您还有0次机会，您可以点击下方的“忘记密码”来找回密码。");
				} else {
					result.put("message", "您还有" + (5 - Integer.valueOf(failedCount)) + "次机会，您可以点击下方的“忘记密码”来找回密码。");
				}
				result.put("token", "");

			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "用户异常");
		}

		return result;
	}

	/**
	 * 忘记登陆密码接口---前端加密
	 * 
	 * @param from
	 * @param pass
	 * @param token
	 * @return
	 */
	@POST
	@Path("/newForgetPassword")
	public Map<String, Object> newForgetPassword(@FormParam("from") String from, @FormParam("pwd") String pwd, @FormParam("name") String name) {

		Map<String, Object> result = new HashMap<String, Object>();

		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");

		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pwd) || StringUtils.isBlank(name)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			UserInfo userInfo = new UserInfo();
			userInfo = userInfoDao.getUserInfoByPhone(name);

			if (userInfo != null) {
				userInfo.setPwd(pwd);
				int i = userInfoDao.update(userInfo);
				if (i > 0) {
					String token = EncoderUtil.encrypt(sdf.format(time) + userInfo.getId()).replace("+", "a");
					// 设置缓存
					JedisUtils.set(token, userInfo.getId(), 1200);
					// 返回结果集
					result.put("state", "0");
					result.put("message", "重设登陆密码成功");
					Map<String, String> map = new HashMap<String, String>();
					map.put("token", token);
					result.put("data", map);
				}
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "退出登录失败");
			result.put("data", null);
		}

		return result;

	}

	/**
	 * 流入流出统计（按日）
	 * 
	 * @return
	 */
	@POST
	@Path("/getInOutCount")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getInOutCount(@Context HttpServletRequest servletrequest) {

		Map<String, Object> params = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		String date = DateUtils.getDate();
		String inAmont = "0";
		String outAmont = "0";
		double amount = 0;
		CgbUserTransDetail cgbUserTransDetail = new CgbUserTransDetail();
		// cgbUserTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2);//成功

		while (DateUtils.compare_date_T("2017-12-31", date)) {
			cgbUserTransDetail.setTransDate(DateUtils.getDateOfString(date));
			cgbUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0);// 充值
			inAmont = cgbUserTransDetailService.inOutCount(cgbUserTransDetail);
			cgbUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1);// 提现
			outAmont = cgbUserTransDetailService.inOutCount(cgbUserTransDetail);
			if (StringUtils.isBlank(inAmont)) {
				inAmont = "0";
			}
			if (StringUtils.isBlank(outAmont)) {
				outAmont = "0";
			}
			amount = Double.parseDouble(inAmont) - Double.parseDouble(outAmont);
			amount = NumberUtils.scaleDouble(amount);
			// amount = new DecimalFormat("0.00").format(amount);
			list.add(date + "," + new DecimalFormat("0.00").format(amount));
			date = DateUtils.getSpecifiedDayBefore(date);

		}

		params.put("list", list);
		return params;
	}
	
	
	/**
	 * 用户预注册---wap端
	 * 
	 * @param from
	 * @param name
	 * @param pwd
	 * @return
	 * @throws ParseException
	 */
	@POST
	@Path("/wapPreRegist")
	public Map<String, Object> wapPreRegist(@FormParam("from") String from, @FormParam("name") String name, @FormParam("pwd") String pwd, @FormParam("refer") String refer, @FormParam("recommendMobilePhone") String recommendMobilePhone, @Context HttpServletRequest servletrequest) throws ParseException {

		// 推荐人手机号码，推荐人ID.
		String recommendId = "";
		// 邀请链接，推荐人ID.
		String refereesId = "";
		// IP.
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(name) || StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			UserInfo user = new UserInfo();
			user.setName(name);
			List<UserInfo> returnUser = registUserDao.findList(user);
			if (returnUser != null && returnUser.size() > 0) {
				result.put("state", "1");
				result.put("message", "该用户已注册");
				return result;
			}
			//wap 预注册需修改手机号： 手机号+"_wap"
			String nameWap = name + "_wap";
			user.setName(nameWap);
			//查询是否已经预注册了。有删除，没有新增。
			List<UserInfo> userList = registUserDao.findList(user);
			if (userList != null && userList.size() > 0) {
				for (Iterator iterator = userList.iterator(); iterator.hasNext();) {
					UserInfo userInfo = (UserInfo) iterator.next();
					//说明之前已经预注册，需物理删除
					int b = registUserDao.deletePhysics(userInfo);
					if(b==1) {
						Log.info("旧的预注册数据删除成功。");
					}else {
						Log.info("旧的预注册数据删除失败。");
					}
				}
			}
			// 注册客户账号信息.
			// 设定客户推荐人及推荐人手机号
			UserInfo userInfo = new UserInfo();
			if (recommendMobilePhone != null && recommendMobilePhone.length() > 0) {
				UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendMobilePhone);
				if (null != recommendUserInfo) {
					recommendId = recommendUserInfo.getId();
					userInfo.setRecommendUserId(recommendUserInfo.getId());
					userInfo.setRecommendUserPhone(recommendMobilePhone);
	
				}
			}
			userInfo.setId(String.valueOf(IdGen.randomLong()));
			//wap 预注册需修改手机号： 手机号+"_wap"  ---成功注册之后要把"_wap"后缀去掉,状态改为正常，再保存到数据库里。
			userInfo.setName(nameWap);
			userInfo.setPwd(pwd);
			userInfo.setRecomType(0);
			userInfo.setUserType(UserType.BID);// 投资用户
			userInfo.setState(UserStateType.PRE_REGISTRATION);//状态为：预注册
			userInfo.setRegisterFrom(StringUtils.toInteger(from));
			Date date = new Date();
			userInfo.setCreateDate(date);
			userInfo.setEmailChecked(UserInfo.BIND_EMAIL_NO);// 邮箱未验证
			userInfo.setCertificateChecked(UserInfo.CERTIFICATE_YES);// 实名认证
			userInfo.setBindBankCardState(UserInfo.BIND_CARD_NO);// 绑定银行卡
			userInfo.setLastLoginIp(ip);
			userInfo.setIsActivate("TRUE");
			userInfo.setRegisterDate(date);
			userInfo.setLastLoginDate(date);
			userInfo.setAccountId(String.valueOf(IdGen.randomLong()));
			userInfo.setCgbBindBankCardState(UserInfo.CGB_BIND_CARD_NO); // 未开通银行存管
			int a = registUserDao.insert(userInfo);
			if(a==1) {
				Log.info("预注册数据生成成功。");
			}else {
				Log.info("预注册数据生成失败。");
			}
			// 如果recommendUserId非空，则生成三级关系.
	
			if (userInfo.getRecommendUserPhone() != null && userInfo.getRecommendUserPhone().length() > 0) {
				addRelationship(recommendId, refereesId, userInfo.getId());
			}
			result.put("state", "0");
			result.put("message", "客户预注册--第一步已成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}
	
	/**
	 * wap注册
	 * 
	 * @param name
	 * @return
	 */
	@POST
	@Path("/wapRegist")
	public Map<String, Object> wapRegist(@FormParam("name") String name, @Context HttpServletRequest servletrequest) throws ParseException {
		
		// IP.
		String ip = (String) servletrequest.getAttribute("ip");
		ip.replace("_", ".");
		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(name)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			UserInfo user = new UserInfo();
			//查询预处理数据
			String nameWap = name + "_wap";
			user.setName(nameWap);
			List<UserInfo> returnuser = registUserDao.findList(user);
			UserInfo userInfo = null;
			if (returnuser != null && returnuser.size() > 0) {
				// 唯一一个
				//成功注册之后要把"_wap"后缀去掉,状态改为正常，再保存到数据库里。
				userInfo = returnuser.get(0);
				userInfo.setName(name);
				userInfo.setState(UserStateType.NORMAL);
				Date date = new Date();
				userInfo.setUpdateDate(date);
				userInfo.setLastLoginIp(ip);
				userInfo.setLastLoginDate(date);
				int a = registUserDao.update(userInfo);
				if(a==1) {
					Log.info("注册数据修改成功。");
				}else {
					Log.info("注册数据修改失败。");
				}
				
				// 注册用户银行托管保账户
				CgbUserAccount userAccountInfo = new CgbUserAccount();
				if (a > 0) {
					System.out.println("用户注册插入客户表完成");
					userAccountInfo.setId(userInfo.getAccountId());
					userAccountInfo.setUserId(userInfo.getId());
					userAccountInfo.setTotalAmount(0d);
					userAccountInfo.setTotalInterest(0d);
					userAccountInfo.setAvailableAmount(0d);
					userAccountInfo.setFreezeAmount(0d);
					userAccountInfo.setRechargeAmount(0d);
					userAccountInfo.setRechargeCount(0);
					userAccountInfo.setCashAmount(0d);
					userAccountInfo.setCashCount(0);
					userAccountInfo.setCurrentAmount(0d);
					userAccountInfo.setRegularDuePrincipal(0d);
					userAccountInfo.setRegularDueInterest(0d);
					userAccountInfo.setRegularTotalAmount(0d);
					userAccountInfo.setRegularTotalInterest(0d);
					userAccountInfo.setCurrentTotalAmount(0d);
					userAccountInfo.setCurrentTotalInterest(0d);
					userAccountInfo.setCurrentYesterdayInterest(0d);
					userAccountInfo.setReguarYesterdayInterest(0d);
					userAccountInfo.setUserInfo(userInfo);
					// 同时生成客户账户
					cgbUserAccountDao.insert(userAccountInfo);
				}

				// 注册成功送500积分
				UserBounsPoint userBounsPoint1 = new UserBounsPoint();
				userBounsPoint1.setId(IdGen.uuid());
				userBounsPoint1.setUserId(userInfo.getId());
				userBounsPoint1.setScore(500);
				userBounsPoint1.setCreateDate(new Date());
				userBounsPoint1.setUpdateDate(new Date());
				userBounsPoint1.setUserInfo(userInfo);
				userBounsPointDao.insert(userBounsPoint1);
				// 添加账户积分历史明细
				UserBounsHistory userBounsHistory = new UserBounsHistory();
				Double userBouns = 500d;

				userBounsHistory.setId(IdGen.uuid());
				userBounsHistory.setUserId(userInfo.getId());
				userBounsHistory.setAmount(userBouns);
				userBounsHistory.setCreateDate(new Date());
				userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST);
				userBounsHistory.setTransId(userBounsPoint1.getId());
				userBounsHistory.setCurrentAmount(userBounsPoint1.getScore().toString());// 设置剩余积分
				userBounsHistoryService.insert(userBounsHistory);


				// 注册送合计600元抵用券.
				registByAddVouchers(userInfo.getId());

				// 发送注册成功短信提醒
				weixinSendTempMsgService.sendRegistMsg(name);
				// 发送站内信
				StationLetter letter = new StationLetter();
				letter.setUserId(userInfo.getId());
				letter.setLetterType(StationLettersService.LETTER_TYPE_REGIST);
				letter.setTitle("恭喜您注册成功，现送您【600元抵扣券】，可登录APP在【我的-优惠券】中查看");
				letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
				letter.setState(StationLettersService.LETTER_STATE_UNREAD);
				letter.setSendTime(new Date());
				stationLettersService.save(letter);

				// 缓存客户信息.
				Date time = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhhmmss");
				String token = EncoderUtil.encrypt(sdf.format(time) + userInfo.getId()).replace("+", "a");
				// 设置缓存
				JedisUtils.set(token, userInfo.getId(), 1200);
				result.put("state", "0");
				result.put("message", "注册成功");
				result.put("token", token);
				result.put("username", userInfo.getName());
			} else {
				result.put("state", "4");
				result.put("message", "数据错误，请重新注册");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}
}
