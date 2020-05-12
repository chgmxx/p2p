package com.power.platform.pay.cash.service;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.pay.cash.dao.UserCashDao;
import com.power.platform.pay.cash.entity.UserCash;

@Service("userCashService")
@Transactional(readOnly = true)
public class UserCashService extends CrudService<UserCash> {

	private static final Logger logger = Logger.getLogger(UserCashService.class);

	@Resource
	private UserCashDao userCashDao;
	@Resource
	private CgbUserTransDetailService cgbUserTransDetailService;

	protected CrudDao<UserCash> getEntityDao() {

		return userCashDao;
	}

	/**
	 * 
	 * 方法: findExcelReportPage <br>
	 * 描述: 财务需求，提现列表分页展示. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月9日 上午10:27:51
	 * 
	 * @param page
	 * @param entity
	 * @return
	 */
	public Page<UserCash> findExcelReportPage(Page<UserCash> page, UserCash entity) {

		entity.setPage(page);
		page.setList(userCashDao.findExcelReportList(entity));
		return page;
	}

	/**
	 * 保存提现信息
	 */

	@Transactional(readOnly = false)
	public int insert(UserCash userCash) {

		int flag = 0;
		try {

			flag = userCashDao.insert(userCash);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:insert,{异常：" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 更新提现状态
	 */

	@Transactional(readOnly = false)
	public int updateState(UserCash userCash) {

		int a = userCashDao.updateState(userCash);
		return a;
	}

	public List<UserCash> getCashCount(UserCash userCash) {

		return userCashDao.getCashCount(userCash);
	}

	/**
	 * 投资用户提现次数
	 * @param userId
	 * @return
	 */
	public int getFreeCashCount(String userId) {

		int freeCash = 2;
		UserCash userCash = new UserCash();
		userCash.setUserId(userId); // 用户ID
		userCash.setState(UserCash.CASH_SUCCESS); // 提现到账成功
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		userCash.setBeginBeginDate(calendar.getTime()); // 查询开始日期
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		userCash.setEndBeginDate(calendar.getTime()); // 查询结束日期
		List<UserCash> userCashs = userCashDao.findNumberOfWithdrawal(userCash);
		if (userCashs != null && userCashs.size() > 0) {
			freeCash = freeCash - userCashs.size() >= 0 ? freeCash - userCashs.size() : 0;
		}
		return freeCash;
	}

	public UserCash getInfoById(String orderId) {
		// TODO Auto-generated method stub
		return userCashDao.getInfoById(orderId);
	}
	
	/**
	 * 借款用户提现次数
	 * @param userId
	 * @return
	 */
	public int getCreditUserCashCount(String userId) {
		int freeCash = 2;
		UserCash userCash = new UserCash();
		userCash.setUserId(userId);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		userCash.setBeginBeginDate(calendar.getTime());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		userCash.setEndBeginDate(calendar.getTime());
		List<UserCash> userCashs = userCashDao.getCreditUserCashCount(userCash);
		if (userCashs != null && userCashs.size() > 0) {
			freeCash = freeCash - userCashs.size() >= 0 ? freeCash - userCashs.size() : 0;
		}
		return freeCash;
	}

	@Transactional(readOnly = false,rollbackFor=Exception.class)
	public void updateUserCashState() {
		// TODO Auto-generated method stub
		int numC = 0;
		int numJ = 0;
		//N1.查询交易流水提现记录
		CgbUserTransDetail detail = new CgbUserTransDetail();
		detail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1);
		detail.setState(CgbUserTransDetailService.TRUST_STATE_2);
		detail.setBeginTransDate(DateUtils.parseDate("2017-12-21"));
		detail.setEndTransDate(DateUtils.parseDate(DateUtils.getDate()));
		List<CgbUserTransDetail> cList = cgbUserTransDetailService.findList(detail);
		List<CgbUserTransDetail> jList = cgbUserTransDetailService.findCreditList(detail);
		logger.info("出借人===============>>>"+cList.size());
		if(cList!=null){
			for (CgbUserTransDetail cgbUserTransDetail : cList) {
				if(cgbUserTransDetail.getState()!=null && !cgbUserTransDetail.getState().equals("")){
					UserCash userCash = userCashDao.get(cgbUserTransDetail.getTransId());
					if(userCash!=null){
						logger.info("出借人第"+numC+"条");
						userCash.setFrom(2);;
						userCash.setState(UserCash.CASH_SUCCESS);
						int i = userCashDao.update(userCash);
						if(i>0){
							logger.info("提现ID为["+userCash.getId()+"]状态更新成功");
						}
					}
				}
				numC++;
			}
		}
		logger.info("借款人===============>>>"+jList.size());
		if(jList!=null){
			for (CgbUserTransDetail cgbUserTransDetail : jList) {
				if(cgbUserTransDetail.getState()!=null && !cgbUserTransDetail.getState().equals("")){
					UserCash userCash = userCashDao.get(cgbUserTransDetail.getTransId());
					if(userCash!=null){
						logger.info("借款人第"+numJ+"条");
						userCash.setFrom(2);;
						userCash.setState(UserCash.CASH_SUCCESS);
						int i = userCashDao.update(userCash);
						if(i>0){
							logger.info("提现ID为["+userCash.getId()+"]状态更新成功");
						}
					}
				}
				numJ++;
			}
		}
	}

}
