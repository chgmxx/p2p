package com.power.platform.bouns.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.bouns.dao.AwardInfoDao;
import com.power.platform.bouns.entity.AwardInfo;
import com.power.platform.bouns.entity.UserAward;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.entity.UserConsigneeAddress;
import com.power.platform.cache.Cache;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 奖品信息接口
 * 
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/awardInfo")
@Service("awardInforService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AwardInforService {

	@Autowired
	private AwardInfoService awardInfoService;
	@Autowired
	private UserBounsPointService userBounsPointService;
	@Autowired
	private UserAwardService userAwardService;
	@Autowired
	private UserConsigneeAddressService userConsigneeAddressService;
	@Autowired
	private UserBounsHistoryService userBounsHistoryService;
	@Autowired
	private AwardInfoDao awardInfoDao;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserVouchersHistoryDao userVouchersHistoryDao;

	/**
	 * 奖品信息列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param isLottery
	 *            是否参加抽奖 1-是,0-否,2-全部
	 * @return
	 */
	@POST
	@Path("/getAwardInfoList")
	public Map<String, Object> awardInfoList(@FormParam("from") String from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("isLottery") String isLottery) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			AwardInfo awardInfo = new AwardInfo();
			Page<AwardInfo> page = new Page<AwardInfo>();
			page.setPageNo(pageNo);
			page.setPageSize(pageSize);
			page.setOrderBy("need_amount ASC");
			awardInfo.setState("0");// 状态 0-上架
			Page<AwardInfo> awardInfoPage = null;
			if (!isLottery.equals("2")) {
				awardInfo.setIsLottery(isLottery);// 抽奖
				awardInfoPage = awardInfoService.findPage(page, awardInfo);
			} else {
				awardInfo.setIsTrue("1");// 奖品列表显示时去掉谢谢惠顾
				// 针对奖品列表是否显示谢谢惠顾 isTrue=1时 不显示
				awardInfoPage = awardInfoService.findPage1(page, awardInfo);
				result.put("islock", "0");
			}

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<AwardInfo> awardlist = awardInfoPage.getList();
			if (awardlist != null && awardlist.size() > 0) {
				for (AwardInfo entity : awardlist) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("awardId", entity.getId());// 奖品ID
					map.put("name", entity.getName());// 奖品名称
					map.put("needAmount", entity.getNeedAmount());// 奖品积分
					map.put("docs", entity.getDocs());// 奖品简介
					map.put("imgWeb", Global.getConfig("img_new_path") + imgUrl(entity.getImgWeb()).get(0)); // 移动端图片
					map.put("imgWap", Global.getConfig("img_new_path") + imgUrl(entity.getImgWap()).get(0)); // 电脑端图片
					list.add(map);
				}
			}

			data.put("awardlist", list);
			data.put("pageNo", awardInfoPage.getPageNo());
			data.put("pageSize", awardInfoPage.getPageSize());
			data.put("totalCount", awardInfoPage.getCount());
			data.put("last", awardInfoPage.getLast());
			data.put("pageCount", awardInfoPage.getLast());

			result.put("state", "0");
			result.put("message", "奖品信息查询成功");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			result.put("data", null);
			return result;
		}

	}
	
	/**
	 * 奖品信息列表---改版
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param isTrue 0 实体  1虚拟
	 * @return
	 */
	@POST
	@Path("/getNewAwardInfoList")
	public Map<String, Object> getNewAwardInfoList(@FormParam("from") String from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("isTrue") String isTrue) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			AwardInfo awardInfo = new AwardInfo();
			Page<AwardInfo> page = new Page<AwardInfo>();
			page.setPageNo(pageNo);
			page.setPageSize(pageSize);
			page.setOrderBy("need_amount ASC");
			awardInfo.setState("0");// 状态 0-上架
			Page<AwardInfo> awardInfoPage = null;
			awardInfo.setIsTrue(isTrue);
			awardInfoPage = awardInfoService.findPage(page, awardInfo);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<AwardInfo> awardlist = awardInfoPage.getList();
			if (awardlist != null && awardlist.size() > 0) {
				for (AwardInfo entity : awardlist) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("awardId", entity.getId());// 奖品ID
					map.put("name", entity.getName());// 奖品名称
					map.put("needAmount", entity.getNeedAmount());// 奖品积分
					map.put("docs", entity.getDocs());// 奖品简介
					map.put("imgWeb", Global.getConfig("img_new_path") + imgUrl(entity.getImgWeb()).get(0)); // 移动端图片
					map.put("imgWap", Global.getConfig("img_new_path") + imgUrl(entity.getImgWap()).get(0)); // 电脑端图片
					list.add(map);
				}
			}

			data.put("awardlist", list);
			data.put("pageNo", awardInfoPage.getPageNo());
			data.put("pageSize", awardInfoPage.getPageSize());
			data.put("totalCount", awardInfoPage.getCount());
			data.put("last", awardInfoPage.getLast());
			data.put("pageCount", awardInfoPage.getLast());

			result.put("state", "0");
			result.put("message", "奖品信息查询成功");
			result.put("data", data);
			result.put("islock", "0");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			result.put("data", null);
			return result;
		}

	}

	
	/**
	 * 奖品信息列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param isLottery
	 *            是否参加抽奖 1-是,0-否,2-全部
	 * @return
	 */
	@POST
	@Path("/getAwardInfoListForPC")
	public Map<String, Object> awardInfoListForPC(@FormParam("from") String from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("isLottery") String isLottery,@FormParam("orderByType")String orderByType,@FormParam("needAbout")String needAbout) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			AwardInfo awardInfo = new AwardInfo();
			Page<AwardInfo> page = new Page<AwardInfo>();
			page.setPageNo(pageNo);
			page.setPageSize(pageSize);
			if(orderByType==null || orderByType.trim().equals("")){
				page.setOrderBy("need_amount ASC");
			}else{
				page.setOrderBy("creat_time DESC");
			}
			if(needAbout==null || needAbout.trim().equals("")){
				
			}else{
				awardInfo.setNeedAmount(needAbout);
			}
			awardInfo.setState("0");// 状态 0-上架
			Page<AwardInfo> awardInfoPage = null;
			if (!isLottery.equals("2")) {
				awardInfo.setIsLottery(isLottery);// 抽奖
				awardInfoPage = awardInfoService.findPage(page, awardInfo);
			} else {
				awardInfo.setIsTrue("1");// 奖品列表显示时去掉谢谢惠顾
				// 针对奖品列表是否显示谢谢惠顾 isTrue=1时 不显示
				awardInfoPage = awardInfoService.findPage2(page, awardInfo);
			}

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<AwardInfo> awardlist = awardInfoPage.getList();
			if (awardlist != null && awardlist.size() > 0) {
				for (AwardInfo entity : awardlist) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("awardId", entity.getId());// 奖品ID
					map.put("name", entity.getName());// 奖品名称
					map.put("needAmount", entity.getNeedAmount());// 奖品积分
					map.put("docs", entity.getDocs());// 奖品简介
					map.put("imgWeb", Global.getConfig("img_new_path") + imgUrl(entity.getImgWeb()).get(0)); // 移动端图片
					map.put("imgWap", Global.getConfig("img_new_path") + imgUrl(entity.getImgWap()).get(0)); // 电脑端图片
					list.add(map);
				}
			}

			data.put("awardlist", list);
			data.put("pageNo", awardInfoPage.getPageNo());
			data.put("pageSize", awardInfoPage.getPageSize());
			data.put("totalCount", awardInfoPage.getCount());
			data.put("last", awardInfoPage.getLast());
			data.put("pageCount", awardInfoPage.getLast());

			result.put("state", "0");
			result.put("message", "奖品信息查询成功");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			result.put("data", null);
			return result;
		}

	}
	
	/**
	 * 奖品详细信息
	 * 
	 * @param from
	 * @param awardId
	 * @return
	 */
	@POST
	@Path("/getAwardInfo")
	public Map<String, Object> awardInfo(@FormParam("from") String from, @FormParam("awardId") String awardId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		AwardInfo awardInfo = null;
		if (!StringUtils.isBlank(awardId)) {
			try {
				awardInfo = awardInfoService.get(awardId);
				data.put("awardId", awardInfo.getId()); // 奖品ID
				data.put("needAmount", awardInfo.getNeedAmount()); // 奖品积分
				data.put("state", awardInfo.getState()); // 奖品状态
				data.put("name", awardInfo.getName()); // 奖品名称
				data.put("docs", awardInfo.getDocs()); // 奖品简介
				data.put("imgWeb", Global.getConfig("img_new_path") + imgUrl(awardInfo.getImgWeb()).get(0)); // 移动端图片
				data.put("imgWap", Global.getConfig("img_new_path") + imgUrl(awardInfo.getImgWap()).get(0)); // 电脑端图片
				data.put("isLottery", awardInfo.getIsLottery()); // 是否参加抽奖
				data.put("isTrue", awardInfo.getIsTrue()); // 是否虚拟奖品
				data.put("odds", awardInfo.getOdds()); // 中奖概率
				data.put("awardStandard", awardInfo.getAwardStandard());// 奖品规格
				data.put("exchangeFlow", awardInfo.getExchangeFlow());// 兑换流程
				data.put("exchangeDocs", awardInfo.getExchangeDocs());// 兑换说明

				result.put("state", "0");
				result.put("message", "奖品详细信息查询成功");
				result.put("data", data);
			} catch (Exception e) {
				result.put("state", "1");
				result.put("message", "系统异常");
				result.put("data", null);
				return result;
			}

		} else {
			result.put("state", "2");
			result.put("message", "奖品Id为空，缺少参数");
			result.put("data", null);
		}

		return result;
	}

	/**
	 * 获得用户已兑换或者已抽取奖品信息
	 * 
	 * @param from
	 * @param awardId
	 * @return
	 */
	@POST
	@Path("/getUserAwardInfo")
	public Map<String, Object> getUserAwardInfo(@FormParam("from") String from, @FormParam("token") String token, @FormParam("userAwardId") String userAwardId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		UserAward userAward = new UserAward();
		UserBounsPoint userBouns = new UserBounsPoint();
		userAward.setId(userAwardId);
		if (!StringUtils.isBlank(from) && !StringUtils.isBlank(token) && !StringUtils.isBlank(userAwardId)) {
			try {
				// 从缓存获取用户信息
				String jedisUserId = JedisUtils.get(token);
				if(!StringUtils.isBlank(jedisUserId)){
					UserInfo user = userInfoService.getCgb(jedisUserId);
					if(user==null){
						user = userInfoService.get(jedisUserId);
					}
					if (user != null) {
						userBouns.setUserId(user.getId());
						List<UserBounsPoint> listUserBouns = userBounsPointService.findList(userBouns);
						if (listUserBouns != null && listUserBouns.size() > 0) {
							userBouns = listUserBouns.get(0);
							data.put("userAvailableBouns", userBouns.getScore());
						}

						userAward = userAwardService.get(userAward);

						if (userAward != null) {
							// 用户兑换奖品信息
							data.put("userAwardId", userAward.getId());
							data.put("awardId", userAward.getAwardId());
							data.put("state", userAward.getState());
							data.put("realNeedAmount", userAward.getneedAmount());
							data.put("expressNo", userAward.getExpressNo());
							data.put("expressName", userAward.getExpressName());
							data.put("addressId", userAward.getAddressId());
							data.put("deadline", userAward.getDeadline()==null?"":DateUtils.formatDateTime(userAward.getDeadline()));

							// 奖品信息（公共）
							data.put("awardName", userAward.getAwardInfo().getName());
							data.put("awardDocs", userAward.getAwardInfo().getDocs());
							data.put("awardNeedAmount", userAward.getAwardInfo().getNeedAmount());
							data.put("awardIsTrue", userAward.getAwardInfo().getIsTrue());
							data.put("awardImgWap", Global.getConfig("img_new_path") + imgUrl(userAward.getAwardInfo().getImgWap()).get(0));
							data.put("awardImgWeb", Global.getConfig("img_new_path") + imgUrl(userAward.getAwardInfo().getImgWeb()).get(0));

							result.put("data", data);
						}
					}
				}else {
					throw new Exception("系统超时");
				}

				result.put("state", "0");
				result.put("message", "奖品详细信息查询成功");
				result.put("data", data);
			} catch (Exception e) {
				result.put("state", "1");
				result.put("message", "系统异常");
				return result;
			}
		} else {
			result.put("state", "2");
			result.put("message", "缺少参数");
		}

		return result;
	}

	/**
	 * 用户兑换奖品
	 * 
	 * @param from
	 * @param token
	 * @param awardId
	 * @param needAmount
	 * @return
	 */
	@POST
	@Path("/awardToUser")
	public Map<String, Object> awardToUser(@FormParam("from") String from, @FormParam("token") String token, @FormParam("awardId") String awardId, @FormParam("needAmount") Integer needAmount) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(awardId) || StringUtils.isBlank(token) || from == null) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if(!StringUtils.isBlank(jedisUserId)){
				UserInfo user = userInfoService.getCgb(jedisUserId);
				if(user==null){
					user = userInfoService.get(jedisUserId);
				}
				if (user == null) {
					throw new Exception("用户登录信息错误，请重新登录");
				} else {
					// 查询用户积分
					Integer userBouns = 0;
					UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(user.getId());
					AwardInfo awardInfo = awardInfoDao.get(awardId);
					Integer awardNeedAmount = 0;
					if (userBounsPoint != null) {
						userBouns = userBounsPoint.getScore();
						if(awardInfo!=null){
							awardNeedAmount = Integer.valueOf(awardInfo.getNeedAmount());
						}
						if(userBouns - awardNeedAmount < 0 ){
							throw new WinException("用户积分不足");
						}
						// 判断用户积分是否可兑换奖品
						if (needAmount < 0) { // 奖品所需积分不能为负数.
							throw new WinException("奖品所需积分不能为负数");
						} else if (userBouns - needAmount >= 0) {
							// 兑换奖品
							data = awardInfoService.insertawardToUser(user, awardId, needAmount, userBounsPoint);
						} else {
							throw new WinException("用户积分不足");
						}
					}

				}
			}else{
				throw new Exception("用户登录信息错误，请重新登录");
			}

			if (data != null) {
				result.put("state", "0");
				result.put("message", "奖品兑换成功");
				result.put("data", data);
			} else {
				result.put("state", "1");
				result.put("message", "奖品兑换失败");
				result.put("data", null);
			}
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}

	/**
	 * 用户兑换奖品列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @return
	 */
	@POST
	@Path("/userAwardList")
	public Map<String, Object> userAwardList(@FormParam("from") String from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("token") String token, @FormParam("type") String type) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(token) || from == null) {
				result.put("state", "2");
				result.put("message", "缺少参数");
				result.put("data", null);
			} else {
				// 从缓存获取用户信息
				String jedisUserId = JedisUtils.get(token);
				
				if(!StringUtils.isBlank(jedisUserId)){
					UserInfo user = userInfoService.getCgb(jedisUserId);
					if(user==null){
						user = userInfoService.get(jedisUserId);
					}
					UserAward userAward = new UserAward();
					Page<UserAward> page = new Page<UserAward>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					page.setOrderBy("a.state ASC,a.create_time desc");
					userAward.setUserId(user.getId());
					if(type!=null &&!StringUtils.isBlank(type)){
						AwardInfo awardInfo = new AwardInfo();
						awardInfo.setIsTrue(type);
						userAward.setAwardInfo(awardInfo);
					}
					Page<UserAward> userAwardPage = userAwardService.findPage(page, userAward);
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					List<UserAward> userAwardlist = userAwardPage.getList();

					if (userAwardlist != null && userAwardlist.size() > 0) {
						for (int i = 0; i < userAwardlist.size(); i++) {
							Map<String, Object> map = new HashMap<String, Object>();
							userAward = userAwardlist.get(i);

							UserConsigneeAddress address = null;
							String userAddressInfo = "";
							if (userAward.getAddressId() != null && !userAward.getAddressId().equals("")) {
								address = userConsigneeAddressService.get(userAward.getAddressId());
								userAddressInfo = address.getProvince().getName() + " " + address.getCity().getName() + " " + address.getAddress();
							}

							map.put("awardName", userAward.getAwardInfo().getName());// 奖品名称
							map.put("awardNeedAmount", userAward.getneedAmount());// 奖品积分
							map.put("awardDate", DateUtils.formatDate(userAward.getCreateTime(), "yyyy-MM-dd"));// 奖品领取时间
							map.put("awardimgWap", Global.getConfig("img_new_path") + imgUrl(userAward.getAwardInfo().getImgWap()).get(0));// 电脑端图片
							map.put("awardimgWeb", Global.getConfig("img_new_path") + imgUrl(userAward.getAwardInfo().getImgWeb()).get(0));// 移动端图片
							map.put("awardId", userAward.getAwardInfo().getId());// 奖品ID
							map.put("isTrue", userAward.getAwardInfo().getIsTrue());// 是否为虚拟奖品
							map.put("userAddress", userAddressInfo);// 收货地址
							
							if(userAward.getVoucherId()!=null && !userAward.getVoucherId().equals("")){
								UserVouchersHistory userVoucher = userVouchersHistoryDao.get(userAward.getVoucherId());
								if(userVoucher!=null){
									map.put("state", userVoucher.getState());
									map.put("deadline", userVoucher.getOverdueDate()==null?"":DateUtils.formatDate(userVoucher.getOverdueDate(), "yyyy-MM-dd"));
								}else{
									map.put("state", userAward.getState());
									map.put("deadline", userAward.getDeadline()==null?"":DateUtils.formatDate(userAward.getDeadline(), "yyyy-MM-dd"));
								}
							}else{
								map.put("state", userAward.getState());
								map.put("deadline", userAward.getDeadline()==null?"":DateUtils.formatDate(userAward.getDeadline(), "yyyy-MM-dd"));
							}
							map.put("expressNo", userAward.getExpressNo());
							map.put("myAwardId", userAward.getId());
							map.put("docs", userAward.getAwardInfo().getDocs());
							list.add(map);
						}
					}

					data.put("awardlist", list);
					data.put("pageNo", userAwardPage.getPageNo());
					data.put("pageSize", userAwardPage.getPageSize());
					data.put("totalCount", userAwardPage.getCount());
					data.put("last", userAwardPage.getLast());
					data.put("pageCount", userAwardPage.getLast());

					result.put("state", "0");
					result.put("message", "奖品信息查询成功");
					result.put("data", data);
					return result;
				}
				


			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 用户确认兑换
	 * 
	 * @param from
	 * @param token
	 * @param awardId
	 * @param needAmount
	 * @param addressId
	 * @return
	 */
	@POST
	@Path("/myAwardInfo")
	public Map<String, Object> myAwardInfo(@FormParam("from") String from, @FormParam("token") String token, @FormParam("myAwardId") String myAwardId, @FormParam("addressId") String addressId, @FormParam("needAmount") Integer needAmount) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(myAwardId) || StringUtils.isBlank(token) || StringUtils.isBlank(from)) {
				result.put("state", "2");
				throw new Exception("缺少参数必要参数");
			}
			// 从缓存获取用户信息
			String jedisUserId = JedisUtils.get(token);
			if(!StringUtils.isBlank(jedisUserId)){
				UserInfo user = userInfoService.getCgb(jedisUserId);
				if(user==null){
					user = userInfoService.get(jedisUserId);
				}
				int i = 0;
				if (user == null) {
					throw new Exception("用户登录信息错误，请重新登录");
				} else {
					// N1.查询用户积分
					Integer userBouns = 0;
					UserBounsPoint userBounsPoint = userBounsPointService.getUserBounsPoint(user.getId());
					UserAward userAward = userAwardService.get(myAwardId);
					String awardInfoId = "";
					Integer awardNeedAmount = 0;
					if (userBounsPoint != null) {
						userBouns = userBounsPoint.getScore();
						if(userAward.getneedAmount().intValue() != 0){
							if(userAward!=null){
								awardInfoId = userAward.getAwardId();
							}
							AwardInfo awardInfo = awardInfoDao.get(awardInfoId);
							if(awardInfo!=null){
								awardNeedAmount = Integer.valueOf(awardInfo.getNeedAmount());
							}
							if(userBouns - awardNeedAmount < 0 ){
								throw new WinException("用户积分不足");
							}
						}
						// N2.判断用户奖品是否已经过了失效时间
						Date deadline = userAward.getDeadline();
						Date now = new Date();
						if(!DateUtils.compare_date(DateUtils.formatDateTime(now), DateUtils.formatDateTime(deadline))){
							throw new WinException("该商品已经过了兑换日期");
						}
						// N3.判断用户积分是否可兑换奖品
						Integer score = userBouns - needAmount;
						if (score >= 0) {
							// 兑换奖品
							i = awardInfoService.updateAwardToUser(user, myAwardId, needAmount, userBounsPoint, addressId);
							
						} else {
							throw new WinException("用户积分不足");
						}
					}

				}
				if (i > 0) {
					result.put("state", "0");
					result.put("message", "奖品领取成功");
				} else {
					result.put("state", "1");
					result.put("message", "奖品领取失败");
				}
			}else{
				throw new Exception("用户登录信息错误，请重新登录");
			}
			return result;
		} catch (WinException e) {
			e.printStackTrace();
			result.put("message", e.getMessage());
			result.put("state", "3");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			return result;
		}
	}

	/**
	 * 拆分图片URL
	 * 
	 * @param urlStr
	 * @return
	 */
	public List<String> imgUrl(String urlStr) {

		List<String> urlList = null;
		urlList = new ArrayList<String>();
		if (urlStr != null && urlStr != "") {
			String urlArr[] = urlStr.split("\\|");
			for (int i = 1; i < urlArr.length; i++) {
				urlList.add(urlArr[i]);
			}
		}
		return urlList;
	}

	/**
	 * 用户抽取奖品列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @return
	 */
	@POST
	@Path("/userBounsList")
	public Map<String, Object> userBounsList(@FormParam("from") String from,@FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(from)) {
				result.put("state", "2");
				result.put("message", "缺少参数");
				result.put("data", null);
			} else {
				UserBounsHistory userBounsHistory = new UserBounsHistory();
				if(pageNo!=null && pageSize!=null){
					Page<UserBounsHistory> page = new Page<UserBounsHistory>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					page.setOrderBy(" create_time DESC");
					userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW);
					Page<UserBounsHistory> userBounsHistoryPage = userBounsHistoryService.findPage1(page, userBounsHistory);
					List<UserBounsHistory> userBounsHistoryList = userBounsHistoryPage.getList();
					if (userBounsHistoryList != null && userBounsHistoryList.size() > 0) {
						for (int i = 0; i < userBounsHistoryList.size(); i++) {
							Map<String, Object> map = new HashMap<String, Object>();
							userBounsHistory = userBounsHistoryList.get(i);
							if (userBounsHistory.getAwardInfo() != null) {
								map.put("awardName", userBounsHistory.getAwardInfo().getName());// 奖品名称
								if (userBounsHistory.getUserInfo() != null) {
									map.put("userPhone", Util.hideString(userBounsHistory.getUserInfo().getName(), 3, 4));// 获奖手机号
								} else {
									map.put("userPhone", "111****1111");// 获奖手机号
								}
								list.add(map);
							}
						}
					}
				}else{
					userBounsHistory.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW);
					List<UserBounsHistory> userBounsHistoryList = userBounsHistoryService.findBounsHistoryList(userBounsHistory);
					if (userBounsHistoryList != null && userBounsHistoryList.size() > 0) {
						for (int i = 0; i < userBounsHistoryList.size(); i++) {
							Map<String, Object> map = new HashMap<String, Object>();
							userBounsHistory = userBounsHistoryList.get(i);
							if (userBounsHistory.getAwardInfo() != null) {
								map.put("awardName", userBounsHistory.getAwardInfo().getName());// 奖品名称
								if (userBounsHistory.getUserInfo() != null) {
									map.put("userPhone", Util.hideString(userBounsHistory.getUserInfo().getName(), 3, 4));// 获奖手机号
								} else {
									map.put("userPhone", "111****1111");// 获奖手机号
								}
								list.add(map);
							}
						}
					}
				}
				data.put("awardlist", list);
				result.put("state", "0");
				result.put("message", "获奖信息列表查询成功");
				result.put("data", data);
				return result;

			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "1");
			result.put("data", null);
			return result;
		}
		return result;
	}

	/**
	 * 用户兑换奖品列表---区分兑换,抽奖
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @param awardGetType
	 * @return
	 */
	@POST
	@Path("/newUserAwardList")
	public Map<String, Object> newUserAwardList(@FormParam("from") String from, @FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("token") String token, @FormParam("awardGetType") String awardGetType, @FormParam("isTrue") String isTrue) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			// 判断必要参数是否为空
			if (StringUtils.isBlank(token) || from == null) {
				result.put("state", "2");
				result.put("message", "缺少参数");
				result.put("data", null);
			} else {
				// 从缓存获取用户信息
				String jedisUserId = JedisUtils.get(token);
				
				if(!StringUtils.isBlank(jedisUserId)){
					UserInfo user = userInfoService.getCgb(jedisUserId);
					if(user==null){
						user = userInfoService.get(jedisUserId);
					}
					UserAward userAward = new UserAward();
					Page<UserAward> page = new Page<UserAward>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					page.setOrderBy("a.state ASC,a.create_time desc");
					userAward.setUserId(user.getId());
					if(awardGetType!=null &&!StringUtils.isBlank(awardGetType)){
						userAward.setAwardGetType(awardGetType);
					}
					
					if(isTrue!=null &&!StringUtils.isBlank(isTrue)){
						AwardInfo awardInfo = new AwardInfo();
						awardInfo.setIsTrue(isTrue);
						userAward.setAwardInfo(awardInfo);
					}
					
					Page<UserAward> userAwardPage = userAwardService.findPage2(page, userAward);
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					List<UserAward> userAwardlist = userAwardPage.getList();

					if (userAwardlist != null && userAwardlist.size() > 0) {
						for (int i = 0; i < userAwardlist.size(); i++) {
							Map<String, Object> map = new HashMap<String, Object>();
							userAward = userAwardlist.get(i);

							UserConsigneeAddress address = null;
							String userAddressInfo = "";
							if (userAward.getAddressId() != null && !userAward.getAddressId().equals("")) {
								address = userConsigneeAddressService.get(userAward.getAddressId());
								userAddressInfo = address.getProvince().getName() + " " + address.getCity().getName() + " " + address.getAddress();
							}

							map.put("awardName", userAward.getAwardInfo().getName());// 奖品名称
							map.put("awardNeedAmount", userAward.getneedAmount());// 奖品积分
							map.put("awardDate", DateUtils.formatDate(userAward.getCreateTime(), "yyyy-MM-dd"));// 奖品领取时间
							map.put("awardimgWap", Global.getConfig("img_new_path") + imgUrl(userAward.getAwardInfo().getImgWap()).get(0));// 电脑端图片
							map.put("awardimgWeb", Global.getConfig("img_new_path") + imgUrl(userAward.getAwardInfo().getImgWeb()).get(0));// 移动端图片
							map.put("awardId", userAward.getAwardInfo().getId());// 奖品ID
							map.put("isTrue", userAward.getAwardInfo().getIsTrue());// 是否为虚拟奖品
							map.put("userAddress", userAddressInfo);// 收货地址
							
							if(userAward.getVoucherId()!=null && !userAward.getVoucherId().equals("")){
								UserVouchersHistory userVoucher = userVouchersHistoryDao.get(userAward.getVoucherId());
								if(userVoucher!=null){
									map.put("state", userVoucher.getState());
									map.put("deadline", userVoucher.getOverdueDate()==null?"":DateUtils.formatDate(userVoucher.getOverdueDate(), "yyyy-MM-dd"));
								}else{
									map.put("state", userAward.getState());
									map.put("deadline", userAward.getDeadline()==null?"":DateUtils.formatDate(userAward.getDeadline(), "yyyy-MM-dd"));
								}
							}else{
								map.put("state", userAward.getState());
								map.put("deadline", userAward.getDeadline()==null?"":DateUtils.formatDate(userAward.getDeadline(), "yyyy-MM-dd"));
							}
							map.put("expressNo", userAward.getExpressNo());
							map.put("myAwardId", userAward.getId());
							map.put("docs", userAward.getAwardInfo().getDocs());
							list.add(map);
						}
					}

					data.put("awardlist", list);
					data.put("pageNo", userAwardPage.getPageNo());
					data.put("pageSize", userAwardPage.getPageSize());
					data.put("totalCount", userAwardPage.getCount());
					data.put("last", userAwardPage.getLast());
					data.put("pageCount", userAwardPage.getLast());

					result.put("state", "0");
					result.put("message", "奖品信息查询成功");
					result.put("data", data);
					return result;
				}
				


			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "2");
			result.put("data", null);
			return result;
		}
		return result;
	}
}
