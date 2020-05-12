/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.AUserAwardsHistoryDao;
import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.bouns.dao.AwardInfoDao;
import com.power.platform.bouns.dao.UserAwardDao;
import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.entity.AwardInfo;
import com.power.platform.bouns.entity.UserAward;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.userinfo.entity.UserInfo;


/**
 * 奖品信息Service
 * @author yb
 * @version 2016-12-13
 */
@Service("awardInfoService")
@Transactional(readOnly = false)
public class AwardInfoService extends CrudService<AwardInfo> {

	private static final Logger LOG = LoggerFactory.getLogger(AwardInfoService.class);
	
	@Resource
	private AwardInfoDao awardInfoDao;
	@Resource
	private UserBounsPointDao userBounsPointDao;
	@Resource
	private UserAwardDao userAwardDao;
	@Resource 
	private UserBounsHistoryDao userBounsHistoryDao;
	@Resource
	private AVouchersDicDao aVouchersDicDao;
	@Resource
	private AUserAwardsHistoryDao aUserAwardsHistoryDao;
	@Resource
	private UserVouchersHistoryDao userVouchersHistoryDao;
	
	public AwardInfo get(String id) {
		return super.get(id);
	}
	
	public List<AwardInfo> findList(AwardInfo awardInfo) {
		return super.findList(awardInfo);
	}
	
	
	@Transactional(readOnly = false)
	public void save(AwardInfo awardInfo) {
		super.save(awardInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(AwardInfo awardInfo) {
		super.delete(awardInfo);
	}

	@Override
	protected CrudDao<AwardInfo> getEntityDao() {
		// TODO Auto-generated method stub
		return awardInfoDao;
	}

	/**
	 * 奖品兑换
	 * @param user
	 * @param awardId
	 * @param needAmount 
	 * @param userBounsPoint 
	 * @return
	 */
	public Map<String, Object> insertawardToUser(UserInfo user, String awardId, Integer needAmount, UserBounsPoint userBounsPoint) {
		Map<String, Object> map = new HashMap<String, Object>();
		//新增用户奖品表
		UserAward userAward = null; // 清空该对象引用的数据，用户在短时间兑换奖品，1）会产生大量对象引用（增大GC负荷），2）重复数据入库.
		userAward = new UserAward();
		userAward.setId(IdGen.uuid());
		userAward.setUserId(user.getId());
		userAward.setAwardId(awardId);
		userAward.setCreateTime(new Date());
		userAward.setUpdateDate(new Date());
		userAward.setUpdateTime(new Date());
		
		
		
		// 扣积分（判断是否是虚拟商品然后扣积分）
		AwardInfo awardInfo = awardInfoDao.get(awardId);
		if(awardInfo != null){
			
			// 奖品所需积分不能为负数.
			if (Integer.parseInt(awardInfo.getNeedAmount()) < 0) {
				throw new WinException("奖品所需积分不能为负数");
			}
			
			if(awardInfo.getIsTrue().equals("1")){
				// 扣积分，添加历史
				Integer score = userBounsPoint.getScore();
				score = score - needAmount;
				userBounsPoint.setScore(score);		//扣除积分
				userBounsPoint.setUpdateDate(new Date());
				userBounsPointDao.update(userBounsPoint);
				
				
				// 添加积分明细信息
				UserBounsHistory bounsHistory = new UserBounsHistory();
				bounsHistory.setId(IdGen.uuid());
				bounsHistory.setAmount(- Double.valueOf(String.valueOf(needAmount)));
				bounsHistory.setUserId(user.getId());
				bounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_CASH_LOTTERY);
				bounsHistory.setTransId(userAward.getId());
				bounsHistory.setCreateDate(new Date());
				bounsHistory.setCurrentAmount(score.toString());
				userBounsHistoryDao.insert(bounsHistory);

				userAward.setneedAmount(needAmount);
				
				
				/*
				 * 虚拟物品抵用劵直接发放
				 */
				//N1.商品名称
				
				String voucherId = awardInfo.getVouchersId();
				
				//N2.查询抵用券是否有相应面额
				AVouchersDic vouchersDic = aVouchersDicDao.get(voucherId);
				if(vouchersDic !=null){
				   //N3.发放抵用劵
					UserVouchersHistory vouchersHistory = new UserVouchersHistory();
					vouchersHistory.setId(IdGen.uuid());
					vouchersHistory.setAwardId(vouchersDic.getId());
					vouchersHistory.setUserId(user.getId());
					vouchersHistory.setOverdueDate(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
					vouchersHistory.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
					vouchersHistory.setType(AUserAwardsHistoryService.COUPONS_TYPE_1);
					vouchersHistory.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
					vouchersHistory.setCreateDate(new Date());
					vouchersHistory.setUpdateDate(new Date());
					vouchersHistory.setRemark(vouchersDic.getRemarks());
					vouchersHistory.setSpans(vouchersDic.getSpans());
					vouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
					vouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
					int flag = userVouchersHistoryDao.insert(vouchersHistory);
					if(flag>0){
						LOG.info("{用户}"+user.getId()+"兑奖发放{"+vouchersDic.getAmount().toString()+"}元抵用劵成功");
						userAward.setUpdateDate(new Date());
						userAward.setState(UserAwardService.AWARD_END);//已结束
					}else{
						userAward.setState(UserAwardService.AWARD_ALREADY);//已下单
					}
					userAward.setVoucherId(vouchersHistory.getId());//用户抵用券ID
					userAward.setDeadline(DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays()));
				}else{
					userAward.setState(UserAwardService.AWARD_ALREADY);//已下单
				}
				
				
			} else {
				userAward.setneedAmount(needAmount);
				userAward.setState(UserAwardService.AWARD_WAITING);//待下单
				int day = 0;
				if(awardInfo.getDeadline()!=null){
					day = Integer.valueOf(awardInfo.getDeadline());
				}
				userAward.setDeadline(DateUtils.getSpecifiedMonthAfter(userAward.getCreateTime(), day));
			}
			
			map.put("awardId", awardInfo.getId());
			map.put("awardIsTrue", awardInfo.getIsTrue());
			map.put("userAwardId", userAward.getId());
		}
		
		userAwardDao.insert(userAward);
		
		return map;
	}
	
	
	/**
	 * 用户确认兑换奖品
	 * @param user
	 * @param awardId
	 * @param needAmount
	 * @param userBounsPoint
	 * @param addressId
	 * @return
	 */
	public int updateAwardToUser(UserInfo user, String myAwardId, Integer needAmount, UserBounsPoint userBounsPoint, String addressId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		int i = 0;
		//N1.扣除奖品对应积分
		Integer score = userBounsPoint.getScore();
		score = score -needAmount;
		userBounsPoint.setScore(score);//扣除积分
		userBounsPoint.setUpdateDate(new Date());
		userBounsPointDao.update(userBounsPoint);
		
		// 添加积分明细信息
		UserBounsHistory bounsHistory = new UserBounsHistory();
		bounsHistory.setId(IdGen.uuid());
		bounsHistory.setAmount(- Double.valueOf(String.valueOf(needAmount)));
		bounsHistory.setUserId(user.getId());
		bounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_CASH_LOTTERY);
		bounsHistory.setTransId(myAwardId);
		bounsHistory.setCreateDate(new Date());
		bounsHistory.setCurrentAmount(score.toString());
		userBounsHistoryDao.insert(bounsHistory);
		
		//N2.绑定地址
		UserAward userAward = userAwardDao.get(myAwardId);
		if(userAward!=null){
			userAward.setAddressId(addressId);
			userAward.setState(UserAwardService.AWARD_ALREADY);//已下单
			userAward.setUpdateDate(new Date());
			userAward.setUpdateTime(new Date());
			i = userAwardDao.update(userAward);
			return i;
		}else{
			return i;
		}
	}

	public Page<AwardInfo> findPage1(Page<AwardInfo> page, AwardInfo awardInfo) {
		awardInfo.setPage(page);
		page.setList(awardInfoDao.findList1(awardInfo));
		return page;
	}
	
	public Page<AwardInfo> findPage2(Page<AwardInfo> page, AwardInfo awardInfo) {
		awardInfo.setPage(page);
		page.setList(awardInfoDao.findList2(awardInfo));
		return page;
	}
	
}