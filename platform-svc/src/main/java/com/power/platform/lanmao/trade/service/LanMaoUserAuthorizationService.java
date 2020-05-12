package com.power.platform.lanmao.trade.service;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.ZtmgUserAuthorizationDao;
import com.power.platform.cgb.entity.ZtmgUserAuthorization;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.SendMailUtil;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.ZipUtils;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.config.RedirectUrlConfig;
import com.power.platform.lanmao.dao.AsyncTransactionLogDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.AsyncTransactionLog;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.trade.pojo.AsyncTransaction;
import com.power.platform.lanmao.trade.pojo.CancelPreTransaction;
import com.power.platform.lanmao.trade.pojo.SyncTransaction;
import com.power.platform.lanmao.trade.pojo.SyncTransactionDetail;
import com.power.platform.lanmao.trade.pojo.UserAuthorization;
import com.power.platform.lanmao.type.AsyncTransactionLogStatusEnum;
import com.power.platform.lanmao.type.BizOriginEnum;
import com.power.platform.lanmao.type.BizTypeEnum;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.BusinessTypeEnum;
import com.power.platform.lanmao.type.ConfirmTradeTypEnum;
import com.power.platform.lanmao.type.ProjectStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;

@Path("/lanMaoTrade")
@Service("lanMaoUserAuthorizationService")
@Produces(MediaType.APPLICATION_JSON)
public class LanMaoUserAuthorizationService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoUserAuthorizationService.class);
	/**
	 * 过期时间，600秒
	 */
	private static final Integer EXPIRE_SECONDS = 600;
	@Autowired
	private LanMaoAuthorizationService lanMaoAuthorizationService;
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private ZtmgUserAuthorizationDao ztmgUserAuthorizationDao;
	@Autowired
	private UserAuthorizationNotifyService userAuthorizationNotifyService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private LanMaoPreTransactionService lanMaoPreTransactionService;
	@Autowired
	private AsyncTransactionLogDao asyncTransactionLogDao;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private LanMaoAsyncTransactionService lanMaoAsyncTransactionService;
	@Autowired
	private LanMaoProjectService lanMaoProjectService;

	/**
	 * 
	 * methods: redirectUserAuthorization <br>
	 * description: 用户授权，同步回调地址 <br>
	 * author: Roy <br>
	 * date: 2019年10月7日 下午7:46:16
	 * 
	 * @param serviceName
	 * @param platformNo
	 * @param userDevice
	 * @param responseType
	 * @param keySerial
	 * @param respData
	 * @param sign
	 * @param response
	 */
	@POST
	@Path("/redirectUserAuthorization")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized void redirectUserAuthorization(@FormParam("serviceName") String serviceName, @FormParam("platformNo") String platformNo, @FormParam("userDevice") String userDevice, @FormParam("responseType") String responseType, @FormParam("keySerial") String keySerial, @FormParam("respData") String respData, @FormParam("sign") String sign, @Context HttpServletResponse response) {

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
			try {
				logger.info("用户授权同步回调-redirect-start...");
				JSONObject jsonObject = JSONObject.parseObject(respData);
				String platformUserNo = jsonObject.getString("platformUserNo"); // 平台用户编号
				// 平台页面回跳地址
				response.sendRedirect(RedirectUrlConfig.USER_AUTHORIZATION_RETURN_URL + "?id=" + platformUserNo);
				logger.info("用户授权同步回调-redirect-end...");
				// String requestNo = jsonObject.getString("requestNo"); //
				// 请求流水号
				// String code = jsonObject.getString("code");
				// String status = jsonObject.getString("status"); //
				// 业务处理状态（处理失败INIT；处理成功SUCCESS），平台可根据非SUCCESS状态做相应处理，处理失败时可参考错误码及描述
				// String errorCode = jsonObject.getString("errorCode");
				// String errorMessage = jsonObject.getString("errorMessage");
				// String authList = jsonObject.getString("authList");
				// String amount = jsonObject.getString("amount");
				// String failTime = jsonObject.getString("failTime");
				// // --
				// LmTransaction lmTransaction = null;
				// if (BusinessStatusEnum.SUCCESS.getValue().equals(status)) {
				// logger.info("用户平台编号:{}，授权列表:{}", platformUserNo, authList);
				//
				// String key = "USER_AUTHORIZATION:" + platformUserNo;
				// String JedisSetResult = JedisUtils.set(key, platformUserNo,
				// EXPIRE_SECONDS);
				// if ("OK".equals(JedisSetResult)) {
				// logger.info("用户授权Key创建成功 ......");
				// ZtmgUserAuthorization zua = new ZtmgUserAuthorization();
				// zua.setUserId(platformUserNo);
				// zua.setMerchantId(platformNo);
				// zua.setSignature(sign);
				// zua.setGrantAmountList(amount);
				// zua.setGrantTimeList(failTime);
				// boolean ztmgUserAuthorization =
				// userAuthorizationNotifyService.ztmgUserAuthorization(zua);
				// if (ztmgUserAuthorization) {
				// logger.info("用户授权同步回调成功 ......");
				// // 懒猫交易留存
				// lmTransaction = new LmTransaction();
				// lmTransaction.setId(IdGen.uuid());
				// lmTransaction.setRequestNo(requestNo);
				// lmTransaction.setServiceName(ServiceNameEnum.USER_AUTHORIZATION.getValue());
				// lmTransaction.setPlatformUserNo(platformUserNo);
				// lmTransaction.setCreateDate(new Date());
				// lmTransaction.setUpdateDate(new Date());
				// lmTransaction.setCode(code);
				// lmTransaction.setStatus(status);
				// int lmTransactionFlag =
				// lmTransactionDao.insert(lmTransaction);
				// logger.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" :
				// "失败");
				// }
				// } else {
				// logger.info("用户授权Key创建失败 ......");
				// }
				// } else {
				// logger.info("用户授权同步回调失败 ......");
				// // 懒猫交易留存
				// lmTransaction = new LmTransaction();
				// lmTransaction.setId(IdGen.uuid());
				// lmTransaction.setRequestNo(requestNo);
				// lmTransaction.setServiceName(ServiceNameEnum.USER_AUTHORIZATION.getValue());
				// lmTransaction.setPlatformUserNo(platformUserNo);
				// lmTransaction.setCreateDate(new Date());
				// lmTransaction.setUpdateDate(new Date());
				// lmTransaction.setCode(code);
				// lmTransaction.setStatus(status);
				// lmTransaction.setErrorCode(errorCode);
				// lmTransaction.setErrorMessage(errorMessage);
				// int lmTransactionFlag =
				// lmTransactionDao.insert(lmTransaction);
				// logger.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" :
				// "失败");
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.debug("用户授权同步回调-验签失败...");
		}
	}

	/**
	 * 
	 * methods: userAuthorization <br>
	 * description: 用户授权 <br>
	 * 是否幂等：否，接口模式：网关，异步通知：是 <br>
	 * author: Roy <br>
	 * date: 2019年9月24日 下午2:30:56
	 * 
	 * @param userDevice
	 * @param userId
	 * @param authList
	 * @param request
	 * @return
	 */
	@POST
	@Path("/userAuthorization")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> userAuthorization(@FormParam("userDevice") String userDevice, @FormParam("platformUserNo") String platformUserNo, @FormParam("authList") String authList, @Context HttpServletRequest request) {

		Map<String, String> result = new HashMap<String, String>();
		try {
			String requestNo = IdGen.uuid();

			UserAuthorization userAuthorization = new UserAuthorization();
			userAuthorization.setPlatformUserNo(platformUserNo);
			userAuthorization.setRequestNo(requestNo);
			userAuthorization.setAuthList(authList);
			userAuthorization.setAmount("1000000.00"); // 授权还款金额1000000.00万每笔
			userAuthorization.setFailTime(DateUtils.formatDate(DateUtils.yearAddNum(new Date(), 3), "yyyyMMdd")); // 授权截至时间，默认三年
			userAuthorization.setRedirectUrl(RedirectUrlConfig.USER_AUTHORIZATION_REDIRECT_URL);
			Map<String, String> requestParam = lanMaoAuthorizationService.userAuthorization(userAuthorization);

			// 懒猫交易留存
			LmTransaction lmTransaction = new LmTransaction();
			lmTransaction.setId(IdGen.uuid());
			lmTransaction.setRequestNo(requestNo);
			lmTransaction.setServiceName(ServiceNameEnum.USER_AUTHORIZATION.getValue());
			lmTransaction.setPlatformUserNo(platformUserNo);
			lmTransaction.setCreateDate(new Date());
			lmTransaction.setUpdateDate(new Date());
			int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
			logger.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
			requestParam.put("state", "0");
			requestParam.put("message", "用户授权接口请求数据封装成功 ...");
			return requestParam;
		} catch (Exception e) {
			logger.error("error:{}", e.getMessage());
			result.put("message", "系统异常 ...");
			result.put("state", "1");
			return result;
		}
	}

	/**
	 * 
	 * 方法: cancelPreTransaction <br>
	 * 描述: 预处理取消，场景：用于借款人/代偿人授权预处理还款时预处理取消. <br>
	 * 作者: Roy <br>
	 * 时间: 2019年12月3日 下午8:47:12
	 * 
	 * @param preTransactionNo
	 * @param amount
	 * @param request
	 * @return
	 */
	@POST
	@Path("/cancelPreTransaction")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> cancelPreTransaction(@FormParam("preTransactionNo") String preTransactionNo, @FormParam("amount") String amount, @Context HttpServletRequest request) {

		Map<String, String> result = new HashMap<String, String>();
		try {
			if (StringUtils.isBlank(preTransactionNo) || StringUtils.isBlank(amount)) {
				result.put("message", "缺少必要参数...");
				result.put("state", "0");
				return result;
			}
			// 批次号
			String batchNo = IdGen.uuid();
			// 请求流水号
			String requestNo = IdGen.uuid();
			// 预处理取消
			CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
			cancelPreTransaction.setRequestNo(requestNo);
			cancelPreTransaction.setPreTransactionNo(preTransactionNo);
			cancelPreTransaction.setAmount(amount);
			Map<String, String> cancelPreTransactionMap = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
			long currentTimeMillis = System.currentTimeMillis();
			LmTransaction lt = null;
			if (BusinessStatusEnum.SUCCESS.getValue().equals(cancelPreTransactionMap.get("status")) && "0".equals(cancelPreTransactionMap.get("code"))) {
				lt = new LmTransaction();
				// 查询授权预处理的流水
				LmTransaction lmTransaction = new LmTransaction();
				lmTransaction.setRequestNo(preTransactionNo); // 授权预处理，请求流水号
				List<LmTransaction> ltList = lmTransactionDao.findList(lmTransaction);
				if (ltList != null && ltList.size() > 0) {
					lt = ltList.get(0);
				}
				lt.setId(IdGen.uuid());
				lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
				lt.setBatchNo(batchNo); // 批次号
				lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
				lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
				lt.setCode(cancelPreTransactionMap.get("code"));
				lt.setStatus(cancelPreTransactionMap.get("status"));
				lt.setCreateDate(new Date(currentTimeMillis));
				lt.setUpdateDate(new Date(currentTimeMillis));
				int insertLtFlag = lmTransactionDao.insert(lt);
				logger.info("间接代偿授权预处理取消，插入:{}", insertLtFlag == 1 ? "成功" : "失败");
			} else {
				lt = new LmTransaction();
				// 查询授权预处理的流水
				LmTransaction lmTransaction = new LmTransaction();
				lmTransaction.setRequestNo(preTransactionNo); // 授权预处理，请求流水号
				List<LmTransaction> ltList = lmTransactionDao.findList(lmTransaction);
				if (ltList != null && ltList.size() > 0) {
					lt = ltList.get(0);
				}
				lt.setId(IdGen.uuid());
				lt.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
				lt.setBatchNo(batchNo); // 批次号
				lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
				lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
				lt.setCode(cancelPreTransactionMap.get("code"));
				lt.setStatus(cancelPreTransactionMap.get("status"));
				lt.setErrorCode(cancelPreTransactionMap.get("errorCode"));
				lt.setErrorMessage(cancelPreTransactionMap.get("errorMessage"));
				lt.setCreateDate(new Date(currentTimeMillis));
				lt.setUpdateDate(new Date(currentTimeMillis));
				int insertLtFlag = lmTransactionDao.insert(lt);
				logger.info("间接代偿授权预处理取消，插入:{}", insertLtFlag == 1 ? "成功" : "失败");
			}
			result.put("message", "程序执行完成...");
			result.put("state", "0");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常 ...");
			result.put("state", "1");
		}
		return result;
	}

	/**
	 * 
	 * 方法: asyncTransactionIndirectCompensatory <br>
	 * 描述: 标的当期部分还款订单失败，重复进行间接代偿操作，只支持间接代偿 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年12月3日 下午11:22:02
	 * 
	 * @param proId
	 * @param repayType
	 * @param repaymentDate
	 * @param request
	 * @return
	 */
	@POST
	@Path("/asyncTransactionIndirectCompensatory")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> asyncTransactionIndirectCompensatory(@FormParam("proId") String proId, @FormParam("repayType") String repayType, @FormParam("repaymentDate") String repaymentDate, @Context HttpServletRequest request) {

		Map<String, String> result = new HashMap<String, String>();
		try {
			if (StringUtils.isBlank(proId) || StringUtils.isBlank(repayType) || StringUtils.isBlank(repaymentDate)) {
				result.put("message", "缺少必要参数...");
				result.put("state", "0");
				return result;
			}
			WloanTermProject repaymentProject = wloanTermProjectService.get(proId);
			String collaboratorSourcePlatformUserNo = ""; // 合作机构（核心企业）方平台用户编号
			String borrowersSourcePlatformUserNo = ""; // 借款（供应商）方平台用户编号
			String projectNo = ""; // 标的号，平台的标的编号
			String requestNo = IdGen.uuid(); // 授权预处理，请求流水号，唯一标识
			Double userPlanRepayAmount = 0D; // 本期用户还款计划还款合计总额
			if (null != repaymentProject) {
				projectNo = repaymentProject.getSn(); // 标的编号
				collaboratorSourcePlatformUserNo = repaymentProject.getReplaceRepayId(); // 代偿人
				WloanSubject wloanSubject = wloanSubjectService.get(repaymentProject.getSubjectId());
				if (null != wloanSubject) {
					borrowersSourcePlatformUserNo = wloanSubject.getLoanApplyId(); // 借款人
				}
				if (WloanTermProject.IS_REPLACE_REPAY_1.equals(repaymentProject.getIsReplaceRepay())) { // 是否代偿，是.
					/**
					 * 失败订单查询
					 */
					String batchNo = IdGen.uuid(); // Y 批次号
					AsyncTransactionLog entity = new AsyncTransactionLog();
					entity.setStatus("FAIL");
					List<AsyncTransactionLog> atlList = asyncTransactionLogDao.findList(entity);
					WloanTermUserPlan wloanTermUserPlan = null;
					SyncTransaction syncTransaction = null;
					SyncTransactionDetail std = null;
					List<SyncTransaction> bizDetails = new ArrayList<SyncTransaction>(); // 交易明细
					List<AsyncTransactionLog> atls = new ArrayList<AsyncTransactionLog>(); // 批量交易订单日志
					List<String> asyncTransactionLogList = new ArrayList<String>(); // 存放失败交易订单的主键id
					AsyncTransactionLog atl = null; // 批量交易日志，交易失败逻辑处理
					LmTransaction lt = null;
					long currentTimeMillis = System.currentTimeMillis();
					// 失败的交易订单
					for (AsyncTransactionLog asyncTransactionLog : atlList) {
						String freezeRequestNo = asyncTransactionLog.getFreezeRequestNo(); // 用户还款计划订单id
						logger.info("freezeRequestNo：\t" + freezeRequestNo);
						// 查询本期标的还款的所有用户还款计划
						WloanTermUserPlan up = new WloanTermUserPlan();
						WloanTermProject repayProject = new WloanTermProject();
						repayProject.setId(proId);
						up.setWloanTermProject(repayProject);
						up.setRepaymentDate(DateUtils.parseDate(repaymentDate, "yyyy-MM-dd"));
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(up);
						boolean isExist = false;
						for (WloanTermUserPlan upPlan : userPlanList) {
							if (freezeRequestNo != null) {
								if (freezeRequestNo.equals(upPlan.getId())) {
									isExist = true;
									continue;
								}
							}
						}
						if (isExist) { // 标的当期用户还款计划存在失败订单
							asyncTransactionLogList.add(asyncTransactionLog.getId()); // 存放失败交易订单id
							wloanTermUserPlan = wloanTermUserPlanDao.get(freezeRequestNo);
							String userPlanId = wloanTermUserPlan.getId();
							userPlanRepayAmount = NumberUtils.add(userPlanRepayAmount, wloanTermUserPlan.getInterest());
							String tradeRequestNo = IdGen.uuid();
							// Y 交易明细
							syncTransaction = new SyncTransaction();
							// Y 交易明细订单号
							syncTransaction.setRequestNo(tradeRequestNo);
							// Y 交易类型
							syncTransaction.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue());
							syncTransaction.setProjectNo(projectNo);
							// N 债权出让请求流水号
							syncTransaction.setSaleRequestNo(null);
							// Y 业务明细
							List<SyncTransactionDetail> details = new ArrayList<SyncTransactionDetail>();
							// 间接代偿，业务明细最少两条
							for (int j = 0; j < 2; j++) {
								if (j == 0) { // 代偿人[代偿确认]借款人
									std = new SyncTransactionDetail();
									// Y 业务类型
									std.setBizType(BusinessTypeEnum.COMPENSATORY.getValue());
									// N 授权预处理，请求流水号
									std.setFreezeRequestNo(requestNo);
									// N 出款方（代偿人）用户编号
									std.setSourcePlatformUserNo(collaboratorSourcePlatformUserNo);
									// N 收款方（借款人）用户编号
									std.setTargetPlatformUserNo(borrowersSourcePlatformUserNo);
									if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(wloanTermUserPlan.getPrincipal())) {
										// Y 本息和
										std.setAmount(NumberUtils.scaleDoubleStr(wloanTermUserPlan.getInterest()));
										// N 利息
										std.setIncome(NumberUtils.scaleDoubleStr(wloanTermUserPlan.getInterest()));
										// N 债权份额（债权认购且需校验债权关系的必传）
										std.setShare(null);
										// N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setCustomDefine(null);
										std.setRemark("代偿人[代偿确认]借款人，付息");// N
									} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(wloanTermUserPlan.getPrincipal())) {
										// Y 本息和
										std.setAmount(NumberUtils.scaleDoubleStr(wloanTermUserPlan.getInterest()));
										// N 利息
										std.setIncome(NumberUtils.scaleDoubleStr(NumberUtils.subtract(wloanTermUserPlan.getInterest(), wloanTermUserPlan.getWloanTermInvest().getAmount())));
										// N 债权份额（债权认购且需校验债权关系的必传）
										std.setShare(null);
										// N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setCustomDefine(null);
										std.setRemark("代偿人[代偿确认]借款人，还本付息");// N
									}
									details.add(std);
								}
								if (j == 1) { // 借款人[代偿确认]出借人
									std = new SyncTransactionDetail();
									// Y 业务类型
									std.setBizType(BusinessTypeEnum.COMPENSATORY.getValue());
									// N 授权预处理，请求流水号
									std.setFreezeRequestNo(requestNo);
									// N 出款方（借款人）用户编号
									std.setSourcePlatformUserNo(borrowersSourcePlatformUserNo);
									// N 收款方（出借人）用户编号
									std.setTargetPlatformUserNo(wloanTermUserPlan.getUserInfo().getId());
									if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(wloanTermUserPlan.getPrincipal())) {
										// Y 本息和
										std.setAmount(NumberUtils.scaleDoubleStr(wloanTermUserPlan.getInterest()));
										// N 利息
										std.setIncome(NumberUtils.scaleDoubleStr(wloanTermUserPlan.getInterest()));
										// N 债权份额（债权认购且需校验债权关系的必传）
										std.setShare(null);
										// N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setCustomDefine(null);
										std.setRemark("借款人[代偿确认]出借人，付息");// N
									} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(wloanTermUserPlan.getPrincipal())) {
										// Y 本息和
										std.setAmount(NumberUtils.scaleDoubleStr(wloanTermUserPlan.getInterest()));
										// N 利息
										std.setIncome(NumberUtils.scaleDoubleStr(NumberUtils.subtract(wloanTermUserPlan.getInterest(), wloanTermUserPlan.getWloanTermInvest().getAmount())));
										// N 债权份额（债权认购且需校验债权关系的必传）
										std.setShare(null);
										// N 平台商户自定义参数，平台交易时传入的自定义参数
										std.setCustomDefine(null);
										std.setRemark("借款人[代偿确认]出借人，还本付息");// N
									}
									details.add(std);
								}
							}
							syncTransaction.setDetails(details); // Y
							bizDetails.add(syncTransaction);
							// 批量交易日志，交易失败逻辑处理
							atl = new AsyncTransactionLog();
							atl.setId(IdGen.uuid());
							atl.setAsyncRequestNo(tradeRequestNo); // 异步通知时的交易明细订单号
							atl.setFreezeRequestNo(userPlanId); // 用户还款计划订单id
							atl.setBizType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型：还款
							atl.setBizOrigin(BizOriginEnum.DISPERSION.getValue()); // 业务来源
							atl.setStatus(AsyncTransactionLogStatusEnum.INIT.getValue()); // 处理中
							currentTimeMillis = currentTimeMillis + 1000;
							atl.setCreateDate(new Date(currentTimeMillis));
							atl.setUpdateDate(new Date(currentTimeMillis));
							atls.add(atl);
						} else {
							logger.info("该笔失败交易订单不存在当期用户还款计划中 ...");
						}
					}
					logger.info("userPlanRepayAmount:{}", userPlanRepayAmount);
					logger.info("atls:{}", JSONObject.toJSON(atls).toString());
					logger.info("bizDetails:{}", JSONObject.toJSON(bizDetails).toString());
					logger.info("asyncTransactionLogList:{}", JSONObject.toJSON(asyncTransactionLogList).toString());
					// 还款授权预处理
					UserAuthorization userAuthorization = new UserAuthorization();
					userAuthorization.setRequestNo(requestNo); // Y 授权预处理，请求流水号
					// Y 平台用户编号（代偿人）
					userAuthorization.setPlatformUserNo(collaboratorSourcePlatformUserNo);
					// N 关联充值请求流水号（原充值成功请求流水号）
					userAuthorization.setOriginalRechargeNo(null);
					// Y 见【预处理业务类型】若传入关联请求流水号，则固定为TENDER
					userAuthorization.setBizType(ConfirmTradeTypEnum.COMPENSATORY.getValue());
					// Y 冻结金额，本次还款合计总额
					userAuthorization.setAmount(NumberUtils.scaleDoubleStr(userPlanRepayAmount));
					// N 预备使用的红包金额，只记录不冻结，仅限出借业务类型
					userAuthorization.setPreMarketingAmount(null);
					if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0.equals(repayType)) {
						userAuthorization.setRemark("[间接代偿]代偿人付息");
					} else if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayType)) {
						userAuthorization.setRemark("[间接代偿]代偿人还本付息");
					}
					// Y 标的号,若传入关联充值请求流水号，则标的号固定为充值请求传入的标的号
					userAuthorization.setProjectNo(projectNo);
					// N 购买债转份额，业务类型为债权认购时，需要传此参数
					userAuthorization.setShare(null);
					// N 债权出让请求流水号，只有债权认购业务需填此参数
					userAuthorization.setCreditsaleRequestNo(null);
					logger.info("userAuthorization:{}", JSONObject.toJSON(userAuthorization).toString());
					Map<String, String> userAutoPreTransactionMap = lanMaoAuthorizationService.userAutoPreTransaction(userAuthorization);
					// 调用成功且业务处理成功
					if (BusinessStatusEnum.SUCCESS.getValue().equals(userAutoPreTransactionMap.get("status")) && "0".equals(userAutoPreTransactionMap.get("code"))) {
						logger.info("懒猫代偿人间接代偿授权预处理:{}", BusinessStatusEnum.SUCCESS.getValue());
						lt = new LmTransaction();
						lt.setId(IdGen.uuid());
						lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
						lt.setBatchNo(batchNo); // 批次号
						lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
						// Y 平台用户编号
						lt.setPlatformUserNo(collaboratorSourcePlatformUserNo);
						lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
						lt.setProjectNo(projectNo);// 标的编号
						lt.setCode(userAutoPreTransactionMap.get("code"));
						lt.setStatus(userAutoPreTransactionMap.get("status"));
						currentTimeMillis = currentTimeMillis + 1000;
						lt.setCreateDate(new Date(currentTimeMillis));
						lt.setUpdateDate(new Date(currentTimeMillis));
						int insertLtFlag = lmTransactionDao.insert(lt);
						logger.info("间接代偿授权预处理留存插入:{}", insertLtFlag == 1 ? "成功" : "失败");
						/**
						 * 还款确认，批量交易
						 */
						AsyncTransaction asyncTransaction = new AsyncTransaction();
						asyncTransaction.setBatchNo(batchNo);
						asyncTransaction.setBizDetails(bizDetails);
						Map<String, String> asyncTransactionMap = lanMaoAsyncTransactionService.asyncTransaction(asyncTransaction);
						if (BusinessStatusEnum.SUCCESS.getValue().equals(asyncTransactionMap.get("status")) && "0".equals(asyncTransactionMap.get("code"))) {
							logger.info("间接代偿，批量交易:{}", BusinessStatusEnum.SUCCESS.getValue());
							// 平台留存
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setSourcePlatformUserNo(collaboratorSourcePlatformUserNo); // 出款方
							lt.setTargetPlatformUserNo(borrowersSourcePlatformUserNo); // 收款方
							lt.setCode(asyncTransactionMap.get("code"));
							lt.setStatus(asyncTransactionMap.get("status"));
							lt.setRemarks("间接代偿，还款，批量交易");
							lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue());
							lt.setProjectNo(projectNo);// 标的编号
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertCreditAssignmentFlag = lmTransactionDao.insert(lt);
							logger.info("间接代偿，还款，批量交易记录留存:{}", insertCreditAssignmentFlag == 1 ? "成功" : "失败");

							// 批量交易，交易日志留存
							for (AsyncTransactionLog atlNext : atls) {
								int insertAtlNextFlag = asyncTransactionLogDao.insert(atlNext);
								logger.info("间接代偿，批量交易日志，插入:{}", insertAtlNextFlag == 1 ? "成功" : "失败");
							}

							// 失败的交易，变更状态为成功
							for (String atlId : asyncTransactionLogList) {
								AsyncTransactionLog asyncTransactionLog = asyncTransactionLogDao.get(atlId);
								asyncTransactionLog.setStatus(AsyncTransactionLogStatusEnum.SUCCESS.getValue());
								int updateAsyncTransactionLogFlag = asyncTransactionLogDao.update(asyncTransactionLog);
								logger.info("间接代偿，失败的批量交易日志，变更:{}", updateAsyncTransactionLogFlag == 1 ? "成功" : "失败");
							}
							
							if (WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1.equals(repayType)) { // 还本付息
								// 标的状态，已截标
								// -----------------------------------------------------------------------------
								Map<String, Object> modifyProjectMap = lanMaoProjectService.modifyProject(batchNo, projectNo, ProjectStatusEnum.FINISH.getValue());
								if (BusinessStatusEnum.SUCCESS.getValue().equals(modifyProjectMap.get("status")) && "0".equals(modifyProjectMap.get("code"))) {
									logger.info("代偿人间接代偿，最后一期间接代偿，标的生命周期已结束，截标成功......");
									// 平台留存
									lt = new LmTransaction();
									lt.setId(IdGen.uuid());
									lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
									lt.setBatchNo(batchNo); // 批次号
									lt.setRequestNo(batchNo);
									lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
									lt.setCode(asyncTransactionMap.get("code"));
									lt.setStatus(asyncTransactionMap.get("status"));
									lt.setRemarks("代偿人间接代偿，最后一期间接代偿，截标");
									lt.setProjectNo(projectNo);// 标的编号
									currentTimeMillis = currentTimeMillis + 1000;
									lt.setCreateDate(new Date(currentTimeMillis));
									lt.setUpdateDate(new Date(currentTimeMillis));
									int insertLmtFlag = lmTransactionDao.insert(lt);
									logger.info("代偿人间接代偿，最后一期间接代偿，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
								} else {
									logger.info("代偿人间接代偿，最后一期间接代偿，标的生命周期已结束，截标失败......");
									// 平台留存
									lt = new LmTransaction();
									lt.setId(IdGen.uuid());
									lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
									lt.setBatchNo(batchNo); // 批次号
									lt.setRequestNo(batchNo);
									lt.setPlatformUserNo(borrowersSourcePlatformUserNo);
									lt.setCode(asyncTransactionMap.get("code"));
									lt.setStatus(asyncTransactionMap.get("status"));
									lt.setErrorCode(asyncTransactionMap.get("errorCode"));
									lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
									lt.setRemarks("代偿人间接代偿，最后一期间接代偿，截标");
									lt.setProjectNo(projectNo);// 标的编号
									currentTimeMillis = currentTimeMillis + 1000;
									lt.setCreateDate(new Date(currentTimeMillis));
									lt.setUpdateDate(new Date(currentTimeMillis));
									int insertLmtFlag = lmTransactionDao.insert(lt);
									logger.info("代偿人间接代偿，最后一期间接代偿，变更标的记录留存:{}", insertLmtFlag == 1 ? "成功" : "失败");
								}
							}

							logger.info("自动还款，代偿人间接代偿结束...end...");
						} else {
							lt = new LmTransaction();
							lt.setId(IdGen.uuid());
							lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
							lt.setBatchNo(batchNo); // 批次号
							lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
							lt.setSourcePlatformUserNo(collaboratorSourcePlatformUserNo); // 出款方
							lt.setTargetPlatformUserNo(borrowersSourcePlatformUserNo); // 收款方
							lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
							lt.setProjectNo(projectNo);// 标的编号
							lt.setCode(asyncTransactionMap.get("code"));
							lt.setStatus(asyncTransactionMap.get("status"));
							lt.setErrorCode(asyncTransactionMap.get("errorCode"));
							lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
							currentTimeMillis = currentTimeMillis + 1000;
							lt.setCreateDate(new Date(currentTimeMillis));
							lt.setUpdateDate(new Date(currentTimeMillis));
							int insertAsyncTransactionFlag = lmTransactionDao.insert(lt);
							logger.info("间接代偿，批量交易留存插入:{}", insertAsyncTransactionFlag == 1 ? "成功" : "失败");
							logger.info("代偿人间接代偿结束...end...");
						}
					} else {
						logger.info("懒猫代偿人间接代偿授权预处理:{}", BusinessStatusEnum.INIT.getValue());
						lt = new LmTransaction();
						lt.setId(IdGen.uuid());
						lt.setServiceName(ServiceNameEnum.USER_AUTO_PRE_TRANSACTION.getValue());
						lt.setBatchNo(batchNo); // 批次号
						lt.setRequestNo(requestNo); // Y 授权预处理，请求流水号
						// Y 平台用户编号
						lt.setPlatformUserNo(collaboratorSourcePlatformUserNo);
						lt.setTradeType(BizTypeEnum.INDIRECT_COMPENSATORY.getValue()); // 交易类型
						lt.setProjectNo(projectNo);// 标的编号
						lt.setCode(userAutoPreTransactionMap.get("code"));
						lt.setStatus(userAutoPreTransactionMap.get("status"));
						lt.setErrorCode(userAutoPreTransactionMap.get("errorCode"));
						lt.setErrorMessage(userAutoPreTransactionMap.get("errorMessage"));
						currentTimeMillis = currentTimeMillis + 1000;
						lt.setCreateDate(new Date(currentTimeMillis));
						lt.setUpdateDate(new Date(currentTimeMillis));
						int insertLtFlag = lmTransactionDao.insert(lt);
						logger.info("间接代偿授权预处理留存插入:{}", insertLtFlag == 1 ? "成功" : "失败");
						logger.info("代偿人间接代偿结束...end...");
					}
				} else {
					logger.info("本期还款标的不支持代偿业务......");
				}
			}
			result.put("message", "程序执行完成...");
			result.put("state", "0");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常 ...");
			result.put("state", "1");
		}
		return result;
	}

}
