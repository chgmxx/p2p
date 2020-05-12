package com.power.platform.lanmao.search.service;

import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.search.utils.ResultVOUtil;
import com.power.platform.lanmao.search.pojo.*;
import com.power.platform.lanmao.type.*;
import com.power.platform.sys.utils.HttpUtil;

/**
 * 单笔交易查询-充值,提现,交易预处理,交易确认,冻结,债权出让,取消预处理,解冻,提现拦截,调整平台垫资额度
 * @author fuwei
 *
 */
@Service("lanMaoSearchOneDealDataService")
public class LanMaoSearchOneTransactionDataService {
	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");
	private static final Logger log = LoggerFactory.getLogger(LanMaoSearchProjectDataService.class);

	public Map<String, Object> searchOneTransaction(String requestNo,String transactionType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();

       SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       SimpleDateFormat sdt2 = new SimpleDateFormat("yyyyMMddHHmmss");
		try {

			// 定义reqData参数集合：业务的请求流水号+交易查询类型
			Map<String, String> reqDataMap = new HashMap<String, String>();
			reqDataMap.put("requestNo", requestNo);
			reqDataMap.put("transactionType", transactionType);
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			// logger.debug("reques data json:{}", JSON.toJSONString(reqDataMap));
			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.QUERY_TRANSACTION.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			// logger.debug("response json:{}", responseStr);

			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if(jsonObject.getString("code").equals("0")&&jsonObject.getString("status").contains("SUCCESS")) {
				result.put("code", ResultVOUtil.code(jsonObject.getString("code")));
				result.put("transactionType", transactionType);
				result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");		
				if(TransactionTypeEnum.RECHARGE.getValue().equals(transactionType)) {	//充值				
					List<LanMaoRechange> rechanges = null;
					if(jsonObject.getString("records") != null) {
						rechanges = JSONObject.parseArray(jsonObject.getString("records"), LanMaoRechange.class);
						for(LanMaoRechange rechange : rechanges) {
							rechange.setRechargeWay(RechargeWayEnum.getTextByValue(rechange.getRechargeWay()));
							rechange.setPayCompany(ExpectPayCompanyEnum.getTextByValue(rechange.getPayCompany()));
							if(rechange.getCreateTime()!=null)
							rechange.setCreateTime(sdt.format(sdt2.parse(rechange.getCreateTime())));
							if(rechange.getTransactionTime()!=null)
							rechange.setTransactionTime(sdt.format(sdt2.parse(rechange.getTransactionTime())));
						}
					}
				    result.put("records", jsonObject.getString("records") != null ? rechanges: "");
				}else if(TransactionTypeEnum.WITHDRAW.getValue().equals(transactionType)) {//提现
					List<LanMaoWithDraw> withDraws = null;
					if(jsonObject.getString("records") != null) {
						withDraws = JSONObject.parseArray(jsonObject.getString("records"), LanMaoWithDraw.class);
						for(LanMaoWithDraw withDraw : withDraws) {
							withDraw.setStatus(WithdrawStatusEnum.getTextByValue(withDraw.getStatus()));
							withDraw.setWithdrawForm(WithdrawFormEnum.getTextByValue(withDraw.getWithdrawForm()));
							withDraw.setRemitType(RemitTypeRnum.getTextByValue(withDraw.getRemitType()));
							withDraw.setWithdrawWay(WithdrawWayEnum.getTextByValue(withDraw.getWithdrawWay()));	
							if(withDraw.getCreateTime()!=null)
							withDraw.setCreateTime(sdt.format(sdt2.parse(withDraw.getCreateTime())));
							if(withDraw.getTransactionTime()!=null)
							withDraw.setTransactionTime(sdt.format(sdt2.parse(withDraw.getTransactionTime())));
							if(withDraw.getRemitTime()!=null)
								withDraw.setRemitTime(sdt.format(sdt2.parse(withDraw.getRemitTime())));
							if(withDraw.getCompletedTime() !=null)
								withDraw.setCompletedTime(sdt.format(sdt2.parse(withDraw.getCompletedTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? withDraws: "");
				}else if(TransactionTypeEnum.PRETRANSACTION.getValue().equals(transactionType)) {//交易预处理
					List<LanMaoPretransaction> pretransactions = null;
					if(jsonObject.getString("records") != null) {
						pretransactions = JSONObject.parseArray(jsonObject.getString("records"), LanMaoPretransaction.class);	
						for(LanMaoPretransaction pretransaction:pretransactions) {
							pretransaction.setBizType(BizTypeEnum.getTextByValue(pretransaction.getBizType()));
							if(pretransaction.getCreateTime()!=null)
								pretransaction.setCreateTime(sdt.format(sdt2.parse(pretransaction.getCreateTime())));
							if(pretransaction.getTransactionTime()!=null)
								pretransaction.setTransactionTime(sdt.format(sdt2.parse(pretransaction.getTransactionTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? pretransactions: "");
				}else if(TransactionTypeEnum.TRANSACTION.getValue().equals(transactionType)) {//交易确认
					List<LanMaoTransaction> transactions = null;
					if(jsonObject.getString("records") != null) {
						transactions = JSONObject.parseArray(jsonObject.getString("records"), LanMaoTransaction.class);	
						for(LanMaoTransaction transaction:transactions) {
							transaction.setConfirmTradeType(TransactionTypeEnum.getTextByValue(transaction.getConfirmTradeType()));
							if(transaction.getCreateTime()!=null)
								transaction.setCreateTime(sdt.format(sdt2.parse(transaction.getCreateTime())));
							if(transaction.getTransactionTime()!=null)
								transaction.setTransactionTime(sdt.format(sdt2.parse(transaction.getTransactionTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? transactions: "");
				}else if(TransactionTypeEnum.FREEZE.getValue().equals(transactionType)) {//冻结
					List<LanMaoFreeze> freezes = null;
					if(jsonObject.getString("records") != null) {
						freezes = JSONObject.parseArray(jsonObject.getString("records"), LanMaoFreeze.class);	
						for(LanMaoFreeze freeze:freezes) {
							freeze.setStatus(FreezeStatusEnum.getTextByValue(freeze.getStatus()));
							if(freeze.getCreateTime()!=null)
								freeze.setCreateTime(sdt.format(sdt2.parse(freeze.getCreateTime())));
							if(freeze.getTransactionTime()!=null)
								freeze.setTransactionTime(sdt.format(sdt2.parse(freeze.getTransactionTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? freezes: "");
				}else if(TransactionTypeEnum.DEBENTURE_SALE.getValue().equals(transactionType)){//债权出让
					List<LanMaoDebentureSale> debentureSales = null;
					if(jsonObject.getString("records") != null) {
						debentureSales = JSONObject.parseArray(jsonObject.getString("records"), LanMaoDebentureSale.class);	
						for(LanMaoDebentureSale debentureSale:debentureSales) {
							debentureSale.setStatus(DebentureSaleStatusEnum.getTextByValue(debentureSale.getStatus()));
							if(debentureSale.getCreateTime()!=null)
								debentureSale.setCreateTime(sdt.format(sdt2.parse(debentureSale.getCreateTime())));
							if(debentureSale.getTransactionTime()!=null)
								debentureSale.setTransactionTime(sdt.format(sdt2.parse(debentureSale.getTransactionTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? debentureSales: "");
				}else if(TransactionTypeEnum.CANCEL_PRETRANSACTION.getValue().equals(transactionType)) {//取消预处理
					List<LanMaoCancelPertranction> cancelPertranctions = null;
					if(jsonObject.getString("records") != null) {
						cancelPertranctions = JSONObject.parseArray(jsonObject.getString("records"), LanMaoCancelPertranction.class);	
						for(LanMaoCancelPertranction cancelPertranction:cancelPertranctions) {
							if(cancelPertranction.getCreateTime()!=null)
								cancelPertranction.setCreateTime(sdt.format(sdt2.parse(cancelPertranction.getCreateTime())));
							if(cancelPertranction.getTransactionTime()!=null)
								cancelPertranction.setTransactionTime(sdt.format(sdt2.parse(cancelPertranction.getTransactionTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? cancelPertranctions: "");
				}else if(TransactionTypeEnum.UNFREEZE.getValue().equals(transactionType)) {//解冻
					List<LanMaoUnFreeze> freezes = null;
					if(jsonObject.getString("records") != null) {
						freezes = JSONObject.parseArray(jsonObject.getString("records"), LanMaoUnFreeze.class);	
						for(LanMaoUnFreeze freeze:freezes) {
							if(freeze.getCreateTime()!=null)
								freeze.setCreateTime(sdt.format(sdt2.parse(freeze.getCreateTime())));
							if(freeze.getTransactionTime()!=null)
								freeze.setTransactionTime(sdt.format(sdt2.parse(freeze.getTransactionTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? freezes: "");
				}else if(TransactionTypeEnum.INTERCEPT_WITHDRAW.getValue().equals(transactionType)) {//提现拦截
					List<LanMaoInterceptWithDraw> interceptWithDraws = null;
					if(jsonObject.getString("records") != null) {
						interceptWithDraws = JSONObject.parseArray(jsonObject.getString("records"), LanMaoInterceptWithDraw.class);	
						for(LanMaoInterceptWithDraw interceptWithDraw:interceptWithDraws) {
							interceptWithDraw.setStatus(StatusEnum.getTextByValue(interceptWithDraw.getStatus()));
							if(interceptWithDraw.getCreatTime()!=null)
								interceptWithDraw.setCreatTime(sdt.format(sdt2.parse(interceptWithDraw.getCreatTime())));
							if(interceptWithDraw.getCompletedTime()!=null)
								interceptWithDraw.setCompletedTime(sdt.format(sdt2.parse(interceptWithDraw.getCompletedTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? interceptWithDraws: "");
				}else if(TransactionTypeEnum.ADJUST_URGENT_BALANCE.getValue().equals(transactionType)) {//调整平台垫资额度
					List<LanMaoAdjustUrgentBalance> adjustUrgentBalances = null;
					if(jsonObject.getString("records") != null) {
						adjustUrgentBalances = JSONObject.parseArray(jsonObject.getString("records"), LanMaoAdjustUrgentBalance.class);	
						for(LanMaoAdjustUrgentBalance adjustUrgentBalance:adjustUrgentBalances) {
							adjustUrgentBalance.setUrgentAccountAdjustStatus(UrgentAccountAdjustStatusEnum.getTextByValue(adjustUrgentBalance.getUrgentAccountAdjustStatus()));
							adjustUrgentBalance.setAccountAdjustType(AccountAdjustTypeEnum.getTextByValue(adjustUrgentBalance.getAccountAdjustType()));
							if(adjustUrgentBalance.getCreateTime()!=null)
				 				adjustUrgentBalance.setCreateTime(sdt.format(sdt2.parse(adjustUrgentBalance.getCreateTime())));
							if(adjustUrgentBalance.getTransactionTime()!=null)
								adjustUrgentBalance.setTransactionTime(sdt.format(sdt2.parse(adjustUrgentBalance.getTransactionTime())));
							if(adjustUrgentBalance.getTransferTime()!=null)
								adjustUrgentBalance.setTransferTime(sdt.format(sdt2.parse(adjustUrgentBalance.getTransferTime())));
							if(adjustUrgentBalance.getTransferCompletedTime()!=null)
								adjustUrgentBalance.setTransferCompletedTime(sdt.format(sdt2.parse(adjustUrgentBalance.getTransferCompletedTime())));
						}
					}
					result.put("records", jsonObject.getString("records") != null ? adjustUrgentBalances: "");
				}else 
					result.put("records", "");				
			}else{
				result.put("code", jsonObject.getString("code") != null ?ResultVOUtil.code(jsonObject.getString("code")): "");
				result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
				result.put("errorCode", jsonObject.getString("errorCode") != null ?  ResultVOUtil.getErrorCode(jsonObject.getString("errorCode")) : "");
				result.put("errorMessage", jsonObject.getString("errorMessage") != null ? jsonObject.getString("errorMessage") : "");
			}			
			return result;
		} catch (Exception e) {
			log.error("Exception:{}", e.getMessage());
			result.put("code", "1");
			result.put("status", "INIT");
			result.put("errorCode", "1");
			result.put("errorMessage", "调用失败");
			return result;
		}	
	}
}
