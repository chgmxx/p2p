package com.power.platform.lanmao.trade.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.HttpUtils;
import com.power.platform.lanmao.common.VerifySignUtils;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.trade.pojo.ResponsePojo;
import com.power.platform.lanmao.trade.pojo.ResponsePojoUtil;
import com.power.platform.lanmao.type.ProductTypeEnum;
import com.power.platform.lanmao.type.ProjectTypeEnum;
import com.power.platform.lanmao.type.RepaymentWayEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;

@Service("lanMaoProjectService")
public class LanMaoProjectService {

	private final static Logger logger = LoggerFactory.getLogger(LanMaoProjectService.class);

	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private LmTransactionDao lmTransactionDao;

	/**
	 * 
	 * methods: modifyProject <br>
	 * description: 变更标的 <br>
	 * author: Roy <br>
	 * date: 2019年10月6日 下午2:17:22
	 * 
	 * @param requestNo
	 *            请求流水号
	 * @param projectNo
	 *            标的编号
	 * @param proStatus
	 *            变的标的状态
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> modifyProject(String requestNo, String projectNo, String proStatus) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {

			// 定义reqData参数集合
			Map<String, Object> reqDataMap = new HashMap<String, Object>();
			reqDataMap.put("requestNo", requestNo); // Y
			reqDataMap.put("projectNo", projectNo); // Y
			reqDataMap.put("status", proStatus); // Y
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.MODIFY_PROJECT.getValue(), reqDataMap);
			logger.debug("reques json:{}", JSON.toJSONString(requestParam));

			List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair n1 = new BasicNameValuePair("serviceName", requestParam.get("serviceName"));
			BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestParam.get("platformNo"));
			BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestParam.get("reqData"));
			BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestParam.get("keySerial"));
			BasicNameValuePair n5 = new BasicNameValuePair("sign", requestParam.get("sign"));
			formParams.add(n1);
			formParams.add(n2);
			formParams.add(n3);
			formParams.add(n4);
			formParams.add(n5);
			response = HttpUtils.httpPostWithPAaram(SERVICE_URL, formParams);
			lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			logger.debug("lmresult:{}", lmresult);

		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		ResponsePojo handler = ResponsePojoUtil.handler(lmresult);
		// 验签
		String code = handler.getCode();
		String status = handler.getStatus();
		try {
			if (!"0".equals(code) || !"SUCCESS".equals(status)) {
				logger.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
			} else {
				if (!VerifySignUtils.verifySign(response, lmresult)) {
					result.put("code", "1");
					result.put("status", "INIT");
					result.put("errorCode", "1");
					result.put("errorMessage", "sign fail");
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("sign fail..... " + e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		result.put("code", code);
		result.put("status", status);
		result.put("requestNo", handler.getRequestNo());
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

	/**
	 * 
	 * methods: modifyProject <br>
	 * description: 变更标的 <br>
	 * author: Roy <br>
	 * date: 2019年9月21日 上午10:59:44
	 * 
	 * @param projectNo
	 *            标的编号
	 * @param status
	 *            更新的标的状态
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> modifyProject(String projectNo, String proStatus) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {

			// 定义reqData参数集合
			Map<String, Object> reqDataMap = new HashMap<String, Object>();
			reqDataMap.put("requestNo", IdGen.uuid()); // Y
			reqDataMap.put("projectNo", projectNo); // Y
			reqDataMap.put("status", proStatus); // Y
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.MODIFY_PROJECT.getValue(), reqDataMap);
			logger.debug("reques json:{}", JSON.toJSONString(requestParam));

			List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair n1 = new BasicNameValuePair("serviceName", requestParam.get("serviceName"));
			BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestParam.get("platformNo"));
			BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestParam.get("reqData"));
			BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestParam.get("keySerial"));
			BasicNameValuePair n5 = new BasicNameValuePair("sign", requestParam.get("sign"));
			formParams.add(n1);
			formParams.add(n2);
			formParams.add(n3);
			formParams.add(n4);
			formParams.add(n5);
			response = HttpUtils.httpPostWithPAaram(SERVICE_URL, formParams);
			lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			logger.debug("lmresult:{}", lmresult);

		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		ResponsePojo handler = ResponsePojoUtil.handler(lmresult);
		// 验签
		String code = handler.getCode();
		String status = handler.getStatus();
		try {
			if (!"0".equals(code) || !"SUCCESS".equals(status)) {
				logger.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
			} else {
				if (!VerifySignUtils.verifySign(response, lmresult)) {
					result.put("code", "1");
					result.put("status", "INIT");
					result.put("errorCode", "1");
					result.put("errorMessage", "sign fail");
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("sign fail..... " + e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		result.put("code", code);
		result.put("status", status);
		result.put("requestNo", handler.getRequestNo());
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

	/**
	 * 
	 * methods: establishProject <br>
	 * description: 创建标的 <br>
	 * author: Roy <br>
	 * date: 2019年9月21日 上午11:01:15
	 * 
	 * @param project
	 *            标的信息
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> establishProject(WloanTermProject project) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		CloseableHttpResponse response = null;
		String lmresult = "";
		try {
			// 融资主体
			WloanSubject subject = wloanSubjectService.get(project.getSubjectId());
			String platformUserNo = ""; // 借款方平台用户编号
			if (null != subject) {
				CreditUserInfo creditUserInfo = creditUserInfoService.get(subject.getLoanApplyId());
				if (null != creditUserInfo) {
					platformUserNo = creditUserInfo.getId();
				}
			}

			String requestNo = IdGen.uuid();
			String sn = project.getSn();
			// 定义reqData参数集合
			Map<String, Object> reqDataMap = new HashMap<String, Object>();
			reqDataMap.put("requestNo", requestNo); // Y
			reqDataMap.put("platformUserNo", platformUserNo); // Y
			reqDataMap.put("projectNo", sn); // Y 标的编号
			reqDataMap.put("projectAmount", NumberUtils.scaleDoubleStr(project.getAmount())); // Y
			reqDataMap.put("projectName", project.getName()); // Y
			reqDataMap.put("projectDescription", ""); // N
			reqDataMap.put("projectType", ProjectTypeEnum.STANDARDPOWDER.getValue()); // Y
			reqDataMap.put("projectPeriod", project.getSpan().toString()); // Y
			reqDataMap.put("annualInterestRate", NumberUtils.scaleDoubleStr((project.getAnnualRate() / 100))); // Y
			reqDataMap.put("repaymentWay", RepaymentWayEnum.FIRSEINTREST_LASTPRICIPAL.getValue()); // Y
			// ExtendProjectPojo extendProjectPojo = new ExtendProjectPojo();
			// extendProjectPojo.setLabel(project.getLabel());
			// extend该参数影响接口联调，不用传递
			// reqDataMap.put("extend", JSON.toJSONString(extendProjectPojo)); // N
			// reqDataMap.put("productType", ProductTypeEnum.COMMONPRODUCT.getValue()); // N
			reqDataMap.put("productType", ProductTypeEnum.AUTOMATICPRODUCT.getValue()); // N
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestParam = AppUtil.lmGeneratePostParam(ServiceNameEnum.ESTABLISH_PROJECT.getValue(), reqDataMap);
			logger.debug("request json:{}", JSON.toJSONString(requestParam));

			// 懒猫交易留存
			LmTransaction lmTransaction = new LmTransaction();
			lmTransaction.setId(IdGen.uuid());
			lmTransaction.setRequestNo(requestNo);
			lmTransaction.setProjectNo(sn);
			lmTransaction.setServiceName(ServiceNameEnum.ESTABLISH_PROJECT.getValue());
			lmTransaction.setPlatformUserNo(platformUserNo);
			lmTransaction.setCreateDate(new Date());
			lmTransaction.setUpdateDate(new Date());
			int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
			logger.debug("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");

			List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair n1 = new BasicNameValuePair("serviceName", requestParam.get("serviceName"));
			BasicNameValuePair n2 = new BasicNameValuePair("platformNo", requestParam.get("platformNo"));
			BasicNameValuePair n3 = new BasicNameValuePair("reqData", requestParam.get("reqData"));
			BasicNameValuePair n4 = new BasicNameValuePair("keySerial", requestParam.get("keySerial"));
			BasicNameValuePair n5 = new BasicNameValuePair("sign", requestParam.get("sign"));
			formParams.add(n1);
			formParams.add(n2);
			formParams.add(n3);
			formParams.add(n4);
			formParams.add(n5);
			response = HttpUtils.httpPostWithPAaram(SERVICE_URL, formParams);
			lmresult = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			logger.debug("lmresult:{}", lmresult);

		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		ResponsePojo handler = ResponsePojoUtil.handler(lmresult);
		// 验签
		String code = handler.getCode();
		String status = handler.getStatus();
		try {
			if (!"0".equals(code) || !"SUCCESS".equals(status)) {
				logger.debug("接口返回code!=0 || status!=SUCCESS时，不做验签处理");
			} else {
				if (!VerifySignUtils.verifySign(response, lmresult)) {
					result.put("code", "1");
					result.put("status", "INIT");
					result.put("errorCode", "1");
					result.put("errorMessage", "sign fail");
					return result;
				}
			}
		} catch (Exception e) {
			logger.error("sign fail..... " + e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "local exception");
			return result;
		}

		result.put("code", code);
		result.put("status", status);
		result.put("errorCode", handler.getErrorCode());
		result.put("errorMessage", handler.getErrorMessage());
		return result;
	}

}
