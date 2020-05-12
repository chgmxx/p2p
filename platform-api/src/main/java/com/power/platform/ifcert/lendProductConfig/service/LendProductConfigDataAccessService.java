package com.power.platform.ifcert.lendProductConfig.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.LendProductConfig;
import com.power.platform.ifcert.dao.LendProductConfigDao;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.dao.UserInfoDao;

/**
 * 
 * class: CreditorDataAccessService <br>
 * description: 初始债权. <br>
 * 说明：（1）初始债权是针对出借人而言，指出借人投资一个原始散标而生成的对该借款人的债权，这里的初始债权必须是在出借债权。<br>
 * （2）对于存量数据而言，存量数据不需要上报历史转让信息，即所有未完结债权均视为初始债权，无需追溯其历史转让，历史承接债权均视为完结债权，不用上报。<br>
 * （3）上报初始债权数据触发时间：初始债权对应的散标放款成功之后上报该数据。<br>
 * (4)针对表里的“锁定期”，有一种场景需要注意：如果多个散标对应一个产品（出借人购买），其中某个散标在该产品锁定期内到期，那该散标则传实际到期时间。
 * 例：产品A 锁定期为2019年1月1日-2019年3月30日，产品A对应10个散标，10个散标中有一个叫b散标，b 散标在2019年2月1日到期，
 * 则b散标对应的初始债权锁定截止日期传2019年2月1日。为保持产品A正常运转，平台会将撮合一个新的散标c以替换b散标，c散标到期日期大于产品A锁定期，
 * 该c 散标对应的初始债权锁定截止日期传2019年3月30日。<br>
 * author: Roy <br>
 * date: 2019年5月14日 下午3:51:09
 */
@Service("lendProjectConfigDataAccessService")
public class LendProductConfigDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(LendProductConfigDataAccessService.class);

	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private BatchNumDao batchNumDao;
	@Resource
	private LendProductConfigDao lendProductConfigDao;
	@Resource
	private UserInfoDao userInfoDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushLendProductConfigInfo <br>
	 * description: 补推产品配置信息. <br>
	 * author: Roy <br>
	 * date: 2019年7月26日 下午7:00:37
	 * 
	 * @param lpConfigs
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushLendProductConfigInfo(List<LendProductConfig> lpConfigs) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			for (LendProductConfig lpConfig : lpConfigs) {
				String configId = lpConfig.getConfigId(); // 产品配置编号（出借信息ID）.
				String finClaimId = lpConfig.getFinClaimId();// 初始债权（出借信息ID）.
				String sourceFinancingCode = lpConfig.getSourceFinancingCode(); // 产品信息编号（标的编号）.
				String userIdcardHash = lpConfig.getUserIdcardHash(); // 出借用户证件号Hash值.
				String sourceProductCode = lpConfig.getSourceProductCode(); // 散标信息编号（标的编号）.
				// 数据中心初始债权表.
				LendProductConfig productConfig = new LendProductConfig();
				/**
				 * 产品配置信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				productConfig.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				productConfig.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("configId", configId); // 产品配置编号.
				productConfig.setConfigId(configId);
				param.put("finClaimId", finClaimId); // 初始债权（出借信息ID）.
				productConfig.setFinClaimId(finClaimId);
				param.put("sourceFinancingCode", sourceFinancingCode); // 产品信息编号.
				productConfig.setSourceFinancingCode(sourceFinancingCode);
				param.put("userIdcardHash", userIdcardHash); // 出借用户证件号hash值.
				productConfig.setUserIdcardHash(userIdcardHash);
				param.put("sourceProductCode", sourceProductCode); // 散标信息编号.
				productConfig.setSourceProductCode(sourceProductCode);
				productConfig.setBatchNum(batchNum);
				productConfig.setSendTime(sentTime);
				int insert = lendProductConfigDao.insert(productConfig);
				if (insert == 1) {
					log.info("产品配置信息插入成功！");
				} else {
					log.info("产品配置信息插入失败！");
				}
				list.add(param);
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_87.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PRODUCT_CONFIG, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}
		return result;
	}

	/**
	 * 
	 * methods: pushCreditorInfo <br>
	 * description: 推送产品配置. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月14日 下午3:49:11
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushLendProductConfigInfo(List<String> projectIdList) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {
			WloanTermProject project = null;
			for (String projectId : projectIdList) {
				project = wloanTermProjectDao.get(projectId);
				if (null != project) {
					if (WloanTermProjectService.REPAYMENT.equals(project.getState()) || WloanTermProjectService.FINISH.equals(project.getState())) {
						List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(projectId);
						for (WloanTermInvest invest : investList) {
							// 数据中心初始债权表.
							LendProductConfig productConfig = new LendProductConfig();
							/**
							 * 产品配置信息封装.
							 */
							Map<String, Object> param = new LinkedHashMap<String, Object>();
							param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
							productConfig.setVersion(ServerURLConfig.VERSION);
							param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
							productConfig.setSourceCode(ServerURLConfig.SOURCE_CODE);
							param.put("configId", invest.getId()); // 产品配置编号.
							productConfig.setConfigId(invest.getId());
							param.put("finClaimId", invest.getId()); // 散标信息编号.
							productConfig.setFinClaimId(invest.getId());
							param.put("sourceFinancingCode", invest.getWloanTermProject().getSn()); // 产品信息编号.
							productConfig.setSourceFinancingCode(invest.getWloanTermProject().getSn());
							param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo()))); // 出借用户证件号hash值.
							productConfig.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo())));
							param.put("sourceProductCode", invest.getWloanTermProject().getSn()); // 散标信息编号.
							productConfig.setSourceProductCode(invest.getWloanTermProject().getSn());
							productConfig.setBatchNum(batchNum);
							productConfig.setSendTime(sentTime);
							int insert = lendProductConfigDao.insert(productConfig);
							if (insert == 1) {
								log.info("产品配置信息插入成功！");
							} else {
								log.info("产品配置信息插入失败！");
							}
							list.add(param);
						}
					}
				}
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_87.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PRODUCT_CONFIG, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_87.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}

}
