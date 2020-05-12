package com.power.platform.bouns.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.dao.UserSignedDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.entity.UserSigned;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IsHolidayOrBirthday;

/**
 * 
 * 类: UserSignedService <br>
 * 描述: 客户签到Service. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年12月13日 下午3:56:05
 */
@Service
@Transactional(readOnly = true)
public class UserSignedService extends CrudService<UserSigned> {

	/**
	 * 连续签到次数1.
	 */
	public static final Integer CONTINUOUS_TIME_1 = 1;
	/**
	 * 签到成功.
	 */
	public static final Integer SIGNED_STATE_1 = 1;
	/**
	 * 已签到，请明天再来.
	 */
	public static final Integer SIGNED_STATE_2 = 2;
	/**
	 * 未签到.
	 */
	public static final Integer SIGNED_STATE_3 = 3;

	@Resource
	private UserSignedDao userSignedDao;
	@Resource
	private UserBounsHistoryDao userBounsHistoryDao;
	@Resource
	private UserBounsPointDao userBounsPointDao;
	@Autowired
	private UserBounsPointService userBounsPointService;

	@Override
	protected CrudDao<UserSigned> getEntityDao() {

		logger.info(this.getClass() + "fn:getEntityDao,{获取当前DAO}");
		return userSignedDao;
	}

	/**
	 * 
	 * methods: signed <br>
	 * description: 签到 <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月3日 下午12:58:25
	 * 
	 * @param userId
	 * @param certificateNo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> signed(String userId, String certificateNo) {

		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();

		/**
		 * 判断客户当天是否签到.
		 */
		String date = DateUtils.getDate(new Date(), "yyyy-MM-dd");
		String beginNowDate = date + " 00:00:00";
		String endNowDate = date + " 23:59:59";
		// 封装查询条件.
		UserSigned nowEntity = new UserSigned();
		nowEntity.setBeginDate(beginNowDate);
		nowEntity.setEndDate(endNowDate);
		nowEntity.setUserId(userId);
		List<UserSigned> nowList = userSignedDao.findExists(nowEntity);

		/**
		 * 获得是否是节假日
		 */
		// 是否是用户生日.
		boolean isHoliday = IsHolidayOrBirthday.isHoliday(certificateNo);
		// 是否是节假日.
		ArrayList<String> holidays = IsHolidayOrBirthday.getHolidays();
		Date todayDate = new Date();
		String todayStr = DateUtils.getDate(todayDate, "MMdd"); // 当天
		if (holidays.contains(todayStr)) {
			isHoliday = true;
		}

		if (nowList.size() == 0) { // 当天没有签到.
			/**
			 * 判断客户前一天是否签到.
			 */
			String dateBefore = DateUtils.getDateBefore();
			String beginDate = dateBefore + " 00:00:00";
			String endDate = dateBefore + " 23:59:59";
			// 封装查询条件.
			UserSigned entity = new UserSigned();
			entity.setBeginDate(beginDate);
			entity.setEndDate(endDate);
			entity.setUserId(userId);
			List<UserSigned> list = userSignedDao.findExists(entity);
			/**
			 * 判断客户连续签到次数.
			 */
			// 新增客户签到记录.
			UserSigned model = new UserSigned();
			model.setId(IdGen.uuid()); // 主键ID.
			model.setUserId(userId); // 客户ID.
			model.setCreateDate(new Date());
			model.setUpdateDate(new Date());
			if (list.size() == 0) { // 当前客户没有连续签到，签到连续次数为1，送1积分.

				model.setContinuousTime(CONTINUOUS_TIME_1);
				// 连续签到次数.
				data.put("continuousTime", CONTINUOUS_TIME_1);
				/**
				 * 用户积分信息
				 */
				UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
				Integer score = userBounsPoint.getScore();
				if (isHoliday) {
					score = score + 1 * 2;
					userBounsPoint.setScore(score);
				} else {
					score = score + 1;
					userBounsPoint.setScore(score);
				}
				int updateBounsPointFlag = userBounsPointDao.update(userBounsPoint);
				if (updateBounsPointFlag == 1) {
					logger.info(this.getClass() + "：客户积分账户更新成功！");
					// 客户积分账户总积分.
					data.put("integralCount", score);
				} else {
					logger.info(this.getClass() + "：客户积分账户更新失败！");
				}
				/**
				 * 新增积分历史记录.
				 */
				UserBounsHistory userBounsHistory = new UserBounsHistory();
				userBounsHistory.setId(IdGen.uuid());
				userBounsHistory.setUserId(userId);

				if (isHoliday) { // 节假日双倍
					userBounsHistory.setAmount(2D);
					// 积分.
					data.put("integral", 1 * 2);
				} else { // 非节假日+1
					userBounsHistory.setAmount(1D);
					// 积分.
					data.put("integral", 1);
				}

				userBounsHistory.setCreateDate(new Date());
				userBounsHistory.setTransId(model.getId());
				userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_SIGNED);
				userBounsHistory.setCurrentAmount(score.toString()); // 当前积分（签到之后）
				int flag = userBounsHistoryDao.insert(userBounsHistory);
				if (flag == 1) {
					logger.info(this.getClass() + "\t积分流水新增成功");
				} else {
					logger.info(this.getClass() + "\t积分流水新增失败");
				}
			} else { // 当前客户为连续签到，在原有的签到次数上加1.

				UserSigned userSigned = list.get(0);
				int continuousTime = userSigned.getContinuousTime() + CONTINUOUS_TIME_1;
				model.setContinuousTime(continuousTime);
				// 连续签到次数.
				data.put("continuousTime", continuousTime);
				/**
				 * 积分账户
				 */
				UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
				Integer score = userBounsPoint.getScore();
				// 判断签到次数，签到次数是否为7的倍数.
				if (continuousTime % 7 == 0) { // 7的倍数，多赠送3积分.
					if (isHoliday) {
						score = score + 4 * 2;
						userBounsPoint.setScore(score);
					} else {
						score = score + 4;
						userBounsPoint.setScore(score);
					}
				} else { // 反之，赠送1积分.
					if (isHoliday) {
						score = score + 1 * 2;
						userBounsPoint.setScore(score);
					} else {
						score = score + 1;
						userBounsPoint.setScore(score);
					}
				}
				int updateBounsPointFlag = userBounsPointDao.update(userBounsPoint);
				if (updateBounsPointFlag == 1) {
					logger.info(this.getClass() + "\t积分账户更新成功！");
					// 客户积分账户总积分.
					data.put("integralCount", score);
				} else {
					logger.info(this.getClass() + "\t积分账户更新失败！");
				}
				/**
				 * 新增积分历史记录.
				 */
				UserBounsHistory userBounsHistory = new UserBounsHistory();
				userBounsHistory.setId(IdGen.uuid());
				userBounsHistory.setUserId(userId);
				// 判断签到次数，签到次数是否为7的倍数.
				if (continuousTime % 7 == 0) { // 7的倍数，多赠送3积分.
					if (isHoliday) {
						userBounsHistory.setAmount(4D * 2);
						data.put("integral", 4 * 2);
					} else {
						userBounsHistory.setAmount(4D);
						data.put("integral", 4);
					}
				} else { // 反之，赠送1积分.
					if (isHoliday) {
						userBounsHistory.setAmount(1D * 2);
						// 积分.
						data.put("integral", 1 * 2);
					} else {
						userBounsHistory.setAmount(1D);
						// 积分.
						data.put("integral", 1);
					}
				}
				userBounsHistory.setCreateDate(new Date());
				userBounsHistory.setTransId(model.getId());
				userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_SIGNED);
				userBounsHistory.setCurrentAmount(score.toString());
				int flag = userBounsHistoryDao.insert(userBounsHistory);
				if (flag == 1) {
					logger.info(this.getClass() + "\t积分流水新增成功");
				} else {
					logger.info(this.getClass() + "\t积分流水新增失败");
				}
			}
			int usFlag = userSignedDao.insert(model);
			if (usFlag == 1) {
				logger.info(this.getClass() + "\t签到成功");
			} else {
				logger.info(this.getClass() + "\t签到失败");
			}
			// 签到状态.
			data.put("signed", SIGNED_STATE_1);
			return data;
		} else { // 当天已签到.
			logger.info(this.getClass() + "\t已经签到，明天再来吧！");
			UserSigned userSigned = nowList.get(0);
			// 连续签到次数.
			data.put("continuousTime", userSigned.getContinuousTime());
			// 判断连续签到次数，签到次数是否为7的倍数.
			if (userSigned.getContinuousTime() % 7 == 0) { // 7的倍数，多赠送3积分.
				if (isHoliday) {
					// 节假日积分双倍.
					data.put("integral", 4 * 2);
				} else {
					// 自然日单倍.
					data.put("integral", 4);
				}
			} else { // 反之，赠送1积分.
				if (isHoliday) {
					// 节假日积分双倍.
					data.put("integral", 1 * 2);
				} else {
					// 节假日积分双倍.
					data.put("integral", 1);
				}
			}
			// 签到状态.
			data.put("signed", SIGNED_STATE_2);
			UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(userId);
			// 客户积分账户总积分.
			data.put("integralCount", userBounsPoint.getScore());
			return data;
		}
	}

}