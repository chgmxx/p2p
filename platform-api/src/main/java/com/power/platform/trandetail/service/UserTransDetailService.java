package com.power.platform.trandetail.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.cgb.dao.CgbCheckAccountDao;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbCheckAccount;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.trandetail.dao.UserTransDetailDao;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserCheckAccountDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserCheckAccount;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 客户流水记录Service
 * 
 * @author soler
 * @version 2015-12-23
 */
@Service("userTransDetailService")
@Transactional(readOnly = false)
public class UserTransDetailService extends CrudService<UserTransDetail> {

	/**
	 * 交易类型---充值
	 */
	public static final Integer trust_type0 = 0;

	/**
	 * 交易类型---提现
	 */
	public static final Integer trust_type1 = 1;
	/**
	 * 交易类型---活期投资
	 */
	public static final Integer trust_type2 = 2;
	/**
	 * 交易类型---定期投资
	 */
	public static final Integer trust_type3 = 3;
	/**
	 * 交易类型---还利息
	 */
	public static final Integer trust_type4 = 4;
	/**
	 * 交易类型---还本金
	 */
	public static final Integer trust_type5 = 5;
	/**
	 * 交易类型---活期赎回
	 */
	public static final Integer trust_type6 = 6;
	/**
	 * 交易类型---活动返现
	 */
	public static final Integer trust_type7 = 7;
	/**
	 * 交易类型---活期收益.
	 */
	public static final Integer TRUST_TYPE_8 = 8;
	/**
	 * 交易类型---佣金
	 */
	public static final Integer trust_type9 = 9;
	/**
	 * 交易类型---优惠券
	 */
	public static final Integer trust_type10 = 10;
	/**
	 * 交易类型，11：放款.
	 */
	public static final Integer TRUST_TYPE_11 = 11;
	/**
	 * 交易类型，12：受托支付提现.
	 */
	public static final Integer TRUST_TYPE_12 = 12;
	/**
	 * 收支交易----收入
	 */
	public static final Integer in_type = 1;

	/**
	 * 收支交易---支出
	 */
	public static final Integer out_type = 2;

	/**
	 * 交易状态---处理中
	 */
	public static final Integer tran_type1 = 1;
	/**
	 * 交易状态---成功
	 */
	public static final Integer tran_type2 = 2;
	/**
	 * 交易状态---失败
	 */
	public static final Integer tran_type3 = 3;

	private static final Logger logger = Logger.getLogger(UserTransDetailService.class);

	@Resource
	private UserTransDetailDao userTransDetailDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserCheckAccountDao userCheckAccountDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Resource
	private CgbUserAccountDao cgbUserAccountDao;
	@Resource
	private CgbCheckAccountDao cgbCheckAccountDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;

	@Override
	protected CrudDao<UserTransDetail> getEntityDao() {

		return userTransDetailDao;
	}

	/**
	 * 新增交易记录信息
	 * 
	 * @param userTransDetail
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insert(UserTransDetail userTransDetail) {

		logger.info("fn:insert,{交易ID：" + userTransDetail.getTransId() + "}");
		int flag = 0;
		try {

			flag = userTransDetailDao.insert(userTransDetail);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insert,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 更新交易记录状态
	 * 
	 * @param userTransDetail
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateState(UserTransDetail userTransDetail) {

		// TODO Auto-generated method stub
		System.out.println("[交易记录ID]" + userTransDetail.getId() + "[更新状态]" + userTransDetail.getState());
		int a = userTransDetailDao.updateState(userTransDetail);
		return a;
	}

	public Page<UserTransDetail> findPage(Page<UserTransDetail> page, UserTransDetail entity) {

		page.setOrderBy("a.trans_date DESC");
		entity.setPage(page);
		page.setList(userTransDetailDao.findList(entity));
		return page;
	}

	public Page<UserTransDetail> findPage1(Page<UserTransDetail> page, UserTransDetail entity) {

		page.setOrderBy("a.trans_date DESC");
		entity.setPage(page);
		page.setList(userTransDetailDao.findList1(entity));
		return page;
	}

	/**
	 * 借款用户交易流水
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<UserTransDetail> findPage2(Page<UserTransDetail> page, UserTransDetail entity) {

		page.setOrderBy("a.trans_date DESC");
		entity.setPage(page);
		page.setList(userTransDetailDao.findList2(entity));
		return page;
	}

	public UserTransDetail get(String id) {

		return userTransDetailDao.get(id);
	}

	/**
	 * 根据交易ID查询交易流水记录
	 * 
	 * @param tranId
	 * @return
	 */
	public UserTransDetail getByTransId(String tranId) {

		return userTransDetailDao.getByTransId(tranId);
	}

	/**
	 * 导出连连对账结果
	 * 
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void checkAccount() {

		// TODO Auto-generated method stub
		// N1.查询所有用户
		UserInfo user = new UserInfo();
		user.setBindBankCardState(UserInfo.BIND_CARD_YES);
		List<UserInfo> userList = userInfoDao.findList(user);
		if (userList != null && userList.size() > 0) {
			for (UserInfo userInfo : userList) {
				// 账户总资产
				Double userAccount = 0d;
				// 账户可用余额
				Double userAvailableAmount = 0d;
				// 对账资产
				Double checkAccount = 0d;
				// N2.根据用户ID查询账户资产
				UserAccountInfo userAccountInfo = userAccountInfoDao.getUserAccountInfo(userInfo.getId());
				if (userAccountInfo != null) {
					userAccount = userAccountInfo.getTotalAmount();
					userAvailableAmount = userAccountInfo.getAvailableAmount();
				}
				// N3.根据用户ID查询交易流水
				UserTransDetail userTransDetail = new UserTransDetail();
				userTransDetail.setUserInfo(userInfo);
				List<UserTransDetail> detaileList = userTransDetailDao.findList(userTransDetail);
				if (detaileList != null && detaileList.size() > 0) {
					for (UserTransDetail detail : detaileList) {
						// 1.收入 2.支出
						if (detail.getInOutType() == 1 && detail.getState() == UserTransDetail.TRANS_STATE_SUCCESS) {
							checkAccount = checkAccount + NumberUtils.scaleDouble(detail.getAmount());
						} else if (detail.getInOutType() == 2 && detail.getState() == UserTransDetail.TRANS_STATE_SUCCESS) {
							checkAccount = checkAccount - NumberUtils.scaleDouble(detail.getAmount());
						}
					}
				}
				
				if(NumberUtils.scaleDouble(userAvailableAmount) - NumberUtils.scaleDouble(checkAccount) != 0){
					logger.info("===============用户【" + userInfo.getRealName() + "】账户可用余额为【" + userAvailableAmount + "】对账可用余额为【" + checkAccount + "】");
					UserCheckAccount userCheckAccount = new UserCheckAccount();
					userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
					userCheckAccount.setPhone(userInfo.getName());
					userCheckAccount.setRealName(userInfo.getRealName());
					userCheckAccount.setAccountAmount(userAvailableAmount);
					userCheckAccount.setCheckAmount(checkAccount);
					userCheckAccount.setRemark("连连账户可用余额");
					userCheckAccount.setCreateDate(new Date());
					userCheckAccount.setUpdateDate(new Date());
					int i = userCheckAccountDao.insert(userCheckAccount);
					if (i > 0) {
						logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
					}
				}
				
				
				checkAccount = NumberUtils.scaleDouble(checkAccount) + NumberUtils.scaleDouble(userAccountInfo.getRegularDueInterest()) + NumberUtils.scaleDouble(userAccountInfo.getRegularDuePrincipal());
				// N5.将问题账户插入到客户对账问题表
				if (NumberUtils.scaleDouble(userAccount) - NumberUtils.scaleDouble(checkAccount) != 0) {
					logger.info("===============用户【" + userInfo.getRealName() + "】账户资产为【" + userAccount + "】对账资产为【" + checkAccount + "】");
					UserCheckAccount userCheckAccount = new UserCheckAccount();
					userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
					userCheckAccount.setPhone(userInfo.getName());
					userCheckAccount.setRealName(userInfo.getRealName());
					userCheckAccount.setAccountAmount(userAccount);
					userCheckAccount.setCheckAmount(checkAccount);
					userCheckAccount.setRemark("连连账户总额");
					userCheckAccount.setCreateDate(new Date());
					userCheckAccount.setUpdateDate(new Date());
					int i = userCheckAccountDao.insert(userCheckAccount);
					if (i > 0) {
						logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
					}
				}
				
				// N6.充值总额对账
				checkUserRecharge(userInfo, userAccountInfo);
				// N7.提现总额对账
				checkUserCash(userInfo, userAccountInfo);
				// N8.投资总额对账
				checkUserRegularTotalAmount(userInfo, userAccountInfo);
				// N9.投资总收益
				checkUserRegularTotalInerest(userInfo, userAccountInfo);
				// N10.待收本金对账
				checkUserRegularDueAmount(userInfo, userAccountInfo);
				// N11.待收收益对账
				checkUserRegularDueInterest(userInfo);
				// N12.已收本金
				checkUserRegularAmount(userInfo);
				// N13.已收收益
				checkUserRegularInterest(userInfo);
			}
		}
	}
	
	
	/**
	 * 充值总额对账
	 * @param userInfo
	 */
	public void checkUserRecharge(UserInfo userInfo, UserAccountInfo userAccountInfo){
		
		Double detailRecharge = 0d;
		Double accountRecharge = 0d;
		// N1.根据用户ID查询账户资产
		if(userAccountInfo!=null){
			accountRecharge = userAccountInfo.getRechargeAmount() == null ? 0d : userAccountInfo.getRechargeAmount();
		}
		// N2.根据用户ID查询交易流水
		UserTransDetail userTransDetail = new UserTransDetail();
		userTransDetail.setUserInfo(userInfo);
		userTransDetail.setTrustType(trust_type0);
		List<UserTransDetail> detaileList = userTransDetailDao.findList(userTransDetail);
		if (detaileList != null && detaileList.size() > 0) {
		    for (UserTransDetail userDetail : detaileList) {
		    	if(userDetail.getState() == UserTransDetail.TRANS_STATE_SUCCESS){
		    		detailRecharge = NumberUtils.scaleDouble(detailRecharge) + NumberUtils.scaleDouble(userDetail.getAmount());
		    	}
				
			}
		}
		if (NumberUtils.scaleDouble(detailRecharge) - NumberUtils.scaleDouble(accountRecharge) != 0) {
			logger.info("===============用户【" + userInfo.getRealName() + "】账户充值金额为【" + accountRecharge + "】流水充值金额为【" + detailRecharge + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(accountRecharge);
			userCheckAccount.setCheckAmount(detailRecharge);
			userCheckAccount.setRemark("连连账户充值金额");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
			}
		}
		
	}
	
	
	/**
	 * 提现总额对账
	 * @param userInfo
	 * @param userAccountInfo
	 */
	public void checkUserCash(UserInfo userInfo, UserAccountInfo userAccountInfo){
		
		Double detailCash = 0d;
		Double accountCash = 0d;
		// N1.根据用户ID查询账户资产
		if(userAccountInfo!=null){
			accountCash = userAccountInfo.getCashAmount() == null ? 0d : userAccountInfo.getCashAmount();
		}
		// N2.根据用户ID查询交易流水
		UserTransDetail userTransDetail = new UserTransDetail();
		userTransDetail.setUserInfo(userInfo);
		userTransDetail.setTrustType(trust_type1);
		List<UserTransDetail> detaileList = userTransDetailDao.findList(userTransDetail);
		if (detaileList != null && detaileList.size() > 0) {
		    for (UserTransDetail userDetail : detaileList) {
		    	if(userDetail.getState() == UserTransDetail.TRANS_STATE_SUCCESS){
		    		detailCash = NumberUtils.scaleDouble(detailCash) + NumberUtils.scaleDouble(userDetail.getAmount());
		    	}
				
			}
		}
		if (NumberUtils.scaleDouble(detailCash) - NumberUtils.scaleDouble(accountCash) != 0) {
			logger.info("===============用户【" + userInfo.getRealName() + "】账户充值金额为【" + accountCash + "】流水充值金额为【" + detailCash + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(accountCash);
			userCheckAccount.setCheckAmount(detailCash);
			userCheckAccount.setRemark("连连账户提现金额");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
			}
		}
		
	}
	
	/**
	 * 投资总额对账
	 * @param userInfo
	 * @param userAccountInfo
	 */
	public void checkUserRegularTotalAmount(UserInfo userInfo, UserAccountInfo userAccountInfo){
		
		Double detailRegularTotalAmount = 0d;
		Double accountRegularTotalAmount = 0d;
		Double investTotalAmount = 0d;
		// N1.根据用户ID查询账户资产
		if(userAccountInfo!=null){
			accountRegularTotalAmount = userAccountInfo.getRegularTotalAmount() == null ? 0d : userAccountInfo.getRegularTotalAmount();
		}
		// N2.根据用户ID查询交易流水
		UserTransDetail userTransDetail = new UserTransDetail();
		userTransDetail.setUserInfo(userInfo);
		userTransDetail.setTrustType(trust_type3);
		List<UserTransDetail> detaileList = userTransDetailDao.findList(userTransDetail);
		if (detaileList != null && detaileList.size() > 0) {
		    for (UserTransDetail userDetail : detaileList) {
		    	if(userDetail.getState() == UserTransDetail.TRANS_STATE_SUCCESS){
		    		detailRegularTotalAmount = NumberUtils.scaleDouble(detailRegularTotalAmount) + NumberUtils.scaleDouble(userDetail.getAmount());
		    	}
				
			}
		}
		// N3.根据用户ID查询出借记录表
		WloanTermInvest invest = new WloanTermInvest();
		invest.setUserInfo(userInfo);
		List<String> stateList = new ArrayList<String>();
		stateList.add("1");
		stateList.add("3");
		stateList.add("9");
		invest.setStateItem(stateList);
		invest.setEndBeginDate(DateUtils.getDateOfString("2017-12-20 23:59:59"));
		List<WloanTermInvest> investList = wloanTermInvestDao.findList(invest);
		if(investList!=null && investList.size()>0){
			for (WloanTermInvest wloanTermInvest : investList) {
				investTotalAmount = NumberUtils.scaleDouble(investTotalAmount) + wloanTermInvest.getAmount();
			}
		}
		
		// N4.流水出借总额与账户出借总额比较
		if (NumberUtils.scaleDouble(detailRegularTotalAmount) - NumberUtils.scaleDouble(accountRegularTotalAmount) != 0) {
			logger.info("===============用户【" + userInfo.getRealName() + "】账户出借总额为【" + accountRegularTotalAmount + "】流水出借总额为【" + detailRegularTotalAmount + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(accountRegularTotalAmount);
			userCheckAccount.setCheckAmount(detailRegularTotalAmount);
			userCheckAccount.setRemark("连连账户出借总额与流水出借总额");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
			}
		}
		
		// N5.出借记录总额与账户出借总额比较
		if (NumberUtils.scaleDouble(investTotalAmount) - NumberUtils.scaleDouble(accountRegularTotalAmount) != 0) {
			logger.info("===============用户【" + userInfo.getRealName() + "】账户出借总额为【" + accountRegularTotalAmount + "】出借记录总额为【" + investTotalAmount + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(accountRegularTotalAmount);
			userCheckAccount.setCheckAmount(investTotalAmount);
			userCheckAccount.setRemark("连连账户出借总额与出借记录总额");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
			}
		}
		
	}
	
    
	/**
	 * 投资总收益
	 */
	public void checkUserRegularTotalInerest(UserInfo userInfo, UserAccountInfo userAccountInfo){
		
		Double accountRegularTotalInerest = 0d;
		Double investTotalInerest = 0d;
		//N1.根据用户ID查询账户资产
		if(userAccountInfo!=null){
			accountRegularTotalInerest = userAccountInfo.getRegularTotalInterest() == null ? 0d : userAccountInfo.getRegularTotalInterest();
		}
		// N2.根据用户ID查询出借记录表
				WloanTermInvest invest = new WloanTermInvest();
				invest.setUserInfo(userInfo);
				List<String> stateList = new ArrayList<String>();
				stateList.add("1");
				stateList.add("3");
				stateList.add("9");
				invest.setStateItem(stateList);
				invest.setEndBeginDate(DateUtils.getDateOfString("2017-12-20 23:59:59"));
				List<WloanTermInvest> investList = wloanTermInvestDao.findList(invest);
				if(investList!=null && investList.size()>0){
					for (WloanTermInvest wloanTermInvest : investList) {
						investTotalInerest = NumberUtils.scaleDouble(investTotalInerest) + wloanTermInvest.getInterest();
					}
				}
		// N3.已还利息(还款计划)VS账户定期已收收益
		if (NumberUtils.scaleDouble(investTotalInerest) - NumberUtils.scaleDouble(accountRegularTotalInerest) != 0) {
			logger.info("===============用户【" + userInfo.getRealName() + "】账户定期已收收益总额为【" + accountRegularTotalInerest + "】已还利息总额为【" + investTotalInerest + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(accountRegularTotalInerest);
			userCheckAccount.setCheckAmount(investTotalInerest);
			userCheckAccount.setRemark("总收益");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
			}
		}
	}
	
	
	/**
	 * 定期待收本金
	 */
	public void checkUserRegularDueAmount(UserInfo userInfo, UserAccountInfo userAccountInfo){
		
		Double accountRegularDueAmount = 0d;
		Double checkRegularDueAmount = 0d;
		//N1.根据用户ID查询账户资产
		if(userAccountInfo!=null){
			accountRegularDueAmount = userAccountInfo.getRegularDuePrincipal() == null ? 0d : userAccountInfo.getRegularDuePrincipal();
		}
		 //N2.根据项目查询用户还款计划
		WloanTermUserPlan userPlan = new WloanTermUserPlan();
		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
		userPlan.setUserInfo(userInfo);
		WloanTermProject oldProject = new WloanTermProject();
		oldProject.setEndTimeToOnline("2017-12-23 00:00:00");
		userPlan.setWloanTermProject(oldProject);
		List<WloanTermUserPlan> planList = wloanTermUserPlanDao.findUserPlan(userPlan);
		if(planList!=null && planList.size()>0){
			for (WloanTermUserPlan userRepay : planList) {
				// 还本金
				if(userRepay.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1)){
					// 根据还款计划投资ID查询用户投资金额(投资本金)
					double principalAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(userRepay.getProjectId(), userRepay.getUserId(), userRepay.getWloanTermInvestId());
					checkRegularDueAmount = NumberUtils.scaleDouble(checkRegularDueAmount) + NumberUtils.scaleDouble(principalAmount);
				}
			}
		}
		// N3.账户定期待收本金VS定期待收本金
		if (NumberUtils.scaleDouble(checkRegularDueAmount) - NumberUtils.scaleDouble(accountRegularDueAmount) != 0) {
			logger.info("===============用户【" + userInfo.getRealName() + "】账户定期待收总额为【" + accountRegularDueAmount + "】定期待收本金为【" + checkRegularDueAmount + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(accountRegularDueAmount);
			userCheckAccount.setCheckAmount(checkRegularDueAmount);
			userCheckAccount.setRemark("连连账户定期待收本金");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("################已筛出问题连连用户【" + userInfo.getRealName() + "】");
			}
		}
	}
	
	
	/**
	 * 连连账户定期待收收益核对
	 * 
	 * @param userId
	 */
	public void checkUserRegularDueInterest(UserInfo userInfo) {

		// 账户显示定期待收收益
		Double regularDueInterest = 0d;
		Double checkUserRepay = 0d;
		// N1.根据用户ID查询账户资产
		UserAccountInfo userAccountInfo = userAccountInfoDao.getUserAccountInfo(userInfo.getId());
		if (userAccountInfo != null) {
			regularDueInterest = userAccountInfo.getRegularDueInterest() == null ? 0d : userAccountInfo.getRegularDueInterest();
		}
		 //N3.根据项目查询用户还款计划
		WloanTermUserPlan userPlan = new WloanTermUserPlan();
		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
		userPlan.setUserInfo(userInfo);
		WloanTermProject oldProject = new WloanTermProject();
		oldProject.setEndTimeToOnline("2017-12-23 00:00:00");
		userPlan.setWloanTermProject(oldProject);
		List<WloanTermUserPlan> planList = wloanTermUserPlanDao.findUserPlan(userPlan);
		for (WloanTermUserPlan userRepay : planList) {
			// 还利息
			if (userRepay.getPrincipal().equals("2")) {
				checkUserRepay = checkUserRepay + NumberUtils.scaleDouble(userRepay.getInterest());
			}
			// 还本息
			else {
				// 根据还款计划投资ID查询用户投资金额(投资本金)
				double principalAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(userRepay.getProjectId(), userRepay.getUserId(), userRepay.getWloanTermInvestId());
				// 利息 = 还本息 - 投资本金
				double backInterestAmount = NumberUtils.scaleDouble(userRepay.getInterest()) - principalAmount;
				checkUserRepay = NumberUtils.scaleDouble(checkUserRepay) + NumberUtils.scaleDouble(backInterestAmount);

			}

		}
		// N5.将问题账户插入到客户对账问题表
		if (NumberUtils.scaleDouble(regularDueInterest) - NumberUtils.scaleDouble(checkUserRepay) != 0) {
			logger.info("===============连连用户【" + userInfo.getRealName() + "】账户显示定期待收收益为【" + regularDueInterest + "】对账收益为【" + checkUserRepay + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(regularDueInterest);
			userCheckAccount.setCheckAmount(checkUserRepay);
			userCheckAccount.setRemark("定期待收收益");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("##############已筛出问题用户【" + userInfo.getRealName() + "】");
			}
		}

	}
	
	/**
	 * 已还本金
	 * @param userInfo
	 */
	public void checkUserRegularAmount(UserInfo userInfo){
		Double detailRegularAmount = 0d;
		Double investRegularAmoount = 0d;
		//N1.流水已还本金
		UserTransDetail userTransDetail = new UserTransDetail();
		userTransDetail.setUserInfo(userInfo);
		userTransDetail.setTrustType(trust_type5);
		List<UserTransDetail> detaileList = userTransDetailDao.findList(userTransDetail);
		if (detaileList != null && detaileList.size() > 0) {
		    for (UserTransDetail userDetail : detaileList) {
		    	if(userDetail.getState() == UserTransDetail.TRANS_STATE_SUCCESS){
		    		//根据transId查询还款计划
		    		if(userDetail.getTransId()!=null){
		    			WloanTermUserPlan userPlan = wloanTermUserPlanDao.get(userDetail.getTransId());
		    			if(userPlan!=null){
		    				detailRegularAmount = NumberUtils.scaleDouble(detailRegularAmount) + NumberUtils.scaleDouble(userPlan.getWloanTermInvest().getAmount());
		    			}
		    			
		    		}
		    	}
			}
		}
		//查询出借记录已经结束的项目
		WloanTermProject wloanTermProject = new WloanTermProject();
		WloanTermInvest loanTermInvest = new WloanTermInvest();
		List<String> stateItem = new ArrayList<String>();
		stateItem.add("7");// 已结束.
		wloanTermProject.setStateItem(stateItem);
		loanTermInvest.setWloanTermProject(wloanTermProject);
		loanTermInvest.setUserInfo(userInfo);
		loanTermInvest.setEndBeginDate(DateUtils.getDateOfString("2017-12-20 23:59:59"));
		List<WloanTermInvest> list = wloanTermInvestDao.findList(loanTermInvest);
		if(list!=null && list.size()>0){
			for (WloanTermInvest wloanTermInvest : list) {
				investRegularAmoount = NumberUtils.scaleDouble(investRegularAmoount) + NumberUtils.scaleDouble(wloanTermInvest.getAmount());
			}
		}
		if (NumberUtils.scaleDouble(investRegularAmoount) - NumberUtils.scaleDouble(detailRegularAmount) != 0) {
			logger.info("===============连连用户【" + userInfo.getRealName() + "】出借记录【" + investRegularAmoount + "】交易流水为【" + detailRegularAmount + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(investRegularAmoount);
			userCheckAccount.setCheckAmount(detailRegularAmount);
			userCheckAccount.setRemark("已还本金");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("##############已筛出问题用户【" + userInfo.getRealName() + "】");
			}
		}
		
	}
	
	
	/**
	 * 已还利息
	 * @param userInfo
	 */
	public void checkUserRegularInterest(UserInfo userInfo){
		Double detailRegularInterest = 0d;
		Double investRegularInterest = 0d;
		//N1.流水已还利息
		UserTransDetail userTransDetail = new UserTransDetail();
		userTransDetail.setUserInfo(userInfo);
		userTransDetail.setTrustType(trust_type4);
		List<UserTransDetail> detaileList = userTransDetailDao.findList(userTransDetail);
		if (detaileList != null && detaileList.size() > 0) {
		    for (UserTransDetail userDetail : detaileList) {
		    	if(userDetail.getState() == UserTransDetail.TRANS_STATE_SUCCESS){
		    		detailRegularInterest = NumberUtils.scaleDouble(detailRegularInterest) + NumberUtils.scaleDouble(userDetail.getAmount());
		    	}
			}
		}
		//查询出借记录已经结束的项目
		WloanTermProject wloanTermProject = new WloanTermProject();
		WloanTermInvest loanTermInvest = new WloanTermInvest();
		List<String> stateItem = new ArrayList<String>();
		stateItem.add("7");// 已结束.
		wloanTermProject.setStateItem(stateItem);
		loanTermInvest.setWloanTermProject(wloanTermProject);
		loanTermInvest.setUserInfo(userInfo);
		loanTermInvest.setEndBeginDate(DateUtils.getDateOfString("2017-12-20 23:59:59"));
		List<WloanTermInvest> list = wloanTermInvestDao.findList(loanTermInvest);
		if(list!=null && list.size()>0){
			for (WloanTermInvest wloanTermInvest : list) {
				investRegularInterest = NumberUtils.scaleDouble(investRegularInterest) + NumberUtils.scaleDouble(wloanTermInvest.getInterest());
			}
		}
		if (NumberUtils.scaleDouble(investRegularInterest) - NumberUtils.scaleDouble(detailRegularInterest) != 0) {
			logger.info("===============连连用户【" + userInfo.getRealName() + "】出借记录【" + investRegularInterest + "】交易流水为【" + detailRegularInterest + "】");
			UserCheckAccount userCheckAccount = new UserCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(investRegularInterest);
			userCheckAccount.setCheckAmount(detailRegularInterest);
			userCheckAccount.setRemark("已还利息");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = userCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("##############已筛出问题用户【" + userInfo.getRealName() + "】");
			}
		}
		
	}

	/**
	 * 数据统计
	 * @param userId
	 * @param i
	 * @return
	 */
	public double findCountAmount(String userId, int trustType) {
		// TODO Auto-generated method stub
		return userTransDetailDao.findCountAmount(userId,trustType);
	}
	
	
	/**
	 * 导出存管宝对账结果
	 * 
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void checkCGBAccount() {

		// TODO Auto-generated method stub
		// N1.查询所有用户
		UserInfo user = new UserInfo();
		user.setCgbBindBankCardState(UserInfo.BIND_CARD_YES);
		List<UserInfo> userList = userInfoDao.findList1(user);
		if (userList != null && userList.size() > 0) {
			for (UserInfo userInfo : userList) {
				// 账户总资产
				Double userAccount = 0d;
				// 对账资产
				Double checkAccount = 0d;
				// N2.根据用户ID查询账户资产
				CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
				if (userAccountInfo != null) {
					userAccount = userAccountInfo.getTotalAmount();
				}
				// N3.根据用户ID查询交易流水
				CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
				userTransDetail.setUserInfo(userInfo);
				List<CgbUserTransDetail> detaileList = cgbUserTransDetailDao.findList(userTransDetail);
				if (detaileList != null && detaileList.size() > 0) {
					for (CgbUserTransDetail detail : detaileList) {
						// 1.收入 2.支出
						if (detail.getInOutType() == 1 && detail.getState() == UserTransDetail.TRANS_STATE_SUCCESS) {
							checkAccount = checkAccount + NumberUtils.scaleDouble(detail.getAmount());
						} else if (detail.getInOutType() == 2 && detail.getState() == UserTransDetail.TRANS_STATE_SUCCESS) {
							checkAccount = checkAccount - NumberUtils.scaleDouble(detail.getAmount());
						}
					}
				}
				checkAccount = NumberUtils.scaleDouble(checkAccount) + NumberUtils.scaleDouble(userAccountInfo.getRegularDueInterest()) + NumberUtils.scaleDouble(userAccountInfo.getRegularDuePrincipal());
				// N5.将问题账户插入到客户对账问题表
				if (NumberUtils.scaleDouble(userAccount) - NumberUtils.scaleDouble(checkAccount) !=0 ) {
					logger.info("===============存管宝用户【" + userInfo.getRealName() + "】账户资产为【" + userAccount + "】对账资产为【" + checkAccount + "】");
					CgbCheckAccount userCheckAccount = new CgbCheckAccount();
					userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
					userCheckAccount.setPhone(userInfo.getName());
					userCheckAccount.setRealName(userInfo.getRealName());
					userCheckAccount.setAccountAmount(userAccount);
					userCheckAccount.setCheckAmount(checkAccount);
					userCheckAccount.setRemark("存管宝账户总额");
					userCheckAccount.setCreateDate(new Date());
					userCheckAccount.setUpdateDate(new Date());
					int i = cgbCheckAccountDao.insert(userCheckAccount);
					if (i > 0) {
						logger.info("*****************已筛出问题存管宝用户【" + userInfo.getRealName() + "】");
					}
				}
				// N6.新增账户待收收益对账
				checkCGBUserRepay(userInfo);
			}
		}
	}
	
	
	/**
	 * 存管宝账户定期待收收益核对
	 * 
	 * @param userId
	 */
	public void checkCGBUserRepay(UserInfo userInfo) {

		// 账户显示定期待收收益
		Double regularDueInterest = 0d;
		Double checkUserRepay = 0d;
		// N1.根据用户ID查询账户资产
		CgbUserAccount userAccountInfo = cgbUserAccountDao.getUserAccountInfo(userInfo.getId());
		if (userAccountInfo != null) {
			regularDueInterest = userAccountInfo.getRegularDueInterest() == null ? 0d : userAccountInfo.getRegularDueInterest();
		}
		// N3.查询存管宝上线前项目
		WloanTermUserPlan userPlan = new WloanTermUserPlan();
		userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
		userPlan.setUserInfo(userInfo);
		WloanTermProject newProject = new WloanTermProject();
		newProject.setBeginTimeFromOnline("2017-12-23 00:00:00");
		userPlan.setWloanTermProject(newProject);
		List<WloanTermUserPlan> planList = wloanTermUserPlanDao.findUserPlan(userPlan);
		for (WloanTermUserPlan userRepay : planList) {
			// 还利息
			if (userRepay.getPrincipal().equals("2")) {
				checkUserRepay = checkUserRepay + NumberUtils.scaleDouble(userRepay.getInterest());
			}
			// 还本息
			else {
				// 根据还款计划投资ID查询用户投资金额(投资本金)
				double principalAmount = wloanTermInvestDao.findInvestAmountByProjectAndUser(userRepay.getProjectId(), userRepay.getUserId(), userRepay.getWloanTermInvestId());
				// 利息 = 还本息 - 投资本金
				double backInterestAmount = NumberUtils.scaleDouble(userRepay.getInterest()) - principalAmount;
				checkUserRepay = NumberUtils.scaleDouble(checkUserRepay) + NumberUtils.scaleDouble(backInterestAmount);

			}

		}
		// N5.将问题账户插入到客户对账问题表
		if (NumberUtils.scaleDouble(regularDueInterest) - NumberUtils.scaleDouble(checkUserRepay) != 0) {
			logger.info("===============存管宝用户【" + userInfo.getRealName() + "】账户显示定期待收收益为【" + regularDueInterest + "】对账收益为【" + checkUserRepay + "】");
			CgbCheckAccount userCheckAccount = new CgbCheckAccount();
			userCheckAccount.setId(String.valueOf(IdGen.randomLong()));
			userCheckAccount.setPhone(userInfo.getName());
			userCheckAccount.setRealName(userInfo.getRealName());
			userCheckAccount.setAccountAmount(regularDueInterest);
			userCheckAccount.setCheckAmount(checkUserRepay);
			userCheckAccount.setRemark("存管宝定期待收收益");
			userCheckAccount.setCreateDate(new Date());
			userCheckAccount.setUpdateDate(new Date());
			int i = cgbCheckAccountDao.insert(userCheckAccount);
			if (i > 0) {
				logger.info("******************已筛出问题存管宝用户【" + userInfo.getRealName() + "】");
			}
		}

	}
}