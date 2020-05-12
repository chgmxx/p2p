package com.power.platform.lanmao.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.pojo.RepayOrder;
import com.power.platform.cgb.pojo.ShareProfitOrder;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IsHolidayOrBirthday;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.basicinfo.CreditBasicInfoService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.ifcert.creditor.service.CreditorDataAccessService;
import com.power.platform.ifcert.dao.IfCertUserInfoDao;
import com.power.platform.ifcert.entity.IfCertUserInfo;
import com.power.platform.ifcert.lendProductConfig.service.LendProductConfigDataAccessService;
import com.power.platform.ifcert.lendparticulars.service.LendParticularsDataAccessService;
import com.power.platform.ifcert.lendproduct.service.LendProductDataAccessService;
import com.power.platform.ifcert.repayplan.service.RepayPlanDataAccessService;
import com.power.platform.ifcert.status.service.ScatterInvestStatusDataAccessService;
import com.power.platform.ifcert.transact.service.TransactDataAccessService;
import com.power.platform.ifcert.userInfo.service.IfcertUserInfoDataAccessService;
import com.power.platform.lanmao.trade.service.LanMaoProjectService;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.UniversalCodeEnum;
import com.power.platform.lanmao.type.ProjectStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.UserInvestWebService;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.lanmao.trade.service.LanMaoPreTransactionService;
import com.power.platform.lanmao.trade.pojo.CancelPreTransaction;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;

/**
 * 
 * 类: P2pTradeBidController <br>
 * 描述: 网贷资金存管接口，交易类. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年5月23日 上午9:00:38
 */
@Controller
@RequestMapping(value = "${adminPath}/lm/p2p/trade")
public class LanMaoP2pTradeController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(LanMaoP2pTradeController.class);

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
	 * 受托支付提现回调地址.
	 */
	private static final String CALLBACK_URL_ENTRUSTED_WITHDRAW = Global.getConfigUb("CALLBACK_URL_ENTRUSTED_WITHDRAW");
	/**
	 * 放款回调地址.
	 */
	private static final String CALLBACK_URL_GRANT = Global.getConfigUb("CALLBACK_URL_GRANT");
	/**
	 * 流标回调地址.
	 */
	// private static final String CALLBACK_URL_CANCEL = Global.getConfigUb("CALLBACK_URL_CANCEL");
	/**
	 * 还款回调地址.
	 */
	private static final String CALLBACK_URL_REPAY = Global.getConfigUb("CALLBACK_URL_REPAY");
	/**
	 * 代偿还款回调地址.
	 */
	private static final String CALLBACK_URL_REPLACE_REPAY = Global.getConfigUb("CALLBACK_URL_REPLACE_REPAY");

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditBasicInfoService creditBasicInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CreditUserAccountDao creditUserAccountDao;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private UserInfoDao userInfoDao;

	@Resource
	private IfCertUserInfoDao ifCertUserInfoDao;

	@Autowired
	private LendProductDataAccessService lendProductDataAccessService;
	@Autowired
	private ScatterInvestStatusDataAccessService scatterInvestStatusDataAccessService;
	@Autowired
	private CreditorDataAccessService creditorDataAccessService;
	@Autowired
	private RepayPlanDataAccessService repayPlanDataAccessService;
	@Autowired
	private LendProductConfigDataAccessService lendProductConfigDataAccessService;
	@Autowired
	private TransactDataAccessService transactDataAccessService;
	@Autowired
	private LendParticularsDataAccessService lendParticularsDataAccessService;
	@Autowired
	private IfcertUserInfoDataAccessService ifcertUserInfoDataAccessService;

	@Autowired
	private LanMaoProjectService lanMaoProjectService;

	@Autowired
	private LanMaoPreTransactionService lanMaoPreTransactionService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Autowired
	private UserVouchersHistoryService userVouchersHistoryService;
	@Autowired
	private UserVouchersHistoryDao userVouchersHistoryDao;
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Autowired
	private LmTransactionDao transactionDao;

	@ModelAttribute
	public WloanTermProject get(@RequestParam(required = false) String id) {

		WloanTermProject entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = wloanTermProjectService.get(id);
		}
		if (entity == null) {
			entity = new WloanTermProject();
		}
		return entity;
	}

	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * 方法: replaceRepay <br>
	 * 描述: 代偿还款. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月19日 上午10:22:18 <br>
	 * 功能介绍:借款人不能按时还款时可由代偿方（代偿方账户类型为：网贷平台户、代偿账户、担保账户）替借款人还款给投资人<br>
	 * 交互流程：<br>
	 * 1. 借款人不能按时还款时，代偿方发起代偿还款<br>
	 * 2. 网贷平台计算利息、收益、收费、分润并指定代偿方式（直接代偿/间接代偿）向存管系统发起申请<br>
	 * 3. 存管系统接收到申请后校验代偿方、标的信息、还款信息，同步返回受理结果<br>
	 * 4. 存管系统异步逐笔将本金、利息、收费、分润划转至指定账户<br>
	 * 5. 处理完成后将结果通知给网贷平台<br>
	 * 6. 网贷平台接收到异步通知后需给出响应
	 * 
	 * @param repaymentDate
	 *            还款日期
	 * @param ip
	 *            IP
	 * @param projectPlanId
	 *            项目还款计划，还款ID
	 * @param proid
	 *            项目ID
	 * @param type
	 *            标的产品类型
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:replaceRepay:edit")
	@RequestMapping(value = "replaceRepay")
	@Transactional(rollbackFor = Exception.class)
	public synchronized String replaceRepay(Date repaymentDate, String ip, String projectPlanId, String proid, String type, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("fn:replaceRepay-代偿方还款");
		Map<String, String> params = new HashMap<String, String>();
		/**
		 * 业务请求参数封装.
		 */
		// 标的Id.
		params.put("bidId", proid);
		// 由网贷平台生成的唯一的交易流水号（标的还款计划的主键ID）.
		params.put("orderId", projectPlanId);
		// 项目还款计划.
		WloanTermProjectPlan projectPlan = wloanTermProjectPlanService.get(projectPlanId);
		if (null != projectPlan) { // 项目还款计划详情.
			// 还款总额.
			Double repayTotalInterest = NumberUtils.scaleDouble(projectPlan.getInterest());
			WloanTermProject replaceRepayProject = wloanTermProjectService.get(proid);
			if (null != replaceRepayProject) { // 项目详情.
				// 是否代偿还款，0：否，1：是.
				if (WloanTermProject.IS_REPLACE_REPAY_1.equals(replaceRepayProject.getIsReplaceRepay())) { // 是.
					// 代偿方，网贷平台唯一的用户编码.
					params.put("payUserId", replaceRepayProject.getReplaceRepayId()); // 代偿还款人ID.
					// 代偿人帐号.
					CreditUserInfo replaceRepayUserInfo = creditUserInfoService.get(replaceRepayProject.getReplaceRepayId());
					if (null != replaceRepayUserInfo) { // 非空判断.
						// 代偿人账户.
						CreditUserAccount replaceRepayUserAccount = creditUserAccountDao.get(replaceRepayUserInfo.getAccountId());
						if (null != replaceRepayUserAccount) { // 非空判断.
							Double availableAmount = NumberUtils.scaleDouble(replaceRepayUserAccount.getAvailableAmount()); // 可用余额.
							if (availableAmount < repayTotalInterest) { // 账户余额是否满足还款金额.
								addMessage(redirectAttributes, "代偿人账户余额不足");
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							} else { // 余额充足，查询原始借款人.
								WloanSubject wloanSubject = wloanSubjectService.get(replaceRepayProject.getSubjectId());
								if (null != wloanSubject) { // 融资主体，非空判断.
									// 借款人.
									CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
									if (null != creditUserInfo) { // 借款人帐号，非空判断.
										// 原始借款人，网贷平台唯一的用户编码.
										params.put("originalPayUserId", creditUserInfo.getId()); // 借款人ID.
									} else {
										addMessage(redirectAttributes, "借款帐号不存在");
										if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
											return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
										} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
											return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
										}
									}
								} else {
									addMessage(redirectAttributes, "融资主体不存在.");
									if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
										return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
									} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
										return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
									}
								}
							}
						} else {
							addMessage(redirectAttributes, "代偿账户不存在");
							if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
								return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
							} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
								return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
							}
						}
					} else {
						addMessage(redirectAttributes, "代偿帐号不存在");
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					}
				} else if (WloanTermProject.IS_REPLACE_REPAY_0.equals(replaceRepayProject.getIsReplaceRepay())) { // 否.
					addMessage(redirectAttributes, "确认该项目是否代偿还款");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			} else {
				addMessage(redirectAttributes, "项目信息不存在");
				if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				}
			}
		} else {
			addMessage(redirectAttributes, "项目还款计划信息不存在");
			if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
			} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
			}
		}

		// 代偿方式：D-直接代偿，I-代垫(间接代偿).
		// D. 直接代偿：代偿方直接将资金还款给投资人.
		// I. 间接代偿：代偿方先将资金打款至借款人账户，再将资金还款给投资人.
		// 默认直接代偿，随需求进行变更.
		params.put("bizType", "I"); // 需求变更-间接代偿.
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

		log.info("fn:replaceRepay-项目当前还款日期：" + DateUtils.formatDate(repaymentDate, "yyyy-MM-dd"));
		// 该项目还款计划.
		List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
		// 最后一期还款日期.
		Date lastRepaymentDate = null;
		if (plans != null && plans.size() > 0) {
			lastRepaymentDate = plans.get(plans.size() - 1).getRepaymentDate();
			log.info("fn:replaceRepay--项目最后一期还款日期：" + DateUtils.formatDate(lastRepaymentDate, "yyyy-MM-dd"));
		}
		// 比较两个时间是否为同一天.
		boolean flag = DateUtils.isSameDate(repaymentDate, lastRepaymentDate);
		log.info("fn:replaceRepay-是否为最后一期还款：" + flag);

		if (null != projectPlan) { // 项目还款计划详情.
			if (flag) { // 最后一期还款，本金、利息拆分请求存管方接口.
				// 再次确认是否是还本付息.
				if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
					// 客户还款计划查询封装.
					WloanTermUserPlan entity = new WloanTermUserPlan();
					entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					WloanTermProject project = new WloanTermProject();
					project.setId(proid);
					entity.setWloanTermProject(project);
					entity.setRepaymentDate(repaymentDate);
					List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
					// 该批次总条数.
					params.put("totalNum", String.valueOf(userPlanList.size()));
					// 分两次还款.
					for (int i = 1; i < 3; i++) {
						// 还款订单集合.
						List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
						log.info("fn:replaceRepay-还款次数：" + i);
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
							log.info("fn:replaceRepay-还本付息，最后一期还款，付息总额：" + totalAmount);
							BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
							params.put("totalAmount", b.toString());
							// 每次请求的业务参数的签名值，详细请参考【签名】.
							// "RSA"商户私钥加密签名.
							String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
							params.put("signature", sign);
							String paramsJsonStr = JSON.toJSONString(params);
							System.err.println("参数列表：" + paramsJsonStr);
							// 商户自己的RSA公钥加密.
							Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
							/**
							 * HTTP.
							 */
							encryptRet.put("merchantId", MERCHANT_ID);
							String requestJsonStr = JSON.toJSONString(encryptRet);
							System.err.println("请求：" + requestJsonStr);
							String responseStr = HttpUtil.sendPost(HOST, encryptRet);
							System.err.println("响应：" + responseStr);
							/**
							 * 解析响应.
							 */
							JSONObject jsonObject = JSONObject.parseObject(responseStr);
							String tm = (String) jsonObject.get("tm");
							String data = (String) jsonObject.getString("data");
							String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
							Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
							});
							System.out.println("解析响应：" + JSON.toJSONString(map));
							/**
							 * 公共响应参数.
							 */
							String respCode = map.get("respCode");
							if (respCode.equals("00")) { // 成功.
								log.info("fn:replaceRepay-该次还款类型-还本付息-付息：" + map.get("respSubCode") + "：" + map.get("respMsg"));
							} else if (respCode.equals("01")) { // 处理中.
								log.info("fn:replaceRepay-该次还款类型-还本付息-付息：" + map.get("respSubCode") + "：" + map.get("respMsg"));
							} else if (respCode.equals("02")) { // 失败.
								log.info("fn:replaceRepay-该次还款类型-还本付息-付息：" + map.get("respSubCode") + "：" + map.get("respMsg"));
								addMessage(redirectAttributes, "代偿方还款-还本付息-付息" + map.get("respSubCode") + "：" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							}
						}
						if (i == 2) { // 还本.
							// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
							params.put("reqSn", IdGen.uuid());
							// 由网贷平台生成的唯一的交易流水号(项目还款计划subOrderId).
							params.put("orderId", projectPlan.getSubOrderId());
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
							log.info("fn:replaceRepay-还本付息，最后一期还款，还本总额：" + totalAmount);
							BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
							params.put("totalAmount", b.toString());
							// 每次请求的业务参数的签名值，详细请参考【签名】.
							// "RSA"商户私钥加密签名.
							String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
							params.put("signature", sign);
							String paramsJsonStr = JSON.toJSONString(params);
							System.err.println("参数列表：" + paramsJsonStr);
							// 商户自己的RSA公钥加密.
							Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
							/**
							 * HTTP.
							 */
							encryptRet.put("merchantId", MERCHANT_ID);
							String requestJsonStr = JSON.toJSONString(encryptRet);
							System.err.println("请求：" + requestJsonStr);
							String responseStr = HttpUtil.sendPost(HOST, encryptRet);
							System.err.println("响应：" + responseStr);
							/**
							 * 解析响应.
							 */
							JSONObject jsonObject = JSONObject.parseObject(responseStr);
							String tm = (String) jsonObject.get("tm");
							String data = (String) jsonObject.getString("data");
							String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
							Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
							});
							System.out.println("解析响应：" + JSON.toJSONString(map));
							/**
							 * 公共响应参数.
							 */
							String respCode = map.get("respCode");
							if (respCode.equals("00")) { // 成功.
								log.info("fn:replaceRepay-该次还款类型-还本付息-付息：" + map.get("respSubCode") + "：" + map.get("respMsg"));
								addMessage(redirectAttributes, "代偿方还款-还本付息-付息" + map.get("respSubCode") + "：" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							} else if (respCode.equals("01")) { // 处理中.
								log.info("fn:replaceRepay-该次还款类型-还本付息-付息：" + map.get("respSubCode") + "：" + map.get("respMsg"));
								addMessage(redirectAttributes, "代偿方还款-还本付息-付息" + map.get("respSubCode") + "：" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							} else if (respCode.equals("02")) { // 失败.
								log.info("fn:replaceRepay-该次还款类型-还本付息-付息：" + map.get("respSubCode") + "：" + map.get("respMsg"));
								addMessage(redirectAttributes, "代偿方还款-还本付息-付息" + map.get("respSubCode") + "：" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							}
						}
					}
				} else if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
					addMessage(redirectAttributes, "代偿方还款类型-付息-请联系开发人员");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			} else { // 付息.
				// 该批次项目还款总额.
				Double totalRepayAmount = NumberUtils.scaleDouble(projectPlan.getInterest());
				log.info("fn:replaceRepay-该批次项目还款总额：" + totalRepayAmount);
				if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
					// 客户还款计划查询封装.
					WloanTermUserPlan entity = new WloanTermUserPlan();
					entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					WloanTermProject project = new WloanTermProject();
					project.setId(proid);
					entity.setWloanTermProject(project);
					entity.setRepaymentDate(repaymentDate);
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
					log.info("付息，其中一期还款，付息总额：" + totalAmount);
					BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
					params.put("totalAmount", b.toString());
					// 每次请求的业务参数的签名值，详细请参考【签名】.
					// "RSA"商户私钥加密签名.
					String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
					params.put("signature", sign);
					String paramsJsonStr = JSON.toJSONString(params);
					System.err.println("参数列表：" + paramsJsonStr);
					// 商户自己的RSA公钥加密.
					Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
					/**
					 * HTTP.
					 */
					encryptRet.put("merchantId", MERCHANT_ID);
					String requestJsonStr = JSON.toJSONString(encryptRet);
					System.err.println("请求：" + requestJsonStr);
					String responseStr = HttpUtil.sendPost(HOST, encryptRet);
					System.err.println("响应：" + responseStr);
					/**
					 * 解析响应.
					 */
					JSONObject jsonObject = JSONObject.parseObject(responseStr);
					String tm = (String) jsonObject.get("tm");
					String data = (String) jsonObject.getString("data");
					String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
					Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
					});
					System.out.println("解析响应：" + JSON.toJSONString(map));
					/**
					 * 公共响应参数.
					 */
					String respCode = map.get("respCode");
					if (respCode.equals("00")) { // 成功.
						log.info("fn:replaceRepay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						addMessage(redirectAttributes, "代偿方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					} else if (respCode.equals("01")) { // 处理中.
						log.info("fn:replaceRepay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						addMessage(redirectAttributes, "代偿方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					} else if (respCode.equals("02")) { // 失败.
						log.info("fn:replaceRepay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						addMessage(redirectAttributes, "代偿方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					}
				} else if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
					addMessage(redirectAttributes, "代偿方还款类型-还本付息-请联系开发人员");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			}
		}

		if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
		} // 供应链.
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
	}

	/**
	 * 
	 * 方法: entrustedWithdraw <br>
	 * 描述: 受托支付提现. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月6日 上午10:22:46
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:entrustedWithdraw:edit")
	@RequestMapping(value = "entrustedWithdraw")
	@Transactional(rollbackFor = Exception.class)
	public String entrustedWithdraw(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("受托支付提现");

		Map<String, String> params = new HashMap<String, String>();
		/**
		 * 业务请求参数封装.
		 */
		// 原放款单号.
		params.put("grandOrderId", wloanTermProject.getId());
		// 由网贷平台生成的唯一的交易流水号.
		params.put("orderId", wloanTermProject.getId());
		// 金额，单位（分）.
		BigDecimal amount = new BigDecimal(NumberUtils.scaleDouble(wloanTermProject.getAmount() * 100));
		params.put("amount", amount.toString());
		// 币种，默认为CNY，以下为可选，CNY (人民币).
		params.put("currency", "CNY");
		// 融资主体.
		WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
		if (null != wloanSubject) {
			// 受托人银行卡号（否）.
			String cashierBankNo = wloanSubject.getCashierBankNo();
			params.put("bankCardNO", cashierBankNo);
			// 受托人银行代码（否）.
			String cashierBankCode = wloanSubject.getCashierBankCode();
			params.put("bankCode", cashierBankCode);
			// 联行号，账户对公时，必填（否）.
			String cashierBankIssuer = wloanSubject.getCashierBankIssuer();
			params.put("issuer", cashierBankIssuer);
			// 受托人（银行户名）（否）.
			String cashierUser = wloanSubject.getCashierUser();
			params.put("cardName", cashierUser);
			// 账户对公对私标识，1：对公，2：对私 ，默认：2 （对私）（否）.
			String cashierBankNoFlag = wloanSubject.getCashierBankNoFlag();
			params.put("cardFlag", cashierBankNoFlag);
		}
		// 备注（否）.
		params.put("remark", "受托支付提现");
		// 异步通知地址（是）.
		params.put("callbackUrl", CALLBACK_URL_ENTRUSTED_WITHDRAW);
		/**
		 * 公共请求参数封装.
		 */
		// 接口名称：每个接口提供不同的编码.
		params.put("service", "p2p.trade.bid.entrustedwithdraw");
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
		// 每次请求的业务参数的签名值，详细请参考【签名】.
		// "RSA"商户私钥加密签名.
		String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
		params.put("signature", sign);
		String paramsJsonStr = JSON.toJSONString(params);
		log.info("参数列表：" + paramsJsonStr);
		// 商户自己的RSA公钥加密.
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
		/**
		 * HTTP.
		 */
		encryptRet.put("merchantId", MERCHANT_ID);
		String requestJsonStr = JSON.toJSONString(encryptRet);
		log.info("请求：" + requestJsonStr);
		String responseStr = HttpUtil.sendPost(HOST, encryptRet);
		log.info("响应：" + responseStr);

		/**
		 * 解析响应.
		 */
		JSONObject jsonObject = JSONObject.parseObject(responseStr);
		String tm = (String) jsonObject.get("tm");
		String data = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		log.info("解析响应：" + JSON.toJSONString(map));
		// 页面消息通知.
		// model.addAttribute("respCode", map.get("respCode"));
		// model.addAttribute("respMsg", map.get("respMsg"));
		// model.addAttribute("respSubCode", map.get("respSubCode"));
		addMessage(redirectAttributes, map.get("respMsg"));
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	/**
	 * 
	 * 方法: repay <br>
	 * 描述: 还款. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月12日 下午3:00:40
	 * 
	 * @param repaymentDate
	 *            还款日期
	 * @param ip
	 *            IP
	 * @param projectPlanId
	 *            项目还款计划，还款ID
	 * @param proid
	 *            项目ID
	 * @param type
	 *            标的产品类型
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:repay:edit")
	@RequestMapping(value = "repay")
	@Transactional(rollbackFor = Exception.class)
	public synchronized String repay(Date repaymentDate, String ip, String projectPlanId, String proid, String type, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("fn:repay-借款方还款");

		// 参数列表.
		Map<String, String> params = new HashMap<String, String>();
		// 项目还款计划.
		WloanTermProjectPlan projectPlan = wloanTermProjectPlanService.get(projectPlanId);

		/**
		 * 业务请求参数封装.
		 */
		// 标的Id.
		params.put("bidId", proid);
		// 由网贷平台生成的唯一的交易流水号(项目还款计划ID)projectPlanId.
		params.put("orderId", projectPlanId);
		if (null != projectPlan) { // 项目还款计划详情.
			// 还款总额.
			Double repayTotalInterest = NumberUtils.scaleDouble(projectPlan.getInterest());
			WloanTermProject loanProject = wloanTermProjectService.get(proid);
			if (null != loanProject) { // 项目详情.
				WloanSubject wloanSubject = wloanSubjectService.get(loanProject.getSubjectId());
				if (null != wloanSubject) { // 融资主体.
					// 借款人.
					CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
					if (null != creditUserInfo) {
						// 还款人，网贷平台唯一的用户编码(还款人必须是原始借款人).
						params.put("payUserId", creditUserInfo.getId());
						CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
						if (null != creditUserAccount) { // 借款人账户.
							// 可用余额.
							Double availableAmount = NumberUtils.scaleDouble(creditUserAccount.getAvailableAmount());
							if (availableAmount < repayTotalInterest) {
								addMessage(redirectAttributes, "借款人账户余额不足.");
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							}
						}
					}
				} else {
					addMessage(redirectAttributes, "融资主体不存在.");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			} else {
				addMessage(redirectAttributes, "该项目不存在.");
				if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
				}
			}
		} else {
			addMessage(redirectAttributes, "该批次项目还款计划不存在.");
			if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
			} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
			}
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

		log.info("fn:repay-项目当前还款日期：" + DateUtils.formatDate(repaymentDate, "yyyy-MM-dd"));
		// 该项目还款计划.
		List<WloanTermProjectPlan> plans = wloanTermProjectPlanService.findListByProjectId(proid);
		// 最后一期还款日期.
		Date lastRepaymentDate = null;
		if (plans != null && plans.size() > 0) {
			lastRepaymentDate = plans.get(plans.size() - 1).getRepaymentDate();
			log.info("fn:repay-项目最后一期还款日期：" + DateUtils.formatDate(lastRepaymentDate, "yyyy-MM-dd"));
		}

		// 比较两个时间是否为同一天.
		boolean flag = DateUtils.isSameDate(repaymentDate, lastRepaymentDate);
		log.info("fn:repay-是否为最后一期还款：" + flag);

		if (null != projectPlan) { // 项目还款计划详情.
			if (flag) { // 最后一期还款，本金、利息拆分请求存管方接口.
				if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
					// 客户还款计划查询封装.
					WloanTermUserPlan entity = new WloanTermUserPlan();
					entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					WloanTermProject project = new WloanTermProject();
					project.setId(proid);
					entity.setWloanTermProject(project);
					entity.setRepaymentDate(repaymentDate);
					List<WloanTermUserPlan> userPlanList = wloanTermUserPlanDao.findList(entity);
					// 该批次总条数.
					params.put("totalNum", String.valueOf(userPlanList.size()));
					// 分两次还款.
					for (int i = 1; i < 3; i++) {
						// 还款订单集合.
						List<RepayOrder> repayOrderList = new ArrayList<RepayOrder>();
						log.info("fn:repay-还款次数：" + i);
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
							log.info("fn:repay-还本付息，最后一期还款，付息总额：" + totalAmount);
							BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
							params.put("totalAmount", b.toString());
							// 每次请求的业务参数的签名值，详细请参考【签名】.
							// "RSA"商户私钥加密签名.
							String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
							params.put("signature", sign);
							String paramsJsonStr = JSON.toJSONString(params);
							System.err.println("参数列表：" + paramsJsonStr);
							// 商户自己的RSA公钥加密.
							Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
							/**
							 * HTTP.
							 */
							encryptRet.put("merchantId", MERCHANT_ID);
							String requestJsonStr = JSON.toJSONString(encryptRet);
							System.err.println("请求：" + requestJsonStr);
							String responseStr = HttpUtil.sendPost(HOST, encryptRet);
							System.err.println("响应：" + responseStr);
							/**
							 * 解析响应.
							 */
							JSONObject jsonObject = JSONObject.parseObject(responseStr);
							String tm = (String) jsonObject.get("tm");
							String data = (String) jsonObject.getString("data");
							String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
							Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
							});
							System.out.println("解析响应：" + JSON.toJSONString(map));
							/**
							 * 公共响应参数.
							 */
							String respCode = map.get("respCode");
							if (respCode.equals("00")) { // 成功.
								log.info("fn:repay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
							} else if (respCode.equals("01")) { // 处理中.
								log.info("fn:repay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
							} else if (respCode.equals("02")) { // 失败.
								log.info("fn:repay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								addMessage(redirectAttributes, "借款方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							}
						}
						if (i == 2) { // 还本.
							// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
							params.put("reqSn", IdGen.uuid());
							// 由网贷平台生成的唯一的交易流水号(项目还款计划subOrderId).
							params.put("orderId", projectPlan.getSubOrderId());
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
							log.info("fn:repay-还本付息，最后一期还款，还本总额：" + totalAmount);
							BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
							params.put("totalAmount", b.toString());
							// 每次请求的业务参数的签名值，详细请参考【签名】.
							// "RSA"商户私钥加密签名.
							String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
							params.put("signature", sign);
							String paramsJsonStr = JSON.toJSONString(params);
							System.err.println("参数列表：" + paramsJsonStr);
							// 商户自己的RSA公钥加密.
							Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
							/**
							 * HTTP.
							 */
							encryptRet.put("merchantId", MERCHANT_ID);
							String requestJsonStr = JSON.toJSONString(encryptRet);
							System.err.println("请求：" + requestJsonStr);
							String responseStr = HttpUtil.sendPost(HOST, encryptRet);
							System.err.println("响应：" + responseStr);
							/**
							 * 解析响应.
							 */
							JSONObject jsonObject = JSONObject.parseObject(responseStr);
							String tm = (String) jsonObject.get("tm");
							String data = (String) jsonObject.getString("data");
							String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
							Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
							});
							System.out.println("解析响应：" + JSON.toJSONString(map));
							/**
							 * 公共响应参数.
							 */
							String respCode = map.get("respCode");
							if (respCode.equals("00")) { // 成功.
								log.info("fn:repay-该次还款类型-还本|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								addMessage(redirectAttributes, "借款方还本款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							} else if (respCode.equals("01")) { // 处理中.
								log.info("fn:repay-该次还款类型-还本|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								addMessage(redirectAttributes, "借款方还本款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							} else if (respCode.equals("02")) { // 失败.
								log.info("fn:repay-该次还款类型-还本|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								addMessage(redirectAttributes, "借款方还本款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
								if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
									return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
								}
							}
						}
					}
				} else if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
					addMessage(redirectAttributes, "借款方还款类型-付息-请联系开发人员");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			} else {
				// 该批次项目还款总额.
				Double totalRepayAmount = NumberUtils.scaleDouble(projectPlan.getInterest());
				log.info("fn:repay-该批次项目还款总额：" + totalRepayAmount);
				if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0)) { // 付息.
					// 客户还款计划查询封装.
					WloanTermUserPlan entity = new WloanTermUserPlan();
					entity.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
					WloanTermProject project = new WloanTermProject();
					project.setId(proid);
					entity.setWloanTermProject(project);
					entity.setRepaymentDate(repaymentDate);
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
					log.info("fn:repay-借款方还付息款，付息总额：" + totalAmount);
					BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(totalAmount * 100));
					params.put("totalAmount", b.toString());
					// 每次请求的业务参数的签名值，详细请参考【签名】.
					// "RSA"商户私钥加密签名.
					String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
					params.put("signature", sign);
					String paramsJsonStr = JSON.toJSONString(params);
					System.err.println("参数列表：" + paramsJsonStr);
					// 商户自己的RSA公钥加密.
					Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
					/**
					 * HTTP.
					 */
					encryptRet.put("merchantId", MERCHANT_ID);
					String requestJsonStr = JSON.toJSONString(encryptRet);
					System.err.println("请求：" + requestJsonStr);
					String responseStr = HttpUtil.sendPost(HOST, encryptRet);
					System.err.println("响应：" + responseStr);
					/**
					 * 解析响应.
					 */
					JSONObject jsonObject = JSONObject.parseObject(responseStr);
					String tm = (String) jsonObject.get("tm");
					String data = (String) jsonObject.getString("data");
					String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
					Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
					});
					System.out.println("解析响应：" + JSON.toJSONString(map));
					/**
					 * 公共响应参数.
					 */
					String respCode = map.get("respCode");
					if (respCode.equals("00")) { // 成功.
						log.info("fn:repay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						addMessage(redirectAttributes, "借款方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					} else if (respCode.equals("01")) { // 处理中.
						log.info("fn:repay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						addMessage(redirectAttributes, "借款方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					} else if (respCode.equals("02")) { // 失败.
						log.info("fn:repay-该次还款类型-付息|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						addMessage(redirectAttributes, "借款方还付息款|" + map.get("respSubCode") + "|" + map.get("respMsg"));
						if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
							return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
						}
					}
				} else if (projectPlan.getPrincipal().equals(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1)) { // 还本付息.
					addMessage(redirectAttributes, "借款方还款类型-还本付息-请联系开发人员");
					if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					} else if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) { // 供应链.
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
					}
				}
			}
		}

		if (type.equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) { // 安心投.
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findAxtUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
		} // 供应链.
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProjectPlan/findUserPlanListByProPlanId?projectPlanId=" + projectPlanId + "&proid=" + proid;
	}

	/**
	 * 
	 * 方法: cancel <br>
	 * 描述: 流标. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月2日 上午9:52:20
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:cancel:edit")
	@RequestMapping(value = "miscarry")
	@Transactional(rollbackFor = Exception.class)
	public String miscarry(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("fn:cancel-流标");

		WloanTermProject entity = wloanTermProjectService.get(wloanTermProject.getId());
		// if (null != entity) {
		// // 放款日期.
		// Date loanDate = entity.getLoanDate();
		// boolean isBoolean = DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), DateUtils.getDate(loanDate, "yyyy-MM-dd HH:mm:ss"));
		// if (isBoolean) { // 项目没到放款日期，可继续投资.
		// addMessage(redirectAttributes, "项目没到流标日期，项目可以继续出借.");
		// if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
		// return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
		// } else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
		// return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
		// }
		// } else { // 可以操作流标或者放款.
		// log.info("fn:callbackCancel-可以操作流标，可以操作放款.");
		// }
		// }

		Map<String, String> cancelResult = new HashMap<String, String>();
		Map<String, Object> projectResult = new HashMap<String, Object>();
		/**
		 * 查询出借记录，调用取消预处理取消接口
		 */
		WloanTermInvest searchInvest = new WloanTermInvest();
		searchInvest.setProjectId(wloanTermProject.getId());
		searchInvest.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
		List<WloanTermInvest> termInvests = wloanTermInvestDao.findList(searchInvest);

		if (wloanTermProject.getSn() != null) {
			projectResult = lanMaoProjectService.modifyProject(wloanTermProject.getSn(), ProjectStatusEnum.MISCARRY.getValue());
			if ("0".equals(projectResult.get("code")) && "SUCCESS".equals(projectResult.get("status"))) {
				entity.setState(WloanTermProjectService.P2P_TRADE_BID_CANCEL);
				entity.setUpdateDate(new Date());
				int j = wloanTermProjectDao.update(entity);
				logger.info("标的状态更改:{}", j == 1 ? "成功" : "失败");
				if (null != termInvests && termInvests.size() > 0) {
					long currentTimeMillis = System.currentTimeMillis();
					for (WloanTermInvest termInvest : termInvests) {
						String id = IdGen.uuid();
						Double checkAmount = 0.0;
						if (termInvest.getAmount() != null) {
							checkAmount = termInvest.getAmount();
						}
						if (termInvest.getVoucherAmount() != null) {
							checkAmount = checkAmount - termInvest.getVoucherAmount();
						}
						CancelPreTransaction cancelPreTransaction = new CancelPreTransaction();
						cancelPreTransaction.setRequestNo(id); // 请求的流水号
						cancelPreTransaction.setPreTransactionNo(termInvest.getId());// 出借预处理流水号
						cancelPreTransaction.setAmount(NumberUtils.scaleDoubleStr(checkAmount)); // 取消金额
						cancelResult = lanMaoPreTransactionService.cancelPreTransaction(cancelPreTransaction);
						LmTransaction transaction = new LmTransaction();
						transaction.setId(IdGen.uuid());
						transaction.setRequestNo(id);
						transaction.setCode(cancelResult.get("code"));
						transaction.setErrorCode(cancelResult.get("errorCode"));
						transaction.setErrorMessage(cancelResult.get("errorMessage"));
						transaction.setStatus(cancelResult.get("status"));
						transaction.setServiceName(ServiceNameEnum.CANCEL_PRE_TRANSACTION.getValue());
						transaction.setCreateDate(new Date());
						transaction.setUpdateDate(new Date());
						transaction.setPlatformUserNo(termInvest.getUserId());
						transaction.setProjectNo(wloanTermProject.getSn());
						transaction.setOriginalFreezeRequestNo(termInvest.getAmount().toString());// 取消金额
						transaction.setSourcePlatformUserNo(termInvest.getId());// 出借预处理流水号
						int transactionFlag = transactionDao.insert(transaction);
						logger.info("取消预处理记录插入:{}", transactionFlag == 1 ? "成功" : "失败");
						if (cancelResult.get("code").equals("0") && cancelResult.get("status").equals("SUCCESS")) {
							/**
							 * 修改出借记录状态--出借失败
							 */
							termInvest.setState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
							termInvest.setBidState(UserInvestWebService.WLOAN_TERM_INVEST_STATE_2);
							termInvest.setEndDate(new Date());
							termInvest.setUpdateDate(new Date());
							int updateInvestFlag = wloanTermInvestDao.update(termInvest);
							logger.info("预处理出借取消，出借记录更新:{}", updateInvestFlag == 1 ? "成功" : "失败");

							Double amount = 0.0;
							Double availableAmount = 0D;

							CgbUserAccount cgbUserAccount = cgbUserAccountDao.getUserAccountInfo(termInvest.getUserId());
							if (cgbUserAccount != null) {
								availableAmount = cgbUserAccount.getAvailableAmount();
								int updateCgbUserAccountFlag = cgbUserAccountDao.updateCancelById(termInvest.getInterest() != null ? NumberUtils.scaleDouble(termInvest.getInterest()) : amount, termInvest.getAmount() != null ? NumberUtils.scaleDouble(termInvest.getAmount()) : amount, cgbUserAccount.getId());
								logger.info("账户信息更新:{}", updateCgbUserAccountFlag == 1 ? "成功" : "失败");
							}
							/**
							 * 出借流水--添加流标记录
							 */
							currentTimeMillis = currentTimeMillis + 1000;
							UserInfo userInfo = userInfoDao.getCgb(termInvest.getUserId());
							CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
							userTransDetail.setId(IdGen.uuid()); // 主键id
							userTransDetail.setTransId(termInvest.getId()); // 出借记录id
							userTransDetail.setUserId(userInfo.getId()); // 帐号id
							userTransDetail.setAccountId(userInfo.getAccountId()); // 账户id
							userTransDetail.setTransDate(new Date(currentTimeMillis)); // 出借交易时间
							userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_3); // 定期出借
							userTransDetail.setAmount(termInvest.getAmount()); // 出借交易金额

							availableAmount = NumberUtils.add(availableAmount, termInvest.getAmount());
							userTransDetail.setAvaliableAmount(availableAmount); // 当前可用余额
							userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 支出
							userTransDetail.setRemarks("流标"); // 备注信息
							userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态-成功
							int insertUserTransDetailFlag = cgbUserTransDetailService.insert(userTransDetail);
							logger.info("出借交易流水插入:{}", insertUserTransDetailFlag == 1 ? "成功" : "失败");

							AUserAwardsHistory userAwardsHistory = new AUserAwardsHistory();
							userAwardsHistory.setBidId(termInvest.getId());
							userAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_2);
							userAwardsHistory.setUserId(termInvest.getUserId());
							List<AUserAwardsHistory> histories = aUserAwardsHistoryService.findVouchers(userAwardsHistory);
							if (histories.size() > 0) {
								for (AUserAwardsHistory userAwardsHistory2 : histories) {
									currentTimeMillis = currentTimeMillis + 1000;
									AVouchersDic dic = aVouchersDicDao.get(userAwardsHistory2.getAwardId());
									if (dic != null) {
										UserVouchersHistory insertHistory = new UserVouchersHistory();
										insertHistory.setId(IdGen.uuid());
										insertHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
										insertHistory.setBidId(termInvest.getId());
										insertHistory.setUserId(userInfo.getId());
										insertHistory.setAwardId(dic.getId());
										insertHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), dic.getOverdueDays()));
										insertHistory.setCreateDate(new Date());
										insertHistory.setUpdateDate(null);
										insertHistory.setSpans(dic.getSpans());
										insertHistory.setOverdueDays(dic.getOverdueDays());
										insertHistory.setLimitAmount(dic.getLimitAmount());
										insertHistory.setRemark(dic.getRemarks());
										insertHistory.setType("1");// 类型为:抵用劵
										insertHistory.setValue(dic.getAmount().toString());
										int i = userVouchersHistoryDao.insert(insertHistory);
										logger.info("抵用券发放:{}", i == 1 ? "成功" : "失败");
										/**
										 * 抵用券添加流水记录
										 */
										int updateCgbUserAccountFlag2 = cgbUserAccountDao.updateMiscarryById(amount, dic.getAmount() != null ? NumberUtils.scaleDouble(dic.getAmount()) : amount, cgbUserAccount.getId());
										logger.info("账户信息更新:{}", updateCgbUserAccountFlag2 == 1 ? "成功" : "失败");
										CgbUserTransDetail awardsDetail = new CgbUserTransDetail();
										awardsDetail.setId(IdGen.uuid()); // 主键id
										awardsDetail.setTransId(termInvest.getId()); // 出借记录id
										awardsDetail.setUserId(userInfo.getId()); // 帐号id
										awardsDetail.setAccountId(userInfo.getAccountId()); // 账户id
										awardsDetail.setTransDate(new Date(currentTimeMillis)); // 出借交易时间
										awardsDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 定期出借
										awardsDetail.setAmount(dic.getAmount()); // 抵用券金额
										availableAmount = NumberUtils.subtract(availableAmount, dic.getAmount());
										awardsDetail.setAvaliableAmount(availableAmount); // 当前可用余额
										awardsDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 获得
										awardsDetail.setRemarks("抵用券返回（流标）"); // 备注信息
										awardsDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 流水状态-成功
										int insertAwardsTransDetailFlag = cgbUserTransDetailService.insert(awardsDetail);
										logger.info("抵用券添加流水插入:{}", insertAwardsTransDetailFlag == 1 ? "成功" : "失败");
									}
								}
							}
							/**
							 * 出借人及推荐人扣除积分规则
							 */
							// giveInvestBouns(userInfo.getId(), userInfo.getName(), userInfo.getCertificateNo(), userInfo.getRecommendUserId(), userInfo.getRecommendUserPhone(), termInvest.getId(), termInvest.getAmount(), wloanTermProject.getProjectProductType(), wloanTermProject.getSpan(),termInvest.getUpdateDate());
						} else {
							logger.info("取消预处理失败:" + cancelResult.get("errorMessage"));
						}
					}
				}
			}
		}

		if ("0".equals(projectResult.get("code"))) {
			if (ServerURLConfig.IS_REAL_TIME_PUSH) {
				// 4.1.3 散标状态-（流标）
				// 时时推送散标状态
				Map<String, Object> result = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
				logger.info("时时推送散标状态:" + result.get("respMsg").toString());
			}
		}
		// 页面消息通知.
		// model.addAttribute("respCode", map.get("respCode"));
		// model.addAttribute("respMsg", map.get("respMsg"));
		// model.addAttribute("respSubCode", map.get("respSubCode"));

		addMessage(redirectAttributes, projectResult.get("code") + "|" + projectResult.get("errorCode") + "|" + projectResult.get("errorMessage"));
		//
		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
		} else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	/**
	 * 
	 * methods: establishProject <br>
	 * description: 懒猫创建标的 <br>
	 * author: Roy <br>
	 * date: 2019年9月27日 下午10:24:16
	 * 
	 * @param project
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("lm:establishProject:edit")
	@RequestMapping(value = "establishProject")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String establishProject(WloanTermProject project, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("懒猫创建标的---start---");
		Map<String, Object> resultMap = lanMaoProjectService.establishProject(project);
		log.info("懒猫创建标的---end---");

		String code = (String) resultMap.get("code"); // 调用状态码
		String status = (String) resultMap.get("status"); // 业务处理状态
		String errorCode = (String) resultMap.get("errorCode"); // 错误码
		String errorMessage = (String) resultMap.get("errorMessage"); // 错误消息

		if (UniversalCodeEnum.UNIVERSAL_CODE_1.getValue().equals(code)) { // 调用失败，失败原因请查看【调用失败 错误码】
			addMessage(redirectAttributes, errorCode + ":" + errorMessage);
			if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) {
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
			} else {
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
			}
		} else {
			if (BusinessStatusEnum.INIT.getValue().equals(status)) {
				addMessage(redirectAttributes, errorCode + ":" + errorMessage);
				if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
				} else {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
				}
			} else {
				if (ServerURLConfig.IS_REAL_TIME_PUSH) {
					// 4.1.10 产品信息
					// 实时推送产品信息
					// 上报产品信息数据触发时间：产品对出借人发布时，上报产品信息数据。
					List<String> projectIdList = new ArrayList<String>();
					projectIdList.add(project.getId());
					Map<String, Object> result = lendProductDataAccessService.pushLendProduct(projectIdList);
					log.info("实时推送产品信息:" + result.get("respMsg").toString());
				}
			}
		}

		/**
		 * 懒猫创建标的成功，标的状态为募集中，平台业务，标的状态为发布
		 */
		// 该key用于标的定时上线逻辑
		String key = "PROJECT:ONLINE:LIST";
		// 项目还款计划逻辑
		if (WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0.equals(project.getProjectRepayPlanType())) { // 旧版.
			String flag = wloanTermProjectPlanService.initWloanTermProjectPlan(project);
			if (flag.equals("SUCCESS")) {
				logger.info("标的[{}]旧版还款计划，初始化完成 ......", project.getSn());
				List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findListByProjectId(project.getId());
				if (planList != null && planList.size() > 0) {
					logger.info("标的[{}]还款期数:{}", project.getSn(), planList.size());
					WloanTermProjectPlan proPlan = planList.get(planList.size() - 1); // 最后一期
					project.setState(WloanTermProjectService.PUBLISH); // 标的发布
					project.setPublishDate(new Date());
					project.setUpdateDate(new Date()); // 标的更新时间
					project.setUpdateBy(SessionUtils.getUser());
					project.setEndDate(proPlan.getRepaymentDate()); // 标的结束时间
					int updateStateFlag = wloanTermProjectDao.updateStateById(project);
					logger.info("标的[{}]更新{}", project.getSn(), updateStateFlag == 1 ? "成功" : "失败");
					addMessage(redirectAttributes, "懒猫创建标的成功，标的状态为募集中，平台标的状态为发布，请及时上线");
					/**
					 * 缓存数据库，留存标的ID，处理定时上线逻辑
					 */
					Long num = JedisUtils.lPush(key, project.getId());
					logger.info("project id list number:{}", num);
					if (num > 0L) {
						logger.info("project id list，element push success...");
					}
				}
			}
		} else if (WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1.equals(project.getProjectRepayPlanType())) { // 新版
			String flag = wloanTermProjectPlanService.initCgbWloanTermProjectPlan(project);
			if (flag.equals("SUCCESS")) {
				logger.info("标的[{}]旧版还款计划，初始化完成 ......", project.getSn());
				List<WloanTermProjectPlan> planList = wloanTermProjectPlanService.findListByProjectId(project.getId());
				if (planList != null && planList.size() > 0) {
					logger.info("标的[{}]还款期数:{}", project.getSn(), planList.size());
					WloanTermProjectPlan proPlan = planList.get(planList.size() - 1); // 最后一期
					project.setState(WloanTermProjectService.PUBLISH); // 标的发布
					project.setPublishDate(new Date());
					project.setUpdateDate(new Date()); // 标的更新时间
					project.setUpdateBy(SessionUtils.getUser());
					project.setEndDate(proPlan.getRepaymentDate()); // 标的结束时间
					int updateStateFlag = wloanTermProjectDao.updateStateById(project);
					logger.info("标的[{}]更新{}", project.getSn(), updateStateFlag == 1 ? "成功" : "失败");
					addMessage(redirectAttributes, "懒猫创建标的成功，标的状态为募集中，平台标的状态为发布，请及时上线");
					/**
					 * 缓存数据库，留存标的ID，处理定时上线逻辑
					 */
					Long num = JedisUtils.lPush(key, project.getId());
					logger.info("project id list number:{}", num);
					if (num > 0L) {
						logger.info("project id list，element push success...");
					}
				}
			}
		}

		if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	/**
	 * 
	 * 方法: grant <br>
	 * 描述: 放款. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月23日 上午9:05:28
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:grant:edit")
	@RequestMapping(value = "grant")
	@Transactional(rollbackFor = Exception.class)
	public synchronized String grant(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("放款");

		Map<String, String> params = new HashMap<String, String>();
		/**
		 * 业务请求参数封装.
		 */
		// 由网贷平台生成的唯一的交易流水号（是）.
		params.put("orderId", wloanTermProject.getId());
		// 标的Id（是）.
		params.put("bidId", wloanTermProject.getId());
		// 借款人，网贷平台唯一的用户编码（是）.
		WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
		if (wloanSubject != null) { // 融资主体.
			// 借款人.
			CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
			if (null != creditUserInfo) {
				params.put("userId", creditUserInfo.getId());
			}
		}
		// 币种，默认为CNY，以下为可选，CNY (人民币)（是）.
		params.put("currency", "CNY");
		List<WloanTermInvest> list = wloanTermInvestDao.findProjectInvestNumbers(wloanTermProject.getId());
		if (list != null) { // 项目投资笔数.
			int size = list.size();
			log.warn("放款总笔数 = " + size + " 笔");
			// 放款总笔数（是），该项目投资笔数.
			params.put("totalNum", String.valueOf(size));

		}
		// 放款总金额，单位（分），放款总金额=投资总金额，系统将进行校验，不一致则放款失败（是）.
		// 筹集金额.
		double currentAmount = wloanTermProject.getCurrentRealAmount();
		// 保证金.
		// double marginPercentage = wloanTermProject.getMarginPercentage();
		// 手续费率.
		// double feeRate = wloanTermProject.getFeeRate();
		// 真正放款金额
		// double realAmount = currentAmount - marginPercentage - feeRate;
		// 以分为单位.
		// realAmount = NumberUtils.scaleDouble(realAmount) * 100;
		BigDecimal b = new BigDecimal(NumberUtils.scaleDouble(currentAmount * 100));
		params.put("totalAmount", b.toString());
		/**
		 * String realAmountStr = String.valueOf(realAmount);
		 * if (realAmountStr.indexOf(".") > 0) {
		 * // 去掉后面无用的零.
		 * realAmountStr = realAmountStr.replaceAll("0+?$", "");
		 * // 如小数点后面全是零则去掉小数点.
		 * realAmountStr = realAmountStr.replaceAll("[.]$", "");
		 * }
		 */
		// 借款人实际收款金额，单位（分）（是）.
		params.put("grantAmount", b.toString());
		// 分润列表（否）.
		List<ShareProfitOrder> shareProfitOrderList = new ArrayList<ShareProfitOrder>();
		ShareProfitOrder item = new ShareProfitOrder();
		// item.setReceiveUserId("789"); // 分润收款用户，网贷平台唯一的用户编码.
		// item.setSubOrderId(UUID.randomUUID().toString()); // 分润订单Id.
		// item.setAmount(2000L); // 分润金额，单位（分）货币类型与主单一致.
		shareProfitOrderList.add(item);
		// params.put("shareProfitOrderList",
		// JSONArray.toJSONString(shareProfitOrderList));
		// 备注（否）.
		params.put("remark", "放款");
		// 异步通知地址（是）.
		params.put("callbackUrl", CALLBACK_URL_GRANT);
		/**
		 * 公共请求参数封装.
		 */
		// 接口名称：每个接口提供不同的编码.
		params.put("service", "p2p.trade.bid.grant");
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
		// 每次请求的业务参数的签名值，详细请参考【签名】.
		// "RSA"商户私钥加密签名.
		String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
		params.put("signature", sign);
		String paramsJsonStr = JSON.toJSONString(params);
		System.err.println("参数列表：" + paramsJsonStr);
		// 商户自己的RSA公钥加密.
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
		/**
		 * HTTP.
		 */
		encryptRet.put("merchantId", MERCHANT_ID);
		String requestJsonStr = JSON.toJSONString(encryptRet);
		System.err.println("请求：" + requestJsonStr);
		String responseStr = HttpUtil.sendPost(HOST, encryptRet);

		System.err.println("响应：" + responseStr);

		/**
		 * 解析响应.
		 */
		JSONObject jsonObject = JSONObject.parseObject(responseStr);
		String tm = (String) jsonObject.get("tm");
		String data = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		System.out.println("解析响应：" + JSON.toJSONString(map));

		/**
		 * 同步响应处理.
		 */
		String respCode = map.get("respCode");
		String respMsg = map.get("respMsg");
		String respSubCode = map.get("respSubCode");
		log.info("fn:grant-respCode{成功}-respSubCode{" + respSubCode + "}-respMsg{" + respMsg + "}");
		if ("00".equals(respCode)) { // 成功.
			// 项目状态更新为已放款.
			wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
			wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
			wloanTermProject.setUpdateDate(new Date());
			wloanTermProject.setRealLoanDate(new Date());
			// 生成项目合同.
			String contractUrl = productContract(wloanTermProject);
			wloanTermProject.setContractUrl(contractUrl.split("data")[1]);
			wloanTermProjectService.updateProState(wloanTermProject);
			log.info("fn:grant-项目状态更新成功");

			if (ServerURLConfig.IS_REAL_TIME_PUSH) {
				// 4.1.4 还款计划
				// 时时推送还款计划
				// 上报还款计划数据触发时间：某个借款人成功申请借款，平台准备放款时，还款计划业务数据产生之后上报该业务数据。
				List<String> projectIdList = new ArrayList<String>();
				projectIdList.add(wloanTermProject.getId());
				Map<String, Object> res = repayPlanDataAccessService.pushRepayPlanInfo(projectIdList);
				logger.info("时时推送散标状态:" + res.get("respMsg").toString());

				// 4.1.3 散标状态-（放款）
				// 时时推送散标状态 的满标、放款、还款状态
				Map<String, Object> map1 = scatterInvestStatusDataAccessService.pushScatterInvestStatus(wloanTermProject.getId());
				logger.info("时时推送散标状态:" + map1.get("respMsg").toString());

				// 4.1.5 初始债权
				// 时时推送初始债权
				// 上报初始债权数据触发时间：初始债权对应的散标放款成功之后上报该数据。
				Map<String, Object> results = creditorDataAccessService.pushCreditorInfo(projectIdList);
				logger.info("时时推送散标状态:" + results.get("respMsg").toString());

				// 4.1.11 产品配置
				// 时时推送产品配置
				// 上报产品配置数据触发时间：出借用户购买的产品撮合成功 1个推送 1 条产品配置数据。
				Map<String, Object> resu = lendProductConfigDataAccessService.pushLendProductConfigInfo(projectIdList);
				log.info("时时推送产品配置:" + resu.get("respMsg").toString());

				// 4.2.9 交易流水接口
				// 借款人用户放款流水
				List<CgbUserTransDetail> cutdList = new ArrayList<CgbUserTransDetail>();
				// 回调接口可能还没有执行--构建一条流水
				CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
				userTransDetail.setId(IdGen.uuid()); // 主键.
				userTransDetail.setTransId(wloanTermProject.getId()); // 项目ID(放款单号).
				// userTransDetail.setUserId(wloanSubject.getLoanApplyId()); // 客户帐号.
				// userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户.
				userTransDetail.setTransDate(new Date()); // 交易时间.
				userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_11); // 放款.
				userTransDetail.setAmount(currentAmount); // 金额.
				// userTransDetail.setAvaliableAmount(creditUserAccount.getAvailableAmount()); // 可用余额.
				userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
				userTransDetail.setRemarks("放款"); // 备注信息.
				userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
				cutdList.add(userTransDetail);
				Long currentTime = System.currentTimeMillis();
				Map<String, Object> maps = transactDataAccessService.pushTransactCreGrantInfo(cutdList, currentTime);
				log.info("借款人用户放款流水:" + maps.get("respMsg").toString());

				// 4.2.9 交易流水接口
				// 出借人购买散标产生的初始债权交易流水
				Map<String, Object> mapRecharge = transactDataAccessService.pushTransactInvInfo(projectIdList);
				log.info("出借人购买散标产生的初始债权交易流水:" + mapRecharge.get("respMsg").toString());

				// 4.1.12 投资明细
				// 标的放款时，进行出借人出借投资明细的推送
				List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(wloanTermProject.getId());
				if (!investList.isEmpty() && investList != null) {
					currentTime = System.currentTimeMillis();
					Map<String, Object> mapTransact = lendParticularsDataAccessService.pushLendParticularsInvTransInfo(investList, currentTime);
					log.info("出借人出借投资明细的推送:" + mapTransact.get("respMsg").toString());
				}

				// 4.1.1用户信息
				// 推送当前标的所有出借人信息.
				List<String> userIdList = new ArrayList<String>();
				for (WloanTermInvest wloanTermInvest : list) {
					IfCertUserInfo ifCertUserInfo = new IfCertUserInfo();
					UserInfo ui = userInfoDao.get(wloanTermInvest.getUserId());
					if (ui != null) {
						if (!"".equals(ui.getCertificateNo()) && ui.getCertificateNo() != null) {
							ifCertUserInfo.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(ui.getCertificateNo())));
							List<IfCertUserInfo> ifCertUserInfos = ifCertUserInfoDao.findList(ifCertUserInfo);
							if (ifCertUserInfos == null && ifCertUserInfos.size() == 0) {
								userIdList.add(wloanTermInvest.getUserId());
							}
						}
					}
				}
				if (userIdList != null && userIdList.size() > 0) {
					Map<String, Object> pushUserResult = ifcertUserInfoDataAccessService.pushInvestUserInfo(userIdList);
					log.info("推送出借人用户信息-实时:" + pushUserResult.get("respMsg"));
				}

			}
		}
		// 页面消息通知.
		// model.addAttribute("respCode", respCode);
		// model.addAttribute("respMsg", respMsg);
		// model.addAttribute("respSubCode", respSubCode);
		addMessage(redirectAttributes, respMsg);
		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}

	/**
	 * 
	 * 方法: productContract <br>
	 * 描述: 生成项目合同. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月26日 上午11:31:55
	 * 
	 * @param wloanTermProject
	 * @return
	 */
	public String productContract(WloanTermProject wloanTermProject) {

		String resultURL = "";
		String templateName = "pdf_template.pdf";

		/**
		 * 融资主体.
		 */
		WloanSubject wloanSubject = wloanTermProject.getWloanSubject();

		/**
		 * 查找担保人信息
		 */
		// WGuaranteeCompany wCompany = wloanTermProject.getWgCompany();

		// wCompany.setBriefInfo(StringEscapeUtils.unescapeHtml(wCompany.getBriefInfo()));
		// wCompany.setGuaranteeCase(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeCase()));
		// wCompany.setGuaranteeScheme(StringEscapeUtils.unescapeHtml(wCompany.getGuaranteeScheme()));

		Map<String, String> data = new HashMap<String, String>();
		data.put("contract_no", DateUtils.getDateStr()); // 合同编号
		data.put("name", wloanSubject.getCompanyName()); // 乙方（借款人）
		data.put("card_id", wloanSubject.getLoanIdCard()); // 身份证号码
		// data.put("third_name", wCompany.getBriefName()); // 丙方（担保人）
		// data.put("legal_person", wCompany.getCorporation()); // 法人代表
		// data.put("residence", wCompany.getAddress()); // 住所
		// data.put("telphone", wCompany.getPhone()); // 电话

		data.put("project_name", wloanTermProject.getName()); // 借款项目名称
		data.put("project_no", wloanTermProject.getSn()); // 借款项目编号
		data.put("service_no", ""); // 《借款服务合同》编号
		data.put("guarantee_no", ""); // 《担保函》编号
		data.put("rmb", wloanTermProject.getAmount().toString()); // 借款金额（小写）
		data.put("rmd_da", PdfUtils.change(wloanTermProject.getAmount())); // 借款金额（大写）
		data.put("uses", wloanTermProject.getPurpose()); // 借款用途
		data.put("lend_date", DateUtils.formatDate(wloanTermProject.getRealLoanDate(), "yyyy-MM-dd")); // 借款日期
		data.put("term_date", wloanTermProject.getSpan().toString()); // 借款期限
		data.put("back_date", DateUtils.getSpecifiedMonthAfter(DateUtils.formatDate(wloanTermProject.getLoanDate(), "yyyy-MM-dd"), wloanTermProject.getSpan() / 30)); // 还本时间
		data.put("year_interest", wloanTermProject.getAnnualRate().toString()); // 年利率
		data.put("interest_sum", String.valueOf(NumberUtils.scaleDouble(wloanTermProject.getAmount() * wloanTermProject.getAnnualRate() * wloanTermProject.getSpan() / (365 * 100)))); // 利息总额

		data.put("bottom_name", wloanSubject.getCompanyName()); // 乙方（借款人）
		// data.put("bottom_third_name", wCompany.getBriefName()); // 丙方（担保人）
		data.put("sign_date", DateUtils.formatDate(wloanTermProject.getRealLoanDate(), "yyyy年MM月dd日")); // 合同签订日期

		/**
		 * 查找项目投资记录
		 */
		WloanTermInvest wloanTermInvest = new WloanTermInvest();
		wloanTermInvest.setWloanTermProject(wloanTermProject);
		List<WloanTermInvest> investList = wloanTermInvestService.findList(wloanTermInvest);
		String title = "出借人本金利息表";

		String[] rowTitle = new String[] { "编号", "出借人", "身份证号", "出借金额", "利息总额" };

		List<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		for (int i = 0; i < investList.size(); i++) {
			WloanTermInvest invest = investList.get(i);
			strings = new String[rowTitle.length];
			strings[0] = String.valueOf(i + 1);
			strings[1] = invest.getUserInfo().getRealName() == null ? Util.hideString(invest.getUserInfo().getName(), 3, 4) : Util.hideString(invest.getUserInfo().getRealName(), 1, 1);
			strings[2] = invest.getUserInfo().getCertificateNo() == null ? "****************" : Util.hideString(invest.getUserInfo().getCertificateNo(), 5, 8);
			strings[3] = String.valueOf(invest.getAmount());
			strings[4] = String.valueOf(invest.getInterest());
			dataList.add(strings);
		}
		try {
			resultURL = PdfUtils.createPdfByTemplate(templateName, data, title, rowTitle, dataList, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultURL;
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
	public void giveInvestBouns(String userId, String investUserPhone, String certificateNo, String recommendUserId, String recommendUserPhone, String investId, Double investAmount, String projectProductType, Integer span, Date date) {

		List<WloanTermInvest> sumInvestExists = wloanTermInvestDao.findWloanTermInvestExists(userId);
		int investNumber = 0; // 出借次数
		if (null != sumInvestExists) {
			investNumber = sumInvestExists.size();
		}
		logger.info("出借次数:{}", investNumber);
		/**
		 * 出借人获取出借积分((investAmount × (span / 30)) / 100)
		 */
		Double userBouns = NumberUtils.divide(NumberUtils.multiply(investAmount, NumberUtils.divide(Double.valueOf(span), 30D, 2)), 100D, 2);
		// 渠道推荐逻辑待定......
		// 正常出借
		if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(projectProductType)) { // 安心投类
			if (IsHolidayOrBirthday.isCheckHoliday(certificateNo, date)) { // 生日及节假日1.5倍积分
				subtractBouns(userId, NumberUtils.multiply(userBouns, 1.5D), investId);
			} else { // 正常积分
				subtractBouns(userId, userBouns, investId);
			}
		} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(projectProductType)) { // 供应链类
			if (IsHolidayOrBirthday.isCheckActivity(date)) { // 活动日双倍积分
				subtractBouns(userId, NumberUtils.multiply(userBouns, 2D), investId);
			} else {
				if (IsHolidayOrBirthday.isCheckHoliday(certificateNo, date)) { // 生日及节假日1.5倍积分
					subtractBouns(userId, NumberUtils.multiply(userBouns, 1.5D), investId);
				} else { // 正常积分
					subtractBouns(userId, userBouns, investId);
				}
			}
		}

	}

	/**
	 * 
	 * methods: addBouns <br>
	 * description: 出借减掉积分 <br>
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
	public void subtractBouns(String userId, Double userBouns, String investId) {

		UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
		if (null != userBounsPoint) {
			String sumBounsStr = NumberUtils.scaleDoubleStr(NumberUtils.subtract(Double.valueOf(userBounsPoint.getScore()), userBouns));
			UserBounsHistory userBounsHistory = new UserBounsHistory();
			userBounsHistory.setId(IdGen.uuid());
			userBounsHistory.setUserId(userId);
			userBounsHistory.setAmount(-userBouns);
			userBounsHistory.setCreateDate(new Date());
			userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_MISCARRY);
			userBounsHistory.setTransId(investId);
			userBounsHistory.setCurrentAmount(sumBounsStr);
			int insertUserBounsHistoryFlag = userBounsHistoryService.insert(userBounsHistory);
			if (insertUserBounsHistoryFlag == 1) {
				logger.info("积分历史扣除成功......");
				userBounsPoint.setScore(Double.valueOf(sumBounsStr).intValue());
				userBounsPoint.setUpdateDate(new Date());
				int updateUserBounsFlag = userBounsPointService.update(userBounsPoint);
				logger.info("积分账户更新:{}", updateUserBounsFlag == 1 ? "成功" : "失败");
			}
		}
	}

}
