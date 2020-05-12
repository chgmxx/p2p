package com.power.platform.cgb.web;

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
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.pojo.RepayOrder;
import com.power.platform.cgb.pojo.ShareProfitOrder;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
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
import com.power.platform.ifcert.scatterInvest.service.ScatterInvestDataAccessService;
import com.power.platform.ifcert.status.service.ScatterInvestStatusDataAccessService;
import com.power.platform.ifcert.transact.service.TransactDataAccessService;
import com.power.platform.ifcert.userInfo.service.IfcertUserInfoDataAccessService;
import com.power.platform.lanmao.dao.AsyncTransactionLogDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.AsyncTransactionLog;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.search.service.LanMaoSearchUserInfoDataService;
import com.power.platform.lanmao.trade.pojo.AsyncTransaction;
import com.power.platform.lanmao.trade.pojo.SyncTransaction;
import com.power.platform.lanmao.trade.pojo.SyncTransactionDetail;
import com.power.platform.lanmao.trade.service.LanMaoAsyncTransactionService;
import com.power.platform.lanmao.trade.service.LanMaoProjectService;
import com.power.platform.lanmao.type.AsyncTransactionLogStatusEnum;
import com.power.platform.lanmao.type.BizOriginEnum;
import com.power.platform.lanmao.type.BizTypeEnum;
import com.power.platform.lanmao.type.BusinessStatusEnum;
import com.power.platform.lanmao.type.BusinessTypeEnum;
import com.power.platform.lanmao.type.ProjectStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.weixin.service.WeixinSendTempMsgService;

/**
 * 
 * 类: P2pTradeBidController <br>
 * 描述: 网贷资金存管接口，交易类. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年5月23日 上午9:00:38
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/p2p/trade/bid")
public class P2pTradeBidController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(P2pTradeBidController.class);

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
	 * 标的报备回调地址.
	 */
	private static final String CALLBACK_URL_CREATE = Global.getConfigUb("CALLBACK_URL_CREATE");
	/**
	 * 放款回调地址.
	 */
	private static final String CALLBACK_URL_GRANT = Global.getConfigUb("CALLBACK_URL_GRANT");
	/**
	 * 流标回调地址.
	 */
	private static final String CALLBACK_URL_CANCEL = Global.getConfigUb("CALLBACK_URL_CANCEL");
	/**
	 * 还款回调地址.
	 */
	private static final String CALLBACK_URL_REPAY = Global.getConfigUb("CALLBACK_URL_REPAY");
	/**
	 * 代偿还款回调地址.
	 */
	private static final String CALLBACK_URL_REPLACE_REPAY = Global.getConfigUb("CALLBACK_URL_REPLACE_REPAY");

	/**
	 * 平台营销款账户
	 */
	private static final String SYS_GENERATE_002 = Global.getConfigLanMao("sys_generate_002");

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
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
	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Resource
	private IfCertUserInfoDao ifCertUserInfoDao;

	@Autowired
	private ScatterInvestDataAccessService scatterInvestDataAccessService;
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
	private LanMaoAsyncTransactionService lanMaoAsyncTransactionService;

	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private LanMaoProjectService lanMaoProjectService;
	@Autowired
	private AsyncTransactionLogDao asyncTransactionLogDao;
	@Autowired
	private LanMaoSearchUserInfoDataService lanMaoSearchUserInfoDataService;

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
	@RequestMapping(value = "cancel")
	@Transactional(rollbackFor = Exception.class)
	public String cancel(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("fn:cancel-流标");

		WloanTermProject entity = wloanTermProjectService.get(wloanTermProject.getId());
		if (null != entity) {
			// 放款日期.
			Date loanDate = entity.getLoanDate();
			boolean isBoolean = DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), DateUtils.getDate(loanDate, "yyyy-MM-dd HH:mm:ss"));
			if (isBoolean) { // 项目没到放款日期，可继续投资.
				addMessage(redirectAttributes, "项目没到流标日期，项目可以继续出借.");
				if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
				} else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
				}
			} else { // 可以操作流标或者放款.
				log.info("fn:callbackCancel-可以操作流标，可以操作放款.");
			}
		}

		Map<String, String> params = new HashMap<String, String>();
		/**
		 * 业务请求参数封装.
		 */
		// 标的Id.
		params.put("bidId", wloanTermProject.getId());
		// 由网贷平台生成的唯一的交易流水号非必填项，如填写，则回调返回本流水号，如不填写，返回bidId（否）.
		// params.put("orderId", wloanTermProject.getId());
		// 红包流向，01-返还到红包账户，02-留在投资人账户，默认01.
		params.put("rpDirect", "01");
		// 异步通知地址（是）.
		params.put("callbackUrl", CALLBACK_URL_CANCEL);
		/**
		 * 公共请求参数封装.
		 */
		// 接口名称：每个接口提供不同的编码.
		params.put("service", "p2p.trade.bid.cancel");
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
		log.info("fn:cancel-参数列表：" + paramsJsonStr);
		// 商户自己的RSA公钥加密.
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);
		/**
		 * HTTP.
		 */
		encryptRet.put("merchantId", MERCHANT_ID);
		String requestJsonStr = JSON.toJSONString(encryptRet);
		log.info("fn:cancel-请求：" + requestJsonStr);
		String responseStr = HttpUtil.sendPost(HOST, encryptRet);
		log.info("fn:cancel-响应：" + responseStr);

		/**
		 * 解析响应.
		 */
		JSONObject jsonObject = JSONObject.parseObject(responseStr);
		String tm = (String) jsonObject.get("tm");
		String data = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> map = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		log.info("fn:cancel-解析响应：" + JSON.toJSONString(map));

		if ("00".equals(map.get("respCode"))) {
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

		addMessage(redirectAttributes, map.get("respCode") + "|" + map.get("respSubCode") + "|" + map.get("respMsg"));
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
	 * 方法: create <br>
	 * 描述: 标的报备. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月25日 上午10:07:22
	 * 
	 * @param wloanTermProject
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:create:edit")
	@RequestMapping(value = "create")
	@Transactional(rollbackFor = Exception.class)
	public String create(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("标的报备");

		Map<String, String> params = new HashMap<String, String>();
		/**
		 * 业务请求参数封装.
		 */
		// 标的Id.
		params.put("bidId", wloanTermProject.getId());
		// 标的名称.
		params.put("name", wloanTermProject.getName());
		// 币种，默认为CNY，以下为可选，CNY (人民币).
		params.put("currency", "CNY");
		// 标的金额，单位（分）.
		BigDecimal amount = new BigDecimal(NumberUtils.scaleDouble(wloanTermProject.getAmount() * 100));
		params.put("amount", amount.toString());
		// 融资主体.
		WloanSubject wloanSubject = wloanSubjectService.get(wloanTermProject.getSubjectId());
		if (null != wloanSubject) {
			/**
			 * 借款人信息.
			 */
			CreditUserInfo creditUserInfo = creditUserInfoService.get(wloanSubject.getLoanApplyId());
			if (null != creditUserInfo) {
				// 借款人，网贷平台唯一的用户编码.
				params.put("userId", creditUserInfo.getId());
			}
			/**
			 * 受托支付标识，0：否，1：是.
			 */
			if (WloanSubjectService.IS_ENTRUSTED_PAY_0.equals(wloanTermProject.getIsEntrustedPay())) {
			} else if (WloanSubjectService.IS_ENTRUSTED_PAY_1.equals(wloanTermProject.getIsEntrustedPay())) {
				// 受托支付标识（默认0），0- 否，1- 是，（否）.
				params.put("isEntrustedPay", "1");
				// 受托人银行卡号（isEntrustedPay =1时必填）（否）.
				params.put("bankCardNO", wloanSubject.getCashierBankNo());
				// 户名（isEntrustedPay =1时必填）（否）.
				params.put("cardName", wloanSubject.getCashierUser());
				// 银行编码（isEntrustedPay =1时必填）（否）.
				params.put("bankCode", wloanSubject.getCashierBankCode());
				// 账户对公对私标识（isEntrustedPay =1时必填），1-对公，2-对私（否）.
				params.put("cardFlag", wloanSubject.getCashierBankNoFlag());
				if (WloanSubjectService.CASHIER_BANK_NO_FLAG_2.equals(wloanSubject.getCashierBankNoFlag())) {
				} else if (WloanSubjectService.CASHIER_BANK_NO_FLAG_1.equals(wloanSubject.getCashierBankNoFlag())) {
					// 联行号（isEntrustedPay =1且cardFlag=1时必填）（否）.
					params.put("issuer", wloanSubject.getCashierBankIssuer());
				}
			}

			// 借款方用户类型，1- 个人，2- 企业.
			params.put("borrUserType", wloanSubject.getType());
			if (wloanSubject.getType().equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) { // 个人.
				// 借款方证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照，BLC-营业执照，USCC-统一社会信用代码.
				params.put("borrCertType", wloanSubject.getCorporationCertType());
				// 借款方证件号码，社会信用证或营业执照号（借款方类型为企业时）.
				params.put("borrCertNo", wloanSubject.getLoanIdCard());
				// 借款方名称.
				params.put("borrName", wloanSubject.getLoanUser());
			} else if (wloanSubject.getType().equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) { // 企业.
				// 借款方证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照，BLC-营业执照，USCC-统一社会信用代码.
				params.put("borrCertType", wloanSubject.getBusinessLicenseType());
				// 借款方证件号码，社会信用证或营业执照号（借款方类型为企业时）.
				params.put("borrCertNo", wloanSubject.getBusinessNo());
				// 借款方名称.
				params.put("borrName", wloanSubject.getCompanyName());
			}
			// 借款方手机号码（否）.
			params.put("borrMobiPhone", wloanSubject.getLoanPhone());
		}
		// 借标的年化收益率，小数点前最多一位，后面最多四位.
		Double annualRate = wloanTermProject.getAnnualRate() / 100;
		params.put("bidRate", annualRate.toString());
		// 标的类型，01-信用，02-抵押，03-债权转让，99-其他.
		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			params.put("bidType", "99");
		} else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
			params.put("bidType", "03");
		}

		// 标的开始时间（否）.
		SimpleDateFormat bt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginTime = bt.format(wloanTermProject.getOnlineDate()); // 标的上线日期.
		params.put("beginTime", beginTime);
		// 借款周期.
		params.put("cycle", wloanTermProject.getSpan().toString());
		// 还款方式，01-一次还本付息，02-等额本金，03-等额本息，04-按期付息到期还本，99-其他.
		if (wloanTermProject.getRepayType().equals(WloanTermProjectService.REPAY_TYPE_1)) {
			params.put("repaymentType", "01");
		} else if (wloanTermProject.getRepayType().equals(WloanTermProjectService.REPAY_TYPE_2)) {
			params.put("repaymentType", "04");
		}
		// 借款用途.
		params.put("borrPurpose", wloanTermProject.getPurpose());
		// 逾期罚息，小数点前最多一位，后面最多四位（否）.
		// params.put("overDueRate", "0.12");
		// 推荐机构（否）.
		// params.put("recommer", wloanTermProject.getWgCompany().getName());
		// 限定最低投标份数（否）.
		// params.put("minLimitNum", "1");
		// 限定每份投标金额（否）.
		// params.put("limitAmount", "1");
		// 限定最大投标金额（否）.
		BigDecimal maxLimitAmount = new BigDecimal(NumberUtils.scaleDouble(wloanTermProject.getMaxAmount() * 100));
		params.put("maxLimitAmount", maxLimitAmount.toString());
		// 限定最少投标金额（否）.
		BigDecimal minLimitAmount = new BigDecimal(NumberUtils.scaleDouble(wloanTermProject.getMinAmount() * 100));
		params.put("minLimitAmount", minLimitAmount.toString());
		// 借款人年化利率，小数点前最多一位，后面最多四位（否）.
		// params.put("lenderRate", "1");
		// 标的公示地址URL（否）.
		// params.put("url", "1");
		// 标的产品类型，01-房贷类，02-车贷类，03-收益权转让类，04-信用贷款类，05-股票配资类，06-银行承兑汇票，07-商业承兑汇票，08-消费贷款类，09-供应链类，99-其他.
		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			params.put("productType", "99");
		} else if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2)) {
			params.put("productType", "09");
		}

		// 借款方固定电话（否）.
		// params.put("borrPhone", "1");
		// 借款方工作单位（否）.
		// params.put("borrWork", "1");
		// 借款方工作年限（否）.
		// params.put("borrWorkYear", "1");
		// 借款方税后月收入（元）（否）.
		// params.put("borrIncome", "1");
		// 借款方学历（否）.
		// params.put("borrEdu", "1");
		// 借款方婚姻状况，N-未婚，Y-已婚（否）.
		// params.put("borrMarriage", "1");
		// 借款方地址（否）.
		// params.put("borrAddr", "1");
		// 借款方电子邮箱（否）.
		// params.put("borrEmail", "1");
		// 异步通知地址（否）.
		// params.put("callbackUrl", "#");
		// 备注（否）.
		params.put("remark", "标的报备");
		// 受托支付标识（默认0），0- 否，1- 是，（否）.
		// params.put("isEntrustedPay", "1");
		// 受托人银行卡号（isEntrustedPay =1时必填）（否）.
		// params.put("bankCardNO", "1");
		// 户名（isEntrustedPay =1时必填）（否）.
		// params.put("cardName", "1");
		// 银行编码（isEntrustedPay =1时必填）（否）.
		// params.put("bankCode", "1");
		// 账户对公对私标识（isEntrustedPay =1时必填），1-对公，2-对私（否）.
		// params.put("cardFlag", "1");
		// 联行号（isEntrustedPay =1且cardFlag=1时必填）（否）.
		// params.put("issuer", "1");
		// 异步通知地址（是）.
		params.put("callbackUrl", CALLBACK_URL_CREATE);
		/**
		 * 公共请求参数封装.
		 */
		// 接口名称：每个接口提供不同的编码.
		params.put("service", "p2p.trade.bid.create");
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
		System.err.println("参数列表：" + paramsJsonStr);
		// log.info("MERCHANT_RSA_PUBLIC_KEY = " + MERCHANT_RSA_PUBLIC_KEY);
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
		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 4.1.10 产品信息
			// 时时推送产品信息
			// 上报产品信息数据触发时间：产品对出借人发布时，上报产品信息数据。
			List<String> projectIdList = new ArrayList<String>();
			projectIdList.add(wloanTermProject.getId());
			Map<String, Object> result = lendProductDataAccessService.pushLendProduct(projectIdList);
			log.info("时时推送产品信息:" + result.get("respMsg").toString());
		}
		
		if (ServerURLConfig.IS_REAL_TIME_PUSH) {
			// 4.1.2 散标信息
			// 时时推送散标信息
			// 上报散标数据触发时间：散标在“散标状态-初始公布”时，需上报散标信息数据。
			List<String> projectIdList = new ArrayList<String>();
			projectIdList.add(wloanTermProject.getId());
			Map<String, Object> result = scatterInvestDataAccessService.pushScatterInvestInfo(projectIdList);
			log.info("时时推送散标信息:" + result.get("respMsg").toString());
		}
		
		// 页面消息通知.
		// model.addAttribute("respCode", map.get("respCode"));
		// model.addAttribute("respMsg", map.get("respMsg"));
		// model.addAttribute("respSubCode", map.get("respSubCode"));
		addMessage(redirectAttributes, map.get("respMsg"));
		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
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
	// @RequestMapping(value = "grant")
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
	 * methods: lmGrant <br>
	 * description: 懒猫，出借确认（放款）. <br>
	 * author: Roy <br>
	 * date: 2019年10月5日 下午8:31:36
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
	public synchronized String lmGrant(WloanTermProject wloanTermProject, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("懒猫出借确认（放款）...start...");

		try {
			String projectId = ""; // 标的id
			String subjectId = ""; // 融资主体id
			String borrowersId = ""; // 借款人id
			String projectNo = ""; // 标的编号
			Double proAmount = 0D; // 标的金额
			if (null != wloanTermProject) {
				projectId = wloanTermProject.getId();
				subjectId = wloanTermProject.getSubjectId();
				projectNo = wloanTermProject.getSn();
				proAmount = wloanTermProject.getAmount();
				WloanSubject subject = wloanSubjectService.get(subjectId);
				if (null != subject) {
					borrowersId = subject.getLoanApplyId();
				}
			}

			String batchNo = IdGen.uuid(); // Y 批次号
			List<SyncTransaction> bizDetails = new ArrayList<SyncTransaction>(); // 交易明细
			List<AsyncTransactionLog> atls = new ArrayList<AsyncTransactionLog>(); // 批量交易订单日志

			WloanTermInvest entity = new WloanTermInvest();
			entity.setState(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1);
			entity.setProjectId(projectId);
			List<WloanTermInvest> investList = wloanTermInvestDao.findList(entity);

			WloanTermInvest invest = null;
			SyncTransaction syncTransaction = null;
			SyncTransactionDetail std = null;
			LmTransaction lt = null; // 流水号留存
			AsyncTransactionLog atl = null; // 批量交易日志，交易失败逻辑处理
			Double sumInterestAmount = 0D; // 该标的出借所产生的利息总额
			Double sumVoucherAmount = 0D;// 该标的出借所使用的红包总额
			long currentTimeMillis = System.currentTimeMillis();
			if (null != investList && investList.size() > 0) {
				for (int i = 0; i < investList.size(); i++) {
					invest = investList.get(i);
					String investId = invest.getId(); // 出借订单id
					String userId = invest.getUserId(); // 出借人帐号id
					Double interest = invest.getInterest();
					sumInterestAmount = NumberUtils.add(sumInterestAmount, interest);
					Double amount = invest.getAmount(); // 出借金额
					Double voucherAmount = invest.getVoucherAmount(); // 红包金额
					sumVoucherAmount = NumberUtils.add(sumVoucherAmount, voucherAmount); // 该标的出借确认，使用红包总额
					Double realInvestAmount = NumberUtils.subtract(amount, voucherAmount); // 实际出借金额
					String tradeRequestNo = IdGen.uuid(); // 交易明细订单号
					// Y 交易明细
					syncTransaction = new SyncTransaction();
					syncTransaction.setRequestNo(tradeRequestNo); // Y 交易明细订单号
					syncTransaction.setTradeType(BizTypeEnum.TENDER.getValue()); // Y 交易类型
					syncTransaction.setProjectNo(projectNo); // N 标的编号
					syncTransaction.setSaleRequestNo(null); // N 债权出让请求流水号
					// Y 业务明细
					List<SyncTransactionDetail> details = new ArrayList<SyncTransactionDetail>();
					std = new SyncTransactionDetail();
					std.setBizType(BusinessTypeEnum.TENDER.getValue()); // Y 业务类型
					std.setFreezeRequestNo(investId); // N 授权预处理，请求流水号
					std.setSourcePlatformUserNo(userId); // N 出款方用户编号
					std.setTargetPlatformUserNo(borrowersId); // N 收款方用户编号
					std.setAmount(NumberUtils.scaleDoubleStr(realInvestAmount)); // Y 本息和
					std.setIncome(null); // N 利息
					std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
					std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
					std.setRemark("出借确认放款");// N
					details.add(std);
					// 批量交易日志，交易失败逻辑处理
					atl = new AsyncTransactionLog();
					atl.setId(IdGen.uuid());
					atl.setAsyncRequestNo(tradeRequestNo); // 异步通知时的交易明细订单号
					atl.setFreezeRequestNo(investId); // 出借订单
					atl.setBizType(BizTypeEnum.TENDER.getValue()); // 交易类型：出借
					atl.setBizOrigin(BizOriginEnum.DISPERSION.getValue()); // 业务来源
					atl.setStatus(AsyncTransactionLogStatusEnum.INIT.getValue()); // 处理中
					currentTimeMillis = currentTimeMillis + 1000;
					atl.setCreateDate(new Date(currentTimeMillis));
					atl.setUpdateDate(new Date(currentTimeMillis));
					atls.add(atl);

					/**
					 * 抵用券逻辑，获取出借订单中的红包金额（抵用券合计总额）
					 */
					std = new SyncTransactionDetail();
					std.setBizType(BusinessTypeEnum.MARKETING.getValue()); // Y 业务类型
					std.setFreezeRequestNo(investId); // N 授权预处理，请求流水号
					std.setSourcePlatformUserNo(SYS_GENERATE_002); // N 出款方用户编号
					std.setTargetPlatformUserNo(borrowersId); // N 收款方用户编号
					std.setAmount(NumberUtils.scaleDoubleStr(voucherAmount)); // Y 本息和
					std.setIncome(null); // N 利息
					std.setShare(null); // N 债权份额（债权认购且需校验债权关系的必传）
					std.setCustomDefine(null); // N 平台商户自定义参数，平台交易时传入的自定义参数
					std.setRemark("营销红包");// N
					details.add(std);
					syncTransaction.setDetails(details);
					bizDetails.add(syncTransaction);
				}
			}

			// 平台营销账户查询
			Map<String, Object> searchUserInfoMap = lanMaoSearchUserInfoDataService.searchUserInfo(SYS_GENERATE_002);
			Object availableAmountObject = searchUserInfoMap.get("availableAmount");
			Double sysAvailableAmount = Double.parseDouble(availableAmountObject.toString()); // 平台营销账户可用余额
			logger.info("平台营销账户可用余额:{}", NumberUtils.scaleDoubleStr(sysAvailableAmount));
			if (sumVoucherAmount > NumberUtils.scaleDouble(sysAvailableAmount)) {
				addMessage(redirectAttributes, "平台营销款账户可用余额不足以抵扣此标的出借所使用红包金额，请财务人员尽快充值到账......");
				if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
				}
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
			}

			/**
			 * 出借确认（放款），批量交易
			 */
			AsyncTransaction asyncTransaction = new AsyncTransaction();
			asyncTransaction.setBatchNo(batchNo);
			asyncTransaction.setBizDetails(bizDetails);
			Map<String, String> asyncTransactionMap = lanMaoAsyncTransactionService.asyncTransaction(asyncTransaction);
			if (BusinessStatusEnum.SUCCESS.getValue().equals(asyncTransactionMap.get("status")) && "0".equals(asyncTransactionMap.get("code"))) {
				log.info("懒猫出借确认（放款），批量交易成功......");
				// 平台留存
				lt = new LmTransaction();
				lt.setId(IdGen.uuid());
				lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
				lt.setBatchNo(batchNo); // 批次号
				lt.setPlatformUserNo(borrowersId);
				lt.setCode(asyncTransactionMap.get("code"));
				lt.setStatus(asyncTransactionMap.get("status"));
				lt.setRemarks("债权认购，出借确认（放款），批量交易");
				lt.setTradeType(BizTypeEnum.CREDIT_ASSIGNMENT.getValue());
				lt.setProjectNo(projectNo);// 标的编号
				lt.setCreateDate(new Date(currentTimeMillis));
				lt.setUpdateDate(new Date(currentTimeMillis));
				int insertCreditAssignmentFlag = lmTransactionDao.insert(lt);
				logger.info("债权认购，出借确认（放款），批量交易记录留存:{}", insertCreditAssignmentFlag == 1 ? "成功" : "失败");

				// 批量交易日志，最终确认出借确认是否成功
				for (AsyncTransactionLog atlNext : atls) {
					int insertAtlNextFlag = asyncTransactionLogDao.insert(atlNext);
					logger.info("出借批量交易日志，插入:{}", insertAtlNextFlag == 1 ? "成功" : "失败");
				}

				/**
				 * 标的生命周期进入还款中，变更标的状态
				 */
				String proRequestNo = IdGen.uuid();
				Map<String, Object> modifyProjectMap = lanMaoProjectService.modifyProject(proRequestNo, projectNo, ProjectStatusEnum.REPAYING.getValue());
				if (BusinessStatusEnum.SUCCESS.getValue().equals(modifyProjectMap.get("status")) && "0".equals(modifyProjectMap.get("code"))) {
					log.info("懒猫变更标的成功......");
					wloanTermProject.setState(WloanTermProjectService.REPAYMENT);
					wloanTermProject.setUpdateById(SessionUtils.getUser().getId());
					wloanTermProject.setUpdateDate(new Date(currentTimeMillis));
					wloanTermProject.setRealLoanDate(new Date(currentTimeMillis));
					wloanTermProjectService.updateProState(wloanTermProject);
					log.info("平台变更标的已执行......");
					// 平台留存
					lt = new LmTransaction();
					lt.setId(IdGen.uuid());
					lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
					lt.setBatchNo(batchNo); // 批次号
					lt.setRequestNo(proRequestNo);
					lt.setPlatformUserNo(borrowersId);
					lt.setCode(asyncTransactionMap.get("code"));
					lt.setStatus(asyncTransactionMap.get("status"));
					lt.setRemarks("出借确认（放款），变更标的");
					lt.setProjectNo(projectNo);// 标的编号
					currentTimeMillis = currentTimeMillis + 1000;
					lt.setCreateDate(new Date(currentTimeMillis));
					lt.setUpdateDate(new Date(currentTimeMillis));
					int insertLtFlag = lmTransactionDao.insert(lt);
					logger.info("出借确认（放款），变更标的记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");
					/**
					 * 调整标的还款计划每期金额
					 */
					List<WloanTermProjectPlan> projectPlans = wloanTermProjectPlanService.findListByProjectId(projectId);
					for (WloanTermProjectPlan projectPlan : projectPlans) {
						Double sumInterest = 0D;
						WloanTermUserPlan userPlanQuery = new WloanTermUserPlan();
						userPlanQuery.setProjectId(projectId);
						userPlanQuery.setRepaymentDate(projectPlan.getRepaymentDate());
						List<WloanTermUserPlan> userPlans = wloanTermUserPlanDao.findUserRepayPlans(userPlanQuery);
						for (WloanTermUserPlan userPlan : userPlans) {
							sumInterest = NumberUtils.add(sumInterest, userPlan.getInterest());
						}
						projectPlan.setInterest(NumberUtils.scaleDouble(sumInterest)); // 调整后的标的每期还款金额
						int updateProjectPlanFlag = wloanTermProjectPlanDao.update(projectPlan);
						logger.info("更新标的还款计划，调整:{}", updateProjectPlanFlag == 1 ? "成功" : "失败");
					}
					/**
					 * 调整平台营销款账户可用余额与账户总额，记录红包支出流水
					 */
					CreditUserInfo sysGenerateUserInfo = creditUserInfoDao.get(SYS_GENERATE_002);
					if (null != sysGenerateUserInfo) {
						CreditUserAccount sysGenerateUserAccount = creditUserAccountDao.get(sysGenerateUserInfo.getAccountId());
						Double sysGenerateAvailableAmount = 0D;
						if (null != sysGenerateUserAccount) {
							sysGenerateAvailableAmount = sysGenerateUserAccount.getAvailableAmount();
							int updateSysGenerateCreditUserAccountFlag = creditUserAccountDao.updateSysGenerateCreditUserAccount(sysGenerateUserAccount.getId(), sumVoucherAmount);
							CgbUserTransDetail userTransDetail = null;
							if (updateSysGenerateCreditUserAccountFlag == 1) {
								logger.info("平台营销款账户更新成功......");
								// 记录，平台营销款账户支出红包流水
								userTransDetail = new CgbUserTransDetail();
								userTransDetail.setId(IdGen.uuid()); // 主键.
								userTransDetail.setTransId(projectId); // 项目ID(放款单号).
								userTransDetail.setUserId(sysGenerateUserInfo.getId()); // 客户帐号.
								userTransDetail.setAccountId(sysGenerateUserAccount.getId()); // 客户账户.
								currentTimeMillis = currentTimeMillis + 1000;
								userTransDetail.setTransDate(new Date(currentTimeMillis)); // 交易时间.
								userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 抵用券支出.
								userTransDetail.setAmount(NumberUtils.scaleDouble(sumVoucherAmount)); // 金额.
								sysGenerateAvailableAmount = NumberUtils.subtract(sysGenerateAvailableAmount, sumVoucherAmount);
								userTransDetail.setAvaliableAmount(sysGenerateAvailableAmount); // 可用余额.
								userTransDetail.setInOutType(CgbUserTransDetailService.OUT_TYPE_2); // 支出.
								userTransDetail.setRemarks("抵用券"); // 备注信息.
								userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
								int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
								logger.info("平台营销款账户，抵用券支出流水插入:{}", userTransDetailFlag == 1 ? "成功" : "失败");
							} else {
								logger.info("平台营销款账户更新失败......");
							}
						}
					}
				} else {
					// 平台留存
					lt = new LmTransaction();
					lt.setId(IdGen.uuid());
					lt.setServiceName(ServiceNameEnum.MODIFY_PROJECT.getValue());
					lt.setBatchNo(batchNo); // 批次号
					lt.setRequestNo(proRequestNo);
					lt.setPlatformUserNo(borrowersId);
					lt.setCode(asyncTransactionMap.get("code"));
					lt.setStatus(asyncTransactionMap.get("status"));
					lt.setErrorCode(asyncTransactionMap.get("errorCode"));
					lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
					lt.setRemarks("出借确认（放款），变更标的");
					lt.setProjectNo(projectNo);// 标的编号
					lt.setCreateDate(new Date(currentTimeMillis));
					lt.setUpdateDate(new Date(currentTimeMillis));
					int insertLtFlag = lmTransactionDao.insert(lt);
					logger.info("出借确认（放款），变更标的记录留存:{}", insertLtFlag == 1 ? "成功" : "失败");
					addMessage(redirectAttributes, "出借确认（放款），变更标的，errorCode:" + modifyProjectMap.get("errorCode") + "，errorMessage:" + modifyProjectMap.get("errorMessage"));
					if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
						return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
					}
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
				}

				/**
				 * 借款人账户变更
				 */
				CreditUserInfo creditUserInfo = creditUserInfoDao.get(borrowersId);
				if (null != creditUserInfo) { // 借款人帐号信息.
					CreditUserAccount creditUserAccount = creditUserAccountDao.get(creditUserInfo.getAccountId());
					Double availableAmount = creditUserAccount.getAvailableAmount(); // 借款人账户可用余额
					Double surplusAmount = NumberUtils.scaleDouble(NumberUtils.add(proAmount, sumInterestAmount)); // 借款人应还金额
					int creditUserAccountFlag = creditUserAccountDao.updateAmount(creditUserAccount.getId(), proAmount, surplusAmount);
					if (creditUserAccountFlag == 1) { // 借款人账户更新.
						log.info("借款人账户更新成功......");
						availableAmount = NumberUtils.add(availableAmount, proAmount); // 借款人收到放款金额后，账户可用余额
						/**
						 * 计入流水.
						 */
						CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
						userTransDetail.setId(IdGen.uuid()); // 主键.
						userTransDetail.setTransId(projectId); // 项目ID(放款单号).
						userTransDetail.setUserId(borrowersId); // 客户帐号.
						userTransDetail.setAccountId(creditUserAccount.getId()); // 客户账户.
						userTransDetail.setTransDate(new Date()); // 交易时间.
						userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_11); // 放款.
						userTransDetail.setAmount(NumberUtils.scaleDouble(proAmount)); // 金额.
						userTransDetail.setAvaliableAmount(availableAmount); // 可用余额.
						userTransDetail.setInOutType(CgbUserTransDetailService.IN_TYPE_1); // 收入.
						userTransDetail.setRemarks("放款"); // 备注信息.
						userTransDetail.setState(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
						int userTransDetailFlag = cgbUserTransDetailDao.insert(userTransDetail);
						if (userTransDetailFlag == 1) {
							log.info("放款流水添加成功......");
							// 放款短消息发送.
							weixinSendTempMsgService.cgbSendGrantInfoMsg(creditUserInfo, proAmount);
						} else {
							log.info("放款流水添加失败......");
						}
					} else {
						log.info("借款人账户更新失败......");
					}
				}

				logger.info("懒猫出借确认（放款）...end...");

				/**
				 * 国家互联网应急中心，数据推送
				 */
				if (ServerURLConfig.IS_REAL_TIME_PUSH) {
					// 4.1.4 还款计划
					// 时时推送还款计划
					// 上报还款计划数据触发时间：某个借款人成功申请借款，平台准备放款时，还款计划业务数据产生之后上报该业务数据。
					List<String> projectIdList = new ArrayList<String>();
					projectIdList.add(projectId);
					Map<String, Object> res = repayPlanDataAccessService.pushRepayPlanInfo(projectIdList);
					logger.info("时时推送散标状态:" + res.get("respMsg").toString());

					// 4.1.3 散标状态-（放款）
					// 时时推送散标状态 的满标、放款、还款状态
					Map<String, Object> map1 = scatterInvestStatusDataAccessService.pushScatterInvestStatus(projectId);
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
					userTransDetail.setTransDate(new Date()); // 交易时间.
					userTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_11); // 放款.
					userTransDetail.setAmount(proAmount); // 金额.
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
					List<WloanTermInvest> wtiList = wloanTermInvestDao.findListByProjectId(projectId);
					if (wtiList != null && wtiList.size() > 0) {
						currentTime = System.currentTimeMillis();
						Map<String, Object> mapTransact = lendParticularsDataAccessService.pushLendParticularsInvTransInfo(wtiList, currentTime);
						log.info("出借人出借投资明细的推送:" + mapTransact.get("respMsg").toString());
					}

					// 4.1.1用户信息
					// 推送当前标的所有出借人信息.
					List<String> userIdList = new ArrayList<String>();
					for (WloanTermInvest wloanTermInvest : investList) {
						IfCertUserInfo ifCertUserInfo = new IfCertUserInfo();
						UserInfo ui = userInfoDao.getCgb(wloanTermInvest.getUserId());
						if (ui != null) {
							if (!"".equals(ui.getCertificateNo()) && ui.getCertificateNo() != null) {
								ifCertUserInfo.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(ui.getCertificateNo())));
								List<IfCertUserInfo> ifCertUserInfos = ifCertUserInfoDao.findList(ifCertUserInfo);
								if (ifCertUserInfos == null && ifCertUserInfos.size() == 0) {
									userIdList.add(ui.getId());
								}
							}
						}
					}
					if (userIdList != null && userIdList.size() > 0) {
						Map<String, Object> pushUserResult = ifcertUserInfoDataAccessService.pushInvestUserInfo(userIdList);
						log.info("推送出借人用户信息-实时:" + pushUserResult.get("respMsg"));
					}
				}
			} else {
				log.info("懒猫出借确认（放款），批量交易失败......");
				// 平台留存
				lt = new LmTransaction();
				lt.setId(IdGen.uuid());
				lt.setServiceName(ServiceNameEnum.ASYNC_TRANSACTION.getValue());
				lt.setBatchNo(batchNo); // 批次号
				lt.setPlatformUserNo(borrowersId);
				lt.setCode(asyncTransactionMap.get("code"));
				lt.setStatus(asyncTransactionMap.get("status"));
				lt.setErrorCode(asyncTransactionMap.get("errorCode"));
				lt.setErrorMessage(asyncTransactionMap.get("errorMessage"));
				lt.setRemarks("债权认购，出借确认（放款），批量交易");
				lt.setTradeType(BizTypeEnum.CREDIT_ASSIGNMENT.getValue());
				lt.setProjectNo(projectNo);// 标的编号
				lt.setCreateDate(new Date(currentTimeMillis));
				lt.setUpdateDate(new Date(currentTimeMillis));
				int insertCreditAssignmentFlag = lmTransactionDao.insert(lt);
				logger.info("债权认购，出借确认（放款），批量交易记录留存:{}", insertCreditAssignmentFlag == 1 ? "成功" : "失败");
				addMessage(redirectAttributes, "出借确认（放款），批量交易失败，errorCode:" + asyncTransactionMap.get("errorCode") + "，errorMessage:" + asyncTransactionMap.get("errorMessage"));
				if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
					return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
				}
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error:{}", e.getMessage());
			addMessage(redirectAttributes, "出借确认，放款异常，请联系开发小哥哥......");
			if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
				return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
			}
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
		}

		addMessage(redirectAttributes, "出借确认，放款成功，标的生命周期进入还款中......");
		if (wloanTermProject.getProjectProductType().equals(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1)) {
			return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/wloanproject/wloanTermProject/creditUserLoanList?repage";
	}
}