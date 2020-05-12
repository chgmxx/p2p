package com.power.platform.lanmao.search.service;

import java.text.DateFormat;





import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.lanmao.search.service.LanMaoSearchUserInfoDataService;
import com.power.platform.lanmao.search.service.LanMaoSearchProjectDataService;
import com.power.platform.lanmao.search.service.LanMaoSearchOneTransactionDataService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListAddDataService;
import com.power.platform.lanmao.search.service.LanMaoWhiteListDelDataService;
import com.power.platform.lanmao.search.service.LanMaoRemitQueryServiceService;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
@Component
@Path("/lanMaoSearch")
@Service("lanMaoSearchService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class LanMaoSearchService {
	@Autowired
	private LanMaoSearchUserInfoDataService lanMaoSearchUserInfoDataService;
	@Autowired
	private LanMaoSearchProjectDataService lanMaoSearchProjectDataService;
	@Autowired
	private LanMaoSearchOneTransactionDataService lanMaoSearchOneTransactionDataService;
	@Autowired
	private LanMaoWhiteListAddDataService lanMaoWhiteListAddDataService;
	@Autowired
	private LanMaoWhiteListDelDataService lanMaoWhiteListDelDataService;
	@Autowired
	private LanMaoQueryTransactionRecordService lanMaoQueryTransactionRecordService;
	@Autowired
	private LanMaoRemitQueryServiceService lanMaoRemitQueryServiceService;
	
	@POST
	@Path("/searchUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> searchUserInfo(@FormParam("platformUserNo") String platformUserNo,@FormParam("additionalFields") String additionalFields) {

		Map<String, Object> result = null;
//		用户信息查询：平台用户编号
		result = lanMaoSearchUserInfoDataService.searchUserInfo(platformUserNo);
		return result;
	}
	@POST
	@Path("/searchProject")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> searchProject(@FormParam("projectNo") String projectNo) {

		Map<String, Object> result = null;
//标的信息查询：标的号
		result = lanMaoSearchProjectDataService.searchProject(projectNo);
		return result;
	}
	@POST
	@Path("/searchOneTransaction")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> searchOneTransaction(@FormParam("requestNo") String requestNo,@FormParam("transactionType") String transactionType) {

		Map<String, Object> result = null;
//单笔交易查询：业务的请求流水号+交易查询类型
		result = lanMaoSearchOneTransactionDataService.searchOneTransaction(requestNo, transactionType);
		return result;
	}
	@POST
	@Path("/whiteListAdd")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> whiteListAdd(@FormParam("requestNo") String requestNo,@FormParam("platformUserNo") String platformUserNo,@FormParam("bankcardNo") String bankcardNo) {

		Map<String, Object> result = null;
		LanMaoWhiteList list = new LanMaoWhiteList();
		list.setBankcardNo(bankcardNo);
		list.setPlatformUserNo(platformUserNo);
		list.setRequestNo(requestNo);
		list.setUserRole("INVESTOR");
        //添加白名单：请求流水号+平台用户编号（user_id）+银行卡号（选填）
		result = lanMaoWhiteListAddDataService.whiteListAdd(list,requestNo);
		return result;
	}
	@POST
	@Path("/whiteListDel")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> whiteListDel(@FormParam("requestNo") String requestNo,@FormParam("platformUserNo") String platformUserNo,@FormParam("bankcardNo") String bankcardNo) {

		Map<String, Object> result = null;
		LanMaoWhiteList list = new LanMaoWhiteList();
		list.setBankcardNo(bankcardNo);
		list.setPlatformUserNo(platformUserNo);
		list.setRequestNo(requestNo);
		list.setUserRole("INVESTOR");
//		删除白名单：请求流水号+平台用户编号（user_id）+银行卡号（必填）
		result = lanMaoWhiteListDelDataService.whiteListDel(list,requestNo);
		return result;
	}
	@POST
	@Path("/queryTransactionRecord")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> queryTransactionRecord(@FormParam("platformUserNo") String platformUserNo,@FormParam("transactionType") String transactionType,@FormParam("startTime") String startTime,@FormParam("endTime") String endTime) {

		Map<String, Object> result = null;
//		网银转账充值记录查询
		result = lanMaoQueryTransactionRecordService.rueryTransactionRecord(platformUserNo, transactionType, startTime, endTime);
		return result;
	}
	@POST
	@Path("/remitQueryService")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> remitQueryService(@FormParam("accountNo") String accountNo,@FormParam("startTime") String startTime,@FormParam("endTime") String endTime) {

		Map<String, Object> result = null;
//		网银转账充值代付查询
		result = lanMaoRemitQueryServiceService.remitQueryService(accountNo, startTime, endTime);
		return result;
	}
}
