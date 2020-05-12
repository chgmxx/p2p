package com.power.platform.cgb.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.AVouchersDicService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.cache.Cache;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.NewInvestService;
import com.power.platform.regular.service.UserInvestWebService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 用户投资API
 * 
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/newinvest")
@Service("cGBUserInvestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CGBUserInvestService {

	private static final Logger LOG = LoggerFactory.getLogger(CGBUserInvestService.class);

	@Autowired
	private NewInvestService newInvestService;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private UserAccountInfoService userAccountInfoService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private AVouchersDicService aVouchersDicService;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;
	@Autowired
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Autowired
	private UserInvestWebService userInvestWebService;

	/**
	 * 用户投资API
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 *            投资金额
	 * @param vouid
	 *            抵用劵ID
	 * @param vouAmount
	 *            抵用券金额
	 * @param projectId
	 *            标ID
	 * @param request
	 * @return
	 */
	@POST
	@Path("/saveUserInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> saveUserInvest(@FormParam("from") String from, @FormParam("token") String token, @FormParam("amount") String amount,@FormParam("vouchers")String vouchers, @FormParam("projectId") String projectId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			
			if(!StringUtils.isBlank(token)){
				throw new Exception("请更新版本");
			}
			
			// 判断必要参数是否为空
			if (StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}

			String ip = (String) request.getAttribute("ip");
			ip = ip.replace("_", ".");

			Double vAmountTotal = 0d;
			Double investAmount = Double.valueOf(amount);

			List<String> voucherList = new ArrayList<String>();
			if(vouchers!=null && !vouchers.equals("")){
				String[] voucherData = vouchers.split(",");
				voucherList = Arrays.asList(voucherData);
			}

			WloanTermProject project = wloanTermProjectService.get(projectId);
			String canUseCoupon = project.getIsCanUseCoupon();
			if(WloanTermProjectService.ISCANUSE_COUPON_NO.equals(canUseCoupon)){
				voucherList = null;//
			}
			
			if(voucherList!=null && voucherList.size()>0){
				for (int i = 0; i < voucherList.size(); i++) {
					String vouid = voucherList.get(i);
					AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
					if(aUserAwardsHistory!=null){
						AVouchersDic voucher = aVouchersDicService.get(aUserAwardsHistory.getAwardId());
						if(voucher!=null){
							vAmountTotal = vAmountTotal + voucher.getAmount();
						}
					}
				}
			}
			if(vAmountTotal > investAmount*0.01){
				throw new WinException("使用抵用券总额超过最大可用额度");
			}
			
			// 从缓存获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null == principal) { // 空判断，系统超时.
				LOG.info("fn:saveUserInvest，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", data);
				return result;
			}
			UserInfo user = principal.getUserInfo();
			CgbUserAccount account = cgbUserAccountService.get(user.getAccountId());
			// 判断账户余额是否可进行出借
			Double userAvailableAmount = NumberUtils.scaleDouble(account.getAvailableAmount());
			if (userAvailableAmount < Double.valueOf(amount)) {
				throw new WinException("账户可用余额不足");
			}

			

			if (project == null) {
				throw new WinException("项目信息为空");
			} else {
				Date createDate = new Date();
				Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
				// 判断项目是否过期
				if (createDate.getTime() >= loan_date.getTime()) {
					throw new WinException("该项目已到期，不能进行投资");
				}
				/**
				 * 新手标的判断.
				 */
				String isNewType = project.getProjectType();
				if (isNewType.equals(WloanTermProjectService.PROJECT_TYPE_2)) { // 1：其它，2：新手标的，3：推荐标的.
					// 是新手标的，查看投资人是否是新手
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests != null && newUserInvests.size() > 0) {
						throw new WinException("该项目只能新手出借");
					}
				}

				// 固定某个标的新手只可投资一万

				if (project.getId() == "ef2e5190a4c34ac5aa12bc02c2b6ed99" || project.getId().equals("ef2e5190a4c34ac5aa12bc02c2b6ed99")) {
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests == null) {
						if (Double.valueOf(amount) > 10000) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
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
						if (!Double.valueOf(amount).equals(balanceAmount)) {
							throw new WinException("该项目目前只能投资" + balanceAmount + "元");
						}
					} else {
						// 投资金额大于融资金额.
						if (Double.valueOf(amount) > balanceAmount) {
							throw new WinException("您的投资金额大于可投资金额！");
						}
						// 最小投资、最大投资判断
						if (Double.valueOf(amount) < project.getMinAmount() || Double.valueOf(amount) > project.getMaxAmount()) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
			}

			Map<?, ?> map = new HashMap<String, Object>();
			// 增加是否授权的判断
			UserInfo userInfo = userInfoService.get(user.getId());
			if (userInfo == null) {
				userInfo = userInfoService.getCgb(user.getId());
			}
			if (userInfo != null) {
				if (userInfo.getAutoState() != null) {
					if (userInfo.getAutoState().equals("0")) {
						data.put("respSubCode", "200419");
						data.put("respMsg", "免密无权限");
						LOG.info("投资失败" + map);
						result.put("state", "1");
						result.put("message", "投资失败");
						result.put("data", data);
						return result;
					}
				} else {
					data.put("respSubCode", "200419");
					data.put("respMsg", "免密无权限");
					LOG.info("投资失败" + map);
					result.put("state", "1");
					result.put("message", "投资失败");
					result.put("data", data);
					return result;
				}
			}
			map = newInvestService.newUserInvest(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, account, ip);
			
			if (map.get("respSubCode").equals("000000")) {
				LOG.info("投资成功" + map);
				result.put("state", "0");
				result.put("message", "投资成功");
				result.put("data", map);
			} else {
				LOG.info("投资失败" + map);
				result.put("state", "1");
				result.put("message", "投资失败");
				result.put("data", map);
			}
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		}
	}
	
	/**
	 * 发放抵用劵
	 * 
	 * @param userId
	 * @param voucherList
	 */
	public void addVouchers(String userId, List<Double> voucherList,String spans) {

		// 先查询是否有对应金额的抵用劵
		if (voucherList != null && voucherList.size() > 0) {
			for (Double voucher : voucherList) {
				AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
				AVouchersDic aVouchersDic = aVouchersDicDao.findByVoucher(voucher);
				if (aVouchersDic != null) {
					aUserAwardsHistory.setId(String.valueOf(IdGen.randomLong()));
					aUserAwardsHistory.setAwardId(aVouchersDic.getId());
					aUserAwardsHistory.setCreateDate(new Date());
					aUserAwardsHistory.setUserId(userId);
					aUserAwardsHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), aVouchersDic.getOverdueDays()));
					aUserAwardsHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
					aUserAwardsHistory.setType("1");// 类型为:抵用劵
					aUserAwardsHistory.setValue(voucher.toString());
					aUserAwardsHistory.setSpans(spans);
					aUserAwardsHistory.setRemark("2018MQJ");
					int i = aUserAwardsHistoryDao.insert(aUserAwardsHistory);
					if (i > 0) {
						LOG.info("{用户ID为}" + userId + "发放{" + voucher + "}元抵用劵成功");
					}
				} else {
					LOG.info("{尚未添加}" + voucher + "元面额的抵用劵");
				}
			}
		}
	}
	
	/**
	 * 用户投资Web
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 *            投资金额
	 * @param vouid
	 *            抵用劵ID
	 * @param vouAmount
	 *            抵用券金额
	 * @param projectId
	 *            标ID
	 * @param request
	 * @return
	 */
	@POST
	@Path("/saveUserInvestPwd")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> saveUserInvestPwd(@FormParam("lock") String lock, @FormParam("from") String from, @FormParam("token") String token, @FormParam("amount") String amount,@FormParam("vouchers")String vouchers, @FormParam("projectId") String projectId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount) || StringUtils.isBlank(lock)) {
				result.put("state", "2");
				throw new Exception("请更新至最新版本");
			}

			String ip = (String) request.getAttribute("ip");
			ip = ip.replace("_", ".");

			Double vAmountTotal = 0d;
			Double investAmount = Double.valueOf(amount);

			List<String> voucherList = new ArrayList<String>();
			if(vouchers!=null && !vouchers.equals("")){
				String[] voucherData = vouchers.split(",");
				voucherList = Arrays.asList(voucherData);
			}

			WloanTermProject project = wloanTermProjectService.get(projectId);
			String canUseCoupon = project.getIsCanUseCoupon();
			if(WloanTermProjectService.ISCANUSE_COUPON_NO.equals(canUseCoupon)){
				voucherList = null;
			}
			
			String span = project.getSpan().toString();
			
			if(voucherList!=null && voucherList.size()>0){
				for (int i = 0; i < voucherList.size(); i++) {
					String vouid = voucherList.get(i);
					AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
					if(aUserAwardsHistory!=null){
						//判断抵用券使用期限是否在使用范围内
						if(aUserAwardsHistory.getSpans()!=null && !aUserAwardsHistory.getSpans().equals(UserVouchersHistoryService.SPAN_1)){
							if(!aUserAwardsHistory.getSpans().contains(span)){
								throw new Exception("抵用券使用期限范围为"+aUserAwardsHistory.getSpans()+"天");
							}
						}
						
						AVouchersDic voucher = aVouchersDicService.get(aUserAwardsHistory.getAwardId());
						if(voucher!=null){
							vAmountTotal = vAmountTotal + voucher.getAmount();
						}
					}
				}
			}
			if(vAmountTotal > investAmount*0.01){
				throw new WinException("使用抵用券总额超过最大可用额度");
			}
			
			// 从缓存获取用户信息
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null == principal) { // 空判断，系统超时.
				LOG.info("fn:saveUserInvest，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", data);
				return result;
			}
			UserInfo user = principal.getUserInfo();
			String userId = user.getId();
			user = userInfoService.getCgb(userId);
			CgbUserAccount account = cgbUserAccountService.get(user.getAccountId());
			// 判断账户余额是否可进行出借
			Double userAvailableAmount = NumberUtils.scaleDouble(account.getAvailableAmount());
			if (userAvailableAmount < Double.valueOf(amount)) {
				throw new WinException("账户可用余额不足");
			}


			if (project == null) {
				throw new WinException("项目信息为空");
			} else {
				Date createDate = new Date();
				Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
				// 判断项目是否过期
				if (createDate.getTime() >= loan_date.getTime()) {
					throw new WinException("该项目已到期，不能进行投资");
				}
				/**
				 * 新手标的判断.
				 */
				String isNewType = project.getProjectType();
				if (isNewType.equals(WloanTermProjectService.PROJECT_TYPE_2)) { // 1：其它，2：新手标的，3：推荐标的.
					// 是新手标的，查看投资人是否是新手
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests != null && newUserInvests.size() > 0) {
						throw new WinException("该项目只能新手出借");
					}
				}

				// 固定某个标的新手只可投资一万

				if (project.getId() == "ef2e5190a4c34ac5aa12bc02c2b6ed99" || project.getId().equals("ef2e5190a4c34ac5aa12bc02c2b6ed99")) {
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests == null) {
						if (Double.valueOf(amount) > 10000) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
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
						if (!Double.valueOf(amount).equals(balanceAmount)) {
							throw new WinException("该项目目前只能投资" + balanceAmount + "元");
						}
					} else {
						// 投资金额大于融资金额.
						if (Double.valueOf(amount) > balanceAmount) {
							throw new WinException("您的投资金额大于可投资金额！");
						}
						// 最小投资、最大投资判断
						if (Double.valueOf(amount) < project.getMinAmount() || Double.valueOf(amount) > project.getMaxAmount()) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
			}

			//发放50抵用券
			//判断时间是否在活动期内
			if(DateUtils.compare_date(DateUtils.getDate(new Date(), "yyyy-MM-dd HH:mm:ss"), "2018-06-11 00:00:00")){
				//判断活动期内是否第一次领取
				AUserAwardsHistory aUserAwardsHistory = new AUserAwardsHistory();
				aUserAwardsHistory.setUserId(userId);
				aUserAwardsHistory.setRemark("2018MQJ");
				List<AUserAwardsHistory> aUserAwardsHistorieList = aUserAwardsHistoryDao.findList(aUserAwardsHistory);
				if(aUserAwardsHistorieList.size()==0){
					//判断是否属于活动项目
					String coreId = project.getReplaceRepayId();
					if("5685145015583919274".equals(coreId) || "8109132022784559441".equals(coreId)){
						List<Double> voucherList1 = new ArrayList<Double>();
						voucherList1.add(50d);
						addVouchers(user.getId(), voucherList1, "90,120,180,360");
					}
				}
			}
			
			Map<?, ?> map = new HashMap<String, Object>();
			if("1".equals(from)){
				map = newInvestService.newUserInvestPwdWeb(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, account, ip);

			}else {
				map = newInvestService.newUserInvestPwdH5(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, account, ip);

			}
			result.put("state", "0");
			result.put("data", map);
			result.put("message", "投资申请成功");
			
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		}
	}
	
	/**
	 * 用户投资Web
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 *            投资金额
	 * @param vouid
	 *            抵用劵ID
	 * @param vouAmount
	 *            抵用券金额
	 * @param projectId
	 *            标ID
	 * @param request
	 * @return
	 */
	@POST
	@Path("/userToInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> userToInvest(@FormParam("isLock") String isLock, @FormParam("from") String from, @FormParam("token") String token, @FormParam("amount") String amount,@FormParam("vouchers")String vouchers, @FormParam("projectId") String projectId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(isLock) ||StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
				result.put("state", "2");
				throw new Exception("请更新至最新版本");
			}

			String ip = (String) request.getAttribute("ip");
			ip = ip.replace("_", ".");

			Double vAmountTotal = 0d;
			Double investAmount = Double.valueOf(amount);

			List<String> voucherList = new ArrayList<String>();
			if(vouchers!=null && !vouchers.equals("")){
				String[] voucherData = vouchers.split(",");
				voucherList = Arrays.asList(voucherData);
			}

			WloanTermProject project = wloanTermProjectService.get(projectId);
			String canUseCoupon = project.getIsCanUseCoupon();
			if(WloanTermProjectService.ISCANUSE_COUPON_NO.equals(canUseCoupon)){
				voucherList = null;
			}
			
			String span = project.getSpan().toString();
			
			if(voucherList!=null && voucherList.size()>0){
				for (int i = 0; i < voucherList.size(); i++) {
					String vouid = voucherList.get(i);
					AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
					if(aUserAwardsHistory!=null){
						//判断抵用券使用期限是否在使用范围内
						if(aUserAwardsHistory.getSpans()!=null && !aUserAwardsHistory.getSpans().equals(UserVouchersHistoryService.SPAN_1)){
							if(!aUserAwardsHistory.getSpans().contains(span)){
								throw new Exception("抵用券使用期限范围为"+aUserAwardsHistory.getSpans()+"天");
							}
						}
						
						AVouchersDic voucher = aVouchersDicService.get(aUserAwardsHistory.getAwardId());
						if(voucher!=null){
							vAmountTotal = vAmountTotal + voucher.getAmount();
						}
					}
				}
			}
			if(vAmountTotal > investAmount*0.01){
				throw new WinException("使用抵用券总额超过最大可用额度");
			}
			
			// 从缓存获取用户信息

			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) { // 空判断，系统超时.
				LOG.info("fn:saveUserInvest，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", data);
				return result;
			}
			UserInfo user = userInfoService.getCgb(jedisUserId);
			String userId = user.getId();
			user = userInfoService.getCgb(userId);
			CgbUserAccount account = cgbUserAccountService.get(user.getAccountId());
			// 判断账户余额是否可进行出借
			Double userAvailableAmount = NumberUtils.scaleDouble(account.getAvailableAmount());
			if (userAvailableAmount < Double.valueOf(amount)) {
				throw new WinException("账户可用余额不足");
			}


			if (project == null) {
				throw new WinException("项目信息为空");
			} else {
				Date createDate = new Date();
				Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
				// 判断项目是否过期
				if (createDate.getTime() >= loan_date.getTime()) {
					throw new WinException("该项目已到期，不能进行投资");
				}
				/**
				 * 新手标的判断.
				 */
				String isNewType = project.getProjectType();
				if (isNewType.equals(WloanTermProjectService.PROJECT_TYPE_2)) { // 1：其它，2：新手标的，3：推荐标的.
					// 是新手标的，查看投资人是否是新手
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests != null && newUserInvests.size() > 0) {
						throw new WinException("该项目只能新手出借");
					}
				}

				// 固定某个标的新手只可投资一万

				if (project.getId() == "ef2e5190a4c34ac5aa12bc02c2b6ed99" || project.getId().equals("ef2e5190a4c34ac5aa12bc02c2b6ed99")) {
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests == null) {
						if (Double.valueOf(amount) > 10000) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
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
					Double balanceAmount = NumberUtils.scaleDouble(projectAmount - currentAmount);

					// 最后一次投资
					if (balanceAmount < project.getMinAmount()) {
						if (!Double.valueOf(amount).equals(balanceAmount)) {
							throw new WinException("该项目目前只能投资" + balanceAmount + "元");
						}
					} else {
						// 投资金额大于融资金额.
						if (Double.valueOf(amount) > balanceAmount) {
							throw new WinException("您的投资金额大于可投资金额！");
						}
						
						if(NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) < 100 && NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) > 0){
							throw new WinException("尾笔出借金额必须为"+balanceAmount+"元");
						}
						
						// 最小投资、最大投资判断
						if (Double.valueOf(amount) < project.getMinAmount() || Double.valueOf(amount) > project.getMaxAmount()) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
			}

			Map<?, ?> map = new HashMap<String, Object>();
			if("1".equals(from)){
				map = userInvestWebService.newUserInvestWeb(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, ip);

			}else {
				map = userInvestWebService.newUserInvestH5(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, ip);

			}
			result.put("state", "0");
			result.put("data", map);
			result.put("message", "出借申请成功");
			
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		}
	}
	
	
	/**
	 * 出借订单结果查询
	 * @param orderId
	 * @return
	 */
	@POST
	@Path("/seachInvestResult")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> seachInvestResult(@FormParam("orderId") String orderId) {
		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		//判断必要参数是否为空
		if(StringUtils.isBlank(orderId)){
			result.put("state", "1");
			result.put("message", "订单处理中");
			return result;
		}

	try {
			
			Map<String, Object> data= userInvestWebService.seachInvestResult(orderId);
			result.put("state", "0");
			result.put("message", "出借订单结果查询");
			result.put("data", data);
			return result;

	} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "2");
			result.put("message", "系统错误！");
			result.put("data", null);
			return result;
		}

	}
	
	
	/**
	 * 用户投资Web----新
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 *            投资金额
	 * @param vouid
	 *            抵用劵ID
	 * @param vouAmount
	 *            抵用券金额
	 * @param projectId
	 *            标ID
	 * @param request
	 * @return
	 */
	@POST
	@Path("/newUserToInvest")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> newUserToInvest(@FormParam("islock") String islock,@FormParam("from") String from, @FormParam("token") String token, @FormParam("amount") String amount,@FormParam("vouchers")String vouchers, @FormParam("projectId") String projectId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount) || StringUtils.isBlank(islock)) {
				result.put("state", "2");
				throw new Exception("请更新至最新版本");
			}

			String ip = (String) request.getAttribute("ip");
			ip = ip.replace("_", ".");

			Double vAmountTotal = 0d;
			Double investAmount = Double.valueOf(amount);

			List<String> voucherList = new ArrayList<String>();
			if(vouchers!=null && !vouchers.equals("")){
				String[] voucherData = vouchers.split(",");
				voucherList = Arrays.asList(voucherData);
			}

			WloanTermProject project = wloanTermProjectService.get(projectId);
			String canUseCoupon = project.getIsCanUseCoupon();
			if(WloanTermProjectService.ISCANUSE_COUPON_NO.equals(canUseCoupon)){
				voucherList = null;
			}
			
			String span = project.getSpan().toString();
			
			if(voucherList!=null && voucherList.size()>0){
				for (int i = 0; i < voucherList.size(); i++) {
					String vouid = voucherList.get(i);
					AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
					if(aUserAwardsHistory!=null){
						//判断抵用券使用期限是否在使用范围内
						if(aUserAwardsHistory.getSpans()!=null && !aUserAwardsHistory.getSpans().equals(UserVouchersHistoryService.SPAN_1)){
							if(!aUserAwardsHistory.getSpans().contains(span)){
								throw new Exception("抵用券使用期限范围为"+aUserAwardsHistory.getSpans()+"天");
							}
						}
						
						AVouchersDic voucher = aVouchersDicService.get(aUserAwardsHistory.getAwardId());
						if(voucher!=null){
							vAmountTotal = vAmountTotal + voucher.getAmount();
						}
					}
				}
			}
			if(vAmountTotal > investAmount*0.01){
				throw new WinException("使用抵用券总额超过最大可用额度");
			}
			
			// 从缓存获取用户信息

			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) { // 空判断，系统超时.
				LOG.info("fn:saveUserInvest，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", data);
				return result;
			}
			UserInfo user = userInfoService.getCgb(jedisUserId);
			String userId = user.getId();
			user = userInfoService.getCgb(userId);
			CgbUserAccount account = cgbUserAccountService.get(user.getAccountId());
			// 判断账户余额是否可进行出借
			Double userAvailableAmount = NumberUtils.scaleDouble(account.getAvailableAmount());
			if (userAvailableAmount < Double.valueOf(amount)) {
				throw new WinException("账户可用余额不足");
			}


			if (project == null) {
				throw new WinException("项目信息为空");
			} else {
				Date createDate = new Date();
				Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
				// 判断项目是否过期
				if (createDate.getTime() >= loan_date.getTime()) {
					throw new WinException("该项目已到期，不能进行投资");
				}
				/**
				 * 新手标的判断.
				 */
				String isNewType = project.getProjectType();
				if (isNewType.equals(WloanTermProjectService.PROJECT_TYPE_2)) { // 1：其它，2：新手标的，3：推荐标的.
					// 是新手标的，查看投资人是否是新手
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests != null && newUserInvests.size() > 0) {
						throw new WinException("该项目只能新手出借");
					}
				}

				// 固定某个标的新手只可投资一万

				if (project.getId() == "ef2e5190a4c34ac5aa12bc02c2b6ed99" || project.getId().equals("ef2e5190a4c34ac5aa12bc02c2b6ed99")) {
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests == null) {
						if (Double.valueOf(amount) > 10000) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
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
					Double balanceAmount = NumberUtils.scaleDouble(projectAmount - currentAmount);

					// 最后一次投资
					if (balanceAmount < project.getMinAmount()) {
						if (!Double.valueOf(amount).equals(balanceAmount)) {
							throw new WinException("该项目目前只能投资" + balanceAmount + "元");
						}
					} else {
						// 投资金额大于融资金额.
						if (Double.valueOf(amount) > balanceAmount) {
							throw new WinException("您的投资金额大于可投资金额！");
						}
						
						if(NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) < 100 && NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) > 0){
							throw new WinException("尾笔出借金额必须为"+balanceAmount+"元");
						}
						
						// 最小投资、最大投资判断
						if (Double.valueOf(amount) < project.getMinAmount() || Double.valueOf(amount) > project.getMaxAmount()) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
			}

			Map<?, ?> map = new HashMap<String, Object>();
			if("1".equals(from)){
				map = userInvestWebService.newUserToInvestWeb(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, ip);

			}else {
				map = userInvestWebService.newUserToInvestH5(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, ip);

			}
			result.put("state", "0");
			result.put("data", map);
			result.put("message", "出借申请成功");
			
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		}
	}
	
	/**
	 * 用户投资Web----2.2.1版本
	 * 
	 * @param from
	 * @param token
	 * @param amount
	 *            投资金额
	 * @param vouid
	 *            抵用劵ID
	 * @param vouAmount
	 *            抵用券金额
	 * @param projectId
	 *            标ID
	 * @param request
	 * @return
	 */
	@POST
	@Path("/userToInvest2_2_1")
	@Produces(MediaType.APPLICATION_JSON)
	public synchronized Map<String, Object> userToInvest2_2_1(@FormParam("from") String from, @FormParam("token") String token, @FormParam("amount") String amount,@FormParam("vouchers")String vouchers, @FormParam("projectId") String projectId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(projectId) || StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(amount)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}

			String ip = (String) request.getAttribute("ip");
			ip = ip.replace("_", ".");

			Double vAmountTotal = 0d;
			Double investAmount = Double.valueOf(amount);

			List<String> voucherList = new ArrayList<String>();
			if(vouchers!=null && !vouchers.equals("")){
				String[] voucherData = vouchers.split(",");
				voucherList = Arrays.asList(voucherData);
			}

			WloanTermProject project = wloanTermProjectService.get(projectId);
			String canUseCoupon = project.getIsCanUseCoupon();
			if(WloanTermProjectService.ISCANUSE_COUPON_NO.equals(canUseCoupon)){
				voucherList = null;
			}
			
			String span = project.getSpan().toString();
			
			if(voucherList!=null && voucherList.size()>0){
				for (int i = 0; i < voucherList.size(); i++) {
					String vouid = voucherList.get(i);
					AUserAwardsHistory aUserAwardsHistory = aUserAwardsHistoryService.get(vouid);
					if(aUserAwardsHistory!=null){
						//判断抵用券使用期限是否在使用范围内
						if(aUserAwardsHistory.getSpans()!=null && !aUserAwardsHistory.getSpans().equals(UserVouchersHistoryService.SPAN_1)){
							if(!aUserAwardsHistory.getSpans().contains(span)){
								throw new Exception("抵用券使用期限范围为"+aUserAwardsHistory.getSpans()+"天");
							}
						}
						
						AVouchersDic voucher = aVouchersDicService.get(aUserAwardsHistory.getAwardId());
						if(voucher!=null){
							vAmountTotal = vAmountTotal + voucher.getAmount();
						}
					}
				}
			}
			if(vAmountTotal > investAmount*0.01){
				throw new WinException("使用抵用券总额超过最大可用额度");
			}
			
			// 从缓存获取用户信息

			String jedisUserId = JedisUtils.get(token);
			if (StringUtils.isBlank(jedisUserId)) { // 空判断，系统超时.
				LOG.info("fn:saveUserInvest，系统超时.");
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", data);
				return result;
			}
			UserInfo user = userInfoService.getCgb(jedisUserId);
			String userId = user.getId();
			user = userInfoService.getCgb(userId);
			CgbUserAccount account = cgbUserAccountService.get(user.getAccountId());
			// 判断账户余额是否可进行出借
			Double userAvailableAmount = NumberUtils.scaleDouble(account.getAvailableAmount());
			if (userAvailableAmount < Double.valueOf(amount)) {
				throw new WinException("账户可用余额不足");
			}


			if (project == null) {
				throw new WinException("项目信息为空");
			} else {
				Date createDate = new Date();
				Date loan_date = DateUtils.getDateOfString(DateUtils.getDate(project.getLoanDate(), "yyyy-MM-dd"));
				// 判断项目是否过期
				if (createDate.getTime() >= loan_date.getTime()) {
					throw new WinException("该项目已到期，不能进行投资");
				}
				/**
				 * 新手标的判断.
				 */
				String isNewType = project.getProjectType();
				if (isNewType.equals(WloanTermProjectService.PROJECT_TYPE_2)) { // 1：其它，2：新手标的，3：推荐标的.
					// 是新手标的，查看投资人是否是新手
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests != null && newUserInvests.size() > 0) {
						throw new WinException("该项目只能新手出借");
					}
				}

				// 固定某个标的新手只可投资一万

				if (project.getId() == "ef2e5190a4c34ac5aa12bc02c2b6ed99" || project.getId().equals("ef2e5190a4c34ac5aa12bc02c2b6ed99")) {
					WloanTermInvest newUserInvest = new WloanTermInvest();
					newUserInvest.setUserInfo(user);
					List<WloanTermInvest> newUserInvests = wloanTermInvestService.findList(newUserInvest);
					if (newUserInvests == null) {
						if (Double.valueOf(amount) > 10000) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
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
					Double balanceAmount = NumberUtils.scaleDouble(projectAmount - currentAmount);

					// 最后一次投资
					if (balanceAmount < project.getMinAmount()) {
						if (!Double.valueOf(amount).equals(balanceAmount)) {
							throw new WinException("该项目目前只能投资" + balanceAmount + "元");
						}
					} else {
						// 投资金额大于融资金额.
						if (Double.valueOf(amount) > balanceAmount) {
							throw new WinException("您的投资金额大于可投资金额！");
						}
						
						if(NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) < 100 && NumberUtils.scaleDouble(balanceAmount - Double.valueOf(amount)) > 0){
							throw new WinException("尾笔出借金额必须为"+balanceAmount+"元");
						}
						
						// 最小投资、最大投资判断
						if (Double.valueOf(amount) < project.getMinAmount() || Double.valueOf(amount) > project.getMaxAmount()) {
							throw new WinException("您的投资金额不能小于起投金额且不能大于最大投资金额");
						}
					}
				}
			}

			Map<?, ?> map = new HashMap<String, Object>();
			if("1".equals(from)){
				map = userInvestWebService.newUserToInvestWeb2_2_1(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, ip);

			}else {
				map = userInvestWebService.newUserToInvestH52_2_1(token, projectId, Double.valueOf(amount), voucherList, vAmountTotal,user, ip);

			}
			result.put("state", "0");
			result.put("data", map);
			result.put("message", "出借申请成功");
			
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("data", null);
			result.put("state", "3");
			return result;
		}
	}

}
