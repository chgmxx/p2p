package com.power.platform.lanmao.search.service;

import java.security.PrivateKey;



import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.bank.BankEnum;
import com.power.platform.common.utils.bank.BankUtils;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.type.BankCodeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.lanmao.type.UserTypeEnum;
import com.power.platform.lanmao.type.WhiteStatusEnum;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.lanmao.search.pojo.LanMaoWhiteList;
import com.power.platform.lanmao.search.utils.ResultVOUtil;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteDao;
import com.power.platform.lanmao.dao.CgbBigrechargeWhiteRecordDao;
import com.power.platform.lanmao.entity.CgbBigrechargeWhite;
import com.power.platform.lanmao.entity.CgbBigrechargeWhiteRecord;

/**
 * 网银转账充值白名单删除
 * @author fuwei
 *
 */
@Service("lanMaoWhiteListDelDataService")
public class LanMaoWhiteListDelDataService {
	@Resource
	private CgbBigrechargeWhiteDao whiteDao;
	@Resource
	private CgbBigrechargeWhiteRecordDao cgbBigrechargeWhiteRecordDao;
	/**
	 * 平台编号
	 */
	private static final String PLATFORM_NO = Global.getConfigLanMao("platformNo");
	/**
	 * 直连请求地址
	 */
	private static final String SERVICE_URL = Global.getConfigLanMao("serviceUrl");

	private static final Logger log = LoggerFactory.getLogger(LanMaoWhiteListDelDataService.class);

	public Map<String, Object> whiteListDel(LanMaoWhiteList whitelist,String requestNo) {

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		try {			
			// 定义reqData参数集合：请求流水号（白名单 ID）+平台用户编号+银行卡号
			Map<String, String> reqDataMap = new HashMap<String, String>();			
			reqDataMap.put("requestNo", whitelist.getRequestNo());
			reqDataMap.put("platformUserNo", whitelist.getPlatformUserNo());
			reqDataMap.put("bankcardNo", whitelist.getBankcardNo());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqDataMap.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

			Map<String, String> requestMap = AppUtil.generatePostParam(ServiceNameEnum.ONLINE_WHITELIST_DEL.getValue(), reqDataMap);
			log.debug("reques json:{}", JSON.toJSONString(requestMap));

			Map<String, String> header = new HashMap<>();
			String responseStr = HttpUtil.sendPost(SERVICE_URL, requestMap, header, "utf-8");
			// logger.debug("response json:{}", responseStr);
           
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			log.info("删除白名单报文》》》"+jsonObject);
			if(jsonObject.getString("code").equals("0")&&jsonObject.getString("status").contains("SUCCESS")) {
			result.put("code",ResultVOUtil.code(jsonObject.getString("code")));
			result.put("status", jsonObject.getString("status") != null ? ResultVOUtil.status(jsonObject.getString("status")) : "");
			result.put("requestNo", jsonObject.getString("requestNo") != null ? jsonObject.getString("requestNo") : "");
			result.put("platformUserNo", jsonObject.getString("platformUserNo") != null ? jsonObject.getString("platformUserNo") : "");
			result.put("realName", jsonObject.getString("realName") != null ? jsonObject.getString("realName") : "");
			result.put("bankcardNo", jsonObject.getString("bankcardNo") != null ? jsonObject.getString("bankcardNo") : "");
			result.put("message", "成功删除白名单");
			
			String jsonStr = BankUtils.getCardDetail(whitelist.getBankcardNo());
			JSONObject json = JSONObject.parseObject(jsonStr);
			String validated = json.getString("validated");
			CgbBigrechargeWhite white =new CgbBigrechargeWhite();
			white.setBankNo(whitelist.getBankcardNo());
			List<CgbBigrechargeWhite> whites = whiteDao.findList(white);
			if(whites.size()>0) {
				log.info("白名单有数据");
				if(!whites.get(0).getStatus().equals(WhiteStatusEnum.BLACK)) {
					log.info("非黑名单执行-- 更新变白为黑");
				CgbBigrechargeWhite white2 =new CgbBigrechargeWhite();
				white2.setId(whites.get(0).getId());
				white2.setPlatformId(PLATFORM_NO);
				white2.setRealName(jsonObject.getString("realName"));
				white2.setUserId(jsonObject.getString("platformUserNo"));
				if("true".equals(validated)) {
			    	String bankName = BankEnum.getTextByValue(json.get("bank").toString());		    	 
				white2.setBankCode(BankCodeEnum.getTextByText(bankName));
				}else {
					white2.setBankCode(BankCodeEnum.getTextByText(null));
				}
				white2.setBankNo(whitelist.getBankcardNo());									
				white2.setStatus(WhiteStatusEnum.BLACK);
				white2.setUserRole(UserRoleEnum.getTextByValue(whitelist.getUserRole()));
				white2.setOperationDesc(null);
				white2.setUpdateDate(new Date());
				whiteDao.update(white2);
				}
			}else {
				log.info("没有找到白名单数据--添加一条黑名单");
				CgbBigrechargeWhite white2 =new CgbBigrechargeWhite();
				white2.setId(IdGen.uuid());
				white2.setPlatformId(PLATFORM_NO);
				white2.setRealName(jsonObject.getString("realName"));
				white2.setUserId(jsonObject.getString("platformUserNo"));
				if("true".equals(validated)) {
			    	String bankName = BankEnum.getTextByValue(json.get("bank").toString());		    	 
				    white2.setBankCode(BankCodeEnum.getTextByText(bankName));
				}else {
					white2.setBankCode(BankCodeEnum.getTextByText(null));
				}
				white2.setBankNo(whitelist.getBankcardNo());									
				white2.setStatus(WhiteStatusEnum.BLACK);
				white2.setUserRole(UserRoleEnum.getTextByValue(whitelist.getUserRole()));
				white2.setOperationDesc(null);
				white2.setCreateDate(new Date());
				white2.setUpdateDate(new Date());
				whiteDao.insert(white2);
			}
			
			CgbBigrechargeWhiteRecord whiteRecord = new CgbBigrechargeWhiteRecord();
			whiteRecord.setRequestNo(requestNo);
			List<CgbBigrechargeWhiteRecord> whiteRecordList = cgbBigrechargeWhiteRecordDao.findList(whiteRecord);
			if(whiteRecordList!=null && whiteRecordList.size() != 0) {
				CgbBigrechargeWhiteRecord whiteRecord1 = whiteRecordList.get(0);
				whiteRecord1.setStatus(WhiteStatusEnum.BLACK);
				whiteRecord1.setUpdateDate(new Date());
				int m = cgbBigrechargeWhiteRecordDao.update(whiteRecord1);
				log.info("变更白名单记录表信息:{}", m == 1 ? "成功" : "失败");
			}
			}else{
				result.put("code", jsonObject.getString("code") != null ? ResultVOUtil.code(jsonObject.getString("code")) : "");
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
