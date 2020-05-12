package com.power.platform.regular.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
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

import cn.tsign.ching.eSign.SignHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.ARateCouponDicDao;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.entity.ARateCouponDic;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.BrokerageService;
import com.power.platform.activity.service.RedPacketService;
import com.power.platform.activity.service.ZtmgPartnerPlatformService;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.IsHolidayOrBirthday;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.electronic.ElectronicSign;
import com.power.platform.credit.entity.electronic.ElectronicSignTranstail;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.utils.AiQinPdfContract;
import com.power.platform.utils.CreateSupplyChainPdfContract;
import com.power.platform.utils.LoanAgreementPdfUtil;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.power.platform.zdw.dao.ZdwProOrderInfoDao;
import com.power.platform.zdw.entity.ZdwProOrderInfo;
import com.power.platform.zdw.type.ProOrderStatusEnum;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

@Service("userInvestWebService")
public class UserInvestWebService extends CrudService<WloanTermInvest> {

	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private ZdwProOrderInfoDao zdwProOrderInfoDao;

	private static final Logger logger = Logger.getLogger(UserInvestWebService.class);

	/**
	 * 融资类型，1：应收账款.
	 */
	private static final String FINANCING_TYPE_1 = "1";
	/**
	 * 融资类型，2：订单融资.
	 */
	private static final String FINANCING_TYPE_2 = "2";

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
	private WloanTermInvestService wloanTermInvestService;
	@Resource
	private LevelDistributionDao levelDistributionDao;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Resource
	private RedPacketService redPacketService;
	@Resource
	private ZtmgPartnerPlatformService ztmgPartnerPlatformService;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private ElectronicSignService electronicSignService;
	@Resource
	private ElectronicSignDao electronicSignDao;
	@Resource
	private CreditUserApplyService creditUserApplyService;
	@Resource
	private ElectronicSignTranstailDao electronicSignTranstailDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Resource
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Resource
	private CgbUserAccountService cgbUserAccountService;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;

	// 商户号
	private static final String merchantId = Global.getConfig("merchantId");
	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	private static final String PAY_USER = Global.getConfig("payUserId");

	/**
	 * 融资投资记录状态，0：投标受理中.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_0 = "0";
	/**
	 * 融资投资记录状态，1：投标成功.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_1 = "1";
	/**
	 * 融资投资记录状态，2：投标失败.
	 */
	public static final String WLOAN_TERM_INVEST_STATE_2 = "2";

	@Override
	protected CrudDao<WloanTermInvest> getEntityDao() {

		// TODO Auto-generated method stub
		return wloanTermInvestDao;
	}

	/**
	 * 增加平台投资记录
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> insertUserInvestInfo(String token, String projectId, Double amount, List<String> voucherList, UserInfo user, CgbUserAccount account, String ip, String orderId) throws WinException, Exception {

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
		String projectProductTypeString = project.getProjectProductType();
		synchronized (project) {
			Date createDate = new Date();
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now2 = sdf2.format(createDate).toString();
			Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
			Double interest = NumberUtils.scaleDouble(amount * project.getAnnualRate() / (365 * 100));

			interest = interest * (project.getSpan());

			// 客户投资金额
			amount = NumberUtils.scaleDouble(amount);
			interest = NumberUtils.scaleDouble(interest);

			Double vouAmount = 0d;
			Double addRate = 0d;
			Double addInterest = 0d;
			String investID = orderId;
			Double vouAmountTotal = 0d;
			// 遍历抵用券list
			if (voucherList != null && voucherList.size() > 0) {
				for (int i = 0; i < voucherList.size(); i++) {
					String vouid = voucherList.get(i);
					// 获取抵用券信息
					if (!StringUtils.isBlank(vouid)) {
						// 用户使用抵用券，获取抵用券信息
						AUserAwardsHistory aUserAwardsHistory = userVouchersDao.get(vouid);
						if (aUserAwardsHistory.getType().equals(AUserAwardsHistoryService.COUPONS_TYPE_1)) { // 抵用券
							String canUseCoupon = project.getIsCanUseCoupon();
							if (WloanTermProjectService.ISCANUSE_COUPON_NO.equals(canUseCoupon)) {
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
							// 抵用券过期日期
							Date voucherEndDate = aUserAwardsHistory.getOverdueDate();
							String overDate = "";
							if (voucherEndDate != null) {
								overDate = DateUtils.formatDateTime(voucherEndDate);
							} else {
								Integer overDays = voucher.getOverdueDays();
								overDate = DateUtils.getSpecifiedMonthAfterFormat(voucherDate, overDays);
							}
							// 比较过期日期与当前日期的大小(返回值为 false )
							boolean flag = DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), overDate);
							if (!flag) { // 抵用券过期
								throw new WinException("抵用券已过期");
							}
							// 抵用券金额
							vouAmount = voucher.getAmount();
							vouAmountTotal = vouAmountTotal + vouAmount;
							// 投资实际需要金额
							// amount = amount - vouAmountTotal;

						} else { // 加息券
							String canUsePlusCoupon = project.getIsCanUsePlusCoupon();
							if (WloanTermProjectService.ISCANUSE_PLUSCOUPON_NO.equals(canUsePlusCoupon)) {
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
							Date voucherEndDate = aUserAwardsHistory.getOverdueDate();
							String overDate = "";
							if (voucherEndDate != null) {
								overDate = DateUtils.formatDateTime(voucherEndDate);
							} else {
								Integer overDays = aRateCouponDic.getOverdueDays();
								overDate = DateUtils.getSpecifiedMonthAfterFormat(voucherDate, overDays);
							}
							boolean flag = DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), overDate);
							if (!flag) { // 抵用券过期
								throw new WinException("抵用券已过期");
							}
							// 加息券加息值
							addInterest = ((aRateCouponDic.getRate() + project.getAnnualRate()) / project.getAnnualRate()) * interest;
							// 投资实际利息值
							interest = NumberUtils.scaleDouble(addInterest);
						}

						// 查询账户信息（可用余额）
						account = cgbUserAccountDao.get(account.getId());

						// 更改抵用券状态、加标的
						logger.info(this.getClass().getName() + "——————更改抵用券状态开始");
						aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
						aUserAwardsHistory.setBidId(investID);
						aUserAwardsHistory.setUpdateDate(new Date());
						int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
						if (updateVoucher == 1) {
							logger.info(this.getClass().getName() + "——————更改抵用券状态成功");
						}
					}
				}
			}

			/*
			 * 2017-10-9 修改投资利息算法
			 */
			Date nowDate = DateUtils.getDateOfString(DateUtils.getDate(createDate, "yyyy-MM-dd"));
			double day = DateUtils.getDistanceOfTwoDate(nowDate, project.getLoanDate());
			// 计算每日利息
			Double dayInterest = InterestUtils.getDayInterest((amount), project.getAnnualRate());
			// 投资时间到满标时间产生的利息
			Double dayToLoanDateInerest = InterestUtils.format(day * dayInterest);
			// 投资总利息

			interest = NumberUtils.scaleDouble(interest);// +
															// dayToLoanDateInerest

			// 开始插入投资详情
			logger.info(this.getClass().getName() + "——————开始插入用户投资信息");
			WloanTermInvest invest = new WloanTermInvest();
			invest.setId(investID); // ID
			invest.setWloanTermProject(project); // 项目信息
			invest.setUserInfo(user); // 用户信息
			invest.setAmount(amount); // 投资金额
			invest.setInterest(interest); // 利息
			invest.setBeginDate(createDate); // 投资时间
			invest.setCreateDate(createDate);
			invest.setIp(ip); // 投资Ip地址
			invest.setState(WLOAN_TERM_INVEST_STATE_0); // 投标状态（受理中）
			invest.setBidState(WLOAN_TERM_INVEST_STATE_0); // 投标状态
			if (vouAmountTotal != 0d) {
				invest.setVoucherAmount(vouAmountTotal); // 抵用券金额
				invest.setRemarks("使用" + vouAmountTotal + "元抵用券");
			}

			if (addRate != 0d) {
				invest.setVoucherAmount(addRate); // 加息券金额
				invest.setRemarks("使用" + addRate + "%加息券");
			}

			int insertInvest = wloanTermInvestDao.insert(invest);
			if (insertInvest == 1) {
				logger.info(this.getClass().getName() + "——————插入用户投资信息成功");
			}

			logger.info(this.getClass().getName() + "  冻结用户投资金额开始");
			CgbUserAccount userAccount = cgbUserAccountDao.get(account.getId());
			userAccount.setAvailableAmount(userAccount.getAvailableAmount() - invest.getAmount()); // 可用余额
			userAccount.setFreezeAmount(userAccount.getFreezeAmount() + invest.getAmount());// 冻结金额
			int updateAccount = cgbUserAccountDao.update(userAccount);
			if (updateAccount == 1) {
				logger.info(this.getClass().getName() + "冻结用户投资金额结束");
			}

			// 生成客户投资还款计划-旧.
			if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0)) {
				String wloanTermUserPlanFlag = wloanTermUserPlanService.initWloanTermUserPlan(invest);
				if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
					logger.info(this.getClass().getName() + "——————生成客户投资还款计划成功");
				} else {
					throw new Exception("系统异常");
				}
			}
			// 生成客户投资还款计划-新.
			else if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1)) {
				String wloanTermUserPlanFlag = wloanTermUserPlanService.initCgbWloanTermUserPlan(invest);
				if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
					logger.info(this.getClass().getName() + "——————生成客户投资还款计划成功");
				} else {
					throw new Exception("系统异常");
				}
			}

			// 更改项目信息
			logger.info(this.getClass().getName() + "——————更改项目信息开始");
			double currentRealAmount = project.getCurrentAmount() + amount;
			project.setCurrentAmount(currentRealAmount);
			project.setCurrentRealAmount(currentRealAmount);
			if (project.getCurrentRealAmount().equals(project.getAmount())) { // 判断项目是否满标
				project.setState(WloanTermProjectService.FULL);
				project.setFullDate(new Date());
			}
			int newProjectFlag = wloanTermProjectDao.update(project);
			if (newProjectFlag == 1) {
				logger.info(this.getClass().getName() + "——————更改项目信息成功");
			}

			final String pdfPathc;
			final UserInfo userc = user;
			final WloanTermProject projectc = project;
			/**
			 * 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
			 */
			if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链项目.
				String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, invest);
				pdfPathc = pdfPath;
				// 启动线程生成电子签章
				new Thread() {

					public void run() {

						// 生成电子签章
						createElectronicSign(pdfPathc, userc, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
					}
				}.start();

				invest.setContractPdfPath(pdfPath.split("data")[1]);
				wloanTermInvestDao.update(invest);
			} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
				// 四方合同存储路径.
				String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, invest);
				pdfPathc = contractPdfPath;
				// 启动线程生成电子签章
				new Thread() {

					public void run() {

						// 生成电子签章
						createElectronicSign(pdfPathc, userc, projectc.getWloanSubject().getLoanApplyId(), projectc.getProjectProductType());
					}
				}.start();

				invest.setContractPdfPath(contractPdfPath.split("data")[1]);
				wloanTermInvestDao.update(invest);
			}

			Double userBouns = amount * (project.getSpan() / 30) / 100;
			int investNum = findWloanTermInvestExists.size();// 投资次数
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String beginDate = "2017-12-26 00:00:00";
			String endDate = "2018-01-02 23:59:59";
			Date now = new Date();
			// N1.查询渠道代码
			if (user.getRecommendUserId() != null && !user.getRecommendUserId().equals("")) {
				ZtmgPartnerPlatform platForm = ztmgPartnerPlatformService.get(user.getRecommendUserId());
				if (platForm != null) {
					if (platForm.getPlatformCode() != null && platForm.getPlatformCode().equals("")) {
						if (platForm.getPlatformCode().equals("008") && investNum == 0 && now.before(sdf1.parse(endDate)) && now.after(sdf1.parse(beginDate)) && project.getId().equals("7847579d93184e2d9b61c7333f8bf4bd") && project.getId().equals("1d2da011f0c14f54a55c4394151a811d") && project.getId().equals("50736392cd184ba1bf7a49d38f227210") && project.getId().equals("830e944706904e91bf3008ae5144f36a") && project.getId().equals("4a46514fc5474eeb863a4b5b603ab396")) {
							addBouns(user.getId(), userBouns * 2, investID);
						}
					} else {
						if ("2".equals(projectProductTypeString)) {// 供应链项目
							if (IsHolidayOrBirthday.isActivity()) {
								userBouns = userBouns * 2;
								addBouns(user.getId(), userBouns, investID);
							} else {
								if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
									userBouns = userBouns * 1.5;
									addBouns(user.getId(), userBouns, investID);
								} else {
									addBouns(user.getId(), userBouns, investID);
								}
							}
						} else {// 安心投项目
							if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
								userBouns = userBouns * 1.5;
								addBouns(user.getId(), userBouns, investID);
							} else {
								addBouns(user.getId(), userBouns, investID);
							}
						}

					}
				}
			} else {
				if ("2".equals(projectProductTypeString)) {// 供应链项目
					if (IsHolidayOrBirthday.isActivity()) {
						userBouns = userBouns * 2;
						addBouns(user.getId(), userBouns, investID);
					} else {
						if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
							userBouns = userBouns * 1.5;
							addBouns(user.getId(), userBouns, investID);
						} else {
							addBouns(user.getId(), userBouns, investID);
						}
					}
				} else {// 安心投项目
					if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
						userBouns = userBouns * 1.5;
						addBouns(user.getId(), userBouns, investID);
					} else {
						addBouns(user.getId(), userBouns, investID);
					}
				}
			}

			/**
			 * 1> 在新增投资记录之前，判断客户是否为首次投资.
			 * 2> 判断当前客户是否存在三级关系，完成投资，推荐人可获得100积分.
			 * 3> 同时获得该被邀请用户每笔投资所获积分的5%(userBouns * 5 / 100).
			 * 4> 邀请好友完成出借可获得好友出借金额1%（年化）现金奖励
			 */
			// 客户每次投资，推荐人所获积分
			long integral = Math.round(userBouns * 5 / 100);
			if (findWloanTermInvestExists.size() == 0) { // 首次投资.
				String recommondUserPhone = user.getRecommendUserPhone();
				if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
					UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
					String recommondUserId = recommondUserInfo.getId();
					// 1.为推荐人赠送100积分，新增积分历史记录.
					UserBounsHistory userBounsHistory_one = new UserBounsHistory();
					userBounsHistory_one.setId(IdGen.uuid());
					userBounsHistory_one.setUserId(recommondUserId); // 推荐人ID.
					userBounsHistory_one.setAmount(100D);
					userBounsHistory_one.setCreateDate(new Date());
					userBounsHistory_one.setTransId(user.getId()); // 当前客户ID.
					userBounsHistory_one.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
					int flag = userBounsHistoryService.insert(userBounsHistory_one);
					// 新增积分历史记录成功后，变更推荐人积分信息.
					if (flag == 1) {
						UserBounsPoint entity = userBounsPointService.getUserBounsPoint(recommondUserId);
						entity.setScore(entity.getScore() + 100);
						userBounsPointService.update(entity);
					}
					// 2.同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
					UserBounsHistory userBounsHistory_three = new UserBounsHistory();
					userBounsHistory_three.setId(IdGen.uuid());
					userBounsHistory_three.setUserId(recommondUserId); // 推荐人ID.
					userBounsHistory_three.setAmount(Double.valueOf(integral));
					userBounsHistory_three.setCreateDate(new Date());
					userBounsHistory_three.setTransId(user.getId()); // 当前客户ID.
					userBounsHistory_three.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
					int mark = userBounsHistoryService.insert(userBounsHistory_three);
					// 新增积分历史记录成功后，变更推荐人积分信息.
					if (mark == 1) {
						UserBounsPoint model = userBounsPointService.getUserBounsPoint(recommondUserId);
						String integralStr = String.valueOf(integral);
						model.setScore(model.getScore() + Integer.parseInt(integralStr));
						userBounsPointService.update(model);
					}
				}
			} else { // 再次投资(二次投资及以后的所有投资).
				String recommondUserPhone = user.getRecommendUserPhone();
				if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
					UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
					String recommondUserId = recommondUserInfo.getId();
					// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
					UserBounsHistory userBounsHistory_two = new UserBounsHistory();
					userBounsHistory_two.setId(IdGen.uuid());
					userBounsHistory_two.setUserId(recommondUserId); // 推荐人ID.
					userBounsHistory_two.setAmount(Double.valueOf(integral));
					userBounsHistory_two.setCreateDate(new Date());
					userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
					userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
					int mark = userBounsHistoryService.insert(userBounsHistory_two);
					// 新增积分历史记录成功后，变更推荐人积分信息.
					if (mark == 1) {
						UserBounsPoint model = userBounsPointService.getUserBounsPoint(recommondUserId);
						String integralStr = String.valueOf(integral);
						model.setScore(model.getScore() + Integer.parseInt(integralStr));
						userBounsPointService.update(model);
					}
				}
			}

			map.put("vouAmountTotal", vouAmountTotal);
		}
		return map;
	}

	// 投资端生成电子签章
	public void createElectronicSign(String srcPdfFile, UserInfo userInfo, String creditUserApplyId, String projectType) {

		int lastF = srcPdfFile.lastIndexOf("\\");
		if (lastF == -1) {
			lastF = srcPdfFile.lastIndexOf("//");
		}
		// 最终签署后的PDF文件路径
		String signedFolder = srcPdfFile.substring(0, lastF + 1);
		// 最终签署后PDF文件名称
		String signedFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
		System.out.println("----<场景演示：使用标准的模板印章签署，签署人之间用文件二进制流传递>----");
		// 初始化项目，做全局使用，只初始化一次即可
		SignHelper.initProject();
		// 创建投资客户签章账户
		String userSignId;// 客户签章id
		ElectronicSign electronicSignUser = new ElectronicSign();
		electronicSignUser.setUserId(userInfo.getId());
		List<ElectronicSign> electronicSignsList = electronicSignService.findList(electronicSignUser);
		if (electronicSignsList != null && electronicSignsList.size() > 0) {
			userSignId = electronicSignsList.get(0).getSignId();
		} else {

			userSignId = SignHelper.addPersonAccountZTMG(userInfo);
			electronicSignUser.setId(IdGen.uuid());
			electronicSignUser.setSignId(userSignId);
			electronicSignUser.setCreateDate(new Date());
			electronicSignDao.insert(electronicSignUser);
		}

		// 创建投资客户印章（甲方）
		AddSealResult userSealData = SignHelper.addPersonTemplateSeal(userSignId);

		if ("1".equals(projectType)) {// 安心投
			String loanUserId = creditUserApplyId;// 借款人id
			CreditUserInfo loanUserInfo = creditUserInfoDao.get(loanUserId);
			String loanUserSignId;// 借款人签章id
			ElectronicSign electronicSignLoanUser = new ElectronicSign();
			electronicSignLoanUser.setUserId(loanUserId);
			List<ElectronicSign> electronicSignsListLoan = electronicSignService.findList(electronicSignLoanUser);
			if (electronicSignsListLoan != null && electronicSignsListLoan.size() > 0) {
				loanUserSignId = electronicSignsListLoan.get(0).getSignId();
			} else {

				loanUserSignId = SignHelper.addPersonAccountZTMGLoan(loanUserInfo);
				electronicSignLoanUser.setId(IdGen.uuid());
				electronicSignLoanUser.setSignId(loanUserSignId);
				electronicSignLoanUser.setCreateDate(new Date());
				electronicSignDao.insert(electronicSignLoanUser);
			}
			// 创建借款客户印章（乙方）
			AddSealResult loanUserSealData = SignHelper.addPersonTemplateSeal(loanUserSignId);

			// 签署
			// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
			FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvestAXT(srcPdfFile);
			// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
			FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvestAXT(platformSignResult.getStream(), userSignId, userSealData.getSealData());
			String serviceIdUser = userPersonSignResult.getSignServiceId();
			// 借款客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（乙方）
			FileDigestSignResult loanUserPersonSignResult = SignHelper.loanUserPersonSignByStreamInvestAXT(userPersonSignResult.getStream(), loanUserSignId, loanUserSealData.getSealData());
			String serviceIdLoanUser = loanUserPersonSignResult.getSignServiceId();

			// 所有签署完成,将最终签署后的文件流保存到本地
			if (0 == loanUserPersonSignResult.getErrCode()) {
				SignHelper.saveSignedByStream(loanUserPersonSignResult.getStream(), signedFolder, signedFileName);
			}
			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
			electronicSignTranstail.setId(IdGen.uuid());
			electronicSignTranstail.setInvestUserId(userInfo.getId());
			electronicSignTranstail.setSupplyId(loanUserId);// 借款人id
			electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
			electronicSignTranstail.setSignServiceIdSupply(serviceIdLoanUser);// 借款人签署后服务id
			electronicSignTranstail.setCreateDate(new Date());
			electronicSignTranstailDao.insert(electronicSignTranstail);
			// SignHelper.userPersonSignByFileInvest(srcPdfFile, signedPdf,
			// accountId, sealData)

		} else {// 供应链
				// 查询借款申请
			CreditUserApply creditUserApply = creditUserApplyService.get(creditUserApplyId);
			if (creditUserApply != null) {
				// 查询供应商签章账户
				String supplyOrganizeAccountId;
				ElectronicSign electronicSignSupply = new ElectronicSign();
				electronicSignSupply.setUserId(creditUserApply.getCreditSupplyId());
				electronicSignSupply.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				List<ElectronicSign> electronicSignsListSupply = electronicSignService.findList(electronicSignSupply);
				if (electronicSignsListSupply.size() > 0) {
					supplyOrganizeAccountId = electronicSignsListSupply.get(0).getSignId();
				} else {
					supplyOrganizeAccountId = null;
					logger.info("获取供应商签章账户失败");
				}

				WloanSubject wloanSubjectSupply = new WloanSubject();
				wloanSubjectSupply.setLoanApplyId(creditUserApply.getCreditSupplyId());
				List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubjectSupply);
				wloanSubjectSupply = wloanSubjectsList1.get(0);

				// 查询核心企业签章账户
				String creditOrganizeAccountId;
				ElectronicSign electronicSignCredit = new ElectronicSign();
				electronicSignCredit.setUserId(creditUserApply.getReplaceUserId());
				List<ElectronicSign> electronicSignsListCredit = electronicSignService.findList(electronicSignCredit);
				if (electronicSignsListCredit.size() > 0) {
					creditOrganizeAccountId = electronicSignsListCredit.get(0).getSignId();
				} else {
					creditOrganizeAccountId = null;
					logger.info("获取企业签章账户失败");
				}

				WloanSubject wloanSubjectCredit = new WloanSubject();
				wloanSubjectCredit.setLoanApplyId(creditUserApply.getReplaceUserId());
				List<WloanSubject> wloanSubjectsListCredit = wloanSubjectService.findList(wloanSubjectCredit);
				wloanSubjectCredit = wloanSubjectsListCredit.get(0);

				// 创建供应商印章（乙方）
				AddSealResult userOrganizeSealDataSupply = null;
				if (supplyOrganizeAccountId != null) {
					userOrganizeSealDataSupply = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId, wloanSubjectSupply);
				} else {
					logger.info("获取供应商签章账户失败，无法生成电子签章");
				}

				// 创建核心企业印章（丁方）
				AddSealResult userOrganizeSealDataCredit = null;
				if (creditOrganizeAccountId != null) {
					userOrganizeSealDataCredit = SignHelper.addOrganizeTemplateSealZTMG(creditOrganizeAccountId, wloanSubjectCredit);
					logger.info("核心企业签章:" + userOrganizeSealDataCredit);
				} else {
					logger.info("获取核心企业签章账户失败，无法生成电子签章");
				}

				// 签署
				// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
				FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvest(srcPdfFile);
				// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
				FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvest(platformSignResult.getStream(), userSignId, userSealData.getSealData());
				String serviceIdUser = userPersonSignResult.getSignServiceId();
				// 供应商客户签署,坐标定位,以文件流的方式传递pdf文档
				FileDigestSignResult userOrganizeSignResultSupply = SignHelper.userOrganizeSignByStreamSupplyInvest(userPersonSignResult.getStream(), supplyOrganizeAccountId, userOrganizeSealDataSupply.getSealData());
				String serviceIdSupply = userOrganizeSignResultSupply.getSignServiceId();
				// 核心企业客户签署,坐标定位,以文件流的方式传递pdf文档
				if (userOrganizeSealDataCredit != null) {
					FileDigestSignResult userOrganizeSignResultCredit = SignHelper.userOrganizeSignByStreamCreditInvest(userOrganizeSignResultSupply.getStream(), creditOrganizeAccountId, userOrganizeSealDataCredit.getSealData());
					String serviceIdCredit = userOrganizeSignResultCredit.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == userOrganizeSignResultCredit.getErrCode()) {
						SignHelper.saveSignedByStream(userOrganizeSignResultCredit.getStream(), signedFolder, signedFileName);
					}
					ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
					electronicSignTranstail.setId(IdGen.uuid());
					electronicSignTranstail.setInvestUserId(userInfo.getId());
					electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
					electronicSignTranstail.setSignServiceIdSupply(serviceIdSupply);
					electronicSignTranstail.setSignServiceIdCore(serviceIdCredit);
					electronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(electronicSignTranstail);
				} else {
					logger.info("核心企业签章userOrganizeSealDataCredit为空！");
				}

				// SignHelper.userPersonSignByFileInvest(srcPdfFile, signedPdf,
				// accountId, sealData)

			} else {
				logger.info("查询借款申请失败！");
			}
		}

	}

	/**
	 * 
	 */
	/**
	 * 
	 * methods: createXiYunElectronicSign <br>
	 * description: 熙耘签章. <br>
	 * author: Roy <br>
	 * date: 2019年4月21日 上午10:25:38
	 * 
	 * @param srcPdfFile
	 * @param userInfo
	 * @param creditUserApplyId
	 * @param projectType
	 */
	public void createXiYunElectronicSign(String srcPdfFile, UserInfo userInfo, String creditUserApplyId, String projectType) {

		int lastF = srcPdfFile.lastIndexOf("\\");
		if (lastF == -1) {
			lastF = srcPdfFile.lastIndexOf("//");
		}
		// 最终签署后的PDF文件路径
		String signedFolder = srcPdfFile.substring(0, lastF + 1);
		// 最终签署后PDF文件名称
		String signedFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
		// System.out.println("----<场景演示：使用标准的模板印章签署，签署人之间用文件二进制流传递>----");
		// 初始化项目，做全局使用，只初始化一次即可
		SignHelper.initProject();
		// 创建投资客户签章账户
		String userSignId;// 客户签章id
		ElectronicSign electronicSignUser = new ElectronicSign();
		electronicSignUser.setUserId(userInfo.getId());
		List<ElectronicSign> electronicSignsList = electronicSignService.findList(electronicSignUser);
		if (electronicSignsList != null && electronicSignsList.size() > 0) {
			userSignId = electronicSignsList.get(0).getSignId();
		} else {
			userSignId = SignHelper.addPersonAccountZTMG(userInfo);
			electronicSignUser.setId(IdGen.uuid());
			electronicSignUser.setSignId(userSignId);
			electronicSignUser.setCreateDate(new Date());
			electronicSignDao.insert(electronicSignUser);
		}

		// 创建投资客户印章（甲方）
		AddSealResult userSealData = SignHelper.addPersonTemplateSeal(userSignId);

		if ("1".equals(projectType)) {// 安心投
			String loanUserId = creditUserApplyId;// 借款人id
			CreditUserInfo loanUserInfo = creditUserInfoDao.get(loanUserId);
			String loanUserSignId;// 借款人签章id
			ElectronicSign electronicSignLoanUser = new ElectronicSign();
			electronicSignLoanUser.setUserId(loanUserId);
			List<ElectronicSign> electronicSignsListLoan = electronicSignService.findList(electronicSignLoanUser);
			if (electronicSignsListLoan != null && electronicSignsListLoan.size() > 0) {
				loanUserSignId = electronicSignsListLoan.get(0).getSignId();
			} else {

				loanUserSignId = SignHelper.addPersonAccountZTMGLoan(loanUserInfo);
				electronicSignLoanUser.setId(IdGen.uuid());
				electronicSignLoanUser.setSignId(loanUserSignId);
				electronicSignLoanUser.setCreateDate(new Date());
				electronicSignDao.insert(electronicSignLoanUser);
			}
			// 创建借款客户印章（乙方）
			AddSealResult loanUserSealData = SignHelper.addPersonTemplateSeal(loanUserSignId);

			// 签署
			// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
			FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvestAXT(srcPdfFile);
			// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
			FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvestAXT(platformSignResult.getStream(), userSignId, userSealData.getSealData());
			String serviceIdUser = userPersonSignResult.getSignServiceId();
			// 借款客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（乙方）
			FileDigestSignResult loanUserPersonSignResult = SignHelper.loanUserPersonSignByStreamInvestAXT(userPersonSignResult.getStream(), loanUserSignId, loanUserSealData.getSealData());
			String serviceIdLoanUser = loanUserPersonSignResult.getSignServiceId();

			// 所有签署完成,将最终签署后的文件流保存到本地
			if (0 == loanUserPersonSignResult.getErrCode()) {
				SignHelper.saveSignedByStream(loanUserPersonSignResult.getStream(), signedFolder, signedFileName);
			}
			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
			electronicSignTranstail.setId(IdGen.uuid());
			electronicSignTranstail.setInvestUserId(userInfo.getId());
			electronicSignTranstail.setSupplyId(loanUserId);// 借款人id
			electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
			electronicSignTranstail.setSignServiceIdSupply(serviceIdLoanUser);// 借款人签署后服务id
			electronicSignTranstail.setCreateDate(new Date());
			electronicSignTranstailDao.insert(electronicSignTranstail);
			// SignHelper.userPersonSignByFileInvest(srcPdfFile, signedPdf,
			// accountId, sealData)

		} else {// 供应链
				// 查询借款申请
			CreditUserApply creditUserApply = creditUserApplyService.get(creditUserApplyId);
			if (creditUserApply != null) {
				// 查询供应商签章账户
				String supplyOrganizeAccountId;
				ElectronicSign electronicSignSupply = new ElectronicSign();
				electronicSignSupply.setUserId(creditUserApply.getCreditSupplyId());
				electronicSignSupply.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				List<ElectronicSign> electronicSignsListSupply = electronicSignService.findList(electronicSignSupply);
				if (electronicSignsListSupply.size() > 0) {
					supplyOrganizeAccountId = electronicSignsListSupply.get(0).getSignId();
				} else {
					supplyOrganizeAccountId = null;
					logger.info("获取供应商签章账户失败");
				}

				WloanSubject wloanSubjectSupply = new WloanSubject();
				wloanSubjectSupply.setLoanApplyId(creditUserApply.getCreditSupplyId());
				List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubjectSupply);
				wloanSubjectSupply = wloanSubjectsList1.get(0);

				// 查询核心企业签章账户
				String creditOrganizeAccountId;
				ElectronicSign electronicSignCredit = new ElectronicSign();
				electronicSignCredit.setUserId(creditUserApply.getReplaceUserId());
				List<ElectronicSign> electronicSignsListCredit = electronicSignService.findList(electronicSignCredit);
				if (electronicSignsListCredit.size() > 0) {
					creditOrganizeAccountId = electronicSignsListCredit.get(0).getSignId();
				} else {
					creditOrganizeAccountId = null;
					logger.info("获取企业签章账户失败");
				}

				WloanSubject wloanSubjectCredit = new WloanSubject();
				wloanSubjectCredit.setLoanApplyId(creditUserApply.getReplaceUserId());
				List<WloanSubject> wloanSubjectsListCredit = wloanSubjectService.findList(wloanSubjectCredit);
				wloanSubjectCredit = wloanSubjectsListCredit.get(0);

				// 创建供应商印章（乙方）
				AddSealResult userOrganizeSealDataSupply = null;
				if (supplyOrganizeAccountId != null) {
					userOrganizeSealDataSupply = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId, wloanSubjectSupply);
				} else {
					logger.info("获取供应商签章账户失败，无法生成电子签章");
				}

				// 创建核心企业印章（丁方）
				AddSealResult userOrganizeSealDataCredit = null;
				if (creditOrganizeAccountId != null) {
					userOrganizeSealDataCredit = SignHelper.addOrganizeTemplateSealZTMG(creditOrganizeAccountId, wloanSubjectCredit);
					logger.info("核心企业签章:" + userOrganizeSealDataCredit);
				} else {
					logger.info("获取核心企业签章账户失败，无法生成电子签章");
				}

				// 戊方签章ID.
				String ePartyOrganizeAccountId;
				ElectronicSign ePartyElectronicSign = new ElectronicSign();
				ePartyElectronicSign.setUserId(CreditUserInfo.AIQIN_ID);
				List<ElectronicSign> ePartyElectronicSigns = electronicSignService.findList(ePartyElectronicSign);
				if (ePartyElectronicSigns.size() > 0) {
					ePartyOrganizeAccountId = ePartyElectronicSigns.get(0).getSignId();
				} else {
					ePartyOrganizeAccountId = null;
				}
				// 戊方
				WloanSubject ePartyWloanSubject = new WloanSubject();
				ePartyWloanSubject.setLoanApplyId(CreditUserInfo.AIQIN_ID);
				List<WloanSubject> ePartyWloanSubjects = wloanSubjectService.findList(ePartyWloanSubject);
				if (ePartyWloanSubjects.size() > 0) {
					ePartyWloanSubject = ePartyWloanSubjects.get(0);
				}
				AddSealResult ePartyUserOrganizeSealData = null;
				if (ePartyOrganizeAccountId != null) {
					ePartyUserOrganizeSealData = SignHelper.addOrganizeTemplateSealZTMG(ePartyOrganizeAccountId, null);
				} else {
					ePartyOrganizeAccountId = SignHelper.addOrganizeAccountZtmg(ePartyWloanSubject);
					ePartyElectronicSign.setId(IdGen.uuid());
					ePartyElectronicSign.setSignId(ePartyOrganizeAccountId);
					ePartyElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
					ePartyElectronicSign.setCreateDate(new Date());
					ePartyElectronicSign.setUpdateDate(new Date());
					int insertFlag = electronicSignDao.insert(ePartyElectronicSign);
					if (insertFlag == 1) {
						logger.info(this.getClass() + "-创建签章身份铭牌成功");
					} else {
						logger.info(this.getClass() + "-创建签章身份铭牌失败");
					}
				}
				// 签署
				// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
				FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvest(srcPdfFile);
				// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
				FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvest(platformSignResult.getStream(), userSignId, userSealData.getSealData());
				String serviceIdUser = userPersonSignResult.getSignServiceId();
				// 供应商客户签署,坐标定位,以文件流的方式传递pdf文档
				FileDigestSignResult userOrganizeSignResultSupply = SignHelper.userOrganizeSignByStreamSupplyInvest(userPersonSignResult.getStream(), supplyOrganizeAccountId, userOrganizeSealDataSupply.getSealData());
				String serviceIdSupply = userOrganizeSignResultSupply.getSignServiceId();
				// 核心企业客户签署,坐标定位,以文件流的方式传递pdf文档
				FileDigestSignResult userOrganizeSignResultCredit = SignHelper.userOrganizeSignByStreamCreditInvest(userOrganizeSignResultSupply.getStream(), creditOrganizeAccountId, userOrganizeSealDataCredit.getSealData());
				String serviceIdCredit = userOrganizeSignResultCredit.getSignServiceId();
				if (ePartyOrganizeAccountId != null) {
					// 戊方签章
					FileDigestSignResult ePartyUserOrganizeSignResult = SignHelper.ePartyUserOrganizeSignByStream(userOrganizeSignResultCredit.getStream(), ePartyOrganizeAccountId, ePartyUserOrganizeSealData.getSealData());
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == ePartyUserOrganizeSignResult.getErrCode()) {
						SignHelper.saveSignedByStream(ePartyUserOrganizeSignResult.getStream(), signedFolder, signedFileName);
					}
					ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
					electronicSignTranstail.setId(IdGen.uuid());
					electronicSignTranstail.setInvestUserId(userInfo.getId());
					electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
					electronicSignTranstail.setSignServiceIdSupply(serviceIdSupply);
					electronicSignTranstail.setSignServiceIdCore(serviceIdCredit);
					electronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(electronicSignTranstail);
				} else {
					logger.info("签章失败！");
				}
			} else {
				logger.info("查询借款申请失败！");
			}
		}

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
	 * 
	 * methods: addBouns <br>
	 * description: 增加积分 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月4日 上午9:38:13
	 * 
	 * @param userId
	 * @param userBouns
	 * @param transId
	 */
	public void addBouns(String userId, Double userBouns, String transId) {

		// 添加客户投资积分信息
		UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
		// 账户积分
		Integer score = userBounsPoint.getScore();
		// 增加的积额度
		int intUserBouns = userBouns.intValue();
		// 账户积分增加
		score = score + intUserBouns;
		// 添加账户积分历史明细
		UserBounsHistory userBounsHistory = new UserBounsHistory();
		userBounsHistory.setId(IdGen.uuid());
		userBounsHistory.setUserId(userId);
		userBounsHistory.setAmount(Double.valueOf(intUserBouns));
		userBounsHistory.setCreateDate(new Date());
		userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST);
		userBounsHistory.setTransId(transId);
		userBounsHistory.setCurrentAmount(score.toString()); // 当前剩余积分
		int insertUserBounsHistoryResult = userBounsHistoryService.insert(userBounsHistory);
		if (insertUserBounsHistoryResult > 0) {
			userBounsPoint.setScore(score);
			userBounsPoint.setUpdateDate(new Date());
			int i = userBounsPointService.update(userBounsPoint);
			if (i > 0) {
				logger.info("用户积分添加[" + userBouns + "]成功");
			}
		}
	}

	/**
	 * 出借人出借请求----PC端
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserInvestWeb(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount, UserInfo user, String ip) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台出借
		logger.info("平台出借处理开始");
		int resultInvest = insertUserInvest(projectId, amount, voucherList, user, ip, orderId);
		if (resultInvest > 0) {
			logger.info("出借申请成功");
		}
		logger.info("平台出借处理开始");
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList != null && voucherList.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			Double vAmount = 100 * NumberUtils.scaleDouble(vouAmount);
			BigDecimal rpAmount = new BigDecimal(vAmount);
			map.put("rpAmount", rpAmount);
			map.put("rpSubOrderId", UUID.randomUUID().toString().replace("-", ""));
			map.put("rpUserId", PAY_USER);
			rpOrderList.add(map);
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		Double tAmount = NumberUtils.scaleDouble(100 * NumberUtils.scaleDouble(amount));
		BigDecimal totalAmount = new BigDecimal(tAmount);
		if (vouAmount == null) {
			vouAmount = 0d;
		}
		Double aAmount = NumberUtils.scaleDouble(100 * (NumberUtils.scaleDouble(amount) - NumberUtils.scaleDouble(vouAmount)));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList != null && voucherList.size() > 0) {
			params.put("rpOrderList", JSONArray.toJSONString(rpOrderList));
		}
		params.put("service", "web.p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.BACK_INVEST_NEWURL);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB + "&orderId=" + orderId);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("出借[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}

	/**
	 * 出借人出借请求----H5
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserInvestH5(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount, UserInfo user, String ip) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台出借
		logger.info("平台出借处理开始");
		int resultInvest = insertUserInvest(projectId, amount, voucherList, user, ip, orderId);
		if (resultInvest > 0) {
			logger.info("出借申请成功");
		}
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList != null && voucherList.size() > 0) {
			for (int i = 0; i < voucherList.size(); i++) {
				String vouid = voucherList.get(i);
				AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
				if (aUserAwardsHistory.getValue() != null && !aUserAwardsHistory.getValue().equals("")) {
					Double voucherAmount = Double.valueOf(aUserAwardsHistory.getValue());
					Map<String, Object> map = new HashMap<String, Object>();
					Double vAmount = 100 * NumberUtils.scaleDouble(voucherAmount);
					BigDecimal rpAmount = new BigDecimal(vAmount);
					map.put("rpAmount", rpAmount);
					map.put("rpSubOrderId", aUserAwardsHistory.getId());
					map.put("rpUserId", PAY_USER);
					rpOrderList.add(map);
				}
			}
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		Double tAmount = NumberUtils.scaleDouble(100 * NumberUtils.scaleDouble(amount));
		BigDecimal totalAmount = new BigDecimal(tAmount);
		if (vouAmount == null) {
			vouAmount = 0d;
		}
		Double aAmount = NumberUtils.scaleDouble((100 * (NumberUtils.scaleDouble(amount) - NumberUtils.scaleDouble(vouAmount))));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList != null && voucherList.size() > 0) {
			params.put("rpOrderList", JSONArray.toJSONString(rpOrderList));
		}
		params.put("service", "h5.p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.BACK_INVEST_NEWURL);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB + "&orderId=" + orderId);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("投资[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}

	/**
	 * 取消投资
	 * 
	 * @param orderId
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> cancelInvest(String orderId) throws WinException, Exception {

		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("origOrderId", orderId);
		params.put("rpDirect", "01");
		params.put("service", "p2p.trade.invest.cancel");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("取消投资[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		String url = ServerURLConfig.CGB_URL;

		String result = HttpUtil.sendPost(url, encryptRet);
		System.out.println("返回结果报文" + result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		System.out.println("解密结果:" + maps);

		return maps;
	}

	/**
	 * 平台取消投资业务处理
	 * 
	 * @param orderId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int cancelUserInvest(String orderId, String remarks, Double availableAmount) {

		int result = 0;
		WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);// 出借记录
		Double investAmount = wloanTermInvest.getAmount();
		logger.info("取消投资时当前可用余额为" + availableAmount + "=====出借金额为" + investAmount);
		// N1.更改出借状态为失败
		wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_2);
		wloanTermInvest.setRemarks(remarks);
		int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);

		availableAmount = availableAmount - investAmount;

		if (i > 0) {
			logger.info("[用户出借失败]" + orderId + "状态更新成功");
			logger.info("保存用户出借交易流水开始");
			CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
			userTransDetail.setId(IdGen.uuid()); // 主键ID.
			userTransDetail.setTransId(orderId); // 客户投资记录ID.
			userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
			userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
			userTransDetail.setTransDate(new Date()); // 投资交易时间.
			userTransDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
			userTransDetail.setAmount(wloanTermInvest.getAmount()); // 投资交易金额.
			userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额.
			userTransDetail.setInOutType(UserTransDetailService.out_type); // 投资支出.
			userTransDetail.setRemarks("出借"); // 备注信息.
			userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
			int userTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
			if (userTransDetailFlag == 1) {
				logger.info(this.getClass().getName() + "——————保存客户出借流水记录成功");
			}
			logger.info("保存用户出借交易流水结束");
			logger.info("保存出借退款交易流水开始");
			CgbUserTransDetail userBackAmountDetail = new CgbUserTransDetail();
			userBackAmountDetail.setId(IdGen.uuid()); // 主键ID.
			userBackAmountDetail.setTransId(orderId); // 客户投资记录ID.
			userBackAmountDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
			userBackAmountDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
			userBackAmountDetail.setTransDate(new Date(System.currentTimeMillis() + 5000)); // 投资交易时间.
			userBackAmountDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
			userBackAmountDetail.setAmount(wloanTermInvest.getAmount()); // 投资交易金额.
			userBackAmountDetail.setAvaliableAmount(availableAmount + investAmount); // 当前可用余额.
			userBackAmountDetail.setInOutType(UserTransDetailService.in_type); // 退款收入.
			userBackAmountDetail.setRemarks("出借退款"); // 备注信息.
			userBackAmountDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
			int backAmount = cgbUserTransDetailService.insert(userBackAmountDetail);
			if (backAmount == 1) {
				logger.info(this.getClass().getName() + "——————保存出借退款交易流水成功");
				logger.info("保存出借退款交易流水结束");
				logger.info("[抵用券]更改抵用券开始");
				AUserAwardsHistory aUserAwards = new AUserAwardsHistory();
				aUserAwards.setBidId(wloanTermInvest.getId());
				List<AUserAwardsHistory> aUserAwardsHistoryList = aUserAwardsHistoryService.findVouchers(aUserAwards);
				if (aUserAwardsHistoryList != null && aUserAwardsHistoryList.size() > 0) {
					for (AUserAwardsHistory aUserAwardsHistory : aUserAwardsHistoryList) {

						AUserAwardsHistory newUserVoucher = new AUserAwardsHistory();
						newUserVoucher.setId(String.valueOf(IdGen.randomLong()));
						newUserVoucher.setAwardId(aUserAwardsHistory.getAwardId());
						newUserVoucher.setCreateDate(aUserAwardsHistory.getCreateDate());
						newUserVoucher.setUserId(aUserAwardsHistory.getUserId());
						newUserVoucher.setOverdueDate(aUserAwardsHistory.getOverdueDate());
						newUserVoucher.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
						newUserVoucher.setType("1");// 类型为:抵用劵
						newUserVoucher.setValue(aUserAwardsHistory.getValue());
						newUserVoucher.setSpans(aUserAwardsHistory.getSpans());
						newUserVoucher.setRemark(aUserAwardsHistory.getRemark());

						// 删除原来已使用的抵用券
						int delUserOldVoucher = aUserAwardsHistoryDao.deleteBy(aUserAwardsHistory.getId());
						if (delUserOldVoucher > 0) {
							logger.info("[抵用券]原抵用券已删除");
						}
						// 新增一张等面值的抵用券
						int newUserVoucherReturn = aUserAwardsHistoryDao.insert(newUserVoucher);
						if (newUserVoucherReturn > 0) {
							logger.info("[抵用券]新抵用券已添加");
						}
					}
				}
				logger.info("[抵用券]更改抵用券结束");
				result = 1;
			}
		}
		return result;
	}

	/**
	 * 新增出借人出借记录
	 * 
	 * @param projectId
	 * @param amount
	 * @param voucherList
	 * @param user
	 * @param ip
	 * @param orderId
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertUserInvest(String projectId, Double amount, List<String> voucherList, UserInfo user, String ip, String orderId) throws WinException, Exception {

		/**
		 * 定义变量
		 */
		Double vouAmount = 0d;
		Double vouAmountTotal = 0d;
		// N1.抵用券金额
		if (voucherList != null && voucherList.size() > 0) {
			for (int i = 0; i < voucherList.size(); i++) {
				String vouid = voucherList.get(i);
				if (!StringUtils.isBlank(vouid)) {
					AUserAwardsHistory aUserAwardsHistory = userVouchersDao.get(vouid);
					// 抵用券
					if (aUserAwardsHistory.getType().equals(AUserAwardsHistoryService.COUPONS_TYPE_1)) {
						AVouchersDic voucher = aVouchersDicDao.get(aUserAwardsHistory.getAwardId());
						vouAmount = voucher.getAmount();
						// 抵用券使用总额
						vouAmountTotal = vouAmountTotal + vouAmount;
					}
				}
			}
		}
		// N2.根据项目ID获取项目信息
		WloanTermProject project = wloanTermProjectDao.get(projectId);
		// N3.利息计算
		// 1.日利息
		Double dayInterest = NumberUtils.scaleDouble(amount * project.getAnnualRate() / (365 * 100));
		// 2.出借利息
		Double interest = NumberUtils.scaleDouble(dayInterest * (project.getSpan()));
		// N4.插入出借记录表
		// 开始插入投资详情
		logger.info(this.getClass().getName() + "——————开始插入用户投资信息");
		WloanTermInvest invest = new WloanTermInvest();
		invest.setId(orderId); // ID
		invest.setWloanTermProject(project); // 项目信息
		invest.setUserInfo(user); // 用户信息
		invest.setAmount(amount); // 投资金额
		invest.setInterest(interest); // 利息
		invest.setBeginDate(new Date()); // 投资时间
		invest.setCreateDate(new Date());
		invest.setIp(ip); // 投资Ip地址
		invest.setState(WLOAN_TERM_INVEST_STATE_0); // 投标状态（受理中）
		invest.setBidState(WLOAN_TERM_INVEST_STATE_0); // 投标状态
		if (vouAmountTotal != 0d) {
			invest.setVoucherAmount(vouAmountTotal); // 抵用券金额
			invest.setRemarks("使用" + vouAmountTotal + "元抵用券");
		}

		int insertInvest = wloanTermInvestDao.insert(invest);
		if (insertInvest == 1) {
			logger.info(this.getClass().getName() + "——————插入用户出借信息成功");
		}
		logger.info("抵用劵业务处理");
		if (voucherList != null && voucherList.size() > 0) {
			for (int i = 0; i < voucherList.size(); i++) {
				String vouid = voucherList.get(i);
				// 获取抵用券信息
				if (!StringUtils.isBlank(vouid)) {
					AUserAwardsHistory aUserAwardsHistory = userVouchersDao.get(vouid);
					logger.info(this.getClass().getName() + "——————更改抵用券出借ID字段开始");
					aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
					aUserAwardsHistory.setBidId(invest.getId());
					aUserAwardsHistory.setUpdateDate(new Date());
					int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
					if (updateVoucher == 1) {
						logger.info(this.getClass().getName() + "——————更改抵用券出借ID成功");
					}
				}

			}
		}
		logger.info("抵用劵业务处理");
		return insertInvest;
	}

	/**
	 * 出借回调业务处理
	 * 
	 * @param orderId
	 * @return
	 * @throws Exception
	 * @throws WinException
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int createUserInvest(String orderId) throws WinException, Exception {

		int result = 0;// 返回结果标识
		String proKey = "";
		String lockProValue = "";
		String accKey = "";
		String lockAccValue = "";// 账户锁
		try {

			long beginTime = System.currentTimeMillis();

			/**
			 * 根据订单ID查询出借记录
			 */
			WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
			if (wloanTermInvest != null) {
				CgbUserAccount userAccount = cgbUserAccountDao.getUserAccountInfo(wloanTermInvest.getUserId());// 账户信息
				String projectId = wloanTermInvest.getWloanTermProject().getId();// 项目ID
				Double amount = wloanTermInvest.getAmount();// 出借金额
				Double vouAmount = wloanTermInvest.getVoucherAmount() == null ? 0d : wloanTermInvest.getVoucherAmount();// 抵用券使用金额
				Double interest = wloanTermInvest.getInterest();// 出借利息
				Double totalInvestAmount = wloanTermInvestDao.getInvestTotalAmount(projectId) == null ? 0d : wloanTermInvestDao.getInvestTotalAmount(projectId);// 已出借成功总额

				proKey = "PRO" + projectId;
				lockProValue = JedisUtils.lockWithTimeout(proKey, 10000, 2000);// 项目锁

				WloanTermProject project = wloanTermProjectDao.get(wloanTermInvest.getWloanTermProject().getId());// 项目信息

				Double balanceAmount = NumberUtils.scaleDouble(project.getAmount() - project.getCurrentAmount());

				// N1.锁项目
				if (lockProValue != null && !lockProValue.equals("")) {

					logger.info("项目锁开始=====" + System.currentTimeMillis());

					// ******如果已出借成功总额等于项目总额时则进行取消投资业务处理并短信通知用户**************
					Double availableAmount = userAccount.getAvailableAmount();// 账户可用余额
					logger.info("=====账户可用余额为" + availableAmount);
					if (totalInvestAmount > 0) {
						if (totalInvestAmount.equals(project.getAmount())) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目已满标", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						} else if (amount > NumberUtils.scaleDouble(project.getAmount() - project.getCurrentAmount())) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目剩余资金不足", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						} else if (NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) < 100 && NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) > 0) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目尾笔金额需全投", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						}
					}

					String projectProductTypeString = project.getProjectProductType();
					// 更改项目信息
					logger.info(this.getClass().getName() + "——————更改项目信息开始");
					double currentRealAmount = project.getCurrentAmount() + amount;
					project.setCurrentAmount(currentRealAmount);
					project.setCurrentRealAmount(currentRealAmount);
					// 判断项目是否满标
					if (NumberUtils.scaleDouble(project.getCurrentRealAmount()).equals(NumberUtils.scaleDouble(project.getAmount()))) {
						project.setState(WloanTermProjectService.FULL);
						project.setFullDate(new Date());
					}
					int newProjectFlag = wloanTermProjectDao.update(project);
					if (newProjectFlag == 1) {
						logger.info(this.getClass().getName() + "——————更改项目信息成功");
					}
					/**
					 * ****************根据出借记录生成个人还款计划**************************
					 */
					// 生成客户投资还款计划-旧.
					if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0)) {
						String wloanTermUserPlanFlag = wloanTermUserPlanService.initWloanTermUserPlan(wloanTermInvest);
						if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
							logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
						} else {
							throw new Exception("系统异常");
						}
					}
					// 生成客户投资还款计划-新.
					else if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1)) {
						String wloanTermUserPlanFlag = wloanTermUserPlanService.initCgbWloanTermUserPlan(wloanTermInvest);
						if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
							logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
						} else {
							throw new Exception("系统异常");
						}
					}
					/**
					 * *************根据出借记录生成合同****************
					 */
					// 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
					final String pdfPathc;
					final UserInfo user = userInfoDao.getCgb(wloanTermInvest.getUserInfo().getId());
					final WloanTermProject projectc = project;
					if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链项目.
						CreditUserApply creditUserApply = creditUserApplyService.get(projectc.getCreditUserApplyId()); // 借款申请.
						if (creditUserApply != null) {
							String financingType = creditUserApply.getFinancingType(); // 融资类型.
							if (FINANCING_TYPE_1.equals(financingType)) {// 应收账款转让
								String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, wloanTermInvest);
								pdfPathc = pdfPath;
								// 启动线程生成电子签章
								new Thread() {

									public void run() {

										// 生成电子签章
										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									}
								}.start();
								wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(wloanTermInvest);
							} else if (FINANCING_TYPE_2.equals(financingType)) {// 订单融资
								String pdfPath = AiQinPdfContract.createOrderFinancingPdf(user, projectc, wloanTermInvest);
								pdfPathc = pdfPath;
								// 启动线程生成电子签章
								new Thread() {

									public void run() {

										// 生成电子签章
										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									}
								}.start();
								wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(wloanTermInvest);
							} else {// 应收账款让
								String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, wloanTermInvest);
								pdfPathc = pdfPath;
								// 启动线程生成电子签章
								new Thread() {

									public void run() {

										// 生成电子签章
										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									}
								}.start();
								wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(wloanTermInvest);
							}
						}
					} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
						// 四方合同存储路径.
						String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, wloanTermInvest);
						pdfPathc = contractPdfPath;
						// 启动线程生成电子签章
						new Thread() {

							public void run() {

								// 生成电子签章
								createElectronicSign(pdfPathc, user, projectc.getWloanSubject().getLoanApplyId(), projectc.getProjectProductType());
							}
						}.start();

						wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
						wloanTermInvestDao.update(wloanTermInvest);
					}
					/**
					 * ************根据出借记录发放积分****************
					 */
					// 出借次数
					List<WloanTermInvest> findWloanTermInvestExists = wloanTermInvestDao.findWloanTermInvestExists(user.getId());
					Double userBouns = amount * (project.getSpan() / 30) / 100;
					int investNum = findWloanTermInvestExists.size();// 投资次数
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String beginDate = "2017-12-26 00:00:00";
					String endDate = "2018-01-02 23:59:59";
					Date now = new Date();
					// N1.查询渠道代码
					if (user.getRecommendUserId() != null && !user.getRecommendUserId().equals("")) {
						ZtmgPartnerPlatform platForm = ztmgPartnerPlatformService.get(user.getRecommendUserId());
						if (platForm != null) {
							if (platForm.getPlatformCode() != null && platForm.getPlatformCode().equals("")) {
								if (platForm.getPlatformCode().equals("008") && investNum == 0 && now.before(sdf1.parse(endDate)) && now.after(sdf1.parse(beginDate)) && project.getId().equals("7847579d93184e2d9b61c7333f8bf4bd") && project.getId().equals("1d2da011f0c14f54a55c4394151a811d") && project.getId().equals("50736392cd184ba1bf7a49d38f227210") && project.getId().equals("830e944706904e91bf3008ae5144f36a") && project.getId().equals("4a46514fc5474eeb863a4b5b603ab396")) {
									addBouns(user.getId(), userBouns * 2, orderId);
								}
							} else {
								if ("2".equals(projectProductTypeString)) {// 供应链项目
									if (IsHolidayOrBirthday.isActivity()) {
										userBouns = userBouns * 2;
										addBouns(user.getId(), userBouns, orderId);
									} else {
										if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
											userBouns = userBouns * 1.5;
											addBouns(user.getId(), userBouns, orderId);
										} else {
											addBouns(user.getId(), userBouns, orderId);
										}
									}
								} else {// 安心投项目
									if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
										userBouns = userBouns * 1.5;
										addBouns(user.getId(), userBouns, orderId);
									} else {
										addBouns(user.getId(), userBouns, orderId);
									}
								}

							}
						}
					} else {
						if ("2".equals(projectProductTypeString)) {// 供应链项目
							if (IsHolidayOrBirthday.isActivity()) {
								userBouns = userBouns * 2;
								addBouns(user.getId(), userBouns, orderId);
							} else {
								if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
									userBouns = userBouns * 1.5;
									addBouns(user.getId(), userBouns, orderId);
								} else {
									addBouns(user.getId(), userBouns, orderId);
								}
							}
						} else {// 安心投项目
							if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
								userBouns = userBouns * 1.5;
								addBouns(user.getId(), userBouns, orderId);
							} else {
								addBouns(user.getId(), userBouns, orderId);
							}
						}
					}
					/**
					 * **********是否首次投资,三级关系****************
					 */
					/**
					 * 1> 在新增投资记录之前，判断客户是否为首次投资.
					 * 2> 判断当前客户是否存在三级关系，完成投资，推荐人可获得100积分.
					 * 3> 同时获得该被邀请用户每笔投资所获积分的5%(userBouns * 5 / 100).
					 * 4> 邀请好友完成出借可获得好友出借金额1%（年化）现金奖励
					 */
					// 客户每次投资，推荐人所获积分
					long integral = Math.round(userBouns * 5 / 100);
					if (findWloanTermInvestExists.size() == 0) { // 首次投资.
						String recommondUserPhone = user.getRecommendUserPhone();
						if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
							UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
							String recommondUserId = recommondUserInfo.getId();
							// 1.为推荐人赠送100积分，新增积分历史记录.
							UserBounsHistory userBounsHistory_one = new UserBounsHistory();
							userBounsHistory_one.setId(IdGen.uuid());
							userBounsHistory_one.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_one.setAmount(100D);
							userBounsHistory_one.setCreateDate(new Date());
							userBounsHistory_one.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_one.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							int flag = userBounsHistoryService.insert(userBounsHistory_one);
							// 新增积分历史记录成功后，变更推荐人积分信息.
							if (flag == 1) {
								UserBounsPoint entity = userBounsPointService.getUserBounsPoint(recommondUserId);
								entity.setScore(entity.getScore() + 100);
								userBounsPointService.update(entity);
							}
							// 2.同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_three = new UserBounsHistory();
							userBounsHistory_three.setId(IdGen.uuid());
							userBounsHistory_three.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_three.setAmount(Double.valueOf(integral));
							userBounsHistory_three.setCreateDate(new Date());
							userBounsHistory_three.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_three.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							int mark = userBounsHistoryService.insert(userBounsHistory_three);
							// 新增积分历史记录成功后，变更推荐人积分信息.
							if (mark == 1) {
								UserBounsPoint model = userBounsPointService.getUserBounsPoint(recommondUserId);
								String integralStr = String.valueOf(integral);
								model.setScore(model.getScore() + Integer.parseInt(integralStr));
								userBounsPointService.update(model);
							}
						}
					} else { // 再次投资(二次投资及以后的所有投资).
						String recommondUserPhone = user.getRecommendUserPhone();
						if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
							UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
							String recommondUserId = recommondUserInfo.getId();
							// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_two = new UserBounsHistory();
							userBounsHistory_two.setId(IdGen.uuid());
							userBounsHistory_two.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_two.setAmount(Double.valueOf(integral));
							userBounsHistory_two.setCreateDate(new Date());
							userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							int mark = userBounsHistoryService.insert(userBounsHistory_two);
							// 新增积分历史记录成功后，变更推荐人积分信息.
							if (mark == 1) {
								UserBounsPoint model = userBounsPointService.getUserBounsPoint(recommondUserId);
								String integralStr = String.valueOf(integral);
								model.setScore(model.getScore() + Integer.parseInt(integralStr));
								userBounsPointService.update(model);
							}
						}
					}
					// N2.释放项目锁
					if (JedisUtils.releaseLock(proKey, lockProValue)) {
						logger.info("项目锁已释放");
						logger.info("项目锁已解锁=====" + System.currentTimeMillis());
					}

					// N3.账户锁

					accKey = "ACC" + userAccount.getId();
					lockAccValue = JedisUtils.lockWithTimeout(accKey, 4000, 2000);// 账户锁

					if (lockAccValue != null && !lockAccValue.equals("")) {
						logger.info("=======账户锁定开始" + System.currentTimeMillis());

						/**
						 * **************出借账户变更************************
						 */
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
						logger.info("更改账户前,账户总额为" + userAccount.getTotalAmount() + "###可用余额为" + userAccount.getAvailableAmount());
						userAccount.setTotalAmount(userAccount.getTotalAmount() + vouAmount + interest); // 账户总额
						userAccount.setAvailableAmount(userAccount.getAvailableAmount() - amount + vouAmount);// 可用余额
						userAccount.setRegularDuePrincipal(userAccount.getRegularDuePrincipal() + amount); // 定期代收本金
						userAccount.setRegularDueInterest(userAccount.getRegularDueInterest() + interest); // 定期代收收益
						userAccount.setRegularTotalAmount(userAccount.getRegularTotalAmount() + amount); // 定期投资总额
						logger.info("更改账户后,账户总额为" + userAccount.getTotalAmount() + "###可用余额为" + userAccount.getAvailableAmount());
						int updateAccount = cgbUserAccountService.updateUserAccountInfo(userAccount);
						if (updateAccount == 1) {
							logger.info(this.getClass().getName() + "——————更改账户信息成功");
							if (JedisUtils.releaseLock(accKey, lockAccValue)) {
								logger.info("账户锁已释放");
								logger.info("=======账户已解锁=====" + System.currentTimeMillis());
							}
							/**
							 * **********抵用券状态变更*************
							 */
							// 记录抵用券流水
							AUserAwardsHistory aUserAwards = new AUserAwardsHistory();
							aUserAwards.setBidId(wloanTermInvest.getId());
							List<AUserAwardsHistory> aUserAwardsHistoryList = aUserAwardsHistoryService.findVouchers(aUserAwards);

							if (aUserAwardsHistoryList != null && aUserAwardsHistoryList.size() > 0) {

								userAccount.setAvailableAmount(availableAmount - amount);

								logger.info("减去出借金额后的可用余额为" + userAccount.getAvailableAmount());

								int time = 1;

								for (AUserAwardsHistory aUserAwardsHistory : aUserAwardsHistoryList) {

									Double voucherAmount = aUserAwardsHistory.getaVouchersDic().getAmount();// 抵用券金额

									Double newAvaAmount = userAccount.getAvailableAmount() + voucherAmount;

									logger.info("[抵用券流水记录开始时]账户可用余额" + newAvaAmount);

									logger.info("抵用券状态变更为已使用开始");
									aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
									aUserAwardsHistory.setBidId(orderId);
									aUserAwardsHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * ++time));
									int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
									if (updateVoucher == 1) {
										logger.info(this.getClass().getName() + "——————更改抵用券状态成功");
									}
									logger.info("抵用券状态变更为已使用结束");
									// 保存客户使用抵用券流水记录
									logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录开始");
									CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
									userTransDetail.setId(IdGen.uuid()); // 主键ID.
									userTransDetail.setTransId(aUserAwardsHistory.getId()); // 客户抵用券记录ID.
									userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
									userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
									userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++time)); // 抵用券使用时间.
									userTransDetail.setTrustType(UserTransDetailService.trust_type10); // 优惠券.
									userTransDetail.setAmount(aUserAwardsHistory.getaVouchersDic().getAmount()); // 优惠券金额.
									userTransDetail.setAvaliableAmount(newAvaAmount); // 当前可用余额.
									userTransDetail.setInOutType(UserTransDetailService.in_type); // 优惠券收入.
									userTransDetail.setRemarks("抵用券"); // 备注信息.
									userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
									int userVoucherDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
									if (userVoucherDetailFlag == 1) {
										logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录成功");
									}
									userAccount.setAvailableAmount(newAvaAmount);
									logger.info("[抵用券流水记录完成时]账户可用余额" + newAvaAmount);
								}
							}
							/**
							 * ***********出借记录状态变更**************
							 */
							wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_1);
							int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
							if (i > 0) {
								logger.info("[用户出借成功]" + orderId + "状态更新成功");
								logger.info("=====记录客户出借交易流水开始======"); // 保存客户流水记录
								CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
								userTransDetail.setId(IdGen.uuid()); // 主键ID.
								userTransDetail.setTransId(orderId); // 客户投资记录ID.
								userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
								userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
								userTransDetail.setTransDate(new Date()); // 投资交易时间.
								userTransDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
								userTransDetail.setAmount(amount); // 投资交易金额.
								userTransDetail.setAvaliableAmount(availableAmount - amount); // 当前可用余额.
								userTransDetail.setInOutType(UserTransDetailService.out_type); // 投资支出.
								userTransDetail.setRemarks("出借"); // 备注信息.
								userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
								int userTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
								if (userTransDetailFlag == 1) {
									logger.info(this.getClass().getName() + "——————保存客户出借流水记录成功");
								}
							}
						}

						// N4.发送站内信
						StationLetter letter = new StationLetter();
						letter.setUserId(wloanTermInvest.getUserInfo().getId());
						letter.setLetterType(StationLettersService.LETTER_TYPE_WLOAN);
						letter.setTitle("您已成功向" + project.getName() + "项目出借资金:" + amount + ",借款周期:" + project.getSpan() + "天。");
						letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
						letter.setState(StationLettersService.LETTER_STATE_UNREAD);
						letter.setSendTime(new Date());
						stationLettersService.save(letter);
						// N5.发送短信,微信
						weixinSendTempMsgService.sendInvestInfoMsg(wloanTermInvest, project);
						result = 1;
						return result;
					}

				} else {
					result = 0;
					return result;
				}
				long endTime = System.currentTimeMillis();
				logger.info("计时结束 耗时：{" + DateUtils.formatDateTime(endTime - beginTime) + "}");

			}
		} catch (Exception e) {
			// 立即释放项目锁
			if (JedisUtils.releaseLock(proKey, lockProValue)) {
				logger.info("[取消投资]项目锁已释放");
				logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
			}
			// 立即释放账户锁
			if (JedisUtils.releaseLock(accKey, lockAccValue)) {
				logger.info("账户锁已释放");
				logger.info("=======账户已解锁=====" + System.currentTimeMillis());
			}
			return result;
		}
		return result;

	}

	/**
	 * 出借记录结果查询
	 * 
	 * @param orderId
	 * @return
	 */
	public Map<String, Object> seachInvestResult(String orderId) {

		// TODO Auto-generated method stub
		Map<String, Object> resultMap = new HashMap<String, Object>();
		WloanTermInvest invest = wloanTermInvestDao.get(orderId);
		if (invest != null) {
			resultMap.put("projectName", invest.getWloanTermProject().getName());
			resultMap.put("projectSn", invest.getWloanTermProject().getSn());
			resultMap.put("amount", NumberUtils.scaleDouble(invest.getAmount()));
			resultMap.put("state", invest.getState());
			resultMap.put("remark", invest.getRemarks());
		}
		return resultMap;
	}

	/**
	 * 出借人出借请求----PC端新
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserToInvestWeb(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount, UserInfo user, String ip) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台出借
		logger.info("平台出借处理开始");
		int resultInvest = insertUserInvest(projectId, amount, voucherList, user, ip, orderId);
		if (resultInvest > 0) {
			logger.info("出借申请成功");
		}
		logger.info("平台出借处理开始");
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList != null && voucherList.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			Double vAmount = 100 * NumberUtils.scaleDouble(vouAmount);
			BigDecimal rpAmount = new BigDecimal(vAmount);
			map.put("rpAmount", rpAmount);
			map.put("rpSubOrderId", UUID.randomUUID().toString().replace("-", ""));
			map.put("rpUserId", PAY_USER);
			rpOrderList.add(map);
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		Double tAmount = NumberUtils.scaleDouble(100 * NumberUtils.scaleDouble(amount));
		BigDecimal totalAmount = new BigDecimal(tAmount);
		if (vouAmount == null) {
			vouAmount = 0d;
		}
		Double aAmount = NumberUtils.scaleDouble(100 * (NumberUtils.scaleDouble(amount) - NumberUtils.scaleDouble(vouAmount)));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList != null && voucherList.size() > 0) {
			params.put("rpOrderList", JSONArray.toJSONString(rpOrderList));
		}
		params.put("service", "web.p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.NEW_BACK_INVEST_URL);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB + "&orderId=" + orderId);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("出借[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}

	/**
	 * 出借人出借请求----H5新
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserToInvestH5(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount, UserInfo user, String ip) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台出借
		logger.info("平台出借处理开始");
		int resultInvest = insertUserInvest(projectId, amount, voucherList, user, ip, orderId);
		if (resultInvest > 0) {
			logger.info("出借申请成功");
		}
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList != null && voucherList.size() > 0) {
			for (int i = 0; i < voucherList.size(); i++) {
				String vouid = voucherList.get(i);
				AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
				if (aUserAwardsHistory.getValue() != null && !aUserAwardsHistory.getValue().equals("")) {
					Double voucherAmount = Double.valueOf(aUserAwardsHistory.getValue());
					Map<String, Object> map = new HashMap<String, Object>();
					Double vAmount = 100 * NumberUtils.scaleDouble(voucherAmount);
					BigDecimal rpAmount = new BigDecimal(vAmount);
					map.put("rpAmount", rpAmount);
					map.put("rpSubOrderId", aUserAwardsHistory.getId());
					map.put("rpUserId", PAY_USER);
					rpOrderList.add(map);
				}
			}
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		Double tAmount = NumberUtils.scaleDouble(100 * NumberUtils.scaleDouble(amount));
		BigDecimal totalAmount = new BigDecimal(tAmount);
		if (vouAmount == null) {
			vouAmount = 0d;
		}
		Double aAmount = NumberUtils.scaleDouble((100 * (NumberUtils.scaleDouble(amount) - NumberUtils.scaleDouble(vouAmount))));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList != null && voucherList.size() > 0) {
			params.put("rpOrderList", JSONArray.toJSONString(rpOrderList));
		}
		params.put("service", "h5.p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.NEW_BACK_INVEST_URL);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB + "&orderId=" + orderId);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("投资[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}

	/**
	 * 出借回调业务处理
	 * 
	 * @param orderId
	 * @return
	 * @throws Exception
	 * @throws WinException
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int newCreateUserInvest(String orderId) throws WinException, Exception {

		int result = 0;// 返回结果标识
		String proKey = "";
		String lockProValue = "";
		String accKey = "";
		String lockAccValue = "";// 账户锁
		try {

			long beginTime = System.currentTimeMillis();

			/**
			 * 根据订单ID查询出借记录
			 */
			WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
			if (wloanTermInvest != null) {
				CgbUserAccount userAccount = cgbUserAccountDao.getUserAccountInfo(wloanTermInvest.getUserId());// 账户信息
				String projectId = wloanTermInvest.getWloanTermProject().getId();// 项目ID
				Double amount = wloanTermInvest.getAmount();// 出借金额
				Double vouAmount = wloanTermInvest.getVoucherAmount() == null ? 0d : wloanTermInvest.getVoucherAmount();// 抵用券使用金额
				Double interest = wloanTermInvest.getInterest();// 出借利息
				Double totalInvestAmount = wloanTermInvestDao.getInvestTotalAmount(projectId) == null ? 0d : wloanTermInvestDao.getInvestTotalAmount(projectId);// 已出借成功总额

				proKey = "PRO" + projectId;
				lockProValue = JedisUtils.lockWithTimeout(proKey, 10000, 2000);// 项目锁

				final WloanTermProject project = wloanTermProjectDao.get(wloanTermInvest.getWloanTermProject().getId());// 项目信息

				Double balanceAmount = NumberUtils.scaleDouble(project.getAmount() - project.getCurrentAmount());

				// N1.锁项目
				if (lockProValue != null && !lockProValue.equals("")) {

					logger.info("项目锁开始=====" + System.currentTimeMillis());

					// ******如果已出借成功总额等于项目总额时则进行取消投资业务处理并短信通知用户**************
					Double availableAmount = userAccount.getAvailableAmount();// 账户可用余额
					logger.info("=====账户可用余额为" + availableAmount);
					if (totalInvestAmount > 0) {
						if (totalInvestAmount.equals(project.getAmount())) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目已满标", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						} else if (amount > NumberUtils.scaleDouble(project.getAmount() - project.getCurrentAmount())) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目剩余资金不足", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						} else if (NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) < 100 && NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) > 0) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目尾笔金额需全投", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						}
					}

					final UserInfo user = userInfoDao.getCgb(wloanTermInvest.getUserInfo().getId());
					String projectProductTypeString = project.getProjectProductType();
					// 更改项目信息
					logger.info(this.getClass().getName() + "——————更改项目信息开始");
					double currentRealAmount = project.getCurrentAmount() + amount;
					project.setCurrentAmount(currentRealAmount);
					project.setCurrentRealAmount(currentRealAmount);
					// 判断项目是否满标
					if (NumberUtils.scaleDouble(project.getCurrentRealAmount()).equals(NumberUtils.scaleDouble(project.getAmount()))) {
						logger.info("项目满标开始生成用户还款计划,合同");
						project.setState(WloanTermProjectService.FULL);
						project.setFullDate(new Date());
						// 生成新的项目还款计划
						wloanTermProjectPlanService.deleteByProjectId(project.getId());
						wloanTermProjectPlanService.newInitWloanTermProjectPlan(project);
						// 线程进行还款计划,合同生成
						new Thread() {

							public void run() {

								// 生成还款计划,合同生成
								try {
									createPlanAndContract(project);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									logger.info(e.getMessage());
								}
							}
						}.start();
						// 个人还款计划.合同生成
						logger.info("个人投资成功用户,生成还款计划,合同========>>>>>>>>");
						/**
						 * ****************根据出借记录生成个人还款计划***********************
						 * ***
						 */
						logger.info("=================生成用户" + wloanTermInvest.getUserId() + "还款计划,合同开始================");
						// 生成客户投资还款计划-旧.
						if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0)) {
							String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitWloanTermUserPlan(wloanTermInvest);
							if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
								logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
							} else {
								throw new Exception("系统异常");
							}
						}
						// 生成客户投资还款计划-新.
						else if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1)) {
							String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitCgbWloanTermUserPlan(wloanTermInvest);
							if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
								logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
							} else {
								throw new Exception("系统异常");
							}
						}

						/**
						 * *************根据出借记录生成合同****************
						 */
						// 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
						final String pdfPathc;
						final WloanTermProject projectc = project;
						if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链项目.
							CreditUserApply creditUserApply = creditUserApplyService.get(projectc.getCreditUserApplyId()); // 借款申请.
							if (creditUserApply != null) {
								String financingType = creditUserApply.getFinancingType(); // 融资类型.
								if (FINANCING_TYPE_1.equals(financingType)) {// 应收账款转让
									logger.info("个人应收账款转让合同开始生成=========>>>");
									String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, wloanTermInvest);
									logger.info("<<<===============合同生成结束");
									pdfPathc = pdfPath;

									// 生成电子签章
									try {
										System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									} catch (Exception e) {
										logger.info("个人生成电子签章catch");
										logger.info(e.getMessage());
										wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
										wloanTermInvestDao.update(wloanTermInvest);
									}
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								} else if (FINANCING_TYPE_2.equals(financingType)) {// 订单融资
									logger.info("个人订单融资合同开始生成=========>>>");
									String pdfPath = AiQinPdfContract.createOrderFinancingPdf(user, projectc, wloanTermInvest);
									pdfPathc = pdfPath;
									logger.info("<<<===============合同生成结束");
									// 生成电子签章
									try {
										System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									} catch (Exception e) {
										logger.info("个人生成电子签章catch");
										logger.info(e.getMessage());
										wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
										wloanTermInvestDao.update(wloanTermInvest);
									}
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								} else {// 应收账款让
									logger.info("个人应收账款转让合同开始生成=========>>>");
									String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, wloanTermInvest);
									logger.info("<<<===============合同生成结束");
									pdfPathc = pdfPath;
									// 生成电子签章
									try {
										System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									} catch (Exception e) {
										logger.info("个人生成电子签章catch");
										logger.info(e.getMessage());
										wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
										wloanTermInvestDao.update(wloanTermInvest);
									}
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								}
							}
						} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
							// 四方合同存储路径.
							logger.info("个人安心投合同开始生成=========>>>用户为#########" + user.getRealName() + "##################");
							String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, wloanTermInvest);
							logger.info("<<<===============合同生成结束");
							pdfPathc = contractPdfPath;
							// 生成电子签章
							try {
								System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
								createElectronicSign(pdfPathc, user, projectc.getWloanSubject().getLoanApplyId(), projectc.getProjectProductType());
							} catch (Exception e) {
								logger.info("个人生成电子签章catch");
								logger.info(e.getMessage());
								wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
								wloanTermInvestDao.update(wloanTermInvest);
							}
							wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
							wloanTermInvestDao.update(wloanTermInvest);
						}
						// 发送短信
						weixinSendTempMsgService.newSendInvestInfoMsg(wloanTermInvest, project);
						logger.info("<<<<<<<=================个人投资成功用户,生成还款计划,合同结束");

						logger.info("项目满标结束生成用户还款计划,合同");
					}
					int newProjectFlag = wloanTermProjectDao.update(project);
					if (newProjectFlag == 1) {
						logger.info(this.getClass().getName() + "——————更改项目信息成功");
					}

					/**
					 * ************根据出借记录发放积分****************
					 */
					// 出借次数
					List<WloanTermInvest> findWloanTermInvestExists = wloanTermInvestDao.findWloanTermInvestExists(user.getId());
					Double userBouns = amount * (project.getSpan() / 30) / 100;
					int investNum = findWloanTermInvestExists.size();// 投资次数
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String beginDate = "2017-12-26 00:00:00";
					String endDate = "2018-01-02 23:59:59";
					Date now = new Date();
					// N1.查询渠道代码
					if (user.getRecommendUserId() != null && !user.getRecommendUserId().equals("")) {
						ZtmgPartnerPlatform platForm = ztmgPartnerPlatformService.get(user.getRecommendUserId());
						if (platForm != null) {
							if (platForm.getPlatformCode() != null && platForm.getPlatformCode().equals("")) {
								if (platForm.getPlatformCode().equals("008") && investNum == 0 && now.before(sdf1.parse(endDate)) && now.after(sdf1.parse(beginDate)) && project.getId().equals("7847579d93184e2d9b61c7333f8bf4bd") && project.getId().equals("1d2da011f0c14f54a55c4394151a811d") && project.getId().equals("50736392cd184ba1bf7a49d38f227210") && project.getId().equals("830e944706904e91bf3008ae5144f36a") && project.getId().equals("4a46514fc5474eeb863a4b5b603ab396")) {
									addBouns(user.getId(), userBouns * 2, orderId);
								}
							} else {
								if ("2".equals(projectProductTypeString)) {// 供应链项目
									if (IsHolidayOrBirthday.isActivity()) {
										userBouns = userBouns * 2;
										addBouns(user.getId(), userBouns, orderId);
									} else {
										if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
											userBouns = userBouns * 1.5;
											addBouns(user.getId(), userBouns, orderId);
										} else {
											addBouns(user.getId(), userBouns, orderId);
										}
									}
								} else {// 安心投项目
									if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
										userBouns = userBouns * 1.5;
										addBouns(user.getId(), userBouns, orderId);
									} else {
										addBouns(user.getId(), userBouns, orderId);
									}
								}

							}
						}
					} else {
						if ("2".equals(projectProductTypeString)) {// 供应链项目
							if (IsHolidayOrBirthday.isActivity()) {
								userBouns = userBouns * 2;
								addBouns(user.getId(), userBouns, orderId);
							} else {
								if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
									userBouns = userBouns * 1.5;
									addBouns(user.getId(), userBouns, orderId);
								} else {
									addBouns(user.getId(), userBouns, orderId);
								}
							}
						} else {// 安心投项目
							if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
								userBouns = userBouns * 1.5;
								addBouns(user.getId(), userBouns, orderId);
							} else {
								addBouns(user.getId(), userBouns, orderId);
							}
						}
					}
					/**
					 * **********是否首次投资,三级关系****************
					 */
					/**
					 * 1> 在新增投资记录之前，判断客户是否为首次投资.
					 * 2> 判断当前客户是否存在三级关系，完成投资，推荐人可获得100积分.
					 * 3> 同时获得该被邀请用户每笔投资所获积分的5%(userBouns * 5 / 100).
					 * 4> 邀请好友完成出借可获得好友出借金额1%（年化）现金奖励
					 */
					// 客户每次投资，推荐人所获积分
					LevelDistribution distribution = levelDistributionDao.selectByUserId(user.getId());// 客户每次投资，推荐人所获积分
					long integral = Math.round(userBouns * 5 / 100);
					if (findWloanTermInvestExists.size() == 0) { // 首次投资.
						String recommondUserPhone = user.getRecommendUserPhone();
						if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
							UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
							String recommondUserId = recommondUserInfo.getId();
							// 1.为推荐人赠送100积分，新增积分历史记录.
							UserBounsHistory userBounsHistory_one = new UserBounsHistory();
							userBounsHistory_one.setId(IdGen.uuid());
							userBounsHistory_one.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_one.setAmount(100D);
							userBounsHistory_one.setCreateDate(new Date());
							userBounsHistory_one.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_one.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							int flag = userBounsHistoryService.insert(userBounsHistory_one);
							// 新增积分历史记录成功后，变更推荐人积分信息.
							if (flag == 1) {
								UserBounsPoint entity = userBounsPointService.getUserBounsPoint(recommondUserId);
								entity.setScore(entity.getScore() + 100);
								userBounsPointService.update(entity);
							}
							// 2.同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_three = new UserBounsHistory();
							userBounsHistory_three.setId(IdGen.uuid());
							userBounsHistory_three.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_three.setAmount(Double.valueOf(integral));
							userBounsHistory_three.setCreateDate(new Date());
							userBounsHistory_three.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_three.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							int mark = userBounsHistoryService.insert(userBounsHistory_three);
							// 新增积分历史记录成功后，变更推荐人积分信息.
							if (mark == 1) {
								UserBounsPoint model = userBounsPointService.getUserBounsPoint(recommondUserId);
								String integralStr = String.valueOf(integral);
								model.setScore(model.getScore() + Integer.parseInt(integralStr));
								userBounsPointService.update(model);
							}
						} else if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {
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
						}
					} else { // 再次投资(二次投资及以后的所有投资).
						String recommondUserPhone = user.getRecommendUserPhone();
						if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
							UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
							String recommondUserId = recommondUserInfo.getId();
							// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_two = new UserBounsHistory();
							userBounsHistory_two.setId(IdGen.uuid());
							userBounsHistory_two.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_two.setAmount(Double.valueOf(integral));
							userBounsHistory_two.setCreateDate(new Date());
							userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							int mark = userBounsHistoryService.insert(userBounsHistory_two);
							// 新增积分历史记录成功后，变更推荐人积分信息.
							if (mark == 1) {
								UserBounsPoint model = userBounsPointService.getUserBounsPoint(recommondUserId);
								String integralStr = String.valueOf(integral);
								model.setScore(model.getScore() + Integer.parseInt(integralStr));
								userBounsPointService.update(model);
							}
						} else if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {
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
						}
					}
					// N2.释放项目锁
					if (JedisUtils.releaseLock(proKey, lockProValue)) {
						logger.info("项目锁已释放");
						logger.info("项目锁已解锁=====" + System.currentTimeMillis());
					}

					// N3.账户锁

					accKey = "ACC" + userAccount.getId();
					lockAccValue = JedisUtils.lockWithTimeout(accKey, 4000, 2000);// 账户锁

					if (lockAccValue != null && !lockAccValue.equals("")) {
						logger.info("=======账户锁定开始" + System.currentTimeMillis());

						/**
						 * **************出借账户变更************************
						 */
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
						logger.info("更改账户前,账户总额为" + userAccount.getTotalAmount() + "###可用余额为" + userAccount.getAvailableAmount());
						userAccount.setTotalAmount(userAccount.getTotalAmount() + vouAmount + interest); // 账户总额
						userAccount.setAvailableAmount(userAccount.getAvailableAmount() - amount + vouAmount);// 可用余额
						userAccount.setRegularDuePrincipal(userAccount.getRegularDuePrincipal() + amount); // 定期代收本金
						userAccount.setRegularDueInterest(userAccount.getRegularDueInterest() + interest); // 定期代收收益
						userAccount.setRegularTotalAmount(userAccount.getRegularTotalAmount() + amount); // 定期投资总额
						logger.info("更改账户后,账户总额为" + userAccount.getTotalAmount() + "###可用余额为" + userAccount.getAvailableAmount());
						int updateAccount = cgbUserAccountService.updateUserAccountInfo(userAccount);
						if (updateAccount == 1) {
							logger.info(this.getClass().getName() + "——————更改账户信息成功");
							if (JedisUtils.releaseLock(accKey, lockAccValue)) {
								logger.info("账户锁已释放");
								logger.info("=======账户已解锁=====" + System.currentTimeMillis());
							}
							/**
							 * **********抵用券状态变更*************
							 */
							// 记录抵用券流水
							AUserAwardsHistory aUserAwards = new AUserAwardsHistory();
							aUserAwards.setBidId(wloanTermInvest.getId());
							List<AUserAwardsHistory> aUserAwardsHistoryList = aUserAwardsHistoryService.findVouchers(aUserAwards);

							if (aUserAwardsHistoryList != null && aUserAwardsHistoryList.size() > 0) {

								userAccount.setAvailableAmount(availableAmount - amount);

								logger.info("减去出借金额后的可用余额为" + userAccount.getAvailableAmount());

								int time = 1;

								for (AUserAwardsHistory aUserAwardsHistory : aUserAwardsHistoryList) {

									Double voucherAmount = aUserAwardsHistory.getaVouchersDic().getAmount();// 抵用券金额

									Double newAvaAmount = userAccount.getAvailableAmount() + voucherAmount;

									logger.info("[抵用券流水记录开始时]账户可用余额" + newAvaAmount);

									logger.info("抵用券状态变更为已使用开始");
									aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
									aUserAwardsHistory.setBidId(orderId);
									aUserAwardsHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * ++time));
									int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
									if (updateVoucher == 1) {
										logger.info(this.getClass().getName() + "——————更改抵用券状态成功");
									}
									logger.info("抵用券状态变更为已使用结束");
									// 保存客户使用抵用券流水记录
									logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录开始");
									CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
									userTransDetail.setId(IdGen.uuid()); // 主键ID.
									userTransDetail.setTransId(aUserAwardsHistory.getId()); // 客户抵用券记录ID.
									userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
									userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
									userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++time)); // 抵用券使用时间.
									userTransDetail.setTrustType(UserTransDetailService.trust_type10); // 优惠券.
									userTransDetail.setAmount(aUserAwardsHistory.getaVouchersDic().getAmount()); // 优惠券金额.
									userTransDetail.setAvaliableAmount(newAvaAmount); // 当前可用余额.
									userTransDetail.setInOutType(UserTransDetailService.in_type); // 优惠券收入.
									userTransDetail.setRemarks("抵用券"); // 备注信息.
									userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
									int userVoucherDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
									if (userVoucherDetailFlag == 1) {
										logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录成功");
									}
									userAccount.setAvailableAmount(newAvaAmount);
									logger.info("[抵用券流水记录完成时]账户可用余额" + newAvaAmount);
								}
							}
							/**
							 * ***********出借记录状态变更**************
							 */
							wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_1);
							int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
							if (i > 0) {
								logger.info("[用户出借成功]" + orderId + "状态更新成功");
								logger.info("=====记录客户出借交易流水开始======"); // 保存客户流水记录
								CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
								userTransDetail.setId(IdGen.uuid()); // 主键ID.
								userTransDetail.setTransId(orderId); // 客户投资记录ID.
								userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
								userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
								userTransDetail.setTransDate(new Date()); // 投资交易时间.
								userTransDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
								userTransDetail.setAmount(amount); // 投资交易金额.
								userTransDetail.setAvaliableAmount(availableAmount - amount); // 当前可用余额.
								userTransDetail.setInOutType(UserTransDetailService.out_type); // 投资支出.
								userTransDetail.setRemarks("出借"); // 备注信息.
								userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
								int userTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
								if (userTransDetailFlag == 1) {
									logger.info(this.getClass().getName() + "——————保存客户出借流水记录成功");
								}
							}
						}

						// N4.发送站内信
						StationLetter letter = new StationLetter();
						letter.setUserId(wloanTermInvest.getUserInfo().getId());
						letter.setLetterType(StationLettersService.LETTER_TYPE_WLOAN);
						letter.setTitle("您已成功向" + project.getName() + "项目出借资金:" + amount + ",借款周期:" + project.getSpan() + "天。");
						letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
						letter.setState(StationLettersService.LETTER_STATE_UNREAD);
						letter.setSendTime(new Date());
						stationLettersService.save(letter);
						// N5.发送短信,微信
						weixinSendTempMsgService.sendInvestInfoMsg(wloanTermInvest, project);
						result = 1;
						return result;
					}

				} else {
					result = 0;
					return result;
				}
				long endTime = System.currentTimeMillis();
				logger.info("计时结束 耗时：{" + DateUtils.formatDateTime(endTime - beginTime) + "}");

			}
		} catch (Exception e) {
			// 立即释放项目锁
			if (JedisUtils.releaseLock(proKey, lockProValue)) {
				logger.info("[取消投资]项目锁已释放");
				logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
			}
			// 立即释放账户锁
			if (JedisUtils.releaseLock(accKey, lockAccValue)) {
				logger.info("账户锁已释放");
				logger.info("=======账户已解锁=====" + System.currentTimeMillis());
			}
			return result;
		}
		return result;

	}

	/**
	 * 所有投资成功用户,生成还款计划,合同
	 * 
	 * @param project
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void createPlanAndContract(WloanTermProject project) throws Exception {

		try {
			// 作用是让当前线程休眠，即当前线程会从“运行状态”进入到“休眠(阻塞)状态”。sleep()会指定休眠时间，线程休眠的时间会大于/等于该休眠时间；在线程重新被唤醒时，它会由“阻塞状态”变成“就绪状态”，从而等待CPU的调度执行。
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 只针对供应链散标.
			/**
			 * 中登网登记落单.
			 */
			ZdwProOrderInfo zpoi = new ZdwProOrderInfo();
			zpoi.setId(IdGen.uuid());
			zpoi.setProId(project.getId());
			zpoi.setProNo(project.getSn());
			zpoi.setStatus(ProOrderStatusEnum.PRO_ORDER_STATUS_01.getValue());
			zpoi.setFullDate(project.getFullDate());
			zpoi.setCreateDate(new Date());
			zpoi.setUpdateDate(new Date());
			zpoi.setRemarks("中登网登记-散标订单");
			int insert = zdwProOrderInfoDao.insert(zpoi);
			if (insert == 1) {
				logger.info("中登网散标信息落单成功！");
			} else {
				logger.info("中登网散标信息落单失败！");
			}
		}

		int i = 0;
		logger.info("根据项目ID[" + project.getId() + "]查询所有投资成功用户,生成还款计划,合同========>>>>>>>>");
		/**
		 * 根据项目ID查询所有投资成功用户,生成还款计划,合同
		 */
		if (project.getId() != null) {
			List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(project.getId());
			logger.info("查询出项目ID为" + project.getId() + "共有投资笔数为" + investList.size());
			if (investList != null && investList.size() > 0) {
				for (WloanTermInvest wloanTermInvest : investList) {
					/**
					 * ****************根据出借记录生成个人还款计划**************************
					 */
					logger.info("=================生成用户" + wloanTermInvest.getUserId() + "还款计划,合同开始================");
					// 生成客户投资还款计划-旧.
					if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0)) {
						String wloanTermUserPlanFlag = wloanTermUserPlanService.initInvUserPlan(wloanTermInvest);
//						String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitWloanTermUserPlan(wloanTermInvest);
						if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
							logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
						} else {
							throw new Exception("系统异常");
						}
					}
					// 生成客户投资还款计划-新.
					else if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1)) {
						String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitCgbWloanTermUserPlan(wloanTermInvest);
						if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
							logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
						} else {
							throw new Exception("系统异常");
						}
					}

					/**
					 * *************根据出借记录生成合同****************
					 */
					// 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
					final String pdfPathc;
					final UserInfo user = userInfoDao.getCgb(wloanTermInvest.getUserInfo().getId());
					final WloanTermProject projectc = project;
					if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链项目.
						CreditUserApply creditUserApply = creditUserApplyService.get(projectc.getCreditUserApplyId()); // 借款申请.
						if (creditUserApply != null) {
							String financingType = creditUserApply.getFinancingType(); // 融资类型.
							if (FINANCING_TYPE_1.equals(financingType)) {// 应收账款转让
								logger.info("第" + i + "条应收账款转让合同开始生成=========>>>");
								// String pdfPath =
								// CreateSupplyChainPdfContract.CreateSupplyChainPdf(user,
								// project, wloanTermInvest);
								// 熙耘（代偿方），爱亲（担保方），全新协议模版
								if (CreditUserInfo.XIYUN_ID.equals(creditUserApply.getReplaceUserId())) {
									// 借款协议（应收账款质押）.
									String pdfPath = LoanAgreementPdfUtil.createXiYunLoanAgreement(user, projectc, wloanTermInvest);
									pdfPathc = pdfPath;
									logger.info(this.getClass() + "【熙耘】借款协议创建成功.");
									// 生成电子签章
									try {
										System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
										createXiYunElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									} catch (Exception e) {
										logger.info("第" + i + "条生成电子签章catch");
										logger.info(e.getMessage());
										wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
										wloanTermInvestDao.update(wloanTermInvest);
									}
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								} else {
									// 借款协议（应收账款质押）.
									String pdfPath = LoanAgreementPdfUtil.createLoanAgreement(user, projectc, wloanTermInvest);
									logger.info("<<<===============合同生成结束");
									pdfPathc = pdfPath;

									// 生成电子签章
									try {
										System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

										createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
									} catch (Exception e) {
										logger.info("第" + i + "条生成电子签章catch");
										logger.info(e.getMessage());
										wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
										wloanTermInvestDao.update(wloanTermInvest);
									}
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								}
							} else if (FINANCING_TYPE_2.equals(financingType)) {// 订单融资
								logger.info("第" + i + "条订单融资合同开始生成=========>>>");
								String pdfPath = AiQinPdfContract.createOrderFinancingPdf(user, projectc, wloanTermInvest);
								logger.info("<<<===============合同生成结束");
								pdfPathc = pdfPath;

								// 生成电子签章
								try {
									System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

									createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
								} catch (Exception e) {
									logger.info("第" + i + "条生成电子签章catch");
									logger.info(e.getMessage());
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								}
								wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(wloanTermInvest);
							} else {// 应收账款让
								logger.info("第" + i + "条应收账款让合同开始生成=========>>>");
								// String pdfPath =
								// CreateSupplyChainPdfContract.CreateSupplyChainPdf(user,
								// project, wloanTermInvest);
								// 借款协议（应收账款质押）.
								String pdfPath = LoanAgreementPdfUtil.createLoanAgreement(user, projectc, wloanTermInvest);
								logger.info("<<<===============合同生成结束");
								pdfPathc = pdfPath;
								// 生成电子签章
								try {
									System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

									createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
								} catch (Exception e) {
									logger.info("第" + i + "条生成电子签章catch");
									logger.info(e.getMessage());
									wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(wloanTermInvest);
								}
								wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(wloanTermInvest);
							}
						}
					} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
						// 四方合同存储路径.
						logger.info("第" + i + "条安心投合同开始生成=========>>>用户为#########" + user.getRealName() + "##################");
						String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, wloanTermInvest);
						logger.info("<<<===============合同生成结束");
						pdfPathc = contractPdfPath;
						// 生成电子签章
						try {
							System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

							createElectronicSign(pdfPathc, user, projectc.getWloanSubject().getLoanApplyId(), projectc.getProjectProductType());
						} catch (Exception e) {
							logger.info("第" + i + "条生成电子签章catch");
							logger.info(e.getMessage());
							wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
							wloanTermInvestDao.update(wloanTermInvest);
						}
						wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
						wloanTermInvestDao.update(wloanTermInvest);
					}
					// 发送短信
					weixinSendTempMsgService.newSendInvestInfoMsg(wloanTermInvest, project);
					logger.info("====================生成用户" + wloanTermInvest.getUserId() + "还款计划,合同结束====================");
					i++;
				}
			} else {
				logger.info("未查询到项目投资记录");
			}
		}
		logger.info("<<<<<=================================根据项目ID[" + project.getId() + "]查询所有投资成功用户,生成还款计划,合同");
	}

	/**
	 * 个人用户,生成还款计划,合同
	 * 
	 * @param project
	 * @throws Exception
	 */
	public void createUserSelfPlanAndContract(WloanTermProject project, WloanTermInvest wloanTermInvest, UserInfo user) throws Exception {

		logger.info("个人投资成功用户,生成还款计划,合同========>>>>>>>>");
		/**
		 * ****************根据出借记录生成个人还款计划**************************
		 */
		logger.info("=================生成用户" + wloanTermInvest.getUserId() + "还款计划,合同开始================");
		// 生成客户投资还款计划-旧.
		if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0)) {
			String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitWloanTermUserPlan(wloanTermInvest);
			if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
				logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
			} else {
				throw new Exception("系统异常");
			}
		}
		// 生成客户投资还款计划-新.
		else if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1)) {
			String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitCgbWloanTermUserPlan(wloanTermInvest);
			if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
				logger.info(this.getClass().getName() + "——————生成客户个人还款计划成功");
			} else {
				throw new Exception("系统异常");
			}
		}

		/**
		 * *************根据出借记录生成合同****************
		 */
		// 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
		final String pdfPathc;
		final WloanTermProject projectc = project;
		if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链项目.
			CreditUserApply creditUserApply = creditUserApplyService.get(projectc.getCreditUserApplyId()); // 借款申请.
			if (creditUserApply != null) {
				String financingType = creditUserApply.getFinancingType(); // 融资类型.
				if (FINANCING_TYPE_1.equals(financingType)) {// 应收账款转让
					logger.info("个人应收账款转让合同开始生成=========>>>");
					String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, wloanTermInvest);
					logger.info("<<<===============合同生成结束");
					pdfPathc = pdfPath;

					// 生成电子签章
					try {
						System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
					} catch (Exception e) {
						logger.info("个人生成电子签章catch");
						logger.info(e.getMessage());
						wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						wloanTermInvestDao.update(wloanTermInvest);
					}
					wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
					wloanTermInvestDao.update(wloanTermInvest);
				} else if (FINANCING_TYPE_2.equals(financingType)) {// 订单融资
					logger.info("个人订单融资合同开始生成=========>>>");
					String pdfPath = AiQinPdfContract.createOrderFinancingPdf(user, projectc, wloanTermInvest);
					pdfPathc = pdfPath;
					logger.info("<<<===============合同生成结束");
					// 生成电子签章
					try {
						System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
					} catch (Exception e) {
						logger.info("个人生成电子签章catch");
						logger.info(e.getMessage());
						wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						wloanTermInvestDao.update(wloanTermInvest);
					}
					wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
					wloanTermInvestDao.update(wloanTermInvest);
				} else {// 应收账款让
					logger.info("个人应收账款转让合同开始生成=========>>>");
					String pdfPath = CreateSupplyChainPdfContract.CreateSupplyChainPdf(user, project, wloanTermInvest);
					logger.info("<<<===============合同生成结束");
					pdfPathc = pdfPath;
					// 生成电子签章
					try {
						System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						createElectronicSign(pdfPathc, user, projectc.getCreditUserApplyId(), projectc.getProjectProductType());
					} catch (Exception e) {
						logger.info("个人生成电子签章catch");
						logger.info(e.getMessage());
						wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						wloanTermInvestDao.update(wloanTermInvest);
					}
					wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
					wloanTermInvestDao.update(wloanTermInvest);
				}
			}
		} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
			// 四方合同存储路径.
			logger.info("个人安心投合同开始生成=========>>>用户为#########" + user.getRealName() + "##################");
			String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, wloanTermInvest);
			logger.info("<<<===============合同生成结束");
			pdfPathc = contractPdfPath;
			// 生成电子签章
			try {
				System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
				createElectronicSign(pdfPathc, user, projectc.getWloanSubject().getLoanApplyId(), projectc.getProjectProductType());
			} catch (Exception e) {
				logger.info("个人生成电子签章catch");
				logger.info(e.getMessage());
				wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
				wloanTermInvestDao.update(wloanTermInvest);
			}
			wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
			wloanTermInvestDao.update(wloanTermInvest);
		}
		// 发送短信
		weixinSendTempMsgService.newSendInvestInfoMsg(wloanTermInvest, project);
		logger.info("<<<<<<<=================个人投资成功用户,生成还款计划,合同结束");
	}

	/**
	 * 出借人出借请求----PC端新2_2_1
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserToInvestWeb2_2_1(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount, UserInfo user, String ip) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台出借
		logger.info("平台出借处理开始");
		int resultInvest = insertUserInvest2_2_1(projectId, amount, voucherList, user, ip, orderId);
		if (resultInvest > 0) {
			logger.info("出借申请成功");
		}
		logger.info("平台出借处理开始");
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		Double tAmount = NumberUtils.scaleDouble(100 * NumberUtils.scaleDouble(amount));
		BigDecimal totalAmount = new BigDecimal(tAmount);
		if (vouAmount == null) {
			vouAmount = 0d;
		}
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", totalAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		params.put("service", "web.p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.NEW_BACK_INVEST_URL2_2_1);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB + "&orderId=" + orderId);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("出借WEB 2_2_1版本[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}

	/**
	 * 出借人出借请求----H5新2_2_1
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserToInvestH52_2_1(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount, UserInfo user, String ip) throws WinException, Exception {

		String jedisUserId = JedisUtils.get(token);
		UserInfo userInfo = userInfoDao.getCgb(jedisUserId);
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台出借
		logger.info("平台出借处理开始");
		int resultInvest = insertUserInvest2_2_1(projectId, amount, voucherList, user, ip, orderId);
		if (resultInvest > 0) {
			logger.info("出借申请成功");
		}
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("userId", userId);
		Double tAmount = NumberUtils.scaleDouble(100 * NumberUtils.scaleDouble(amount));
		BigDecimal totalAmount = new BigDecimal(tAmount);
		if (vouAmount == null) {
			vouAmount = 0d;
		}
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", totalAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		params.put("service", "h5.p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.NEW_BACK_INVEST_URL2_2_1);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB + "&orderId=" + orderId);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("出借H5 2_2_1版本[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);

		String data = encryptRet.get("data");
		data = URLEncoder.encode(data, "UTF-8");
		String tm = encryptRet.get("tm");
		tm = URLEncoder.encode(tm, "UTF-8");
		encryptRet.put("tm", tm);
		encryptRet.put("data", data);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}

	/**
	 * 新增出借人出借记录2_2_1版本
	 * 
	 * @param projectId
	 * @param amount
	 * @param voucherList
	 * @param user
	 * @param ip
	 * @param orderId
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertUserInvest2_2_1(String projectId, Double amount, List<String> voucherList, UserInfo user, String ip, String orderId) throws WinException, Exception {

		/**
		 * 定义变量
		 */
		Double vouAmount = 0d;
		Double vouAmountTotal = 0d;
		// N1.抵用券金额
		if (voucherList != null && voucherList.size() > 0) {
			for (int i = 0; i < voucherList.size(); i++) {
				String vouid = voucherList.get(i);
				if (!StringUtils.isBlank(vouid)) {
					AUserAwardsHistory aUserAwardsHistory = userVouchersDao.get(vouid);
					// 抵用券
					if (aUserAwardsHistory.getType().equals(AUserAwardsHistoryService.COUPONS_TYPE_1)) {
						AVouchersDic voucher = aVouchersDicDao.get(aUserAwardsHistory.getAwardId());
						vouAmount = voucher.getAmount();
						// 抵用券使用总额
						vouAmountTotal = vouAmountTotal + vouAmount;
					}
				}
			}
		}
		// N2.根据项目ID获取项目信息
		WloanTermProject project = wloanTermProjectDao.get(projectId);
		// N3.利息计算
		// 1.日利息
//		Double dayInterest = NumberUtils.scaleDouble(amount * project.getAnnualRate() / (365 * 100));
		// 2.出借利息
//		Double interest = NumberUtils.scaleDouble(dayInterest * (project.getSpan()));
		Double interest = InterestUtils.getInvInterest(amount, project.getAnnualRate(), project.getSpan());
		// N4.插入出借记录表
		// 开始插入投资详情
		logger.info(this.getClass().getName() + "——————开始插入用户投资信息");
		WloanTermInvest invest = new WloanTermInvest();
		invest.setId(orderId); // ID
		invest.setWloanTermProject(project); // 项目信息
		invest.setUserInfo(user); // 用户信息
		invest.setAmount(amount); // 投资金额
		invest.setInterest(interest); // 利息
		invest.setBeginDate(new Date()); // 投资时间
		invest.setCreateDate(new Date());
		invest.setIp(ip); // 投资Ip地址
		invest.setState(WLOAN_TERM_INVEST_STATE_0); // 投标状态（受理中）
		invest.setBidState(WLOAN_TERM_INVEST_STATE_0); // 投标状态
		// if (vouAmountTotal != 0d) {
		// invest.setVoucherAmount(vouAmountTotal); // 抵用券金额
		// invest.setRemarks("使用" + vouAmountTotal + "元抵用券");
		// }

		int insertInvest = wloanTermInvestDao.insert(invest);
		if (insertInvest == 1) {
			logger.info(this.getClass().getName() + "——————插入用户出借信息成功");
		}
		logger.info("抵用劵业务处理");
		if (voucherList != null && voucherList.size() > 0) {
			for (int i = 0; i < voucherList.size(); i++) {
				String vouid = voucherList.get(i);
				// 获取抵用券信息
				if (!StringUtils.isBlank(vouid)) {
					AUserAwardsHistory aUserAwardsHistory = userVouchersDao.get(vouid);
					logger.info(this.getClass().getName() + "——————更改抵用券出借ID字段开始");
					// aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
					aUserAwardsHistory.setBidId(invest.getId());
					aUserAwardsHistory.setUpdateDate(new Date());
					int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
					if (updateVoucher == 1) {
						logger.info(this.getClass().getName() + "——————更改抵用券出借ID成功");
					}
				}

			}
		}
		logger.info("抵用劵业务处理");
		return insertInvest;
	}

	/**
	 * 
	 * methods: newCreateUserInvest2_2_1 <br>
	 * description: 出借回调业务处理(2_2_1) <br>
	 * author: Mr.Roy <br>
	 * date: 2018年11月29日 下午7:11:20
	 * 
	 * @param orderId
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int newCreateUserInvest2_2_1(String orderId) throws WinException, Exception {

		int result = 0;// 返回结果标识
		String proKey = "";
		String lockProValue = "";
		String accKey = "";
		String lockAccValue = "";// 账户锁
		try {

			long beginTime = System.currentTimeMillis();
			/**
			 * 根据订单ID查询出借记录
			 */
			WloanTermInvest wloanTermInvest = wloanTermInvestService.get(orderId);
			if (wloanTermInvest != null) {
				CgbUserAccount userAccount = cgbUserAccountDao.getUserAccountInfo(wloanTermInvest.getUserId());// 账户信息
				String projectId = wloanTermInvest.getWloanTermProject().getId();// 项目ID
				Double amount = wloanTermInvest.getAmount();// 出借金额
				Double interest = wloanTermInvest.getInterest();// 出借利息
				Double totalInvestAmount = wloanTermInvestDao.getInvestTotalAmount(projectId) == null ? 0d : wloanTermInvestDao.getInvestTotalAmount(projectId);// 已出借成功总额

				proKey = "PRO" + projectId;
				lockProValue = JedisUtils.lockWithTimeout(proKey, 10000, 2000);// 项目锁

				final WloanTermProject project = wloanTermProjectDao.get(wloanTermInvest.getWloanTermProject().getId());// 项目信息

				Double balanceAmount = NumberUtils.scaleDouble(project.getAmount() - project.getCurrentAmount());

				// N1.锁项目
				if (lockProValue != null && !lockProValue.equals("")) {

					logger.info("项目锁开始=====" + System.currentTimeMillis());

					// ******如果已出借成功总额等于项目总额时则进行取消投资业务处理并短信通知用户**************
					Double availableAmount = userAccount.getAvailableAmount();// 账户可用余额
					logger.info("=====账户可用余额为" + availableAmount);
					if (totalInvestAmount > 0) {
						if (totalInvestAmount.equals(project.getAmount())) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目已满标", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						} else if (amount > NumberUtils.scaleDouble(project.getAmount() - project.getCurrentAmount())) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目剩余资金不足", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						} else if (NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) < 100 && NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) > 0) {
							Map<String, String> resCancelInvest = cancelInvest(orderId);
							if (resCancelInvest.get("respSubCode").equals("000000")) {
								logger.info("项目已满标订单" + orderId + "取消投资成功");
								logger.info("平台取消投资业务处理开始");
								int cancelUserInvest = cancelUserInvest(orderId, "项目尾笔金额需全投", availableAmount);
								logger.info("平台取消投资业务处理结束");
								if (cancelUserInvest > 0) {
									logger.info("短信通知用户出借失败");
									weixinSendTempMsgService.sendInvestFailMsg(wloanTermInvest, project);
								}
							}

							// 立即释放项目锁
							if (JedisUtils.releaseLock(proKey, lockProValue)) {
								logger.info("[取消投资]项目锁已释放");
								logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
							}

							result = 1;
							return result;
						}
					}

					final UserInfo user = userInfoDao.getCgb(wloanTermInvest.getUserInfo().getId());

					String projectProductTypeString = project.getProjectProductType();
					// 更改项目信息
					logger.info(this.getClass().getName() + "——————更改项目信息开始");
					double currentRealAmount = project.getCurrentAmount() + amount;
					project.setCurrentAmount(currentRealAmount);
					project.setCurrentRealAmount(currentRealAmount);
					// 判断项目是否满标
					if (NumberUtils.scaleDouble(project.getCurrentRealAmount()).equals(NumberUtils.scaleDouble(project.getAmount()))) {
						logger.info("项目满标开始生成用户还款计划,合同");
						project.setState(WloanTermProjectService.FULL);
						project.setFullDate(new Date());
						// 生成新的项目还款计划
						wloanTermProjectPlanService.deleteByProjectId(project.getId());
						wloanTermProjectPlanService.newInitWloanTermProjectPlan(project);

						// logger.info("个人投资成功用户,生成还款计划,合同========>>>>>>>>");
						/**
						 * ****************根据出借记录生成个人还款计划***********************
						 * ***
						 */
						// logger.info("=================生成用户" +
						// wloanTermInvest.getUserId() +
						// "还款计划,合同开始================");
						// // 生成客户投资还款计划-旧.
						// if
						// (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0))
						// {
						// String wloanTermUserPlanFlag =
						// wloanTermUserPlanService.newInitWloanTermUserPlan(wloanTermInvest);
						// if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
						// logger.info(this.getClass().getName() +
						// "——————生成客户个人还款计划成功");
						// } else {
						// throw new Exception("系统异常");
						// }
						// }
						// // 生成客户投资还款计划-新.
						// else if
						// (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1))
						// {
						// String wloanTermUserPlanFlag =
						// wloanTermUserPlanService.newInitCgbWloanTermUserPlan(wloanTermInvest);
						// if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
						// logger.info(this.getClass().getName() +
						// "——————生成客户个人还款计划成功");
						// } else {
						// throw new Exception("系统异常");
						// }
						// }

						/**
						 * *************根据出借记录生成合同****************
						 */
						// 安心投与供应链(四方合同/应收账款转让协议)的分离创建.
						// final String pdfPathc;
						// final WloanTermProject projectc = project;
						// if
						// (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType()))
						// { // 供应链项目.
						// CreditUserApply creditUserApply =
						// creditUserApplyService.get(projectc.getCreditUserApplyId());
						// // 借款申请.
						// if (creditUserApply != null) {
						// String financingType =
						// creditUserApply.getFinancingType(); // 融资类型.
						// if (FINANCING_TYPE_1.equals(financingType)) {//
						// 应收账款转让
						// logger.info("个人应收账款转让合同开始生成=========>>>");
						// // String pdfPath =
						// //
						// CreateSupplyChainPdfContract.CreateSupplyChainPdf(user,
						// // project, wloanTermInvest);
						// // 借款协议（应收账款质押）.
						// String pdfPath =
						// LoanAgreementPdfUtil.createLoanAgreement(user,
						// projectc, wloanTermInvest);
						// logger.info("<<<===============合同生成结束");
						// pdfPathc = pdfPath;
						// // 生成电子签章
						// try {
						// System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
						// "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						//
						// new Thread() {
						//
						// public void run() {
						//
						// // 生成电子签章
						// createElectronicSign(pdfPathc, user,
						// projectc.getCreditUserApplyId(),
						// projectc.getProjectProductType());
						// }
						// }.start();
						//
						// } catch (Exception e) {
						// logger.info("个人生成电子签章catch");
						// logger.info(e.getMessage());
						// wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// }
						// wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// } else if (FINANCING_TYPE_2.equals(financingType))
						// {// 订单融资
						// logger.info("个人订单融资合同开始生成=========>>>");
						// String pdfPath =
						// AiQinPdfContract.createOrderFinancingPdf(user,
						// projectc, wloanTermInvest);
						// pdfPathc = pdfPath;
						// logger.info("<<<===============合同生成结束");
						// // 生成电子签章
						// try {
						// System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
						// "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						// new Thread() {
						//
						// public void run() {
						//
						// // 生成电子签章
						// createElectronicSign(pdfPathc, user,
						// projectc.getCreditUserApplyId(),
						// projectc.getProjectProductType());
						// }
						// }.start();
						// } catch (Exception e) {
						// logger.info("个人生成电子签章catch");
						// logger.info(e.getMessage());
						// wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// }
						// wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// } else {// 应收账款让
						// logger.info("个人应收账款转让合同开始生成=========>>>");
						// // String pdfPath =
						// //
						// CreateSupplyChainPdfContract.CreateSupplyChainPdf(user,
						// // project, wloanTermInvest);
						// // 借款协议（应收账款质押）.
						// String pdfPath =
						// LoanAgreementPdfUtil.createLoanAgreement(user,
						// projectc, wloanTermInvest);
						// logger.info("<<<===============合同生成结束");
						// pdfPathc = pdfPath;
						// // 生成电子签章
						// try {
						// System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
						// "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						// new Thread() {
						//
						// public void run() {
						//
						// // 生成电子签章
						// createElectronicSign(pdfPathc, user,
						// projectc.getCreditUserApplyId(),
						// projectc.getProjectProductType());
						// }
						// }.start();
						// } catch (Exception e) {
						// logger.info("个人生成电子签章catch");
						// logger.info(e.getMessage());
						// wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// }
						// wloanTermInvest.setContractPdfPath(pdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// }
						// }
						// } else if
						// (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType()))
						// { // 安心投项目.
						// // 四方合同存储路径.
						// logger.info("个人安心投合同开始生成=========>>>用户为#########" +
						// user.getRealName() + "##################");
						// String contractPdfPath =
						// CreateSupplyChainPdfContract.CreateRelievedPdf(user,
						// project, wloanTermInvest);
						// logger.info("<<<===============合同生成结束");
						// pdfPathc = contractPdfPath;
						// // 生成电子签章
						// try {
						// System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
						// "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						// new Thread() {
						//
						// public void run() {
						//
						// // 生成电子签章
						// createElectronicSign(pdfPathc, user,
						// projectc.getWloanSubject().getLoanApplyId(),
						// projectc.getProjectProductType());
						// }
						// }.start();
						// } catch (Exception e) {
						// logger.info("个人生成电子签章catch");
						// logger.info(e.getMessage());
						// wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// }
						// wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
						// wloanTermInvestDao.update(wloanTermInvest);
						// }
						// 发送短信
						// weixinSendTempMsgService.newSendInvestInfoMsg(wloanTermInvest, project);
						// logger.info("<<<<<<<=================个人投资成功用户,生成还款计划,合同结束");

					}
					int newProjectFlag = wloanTermProjectDao.update(project);
					if (newProjectFlag == 1) {
						logger.info(this.getClass().getName() + "——————更改项目信息成功");
					}

					/**
					 * ************根据出借记录发放积分****************
					 */
					// 出借次数
					List<WloanTermInvest> findWloanTermInvestExists = wloanTermInvestDao.findWloanTermInvestExists(user.getId());
					Double userBouns = amount * (project.getSpan() / 30) / 100;
					userBouns = NumberUtils.scaleDouble(userBouns);
					int investNum = findWloanTermInvestExists.size();// 投资次数
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String beginDate = "2017-12-26 00:00:00";
					String endDate = "2018-01-02 23:59:59";
					Date now = new Date();
					// N1.查询渠道代码
					if (user.getRecommendUserId() != null && !user.getRecommendUserId().equals("")) {
						ZtmgPartnerPlatform platForm = ztmgPartnerPlatformService.get(user.getRecommendUserId());
						if (platForm != null) {
							if (platForm.getPlatformCode() != null) {
								if (platForm.getPlatformCode().equals("008") && investNum == 0 && now.before(sdf1.parse(endDate)) && now.after(sdf1.parse(beginDate)) && project.getId().equals("7847579d93184e2d9b61c7333f8bf4bd") && project.getId().equals("1d2da011f0c14f54a55c4394151a811d") && project.getId().equals("50736392cd184ba1bf7a49d38f227210") && project.getId().equals("830e944706904e91bf3008ae5144f36a") && project.getId().equals("4a46514fc5474eeb863a4b5b603ab396")) {
									addBouns(user.getId(), userBouns * 2, orderId);
								}
							} else {
								if ("2".equals(projectProductTypeString)) {// 供应链项目
									if (IsHolidayOrBirthday.isActivity()) {
										userBouns = userBouns * 2;
										addBouns(user.getId(), userBouns, orderId);
									} else {
										if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
											userBouns = userBouns * 1.5;
											addBouns(user.getId(), userBouns, orderId);
										} else {
											addBouns(user.getId(), userBouns, orderId);
										}
									}
								} else {// 安心投项目
									if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
										userBouns = userBouns * 1.5;
										addBouns(user.getId(), userBouns, orderId);
									} else {
										addBouns(user.getId(), userBouns, orderId);
									}
								}

							}
						}
					} else {
						if ("2".equals(projectProductTypeString)) {// 供应链项目
							if (IsHolidayOrBirthday.isActivity()) {
								userBouns = userBouns * 2;
								addBouns(user.getId(), userBouns, orderId);
							} else {
								if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
									userBouns = userBouns * 1.5;
									addBouns(user.getId(), userBouns, orderId);
								} else {
									addBouns(user.getId(), userBouns, orderId);
								}
							}
						} else {// 安心投项目
							if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
								userBouns = userBouns * 1.5;
								addBouns(user.getId(), userBouns, orderId);
							} else {
								addBouns(user.getId(), userBouns, orderId);
							}
						}
					}
					/**
					 * **********是否首次投资,三级关系****************
					 */
					/**
					 * 1> 在新增投资记录之前，判断客户是否为首次投资.
					 * 2> 判断当前客户是否存在三级关系，完成投资，推荐人可获得100积分.
					 * 3> 同时获得该被邀请用户每笔投资所获积分的5%(userBouns * 5 / 100).
					 * 4> 邀请好友完成出借可获得好友出借金额1%（年化）现金奖励
					 */
					// 客户每次投资，推荐人所获积分
					LevelDistribution distribution = levelDistributionDao.selectByUserId(user.getId());// 客户每次投资，推荐人所获积分
					Double integral = userBouns * 5 / 100;
					integral = NumberUtils.scaleDouble(integral);
					if (findWloanTermInvestExists.size() == 0) { // 首次投资.
						String recommondUserPhone = user.getRecommendUserPhone();
						if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
							UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
							String recommondUserId = recommondUserInfo.getId();

							// 推荐人积分账户
							UserBounsPoint recommondUserBounsPoint = userBounsPointService.getUserBounsPoint(recommondUserId);
							Integer recommondUserScore = 0;
							if (recommondUserBounsPoint != null) {
								recommondUserScore = recommondUserBounsPoint.getScore(); // 账户积分
								recommondUserScore = recommondUserScore + 100; // 积分+100
								recommondUserBounsPoint.setScore(recommondUserScore);
								int recommondUserBounsPointFlag = userBounsPointService.update(recommondUserBounsPoint);
								if (recommondUserBounsPointFlag == 1) {
									logger.info("三级关系：\t推荐人积分账户更新成功");
								} else {
									logger.info("三级关系：\t推荐人积分账户更新失败");
								}
							}

							// 1.为推荐人赠送100积分，新增积分历史记录.
							UserBounsHistory userBounsHistory_one = new UserBounsHistory();
							userBounsHistory_one.setId(IdGen.uuid());
							userBounsHistory_one.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_one.setAmount(100D);
							userBounsHistory_one.setCreateDate(new Date());
							userBounsHistory_one.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_one.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							userBounsHistory_one.setCurrentAmount(recommondUserScore.toString()); // 当前推荐人账户剩余积分
							int recommondUserBounsHistoryFlag = userBounsHistoryService.insert(userBounsHistory_one);
							if (recommondUserBounsHistoryFlag == 1) {
								logger.info("三级关系：\t推荐人积分流水新增成功");
							} else {
								logger.info("三级关系：\t推荐人积分流水新增失败");
							}

							// 2.同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_two = new UserBounsHistory();
							userBounsHistory_two.setId(IdGen.uuid());
							userBounsHistory_two.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_two.setAmount(Double.valueOf(integral.intValue()));
							userBounsHistory_two.setCreateDate(new Date());
							userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							recommondUserScore = recommondUserScore + integral.intValue(); // 赠送投资人所获积分的5%
							userBounsHistory_two.setCurrentAmount(recommondUserScore.toString());
							int recommondUserBounsHistoryTwo = userBounsHistoryService.insert(userBounsHistory_two);
							// 推荐人积分流水新增成功
							if (recommondUserBounsHistoryTwo == 1) {
								logger.info("三级关系：\t推荐人积分流水新增成功");
								if (recommondUserBounsPoint != null) {
									recommondUserBounsPoint.setScore(recommondUserScore);
								}
								int recommondUserBounsPointFlag = userBounsPointService.update(recommondUserBounsPoint);
								if (recommondUserBounsPointFlag == 1) {
									logger.info("三级关系：\t推荐人积分账户更新成功");
								} else {
									logger.info("三级关系：\t推荐人积分账户更新失败");
								}
							} else {
								logger.info("三级关系：\t推荐人积分流水新增失败");
							}
						} else if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {

							UserBounsPoint parentUserBounsPoint = userBounsPointService.getUserBounsPoint(distribution.getParentId());
							Integer parentScore = 0;
							if (parentUserBounsPoint != null) {
								parentScore = parentUserBounsPoint.getScore(); // 账户积分
								parentScore = parentScore + 100; // 积分+100
								parentUserBounsPoint.setScore(parentScore);
							}
							int parentUserBounsPointUpdateFlag = userBounsPointService.update(parentUserBounsPoint);
							if (parentUserBounsPointUpdateFlag == 1) {
								logger.info("三级关系：\t推荐人积分账户更新成功");
							} else {
								logger.info("三级关系：\t推荐人积分账户更新失败");
							}

							// 1.为推荐人赠送100积分，新增积分历史记录.
							UserBounsHistory userBounsHistory_one = new UserBounsHistory();
							userBounsHistory_one.setId(IdGen.uuid());
							userBounsHistory_one.setUserId(distribution.getParentId()); // 推荐人ID.
							userBounsHistory_one.setAmount(100D);
							userBounsHistory_one.setCreateDate(new Date());
							userBounsHistory_one.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_one.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							userBounsHistory_one.setCurrentAmount(parentScore.toString()); // 当前剩余积分
							int userBounsHistoryOneUpdateFlag = userBounsHistoryService.insert(userBounsHistory_one);
							if (userBounsHistoryOneUpdateFlag == 1) {
								logger.info("三级关系：\t推荐人积分流水新增成功");
							} else {
								logger.info("三级关系：\t推荐人积分流水新增失败");
							}

							// 2.同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_two = new UserBounsHistory();
							userBounsHistory_two.setId(IdGen.uuid());
							userBounsHistory_two.setUserId(distribution.getParentId()); // 推荐人ID.
							userBounsHistory_two.setAmount(Double.valueOf(integral.intValue()));
							userBounsHistory_two.setCreateDate(new Date());
							userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							parentScore = parentScore + integral.intValue();
							userBounsHistory_two.setCurrentAmount(parentScore.toString()); // 赠送投资人所获积分的5%
							int userBounsHistoryTwoInsertFlag = userBounsHistoryService.insert(userBounsHistory_two);
							if (userBounsHistoryTwoInsertFlag == 1) {
								logger.info("三级关系：\t推荐人积分流水新增成功");
								if (parentUserBounsPoint != null) {
									parentUserBounsPoint.setScore(parentScore);
								}
								int userBounsPointUpdateFlag = userBounsPointService.update(parentUserBounsPoint);
								if (userBounsPointUpdateFlag == 1) {
									logger.info("三级关系：\t推荐人积分账户更新成功");
								} else {
									logger.info("三级关系：\t推荐人积分账户更新失败");
								}
							} else {
								logger.info("三级关系：\t推荐人积分流水新增失败");
							}
						}
					} else { // 再次投资(二次投资及以后的所有投资).
						String recommondUserPhone = user.getRecommendUserPhone();
						if (recommondUserPhone != null && !recommondUserPhone.equals("")) {
							UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
							String recommondUserId = recommondUserInfo.getId();

							// 推荐人积分账户
							UserBounsPoint recommondUserBounsPoint = userBounsPointService.getUserBounsPoint(recommondUserId);
							Integer recommondUserBounsPointScore = 0;
							if (recommondUserBounsPoint != null) {
								recommondUserBounsPointScore = recommondUserBounsPoint.getScore(); // 账户积分
								recommondUserBounsPointScore = recommondUserBounsPointScore + integral.intValue(); // 积分+出借人所获积分的5%
								recommondUserBounsPoint.setScore(recommondUserBounsPointScore);
							}

							int recommondUserBounsPointUpdateFlag = userBounsPointService.update(recommondUserBounsPoint);
							if (recommondUserBounsPointUpdateFlag == 1) {
								logger.info("三级关系：\t推荐人积分账户更新成功");
							} else {
								logger.info("三级关系：\t推荐人积分账户更新失败");
							}

							// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_two = new UserBounsHistory();
							userBounsHistory_two.setId(IdGen.uuid());
							userBounsHistory_two.setUserId(recommondUserId); // 推荐人ID.
							userBounsHistory_two.setAmount(Double.valueOf(integral.intValue()));
							userBounsHistory_two.setCreateDate(new Date());
							userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							userBounsHistory_two.setCurrentAmount(recommondUserBounsPointScore.toString()); // 当前剩余积分
							int mark = userBounsHistoryService.insert(userBounsHistory_two);
							if (mark == 1) {
								logger.info("三级关系：\t推荐人积分积分流水新增成功");
							} else {
								logger.info("三级关系：\t推荐人积分积分流水新增失败");
							}
						} else if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {

							// 推荐人积分账户
							UserBounsPoint recommondUserBounsPoint = userBounsPointService.getUserBounsPoint(distribution.getParentId());
							Integer recommondUserBounsPointScore = 0;
							if (recommondUserBounsPoint != null) {
								recommondUserBounsPointScore = recommondUserBounsPoint.getScore(); // 账户积分
								recommondUserBounsPointScore = recommondUserBounsPointScore + integral.intValue(); // 积分+出借人所获积分的5%
								recommondUserBounsPoint.setScore(recommondUserBounsPointScore);
							}

							int recommondUserBounsPointUpdateFlag = userBounsPointService.update(recommondUserBounsPoint);
							if (recommondUserBounsPointUpdateFlag == 1) {
								logger.info("三级关系：\t推荐人积分账户更新成功");
							} else {
								logger.info("三级关系：\t推荐人积分账户更新失败");
							}

							// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
							UserBounsHistory userBounsHistory_two = new UserBounsHistory();
							userBounsHistory_two.setId(IdGen.uuid());
							userBounsHistory_two.setUserId(distribution.getParentId()); // 推荐人ID.
							userBounsHistory_two.setAmount(Double.valueOf(integral.intValue()));
							userBounsHistory_two.setCreateDate(new Date());
							userBounsHistory_two.setTransId(user.getId()); // 当前客户ID.
							userBounsHistory_two.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
							userBounsHistory_two.setCurrentAmount(recommondUserBounsPointScore.toString()); // 当前剩余积分
							int mark = userBounsHistoryService.insert(userBounsHistory_two);
							if (mark == 1) {
								logger.info("三级关系：\t推荐人积分积分流水新增成功");
							} else {
								logger.info("三级关系：\t推荐人积分积分流水新增失败");
							}
						}
					}

					// N2.释放项目锁
					if (JedisUtils.releaseLock(proKey, lockProValue)) {
						logger.info("项目锁已释放");
						logger.info("项目锁已解锁=====" + System.currentTimeMillis());
					}

					// N3.账户锁
					accKey = "ACC" + userAccount.getId();
					lockAccValue = JedisUtils.lockWithTimeout(accKey, 4000, 2000);// 账户锁
					Double vouAmount = 0d;// 抵用券使用金额

					if (lockAccValue != null && !lockAccValue.equals("")) {
						logger.info("=======账户锁定开始" + System.currentTimeMillis());
						/**
						 * **********抵用券状态变更*************
						 */
						// 记录抵用券流水
						AUserAwardsHistory aUserAwards = new AUserAwardsHistory();
						aUserAwards.setBidId(wloanTermInvest.getId());
						aUserAwards.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
						List<AUserAwardsHistory> aUserAwardsHistoryList = aUserAwardsHistoryService.findVouchers(aUserAwards);
						logger.info("共使用了" + aUserAwardsHistoryList.size() + "张抵用券");
						if (aUserAwardsHistoryList != null && aUserAwardsHistoryList.size() > 0) {
							userAccount.setAvailableAmount(availableAmount - amount);
							logger.info("减去出借金额后的可用余额为" + userAccount.getAvailableAmount());
							int time = 1;
							for (AUserAwardsHistory aUserAwardsHistory : aUserAwardsHistoryList) {
								Double voucherAmount = aUserAwardsHistory.getaVouchersDic().getAmount();// 抵用券金额
								vouAmount = vouAmount + voucherAmount;
								Double newAvaAmount = userAccount.getAvailableAmount() + voucherAmount;
								logger.info("[抵用券流水记录开始时]账户可用余额" + newAvaAmount);
								/**
								 * 请求返利接口调用
								 */
								logger.info("返利抵用券处理开始==============>>>>>");
								Map<String, String> redPacketMap = redPacketService.giveRedPacketWeb(user.getName(), "8003", voucherAmount, aUserAwardsHistory.getId());
								logger.info("返利抵用券处理结果" + redPacketMap.get("respSubCode"));
								if (redPacketMap.get("respSubCode").equals("000100")) {
									logger.info("[抵用券" + voucherAmount + "元]红包受理成功");
									logger.info("抵用券状态变更为已使用开始");
									aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
									aUserAwardsHistory.setBidId(orderId);
									aUserAwardsHistory.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * ++time));
									int updateVoucher = userVouchersDao.update(aUserAwardsHistory);
									if (updateVoucher == 1) {
										logger.info(this.getClass().getName() + "——————更改抵用券状态成功");
									}
									logger.info("抵用券状态变更为已使用结束");
									// 保存客户使用抵用券流水记录
									logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录开始[处理中]");
									CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
									userTransDetail.setId(IdGen.uuid()); // 主键ID.
									userTransDetail.setTransId(aUserAwardsHistory.getId()); // 客户抵用券记录ID.
									userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
									userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
									userTransDetail.setTransDate(new Date(System.currentTimeMillis() + 1000 * ++time)); // 抵用券使用时间.
									userTransDetail.setTrustType(UserTransDetailService.trust_type10); // 优惠券.
									userTransDetail.setAmount(aUserAwardsHistory.getaVouchersDic().getAmount()); // 优惠券金额.
									userTransDetail.setAvaliableAmount(newAvaAmount); // 当前可用余额.
									userTransDetail.setInOutType(UserTransDetailService.in_type); // 优惠券收入.
									userTransDetail.setRemarks("抵用券"); // 备注信息.
									userTransDetail.setState(UserTransDetailService.tran_type1); // 流水状态，成功.
									int userVoucherDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
									if (userVoucherDetailFlag == 1) {
										logger.info(this.getClass().getName() + "——————保存客户使用抵用券流水记录成功[处理中]");
									}
									userAccount.setAvailableAmount(newAvaAmount);
									logger.info("[抵用券流水记录完成时]账户可用余额" + newAvaAmount);
								}
								logger.info("<<<==================返利抵用券处理结束");
							}
						}

						/**
						 * **************出借账户变更************************
						 */
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
						CgbUserAccount userAccount1 = cgbUserAccountDao.getUserAccountInfo(wloanTermInvest.getUserId());// 账户信息
						logger.info("更改账户前,账户总额为" + userAccount1.getTotalAmount() + "###可用余额为" + userAccount1.getAvailableAmount());
						userAccount1.setTotalAmount(userAccount1.getTotalAmount() + vouAmount + interest); // 账户总额
						userAccount1.setAvailableAmount(userAccount1.getAvailableAmount() - amount + vouAmount);// 可用余额
						userAccount1.setRegularDuePrincipal(userAccount1.getRegularDuePrincipal() + amount); // 定期代收本金
						userAccount1.setRegularDueInterest(userAccount1.getRegularDueInterest() + interest); // 定期代收收益
						userAccount1.setRegularTotalAmount(userAccount1.getRegularTotalAmount() + amount); // 定期投资总额
						logger.info("更改账户后,账户总额为" + userAccount1.getTotalAmount() + "###可用余额为" + userAccount1.getAvailableAmount());
						int updateAccount = cgbUserAccountService.updateUserAccountInfo(userAccount1);
						if (updateAccount == 1) {
							logger.info(this.getClass().getName() + "——————更改账户信息成功");
							if (JedisUtils.releaseLock(accKey, lockAccValue)) {
								logger.info("账户锁已释放");
								logger.info("=======账户已解锁=====" + System.currentTimeMillis());
							}
							/**
							 * ***********出借记录状态变更**************
							 */
							wloanTermInvest.setState(NewInvestService.WLOAN_TERM_INVEST_STATE_1); // 出借记录状态变更为成功
							wloanTermInvest.setVoucherAmount(vouAmount);
							wloanTermInvest.setRemarks("使用" + vouAmount + "元抵用券");
							int i = wloanTermInvestService.updateWloanTermInvest(wloanTermInvest);
							if (i > 0) {
								logger.info("[用户出借成功]" + orderId + "状态更新成功");
								logger.info("=====记录客户出借交易流水开始======"); // 保存客户流水记录
								CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
								userTransDetail.setId(IdGen.uuid()); // 主键ID.
								userTransDetail.setTransId(orderId); // 客户投资记录ID.
								userTransDetail.setUserId(wloanTermInvest.getUserInfo().getId()); // 客户账号ID.
								userTransDetail.setAccountId(wloanTermInvest.getUserInfo().getAccountId()); // 客户账户ID.
								userTransDetail.setTransDate(new Date()); // 投资交易时间.
								userTransDetail.setTrustType(UserTransDetailService.trust_type3); // 定期投资.
								userTransDetail.setAmount(amount); // 投资交易金额.
								userTransDetail.setAvaliableAmount(availableAmount - amount); // 当前可用余额.
								userTransDetail.setInOutType(UserTransDetailService.out_type); // 投资支出.
								userTransDetail.setRemarks("出借"); // 备注信息.
								userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
								int userTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
								if (userTransDetailFlag == 1) {
									logger.info(this.getClass().getName() + "——————保存客户出借流水记录成功");
								}
							}
						}

						// N4.发送站内信
						StationLetter letter = new StationLetter();
						letter.setUserId(wloanTermInvest.getUserInfo().getId());
						letter.setLetterType(StationLettersService.LETTER_TYPE_WLOAN);
						letter.setTitle("您已成功向" + project.getName() + "项目出借资金:" + amount + ",借款周期:" + project.getSpan() + "天。");
						letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎");
						letter.setState(StationLettersService.LETTER_STATE_UNREAD);
						letter.setSendTime(new Date());
						stationLettersService.save(letter);
						// N5.发送短信,微信
						weixinSendTempMsgService.sendInvestInfoMsg(wloanTermInvest, project);
						result = 1;

						// 在return之前，启动线程
						logger.info("项目流转状态：\t" + project.getState() + "\t满标");
						// logger.info("项目实际融资金额：\t" +
						// project.getCurrentAmount());
						// logger.info("项目融资金额：\t" + project.getAmount());
						if (WloanTermProjectService.FULL.equals(project.getState())) {
							logger.info("new Thread() start...");
							// 线程进行还款计划及合同生成
							new Thread() {

								public void run() {

									try {
										// 生成还款计划及合同的生成
										createPlanAndContract(project);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}.start();
							logger.info("new Thread() end...");
						}
						return result;
					}
				} else {
					result = 0;
					return result;
				}

				long endTime = System.currentTimeMillis();
				logger.info("计时结束 耗时：{" + DateUtils.formatDateTime(endTime - beginTime) + "}");

			}
		} catch (Exception e) {
			// 立即释放项目锁
			if (JedisUtils.releaseLock(proKey, lockProValue)) {
				logger.info("[取消投资]项目锁已释放");
				logger.info("[取消投资]项目锁已解锁=====" + System.currentTimeMillis());
			}
			// 立即释放账户锁
			if (JedisUtils.releaseLock(accKey, lockAccValue)) {
				logger.info("账户锁已释放");
				logger.info("=======账户已解锁=====" + System.currentTimeMillis());
			}
			return result;
		}
		return result;

	}
}
