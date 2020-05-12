package com.power.platform.bouns.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.entity.AUserAwardsHistory;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.AVouchersDicService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.bouns.entity.AwardInfo;
import com.power.platform.bouns.entity.UserAward;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.cache.Cache;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

@Component
@Path("/userDrawLottery")
@Service("userDrawLotteryService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserBounsDrawLotteryService {

	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Autowired
	private AwardInfoService awardInfoService;
	@Autowired
	private UserAwardService userAwardService;
	@Autowired
	private AVouchersDicService aVouchersDicService;
	@Autowired
	private AUserAwardsHistoryService aUserAwardsHistoryService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AVouchersDicDao aVouchersDicDao;
	@Autowired
	private UserVouchersHistoryDao userVouchersHistoryDao;
	
	
	private static final Logger logger = Logger.getLogger(UserBounsDrawLotteryService.class);
	
	
	/**
	 * 抽奖方法
	 * @param request
	 * @return
	 */
	@POST
	@Path("/drawLottery")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getLottery( @FormParam("from")String from, @FormParam("token")String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String> map = new HashMap<String, String>();
		Integer i = 10;
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
				result.put("state", "1");
				throw new Exception("缺少必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			UserInfo userInfo = null;
			UserBounsPoint userBounsPoint = null;
			if (!StringUtils.isBlank(jedisUserId)) {
				userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo==null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					
					// 判断用户所剩积分是否可以进行抽奖
					userBounsPoint = userBounsPointService.getUserBounsPoint(userInfo.getId());
					if(userBounsPoint.getScore() < UserBounsPointService.USER_DRAW_LOTTERY_BOUNS){
						throw new WinException("用户积分不足");
					}
					
					
					UserBounsHistory userBounsHistory = new UserBounsHistory();
					userBounsHistory.setUserId(userInfo.getId());
					userBounsHistory.setBeginCreateDate(DateUtils.getDate());
					userBounsHistory.setEndCreateDate(DateUtils.getSpecifiedDayAfter(DateUtils.getDate()));
					userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW);
					List<UserBounsHistory> list = userBounsHistoryService.findList(userBounsHistory);
					if(list!=null && list.size()>0){
						i = i - list.size();
					}
					
					
					if(i<=0){
						throw new WinException("今日抽奖次数已用光");
					}
					
					// 获取抽奖列表
					Object[][] prizeArr = getAwardsObj();

					// 进行抽奖
					map = award(prizeArr);
					
					// 获取抽到的奖品信息
					AwardInfo awardInfo = new AwardInfo();
					
					
					
					UserAward userAward = new UserAward();
					
					
					// 判断是否是谢谢惠顾
					if(map.get("awardName").equals("谢谢惠顾")){
						map.put("isDrawnPrize", "1");
					} else {
						map.put("isDrawnPrize", "0");
						
						awardInfo = awardInfoService.get( map.get("awardId").toString() );
						
						// 将抽到的奖项加入到用户抽奖表
						userAward.setUserId(userInfo.getId());
						userAward.setAwardId(map.get("awardId"));
						userAward.setCreateTime(new Date());
						userAward.setUpdateDate(new Date());
						if(awardInfo.getIsTrue().equals("0")){
							int day = 0;
							if(awardInfo.getDeadline()!=null){
								day = Integer.valueOf(awardInfo.getDeadline());
							}
							userAward.setDeadline(DateUtils.getSpecifiedMonthAfter(userAward.getCreateTime(), day));
							userAward.setState(UserAwardService.AWARD_WAITING);//待下单
						} else if(awardInfo.getIsTrue().equals("1")){
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
								vouchersHistory.setUserId(userInfo.getId());
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
									logger.info("{用户}"+userInfo.getId()+"兑奖发放{"+vouchersDic.getAmount().toString()+"}元抵用劵成功");
									userAward.setUpdateDate(new Date());
									userAward.setState(UserAwardService.AWARD_END);//已结束
									userAward.setVoucherId(vouchersHistory.getId());
								}else{
									userAward.setState(UserAwardService.AWARD_ALREADY);//已下单
								}
							}else{
								userAward.setState(UserAwardService.AWARD_ALREADY);//已下单
							}
						}
							
						userAward.setneedAmount(0);
						userAwardService.save(userAward);
						map.put("isTrue", awardInfo.getIsTrue());
						map.put("deadline", awardInfo.getDeadline());
					}
					
					// 积分账户减去本次抽奖消耗的积分
					Integer score = userBounsPoint.getScore();
					Integer score1 = score - UserBounsPointService.USER_DRAW_LOTTERY_BOUNS;
					userBounsPoint.setScore(score1);
					userBounsPoint.setUpdateDate(new Date());
					userBounsPointService.update(userBounsPoint);
					map.put("score", userBounsPoint.getScore().toString());
					
					
					// 添加积分历史明细记录
					UserBounsHistory bounsHistory = new UserBounsHistory();
					bounsHistory.setId(IdGen.uuid());
					bounsHistory.setUserId(userInfo.getId());
					bounsHistory.setAmount( - UserBounsPointService.USER_DRAW_LOTTERY_BOUNS.doubleValue());
					bounsHistory.setCreateDate(new Date());
					bounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW);
					bounsHistory.setTransId(userAward.getId() == null ? "thanks" : userAward.getId());
					bounsHistory.setCurrentAmount(score1.toString());
					userBounsHistoryService.insert(bounsHistory);
					
				} else {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}
			
			i = i -1;
			//剩余抽奖次数
			map.put("drawLotteryNum", i.toString());
			
			result.put("state","0");
			result.put("message", "用户抽奖成功");
			result.put("data", map);
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
		}
		return result;
	}
	
	
	// 获取抽奖产品列表（id， name, odds）
	public Object[][] getAwardsObj(){
		Object[][] awardObj = new Object[8][];
		AwardInfo award = new AwardInfo();
		award.setIsLottery("1");
		List<AwardInfo> list = awardInfoService.findList(award);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = new Object[3];
				award = list.get(i);
				obj[0] = award.getId();
				obj[1] = award.getName();
				obj[2] = award.getOdds();
				awardObj[i] = obj;
			}
		}
		return awardObj;
	}
	
	//抽奖并返回角度和奖项
	public Map<String, String> award(Object[][] prizeArr){
		Map<String, String> map = new HashMap<String, String>();
		
		//概率数组
		Integer obj[] = new Integer[prizeArr.length];
		for(int i = 0; i < prizeArr.length; i++){
			Double oddInstance = Double.parseDouble((String)prizeArr[i][2]) * 100;
			int odd = (int)oddInstance.doubleValue();;
			obj[i] = odd;
		}
		
		Integer prizeId = getRand(obj); //根据概率获取奖项id
		
		map.put("awardId", (String) prizeArr[prizeId][0]);
		map.put("awardName", (String) prizeArr[prizeId][1]);
			
		return map;
	}
	
	
	
	
	//根据概率获取奖项
	public Integer getRand(Integer obj[]){
		Integer result = null;
		try {
			int  sum = 0;					//概率数组的总概率精度 
			for(int i = 0; i < obj.length; i++){
				sum += obj[i];
			}
			for(int i = 0; i < obj.length; i++){						//概率数组循环 
				int randomNum = new Random().nextInt(sum);		//随机生成1到sum的整数
				System.out.println(randomNum + "___" + i);
				if(randomNum<obj[i]){//中奖
					result = i;
					break;
				}else{
					sum -=obj[i];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		Object[][] prizeArr = new  Object[][]{
				{0, "酸奶机", 1},
				{1, "酸奶机", 1},
				{2, "50元抵用券", 31},
				{3, "京东购物卡100元", 1},
				{4, "100元抵用券", 31},
				{5, "电话充值卡50元", 1},
				{6, "20元抵用券", 21},
				{7, "品胜20000毫安充电宝", 1},
				{8, "10元抵用券", 13}
			};
			
	}
	
	/**
	 * 抽奖次数
	 * @param request
	 * @return
	 */
	@POST
	@Path("/userDrawLotteryNum")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> userDrawLotteryNum( @FormParam("from")String from, @FormParam("token")String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		int i = 10;
		try {
			//判断必要参数是否为空
			if (StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}
			
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			
			UserInfo userInfo = null;
			if (!StringUtils.isBlank(jedisUserId)) {
				userInfo = userInfoService.getCgb(jedisUserId);
				if(userInfo == null){
					userInfo = userInfoService.get(jedisUserId);
				}
				if (userInfo != null) {
					UserBounsHistory bounsHistory = new UserBounsHistory();
					bounsHistory.setUserId(userInfo.getId());
					bounsHistory.setBeginCreateDate(DateUtils.getDate());
					bounsHistory.setEndCreateDate(DateUtils.getSpecifiedDayAfter(DateUtils.getDate()));
					bounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW);
					List<UserBounsHistory> list = userBounsHistoryService.findList(bounsHistory);
					if(list!=null && list.size()>0){
						i = i - list.size();
					}
				} else {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}
			
			result.put("state","0");
			result.put("message", "用户抽奖次数获取成功");
			result.put("num", i);
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
		}
		return result;
	}
}
