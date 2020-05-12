package com.power.platform.activity.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.BrokerageDao;
import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.dao.WeixinShareDetailsDao;
import com.power.platform.activity.entity.Brokerage;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.utils.WinPageResult;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserAccountInfoService;

/**
 * 
 * 类: BrokerageService <br>
 * 描述: 三级分销邀请Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月25日 下午6:06:58
 */
@Service("brokerageService")
public class BrokerageService extends CrudService<Brokerage> {

	@Autowired
	private BrokerageDao brokerageDao;

	@Autowired
	private LevelDistributionDao levelDistributionDao;

	@Autowired
	private WeixinShareDetailsDao weixinShareDetailsDao;

	@Autowired
	private WloanTermProjectService projectService;

	@Autowired
	private UserAccountInfoService accountInfoService;

	@Autowired
	private UserTransDetailService userTransDetailService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private CgbUserAccountService cgbUserAccountService;

	/**
	 * 
	 * 方法: findBrokeragePage <br>
	 * 描述: 佣金列表接口. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年8月25日 下午12:17:49
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<Brokerage> findBrokeragePage(Page<Brokerage> page, Brokerage entity) {

		entity.setPage(page);
		page.setList(brokerageDao.findBrokerageList(entity));
		return page;
	}

	protected CrudDao<Brokerage> getEntityDao() {

		return brokerageDao;
	}

	public List<Brokerage> findAll() {

		return null;
	}

	public WinPageResult<Map<String, Object>> queryWBrokerageMap(String userId, Page<Brokerage> page, Date startDate, Date endDate, String type) {

		WinPageResult<Map<String, Object>> result = new WinPageResult<Map<String, Object>>();

		List<Map<String, Object>> amountList = null;
		int total = 0;
		if (type.equals("1")) {
			amountList = levelDistributionDao.notQueryUserWbidAmount(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
			total = levelDistributionDao.notCountByExample(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
		} else {
			amountList = levelDistributionDao.queryUserWbidAmount(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
			total = levelDistributionDao.countByExample(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
		}
		result.setRows(amountList);
		result.setTotal(total);
		result.setPageNumber(page.getPageNo());
		result.setPageSize(page.getPageSize());
		result.setSuccess(true);
		return result;
	}

	public Page<Map<String, Object>> findMapPage(Page<Map<String, Object>> page, String userId, String type) {

		List<Map<String, Object>> amountList = null;
		int total = 0;
		if (type.equals("1")) {
			amountList = levelDistributionDao.notQueryUserWbidAmount(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
			total = levelDistributionDao.notCountByExample(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
		} else {
			amountList = levelDistributionDao.queryUserWbidAmount(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
			total = levelDistributionDao.countByExample(userId, page.getPageNo() * page.getPageSize(), page.getPageSize());
		}
		page.setList(amountList);
		page.setCount(total);
		return page;
	}

	/**
	 * 
	 * 方法: queryBrokerageMap <br>
	 * 描述: 查询客户微信公众号推广奖励及三级佣金奖励. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年4月25日 下午3:44:32
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, Object> queryBrokerageMap(UserInfo user) {

		/**
		 * 客户参与三级分销，邀请好友投资佣金总额.
		 */
		double brokerage = brokerageDao.brokerageTotalAmount(user.getId());
		/**
		 * 客户参与三级分销，邀请好友投资总额.
		 */
		double bidTotalAmount = levelDistributionDao.queryUserWbidSumAmount(user.getId());
		/**
		 * 客户微信服务号推广粉丝数.
		 */
		int countUsers = weixinShareDetailsDao.queryUsers(user.getId());
		/**
		 * 客户微信服务号推广分享奖励.
		 */
		double publicAwards = weixinShareDetailsDao.queryAmount(user.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", user.getId());
		map.put("brokerage", NumberUtils.scaleDouble(brokerage));
		map.put("bidTotalAmount", NumberUtils.scaleDouble(bidTotalAmount));
		map.put("countUsers", countUsers);
		map.put("publicAwards", NumberUtils.scaleDouble(publicAwards));

		return map;
	}

	public void insertWBrokerageMap(double wbAmount, String userId, String projectId, String wloanTermInvestId) {

		Brokerage brokerage = new Brokerage();
		Brokerage brokerage2 = new Brokerage();
		LevelDistribution distribution = levelDistributionDao.selectByUserId(userId);
		WloanTermProject project = projectService.get(projectId);
		double day = project.getSpan();
		double sumDay = 365;
		double brokerageAmount = (day / sumDay) * wbAmount * (0.5 / 100);

		if (distribution != null && distribution.getParentId() != null && !distribution.getParentId().equals("")) {
			brokerage.setId(IdGen.uuid()); // 主键ID.
			brokerage.setAmount(brokerageAmount);
			brokerage.setCreateDate(new Date());
			brokerage.setFromUserId(userId);
			brokerage.setUserId(distribution.getParentId());
			brokerageDao.insert(brokerage);
			updateUserAccount(distribution.getParentId(), wloanTermInvestId, brokerageAmount);
		}

		if (distribution != null && distribution.getGrandpaId() != null && !distribution.getParentId().equals("")) {
			brokerage2.setId(IdGen.uuid()); // 主键ID.
			brokerage2.setAmount(brokerageAmount);
			brokerage2.setCreateDate(new Date());
			brokerage2.setFromUserId(userId);
			brokerage2.setUserId(distribution.getGrandpaId());
			brokerageDao.insert(brokerage2);
			updateUserAccount(distribution.getGrandpaId(), wloanTermInvestId, brokerageAmount);
		}
	}

	private void updateUserAccount(String userId, String wloanTermInvestId, double brokerageAmount) {

		CgbUserAccount account = cgbUserAccountService.getUserAccountInfo(userId);
		account.setTotalAmount(account.getTotalAmount() + brokerageAmount);
		account.setAvailableAmount(account.getAvailableAmount() + brokerageAmount);
		cgbUserAccountService.updateUserAccountInfo(account);

		/**
		 * 保存客户流水记录.
		 */
		CgbUserTransDetail userTransDetail = new CgbUserTransDetail();
		userTransDetail.preInsert();
		userTransDetail.setTransId(wloanTermInvestId); // 佣金来源客户投资记录ID.
		userTransDetail.setUserId(userId); // 客户账号ID.
		userTransDetail.setAccountId(account.getId()); // 客户账户ID.
		userTransDetail.setTransDate(new Date());// 投资交易时间.
		userTransDetail.setTrustType(UserTransDetailService.trust_type9); // 佣金.
		userTransDetail.setAmount(brokerageAmount); // 投资交易金额.
		userTransDetail.setAvaliableAmount(account.getAvailableAmount()); // 当前可用余额.
		userTransDetail.setInOutType(UserTransDetailService.in_type);
		userTransDetail.setRemarks("佣金"); // 备注信息.
		userTransDetail.setState(UserTransDetailService.tran_type2); // 流水状态，成功.
		cgbUserTransDetailService.insert(userTransDetail);
	}

}