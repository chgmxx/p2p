package com.power.platform.p2peye.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.power.platform.cache.Cache;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.p2peye.pojo.P2PEyeInvestPojo;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.Principal;


/**
 * p2pEye 投资接口
 * @author YHAGZALUN WO SJIAOSY
 *
 */
@Component
@Path("/p2pEyeInvest")
@Service("p2PEyeInvestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class P2PEyeInvestService {

	private static final Logger log = LoggerFactory.getLogger(P2PEyeInvestService.class);
	
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	
	/**
     *
	 * @param status 标的状态:0.正在投标中的借款标;1.已完成(包括还款中和已完成的借款标).
	 * @param timeFrom 起始时间如:2014-05-09 06:10:00
	 * @param timeTo 截止时间如:2014-05-09 06:10:00
	 * @param pageSize 每页记录条数
	 * @param pageIndex 请求的页码
	 * @param token 请求 token 链接平台返回的秘钥或签名
	 * @return
	 */
	@POST
	@Path("/invest")
	@Produces(MediaType.APPLICATION_JSON)
	public  Map<String, Object> saveUserInvest(@FormParam("status")Integer status, 
			@FormParam("time_from")String timeFrom, 
			@FormParam("time_to")String timeTo,
			@FormParam("page_size")Integer pageSize,
			@FormParam("page_index")Integer pageIndex,
			@FormParam("token")String token){
		Map<String, Object> result = new HashMap<String, Object>();
		WloanTermProject project = new WloanTermProject();
		List<String> stateLists = new ArrayList<String>();
		List<P2PEyeInvestPojo> list = new ArrayList<P2PEyeInvestPojo>();
        int pageNo = 0;
        int pageCount = 0;
        
        
		if (status < 0 || status > 1) {
			log.info("fn:invest-请求投资数据-标的状态应为0或者1.");
			result.put("result_code", "2");
			result.put("result_msg", "标的状态应为0或者1");
			result.put("page_count", "0");
			result.put("page_index", pageIndex);
			result.put("loans", null);
			return result;
		} else if (StringUtils.isBlank(timeFrom)) {// 起始时间.
			log.info("fn:invest-请求投资数据-缺少必要参数值[起始时间].");
			result.put("result_code", "3");
			result.put("result_msg", "缺少必要参数值[起始时间]");
			result.put("page_count", "0");
			result.put("page_index", pageIndex);
			result.put("loans", null);
			return result;
		} else if (StringUtils.isBlank(timeTo)) {// 截至时间.
			log.info("fn:invest-请求投资数据-缺少必要参数值[截至时间].");
			result.put("result_code", "4");
			result.put("result_msg", "缺少必要参数值[截至时间]");
			result.put("page_count", "0");
			result.put("page_index", pageIndex);
			result.put("loans", null);
			return result;
		} else if (pageSize <= 0) {// 每页记录条数.
			log.info("fn:invest-请求投资数据-每页展示条数应为正整数且不能为0.");
			result.put("result_code", "5");
			result.put("result_msg", "每页展示条数应为正整数且不能为0");
			result.put("page_count", "0");
			result.put("page_index", pageIndex);
			result.put("loans", null);
			return result;
		} else if (pageIndex <= 0) {// 请求的页码.
			log.info("fn:invest-请求投资数据-请求页码应为正整数且不能为0.");
			result.put("result_code", "6");
			result.put("result_msg", "请求页码应为正整数且不能为0");
			result.put("page_count", "0");
			result.put("page_index", pageIndex);
			result.put("loans", null);
			return result;
		} else if (StringUtils.isBlank(token)) { // token.
			log.info("fn:invest-请求投资数据-token不能为Null.");
			result.put("result_code", "7");
			result.put("result_msg", "缺少必要参数值[token]");
			result.put("page_count", "0");
			result.put("page_index", pageIndex);
			result.put("loans", null);
			return result;
		}
        
		Page<WloanTermInvest> page = new Page<WloanTermInvest>();
		page.setPageNo(pageIndex);
		page.setPageSize(pageSize);
		page.setOrderBy("a.create_date DESC");
		
		try {
			
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if(principal!=null){
				if(principal.getUserInfo()!=null){
					if(status == 0){
						stateLists.clear();
						stateLists.add(WloanTermProjectService.ONLINE);
						project.setStateItem(stateLists);
					}else if(status == 1){
						stateLists.clear();
						stateLists.add(WloanTermProjectService.FULL);
						stateLists.add(WloanTermProjectService.REPAYMENT);
						stateLists.add(WloanTermProjectService.FINISH);
						project.setStateItem(stateLists);
						}
					if (project != null) {
						WloanTermInvest invest = new WloanTermInvest();
						invest.setBeginBeginDate(DateUtils.getDateOfString(timeFrom));
						invest.setEndBeginDate(DateUtils.getDateOfString(timeTo));
						invest.setWloanTermProject(project);
						Page<WloanTermInvest> investPage = wloanTermInvestService.findPage(page, invest);
						List<WloanTermInvest> investList = investPage.getList();
						if (investList != null && investList.size() > 0) {
							for (int i = 0; i < investList.size(); i++) {
								invest = investList.get(i);
								P2PEyeInvestPojo pojo = new P2PEyeInvestPojo();
								pojo.setId(invest.getWloanTermProject().getId());
								pojo.setLink("https://www.cicmorgan.com/investment_details.html?id="+invest.getWloanTermProject().getId());
								pojo.setUseraddress("");
								pojo.setUsername(Util.hideString(invest.getUserInfo().getName(), 3, 4));
								pojo.setUserid(invest.getUserInfo().getId());
								pojo.setType("手动");
								pojo.setMoney(invest.getAmount());
								pojo.setAccount(invest.getAmount());
								pojo.setStatus(invest.getState().equals(WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1)?"成功":"失败");
								pojo.setAdd_time(DateUtils.formatDateTime(invest.getCreateDate()));
								list.add(pojo);
			
							}
							pageNo = investPage.getPageNo();
							pageCount = investPage.getTotalPage();
						}else{
							log.info("fn:invest-请求投资数据-暂无数据.");
							result.put("result_code", "-1");
							result.put("result_msg", "未授权的访问");
							result.put("page_count", "0");
							result.put("page_index", "0");
							result.put("loans", null);
							return result;
						}
					}
				}else{
					log.info("fn:invest-请求投资数据-token失效.");
					result.put("result_code", "-1");
					result.put("result_msg", "未授权的访问");
					result.put("page_count", "0");
					result.put("page_index", "0");
					result.put("loans", null);
					return result;
				}
			}
			result.put("result_code", "1");
			result.put("result_msg", "获取数据成功");
			result.put("page_index", pageNo);
			result.put("page_count", pageCount);
			result.put("loans", list);
		return result;
	}catch (Exception e){
		log.info("fn:invest-系统异常，请联系开发人员.");
		result.put("result_code", "0");
		result.put("result_msg", "系统异常，请联系开发人员");
		result.put("page_count", "0");
		result.put("page_index", "0");
		result.put("loans", null);
		return result;
	}
	}
}