package com.power.platform.invest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtil;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.trandetail.service.UserTransDetailService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 
 * 用户交易流水信息service
 * 
 * @author Mr.Jia
 * 
 */

@Path("/trans")
@Service("transDetailsService")
@Produces(MediaType.APPLICATION_JSON)
public class TransDetailsService {

	/**
	 * PC.
	 */
	private final static String FROM_1 = "1";
	/**
	 * WAP.
	 */
	private final static String FROM_2 = "2";
	/**
	 * ANDROID.
	 */
	private final static String FROM_3 = "3";
	/**
	 * IOS.
	 */
	private final static String FROM_4 = "4";
	/**
	 * 4：个人中心改版之后，代表交易记录中的回款类型
	 */
	private final static String TYPE_4 = "4";

	@Autowired
	private UserTransDetailService userTransDetailService;
	@Autowired
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	/*
	 * 银行存管
	 */
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 
	 * methods: findTradeBatchDetailList <br>
	 * description: 批量查找交易列表详情. <br>
	 * author: Mr.Roy <br>
	 * date: 2018年12月18日 下午3:08:19
	 * 
	 * @param token
	 *            用户登陆唯一标识
	 * @param from
	 *            访问来源
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页展示记录数
	 * @param type
	 *            交易类型
	 * @param beginDate
	 *            交易流水开始时间
	 * @param endDate
	 *            交易流水结束时间
	 * @return
	 */
	@POST
	@Path("/findTradeBatchDetailList")
	public Map<String, Object> findTradeBatchDetailList(@FormParam("token") String token, @FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("type") String type, @FormParam("beginDate") String beginDate, @FormParam("endDate") String endDate) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(type)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			result.put("data", null);
			return result;
		}

		// 数据源.
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String userId = "-1";
		try {

			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				userId = jedisUserId;
			}

			CgbUserTransDetail details = new CgbUserTransDetail();
			if (Integer.valueOf(type) != -1) {
				if ((from.equals(FROM_2) || from.equals(FROM_3) || from.equals(FROM_4)) && (Integer.valueOf(type).equals(UserTransDetail.TRANS_INTEREST) || Integer.valueOf(type).equals(UserTransDetail.TRANS_PRINCIPAL))) {
					List<Integer> types = new ArrayList<Integer>();
					types.add(UserTransDetail.TRANS_INTEREST);
					types.add(UserTransDetail.TRANS_PRINCIPAL);
					details.setTranstypes(types);
				} else if (from.equals(FROM_1)) { // PC请求
					if (TYPE_4.equals(type)) { // 如果是回款查询，包含了还本和付息
						List<Integer> types = new ArrayList<Integer>();
						types.add(UserTransDetail.TRANS_INTEREST);
						types.add(UserTransDetail.TRANS_PRINCIPAL);
						details.setTranstypes(types);
					} else {
						// 3：出借，0：充值，1：提现，4：回款，10：优惠券
						details.setTrustType(Integer.valueOf(type));
						if (UserTransDetail.TRANS_CASH.equals(Integer.valueOf(type))) { // 提现
							details.setState(UserTransDetail.TRANS_STATE_SUCCESS); // 提现只展示成功的流水
						}
					}
				}
			}
			details.setUserId(userId);
			if (beginDate != null && beginDate.trim().length() > 0) {
				details.setBeginTransDate(DateUtil.getTextDate(beginDate.concat(" 00:00:00"), "yyyy-MM-dd HH:mm:ss"));
			}
			if (endDate != null && endDate.trim().length() > 0) {
				details.setEndTransDate(DateUtil.getTextDate(endDate.concat(" 23:59:59"), "yyyy-MM-dd HH:mm:ss"));
			}
			// details.setState(UserTransDetail.TRANS_STATE_SUCCESS);

			Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));

			Page<CgbUserTransDetail> resultPage = cgbUserTransDetailService.findPage(page, details);

			if (resultPage != null && resultPage.getList().size() > 0) {
				List<CgbUserTransDetail> transList = resultPage.getList();
				for (int i = 0; i < transList.size(); i++) {
					details = transList.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("transTypeStr", details.getTrustTypeStr()); // 交易类型
					map.put("transType", details.getTrustType()); // 交易类型
					if (UserTransDetail.TRANS_CASH.equals(details.getTrustType())) {
						if (UserTransDetail.TRANS_STATE_DOING.equals(details.getState())) {
							continue;
						}
					}
					// 交易时间，yyyy-MM-dd
					map.put("trandDate", DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd"));
					// 交易时间，yyyy-MM-dd HH:mm:ss
					map.put("trandDateTime", DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd HH:mm:ss"));
					map.put("surplusMoney", NumberUtils.scaleDoubleStr(details.getAvaliableAmount())); // 剩余金额
					map.put("transactionAmount", NumberUtils.scaleDoubleStr(details.getAmount())); // 交易金额
					map.put("status", details.getStateStr()); // 流水状态
					map.put("state", details.getState()); // 流水状态
					map.put("remark", details.getRemarks()); // 流水备注
					WloanTermInvest invest = null;
					String projectid = "";
					// 出借查询
					if (UserTransDetail.TRANS_TERM.equals(details.getTrustType())) {
						invest = wloanTermInvestDao.get(details.getTransId().trim());
						if (invest != null) {
							projectid = invest.getWloanTermProject().getId() == null ? "" : invest.getWloanTermProject().getId();
						}
					}

					// 用户还款计划
					WloanTermUserPlan plan = null;
					// 还本付息查询
					if (UserTransDetail.TRANS_INTEREST.equals(details.getTrustType())) { // 付息
						plan = wloanTermUserPlanDao.get(details.getTransId().trim());
						if (null != plan) {
							projectid = plan.getWloanTermProject().getId() == null ? "" : plan.getWloanTermProject().getId();
						}
					} else if (UserTransDetail.TRANS_PRINCIPAL.equals(details.getTrustType())) { // 还本
						plan = wloanTermUserPlanDao.get(details.getTransId().trim());
						if (null != plan) {
							projectid = plan.getWloanTermProject().getId() == null ? "" : plan.getWloanTermProject().getId();
						} else { // 还本时，由于transId被截取掉了最后一个字符，所以需要做一些特殊处理
							// 获取还本的交易时间
							DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd");
							// 封装流水查询对象.
							CgbUserTransDetail entity = new CgbUserTransDetail();
							// 当前还本的交易时间，当前日的开始时间
							entity.setBeginTransDate(DateUtils.parseDate(DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd").concat(" 00:00:00"), "yyyy-MM-dd HH:mm:ss"));
							// 当前还本的交易时间，当前日的结束时间
							entity.setEndTransDate(DateUtils.parseDate(DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd").concat(" 23:59:59"), "yyyy-MM-dd HH:mm:ss"));
							// 交易类型，付息
							entity.setTrustType(UserTransDetail.TRANS_INTEREST);
							// 交易流水
							List<CgbUserTransDetail> transInterestList = cgbUserTransDetailService.findList(entity);
							for (int j = 0; j < transInterestList.size(); j++) {
								CgbUserTransDetail transInteresDetail = transInterestList.get(j);
								String transId = transInteresDetail.getTransId().substring(0, transInteresDetail.getTransId().trim().length() - 1);
								if (transId != null) {
									if (transId.equals(details.getTransId().trim())) { // 是否相等
										plan = wloanTermUserPlanDao.get(transInteresDetail.getTransId().trim());
										if (plan != null) {
											projectid = plan.getWloanTermProject().getId() == null ? "" : plan.getWloanTermProject().getId();
										}
									}
								}
							}
						}
					}
					// 项目ID.
					map.put("projectid", projectid);
					// 项目详情.
					WloanTermProject wloanTermProject = wloanTermProjectService.get(projectid);
					if (null != wloanTermProject) {
						map.put("projectType", wloanTermProject.getProjectType()); // 标的类型
						map.put("projectProductType", wloanTermProject.getProjectProductType()); // 标的产品类型
						map.put("projectName", wloanTermProject.getName()); // 标的名称
						map.put("projectSn", wloanTermProject.getSn()); // 标的编号
					} else {
						map.put("projectType", "");
						map.put("projectProductType", "");
						map.put("projectName", "");
						map.put("projectSn", "");
					}
					// 收支（1：收入，2：支出）
					map.put("inOutType", details.getInOutType());
					if (UserTransDetail.TRANS_TYPE_IN.equals(details.getInOutType())) {
						map.put("inOutTypeStr", "收入");
					} else if (UserTransDetail.TRANS_TYPE_OUT.equals(details.getInOutType())) {
						map.put("inOutTypeStr", "支出");
					}
					list.add(map);
				}
			}

			data.put("pageNo", resultPage.getPageNo());
			data.put("pageSize", resultPage.getPageSize());
			data.put("pageCount", resultPage.getCount());
			data.put("last", resultPage.getLast());
			data.put("translist", list);

			result.put("state", "0");
			result.put("message", "交易记录查找成功");
			result.put("data", data);
			result.put("paramType", type);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}
		return result;
	}

	/**
	 * 投资用户交易流水---旧版
	 * 
	 * @param token
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param type
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	@POST
	@Path("/getUserTransDetailList")
	public Map<String, Object> getUserInfo(@FormParam("token") String token, @FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("type") String type, @FormParam("begindate") String begindate, @FormParam("enddate") String enddate) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(type)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			result.put("data", null);
			return result;
		}

		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String userId = "";
		try {
			String jedisUserId = JedisUtils.get(token);

			if (!StringUtils.isBlank(jedisUserId)) {
				UserInfo user = userInfoService.get(jedisUserId);
				if (null != user) {
					userId = user.getId();
				}
			}

			UserTransDetail details = new UserTransDetail();
			if (Integer.valueOf(type) != -1) {
				if ((from.equals(3) || from.equals(4) || from.equals(2)) && (Integer.valueOf(type).equals(UserTransDetail.TRANS_INTEREST) || Integer.valueOf(type).equals(UserTransDetail.TRANS_PRINCIPAL))) {
					List<Integer> types = new ArrayList<Integer>();
					types.add(UserTransDetail.TRANS_INTEREST);
					types.add(UserTransDetail.TRANS_PRINCIPAL);
					details.setTranstypes(types);
				} else {
					details.setTrustType(Integer.valueOf(type));
				}
			}
			details.setUserId(userId);
			if (begindate != null && begindate.trim().length() > 0) {
				details.setBeginTransDate(DateUtil.getTextDate(begindate, "yyyy-MM-dd"));
			}
			if (enddate != null && enddate.trim().length() > 0) {
				details.setEndTransDate(DateUtil.getTextDate(enddate, "yyyy-MM-dd"));
			}

			Page<UserTransDetail> page = new Page<UserTransDetail>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.trans_date DESC");
			Page<UserTransDetail> resultPage = userTransDetailService.findPage(page, details);
			if (resultPage != null && resultPage.getList().size() > 0) {
				List<UserTransDetail> transList = resultPage.getList();
				for (int i = 0; i < transList.size(); i++) {
					details = transList.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("transtype", details.getTrustTypeStr()); // 交易类型
					map.put("tranddate", DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd"));
					map.put("balancemoney", details.getAvaliableAmount());
					map.put("amount", details.getAmount());
					map.put("transstate", details.getStateStr());
					WloanTermInvest invest = null;
					String projectid = "";
					String name = "";
					if (details.getTrustType() == UserTransDetail.TRANS_TERM) {
						invest = wloanTermInvestDao.get(details.getTransId().trim());
						projectid = invest.getWloanTermProject().getId() == null ? "" : invest.getWloanTermProject().getId();
						name = invest.getWloanTermProject().getName() == null ? "" : invest.getWloanTermProject().getName();
					}

					WloanTermUserPlan plan = null;
					if (details.getTrustType() == UserTransDetail.TRANS_INTEREST || details.getTrustType() == UserTransDetail.TRANS_PRINCIPAL) {
						plan = wloanTermUserPlanDao.get(details.getTransId().trim());
						projectid = plan.getWloanTermProject().getId() == null ? "" : plan.getWloanTermProject().getId();
						name = plan.getWloanTermProject().getName() == null ? "" : plan.getWloanTermProject().getName();
					}
					map.put("projectid", projectid);
					map.put("name", name);
					map.put("type", details.getTrustType());
					map.put("state", details.getState());
					list.add(map);
				}
			}

			data.put("pageNo", resultPage.getPageNo());
			data.put("pageSize", resultPage.getPageSize());
			data.put("pageCount", resultPage.getCount());
			data.put("last", resultPage.getLast());
			data.put("translist", list);
			data.put("type", type);

			result.put("state", "0");
			result.put("message", "查询用户交易记录信息成功");
			result.put("data", data);
			result.put("pramatype", type);
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}

		return result;
	}

	/**
	 * 投资用户交易流水---新版
	 * 
	 * @param token
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param type
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	@POST
	@Path("/getcgbUserTransDetailList")
	public Map<String, Object> getcgbUserTransDetailList(@FormParam("token") String token, @FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("type") String type, @FormParam("begindate") String begindate, @FormParam("enddate") String enddate) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(type)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			result.put("data", null);
			return result;
		}

		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String userId = "";
		try {

			String jedisUserId = JedisUtils.get(token);
			if (!StringUtils.isBlank(jedisUserId)) {
				userId = jedisUserId;
			}

			CgbUserTransDetail details = new CgbUserTransDetail();
			if (Integer.valueOf(type) != -1) {
				if ((from.equals("3") || from.equals("4") || from.equals("2")) && (Integer.valueOf(type).equals(UserTransDetail.TRANS_INTEREST) || Integer.valueOf(type).equals(UserTransDetail.TRANS_PRINCIPAL))) {
					List<Integer> types = new ArrayList<Integer>();
					types.add(UserTransDetail.TRANS_INTEREST);
					types.add(UserTransDetail.TRANS_PRINCIPAL);
					details.setTranstypes(types);
				} else {
					details.setTrustType(Integer.valueOf(type));
				}
			}
			details.setUserId(userId);
			if (begindate != null && begindate.trim().length() > 0) {
				details.setBeginTransDate(DateUtil.getTextDate(begindate, "yyyy-MM-dd"));
			}
			if (enddate != null && enddate.trim().length() > 0) {
				details.setEndTransDate(DateUtil.getTextDate(enddate, "yyyy-MM-dd"));
			}
			// details.setState(UserTransDetail.TRANS_STATE_SUCCESS);

			Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.trans_date DESC");

			Page<CgbUserTransDetail> resultPage = cgbUserTransDetailService.findPage(page, details);

			if (resultPage != null && resultPage.getList().size() > 0) {
				List<CgbUserTransDetail> transList = resultPage.getList();
				for (int i = 0; i < transList.size(); i++) {
					details = transList.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("transtype", details.getTrustTypeStr()); // 交易类型
					map.put("tranddate", DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd"));
					map.put("balancemoney", details.getAvaliableAmount());
					map.put("amount", details.getAmount());
					map.put("transstate", details.getStateStr());
					map.put("remark", details.getRemarks());
					WloanTermInvest invest = null;
					String projectid = "";
					String name = "";
					if (details.getTrustType() == UserTransDetail.TRANS_TERM) {
						invest = wloanTermInvestDao.get(details.getTransId().trim());
						projectid = invest.getWloanTermProject().getId() == null ? "" : invest.getWloanTermProject().getId();
						name = invest.getWloanTermProject().getName() == null ? "" : invest.getWloanTermProject().getName();
					}

					WloanTermUserPlan plan = null;
					if (details.getTrustType() == UserTransDetail.TRANS_INTEREST || details.getTrustType() == UserTransDetail.TRANS_PRINCIPAL) {
						plan = wloanTermUserPlanDao.get(details.getTransId().trim());
						if (null == plan) {
							projectid = "";
							name = "";
						} else {
							projectid = plan.getWloanTermProject().getId() == null ? "" : plan.getWloanTermProject().getId();
							name = plan.getWloanTermProject().getName() == null ? "" : plan.getWloanTermProject().getName();
						}
					}
					map.put("projectid", projectid); // 项目ID.
					// 项目详情.
					WloanTermProject wloanTermProject = wloanTermProjectService.get(projectid);
					if (null != wloanTermProject) {
						map.put("projectType", wloanTermProject.getProjectType());
						map.put("projectProductType", wloanTermProject.getProjectProductType());
						map.put("sn", wloanTermProject.getSn());
					} else {
						map.put("projectType", null);
						map.put("projectProductType", null);
						map.put("sn", null);
					}
					map.put("name", name);
					map.put("name", name);
					map.put("type", details.getTrustType());
					map.put("state", details.getState());
					map.put("inouttype", details.getInOutType());
					list.add(map);
				}
			}

			data.put("pageNo", resultPage.getPageNo());
			data.put("pageSize", resultPage.getPageSize());
			data.put("pageCount", resultPage.getCount());
			data.put("last", resultPage.getLast());
			data.put("translist", list);
			data.put("type", type);

			result.put("state", "0");
			result.put("message", "查询用户交易记录信息成功");
			result.put("data", data);
			result.put("pramatype", type);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}

		return result;
	}

	public static void main(String[] args) {

		Cache cache = null;
		try {
			cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get("token");
			String userid = principal.getUserInfo().getName();
			System.out.println(userid);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 借款用户交易流水
	 * 
	 * @param token
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param type
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	@POST
	@Path("/getCreditUserTransDetailList")
	public Map<String, Object> getCreditUserTransDetailList(@FormParam("token") String token, @FormParam("from") String from, @FormParam("pageNo") String pageNo, @FormParam("pageSize") String pageSize, @FormParam("type") String type, @FormParam("begindate") String begindate, @FormParam("enddate") String enddate) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(token) || StringUtils.isBlank(from) || StringUtils.isBlank(pageNo) || StringUtils.isBlank(pageSize) || StringUtils.isBlank(type)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			result.put("data", null);
			return result;
		}

		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String userId = "";
		try {
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					userId = principal.getCreditUserInfo().getId();
				}
			}

			CgbUserTransDetail details = new CgbUserTransDetail();
			if (Integer.valueOf(type) != -1) {
				if ((from.equals(3) || from.equals(4) || from.equals(2)) && (Integer.valueOf(type).equals(UserTransDetail.TRANS_INTEREST) || Integer.valueOf(type).equals(UserTransDetail.TRANS_PRINCIPAL))) {
					List<Integer> types = new ArrayList<Integer>();
					types.add(UserTransDetail.TRANS_INTEREST);
					types.add(UserTransDetail.TRANS_PRINCIPAL);
					details.setTranstypes(types);
				} else {
					details.setTrustType(Integer.valueOf(type));
				}
			}
			details.setUserId(userId);
			if (begindate != null && begindate.trim().length() > 0) {
				details.setBeginTransDate(DateUtil.getTextDate(begindate, "yyyy-MM-dd"));
			}
			if (enddate != null && enddate.trim().length() > 0) {
				details.setEndTransDate(DateUtil.getTextDate(enddate, "yyyy-MM-dd"));
			}

			Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
			page.setPageNo(Integer.valueOf(pageNo));
			page.setPageSize(Integer.valueOf(pageSize));
			page.setOrderBy("a.trans_date DESC");
			Page<CgbUserTransDetail> resultPage = cgbUserTransDetailService.findCreditPage(page, details);
			if (resultPage != null && resultPage.getList().size() > 0) {
				List<CgbUserTransDetail> transList = resultPage.getList();
				for (int i = 0; i < transList.size(); i++) {
					details = transList.get(i);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("transtype", details.getTrustTypeStr()); // 交易类型
					map.put("tranddate", DateUtils.formatDate(details.getTransDate(), "yyyy-MM-dd"));
					map.put("balancemoney", details.getAvaliableAmount());
					map.put("amount", details.getAmount());
					map.put("transstate", details.getStateStr());
					WloanTermInvest invest = null;
					String projectid = "";
					String name = "";
					if (details.getTrustType() == UserTransDetail.TRANS_TERM) {
						invest = wloanTermInvestDao.get(details.getTransId().trim());
						projectid = invest.getWloanTermProject().getId() == null ? "" : invest.getWloanTermProject().getId();
						name = invest.getWloanTermProject().getName() == null ? "" : invest.getWloanTermProject().getName();
					}

					WloanTermUserPlan plan = null;
					if (details.getTrustType() == UserTransDetail.TRANS_INTEREST || details.getTrustType() == UserTransDetail.TRANS_PRINCIPAL) {
						plan = wloanTermUserPlanDao.get(details.getTransId().trim());
						projectid = plan.getWloanTermProject().getId() == null ? "" : plan.getWloanTermProject().getId();
						name = plan.getWloanTermProject().getName() == null ? "" : plan.getWloanTermProject().getName();
					}

					// 项目详情.
					WloanTermProject wloanTermProject = wloanTermProjectService.get(projectid);
					if (null != wloanTermProject) {
						map.put("projectType", wloanTermProject.getProjectType());
						map.put("projectProductType", wloanTermProject.getProjectProductType());
						map.put("sn", wloanTermProject.getSn());
					} else {
						map.put("projectType", null);
						map.put("projectProductType", null);
						map.put("sn", null);
					}

					map.put("projectid", projectid);
					map.put("name", name);
					map.put("type", details.getTrustType());
					map.put("state", details.getState());
					list.add(map);
				}
			}

			data.put("pageNo", resultPage.getPageNo());
			data.put("pageSize", resultPage.getPageSize());
			data.put("pageCount", resultPage.getCount());
			data.put("last", resultPage.getLast());
			data.put("translist", list);
			data.put("type", type);

			result.put("state", "0");
			result.put("message", "查询用户交易记录信息成功");
			result.put("data", data);
			result.put("pramatype", type);
		} catch (Exception e) {
			result.put("state", "1");
			result.put("message", "系统异常");
			result.put("data", null);
		}

		return result;
	}
}
