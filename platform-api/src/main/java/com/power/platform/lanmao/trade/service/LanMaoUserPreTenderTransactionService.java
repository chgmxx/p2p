package com.power.platform.lanmao.trade.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.ZtmgPartnerPlatformService;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IsHolidayOrBirthday;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.trade.pojo.CancelPreTransaction;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.more.stationletter.entity.StationLetter;
import com.power.platform.more.stationletter.service.StationLettersService;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.UserInvestWebService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Service("lanMaoUserPreTenderTransactionService")
public class LanMaoUserPreTenderTransactionService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoUserPreTenderTransactionService.class);

	/**
	 * 标的分布式锁，过期时间，2000毫秒
	 */
	private final static int EXPIRE_TIME = 2000;

	@Autowired
	private LanMaoPreTransactionService lanMaoPreTransactionService;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private StationLettersService stationLettersService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private UserInvestWebService userInvestWebService;
	@Autowired
	private ZtmgPartnerPlatformService ztmgPartnerPlatformService;
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Autowired
	private LevelDistributionDao levelDistributionDao;
	@Autowired
	private LmTransactionDao lmTransactionDao;

	/**
	 * 
	 * methods: userPreTenderTransactionNotify <br>
	 * description: 出借预处理，异步通知 <br>
	 * author: Roy <br>
	 * date: 2019年9月30日 下午3:32:42
	 * 
	 * @param input
	 * @return
	 */
	public boolean userPreTenderTransactionNotify(NotifyVo input) {

		boolean flag = false;
		try {
			JSONObject jsonObject = JSONObject.parseObject(input.getRespData());
			// String bizType = jsonObject.getString("bizType"); // bizType：预处理业务类型，确定此次业务逻辑
			String code = jsonObject.getString("code");
			String status = jsonObject.getString("status"); // 业务处理状态（处理失败INIT；处理成功SUCCESS），平台可根据非SUCCESS状态做相应处理，处理失败时可参考错误码及描述
			String errorCode = jsonObject.getString("errorCode");
			String errorMessage = jsonObject.getString("errorMessage");
			String requestNo = jsonObject.getString("requestNo");
			WloanTermInvest invest = wloanTermInvestDao.get(requestNo);
			// 懒猫交易留存
			LmTransaction lt = null;
			if (null != invest) {
				if (UserInvestWebService.WLOAN_TERM_INVEST_STATE_0.equals(invest.getState())) {
					if ("0".equals(code) && "SUCCESS".equals(status)) { // 调用成功且业务处理成功
						boolean projectProgressFlag = false; // 标的募集进度更新标志位
						boolean projectFullFlag = false; // 标的满标标志位
						String proSn = invest.getWloanTermProject() != null ? invest.getWloanTermProject().getSn() : "";
						String projectId = invest.getProjectId();
						Double investAmount = invest.getAmount(); // 出借金额
						Double voucherAmount = invest.getVoucherAmount(); // 使用红包合计金额
						Double freezeInvestAmount = NumberUtils.subtract(investAmount, voucherAmount); // 实际出借金额
						String lockKey = "project_id:" + projectId; // 标的id，作为标的分布式锁的lock key
						// 标的信息，获取分布式锁
						if (JedisUtils.tryGetDistributedLock(lockKey, projectId, EXPIRE_TIME)) {
							logger.info("分布式锁，获取成功 ......");
							/**
							 * 出借人账户可用余额判断
							 */
							CgbUserAccount cgbUserAccount = cgbUserAccountDao.getUserAccountInfo(invest.getUserId());
							if (cgbUserAccount != null) {
								if (investAmount > cgbUserAccount.getAvailableAmount()) {
									logger.info("出借金额:{}大于账户可用余额:{}", NumberUtils.scaleDoubleStr(investAmount), NumberUtils.scaleDoubleStr(cgbUserAccount.getAvailableAmount()));
									// 分布式锁，释放
									if (JedisUtils.releaseDistributedLock(lockKey, projectId)) {
										logger.info("分布式锁，释放成功 ......");
									} else {
										logger.info("分布式锁，释放失败 ......");
									}
									// 取消预处理参数列表
									String cancelPreTransactionRequestNo = IdGen.uuid();
									CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
									cancelPreTransaction.setRequestNo(cancelPreTransactionRequestNo); // 请求的流水号
									cancelPreTransaction.setPreTransactionNo(requestNo);// 出借预处理流水号
									cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(freezeInvestAmount)); // 取消金额
									// 取消预处理
									Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
									if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status"))) {
										// 出借失败
										invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
										invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
										invest.setEndDate(new Date());
										invest.setUpdateDate(new Date());
										int updateInvestFlag = wloanTermInvestDao.update(invest);
										logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
										// 出借预处理取消日志记录
										lt = new LmTransaction();
										lt.setId(IdGen.uuid());
										lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
										lt.setRequestNo(cancelPreTransactionRequestNo);
										lt.setPlatformUserNo(invest.getUserId());
										lt.setCode(cancelPreTransactionMap.get("code"));
										lt.setStatus(cancelPreTransactionMap.get("status"));
										lt.setRemarks("出借预处理取消");
										lt.setProjectNo(proSn);// 标的编号
										lt.setCreateDate(new Date());
										lt.setUpdateDate(new Date());
										int insertLtFlag = lmTransactionDao.insert(lt);
										logger.info("出借预处理取消，记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");

										// 业务处理完毕
										flag = true;
										return flag;
									}
								}
							}
							/**
							 * 标的募集剩余金额判断
							 */
							WloanTermProject project = wloanTermProjectDao.get(projectId);
							if (null != project) {
								Double projectAmount = project.getAmount(); // 标的募集金额
								Double currentAmount = project.getCurrentAmount(); // 标的当前募集金额
								// 募集剩余金额.
								Double balanceAmount = NumberUtils.subtract(projectAmount, currentAmount);
								if (investAmount > balanceAmount) {// 出借金额应小于等于标的剩余金额
									logger.info("出借金额:{}大于标的募集剩余金额:{}", NumberUtils.scaleDoubleStr(investAmount), NumberUtils.scaleDoubleStr(balanceAmount));
									// 分布式锁，释放
									if (JedisUtils.releaseDistributedLock(lockKey, projectId)) {
										logger.info("分布式锁，释放成功 ......");
									} else {
										logger.info("分布式锁，释放失败 ......");
									}
									// 取消预处理参数列表
									String cancelPreTransactionRequestNo = IdGen.uuid();
									CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
									cancelPreTransaction.setRequestNo(cancelPreTransactionRequestNo); // 请求的流水号
									cancelPreTransaction.setPreTransactionNo(requestNo);// 出借预处理流水号
									cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(freezeInvestAmount)); // 取消金额
									// 取消预处理
									Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
									if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status"))) {
										// 出借失败
										invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
										invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
										invest.setEndDate(new Date());
										invest.setUpdateDate(new Date());
										int updateInvestFlag = wloanTermInvestDao.update(invest);
										logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
										// 出借预处理取消日志记录
										lt = new LmTransaction();
										lt.setId(IdGen.uuid());
										lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
										lt.setRequestNo(cancelPreTransactionRequestNo);
										lt.setPlatformUserNo(invest.getUserId());
										lt.setCode(cancelPreTransactionMap.get("code"));
										lt.setStatus(cancelPreTransactionMap.get("status"));
										lt.setRemarks("出借预处理取消");
										lt.setProjectNo(proSn);// 标的编号
										lt.setCreateDate(new Date());
										lt.setUpdateDate(new Date());
										int insertLtFlag = lmTransactionDao.insert(lt);
										logger.info("出借预处理取消，记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");

										// 业务处理完毕
										flag = true;
										return flag;
									}
								} else {
									// 判断此项目是否满标，更新满标标志位
									if (projectAmount.equals(NumberUtils.scaleDouble(NumberUtils.add(currentAmount, investAmount)))) {
										projectFullFlag = true; // 更新满标标志位
										// 标的当前募集金额更新
										int updateCurrentAmountFlag = wloanTermProjectDao.updateCurrentAmountAndCurrentRealAmountById(projectId, investAmount);
										if (updateCurrentAmountFlag == 1) {
											logger.info("标的当前募集金额更新成功");
											projectProgressFlag = true;
										} else {
											logger.info("标的当前募集金额更新失败");
										}
										// 满标，标的状态更新
										project.setState(WloanTermProjectService.FULL);
										project.setFullDate(new Date());
										project.setUpdateDate(new Date());
										int updateStateFlag = wloanTermProjectDao.updateStateById(project);
										logger.info("满标，标的状态更新:{}", updateStateFlag == 1 ? "成功" : "失败");
									} else {
										// 标的当前募集金额更新
										int updateCurrentAmountFlag = wloanTermProjectDao.updateCurrentAmountAndCurrentRealAmountById(projectId, investAmount);
										if (updateCurrentAmountFlag == 1) {
											logger.info("标的当前募集金额更新成功");
											projectProgressFlag = true;
										} else {
											logger.info("标的当前募集金额更新失败");
										}
									}
								}

								// 分布式锁，释放
								if (JedisUtils.releaseDistributedLock(lockKey, projectId)) {
									logger.info("分布式锁，释放成功 ......");
								} else {
									logger.info("分布式锁，释放失败 ......");
								}
							}
						} else {
							// 获取锁失败，提醒出借人，排队人数较多，请您稍后出借
							// 取消预处理参数列表
							String cancelPreTransactionRequestNo = IdGen.uuid();
							CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
							cancelPreTransaction.setRequestNo(cancelPreTransactionRequestNo); // 请求的流水号
							cancelPreTransaction.setPreTransactionNo(requestNo);// 出借预处理流水号
							cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(freezeInvestAmount)); // 取消金额
							// 取消预处理
							Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
							if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status"))) {
								// 出借失败
								invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
								invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
								invest.setEndDate(new Date());
								invest.setUpdateDate(new Date());
								int updateInvestFlag = wloanTermInvestDao.update(invest);
								logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
								// 出借预处理取消日志记录
								lt = new LmTransaction();
								lt.setId(IdGen.uuid());
								lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
								lt.setRequestNo(cancelPreTransactionRequestNo);
								lt.setPlatformUserNo(invest.getUserId());
								lt.setCode(cancelPreTransactionMap.get("code"));
								lt.setStatus(cancelPreTransactionMap.get("status"));
								lt.setRemarks("出借预处理取消");
								lt.setProjectNo(proSn);// 标的编号
								lt.setCreateDate(new Date());
								lt.setUpdateDate(new Date());
								int insertLtFlag = lmTransactionDao.insert(lt);
								logger.info("出借预处理取消，记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");

								// 业务处理完毕
								flag = true;
								return flag;
							}
						}
						if (projectProgressFlag) { // 标的募集进度已刷新
							if (userPreTenderTransaction(invest, projectFullFlag)) {
								logger.info("investId:{}，出借业务处理成功......", requestNo);

								// 业务处理完毕
								flag = true;
								return flag;
							}
						}
					} else {
						logger.info("errorCode:{},errorMessage:{}", errorCode, errorMessage);
						// 预处理出借失败 ...
						invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
						invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
						invest.setEndDate(new Date());
						invest.setUpdateDate(new Date());
						int updateInvestFlag = wloanTermInvestDao.update(invest);
						logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
					}
				} else if (UserInvestWebService.WLOAN_TERM_INVEST_STATE_1.equals(invest.getState())) {
					// 不做任何处理
					flag = true;
					logger.info("出借同步回调业务已处理完毕，异步通知不做处理 ......");
				} else if (UserInvestWebService.WLOAN_TERM_INVEST_STATE_2.equals(invest.getState())) {
					// 不做任何处理
					flag = true;
					logger.info("出借同步回调业务已处理完毕，异步通知不做处理 ......");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 
	 * methods: userPreTenderTransaction <br>
	 * description: 出借预处理，逻辑处理 <br>
	 * author: Roy <br>
	 * date: 2019年9月29日 下午5:23:54
	 * 
	 * @param invest
	 *            出借记录详情
	 * @param projectFullFlag
	 *            满标标志位
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean userPreTenderTransaction(WloanTermInvest invest, boolean projectFullFlag) {

		boolean flag = false;

		String projectId = "";
		String userId = "";
		String accountId = "";
		String recommendUserId = "";
		String recommendUserPhone = "";
		String certificateNo = "";
		String investUserPhone = "";
		Integer span = 0;
		String name = "";
		String projectProductType = "";
		Double availableAmount = 0D;
		Double investAmount = 0D;
		Double voucherAmount = 0D;
		Double realInvestAmount = 0D;
		UserInfo userInfo = null;
		WloanTermProject project = null;
		long currentTimeMillis = System.currentTimeMillis();
		try {
			// 出借信息
			if (null != invest) {
				String investId = invest.getId(); // 出借记录id
				userId = invest.getUserId(); // 用户帐号id
				userInfo = userInfoDao.getCgb(userId);
				// 帐号信息
				if (null != userInfo) {
					accountId = userInfo.getAccountId();
					recommendUserId = userInfo.getRecommendUserId();
					recommendUserPhone = userInfo.getRecommendUserPhone();
					certificateNo = userInfo.getCertificateNo();
					investUserPhone = userInfo.getName();
				}
				projectId = invest.getProjectId(); // 标的id
				// 标的信息
				project = wloanTermProjectDao.get(projectId);
				if (null != project) {
					span = project.getSpan();
					name = project.getName();
					projectProductType = project.getProjectProductType();
				}
				investAmount = invest.getAmount(); // 出借金额
				voucherAmount = invest.getVoucherAmount(); // 抵用券金额
				realInvestAmount = NumberUtils.subtract(investAmount, voucherAmount); // 实际账户扣除金额
				/**
				 * 出借记录
				 */
				invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_1);
				invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_1);
				invest.setEndDate(new Date(currentTimeMillis));
				invest.setUpdateDate(new Date(currentTimeMillis));
				int updateInvestFlag = wloanTermInvestDao.update(invest);
				if (updateInvestFlag == 1) {
					logger.info("出借记录更新成功");
					/**
					 * 账户信息
					 */
					CgbUserAccount cgbUserAccount = cgbUserAccountDao.getUserAccountInfo(userId);
					if (null != cgbUserAccount) {
						availableAmount = cgbUserAccount.getAvailableAmount();
						int updateCgbUserAccountFlag = cgbUserAccountDao.updateTenderById(NumberUtils.scaleDouble(invest.getInterest()), NumberUtils.scaleDouble(voucherAmount), NumberUtils.scaleDouble(investAmount), NumberUtils.scaleDouble(realInvestAmount), cgbUserAccount.getId());
						logger.info("账户信息更新:{}", updateCgbUserAccountFlag == 1 ? "成功" : "失败");
					}
					/**
					 * 出借流水
					 */
					CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
					userTransDetail.setId(IdGen.uuid()); // 主键id
					userTransDetail.setTransId(investId); // 出借记录id
					userTransDetail.setUserId(userId); // 帐号id
					userTransDetail.setAccountId(accountId); // 账户id
					userTransDetail.setTransDate(new Date(currentTimeMillis)); // 出借交易时间
					userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_3); // 定期出借
					userTransDetail.setAmount(investAmount); // 出借交易金额
					availableAmount = NumberUtils.subtract(availableAmount, investAmount);
					userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额
					userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出
					userTransDetail.setRemarks("出借"); // 备注信息
					userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态-成功
					int insertUserTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
					logger.info("出借交易流水插入:{}", insertUserTransDetailFlag == 1 ? "成功" : "失败");
					/**
					 * 抵用券流水
					 */
					AUserAwardsHistory userAwardsHistory = new AUserAwardsHistory();
					userAwardsHistory.setBidId(investId);
					userAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
					List<AUserAwardsHistory> voucherList = aUserAwardsHistoryService.findVouchers(userAwardsHistory);
					if (null != voucherList && voucherList.size() > 0) {
						for (int i = 0; i < voucherList.size(); i++) {
							currentTimeMillis = currentTimeMillis + 1000;
							AUserAwardsHistory voucher = voucherList.get(i);
							voucher.setUpdateDate(new Date(currentTimeMillis));
							voucher.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
							voucher.setBidId(investId);
							int updateVoucherFlag = aUserAwardsHistoryDao.update(voucher);
							if (updateVoucherFlag == 1) {
								logger.info("使用抵用券状态更新成功......");
								CgbUserTransDetail voucherUserTransDetail = new CgbUserTransDetail();
								voucherUserTransDetail.setId(IdGen.uuid()); // 主键id
								voucherUserTransDetail.setTransId(voucher.getId()); // 抵用券id
								voucherUserTransDetail.setUserId(userId); // 帐号id
								voucherUserTransDetail.setAccountId(accountId); // 账户id
								voucherUserTransDetail.setTransDate(new Date(currentTimeMillis)); // 抵用券使用时间.
								voucherUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 抵用券.
								voucherUserTransDetail.setAmount(NumberUtils.scaleDouble(voucher.getaVouchersDic().getAmount())); // 抵用券金额.
								availableAmount = NumberUtils.add(availableAmount, NumberUtils.scaleDouble(voucher.getaVouchersDic().getAmount()));
								voucherUserTransDetail.setAvaliableAmount(NumberUtils.scaleDouble(availableAmount)); // 当前可用余额.
								voucherUserTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入
								voucherUserTransDetail.setRemarks("抵用券"); // 备注信息.
								voucherUserTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
								int userVoucherDetailFlag = cgbUserTransDetailService.insert(voucherUserTransDetail);
								logger.info("抵用券流水记录:{}", userVoucherDetailFlag == 1 ? "成功" : "失败");
							} else {
								logger.info("使用抵用券状态更新失败......");
							}
						}
					}
					/**
					 * 出借人及推荐人获取积分规则
					 */
					giveInvestBouns(userId, investUserPhone, certificateNo, recommendUserId, recommendUserPhone, investId, investAmount, projectProductType, span);
					/**
					 * 发送站内信
					 */
					StationLetter letter = new StationLetter();
					letter.setUserId(userId);
					letter.setLetterType(StationLettersService.LETTER_TYPE_WLOAN);
					letter.setTitle("您已成功向" + name + "项目出借资金：" + NumberUtils.scaleDoubleStr(investAmount) + "元，借款周期:" + span + "天。");
					letter.setBody("感谢您对平台一如既往的支持！市场有风险，出借需谨慎。");
					letter.setState(StationLettersService.LETTER_STATE_UNREAD);
					letter.setSendTime(new Date());
					stationLettersService.save(letter);
					/**
					 * 发送短消息及微信服务号消息
					 */
					weixinSendTempMsgService.sendInvestInfoMsg(invest, project);
					flag = true;
				} else {
					logger.info("出借记录更新失败");
				}
			}

			/**
			 * 满标后续业务处理
			 */
			if (projectFullFlag) { // 满标重新生成还款计划
				wloanTermProjectPlanService.deleteByProjectId(projectId); // 删除旧的还款计划
				if ("SUCCESS".equals(wloanTermProjectPlanService.newInitWloanTermProjectPlan(project))) {
					logger.info("满标，标的还款计划重新初始化成功......");
					flag = true;
					/**
					 * 启动新的线程，进行出借还款计划及出借协议的生成
					 */
					logger.info("启动新的线程，进行出借还款计划及出借协议的生成 ......");
					// 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程，线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程
					ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
					//
					final WloanTermProject cachedThreadPoolProject = wloanTermProjectDao.get(projectId);
					cachedThreadPool.execute(new Runnable() {

						@Override
						public void run() {

							try {
								userInvestWebService.createPlanAndContract(cachedThreadPoolProject);
							} catch (Exception e) {
								logger.info("error:{}" + e.getMessage());
								e.printStackTrace();
							}
						}
					});
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("error:{}", e.getMessage());
		}
		return flag;
	}

	/**
	 * 
	 * methods: giveInvestBouns <br>
	 * description: 获取出借回报（积分） <br>
	 * author: Roy <br>
	 * date: 2019年10月2日 上午10:24:35
	 * 
	 * @param investUserPhone
	 *            出借用户手机号码
	 * @param userId
	 *            出借人帐号id
	 * @param certificateNo
	 *            出借人身份证号码
	 * @param recommendUserId
	 *            推荐人id
	 * @param recommendUserPhone
	 *            推荐人手机号码
	 * @param investId
	 *            出借订单id
	 * @param investAmount
	 *            出借金额
	 * @param projectProductType
	 *            标的产品类型
	 * @param span
	 *            标的期限
	 */
	private void giveInvestBouns(String userId, String investUserPhone, String certificateNo, String recommendUserId, String recommendUserPhone, String investId, Double investAmount, String projectProductType, Integer span) {

		List<WloanTermInvest> sumInvestExists = wloanTermInvestDao.findWloanTermInvestExists(userId);
		int investNumber = 0; // 出借次数
		if (null != sumInvestExists) {
			investNumber = sumInvestExists.size();
		}
		/**
		 * 出借人获取出借积分((investAmount × (span / 30)) / 100)
		 */
		Double userBouns = NumberUtils.divide(NumberUtils.multiply(investAmount, NumberUtils.divide(Double.valueOf(span), 30D, 2)), 100D, 2);
		// 渠道推荐逻辑待定......
		// 正常出借
		if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(projectProductType)) { // 安心投类
			if (IsHolidayOrBirthday.isHoliday(certificateNo)) { // 生日及节假日1.5倍积分
				addBouns(userId, NumberUtils.multiply(userBouns, 1.5D), investId);
			} else { // 正常积分
				addBouns(userId, userBouns, investId);
			}
		} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(projectProductType)) { // 供应链类
			if (IsHolidayOrBirthday.isActivity()) { // 活动日双倍积分
				addBouns(userId, NumberUtils.multiply(userBouns, 2D), investId);
			} else {
				if (IsHolidayOrBirthday.isHoliday(certificateNo)) { // 生日及节假日1.5倍积分
					addBouns(userId, NumberUtils.multiply(userBouns, 1.5D), investId);
				} else { // 正常积分
					addBouns(userId, userBouns, investId);
				}
			}
		}
		/**
		 * 推荐人获取积分规则
		 */
		LevelDistribution levelDistribution = levelDistributionDao.selectByUserId(userId);// 客户每次投资，推荐人所获积分
		Double fivePercentBouns = NumberUtils.scaleDouble(NumberUtils.multiply(userBouns, 0.05D)); // 推荐人获取出借人出借积分的百分之五
		if (investNumber > 0) { // 二次及多次出借
			if (recommendUserPhone != null && !recommendUserPhone.equals("")) { // 推荐人手机
				// 判断推荐人手机不能为自己注册的手机号码
				if (!recommendUserPhone.equals(investUserPhone)) {
					UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendUserPhone);
					if (null != recommendUserInfo) {
						String recommendUserInfoId = recommendUserInfo.getId();
						// 推荐人积分账户
						UserBounsPoint recommendUserBounsPoint = userBounsPointService.getUserBounsPoint(recommendUserInfoId);
						if (recommendUserBounsPoint != null) {
							String addFivePercentBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(Double.valueOf(recommendUserBounsPoint.getScore()), fivePercentBouns));
							recommendUserBounsPoint.setScore(Double.valueOf(addFivePercentBounsStr).intValue());
							int updateRecommendUserBounsPointFlag = userBounsPointService.update(recommendUserBounsPoint);
							if (updateRecommendUserBounsPointFlag == 1) {
								logger.info("推荐人积分（出借人所获积分的百分之五）账户更新成功......");
								// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
								UserBounsHistory recommendUserBounsHistory = new UserBounsHistory();
								recommendUserBounsHistory.setId(IdGen.uuid());
								recommendUserBounsHistory.setUserId(recommendUserInfoId); // 推荐人id
								recommendUserBounsHistory.setAmount(fivePercentBouns);
								recommendUserBounsHistory.setCreateDate(new Date());
								recommendUserBounsHistory.setTransId(userId); // 用户帐号id
								recommendUserBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
								recommendUserBounsHistory.setCurrentAmount(addFivePercentBounsStr); // 当前积分余额
								int insertRecommendUserBounsHistoryFlag = userBounsHistoryService.insert(recommendUserBounsHistory);
								logger.info("推荐人积分（出借人所获积分的百分之五）流水插入:{}", insertRecommendUserBounsHistoryFlag == 1 ? "成功" : "失败");
							} else {
								logger.info("推荐人积分（出借人所获积分的百分之五）账户更新失败......");
							}
						}
					}
				}
			} else if (levelDistribution != null && levelDistribution.getParentId() != null && !levelDistribution.getParentId().equals("")) { // 三级关系邀请人
				// 推荐人积分账户
				UserBounsPoint recommendUserBounsPoint = userBounsPointService.getUserBounsPoint(levelDistribution.getParentId());
				if (recommendUserBounsPoint != null) {
					String addFivePercentBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(Double.valueOf(recommendUserBounsPoint.getScore()), fivePercentBouns));
					recommendUserBounsPoint.setScore(Double.valueOf(addFivePercentBounsStr).intValue());
					int updateRecommendUserBounsPointFlag = userBounsPointService.update(recommendUserBounsPoint);
					if (updateRecommendUserBounsPointFlag == 1) {
						logger.info("推荐人积分（出借人所获积分的百分之五）账户更新成功......");
						// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
						UserBounsHistory recommendUserBounsHistory = new UserBounsHistory();
						recommendUserBounsHistory.setId(IdGen.uuid());
						recommendUserBounsHistory.setUserId(levelDistribution.getParentId()); // 推荐人id
						recommendUserBounsHistory.setAmount(fivePercentBouns);
						recommendUserBounsHistory.setCreateDate(new Date());
						recommendUserBounsHistory.setTransId(userId); // 用户帐号id
						recommendUserBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
						recommendUserBounsHistory.setCurrentAmount(addFivePercentBounsStr); // 当前积分余额
						int insertRecommendUserBounsHistoryFlag = userBounsHistoryService.insert(recommendUserBounsHistory);
						logger.info("推荐人积分（出借人所获积分的百分之五）流水插入:{}", insertRecommendUserBounsHistoryFlag == 1 ? "成功" : "失败");
					} else {
						logger.info("推荐人积分（出借人所获积分的百分之五）账户更新失败......");
					}
				}
			}
		} else { // 出借人首次出借
			if (recommendUserPhone != null && !recommendUserPhone.equals("")) { // 推荐人手机
				/**
				 * 出借人首次出借，推荐人可获取100积分和出借人所获积分的百分之五
				 */
				// 判断推荐人手机不能为自己注册的手机号码
				if (!recommendUserPhone.equals(investUserPhone)) {
					long currentTimeMillis = System.currentTimeMillis();
					UserInfo recommendUserInfo = userInfoDao.getUserInfoByPhone(recommendUserPhone);
					if (null != recommendUserInfo) {
						String recommendUserInfoId = recommendUserInfo.getId();
						// 推荐人积分账户
						UserBounsPoint recommendUserBounsPoint = userBounsPointService.getUserBounsPoint(recommendUserInfoId);
						if (recommendUserBounsPoint != null) {
							// 百分之五
							String addFivePercentBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(Double.valueOf(recommendUserBounsPoint.getScore()), fivePercentBouns));
							// 一百
							String addOneHundredBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(NumberUtils.add(Double.valueOf(recommendUserBounsPoint.getScore()), fivePercentBouns), 100D));
							recommendUserBounsPoint.setScore(Double.valueOf(addOneHundredBounsStr).intValue());
							int updateRecommendUserBounsPointFlag = userBounsPointService.update(recommendUserBounsPoint);
							if (updateRecommendUserBounsPointFlag == 1) {
								logger.info("推荐人积分账户更新成功......");
								// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
								UserBounsHistory addFivePercentBounsHistory = new UserBounsHistory();
								addFivePercentBounsHistory.setId(IdGen.uuid());
								addFivePercentBounsHistory.setUserId(recommendUserInfoId); // 推荐人id
								addFivePercentBounsHistory.setAmount(fivePercentBouns);
								addFivePercentBounsHistory.setCreateDate(new Date(currentTimeMillis));
								addFivePercentBounsHistory.setTransId(userId); // 用户帐号id
								addFivePercentBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
								addFivePercentBounsHistory.setCurrentAmount(addFivePercentBounsStr); // 当前积分余额
								int insertAddFivePercentBounsHistoryFlag = userBounsHistoryService.insert(addFivePercentBounsHistory);
								logger.info("推荐人积分（出借人所获积分的百分之五）流水插入:{}", insertAddFivePercentBounsHistoryFlag == 1 ? "成功" : "失败");
								// 推荐人可以获取100积分.
								UserBounsHistory addOneHundredBounsHistory = new UserBounsHistory();
								addOneHundredBounsHistory.setId(IdGen.uuid());
								addOneHundredBounsHistory.setUserId(recommendUserInfoId); // 推荐人id
								addOneHundredBounsHistory.setAmount(100D);
								currentTimeMillis = currentTimeMillis + 1;
								addOneHundredBounsHistory.setCreateDate(new Date(currentTimeMillis));
								addOneHundredBounsHistory.setTransId(userId); // 用户帐号id
								addOneHundredBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
								addOneHundredBounsHistory.setCurrentAmount(addOneHundredBounsStr); // 当前积分余额
								int insertAddOneHundredBounsHistoryFlag = userBounsHistoryService.insert(addOneHundredBounsHistory);
								logger.info("推荐人所获（100积分）流水插入:{}", insertAddOneHundredBounsHistoryFlag == 1 ? "成功" : "失败");
							} else {
								logger.info("推荐人积分账户更新失败......");
							}
						}
					}
				}
			} else if (levelDistribution != null && levelDistribution.getParentId() != null && !levelDistribution.getParentId().equals("")) { // 三级关系邀请人
				/**
				 * 出借人首次出借，推荐人可获取100积分和出借人所获积分的百分之五
				 */
				long currentTimeMillis = System.currentTimeMillis();
				UserBounsPoint recommendUserBounsPoint = userBounsPointService.getUserBounsPoint(levelDistribution.getParentId());
				if (recommendUserBounsPoint != null) {
					// 百分之五
					String addFivePercentBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(Double.valueOf(recommendUserBounsPoint.getScore()), fivePercentBouns));
					// 一百
					String addOneHundredBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(NumberUtils.add(Double.valueOf(recommendUserBounsPoint.getScore()), fivePercentBouns), 100D));
					recommendUserBounsPoint.setScore(Integer.valueOf(Double.valueOf(addOneHundredBounsStr).intValue()));
					int updateRecommendUserBounsPointFlag = userBounsPointService.update(recommendUserBounsPoint);
					if (updateRecommendUserBounsPointFlag == 1) {
						logger.info("推荐人积分账户更新成功......");
						// 同时为推荐人赠送投资人所获积分的5%，新增积分历史记录.
						UserBounsHistory recommendUserBounsHistory = new UserBounsHistory();
						recommendUserBounsHistory.setId(IdGen.uuid());
						recommendUserBounsHistory.setUserId(levelDistribution.getParentId()); // 推荐人id
						recommendUserBounsHistory.setAmount(fivePercentBouns);
						recommendUserBounsHistory.setCreateDate(new Date(currentTimeMillis));
						recommendUserBounsHistory.setTransId(userId); // 用户帐号id
						recommendUserBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
						recommendUserBounsHistory.setCurrentAmount(addFivePercentBounsStr); // 当前积分余额
						int insertRecommendUserBounsHistoryFlag = userBounsHistoryService.insert(recommendUserBounsHistory);
						logger.info("推荐人积分（出借人所获积分的百分之五）流水插入:{}", insertRecommendUserBounsHistoryFlag == 1 ? "成功" : "失败");
						// 推荐人可以获取100积分.
						UserBounsHistory addOneHundredBounsHistory = new UserBounsHistory();
						addOneHundredBounsHistory.setId(IdGen.uuid());
						addOneHundredBounsHistory.setUserId(levelDistribution.getParentId()); // 推荐人id
						addOneHundredBounsHistory.setAmount(100D);
						currentTimeMillis = currentTimeMillis + 1;
						addOneHundredBounsHistory.setCreateDate(new Date(currentTimeMillis));
						addOneHundredBounsHistory.setTransId(userId); // 用户帐号id
						addOneHundredBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST);
						addOneHundredBounsHistory.setCurrentAmount(addOneHundredBounsStr); // 当前积分余额
						int insertAddOneHundredBounsHistoryFlag = userBounsHistoryService.insert(addOneHundredBounsHistory);
						logger.info("推荐人所获（100积分）流水插入:{}", insertAddOneHundredBounsHistoryFlag == 1 ? "成功" : "失败");
					} else {
						logger.info("推荐人积分账户更新失败......");
					}
				}
			}
		}
	}

	/**
	 * 
	 * methods: addBouns <br>
	 * description: 出借增加积分 <br>
	 * author: Roy <br>
	 * date: 2019年9月30日 上午10:40:20
	 * 
	 * @param userId
	 *            用户帐号id
	 * @param userBouns
	 *            增加积分的额度
	 * @param investId
	 *            出借id
	 */
	public void addBouns(String userId, Double userBouns, String investId) {

		UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
		if (null != userBounsPoint) {
			String sumBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.add(userBouns, Double.valueOf(userBounsPoint.getScore())));
			UserBounsHistory userBounsHistory = new UserBounsHistory();
			userBounsHistory.setId(IdGen.uuid());
			userBounsHistory.setUserId(userId);
			userBounsHistory.setAmount(userBouns);
			userBounsHistory.setCreateDate(new Date());
			userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST);
			userBounsHistory.setTransId(investId);
			userBounsHistory.setCurrentAmount(sumBounsStr);
			int insertUserBounsHistoryFlag = userBounsHistoryService.insert(userBounsHistory);
			if (insertUserBounsHistoryFlag == 1) {
				logger.info("积分历史插入成功......");
				userBounsPoint.setScore(Double.valueOf(sumBounsStr).intValue());
				userBounsPoint.setUpdateDate(new Date());
				int updateUserBounsFlag = userBounsPointService.update(userBounsPoint);
				logger.info("积分账户更新:{}", updateUserBounsFlag == 1 ? "成功" : "失败");
			}
		}
	}
}
