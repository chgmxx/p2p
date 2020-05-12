package com.power.platform.current.service.invest;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.current.dao.WloanCurrentUserInvestDao;
import com.power.platform.current.entity.WloanCurrentUserInvest;

/**
 * 
 * 类: WloanCurrentUserInvestService <br>
 * 描述: 活期客户投资Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月13日 下午3:21:28
 */
@Service("wloanCurrentUserInvestService")
@Transactional(readOnly=true)
public class WloanCurrentUserInvestService extends CrudService<WloanCurrentUserInvest> {

	/**
	 * 信息提示，1：活期资金池不存在.
	 */
	public static final String MESSAGE_STATE_1 = "1";

	/**
	 * 信息提示，2：活期资金池余额不足.
	 */
	public static final String MESSAGE_STATE_2 = "2";

	/**
	 * 信息提示，3：投资金额 + 在投金额，超出活期资金池最大限额.
	 */
	public static final String MESSAGE_STATE_3 = "3";

	/**
	 * 信息提示，4：陈炜玲债权转让成功.
	 */
	public static final String MESSAGE_STATE_4 = "4";

	/**
	 * 信息提示，5：不符合陈炜玲债权转.
	 */
	public static final String MESSAGE_STATE_5 = "5";

	/**
	 * 数据状态，1：待投资.
	 */
	public static final String WLOAN_CURRENT_USER_INVEST_STATE_1 = "1";

	/**
	 * 数据状态，2：已投资.
	 */
	public static final String WLOAN_CURRENT_USER_INVEST_STATE_2 = "2";

	/**
	 * 数据状态，3：已赎回.
	 */
	public static final String WLOAN_CURRENT_USER_INVEST_STATE_3 = "3";

	/**
	 * 投资状态，1：投资成功.
	 */
	public static final String WLOAN_CURRENT_USER_INVEST_BID_STATE_1 = "1";

	private static final Logger logger = Logger.getLogger(WloanCurrentUserInvestService.class);
	
	@Resource
	private WloanCurrentUserInvestDao wloanCurrentUserInvestDao;
	
	@Override
	protected CrudDao<WloanCurrentUserInvest> getEntityDao() {
		return wloanCurrentUserInvestDao;
	}
	
	/**
	 * 
	 * 方法: findCurrentUserInvestByState <br>
	 * 描述: 根据数据状态(1：待投资，2：已投资)查询客户活期投资记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月19日 下午5:06:26
	 * 
	 * @return
	 */
	public List<WloanCurrentUserInvest> findCurrentUserInvestByState(WloanCurrentUserInvest wloanCurrentUserInvest) {
		return wloanCurrentUserInvestDao.findCurrentUserInvestByState(wloanCurrentUserInvest);
	}

	/**
	 * 
	 * 方法: updateWloanCurrentUserInvest <br>
	 * 描述: 更新. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月14日 下午6:52:14
	 * 
	 * @param wloanCurrentUserInvest
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateWloanCurrentUserInvest(WloanCurrentUserInvest wloanCurrentUserInvest) {
		int flag = 0;
		try {
			flag = wloanCurrentUserInvestDao.update(wloanCurrentUserInvest);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateWloanCurrentUserInvest,{更新保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 
	 * 方法: insertWloanCurrentUserInvest <br>
	 * 描述: 新增. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月14日 下午1:53:11
	 * 
	 * @param wloanCurrentUserInvest
	 * @return
	 */
	@Transactional(readOnly = false)
	public int insertWloanCurrentUserInvest(WloanCurrentUserInvest wloanCurrentUserInvest) {
		int flag = 0;
		try {
			flag = wloanCurrentUserInvestDao.insert(wloanCurrentUserInvest);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insertWloanCurrentUserInvest,{新增保存异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 根据用户ID查询已投资成功未放款(按投资日期升序)
	 * 
	 * @param transfer_person
	 * @return
	 */
	public List<WloanCurrentUserInvest> findListOrderBy(String transfer_person) {
		return wloanCurrentUserInvestDao.findListOrderBy(transfer_person);
	}

	/**
	 * 根据ID更新数据
	 * 
	 * @param amount
	 * @param state
	 */
	public void updateById(Double amount, String state,String Id) {
		wloanCurrentUserInvestDao.updateById(amount,state,Id);
	}

}
