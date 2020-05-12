package com.power.platform.ifcert.lendparticulars.service;

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

import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.LendProductDao;
import com.power.platform.ifcert.dao.LendParticularsDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.LendParticulars;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.type.TransactTypeEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.regular.entity.WloanTermInvest;

/**
 * 
 * class: UserInfoDataAccess <br>
 * description: 用户信息数据访问，是指出借人、借款人、第三方担保公司、平台自身、
 * 受托支付方的相关属性信息。需要注意的是，借款人用户姓名需要推送明文. <br>
 * author: Roy <br>
 * date: 2019年4月22日 上午10:52:25
 */
@Service("lendParticularsDataAccessService")
public class LendParticularsDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(LendParticularsDataAccessService.class);

	@Resource
	private LendProductDao lendProductDao;
	@Resource
	private LendParticularsDao lendParticularsDao;
	@Resource
	private UserInfoDao userinfoDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private BatchNumDao batchNumDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushLendParticularsInvTransInfo <br>
	 * description: 补推投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月13日 下午4:03:32
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushLendParticularsInvTransInfo(List<LendParticulars> lendParticularList) {

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
			for (LendParticulars lp : lendParticularList) {
				// 投资明细.
				LendParticulars lendParticular = new LendParticulars();
				// 数据封装.
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				// 接口版本号.
				param.put("version", ServerURLConfig.VERSION);
				lendParticular.setVersion(ServerURLConfig.VERSION);
				// 网贷机构平台在应急中心系统的唯一编号，网贷机构在应急中心系统注册实名之后自动生成.
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				lendParticular.setSourceCode(ServerURLConfig.SOURCE_CODE);
				// 出借人每笔交易流水的唯一编号.
				param.put("transId", lp.getTransId());
				lendParticular.setTransId(lp.getTransId());
				// 产品编号.
				param.put("sourceFinancingCode", lp.getSourceFinancingCode());
				lendParticular.setSourceFinancingCode(lp.getSourceFinancingCode());
				// 交易类型.
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_2.getValue());
				lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_2.getValue());
				// 交易金额.
				param.put("transMoney",lp.getTransMoney());
				lendParticular.setTransMoney(lp.getTransMoney());
				// 出借用户证件号码Hash值
				param.put("userIdcardHash", lp.getUserIdcardHash()); // 出借用户证件号hash值.
				lendParticular.setUserIdcardHash(lp.getUserIdcardHash());
				// 交易发生时间.
				param.put("transTime",lp.getTransTime());
				lendParticular.setTransTime(lp.getTransTime());
				lendParticular.setBatchNum(batchNum); // 批次号.
				lendParticular.setSendTime(sentTime); // 发送时间.
				int insert = lendParticularsDao.insert(lendParticular);
				if (insert == 1) {
					log.info("投资明细-出借-插入成功！");
				} else {
					log.info("投资明细-出借-插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_88.getValue());// 投资明细接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			log.info("投资明细-出借-接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PARTICULARS, "utf-8");
			log.info("投资明细-出借-接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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
	 * methods: pushLendParticularsInvTransInfoC <br>
	 * description: 出借人-出借投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月13日 下午4:03:32
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTransInfo(List<WloanTermInvest> batchList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			for (WloanTermInvest invest : batchList) {
				// 投资明细.
				LendParticulars lendParticular = new LendParticulars();
				// 数据封装.
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				// 接口版本号.
				param.put("version", ServerURLConfig.VERSION);
				lendParticular.setVersion(ServerURLConfig.VERSION);
				// 网贷机构平台在应急中心系统的唯一编号，网贷机构在应急中心系统注册实名之后自动生成.
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				lendParticular.setSourceCode(ServerURLConfig.SOURCE_CODE);
				// 出借人每笔交易流水的唯一编号.
				param.put("transId", invest.getId());
				lendParticular.setTransId(invest.getId());
				// 产品编号.
				param.put("sourceFinancingCode", invest.getWloanTermProject().getSn());
				lendParticular.setSourceFinancingCode(invest.getWloanTermProject().getSn());
				// 交易类型.
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_2.getValue());
				lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_2.getValue());
				// 交易金额.
				param.put("transMoney", NumberUtils.scaleDoubleStr(invest.getAmount()));
				lendParticular.setTransMoney(NumberUtils.scaleDoubleStr(invest.getAmount()));
				// 出借用户证件号码Hash值
				param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo()))); // 出借用户证件号hash值.
				lendParticular.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo())));
				// 交易发生时间.
				param.put("transTime", DateUtils.formatDate(invest.getBeginDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setTransTime(DateUtils.formatDate(invest.getBeginDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setBatchNum(batchNum); // 批次号.
				lendParticular.setSendTime(sentTime); // 发送时间.
				int insert = lendParticularsDao.insert(lendParticular);
				if (insert == 1) {
					log.info("投资明细-出借-插入成功！");
				} else {
					log.info("投资明细-出借-插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_88.getValue());// 投资明细接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			log.info("投资明细-出借-接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PARTICULARS, "utf-8");
			log.info("投资明细-出借-接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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
	 * methods: pushInvestUserInfo <br>
	 * description: 投资明细(充提类，出借返现). <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月7日 下午4:00:24
	 * 
	 * @param userIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticulars(List<CgbUserTransDetail> cutdList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			/**
			 * 该批次出借人集合数据接口封装.
			 */
			for (CgbUserTransDetail userTransDetail : cutdList) {
				// 国家应急中心用户信息.
				LendParticulars lendParticular = new LendParticulars();
				/**
				 * 产品信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				/**
				 * 接口版本号.
				 */
				param.put("version", ServerURLConfig.VERSION);
				lendParticular.setVersion(ServerURLConfig.VERSION);
				/**
				 * 网贷机构平台在应急中心系统的唯一编号，
				 * 网贷机构在应急中心系统注册实名之后自动生成.
				 */
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				lendParticular.setSourceCode(ServerURLConfig.SOURCE_CODE);
				/**
				 * 出借人每笔交易流水的唯一编号.
				 */
				param.put("transId", userTransDetail.getTransId());
				lendParticular.setTransId(userTransDetail.getTransId());
				/**
				 * 产品信息编号
				 */
				if (CgbUserTransDetailService.TRUST_TYPE_3.equals(userTransDetail.getTrustType())) { // 出借.
					WloanTermInvest invest = wloanTermInvestDao.get(userTransDetail.getTransId());
					if (invest != null) {
						if (invest.getWloanTermProject() != null) {
							param.put("sourceFinancingCode", invest.getWloanTermProject().getSn());
							lendParticular.setSourceFinancingCode(invest.getWloanTermProject().getSn());
						} else {
							param.put("sourceFinancingCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
							lendParticular.setSourceFinancingCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
						}
					} else {
						param.put("sourceFinancingCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
						lendParticular.setSourceFinancingCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					}
				} else {
					param.put("sourceFinancingCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					lendParticular.setSourceFinancingCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				}
				/**
				 * // 交易类型.
				 */
				if (CgbUserTransDetailService.TRUST_TYPE_0.equals(userTransDetail.getTrustType())) { // 充值.
					param.put("transType", TransactTypeEnum.TRANSACT_TYPE_6.getValue());
					lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_6.getValue());
				} else if (CgbUserTransDetailService.TRUST_TYPE_1.equals(userTransDetail.getTrustType())) { // 提现.
					param.put("transType", TransactTypeEnum.TRANSACT_TYPE_7.getValue());
					lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_7.getValue());
				} else if (CgbUserTransDetailService.TRUST_TYPE_3.equals(userTransDetail.getTrustType())) { // 出借.
					param.put("transType", TransactTypeEnum.TRANSACT_TYPE_2.getValue());
					lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_2.getValue());
				} else if (CgbUserTransDetailService.TRUST_TYPE_10.equals(userTransDetail.getTrustType())) { // 抵用券，出借返现.
					param.put("transType", TransactTypeEnum.TRANSACT_TYPE_44.getValue());
					lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_44.getValue());
				}
				/**
				 * 交易金额（元）
				 */
				param.put("transMoney", NumberUtils.scaleDoubleStr(userTransDetail.getAmount()));
				lendParticular.setTransMoney(NumberUtils.scaleDoubleStr(userTransDetail.getAmount()));
				/**
				 * 出借用户证件号码Hash值
				 */
				param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(userTransDetail.getUserInfo().getCertificateNo())));
				lendParticular.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(userTransDetail.getUserInfo().getCertificateNo())));
				/**
				 * 交易发生时间
				 */
				param.put("transTime", DateUtils.formatDate(userTransDetail.getTransDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setTransTime(DateUtils.formatDate(userTransDetail.getTransDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setBatchNum(batchNum); // 批次号.
				lendParticular.setSendTime(sentTime); // 发送时间.
				int insert = lendParticularsDao.insert(lendParticular);
				if (insert == 1) {
					log.info("投资明细信息插入成功！");
				} else {
					log.info("投资明细插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_88.getValue());// 产品配置接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			log.info("投资明细(充提类，出借返现)接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PARTICULARS, "utf-8");
			log.info("投资明细(充提类，出借返现)接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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

	// 围绕出借人-收回利息.
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTakeBackInterest(List<WloanTermUserPlan> cutdList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			for (WloanTermUserPlan userPlan : cutdList) {
				// 国家应急中心投资明细信息.
				LendParticulars lendParticular = new LendParticulars();
				/**
				 * 产品信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				/**
				 * 接口版本号.
				 */
				param.put("version", ServerURLConfig.VERSION);
				lendParticular.setVersion(ServerURLConfig.VERSION);
				/**
				 * 网贷机构平台在应急中心系统的唯一编号，
				 * 网贷机构在应急中心系统注册实名之后自动生成.
				 */
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				lendParticular.setSourceCode(ServerURLConfig.SOURCE_CODE);
				// 出借人每笔交易流水的唯一编号.
				param.put("transId", userPlan.getId());
				lendParticular.setTransId(userPlan.getId());
				// 产品信息编号.
				param.put("sourceFinancingCode", userPlan.getWloanTermProject().getSn());
				lendParticular.setSourceFinancingCode(userPlan.getWloanTermProject().getSn());
				// 交易类型.
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_9.getValue());
				lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_9.getValue());
				/**
				 * 交易金额（元）
				 */
				param.put("transMoney", NumberUtils.scaleDoubleStr(userPlan.getInterest()));
				lendParticular.setTransMoney(NumberUtils.scaleDoubleStr(userPlan.getInterest()));
				/**
				 * 出借用户证件号码Hash值
				 */
				param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo())));
				lendParticular.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo())));
				/**
				 * 交易发生时间
				 */
				param.put("transTime", DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setTransTime(DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setBatchNum(batchNum); // 批次号.
				lendParticular.setSendTime(sentTime); // 发送时间.
				int insert = lendParticularsDao.insert(lendParticular);
				if (insert == 1) {
					log.info("投资明细信息插入成功！");
				} else {
					log.info("投资明细插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_88.getValue());// 产品配置接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			log.info("产品信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PARTICULARS, "utf-8");
			log.info("产品信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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

	// 存量-出借人-还本付息.
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTakeBackPrincipal(List<WloanTermUserPlan> cutdList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		try {
			/**
			 * 该批次出借人集合数据接口封装.
			 */
			for (WloanTermUserPlan userPlan : cutdList) {
				// 国家应急中心用户信息.
				LendParticulars lendParticular = new LendParticulars();
				/**
				 * 产品信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				/**
				 * 接口版本号.
				 */
				param.put("version", ServerURLConfig.VERSION);
				lendParticular.setVersion(ServerURLConfig.VERSION);
				/**
				 * 网贷机构平台在应急中心系统的唯一编号，
				 * 网贷机构在应急中心系统注册实名之后自动生成.
				 */
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				lendParticular.setSourceCode(ServerURLConfig.SOURCE_CODE);
				/**
				 * 出借人每笔交易流水的唯一编号.
				 */
				param.put("transId", userPlan.getId());
				lendParticular.setTransId(userPlan.getId());
				/**
				 * 产品信息编号
				 */
				param.put("sourceFinancingCode", userPlan.getWloanTermProject().getSn());
				lendParticular.setSourceFinancingCode(userPlan.getWloanTermProject().getSn());
				/**
				 * 出借用户证件号码Hash值
				 */
				param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo())));
				lendParticular.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo())));
				/**
				 * 交易发生时间
				 */
				param.put("transTime", DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setTransTime(DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				lendParticular.setBatchNum(batchNum); // 批次号.
				lendParticular.setSendTime(sentTime); // 发送时间.
				/**
				 * // 交易类型.
				 */
				for (int i = 1; i < 3; i++) {
					if (i == 1) {
						param.put("transType", TransactTypeEnum.TRANSACT_TYPE_8.getValue()); // 出借方收回本金.
						lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_8.getValue());
						/**
						 * 交易金额（元）本金
						 */
						param.put("transMoney", NumberUtils.scaleDoubleStr(userPlan.getWloanTermInvest().getAmount()));
						lendParticular.setTransMoney(NumberUtils.scaleDoubleStr(userPlan.getWloanTermInvest().getAmount()));
						int insert = lendParticularsDao.insert(lendParticular);
						if (insert == 1) {
							log.info("投资明细信息插入成功！");
						} else {
							log.info("投资明细插入失败！");
						}
						list.add(param);
					} else {
						param.put("transType", TransactTypeEnum.TRANSACT_TYPE_9.getValue()); // 出借方收回利息.
						lendParticular.setTransType(TransactTypeEnum.TRANSACT_TYPE_9.getValue());
						/**
						 * 交易金额（元）利息
						 */
						param.put("transMoney", NumberUtils.scaleDoubleStr(userPlan.getInterest() - userPlan.getWloanTermInvest().getAmount()));
						lendParticular.setTransMoney(NumberUtils.scaleDoubleStr(userPlan.getInterest() - userPlan.getWloanTermInvest().getAmount()));
						int insert = lendParticularsDao.insert(lendParticular);
						if (insert == 1) {
							log.info("投资明细信息插入成功！");
						} else {
							log.info("投资明细插入失败！");
						}
						list.add(param);
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_88.getValue());// 产品配置接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			log.info("产品信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PARTICULARS, "utf-8");
			log.info("产品信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_88.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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
