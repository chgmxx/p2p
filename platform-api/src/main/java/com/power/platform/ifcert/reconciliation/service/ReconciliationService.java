package com.power.platform.ifcert.reconciliation.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.ifcert.dao.BatchNumCheckDao;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.BatchNumCheck;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.utils.http.HttpsUtil;
import com.power.platform.ifcert.utils.sha.ShaApiKey;



@Service("reconciliationService")
public class ReconciliationService {

	private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

	@Resource
	private BatchNumDao batchNumDao;
	
	@Resource
	private BatchNumCheckDao batchNumCheckDao;
	
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * @Title: checkReconciliation
	 * @Description:批次异步消息接口
	 * @Author: yangzf 
	 * @param @return
	 * @return Map<String,Object>
	 * @DateTime 2019年6月28日  下午2:26:21
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> checkReconciliation() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		int count =0;
		try {
			BatchNum bn = new BatchNum();
			bn.setCode("0000");
			bn.setStatus("01");
			List<BatchNum> batchNumList = batchNumDao.fingBatchNumList(bn);
			if(batchNumList!=null && batchNumList.size()!=0) {
				for (BatchNum batchNum : batchNumList) {
					Map<String, String> params = new HashMap<String, String>();
					Long timestamp = System.currentTimeMillis();
					String nonce = Integer.toHexString(new Random().nextInt());
					JSONObject json = new JSONObject();
					String token = ShaApiKey.getApiKey(ServerURLConfig.API_KEY, ServerURLConfig.SOURCE_CODE, ServerURLConfig.VERSION, timestamp, nonce);
					params.put("apiKey", token);
					params.put("dataType", "1");
					params.put("batchNum", batchNum.getBatchNum());
					params.put("timestamp", timestamp +"");
					params.put("nonce", nonce);
					params.put("sourceCode", ServerURLConfig.SOURCE_CODE);
					params.put("version", ServerURLConfig.VERSION);
					params.put("infType", batchNum.getInfType());
					
					String responseStr = HttpsUtil.sendHttpsGet(ServerURLConfig.RECONCILIATION_MESSAGE_URL,params);
					if(responseStr!=null&&responseStr!="") {
						responseStr = responseStr.replaceAll("\\u005c", "");
						responseStr = responseStr.substring(1, responseStr.length()-1);
					}
//					String jsonStr = "{\"watermark\":{\"timestamp\":15204068,\"appid\":\"wx15b96f75eba141\"},\"phoneNumber\":\"176823109\",\"countryCode\":\"86\",\"purePhoneNumber\":\"176823109\"}";
					JSONObject jsonObject = (JSONObject) JSON.parseObject(responseStr);
					if (jsonObject != null) {
						String code = (String) jsonObject.get("code");
						String message = (String) jsonObject.get("message");
						log.info("code:{},message:{}",code,message);
						if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功入库.
							JSONArray playInfo = (JSONArray) jsonObject.get("result");
							for(Iterator iterator = playInfo.iterator();iterator.hasNext();) {
								JSONObject jsonObject1 = (JSONObject) iterator.next();
								String errorMsg = jsonObject1.get("errorMsg") + "";
								if("success".equals(errorMsg)) {
									batchNum.setStatus("00");
									int n = batchNumDao.update(batchNum);
									if (n == 1) {
										log.info("批次数:"+batchNum.getBatchNum()+"数据插入成功！");
									} else {
										log.info("批次数:"+batchNum.getBatchNum()+"数据插入失败！");
									}
									count++;
								} else if("failed".equals(errorMsg)||"isNot".equals(errorMsg)) {
									String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
									BatchNumCheck bnc = new BatchNumCheck();
									bnc.setId(IdGen.uuid());
									bnc.setBatchNum(batchNum.getBatchNum());
									bnc.setCreateTime(sentTime);
									bnc.setInfType(batchNum.getInfType());
									bnc.setErrorMessage(errorMsg);
									bnc.setCode(code);
									bnc.setMessage(message);
									bnc.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
									int insert = batchNumCheckDao.insert(bnc);
									if (insert == 1) {
										log.info("批次数:"+batchNum.getBatchNum()+"数据插入成功！");
									} else {
										log.info("批次数:"+batchNum.getBatchNum()+"数据插入失败！");
									}
								}
							}
							
						}else {
							String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
							BatchNumCheck bnc = new BatchNumCheck();
							bnc.setId(IdGen.uuid());
							bnc.setBatchNum(batchNum.getBatchNum());
							bnc.setCreateTime(sentTime);
							bnc.setInfType(batchNum.getInfType());
							bnc.setMessage(message);
							bnc.setCode(code);
							bnc.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
							int insert = batchNumCheckDao.insert(bnc);
						}
					}
				}
			}
			if(batchNumList.size()==count) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}
		return result;
	}
	
}
