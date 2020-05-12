package com.power.platform.task;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.dao.ZtmgOrderInfoDao;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.task.pojo.RepayAvailableAmount;
import com.power.platform.task.pojo.RepayOrder;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

@Service
@Lazy(false)
public class CgbProjectReplaceRepayPlanTask {

	private static final Logger log = Logger.getLogger(CgbProjectReplaceRepayPlanTask.class);

	/**
	 * 商户自己的RSA公钥.
	 */
	private static final String MERCHANT_RSA_PUBLIC_KEY = Global.getConfigUb("UB_MERCHANT_RSA_PUBLIC_KEY");
	/**
	 * 商户自己的RSA私钥.
	 */
	private static final String MERCHANT_RSA_PRIVATE_KEY = Global.getConfigUb("UB_MERCHANT_RSA_PRIVATE_KEY");
	/**
	 * 测试环境网关地址.
	 */
	private static final String HOST = Global.getConfigUb("UB_HOST");
	/**
	 * 商户号.
	 */
	private static final String MERCHANT_ID = Global.getConfigUb("UB_MERCHANT_ID");
	/**
	 * 借款户还款回调地址.
	 */
	private static final String CALLBACK_URL_REPAY = Global.getConfigUb("CALLBACK_URL_REPAY");
	/**
	 * 代偿户还款回调地址.
	 */
	private static final String CALLBACK_URL_REPLACE_REPAY = Global.getConfigUb("CALLBACK_URL_REPLACE_REPAY");
	/**
	 * 风控手机.
	 */
	private static final String CRO_MOBILE_PHONE = Global.getConfigUb("CRO_MOBILE_PHONE");
	/**
	 * 风控同事姓名.
	 */
	private static final String CRO_NAME = Global.getConfigUb("CRO_NAME");
	/**
	 * 财务手机.
	 */
	private static final String CFO_MOBILE_PHONE = Global.getConfigUb("CFO_MOBILE_PHONE");
	/**
	 * 财务同事姓名.
	 */
	private static final String CFO_NAME = Global.getConfigUb("CFO_NAME");

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CreditUserAccountDao creditUserAccountDao;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Resource
	private ZtmgOrderInfoDao ztmgOrderInfoDao;
	@Autowired
	private CreditUserInfoService creditUserInfoService;

	/**
	 * 
	 * 方法: repay <br>
	 * 描述: 借款户还款. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月17日 下午1:26:51
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	// @Scheduled(cron = "0 0 9 * * ?")
	public void repay() {

		log.info(this.getClass() + "-借款户还款");

		Map<String, String> params = new HashMap<String, String>();
		try {

			// 项目还款计划.
			WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
			// 项目联查条件.
			WloanTermProject wloanTermProject = new WloanTermProject();
			// 标的流转状态：还款中.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			// 标的产品类型：供应链标的.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1);
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			// 状态：还款中.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			// 还款日期开始时间.
			wloanTermProjectPlan.setBeginDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00");
			// 还款日期结束时间.
			wloanTermProjectPlan.setEndDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59");
			// 当天的项目待还款计划列表.
			List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);

			// 没有项目待还款计划.
			if (projectPlans != null && projectPlans.size() == 0) {
				log.info(this.getClass() + "-借款户还款-没有待还项目还款计划");
				return; // 结束当前方法.
			}

			/**
			 * 封装保存当前批次还款中所有借款人的账户可用余额.
			 */
			// 1）获取当前批次还款中所有借款人的账户Id集合.
			List<String> repayUserAccountIds = new ArrayList<String>();
			for (int i = 0; i < projectPlans.size(); i++) {
				WloanTermProjectPlan model = projectPlans.get(i);
				WloanTermProject loanProject = wloanTermProjectService.get(model.getWloanTermProject().getId());
				if (loanProject != null) {
					WloanSubject wloanSubject = wloanSubjectService.get(loanProject.getSubjectId());
					if (wloanSubject != null) {
						// 借款人.
						CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
						if (creditUserInfo != null) {
							CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
							if (creditUserAccount != null) {
								repayUserAccountIds.add(creditUserAccount.getId());
							}
						}
					}
				}
			}
			log.info("借款人账户Id列表：\t" + repayUserAccountIds.toString());
			// 2）借款人账户Id集合去重.
			List<String> pastLeepUserAccountIds = pastLeep(repayUserAccountIds);
			log.info("去重后的借款人账户Id列表：\t" + pastLeepUserAccountIds.toString());
			// 3）为去重后的借款人账户添加账户可用余额.
			List<RepayAvailableAmount> repayAvailableAmountList = new ArrayList<RepayAvailableAmount>();
			for (int i = 0; i < pastLeepUserAccountIds.size(); i++) {
				RepayAvailableAmount repayAvailableAmount = new RepayAvailableAmount();
				CreditUserAccount creditUserAccount = creditUserAccountDao.get(pastLeepUserAccountIds.get(i));
				if (creditUserAccount != null) {
					repayAvailableAmount.setUserAccountId(creditUserAccount.getId());
					repayAvailableAmount.setAvailableAmount(creditUserAccount.getAvailableAmount());
					repayAvailableAmountList.add(repayAvailableAmount);
				}
			}
			log.info("待还款中所有借款人的账户信息：\t" + repayAvailableAmountList.toString());

			for (int x = 0; x < projectPlans.size(); x++) {
				WloanTermProjectPlan model = projectPlans.get(x);
				/**
				 * 业务请求参数封装.
				 */
				// 标的Id.
				String proid = model.getWloanTermProject().getId();
				params.put("bidId", proid);
				// 由网贷平台生成的唯一的交易流水号(项目还款计划ID)projectPlanId.
				params.put("orderId", model.getId());
				// 还款总额.
				Double repayTotalInterest = NumberUtils.scaleDouble(model.getInterest());
				WloanTermProject loanProject = wloanTermProjectService.get(proid);
				if (null != loanProject) { // 非NULL判断.
					WloanSubject wloanSubject = wloanSubjectService.get(loanProject.getSubjectId());
					if (null != wloanSubject) { // 非NULL判断.
						// 借款人.
						CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
						if (null != creditUserInfo) { // 非NULL判断.
							// 还款人，网贷平台唯一的用户编码(还款人必须是原始借款人).
							params.put("payUserId", creditUserInfo.getId());
							CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
							if (null != creditUserAccount) { // 非NULL判断.
								// 程序执行结束标志.
								boolean flag = false;
								// 待还款借款人账户信息.
								for (int y = 0; y < repayAvailableAmountList.size(); y++) {
									RepayAvailableAmount repayAvailableAmount = repayAvailableAmountList.get(y);
									if (repayAvailableAmount != null) {
										if (repayAvailableAmount.getUserAccountId() != null) {
											if (repayAvailableAmount.getUserAccountId().equals(creditUserAccount.getId())) { // 当前还款人.
												if (repayAvailableAmount.getAvailableAmount() < repayTotalInterest) { // 资金不足判断.
													log.info(this.getClass() + "-借款人账户可用余额不足\t" + repayAvailableAmount.getAvailableAmount());
													flag = true;
													// 风控短消息提醒.
													weixinSendTempMsgService.ztmgSendWarnInfoMsg(CRO_MOBILE_PHONE, CRO_NAME, creditUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_1);
													// 财务短消息提醒.
													weixinSendTempMsgService.ztmgSendWarnInfoMsg(CFO_MOBILE_PHONE, CFO_NAME, creditUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_2);
													// 结束本次循环.
													continue;
												} else {
													repayAvailableAmount.setAvailableAmount(NumberUtils.scaleDouble(repayAvailableAmount.getAvailableAmount() - repayTotalInterest));
													// 结束本次循环.
													continue;
												}
											}
										}
									}
								}
								if (flag) { // 资金不足时，结束当前循环
									continue;
								}
							} else {
								log.info(this.getClass() + "-借款人账户不存在");
							}
						} else {
							log.info(this.getClass() + "-借款人帐号不存在");
						}
					} else {
						log.info(this.getClass() + "-融资主体不存在");
					}
				} else {
					log.info(this.getClass() + "-项目不存在");
				}

				// 币种，默认为CNY，以下为可选，CNY (人民币).
				params.put("currency", "CNY");
				// 备注（否）.
				params.put("remark", "还款");
				// 异步通知地址（是）.
				params.put("callbackUrl", CALLBACK_URL_REPAY);
				/**
				 * 公共请求参数封装.
				 */
				// 接口名称：每个接口提供不同的编码.
				params.put("service", "p2p.trade.bid.repay");
				// 签名算法，固定值：RSA.
				params.put("method", "RSA");
				// 由存管银行分配给网贷平台的唯一的商户编码.
				params.put("merchantId", MERCHANT_ID);
				// 请求来源1:(PC)2:(MOBILE).
				params.put("source", "1");
				// PC端无需传入，移动端需传入两位的数字：
				// 第一位表示请求发起自APP还是WAP。（1表示APP，2表示WAP），第二位表示请求来自的操作系统类型。（1表示IOS，2表示Android）注：移动端请求如果传入空，系统将默认按照12处理，可能出现页面样式不兼容.
				// params.put("mobileType", "");
				// 请求时间格式："yyyy-MM-dd HH:mm:ss".
				SimpleDateFormat rt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String requestTime = rt.format(new Date());
				params.put("requestTime", requestTime);
				// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
				params.put("reqSn", IdGen.uuid());
				// 服务版本号.
				params.put("version", "1.0.0");

				log.info(this.getClass() + "-当前项目还款日期-" + DateUtils.formatDate(model.getRepaymentDate(), "yyyy-MM-dd"));
				// 该项目还款计划.
				List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
				// 最后一期还款日期.
				Date lastRepaymentDate = null;
				if (plans != null && plans.size() > 0) {
					lastRepaymentDate = plans.get(plans.size() - 1).getRepaymentDate();
					log.info(this.getClass() + "-当前项目最后一期还款日期-" + DateUtils.formatDate(lastRepaymentDate, "yyyy-MM-dd"));
				}

				// 比较两个时间是否为同一天.
				boolean flag = DateUtils.isSameDate(model.getRepaymentDate(), lastRepaymentDate);
				log.info(this.getClass() + "-是否为最后一期还款-" + flag);

				if (flag) { // 最后一期还款，本金、利息拆分请求存管方接口.

					if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
						// 客户还款计划查询封装.
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject project = new WloanTermProject();
						project.setId(proid);
						entity.setWloanTermProject(project);
						entity.setRepaymentDate(model.getRepaymentDate());
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						// 该批次总条数.
						params.put("totalNum", String.valueOf(userPlanList.size()));
						// 分两次还款.
						for (int i = 1; i < 3; i++) {
							// 还款订单集合.
							List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
							log.info(this.getClass() + "-还本付息分两次还款-第" + i + "次还款");
							if (i == 1) { // 还息.
								// 该批次总金额，单位（分）.
								Double totalAmount = 0D;
								for (WloanTermUserPlan userPlan : userPlanList) {
									RepayOrder order = new RepayOrder();
									// 外部子订单号.
									order.setSubOrderId(userPlan.getId());
									// 收款人，网贷平台唯一的用户编码.
									order.setReceiveUserId(userPlan.getUserInfo().getId());
									// 还本付息，金额(元).
									Double totalInterest = NumberUtils.scaleDouble(userPlan.getInterest());
									// 投资金额.
									Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(proid, userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
									// 还息.
									Double interest = NumberUtils.scaleDouble(totalInterest - investAmount);
									// 该批次总金额，单位（分）.
									totalAmount = totalAmount + interest;
									// 还款金额，单位（分）.
									BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(interest * 100));
									order.setAmount(userPlanInterest.longValue());
									// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
									order.setType("I");
									repayOrderList.add(order);
								}
								params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
								// 该批次总金额，单位（分）.
								log.info(this.getClass() + "-还本付息-最后一期还款-付息总额-" + totalAmount);
								BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
								params.put("totalAmount", b.toString());
								// 每次请求的业务参数的签名值，详细请参考【签名】.
								// "RSA"商户私钥加密签名.
								String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
								params.put("signature", sign);
								String paramsJsonStr = JSON.toJSONString(params);
								log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
								// 商户自己的RSA公钥加密.
								Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
								/**
								 * HTTP.
								 */
								encryptRet.put("merchantId", MERCHANT_ID);
								String requestJsonStr = JSON.toJSONString(encryptRet);
								log.info(this.getClass() + "-请求-" + requestJsonStr);
								String responseStr = HttpUtil.sendPost(HOST, encryptRet);
								log.info(this.getClass() + "-响应-" + responseStr);
								/**
								 * 解析响应.
								 */
								JSONObject jsonObject = JSONObject.parseObject(responseStr);
								String tm = (String) jsonObject.get("tm");
								String data = (String) jsonObject.getString("data");
								String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
								Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
								});
								log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
								/**
								 * 公共响应参数.
								 */
								String respCode = map.get("respCode");
								if (respCode.equals("00")) { // 成功.
									log.info(this.getClass() + "-该批次还本付息-付息-" + map.get("respSubCode") + "|" + map.get("respMsg"));
								} else if (respCode.equals("01")) { // 处理中.
									log.info(this.getClass() + "-该批次还本付息-付息-" + map.get("respSubCode") + "|" + map.get("respMsg"));
								} else if (respCode.equals("02")) { // 失败.
									log.info(this.getClass() + "-该批次还本付息-付息-" + map.get("respSubCode") + "|" + map.get("respMsg"));
								}
							}
							if (i == 2) { // 还本.
								// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
								params.put("reqSn", IdGen.uuid());
								// 由网贷平台生成的唯一的交易流水号(项目还款计划subOrderId).
								params.put("orderId", model.getSubOrderId());
								// 该批次总金额，单位（分）.
								Double totalAmount = 0D;
								for (WloanTermUserPlan userPlan : userPlanList) {
									RepayOrder order = new RepayOrder();
									// 外部子订单号.
									order.setSubOrderId(userPlan.getId().substring(0, userPlan.getId().length() - 1));
									// 收款人，网贷平台唯一的用户编码.
									order.setReceiveUserId(userPlan.getUserInfo().getId());
									// 投资金额.
									Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(proid, userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
									// 该批次总金额，单位（分）.
									totalAmount = totalAmount + investAmount;
									// 还款金额，单位（分）.
									BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(investAmount * 100));
									order.setAmount(userPlanInterest.longValue());
									// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
									order.setType("P");
									repayOrderList.add(order);
								}
								params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
								// 该批次总金额，单位（分）.
								log.info(this.getClass() + "-还本付息-还本总额-" + totalAmount);
								BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
								params.put("totalAmount", b.toString());
								// 每次请求的业务参数的签名值，详细请参考【签名】.
								// "RSA"商户私钥加密签名.
								String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
								params.put("signature", sign);
								String paramsJsonStr = JSON.toJSONString(params);
								log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
								// 商户自己的RSA公钥加密.
								Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
								/**
								 * HTTP.
								 */
								encryptRet.put("merchantId", MERCHANT_ID);
								String requestJsonStr = JSON.toJSONString(encryptRet);
								log.info(this.getClass() + "-请求-" + requestJsonStr);
								String responseStr = HttpUtil.sendPost(HOST, encryptRet);
								log.info(this.getClass() + "-响应-" + responseStr);
								/**
								 * 解析响应.
								 */
								JSONObject jsonObject = JSONObject.parseObject(responseStr);
								String tm = (String) jsonObject.get("tm");
								String data = (String) jsonObject.getString("data");
								String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
								Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
								});
								log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
								/**
								 * 公共响应参数.
								 */
								String respCode = map.get("respCode");
								if (respCode.equals("00")) { // 成功.
									log.info(this.getClass() + "-该批次还本付息-还本-" + map.get("respSubCode") + "|" + map.get("respMsg"));
								} else if (respCode.equals("01")) { // 处理中.
									log.info(this.getClass() + "-该批次还本付息-还本-" + map.get("respSubCode") + "|" + map.get("respMsg"));
								} else if (respCode.equals("02")) { // 失败.
									log.info(this.getClass() + "-该批次还本付息-还本-" + map.get("respSubCode") + "|" + map.get("respMsg"));
								}
							}
						}
					} else if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
						log.info(this.getClass() + "-该批次还本付息-付息-系统原为还本-出现此日志-请联系开发人员解决");
					}
				} else {

					// 该批次项目还款总额.
					Double totalRepayAmount = NumberUtils.scaleDouble(model.getInterest());
					log.info(this.getClass() + "-该批次项目还款总额-" + totalRepayAmount);
					if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
						// 客户还款计划查询封装.
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject project = new WloanTermProject();
						project.setId(proid);
						entity.setWloanTermProject(project);
						entity.setRepaymentDate(model.getRepaymentDate());
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						// 该批次总条数.
						params.put("totalNum", String.valueOf(userPlanList.size()));
						// 还款订单集合.
						List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
						// 该批次总金额，单位（分）.
						Double totalAmount = 0D;
						for (WloanTermUserPlan userPlan : userPlanList) {
							RepayOrder order = new RepayOrder();
							// 外部子订单号.
							order.setSubOrderId(userPlan.getId());
							// 收款人，网贷平台唯一的用户编码.
							order.setReceiveUserId(userPlan.getUserInfo().getId());
							// 还本付息，金额(元).
							Double totalInterest = NumberUtils.scaleDouble(userPlan.getInterest());
							// 该批次总金额，单位（分）.
							totalAmount = totalAmount + totalInterest;
							// 还款金额，单位（分）.
							BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(totalInterest * 100));
							order.setAmount(userPlanInterest.longValue());
							// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
							order.setType("I");
							repayOrderList.add(order);
						}
						params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
						// 该批次总金额，单位（分）.
						log.info(this.getClass() + "-付息总额-" + totalAmount);
						BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
						params.put("totalAmount", b.toString());
						// 每次请求的业务参数的签名值，详细请参考【签名】.
						// "RSA"商户私钥加密签名.
						String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
						params.put("signature", sign);
						String paramsJsonStr = JSON.toJSONString(params);
						log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
						// 商户自己的RSA公钥加密.
						Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
						/**
						 * HTTP.
						 */
						encryptRet.put("merchantId", MERCHANT_ID);
						String requestJsonStr = JSON.toJSONString(encryptRet);
						log.info(this.getClass() + "-请求-" + requestJsonStr);
						String responseStr = HttpUtil.sendPost(HOST, encryptRet);
						log.info(this.getClass() + "-响应-" + responseStr);
						/**
						 * 解析响应.
						 */
						JSONObject jsonObject = JSONObject.parseObject(responseStr);
						String tm = (String) jsonObject.get("tm");
						String data = (String) jsonObject.getString("data");
						String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
						Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
						});
						log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
						/**
						 * 公共响应参数.
						 */
						String respCode = map.get("respCode");
						if (respCode.equals("00")) { // 成功.
							log.info(this.getClass() + "-该批次还款-付息-" + map.get("respSubCode") + "|" + map.get("respMsg"));
						} else if (respCode.equals("01")) { // 处理中.
							log.info(this.getClass() + "-该批次还款-付息-" + map.get("respSubCode") + "|" + map.get("respMsg"));
						} else if (respCode.equals("02")) { // 失败.
							log.info(this.getClass() + "-该批次还款-付息-" + map.get("respSubCode") + "|" + map.get("respMsg"));
						}
					} else if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
						log.info(this.getClass() + "-该批次还款-还本付息-系统原为付息-出现此日志-请联系开发人员解决问题");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: repayPlan <br>
	 * 描述: 代偿户还款-直接代偿. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月17日 上午10:34:40
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void repayPlan() {

		log.info(this.getClass() + "-代偿户还款");

		Map<String, String> params = new HashMap<String, String>();
		try {

			// 项目还款计划.
			WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
			// 项目联查条件.
			WloanTermProject wloanTermProject = new WloanTermProject();
			// 标的流转状态：还款中.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			// 标的产品类型：供应链标的.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			// 状态：还款中.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			// 还款日期开始时间.
			wloanTermProjectPlan.setBeginDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00");
			// 还款日期结束时间.
			wloanTermProjectPlan.setEndDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59");
			// 当天的项目待还款计划列表.
			List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);
			// 没有项目待还款计划.
			if (projectPlans != null && projectPlans.size() == 0) {
				log.info(this.getClass() + "-代偿户还款-没有待还项目还款计划");
				return; // 结束当前方法.
			}

			// 代偿人户可用余额.
			Double purchaserAvailableAmount = 0D;
			// 遍历项目待还款计划，依次向存管方发起请求.
			for (int x = 0; x < projectPlans.size(); x++) {
				WloanTermProjectPlan model = projectPlans.get(x);
				/**
				 * 业务请求参数封装.
				 */
				// 标的Id.
				String proid = model.getWloanTermProject().getId();
				params.put("bidId", proid);
				// 由网贷平台生成的唯一的交易流水号（标的还款计划的主键ID）.
				params.put("orderId", model.getId());
				// 还款总额.
				Double repayTotalInterest = NumberUtils.scaleDouble(model.getInterest());
				// 还款项目.
				WloanTermProject replaceRepayProject = wloanTermProjectService.get(proid);
				if (null != replaceRepayProject) { // 非NULL判断.
					// 是否代偿还款，0：否，1：是.
					if (WloanTermProject.IS_REPLACE_REPAY_1.equals(replaceRepayProject.getIsReplaceRepay())) { // 是.
						// 代偿方，网贷平台唯一的用户编码，代偿还款人ID.
						params.put("payUserId", replaceRepayProject.getReplaceRepayId());
						// 代偿人帐号.
						CreditUserInfo replaceRepayUserInfo = creditUserInfoService.get(replaceRepayProject.getReplaceRepayId());
						if (null != replaceRepayUserInfo) { // 非NULL判断.
							// 代偿人账户.
							CreditUserAccount replaceRepayUserAccount = creditUserAccountDao.get(replaceRepayUserInfo.getAccountId());
							if (null != replaceRepayUserAccount) { // 非NULL判断.
								// 代偿人账户可用余额.
								Double availableAmount = NumberUtils.scaleDouble(replaceRepayUserAccount.getAvailableAmount());
								if (x == 0) { // 第一笔还款代偿户账户余额.
									purchaserAvailableAmount = availableAmount;
								}
								if (purchaserAvailableAmount < repayTotalInterest) { // 账户余额是否满足还款金额.
									log.info(this.getClass() + "-代偿人账户余额不足");
									// 风控短消息提醒.
									weixinSendTempMsgService.ztmgSendWarnInfoMsg(CRO_MOBILE_PHONE, CRO_NAME, replaceRepayUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_1);
									// 财务短消息提醒.
									weixinSendTempMsgService.ztmgSendWarnInfoMsg(CFO_MOBILE_PHONE, CFO_NAME, replaceRepayUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_2);
									// 结束本次循环.
									continue;
								} else { // 余额充足，查询原始借款人.
									// 代偿户可用余额变更.
									purchaserAvailableAmount = purchaserAvailableAmount - repayTotalInterest;
									WloanSubject wloanSubject = wloanSubjectService.get(replaceRepayProject.getSubjectId());
									if (null != wloanSubject) { // 非NULL判断.
										// 借款人.
										CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
										if (null != creditUserInfo) { // 非NULL判断.
											// 原始借款人，网贷平台唯一的用户编码.
											params.put("originalPayUserId", creditUserInfo.getId()); // 借款人ID.
										} else {
											log.info(this.getClass() + "-借款人帐号不存在");
										}
									} else {
										log.info(this.getClass() + "-融资主体不存在");
									}
								}
							} else {
								log.info(this.getClass() + "-代偿人账户不存在");
							}
						} else {
							log.info(this.getClass() + "-代偿人帐号不存在");
						}
					} else if (WloanTermProject.IS_REPLACE_REPAY_0.equals(replaceRepayProject.getIsReplaceRepay())) { // 否.
						log.info(this.getClass() + "-不属于代偿还款项目");
						// 结束本次操作.
						continue;
					}
				} else {
					log.info(this.getClass() + "-项目不存在");
				}
				// 代偿方式：D-直接代偿，I-代垫(间接代偿).
				// D. 直接代偿：代偿方直接将资金还款给投资人.
				// I. 间接代偿：代偿方先将资金打款至借款人账户，再将资金还款给投资人.
				// 默认直接代偿，随需求进行变更.
				params.put("bizType", "D");
				// 币种：CNY -人民币.
				params.put("currency", "CNY");
				// 备注（否）.
				params.put("remark", "代偿还款");
				// 异步通知地址（是）.
				params.put("callbackUrl", CALLBACK_URL_REPLACE_REPAY);
				/**
				 * 公共请求参数封装.
				 */
				// 接口名称：每个接口提供不同的编码.
				params.put("service", "p2p.trade.bid.replacerepay");
				// 签名算法，固定值：RSA.
				params.put("method", "RSA");
				// 由存管银行分配给网贷平台的唯一的商户编码.
				params.put("merchantId", MERCHANT_ID);
				// 请求来源1:(PC)2:(MOBILE).
				params.put("source", "1");
				// PC端无需传入，移动端需传入两位的数字：
				// 第一位表示请求发起自APP还是WAP。（1表示APP，2表示WAP），第二位表示请求来自的操作系统类型。（1表示IOS，2表示Android）注：移动端请求如果传入空，系统将默认按照12处理，可能出现页面样式不兼容.
				// params.put("mobileType", "");
				// 请求时间格式："yyyy-MM-dd HH:mm:ss".
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String requestTime = sdf.format(new Date());
				params.put("requestTime", requestTime);
				// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
				params.put("reqSn", IdGen.uuid());
				// 服务版本号.
				params.put("version", "1.0.0");
				log.info(this.getClass() + "-当前还款日期-" + DateUtils.formatDate(model.getRepaymentDate(), "yyyy-MM-dd"));
				// 该项目还款计划.
				List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
				// 最后一期还款日期.
				Date lastRepaymentDate = null;
				if (plans != null && plans.size() > 0) {
					lastRepaymentDate = plans.get(plans.size() - 1).getRepaymentDate();
					log.info(this.getClass() + "-最后一期还款日期-" + DateUtils.formatDate(lastRepaymentDate, "yyyy-MM-dd"));
				}
				// 比较两个时间是否为同一天.
				boolean flag = DateUtils.isSameDate(model.getRepaymentDate(), lastRepaymentDate);
				log.info(this.getClass() + "-是否是最后一期还款-" + flag);
				if (flag) { // 最后一期还款，本金、利息拆分请求存管方接口.
					// 再次确认是否是还本付息.
					if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
						// 客户还款计划查询封装.
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject project = new WloanTermProject();
						project.setId(proid);
						entity.setWloanTermProject(project);
						entity.setRepaymentDate(model.getRepaymentDate());
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						// 该批次总条数.
						params.put("totalNum", String.valueOf(userPlanList.size()));
						// 分两次还款.
						for (int i = 1; i < 3; i++) {
							// 还款订单集合.
							List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
							log.info(this.getClass() + "-还本付息分两次还完-第-" + i + "-次还款");
							if (i == 1) { // 还息.
								// 该批次总金额，单位（分）.
								Double totalAmount = 0D;
								for (WloanTermUserPlan userPlan : userPlanList) {
									RepayOrder order = new RepayOrder();
									// 外部子订单号.
									order.setSubOrderId(userPlan.getId());
									// 收款人，网贷平台唯一的用户编码.
									order.setReceiveUserId(userPlan.getUserInfo().getId());
									// 还本付息，金额(元).
									Double totalInterest = NumberUtils.scaleDouble(userPlan.getInterest());
									// 投资金额.
									Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(proid, userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
									// 还息.
									Double interest = NumberUtils.scaleDouble(totalInterest - investAmount);
									// 该批次总金额，单位（分）.
									totalAmount = totalAmount + interest;
									// 还款金额，单位（分）.
									BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(interest * 100));
									order.setAmount(userPlanInterest.longValue());
									// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
									order.setType("I");
									repayOrderList.add(order);
								}
								// 还款订单集合-JSON：每次最大条数3000条.
								params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
								// 该批次总金额，单位（分）.
								log.info(this.getClass() + "-还本付息-最后一期还款-付息总额-" + totalAmount);
								BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
								params.put("totalAmount", b.toString());
								// 每次请求的业务参数的签名值，详细请参考【签名】.
								// "RSA"商户私钥加密签名.
								String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
								params.put("signature", sign);
								String paramsJsonStr = JSON.toJSONString(params);
								log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
								// 商户自己的RSA公钥加密.
								Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
								/**
								 * HTTP.
								 */
								encryptRet.put("merchantId", MERCHANT_ID);
								String requestJsonStr = JSON.toJSONString(encryptRet);
								log.info(this.getClass() + "-请求-" + requestJsonStr);
								String responseStr = HttpUtil.sendPost(HOST, encryptRet);
								log.info(this.getClass() + "-响应-" + responseStr);
								/**
								 * 解析响应.
								 */
								JSONObject jsonObject = JSONObject.parseObject(responseStr);
								String tm = (String) jsonObject.get("tm");
								String data = (String) jsonObject.getString("data");
								String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
								Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
								});
								log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
								/**
								 * 公共响应参数.
								 */
								String respCode = map.get("respCode");
								if (respCode.equals("00")) { // 成功.
									log.info(this.getClass() + "-该批次还款类型-还本付息-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("01")) { // 处理中.
									log.info(this.getClass() + "-该批次还款类型-还本付息-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("02")) { // 失败.
									log.info(this.getClass() + "-该批次还款类型-还本付息-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								}
							}
							if (i == 2) { // 还本.
								// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
								params.put("reqSn", IdGen.uuid());
								// 由网贷平台生成的唯一的交易流水号(项目还款计划subOrderId).
								params.put("orderId", model.getSubOrderId());
								// 该批次总金额，单位（分）.
								Double totalAmount = 0D;
								for (WloanTermUserPlan userPlan : userPlanList) {
									RepayOrder order = new RepayOrder();
									// 外部子订单号.
									order.setSubOrderId(userPlan.getId().substring(0, userPlan.getId().length() - 1));
									// 收款人，网贷平台唯一的用户编码.
									order.setReceiveUserId(userPlan.getUserInfo().getId());
									// 投资金额.
									Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(proid, userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
									// 该批次总金额，单位（分）.
									totalAmount = totalAmount + investAmount;
									// 还款金额，单位（分）.
									BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(investAmount * 100));
									order.setAmount(userPlanInterest.longValue());
									// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
									order.setType("P");
									repayOrderList.add(order);
								}
								// 还款订单集合-JSON：每次最大条数3000条.
								params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
								// 该批次总金额，单位（分）.
								log.info(this.getClass() + "-还本付息-最后一期还款-还本总额-" + totalAmount);
								BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
								params.put("totalAmount", b.toString());
								// 每次请求的业务参数的签名值，详细请参考【签名】.
								// "RSA"商户私钥加密签名.
								String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
								params.put("signature", sign);
								String paramsJsonStr = JSON.toJSONString(params);
								log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
								// 商户自己的RSA公钥加密.
								Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
								/**
								 * HTTP.
								 */
								encryptRet.put("merchantId", MERCHANT_ID);
								String requestJsonStr = JSON.toJSONString(encryptRet);
								log.info(this.getClass() + "-请求-" + requestJsonStr);
								String responseStr = HttpUtil.sendPost(HOST, encryptRet);
								log.info(this.getClass() + "-响应-" + responseStr);
								/**
								 * 解析响应.
								 */
								JSONObject jsonObject = JSONObject.parseObject(responseStr);
								String tm = (String) jsonObject.get("tm");
								String data = (String) jsonObject.getString("data");
								String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
								Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
								});
								log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
								/**
								 * 公共响应参数.
								 */
								String respCode = map.get("respCode");
								if (respCode.equals("00")) { // 成功.
									log.info(this.getClass() + "-该批次还款类型-还本付息-还本-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("01")) { // 处理中.
									log.info(this.getClass() + "-该批次还款类型-还本付息-还本-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("02")) { // 失败.
									log.info(this.getClass() + "-该批次还款类型-还本付息-还本-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								}
							}
						}
					} else if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
						log.info(this.getClass() + "-最后一期还款计划-还款类型为付息-系统应该为还本付息-联系开发人员解决问题");
					}
				} else { // 付息.
					// 该批次项目还款总额.
					Double totalRepayAmount = NumberUtils.scaleDouble(model.getInterest());
					log.info(this.getClass() + "-该批次项目还款总额-付息-" + totalRepayAmount);
					if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
						// 客户还款计划查询封装.
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject project = new WloanTermProject();
						project.setId(proid);
						entity.setWloanTermProject(project);
						entity.setRepaymentDate(model.getRepaymentDate());
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						// 该批次总条数.
						params.put("totalNum", String.valueOf(userPlanList.size()));
						// 还款订单集合.
						List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
						// 该批次总金额，单位（分）.
						Double totalAmount = 0D;
						for (WloanTermUserPlan userPlan : userPlanList) {
							RepayOrder order = new RepayOrder();
							// 外部子订单号.
							order.setSubOrderId(userPlan.getId());
							// 收款人，网贷平台唯一的用户编码.
							order.setReceiveUserId(userPlan.getUserInfo().getId());
							// 还本付息，金额(元).
							Double totalInterest = NumberUtils.scaleDouble(userPlan.getInterest());
							// 该批次总金额，单位（分）.
							totalAmount = totalAmount + totalInterest;
							// 还款金额，单位（分）.
							BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(totalInterest * 100));
							order.setAmount(userPlanInterest.longValue());
							// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
							order.setType("I");
							repayOrderList.add(order);
						}
						params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
						// 该批次总金额，单位（分）.
						log.info(this.getClass() + "-付息-其中一期还款-付息总额-" + totalRepayAmount);
						BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
						params.put("totalAmount", b.toString());
						// 每次请求的业务参数的签名值，详细请参考【签名】.
						// "RSA"商户私钥加密签名.
						String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
						params.put("signature", sign);
						String paramsJsonStr = JSON.toJSONString(params);
						log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
						// 商户自己的RSA公钥加密.
						Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
						/**
						 * HTTP.
						 */
						encryptRet.put("merchantId", MERCHANT_ID);
						String requestJsonStr = JSON.toJSONString(encryptRet);
						log.info(this.getClass() + "-请求-" + requestJsonStr);
						String responseStr = HttpUtil.sendPost(HOST, encryptRet);
						log.info(this.getClass() + "-响应-" + responseStr);
						/**
						 * 解析响应.
						 */
						JSONObject jsonObject = JSONObject.parseObject(responseStr);
						String tm = (String) jsonObject.get("tm");
						String data = (String) jsonObject.getString("data");
						String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
						Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
						});
						log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
						/**
						 * 公共响应参数.
						 */
						String respCode = map.get("respCode");
						if (respCode.equals("00")) { // 成功.
							log.info(this.getClass() + "-该批次还款类型-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
						} else if (respCode.equals("01")) { // 处理中.
							log.info(this.getClass() + "-该批次还款类型-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
						} else if (respCode.equals("02")) { // 失败.
							log.info(this.getClass() + "-该批次还款类型-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
						}
					} else if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
						log.info(this.getClass() + "-该批次还款类型-还本付息-系统本应该为付息-联系开发人员解决问题");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: repayPlan_I <br>
	 * 描述: 代偿户还款-间接代偿. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月25日 上午9:45:29
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	// @Scheduled(cron = "0 0 9 * * ?")
	public void repayPlan_I() {

		log.info(this.getClass() + "-代偿户还款");

		Map<String, String> params = new HashMap<String, String>();
		try {

			// 项目还款计划.
			WloanTermProjectPlan wloanTermProjectPlan = new WloanTermProjectPlan();
			// 项目联查条件.
			WloanTermProject wloanTermProject = new WloanTermProject();
			// 标的流转状态：还款中.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			// 标的产品类型：供应链标的.
			wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
			wloanTermProjectPlan.setWloanTermProject(wloanTermProject);
			// 状态：还款中.
			wloanTermProjectPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_1);
			// 还款日期开始时间.
			wloanTermProjectPlan.setBeginDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 00:00:00");
			// 还款日期结束时间.
			wloanTermProjectPlan.setEndDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd") + " 23:59:59");
			// 当天的项目待还款计划列表.
			List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanDao.findList(wloanTermProjectPlan);
			// 没有项目待还款计划.
			if (projectPlans != null && projectPlans.size() == 0) {
				log.info(this.getClass() + "-代偿户还款-没有待还项目还款计划");
				return; // 结束当前方法.
			}

			/**
			 * 封装保存当前批次还款中所有代偿人的账户可用余额.
			 */
			// 1）获取当前批次还款中所有代偿人的账户Id集合.
			List<String> repayUserAccountIds = new ArrayList<String>();
			for (int i = 0; i < projectPlans.size(); i++) {
				WloanTermProjectPlan model = projectPlans.get(i);
				WloanTermProject loanProject = wloanTermProjectService.get(model.getWloanTermProject().getId());
				if (loanProject != null) {
					// 代偿人帐号.
					CreditUserInfo creditUserInfo = creditUserInfoService.get(loanProject.getReplaceRepayId());
					if (creditUserInfo != null) {
						CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
						if (creditUserAccount != null) {
							repayUserAccountIds.add(creditUserAccount.getId());
						}
					}
				}
			}
			log.info("代偿人账户Id列表：\t" + repayUserAccountIds.toString());
			// 2）借款人账户Id集合去重.
			List<String> pastLeepUserAccountIds = pastLeep(repayUserAccountIds);
			log.info("去重后的代偿人账户Id列表：\t" + pastLeepUserAccountIds.toString());
			// 3）为去重后的借款人账户添加账户可用余额.
			List<RepayAvailableAmount> repayAvailableAmountList = new ArrayList<RepayAvailableAmount>();
			for (int i = 0; i < pastLeepUserAccountIds.size(); i++) {
				RepayAvailableAmount repayAvailableAmount = new RepayAvailableAmount();
				CreditUserAccount creditUserAccount = creditUserAccountDao.get(pastLeepUserAccountIds.get(i));
				if (creditUserAccount != null) {
					repayAvailableAmount.setUserAccountId(creditUserAccount.getId());
					repayAvailableAmount.setAvailableAmount(creditUserAccount.getAvailableAmount());
					repayAvailableAmountList.add(repayAvailableAmount);
				}
			}
			log.info("待还款中所有代偿人的账户信息：\t" + repayAvailableAmountList.toString());

			// 遍历项目待还款计划，依次向存管方发起请求.
			for (int x = 0; x < projectPlans.size(); x++) {
				WloanTermProjectPlan model = projectPlans.get(x);
				/**
				 * 业务请求参数封装.
				 */
				// 标的Id.
				String proid = model.getWloanTermProject().getId();
				params.put("bidId", proid);
				// 由网贷平台生成的唯一的交易流水号（标的还款计划的主键ID）.
				params.put("orderId", model.getId());
				// 还款总额.
				Double repayTotalInterest = NumberUtils.scaleDouble(model.getInterest());
				// 还款项目.
				WloanTermProject replaceRepayProject = wloanTermProjectService.get(proid);
				if (null != replaceRepayProject) { // 非NULL判断.
					// 是否代偿还款，0：否，1：是.
					if (WloanTermProject.IS_REPLACE_REPAY_1.equals(replaceRepayProject.getIsReplaceRepay())) { // 是.
						// 代偿方，网贷平台唯一的用户编码，代偿还款人ID.
						params.put("payUserId", replaceRepayProject.getReplaceRepayId());
						// 代偿人帐号.
						CreditUserInfo replaceRepayUserInfo = creditUserInfoService.get(replaceRepayProject.getReplaceRepayId());
						if (null != replaceRepayUserInfo) { // 非NULL判断.
							// 代偿人账户.
							CreditUserAccount replaceRepayUserAccount = creditUserAccountDao.get(replaceRepayUserInfo.getAccountId());
							if (null != replaceRepayUserAccount) { // 非NULL判断.
								// 程序执行结束标志.
								boolean flag = false;
								// 待还款借款人账户信息.
								for (int y = 0; y < repayAvailableAmountList.size(); y++) {
									RepayAvailableAmount repayAvailableAmount = repayAvailableAmountList.get(y);
									if (repayAvailableAmount != null) {
										if (repayAvailableAmount.getUserAccountId() != null) {
											if (repayAvailableAmount.getUserAccountId().equals(replaceRepayUserAccount.getId())) { // 当前还款人.
												if (repayAvailableAmount.getAvailableAmount() < repayTotalInterest) { // 资金不足判断.
													log.info(this.getClass() + "-代偿人账户可用余额不足\t" + repayAvailableAmount.getAvailableAmount());
													flag = true;
													// 风控短消息提醒.
													weixinSendTempMsgService.ztmgSendWarnInfoMsg(CRO_MOBILE_PHONE, CRO_NAME, replaceRepayUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_1);
													// 财务短消息提醒.
													weixinSendTempMsgService.ztmgSendWarnInfoMsg(CFO_MOBILE_PHONE, CFO_NAME, replaceRepayUserInfo.getEnterpriseFullName(), WeixinSendTempMsgService.ZTMG_SEND_WARN_INFO_MSG_2);
													// 结束本次循环.
													continue;
												} else {
													// 可用余额逻辑扣除还款金额.
													repayAvailableAmount.setAvailableAmount(NumberUtils.scaleDouble(repayAvailableAmount.getAvailableAmount() - repayTotalInterest));
													// 余额充足，查询原始借款人
													WloanSubject wloanSubject = wloanSubjectService.get(replaceRepayProject.getSubjectId());
													if (null != wloanSubject) { // 非NULL判断.
														// 借款人.
														CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
														if (null != creditUserInfo) { // 非NULL判断.
															// 原始借款人，网贷平台唯一的用户编码.
															params.put("originalPayUserId", creditUserInfo.getId()); // 借款人ID.
														} else {
															log.info(this.getClass() + "-借款人帐号不存在");
														}
													} else {
														log.info(this.getClass() + "-融资主体不存在");
													}
													// 结束本次循环.
													continue;
												}
											}
										}
									}
								}
								if (flag) { // 资金不足时，结束当前循环
									continue;
								}
							} else {
								log.info(this.getClass() + "-代偿人账户不存在");
							}
						} else {
							log.info(this.getClass() + "-代偿人帐号不存在");
						}
					} else if (WloanTermProject.IS_REPLACE_REPAY_0.equals(replaceRepayProject.getIsReplaceRepay())) { // 否.
						log.info(this.getClass() + "-不属于代偿还款项目");
						// 结束本次操作.
						continue;
					}
				} else {
					log.info(this.getClass() + "-项目不存在");
				}
				// 代偿方式：D-直接代偿，I-代垫(间接代偿).
				// D. 直接代偿：代偿方直接将资金还款给投资人.
				// I. 间接代偿：代偿方先将资金打款至借款人账户，再将资金还款给投资人.
				// 默认直接代偿，随需求进行变更.
				params.put("bizType", "I");
				// 币种：CNY -人民币.
				params.put("currency", "CNY");
				// 备注（否）.
				params.put("remark", "代偿还款");
				// 异步通知地址（是）.
				params.put("callbackUrl", CALLBACK_URL_REPLACE_REPAY);
				/**
				 * 公共请求参数封装.
				 */
				// 接口名称：每个接口提供不同的编码.
				params.put("service", "p2p.trade.bid.replacerepay");
				// 签名算法，固定值：RSA.
				params.put("method", "RSA");
				// 由存管银行分配给网贷平台的唯一的商户编码.
				params.put("merchantId", MERCHANT_ID);
				// 请求来源1:(PC)2:(MOBILE).
				params.put("source", "1");
				// PC端无需传入，移动端需传入两位的数字：
				// 第一位表示请求发起自APP还是WAP。（1表示APP，2表示WAP），第二位表示请求来自的操作系统类型。（1表示IOS，2表示Android）注：移动端请求如果传入空，系统将默认按照12处理，可能出现页面样式不兼容.
				// params.put("mobileType", "");
				// 请求时间格式："yyyy-MM-dd HH:mm:ss".
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String requestTime = sdf.format(new Date());
				params.put("requestTime", requestTime);
				// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
				params.put("reqSn", IdGen.uuid());
				// 服务版本号.
				params.put("version", "1.0.0");
				log.info(this.getClass() + "-当前还款日期-" + DateUtils.formatDate(model.getRepaymentDate(), "yyyy-MM-dd"));
				// 该项目还款计划.
				List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
				// 最后一期还款日期.
				Date lastRepaymentDate = null;
				if (plans != null && plans.size() > 0) {
					lastRepaymentDate = plans.get(plans.size() - 1).getRepaymentDate();
					log.info(this.getClass() + "-最后一期还款日期-" + DateUtils.formatDate(lastRepaymentDate, "yyyy-MM-dd"));
				}
				// 比较两个时间是否为同一天.
				boolean flag = DateUtils.isSameDate(model.getRepaymentDate(), lastRepaymentDate);
				log.info(this.getClass() + "-是否是最后一期还款-" + flag);
				if (flag) { // 最后一期还款，本金、利息拆分请求存管方接口.
					// 再次确认是否是还本付息.
					if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
						// 客户还款计划查询封装.
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject project = new WloanTermProject();
						project.setId(proid);
						entity.setWloanTermProject(project);
						entity.setRepaymentDate(model.getRepaymentDate());
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						// 该批次总条数.
						params.put("totalNum", String.valueOf(userPlanList.size()));
						// 分两次还款.
						for (int i = 1; i < 3; i++) {
							// 还款订单集合.
							List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
							log.info(this.getClass() + "-还本付息分两次还完-第-" + i + "-次还款");
							if (i == 1) { // 还息.
								// 该批次总金额，单位（分）.
								Double totalAmount = 0D;
								for (WloanTermUserPlan userPlan : userPlanList) {
									RepayOrder order = new RepayOrder();
									// 外部子订单号.
									order.setSubOrderId(userPlan.getId());
									// 收款人，网贷平台唯一的用户编码.
									order.setReceiveUserId(userPlan.getUserInfo().getId());
									// 还本付息，金额(元).
									Double totalInterest = NumberUtils.scaleDouble(userPlan.getInterest());
									// 投资金额.
									Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(proid, userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
									// 还息.
									Double interest = NumberUtils.scaleDouble(totalInterest - investAmount);
									// 该批次总金额，单位（分）.
									totalAmount = totalAmount + interest;
									// 还款金额，单位（分）.
									BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(interest * 100));
									order.setAmount(userPlanInterest.longValue());
									// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
									order.setType("I");
									repayOrderList.add(order);
								}
								// 还款订单集合-JSON：每次最大条数3000条.
								params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
								// 该批次总金额，单位（分）.
								log.info(this.getClass() + "-还本付息-最后一期还款-付息总额-" + totalAmount);
								BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
								params.put("totalAmount", b.toString());
								// 每次请求的业务参数的签名值，详细请参考【签名】.
								// "RSA"商户私钥加密签名.
								String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
								params.put("signature", sign);
								String paramsJsonStr = JSON.toJSONString(params);
								log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
								// 商户自己的RSA公钥加密.
								Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
								/**
								 * HTTP.
								 */
								encryptRet.put("merchantId", MERCHANT_ID);
								String requestJsonStr = JSON.toJSONString(encryptRet);
								log.info(this.getClass() + "-请求-" + requestJsonStr);
								String responseStr = HttpUtil.sendPost(HOST, encryptRet);
								log.info(this.getClass() + "-响应-" + responseStr);
								/**
								 * 解析响应.
								 */
								JSONObject jsonObject = JSONObject.parseObject(responseStr);
								String tm = (String) jsonObject.get("tm");
								String data = (String) jsonObject.getString("data");
								String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
								Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
								});
								log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
								/**
								 * 公共响应参数.
								 */
								String respCode = map.get("respCode");
								if (respCode.equals("00")) { // 成功.
									log.info(this.getClass() + "-该批次还款类型-还本付息-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("01")) { // 处理中.
									log.info(this.getClass() + "-该批次还款类型-还本付息-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("02")) { // 失败.
									log.info(this.getClass() + "-该批次还款类型-还本付息-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								}
							}
							if (i == 2) { // 还本.
								// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
								params.put("reqSn", IdGen.uuid());
								// 由网贷平台生成的唯一的交易流水号(项目还款计划subOrderId).
								params.put("orderId", model.getSubOrderId());
								// 该批次总金额，单位（分）.
								Double totalAmount = 0D;
								for (WloanTermUserPlan userPlan : userPlanList) {
									RepayOrder order = new RepayOrder();
									// 外部子订单号.
									order.setSubOrderId(userPlan.getId().substring(0, userPlan.getId().length() - 1));
									// 收款人，网贷平台唯一的用户编码.
									order.setReceiveUserId(userPlan.getUserInfo().getId());
									// 投资金额.
									Double investAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(proid, userPlan.getUserInfo().getId(), userPlan.getWloanTermInvest().getId());
									// 该批次总金额，单位（分）.
									totalAmount = totalAmount + investAmount;
									// 还款金额，单位（分）.
									BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(investAmount * 100));
									order.setAmount(userPlanInterest.longValue());
									// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
									order.setType("P");
									repayOrderList.add(order);
								}
								// 还款订单集合-JSON：每次最大条数3000条.
								params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
								// 该批次总金额，单位（分）.
								log.info(this.getClass() + "-还本付息-最后一期还款-还本总额-" + totalAmount);
								BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
								params.put("totalAmount", b.toString());
								// 每次请求的业务参数的签名值，详细请参考【签名】.
								// "RSA"商户私钥加密签名.
								String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
								params.put("signature", sign);
								String paramsJsonStr = JSON.toJSONString(params);
								log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
								// 商户自己的RSA公钥加密.
								Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
								/**
								 * HTTP.
								 */
								encryptRet.put("merchantId", MERCHANT_ID);
								String requestJsonStr = JSON.toJSONString(encryptRet);
								log.info(this.getClass() + "-请求-" + requestJsonStr);
								String responseStr = HttpUtil.sendPost(HOST, encryptRet);
								log.info(this.getClass() + "-响应-" + responseStr);
								/**
								 * 解析响应.
								 */
								JSONObject jsonObject = JSONObject.parseObject(responseStr);
								String tm = (String) jsonObject.get("tm");
								String data = (String) jsonObject.getString("data");
								String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
								Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
								});
								log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
								/**
								 * 公共响应参数.
								 */
								String respCode = map.get("respCode");
								if (respCode.equals("00")) { // 成功.
									log.info(this.getClass() + "-该批次还款类型-还本付息-还本-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("01")) { // 处理中.
									log.info(this.getClass() + "-该批次还款类型-还本付息-还本-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								} else if (respCode.equals("02")) { // 失败.
									log.info(this.getClass() + "-该批次还款类型-还本付息-还本-" + map.get("respSubCode") + "-" + map.get("respMsg"));
								}
							}
						}
					} else if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
						log.info(this.getClass() + "-最后一期还款计划-还款类型为付息-系统应该为还本付息-联系开发人员解决问题");
					}
				} else { // 付息.
					// 该批次项目还款总额.
					Double totalRepayAmount = NumberUtils.scaleDouble(model.getInterest());
					log.info(this.getClass() + "-该批次项目还款总额-付息-" + totalRepayAmount);
					if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
						// 客户还款计划查询封装.
						WloanTermUserPlan entity = new WloanTermUserPlan();
						entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
						WloanTermProject project = new WloanTermProject();
						project.setId(proid);
						entity.setWloanTermProject(project);
						entity.setRepaymentDate(model.getRepaymentDate());
						List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
						// 该批次总条数.
						params.put("totalNum", String.valueOf(userPlanList.size()));
						// 还款订单集合.
						List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
						// 该批次总金额，单位（分）.
						Double totalAmount = 0D;
						for (WloanTermUserPlan userPlan : userPlanList) {
							RepayOrder order = new RepayOrder();
							// 外部子订单号.
							order.setSubOrderId(userPlan.getId());
							// 收款人，网贷平台唯一的用户编码.
							order.setReceiveUserId(userPlan.getUserInfo().getId());
							// 还本付息，金额(元).
							Double totalInterest = NumberUtils.scaleDouble(userPlan.getInterest());
							// 该批次总金额，单位（分）.
							totalAmount = totalAmount + totalInterest;
							// 还款金额，单位（分）.
							BigDecimal userPlanInterest = new BigDecimal(NumberUtils.scaleDouble(totalInterest * 100));
							order.setAmount(userPlanInterest.longValue());
							// 本笔还款的交易类型：C-网贷平台佣金，P-本金,I-利息，S-分润.
							order.setType("I");
							repayOrderList.add(order);
						}
						params.put("repayOrderList", JSONArray.toJSONString(repayOrderList));
						// 该批次总金额，单位（分）.
						log.info(this.getClass() + "-付息-其中一期还款-付息总额-" + totalRepayAmount);
						BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
						params.put("totalAmount", b.toString());
						// 每次请求的业务参数的签名值，详细请参考【签名】.
						// "RSA"商户私钥加密签名.
						String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
						params.put("signature", sign);
						String paramsJsonStr = JSON.toJSONString(params);
						log.info(this.getClass() + "-参数列表-" + paramsJsonStr);
						// 商户自己的RSA公钥加密.
						Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
						/**
						 * HTTP.
						 */
						encryptRet.put("merchantId", MERCHANT_ID);
						String requestJsonStr = JSON.toJSONString(encryptRet);
						log.info(this.getClass() + "-请求-" + requestJsonStr);
						String responseStr = HttpUtil.sendPost(HOST, encryptRet);
						log.info(this.getClass() + "-响应-" + responseStr);
						/**
						 * 解析响应.
						 */
						JSONObject jsonObject = JSONObject.parseObject(responseStr);
						String tm = (String) jsonObject.get("tm");
						String data = (String) jsonObject.getString("data");
						String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
						Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
						});
						log.info(this.getClass() + "-解析响应-" + JSON.toJSONString(map));
						/**
						 * 公共响应参数.
						 */
						String respCode = map.get("respCode");
						if (respCode.equals("00")) { // 成功.
							log.info(this.getClass() + "-该批次还款类型-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
						} else if (respCode.equals("01")) { // 处理中.
							log.info(this.getClass() + "-该批次还款类型-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
						} else if (respCode.equals("02")) { // 失败.
							log.info(this.getClass() + "-该批次还款类型-付息-" + map.get("respSubCode") + "-" + map.get("respMsg"));
						}
					} else if (model.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
						log.info(this.getClass() + "-该批次还款类型-还本付息-系统本应该为付息-联系开发人员解决问题");
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 方法: pastLeep <br>
	 * 描述: List去重. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月23日 上午11:31:16
	 * 
	 * @param list
	 * @return
	 */
	public static List<String> pastLeep(List<String> list) {

		List<String> listNew = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		for (String str : list) {
			if (set.add(str)) {
				listNew.add(str);
			}
		}
		return listNew;
	}

}
