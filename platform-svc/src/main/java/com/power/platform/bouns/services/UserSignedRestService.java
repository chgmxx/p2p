package com.power.platform.bouns.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.dao.UserSignedDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 
 * class: UserSignedRestService <br>
 * description: 用户签到Service <br>
 * author: Mr.Roy <br>
 * date: 2018年12月3日 下午12:55:26
 */
@Component
@Path("/signed")
@Service("userSignedRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserSignedRestService {

	private static final Logger LOG = LoggerFactory.getLogger(UserSignedRestService.class);

	@Autowired
	private UserSignedService userSignedService;
	@Autowired
	private UserInfoService userInfoService;
	@Resource
	private UserSignedDao userSignedDao;
	@Resource
	private UserBounsPointDao userBounsPointDao;
	@Resource
	private UserBounsHistoryDao userBounsHistoryDao;

	public static final String BOUNDS_TYPE_0 = "出借";
	public static final String BOUNDS_TYPE_1 = "注册";
	public static final String BOUNDS_TYPE_2 = "邀请好友";
	public static final String BOUNDS_TYPE_3 = "签到";
	public static final String BOUNDS_TYPE_4 = "积分抽奖";
	public static final String BOUNDS_TYPE_5 = "积分兑换";
	public static final String BOUNDS_TYPE_6 = "好友出借";
	public static final String BOUNDS_TYPE_7 = "流标";

	/**
	 * 
	 * methods: adjustUserSigned <br>
	 * description: 2019五一假期期间签到双倍积分调整. <br>
	 * author: Roy <br>
	 * date: 2019年5月5日 下午6:05:50
	 * 
	 * @param userId
	 * @return
	 */
	@POST
	@Path("/adjustUserSigned")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> adjustUserSigned(@FormParam("userId") String userId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(userId)) {
			LOG.info("fn:adjustUserSigned，缺少必要参数.");
			result.put("respCode", "02");
			result.put("respMsg", "缺少必要参数.");
			return result;
		}

		try {

			// CurrentAmount
			Double tempCurrentAmount = 0D;
			// 标志位重复数据处理.
			boolean isExist_51 = false;
			// 标志位重复数据处理.
			boolean isExist_52 = false;
			// 标志位重复数据处理.
			boolean isExist_53 = false;
			// 标志位重复数据处理.
			boolean isExist_54 = false;
			// 标志位重复数据处理.
			boolean isExist_55 = false;

			//
			UserBounsHistory entity = new UserBounsHistory();
			entity.setUserId(userId);
			entity.setBeginCreateDate("2019-05-01 00:00:00");
			entity.setEndCreateDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			Page<UserBounsHistory> page = new Page<UserBounsHistory>();
			page.setOrderBy("a.create_date ASC");
			entity.setPage(page);
			List<UserBounsHistory> list = userBounsHistoryDao.findList(entity);

			for (int i = 0; i < list.size(); i++) {
				UserBounsHistory model = list.get(i);
				if (i == 0) {
					tempCurrentAmount = NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount());
					// System.out.println("调整前积分总额：" + tempCurrentAmount);
					LOG.info("fn:adjustUserSigned，调整前积分总额：" + tempCurrentAmount);
					if (BOUNDS_TYPE_3.equals(model.getBounsType())) { // 签到积分记录调整.
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_SIGNED); // 签到.
						if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-01"))) {
							isExist_51 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-02"))) {
							isExist_52 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-03"))) {
							isExist_53 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-04"))) {
							isExist_54 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-05"))) {
							if (isExist_55) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							} else {
								isExist_55 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							}
						} else {
							tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
						}
					} else if (BOUNDS_TYPE_0.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST); // 出借.
					} else if (BOUNDS_TYPE_1.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST); // 注册.
					} else if (BOUNDS_TYPE_2.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REQUEST); // 邀请好友.
					} else if (BOUNDS_TYPE_4.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW); // 抽奖.
					} else if (BOUNDS_TYPE_5.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_CASH_LOTTERY); // 兑换奖品.
					} else if (BOUNDS_TYPE_6.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST); // 好友投资.
					} else if (BOUNDS_TYPE_7.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_MISCARRY); // 好友投资.
					}
					int update = userBounsHistoryDao.update(model);
					if (update == 1) {
						// System.out.println("更新客户积分流水成功 ...");
						LOG.info("fn:adjustUserSigned，更新客户积分流水成功 ...");
					} else {
						// System.out.println("更新客户积分流水失败 ...");
						LOG.info("fn:adjustUserSigned，更新客户积分流水失败 ...");
					}
					// System.out.println("积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					LOG.info("fn:adjustUserSigned，调整数据：\t" + "积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					if (BOUNDS_TYPE_3.equals(model.getBounsType())) { // 签到积分记录调整.
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_SIGNED); // 签到.
						if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-01"))) {
							if (isExist_51) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_51 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-02"))) {
							if (isExist_52) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_52 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-03"))) {
							if (isExist_53) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_53 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-04"))) {
							if (isExist_54) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_54 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-05"))) {
							if (isExist_55) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							} else {
								isExist_55 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							}
						} else {
							tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
						}
					} else if (BOUNDS_TYPE_0.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST); // 出借.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_1.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST); // 注册.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_2.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REQUEST); // 邀请好友.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_4.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW); // 抽奖.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_5.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_CASH_LOTTERY); // 兑换奖品.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_6.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST); // 好友投资.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					}else if (BOUNDS_TYPE_7.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_MISCARRY); // 流标.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else {
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					}
					int update = userBounsHistoryDao.update(model);
					if (update == 1) {
						// System.out.println("更新客户积分流水成功 ...");
						LOG.info("fn:adjustUserSigned，更新客户积分流水成功 ...");
					} else {
						// System.out.println("更新客户积分流水失败 ...");
						LOG.info("fn:adjustUserSigned，更新客户积分流水失败 ...");
					}
					// System.out.println("积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					LOG.info("fn:adjustUserSigned，调整数据：\t" + "积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
				}
			}
			UserBounsPoint userBounsPoint = new UserBounsPoint();
			userBounsPoint.setUserId(userId);
			List<UserBounsPoint> userBounsPointList = userBounsPointDao.findList(userBounsPoint);
			if (userBounsPointList.size() > 0) {
				UserBounsPoint userBounsPointAccount = userBounsPointList.get(0);
				userBounsPointAccount.setScore(tempCurrentAmount.intValue());
				int update = userBounsPointDao.update(userBounsPointAccount);
				if (update == 1) {
					// System.out.println("更新客户积分账户成功 ...");
					LOG.info("fn:adjustUserSigned，更新客户积分账户成功 ...");
				} else {
					// System.out.println("更新客户积分账户失败 ...");
					LOG.info("fn:adjustUserSigned，更新客户积分账户失败 ...");

				}
			}

			// 接口响应.
			result.put("respCode", "00");
			result.put("respMsg", "2019年五一假期期间签到双倍积分调整完毕.");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:adjustUserSigned，程序异常！");
			result.put("respCode", "01");
			result.put("respMsg", "程序异常！");
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: userSigned <br>
	 * 描述: 客户签到. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年12月15日 上午9:59:47
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/userSigned")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userSigned(@FormParam("from") String from, @FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:userSigned,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		try {

			// 缓存.
			String jedisUserId = JedisUtils.get(token);

			if (!StringUtils.isBlank(jedisUserId)) {
				// 当前客户ID.
				String userId = jedisUserId;
				UserInfo user = userInfoService.getCgb(jedisUserId);
				if (user == null) {
					user = userInfoService.get(jedisUserId);
				}
				data = userSignedService.signed(userId, user.getCertificateNo() == "" ? "" : user.getCertificateNo());
				LOG.info("fn:userSigned,接口响应成功.");
				result.put("state", "0");
				result.put("message", "接口响应成功.");
				result.put("data", data);
			} else {
				LOG.info("fn:userSigned，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:userSigned,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}
}
