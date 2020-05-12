package com.power.platform.p2peye.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.p2peye.pojo.Product;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.userinfo.service.UserInfoService;

@Path("/p2peye/borrowing/data")
@Service("p2peyeBorrowingDataRestService")
@Produces(MediaType.APPLICATION_JSON)
public class P2peyeBorrowingDataRestService {

	private static final Logger log = LoggerFactory.getLogger(P2peyeBorrowingDataRestService.class);

	/**
	 * 标的详情URL.
	 */
	private static final String URL = "https://www.cicmorgan.com/investment_details.html?id=";
	/**
	 * 还款方式：代表其他.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_0 = "0";
	/**
	 * 还款方式：按月等额本息还款.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_1 = "1";
	/**
	 * 还款方式：按月付息,到期还本.
	 */
	private static final String PAY_WAY_2 = "2";
	/**
	 * 还款方式：按天计息,一次性还本付息.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_3 = "3";
	/**
	 * 还款方式：按月计息,一次性还本付息.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_4 = "4";
	/**
	 * 还款方式：按季分期还款.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_5 = "5";
	/**
	 * 还款方式：为等额本金,按月还本金.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_6 = "6";
	/**
	 * 还款方式：先息期本.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_7 = "7";
	/**
	 * 还款方式：按季付息,到期还本.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_8 = "8";
	/**
	 * 还款方式：按半年付息,到期还本.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_9 = "9";
	/**
	 * 还款方式：按年付息，到期还本.
	 */
	@SuppressWarnings("unused")
	private static final String PAY_WAY_10 = "10";
	/**
	 * 期限类型，天.
	 */
	private static final String P_TYPE_0 = "0";
	/**
	 * 期限类型，月.
	 */
	@SuppressWarnings("unused")
	private static final String P_TYPE_1 = "1";
	/**
	 * 正在投标中的借款标.
	 */
	private static final String STATUS_0 = "0";
	/**
	 * 已完成(包括还款中和已完成的借款标).
	 */
	private static final String STATUS_1 = "1";
	/**
	 * 代表信用标.
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_0 = "0";
	/**
	 * 担保标.
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_1 = "1";
	/**
	 * 抵押质押标.
	 */
	private static final String C_TYPE_2 = "2";
	/**
	 * 秒标.
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_3 = "3";
	/**
	 * 债权转让标(流转标,二级市场标的).
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_4 = "4";
	/**
	 * 理财计划(宝类业务_活期).
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_5 = "5";
	/**
	 * 其它.
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_6 = "6";
	/**
	 * 净值标.
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_7 = "7";
	/**
	 * 活动标(体验标).
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_8 = "8";
	/**
	 * 理财计划(宝类业务_定期).
	 */
	@SuppressWarnings("unused")
	private static final String C_TYPE_9 = "9";

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 
	 * 方法: productList <br>
	 * 描述: 借款数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月8日 下午5:31:03
	 * 
	 * @param status
	 *            标的状态:0.正在投标中的借款标;1.已完成(包括还款中和已完成的借款标).
	 * @param time_from
	 *            起始时间如:2014-05-09 06:10:00,状态为1是对应平台满标字段的值检索,状态为0就以平台发标时间字段检索.
	 * @param time_to
	 *            起始时间如:2014-05-09 06:10:00,状态为1是对应平台满标字段的值检索,状态为0就以平台发标时间字段检索.
	 * @param page_size
	 *            每页记录条数.
	 * @param page_index
	 *            请求的页码.
	 * @param token
	 *            请求 token 链接平台返回的秘钥或签名No.
	 * @return
	 */
	@POST
	@Path("/productList")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> productList(@FormParam("status") int status, @FormParam("time_from") String time_from, @FormParam("time_to") String time_to, @FormParam("page_size") int page_size, @FormParam("page_index") int page_index, @FormParam("token") String token) {

		/**
		 * 客户端响应结果集.
		 */
		Map<String, Object> result = new HashMap<String, Object>();
		if (status < 0 || status > 1) {
			log.info("fn:productList-请求借款数据-标的状态应为0或者1.");
			result.put("result_code", "2");
			result.put("result_msg", "标的状态应为0或者1");
			result.put("page_count", "0");
			result.put("page_index", page_index);
			result.put("loans", null);
			return result;
		} else if (StringUtils.isBlank(time_from)) {// 起始时间.
			log.info("fn:productList-请求借款数据-缺少必要参数值[起始时间].");
			result.put("result_code", "3");
			result.put("result_msg", "缺少必要参数值[起始时间]");
			result.put("page_count", "0");
			result.put("page_index", page_index);
			result.put("loans", null);
			return result;
		} else if (StringUtils.isBlank(time_to)) {// 截至时间.
			log.info("fn:productList-请求借款数据-缺少必要参数值[截至时间].");
			result.put("result_code", "4");
			result.put("result_msg", "缺少必要参数值[截至时间]");
			result.put("page_count", "0");
			result.put("page_index", page_index);
			result.put("loans", null);
			return result;
		} else if (page_size <= 0) {// 每页记录条数.
			log.info("fn:productList-请求借款数据-每页展示条数应为正整数且不能为0.");
			result.put("result_code", "5");
			result.put("result_msg", "每页展示条数应为正整数且不能为0");
			result.put("page_count", "0");
			result.put("page_index", page_index);
			result.put("loans", null);
			return result;
		} else if (page_index <= 0) {// 请求的页码.
			log.info("fn:productList-请求借款数据-请求页码应为正整数且不能为0.");
			result.put("result_code", "6");
			result.put("result_msg", "请求页码应为正整数且不能为0");
			result.put("page_count", "0");
			result.put("page_index", page_index);
			result.put("loans", null);
			return result;
		} else if (StringUtils.isBlank(token)) { // token.
			log.info("fn:productList-请求借款数据-token不能为Null.");
			result.put("result_code", "7");
			result.put("result_msg", "缺少必要参数值[token]");
			result.put("page_count", "0");
			result.put("page_index", page_index);
			result.put("loans", null);
			return result;
		}

		/**
		 * 缓存中获取客户信息.
		 */
		try {
			
			String jedisUserId = JedisUtils.get(token);
			
			if (!StringUtils.isBlank(jedisUserId) && userInfoService.get(jedisUserId)!=null) {
				// 项目.
				WloanTermProject wloanTermProject = new WloanTermProject();
				// 产品列表.
				List<Product> productList = new ArrayList<Product>();
				if (status == 0) { // 正在投标中的借款标.
					log.info("fn:productList-请求借款数据-查询投标中的借款标.");
					// 上线.
					wloanTermProject.setState(WloanTermProjectService.ONLINE);
					// 上线时间，起始时间.
					wloanTermProject.setBeginTimeFromOnline(time_from);
					// 上线时间，截至时间.
					wloanTermProject.setEndTimeToOnline(time_to);
					// 借款数据-项目列表.
					Page<WloanTermProject> page = new Page<WloanTermProject>();
					page.setPageNo(page_index); // 当前页码.
					page.setPageSize(page_size); // 每页记录条数.
					Page<WloanTermProject> pages = wloanTermProjectService.findPage(page, wloanTermProject);
					List<WloanTermProject> projectList = pages.getList();
					if (projectList != null && projectList.size() > 0) {
						for (WloanTermProject project : projectList) {
							Product product = new Product();
							product.setId(project.getId());
							product.setUrl(URL + project.getId());
							product.setPlatform_name("中投摩根");
							product.setTitle(project.getName());
							if (project.getWloanSubject() != null) {
								product.setUsername(project.getWloanSubject().getCompanyName());
							}
							product.setStatus(STATUS_0);
							if (project.getWloanSubject() != null) {
								product.setUserid(project.getWloanSubject().getId());
							}
							product.setC_type(C_TYPE_2);
							product.setAmount(NumberUtils.scaleDoubleStr(project.getAmount()));
							Double rate = project.getAnnualRate();
							rate = rate / 100;
							product.setRate(NumberUtils.scaleFourStr(rate));
							product.setPeriod(project.getSpan().toString());
							product.setP_type(P_TYPE_0);
							product.setPay_way(PAY_WAY_2);
							product.setProcess(NumberUtils.scaleOneStr(project.getCurrentAmount() / project.getAmount()));
							product.setReward(NumberUtils.scaleDoubleStr(0.00));
							product.setGuarantee(NumberUtils.scaleDoubleStr(0.00));
							product.setStart_time(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
							// 满标日期为NULL(status == 0).
							product.setEnd_time("");
							// 投资次数这笔借款标有多少个投标记录.
							List<WloanTermInvest> investOfProjectNumbers = wloanTermInvestDao.findProjectInvestNumbers(project.getId());
							product.setInvest_num(String.valueOf(investOfProjectNumbers.size()));
							product.setC_reward(NumberUtils.scaleDoubleStr(0.00));
							productList.add(product);
						}
						log.info("fn:productList-请求借款数据-成功.");
						result.put("result_code", "1");
						result.put("result_msg", "获取借款数据成功");
						result.put("page_count", String.valueOf(pages.getTotalPage()));
						result.put("page_index", String.valueOf(pages.getPageNo()));
						result.put("loans", productList);
					} else {
						log.info("fn:productList-未授权的访问!");
						result.put("result_code", "-1");
						result.put("result_msg", "未授权的访问!");
						result.put("page_count", "0");
						result.put("page_index", "0");
						result.put("loans", null);
					}
				} else if (status == 1) { // 已完成(包括还款中、已完成、满标的借款标).
					log.info("fn:productList-请求借款数据-查询已结束的借款标.");
					// 满标、还款中、已结束.
					List<String> stateItem = new ArrayList<String>();
					stateItem.add(WloanTermProjectService.FULL);
					stateItem.add(WloanTermProjectService.REPAYMENT);
					stateItem.add(WloanTermProjectService.FINISH);
					wloanTermProject.setStateItem(stateItem);
					// 满标时间，起始时间.
					wloanTermProject.setBeginTimeFromFull(time_from);
					// 满标时间，截至时间.
					wloanTermProject.setEndTimeToFull(time_to);
					// 借款数据-项目列表.
					Page<WloanTermProject> page = new Page<WloanTermProject>();
					page.setPageNo(page_index); // 当前页码.
					page.setPageSize(page_size); // 每页记录条数.
					Page<WloanTermProject> pages = wloanTermProjectService.findPage(page, wloanTermProject);
					List<WloanTermProject> projectList = pages.getList();
					if (projectList != null && projectList.size() > 0) {
						for (WloanTermProject project : projectList) {
							Product product = new Product();
							product.setId(project.getId());
							product.setUrl(URL + project.getId());
							product.setPlatform_name("中投摩根");
							product.setTitle(project.getName());
							if (project.getWloanSubject() != null) {
								product.setUsername(project.getWloanSubject().getCompanyName());
							}
							product.setStatus(STATUS_1);
							if (project.getWloanSubject() != null) {
								product.setUserid(project.getWloanSubject().getId());
							}
							product.setC_type(C_TYPE_2);
							product.setAmount(NumberUtils.scaleDoubleStr(project.getAmount()));
							Double rate = project.getAnnualRate();
							rate = rate / 100;
							product.setRate(NumberUtils.scaleFourStr(rate));
							product.setPeriod(project.getSpan().toString());
							product.setP_type(P_TYPE_0);
							product.setPay_way(PAY_WAY_2);
							product.setProcess(NumberUtils.scaleOneStr(project.getCurrentAmount() / project.getAmount()));
							product.setReward(NumberUtils.scaleDoubleStr(0.00));
							product.setGuarantee(NumberUtils.scaleDoubleStr(0.00));
							product.setStart_time(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
							// 满标日期(status == 1).
							product.setEnd_time(DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss"));
							// 投资次数这笔借款标有多少个投标记录.
							List<WloanTermInvest> investOfProjectNumbers = wloanTermInvestDao.findProjectInvestNumbers(project.getId());
							product.setInvest_num(String.valueOf(investOfProjectNumbers.size()));
							product.setC_reward(NumberUtils.scaleDoubleStr(0.00));
							productList.add(product);
						}
						log.info("fn:productList-请求借款数据-成功.");
						result.put("result_code", "1");
						result.put("result_msg", "获取借款数据成功");
						result.put("page_count", String.valueOf(pages.getTotalPage()));
						result.put("page_index", String.valueOf(pages.getPageNo()));
						result.put("loans", productList);
					} else {
						log.info("fn:productList-未授权的访问!");
						result.put("result_code", "-1");
						result.put("result_msg", "未授权的访问!");
						result.put("page_count", "0");
						result.put("page_index", "0");
						result.put("loans", null);
					}
				}
			} else {
				log.info("fn:productList-请求借款数据-token失效.");
				result.put("result_code", "8");
				result.put("result_msg", "token失效");
				result.put("page_count", "0");
				result.put("page_index", page_index);
				result.put("loans", null);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("fn:productList-系统异常，请联系开发人员.");
			result.put("state", "0");
			result.put("message", "系统异常，请联系开发人员");
			result.put("loans", null);
			return result;
		}

		return result;
	}

}
