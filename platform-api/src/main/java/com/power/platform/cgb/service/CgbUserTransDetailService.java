/**
 * 银行托管-流水-Service.
 */
package com.power.platform.cgb.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;

/**
 * 银行托管-流水-Service.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Service("cgbUserTransDetailService")
@Transactional(readOnly = false)
public class CgbUserTransDetailService extends CrudService<CgbUserTransDetail> {

	/**
	 * 0：充值.
	 */
	public static final Integer TRUST_TYPE_0 = 0;
	/**
	 * 1：提现.
	 */
	public static final Integer TRUST_TYPE_1 = 1;
	/**
	 * 2：活期投资！.
	 */
	public static final Integer TRUST_TYPE_2 = 2;
	/**
	 * 3：定期投资.
	 */
	public static final Integer TRUST_TYPE_3 = 3;
	/**
	 * 4：付息o.
	 */
	public static final Integer TRUST_TYPE_4 = 4;
	/**
	 * 5：还本.
	 */
	public static final Integer TRUST_TYPE_5 = 5;
	/**
	 * 6：活期赎回！.
	 */
	public static final Integer TRUST_TYPE_6 = 6;
	/**
	 * 7：活动返现！.
	 */
	public static final Integer TRUST_TYPE_7 = 7;
	/**
	 * 8：活期收益!.
	 */
	public static final Integer TRUST_TYPE_8 = 8;
	/**
	 * 9：佣金!.
	 */
	public static final Integer TRUST_TYPE_9 = 9;
	/**
	 * 10：抵用券.
	 */
	public static final Integer TRUST_TYPE_10 = 10;
	/**
	 * 11：放款.
	 */
	public static final Integer TRUST_TYPE_11 = 11;
	/**
	 * 12：受托支付提现!.
	 */
	public static final Integer TRUST_TYPE_12 = 12;
	/**
	 * 13：间接代偿（代偿还款!）.
	 */
	public static final Integer TRUST_TYPE_13 = 13;

	/**
	 * 1：收入.
	 */
	public static final Integer IN_TYPE_1 = 1;
	/**
	 * 2：支出.
	 */
	public static final Integer OUT_TYPE_2 = 2;
	/**
	 * 3：冻结
	 */
	public static final Integer FREEZE_TYPE_3 = 3;

	/**
	 * 1：处理中.
	 */
	public static final Integer TRUST_STATE_1 = 1;
	/**
	 * 2：成功.
	 */
	public static final Integer TRUST_STATE_2 = 2;
	/**
	 * 3：失败.
	 */
	public static final Integer TRUST_STATE_3 = 3;

	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;

	@Override
	protected CrudDao<CgbUserTransDetail> getEntityDao() {

		return cgbUserTransDetailDao;
	}

	/**
	 * 
	 * methods: findTransactCreUserGrantInfoPageZ <br>
	 * description: 增量-借款人（放款）交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月3日 下午5:59:55
	 * 
	 * @param page
	 * @param cutd
	 * @return
	 */
	public Page<CgbUserTransDetail> findTransactCreUserGrantInfoPageZ(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findTransactCreUserGrantListZ(cutd));
		return page;
	}

	/**
	 * 
	 * methods: findTransactCreUserGrantInfoPage <br>
	 * description: 围绕散标-2019-03-01 00:00:00 before存量借款用户（放款）流水. <br>
	 * author: Roy <br>
	 * date: 2019年5月23日 下午4:21:40
	 * 
	 * @param page
	 * @param cutd
	 * @return
	 */
	public Page<CgbUserTransDetail> findTransactCreUserGrantInfoPage(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findTransactCreUserGrantList(cutd));
		return page;
	}

	/**
	 * 
	 * methods: findTransactCreUserRechargeWithdrawPageZ <br>
	 * description: 围绕散标-增量-借款用户（充值、提现）流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月3日 下午4:25:50
	 * 
	 * @param page
	 * @param cutd
	 * @return
	 */
	public Page<CgbUserTransDetail> findTransactCreUserRechargeWithdrawPageZ(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findTransactCreditUserInfoListZ(cutd));
		return page;
	}

	/**
	 * 
	 * methods: findTransactCreUserRechargeWithdrawPage <br>
	 * description: 围绕散标-2019-03-01 00:00:00 before存量借款用户（充值、提现）流水. <br>
	 * author: Roy <br>
	 * date: 2019年5月30日 下午5:22:49
	 * 
	 * @param page
	 * @param cutd
	 * @return
	 */
	public Page<CgbUserTransDetail> findTransactCreUserRechargeWithdrawPage(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findTransactCreditUserInfoList(cutd));
		return page;
	}

	/**
	 * 
	 * methods: findTransactInvestUserInfoPage <br>
	 * description: 围绕出借人-2019-03-01 00:00:00 before存量出借用户（充值、提现、出借返现）流水. <br>
	 * author: Roy <br>
	 * date: 2019年5月21日 下午5:02:44
	 * 
	 * @param page
	 * @param cutd
	 * @return
	 */
	public Page<CgbUserTransDetail> findTransactInvestUserInfoPage(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findLendParticularsInvUserTransListC(cutd));
		return page;
	}

	// 增量-出借人（充值、提现、出借返现）流水.
	public Page<CgbUserTransDetail> findTransactInvestUserInfoPageZ(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findLendParticularsInvUserTransListZ(cutd));
		return page;
	}

	/**
	 * 
	 * 方法: findCreditPage <br>
	 * 描述: 出借人. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年12月7日 下午1:46:16
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<CgbUserTransDetail> findCreditPage(Page<CgbUserTransDetail> page, CgbUserTransDetail entity) {

		page.setOrderBy("a.trans_date DESC");
		entity.setPage(page);
		page.setList(cgbUserTransDetailDao.findCreditList(entity));
		return page;
	}

	/**
	 * 
	 * 方法: findPage <br>
	 * 描述: 出借人. <br>
	 * 作者: Mr.云.李 <br>
	 * 
	 * @param page
	 * @param entity
	 * @return
	 * @see com.power.platform.common.service.CrudService#findPage(com.power.platform.common.persistence.Page, com.power.platform.common.persistence.DataEntity)
	 */
	public Page<CgbUserTransDetail> findPage(Page<CgbUserTransDetail> page, CgbUserTransDetail entity) {

		page.setOrderBy("a.trans_date DESC");
		entity.setPage(page);
		page.setList(cgbUserTransDetailDao.findList(entity));
		return page;
	}

	/**
	 * 新增交易记录信息
	 * 
	 * @param userTransDetail
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insert(CgbUserTransDetail userTransDetail) {

		logger.info("fn:insert,{交易ID：" + userTransDetail.getTransId() + "}");
		int flag = 0;
		try {

			flag = cgbUserTransDetailDao.insert(userTransDetail);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insert,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * ERP账户清空专用
	 * 
	 * @param page
	 * @param cgbUserTransDetail
	 * @return
	 */
	public Page<CgbUserTransDetail> findPage1(Page<CgbUserTransDetail> page, CgbUserTransDetail cgbUserTransDetail) {

		page.setOrderBy("a.trans_date DESC");
		cgbUserTransDetail.setPage(page);
		page.setList(cgbUserTransDetailDao.findList1(cgbUserTransDetail));
		return page;
	}

	public String inOutCount(CgbUserTransDetail cgbUserTransDetail) {

		String amount = cgbUserTransDetailDao.inOutCount(cgbUserTransDetail);
		return amount;
	}

	public List<CgbUserTransDetail> findCreditList(CgbUserTransDetail detail) {

		// TODO Auto-generated method stub
		return cgbUserTransDetailDao.findCreditList(detail);
	}

	/**
	 * 围绕出借人-2019-03-01 00:00:00 before存量出借用户（充值、提现）流水,帐号余额不为0，投资明细.
	 */
	public Page<CgbUserTransDetail> findTransactUserInfoPage2(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findTransactUserInfoList2(cutd));
		return page;
	}

	/**
	 * 围绕出借人-2019-03-01 00:00:00-存量-出借人（出借返现）流水-投资明细.
	 */
	public Page<CgbUserTransDetail> findLendParticularsInvCashBackC(Page<CgbUserTransDetail> page, CgbUserTransDetail cutd) {

		page.setOrderBy("a.trans_date ASC");
		cutd.setPage(page);
		page.setList(cgbUserTransDetailDao.findLendParticularsInvCashBackC(cutd));
		return page;
	}
}