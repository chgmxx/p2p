package com.power.platform.jbsys.service;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.Page;
import com.power.platform.credit.entity.userinfo.CreditUserInfoDto;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.jbsys.type.FromEnum;

@Component
@Path("/creditUserInfos")
@Service("creditUserInfosService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CreditUserInfosService {

	private static final Logger log = LoggerFactory.getLogger(CreditUserInfosService.class);

	@Resource
	private CreditUserInfoService creditUserInfoService;

	/**
	 * 
	 * @Title: creditUserInfoList
	 * @Description:JBXT-借款用户信息列表
	 * @Author: yangzf 
	 * @param @param from
	 * @param @return
	 * @return Map<String,Object>
	 * @DateTime 2019年6月10日  下午2:56:55
	 */
	@POST
	@Path("/creditUserInfoList")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> creditUserInfoList(@FormParam("from") String from) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		List<CreditUserInfoDto> creditUserList = new ArrayList<CreditUserInfoDto>();
		try {
			if (StringUtils.isBlank(from)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (FromEnum.FROM_ENUM_JB.getValue().equals(from)) { // JB-尖兵系统.
				CreditUserInfoDto creditUserInfo = new CreditUserInfoDto();
				Page<CreditUserInfoDto> page = new Page<CreditUserInfoDto>();
//				page.setPageSize(-1); // 页面大小，设置为“-1”表示不进行分页（分页无效）.
				page.setOrderBy("a.register_date ASC"); // 升序，上线日期.
				creditUserInfo.setPage(page);
				List<CreditUserInfoDto> list = creditUserInfoService.findCreditUserInfo(creditUserInfo);
				System.out.println("befour===list==="+list.size());
				for (CreditUserInfoDto cui : list) {
					if(cui.getPhone().length()==11) {
						creditUserList.add(cui);
					}
				}
				System.out.println("after==creditUserList===="+creditUserList.size());
				result.put("dataList", creditUserList);
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
