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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.tsign.ching.eSign.SignHelper;

import com.alibaba.druid.support.logging.Log;
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
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.BrokerageService;
import com.power.platform.activity.service.RedPacketService;
import com.power.platform.activity.service.ZtmgPartnerPlatformService;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.bouns.services.UserBounsPointService;
import com.power.platform.cache.Cache;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.IsHolidayOrBirthday;
import com.power.platform.common.utils.MemCachedUtis;
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
import com.power.platform.sys.entity.User;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.utils.CreateSupplyChainPdfContract;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

@Service("newInvestService")
public class NewInvestService extends CrudService<WloanTermInvest> {

	@Resource
	private WloanTermInvestDao wloanTermInvestDao;

	private static final Logger logger = Logger.getLogger(NewInvestService.class);

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
	 * 投资方法
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserInvest(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount,UserInfo user, CgbUserAccount account, String ip) throws WinException, Exception {

		Cache cache = MemCachedUtis.getMemCached();
		Principal principal = cache.get(token);
		UserInfo userInfo = principal.getUserInfo();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台投资
		logger.info("平台投资操作开始");
		Map<String, Object> investMap = insertUserInvestInfo(token, projectId, amount, voucherList, user, account, ip, orderId);
		logger.info("平台投资操作结束");
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList!=null && voucherList.size()>0) {
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
		Double tAmount = 100 * NumberUtils.scaleDouble(amount);
		BigDecimal totalAmount = new BigDecimal(tAmount);
		Double aAmount = 100 * NumberUtils.scaleDouble(amount - (vouAmount == null ? 0d : vouAmount));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList!=null && voucherList.size()>0) {
			params.put("rpOrderList", JSONArray.toJSONString(rpOrderList));
		}
		params.put("service", "p2p.trade.invest.create");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		params.put("callbackUrl", ServerURLConfig.BACK_INVEST_URL);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("投资[请求参数]" + jsonString);
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
		maps.put("projectId", projectId);
		maps.put("amount", amount.toString());

		return maps;
	}
	
	/**
	 * 投资方法(PC)
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserInvestPwdWeb(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount,UserInfo user, CgbUserAccount account, String ip) throws WinException, Exception {

		Cache cache = MemCachedUtis.getMemCached();
		Principal principal = cache.get(token);
		UserInfo userInfo = principal.getUserInfo();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台投资
		logger.info("平台投资操作开始");
		Map<String, Object> investMap = insertUserInvestInfo(token, projectId, amount, voucherList, user, account, ip, orderId);
		logger.info("平台投资操作结束");
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList!=null && voucherList.size()>0) {
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
		Double tAmount = 100 * NumberUtils.scaleDouble(amount);
		BigDecimal totalAmount = new BigDecimal(tAmount);
		Double aAmount = 100 * NumberUtils.scaleDouble(amount - (vouAmount == null ? 0d : vouAmount));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList!=null && voucherList.size()>0) {
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
		params.put("callbackUrl", ServerURLConfig.BACK_INVEST_URL);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("投资[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		
		String data = encryptRet.get("data");
      	data = URLEncoder.encode(data,"UTF-8");
      	String tm = encryptRet.get("tm");
      	tm = URLEncoder.encode(tm,"UTF-8");
      	encryptRet.put("tm", tm);
      	encryptRet.put("data", data);
      	encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
	}
	
	
	/**
	 * 投资方法(h5)
	 * 
	 * @param projectId
	 * @param amount
	 * @param vouid
	 * @param user
	 * @param account
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> newUserInvestPwdH5(String token, String projectId, Double amount, List<String> voucherList, Double vouAmount,UserInfo user, CgbUserAccount account, String ip) throws WinException, Exception {

		Cache cache = MemCachedUtis.getMemCached();
		Principal principal = cache.get(token);
		UserInfo userInfo = principal.getUserInfo();
		String orderId = UUID.randomUUID().toString().replace("-", "");
		String userId = userInfo.getId();

		// N1.平台投资
		logger.info("平台投资操作开始");
		Map<String, Object> investMap = insertUserInvestInfo(token, projectId, amount, voucherList, user, account, ip, orderId);
		logger.info("平台投资操作结束");
		// N2.银行存管请求投资
		/*
		 * 构造请求参数
		 */
		List<Map<String, Object>> rpOrderList = new ArrayList<Map<String, Object>>();
		if (voucherList!=null && voucherList.size()>0) {
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
		Double tAmount = 100 * NumberUtils.scaleDouble(amount);
		BigDecimal totalAmount = new BigDecimal(tAmount);
		Double aAmount = 100 * NumberUtils.scaleDouble(amount - (vouAmount == null ? 0d : vouAmount));
		BigDecimal accAmount = new BigDecimal(aAmount);
		params.put("totalAmount", totalAmount.toString());
		params.put("accAmount", accAmount.toString());
		params.put("currency", "CNY");
		params.put("bidId", projectId);
		if (voucherList!=null && voucherList.size()>0) {
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
		params.put("callbackUrl", ServerURLConfig.BACK_INVEST_URL);
		params.put("returnUrl", ServerURLConfig.BACK_INVEST_URL_BACKTOWEB);
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("投资[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		
		String data = encryptRet.get("data");
      	data = URLEncoder.encode(data,"UTF-8");
      	String tm = encryptRet.get("tm");
      	tm = URLEncoder.encode(tm,"UTF-8");
      	encryptRet.put("tm", tm);
      	encryptRet.put("data", data);
      	encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		return encryptRet;
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
			//遍历抵用券list
		    if(voucherList!=null && voucherList.size() >0){
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
							if(voucherEndDate!=null){
								overDate = DateUtils.formatDateTime(voucherEndDate);
							}else{
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
							//amount = amount - vouAmountTotal;

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
							if(voucherEndDate!=null){
								overDate = DateUtils.formatDateTime(voucherEndDate);
							}else{
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
				//启动线程生成电子签章
				new Thread(){
					public void run (){
						//生成电子签章
						createElectronicSign(pdfPathc,userc,projectc.getCreditUserApplyId(),projectc.getProjectProductType());
					}
				}.start();
				
				invest.setContractPdfPath(pdfPath.split("data")[1]);
				wloanTermInvestDao.update(invest);
			} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投项目.
				// 四方合同存储路径.
				String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, invest);
				pdfPathc = contractPdfPath;
				//启动线程生成电子签章
				new Thread(){
					public void run (){
						//生成电子签章
						createElectronicSign(pdfPathc,userc,projectc.getWloanSubject().getLoanApplyId(),projectc.getProjectProductType());
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
						if("2".equals(projectProductTypeString)){//供应链项目
							if (IsHolidayOrBirthday.isActivity()) {
								userBouns = userBouns * 2;
								addBouns(user.getId(), userBouns, investID);
							}else{
								if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
									userBouns = userBouns * 1.5;
									addBouns(user.getId(), userBouns, investID);
								} else {
									addBouns(user.getId(), userBouns, investID);
								}
							}
						}else{//安心投项目
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
				if("2".equals(projectProductTypeString)){//供应链项目
					if (IsHolidayOrBirthday.isActivity()) {
						userBouns = userBouns * 2;
						addBouns(user.getId(), userBouns, investID);
					}else{
						if (IsHolidayOrBirthday.isHoliday(user.getCertificateNo())) {
							userBouns = userBouns * 1.5;
							addBouns(user.getId(), userBouns, investID);
						} else {
							addBouns(user.getId(), userBouns, investID);
						}
					}
				}else{//安心投项目
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
				if(recommondUserPhone!=null && !recommondUserPhone.equals("")){
					UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
					String recommondUserId =  recommondUserInfo.getId();
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
			}else { // 再次投资(二次投资及以后的所有投资).
				String recommondUserPhone = user.getRecommendUserPhone();
				if(recommondUserPhone!=null && !recommondUserPhone.equals("")){
					UserInfo recommondUserInfo = userInfoDao.getUserInfoByPhone(recommondUserPhone);
					String recommondUserId =  recommondUserInfo.getId();
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

	//投资端生成电子签章
	public void createElectronicSign(String srcPdfFile,UserInfo userInfo,String creditUserApplyId,String projectType){
		int lastF = srcPdfFile.lastIndexOf("\\");
		if(lastF == -1){
			lastF = srcPdfFile.lastIndexOf("//");
		}
		// 最终签署后的PDF文件路径
		String signedFolder = srcPdfFile.substring(0, lastF+1);
		// 最终签署后PDF文件名称
		String signedFileName = srcPdfFile.substring(lastF+1, srcPdfFile.length());
		System.out.println("----<场景演示：使用标准的模板印章签署，签署人之间用文件二进制流传递>----");
		// 初始化项目，做全局使用，只初始化一次即可
		SignHelper.initProject();
		// 创建投资客户签章账户
		String userSignId;//客户签章id
		ElectronicSign electronicSignUser = new ElectronicSign();
		electronicSignUser.setUserId(userInfo.getId());
		List<ElectronicSign> electronicSignsList = electronicSignService.findList(electronicSignUser);
		if(electronicSignsList!=null && electronicSignsList.size()>0){
			userSignId = electronicSignsList.get(0).getSignId();
		}else{
			
			userSignId = SignHelper.addPersonAccountZTMG(userInfo);
			electronicSignUser.setId(IdGen.uuid());
			electronicSignUser.setSignId(userSignId);
			electronicSignUser.setCreateDate(new Date());
			electronicSignDao.insert(electronicSignUser);
		}
		
		// 创建投资客户印章（甲方）
		AddSealResult userSealData = SignHelper.addPersonTemplateSeal(userSignId);
		
		
		if("1".equals(projectType)){//安心投
			String loanUserId = creditUserApplyId;//借款人id
			CreditUserInfo loanUserInfo = creditUserInfoDao.get(loanUserId);
			String loanUserSignId;//借款人签章id
			ElectronicSign electronicSignLoanUser = new ElectronicSign();
			electronicSignLoanUser.setUserId(loanUserId);
			List<ElectronicSign> electronicSignsListLoan = electronicSignService.findList(electronicSignLoanUser);
			if(electronicSignsListLoan!=null && electronicSignsListLoan.size()>0){
				loanUserSignId = electronicSignsListLoan.get(0).getSignId();
			}else{
				
				loanUserSignId = SignHelper.addPersonAccountZTMGLoan(loanUserInfo);
				electronicSignLoanUser.setId(IdGen.uuid());
				electronicSignLoanUser.setSignId(loanUserSignId);
				electronicSignLoanUser.setCreateDate(new Date());
				electronicSignDao.insert(electronicSignLoanUser);
			}
			// 创建借款客户印章（乙方）
			AddSealResult loanUserSealData = SignHelper.addPersonTemplateSeal(loanUserSignId);
			
			//签署
			// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
			FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvestAXT(srcPdfFile);
			// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
			FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvestAXT(platformSignResult.getStream(),
					userSignId, userSealData.getSealData());
			String serviceIdUser = userPersonSignResult.getSignServiceId();
			// 借款客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（乙方）
			FileDigestSignResult loanUserPersonSignResult = SignHelper.loanUserPersonSignByStreamInvestAXT(userPersonSignResult.getStream(),
					loanUserSignId, loanUserSealData.getSealData());
			String serviceIdLoanUser = loanUserPersonSignResult.getSignServiceId();
			
			// 所有签署完成,将最终签署后的文件流保存到本地
			if (0 == loanUserPersonSignResult.getErrCode()) {
				SignHelper.saveSignedByStream(loanUserPersonSignResult.getStream(), signedFolder, signedFileName);
			}
			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
			electronicSignTranstail.setId(IdGen.uuid());
			electronicSignTranstail.setInvestUserId(userInfo.getId());
			electronicSignTranstail.setSupplyId(loanUserId);//借款人id
			electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
			electronicSignTranstail.setSignServiceIdSupply(serviceIdLoanUser);//借款人签署后服务id
			electronicSignTranstail.setCreateDate(new Date());
			electronicSignTranstailDao.insert(electronicSignTranstail);
//			SignHelper.userPersonSignByFileInvest(srcPdfFile, signedPdf, accountId, sealData)
			
			
		
		}else{//供应链
			//查询借款申请
			CreditUserApply creditUserApply = creditUserApplyService.get(creditUserApplyId);
			if(creditUserApply!=null){
				//查询供应商签章账户
				String supplyOrganizeAccountId;
				ElectronicSign electronicSignSupply = new ElectronicSign();
				electronicSignSupply.setUserId(creditUserApply.getCreditSupplyId());
				List<ElectronicSign> electronicSignsListSupply = electronicSignService.findList(electronicSignSupply);
				if(electronicSignsListSupply.size()>0){
					supplyOrganizeAccountId = electronicSignsListSupply.get(0).getSignId();
				}else{
					supplyOrganizeAccountId = null;
					logger.info("获取供应商签章账户失败");
				}
				
				WloanSubject wloanSubjectSupply = new WloanSubject();
				wloanSubjectSupply.setLoanApplyId(creditUserApply.getCreditSupplyId());
				List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubjectSupply);
				wloanSubjectSupply = wloanSubjectsList1.get(0);
				
				
				//查询核心企业签章账户
				String creditOrganizeAccountId;
				ElectronicSign electronicSignCredit = new ElectronicSign();
				electronicSignCredit.setUserId(creditUserApply.getReplaceUserId());
				List<ElectronicSign> electronicSignsListCredit = electronicSignService.findList(electronicSignCredit);
				if(electronicSignsListCredit.size()>0){
					creditOrganizeAccountId = electronicSignsListCredit.get(0).getSignId();
				}else {
					creditOrganizeAccountId =null;
					logger.info("获取企业签章账户失败");
				}
				
				WloanSubject wloanSubjectCredit = new WloanSubject();
				wloanSubjectCredit.setLoanApplyId(creditUserApply.getReplaceUserId());
				List<WloanSubject> wloanSubjectsListCredit = wloanSubjectService.findList(wloanSubjectCredit);
				wloanSubjectCredit = wloanSubjectsListCredit.get(0);
				
				// 创建供应商印章（乙方）
				AddSealResult userOrganizeSealDataSupply = null;
				if(supplyOrganizeAccountId!=null){
					userOrganizeSealDataSupply = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId,wloanSubjectSupply);
				}else{
					logger.info("获取供应商签章账户失败，无法生成电子签章");
				}
				
				// 创建核心企业印章（丁方）
				AddSealResult userOrganizeSealDataCredit = null;
				if(creditOrganizeAccountId!=null){
					userOrganizeSealDataCredit = SignHelper.addOrganizeTemplateSealZTMG(creditOrganizeAccountId,wloanSubjectCredit);
					logger.info("核心企业签章:"+userOrganizeSealDataCredit);
				}else{
					logger.info("获取核心企业签章账户失败，无法生成电子签章");
				}
				
				
				//签署
				// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
				FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvest(srcPdfFile);
				// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
				FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvest(platformSignResult.getStream(),
						userSignId, userSealData.getSealData());
				String serviceIdUser = userPersonSignResult.getSignServiceId();
				// 供应商客户签署,坐标定位,以文件流的方式传递pdf文档
				FileDigestSignResult userOrganizeSignResultSupply = SignHelper.userOrganizeSignByStreamSupplyInvest(
						userPersonSignResult.getStream(), supplyOrganizeAccountId,userOrganizeSealDataSupply.getSealData());
				String serviceIdSupply = userOrganizeSignResultSupply.getSignServiceId();
				// 核心企业客户签署,坐标定位,以文件流的方式传递pdf文档
				if(userOrganizeSealDataCredit!=null){
					FileDigestSignResult userOrganizeSignResultCredit = SignHelper.userOrganizeSignByStreamCreditInvest(
							userOrganizeSignResultSupply.getStream(), creditOrganizeAccountId,userOrganizeSealDataCredit.getSealData());
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
				}else{
					logger.info("核心企业签章userOrganizeSealDataCredit为空！");
				}
				
				
				
				
//				SignHelper.userPersonSignByFileInvest(srcPdfFile, signedPdf, accountId, sealData)
				
				
			}else{
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

	public void addBouns(String userId, Double userBouns, String transId) {

		// 添加客户投资积分信息
		UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
		// 添加账户积分历史明细
		UserBounsHistory userBounsHistory = new UserBounsHistory();
		userBounsHistory.setId(IdGen.uuid());
		userBounsHistory.setUserId(userId);
		userBounsHistory.setAmount(userBouns);
		userBounsHistory.setCreateDate(new Date());
		userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST);
		userBounsHistory.setTransId(transId);
		int insertUserBounsHistoryResult = userBounsHistoryService.insert(userBounsHistory);
		if (insertUserBounsHistoryResult > 0) {
			userBounsPoint.setScore(userBounsPoint.getScore() + userBouns.intValue());
			userBounsPoint.setUpdateDate(new Date());
			int i = userBounsPointService.update(userBounsPoint);
			if (i > 0) {
				logger.info("用户积分添加[" + userBouns + "]成功");
			}
		}
	}

	/**
	 * 取消投资
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
}
