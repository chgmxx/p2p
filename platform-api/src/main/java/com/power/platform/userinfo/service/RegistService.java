package com.power.platform.userinfo.service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.activity.dao.LevelDistributionDao;
import com.power.platform.activity.entity.LevelDistribution;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.EncoderUtil;
import com.power.platform.common.utils.IdGen;
import com.power.platform.sys.type.RecomUserType;
import com.power.platform.sys.type.RegisterFromType;
import com.power.platform.sys.type.UserStateType;
import com.power.platform.sys.type.UserType;
import com.power.platform.userinfo.dao.RegistUserDao;
import com.power.platform.userinfo.dao.UserAccountDao;
import com.power.platform.userinfo.dao.UserAccountInfoDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.dao.UserSpreadHistoryDao;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserSpreadHistory;

@Service("registService")
public class RegistService extends CrudService<UserInfo> {

	@Resource
	private RegistUserDao registUserDao;
	@Resource
	private UserSpreadHistoryDao userSpreadHistoryDao;
	@Resource
	private UserAccountDao userAccountDao;
	@Resource
	private UserAccountInfoDao userAccountInfoDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private LevelDistributionDao levelDistributionDao;
	@Resource
	private UserBounsPointDao userBounsPointDao;
	

	public List<UserInfo> findAll() {

		// TODO Auto-generated method stub
		return null;
	}

	protected CrudDao<UserInfo> getEntityDao() {

		// TODO Auto-generated method stub
		return null;
	}

	public boolean isNumeric1(String str) {

		Pattern pattern = Pattern.compile("[0-9]+");
		return pattern.matcher(str).matches();
	}

	/**
	 * 校验是否注册
	 */

	public boolean checkUsername(String name) {

		// TODO Auto-generated method stub
		UserInfo user = new UserInfo();
		user.setName(name);
		List<UserInfo> returnuser = registUserDao.findList(user);
		if (returnuser != null && returnuser.size() > 0) {
			System.out.println("=====手机号为" + name + "已注册=====");
			return true;
		}
		System.out.println("=====手机号为" + name + "未注册=====");
		return false;
	}

	/**
	 * 检查用户是否存在,不存在给用户设置级别
	 * 
	 * @param reomId
	 * @param wUser
	 * @return
	 * @throws Exception
	 */

	public boolean CheckUser(String reomId, UserInfo userInfo) {

		UserInfo wusers = registUserDao.get(reomId);
		UserSpreadHistory spreadHistory;
		if (wusers != null) {
			if (wusers.getRecomType() == 0) {
				wusers.setRecomType(RecomUserType.Common_promotion);
				registUserDao.update(wusers);
				spreadHistory = new UserSpreadHistory();
				spreadHistory.setId(new IdGen().getNextId());
				spreadHistory.setRecomType(String.valueOf(RecomUserType.Common_promotion));
				spreadHistory.getUser().setId(String.valueOf(reomId));
				spreadHistory.setCreateTime(new Date());
				userSpreadHistoryDao.insert(spreadHistory);
			}
			userInfo.setRecommendUserId(reomId);
			return true;
		}
		return false;
	}

	/**
	 * 注册用户
	 */

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String registUser(UserInfo userInfo, String refer, String field1, String refereesMobilePhone) {

		// TODO Auto-generated method stub
		UserInfo user = new UserInfo();
		System.out.println("进入注册ServiceImpl层");
		user.setName(userInfo.getName());
		user.setUserType(UserType.BID);
		List<UserInfo> users = registUserDao.findList(user);
		if (users != null && users.size() > 0) {
			userInfo = users.get(0);
		} else {

			if (userInfo.getId() == null || userInfo.getId().length() == 0) {
				userInfo.setId(String.valueOf(new IdGen().randomLong()));
			}
			if (refer != null && isNumeric1(refer)) {
				CheckUser(refer, userInfo);
			}

			userInfo.setRecomType(0);
			userInfo.setUserType(UserType.BID);// 投资用户
			userInfo.setPwd(EncoderUtil.encrypt(userInfo.getPwd())); // MD5加密
			userInfo.setState(UserStateType.NORMAL);
			userInfo.setRegisterFrom(RegisterFromType.PC);
			Date date = new Date();
			userInfo.setCreateDate(date);
			userInfo.setEmailChecked(1);// 邮箱未验证
			userInfo.setRegisterDate(date);
			userInfo.setLastLoginIp(userInfo.getLastLoginIp());
			userInfo.setAccountId(String.valueOf(new IdGen().randomLong()));
			// 首先插入客户信息表
			int a = registUserDao.insert(userInfo);
			if (a > 0) {
				logger.debug("用户注册插入客户表完成");
				UserAccountInfo userAccountInfo = new UserAccountInfo();
				userAccountInfo.setId(userInfo.getAccountId());
				userAccountInfo.setUserId(userInfo.getId());
				userAccountInfo.setTotalAmount(0d);
				userAccountInfo.setTotalInterest(0d);
				userAccountInfo.setAvailableAmount(0d);
				userAccountInfo.setFreezeAmount(0d);
				userAccountInfo.setRechargeAmount(0d);
				userAccountInfo.setRechargeCount(0);
				userAccountInfo.setCashAmount(0d);
				userAccountInfo.setCashCount(0);
				userAccountInfo.setCurrentAmount(0d);
				userAccountInfo.setRegularDuePrincipal(0d);
				userAccountInfo.setRegularDueInterest(0d);
				userAccountInfo.setRegularTotalAmount(0d);
				userAccountInfo.setRegularTotalInterest(0d);
				userAccountInfo.setCurrentTotalAmount(0d);
				userAccountInfo.setCurrentTotalInterest(0d);
				userAccountInfo.setCurrentYesterdayInterest(0d);
				userAccountInfo.setReguarYesterdayInterest(0d);
				userAccountInfo.setUserInfo(userInfo);
				// 同时生成客户账户
				userAccountInfoDao.insert(userAccountInfo);
			}


			
			/**
			 * LevelDistribution.
			 */

			// 推荐人手机号码.
			if (!StringUtils.isBlank(refereesMobilePhone)) {
				// 推荐人手机号码不能是自己的手机号码.
				if (refereesMobilePhone.equals(userInfo.getName())) {
					logger.info("fn:registUser,{三级雇佣关系建立条件不足}");
				} else {
					// 根据推荐人的手机查询推荐人的ID并建立雇佣关系(三级佣金关系).
					UserInfo userPhone = userInfoDao.getUserInfoByPhone(refereesMobilePhone);
					if (userPhone != null) {
						String refereesUserId = userPhone.getId(); // wUserList.get(0).getId();
						// 在三级佣金表中查询推荐人的上线userId.
						LevelDistribution threeLevelModel = levelDistributionDao.selectByUserId(refereesUserId);
						LevelDistribution record = new LevelDistribution();
						record.preInsert();
						record.setUserId(userInfo.getId()); // 当前客户(三级客户).
						record.setParentId(refereesUserId); // 推荐人(二级客户).
						if (threeLevelModel != null) {
							record.setGrandpaId(threeLevelModel.getParentId()); // 推荐人上线(一级客户).
						}
						record.setInviteCode(refereesMobilePhone); // 推广方式(手机号码优先级大于邀请码).
						record.setType(null); // 默认客户类型为普通客户.
						record.setCreateDate(new Date());
						int flag = levelDistributionDao.insert(record);
						if (flag == 1) {
							logger.info("fn:registUser,{三级雇佣关系建立成功}");
						} else {
							logger.info("fn:registUser,{三级雇佣关系建立失败}");
						}
					}
				}
			} else {
				if (!StringUtils.isBlank(refer)) { // refer：就是推荐人的推广码同时也是推荐人的唯一标识userId.
					UserInfo refereesUserInfo = userInfoDao.get(refer);
					if (refereesUserInfo != null) {
						// 在三级佣金表中查询推荐人的上线userId.
						LevelDistribution threeLevelModel = levelDistributionDao.selectByUserId(refereesUserInfo.getId());
						LevelDistribution record = new LevelDistribution();
						record.preInsert();
						record.setUserId(userInfo.getId()); // 当前客户ID.
						record.setParentId(refereesUserInfo.getId()); // 推荐人客户ID.
						if (threeLevelModel != null) {
							record.setGrandpaId(threeLevelModel.getParentId()); // 推荐人上线(一级客户).
						}
						record.setInviteCode(refer); // 推广方式(手机号码优先级大于邀请码).
						record.setType(null); // 默认客户类型为普通客户.
						record.setCreateDate(new Date());
						int flag = levelDistributionDao.insert(record);
						if (flag == 1) {
							logger.info("fn:registUser,{三级雇佣关系建立成功}");
						} else {
							logger.info("fn:registUser,{三级雇佣关系建立失败}");
						}
					}
				}
			}
		}
		System.out.println("用户ID============" + userInfo.getId());
		return userInfo.getId();
	}
	
}
