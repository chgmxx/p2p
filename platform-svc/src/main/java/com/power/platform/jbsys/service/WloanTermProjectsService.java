package com.power.platform.jbsys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.jbsys.type.FromEnum;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlanDto;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.entity.WloanTermProjectDto;
import com.power.platform.regular.service.WloanTermProjectService;

@Component
@Path("/wloanTermProjects")
@Service("wloanTermProjectsService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class WloanTermProjectsService {

	private static final Logger log = LoggerFactory.getLogger(WloanTermProjectsService.class);

	@Resource
	private CreditUserInfoService creditUserInfoService;
	@Resource
	private WloanTermProjectService wloanTermProjectService;

	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;

	@Resource
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * @Title: creditUserLoanList
	 * @Description:jbxt-供应链标的列表
	 * @Author: yangzf
	 * @param @param from
	 * @param @return
	 * @return Map<String,Object>
	 * @DateTime 2019年6月10日 上午9:40:31
	 */
	@POST
	@Path("/creditUserLoanList")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> creditUserLoanList(@FormParam("from") String from) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(from)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (FromEnum.FROM_ENUM_JB.getValue().equals(from)) { // JB-尖兵系统.
				WloanTermProjectDto wloanTermProject = new WloanTermProjectDto();
				if (null == wloanTermProject.getProjectProductType()) { // 标的产品类型为Null（供应链）.
					wloanTermProject.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_2);
				}
				List<WloanTermProjectDto> list = wloanTermProjectService.findProjectDtoList(wloanTermProject);
				for (WloanTermProjectDto wtp : list) {
					if (wtp.getInterestRateIncrease() == null) {
						wtp.setInterestRateIncrease(0.00D);
					}
				}
				result.put("dataList", list);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
			return result;
		}
		return result;
	}

	/**
	 * 
	 * @Title: findProjectPlan
	 * @Description:jbxt-标的的还款计划
	 * @Author: yangzf
	 * @param @param from
	 * @param @param proid
	 * @param @return
	 * @return Map<String,Object>
	 * @DateTime 2019年6月10日 上午11:48:30
	 */
	@POST
	@Path("/findProjectPlan")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> findProjectPlan(@FormParam("from") String from, @FormParam("proid") String proid) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			if (StringUtils.isBlank(from) || StringUtils.isBlank(proid)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (FromEnum.FROM_ENUM_JB.getValue().equals(from)) { // JB-尖兵系统.
				List<WloanTermProjectPlanDto> projectPlanDtos = wloanTermProjectPlanDao.findPlanList(proid);
				for (int i = 0; i < projectPlanDtos.size(); i++) {
					WloanTermProjectPlanDto dto = projectPlanDtos.get(i);
					Map<String, Object> param = new LinkedHashMap<String, Object>();
					param.put("id", dto.getId()); // 散标还款计划主键.
					param.put("proId", dto.getWloanTermProject() != null ? "" : dto.getWloanTermProject().getId()); // 散标主键.
					param.put("principal", dto.getPrincipal()); // 0：付息，1：还本付息.
					param.put("repaymentDate", DateUtils.formatDate(dto.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 还款日期（应还时间）.
					param.put("realRepaymentDate", DateUtils.formatDate(dto.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 还款日期（实还时间）.
					param.put("repayAmount", NumberUtils.scaleDoubleStr(dto.getInterest())); // 还款金额（应还金额）.
					param.put("realRepayAmount", NumberUtils.scaleDoubleStr(dto.getInterest())); // 还款金额（实还金额）.
					param.put("state", dto.getState()); // 还款状态，1：还款中，2：成功，3：失败，4：流标.
					param.put("nper", (i + 1)); // 还款期数.
					log.info("还款计划:{}:{}", (i + 1), JSON.toJSONString(param));
					list.add(param);
				}
				result.put("dataList", list);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
			return result;
		}
		return result;
	}
}
