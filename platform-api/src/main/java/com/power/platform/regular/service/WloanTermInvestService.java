package com.power.platform.regular.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.ARateCouponDicDao;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.BrokerageDao;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.dao.ZtmgPartnerPlatformDao;
import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.BrokerageService;
import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.IsHolidayOrBirthday;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.utils.CreateSupplyChainPdfContract;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;

/**
 * 
 * 类: WloanTermInvestService <br>
 * 描述: 定期融资投资记录Service Interface. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月4日 下午7:14:59
 */
@Service("wloanTermInvestService")
public class WloanTermInvestService extends CrudService<WloanTermInvest> {

	/**
	 * 投资期限：30天.
	 */
	public static final String SPAN_30 = "30";

	/**
	 * 投资期限：90天.
	 */
	public static final String SPAN_90 = "90";

	/**
	 * 投资期限：180天.
	 */
	public static final String SPAN_180 = "180";

	/**
	 * 投资期限：360天.
	 */
	public static final String SPAN_360 = "360";

	/**
	 * 用户类型，1：全部.
	 */
	public static final Integer USER_FLAG_ALL = 1;

	/**
	 * 用户类型，2：新用户.
	 */
	public static final Integer USER_FLAG_NEW = 2;

	/**
	 * 用户类型，3：老用户.
	 */
	public static final Integer USER_FLAG_OLD = 3;

	/**
	 * 融资投资记录状态，1：投标成功.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_1 = "1";
	/**
	 * 融资投资记录状态，2：投标失败.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_2 = "2";
	/**
	 * 融资投资记录状态，3：投标成功.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_3 = "3";
	/**
	 * 融资投资记录状态，9：投标成功.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_9 = "9";

	/**
	 * 商户自己的RSA公钥.
	 */
	private static final String MERCHANT_RSA_PUBLIC_KEY = Global.getConfig("MERCHANT_RSA_PUBLIC_KEY");
	/**
	 * 商户自己的RSA私钥.
	 */
	private static final String MERCHANT_RSA_PRIVATE_KEY = Global.getConfig("MERCHANT_RSA_PRIVATE_KEY");
	/**
	 * 测试环境网关地址.
	 */
	private static final String HOST = Global.getConfig("HOST");
	/**
	 * 商户号.
	 */
	private static final String MERCHANT_ID = Global.getConfig("MERCHANT_ID");

	private static final Logger logger = Logger.getLogger(WloanTermInvestService.class);

	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private ARateCouponDicDao aRateCouponDicDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private AUserAwardsHistoryDao userVouchersDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private BrokerageService wbrokerageService;
	@Resource
	private WloanSubjectService wloanSubjectService;
	@Resource
	private StationLettersService stationLettersService;
	@Resource
	private WGuaranteeCompanyService wGuaranteeCompanyService;
	@Resource
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Resource
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Resource
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Resource
	private UserBounsPointService userBounsPointService;
	@Resource
	private UserBounsHistoryService userBounsHistoryService;
	@Resource
	private LevelDistributionDao levelDistributionDao;
	@Resource
	private ZtmgPartnerPlatformDao ztmgPartnerPlatformDao;
	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private BrokerageDao brokerageDao;

	@Autowired
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;

	@Resource
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Autowired
	private UserBounsPointDao userBounsPointDao;
	@Autowired
	private UserBounsHistoryDao userBounsHistoryDao;

	@Override
	protected CrudDao<WloanTermInvest> getEntityDao() {

		return wloanTermInvestDao;
	}

	public List<WloanTermInvest> volunterrList() {

		return wloanTermInvestDao.volunterrList();

	}

	/**
	 * 
	 * 方法: findOldUserPage <br>
	 * 描述: 老用户统计数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月12日 上午10:13:37
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<WloanTermInvest> findOldUserPage(Page<WloanTermInvest> page, WloanTermInvest entity) {

		entity.setPage(page);
		page.setList(getEntityDao().findList(entity));
		return page;
	}

	/**
	 * 
	 * 方法: findNewUserPage <br>
	 * 描述: 新用户统计数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年7月12日 上午10:12:48
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<WloanTermInvest> findNewUserPage(Page<WloanTermInvest> page, WloanTermInvest entity) {

		entity.setPage(page);
		page.setList(getEntityDao().findList(entity));
		return page;
	}

	/**
	 * 
	 * 方法: updateWloanTermInvest <br>
	 * 描述: 更新. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:40:38
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateWloanTermInvest(WloanTermInvest wloanTermInvest) {

		int flag = 0;
		try {
			flag = wloanTermInvestDao.update(wloanTermInvest);
			logger.info("fn:updateWloanTermInvest,{更新保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateWloanTermInvest,{更新保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: insertWloanTermInvest <br>
	 * 描述: 新增. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月5日 上午11:40:10
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertWloanTermInvest(WloanTermInvest wloanTermInvest) {

		int flag = 0;
		try {
			flag = wloanTermInvestDao.insert(wloanTermInvest);
			logger.info("fn:insertWloanTermInvest,{新增保存成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertWloanTermInvest,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 根据用户ID查询定期投资总额（包括已完成项目）
	 * 
	 * @param userId
	 * @return
	 */
	public Double selectAmountTotle(String userId) {

		Double amountTotal = wloanTermInvestDao.selectAmountTotle(userId);
		return amountTotal;
	}

	/**
	 * 根据用户ID查询定期待收本金
	 * 
	 * @param userId
	 * @return
	 */
	public Double selectToBePrincipal(String userId) {

		Double toBePrincipal = wloanTermInvestDao.selectToBePrincipal(userId);
		return toBePrincipal;
	}

	/**
	 * 根据用户ID查询定期投资已收收益
	 * 
	 * @param userId
	 * @return
	 */
	public Double selectBeInterest(String userId) {

		Double beInterest = wloanTermInvestDao.selectBeInterest(userId);
		return beInterest;
	}

	/**
	 * 根据用户ID查询定期投资待收收益
	 * 
	 * @param userId
	 * @return
	 */
	public Double selectToBeInterest(String userId) {

		Double toBeInterest = wloanTermInvestDao.selectToBeInterest(userId);
		return toBeInterest;
	}

	/**
	 * 查询某一投资项目某个人投资金额
	 * 
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public Double findInvestAmountByProjectAndUser(String projectId, String userId) {

		return wloanTermInvestDao.findInvestAmountByProjectAndUser1(projectId, userId);
	}

	/**
	 * 定期投资方法
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public synchronized Map<String, Object> insertUserInvestInfo(String token, String projectId, Double amount, String vouid, UserInfo user, UserAccountInfo account, String ip) throws WinException, Exception {

		/**
		 * 查找客户投资记录.
		 */
		List<WloanTermInvest> findWloanTermInvestExists = wloanTermInvestDao.findWloanTermInvestExists(user.getId());

		/**
		 * 1、先校验用户是否使用抵用券
		 * 2、根据抵用券id查询抵用券信息，是否可用，（包括金额、日期校验）
		 * 3、查询项目信息，是否在融资期限内，剩余金额是否满足可投，投资金额是否符合要求
		 * 4、插入标的信息，更改账户信息，
		 * 5、流水
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		// 利息
		WloanTermProject project = wloanTermProjectDao.get(projectId);

		if (project == null) {
			throw new WinException("项目信息为null，系统错误");
		}

		Date createDate = new Date();
		Date now = DateUtils.getDateOfString(DateUtils.getDate(createDate, "yyyy-MM-dd"));
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now2 = sdf2.format(createDate).toString();
		Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
		Double days = DateUtils.getDistanceOfTwoDate(now, loan_date);
		Double interest = amount * project.getAnnualRate() * (project.getSpan()) / (365 * 100);

		// 判断项目是否过期
		if (createDate.getTime() >= loan_date.getTime()) {
			throw new WinException("该项目已到期，不能进行出借");
		}

		// 判断是否是新手标的
		String isNewType = project.getProjectType(); // 0-其他 、1-新手
		if (isNewType.equals("1")) {
			// 是新手标的，查看投资人是否是新手
			WloanTermInvest newUserInvest = new WloanTermInvest();
			newUserInvest.setUserInfo(user);
			List<WloanTermInvest> newUserInvests = wloanTermInvestDao.findList(newUserInvest);
			if (newUserInvests != null && newUserInvests.size() > 0) {
				throw new WinException("该项目只能新手出借");
			}
		}

		// 固定某个标的新手只可投资一万

		if (project.getId() == "ef2e5190a4c34ac5aa12bc02c2b6ed99" || project.getId().equals("ef2e5190a4c34ac5aa12bc02c2b6ed99")) {
			WloanTermInvest newUserInvest = new WloanTermInvest();
			newUserInvest.setUserInfo(user);
			List<WloanTermInvest> newUserInvests = wloanTermInvestDao.findList(newUserInvest);
			if (newUserInvests == null) {
				if (amount > 10000) {
					throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
				}
			}
		}

		// 客户投资金额
		amount = NumberUtils.scaleDouble(amount);
		interest = NumberUtils.scaleDouble(interest);

		// 项目融资金额(amount),此金额不做计算,用于展示当前项目的融资金额.
		Double projectAmount = project.getAmount();

		// 项目状态.
		String projectState = project.getState();
		if (WloanTermProjectService.FULL.equals(projectState)) {
			throw new WinException("该项目已经满标了");
		} else if (WloanTermProjectService.PUBLISH.equals(projectState)) {
			throw new WinException("该项目暂未开始投资");
		} else if (WloanTermProjectService.REPAYMENT.equals(projectState)) {
			throw new WinException("该项目已过融资期");
		} else if (WloanTermProjectService.FINISH.equals(projectState)) {
			throw new WinException("该项目已完成");
		} else {
			// 投资实际进度(currentRealAmount),此金额是客户累计投资金额(+).
			Double currentAmount = project.getCurrentAmount();
			if (null == currentAmount) {
				currentAmount = 0D;
			}
			// 当前项目剩余融资金额.
			Double balanceAmount = projectAmount - currentAmount;

			// 最后一次投资
			if (balanceAmount < project.getMinAmount()) {
				if (!amount.equals(balanceAmount)) {
					throw new WinException("该项目目前只能投资" + balanceAmount + "元");
				}
			} else {
				// 投资金额大于融资金额.
				if (amount > balanceAmount) {
					throw new WinException("您的投资金额大于可投资金额！");
				}
				// 最小投资、最大投资判断
				if (amount < project.getMinAmount() || amount > project.getMaxAmount()) {
					throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
				}
			}
		}

		Double vouAmount = 0d;
		Double addRate = 0d;
		Double addInterest = 0d;
		String investID = IdGen.uuid();
		// 获取抵用券信息
		if (!StringUtils.isBlank(vouid)) {
			// 用户使用抵用券，获取抵用券信息
			AUserAwardsHistory aUserAwardsHistory = userVouchersDao.get(vouid);
			if (aUserAwardsHistory.getType().equals("1")) { // 抵用券
				String canUseCoupon = project.getIsCanUseCoupon();
				if ("0" != canUseCoupon && !"0".equals(canUseCoupon)) {
					throw new WinException("该项目不可以使用抵用券");
				}
				AVouchersDic voucher = aVouchersDicDao.get(aUserAwardsHistory.getAwardId());
				// 抵用券起投金额判断
				Double beginAmount = voucher.getLimitAmount();
				if (amount < beginAmount) {
					// 客户投资金额小于起投金额
					throw new WinException("投资金额小于该优惠券起投金额");
				}
				// 优惠券是否过期判断
				Date voucherDate = aUserAwardsHistory.getCreateDate();
				Integer overDays = voucher.getOverdueDays();
				// 抵用券过期日期
				String overDate = DateUtils.getSpecifiedMonthAfterFormat(voucherDate, overDays);
				// 比较过期日期与当前日期的大小(返回值为 false )
				boolean flag = DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), overDate);
				if (!flag) { // 抵用券过期
					throw new WinException("抵用券已过期");
				}
				// 抵用券金额
				vouAmount = voucher.getAmount();
				// 投资实际需要金额
				amount = amount - vouAmount;

			} else { // 加息券
				String canUsePlusCoupon = project.getIsCanUsePlusCoupon();
				if ("0" != canUsePlusCoupon && !"0".equals(canUsePlusCoupon)) {
					throw new WinException("该项目不可以使用加息券");
				}
				ARateCouponDic aRateCouponDic = aRateCouponDicDao.get(aUserAwardsHistory.getAwardId());
				addRate = aRateCouponDic.getRate();
				// 加息券起投金额判断
				Double beginAmount = aRateCouponDic.getLimitAmount();
				if (amount < beginAmount) {
					throw new WinException("投资金额小于该加息券起投金额");
				}
				// 加息券是否过期判断
				Date voucherDate = aUserAwardsHistory.getCreateDate();
				Integer overDays = aRateCouponDic.getOverdueDays();
				String overDate = DateUtils.getSpecifiedMonthAfterFormat(voucherDate, overDays);
				boolean flag = DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), overDate);
				if (!flag) { // 抵用券过期
					throw new WinException("抵用券已过期");
				}
				// 加息券加息值
				addInterest = ((aRateCouponDic.getRate() + project.getAnnualRate()) / project.getAnnualRate()) * interest;
				// 投资实际利息值
				interest = NumberUtils.scaleDouble(addInterest);
			}

			// 更改抵用券状态、加标的
			logger.info(this.getClass().getName() + "——————更改抵用券状态开始");
			aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
			aUserAwardsHistory.setBidId(investID);
			aUserAwardsHistory.setUpdateDate(new Date());
			int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
			if (updateVoucher == 1) {
				logger.info(this.getClass().getName() + "——————更改抵用券状态成功");
				// 保存客户使用抵用券流水记录
				logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录开始");
				UserTransDetail userTransDetail = new UserTransDetail();
				userTransDetail.setId(IdGen.uuid()); // 主键ID.
				userTransDetail.setTransId(vouid); // 客户抵用券记录ID.
				userTransDetail.setUserId(user.getId()); // 客户账号ID.
				userTransDetail.setAccountId(user.getAccountId()); // 客户账户ID.
				userTransDetail.setTransDate(new Date()); // 抵用券使用时间.
				userTransDetail.setTrustType(UserTransDetailService.trust_type10); // 优惠券.
				userTransDetail.setAmount(aUserAwardsHistory.getType().equals("1") ? vouAmount : addInterest); // 优惠券金额.
				userTransDetail.setAvaliableAmount(0d); // 当前可用余额.
				userTransDetail.setInOutType(UserTransDetailService.in_type); // 优惠券收入.
				userTransDetail.setRemarks(aUserAwardsHistory.getType().equals("1") ? "抵用券" : "加息券"); // 备注信息.
				userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
				int userVoucherDetailFlag = userTransDetailDao.insert(userTransDetail);
				if (userVoucherDetailFlag == 1) {
					logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录成功");
				}
			}
		}

		// 查询账户信息（可用余额）
		account = userAccountInfoDao.get(account.getId());
		Double userAvailableAmount = NumberUtils.scaleDouble(account.getAvailableAmount());
		if (userAvailableAmount < amount) {
			throw new WinException("账户可用余额不足");
		}

		/*
		 * 2017-10-9 修改投资利息算法
		 */
		Date nowDate = DateUtils.getDateOfString(DateUtils.getDate(createDate, "yyyy-MM-dd"));
		double day = DateUtils.getDistanceOfTwoDate(nowDate, project.getLoanDate());
		// 计算每日利息
		Double dayInterest = InterestUtils.getDayInterest((amount + vouAmount), project.getAnnualRate());
		// 投资时间到满标时间产生的利息
		Double dayToLoanDateInerest = InterestUtils.format(day * dayInterest);
		// 投资总利息
		interest = NumberUtils.scaleDouble(interest + dayToLoanDateInerest);

		// 开始插入投资详情
		logger.info(this.getClass().getName() + "——————开始插入用户投资信息");
		WloanTermInvest invest = new WloanTermInvest();
		invest.setId(investID); // ID
		invest.setWloanTermProject(project); // 项目信息
		invest.setUserInfo(user); // 用户信息
		invest.setAmount(amount + vouAmount); // 投资金额
		invest.setInterest(interest); // 利息
		invest.setBeginDate(createDate); // 投资时间
		invest.setCreateDate(createDate);
		invest.setIp(ip); // 投资Ip地址
		invest.setState(WLOAN_TERM_INVEST_STATE_1); // 投标状态（默认成功）
		invest.setBidState(WLOAN_TERM_INVEST_STATE_1); // 投标状态
		if (vouAmount != 0d) {
			invest.setVoucherAmount(vouAmount); // 抵用券金额
			invest.setRemarks("使用" + vouAmount + "元抵用券");
		}

		if (addRate != 0d) {
			invest.setVoucherAmount(addRate); // 加息券金额
			invest.setRemarks("使用" + addRate + "%加息券");
		}

		int insertInvest = wloanTermInvestDao.insert(invest);
		if (insertInvest == 1) {
			logger.info(this.getClass().getName() + "——————插入用户投资信息成功");
		}

		/**
		 * 更改账户信息
		 * 1、账户总额（有抵用券加上抵用券金额）
		 * 2、可用金额（可用金额 - 投资金额）4900
		 * 3、定期代收本金（ + 投资金额）5000
		 * 4、定期代收收益（ + 投资利息）
		 * 5、定期投资总金额（ + 投资金额）5000
		 * 6、定期累计收益（不动，还款的时候在添加）
		 */
		logger.info(this.getClass().getName() + "——————更改账户信息开始");
		UserAccountInfo userAccount = userAccountInfoDao.get(account.getId());
		userAccount.setTotalAmount(userAccount.getTotalAmount() + vouAmount + interest); // 账户总额
		userAccount.setAvailableAmount(userAccount.getAvailableAmount() - amount); // 可用余额
		userAccount.setRegularDuePrincipal(userAccount.getRegularDuePrincipal() + amount + vouAmount); // 定期代收本金
		userAccount.setRegularDueInterest(userAccount.getRegularDueInterest() + interest); // 定期代收收益
		userAccount.setRegularTotalAmount(userAccount.getRegularTotalAmount() + amount + vouAmount); // 定期投资总额
		int updateAccount = userAccountInfoDao.update(userAccount);
		if (updateAccount == 1) {
			logger.info(this.getClass().getName() + "——————更改账户信息成功");
		}

		// 生成客户投资还款计划.
		String wloanTermUserPlanFlag = wloanTermUserPlanService.initWloanTermUserPlan(invest);
		if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
			logger.info(this.getClass().getName() + "——————生成客户投资还款计划成功");
		} else {
			throw new Exception("系统异常");
		}

		// 更改项目信息
		logger.info(this.getClass().getName() + "——————更改项目信息开始");
		WloanTermProject project1 = wloanTermProjectDao.get(project.getId());
		project1.setCurrentAmount((project1.getCurrentAmount() == null ? 0 : project1.getCurrentAmount()) + amount + vouAmount);
		project1.setCurrentRealAmount(project1.getCurrentAmount());
		if (project1.getCurrentAmount().equals(project1.getAmount())) { // 判断项目是否满标
			project1.setState(WloanTermProjectService.FULL);
			project1.setFullDate(new Date());
			wloanTermProjectPlanService.deleteByProjectId(project1.getId());
			wloanTermProjectPlanService.initWloanTermProjectPlan(project1);
		}
		int updateProject = wloanTermProjectDao.update(project1);
		if (updateProject == 1) {
			logger.info(this.getClass().getName() + "——————更改项目信息成功");
		}

		/**
		 * 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
		 */
		if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectType())) { // 供应链项目.
			String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, invest);
			invest.setContractPdfPath(pdfPath.split("data")[1]);
			wloanTermInvestDao.update(invest);
		} else { // 安心投项目.
			// 四方合同存储路径.
			String contractPdfPath = createContractPdfPath(user, project, invest);
			invest.setContractPdfPath(contractPdfPath.split("data")[1]);
			wloanTermInvestDao.update(invest);
		}

		// 保存客户流水记录
		logger.info(this.getClass().getName() + "——————保存客户投资流水记录开始");
		UserTransDetail userTransDetail = new UserTransDetail();
		userTransDetail.setId(IdGen.uuid()); // 主键ID.
		userTransDetail.setTransId(investID); // 客户投资记录ID.
		userTransDetail.setUserId(user.getId()); // 客户账号ID.
		userTransDetail.setAccountId(userAccount.getId()); // 客户账户ID.
		userTransDetail.setTransDate(new Date()); // 投资交易时间.
		userTransDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
		userTransDetail.setAmount(amount + vouAmount); // 投资交易金额.
		userTransDetail.setAvaliableAmount(userAccount.getAvailableAmount()); // 当前可用余额.
		userTransDetail.setInOutType(UserTransDetailService.out_type); // 投资支出.
		userTransDetail.setRemarks("定期投资"); // 备注信息.
		userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
		int userTransDetailFlag = userTransDetailDao.insert(userTransDetail);
		if (userTransDetailFlag == 1) {
			logger.info(this.getClass().getName() + "——————保存客户投资流水记录成功");
		}

		// 添加客户投资积分信息
		UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(user.getId());

		// 添加账户积分历史明细
		UserBounsHistory userBounsHistory = new UserBounsHistory();
		Double userBouns = (amount + vouAmount) * (project1.getSpan() / 30) / 100;

		// 判断是否是生日或者节假日投资
		if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
			userBouns = userBouns * 1.5;
		}

		/*
		 * 2017年9月29号至2017年10月10号活动---积分三倍
		 * SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * String beginDate = "2017-09-29 00:00:00";
		 * String endDate = "2017-10-10 23:59:59";
		 * Date today = new Date();
		 * if(today.before( sdf1.parse(endDate)) &&
		 * today.after(sdf1.parse(beginDate)))
		 * {
		 * userBouns = userBouns * 3;
		 * }
		 */

		userBounsHistory.setId(IdGen.uuid());
		userBounsHistory.setUserId(user.getId());
		userBounsHistory.setAmount(userBouns);
		userBounsHistory.setCreateDate(new Date());
		userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST);
		userBounsHistory.setTransId(investID);
		int insertUserBounsHistoryResult = userBounsHistoryService.insert(userBounsHistory);
		if (insertUserBounsHistoryResult > 0) {
			userBounsPoint.setScore(userBounsPoint.getScore() + userBouns.intValue());
			userBounsPoint.setUpdateDate(new Date());
			userBounsPointService.update(userBounsPoint);
		}

		/**
		 * 1> 在新增投资记录之前，判断客户是否为首次投资.
		 * 2> 判断当前客户是否存在三级关系，完成投资，推荐人可获得100积分.
		 * 3> 同时获得该被邀请用户每笔投资所获积分的5%(userBouns * 5 / 100).
		 * 4> 邀请好友完成出借可获得好友出借金额1%（年化）现金奖励
		 */
		// 端午节活动
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String beginDate = "2017-05-22 00:00:00";
		// String endDate = "2017-06-22 23:59:59";

		// 查询客户三级关系.
		LevelDistribution distribution = levelDistributionDao.selectByUserId(user.getId());
		// 客户每次投资，推荐人所获积分
		long integral = Math.round(userBouns * 5 / 100);
		if (findWloanTermInvestExists.size() == 0) { // 首次投资.
			if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {

				// 判断推荐人是否属于渠道用户或者房地产，若属于，则只返利现金（目前写死，只有固定几个房地产客户
				// 8718501042934562536 591427078281367552）
				if (isPartner(distribution.getParentId())) {
					partnerCashReward(distribution, project, amount, vouAmount, investID, user, now2, findWloanTermInvestExists);
				} else if (isEstate(distribution.getParentId())) {
					estateCashReward(distribution, project, amount, vouAmount, investID, user, now2);
				} else {
					// 1.为推荐人赠送100积分，新增积分历史记录.
					UserBounsHistory userBounsHistory_one = new UserBounsHistory();
					userBounsHistory_one.setId(IdGen.uuid());
					userBounsHistory_one.setUserId(distribution.getParentId()); // 推荐人ID.
					userBounsHistory_one.setAmount(100D);
					userBounsHistory_one.setCreateDate(new Date());
					userBounsHistory_one.setTransId(user.getId()); // 当前客户ID.
					userBounsHistory_one.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
					int flag = userBounsHistoryService.insert(userBounsHistory_one);
					// 新增积分历史记录成功后，变更推荐人积分信息.
					if (flag == 1) {
						UserBounsPoint entity = userBounsPointService.getUserBounsPoint(distribution.getParentId());
						entity.setScore(entity.getScore() + 100);
						userBounsPointService.update(entity);
					}
					// 2.同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
					UserBounsHistory userBounsHistory_three = new UserBounsHistory();
					userBounsHistory_three.setId(IdGen.uuid());
					userBounsHistory_three.setUserId(distribution.getParentId()); // 推荐人ID.
					userBounsHistory_three.setAmount(Double.valueOf(integral));
					userBounsHistory_three.setCreateDate(new Date());
					userBounsHistory_three.setTransId(user.getId()); // 当前客户ID.
					userBounsHistory_three.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
					int mark = userBounsHistoryService.insert(userBounsHistory_three);
					// 新增积分历史记录成功后，变更推荐人积分信息.
					if (mark == 1) {
						UserBounsPoint model = userBounsPointService.getUserBounsPoint(distribution.getParentId());
						String integralStr = String.valueOf(integral);
						model.setScore(model.getScore() + Integer.parseInt(integralStr));
						userBounsPointService.update(model);
					}
					/*
					 * 3.为推荐人赠送好友出借金额1%的现金奖励(现金奖励=被邀请好友投资金额*1%*项目期限/365)
					 */
					/*
					 * if(distribution.getCreateDate().before(sdf.parse(endDate))
					 * &&
					 * distribution.getCreateDate().after(sdf.parse(beginDate)
					 * )&& distribution.getParentId() !="3111392815609158838"){
					 * cashReward(distribution, project, amount, vouAmount,
					 * investID, user, now2);
					 * 
					 * }
					 */
				}
			}
		} else { // 再次投资(二次投资及以后的所有投资).
			if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {
				// 判断推荐人是否属于房地产，若属于，则只返利现金（目前写死，只有固定几个房地产客户 8718501042934562536
				// 591427078281367552）
				if (isPartner(distribution.getParentId())) {

					Integer a = partnerCashReward(distribution, project, amount, vouAmount, investID, user, now2, findWloanTermInvestExists);
					if (a == 1) {
						logger.info("佣金返利完成");
					}
				} else if (isEstate(distribution.getParentId())) {

					Integer a = estateCashReward(distribution, project, amount, vouAmount, investID, user, now2);
					if (a == 1) {
						logger.info("佣金返利完成");
					}
				} else {
					// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
					UserBounsHistory userBounsHistory_two = new UserBounsHistory();
					userBounsHistory_two.setId(IdGen.uuid());
					userBounsHistory_two.setUserId(distribution.getParentId()); // 推荐人ID.
					userBounsHistory_two.setAmount(Double.valueOf(integral));
					userBounsHistory_two.setCreateDate(new Date());
					userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
					userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
					int mark = userBounsHistoryService.insert(userBounsHistory_two);
					// 新增积分历史记录成功后，变更推荐人积分信息.
					if (mark == 1) {
						UserBounsPoint model = userBounsPointService.getUserBounsPoint(distribution.getParentId());
						String integralStr = String.valueOf(integral);
						model.setScore(model.getScore() + Integer.parseInt(integralStr));
						userBounsPointService.update(model);
					}
					/*
					 * 3.为推荐人赠送好友出借金额1%的现金奖励(现金奖励=被邀请好友投资金额*1%*项目期限/365)
					 * 
					 * if(distribution.getCreateDate().before(sdf.parse(endDate))
					 * &&
					 * distribution.getCreateDate().after(sdf.parse(beginDate)
					 * )&& distribution.getParentId() !="3111392815609158838"){
					 * Integer a = cashReward(distribution, project, amount,
					 * vouAmount, investID, user, now2);
					 * if(a==1){
					 * logger.info("佣金返利完成");
					 * }
					 * }
					 */
				}
			}
		}

		// 三级分享关系，推荐人获得佣金
		// wbrokerageService.insertWBrokerageMap(amount + vouAmount,
		// user.getId(), project1.getId(), investID);

		// 发送站内信
		StationLetter letter = new StationLetter();
		letter.setUserId(user.getId());
		letter.setLetterType(StationLettersService.LETTER_TYPE_WLOAN);
		letter.setTitle("您已成功向" + project1.getName() + "项目出借资金:" + invest.getAmount() + ",借款周期:" + project.getSpan() + "天。");
		letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
		letter.setState(StationLettersService.LETTER_STATE_UNREAD);
		letter.setSendTime(new Date());
		stationLettersService.save(letter);

		// 开始发送微信、短信提醒
		weixinSendTempMsgService.sendInvestInfoMsg(invest, project1);

		map.put("invest", invest.getInterest());
		map.put("amount", invest.getAmount());
		map.put("userAccount", userAccount);
		map.put("project", project1.getName());

		return map;
	}

	/**
	 * 判断是否属于渠道用户
	 * 
	 * @param id
	 * @return
	 */
	public Boolean isPartner(String id) {

		ZtmgPartnerPlatform entity = ztmgPartnerPlatformDao.get(id);
		if (entity != null && entity.getId() != null) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否属于房地产用户
	 * 
	 * @param id
	 * @return
	 */
	public Boolean isEstate(String id) {

		UserInfo recommendUserInfo = userInfoDao.get(id);
		if (recommendUserInfo == null) {
			recommendUserInfo = userInfoDao.getCgb(id);
		}
		if (recommendUserInfo != null && recommendUserInfo.getId() != null) {
			if (id.equals("8873042006211332719")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 房地产推荐用户返利模式：为推荐人赠送好友出借金额年化1.8%的现金奖励(现金奖励=被邀请好友投资金额*1.8%*项目期限/365)
	 * 
	 * @param distribution
	 * @param project
	 * @param amount
	 * @param vouAmount
	 * @param investID
	 * @param user
	 * @param now2
	 * @return
	 */
	public Integer estateCashReward(LevelDistribution distribution, WloanTermProject project, Double amount, Double vouAmount, String investID, UserInfo user, String now2) {

		// N1.账户增加
		Integer span = project.getSpan(); // 项目期限
		Double inverstAmount = amount + vouAmount; // 投资金额
		Double integr = inverstAmount * 1.8 / 100;
		Double rewardAmount = NumberUtils.scaleDouble(integr * span / 365);
		UserAccountInfo parentUserAccount = userAccountInfoDao.getUserAccountInfo(distribution.getParentId());
		if (parentUserAccount != null && parentUserAccount.getId() != null) {
			// 更新推荐人账户
			parentUserAccount.setTotalAmount(parentUserAccount.getTotalAmount() + rewardAmount);
			parentUserAccount.setAvailableAmount(parentUserAccount.getAvailableAmount() + rewardAmount);
			int updateparentUserAccount = userAccountInfoDao.update(parentUserAccount);

			if (updateparentUserAccount == 1) {
				logger.info("[推荐人]" + distribution.getParentId() + "——————更改账户信息成功");
				// N2.记录交易流水
				logger.info(this.getClass().getName() + "——————保存推荐人现金奖励流水记录开始");
				CgbUserTransDetail parentUserTransDetail = new CgbUserTransDetail();
				parentUserTransDetail.setId(IdGen.uuid()); // 主键ID.
				parentUserTransDetail.setTransId(investID); // 被推荐人投资记录ID.
				parentUserTransDetail.setUserId(distribution.getParentId()); // 推荐人账号ID.
				parentUserTransDetail.setAccountId(parentUserAccount.getId()); // 推荐人账户ID.
				parentUserTransDetail.setTransDate(new Date()); // 交易时间.
				parentUserTransDetail.setTrustType(UserTransDetailService.trust_type9); // 佣金.
				parentUserTransDetail.setAmount(rewardAmount); // 现金金额.
				parentUserTransDetail.setAvaliableAmount(parentUserAccount.getAvailableAmount()); // 当前可用余额.
				parentUserTransDetail.setInOutType(UserTransDetailService.in_type); // 佣金收入.
				parentUserTransDetail.setRemarks("现金奖励"); // 备注信息.
				parentUserTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
				int parentUserTransDetailFlag = cgbUserTransDetailDao.insert(parentUserTransDetail);
				if (parentUserTransDetailFlag == 1) {
					logger.info(this.getClass().getName() + "——————保存推荐人现金奖励流水记录成功");
				}
			}
		}
		// 更新推荐人邀请好友佣金信息
		// double brokerage = brokerageDao.brokerageTotalAmount(user.getId());
		// brokerage +=rewardAmount;
		String idBrokerage = IdGen.uuid();
		int insertBrokerage = brokerageDao.insertBrokerage(idBrokerage, distribution.getParentId(), user.getId(), rewardAmount, now2.toString(), now2.toString());
		if (insertBrokerage == 1) {
			logger.info("保存推荐人好友佣金成功");
		}
		return 1;

	}

	/**
	 * 渠道推荐用户投资返利模式：总投资金额1%+首次投资（30元）
	 * 
	 * @param distribution
	 * @param project
	 * @param amount
	 * @param vouAmount
	 * @param investID
	 * @param user
	 * @param now2
	 * @param findWloanTermInvestExists
	 * @return
	 */
	public Integer partnerCashReward(LevelDistribution distribution, WloanTermProject project, Double amount, Double vouAmount, String investID, UserInfo user, String now2, List<WloanTermInvest> findWloanTermInvestExists) {

		// N1.账户增加
		ZtmgPartnerPlatform entity = ztmgPartnerPlatformDao.get(distribution.getParentId());
		Double inverstAmount = amount + vouAmount; // 投资金额
		Integer result = 0;
		if (entity != null && entity.getId() != null) {
			// N2.记录交易流水
			Double rate = entity.getRate();// 返利利率
			Double money = entity.getMoney();// 有效用户首次奖励金额
			if (rate != null && rate > 0) {
				Double integr = inverstAmount * rate;
				if (findWloanTermInvestExists.size() == 0) {// 首次投资 奖励30元
					integr += money;
				}
				Double rewardAmount = NumberUtils.scaleDouble(integr);
				logger.info(this.getClass().getName() + "——————保存推荐人现金奖励流水记录开始");
				CgbUserTransDetail parentUserTransDetail = new CgbUserTransDetail();
				parentUserTransDetail.setId(IdGen.uuid()); // 主键ID.
				parentUserTransDetail.setTransId(investID); // 被推荐人投资记录ID.
				parentUserTransDetail.setUserId(distribution.getParentId()); // 推荐人账号ID.
				// parentUserTransDetail.setAccountId(parentUserAccount.getId());
				// // 推荐人账户ID.
				parentUserTransDetail.setTransDate(new Date()); // 交易时间.
				parentUserTransDetail.setTrustType(UserTransDetailService.trust_type9); // 佣金.
				parentUserTransDetail.setAmount(rewardAmount); // 现金金额.
				parentUserTransDetail.setAvaliableAmount(rewardAmount); // 当前可用余额.
				parentUserTransDetail.setInOutType(UserTransDetailService.in_type); // 佣金收入.
				parentUserTransDetail.setRemarks("现金奖励"); // 备注信息.
				parentUserTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
				int parentUserTransDetailFlag = cgbUserTransDetailDao.insert(parentUserTransDetail);
				if (parentUserTransDetailFlag == 1) {
					logger.info(this.getClass().getName() + "——————保存推荐人现金奖励流水记录成功");
				}
				String idBrokerage = IdGen.uuid();
				int insertBrokerage = brokerageDao.insertBrokerage(idBrokerage, distribution.getParentId(), user.getId(), rewardAmount, now2.toString(), now2.toString());
				if (insertBrokerage == 1) {
					logger.info("保存推荐人好友佣金成功");
				}
				result = 1;
			} else {
				result = 1;
			}
		}

		return result;
	}

	/**
	 * 散户为推荐人赠送好友出借金额1%的现金奖励(现金奖励=被邀请好友投资金额*1%*项目期限/365)
	 * 
	 * @param distribution
	 * @param project
	 * @param amount
	 * @param vouAmount
	 * @param investID
	 * @param user
	 * @param now2
	 */
	public Integer cashReward(LevelDistribution distribution, WloanTermProject project, Double amount, Double vouAmount, String investID, UserInfo user, String now2) {

		/*
		 * 3.为推荐人赠送好友出借金额1%的现金奖励(现金奖励=被邀请好友投资金额*1%*项目期限/365)
		 */
		// N1.账户增加
		Integer span = project.getSpan(); // 项目期限
		Double inverstAmount = amount + vouAmount; // 投资金额
		Double integr = inverstAmount * 1 / 100;
		Double rewardAmount = NumberUtils.scaleDouble(integr * span / 365);
		UserAccountInfo parentUserAccount = userAccountInfoDao.getUserAccountInfo(distribution.getParentId());
		if (parentUserAccount != null) {
			// 更新推荐人账户
			parentUserAccount.setTotalAmount(parentUserAccount.getTotalAmount() + rewardAmount);
			parentUserAccount.setAvailableAmount(parentUserAccount.getAvailableAmount() + rewardAmount);
			int updateparentUserAccount = userAccountInfoDao.update(parentUserAccount);

			if (updateparentUserAccount == 1) {
				logger.info("[推荐人]" + distribution.getParentId() + "——————更改账户信息成功");
				// N2.记录交易流水
				logger.info(this.getClass().getName() + "——————保存推荐人现金奖励流水记录开始");
				UserTransDetail parentUserTransDetail = new UserTransDetail();
				parentUserTransDetail.setId(IdGen.uuid()); // 主键ID.
				parentUserTransDetail.setTransId(investID); // 被推荐人投资记录ID.
				parentUserTransDetail.setUserId(distribution.getParentId()); // 推荐人账号ID.
				parentUserTransDetail.setAccountId(parentUserAccount.getId()); // 推荐人账户ID.
				parentUserTransDetail.setTransDate(new Date()); // 交易时间.
				parentUserTransDetail.setTrustType(UserTransDetailService.trust_type9); // 佣金.
				parentUserTransDetail.setAmount(rewardAmount); // 现金金额.
				parentUserTransDetail.setAvaliableAmount(parentUserAccount.getAvailableAmount()); // 当前可用余额.
				parentUserTransDetail.setInOutType(UserTransDetailService.in_type); // 佣金收入.
				parentUserTransDetail.setRemarks("现金奖励"); // 备注信息.
				parentUserTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
				int parentUserTransDetailFlag = userTransDetailDao.insert(parentUserTransDetail);
				if (parentUserTransDetailFlag == 1) {
					logger.info(this.getClass().getName() + "——————保存推荐人现金奖励流水记录成功");
				}
			}
		}
		// 更新推荐人邀请好友佣金信息
		String idBrokerage = IdGen.uuid();
		int insertBrokerage = brokerageDao.insertBrokerage(idBrokerage, distribution.getParentId(), user.getId(), rewardAmount, now2.toString(), now2.toString());
		if (insertBrokerage == 1) {
			logger.info("保存推荐人好友佣金成功");
		}
		return 1;

	}

	/**
	 * 
	 * @param userInfo
	 * @param wloanTermProject
	 * @return
	 */
	public String createContractPdfPath(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest) {

		// 四方合同存储路径.
		String contractPdfPath = "";

		/**
		 * 融资主体.
		 */
		String subjectId = project.getSubjectId();// 融资主体ID.
		WloanSubject wloanSubject = wloanSubjectService.get(subjectId);

		/**
		 * 担保机构.
		 */
		String guaranteeId = project.getGuaranteeId();
		WGuaranteeCompany wGuaranteeCompany = wGuaranteeCompanyService.get(guaranteeId);

		// 模版名称.
		String templateName = "pdf_template.pdf";
		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		map.put("contract_no", DateUtils.getDateStr()); // 合同编号.
		if (wloanSubject != null) { // 融资主体.
			map.put("name", Util.hideString(wloanSubject.getLoanUser() == null ? "**" : wloanSubject.getLoanUser(), 1, 1)); // 乙方（借款人）.
			map.put("card_id", Util.hideString(wloanSubject.getLoanIdCard() == null ? "**********" : wloanSubject.getLoanIdCard(), 6, 8)); // 身份证号码.
			map.put("bottom_name", Util.hideString(wloanSubject.getLoanUser() == null ? "**" : wloanSubject.getLoanUser(), 1, 1)); // 乙方（借款人）.
		}

		if (wGuaranteeCompany != null) { // 担保机构.
			map.put("third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
			map.put("legal_person", wGuaranteeCompany.getCorporation()); // 法人代表.
			map.put("residence", wGuaranteeCompany.getAddress()); // 住所.
			map.put("telphone", wGuaranteeCompany.getPhone()); // 电话.
			map.put("bottom_third_name", wGuaranteeCompany.getName()); // 丙方（担保人）.
		}

		if (project != null) { // 定期融资项目.
			map.put("project_name", project.getName()); // 借款项目名称.
			map.put("project_no", project.getSn()); // 借款项目编号.
			map.put("rmb", project.getAmount().toString()); // 借款总额.
			map.put("rmd_da", PdfUtils.change(project.getAmount())); // 借款总额大写.
			map.put("uses", project.getPurpose()); // 借款用途.
			map.put("lend_date", DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd")); // 借款日期.
			map.put("term_date", project.getSpan().toString()); // 借款期限.
			map.put("back_date", DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(project.getLoanDate()), project.getSpan() / 30)); // 还本日期.
			map.put("year_interest", project.getAnnualRate().toString()); // 年利率.
			map.put("interest_sum", invest.getInterest().toString()); // 利息总额.

		}
		map.put("service_no", ""); // 借款服务合同编号.
		map.put("guarantee_no", ""); // 连带责任保证担保涵编号.
		map.put("sign_date", DateUtils.getDate(new Date(), "yyyy年MM月dd日")); // 签订合同日期.

		/**
		 * 客户投资还款计划.
		 */
		WloanTermUserPlan entity = new WloanTermUserPlan();
		entity.setWloanTermProject(project);
		entity.setWloanTermInvest(invest);
		List<WloanTermUserPlan> WloanTermUserPlanList = wloanTermUserPlanService.findList(entity);
		// 还款计划title.
		String title = "出借人本金利息表";
		// 还款计划rowTitle.
		String[] rowTitle = new String[] { "还款日期", "类型", "本金/利息" };
		// 还款计划rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		for (WloanTermUserPlan wloanTermUserPlan : WloanTermUserPlanList) {
			strings = new String[rowTitle.length];
			strings[0] = DateUtils.getDate(wloanTermUserPlan.getRepaymentDate(), "yyyy年MM月dd日");
			if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(wloanTermUserPlan.getPrincipal())) {
				strings[1] = "还本付息";
			} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(wloanTermUserPlan.getPrincipal())) {
				strings[1] = "付息";
			}
			strings[2] = wloanTermUserPlan.getInterest().toString();
			dataList.add(strings);
		}
		try {
			contractPdfPath = PdfUtils.createPdfByTemplate(templateName, map, title, rowTitle, dataList, null, null);
			logger.info("fn:createContractPdfPath,{生成四方合同成功}");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:createContractPdfPath,{异常：" + e.getMessage() + "}");
		}

		return contractPdfPath;
	}

	/**
	 * 用户当日累计投资金额
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public Double countAmount(String userId, String beginDate, String endDate) {

		Double amount = wloanTermInvestDao.countAmount(userId, beginDate, endDate);
		return amount;
	}

	/**
	 * 用户投资排行
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public int rankList(String userId, String beginDate, String endDate) {

		List<WloanTermInvest> list = wloanTermInvestDao.ranList(userId, beginDate, endDate);
		int i = 0;
		int rank = 0;
		if (list != null && list.size() > 0) {
			for (WloanTermInvest wloanTermInvest : list) {
				i++;
				if (wloanTermInvest.getUserInfo().getId().equals(userId)) {
					rank = i;
				}
			}
		}
		return rank;
	}

	public List<WloanTermInvest> findWloanTermInvestExists(String userId) {

		// TODO Auto-generated method stub
		return wloanTermInvestDao.findWloanTermInvestExists(userId);
	}

	/**
	 * 更新出借订单
	 * 
	 * @throws Exception
	 *             200005
	 * @throws WinException
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void checkInvestDetail() throws WinException, Exception {

		// TODO Auto-generated method stub
		List<WloanTermInvest> list = wloanTermInvestDao.findCheckInvest();
		if (list != null && list.size() > 0) {
			for (WloanTermInvest wloanTermInvest : list) {
				Map<String, String> maps = checkDetail(wloanTermInvest.getId());
				/*
				 * 当订单不存在时 .更新出借数据状态
				 */
				if (maps.get("respSubCode").equals("200005")) {
					wloanTermInvest.setState(WLOAN_TERM_INVEST_STATE_2);
					int j = updateWloanTermInvest(wloanTermInvest);
					if (j > 0) {
						logger.info("订单号" + wloanTermInvest.getId() + "状态更新为成功");
					}
				}
			}
		}
	}

	/**
	 * 订单查询
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> checkDetail(String orderId) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("service", "p2p.trade.order.search");
		params.put("method", "RSA");
		params.put("merchantId", MERCHANT_ID);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("单笔交易查询[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
		encryptRet.put("merchantId", MERCHANT_ID);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(HOST, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);

		return maps;
	}

	/**
	 * 对账结果确认API
	 * 
	 * @param orderId
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> notice(String bizType) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("billDate", DateUtils.getDate());
		params.put("bizType", bizType);
		params.put("billCheckResult", "S");

		params.put("service", "p2p.merchant.check.result.notice");
		params.put("method", "RSA");
		params.put("merchantId", MERCHANT_ID);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("对账结果确认API[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
		encryptRet.put("merchantId", MERCHANT_ID);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(HOST, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);

		return maps;
	}

	/**
	 * @description:出借人信息统计查询
	 * @param page
	 * @return
	 */
	public Page<WloanTermInvest> findInvestPage(Page<WloanTermInvest> page,WloanTermInvest wloanTermInvest) {
		wloanTermInvest.setPage(page);
		page.setList(wloanTermInvestDao.findInvestPage(wloanTermInvest));
		return page;
	}
	

	/**
	 * @description:查询本月出借记录
	 * @return
	 */
	public List<WloanTermInvest> findInvest(String startDate, String endDate) {
		return wloanTermInvestDao.findInvest(startDate,endDate);
	}

}
