package com.power.platform.ifcert.transact.service;

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
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.TransactDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.Transact;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.RepayTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.type.TransactTypeEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 
 * class: TransactDataAccessService <br>
 * description: 交易流水. <br>
 * 说明：资金交易信息，是机构发生的所有真实交易数据。 <br>
 * （1）如果是平台对接银行交易的情况（对接了银行存管），业务实时产生的交易数据实时推送到应急中心，如果存管银行是T+1对账，如果对账出现了交易失败情况，推送冲正数据。如,出借人A充值1000 元，
 * 存管银行对账失败，平台需要推送一条交易类型是充值，交易金额是（- 1000）的交易流水进行冲正。 <br>
 * （2）上报交易流水数据触发时间：交易流水数据产生之后上报该数据。 <br>
 * （3）存量数据充值、提现业务推送方式：账户余额不为0 的用户，历史充值和提现数据都需要推送。 <br>
 * （4）交易类型增加“50—线下代偿收回”类型，适用场景：借款方线下把还款资金支付给第三方担保机构，由第三方担保机构线上代偿给出借人的过程。 <br>
 * author: Roy <br>
 * date: 2019年5月16日 上午9:09:16
 */
@Service("transactDataAccessService")
public class TransactDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(TransactDataAccessService.class);

	@Resource
	private BatchNumDao batchNumDao;
	@Resource
	private TransactDao transactDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;

	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushTransactInterestInfo <br>
	 * description: 补推交易流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月30日 下午6:21:37
	 * 
	 * @param proPlanList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushTransactInterestInfo(List<Transact> tList) {

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

			// 存量借款还款付息计划，成功记录交易流水.
			for (Transact transact : tList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", transact.getTransId()); // 网贷机构交易流水号.
				t.setTransId(transact.getTransId());
				param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
				t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
				t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
				t.setFinClaimId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", transact.getReplanId()); // 还款计划编号.
				t.setReplanId(transact.getReplanId());
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
				t.setTransType(TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
				param.put("transMoney",transact.getTransMoney()); // 交易金额（元）.
				t.setTransMoney(transact.getTransMoney());
				param.put("userIdcardHash",transact.getUserIdcardHash() ); // 交易主体证件号Hash值.
				t.setUserIdcardHash(transact.getUserIdcardHash());
				param.put("transTime",transact.getTransTime()); // 交易发生时间.
				t.setTransTime(transact.getTransTime());
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				int insert = transactDao.insert(t);
				if (insert == 1) {
					log.info("数据中心交易流水【还款利息】插入成功！");
				} else {
					log.info("数据中心交易流水【还款利息】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactCrePayPrincipalInfo <br>
	 * description: 推送存量借款人用户还本流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月30日 下午4:40:34
	 * 
	 * @param cutdList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCrePayPrincipalInfo(List<WloanTermProjectPlan> proPlanList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {

			// 存量借款还款计划还本付息，成功记录交易流水.
			for (WloanTermProjectPlan proPlan : proPlanList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", proPlan.getId()); // 网贷机构交易流水号.
				t.setTransId(proPlan.getId());
				if (proPlan.getWloanTermProject() != null) {
					param.put("sourceProductCode", proPlan.getWloanTermProject().getSn()); // 散标信息编号.
					t.setSourceProductCode(proPlan.getWloanTermProject().getSn());
					param.put("sourceProductName", proPlan.getWloanTermProject().getName()); // 散标名称.
					t.setSourceProductName(proPlan.getWloanTermProject().getName());
				} else {
					param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
					t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
					t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				}
				param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
				t.setFinClaimId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", proPlan.getId()); // 还款计划编号.
				t.setReplanId(proPlan.getId());
				/**
				 * 最后一期还本付息，拆分成两条流水.
				 */
				if (RepayTypeEnum.REPAY_TYPE_0.getValue().equals(proPlan.getPrincipal())) {
				} else if (RepayTypeEnum.REPAY_TYPE_1.getValue().equals(proPlan.getPrincipal())) {
					for (int j = 0; j < 2; j++) {
						if (j == 0) {
							param.put("transType", TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
							t.setTransType(TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
							param.put("transMoney", NumberUtils.scaleDoubleStr(NumberUtils.subtract(proPlan.getInterest(), proPlan.getWloanTermProject().getCurrentAmount()))); // 交易金额（元）.
							t.setTransMoney(NumberUtils.scaleDoubleStr(NumberUtils.subtract(proPlan.getInterest(), proPlan.getWloanTermProject().getCurrentAmount())));
							if (proPlan.getWloanSubject() != null) {
								param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(proPlan.getWloanSubject().getBusinessNo()))); // 交易主体证件号Hash值.
								t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(proPlan.getWloanSubject().getBusinessNo())));
							}
							param.put("transTime", DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
							t.setTransTime(DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
							t.setBatchNum(batchNum);
							t.setSendTime(sentTime);
							int insert = transactDao.insert(t);
							if (insert == 1) {
								log.info("数据中心交易流水【还款利息】插入成功！");
							} else {
								log.info("数据中心交易流水【还款利息】插入失败！");
							}
							list.add(param);
						}
						if (j == 1) {
							param.put("transType", TransactTypeEnum.TRANSACT_TYPE_18.getValue()); // 还款本金（借款人按还款计划偿还的实际本金）.
							t.setTransType(TransactTypeEnum.TRANSACT_TYPE_18.getValue()); // 还款本金（借款人按还款计划偿还的实际本金）.
							param.put("transMoney", NumberUtils.scaleDoubleStr(proPlan.getWloanTermProject().getCurrentAmount())); // 交易金额（元）.
							t.setTransMoney(NumberUtils.scaleDoubleStr(proPlan.getWloanTermProject().getCurrentAmount()));
							if (proPlan.getWloanSubject() != null) {
								param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(proPlan.getWloanSubject().getBusinessNo()))); // 交易主体证件号Hash值.
								t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(proPlan.getWloanSubject().getBusinessNo())));
							}
							param.put("transTime", DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
							t.setTransTime(DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
							t.setBatchNum(batchNum);
							t.setSendTime(sentTime);
							int insert = transactDao.insert(t);
							if (insert == 1) {
								log.info("数据中心交易流水【还款本金】插入成功！");
							} else {
								log.info("数据中心交易流水【还款本金】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactCrePayInterestInfo <br>
	 * description: 推送存量借款人用户付息流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月30日 下午6:21:37
	 * 
	 * @param proPlanList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCrePayInterestInfo(List<WloanTermProjectPlan> proPlanList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {

			// 存量借款还款付息计划，成功记录交易流水.
			for (WloanTermProjectPlan proPlan : proPlanList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", proPlan.getId()); // 网贷机构交易流水号.
				t.setTransId(proPlan.getId());
				if (proPlan.getWloanTermProject() != null) {
					param.put("sourceProductCode", proPlan.getWloanTermProject().getSn()); // 散标信息编号.
					t.setSourceProductCode(proPlan.getWloanTermProject().getSn());
					param.put("sourceProductName", proPlan.getWloanTermProject().getName()); // 散标名称.
					t.setSourceProductName(proPlan.getWloanTermProject().getName());
				} else {
					param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
					t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
					t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				}
				param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
				t.setFinClaimId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", proPlan.getId()); // 还款计划编号.
				t.setReplanId(proPlan.getId());
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
				t.setTransType(TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
				param.put("transMoney", NumberUtils.scaleDoubleStr(proPlan.getInterest())); // 交易金额（元）.
				t.setTransMoney(NumberUtils.scaleDoubleStr(proPlan.getInterest()));
				if (proPlan.getWloanSubject() != null) {
					param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(proPlan.getWloanSubject().getBusinessNo()))); // 交易主体证件号Hash值.
					t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(proPlan.getWloanSubject().getBusinessNo())));
				}
				param.put("transTime", DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
				t.setTransTime(DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				int insert = transactDao.insert(t);
				if (insert == 1) {
					log.info("数据中心交易流水【还款利息】插入成功！");
				} else {
					log.info("数据中心交易流水【还款利息】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactCreGrantInfo <br>
	 * description: 推送存量借款人用户放款流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月22日 上午11:15:12
	 * 
	 * @param cutdList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreGrantInfo(List<CgbUserTransDetail> cutdList, long currentTimeMillis) {

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
			 * 出借人用户放款流水，存量.
			 */
			for (CgbUserTransDetail cutd : cutdList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", cutd.getTransId()); // 网贷机构交易流水号.
				t.setTransId(cutd.getTransId());

				// 借款人放款流水-散标信息编号，交易流水号为：标的ID.
				WloanTermProject pro = null;
				pro = wloanTermProjectDao.get(cutd.getTransId());
				if (pro != null) {
					param.put("sourceProductCode", pro.getSn()); // 散标信息编号.
					t.setSourceProductCode(pro.getSn());
					param.put("sourceProductName", pro.getName()); // 散标名称.
					t.setSourceProductName(pro.getName());
				} else {
					param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
					t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
					t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				}

				param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
				t.setFinClaimId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 还款计划编号，（该笔交易不涉及还款计划，填-1）.
				t.setReplanId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_1.getValue()); // 放款（借款人实际到账金额，不包括所有服务费金额）.
				t.setTransType(TransactTypeEnum.TRANSACT_TYPE_1.getValue()); // 放款（借款人实际到账金额，不包括所有服务费金额）.
				param.put("transMoney", NumberUtils.scaleDoubleStr(cutd.getAmount())); // 交易金额（元）.
				t.setTransMoney(NumberUtils.scaleDoubleStr(cutd.getAmount()));
				if (pro != null) {
					if (pro.getWloanSubject() != null) {
						param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(pro.getWloanSubject().getBusinessNo()))); // 交易主体证件号Hash值.
						t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(pro.getWloanSubject().getBusinessNo())));
					}
				}
				param.put("transTime", DateUtils.formatDate(cutd.getTransDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
				t.setTransTime(DateUtils.formatDate(cutd.getTransDate(), "yyyy-MM-dd HH:mm:ss"));
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				int insert = transactDao.insert(t);
				if (insert == 1) {
					log.info("数据中心交易流水【放款】插入成功！");
				} else {
					log.info("数据中心交易流水【放款】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactCreWithdrawInfo <br>
	 * description: 推送存量借款人用户提现流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月22日 上午10:10:33
	 * 
	 * @param cutdList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreWithdrawInfo(List<CgbUserTransDetail> cutdList, long currentTimeMillis) {

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
			 * 出借人用户充值流水，存量.
			 */
			for (CgbUserTransDetail cutd : cutdList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", cutd.getTransId()); // 网贷机构交易流水号.
				t.setTransId(cutd.getTransId());
				param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
				t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
				t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
				t.setFinClaimId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 还款计划编号，（该笔交易不涉及还款计划，填-1）.
				t.setReplanId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_7.getValue()); // 提现.
				t.setTransType(TransactTypeEnum.TRANSACT_TYPE_7.getValue()); // 提现.
				param.put("transMoney", NumberUtils.scaleDoubleStr(cutd.getAmount())); // 交易金额（元）.
				t.setTransMoney(NumberUtils.scaleDoubleStr(cutd.getAmount()));
				if (cutd.getCreditUserInfo() != null) {
					List<WloanSubject> subjects = wloanSubjectDao.getByLoanApplyId(cutd.getCreditUserInfo().getId());
					if (subjects != null && subjects.size() > 0) {
						param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(subjects.get(0).getBusinessNo()))); // 交易主体证件号Hash值.
						t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(subjects.get(0).getBusinessNo())));
					}
				}
				param.put("transTime", DateUtils.formatDate(cutd.getTransDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
				t.setTransTime(DateUtils.formatDate(cutd.getTransDate(), "yyyy-MM-dd HH:mm:ss"));
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				int insert = transactDao.insert(t);
				if (insert == 1) {
					log.info("数据中心交易流水【提现】插入成功！");
				} else {
					log.info("数据中心交易流水【提现】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactInvRechargeInfo <br>
	 * description: 推送存量借款人用户充值流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月21日 下午4:05:34
	 * 
	 * @param cutdList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreRechargeInfo(List<CgbUserTransDetail> cutdList, long currentTimeMillis) {

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
			 * 出借人用户充值流水，存量.
			 */
			for (CgbUserTransDetail cutd : cutdList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", cutd.getTransId()); // 网贷机构交易流水号.
				t.setTransId(cutd.getTransId());
				param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
				t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
				t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
				t.setFinClaimId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 还款计划编号，（该笔交易不涉及还款计划，填-1）.
				t.setReplanId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_6.getValue()); // 充值.
				t.setTransType(TransactTypeEnum.TRANSACT_TYPE_6.getValue()); // 充值.
				param.put("transMoney", NumberUtils.scaleDoubleStr(cutd.getAmount())); // 交易金额（元）.
				t.setTransMoney(NumberUtils.scaleDoubleStr(cutd.getAmount()));
				if (cutd.getCreditUserInfo() != null) {
					List<WloanSubject> subjects = wloanSubjectDao.getByLoanApplyId(cutd.getCreditUserInfo().getId());
					if (subjects != null && subjects.size() > 0) {
						param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(subjects.get(0).getBusinessNo()))); // 交易主体证件号Hash值.
						t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(subjects.get(0).getBusinessNo())));
					}
				}
				param.put("transTime", DateUtils.formatDate(cutd.getTransDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
				t.setTransTime(DateUtils.formatDate(cutd.getTransDate(), "yyyy-MM-dd HH:mm:ss"));
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				int insert = transactDao.insert(t);
				if (insert == 1) {
					log.info("数据中心交易流水【充值】插入成功！");
				} else {
					log.info("数据中心交易流水【充值】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactInvInfo <br>
	 * description: 推送标的出借流水（出借人购买散标产生的初始债权交易流水）. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月16日 下午4:14:29
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvInfo(List<String> projectIdList) {

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
			// 标的ID.
			for (String projectId : projectIdList) {
				WloanTermProject pro = wloanTermProjectDao.get(projectId);
				if (pro != null) {
					if (WloanTermProjectService.REPAYMENT.equals(pro.getState())) {
						List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(projectId);
						for (WloanTermInvest invest : investList) {
							// 数据中心交易流水表.
							Transact t = new Transact();
							/**
							 * 出借人出借流水封装.
							 */
							Map<String, Object> param = new LinkedHashMap<String, Object>();
							param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
							t.setVersion(ServerURLConfig.VERSION);
							param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
							t.setSourceCode(ServerURLConfig.SOURCE_CODE);
							param.put("transId", invest.getId()); // 网贷机构交易流水号.
							t.setTransId(invest.getId());
							param.put("sourceProductCode", invest.getWloanTermProject().getSn()); // 散标信息编号.
							t.setSourceProductCode(invest.getWloanTermProject().getSn());
							param.put("sourceProductName", invest.getWloanTermProject().getName()); // 散标名称.
							t.setSourceProductName(invest.getWloanTermProject().getName());
							param.put("finClaimId", invest.getId()); // 初始债权编号.
							t.setFinClaimId(invest.getId());
							param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
							t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
							param.put("replanId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 还款计划编号，（该笔交易不涉及还款计划，填-1）.
							t.setReplanId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
							param.put("transType", TransactTypeEnum.TRANSACT_TYPE_2.getValue()); // 出借（出借人购买散标产生的初始债权交易流水）.
							t.setTransType(TransactTypeEnum.TRANSACT_TYPE_2.getValue()); // 出借（出借人购买散标产生的初始债权交易流水）.
							param.put("transMoney", NumberUtils.scaleDoubleStr(invest.getAmount())); // 交易金额（元）.
							t.setTransMoney(NumberUtils.scaleDoubleStr(invest.getAmount()));
							param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo()))); // 交易主体证件号Hash值.
							t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo())));
							param.put("transTime", DateUtils.formatDate(invest.getBeginDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
							t.setTransTime(DateUtils.formatDate(invest.getBeginDate(), "yyyy-MM-dd HH:mm:ss"));
							t.setBatchNum(batchNum);
							t.setSendTime(sentTime);
							int insert = transactDao.insert(t);
							if (insert == 1) {
								log.info("数据中心交易流水【出借】插入成功！");
							} else {
								log.info("数据中心交易流水【出借】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactInvTakeBackInterest <br>
	 * description: 出借人-收回利息-交易流水 <br>
	 * author: Roy <br>
	 * date: 2019年6月29日 上午10:54:15
	 * 
	 * @param wtupList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvTakeBackInterest(List<WloanTermUserPlan> wtupList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {
			for (WloanTermUserPlan userPlan : wtupList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				/**
				 * 出借人出借流水封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", userPlan.getId()); // 出借人每笔交易流水的唯一编号.
				t.setTransId(userPlan.getId());
				if (userPlan.getWloanTermProject() != null) {
					param.put("sourceProductCode", userPlan.getWloanTermProject().getSn()); // 散标信息编号.
					t.setSourceProductCode(userPlan.getWloanTermProject().getSn());
					param.put("sourceProductName", userPlan.getWloanTermProject().getName()); // 散标名称.
					t.setSourceProductName(userPlan.getWloanTermProject().getName());
				} else {
					param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
					t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
					t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				}
				param.put("finClaimId", userPlan.getWloanTermInvestId()); // 初始债权编号.
				t.setFinClaimId(userPlan.getWloanTermInvestId());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 还款计划编号.
				t.setReplanId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("transType", TransactTypeEnum.TRANSACT_TYPE_9.getValue()); // 收回利息（出借方收回利息，不包含48-罚息）.
				t.setTransType(TransactTypeEnum.TRANSACT_TYPE_9.getValue());
				param.put("transMoney", NumberUtils.scaleDoubleStr(userPlan.getInterest())); // 交易金额（元）.
				t.setTransMoney(NumberUtils.scaleDoubleStr(userPlan.getInterest()));
				if (userPlan.getUserInfo() != null) {
					param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo()))); // 交易主体证件号Hash值.
					t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo())));
				}
				param.put("transTime", DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
				t.setTransTime(DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				int insert = transactDao.insert(t);
				if (insert == 1) {
					log.info("数据中心交易流水【收回利息】插入成功！");
				} else {
					log.info("数据中心交易流水【收回利息】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("收回利息-推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("收回利息-响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * methods: pushTransactInvTakeBackPrincipal <br>
	 * description: 出借人-还本付息-交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月29日 下午2:49:09
	 * 
	 * @param wtupList
	 * @param currentTimeMillis
	 * @return
	 */
	public Map<String, Object> pushTransactInvTakeBackPrincipal(List<WloanTermUserPlan> wtupList, long currentTimeMillis) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {
			for (WloanTermUserPlan userPlan : wtupList) {
				// 数据中心交易流水表.
				Transact t = new Transact();
				/**
				 * 出借人出借流水封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				t.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				t.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("transId", userPlan.getId()); // 出借人每笔交易流水的唯一编号.
				t.setTransId(userPlan.getId());
				if (userPlan.getWloanTermProject() != null) {
					param.put("sourceProductCode", userPlan.getWloanTermProject().getSn()); // 散标信息编号.
					t.setSourceProductCode(userPlan.getWloanTermProject().getSn());
					param.put("sourceProductName", userPlan.getWloanTermProject().getName()); // 散标名称.
					t.setSourceProductName(userPlan.getWloanTermProject().getName());
				} else {
					param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
					t.setSourceProductCode(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
					param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
					t.setSourceProductName(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				}
				param.put("finClaimId", userPlan.getWloanTermInvestId()); // 初始债权编号.
				t.setFinClaimId(userPlan.getWloanTermInvestId());
				param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
				t.setTransferId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				param.put("replanId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 还款计划编号.
				t.setReplanId(TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue());
				if (userPlan.getUserInfo() != null) {
					param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo()))); // 交易主体证件号Hash值.
					t.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(userPlan.getUserInfo().getCertificateNo())));
				}
				param.put("transTime", DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 交易发生时间.
				t.setTransTime(DateUtils.formatDate(userPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
				t.setBatchNum(batchNum);
				t.setSendTime(sentTime);
				/**
				 * // 交易类型.
				 */
				for (int i = 1; i < 3; i++) {
					if (i == 1) {
						param.put("transType", TransactTypeEnum.TRANSACT_TYPE_8.getValue()); // 收回本金（出借方收回本金）.
						t.setTransType(TransactTypeEnum.TRANSACT_TYPE_8.getValue());
						/**
						 * 交易金额（元）本金
						 */
						param.put("transMoney", NumberUtils.scaleDoubleStr(userPlan.getWloanTermInvest().getAmount())); // 交易金额（元）.
						t.setTransMoney(NumberUtils.scaleDoubleStr(userPlan.getWloanTermInvest().getAmount()));
						int insert = transactDao.insert(t);
						if (insert == 1) {
							log.info("数据中心交易流水【收回本金】插入成功！");
						} else {
							log.info("数据中心交易流水【收回本金】插入失败！");
						}
						list.add(param);
					} else {
						param.put("transType", TransactTypeEnum.TRANSACT_TYPE_9.getValue()); // 收回利息（出借方收回利息，不包含48-罚息）.
						t.setTransType(TransactTypeEnum.TRANSACT_TYPE_9.getValue());
						/**
						 * 交易金额（元）利息
						 */
						param.put("transMoney", NumberUtils.scaleDoubleStr(userPlan.getInterest() - userPlan.getWloanTermInvest().getAmount())); // 交易金额（元）.
						t.setTransMoney(NumberUtils.scaleDoubleStr(userPlan.getInterest() - userPlan.getWloanTermInvest().getAmount()));
						int insert = transactDao.insert(t);
						if (insert == 1) {
							log.info("数据中心交易流水【收回利息】插入成功！");
						} else {
							log.info("数据中心交易流水【收回利息】插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("出借人收回还本息-推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("出借人收回还本息-响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
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
	 * （专用）
	 * methods: transactInterestInfo <br>
	 * description: 解决批次号相同的交易流水. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月30日 下午6:21:37
	 * 
	 * @param proPlanList
	 * @param currentTimeMillis
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> transactInterestInfo(Transact transact) {

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
		//查询批次号
		BatchNum batch = batchNumDao.get("5767d5c4580e4b1eb166840c59695363");
		if(batch==null) {
			result.put("respCode", "100010");
			result.put("respMsg", "找不到该批次号");
			return result;
		}
		try {
			// 存量借款还款付息计划，成功记录交易流水.
			// 数据中心交易流水表.
			Map<String, Object> param = new LinkedHashMap<String, Object>();
			param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
			param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
			param.put("transId", transact.getTransId()); // 网贷机构交易流水号.
			param.put("sourceProductCode", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标信息编号.
			param.put("sourceProductName", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 散标名称.
			param.put("finClaimId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 初始债权编号.
			param.put("transferId", TransactTypeEnum.TRANSACT_TYPE_NAGETIVE_1.getValue()); // 转让信息编号（公司无债权转让业务，填-1）.
			param.put("replanId", transact.getReplanId()); // 还款计划编号.
			param.put("transType", TransactTypeEnum.TRANSACT_TYPE_19.getValue()); // 还款利息（借款人按还款计划偿还的实际利息）.
			param.put("transMoney",transact.getTransMoney()); // 交易金额（元）.
			param.put("userIdcardHash",transact.getUserIdcardHash() ); // 交易主体证件号Hash值.
			param.put("transTime",transact.getTransTime()); // 交易发生时间.
			list.add(param);
			transact.setBatchNum(batchNum);
			transact.setSendTime(sentTime);
			int up = transactDao.update(transact);
			if (up == 1) {
				log.info("数据中心交易流水【收回利息】修改成功！");
			} else {
				log.info("数据中心交易流水【收回利息】修改失败！");
			}

			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_4.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_TRANSACT, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					batch.setBatchNum(batchNum);
					batch.setSendTime(sentTime);
					batch.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
					batch.setTotalNum(list.size() + "");
					batch.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					batch.setCode(code);
					int insert = batchNumDao.update(batch);
					if (insert == 1) {
						log.info("该批次数据状态信息修改成功！");
					} else {
						log.info("该批次数据状态信息修改失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					batch.setBatchNum(batchNum);
					batch.setSendTime(sentTime);
					batch.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
					batch.setTotalNum(list.size() + "");
					batch.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					batch.setCode(code);
					int insert = batchNumDao.update(batch);
					if (insert == 1) {
						log.info("该批次数据状态信息修改成功！");
					} else {
						log.info("该批次数据状态信息修改失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				batch.setBatchNum(batchNum);
				batch.setSendTime(sentTime);
				batch.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
				batch.setTotalNum(list.size() + "");
				batch.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				batch.setCode("resp null");
				int insert = batchNumDao.update(batch);
				if (insert == 1) {
					log.info("该批次数据状态信息修改成功！");
				} else {
					log.info("该批次数据状态信息修改失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			batch.setBatchNum(batchNum);
			batch.setSendTime(sentTime);
			batch.setInfType(InfTypeEnum.INF_TYPE_4.getValue());
			batch.setTotalNum(list.size() + "");
			batch.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			batch.setCode("exception");
			int insert = batchNumDao.update(batch);
			if (insert == 1) {
				log.info("该批次数据状态信息修改成功！");
			} else {
				log.info("该批次数据状态信息修改失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}

}
