package com.power.platform.lanmao.rw.service;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.json.JSONArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.config.Global;
import com.power.platform.common.exception.WinException;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.common.SignatureAlgorithm;
import com.power.platform.lanmao.common.SignatureUtils;
import com.power.platform.lanmao.rw.pojo.RechargeVo;
import com.power.platform.lanmao.rw.utils.ResultVO;
import com.power.platform.lanmao.rw.utils.ResultVOUtil;
import com.power.platform.lanmao.type.ExpectPayCompanyEnum;
import com.power.platform.lanmao.type.RechargeWayEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.pay.recharge.service.NewRechargeService;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.entity.UserLog;

import common.toolkit.java.util.StringUtil;

/**
 * 用户在网贷平台发起充值请求，平台调用此接口引导用户跳转至存管页面完成充值
 * R1.用户在网贷平台选择充值方式和银行；
	R2.如用户选择网银支付方式，网贷平台调用此接口，用户直接跳转至银行页面完成充值；如用户选择快捷支
	付方式，网贷平台调用此接口，用户跳转至存管快捷支付页面完成充值；
	R3.平台功能账户可以调用此接口进行网银充值，platformUserNo 传入对应【平台用户编号】即可；
	R4.用户充值实际到账金额=充值金额-平台佣金，平台佣金不得超过充值金额；
	R5.网银充值：网银直连模式（直接跳转银行页面充值）下平台必填【银行编码】【网银类型】；网银是否支
	持银行直连视支付公司而定；
	R6.若传入【标的号】、【授权出借金额】、【交易类型】（固定为“TENDER”），充值成功，则单次授权
	授权出借成功；可操作未主动授权的用户做一次出借授权预处理，出借金额需小于等于授权出借金额，标的
	号须一致。
	R7.商户自定义快捷路由只适用于快捷支付，如商户传入则返回相应枚举值，如不传则不返回。
 * @author chenhj ant-loiter.com
 * 充值
 */
@Service("rechargeService")
public class RechargeService {

	private final static Logger logger = LoggerFactory.getLogger(RechargeService.class);
	
	@Autowired 
	private NewRechargeService newRechargeService;
	
	/**
	 * 充值 重载  默认设计超时时间为30分
	 * @param platFormUserNo  平台用户编号
	 * @param requestNo  请求流水号
	 * @param amount 充值金额
	 * @param commission  平台佣金
	 * @param rechargeWay 【支付方式】，支持网银（WEB）、快捷支付（SWIFT）
	 * @param bankCode 【见银行编码】若支付方式为快捷支付，此处必填；若支付方式为网银，此处可以
						不填；网银支付方式下，若此处填写，则直接跳转至银行页面，不填则跳转至支付
						公司收银台页面；
	 * @param payType   【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
	 * @param redirectUrl   页面回跳 URL
	 * @param userDevice   设备  PC  MOBILE
	 * @param AccountId  
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> doRechargeR2(String ip,
											String platFormUserNo, 
											String requestNo,
											Double amount,
											Double commission,
											String rechargeWay,
											String payType,
											String bankCode,
											String redirectUrl,
											String accountId) throws WinException, Exception{
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("platformUserNo", platFormUserNo);
		signMap.put("requestNo", requestNo);
		signMap.put("amount", String.valueOf(amount)); 
		if(commission <= 0.01) {
			;
		}else {
			signMap.put("commission", String.valueOf(commission)); // 平台佣金
		}
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany")); // 易宝
		signMap.put("rechargeWay", rechargeWay); // 支付方式
		if(!StringUtil.isBlank(payType)) {
			signMap.put("payType", payType);//【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
		}
		if(!StringUtil.isBlank(bankCode)) {
			signMap.put("bankcode", bankCode); // 如果支付方式选择的是快捷支持， 这个必填 ；
		}
		signMap.put("redirectUrl", redirectUrl);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
		
//		Map<String, Object> paramMap = AppUtil.lmGeneratePostParam(ServiceNameEnum.RECHARGE.getValue(), signMap);
		
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
//		paramMap.put("userDevice", AppUtil.CheckStringByDefault(userDevice, "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		// 记录充值信息 , 流水修改到充值回调里了；
		try {
			newRechargeService.insertRechargeInit(Double.valueOf(amount), ip, requestNo, platFormUserNo, accountId);
		}catch(Exception e) {
			e.printStackTrace();
			paramMap.put("state", "err");
		}
		return paramMap;
	
	}
	
	
	/**
	 * 充值 重载  默认设计超时时间为30分
	 * @param platFormUserNo  平台用户编号
	 * @param requestNo  请求流水号
	 * @param amount 充值金额
	 * @param commission  平台佣金
	 * @param rechargeWay 【支付方式】，支持网银（WEB）、快捷支付（SWIFT）
	 * @param bankCode 【见银行编码】若支付方式为快捷支付，此处必填；若支付方式为网银，此处可以
						不填；网银支付方式下，若此处填写，则直接跳转至银行页面，不填则跳转至支付
						公司收银台页面；
	 * @param payType   【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
	 * @param redirectUrl   页面回跳 URL
	 * @param userDevice   设备  PC  MOBILE
	 * @param AccountId  
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> doRechargeR2(String ip,
											String platFormUserNo, 
											String requestNo,
											Double amount,
											Double commission,
											String rechargeWay,
											String redirectUrl,
											String accountId) throws WinException, Exception{
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("platformUserNo", platFormUserNo);
		signMap.put("requestNo", requestNo);
		signMap.put("amount", String.valueOf(amount)); 
		if(commission <= 0.01) {
			;
		}else {
			signMap.put("commission", String.valueOf(commission)); // 平台佣金
		}
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany")); // 易宝
		signMap.put("rechargeWay", rechargeWay); // 支付方式
//		signMap.put("bankcode", bankCode); // 如果支付方式选择的是快捷支持， 这个必填 ；
//		signMap.put("payType", payType);//【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
		signMap.put("redirectUrl", redirectUrl);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
		
//		Map<String, Object> paramMap = AppUtil.lmGeneratePostParam(ServiceNameEnum.RECHARGE.getValue(), signMap);
		
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
//		paramMap.put("userDevice", AppUtil.CheckStringByDefault(userDevice, "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		// 记录充值信息, 初始记录
		try {
			newRechargeService.insertRechargeInit(Double.valueOf(amount), ip, requestNo, platFormUserNo, accountId);
		}catch(Exception e) {
			e.printStackTrace();
			paramMap.put("state", "err");
		}
		return paramMap;
	
	}
	
	/**
	 * 充值 重载  默认设计超时时间为30分
	 * @param platFormUserNo  平台用户编号
	 * @param requestNo  请求流水号
	 * @param amount 充值金额
	 * @param commission  平台佣金
	 * @param rechargeWay 【支付方式】，支持网银（WEB）、快捷支付（SWIFT）
	 * @param bankCode 【见银行编码】若支付方式为快捷支付，此处必填；若支付方式为网银，此处可以
						不填；网银支付方式下，若此处填写，则直接跳转至银行页面，不填则跳转至支付
						公司收银台页面；
	 * @param payType   【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
	 * @param redirectUrl   页面回跳 URL
	 * @param userDevice   设备  PC  MOBILE
	 * @param AccountId  
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> doRechargeSwift(String ip,
											String platFormUserNo, 
											String requestNo,
											String bankNo,
											Double amount,
											String redirectUrl,
											String accountId) throws WinException, Exception{
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("platformUserNo", platFormUserNo); 
		signMap.put("requestNo", requestNo);
		signMap.put("amount", String.valueOf(amount)); 
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany")); // 易宝
		signMap.put("rechargeWay", RechargeWayEnum.SWIFT.getValue()); // 支付方式
		signMap.put("bankcode", bankNo); // 如果支付方式选择的是快捷支持， 这个必填 ；
		signMap.put("redirectUrl", redirectUrl);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
		//添加快捷充值回调模式：
//		signMap.put("callbackMode", "DIRECT_CALLBACK");

		String _singMapStr = JSON.toJSONString(signMap);
		StringBuilder sb = new StringBuilder(_singMapStr.substring(0, _singMapStr.length() -1));
		sb.append(",");
		sb.append("riskitem:{\"merEquipmentIp\":\"");
		sb.append(ip.replace("_", "."));
		sb.append("\"}");
		sb.append("}");
		logger.info("##### {}", sb.toString());
		String _sign = AppUtil.signParamStr(sb.toString());
		
		
//		String _sign = AppUtil.signParam(signMap);
//		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
		paramMap.put("reqData", sb.toString().replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		// 记录充值信息, 初始记录
		try {
			newRechargeService.insertRechargeInit(Double.valueOf(amount), ip, requestNo, platFormUserNo, accountId);
		}catch(Exception e) {
			e.printStackTrace();
			throw new WinException("添加充值订单异常，充值失败！"); 
		}
		return paramMap;
	}
	
	/**
	 * 充值 重载  默认设计超时时间为30分
	 * @param platFormUserNo  平台用户编号
	 * @param requestNo  请求流水号
	 * @param amount 充值金额
	 * @param commission  平台佣金
	 * @param rechargeWay 【支付方式】，支持网银（WEB）、快捷支付（SWIFT）
	 * @param bankCode 【见银行编码】若支付方式为快捷支付，此处必填；若支付方式为网银，此处可以
						不填；网银支付方式下，若此处填写，则直接跳转至银行页面，不填则跳转至支付
						公司收银台页面；
	 * @param payType   【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
	 * @param redirectUrl   页面回跳 URL
	 * @param userDevice   设备  PC  MOBILE
	 * @param AccountId  
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> doRechargeWEB(String ip,
											String platFormUserNo, 
											String requestNo,
											Double amount,
											String isBankCode,
											String bankNo,
											String redirectUrl,
											String accountId) throws WinException, Exception{
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("platformUserNo", platFormUserNo);
		signMap.put("requestNo", requestNo);
		signMap.put("amount", String.valueOf(amount)); 
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany"));//ExpectPayCompanyEnum.YEEPAY.getValue()); // 易宝
		signMap.put("rechargeWay", RechargeWayEnum.WEB.getValue()); // 支付方式
		//通过用户id查询出用户对应的银行编码, 
		if("1".equals(isBankCode)) {
			signMap.put("bankcode", bankNo); // 如果支付方式选择的是快捷支持， 这个必填 ；  
			if(redirectUrl.indexOf("rechargeWebNotifyH5") != -1) {// 企业
				signMap.put("payType", "B2B");
			} else {
				signMap.put("payType", "B2C");
			}
		}
		signMap.put("redirectUrl", redirectUrl);
//		StringBuilder _riskItemJson = new StringBuilder("{\"merEquipmentIp\":\"");
//		_riskItemJson.append(ip.replace("_", "."));
//		_riskItemJson.append("\"}");
//		Map<String,String> _ip = new HashMap<>();
//		_ip.put("merEquipmentIp", ip.replace("_", "."));
//		signMap.put("riskitem", JSON.toJSONString(_ip));
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
//		JSONObject _ip = new JSONObject();
//		_ip.put("merEquipmentIp", ip.replace("_", "."));
//		signMap.put("riskitem", JSON.toJSONString(_ip));
		String _singMapStr = JSON.toJSONString(signMap);
		StringBuilder sb = new StringBuilder(_singMapStr.substring(0, _singMapStr.length() -1));
		sb.append(",");
		sb.append("riskitem:{\"merEquipmentIp\":\"");
		sb.append(ip.replace("_", "."));
		sb.append("\"}");
		sb.append("}");
		logger.info("##### {}", sb.toString());
		String _sign = AppUtil.signParamStr(sb.toString());
//		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
		paramMap.put("reqData", sb.toString().replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		// 记录充值信息, 初始记录
		try {
			newRechargeService.insertRechargeInit(Double.valueOf(amount), ip, requestNo, platFormUserNo, accountId);
		}catch(Exception e) {
			e.printStackTrace();
			throw new WinException("添加充值订单异常，充值失败！"); 
		}
		return paramMap;
	}
	
	
	/**
	 * loan 专用方法
	 * @param ip
	 * @param platFormUserNo
	 * @param requestNo
	 * @param amount
	 * @param commission
	 * @param rechargeWay
	 * @param redirectUrl
	 * @param accountId
	 * @return
	 * @throws WinException
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> doRechargeR3(String ip,
											String platFormUserNo, 
											String requestNo,
											Double amount,
											Double commission,
											String rechargeWay,
											String redirectUrl,
											String accountId,
											String from ) throws WinException, Exception{
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		if("05".equals(from)) { // 平台营销账户充值 
			signMap.put("platformUserNo", Global.getConfigLanMao("sys_generate_002"));
		} else {
			signMap.put("platformUserNo", platFormUserNo);
		}
		signMap.put("requestNo", requestNo);
		signMap.put("amount", String.valueOf(amount)); 
		if(commission <= 0.01) {
			;
		}else {
			signMap.put("commission", String.valueOf(commission)); // 平台佣金
		}
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany")); // 易宝
		signMap.put("rechargeWay", rechargeWay); // 支付方式
//		signMap.put("bankcode", bankCode); // 如果支付方式选择的是快捷支持， 这个必填 ；
//		signMap.put("payType", payType);//【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
		signMap.put("redirectUrl", redirectUrl);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
		
//		paramMap.put("userDevice", AppUtil.CheckStringByDefault(userDevice, "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		// 记录充值信息
		try {
			newRechargeService.insertRechargeInit(Double.valueOf(amount), ip, requestNo, platFormUserNo, accountId);
		}catch(Exception e) {
			e.printStackTrace();
			paramMap.put("state", "err");
		}
		return paramMap;
	}
	
	/**
	 * 充值 重载  默认设计超时时间为30分
	 * @param platFormUserNo  平台用户编号
	 * @param requestNo  请求流水号
	 * @param amount 充值金额
	 * @param commission  平台佣金
	 * @param rechargeWay 【支付方式】，支持网银（WEB）、快捷支付（SWIFT）
	 * @param bankCode 【见银行编码】若支付方式为快捷支付，此处必填；若支付方式为网银，此处可以
						不填；网银支付方式下，若此处填写，则直接跳转至银行页面，不填则跳转至支付
						公司收银台页面；
	 * @param payType   【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
	 * @param redirectUrl   页面回跳 URL
	 * @param userDevice   设备  PC  MOBILE
	 * @param AccountId  
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> doRecharge(String ip,
											String platFormUserNo, 
											String requestNo,
											Double amount,
											Double commission,
											String rechargeWay,
											String redirectUrl,
											String userDevice,
											String accountId) throws WinException, Exception{
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("platformUserNo", platFormUserNo);
		signMap.put("requestNo", requestNo);
		signMap.put("amount", amount);
		signMap.put("commission", commission); // 平台佣金
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany")); // 易宝
		signMap.put("rechargeWay", rechargeWay); // 支付方式
//		signMap.put("bankcode", bankCode); // 如果支付方式选择的是快捷支持， 这个必填 ；
//		signMap.put("payType", payType);//【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
		signMap.put("redirectUrl", redirectUrl);
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
		paramMap.put("userDevice", AppUtil.CheckStringByDefault(userDevice, "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		logger.info("请求JSON： " + JSON.toJSONString(paramMap));
		// 记录充值信息
		newRechargeService.insertRechargeInit(Double.valueOf(amount), ip, requestNo, platFormUserNo, accountId);
				
		return paramMap;
	
	}
	
	

	/**
	 * 
	 * @param rechargeVo
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String>  doRecharge(String ip, UserInfo userInfo,  RechargeVo rv) throws WinException, Exception{
		
		if(rv == null) {
			return null;
		} 
		// 构造请求参数
		Map<String, Object> signMap = new HashMap<String, Object>();
		signMap.put("platformUserNo", rv.getPlatformUserNo());
		signMap.put("requestNo", rv.getRequestNo());
		signMap.put("amount", rv.getAmount());
		signMap.put("commission", rv.getCommission()); // 平台佣金
		signMap.put("expectPayCompany", Global.getConfigLanMao("expectPayCompany")); // 易宝
		signMap.put("rechargeWay", rv.getRechargeWay()); // 支付方式  
		signMap.put("bankcode", rv.getBankcode()); // 如果支付方式选择的是快捷支持， 这个必填 ；
		signMap.put("payType", rv.getPayType());//【网银类型】，若支付方式填写为网银，且对【银行编码】进行了填写，则此处必填。
		signMap.put("authtradeType", rv.getAuthtradeType()); // 【交易类型】，若想实现充值+出借单次授权，则此参数必传，固定“TENDER
		signMap.put("authtenderAmount ", rv.getAuthtenderAmount());// 授权出借金额，充值成功后可操作对应金额范围内的出借授权预处理；若传入了【交易类型】，则此参数必传
		signMap.put("projectNo", rv.getProjectNo());//
		signMap.put("swiftRoute", rv.getSwiftRoute());
		signMap.put("redirectUrl", rv.getRedirectUrl());
		signMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		// 计算页面过期时间 当前时间增加30分钟
		DateTime dateTime = new DateTime();
		signMap.put("expired", dateTime.plusMinutes(30).toString("yyyyMMddHHmmss"));
		signMap.put("callbackMode", rv.getCallbackMode());
//		signMap.put("redirectUrl", "http://222.249.226.103:8088/svc/services/cicnotify/notify.do");
		String _sign = AppUtil.signParam(signMap);
		String signMapTOString = JSON.toJSONString(signMap);
		// 定义reqData参数集合
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("serviceName", ServiceNameEnum.RECHARGE.getValue());
		paramMap.put("platformNo", Global.getConfigLanMao("platformNo"));
		paramMap.put("userDevice", AppUtil.CheckStringByDefault(rv.getUserDevice(), "PC"));
		paramMap.put("reqData", signMapTOString.replace("\\", ""));
		paramMap.put("keySerial", Global.getConfigLanMao("keySerial"));
		paramMap.put("sign", _sign);
		
		// 记录充值信息
		newRechargeService.insertRecharge(Double.valueOf(rv.getAmount()), ip, rv.getRequestNo(), rv.getPlatformUserNo(), userInfo.getAccountId());
		
		return paramMap;
//		/**
//		 * 发送HTTP或者 HTTPS请求
//		 * @param url 请求url
//		 * @param param 参数
//		 * @param header 头部参数
//		 * @param charset 编码方式
//		 */
//		Map<String, String> header = new HashMap<>();
//		String responseStr = HttpUtil.sendPost(Global.getConfigLanMao("gatewayUrl"), paramMap, header, "utf-8");
		
//		 return ResultVOUtil.success(responseStr);
		
	}
}
