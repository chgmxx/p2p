package com.power.platform.ifcert.repayplan.service;

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
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.RepayPlanDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.RepayPlan;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.RepayTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;

/**
 * 
 * class: RepayPlanDataAccessService <br>
 * description: 还款计划. <br>
 * 说明：（1）每一个散标需上传该散标的还款计划明细，计划分多少期就有多少条记录，每一期需要填写还款本金、利息、计划还款时间等信息。 <br>
 * （2）具体还款过程中，借款人出现提前还款或者延期还款（展期）情况，平台需将重新制定的全部还款计划（提前还款时，场景1：还款计划不变，只是把后几期“当期应还款时间点”改成提前还款时间点或其他字段发生变更；
 * 场景2：还款计划变更，如：10 期变成5 期，重新推送新的5 期）包括前几期已还款数据推送过来，整体替换原来计划方案,应急中心将会以最新的还款计划计算相关指标。
 * 已还款并且历史上已推送过来的几期数据仍需上报，上报内容为原还款计划中散标信息编号、借款人用户证件号hash 值、还款计划编码、应还本金、应还利息、应还服务费和应还款时间点等。 <br>
 * （3）上报还款计划数据触发时间：某个借款人成功申请借款，平台准备放款时，还款计划业务数据产生之后上报该业务数据。 <br>
 * author: Roy <br>
 * date: 2019年5月13日 下午2:53:48
 */
@Service("repayPlanDataAccessService")
public class RepayPlanDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(RepayPlanDataAccessService.class);

	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private BatchNumDao batchNumDao;
	@Resource
	private RepayPlanDao repayPlanDao;

	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushRepayPlanInfo <br>
	 * description: 补推还款计划信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月13日 下午3:01:13
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushRepayPlanInfo(List<RepayPlan> rpList) {

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
			for (RepayPlan repayPlan : rpList) {
				// 数据中心还款计划表.
				RepayPlan rp = new RepayPlan();
				/**
				 * 还款计划封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				rp.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				rp.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("sourceProductCode", repayPlan.getSourceProductCode()); // 散标信息编号.
				rp.setSourceProductCode(repayPlan.getSourceProductCode());
				param.put("userIdcardHash",repayPlan.getUserIdcardHash()); // 借款用户证件号hash值（法人证件号码）.
				rp.setUserIdcardHash(repayPlan.getUserIdcardHash());
				param.put("totalIssue", repayPlan.getTotalIssue()); // 分期还款的总期数.
				rp.setTotalIssue(repayPlan.getTotalIssue());
				param.put("issue", repayPlan.getIssue()); // 本条记录（当期）所属期数.
				rp.setIssue(repayPlan.getIssue());
				/**
				 * 指借款人按期应该支付的平台服务费。场景1，放款时，平台一次性从借款本金中扣除平台服务费或无平台服务费，则填写0，如：借款人A，
				 * 借款1000 元，放款时A 实际到账800 元，扣除150 元担保手续费、20 元第三方推荐费、30 元平台服务费，则应还服务费填写0 元；场景2，
				 * 平台服务费、第三方推荐费、担保手续费通过还款计划按期返还，则把三类费用合并到”应还服务费”中报送，如：借款人B，借款1000 元，
				 * 放款时B 实际到账1000 元，分10 期还款，每期100 元还款本金、4 元还款利息、15 元担保手续费、3 元平台服务费、2 元第三方推荐费，
				 * 则每期应还服务费填写20 元（15+3+2）。保留小数点后2 位说明：如果本条记录不涉及应还服务费，则该项填0。
				 */
				param.put("curServiceCharge", "0.00"); // 本条记录（当期）应还服务费（元）.
				rp.setCurServiceCharge("0.00");
				param.put("repayTime", repayPlan.getRepayTime()); // 本条记录（当期）应还款时间点（本条记录（当期）期应还款时间， 格式yyyy-MM-dd HH:mm:ss）.
				rp.setRepayTime(repayPlan.getRepayTime());
				param.put("replanId",repayPlan.getReplanId()); // 还款计划编号（还款计划编号是指网贷机构内部对每一个借款项目的每一次还款计划所制定的唯一编号。还款计划编号主要用于关联交易流水）.
				rp.setReplanId(repayPlan.getReplanId());
				param.put("curFund", "0.00"); // 本条记录（当期）应还本金（元）.
				rp.setCurFund("0.00");
				param.put("curInterest", repayPlan.getCurInterest()); // 本条记录（当期）应还利息（元）.
				rp.setCurInterest(repayPlan.getCurInterest());
				rp.setBatchNum(batchNum);
				rp.setSendTime(sentTime);
				int insert = repayPlanDao.insert(rp);
				if (insert == 1) {
					log.info("数据中心还款计划插入成功！");
				} else {
					log.info("数据中心还款计划插入失败！");
				}
				list.add(param); // 还款计划添加付息.
				
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_81.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("还款计划信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_REPAY_PLAN, "utf-8");
			log.info("还款计划信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
	 * methods: pushRepayPlanInfo <br>
	 * description: 推送还款计划信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月13日 下午3:01:13
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushRepayPlanInfo(List<String> projectIdList) {

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
			for (String projectId : projectIdList) {
				WloanTermProject project = wloanTermProjectDao.get(projectId);
				if (project != null) {
					// 融资主体.
					WloanSubject subject = wloanSubjectDao.get(project.getSubjectId());
					String userIdcardHash = "";
					if (subject != null) {
						// 借款方企业的统一社会信用代码.
						userIdcardHash = tool.idCardHash(StringUtils.replaceBlanK(subject.getBusinessNo()));
					}
					List<WloanTermProjectPlan> proPlanList = wloanTermProjectPlanDao.findProPlansByProId(projectId);
					for (int i = 0; i < proPlanList.size(); i++) {
						WloanTermProjectPlan proPlan = proPlanList.get(i);
						// 数据中心还款计划表.
						RepayPlan rp = new RepayPlan();
						/**
						 * 还款计划封装.
						 */
						Map<String, Object> param = new LinkedHashMap<String, Object>();
						param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
						rp.setVersion(ServerURLConfig.VERSION);
						param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
						rp.setSourceCode(ServerURLConfig.SOURCE_CODE);
						param.put("sourceProductCode", project.getSn()); // 散标信息编号.
						rp.setSourceProductCode(project.getSn());
						param.put("userIdcardHash", userIdcardHash); // 借款用户证件号hash值（法人证件号码）.
						rp.setUserIdcardHash(userIdcardHash);
						param.put("totalIssue", proPlanList.size() + ""); // 分期还款的总期数.
						rp.setTotalIssue(proPlanList.size() + "");
						param.put("issue", (i + 1) + ""); // 本条记录（当期）所属期数.
						rp.setIssue((i + 1) + "");
						/**
						 * 指借款人按期应该支付的平台服务费。场景1，放款时，平台一次性从借款本金中扣除平台服务费或无平台服务费，则填写0，如：借款人A，
						 * 借款1000 元，放款时A 实际到账800 元，扣除150 元担保手续费、20 元第三方推荐费、30 元平台服务费，则应还服务费填写0 元；场景2，
						 * 平台服务费、第三方推荐费、担保手续费通过还款计划按期返还，则把三类费用合并到”应还服务费”中报送，如：借款人B，借款1000 元，
						 * 放款时B 实际到账1000 元，分10 期还款，每期100 元还款本金、4 元还款利息、15 元担保手续费、3 元平台服务费、2 元第三方推荐费，
						 * 则每期应还服务费填写20 元（15+3+2）。保留小数点后2 位说明：如果本条记录不涉及应还服务费，则该项填0。
						 */
						param.put("curServiceCharge", "0.00"); // 本条记录（当期）应还服务费（元）.
						rp.setCurServiceCharge("0.00");
						param.put("repayTime", DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss")); // 本条记录（当期）应还款时间点（本条记录（当期）期应还款时间， 格式yyyy-MM-dd HH:mm:ss）.
						rp.setRepayTime(DateUtils.formatDate(proPlan.getRepaymentDate(), "yyyy-MM-dd HH:mm:ss"));
						if (RepayTypeEnum.REPAY_TYPE_0.getValue().equals(proPlan.getPrincipal())) {
							param.put("replanId", proPlan.getId()); // 还款计划编号（还款计划编号是指网贷机构内部对每一个借款项目的每一次还款计划所制定的唯一编号。还款计划编号主要用于关联交易流水）.
							rp.setReplanId(proPlan.getId());
							param.put("curFund", "0.00"); // 本条记录（当期）应还本金（元）.
							rp.setCurFund("0.00");
							param.put("curInterest", NumberUtils.scaleDoubleStr(proPlan.getInterest())); // 本条记录（当期）应还利息（元）.
							rp.setCurInterest(NumberUtils.scaleDoubleStr(proPlan.getInterest()));
							rp.setBatchNum(batchNum);
							rp.setSendTime(sentTime);
							int insert = repayPlanDao.insert(rp);
							if (insert == 1) {
								log.info("数据中心还款计划插入成功！");
							} else {
								log.info("数据中心还款计划插入失败！");
							}
							list.add(param); // 还款计划添加付息.
						} else if (RepayTypeEnum.REPAY_TYPE_1.getValue().equals(proPlan.getPrincipal())) {
							param.put("replanId", proPlan.getId()); // 还款计划编号（还款计划编号是指网贷机构内部对每一个借款项目的每一次还款计划所制定的唯一编号。还款计划编号主要用于关联交易流水）.
							rp.setReplanId(proPlan.getId());
							param.put("curFund", NumberUtils.scaleDoubleStr(project.getCurrentRealAmount())); // 本条记录（当期）应还本金（元）.
							rp.setCurFund(NumberUtils.scaleDoubleStr(project.getCurrentRealAmount()));
							param.put("curInterest", NumberUtils.scaleDoubleStr(NumberUtils.subtract(proPlan.getInterest(), project.getCurrentRealAmount()))); // 本条记录（当期）应还利息（元）.
							rp.setCurInterest(NumberUtils.scaleDoubleStr(NumberUtils.subtract(proPlan.getInterest(), project.getCurrentRealAmount())));
							rp.setBatchNum(batchNum);
							rp.setSendTime(sentTime);
							int insert = repayPlanDao.insert(rp);
							if (insert == 1) {
								log.info("数据中心还款计划插入成功！");
							} else {
								log.info("数据中心还款计划插入失败！");
							}
							list.add(param); // 还款计划添加付息.
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_81.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("还款计划信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_REPAY_PLAN, "utf-8");
			log.info("还款计划信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_81.getValue());
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
