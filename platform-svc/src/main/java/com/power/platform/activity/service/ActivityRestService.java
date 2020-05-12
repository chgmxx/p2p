package com.power.platform.activity.service;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.BrokerageDao;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.Brokerage;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.pojo.UserAwardsHistoryPojo;
import com.power.platform.activity.pojo.UserRateCouponPojo;
import com.power.platform.activity.pojo.UserVoucherPojo;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.questionnaire.dao.QuestionUserDao;
import com.power.platform.questionnaire.entity.QuestionUser;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

@Component
@Path("/activity")
@Service("activityRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ActivityRestService {

	private static final Logger LOG = LoggerFactory.getLogger(ActivityRestService.class);

	@Autowired
	private LevelDistributionService levelDistributionService;
	@Autowired
	private BrokerageService brokerageService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private UserTransDetailService userTransDetailService;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Resource
	private LevelDistributionDao levelDistributionDao;
	@Resource
	private BrokerageDao brokerageDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private UserBounsPointService userBounsPointService;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private QuestionUserDao questionUserDao;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;

	/**
	 * 
	 * 方法: superiorRateIncreasesTeamMembers <br>
	 * 描述: 上级邀请的加息团队. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月30日 下午3:50:16
	 * 
	 * @param fromuVF74Dakz6ojTihnDsl7mA
	 *            ==
	 * @param token
	 * @return
	 */
	@POST
	@Path("/superiorRateIncreasesTeamMembers")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> superiorRateIncreasesTeamMembers(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:superiorRateIncreasesTeamMembers,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		/**
		 * 业务处理.
		 */
		try {
			// 活动开始日期.
			String beginDate = "2016-09-01 00:00:00";
			// 活动截至日期.
			String endDate = "2016-09-30 23:59:59";
			// 缓存.
			String jedisUserId = JedisUtils.get(token);
			// 数据域.
			Map<String, Object> data = new HashMap<String, Object>();
			// 团队投资总人数(根据投资总额是否大于零来判断是否投资).
			int investTotalMembers = 0;
			// 团队投资总额.
			Double investTotalAmount = 0D;

			if (!StringUtils.isBlank(jedisUserId)) {
				// 当前客户ID.
				String userId = jedisUserId;
				// 三级关系中，查找当前客户的上级成员.
				LevelDistribution currentEntity = levelDistributionDao.selectByUserId(userId);
				if (null == currentEntity) {
					// 用户需邀请至少1人注册中投摩根账户才能组成加息团(我邀请的加息团人数).
					data.put("superiorMembers", 0);
					// 投资总人数.
					data.put("superiorInvestTotalMembers", 0);
					// 投资总额.
					data.put("superiorInvestTotalAmount", 0);
					LOG.info("fn:superiorRateIncreasesTeamMembers,接口响应成功.");
					result.put("state", "0");
					result.put("message", "接口响应成功.");
					result.put("data", data);
					return result;
				} else {
					// 查询上级客户投资记录.
					WloanTermInvest myWloanTermInvest = new WloanTermInvest();
					myWloanTermInvest.setUserId(currentEntity.getParentId());
					myWloanTermInvest.setBeginInvestDate(beginDate);
					myWloanTermInvest.setEndInvestDate(endDate);
					List<WloanTermInvest> myInvestList = wloanTermInvestDao.getSpetInvestInfo(myWloanTermInvest);
					for (WloanTermInvest model : myInvestList) {
						// 累计团队投资总额.
						investTotalAmount = investTotalAmount + model.getAmount();
					}
					// 判断当前客户是否投资.
					if (investTotalAmount > 0) {
						// 累计团队成员投资人数.
						investTotalMembers = investTotalMembers + 1;
					}
					// 三级关系中，查找客户下级成员.
					LevelDistribution entity = new LevelDistribution();
					// 上级客户加息团.
					entity.setUserId(currentEntity.getParentId());
					entity.setBeginDate(beginDate);
					entity.setEndDate(endDate);
					List<LevelDistribution> list = levelDistributionDao.myRateIncreasesTeamMembers(entity);
					for (LevelDistribution levelDistribution : list) {
						// 查询团队成员在活动期间的投资总额(同时也包括自己).
						WloanTermInvest otherWloanTermInvest = new WloanTermInvest();
						otherWloanTermInvest.setUserId(levelDistribution.getUserInfo().getId());
						otherWloanTermInvest.setBeginInvestDate(beginDate);
						otherWloanTermInvest.setEndInvestDate(endDate);
						List<WloanTermInvest> otherInvestList = wloanTermInvestDao.getSpetInvestInfo(otherWloanTermInvest);
						// 累计当前客户投资总额.
						Double sumInvestAmount = 0D;
						for (WloanTermInvest model : otherInvestList) {
							sumInvestAmount = sumInvestAmount + model.getAmount();
							// 累计团队投资总额.
							investTotalAmount = investTotalAmount + model.getAmount();
						}
						// 累计投资总人数.
						if (sumInvestAmount > 0) {
							// 累计团队成员投资人数.
							investTotalMembers = investTotalMembers + 1;
						}
					}
					// 用户需邀请至少1人注册中投摩根账户才能组成加息团(我邀请的加息团人数).
					if (list.size() <= 0) {
						data.put("superiorMembers", 0);
						// 投资总人数.
						data.put("superiorInvestTotalMembers", 0);
						// 投资总额.
						data.put("superiorInvestTotalAmount", 0);
					} else {
						// 团队成员也包括自己，所以要+1.
						data.put("superiorMembers", list.size() + 1);
						// 投资总人数.
						data.put("superiorInvestTotalMembers", investTotalMembers);
						// 投资总额.
						NumberFormat number = NumberFormat.getNumberInstance();
						number.setMinimumFractionDigits(2);
						String val = number.format(investTotalAmount);
						data.put("superiorInvestTotalAmount", val);
					}
					LOG.info("fn:superiorRateIncreasesTeamMembers,接口响应成功.");
					result.put("state", "0");
					result.put("message", "接口响应成功.");
					result.put("data", data);
					return result;
				}
			} else {
				LOG.info("fn:superiorRateIncreasesTeamMembers,客户账号信息为NULL.");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL.");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:superiorRateIncreasesTeamMembers,系统错误.");
			result.put("state", "1");
			result.put("message", "系统错误.");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * 方法: myRateIncreasesTeamMembers <br>
	 * 描述: 我邀请的加息团队. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月30日 下午3:49:25
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/myRateIncreasesTeamMembers")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> myRateIncreasesTeamMembers(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:myRateIncreasesTeamInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		/**
		 * 业务处理.
		 */
		try {
			// 活动开始日期.
			String beginDate = "2016-09-01 00:00:00";
			// 活动截至日期.
			String endDate = "2016-09-30 23:59:59";
			// 缓存.
			String jedisUserId = JedisUtils.get(token);
			// 数据域.
			Map<String, Object> data = new HashMap<String, Object>();
			// 团队投资总人数(根据投资总额是否大于零来判断是否投资).
			int investTotalMembers = 0;
			// 团队投资总额.
			Double investTotalAmount = 0D;

			if (!StringUtils.isBlank(jedisUserId)) {
				// 当前客户ID.
				String userId = jedisUserId;
				// 查询当前客户所在活动期间进行的投资记录.
				WloanTermInvest myWloanTermInvest = new WloanTermInvest();
				myWloanTermInvest.setUserId(userId);
				myWloanTermInvest.setBeginInvestDate(beginDate);
				myWloanTermInvest.setEndInvestDate(endDate);
				List<WloanTermInvest> myInvestList = wloanTermInvestDao.getSpetInvestInfo(myWloanTermInvest);
				for (WloanTermInvest model : myInvestList) {
					// 累计团队投资总额.
					investTotalAmount = investTotalAmount + model.getAmount();
				}
				// 判断当前客户是否投资.
				if (investTotalAmount > 0) {
					// 累计团队成员投资人数.
					investTotalMembers = investTotalMembers + 1;
				}
				// 三级关系中，查找客户下级成员.
				LevelDistribution entity = new LevelDistribution();
				entity.setUserId(userId);
				entity.setBeginDate(beginDate);
				entity.setEndDate(endDate);
				List<LevelDistribution> list = levelDistributionDao.myRateIncreasesTeamMembers(entity);
				for (LevelDistribution levelDistribution : list) {
					// 查询团队成员在活动期间的投资总额(同时也包括自己).
					WloanTermInvest otherWloanTermInvest = new WloanTermInvest();
					otherWloanTermInvest.setUserId(levelDistribution.getUserInfo().getId());
					otherWloanTermInvest.setBeginInvestDate(beginDate);
					otherWloanTermInvest.setEndInvestDate(endDate);
					List<WloanTermInvest> otherInvestList = wloanTermInvestDao.getSpetInvestInfo(otherWloanTermInvest);
					// 累计当前客户投资总额.
					Double sumInvestAmount = 0D;
					for (WloanTermInvest model : otherInvestList) {
						sumInvestAmount = sumInvestAmount + model.getAmount();
						// 累计团队投资总额.
						investTotalAmount = investTotalAmount + model.getAmount();
					}
					// 累计投资总人数.
					if (sumInvestAmount > 0) {
						// 累计团队成员投资人数.
						investTotalMembers = investTotalMembers + 1;
					}
				}
				// 用户需邀请至少1人注册中投摩根账户才能组成加息团(我邀请的加息团人数).
				if (list.size() <= 0) {
					data.put("members", 0);
					// 投资总人数.
					data.put("investTotalMembers", 0);
					// 投资总额.
					data.put("investTotalAmount", 0);
				} else {
					// 团队成员也包括自己，所以要+1.
					data.put("members", list.size() + 1);
					// 投资总人数.
					data.put("investTotalMembers", investTotalMembers);
					// 投资总额.
					NumberFormat number = NumberFormat.getNumberInstance();
					number.setMinimumFractionDigits(2);
					String val = number.format(investTotalAmount);
					data.put("investTotalAmount", val);
				}
				LOG.info("fn:myRateIncreasesTeamInfo,接口响应成功.");
				result.put("state", "0");
				result.put("message", "接口响应成功.");
				result.put("data", data);
				return result;
			} else {
				LOG.info("fn:myRateIncreasesTeamInfo,客户账号信息为NULL.");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL.");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:myRateIncreasesTeamInfo,系统错误.");
			result.put("state", "1");
			result.put("message", "系统错误.");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * 方法: getBrokerageList <br>
	 * 描述: 佣金列表接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月25日 下午12:43:01
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getBrokerageList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getBrokerageList(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
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

			// 缓存.
			String jedisUserId = JedisUtils.get(token);
			// 数据域.
			Map<String, Object> data = new HashMap<String, Object>();
			if (!StringUtils.isBlank(jedisUserId)) {

				// 当前客户ID.
				String userId = jedisUserId;
				Brokerage entity = new Brokerage();
				entity.setUserId(userId);
				// 分页.
				Page<Brokerage> page = new Page<Brokerage>();
				page.setPageNo(Integer.valueOf(pageNo));
				page.setPageSize(Integer.valueOf(pageSize));

				Page<Brokerage> list = brokerageService.findBrokeragePage(page, entity);
				// 处理封装数据域.
				data.put("pageNo", list.getPageNo());
				data.put("pageSize", list.getPageSize());
				data.put("last", page.getLast());
				data.put("totalCount", list.getCount());
				// 封装后的列表数据.
				List<Brokerage> newList = new ArrayList<Brokerage>();
				Brokerage newModel = null;
				for (Brokerage model : list.getList()) {
					newModel = new Brokerage();
					if (null == model.getFromUserInfo()) {
						// 移动电话.
						newModel.setMobilePhone("客户已销号");
						// 佣金.
						newModel.setAmount(NumberUtils.scaleDouble(0d));
						// 创建时间.
						newModel.setCreateDate(model.getCreateDate());
						newList.add(newModel);
					} else {
						// 移动电话.
						String mobilePhone = model.getFromUserInfo().getName();
						String subMobilePhone = mobilePhone.substring(0, 4);
						String endMobilePhone = mobilePhone.substring(mobilePhone.length() - 3, mobilePhone.length());
						newModel.setMobilePhone(subMobilePhone + "****" + endMobilePhone);
						// 真是姓名.
						newModel.setRealName(model.getUserInfo().getRealName());
						// 佣金.
						newModel.setAmount(NumberUtils.scaleDouble(model.getAmount()));
						// 创建时间.
						newModel.setCreateDate(model.getCreateDate());
						newList.add(newModel);
					}
				}
				data.put("list", newList);
				LOG.info("fn:getBrokerageList,接口响应成功.");
				result.put("state", "0");
				result.put("message", "接口响应成功.");
				result.put("data", data);
				return result;
			} else {
				LOG.info("fn:getBrokerageList,客户账号信息为NULL.");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL.");
				result.put("data", null);
				return result;
			}

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
	 * 
	 * 方法: getInviteFriendsList <br>
	 * 描述: 邀请好友，好友列表展示接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月25日 上午11:48:15
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getInviteFriendsList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getInviteFriendsList(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getInviteFriendsList,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		/**
		 * 业务处理.
		 */
		try {

			// 缓存.
			String jedisUserId = JedisUtils.get(token);
			// 数据域.
			Map<String, Object> data = new HashMap<String, Object>();
			if (!StringUtils.isBlank(jedisUserId)) {

				// 当前客户ID.
				String userId = jedisUserId;
				LevelDistribution entity = new LevelDistribution();
				entity.setUserId(userId);
				// 分页.
				Page<LevelDistribution> page = new Page<LevelDistribution>();
				page.setPageNo(Integer.valueOf(pageNo));
				page.setPageSize(Integer.valueOf(pageSize));
				Page<LevelDistribution> list = levelDistributionService.findLevelDistributionPage(page, entity);
				// 处理封装数据域.
				data.put("pageNo", page.getPageNo());
				data.put("pageSize", page.getPageSize());
				data.put("last", page.getLast());
				data.put("totalCount", page.getCount());
				// 封装后的列表数据.
				List<LevelDistribution> newList = new ArrayList<LevelDistribution>();
				LevelDistribution newModel = null;
				for (LevelDistribution model : list.getList()) {
					newModel = new LevelDistribution();
					// 移动电话.
					if (null == model.getUserInfo()) {
						newModel.setMobilePhone("客户已注销");
						newModel.setRealName("***");
						newModel.setRegisterDate(new Date());
						newList.add(newModel);
					} else {
						String mobilePhone = model.getUserInfo().getName();
						String subMobilePhone = mobilePhone.substring(0, 3);
						String endMobilePhone = mobilePhone.substring(mobilePhone.length() - 4, mobilePhone.length());
						newModel.setMobilePhone(subMobilePhone + "****" + endMobilePhone);
						// 真是姓名.
						newModel.setRealName(model.getUserInfo().getRealName());
						// 注册时间.
						newModel.setRegisterDate(model.getUserInfo().getRegisterDate());
						newList.add(newModel);
					}
				}
				data.put("list", newList);
				LOG.info("fn:getInviteFriendsList,接口响应成功.");
				result.put("state", "0");
				result.put("message", "接口响应成功.");
				result.put("data", data);
				return result;
			} else {
				LOG.info("fn:getInviteFriendsList,客户账号信息为NULL.");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL.");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getInviteFriendsList,系统错误.");
			result.put("state", "1");
			result.put("message", "系统错误.");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * 方法: getInviteFriends <br>
	 * 描述: 你已邀请好友多少人. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月24日 下午4:35:09
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getInviteFriends")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getInviteFriends(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getInviteFriends,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", null);
			return result;
		}

		/**
		 * 业务处理.
		 */
		try {

			// 缓存.
			String jedisUserId = JedisUtils.get(token);
			// 数据域.
			Map<String, Object> data = new HashMap<String, Object>();
			if (!StringUtils.isBlank(jedisUserId)) {
				// 当前客户ID.
				String userId = jedisUserId;
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				// 邀请好友人数.
				int inviteFriends = levelDistributionDao.getInviteFriends(userId);
				data.put("inviteFriends", inviteFriends);
				// 邀请好友投资佣金总额.
				double brokerage = brokerageDao.brokerageTotalAmount(userId);
				data.put("brokerage", NumberUtils.scaleDouble(brokerage));
				// 生成邀请链接.
				data.put("inviteLink", Global.getConfig("wap_invite_url").concat(userInfo != null ? userInfo.getName() : ""));
				LOG.info("fn:getInviteFriends,接口响应成功.");
				result.put("state", "0");
				result.put("message", "接口响应成功.");
				result.put("data", data);
				return result;
			} else {
				LOG.info("fn:getInviteFriends,客户账号信息为NULL.");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL.");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getInviteFriends,系统错误.");
			result.put("state", "1");
			result.put("message", "系统错误.");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 
	 * 方法: getInviteInvestmentFriends <br>
	 * 描述: 获取邀请投资的好友列表(三级分销-钱多多大联盟). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月27日 下午3:26:22
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param type
	 * <br>
	 *            0：已投资.<br>
	 *            1：为投资.
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getInviteInvestmentFriends")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getInviteInvestmentFriends(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("type") String type, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(type) || StringUtils.isBlank(token)) {
			LOG.info("fn:getInviteInvestmentFriends,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);

			if (!StringUtils.isBlank(jedisUserId)) {

				// 客户ID.
				String userId = jedisUserId;

				/**
				 * 数据域.
				 */
				Map<String, Object> data = new HashMap<String, Object>();
				// 存储已投资的好友/未投资的好友，相关信息.
				List<Map<String, Object>> investmentList = null;
				// 存储已投资的好友/未投资的好友，总记录数.
				int totalCount = 0;

				// 分页参数构造.
				Page<Map<String, Object>> page = new Page<Map<String, Object>>();
				page.setPageNo(Integer.valueOf(pageNo));
				page.setPageSize(Integer.valueOf(pageSize));

				// 已投资的好友.
				if (Integer.valueOf(type) == 0) {
					investmentList = levelDistributionDao.queryUserWbidAmount(userId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
					totalCount = levelDistributionDao.countByExample(userId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
					data.put("pageNo", page.getPageNo());
					data.put("pageSize", page.getPageSize());
					data.put("totalCount", totalCount);
					data.put("investmentList", investmentList);

					// 响应.
					LOG.info("fn:getInviteInvestmentFriends,【多多大联盟】获取'已'投资好友列表成功！");
					result.put("state", "0");
					result.put("message", "【多多大联盟】获取'已'投资好友列表成功！");
					result.put("data", data);
					return result;
				}

				// 未投资的好友.
				if (Integer.valueOf(type) == 1) {

					investmentList = levelDistributionDao.notQueryUserWbidAmount(userId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
					totalCount = levelDistributionDao.notCountByExample(userId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
					data.put("pageNo", page.getPageNo());
					data.put("pageSize", page.getPageSize());
					data.put("totalCount", totalCount);
					data.put("investmentList", investmentList);

					// 响应.
					LOG.info("fn:getInviteInvestmentFriends,【多多大联盟】获取'未'投资好友列表成功！");
					result.put("state", "6");
					result.put("message", "【多多大联盟】获取'未'投资好友列表成功！");
					result.put("data", data);
					return result;
				}

			} else {
				LOG.info("fn:getInviteInvestmentFriends,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getInviteInvestmentFriends,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;

	}

	/**
	 * 
	 * 方法: getUserBrokerage <br>
	 * 描述: 获取客户推广佣金. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月26日 上午11:03:52
	 * 
	 * @param token
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserBrokerage")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUserBrokerage(@FormParam("token") String token, @FormParam("from") String from) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getUserBrokerage,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		try {

			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				/**
				 * 数据域.
				 */
				Map<String, Object> data = new HashMap<String, Object>();
				Map<String, Object> map = brokerageService.queryBrokerageMap(userInfo);
				data.put("userId", map.get("userId"));
				data.put("inviteMobilePhone", userInfo != null ? userInfo.getName() : "");
				data.put("inviteUrl", Global.getConfig("wap_invite_url").concat(userInfo != null ? userInfo.getName() : ""));
				data.put("brokerage", String.valueOf(NumberUtils.scaleDouble(Double.parseDouble(map.get("brokerage").toString()))));
				data.put("bidTotalAmount", map.get("bidTotalAmount"));
				data.put("countUsers", map.get("countUsers"));
				data.put("publicAwards", String.valueOf(NumberUtils.scaleDouble(Double.parseDouble(map.get("publicAwards").toString()))));
				double sumBrokerage = Double.parseDouble(map.get("brokerage").toString()) + Double.parseDouble(map.get("publicAwards").toString());
				data.put("sumBrokerage", String.valueOf(NumberUtils.scaleDouble(sumBrokerage)));
				LOG.info("fn:getUserBrokerage,获取客户佣金成功！");
				result.put("state", "0");
				result.put("message", "获取客户佣金成功！");
				result.put("data", data);
				return result;
			} else {
				LOG.info("fn:getUserBrokerage,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("fn:getUserBrokerage,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 
	 * 方法: getUserVouchersList <br>
	 * 描述: 获取用户抵用券列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午7:57:36
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param token
	 * @param state
	 * @return
	 */
	@POST
	@Path("/getUserVouchersList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUserVouchersList(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("token") String token, @FormParam("state") String state) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(state)) {
			LOG.info("fn:getUserVouchersList,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取token.
		 */
		try {
			// 客户账号信息.
			UserInfo userInfo = null;
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				userInfo = userInfoService.getCgb(jedisUserId);
			} else {
				LOG.info("fn:getUserVouchersList,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();
			// 查询客户的抵用券列表.
			AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
			// --
			aUserAwardsHistory.setUserInfo(userInfo);
			// --
			if (Integer.valueOf(state) != 0) {
				aUserAwardsHistory.setState(state);
			}
			// 分页Page.
			Page<AUserAwardsHistory> page = new Page<AUserAwardsHistory>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			Page<AUserAwardsHistory> pageResult = aUserAwardsHistoryService.findVouchersPage(page, aUserAwardsHistory);
			List<UserVoucherPojo> vouchersList = new ArrayList<UserVoucherPojo>();
			if (null != pageResult) {
				List<AUserAwardsHistory> list = pageResult.getList();
				UserVoucherPojo userVoucherPojo = null;
				for (AUserAwardsHistory model : list) {
					userVoucherPojo = new UserVoucherPojo();
					userVoucherPojo.setId(model.getId());
					userVoucherPojo.setGetDate(model.getCreateDate());
					userVoucherPojo.setGetDateStr(DateUtils.formatDateTime(model.getCreateDate()));
					/**
					 * 过期时间，过滤.
					 */
					// Date nowDate = new Date();
					// 如果没有过期时间.
					if (null == model.getOverdueDate()) {
						Date overdueDate = DateUtils.getSpecifiedMonthAfter(model.getCreateDate(), model.getaVouchersDic().getOverdueDays());
						// boolean flag = overdueDate.before(nowDate);
						// if (flag) {
						// continue;
						// }
						userVoucherPojo.setOverdueDate(overdueDate);
					} else {
						Date overdueDate = model.getOverdueDate();
						// boolean flag = overdueDate.before(nowDate);
						// if (flag) {
						// continue;
						// }
						userVoucherPojo.setOverdueDate(overdueDate);
					}
					userVoucherPojo.setAmount(model.getaVouchersDic().getAmount());
					userVoucherPojo.setLimitAmount(model.getaVouchersDic().getLimitAmount());
					userVoucherPojo.setState(model.getState());
					userVoucherPojo.setType(model.getType());
					String spanName = "";
					if (model.getSpans() != null) {
						if (model.getSpans().equals(UserVouchersHistoryService.SPAN_1)) {
							spanName = "通用";
						} else {
							String[] spanList = model.getSpans().split(",");
							for (int i = 0; i < spanList.length; i++) {
								if (i < (spanList.length - 1)) {
									spanName = spanName + spanList[i] + "天 ,";
								} else if (i == (spanList.length - 1)) {
									spanName = spanName + spanList[i] + "天项目可用";
								}

							}
						}
					}
					userVoucherPojo.setSpans(spanName);
					// 优惠券来源
					if (null != model.getaVouchersDic()) {
						String remarks = model.getaVouchersDic().getRemarks();
						if (null != remarks) {
							if (remarks.indexOf("新手") != -1) {// 有返回值
								userVoucherPojo.setVoucherFrom("新手注册");
							} else if (remarks.indexOf("4周年活动群发全用户") != -1) {
								userVoucherPojo.setVoucherFrom("周年活动");
							} else if (remarks.indexOf("积分商城") != -1) {
								userVoucherPojo.setVoucherFrom("积分兑换");
							} else {
								userVoucherPojo.setVoucherFrom(remarks);
							}
						} else {
							userVoucherPojo.setVoucherFrom(remarks);
						}
					} else {
						userVoucherPojo.setVoucherFrom(model.getRemark());
					}

					vouchersList.add(userVoucherPojo);
				}
				data.put("pageNo", pageNo);
				data.put("pageSize", pageSize);
				data.put("totalCount", pageResult.getCount());
				data.put("last", pageResult.getLast());
				data.put("pageCount", pageResult.getLast());
				data.put("vouchersList", vouchersList);
			}

			LOG.info("fn:getUserVouchersList,抵用券列表响应成功了！");
			result.put("state", "0");
			result.put("message", "抵用券列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getUserVouchersList,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: getUserRateCouponList <br>
	 * 描述: 获取用户加息券列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午7:57:50
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param token
	 * @param state
	 * @return
	 */
	@POST
	@Path("/getUserRateCouponList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUserRateCouponList(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("token") String token, @FormParam("state") String state) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(state)) {
			LOG.info("fn:getUserRateCouponList,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取token.
		 */
		try {
			// 客户账号信息.
			UserInfo userInfo = null;
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				userInfo = userInfoService.getCgb(jedisUserId);
			} else {
				LOG.info("fn:getUserRateCouponList,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();
			// 查询客户的抵用券列表.
			AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
			aUserAwardsHistory.setUserInfo(userInfo);
			// --
			if (Integer.valueOf(state) != 0) {
				aUserAwardsHistory.setState(state);
			}
			// 分页Page.
			Page<AUserAwardsHistory> page = new Page<AUserAwardsHistory>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.state");
			Page<AUserAwardsHistory> pageResult = aUserAwardsHistoryService.findRateCouponPage(page, aUserAwardsHistory);

			List<UserRateCouponPojo> rateCouponList = new ArrayList<UserRateCouponPojo>();
			if (null != pageResult) {
				List<AUserAwardsHistory> list = pageResult.getList();
				UserRateCouponPojo userRateCouponPojo = null;
				for (AUserAwardsHistory model : list) {
					userRateCouponPojo = new UserRateCouponPojo();
					userRateCouponPojo.setId(model.getId());
					userRateCouponPojo.setGetDate(model.getCreateDate());
					userRateCouponPojo.setGetDateStr(DateUtils.formatDateTime(model.getCreateDate()));
					/**
					 * 过期时间过滤.
					 */
					// Date nowDate = new Date();
					// 如果没有过期时间.
					if (null == model.getOverdueDate()) {
						Date overdueDate = DateUtils.getSpecifiedMonthAfter(model.getCreateDate(), model.getaRateCouponDic().getOverdueDays());
						// boolean flag = overdueDate.before(nowDate);
						// if (flag) {
						// continue;
						// }
						userRateCouponPojo.setOverdueDate(overdueDate);
					} else {
						Date overdueDate = model.getOverdueDate();
						// boolean flag = overdueDate.before(nowDate);
						// if (flag) {
						// continue;
						// }
						userRateCouponPojo.setOverdueDate(overdueDate);
					}
					userRateCouponPojo.setRate(model.getaRateCouponDic().getRate());
					userRateCouponPojo.setLimitAmount(model.getaRateCouponDic().getLimitAmount());
					userRateCouponPojo.setState(model.getState());
					userRateCouponPojo.setType(model.getType());
					rateCouponList.add(userRateCouponPojo);
				}
				data.put("pageNo", pageNo);
				data.put("pageSize", pageSize);
				data.put("totalCount", pageResult.getCount());
				data.put("pageCount", pageResult.getLast());
				data.put("rateCouponList", rateCouponList);
			}

			LOG.info("fn:getUserRateCouponList,加息券列表响应成功了！");
			result.put("state", "0");
			result.put("message", "加息券列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getUserRateCouponList,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: getUserAwardsHistoryList <br>
	 * 描述: 客户优惠券奖励列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年5月10日 下午9:33:43
	 * 
	 * @param from
	 * @param token
	 * @param state
	 * @return
	 */
	@POST
	@Path("/getUserAwardsHistoryList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getUserAwardsHistoryList(@FormParam("from") String from, @FormParam("token") String token, @FormParam("state") String state, @FormParam("projectId") String projectId) {

		/**
		 * 过期时间，过滤.
		 */
		// Date nowDate = new Date();

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();

		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(state)) {
			LOG.info("fn:getUserAwardsHistoryList,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}

		if (StringUtils.isBlank(projectId)) {
			LOG.info("fn:getUserAwardsHistoryList,抵用券列表！");
			result.put("state", "3");
			result.put("message", "请更新版本");
			result.put("data", null);
			return result;
		}

		/**
		 * 获取token.
		 */
		try {
			// 客户账号信息.
			UserInfo userInfo = null;
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				userInfo = userInfoService.getCgb(jedisUserId);
			} else {
				LOG.info("fn:getUserAwardsHistoryList,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}

			WloanTermProject project = wloanTermProjectDao.get(projectId);
			String span = "";// 项目期限
			if (project != null) {
				span = project.getSpan().toString();
			}

			/**
			 * 数据域.
			 */
			Map<String, Object> data = new HashMap<String, Object>();
			// 查询客户的抵用券列表.
			AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
			aUserAwardsHistory.setUserInfo(userInfo);
			// --
			if (Integer.valueOf(state) != 0) {
				aUserAwardsHistory.setState(state);
			}

			// 优惠券结果集.
			List<UserAwardsHistoryPojo> awardsList = new ArrayList<UserAwardsHistoryPojo>();
			// 优惠券奖励历史POJO.
			UserAwardsHistoryPojo userAwardsHistoryPojo = null;
			// 加息券结果集.
			List<AUserAwardsHistory> rateCouponList = aUserAwardsHistoryService.findRateCoupon(aUserAwardsHistory);
			for (AUserAwardsHistory rateCoupon : rateCouponList) {
				userAwardsHistoryPojo = new UserAwardsHistoryPojo();
				userAwardsHistoryPojo.setId(rateCoupon.getId());
				userAwardsHistoryPojo.setGetDate(rateCoupon.getCreateDate());
				// 如果没有过期时间.
				if (null == rateCoupon.getOverdueDate()) {
					Date overdueDate = DateUtils.getSpecifiedMonthAfter(rateCoupon.getCreateDate(), rateCoupon.getaRateCouponDic().getOverdueDays());
					// boolean flag = overdueDate.before(nowDate);
					// if (flag) {
					// continue;
					// }
					userAwardsHistoryPojo.setOverdueDate(overdueDate);
				} else {
					Date overdueDate = rateCoupon.getOverdueDate();
					// boolean flag = overdueDate.before(nowDate);
					// if (flag) {
					// continue;
					// }
					userAwardsHistoryPojo.setOverdueDate(overdueDate);
				}
				userAwardsHistoryPojo.setValue(rateCoupon.getaRateCouponDic().getRate());
				userAwardsHistoryPojo.setLimitAmount(rateCoupon.getaRateCouponDic().getLimitAmount());
				userAwardsHistoryPojo.setState(rateCoupon.getState());
				userAwardsHistoryPojo.setType(rateCoupon.getType());
				awardsList.add(userAwardsHistoryPojo);
			}
			// 抵用券结果集.
			List<AUserAwardsHistory> vouchersList = aUserAwardsHistoryService.findVouchers(aUserAwardsHistory);
			for (AUserAwardsHistory vouchers : vouchersList) {
				userAwardsHistoryPojo = new UserAwardsHistoryPojo();
				userAwardsHistoryPojo.setId(vouchers.getId());
				userAwardsHistoryPojo.setGetDate(vouchers.getCreateDate());
				// 如果没有过期时间.
				if (null == vouchers.getOverdueDate()) {
					Date overdueDate = DateUtils.getSpecifiedMonthAfter(vouchers.getCreateDate(), vouchers.getaVouchersDic().getOverdueDays());
					// boolean flag = overdueDate.before(nowDate);
					// if (flag) {
					// continue;
					// }
					userAwardsHistoryPojo.setOverdueDate(overdueDate);
				} else {
					Date overdueDate = vouchers.getOverdueDate();
					// boolean flag = overdueDate.before(nowDate);
					// if (flag) {
					// continue;
					// }
					userAwardsHistoryPojo.setOverdueDate(overdueDate);
				}
				userAwardsHistoryPojo.setValue(vouchers.getaVouchersDic().getAmount());
				userAwardsHistoryPojo.setLimitAmount(vouchers.getaVouchersDic().getLimitAmount());
				userAwardsHistoryPojo.setState(vouchers.getState());
				userAwardsHistoryPojo.setType(vouchers.getType());
				// 判断抵用券项目使用期限范围
				String spans = vouchers.getSpans();
				if (spans != null) {
					if (spans.equals(UserVouchersHistoryService.SPAN_1)) {// 通用
						userAwardsHistoryPojo.setSpans("通用");
						awardsList.add(userAwardsHistoryPojo);
					} else if (spans.contains(span)) {
						String spanName = "";
						String[] spanList = spans.split(",");
						for (int i = 0; i < spanList.length; i++) {
							if (i < (spanList.length - 1)) {
								spanName = spanName + spanList[i] + "天 ,";
							} else if (i == (spanList.length - 1)) {
								spanName = spanName + spanList[i] + "天";
							}

						}
						userAwardsHistoryPojo.setSpans(spanName + "项目可用");
						awardsList.add(userAwardsHistoryPojo);
					}
				}

			}
			// 优惠券总记录.
			data.put("totalCount", awardsList.size());
			// 优惠券列表.
			data.put("awardsList", awardsList);
			LOG.info("fn:getUserAwardsHistoryList,优惠券奖励列表响应成功了！");
			result.put("state", "0");
			result.put("message", "优惠券奖励列表响应成功了！");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getUserAwardsHistoryList,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

		return result;
	}

	/**
	 * 我的人脉列表接口
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getMyInviteInvestmentFriends")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getMyInviteInvestmentFriends(@FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getInviteInvestmentFriends,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				// 客户ID.
				String userId = jedisUserId;
				/**
				 * 数据域.
				 */
				Map<String, Object> data = new HashMap<String, Object>();
				List<Map<String, Object>> investmentList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> inList = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
				// 存储总记录数.
				int totalCount = 0;
				// 分页参数构造.
				Page<Map<String, Object>> page = new Page<Map<String, Object>>();
				page.setPageNo(Integer.valueOf(pageNo));
				page.setPageSize(Integer.valueOf(pageSize));

				investmentList = levelDistributionDao.findListByUserId(userId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
				totalCount = levelDistributionDao.countByUserId(userId, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
				if (investmentList != null && investmentList.size() > 0) {
					for (int i = 0; i < investmentList.size(); i++) {
						Map<String, Object> map = investmentList.get(i);
						// 我的人脉列表
						Map<String, Object> reqMap = new HashMap<String, Object>();
						// 根据用户ID查询是否投资
						WloanTermInvest wloanTermInvest = new WloanTermInvest();
						UserInfo userInfo = new UserInfo();
						userInfo.setId(map.get("userId").toString());
						wloanTermInvest.setUserInfo(userInfo);
						List<WloanTermInvest> wlist = wloanTermInvestDao.findList(wloanTermInvest);
						if (wlist != null && wlist.size() > 0) {
							// 投资
							reqMap.put("isLoan", "1");
						} else {
							// 未投资
							reqMap.put("isLoan", "0");
						}
						// 被邀请人
						reqMap.put("name", Util.hideString((String) map.get("name"), 3, 4));
						// 注册日期
						reqMap.put("registtime", DateUtils.formatDate((Date) map.get("registtime"), "yyyy-MM-dd"));
						// 是否实名认证 1未认证 2已认证
						reqMap.put("checkIdCard", map.get("idCard") == null ? "1" : "2");
						// 是否绑卡
						reqMap.put("bindBankCard", map.get("bindBankCard"));
						inList.add(reqMap);

						// 我的佣金明细

						UserTransDetail userTransDetail = new UserTransDetail();
						userTransDetail.setUserId(userId.toString());
						userTransDetail.setTrustType(UserTransDetailService.trust_type9);
						List<UserTransDetail> tranList = userTransDetailDao.findList(userTransDetail);
						if (tranList != null && tranList.size() > 0) {
							for (UserTransDetail userDetail : tranList) {
								Map<String, Object> reqDetailMap = new HashMap<String, Object>();
								// 被邀请人
								reqDetailMap.put("name", Util.hideString((String) map.get("name"), 3, 4));
								// 交易日期
								reqDetailMap.put("registtime", DateUtils.formatDate((Date) userDetail.getTransDate(), "yyyy-MM-dd"));
								// 事件类型
								reqDetailMap.put("type", userDetail.getRemarks());
								// 奖励金额
								reqDetailMap.put("amount", userDetail.getAmount());
								// 奖励模式
								reqDetailMap.put("model", "返现");
								detailList.add(reqDetailMap);
							}
						}
						// detailList.add(reqDetailMap);
					}
				}
				// 数据域
				data.put("pageNo", page.getPageNo());
				data.put("pageSize", page.getPageSize());
				data.put("totalCount", totalCount);
				int last = 1;
				if (totalCount != 0) {
					last = ((totalCount % Integer.valueOf(pageSize)) == 0) ? totalCount / Integer.valueOf(pageSize) : ((totalCount / Integer.valueOf(pageSize) + 1));
				}
				System.out.println(last);
				data.put("last", last);
				data.put("investmentList", inList);
				data.put("detailList", detailList);
				// 响应.
				LOG.info("fn:getMyInviteInvestmentFriends,【多多大联盟】获取我的人脉列表成功！");
				result.put("state", "0");
				result.put("message", "获取我的人脉列表成功！");
				result.put("data", data);
				return result;

			} else {
				LOG.info("fn:getInviteInvestmentFriends,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getInviteInvestmentFriends,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 我的人脉列表接口
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/dragonBoatSource")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> dragonBoatSource(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getInviteInvestmentFriends,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {

				// 客户ID.
				String userId = jedisUserId;
				/**
				 * 数据域.
				 */
				Map<String, Object> data = new HashMap<String, Object>();
				List<Map<String, Object>> investmentParentList = new ArrayList<Map<String, Object>>();

				// 查找邀请用户的信息
				investmentParentList = levelDistributionDao.findListByUserIdParent(userId);
				// 查找当前用户的积分
				UserBounsPoint userBounsPoint = new UserBounsPoint();
				userBounsPoint.setUserId(userId);
				List<UserBounsPoint> userBounsPoints = userBounsPointService.findList(userBounsPoint);

				for (int a = 0; a < userBounsPoints.size(); a++) {
					UserBounsPoint entity = userBounsPoints.get(0);
					System.out.println("积分" + entity.getScore());
					data.put("userScore", entity.getScore());

				}
				// 邀请好友人数
				int inviteFriends = investmentParentList.size();

				// 邀请好友投资佣金总额.
				Double brokerage = 0d;
				UserTransDetail userTransDetail = new UserTransDetail();
				userTransDetail.setRemarks("现金奖励");
				List<UserTransDetail> list = userTransDetailDao.findList(userTransDetail);
				if (list != null && list.size() > 0) {
					for (UserTransDetail transDetail : list) {
						brokerage = brokerage + NumberUtils.scaleDouble(transDetail.getAmount());
					}
				}

				// 邀请好友出借总金额
				double bidTotalAmounts = levelDistributionDao.queryUserWbidSumAmountBote(userId);
				// Map<String, Object> mapF =
				// brokerageService.queryBrokerageMap(principal.getUserInfo());

				// double
				// bidTotalAmounts=Double.parseDouble(mapF.get("bidTotalAmount").toString());
				if (investmentParentList != null && investmentParentList.size() > 0) {
					for (int i = 0; i < investmentParentList.size(); i++) {
						Map<String, Object> map = investmentParentList.get(i);
						map.put("name", Util.hideString((String) map.get("name"), 3, 4));
						map.put("registtime", DateUtils.formatDate((Date) map.get("registtime"), "yyyy-MM-dd"));
					}
					data.put("investmentParentList", investmentParentList);
				} else {
					data.put("investmentParentList", null);
				}

				data.put("inviteFriends", inviteFriends);
				data.put("brokerage", brokerage);
				data.put("bidTotalAmount", Math.round(bidTotalAmounts * 100) / 100);

				// 响应.
				LOG.info("fn:dragonBoatSource,【多多大联盟】获取我的人脉列表成功！");
				result.put("state", "0");
				result.put("message", "获取我的人脉列表成功！");
				result.put("data", data);
				return result;
			} else {
				LOG.info("fn:dragonBoatSource,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:dragonBoatSource,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 首页新手引导
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/userGuidance")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userGuidance(@FormParam("from") String from, @FormParam("token") String token, @FormParam("type") String type) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(type)) {
			LOG.info("fn:userGuidance,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) {
				LOG.info("fn:userGuidance,系统超时");
				result.put("state", "5");
				result.put("message", "系统超时！");
				result.put("data", null);
				return result;
			}
			if (!StringUtils.isBlank(jedisUserId)) {
				Map<String, Object> data = new HashMap<String, Object>();
				// 客户ID.
				UserInfo userInfo = userInfoService.getCgb(jedisUserId);
				if (userInfo == null) {
					userInfo = userInfoService.get(jedisUserId);
				}
				if(userInfo!=null) {
					if(userInfo.getCertificateChecked()==null) {
						data.put("certificateChecked", "1");//客户未开户
					}else {
						data.put("certificateChecked", userInfo.getCertificateChecked());
					}
					if(userInfo.getCgbBindBankCardState()==null) {
						data.put("cgbBindBankCardState", "1");//银行卡未认证
					}else {
						data.put("cgbBindBankCardState", userInfo.getCgbBindBankCardState());
					}
				}
				CgbUserBankCard bankCard = cgbUserBankCardService.findByUserId(jedisUserId);
				if (bankCard != null||userInfo.getCertificateChecked()==2) {
					LOG.info("fn:userGuidance,已开户！");
					// 查询是否做过风险测评
					QuestionUser questionUser = new QuestionUser();
					questionUser.setUser(userInfo);
					List<QuestionUser> questionList = questionUserDao.findList(questionUser);
					if (questionList != null && questionList.size() > 0) {
						LOG.info("fn:userGuidance,已完成风险评测！");
						data.put("step", 5);// 已完成风险评测
						result.put("state", "0");
						result.put("message", "已完成风险评测！");
						result.put("data", data);
					} else {
						if ("0".equals(type)) {// 自动加载
							if (userInfo.getRiskFirst() == null) {// 第一次弹
								// 继续
								LOG.info("fn:userGuidance,未做风险评测！");
								data.put("step", 4);// 未做风险评测
								
								result.put("state", "0");
								result.put("message", "未做风险评测！");
								result.put("data", data);
								// 风险评测赋值
								userInfo.setRiskFirst("1");
								userInfoService.updateUserInfo(userInfo);
							} else {// 风险评测弹过
								data.put("step", 6);// 不弹框！
								result.put("state", "0");
								result.put("message", "不弹框！");
								result.put("data", data);
							}
						} else {
							LOG.info("fn:userGuidance,未做风险评测！");
							data.put("step", 4);// 未做风险评测
							result.put("state", "0");
							result.put("message", "未做风险评测！");
							result.put("data", data);
						}

					}
				} else {
					if ("0".equals(type)) {// 自动加载
						if (userInfo.getBankFirst() == null) {// 第一次弹
							// 继续
							LOG.info("fn:userGuidance,未开通存管银行！");
							data.put("step", 3);// 未开通存管银行
							result.put("state", "0");
							result.put("message", "未开通存管银行！");
							result.put("data", data);
							// bankFirst赋值
							userInfo.setBankFirst("1");
							userInfoService.updateUserInfo(userInfo);
						} else {// 银行弹过
							data.put("step", 6);// 不弹框！
							result.put("state", "0");
							result.put("message", "不弹框！");
							result.put("data", data);
						}
					} else {
						LOG.info("fn:userGuidance,未开通存管银行！");
						data.put("step", 3);// 未开通存管银行
						result.put("state", "0");
						result.put("message", "未开通存管银行！");
						result.put("data", data);
					}
				}

			} else {
				LOG.info("fn:dragonBoatSource,客户账号信息为NULL！");
				result.put("state", "5");
				result.put("message", "客户账号信息为NULL！");
				result.put("data", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:userGuidance,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;

	}

	/**
	 * 个人投资总额
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/userSumInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userSumInvest(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getInviteInvestmentFriends,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				if (null != userInfoService.getCgb(jedisUserId)) {
					Map<String, Object> data = new HashMap<String, Object>();
					// 客户ID.
					String userId = jedisUserId;
					Double userSumInvest = 0.0;
					WloanTermInvest wloanTermInvest = new WloanTermInvest();
					wloanTermInvest.setUserId(userId);
					wloanTermInvest.setBeginBeginDate(DateUtils.parseDate("2018.05.11"));
					wloanTermInvest.setEndBeginDate(DateUtils.parseDate("2018.06.11"));
					List<WloanTermInvest> list = wloanTermInvestDao.findList(wloanTermInvest);
					for (WloanTermInvest invest : list) {
						if ("1".equals(invest.getState())) {
							if (Integer.parseInt(invest.getSpan()) >= 90) {
								userSumInvest += invest.getAmount();
							}

						}
					}
					LOG.info("fn:userSumInvest,查询成功！");
					data.put("sumAmount", userSumInvest);// 查询成功！
					result.put("state", "0");
					result.put("message", "查询成功！");
					result.put("data", data);
				} else {
					LOG.info("fn:dragonBoatSource,客户账号信息为NULL！");
					result.put("state", "5");
					result.put("message", "客户账号信息为NULL！");
					result.put("data", null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:userSumInvest,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}
		return result;

	}

	/**
	 * 世界杯---领取抵用券
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getWorldCupVouAmount")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getWorldCupVouAmount(@FormParam("from") String from, @FormParam("token") String token, @FormParam("vouAmount") String vouAmount) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginDate = "2018-07-02 00:00:00";
		String endDate = "2018-07-31 23:59:59";
		Date now = new Date();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token) || StringUtils.isBlank(vouAmount)) {
			LOG.info("fn:getWorldCupVouAmount,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("data", null);
			return result;
		}
		try {
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				if (null != userInfoService.getCgb(jedisUserId)) {
					// 客户ID.
					String userId = jedisUserId;
					// 判断时间是否在活动期内
					if (now.before(sdf.parse(endDate)) && now.after(sdf.parse(beginDate))) {
						// 判断活动期内是否第一次领取
						AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
						aUserAwardsHistory.setUserId(userId);
						aUserAwardsHistory.setValue(vouAmount + ".0");
						aUserAwardsHistory.setRemark("2018WorldCup");
						List<AUserAwardsHistory> aUserAwardsHistorieList = aUserAwardsHistoryDao.findList(aUserAwardsHistory);
						if (aUserAwardsHistorieList.size() == 0) {
							List<Double> voucherList = new ArrayList<Double>();
							if (vouAmount.equals("10")) {
								// 10元抵用券
								voucherList.add(10d);
								addVouchers(userId, voucherList, "30,90,120,180,360", "2018WorldCup");
							} else if (vouAmount.equals("20")) {
								// 20元抵用券
								voucherList.add(20d);
								addVouchers(userId, voucherList, "30,90,120,180,360", "2018WorldCup");
							} else if (vouAmount.equals("30")) {
								// 30元抵用券
								voucherList.add(30d);
								addVouchers(userId, voucherList, "30,90,120,180,360", "2018WorldCup");
							} else if (vouAmount.equals("50")) {
								// 50元抵用券
								voucherList.add(50d);
								addVouchers(userId, voucherList, "90,120,180,360", "2018WorldCup");
							} else if (vouAmount.equals("100")) {
								// 100元抵用券
								voucherList.add(100d);
								addVouchers(userId, voucherList, "90,120,180,360", "2018WorldCup");
							} else if (vouAmount.equals("200")) {
								// 200元抵用券
								voucherList.add(200d);
								addVouchers(userId, voucherList, "90,120,180,360", "2018WorldCup");
							}
						} else {
							LOG.info("fn:getWorldCupVouAmount，您已经领取过此抵用券.");
							result.put("state", "3");
							result.put("message", "您已经领取过此抵用券.");
							return result;
						}
						LOG.info("fn:getWorldCupVouAmount,[世界杯]领取抵用券成功！");
						result.put("state", "0");
						result.put("message", "[世界杯]领取抵用券成功！");
					} else {
						LOG.info("fn:getWorldCupVouAmount,活动还未开始");
						result.put("state", "5");
						result.put("message", "活动还未开始");
					}
				} else {
					LOG.info("fn:getWorldCupVouAmount，系统超时.");
					result.put("state", "4");
					result.put("message", "系统超时");
					return result;
				}
			} else {
				LOG.info("fn:getWorldCupVouAmount，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getWorldCupVouAmount,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			return result;
		}
		return result;

	}

	/**
	 * 发放抵用劵
	 * 
	 * @param userId
	 * @param voucherList
	 */
	public void addVouchers(String userId, List<Double> voucherList, String spans, String remark) {

		// 先查询是否有对应金额的抵用劵
		if (voucherList != null && voucherList.size() > 0) {
			for (Double voucher : voucherList) {
				AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
				AVouchersDic aVouchersDic = aVouchersDicDao.findByVoucher(voucher);
				if (aVouchersDic != null) {
					aUserAwardsHistory.setId(String.valueOf(IdGen.randomLong()));
					aUserAwardsHistory.setAwardId(aVouchersDic.getId());
					aUserAwardsHistory.setCreateDate(new Date());
					aUserAwardsHistory.setUpdateDate(new Date());
					aUserAwardsHistory.setUserId(userId);
					aUserAwardsHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), aVouchersDic.getOverdueDays()));
					aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
					aUserAwardsHistory.setType("1");// 类型为:抵用劵
					aUserAwardsHistory.setValue(voucher.toString());
					aUserAwardsHistory.setSpans(spans);
					aUserAwardsHistory.setRemark(remark);
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
	 * 世界杯---已获取抵用券列表
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getWorldCupVouAmountList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getWorldCupVouAmountList(@FormParam("from") String from, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<AUserAwardsHistory> list = new ArrayList<AUserAwardsHistory>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		String beginDate = "2018-07-02 00:00:00";
		String endDate = "2018-07-31 23:59:59";
		Date now = new Date();
		/**
		 * 判断参数是否传递.
		 */
		if (StringUtils.isBlank(from) || StringUtils.isBlank(token)) {
			LOG.info("fn:getWorldCupVouAmount,缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			result.put("list", list);
			return result;
		}
		try {
			/**
			 * 缓存中获取客户信息.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				if (null != userInfoService.getCgb(jedisUserId)) {
					// 客户ID.
					String userId = jedisUserId;
					// 判断时间是否在活动期内
					if (now.before(sdf.parse(endDate)) && now.after(sdf.parse(beginDate))) {
						// 判断活动期内是否第一次领取
						AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
						aUserAwardsHistory.setUserId(userId);
						aUserAwardsHistory.setRemark("2018WorldCup");
						list = aUserAwardsHistoryDao.findList(aUserAwardsHistory);
						for (AUserAwardsHistory uservouAmount : list) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("amount", uservouAmount.getValue());
							returnList.add(map);
						}
					}
					LOG.info("fn:getWorldCupVouAmount,[世界杯]抵用券列表响应成功！");
					result.put("state", "0");
					result.put("message", "[世界杯]抵用券列表响应成功！");
					result.put("list", returnList);
				} else {
					LOG.info("fn:getWorldCupVouAmount，系统超时.");
					result.put("state", "4");
					result.put("message", "系统超时");
					result.put("list", returnList);
					return result;
				}
			} else {
				LOG.info("fn:getWorldCupVouAmount，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("list", returnList);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("fn:getWorldCupVouAmount,系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			result.put("list", returnList);
			return result;
		}
		return result;

	}
}
