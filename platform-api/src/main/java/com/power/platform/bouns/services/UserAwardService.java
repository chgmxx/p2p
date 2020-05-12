/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.services;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bouns.dao.UserAwardDao;
import com.power.platform.bouns.entity.UserAward;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;


/**
 * 用户兑换奖品Service
 * @author yb
 * @version 2016-12-13
 */
@Service("userAwardService")
@Transactional(readOnly = false)
public class UserAwardService extends CrudService<UserAward> {

	@Resource
	private UserAwardDao userAwardDao;
	
	public static final String AWARD_WAITING = "0"; //待下单
	public static final String AWARD_ALREADY = "1"; //已下单
	public static final String AWARD_DELIVERY = "2";//已发货
	public static final String AWARD_END = "3";//已结束
	public static final String AWARD_PAY = "4";//已兑现
	public static final String AWARD_LOSE = "5";//已失效
	
	
	public UserAward get(String id) {
		return super.get(id);
	}
	
	public List<UserAward> findList(UserAward userAward) {
		return super.findList(userAward);
	}
	
	@Transactional(readOnly = false)
	public void save(UserAward userAward) {
		super.save(userAward);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserAward userAward) {
		super.delete(userAward);
	}

	@Override
	protected CrudDao<UserAward> getEntityDao() {
		// TODO Auto-generated method stub
		return userAwardDao;
	}

	public List<UserAward> findList2(UserAward userAward) {
		
		return userAwardDao.findList2(userAward);
	}

	public Page<UserAward> findPage0(Page<UserAward> page, UserAward userAward) {
		userAward.setPage(page);
		page.setList(userAwardDao.findNeedAmount0(userAward));
		return page;
	}

	public Page<UserAward> findPage1(Page<UserAward> page, UserAward userAward) {
		userAward.setPage(page);
		page.setList(userAwardDao.findNeedAmount1(userAward));
		return page;
	}
	
	public Page<UserAward> findPage2(Page<UserAward> page, UserAward userAward) {
		userAward.setPage(page);
		page.setList(userAwardDao.findList2(userAward));
		return page;
	}

	public void checkTodead() {
		// TODO Auto-generated method stub
		//N1.查询所有待下单并且已过失效时间的兑奖记录
		List<UserAward> list = userAwardDao.findToDeadList();
		if(list!=null && list.size()>0){
			for (UserAward userAward : list) {
				userAward.setState(AWARD_LOSE);
				userAward.setUpdateDate(new Date());
				int i = userAwardDao.update(userAward);
				if(i>0){
					logger.info("兑奖纪录"+userAward.getId()+"已更改为失效状态");
				}
			}
		}
	}

	/**
	 * 更新用户兑奖记录状态字段以适应新的状态规则
	 */
	public void updateUserAwardInfo() {
		// TODO Auto-generated method stub
		UserAward entity = new UserAward();
		entity.setEndDate(DateUtils.getDateOfString("2018-08-13 0:00:00"));
		List<UserAward> list = userAwardDao.findList(entity);
		if(list!=null && list.size()>0){
			for (UserAward userAward : list) {
			    if(userAward.getState()!=null && !userAward.getState().equals("")){
			    	if(userAward.getState().equals("0")){
			    		//0已下单变更===>1已下单
			    		userAward.setState(AWARD_ALREADY);
			    		int i = userAwardDao.update(userAward);
			    		if(i>0){
			    			logger.info("0===>1已下单");
			    		}
			    	}else if(userAward.getState().equals("1")){
			    		//1已发货变更===>2已发货
			    		userAward.setState(AWARD_DELIVERY);
			    		int i = userAwardDao.update(userAward);
			    		if(i>0){
			    			logger.info("1===>2已发货");
			    		}
			    	}else if(userAward.getState().equals("2")){
			    		//2已结束变更===>3已结束
			    		userAward.setState(AWARD_END);
			    		int i = userAwardDao.update(userAward);
			    		if(i>0){
			    			logger.info("2===>3已结束");
			    		}
			    	}else if(userAward.getState().equals("3")){
			    		//3已兑换变更===>0待下单
			    		userAward.setState(AWARD_WAITING);
			    		userAward.setDeadline(DateUtils.getSpecifiedMonthAfter(userAward.getCreateTime(), 3));
			    		int i = userAwardDao.update(userAward);
			    		if(i>0){
			    			logger.info("3===>1待下单");
			    		}
			    	}
			    }	
			}
		}
	}
	
}