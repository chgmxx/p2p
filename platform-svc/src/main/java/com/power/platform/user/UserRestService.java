package com.power.platform.user;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.filter.utils.QRCodeUtils;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.LevelDistributionService;
import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserSignedDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.entity.UserSigned;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.bouns.services.UserSignedService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.pay.cash.service.UserCashService;
import com.power.platform.pay.utils.BankCodeUtils;
import com.power.platform.pay.utils.CGBBankCodeUtils;
import com.power.platform.pay.utils.FuncUtils;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.questionnaire.dao.AnswerDao;
import com.power.platform.questionnaire.dao.QuestionUserDao;
import com.power.platform.questionnaire.entity.Answer;
import com.power.platform.questionnaire.entity.QuestionUser;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sms.service.SendEmailService;
import com.power.platform.sys.dao.AnnexFileDao;
import com.power.platform.sys.entity.AnnexFile;
import com.power.platform.sys.service.AnnexFileService;
import com.power.platform.user.pojo.UserRepayPlanPojo;
import com.power.platform.userinfo.dao.RegistUserDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.dao.UserLoginDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserBankCard;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserBankCardService;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Path("/user")
@Service("userRestService")
@Produces(MediaType.APPLICATION_JSON)
public class UserRestService {

	private static final Logger LOG = LoggerFactory.getLogger(UserRestService.class);

	/**
	 * 3：ANDROID.
	 */
	public static final String FROM_3 = "3";
	/**
	 * 4：IOS.
	 */
	public static final String FROM_4 = "4";
	/**
	 * 30：核心企业图片.
	 */
	public static final String MIDDLEMEN_ANNEX_FILE_30 = "30";

	@Autowired
	private RegistUserDao registUserDao;
	@Autowired
	private UserAccountInfoDao userAccountInfoDao;
	@Autowired
	private UserLoginDao userLoginDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private SendEmailService sendEmailService;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private UserCashService userCashService;
	@Resource
	private UserSignedDao userSignedDao;
	@Autowired
	private QuestionUserDao questionUserDao;
	@Autowired
	private AnswerDao answerDao;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private AnnexFileDao annexFileDao;
	@Autowired
	private AnnexFileService annexFileService;

	// 银行存管
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Resource
	private UserBounsHistoryDao userBounsHistoryDao;
	@Resource
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private CreditUserInfoService creditUserInfoService;
	@Resource
	private CreditAnnexFileService creditAnnexFileService;
	@Resource
	private UserInfoService userInfoService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private LevelDistributionService levelDistributionService;
	@Resource
	private LevelDistributionDao levelDistributionDao;

	/**
	 * 
	 * methods: findMyInvestList <br>
	 * description: 查找我的投资出借清单. <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月21日 上午9:22:57
	 * 
	 * @param token
	 *            用户登陆唯一标识
	 * @param from
	 *            用户访问来源
	 * @param projectState
	 *            标的状态
	 * @param projectProductType
	 *            标的产品类型
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页展示记录
	 * @return
	 */
	@POST
	@Path("/findMyInvestList")
	public Map<String, Object> findMyInvestList(@FormParam("token") String token, @FormParam("from") String from, @FormParam("projectState") String projectState, @FormParam("projectProductType") String projectProductType, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(projectState) || StringUtils.isBlank(projectProductType) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			result.put("data", null);
			return result;
		}

		// 数据源.
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			// 封装我的出借Page对象
			Page<WloanTermInvest> page = new Page<WloanTermInvest>();
			page.setPageNo(StringUtils.toInteger(pageNo)); // 页码
			page.setPageSize(StringUtils.toInteger(pageSize)); // 每页展示记录数
			// 封装我的出借实体类
			WloanTermInvest wLoanTermInvest = new WloanTermInvest();
			wLoanTermInvest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1); // 出借成功
			/**
			 * 获取token.
			 */
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoDao.getCgb(jedisUserId); // 接入存管行用户
				if (user == null) { // 连连用户
					user = userInfoDao.get(jedisUserId);
				}
				if (user != null) {
					// set用户ID.
					wLoanTermInvest.setUserId(user.getId());
				}
			}
			// 封装标的实体类
			WloanTermProject wloanTermProject = new WloanTermProject();
			/**
			 * 标的产品类型查询.
			 */
			// 安心投
			if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(projectProductType)) {
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
			} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(projectProductType)) { // 供应链
				wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
			} else { // 全部
				List<String> projectProductTypeItem = new ArrayList<String>();
				projectProductTypeItem.add(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
				projectProductTypeItem.add(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
				wloanTermProject.setProjectTypeItem(projectProductTypeItem);
			}
			/**
			 * 标的流转状态查询.
			 */
			if (WloanTermProjectService.ONLINE.equals(projectState)) { // 募集中
				wloanTermProject.setState(WloanTermProjectService.ONLINE);
			} else if (WloanTermProjectService.FULL.equals(projectState)) { // 满标中
				wloanTermProject.setState(WloanTermProjectService.FULL);
			} else if (WloanTermProjectService.REPAYMENT.equals(projectState)) { // 还款中
				wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			} else if (WloanTermProjectService.FINISH.equals(projectState)) { // 已结束
				wloanTermProject.setState(WloanTermProjectService.FINISH);
			} else if (WloanTermProjectService.P2P_TRADE_BID_CANCEL.equals(projectState)) { // 流标
				wloanTermProject.setState(WloanTermProjectService.P2P_TRADE_BID_CANCEL);
			} else { // 全部
				List<String> stateItem = new ArrayList<String>();
				stateItem.add(WloanTermProjectService.ONLINE);
				stateItem.add(WloanTermProjectService.FULL);
				stateItem.add(WloanTermProjectService.REPAYMENT);
				stateItem.add(WloanTermProjectService.FINISH);
				stateItem.add(WloanTermProjectService.P2P_TRADE_BID_CANCEL);
				wloanTermProject.setStateItem(stateItem);
			}
			wLoanTermInvest.setWloanTermProject(wloanTermProject);
			// 分页查询
			Page<WloanTermInvest> pages = wloanTermInvestService.findPage(page, wLoanTermInvest);
			if (pages != null) {
				List<WloanTermInvest> invests = pages.getList();
				for (int i = 0; i < invests.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("bidId", invests.get(i).getId()); // 出借ID
					if (invests.get(i).getWloanTermProject() != null) { // 标的非空判断
						map.put("projectId", invests.get(i).getWloanTermProject().getId()); // 标的ID
						map.put("projectName", invests.get(i).getWloanTermProject().getName()); // 标的名称
						map.put("projectType", invests.get(i).getWloanTermProject().getProjectType()); // 标的类型
						map.put("projectProductType", invests.get(i).getWloanTermProject().getProjectProductType()); // 标的产品类型
						map.put("projectState", invests.get(i).getWloanTermProject().getState()); // 标的流转状态
						map.put("projectSpan", invests.get(i).getWloanTermProject().getSpan()); // 标的期限
						map.put("projectRate", NumberUtils.scaleDoubleStr(invests.get(i).getWloanTermProject().getAnnualRate())); // 标的年化利率
						map.put("projectSn", invests.get(i).getWloanTermProject().getSn()); // 标的编号
						if (WloanTermProjectService.ONLINE.equals(invests.get(i).getWloanTermProject().getState())) { // 募集中
							// 展示标的流标日期
							map.put("bidEndDate", invests.get(i).getWloanTermProject().getLoanDate() == null ? "" : DateUtils.formatDate(invests.get(i).getWloanTermProject().getLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						} else {
							// 展示标的结束日期
							map.put("bidEndDate", invests.get(i).getWloanTermProject().getEndDate() == null ? "" : DateUtils.formatDate(invests.get(i).getWloanTermProject().getEndDate(), "yyyy-MM-dd HH:mm:ss"));
						}
						// 核心企业信息
						WloanTermProject project = wloanTermProjectService.get(invests.get(i).getWloanTermProject().getId());
						if (project != null) {
							String middlemenId = project.getReplaceRepayId();
							if (middlemenId != null) {
								CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
								if (creditUserInfo != null) {
									map.put("creditMiddlemenName", creditUserInfo.getEnterpriseFullName()); // 核心企业名称
								} else {
									map.put("creditMiddlemenName", "");
								}
								CreditAnnexFile annexFile = new CreditAnnexFile();
								annexFile.setOtherId(middlemenId);
								annexFile.setType(MIDDLEMEN_ANNEX_FILE_30);
								List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
								if (annexFileList != null && annexFileList.size() > 0) {
									map.put("creditMiddlemenUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId); // 核心企业图片
								} else {
									map.put("creditMiddlemenUrl", "");
								}
							} else {
								map.put("creditMiddlemenName", "");
								map.put("creditMiddlemenUrl", "");
							}
						}
					} else {
						map.put("projectId", "");
						map.put("projectName", "");
						map.put("projectType", "");
						map.put("projectProductType", "");
						map.put("projectState", "");
						map.put("projectSpan", "");
						map.put("projectRate", "");
						map.put("projectSn", "");
						map.put("bidEndDate", "");
						map.put("creditMiddlemenName", "");
						map.put("creditMiddlemenUrl", "");
					}
					map.put("bidAmount", NumberUtils.scaleDoubleStr(invests.get(i).getAmount())); // 出借金额
					map.put("bidInterest", NumberUtils.scaleDoubleStr(invests.get(i).getInterest())); // 出借收益
					map.put("bidDate", DateUtils.formatDate(invests.get(i).getBeginDate(), "yyyy-MM-dd")); // 出借日期，年月日
					map.put("bidDateTime", DateUtils.formatDate(invests.get(i).getBeginDate(), "yyyy-MM-dd HH:mm:ss")); // 出借日期，年月日时分秒
					Date newVersionDate = DateUtils.parseDate("2016-06-16 00:00:00"); // 新版本上线日期.
					if (invests.get(i).getBeginDate().before(newVersionDate)) { // 判断新旧合同展示URL.
						map.put("bidSignature", "http://112.126.73.20/" + invests.get(i).getContractPdfPath());
					} else {
						map.put("bidSignature", Global.getConfig("pdf_show_path") + invests.get(i).getContractPdfPath());
					}
					list.add(map);
				}
			}
			data.put("pageNo", pages.getPageNo()); // 当前页码
			data.put("pageSize", pages.getPageSize()); // 当前页面大小
			data.put("totalCount", pages.getCount()); // 总记录数
			data.put("pageCount", pages.getLast()); // 尾页索引
			data.put("bidList", list); // 出借列表
			result.put("state", "0");
			result.put("message", "我的出借数据接口请求成功");
			result.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("pageNo", 1);
			data.put("pageSize", 10);
			data.put("totalCount", 0);
			data.put("userBidHistoryList", "[]");
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", data);
		}

		return result;
	}

	/**
	 * 
	 * 方法: findUserRepayPlans <br>
	 * 描述: 查找用户的还款计划. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月31日 上午10:37:16
	 * 
	 * @param from
	 *            请求来源
	 * @param token
	 *            用户唯一标识
	 * @return
	 */
	@POST
	@Path("/findUserRepayPlanStatistical")
	public Map<String, Object> findUserRepayPlanStatistical(@FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("token") String token, @FormParam("nowDate") String nowDate) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据集.
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(token) || StringUtils.isBlank(nowDate)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		if (from.equals(FROM_3)) {
			LOG.info(this.getClass() + "，请求来源：ANDROID.");
		} else if (from.equals(FROM_4)) {
			LOG.info(this.getClass() + "，请求来源：IOS.");
		}

		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoDao.getCgb(jedisUserId);
				if (null != user) {
					// 封装客户还款计划查询.
					WloanTermUserPlan wloanTermUserPlan = new WloanTermUserPlan();
					// 分页.
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(Integer.parseInt(pageNo));
					page.setPageSize(Integer.parseInt(pageSize));
					wloanTermUserPlan.setPage(page);
					// 设置用户信息.
					wloanTermUserPlan.setUserInfo(user);
					// 还款中.
					wloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					// 还款日.
					wloanTermUserPlan.setRepaymentDate(DateUtils.getShortDateOfString(nowDate));
					List<WloanTermUserPlan> list = wloanTermUserPlanDao.findUserRepayPlanStatistical(wloanTermUserPlan);
					LOG.info("pageNo = " + page.getPageNo() + "，pageSize = " + page.getPageSize() + "，count = " + page.getCount());

					// 获取总页数
					// 总页数.
					long pageCount;
					if (page.getCount() % page.getPageSize() == 0) {
						pageCount = page.getCount() / page.getPageSize();
					} else
						pageCount = page.getCount() / page.getPageSize() + 1;

					LOG.info("总页数 -> pageCount =  " + pageCount);

					if (page.getPageNo() > pageCount) {
						// 总页数.
						data.put("count", page.getCount());
						// 页码.
						data.put("pageNo", page.getPageNo());
						// 每页大小.
						data.put("pageSize", page.getPageSize());
						// 总页码.
						data.put("pageCount", pageCount);
						// 计划列表.
						data.put("plans", new ArrayList<UserRepayPlanPojo>());
						// 成功状态.
						result.put("state", "0");
						// 成功消息.
						result.put("message", "接口请求成功！");
						// 数据域.
						result.put("data", data);
						return result;
					} else {
						// 新的数据域.
						List<UserRepayPlanPojo> pojos = new ArrayList<UserRepayPlanPojo>();
						for (WloanTermUserPlan entity : list) { // 遍历客户还款计划.
							entity.setUserInfo(user); // 设置用户信息.
							Double remainingRepayAmount = wloanTermUserPlanDao.getWaitRepayMoney(entity);
							UserRepayPlanPojo pojo = new UserRepayPlanPojo();
							pojo.setProjectName(entity.getWloanTermProject().getName());
							pojo.setProjectSn(entity.getWloanTermProject().getSn());
							pojo.setRepaymentDate(DateUtils.formatDate(entity.getRepaymentDate(), "yyyy-MM-dd"));
							pojo.setNowRepayAmount(entity.getInterest());
							pojo.setRemainingRepayAmount(remainingRepayAmount);
							pojo.setStatus(entity.getState());
							pojo.setType(entity.getPrincipal());
							pojos.add(pojo);
						}
						// 总页数.
						data.put("count", page.getCount());
						// 页码.
						data.put("pageNo", page.getPageNo());
						// 每页大小.
						data.put("pageSize", page.getPageSize());
						// 总页码.
						data.put("pageCount", pageCount);
						// 计划列表.
						data.put("plans", pojos);
						// 成功状态.
						result.put("state", "0");
						// 成功消息.
						result.put("message", "接口请求成功！");
						// 数据域.
						result.put("data", data);
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "系统超时！");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时！");
				return result;
			}
		} catch (Exception e) {
			LOG.info(this.getClass() + "，异常信息：" + e.getMessage());
			result.put("state", "1");
			result.put("message", e.getMessage());
			return result;
		}
	}

	/**
	 * 用户信息
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserInfo")
	public Map<String, Object> getUserInfo(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) { // 用户信息为null，系统超时，跳登陆页.
				result.put("state", "4");
				result.put("message", "系统超时！");
				return result;
			}
			UserInfo userInfo = userInfoDao.get(jedisUserId);
			if (userInfo == null) {
				userInfo = userInfoDao.getCgb(jedisUserId);
			}
			if (userInfo != null) {
				if(userInfo.getCertificateChecked()==null) {
					map.put("certificateChecked", "1");//客户未开户
				}else {
					map.put("certificateChecked", userInfo.getCertificateChecked());
				}
				if(userInfo.getCgbBindBankCardState()==null) {
					map.put("cgbBindBankCardState", "1");//银行卡未认证
				}else {
					map.put("cgbBindBankCardState", userInfo.getCgbBindBankCardState());
				}
				map.put("cgbBindBankCard", userInfo.getCgbBindBankCardState() == null ? "1" : userInfo.getCgbBindBankCardState());
				map.put("name", userInfo.getName());
				map.put("IdCard", Util.hideString(userInfo.getCertificateNo() == null ? "" : userInfo.getCertificateNo(), 6, 8));
				map.put("realName", userInfo.getRealName() == null ? "" : userInfo.getRealName());
				map.put("email", userInfo.getEmail() == null ? "" : userInfo.getEmail());
				map.put("lastLoginDate", DateUtils.getDate(userInfo.getLastLoginDate(), "yyyy.MM.dd HH:mm:ss"));
				String businessPwd = userInfo.getBusinessPwd();
				if (null == businessPwd) {
					map.put("businessPwd", "");
				} else {
					map.put("businessPwd", businessPwd.equals("") ? "" : "*************");
				}
				map.put("bindEmail", userInfo.getEmailChecked() == null ? "1" : userInfo.getEmailChecked());
				map.put("emergencyUser", userInfo.getEmergencyUser() == null ? "" : userInfo.getEmergencyUser());
				map.put("emergencyTel", userInfo.getEmergencyTel() == null ? "" : userInfo.getEmergencyTel());
				map.put("address", userInfo.getAddress() == null ? "" : userInfo.getAddress());
				map.put("gesturePwd", userInfo.getGesturePwd() == null ? "0" : "1");
//				UserBankCard userBankCard = userBankCardService.getBankCardInfoByUserId(userInfo.getId());
				CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userInfo.getId());
				if (userBankCard != null) {
					map.put("bindBankCardNo", FuncUtils.hiddenBankCard(userBankCard.getBankAccountNo()));
//					map.put("bindBankCardNo", userBankCard.getBankAccountNo());
					map.put("bankName", userBankCard.getBankName() == null ? "" : userBankCard.getBankName());
					map.put("bankNo", userBankCard.getBankNo() == null ? "" : userBankCard.getBankNo());
					map.put("bankNoImage", userBankCard.getBankNo() == null ? "" : Global.getConfig("credit_file_path") + "bankImages/" + userBankCard.getBankNo() + ".png");
					map.put("bankCardPhone", userBankCard.getBankCardPhone());
					// 查询客户银行卡信息，校验是否绑卡.
					if (UserBankCard.CERTIFY_YES.equals(userBankCard.getState())) {
						// 已绑卡.
						map.put("bindBankCard", UserInfo.BIND_CARD_YES);
					} else {
						// 未绑卡.
						map.put("bindBankCard", UserInfo.BIND_CARD_NO);
					}
				} else {
					// 未绑卡.
					map.put("bindBankCard", UserInfo.BIND_CARD_NO);
					map.put("bindBankCardNo", "  ");
				}
				// 银行存管开户字段
				int freeCash = userCashService.getFreeCashCount(userInfo.getId());
				map.put("freeCash", freeCash);
				/**
				 * 判断客户当天是否签到.
				 */
				String date = DateUtils.getDate(new Date(), "yyyy-MM-dd");
				String beginNowDate = date + " 00:00:00";
				String endNowDate = date + " 23:59:59";
				// 封装查询条件.
				UserSigned nowEntity = new UserSigned();
				nowEntity.setBeginDate(beginNowDate);
				nowEntity.setEndDate(endNowDate);
				nowEntity.setUserId(userInfo.getId());
				List<UserSigned> nowList = userSignedDao.findExists(nowEntity);
				if (nowList.size() == 0) {
					map.put("signed", UserSignedService.SIGNED_STATE_3);
				} else {
					map.put("signed", UserSignedService.SIGNED_STATE_2);
				}
				// 查询是否做过风险测评
				// int isTest = 0;// 是否做过风险测评
				// String userType = "";// 用户风险类型
				// String riskType = userInfo.getRiskType();
				// if(!StringUtils.isBlank(riskType)) {
				// isTest = 1;
				// if(riskType.equals(userInfo.RISKTYPE5)) {
				// userType = "保守型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE4)) {
				// userType = "谨慎型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE3)) {
				// userType = "稳健型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE2)) {
				// userType = "进取型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE1)) {
				// userType = "激进型";
				// }
				// }else {
				// isTest = 0;
				//
				// }
				QuestionUser entity = new QuestionUser();
				entity.setUser(userInfo);
				List<QuestionUser> list = questionUserDao.findList(entity);
				int isTest = 0;// 是否做过风险测评
				int score = 0;// 用户分值
				String userType = "";// 用户风险类型
				if (list != null && list.size() > 0) {
					// 查询用户风险类型

					for (QuestionUser questionUser : list) {
						Answer answer = answerDao.get(questionUser.getAnswerId());
						if (answer != null) {
							score = score + answer.getScore();
						}
					}
					// 是否做过风险测评
					isTest = 1;
				} else {
					isTest = 0;
				}
				if (score >= 10 && score < 20) {
					userType = "保守型";
				} else if (score >= 20 && score < 28) {
					userType = "成长型";
				} else if (score >= 28 && score < 36) {
					userType = "稳重型";
				} else if (score >= 36 && score < 43) {
					userType = "激进型";
				} else if (score >= 43) {
					userType = "激进型";
				} else {
					userType = "保守型";
				}
				map.put("userType", userType);
				map.put("isTest", isTest);
				AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
				aUserAwardsHistory.setUserInfo(userInfo);
				aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
				List<AUserAwardsHistory> userAwardList = aUserAwardsHistoryService.findVouchers(aUserAwardsHistory);// findVouchersList(entity);
				map.put("voucherNum", userAwardList.size());// 优惠券个数
				UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userInfo.getId());
				if (userBounsPoint != null) {
					map.put("score", userBounsPoint.getScore());// 积分
				} else {
					map.put("score", "0");
				}
				
				// 重新赋值token.---解决token超时问题

//				Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
//				// 系统没有登录用户（一般不会进该方法）
//				if (cacheLoginedUser == null) {
//					cacheLoginedUser = new HashMap<String, String>();
//				}
//				String isexitToken = cacheLoginedUser.get(userInfo.getId());
//				if (isexitToken != null && isexitToken != "") {
//					// 不等于null 获取到原来的token，并且移除
//					JedisUtils.del(isexitToken);
//				}
//				cacheLoginedUser.put(userInfo.getId(), token);
//				JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);
				
				//设置缓存
				String a = JedisUtils.set(token, userInfo.getId(), 1200);

				// 获取用户头像
				AnnexFile annexFile = new AnnexFile();
				annexFile.setOtherId(userInfo.getId());
				annexFile.setType("201");
				List<AnnexFile> annexFileList = annexFileService.findList(annexFile);
				if (annexFileList != null && annexFileList.size() > 0 && annexFileList.get(0).getUrl() != null && !annexFileList.get(0).getUrl().equals("")) {
					map.put("avatarPath", Global.getConfig("credit_file_path") + annexFileList.get(0).getUrl());
				} else {
					map.put("avatarPath", "");
				}

				result.put("state", "0");
				result.put("message", "获取用户信息成功");
				result.put("data", map);
			} else {
				result.put("state", "1");
				result.put("message", "系统异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 用户信息---新版
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserInfoNew")
	public Map<String, Object> getUserInfoNew(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) { // 用户信息为null，系统超时，跳登陆页.
				result.put("state", "4");
				result.put("message", "系统超时！");
				return result;
			}
			UserInfo userInfo = userInfoDao.get(jedisUserId);
			if (userInfo == null) {
				userInfo = userInfoDao.getCgb(jedisUserId);
			}
			if (userInfo != null) {
				if(userInfo.getCertificateChecked()==null) {
					map.put("certificateChecked", "1");//客户未开户
				}else {
					map.put("certificateChecked", userInfo.getCertificateChecked());
				}
				if(userInfo.getCgbBindBankCardState()==null) {
					map.put("cgbBindBankCardState", "1");//银行卡未认证
				}else {
					map.put("cgbBindBankCardState", userInfo.getCgbBindBankCardState());
				}
				map.put("cgbBindBankCard", userInfo.getCgbBindBankCardState() == null ? "1" : userInfo.getCgbBindBankCardState());
				map.put("name", userInfo.getName());
				map.put("IdCard", Util.hideString(userInfo.getCertificateNo() == null ? "" : userInfo.getCertificateNo(), 6, 8));
				map.put("realName", userInfo.getRealName() == null ? "" : userInfo.getRealName());
				map.put("email", userInfo.getEmail() == null ? "" : userInfo.getEmail());
				map.put("lastLoginDate", DateUtils.getDate(userInfo.getLastLoginDate(), "yyyy.MM.dd HH:mm:ss"));
				String businessPwd = userInfo.getBusinessPwd();
				if (null == businessPwd) {
					map.put("businessPwd", "");
				} else {
					map.put("businessPwd", businessPwd.equals("") ? "" : "*************");
				}
				map.put("bindEmail", userInfo.getEmailChecked() == null ? "1" : userInfo.getEmailChecked());
				map.put("emergencyUser", userInfo.getEmergencyUser() == null ? "" : userInfo.getEmergencyUser());
				map.put("emergencyTel", userInfo.getEmergencyTel() == null ? "" : userInfo.getEmergencyTel());
				map.put("address", userInfo.getAddress() == null ? "" : userInfo.getAddress());
				map.put("gesturePwd", userInfo.getGesturePwd() == null ? "0" : "1");
				CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userInfo.getId());
				if (userBankCard != null) {
					map.put("bindBankCardNo", FuncUtils.hiddenBankCard(userBankCard.getBankAccountNo()));
//					map.put("bindBankCardNo", userBankCard.getBankAccountNo());
					map.put("bankName", userBankCard.getBankName() == null ? "" : userBankCard.getBankName());
					map.put("bankNo", userBankCard.getBankNo() == null ? "" : userBankCard.getBankNo());
					Map<String, String> bankCode = AppUtil.getBankCodeByBankNo(userBankCard.getBankAccountNo());
					map.put("bankNoImage",  Global.getConfig("credit_file_path") + "bankImages/" + bankCode.get("baseBankCode") + ".png");
					map.put("bankCardPhone", userBankCard.getBankCardPhone());
					// 查询客户银行卡信息，校验是否绑卡.
					if (UserBankCard.CERTIFY_YES.equals(userBankCard.getState())) {
						// 已绑卡.
						map.put("bindBankCard", UserInfo.BIND_CARD_YES);
					} else {
						// 未绑卡.
						map.put("bindBankCard", UserInfo.BIND_CARD_NO);
					}
				} else {
					// 未绑卡.
					map.put("bindBankCard", UserInfo.BIND_CARD_NO);
					map.put("bindBankCardNo", "  ");
				}
				// 银行存管开户字段
				int freeCash = userCashService.getFreeCashCount(userInfo.getId());
				map.put("freeCash", freeCash);
				/**
				 * 判断客户当天是否签到.
				 */
				String date = DateUtils.getDate(new Date(), "yyyy-MM-dd");
				String beginNowDate = date + " 00:00:00";
				String endNowDate = date + " 23:59:59";
				// 封装查询条件.
				UserSigned nowEntity = new UserSigned();
				nowEntity.setBeginDate(beginNowDate);
				nowEntity.setEndDate(endNowDate);
				nowEntity.setUserId(userInfo.getId());
				List<UserSigned> nowList = userSignedDao.findExists(nowEntity);
				if (nowList.size() == 0) {
					map.put("signed", UserSignedService.SIGNED_STATE_3);
				} else {
					map.put("signed", UserSignedService.SIGNED_STATE_2);
				}
				// 查询是否做过风险测评
				// int isTest = 0;// 是否做过风险测评
				// String userType = "";// 用户风险类型
				// String riskType = userInfo.getRiskType();
				// if(!StringUtils.isBlank(riskType)) {
				// isTest = 1;
				// if(riskType.equals(userInfo.RISKTYPE5)) {
				// userType = "保守型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE4)) {
				// userType = "谨慎型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE3)) {
				// userType = "稳健型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE2)) {
				// userType = "进取型";
				// }
				// if(riskType.equals(userInfo.RISKTYPE1)) {
				// userType = "激进型";
				// }
				// }else {
				// isTest = 0;
				//
				// }
				QuestionUser entity = new QuestionUser();
				entity.setUser(userInfo);
				List<QuestionUser> list = questionUserDao.findList(entity);
				int isTest = 0;// 是否做过风险测评
				int score = 0;// 用户分值
				String userType = "";// 用户风险类型
				if (list != null && list.size() > 0) {
					// 查询用户风险类型

					for (QuestionUser questionUser : list) {
						Answer answer = answerDao.get(questionUser.getAnswerId());
						if (answer != null) {
							score = score + answer.getScore();
						}
					}
					// 是否做过风险测评
					isTest = 1;
				} else {
					isTest = 0;
				}
				if (score >= 10 && score < 20) {
					userType = "保守型";
				} else if (score >= 20 && score < 28) {
					userType = "成长型";
				} else if (score >= 28 && score < 36) {
					userType = "稳重型";
				} else if (score >= 36 && score < 43) {
					userType = "激进型";
				} else if (score >= 43) {
					userType = "激进型";
				} else {
					userType = "保守型";
				}
				map.put("userType", userType);
				map.put("isTest", isTest);
				AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
				aUserAwardsHistory.setUserInfo(userInfo);
				aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
				List<AUserAwardsHistory> userAwardList = aUserAwardsHistoryService.findVouchers(aUserAwardsHistory);// findVouchersList(entity);
				map.put("voucherNum", userAwardList.size());// 优惠券个数
				UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userInfo.getId());
				if (userBounsPoint != null) {
					map.put("score", userBounsPoint.getScore());// 积分
				} else {
					map.put("score", "0");
				}
				
				// 重新赋值token.---解决token超时问题

//				Map<String, String> cacheLoginedUser = JedisUtils.getMap("cacheLoginedUser");
//				// 系统没有登录用户（一般不会进该方法）
//				if (cacheLoginedUser == null) {
//					cacheLoginedUser = new HashMap<String, String>();
//				}
//				String isexitToken = cacheLoginedUser.get(userInfo.getId());
//				if (isexitToken != null && isexitToken != "") {
//					// 不等于null 获取到原来的token，并且移除
//					JedisUtils.del(isexitToken);
//				}
//				cacheLoginedUser.put(userInfo.getId(), token);
//				JedisUtils.setMap("cacheLoginedUser", cacheLoginedUser, 1200);
				
				//设置缓存
				String a = JedisUtils.set(token, userInfo.getId(), 1200);

				// 获取用户头像
				AnnexFile annexFile = new AnnexFile();
				annexFile.setOtherId(userInfo.getId());
				annexFile.setType("201");
				List<AnnexFile> annexFileList = annexFileService.findList(annexFile);
				if (annexFileList != null && annexFileList.size() > 0 && annexFileList.get(0).getUrl() != null && !annexFileList.get(0).getUrl().equals("")) {
					map.put("avatarPath", Global.getConfig("credit_file_path") + annexFileList.get(0).getUrl());
				} else {
					map.put("avatarPath", "");
				}

				result.put("state", "0");
				result.put("message", "获取用户信息成功");
				result.put("data", map);
			} else {
				result.put("state", "1");
				result.put("message", "系统异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 用户银行卡信息---旧版
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserBankCard")
	public Map<String, Object> getUserBankCard(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.get(jedisUserId);
				if (userInfo != null) {
					// 查询客户是否绑卡.
					// UserInfo entity = userInfoDao.get(userInfo.getId());
					// if (entity.getBindBankCardState().intValue() ==
					// UserInfo.BIND_CARD_NO.intValue()) {
					// result.put("state", "5");
					// result.put("message", "用户未绑定银行卡");
					// return result;
					// }
					String userId = userInfo.getId();
					UserBankCard userBankCard = userBankCardService.getBankCardInfoByUserId(userId);
					UserAccountInfo userAccount = userAccountInfoService.get(userInfo.getAccountId());
					if (userBankCard != null) {
						// 查询客户是否绑卡.
						if (!UserBankCard.CERTIFY_YES.equals(userBankCard.getState())) {
							result.put("state", "5");
							result.put("message", "用户未绑定银行卡");
							return result;
						}

						map.put("bindBankCardNo", FuncUtils.hiddenBankCard(userBankCard.getBankAccountNo()));
//						map.put("bindBankCardNo", userBankCard.getBankAccountNo());
						map.put("bankCode", userBankCard.getBankNo());
						map.put("bankName", userBankCard.getBankName() == null ? "" : userBankCard.getBankName());
						map.put("dayLimitAmount", userBankCard.getBankNo() == null ? "" : BankCodeUtils.getDayLimit(userBankCard.getBankNo()));
						map.put("singleLimitAmount", userBankCard.getBankNo() == null ? "" : BankCodeUtils.getSingleLimit(userBankCard.getBankNo()));
						map.put("bankCardPhone", userBankCard.getBankCardPhone());
						int freeCash = userCashService.getFreeCashCount(userId);
						map.put("freeCash", freeCash);
						if (userAccount != null) {
							map.put("availableAmount", userAccount.getAvailableAmount());
						} else {
							map.put("availableAmount", "");
						}
						result.put("state", "0");
						result.put("message", "获取用户银行卡成功");
						result.put("data", map);
						return result;
					} else {
						result.put("state", "5");
						result.put("message", "用户未绑定银行卡");
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "用户登录超时，请重新登录");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户银行卡信息---新版
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getcgbUserBankCard")
	public Map<String, Object> getcgbUserBankCard(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				if (userInfo != null) {
					// 查询客户是否绑卡.
					// UserInfo entity = userInfoDao.get(userInfo.getId());
					// if (entity.getBindBankCardState().intValue() ==
					// UserInfo.BIND_CARD_NO.intValue()) {
					// result.put("state", "5");
					// result.put("message", "用户未绑定银行卡");
					// return result;
					// }
					if(userInfo.getCertificateChecked()==null) {
						map.put("certificateChecked", "1");//客户未开户
					}else {
						map.put("certificateChecked", userInfo.getCertificateChecked());
					}
					if(userInfo.getCgbBindBankCardState()==null) {
						map.put("cgbBindBankCardState", "1");//银行卡未认证
					}else {
						map.put("cgbBindBankCardState", userInfo.getCgbBindBankCardState());
					}
					String userId = userInfo.getId();
					CgbUserBankCard userBankCard = cgbUserBankCardService.getBankCardInfoByUserId(userId);
					CgbUserAccount userAccount = cgbUserAccountService.get(userInfo.getAccountId());
//					map.put("IdCard", Util.hideString(userInfo.getCertificateNo() == null ? "" : userInfo.getCertificateNo(), 6, 8));
					map.put("IdCardNo", userInfo.getCertificateNo());
					map.put("realName", userInfo.getRealName() == null ? "" : userInfo.getRealName());
					if (userBankCard != null) {
						map.put("IdCard", Util.hideString(userInfo.getCertificateNo() == null ? "" : userInfo.getCertificateNo(), 6, 8));
						map.put("bindBankCardNo", FuncUtils.hiddenBankCard(userBankCard.getBankAccountNo()));
//						map.put("bindBankCardNo", userBankCard.getBankAccountNo());
						map.put("bankCode", userBankCard.getBankNo());
						map.put("bankName", userBankCard.getBankName() == null ? "" : userBankCard.getBankName());
						map.put("dayLimitAmount", userBankCard.getBankNo() == null ? "" : BankCodeUtils.getDayLimit(userBankCard.getBankNo()));
						map.put("singleLimitAmount", userBankCard.getBankNo() == null ? "" : BankCodeUtils.getSingleLimit(userBankCard.getBankNo()));
						map.put("bankCardPhone", userBankCard.getBankCardPhone());
						// 查询客户是否绑卡.
						if (!UserBankCard.CERTIFY_YES.equals(userBankCard.getState())) {
							result.put("state", "5");
							result.put("data", map);
							result.put("message", "用户未绑定银行卡");
							return result;
						}
						
						if (userBankCard.getBankNo() != null) {
							if (CGBBankCodeUtils.getSingleLimit(userBankCard.getBankNo()).equals("维护中") || CGBBankCodeUtils.getSingleLimit(userBankCard.getBankNo()).equals("无限额")) {
								map.put("limitAmountTxt", CGBBankCodeUtils.getSingleLimit(userBankCard.getBankNo()));// 维护中||无限额
							} else {
								map.put("limitAmountTxt", "单笔限额" + CGBBankCodeUtils.getSingleLimit(userBankCard.getBankNo()) + ",日累计限额" + CGBBankCodeUtils.getDayLimit(userBankCard.getBankNo()) + ",以实际限额为准");// 单笔
							}
						} else {
							map.put("limitAmountTxt", userBankCard.getBankNo());// 维护中||无限额
						}

						int freeCash = userCashService.getFreeCashCount(userId);
						map.put("freeCash", freeCash);
						if (userAccount != null) {
							map.put("availableAmount", NumberUtils.scaleDoubleStr(userAccount.getAvailableAmount()));
						} else {
							map.put("availableAmount", "");
						}
						result.put("state", "0");
						result.put("message", "获取用户银行卡成功");
						result.put("data", map);
						return result;
					} else {
						result.put("state", "5");
						result.put("data", map);
						result.put("message", "用户未绑定银行卡");
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "用户登录超时，请重新登录");
				}
			} else {
				result.put("state", "4");
				result.put("message", "用户登录超时，请重新登录");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户账户信息=---旧版
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserAccount")
	public Map<String, Object> getUserAccount(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.get(jedisUserId);
				UserAccountInfo userAccountInfo = userAccountInfoDao.get(userInfo.getAccountId());
				Date now = new Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String beginDate = df.format(now.getTime()) + " 00:00:00";
				String endDate = df.format(now.getTime()) + " 23:59:00";
				Double todayBidAmount = wloanTermInvestService.countAmount(userInfo.getId(), beginDate, endDate);
				int rank = wloanTermInvestService.rankList(userInfo.getId(), beginDate, endDate);
				Map<String, String> map = new HashMap<String, String>();
				if (userAccountInfo != null) {
					map.put("totalAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getTotalAmount()).toString());// 资产总额
					map.put("availableAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getAvailableAmount()).toString());// 可用金额
					map.put("cashAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getCashAmount()).toString());// 提现金额
					map.put("rechargeAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getRechargeAmount()).toString());// 充值总额
					map.put("freezeAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getFreezeAmount()).toString());// 冻结金额
					map.put("totalInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getTotalInterest()).toString());// 总收益
					map.put("currentAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentAmount()).toString());// 活期投资金额
					map.put("regularDuePrincipal", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularDuePrincipal()).toString());// 定期待收本金
					map.put("regularDueInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularDueInterest()).toString());// 定期待收收益
					map.put("regularTotalAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularTotalAmount()).toString());// 定期投资总金额
					map.put("regularTotalInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularTotalInterest()).toString());// 定期累计收益
					map.put("currentTotalInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentTotalInterest()).toString());// 活期总收益
					map.put("currentTotalAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentTotalAmount()).toString());// 活期累计投资金额
					map.put("currentYesterdayInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentYesterdayInterest()).toString());// 活期昨日收益
					map.put("reguarYesterdayInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getReguarYesterdayInterest()).toString());// 定期昨日收益
					map.put("todayBidAmount", NumberUtils.scaleDoubleStr(todayBidAmount).toString());// 当日累计投资金额
					map.put("rank", String.valueOf(rank));// 用户投资排行
					result.put("state", "0");
					result.put("message", "获取用户账户信息成功");
					result.put("data", map);
				} else {
					result.put("state", "1");
					result.put("message", "未查询到账户信息");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户账户信息=---新版
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getcgbUserAccount")
	public Map<String, Object> getcgbUserAccount(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				if (userInfo == null) {
					userInfo = userInfoDao.get(jedisUserId);
				}
				CgbUserAccount userAccountInfo = cgbUserAccountDao.get(userInfo.getAccountId());
				Date now = new Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String beginDate = df.format(now.getTime()) + " 00:00:00";
				String endDate = df.format(now.getTime()) + " 23:59:00";
				Double todayBidAmount = wloanTermInvestService.countAmount(userInfo.getId(), beginDate, endDate);
				int rank = wloanTermInvestService.rankList(userInfo.getId(), beginDate, endDate);
				Map<String, String> map = new HashMap<String, String>();
				if (userAccountInfo != null) {
					map.put("totalAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getTotalAmount()).toString());// 资产总额
					map.put("availableAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getAvailableAmount()).toString());// 可用金额
					map.put("cashAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getCashAmount()).toString());// 提现金额
					map.put("rechargeAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getRechargeAmount()).toString());// 充值总额
					map.put("freezeAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getFreezeAmount()).toString());// 冻结金额
					map.put("totalInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getTotalInterest()).toString());// 总收益
					map.put("currentAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentAmount()).toString());// 活期投资金额
					map.put("regularDuePrincipal", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularDuePrincipal()).toString());// 定期待收本金
					map.put("regularDueInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularDueInterest()).toString());// 定期待收收益
					map.put("regularTotalAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularTotalAmount()).toString());// 定期投资总金额
					map.put("regularTotalInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getRegularTotalInterest()).toString());// 定期累计收益
					map.put("currentTotalInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentTotalInterest()).toString());// 活期总收益
					map.put("currentTotalAmount", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentTotalAmount()).toString());// 活期累计投资金额
					map.put("currentYesterdayInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getCurrentYesterdayInterest()).toString());// 活期昨日收益
					map.put("reguarYesterdayInterest", NumberUtils.scaleDoubleStr(userAccountInfo.getReguarYesterdayInterest()).toString());// 定期昨日收益
					map.put("todayBidAmount", NumberUtils.scaleDoubleStr(todayBidAmount).toString());// 当日累计投资金额
					map.put("rank", String.valueOf(rank));// 用户投资排行
					result.put("state", "0");
					result.put("message", "获取用户账户信息成功");
					result.put("data", map);
				} else {
					result.put("state", "0");
					result.put("message", "未查询到账户信息");
					map.put("totalAmount", "0");// 资产总额
					map.put("availableAmount", "0");// 可用金额
					map.put("cashAmount", "0");// 提现金额
					map.put("rechargeAmount", "0");// 充值总额
					map.put("freezeAmount", "0");// 冻结金额
					map.put("totalInterest", "0");// 总收益
					map.put("currentAmount", "0");// 活期投资金额
					map.put("regularDuePrincipal", "0");// 定期待收本金
					map.put("regularDueInterest", "0");// 定期待收收益
					map.put("regularTotalAmount", "0");// 定期投资总金额
					map.put("regularTotalInterest", "0");// 定期累计收益
					map.put("currentTotalInterest", "0");// 活期总收益
					map.put("currentTotalAmount", "0");// 活期累计投资金额
					map.put("currentYesterdayInterest", "0");// 活期昨日收益
					map.put("reguarYesterdayInterest", "0");// 定期昨日收益
					map.put("todayBidAmount", "0");// 当日累计投资金额
					map.put("rank", String.valueOf(rank));// 用户投资排行
					result.put("data", map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 设置/找回交易密码
	 * 
	 * @param from
	 * @param pass
	 * @return
	 */
	@POST
	@Path("/findTradePassword")
	public Map<String, Object> findTradePassword(@FormParam("from") String from, @FormParam("pass") String pass, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pass)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {

			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.get(jedisUserId);
				if (userInfo != null) {
					// 设置/找回交易密码
					userInfo.setBusinessPwd(EncoderUtil.encrypt(pass));
					int i = userInfoDao.update(userInfo);
					if (i > 0) {
						result.put("state", "0");
						result.put("message", "设置/找回交易密码成功");
					} else {
						result.put("state", "1");
						result.put("message", "系统异常");
					}
				} else {
					result.put("state", "1");
					result.put("message", "系统异常");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 修改交易密码
	 * 
	 * @param from
	 * @param oldPass
	 * @param pass
	 * @return
	 */
	@POST
	@Path("/modifyTradePassword")
	public Map<String, Object> modifyTradePassword(@FormParam("from") String from, @FormParam("oldPass") String oldPass, @FormParam("pass") String pass, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pass)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				if (userInfo != null) {
					if (EncoderUtil.encrypt(oldPass).equals(userInfo.getBusinessPwd())) {
						userInfo.setBusinessPwd(EncoderUtil.encrypt(pass));
						int i = userInfoDao.update(userInfo);
						if (i > 0) {
							result.put("state", "0");
							result.put("message", "修改交易密码成功");
							// 修改交易密码成功发送微信、短信提醒
							weixinSendTempMsgService.sendUpdatePwdMsg("交易", userInfo);
						} else {
							result.put("state", "1");
							result.put("message", "系统异常");
						}
					} else {
						result.put("state", "3");
						result.put("message", "交易密码校验失败");
					}
				} else {
					result.put("state", "1");
					result.put("message", "系统异常");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 个人投资记录
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/getMyBidsdetail")
	public Map<String, Object> getMyBidsdetail(@FormParam("from") String from, @FormParam("state") String state, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> userBidHistoryList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		Page<WloanTermInvest> page = new Page<WloanTermInvest>();
		WloanTermInvest loanTermInvest = new WloanTermInvest();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat dt = new DecimalFormat("######0.00");
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			if (user == null) {
				user = userInfoDao.get(jedisUserId);
			}
			loanTermInvest.setUserInfo(user);

		}
		page.setOrderBy("beginDate desc, state desc ");
		page.setPageNo(StringUtils.toInteger(pageNo));
		page.setPageSize(StringUtils.toInteger(pageSize));
		loanTermInvest.setDelFlag("0");
		loanTermInvest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
		if (state != null && state.length() > 0) {
			WloanTermProject wloanTermProject = new WloanTermProject();
			if (state.equals("-1")) { // 仅适用于移动端.
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("4");// 上线.
				stateItem.add("5");// 满标.
				stateItem.add("6");// 还款中.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("12")) { // state = 12 30天内到期
				Date after30 = DateUtils.getDateOfString(DateUtils.getSpecifiedMonthAfterFormat(new Date(), 30));
				wloanTermProject.setEndEndDate(after30);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("1")) { // PC端，投标中(持有中).
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("4");// 上线.
				stateItem.add("5");// 满标.
				stateItem.add("6");// 还款中.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("6")) { // PC端，回款中.
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("6");// 还款中.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("7")) { // 适用于PC和APP端，已结束.
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("7");// 已结束.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			}
		}
		Page<WloanTermInvest> pages = wloanTermInvestService.findPage(page, loanTermInvest);
		if (pages != null) {
			List<WloanTermInvest> lists = pages.getList();
			if (lists != null && lists.size() > 0) {
				page.setList(lists);
				for (int i = 0; i < lists.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("bidId", lists.get(i).getId());
					map.put("projectName", lists.get(i).getWloanTermProject().getName());
					map.put("amount", dt.format(lists.get(i).getAmount()));
					map.put("state", lists.get(i).getWloanTermProject().getState());
					map.put("span", lists.get(i).getWloanTermProject().getSpan());
					map.put("rate", lists.get(i).getWloanTermProject().getAnnualRate());
					map.put("interest", dt.format(lists.get(i).getInterest()));
					// 投资日期.
					Date investDate = lists.get(i).getBeginDate();
					map.put("dtime", df.format(investDate));
					// 新版本上线日期.
					Date newVersionDate = DateUtils.parseDate("2016-06-16 00:00:00");
					// 判断新旧合同展示URL.
					if (investDate.before(newVersionDate)) {
						map.put("bid_signature", "http://112.126.73.20/" + lists.get(i).getContractPdfPath());
					} else {
						map.put("bid_signature", Global.getConfig("pdf_show_path") + lists.get(i).getContractPdfPath());
					}
					map.put("endDate", lists.get(i).getWloanTermProject().getEndDate() == null ? "" : df.format(lists.get(i).getWloanTermProject().getEndDate()));
					userBidHistoryList.add(map);
				}
			}
			data.put("pageNo", pages.getPageNo());
			data.put("bidHisState", state);
			data.put("pageSize", pages.getPageSize());
			data.put("totalCount", pages.getCount());
			data.put("pageCount", pages.getLast());
			data.put("userBidHistoryList", userBidHistoryList);

			result.put("state", "0");
			result.put("message", "个人投资记录响应成功");
			result.put("data", data);

		} else {
			data.put("pageNo", "");
			data.put("pageSize", "");
			data.put("totalCount", 1);
			data.put("userBidHistoryList", userBidHistoryList);

			result.put("state", "0");
			result.put("message", "个人投资记录响应成功");
			result.put("data", data);
		}

		return result;
	}

	/**
	 * 获取用户所有还款计划
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getUserAllPlan")
	public Map<String, Object> getUserAllPlan(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		UserInfo user = new UserInfo();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				user = userInfoDao.getCgb(jedisUserId);
				WloanTermUserPlan userPlan = new WloanTermUserPlan();
				userPlan.setUserInfo(user);
				userPlan.setState("2");
				List<WloanTermUserPlan> list = wloanTermUserPlanService.findList(userPlan);
				List<String> repayDate = new ArrayList<String>();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						userPlan = list.get(i);
						repayDate.add(df.format(userPlan.getRepaymentDate()));
					}
				}
				result.put("state", "0");
				result.put("message", "返回用户还款计划成功");
				result.put("data", repayDate);
				return result;
			} else {
				throw new Exception("获取用户信息失败");
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", e.getMessage());
		}
		return result;
	}

	/**
	 * 个人还款计划
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param bidId
	 * @return
	 */
	@POST
	@Path("/getMyBidsrepay")
	public Map<String, Object> getMyBidsrepay(@FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("bidId") String bidId, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> userRepayPlanList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
		UserInfo user = new UserInfo();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(bidId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			user = userInfoDao.getCgb(jedisUserId);
		}

		page.setOrderBy(" state desc ");
		page.setPageNo(StringUtils.toInteger(pageNo));
		page.setPageSize(StringUtils.toInteger(pageSize));
		WloanTermInvest loanTermInvest = new WloanTermInvest();
		loanTermInvest.setId(bidId);
		List<WloanTermInvest> wloanTermInvestList = wloanTermInvestService.findList(loanTermInvest);
		if (wloanTermInvestList != null && wloanTermInvestList.size() > 0) {
			for (WloanTermInvest wloanTermInvest : wloanTermInvestList) {
				WloanTermUserPlan loanTermUserPlan = new WloanTermUserPlan();
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				loanTermUserPlan.setUserInfo(user);
				loanTermUserPlan.setWloanTermInvest(wloanTermInvest);
				Page<WloanTermUserPlan> pages = wloanTermUserPlanService.findPage(page, loanTermUserPlan);
				if (pages != null) {
					List<WloanTermUserPlan> lists = pages.getList();
					if (lists != null && lists.size() > 0) {
						page.setList(lists);
						for (int i = 0; i < lists.size(); i++) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("projectName", lists.get(i).getWloanTermProject().getName());
							map.put("projectId", lists.get(i).getWloanTermProject().getId());
							map.put("amount", new DecimalFormat("###,##0.00").format(lists.get(i).getInterest()));
							map.put("dtime", sdf1.format(lists.get(i).getRepaymentDate()));
							map.put("state", lists.get(i).getState());
							userRepayPlanList.add(map);
						}
					} else {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("projectName", "");
						map.put("projectId", "");
						map.put("amount", "");
						map.put("dtime", "");
						map.put("state", "");
						userRepayPlanList.add(map);
					}
				}
				data.put("pageNo", pages.getPageNo());
				data.put("pageSize", pages.getPageSize());
				data.put("totalCount", pages.getCount());
				data.put("pageCount", pages.getLast());
				data.put("userRepayPlanList", userRepayPlanList);
			}
			result.put("state", "0");
			result.put("message", "个人还款计划响应成功");
			result.put("data", data);
		} else {
			result.put("state", 1);
			result.put("message", "系统异常");
			result.put("data", data);
		}
		return result;
	}

	/**
	 * 用户退出登录
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/logout")
	public Map<String, Object> logout(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			// 删除缓存
			long delToken = JedisUtils.del(token);
			LOG.info("退出登录" + delToken);
			System.out.println("退出登录" + delToken);
			if (delToken > 0) {
				result.put("state", "0");
				result.put("message", "退出登录成功");
			} else {
				result.put("state", "1");
				result.put("message", "系统异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 用户手机号修改
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/updateUserPhone")
	public Map<String, Object> updateUserPhone(@FormParam("from") String from, @FormParam("token") String token, @FormParam("newphone") String newphone) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) & StringUtils.isBlank(newphone)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getUserInfoByPhone(newphone);
				if (userInfo != null) {
					result.put("state", "5");
					result.put("message", "新手机号已是注册手机。");
				} else {
					userInfo = userInfoDao.getCgb(jedisUserId);
					userInfo.setName(newphone);
					userInfo.setOldMobilephone(userInfo.getName()); // 旧手机号留存
					int flag = userInfoDao.update(userInfo);
					if (flag == 1) {
						LOG.info("手机号码修改成功！");
					} else {
						LOG.info("手机号码修改失败！");
					}
					result.put("state", "0");
					result.put("message", "手机号号码修改成功");
				}
			} else {
				result.put("state", "1");
				result.put("message", "手机号号码修改失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户登录密码修改
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/updateUserPwd")
	public Map<String, Object> updateUserPwd(@FormParam("from") String from, @FormParam("token") String token, @FormParam("pwd") String pwd) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) & StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				userInfo.setPwd(EncoderUtil.encrypt(pwd));
				userInfoDao.updateUserPwd(userInfo);
				result.put("state", "0");
				result.put("message", "登录密码修改成功");
				// 修改交易密码成功发送微信、短信提醒
				weixinSendTempMsgService.sendUpdatePwdMsg("登录", userInfo);
			} else {
				result.put("state", "1");
				result.put("message", "登录密码修改失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 紧急联系人
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/updateEmergency")
	public Map<String, Object> updateEmergency(@FormParam("from") String from, @FormParam("token") String token, @FormParam("emergencyUser") String emergencyUser, @FormParam("emergencyTel") String emergencyTel) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				userInfo.setEmergencyUser(emergencyUser);
				userInfo.setEmergencyTel(emergencyTel);
				userInfoDao.updateEmergency(userInfo);
				result.put("state", "0");
				result.put("message", "紧急联系信息更新成功");
			} else {
				result.put("state", "1");
				result.put("message", "紧急联系信息更新失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户联系地址修改
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/updateAddress")
	public Map<String, Object> updateAddress(@FormParam("from") String from, @FormParam("token") String token, @FormParam("address") String address) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				userInfo.setAddress(address);
				userInfoDao.updateAddress(userInfo);
				result.put("state", "0");
				result.put("message", "紧急联系信息更新成功");
			} else {
				result.put("state", "1");
				result.put("message", "紧急联系信息更新失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 校验用户旧密码
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/checkOldPwd")
	public Map<String, Object> checkOldPwd(@FormParam("from") String from, @FormParam("token") String token, @FormParam("pwd") String pwd) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) && StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				String password = EncoderUtil.encrypt(pwd);
				if (password.equals(userInfo.getPwd())) {
					result.put("state", "0");
					result.put("message", "登录密码校验成功");
					result.put("data", userInfo.getName());
				} else {
					result.put("state", "3");
					result.put("message", "登录密码校验失败");
				}
			} else {
				result.put("state", "1");
				result.put("message", "系统异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 发送邮箱验证邮件
	 * 
	 * @param token
	 * @param from
	 * @param email
	 * @return
	 */
	@POST
	@Path("/sendCheckEmail")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> sendCheckEmail(@FormParam("token") String token, @FormParam("from") String from, @FormParam("email") String email) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				userInfo.setEmail(email);
				String validateCode = sendEmailService.sendEmail(userInfo);
				long currentTime = new Date().getTime() + 1000 * 60 * 60;
				userInfo.setSendemaildate(new Date(currentTime));
				userInfo.setSalt(validateCode);
				userInfo.setEmailChecked(1);
				userInfoDao.updateEmailInfo(userInfo);
				result.put("state", "0");
				result.put("message", "邮件发送成功,请登录邮箱进行验证");
			} else {
				result.put("state", "1");
				result.put("message", "邮件发送失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "邮件发送失败！");
		}
		return result;
	}

	/**
	 * 用户的回款金额展示
	 */
	@POST
	@Path("/getUserInterestCount")
	public Map<String, Object> getUserInterestCount(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		UserInfo user = new UserInfo();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				user = userInfoDao.getCgb(jedisUserId);
				WloanTermUserPlan userPlan = new WloanTermUserPlan();
				userPlan.setUserInfo(user);
				userPlan.setState("2");
				List<WloanTermUserPlan> list = wloanTermUserPlanService.findinterestCount(userPlan);
				List<String[]> repayDate = new ArrayList<String[]>();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						userPlan = list.get(i);
						String[] data = new String[2];
						data[0] = df.format(userPlan.getRepaymentDate());
						data[1] = userPlan.getInterest().toString();
						repayDate.add(data);
					}
				}
				result.put("state", "0");
				result.put("message", "展示金额回款成功");
				result.put("data", repayDate);
				return result;
			} else {

				throw new Exception("获取回款信息失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			result.put("state", "1");
			result.put("message", e.getMessage());
		}
		return result;
	}

	/**
	 * 个人投资记录H5---根据投资项目类型
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param projecttype
	 * @return
	 */
	@POST
	@Path("/getMyBidsdetailH5")
	public Map<String, Object> getMyBidsdetailH5(@FormParam("from") String from, @FormParam("projectProductType") String projectProductType, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("token") String token, @FormParam("projectstate") String projectState, @FormParam("state") String state) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> userBidHistoryList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		Page<WloanTermInvest> page = new Page<WloanTermInvest>();
		WloanTermInvest loanTermInvest = new WloanTermInvest();
		WloanTermProject wloanTermProject = new WloanTermProject();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat dt = new DecimalFormat("######0.00");
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(projectProductType)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			if (user == null) {
				user = userInfoDao.get(jedisUserId);
			}
			loanTermInvest.setUserInfo(user);

		}
		page.setOrderBy("beginDate desc, state desc ");
		page.setPageNo(StringUtils.toInteger(pageNo));
		page.setPageSize(StringUtils.toInteger(pageSize));
		loanTermInvest.setDelFlag("0");
		wloanTermProject.setProjectProductType(projectProductType);
		if (state != null && state.length() > 0) {
			if (state.equals("1")) { // 持有中
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("4");// 上线.
				stateItem.add("5");// 满标.
				stateItem.add("6");// 还款中.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("2")) { // 30天内到期
				Date after30 = DateUtils.getDateOfString(DateUtils.getSpecifiedMonthAfterFormat(new Date(), 30));
				wloanTermProject.setEndEndDate(after30);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("3")) { // 已结束.
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("7");// 已结束.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("4")) { // 新版pc端
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("4");// 上线.
				stateItem.add("5");// 满标.
				stateItem.add("6");// 还款中.
				stateItem.add("7");// 已结束.
				wloanTermProject.setStateItem(stateItem);
				if (projectState != null) {
					wloanTermProject.setState(projectState);
				}
				loanTermInvest.setWloanTermProject(wloanTermProject);
			}
		}
		loanTermInvest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
		Page<WloanTermInvest> pages = wloanTermInvestService.findPage(page, loanTermInvest);
		if (pages != null) {
			List<WloanTermInvest> lists = pages.getList();
			if (lists != null && lists.size() > 0) {
				page.setList(lists);
				for (int i = 0; i < lists.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("bidId", lists.get(i).getId());
					map.put("projectId", lists.get(i).getWloanTermProject().getId());
					map.put("projectName", lists.get(i).getWloanTermProject().getName());
					map.put("projectType", lists.get(i).getWloanTermProject().getProjectType());
					map.put("projectProductType", lists.get(i).getWloanTermProject().getProjectProductType());
					map.put("amount", dt.format(lists.get(i).getAmount()));
					map.put("state", lists.get(i).getWloanTermProject().getState());
					map.put("span", lists.get(i).getWloanTermProject().getSpan());
					map.put("rate", lists.get(i).getWloanTermProject().getAnnualRate());
					map.put("sn", lists.get(i).getWloanTermProject().getSn());
					map.put("interest", dt.format(lists.get(i).getInterest()));
					// 投资日期.
					Date investDate = lists.get(i).getBeginDate();
					map.put("dtime", df.format(investDate));
					// 新版本上线日期.
					Date newVersionDate = DateUtils.parseDate("2016-06-16 00:00:00");
					// 判断新旧合同展示URL.
					if (investDate.before(newVersionDate)) {
						map.put("bid_signature", "http://112.126.73.20/" + lists.get(i).getContractPdfPath());
					} else {
						map.put("bid_signature", Global.getConfig("pdf_show_path") + lists.get(i).getContractPdfPath());
					}
					map.put("endDate", lists.get(i).getWloanTermProject().getEndDate() == null ? "" : df.format(lists.get(i).getWloanTermProject().getEndDate()));
					// 获取核心企业相关
					WloanTermProject project = wloanTermProjectService.get(lists.get(i).getWloanTermProject().getId());
					if (project != null) {
						// 获取核心企业简介
						String middlemenId = project.getReplaceRepayId();
						if (middlemenId != null) {
							CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
							if (creditUserInfo != null) {
								map.put("creditName", creditUserInfo.getEnterpriseFullName());
							} else {
								map.put("creditName", null);
							}
							CreditAnnexFile annexFile = new CreditAnnexFile();
							annexFile.setOtherId(middlemenId);
							annexFile.setType("30");
							List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
							if (annexFileList != null && annexFileList.size() > 0) {
								map.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId);
							} else {
								map.put("creditUrl", null);
							}
						} else {
							map.put("creditName", null);
							map.put("creditUrl", null);
						}
					}
					userBidHistoryList.add(map);
				}
			}
			data.put("pageNo", pages.getPageNo());
			data.put("pageSize", pages.getPageSize());
			data.put("totalCount", pages.getCount());
			data.put("pageCount", pages.getLast());
			data.put("userBidHistoryList", userBidHistoryList);

			result.put("state", "0");
			result.put("message", "个人投资记录响应成功");
			result.put("data", data);

		} else {
			data.put("pageNo", "");
			data.put("pageSize", "");
			data.put("totalCount", 1);
			data.put("userBidHistoryList", userBidHistoryList);

			result.put("state", "0");
			result.put("message", "个人投资记录响应成功");
			result.put("data", data);
		}

		return result;
	}

	/**
	 * 个人累计投资记录
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@POST
	@Path("/getMyBidsTotal")
	public Map<String, Object> getMyBidsTotal(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// WloanTermInvest loanTermInvest = new WloanTermInvest();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			// loanTermInvest.setUserInfo(userInfoDao.getCgb(jedisUserId));
		}
		// WloanTermProject wloanTermProject = new WloanTermProject();
		// List<String> stateItem = new ArrayList<String>();
		// stateItem.add("4");// 上线.
		// stateItem.add("5");// 满标.
		// stateItem.add("6");// 还款中.
		// stateItem.add("7");
		// wloanTermProject.setStateItem(stateItem);
		// loanTermInvest.setWloanTermProject(wloanTermProject);
		// loanTermInvest.setState("1");
		// List<WloanTermInvest> list =
		// wloanTermInvestService.findList(loanTermInvest);

		// 用户累计出借金额（安心投）.
		WloanTermInvest invest = new WloanTermInvest();
		List<String> iStateItem = new ArrayList<String>();
		iStateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
		iStateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_3);
		iStateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_9);
		invest.setStateItem(iStateItem);
		invest.setUserId(jedisUserId);
		WloanTermProject project = new WloanTermProject();
		List<String> stateItems = new ArrayList<String>();
		stateItems.add(WloanTermProjectService.ONLINE); // 上线.
		stateItems.add(WloanTermProjectService.FULL); // 满标.
		stateItems.add(WloanTermProjectService.REPAYMENT); // 还款中.
		stateItems.add(WloanTermProjectService.FINISH); // 已结束.
		project.setStateItem(stateItems);
		project.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
		invest.setWloanTermProject(project);
		double aAmount = 0d;
		Double axtSumAmount = wloanTermInvestDao.findSumAmountByInvest(invest);
		if (null == axtSumAmount) {
			aAmount = 0D;
		} else {
			aAmount = axtSumAmount;
		}
		// 已结束项目，用户累计出借本金（安心投）.
		double aInerest = 0d;
		WloanTermInvest axtInvest = new WloanTermInvest();
		List<String> axtStateItem = new ArrayList<String>();
		axtStateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
		axtStateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_3);
		axtStateItem.add(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_9);
		axtInvest.setStateItem(axtStateItem);
		axtInvest.setUserId(jedisUserId);
		WloanTermProject axtWloanTermProject = new WloanTermProject();
		List<String> axtProStateItem = new ArrayList<String>();
		axtProStateItem.add(WloanTermProjectService.FINISH);
		axtWloanTermProject.setStateItem(axtProStateItem);
		axtWloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
		axtInvest.setWloanTermProject(axtWloanTermProject);
		// 已结束项目本金之和.
		Double axtSumAmountByInvest = wloanTermInvestDao.findSumAmountByInvest(axtInvest);
		if (null == axtSumAmountByInvest) {
			axtSumAmountByInvest = 0D;
		}
		// 已完成项目，用户累计出借收益=本息（安心投）.
		WloanTermUserPlan axtUserPlan = new WloanTermUserPlan();
		axtUserPlan.setUserId(jedisUserId);
		axtUserPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
		axtUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
		WloanTermProject axtUserPlanProject = new WloanTermProject();
		List<String> axtUserPlStateItem = new ArrayList<String>();
		axtUserPlStateItem.add(WloanTermProjectService.FINISH);
		axtUserPlanProject.setStateItem(axtUserPlStateItem);
		axtUserPlanProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
		axtUserPlan.setWloanTermProject(axtUserPlanProject);
		Double axtSumAmountByPlan = wloanTermUserPlanDao.findSumInterestByPlan(axtUserPlan);
		if (null == axtSumAmountByPlan) {
			axtSumAmountByPlan = 0D;
		}

		// 本息 - 本金 = 已完成项目(n-1)期收益.
		if (axtSumAmountByPlan > axtSumAmountByInvest) {
			aInerest = NumberUtils.scaleDouble(axtSumAmountByPlan - axtSumAmountByInvest);
		}

		// 还款中项目，用户累计出借收益（安心投）.
		WloanTermUserPlan axtWloanTermUserPlan = new WloanTermUserPlan();
		axtWloanTermUserPlan.setUserId(jedisUserId);
		axtWloanTermUserPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
		axtWloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
		WloanTermProject axtUserPlanWloanTermProject = new WloanTermProject();
		List<String> axtUserPlanStateItem = new ArrayList<String>();
		axtUserPlanStateItem.add(WloanTermProjectService.REPAYMENT);
		axtUserPlanStateItem.add(WloanTermProjectService.FINISH);
		axtUserPlanWloanTermProject.setStateItem(axtUserPlanStateItem);
		axtUserPlanWloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
		axtWloanTermUserPlan.setWloanTermProject(axtUserPlanWloanTermProject);
		Double axtUserPlanSumInterestByPlan = wloanTermUserPlanDao.findSumInterestByPlan(axtWloanTermUserPlan);
		if (null == axtUserPlanSumInterestByPlan) {
			aInerest = aInerest + 0D;
		} else {
			aInerest = NumberUtils.scaleDouble(aInerest + axtUserPlanSumInterestByPlan);
		}

		// 用户累计出借金额（供应链）.
		double gAmount = 0d;
		project.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
		invest.setWloanTermProject(project);
		Double gylSumAmount = wloanTermInvestDao.findSumAmountByInvest(invest);
		if (null == gylSumAmount) {
			gAmount = 0D;
		} else {
			gAmount = gylSumAmount;
		}
		double gInerest = 0d;
		// 已结束项目，用户累计出借本金（供应链）.
		axtWloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
		axtInvest.setWloanTermProject(axtWloanTermProject);
		Double gylSumAmountByInvest = wloanTermInvestDao.findSumAmountByInvest(axtInvest);
		if (null == gylSumAmountByInvest) {
			gylSumAmountByInvest = 0D;
		}
		// 本息.
		axtUserPlanProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
		axtUserPlan.setWloanTermProject(axtUserPlanProject);
		Double gylSumAmountByPlan = wloanTermUserPlanDao.findSumInterestByPlan(axtUserPlan);
		if (null == gylSumAmountByPlan) {
			gylSumAmountByPlan = 0D;
		}

		// 本息 - 本金 = 已完成项目(n-1)期收益.
		if (gylSumAmountByPlan > gylSumAmountByInvest) {
			gInerest = NumberUtils.scaleDouble(gylSumAmountByPlan - gylSumAmountByInvest);
		}

		// 还款中项目，用户累计出借收益（供应链）.
		axtUserPlanWloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
		axtWloanTermUserPlan.setWloanTermProject(axtUserPlanWloanTermProject);
		Double gylUserPlanSumInterestByPlan = wloanTermUserPlanDao.findSumInterestByPlan(axtWloanTermUserPlan);
		if (null == gylUserPlanSumInterestByPlan) {
			gInerest = gInerest + 0D;
		} else {
			gInerest = NumberUtils.scaleDouble(gInerest + gylUserPlanSumInterestByPlan);
		}

		result.put("state", "0");
		result.put("message", "个人累计投资响应成功");
		result.put("aAmount", NumberUtils.scaleDouble(aAmount));
		result.put("gAmount", NumberUtils.scaleDouble(gAmount));
		result.put("aInerest", NumberUtils.scaleDouble(aInerest));
		result.put("gInerest", NumberUtils.scaleDouble(gInerest));

		// if (list != null && list.size() > 0) {
		// for (int i = 0; i < list.size(); i++) {
		// // 安心投累计
		// if
		// (list.get(i).getWloanTermProject().getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1))
		// {
		// aAmount = aAmount + list.get(i).getAmount();
		// WloanTermUserPlan userPlan = new WloanTermUserPlan();
		// userPlan.setWloanTermInvest(list.get(i));
		// List<WloanTermUserPlan> planList =
		// wloanTermUserPlanService.findList(userPlan);
		// if (planList != null && planList.size() > 0) {
		// for (int j = 0; j < planList.size(); j++) {
		// if
		// (planList.get(j).getState().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3))
		// {
		// if
		// (planList.get(j).getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1))
		// {
		// aInerest = aInerest + (planList.get(j).getInterest() -
		// list.get(i).getAmount());
		// } else {
		// aInerest = aInerest + planList.get(j).getInterest();
		// }
		// }
		// }
		// }
		// }
		// // 供应链累计
		// else if
		// (list.get(i).getWloanTermProject().getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2))
		// {
		// gAmount = gAmount + list.get(i).getAmount();
		// WloanTermUserPlan userPlan = new WloanTermUserPlan();
		// userPlan.setWloanTermInvest(list.get(i));
		// List<WloanTermUserPlan> planList =
		// wloanTermUserPlanService.findList(userPlan);
		// if (planList != null && planList.size() > 0) {
		// for (int j = 0; j < planList.size(); j++) {
		// if
		// (planList.get(j).getState().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3))
		// {
		// if
		// (planList.get(j).getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1))
		// {
		// gInerest = gInerest + (planList.get(j).getInterest() -
		// list.get(i).getAmount());
		// } else {
		// gInerest = gInerest + planList.get(j).getInterest();
		// }
		// }
		// }
		// }
		// }
		// }
		// result.put("state", "0");
		// result.put("message", "个人累计投资响应成功");
		// result.put("aAmount", NumberUtils.scaleDoubleStr(aAmount));
		// result.put("gAmount", NumberUtils.scaleDoubleStr(gAmount));
		// result.put("aInerest", NumberUtils.scaleDouble(aInerest));
		// result.put("gInerest", NumberUtils.scaleDouble(gInerest));
		// } else {
		// result.put("state", "1");
		// result.put("message", "个人累计投资接口数据为空");
		// result.put("aAmount", aAmount);
		// result.put("gAmount", gAmount);
		// result.put("aInerest", aInerest);
		// result.put("gInerest", gInerest);
		// }
		return result;
	}

	/**
	 * 用户邀请链接二维码
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/getUserQRCode")
	public Map<String, Object> getUserQRCode(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				if (userInfo != null) {

					AnnexFile annexFiles = annexFileService.findByOtherId(jedisUserId);
					if (annexFiles != null) {
						result.put("state", "0");
						result.put("message", "用户邀请链接二维码成功");
						result.put("path", Global.getConfig("pdf_show_path") + annexFiles.getUrl().split("data")[1]);
						// result.put("path", Global.getConfig("pdf_show_path")
						// + annexFiles.getUrl());
						result.put("refCode", userInfo.getName());
					} else {
						String url = Global.getConfig("wap_invite_url");
						String userName = userInfo.getName();
						String inviteUrl = url + userName;
						String path = QRCodeUtils.initQRCode(inviteUrl, userInfo.getName());
						AnnexFile annexFile = new AnnexFile();
						annexFile.setId(IdGen.uuid());
						annexFile.setOtherId(jedisUserId);
						annexFile.setUrl(path);
						annexFile.setCreateDate(new Date());
						annexFile.setUpdateDate(new Date());
						annexFile.setType("200");//
						annexFile.setRemarks("用户二维码");
						int i = annexFileDao.insert(annexFile);
						if (i > 0) {
							LOG.info("用户二维码保存成功");
						}
						result.put("state", "0");
						result.put("message", "用户邀请链接二维码成功");
						result.put("path", Global.getConfig("pdf_show_path") + path.split("data")[1]);
						result.put("refCode", userInfo.getName());
					}
				} else {
					UserInfo userInfo2 = userInfoDao.get(jedisUserId);
					if (userInfo2 != null) {
						AnnexFile annexFiles = annexFileService.findByOtherId(jedisUserId);
						if (annexFiles != null) {
							result.put("state", "0");
							result.put("message", "用户邀请链接二维码成功");
							result.put("path", Global.getConfig("pdf_show_path") + annexFiles.getUrl().split("data")[1]);
							result.put("refCode", "");
						} else {
							String url = Global.getConfig("wap_invite_url");
							String userName = userInfo2.getName();
							String inviteUrl = url + userName;
							String path = QRCodeUtils.initQRCode(inviteUrl, "");
							AnnexFile annexFile = new AnnexFile();
							annexFile.setId(IdGen.uuid());
							annexFile.setOtherId(jedisUserId);
							annexFile.setUrl(path);
							annexFile.setCreateDate(new Date());
							annexFile.setUpdateDate(new Date());
							annexFile.setType("200");//
							annexFile.setRemarks("用户二维码");
							int i = annexFileDao.insert(annexFile);
							if (i > 0) {
								LOG.info("用户二维码保存成功");
							}
							result.put("state", "0");
							result.put("message", "用户邀请链接二维码成功");
							result.put("path", Global.getConfig("pdf_show_path") + path.split("data")[1]);
							result.put("refCode", "");
						}
					} else {
						result.put("state", "4");
						result.put("message", "用户登录超时，请重新登录");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 
	 * methods: findMyFriendsInvestList <br>
	 * description: 我的账户-邀请好友-好友出借积分统计. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月11日 下午1:53:00
	 * 
	 * @param from
	 *            请求来源
	 * @param token
	 *            用户唯一标识
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页大小
	 * @return
	 * @throws ParseException
	 */
	@POST
	@Path("/findMyFriendsInvestList")
	public Map<String, Object> findMyFriendsInvestList(@FormParam("from") String from, @FormParam("token") String token, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize) throws ParseException {

		/**
		 * 结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		/**
		 * 数据域.
		 */
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		LevelDistribution entity = new LevelDistribution();
		UserBounsHistory userBounsHistory = new UserBounsHistory();
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			entity.setUserId(jedisUserId);
			userBounsHistory.setUserId(jedisUserId);
		}

		// 我的好友累计出借总额.
		Double myFriendsInvestTotalAmount = 0D;

		/**
		 * 好友出借统计数据封装.
		 */
		// 有过出借记录的好友.
		List<UserBounsHistory> friendsIntegralList = userBounsHistoryDao.findFriendsIntegralByTransId(userBounsHistory);
		if (friendsIntegralList != null && friendsIntegralList.size() > 0) {
			for (int i = 0; i < friendsIntegralList.size(); i++) {
				UserBounsHistory userBounsHistor = friendsIntegralList.get(i);
				// 累计出借金额
				WloanTermInvest invest = new WloanTermInvest();
				invest.setUserId(userBounsHistor.getUserId());
				Double investTotalAmount = wloanTermInvestDao.findInvestTotalAmountByUserId(invest);
				// 好友的累计出借金额进行累加.
				myFriendsInvestTotalAmount = myFriendsInvestTotalAmount + investTotalAmount;
			}

		}

		// 我的好友积分统计.
		List<Map<String, Object>> bounsHistoryList = new ArrayList<Map<String, Object>>();
		// 分页查询我的好友列表.
		Page<LevelDistribution> page = new Page<LevelDistribution>();
		page.setPageNo(StringUtils.toInteger(pageNo));
		page.setPageSize(StringUtils.toInteger(pageSize));
		Page<LevelDistribution> levelDistributionPages = levelDistributionService.findLevelDistributionPage(page, entity);
		if (levelDistributionPages != null) {
			List<LevelDistribution> list = levelDistributionPages.getList();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					LevelDistribution levelDistribution = list.get(i);
					String userId = levelDistribution.getUserInfo().getId();
					boolean flag = false;
					if (friendsIntegralList != null && friendsIntegralList.size() > 0) {
						for (int j = 0; j < friendsIntegralList.size(); j++) {
							UserBounsHistory userBounsHistor = friendsIntegralList.get(j);
							if (userId != null) { // 非空判断.
								if (userId.equals(userBounsHistor.getUserId())) { // 好友出借获取积分进行统计.
									flag = true;
									if (flag) { // 好友有出借的动作.
										Map<String, Object> map = new HashMap<String, Object>();
										// 好友手机号码（脱敏处理）.
										map.put("phone", CommonStringUtils.mobileEncrypt(userBounsHistor.getUserInfo().getName()));
										// 我的奖励累计积分.
										map.put("amount", userBounsHistor.getAmount());
										// 注册时间.
										map.put("registerDateTime", DateUtils.formatDate(userBounsHistor.getUserInfo().getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
										// 注册日期.
										map.put("registerDate", DateUtils.formatDate(userBounsHistor.getUserInfo().getRegisterDate(), "yyyy-MM-dd"));
										// 累计出借金额
										WloanTermInvest invest = new WloanTermInvest();
										invest.setUserId(userBounsHistor.getUserId());
										Double investTotalAmount = wloanTermInvestDao.findInvestTotalAmountByUserId(invest);
										map.put("investTotalAmount", NumberUtils.scaleDoubleStr(investTotalAmount));
										bounsHistoryList.add(map);
									}
								}
							}
						}
					}
					if (!flag) { // 好友没有出借的动作.
						Map<String, Object> map = new HashMap<String, Object>();
						// 好友手机号码（脱敏处理）.
						map.put("phone", CommonStringUtils.mobileEncrypt(levelDistribution.getUserInfo().getName()));
						// 我的奖励累计积分.
						map.put("amount", "0");
						// 注册时间.
						map.put("registerDateTime", DateUtils.formatDate(levelDistribution.getUserInfo().getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
						// 注册日期.
						map.put("registerDate", DateUtils.formatDate(levelDistribution.getUserInfo().getRegisterDate(), "yyyy-MM-dd"));
						// 累计出借金额
						map.put("investTotalAmount", "0.00");
						bounsHistoryList.add(map);
					}
				}
			}

			data.put("myFriendsInvestTotalAmount", NumberUtils.scaleDoubleStr(myFriendsInvestTotalAmount));
			data.put("pageNo", levelDistributionPages.getPageNo());
			data.put("pageSize", levelDistributionPages.getPageSize());
			data.put("totalCount", levelDistributionPages.getCount());
			data.put("pageCount", levelDistributionPages.getLast());
			data.put("bounsHistoryList", bounsHistoryList);
			result.put("state", "0");
			result.put("message", "我的好友列表请求成功");
			result.put("data", data);
		} else {
			data.put("myFriendsInvestTotalAmount", NumberUtils.scaleDoubleStr(myFriendsInvestTotalAmount));
			data.put("bounsHistoryList", bounsHistoryList);
			result.put("state", "1");
			result.put("message", "暂无邀请好友记录");
			result.put("data", data);
		}

		return result;
	}

	/**
	 * 邀请好友进行投资获取的积分
	 * 
	 * @param from
	 * @param token
	 * @return
	 * @throws ParseException
	 */
	@POST
	@Path("/getMyInviteList")
	public Map<String, Object> getMyInviteList(@FormParam("from") String from, @FormParam("token") String token, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize) throws ParseException {

		Map<String, Object> result = new HashMap<String, Object>();
		UserBounsHistory uBounsHistory = new UserBounsHistory();
		List<Map<String, Object>> uBounsHistoryList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			uBounsHistory.setUserId(jedisUserId);

		}

		Page<UserBounsHistory> page = new Page<UserBounsHistory>();
		page.setPageNo(StringUtils.toInteger(pageNo));
		page.setPageSize(StringUtils.toInteger(pageSize));
		Page<UserBounsHistory> pages = userBounsHistoryService.findInvitePageByUserId(page, uBounsHistory);
		if (pages != null) {
			List<UserBounsHistory> lists = pages.getList();
			if (lists != null && lists.size() > 0) {
				for (int i = 0; i < lists.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("phone", Util.hideString(lists.get(i).getUserInfo().getName(), 3, 4));
					Double amount = lists.get(i).getAmount();
					map.put("amount", amount);
					map.put("createDate", df.format(lists.get(i).getCreateDate()));
					String registerDate = lists.get(i).getRegisterDate();
					String substring = registerDate.substring(0, registerDate.length() - 11);
					map.put("registerDate", substring);// 注册时间
					// 出借状态
					if (lists.get(i).getBounsType().equals("0")) {
						WloanTermInvest wloanTermInvest = wloanTermInvestDao.get(lists.get(i).getTransId());
						Double LendStateByFriend = wloanTermInvest.getAmount();
						map.put("LendStateByFriend", LendStateByFriend);
					} else {
						map.put("LendStateByFriend", "未出借");
					}

					uBounsHistoryList.add(map);
				}
			}

			data.put("pageNo", pages.getPageNo());
			data.put("pageSize", pages.getPageSize());
			data.put("totalCount", pages.getCount());
			data.put("pageCount", pages.getLast());
			data.put("uBounsHistoryList", uBounsHistoryList);

			result.put("state", "0");
			result.put("message", "邀请好友积分所得列表响应成功");
			result.put("data", data);
		} else {
			result.put("state", "1");
			result.put("message", "邀请好友积分所得列表数据为空");
			result.put("data", data);
		}
		return result;
	}

	/**
	 * 用户出借回款计划列表
	 * 
	 * @param from
	 * @param token
	 * @param investId
	 * @return
	 */
	@POST
	@Path("/getUserInterestList")
	public Map<String, Object> getUserInterestList(@FormParam("from") String from, @FormParam("token") String token, @FormParam("investId") String investId) {

		Map<String, Object> result = new HashMap<String, Object>();
		UserInfo user = new UserInfo();
		List<Map<String, Object>> userPlanList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(investId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				user = userInfoDao.getCgb(jedisUserId);
				WloanTermUserPlan userPlan = new WloanTermUserPlan();
				WloanTermInvest invest = new WloanTermInvest();
				invest.setId(investId);
				userPlan.setUserInfo(user);
				userPlan.setWloanTermInvest(invest);
				List<WloanTermUserPlan> list = wloanTermUserPlanService.findList(userPlan);
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				WloanTermInvest wloantermInvest = wloanTermInvestService.get(investId);
				Double investAmount = 0d;
				if (wloantermInvest != null) {
					investAmount = wloantermInvest.getAmount();
				}
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						userPlan = list.get(i);
						if (userPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2)) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("state", userPlan.getState());
							map.put("repaymentDate", df.format(userPlan.getRepaymentDate()));
							map.put("amount", NumberUtils.scaleDoubleStr(userPlan.getInterest()));
							map.put("principal", userPlan.getPrincipal());
							userPlanList.add(map);
						} else {
							// 利息
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("state", userPlan.getState());
							map.put("repaymentDate", df.format(userPlan.getRepaymentDate()));
							map.put("amount", NumberUtils.scaleDoubleStr(userPlan.getInterest() - investAmount));
							map.put("principal", WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2);
							userPlanList.add(map);
							map = new HashMap<String, Object>();
							// 本金
							map.put("state", userPlan.getState());
							map.put("repaymentDate", df.format(userPlan.getRepaymentDate()));
							map.put("amount", investAmount);
							map.put("principal", WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
							userPlanList.add(map);
						}
					}
				}
				data.put("userPlanList", userPlanList);
				result.put("state", "0");
				result.put("message", "展示金额回款成功");
				result.put("data", data);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			result.put("state", "1");
			result.put("message", e.getMessage());
		}
		return result;
	}

	/**
	 * 获取用户存管宝的可用余额和冻结金额
	 * 
	 * @param from
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getCgbUserTolAmount")
	public Map<String, Object> getCgbUserTolAmount(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {

				Map<String, String> data = userInfoService.getUserCgbAmount(jedisUserId);
				result.put("state", "0");
				result.put("message", "获取用户存管宝的可用余额和冻结金额成功");
				result.put("data", data);
				return result;
			} else {
				throw new Exception("获取用户信息失败");
			}
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", e.getMessage());
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 校验用户旧密码---前端加密
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/newCheckOldPwd")
	public Map<String, Object> newCheckOldPwd(@FormParam("from") String from, @FormParam("token") String token, @FormParam("pwd") String pwd) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) && StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				String password = pwd;
				if (password.equals(userInfo.getPwd())) {
					result.put("state", "0");
					result.put("message", "登录密码校验成功");
					result.put("data", userInfo.getName());
				} else {
					result.put("state", "3");
					result.put("message", "登录密码校验失败");
				}
			} else {
				result.put("state", "1");
				result.put("message", "系统异常");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户登录密码修改---前端加密
	 * 
	 * @param from
	 * @param token
	 * @param newphone
	 * @return
	 */
	@POST
	@Path("/newUpdateUserPwd")
	public Map<String, Object> newUpdateUserPwd(@FormParam("from") String from, @FormParam("token") String token, @FormParam("pwd") String pwd) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) & StringUtils.isBlank(pwd)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
				userInfo.setPwd(pwd);
				userInfoDao.updateUserPwd(userInfo);
				result.put("state", "0");
				result.put("message", "登录密码修改成功");
				// 修改交易密码成功发送微信、短信提醒
				weixinSendTempMsgService.sendUpdatePwdMsg("登录", userInfo);
			} else {
				result.put("state", "1");
				result.put("message", "登录密码修改失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 用户的回款金额展示---新版
	 */
	@POST
	@Path("/getNewUserInterestCount")
	public Map<String, Object> getNewUserInterestCount(@FormParam("from") String from, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		UserInfo user = new UserInfo();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				user = userInfoDao.getCgb(jedisUserId);
				WloanTermUserPlan userPlan = new WloanTermUserPlan();
				userPlan.setUserInfo(user);
				Date beginDate = DateUtils.getDateOfString(DateUtils.dayOfMonth_Start());
				userPlan.setBeginDate(beginDate);
				;
				List<WloanTermUserPlan> list = wloanTermUserPlanService.findNewInterestCount(userPlan);
				List<String[]> repayDate = new ArrayList<String[]>();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						userPlan = list.get(i);
						String[] data = new String[2];
						data[0] = df.format(userPlan.getRepaymentDate());
						data[1] = userPlan.getInterest().toString();
						repayDate.add(data);
					}
				}
				result.put("state", "0");
				result.put("message", "展示金额回款成功");
				result.put("data", repayDate);
				return result;
			} else {

				throw new Exception("获取回款信息失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			result.put("state", "1");
			result.put("message", e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * 方法: findUserRepayPlans <br>
	 * 描述: 查找用户的还款计划---新版. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月31日 上午10:37:16
	 * 
	 * @param from
	 *            请求来源
	 * @param token
	 *            用户唯一标识
	 * @return
	 */
	@POST
	@Path("/findNewUserRepayPlanStatistical")
	public Map<String, Object> findNewUserRepayPlanStatistical(@FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("token") String token, @FormParam("nowDate") String nowDate) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据集.
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(token) || StringUtils.isBlank(nowDate)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		if (from.equals(FROM_3)) {
			LOG.info(this.getClass() + "，请求来源：ANDROID.");
		} else if (from.equals(FROM_4)) {
			LOG.info(this.getClass() + "，请求来源：IOS.");
		}

		/**
		 * 获取token.
		 */
		try {
			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoDao.getCgb(jedisUserId);
				if (null != user) {
					// 封装客户还款计划查询.
					WloanTermUserPlan wloanTermUserPlan = new WloanTermUserPlan();
					// 分页.
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(Integer.parseInt(pageNo));
					page.setPageSize(Integer.parseInt(pageSize));
					wloanTermUserPlan.setPage(page);
					// 设置用户信息.
					wloanTermUserPlan.setUserInfo(user);
					// 还款中.
					// wloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					// 还款日.
					wloanTermUserPlan.setRepaymentDate(DateUtils.getShortDateOfString(nowDate));
					List<WloanTermUserPlan> list = wloanTermUserPlanDao.findUserRepayPlanStatistical(wloanTermUserPlan);
					LOG.info("pageNo = " + page.getPageNo() + "，pageSize = " + page.getPageSize() + "，count = " + page.getCount());

					// 获取总页数
					// 总页数.
					long pageCount;
					if (page.getCount() % page.getPageSize() == 0) {
						pageCount = page.getCount() / page.getPageSize();
					} else
						pageCount = page.getCount() / page.getPageSize() + 1;

					LOG.info("总页数 -> pageCount =  " + pageCount);

					if (page.getPageNo() > pageCount) {
						// 总页数.
						data.put("count", page.getCount());
						// 页码.
						data.put("pageNo", page.getPageNo());
						// 每页大小.
						data.put("pageSize", page.getPageSize());
						// 总页码.
						data.put("pageCount", pageCount);
						// 计划列表.
						data.put("plans", new ArrayList<UserRepayPlanPojo>());
						// 成功状态.
						result.put("state", "0");
						// 成功消息.
						result.put("message", "接口请求成功！");
						// 数据域.
						result.put("data", data);
						return result;
					} else {
						// 新的数据域.
						List<UserRepayPlanPojo> pojos = new ArrayList<UserRepayPlanPojo>();
						for (WloanTermUserPlan entity : list) { // 遍历客户还款计划.
							entity.setUserInfo(user); // 设置用户信息.
							Double remainingRepayAmount = wloanTermUserPlanDao.getWaitRepayMoney(entity);
							UserRepayPlanPojo pojo = new UserRepayPlanPojo();
							pojo.setProjectName(entity.getWloanTermProject().getName());
							pojo.setProjectSn(entity.getWloanTermProject().getSn());
							pojo.setRepaymentDate(DateUtils.formatDate(entity.getRepaymentDate(), "yyyy-MM-dd"));
							pojo.setNowRepayAmount(NumberUtils.scaleDouble(entity.getInterest()));
							pojo.setRemainingRepayAmount(NumberUtils.scaleDouble(remainingRepayAmount));
							pojo.setStatus(entity.getState());
							pojo.setType(entity.getPrincipal());
							pojos.add(pojo);
						}
						// 总页数.
						data.put("count", page.getCount());
						// 页码.
						data.put("pageNo", page.getPageNo());
						// 每页大小.
						data.put("pageSize", page.getPageSize());
						// 总页码.
						data.put("pageCount", pageCount);
						// 计划列表.
						data.put("plans", pojos);
						// 成功状态.
						result.put("state", "0");
						// 成功消息.
						result.put("message", "接口请求成功！");
						// 数据域.
						result.put("data", data);
						return result;
					}

				} else {
					result.put("state", "4");
					result.put("message", "系统超时！");
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时！");
				return result;
			}
		} catch (Exception e) {
			LOG.info(this.getClass() + "，异常信息：" + e.getMessage());
			result.put("state", "1");
			result.put("message", e.getMessage());
			return result;
		}
	}

	/**
	 * 个人投资记录H5----改版
	 * 
	 * @param from
	 * @param projectProductType
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @param projectState
	 * @param state
	 * @return
	 */
	@POST
	@Path("/getNewMyBidsdetailH5")
	public Map<String, Object> getNewMyBidsdetailH5(@FormParam("from") String from, @FormParam("projectProductType") String projectProductType, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("token") String token, @FormParam("projectstate") String projectState, @FormParam("state") String state) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> userBidHistoryList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		Page<WloanTermInvest> page = new Page<WloanTermInvest>();
		WloanTermInvest loanTermInvest = new WloanTermInvest();
		WloanTermProject wloanTermProject = new WloanTermProject();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat dt = new DecimalFormat("######0.00");
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(projectProductType)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		/**
		 * 获取token.
		 */
		String jedisUserId = JedisUtils.get(token);
		if (!StringUtils.isBlank(jedisUserId)) {
			UserInfo user = userInfoDao.getCgb(jedisUserId);
			if (user == null) {
				user = userInfoDao.get(jedisUserId);
			}
			loanTermInvest.setUserInfo(user);

		}
		page.setOrderBy("beginDate desc, state desc ");
		page.setPageNo(StringUtils.toInteger(pageNo));
		page.setPageSize(StringUtils.toInteger(pageSize));
		loanTermInvest.setDelFlag("0");
		wloanTermProject.setProjectProductType(projectProductType);
		if (state != null && state.length() > 0) {
			if (state.equals("1")) { // 募集中
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("4");// 上线.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("2")) { // 还款中
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("5");// 满标.
				stateItem.add("6");// 还款中.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("3")) { // 已结束.
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("7");// 已结束.
				wloanTermProject.setStateItem(stateItem);
				loanTermInvest.setWloanTermProject(wloanTermProject);
			} else if (state.equals("4")) { // 新版pc端
				List<String> stateItem = new ArrayList<String>();
				stateItem.add("4");// 上线.
				stateItem.add("5");// 满标.
				stateItem.add("6");// 还款中.
				stateItem.add("7");// 已结束.
				wloanTermProject.setStateItem(stateItem);
				if (projectState != null) {
					wloanTermProject.setState(projectState);
				}
				loanTermInvest.setWloanTermProject(wloanTermProject);
			}
		}
		loanTermInvest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
		Page<WloanTermInvest> pages = wloanTermInvestService.findPage(page, loanTermInvest);
		if (pages != null) {
			List<WloanTermInvest> lists = pages.getList();
			if (lists != null && lists.size() > 0) {
				page.setList(lists);
				for (int i = 0; i < lists.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("bidId", lists.get(i).getId());
					map.put("projectId", lists.get(i).getWloanTermProject().getId());
					map.put("projectName", lists.get(i).getWloanTermProject().getName());
					map.put("projectType", lists.get(i).getWloanTermProject().getProjectType());
					map.put("projectProductType", lists.get(i).getWloanTermProject().getProjectProductType());
					map.put("amount", dt.format(lists.get(i).getAmount()));
					map.put("state", lists.get(i).getWloanTermProject().getState());
					map.put("span", lists.get(i).getWloanTermProject().getSpan());
					map.put("rate", lists.get(i).getWloanTermProject().getAnnualRate());
					map.put("sn", lists.get(i).getWloanTermProject().getSn());
					map.put("interest", dt.format(lists.get(i).getInterest()));
					// 投资日期.
					Date investDate = lists.get(i).getBeginDate();
					map.put("dtime", df.format(investDate));
					// 新版本上线日期.
					Date newVersionDate = DateUtils.parseDate("2016-06-16 00:00:00");
					// 判断新旧合同展示URL.
					if (investDate.before(newVersionDate)) {
						map.put("bid_signature", "http://112.126.73.20/" + lists.get(i).getContractPdfPath());
					} else {
						map.put("bid_signature", Global.getConfig("pdf_show_path") + lists.get(i).getContractPdfPath());
					}
					if (lists.get(i).getWloanTermProject().getState().equals("4")) {
						map.put("endDate", lists.get(i).getWloanTermProject().getLoanDate() == null ? "" : df.format(lists.get(i).getWloanTermProject().getLoanDate()));
					} else {
						map.put("endDate", lists.get(i).getWloanTermProject().getEndDate() == null ? "" : df.format(lists.get(i).getWloanTermProject().getEndDate()));
					}
					// 获取核心企业相关
					WloanTermProject project = wloanTermProjectService.get(lists.get(i).getWloanTermProject().getId());
					if (project != null) {
						// 获取核心企业简介
						String middlemenId = project.getReplaceRepayId();
						if (middlemenId != null) {
							CreditUserInfo creditUserInfo = creditUserInfoService.get(middlemenId);
							if (creditUserInfo != null) {
								map.put("creditName", creditUserInfo.getEnterpriseFullName());
							} else {
								map.put("creditName", null);
							}
							CreditAnnexFile annexFile = new CreditAnnexFile();
							annexFile.setOtherId(middlemenId);
							annexFile.setType("30");
							List<CreditAnnexFile> annexFileList = creditAnnexFileService.findList(annexFile);
							if (annexFileList != null && annexFileList.size() > 0) {
								map.put("creditUrl", annexFileList.get(0).getRemark() + "?middlemenId=" + middlemenId);
							} else {
								map.put("creditUrl", null);
							}
						} else {
							map.put("creditName", null);
							map.put("creditUrl", null);
						}
					}
					userBidHistoryList.add(map);
				}
			}
			data.put("pageNo", pages.getPageNo());
			data.put("pageSize", pages.getPageSize());
			data.put("totalCount", pages.getCount());
			data.put("pageCount", pages.getLast());
			data.put("userBidHistoryList", userBidHistoryList);

			result.put("state", "0");
			result.put("message", "个人投资记录响应成功");
			result.put("data", data);

		} else {
			data.put("pageNo", "");
			data.put("pageSize", "");
			data.put("totalCount", 1);
			data.put("userBidHistoryList", userBidHistoryList);

			result.put("state", "0");
			result.put("message", "个人投资记录响应成功");
			result.put("data", data);
		}

		return result;
	}
}
