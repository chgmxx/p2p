package com.power.platform.ifcert.scatterInvest.service;

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
import com.power.platform.credit.dao.middlemen.CreditMiddlemenRateDao;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.ScatterInvestDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.ScatterInvest;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.LoanTypeEnum;
import com.power.platform.ifcert.type.LoanUseEnum;
import com.power.platform.ifcert.type.PayTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.type.SecurityTypeEnum;
import com.power.platform.ifcert.utils.ServiceCostUtil;
import com.power.platform.ifcert.utils.ServiceCostUtil.ServiceCostPojo;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;

/**
 * 
 * class: ScatterInvestDataAccessService <br>
 * description: 散标信息。 <br>
 * 说明：记录对外借款的散标，又名标的或融资项目。 <br>
 * 1）散标信息如有变更，包括服务费、担保方式、担保公司数量等发生变化，需要将变更之后数据重新上报，应急中心将取最新的数据。<br>
 * 2）对于借款人申请成功后，产生的还款期数、金额等信息，使用“还款计划”接口上报。<br>
 * 3）上报散标数据触发时间：散标在“散标状态-初始公布”时，需上报散标信息数据。<br>
 * author: Roy <br>
 * date: 2019年5月8日 上午11:25:42
 */
@Service("scatterInvestDataAccessService")
public class ScatterInvestDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(ScatterInvestDataAccessService.class);

	@Resource
	private BatchNumDao batchNumDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private CreditMiddlemenRateDao creditMiddlemenRateDao;
	@Resource
	private ScatterInvestDao scatterInvestDao;

	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushScatterInvestInfo <br>
	 * description: 补推散标信息， <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月8日 上午11:29:52
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushScatterInvestInfo(List<ScatterInvest> siList) {

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
			for (ScatterInvest si : siList) {
				// 国家应急中心散标信息.
				ScatterInvest scatterInvest = new ScatterInvest();
				/**
				 * 散标信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				scatterInvest.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				scatterInvest.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("productStartTime",si.getProductStartTime() ); // 开标时间标的对外发布时间，年-月- 日时: 分: 秒24H,NTP 时间），格式yyyy-MM-dd HH:mm:ss.
				scatterInvest.setProductStartTime(si.getProductStartTime());
				param.put("productName",si.getProductName()); // 散标名称.
				scatterInvest.setProductName(si.getProductName());
				param.put("sourceProductCode",si.getSourceProductCode() ); // 散标信息编号.
				scatterInvest.setSourceProductCode(si.getSourceProductCode());
				// 借款企业统一社会信用代码.
				param.put("userIdcardHash",si.getUserIdcardHash()); // 借款企业统一社会信用代码hash值.
				scatterInvest.setUserIdcardHash(si.getUserIdcardHash());
				param.put("loanUse", LoanUseEnum.LOAN_USE_2.getValue()); // 借款用途.
				scatterInvest.setLoanUse(LoanUseEnum.LOAN_USE_2.getValue());
				param.put("loanDescribe", si.getLoanDescribe()); // 借款说明，借款人借款具体描述.
				scatterInvest.setLoanDescribe(si.getLoanDescribe());
				param.put("loanRate",si.getLoanRate()); // 借款年利率，借款合同签订的借款年利率。此数据必须是小数，保留6 位。说明：该借款年利率计算不含服务费。如果网贷机构本身记录是日利率，请乘以365 以后上传，如果公布的是月利率，请乘以12 以后上传.
				scatterInvest.setLoanRate(si.getLoanRate());
				param.put("amount",si.getAmount()); // 借款金额（元）实际融资金额，该金额为合同金额，如借款人a 成功申请1000元借款额度，去除相关费用到手900 元。这里1000 元为借款金额.
				scatterInvest.setAmount(si.getAmount());
				param.put("surplusAmount", si.getSurplusAmount()); // 剩余借款本金，剩余借款本金是指2019年3 月1 日之前存量散标还未偿还的本金，2019 年3 月1 日之后产生的数据，传0。.
				scatterInvest.setSurplusAmount(si.getSurplusAmount());
				param.put("term",si.getTerm()); // 借款期限(天)，该借款期限为合同期限。借款期限单位必须是天，说明：如果网贷机构期限记录的是月，请乘以30 以后上传，如果公布的是年，请乘以365 以后上传。.
				scatterInvest.setTerm(si.getTerm());
				param.put("payType", PayTypeEnum.PAY_TYPE_3.getValue()); // 还款类型，合同约定的还款方式。.
				scatterInvest.setPayType(PayTypeEnum.PAY_TYPE_3.getValue());
				// 服务费率.
				param.put("serviceCost", si.getServiceCost()); // 服务费（元），服务费，部分网贷机构也叫其为手续费，即为平台收取的服务费用，不包括担保手续费等支付第三方平台其它费用。服务费必须按合同内容填写.
				scatterInvest.setServiceCost(si.getServiceCost());
				param.put("loanType", LoanTypeEnum.LOAN_TYPE_2.getValue()); // 借款类型.
				scatterInvest.setLoanType(LoanTypeEnum.LOAN_TYPE_2.getValue());
				param.put("securityType", SecurityTypeEnum.SECURITY_TYPE_NEGATIVE_1.getValue()); // 担保方式.
				scatterInvest.setSecurityType(SecurityTypeEnum.SECURITY_TYPE_NEGATIVE_1.getValue());
				param.put("securityCompanyAmount", ""); // （选填）担保公司数量.
				scatterInvest.setSecurityCompanyAmount("");
				param.put("securityCompanyName", ""); // （选填）第三方担保机构/保险公司的全称（第三方担保机构/自然人的全称，不允许脱敏。说明：如果是多家担保公司以英文逗号隔开）.
				scatterInvest.setSecurityCompanyName("");
				param.put("securityCompanyIdcard", ""); // （选填）担保机构或保险公司统一社会信用代码（第三方担保机构的统一社会信用代码或自然人证件号，不允许脱敏。说明：如果是多家担保公司以英文逗号隔开，顺序和securityCompanyName字段保持一致）.
				scatterInvest.setSecurityCompanyIdcard("");
				param.put("isFinancingAssure", ""); // （选填）是否具有融资担保业务经营许可证（0-具有, 1-不具有说明：如果是多家担保公司以英文逗号隔开，顺序和securityCompanyName字段保持一致）.
				scatterInvest.setIsFinancingAssure("");
				param.put("securityAmount", ""); // （选填）担保手续费（ 合同金额）（元）（）借款人借款时，需支付给第三方担保公司/自然人的担保费用（说明：如一个借款人在一次借款中涉及多家担保公司同时担保时，担保手续费取相关所有公司担保金额之和）.
				scatterInvest.setSecurityamount("");
				param.put("projectSource", ServerURLConfig.PLATFORM_NAME); // 散标来源（借款端平台名称（机构名称），如：项目来源本平台，则直接传本平台名称）.
				scatterInvest.setProjectSource(ServerURLConfig.PLATFORM_NAME);
				scatterInvest.setBatchNum(batchNum);
				scatterInvest.setSentTime(sentTime);
				int insert = scatterInvestDao.insert(scatterInvest);
				if (insert == 1) {
					log.info("散标信息插入成功！");
				} else {
					log.info("散标信息插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_2.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量散标信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_SCATTER_INVEST, "utf-8");
			log.info("存量散标信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
	 * methods: pushScatterInvestInfo <br>
	 * description: 存量散标信息，时间节点20190301000000. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月8日 上午11:29:52
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestInfo(List<String> projectIdList) {

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
				// 国家应急中心散标信息.
				ScatterInvest scatterInvest = new ScatterInvest();
				/**
				 * 散标信息封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				scatterInvest.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				scatterInvest.setSourceCode(ServerURLConfig.SOURCE_CODE);
				WloanTermProject project = wloanTermProjectDao.get(projectId);
				if (project != null) {
					param.put("productStartTime", DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss")); // 开标时间标的对外发布时间，年-月- 日时: 分: 秒24H,NTP 时间），格式yyyy-MM-dd HH:mm:ss.
					scatterInvest.setProductStartTime(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
					param.put("productName", project.getName()); // 散标名称.
					scatterInvest.setProductName(project.getName());
					param.put("sourceProductCode", project.getSn()); // 散标信息编号.
					scatterInvest.setSourceProductCode(project.getSn());
					// 融资主体.
					WloanSubject subject = wloanSubjectDao.get(project.getSubjectId());
					if (subject != null) {
						// 借款企业统一社会信用代码.
						param.put("userIdcardHash", tool.idCardHash(subject.getBusinessNo())); // 借款企业统一社会信用代码hash值.
						scatterInvest.setUserIdcardHash(tool.idCardHash(subject.getBusinessNo()));
					}
					param.put("loanUse", LoanUseEnum.LOAN_USE_2.getValue()); // 借款用途.
					scatterInvest.setLoanUse(LoanUseEnum.LOAN_USE_2.getValue());
					param.put("loanDescribe", project.getPurpose()); // 借款说明，借款人借款具体描述.
					scatterInvest.setLoanDescribe(project.getPurpose());
					param.put("loanRate", NumberUtils.scaleSixStr(NumberUtils.divide(project.getAnnualRate(), 100D))); // 借款年利率，借款合同签订的借款年利率。此数据必须是小数，保留6 位。说明：该借款年利率计算不含服务费。如果网贷机构本身记录是日利率，请乘以365 以后上传，如果公布的是月利率，请乘以12 以后上传.
					scatterInvest.setLoanRate(NumberUtils.scaleSixStr(NumberUtils.divide(project.getAnnualRate(), 100D)));
					param.put("amount", NumberUtils.scaleDoubleStr(project.getCurrentRealAmount())); // 借款金额（元）实际融资金额，该金额为合同金额，如借款人a 成功申请1000元借款额度，去除相关费用到手900 元。这里1000 元为借款金额.
					scatterInvest.setAmount(NumberUtils.scaleDoubleStr(project.getCurrentRealAmount()));
					param.put("surplusAmount", NumberUtils.scaleDoubleStr(project.getCurrentRealAmount())); // 剩余借款本金，剩余借款本金是指2019年3 月1 日之前存量散标还未偿还的本金，2019 年3 月1 日之后产生的数据，传0。.
					scatterInvest.setSurplusAmount(NumberUtils.scaleDoubleStr(project.getCurrentRealAmount()));
					param.put("term", project.getSpan() + ""); // 借款期限(天)，该借款期限为合同期限。借款期限单位必须是天，说明：如果网贷机构期限记录的是月，请乘以30 以后上传，如果公布的是年，请乘以365 以后上传。.
					scatterInvest.setTerm(project.getSpan() + "");
					param.put("payType", PayTypeEnum.PAY_TYPE_3.getValue()); // 还款类型，合同约定的还款方式。.
					scatterInvest.setPayType(PayTypeEnum.PAY_TYPE_3.getValue());
					// 服务费率.
					if (StringUtils.isBlank(project.getReplaceRepayId())) { // 安心投没有代偿户.
						List<ServiceCostPojo> axtServiceCostList = ServiceCostUtil.axtServiceCost(project.getOnlineDate());
						log.info("安心投项目服务费率表：{}", com.alibaba.fastjson.JSONObject.toJSONString(axtServiceCostList));
						String serviceCost = "0.00";
						for (ServiceCostPojo model : axtServiceCostList) {
							if (model.getSpan().equals(project.getSpan().toString())) {
								double serviceCost_A = NumberUtils.multiply(project.getAmount(), Double.parseDouble(model.getServiceRate()));
								double serviceCost_B = NumberUtils.divide(serviceCost_A, 36500, 2);
								serviceCost = NumberUtils.scaleDoubleStr(NumberUtils.multiply(serviceCost_B, project.getSpan())); // 保留小数点后2 位.
								log.info("融资金额：{}，服务利率：{}，融资期限：{}，serviceCost_A：{}，serviceCost_B：{}，serviceCost：{}", project.getAmount(), Double.parseDouble(model.getServiceRate()), project.getSpan(), serviceCost_A, serviceCost_B, serviceCost);
							}
						}
						param.put("serviceCost", serviceCost); // 服务费（元），服务费，部分网贷机构也叫其为手续费，即为平台收取的服务费用，不包括担保手续费等支付第三方平台其它费用。服务费必须按合同内容填写.
						scatterInvest.setServiceCost(serviceCost);
					} else {
						CreditMiddlemenRate cmr = new CreditMiddlemenRate();
						cmr.setCreditUserId(project.getReplaceRepayId());
						List<CreditMiddlemenRate> cmrList = creditMiddlemenRateDao.findList(cmr);
						String serviceCost = "0.00";
						for (CreditMiddlemenRate model : cmrList) {
							if (model.getSpan().equals(project.getSpan().toString())) {
								double serviceCost_A = NumberUtils.multiply(project.getAmount(), Double.parseDouble(model.getRate()));
								double serviceCost_B = NumberUtils.divide(serviceCost_A, 36500, 2);
								serviceCost = NumberUtils.scaleDoubleStr(NumberUtils.multiply(serviceCost_B, project.getSpan())); // 保留小数点后2 位.
								log.info("融资金额：{}，服务利率：{}，融资期限：{}，serviceCost_A：{}，serviceCost_B：{}，serviceCost：{}", project.getAmount(), Double.parseDouble(model.getRate()), project.getSpan(), serviceCost_A, serviceCost_B, serviceCost);
							}
						}
						param.put("serviceCost", serviceCost); // 服务费（元），服务费，部分网贷机构也叫其为手续费，即为平台收取的服务费用，不包括担保手续费等支付第三方平台其它费用。服务费必须按合同内容填写.
						scatterInvest.setServiceCost(serviceCost);
					}
					param.put("loanType", LoanTypeEnum.LOAN_TYPE_2.getValue()); // 借款类型.
					scatterInvest.setLoanType(LoanTypeEnum.LOAN_TYPE_2.getValue());
					param.put("securityType", SecurityTypeEnum.SECURITY_TYPE_NEGATIVE_1.getValue()); // 担保方式.
					scatterInvest.setSecurityType(SecurityTypeEnum.SECURITY_TYPE_NEGATIVE_1.getValue());
					param.put("securityCompanyAmount", ""); // （选填）担保公司数量.
					scatterInvest.setSecurityCompanyAmount("");
					param.put("securityCompanyName", ""); // （选填）第三方担保机构/保险公司的全称（第三方担保机构/自然人的全称，不允许脱敏。说明：如果是多家担保公司以英文逗号隔开）.
					scatterInvest.setSecurityCompanyName("");
					param.put("securityCompanyIdcard", ""); // （选填）担保机构或保险公司统一社会信用代码（第三方担保机构的统一社会信用代码或自然人证件号，不允许脱敏。说明：如果是多家担保公司以英文逗号隔开，顺序和securityCompanyName字段保持一致）.
					scatterInvest.setSecurityCompanyIdcard("");
					param.put("isFinancingAssure", ""); // （选填）是否具有融资担保业务经营许可证（0-具有, 1-不具有说明：如果是多家担保公司以英文逗号隔开，顺序和securityCompanyName字段保持一致）.
					scatterInvest.setIsFinancingAssure("");
					param.put("securityAmount", ""); // （选填）担保手续费（ 合同金额）（元）（）借款人借款时，需支付给第三方担保公司/自然人的担保费用（说明：如一个借款人在一次借款中涉及多家担保公司同时担保时，担保手续费取相关所有公司担保金额之和）.
					scatterInvest.setSecurityamount("");
					param.put("projectSource", ServerURLConfig.PLATFORM_NAME); // 散标来源（借款端平台名称（机构名称），如：项目来源本平台，则直接传本平台名称）.
					scatterInvest.setProjectSource(ServerURLConfig.PLATFORM_NAME);
					scatterInvest.setBatchNum(batchNum);
					scatterInvest.setSentTime(sentTime);
					int insert = scatterInvestDao.insert(scatterInvest);
					if (insert == 1) {
						log.info("散标信息插入成功！");
					} else {
						log.info("散标信息插入失败！");
					}
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_2.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量散标信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_SCATTER_INVEST, "utf-8");
			log.info("存量散标信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_2.getValue());
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
