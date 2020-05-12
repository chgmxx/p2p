package com.power.platform.lanmao.trade.service;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.AVouchersDicService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.trade.pojo.CancelPreTransaction;
import com.power.platform.lanmao.trade.pojo.UserPreTransaction;
import com.power.platform.lanmao.type.BizTypeEnum;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.ConfirmTradeTypEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.UserInvestWebService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

@Path("/lanMaoTenderPre")
@Service("lanMaoUserPreTransactionService")
@Produces(MediaType.APPLICATION_JSON)
public class LanMaoUserPreTransactionService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoUserPreTransactionService.class);
	/**
	 * 标的分布式锁，过期时间，2000毫秒
	 */
	private final static int EXPIRE_TIME = 2000;
	/**
	 * 出借订单号，key过期时间，3600秒
	 */
	private final static int EXPIRE_SECONDS = 3600;

	@Resource(name = "transactionManager")
	private DataSourceTransactionManager transactionManager;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private AVouchersDicService aVouchersDicService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private LanMaoPreTransactionService lanMaoPreTransactionService;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private AUserAwardsHistoryDao userVouchersDao;
	@Autowired
	private LanMaoUserPreTenderTransactionService lanMaoUserPreTenderTransactionService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private LmTransactionDao lmTransactionDao;

	/**
	 * 
	 * methods: redirectUserTender <br>
	 * description: 出借用户预处理回调，网关接口 <br>
	 * 是否幂等性：是，接口模式：网关，异步通知：是 <br>
	 * author: Roy <br>
	 * date: 2019年9月29日 上午9:05:51
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 * @throws Exception
	 */
	@POST
	@Path("/redirectUserPreTransaction")
	public synchronized void redirectUserPreTransaction(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) {

		serviceName = AppUtil.CheckStringByDefault(serviceName, "");
		platformNo = AppUtil.CheckStringByDefault(platformNo, "");
		userDevice = AppUtil.CheckStringByDefault(userDevice, "");
		responseType = AppUtil.CheckStringByDefault(responseType, "");
		keySerial = AppUtil.CheckStringByDefault(keySerial, "");
		respData = AppUtil.CheckStringByDefault(respData, "");
		sign = AppUtil.CheckStringByDefault(sign, "");

		logger.info("serviceName:{},platformNo:{},userDevice:{},responseType:{}", serviceName, platformNo, userDevice, responseType);
		logger.info("keySerial:{},sign{}", keySerial, sign);

		boolean verify = false;
		try {
			// 验签
			PublicKey publicKey = SignatureUtils.getRsaX509PublicKey(Base64.decodeBase64(Global.getConfigLanMao("lmPublicKey")));
			verify = SignatureUtils.verify(SignatureAlgorithm.SHA1WithRSA, publicKey, respData, Base64.decodeBase64(sign));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (verify) {
			JSONObject jsonObject = JSONObject.parseObject(respData);
			String bizType = jsonObject.getString("bizType"); // bizType：预处理业务类型，确定此次业务逻辑
			String code = jsonObject.getString("code");
			String status = jsonObject.getString("status"); // 业务处理状态（处理失败INIT；处理成功SUCCESS），平台可根据非SUCCESS状态做相应处理，处理失败时可参考错误码及描述
			String errorCode = jsonObject.getString("errorCode");
			String errorMessage = jsonObject.getString("errorMessage");
			String requestNo = jsonObject.getString("requestNo");
			if (ConfirmTradeTypEnum.TENDER.getValue().equals(bizType)) { // 出借
				logger.debug("出借用户预处理-同步回调-start...");
				try {
					// 页面响应.
					response.sendRedirect(RedirectUrlConfig.USER_PRE_TENDER_TRANSACTION_RETURN_URL + "?orderId=" + requestNo);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
				logger.debug("出借用户预处理-同步回调-end...");
			// try {
			// // requestNo，请求流水号，REDIS
			// String result = JedisUtils.set(requestNo, BusinessStatusEnum.SUCCESS.getValue(), EXPIRE_SECONDS);
			// if ("OK".equals(result)) {
			// logger.info("redis，出借订单号设置成功 ......");
			// } else {
			// logger.info("redis，出借订单号设置失败 ......");
			// }
			//
			// WloanTermInvest invest = wloanTermInvestDao.get(requestNo);
			// if ("0".equals(code) && "SUCCESS".equals(status)) { // 调用成功且业务处理成功
			// // 懒猫交易留存
			// LmTransaction lt = null;
			// if (null != invest) {
			// boolean projectProgressFlag = false; // 标的募集进度更新标志位
			// boolean projectFullFlag = false; // 标的满标标志位
			// String proSn = invest.getWloanTermProject() != null ? invest.getWloanTermProject().getSn() : "";
			// String projectId = invest.getProjectId();
			// Double investAmount = invest.getAmount(); // 出借金额
			// Double voucherAmount = invest.getVoucherAmount(); // 使用红包合计金额
			// Double freezeInvestAmount = NumberUtils.subtract(investAmount, voucherAmount); // 实际出借金额
			// String lockKey = "project_id:" + projectId; // 标的id，作为标的分布式锁的lock key
			// // 标的信息，获取分布式锁
			// if (JedisUtils.tryGetDistributedLock(lockKey, projectId, EXPIRE_TIME)) {
			// logger.info("分布式锁，获取成功 ......");
			// /**
			// * 出借人账户可用余额判断
			// */
			// CgbUserAccount cgbUserAccount = cgbUserAccountDao.getUserAccountInfo(invest.getUserId());
			// if (cgbUserAccount != null) {
			// if (investAmount > cgbUserAccount.getAvailableAmount()) {
			// logger.info("出借金额:{}大于账户可用余额:{}", NumberUtils.scaleDoubleStr(investAmount), NumberUtils.scaleDoubleStr(cgbUserAccount.getAvailableAmount()));
			// // 分布式锁，释放
			// if (JedisUtils.releaseDistributedLock(lockKey, projectId)) {
			// logger.info("分布式锁，释放成功 ......");
			// } else {
			// logger.info("分布式锁，释放失败 ......");
			// }
			// // 取消预处理参数列表
			// String cancelPreTransactionRequestNo = IdGen.uuid();
			// CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
			// cancelPreTransaction.setRequestNo(cancelPreTransactionRequestNo); // 请求的流水号
			// cancelPreTransaction.setPreTransactionNo(requestNo);// 出借预处理流水号
			// cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(freezeInvestAmount)); // 取消金额
			// // 取消预处理
			// Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
			// if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status"))) {
			// // 出借失败
			// invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setEndDate(new Date());
			// invest.setUpdateDate(new Date());
			// int updateInvestFlag = wloanTermInvestDao.update(invest);
			// logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
			// // 出借预处理取消日志记录
			// lt = new LmTransaction();
			// lt.setId(IdGen.uuid());
			// lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
			// lt.setRequestNo(cancelPreTransactionRequestNo);
			// lt.setPlatformUserNo(invest.getUserId());
			// lt.setCode(cancelPreTransactionMap.get("code"));
			// lt.setStatus(cancelPreTransactionMap.get("status"));
			// lt.setRemarks("出借预处理取消");
			// lt.setProjectNo(proSn);// 标的编号
			// lt.setCreateDate(new Date());
			// lt.setUpdateDate(new Date());
			// int insertLtFlag = lmTransactionDao.insert(lt);
			// logger.info("出借预处理取消，记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");
			// }
			// // 页面响应.
			// response.sendRedirect(RedirectUrlConfig.USER_PRE_TENDER_TRANSACTION_RETURN_URL + "?orderId=" + requestNo);
			// return;
			// }
			// }
			// /**
			// * 标的募集剩余金额判断
			// */
			// WloanTermProject project = wloanTermProjectDao.get(projectId);
			// if (null != project) {
			// Double projectAmount = project.getAmount(); // 标的募集金额
			// Double currentAmount = project.getCurrentAmount(); // 标的当前募集金额
			// // 募集剩余金额.
			// Double balanceAmount = NumberUtils.subtract(projectAmount, currentAmount);
			// if (investAmount > balanceAmount) {// 出借金额应小于等于标的剩余金额
			// logger.info("出借金额:{}大于标的募集剩余金额:{}", NumberUtils.scaleDoubleStr(investAmount), NumberUtils.scaleDoubleStr(balanceAmount));
			// // 分布式锁，释放
			// if (JedisUtils.releaseDistributedLock(lockKey, projectId)) {
			// logger.info("分布式锁，释放成功 ......");
			// } else {
			// logger.info("分布式锁，释放失败 ......");
			// }
			// // 取消预处理参数列表
			// String cancelPreTransactionRequestNo = IdGen.uuid();
			// CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
			// cancelPreTransaction.setRequestNo(cancelPreTransactionRequestNo); // 请求的流水号
			// cancelPreTransaction.setPreTransactionNo(requestNo);// 出借预处理流水号
			// cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(freezeInvestAmount)); // 取消金额
			// // 取消预处理
			// Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
			// if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status"))) {
			// // 出借失败
			// invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setEndDate(new Date());
			// invest.setUpdateDate(new Date());
			// int updateInvestFlag = wloanTermInvestDao.update(invest);
			// logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
			// // 出借预处理取消日志记录
			// lt = new LmTransaction();
			// lt.setId(IdGen.uuid());
			// lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
			// lt.setRequestNo(cancelPreTransactionRequestNo);
			// lt.setPlatformUserNo(invest.getUserId());
			// lt.setCode(cancelPreTransactionMap.get("code"));
			// lt.setStatus(cancelPreTransactionMap.get("status"));
			// lt.setRemarks("出借预处理取消");
			// lt.setProjectNo(proSn);// 标的编号
			// lt.setCreateDate(new Date());
			// lt.setUpdateDate(new Date());
			// int insertLtFlag = lmTransactionDao.insert(lt);
			// logger.info("出借预处理取消，记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");
			// // 页面响应.
			// response.sendRedirect(RedirectUrlConfig.USER_PRE_TENDER_TRANSACTION_RETURN_URL + "?orderId=" + requestNo);
			// return;
			// }
			// } else {
			// // 判断此项目是否满标，更新满标标志位
			// if (projectAmount.equals(NumberUtils.scaleDouble(NumberUtils.add(currentAmount, investAmount)))) {
			// projectFullFlag = true; // 更新满标标志位
			// // 标的当前募集金额更新
			// int updateCurrentAmountFlag = wloanTermProjectDao.updateCurrentAmountAndCurrentRealAmountById(projectId, investAmount);
			// if (updateCurrentAmountFlag == 1) {
			// logger.info("标的当前募集金额更新成功");
			// projectProgressFlag = true;
			// } else {
			// logger.info("标的当前募集金额更新失败");
			// }
			// // 满标，标的状态更新
			// project.setState(WloanTermProjectService.FULL);
			// project.setFullDate(new Date());
			// project.setUpdateDate(new Date());
			// int updateStateFlag = wloanTermProjectDao.updateStateById(project);
			// logger.info("满标，标的状态更新:{}", updateStateFlag == 1 ? "成功" : "失败");
			// } else {
			// // 标的当前募集金额更新
			// int updateCurrentAmountFlag = wloanTermProjectDao.updateCurrentAmountAndCurrentRealAmountById(projectId, investAmount);
			// if (updateCurrentAmountFlag == 1) {
			// logger.info("标的当前募集金额更新成功");
			// projectProgressFlag = true;
			// } else {
			// logger.info("标的当前募集金额更新失败");
			// }
			// }
			// }
			//
			// // 分布式锁，释放
			// if (JedisUtils.releaseDistributedLock(lockKey, projectId)) {
			// logger.info("分布式锁，释放成功 ......");
			// } else {
			// logger.info("分布式锁，释放失败 ......");
			// }
			// }
			// } else {
			// // 获取锁失败，提醒出借人，排队人数较多，请您稍后出借
			// // 取消预处理参数列表
			// String cancelPreTransactionRequestNo = IdGen.uuid();
			// CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
			// cancelPreTransaction.setRequestNo(cancelPreTransactionRequestNo); // 请求的流水号
			// cancelPreTransaction.setPreTransactionNo(requestNo);// 出借预处理流水号
			// cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(freezeInvestAmount)); // 取消金额
			// // 取消预处理
			// Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
			// if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status"))) {
			// // 出借失败
			// invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setEndDate(new Date());
			// invest.setUpdateDate(new Date());
			// int updateInvestFlag = wloanTermInvestDao.update(invest);
			// logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
			// // 出借预处理取消日志记录
			// lt = new LmTransaction();
			// lt.setId(IdGen.uuid());
			// lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
			// lt.setRequestNo(cancelPreTransactionRequestNo);
			// lt.setPlatformUserNo(invest.getUserId());
			// lt.setCode(cancelPreTransactionMap.get("code"));
			// lt.setStatus(cancelPreTransactionMap.get("status"));
			// lt.setRemarks("出借预处理取消");
			// lt.setProjectNo(proSn);// 标的编号
			// lt.setCreateDate(new Date());
			// lt.setUpdateDate(new Date());
			// int insertLtFlag = lmTransactionDao.insert(lt);
			// logger.info("出借预处理取消，记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");
			// // 页面响应.
			// response.sendRedirect(RedirectUrlConfig.USER_PRE_TENDER_TRANSACTION_RETURN_URL + "?orderId=" + requestNo);
			// return;
			// }
			// }
			// if (projectProgressFlag) { // 标的募集进度已刷新
			// if (lanMaoUserPreTenderTransactionService.userPreTenderTransaction(invest, projectFullFlag)) {
			// logger.info("investId:{}，出借业务处理成功......", requestNo);
			// // 页面响应.
			// response.sendRedirect(RedirectUrlConfig.USER_PRE_TENDER_TRANSACTION_RETURN_URL + "?orderId=" + requestNo);
			// return;
			// }
			// }
			// }
			// } else {
			// logger.info("errorCode:{},errorMessage:{}", errorCode, errorMessage);
			// if (null != invest) {
			// // 预处理出借失败 ...
			// invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
			// invest.setEndDate(new Date());
			// invest.setUpdateDate(new Date());
			// int updateInvestFlag = wloanTermInvestDao.update(invest);
			// logger.info("预处理出借失败，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");
			// }
			// // 页面响应.
			// response.sendRedirect(RedirectUrlConfig.USER_PRE_TENDER_TRANSACTION_RETURN_URL + "?orderId=" + requestNo);
			// return;
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// // requestNo，请求流水号，REDIS
			// String result = JedisUtils.set(requestNo, BusinessStatusEnum.INIT.getValue(), EXPIRE_SECONDS);
			// if ("OK".equals(result)) {
			// logger.info("redis，出借订单号设置成功 ......");
			// } else {
			// logger.info("redis，出借订单号设置失败 ......");
			// }
			// }
			}
		} else {
			logger.debug("用户预处理-验签失败...");
		}
	}

	/**
	 * 
	 * methods: userTender <br>
	 * description: 用户预处理 <br>
	 * 1.预先冻结用户部分资金，用于 出借 、还款、代偿、 债权认购<br>
	 * 2.平台发起上述 4 个业务前，需要先调用本接口提交预处理请求，用户将跳转至存管系统预处理页面，验证交易密码后，冻结预处理金额 。<br>
	 * R1.对于新创建的标的，【预处理业务类型】可使用枚举值： 出借 、还款、 债权认购 、代偿；<br>
	 * R2.出借 预处理时，冻结金额 红包金额≤标的金额累计已出借金额；<br>
	 * R3.债权认购预处理时，share≤原债权出让订单未被认购份额。<br>
	 * author: Roy <br>
	 * date: 2019年9月25日 上午11:17:00
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 * @param vouchers
	 * @param projectId
	 * @param request
	 * @return
	 */
	@POST
	@Path("/userTender")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> userTender(@FormParam("from") String from, @FormParam("token") String token, @FormParam("amount") String amount, @FormParam("vouchers") String vouchers, @FormParam("projectId") String projectId, @Context HttpServletRequest request) {

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		// 事物隔离级别，开启新事务，这样会比较安全些.
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态.

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		// 请求IP地址获取
		String ip = (String) request.getAttribute("ip");
		ip = ip.replace("_", ".");
		// 标的信息
		WloanTermProject project = null;
		try {
			// 必传参数校验
			if (StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
				logger.info("缺少必要参数...");
				result.put("state", "3");
				result.put("message", "缺少必要参数");
				result.put("data", null);
				return result;
			}

			// 获取本次出借用户信息
			UserInfo userInfo = null;
			String userId = JedisUtils.get(token);
			if (StringUtils.isBlank(userId)) { // 空判断，系统超时.
				logger.info("系统超时:userId:{}", userId);
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", null);
				return result;
			}

			userInfo = userInfoService.getCgb(userId); // 存管用户信息
			if (null == userInfo) {
				logger.info("存管用户信息不存在:userId:{}", userId);
				result.put("state", "3");
				result.put("message", "存管用户信息不存在");
				result.put("data", null);
				return result;
			}

			// 全局出借记录ID
			String investId = IdGen.uuid();
			// 本次出借，抵用券合计金额
			Double voucherSumAmount = 0D;
			// 出借金额
			Double investAmount = Double.valueOf(amount);

			project = wloanTermProjectDao.get(projectId);
			if (null != project) {
				// 该项目是否到期，流标日期（项目上线日期至流标日期）
				Date loanDate = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
				if (new Date().getTime() > loanDate.getTime()) {
					logger.info("该项目已过期，不允许出借:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "该项目已过期，不允许出借");
					result.put("data", null);
					return result;
				}

				// 项目流转状态判断，是否可以出借
				String projectState = project.getState();
				if (WloanTermProjectService.FINISH.equals(projectState)) {
					logger.info("该项目生命周期已结束:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "该项目生命周期已经结束");
					result.put("data", null);
					return result;
				} else if (WloanTermProjectService.REPAYMENT.equals(projectState)) {
					logger.info("该项目正在还款中:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "该项目正在还款中");
					result.put("data", null);
					return result;
				} else if (WloanTermProjectService.FULL.equals(projectState)) {
					logger.info("该项目已经募集完成:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "该项目已经募集完成");
					result.put("data", null);
					return result;
				} else if (WloanTermProjectService.PUBLISH.equals(projectState)) {
					logger.info("该项目未开始募集:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "该项目未开始募集");
					result.put("data", null);
					return result;
				}

				Double projectAmount = project.getAmount(); // 项目募集金额
				// 出借金额<=募集剩余金额
				Double currentAmount = project.getCurrentAmount(); // 当前募集金额
				if (null == currentAmount) {
					currentAmount = 0D;
				}
				// 募集剩余金额.
				Double balanceAmount = NumberUtils.subtract(projectAmount, currentAmount);
				Double minAmount = project.getMinAmount();
				Double maxAmount = project.getMaxAmount();
				if (balanceAmount < minAmount) {
					if (!investAmount.equals(balanceAmount)) {
						logger.info("该项目最后一笔只能出借{}元:projectId:{},userId:{}", balanceAmount, projectId, userId);
						result.put("state", "3");
						result.put("message", "该项目最后一笔只能出借" + NumberUtils.scaleDoubleStr(balanceAmount) + "元");
						result.put("data", null);
						return result;
					}
				} else if (investAmount > balanceAmount) {
					logger.info("您的出借金额{}元大于项目剩余募集金额{}元:projectId:{},userId:{}", investAmount, balanceAmount, projectId, userId);
					result.put("state", "3");
					result.put("message", "您的出借金额大于项目剩余募集金额");
					result.put("data", null);
					return result;
				} else if (NumberUtils.subtract(balanceAmount, investAmount) > 0D && NumberUtils.subtract(balanceAmount, investAmount) < 100D) {
					logger.info("项目尾笔出借金额必须为{}元:projectId:{},userId:{}", balanceAmount, projectId, userId);
					result.put("state", "3");
					result.put("message", "项目尾笔出借金额必须为" + NumberUtils.scaleDoubleStr(balanceAmount) + "元");
					result.put("data", null);
					return result;
				} else if (investAmount < minAmount) { // 项目起投
					logger.info("您的出借金额必须大于等于项目起投金额{}元:projectId:{},userId:{}", NumberUtils.scaleDoubleStr(minAmount), projectId, userId);
					result.put("state", "3");
					result.put("message", "您的出借金额必须大于等于项目起投金额");
					result.put("data", null);
					return result;
				} else if (investAmount > maxAmount) { // 项目出借单笔上限
					logger.info("您的出借金额必须小于等于项目单笔上限金额{}元:projectId:{},userId:{}", NumberUtils.scaleDoubleStr(maxAmount), projectId, userId);
					result.put("state", "3");
					result.put("message", "您的出借金额必须小于等于项目单笔上限金额");
					result.put("data", null);
					return result;
				}

				// 新手项目类型判断，只允许新手出借
				if (WloanTermProjectService.PROJECT_TYPE_2.equals(project.getProjectType())) {
					List<WloanTermInvest> isExistsInvestList = wloanTermInvestService.findWloanTermInvestExists(userId);
					if (isExistsInvestList != null && isExistsInvestList.size() > 0) {
						logger.info("该项目只允许新手出借:projectId:{},userId:{}", projectId, userId);
						result.put("state", "3");
						result.put("message", "该项目只允许新手出借");
						result.put("data", null);
						return result;
					}
				}

				// 该项目是否可以使用抵用券
				if (WloanTermProjectService.ISCANUSE_COUPON_NO.equals(project.getIsCanUseCoupon())) {
					logger.info("该项目不支持使用抵用券:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "该项目不支持使用抵用券");
					result.put("data", null);
					return result;
				}

				// 抵用券列表
				List<String> voucherList = new ArrayList<String>();
				if (vouchers != null && !vouchers.equals("")) {
					String[] voucherData = vouchers.split(",");
					voucherList = Arrays.asList(voucherData);
				}

				// 出借记录备注
				StringBuffer investRemarks = new StringBuffer();
				// 抵用券项目期限范围校验
				String span = project.getSpan().toString();
				AUserAwardsHistory voucher = null;
				AVouchersDic voucherDic = null;
				if (voucherList.size() > 0) {
					investRemarks.append("使用");
				} else {
					investRemarks.append("未使用抵用券");
				}
				for (int i = 0; i < voucherList.size(); i++) {
					String vouId = voucherList.get(i); // 抵用券ID
					voucher = aUserAwardsHistoryService.get(vouId);
					if (null != voucher) {
						// 抵用券非法使用，校验
						String voucherUserId = voucher.getUserId();
						if (voucherUserId != null) {
							if (!voucherUserId.equals(userId)) {
								logger.info("抵用券非法使用:projectId:{},userId:{}", projectId, userId);
								result.put("state", "3");
								result.put("message", "抵用券非法使用，三思而行");
								result.put("data", null);
								return result;
							}
						}

						// 抵用券有效性判断
						if (AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2.equals(voucher.getState())) {
							logger.info("抵用券已使用:projectId:{},userId:{}", projectId, userId);
							result.put("state", "3");
							result.put("message", "抵用券已使用，请选择有效的抵用券");
							result.put("data", null);
							return result;
						}

						// 抵用券有效性判断
						if (AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_3.equals(voucher.getState())) {
							logger.info("抵用券已过期:projectId:{},userId:{}", projectId, userId);
							result.put("state", "3");
							result.put("message", "抵用券已过期，请选择有效的抵用券");
							result.put("data", null);
							return result;
						}

						if (voucher.getSpans() != null && !voucher.getSpans().equals(UserVouchersHistoryService.SPAN_1)) {
							if (!voucher.getSpans().contains(span)) {
								// 手动回滚事物
								transactionManager.rollback(status);
								logger.info("抵用券项目期限超出本项目使用范围:projectId:{},userId:{}", projectId, userId);
								result.put("state", "3");
								result.put("message", "抵用券项目期限超出本项目使用范围");
								result.put("data", null);
								return result;
							}
						}
						voucherDic = aVouchersDicService.get(voucher.getAwardId()); // 抵用券字典
						if (null != voucherDic) {
							voucherSumAmount = NumberUtils.add(voucherSumAmount, voucherDic.getAmount()); // 累计使用抵用券金额
							investRemarks.append(NumberUtils.scaleDoubleStr(voucherDic.getAmount()) + "元，");
						}
					}
					voucher.setBidId(investId); // 记录该笔出借使用了此抵用券
					voucher.setUpdateDate(new Date());
					int updateVoucherFlag = userVouchersDao.update(voucher);
					logger.info("抵用券使用更新:{}", updateVoucherFlag == 1 ? "成功" : "失败");
					if (i == (voucherList.size() - 1)) {
						investRemarks.append("抵用券");
					}
				}

				// 本次出借使用抵用券最大额度
				Double maxVoucherAmount = NumberUtils.multiply(investAmount, 0.01D);
				if (voucherSumAmount > maxVoucherAmount) {
					logger.info("抵用券合计金额超出本次出借最大使用额度:projectId:{},userId:{}", projectId, userId);
					result.put("state", "3");
					result.put("message", "抵用券合计金额超出本次出借最大使用额度");
					result.put("data", null);
					return result;
				}

				/**
				 * 平台业务处理
				 */
				// --
				logger.info("userId:{}，出借记录初始化---start---", userId);
				Double interest = InterestUtils.getInvInterest(investAmount, project.getAnnualRate(), project.getSpan());
				WloanTermInvest invest = new WloanTermInvest();
				invest.setId(investId);
				invest.setWloanTermProject(project);
				invest.setUserInfo(userInfo);
				invest.setAmount(investAmount);
				invest.setVoucherAmount(voucherSumAmount);
				invest.setInterest(interest);
				invest.setBeginDate(new Date());
				invest.setCreateDate(new Date());
				invest.setIp(ip);
				invest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_0);
				invest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_0);
				invest.setRemarks(investRemarks.toString());
				int insertInvestFlag = wloanTermInvestDao.insert(invest);
				logger.info("用户预处理，出借记录插入:{}", insertInvestFlag == 1 ? "成功" : "失败");
				logger.info("userId:{}，出借记录初始化---end---", userId);
				// --
				// 事物提交
				transactionManager.commit(status);
				// 用户预处理-出借
				UserPreTransaction userPreTransaction = new UserPreTransaction();
				userPreTransaction.setRequestNo(investId);
				userPreTransaction.setPlatformUserNo(userId);
				userPreTransaction.setBizType(BizTypeEnum.TENDER.getValue()); // 出借
				// 实际出借金额，出借人账户扣除金额为：出借金额 - 红包金额
				investAmount = NumberUtils.subtract(investAmount, voucherSumAmount);
				userPreTransaction.setAmount(NumberUtils.scaleDoubleStr(investAmount)); // 出借金额
				if (voucherSumAmount > 0D) {
					userPreTransaction.setPreMarketingAmount(NumberUtils.scaleDoubleStr(voucherSumAmount)); // 红包金额
				} else {
					userPreTransaction.setPreMarketingAmount(null); // 红包金额
				}
				// 计算页面过期时间 当前时间增加30分钟
				DateTime dateTime = new DateTime();
				userPreTransaction.setExpired(dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
				userPreTransaction.setRemark("investId:" + investId + ",amount:" + NumberUtils.scaleDoubleStr(investAmount) + ",preMarketingAmount:" + NumberUtils.scaleDoubleStr(voucherSumAmount));
				userPreTransaction.setRedirectUrl(RedirectUrlConfig.USER_PRE_TRANSACTION_REDIRECT_URL);
				userPreTransaction.setProjectNo(project.getSn()); // Y 标的号为平台的标的编号
				Map<String, String> map = lanMaoPreTransactionService.userPreTransaction(userPreTransaction);
				// 出借结果通知出借人
				result.put("state", "0");
				result.put("data", map);
				result.put("message", "出借预处理数据封装成功");
				return result;
			} else {
				logger.info("存管项目信息不存在:projectId:{},userId:{}", projectId, userId);
				result.put("state", "3");
				result.put("message", "存管项目信息不存在");
				result.put("data", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "3");
			result.put("message", "系统异常，请联系客户，通知技术小哥哥...");
			result.put("data", null);
			return result;
		}
	}
}
