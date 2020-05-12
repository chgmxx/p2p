package com.power.platform.ifcert.demo.service;

import java.util.*;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.ifcert.creditor.service.CreditorDataAccessService;
import com.power.platform.ifcert.dao.BatchNumCheckDao;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.CreditorDao;
import com.power.platform.ifcert.dao.IfCertUserInfoDao;
import com.power.platform.ifcert.dao.LendParticularsDao;
import com.power.platform.ifcert.dao.LendProductConfigDao;
import com.power.platform.ifcert.dao.LendProductDao;
import com.power.platform.ifcert.dao.RepayPlanDao;
import com.power.platform.ifcert.dao.ScatterInvestDao;
import com.power.platform.ifcert.dao.StatusDao;
import com.power.platform.ifcert.dao.TransactDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.BatchNumCheck;
import com.power.platform.ifcert.entity.Creditor;
import com.power.platform.ifcert.entity.IfCertUserInfo;
import com.power.platform.ifcert.entity.LendParticulars;
import com.power.platform.ifcert.entity.LendProduct;
import com.power.platform.ifcert.entity.LendProductConfig;
import com.power.platform.ifcert.entity.RepayPlan;
import com.power.platform.ifcert.entity.ScatterInvest;
import com.power.platform.ifcert.entity.Status;
import com.power.platform.ifcert.entity.Transact;
import com.power.platform.ifcert.lendProductConfig.service.LendProductConfigDataAccessService;
import com.power.platform.ifcert.lendparticulars.service.LendParticularsDataAccessService;
import com.power.platform.ifcert.lendproduct.service.LendProductDataAccessService;
import com.power.platform.ifcert.repayplan.service.RepayPlanDataAccessService;
import com.power.platform.ifcert.scatterInvest.service.ScatterInvestDataAccessService;
import com.power.platform.ifcert.status.service.ScatterInvestStatusDataAccessService;
import com.power.platform.ifcert.transact.service.TransactDataAccessService;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.userInfo.service.IfcertUserInfoDataAccessService;
import com.power.platform.ifcert.utils.http.HttpsUtil;
import com.power.platform.ifcert.utils.sha.ShaApiKey;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.userinfo.dao.UserInfoDao;

@Component
@Path("/ifcert")
@Service("ifcertService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class IfcertService {

	private static final Logger log = LoggerFactory.getLogger(IfcertService.class);

	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private IfcertUserInfoDataAccessService ifcertUserInfoDataAccessService;
	@Autowired
	private ScatterInvestDataAccessService scatterInvestDataAccessService;
	@Autowired
	private ScatterInvestStatusDataAccessService scatterInvestStatusDataAccessService;
	@Autowired
	private RepayPlanDataAccessService repayPlanDataAccessService;
	@Autowired
	private CreditorDataAccessService creditorDataAccessService;
	@Autowired
	private TransactDataAccessService transactDataAccessService;
	@Autowired
	private CgbUserTransDetailService cgbUserTransDetailService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Autowired
	private LendProductDataAccessService lendProductDataAccessService;
	@Autowired
	private LendProductConfigDataAccessService lendProductConfigDataAccessService;
	@Autowired
	private LendParticularsDataAccessService lendParticularsDataAccessService;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private LendProductConfigDao lendProductConfigDao;
	@Autowired
	private StatusDao statusDao;
	@Autowired
	private LendProductDao lendProductDao;
	@Autowired
	private LendParticularsDao lendParticularDao;
	@Autowired
	private RepayPlanDao repayPlanDao;
	@Autowired
	private ScatterInvestDao scatterInvestDao;
	@Autowired
	private TransactDao transactDao;
	@Autowired
	private IfCertUserInfoDao ifCertUserInfoDao;
	@Autowired
	private CreditorDao creditorDao;

	@Autowired
	private BatchNumDao batchNumDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushInfDataByBathNum <br>
	 * description: 补推指定批次的数据接口. <br>
	 * author: Roy <br>
	 * date: 2019年7月26日 上午11:08:12
	 * 
	 * @param InfType
	 * @param BatchNum
	 * @return
	 */
	@POST
	@Path("/fillPushInfDataByBathNum")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> fillPushInfDataByBathNum(@FormParam("InfType") String InfType, @FormParam("BatchNum") String BatchNum) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			if (StringUtils.isBlank(InfType) || StringUtils.isBlank(BatchNum)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else { // 补推该批次数据.
				log.info("接口类型，value = {}，text = {}，", InfType, InfTypeEnum.getTextByValue(InfType));
				log.info("批次号，batchNum = {}", BatchNum);
				if ("".equals(InfTypeEnum.getTextByValue(InfType))) { // 未知结果-.
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
					return result;
				}
				// 以接口类型区分，补推
				switch (InfType) {
					case "87":
						// 补推产品配置.
						LendProductConfig lpc = new LendProductConfig();
						lpc.setBatchNum(BatchNum);
						List<LendProductConfig> lpConfigs = lendProductConfigDao.findList(lpc);
						log.info("该批次总量:{}条产品配置信息 ...", lpConfigs.size());
						for (int i = 0; i < lpConfigs.size(); i++) {
							LendProductConfig lpConfig = lpConfigs.get(i); // 待推送产品配置信息.
							lpConfig.setSourceProductCode(lpConfig.getSourceFinancingCode()); // 散标信息编号.
						}
						Map<String, Object> lendProductConfigResult = lendProductConfigDataAccessService.fillPushLendProductConfigInfo(lpConfigs);
						log.info("补推产品配置信息，响应信息：{}", JSON.toJSONString(lendProductConfigResult));
						break;
					case "1":
						// 补推用户信息.
						IfCertUserInfo ui = new IfCertUserInfo();
						ui.setBatchNum(BatchNum);
						List<IfCertUserInfo> uiList = ifCertUserInfoDao.findList(ui);
						if(uiList!=null&&uiList.size()>0) {
							Map<String, Object> ifCertUserInfoResult = ifcertUserInfoDataAccessService.fillPushUserInfo(uiList);
							log.info("补推用户信息，响应信息：{}", JSON.toJSONString(ifCertUserInfoResult));
						}
						break;
					case "2":
						// 补推散标信息.
						ScatterInvest scatterInvest = new ScatterInvest();
						scatterInvest.setBatchNum(BatchNum);
						List<ScatterInvest> siList = scatterInvestDao.findList(scatterInvest);
						if(siList!=null&&siList.size()>0) {
							Map<String, Object> scatterInvestResult = scatterInvestDataAccessService.fillPushScatterInvestInfo(siList);
							log.info("补推散标信息，响应信息：{}", JSON.toJSONString(scatterInvestResult));
						}
						break;
					case "4":
						// 补推交易流水.
						Transact t = new Transact();
						t.setBatchNum(BatchNum);
						List<Transact> tList = transactDao.findList(t);
						if(tList!=null&&tList.size()>0) {
							Map<String, Object> transactResult = transactDataAccessService.fillPushTransactInterestInfo(tList);
							log.info("补推交易流水，响应信息：{}", JSON.toJSONString(transactResult));
						}
						break;
					case "6":
						// 补推散标状态.
						Status sta = new Status();
						sta.setBatchNum(BatchNum);
						List<Status> staList = statusDao.findList(sta);
						if(staList!=null&&staList.size()>0) {
							Map<String, Object> statusResult = scatterInvestStatusDataAccessService.fillPushScatterInvestStatusInfo(staList);
							log.info("补推散标状态，响应信息：{}", JSON.toJSONString(statusResult));
						}
						break;
					case "81":
						// 补推还款计划.
						RepayPlan rp = new RepayPlan();
						rp.setBatchNum(BatchNum);
						List<RepayPlan> rpList = repayPlanDao.findList(rp);
						if(rpList!=null&&rpList.size()>0) {
							Map<String, Object> rpListResult = repayPlanDataAccessService.fillPushRepayPlanInfo(rpList);
							log.info("补推还款计划，响应信息：{}", JSON.toJSONString(rpListResult));
						}
						break;
					case "82":
						// 补推初始债权.
						Creditor creditor = new Creditor();
						creditor.setBatchNum(BatchNum);
						List<Creditor> creditorList = creditorDao.findList(creditor);
						if(creditorList!=null&&creditorList.size()>0) {
							Map<String, Object> creditorResult = creditorDataAccessService.filePushCreditorInfo(creditorList);
							log.info("补推产品配置信息，响应信息：{}", JSON.toJSONString(creditorResult));
						}
						break;
					case "86":
						// 补推产品信息.
						LendProduct lendProduct = new LendProduct();
						lendProduct.setBatchNum(BatchNum);
						List<LendProduct> lendProductList = lendProductDao.findList(lendProduct);
						if(lendProductList!=null&&lendProductList.size()>0) {
							Map<String, Object> lendProductResult = lendProductDataAccessService.fillPushLendProduct(lendProductList);
							log.info("补推产品信息，响应信息：{}", JSON.toJSONString(lendProductResult));
						}
						break;
					case "88":
						// 补推投资明细.
						LendParticulars lendParticular = new LendParticulars();
						lendParticular.setBatchNum(BatchNum);
						List<LendParticulars> lendParticularList = lendParticularDao.findList(lendParticular);
						if(lendParticularList!=null&&lendParticularList.size()>0) {
							Map<String, Object> LendParticularsResult = lendParticularsDataAccessService.fillPushLendParticularsInvTransInfo(lendParticularList);
							log.info("补推投资明细，响应信息：{}", JSON.toJSONString(LendParticularsResult));
						}
						break;
					default:
						log.info("该批次数据接口未开发，请联系程序猿小哥哥 ...");
						break;
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 
	 * methods: fillPush <br>
	 * description: 根据时间区间补推数据. <br>
	 * author: Yangzf <br>
	 * @param InfType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@POST
	@Path("/fillPush")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> fillPush(@FormParam("InfType") String InfType, @FormParam("startTime") String startTime, @FormParam("endTime") String endTime) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			if (StringUtils.isBlank(InfType) || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else { // 补推该批次数据.
				log.info("接口类型，value = {}，text = {}，", InfType, InfTypeEnum.getTextByValue(InfType));
				if ("".equals(InfTypeEnum.getTextByValue(InfType))) { // 未知结果-.
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
					return result;
				}
				List<BatchNum> bnList = batchNumDao.findBatchNum(InfType,startTime,endTime);
				if(bnList!=null && bnList.size()>0) {
					for (BatchNum batchNum : bnList) {
						// 以接口类型区分，补推				
						switch (InfType) {
						case "87":
							// 补推产品配置.
							LendProductConfig lpc = new LendProductConfig();
							lpc.setBatchNum(batchNum.getBatchNum());
							List<LendProductConfig> lpConfigs = lendProductConfigDao.findList(lpc);
							log.info("该批次总量:{}条产品配置信息 ...", lpConfigs.size());
							for (int i = 0; i < lpConfigs.size(); i++) {
								LendProductConfig lpConfig = lpConfigs.get(i); // 待推送产品配置信息.
								lpConfig.setSourceProductCode(lpConfig.getSourceFinancingCode()); // 散标信息编号.
							}
							Map<String, Object> lendProductConfigResult = lendProductConfigDataAccessService.fillPushLendProductConfigInfo(lpConfigs);
							log.info("补推产品配置信息，响应信息：{}", JSON.toJSONString(lendProductConfigResult));
							break;
						case "1":
							// 补推用户信息.
							IfCertUserInfo ui = new IfCertUserInfo();
							ui.setBatchNum(batchNum.getBatchNum());
							List<IfCertUserInfo> uiList = ifCertUserInfoDao.findList(ui);
							if(uiList!=null&&uiList.size()>0) {
								Map<String, Object> ifCertUserInfoResult = ifcertUserInfoDataAccessService.fillPushUserInfo(uiList);
								log.info("补推用户信息，响应信息：{}", JSON.toJSONString(ifCertUserInfoResult));
							}
							break;
						case "2":
							// 补推散标信息.
							ScatterInvest scatterInvest = new ScatterInvest();
							scatterInvest.setBatchNum(batchNum.getBatchNum());
							List<ScatterInvest> siList = scatterInvestDao.findList(scatterInvest);
							if(siList!=null&&siList.size()>0) {
								Map<String, Object> scatterInvestResult = scatterInvestDataAccessService.fillPushScatterInvestInfo(siList);
								log.info("补推散标信息，响应信息：{}", JSON.toJSONString(scatterInvestResult));
							}
							break;
						case "4":
							// 补推交易流水.
							Transact t = new Transact();
							t.setBatchNum(batchNum.getBatchNum());
							List<Transact> tList = transactDao.findList(t);
							if(tList!=null&&tList.size()>0) {
								Map<String, Object> transactResult = transactDataAccessService.fillPushTransactInterestInfo(tList);
								log.info("补推交易流水，响应信息：{}", JSON.toJSONString(transactResult));
							}
							break;
						case "6":
							// 补推散标状态.
							Status sta = new Status();
							sta.setBatchNum(batchNum.getBatchNum());
							List<Status> staList = statusDao.findList(sta);
							if(staList!=null&&staList.size()>0) {
								Map<String, Object> statusResult = scatterInvestStatusDataAccessService.fillPushScatterInvestStatusInfo(staList);
								log.info("补推散标状态，响应信息：{}", JSON.toJSONString(statusResult));
							}
							break;
						case "81":
							// 补推还款计划.
							RepayPlan rp = new RepayPlan();
							rp.setBatchNum(batchNum.getBatchNum());
							List<RepayPlan> rpList = repayPlanDao.findList(rp);
							if(rpList!=null&&rpList.size()>0) {
								Map<String, Object> rpListResult = repayPlanDataAccessService.fillPushRepayPlanInfo(rpList);
								log.info("补推还款计划，响应信息：{}", JSON.toJSONString(rpListResult));
							}
							break;
						case "82":
							// 补推初始债权.
							Creditor creditor = new Creditor();
							creditor.setBatchNum(batchNum.getBatchNum());
							List<Creditor> creditorList = creditorDao.findList(creditor);
							if(creditorList!=null&&creditorList.size()>0) {
								Map<String, Object> creditorResult = creditorDataAccessService.filePushCreditorInfo(creditorList);
								log.info("补推产品配置信息，响应信息：{}", JSON.toJSONString(creditorResult));
							}
							break;
						case "86":
							// 补推产品信息.
							LendProduct lendProduct = new LendProduct();
							lendProduct.setBatchNum(batchNum.getBatchNum());
							List<LendProduct> lendProductList = lendProductDao.findList(lendProduct);
							if(lendProductList!=null&&lendProductList.size()>0) {
								Map<String, Object> lendProductResult = lendProductDataAccessService.fillPushLendProduct(lendProductList);
								log.info("补推产品信息，响应信息：{}", JSON.toJSONString(lendProductResult));
							}
							break;
						case "88":
							// 补推投资明细.
							LendParticulars lendParticular = new LendParticulars();
							lendParticular.setBatchNum(batchNum.getBatchNum());
							List<LendParticulars> lendParticularList = lendParticularDao.findList(lendParticular);
							if(lendParticularList!=null&&lendParticularList.size()>0) {
								Map<String, Object> LendParticularsResult = lendParticularsDataAccessService.fillPushLendParticularsInvTransInfo(lendParticularList);
								log.info("补推投资明细，响应信息：{}", JSON.toJSONString(LendParticularsResult));
							}
							break;
						default:
							log.info("该批次数据接口未开发，请联系程序猿小哥哥 ...");
							break;
						}
						batchNum.setCode("0005");
						batchNum.setStatus("05");
						int i = batchNumDao.update(batchNum);
						log.info("批次号更新:{}", i == 1 ? "成功" : "失败");
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * methods: fillPushSameData <br>
	 * description: 解决批次号相同问题 <br>
	 * author: Yangzf <br>
	 * date: 2019年8月4日 上午10:28:12
	 * 
	 * @param InfType
	 * @param BatchNum
	 * @param sendTime
	 * @return
	 */
	@POST
	@Path("/fillPushSameData")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> fillPushData(@FormParam("InfType") String InfType, @FormParam("BatchNum") String BatchNum, @FormParam("sendTime") String sendTime) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			if (StringUtils.isBlank(InfType) || StringUtils.isBlank(BatchNum)|| StringUtils.isBlank(sendTime)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else { // 补推该批次数据.
				log.info("接口类型，value = {}，text = {}，", InfType, InfTypeEnum.getTextByValue(InfType));
				log.info("批次号，batchNum = {}", BatchNum);
				log.info("发送时间，sendTime = {}", sendTime);
				if ("".equals(InfTypeEnum.getTextByValue(InfType))) { // 未知结果-.
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
					return result;
				}
				// 以接口类型区分，补推
				switch (InfType) {
					case "87":
						// 补推产品配置.
						LendProductConfig lpc = new LendProductConfig();
						lpc.setBatchNum(BatchNum);
						lpc.setSendTime(sendTime);
						List<LendProductConfig> lpConfigs = lendProductConfigDao.findList(lpc);
						log.info("该批次总量:{}条产品配置信息 ...", lpConfigs.size());
						for (int i = 0; i < lpConfigs.size(); i++) {
							LendProductConfig lpConfig = lpConfigs.get(i); // 待推送产品配置信息.
							lpConfig.setSourceProductCode(lpConfig.getSourceFinancingCode()); // 散标信息编号.
						}
						Map<String, Object> lendProductConfigResult = lendProductConfigDataAccessService.fillPushLendProductConfigInfo(lpConfigs);
						log.info("补推产品配置信息，响应信息：{}", JSON.toJSONString(lendProductConfigResult));
						break;
					case "1":
						// 补推用户信息.
						IfCertUserInfo ui = new IfCertUserInfo();
						ui.setBatchNum(BatchNum);
						ui.setSendTime(sendTime);
						List<IfCertUserInfo> uiList = ifCertUserInfoDao.findList(ui);
						if(uiList!=null&&uiList.size()>0) {
							Map<String, Object> ifCertUserInfoResult = ifcertUserInfoDataAccessService.fillPushUserInfo(uiList);
							log.info("补推用户信息，响应信息：{}", JSON.toJSONString(ifCertUserInfoResult));
						}
						break;
					case "2":
						// 补推散标信息.
						ScatterInvest scatterInvest = new ScatterInvest();
						scatterInvest.setBatchNum(BatchNum);
						scatterInvest.setSentTime(sendTime);
						List<ScatterInvest> siList = scatterInvestDao.findList(scatterInvest);
						if(siList!=null&&siList.size()>0) {
							Map<String, Object> scatterInvestResult = scatterInvestDataAccessService.fillPushScatterInvestInfo(siList);
							log.info("补推散标信息，响应信息：{}", JSON.toJSONString(scatterInvestResult));
						}
						break;
					case "4":
						// 补推交易流水.
						Transact t = new Transact();
						t.setBatchNum(BatchNum);
						t.setSendTime(sendTime);
						List<Transact> tList = transactDao.findList(t);
						if(tList!=null&&tList.size()>0) {
							Map<String, Object> transactResult = transactDataAccessService.fillPushTransactInterestInfo(tList);
							log.info("补推交易流水，响应信息：{}", JSON.toJSONString(transactResult));
						}
						break;
					case "6":
						// 补推散标状态.
						Status sta = new Status();
						sta.setBatchNum(BatchNum);
						sta.setSendTime(sendTime);
						List<Status> staList = statusDao.findList(sta);
						if(staList!=null&&staList.size()>0) {
							Map<String, Object> statusResult = scatterInvestStatusDataAccessService.fillPushScatterInvestStatusInfo(staList);
							log.info("补推散标状态，响应信息：{}", JSON.toJSONString(statusResult));
						}
						break;
					case "81":
						// 补推还款计划.
						RepayPlan rp = new RepayPlan();
						rp.setBatchNum(BatchNum);
						rp.setSendTime(sendTime);
						List<RepayPlan> rpList = repayPlanDao.findList(rp);
						if(rpList!=null&&rpList.size()>0) {
							Map<String, Object> rpListResult = repayPlanDataAccessService.fillPushRepayPlanInfo(rpList);
							log.info("补推还款计划，响应信息：{}", JSON.toJSONString(rpListResult));
						}
						break;
					case "82":
						// 补推初始债权.
						Creditor creditor = new Creditor();
						creditor.setBatchNum(BatchNum);
						creditor.setSendTime(sendTime);
						List<Creditor> creditorList = creditorDao.findList(creditor);
						if(creditorList!=null&&creditorList.size()>0) {
							Map<String, Object> creditorResult = creditorDataAccessService.filePushCreditorInfo(creditorList);
							log.info("补推产品配置信息，响应信息：{}", JSON.toJSONString(creditorResult));
						}
						break;
					case "86":
						// 补推产品信息.
						LendProduct lendProduct = new LendProduct();
						lendProduct.setBatchNum(BatchNum);
						lendProduct.setSendTime(sendTime);
						List<LendProduct> lendProductList = lendProductDao.findList(lendProduct);
						if(lendProductList!=null&&lendProductList.size()>0) {
							Map<String, Object> lendProductResult = lendProductDataAccessService.fillPushLendProduct(lendProductList);
							log.info("补推产品信息，响应信息：{}", JSON.toJSONString(lendProductResult));
						}
						break;
					case "88":
						// 补推投资明细.
						LendParticulars lendParticular = new LendParticulars();
						lendParticular.setBatchNum(BatchNum);
						lendParticular.setSendTime(sendTime);
						List<LendParticulars> lendParticularList = lendParticularDao.findList(lendParticular);
						if(lendParticularList!=null&&lendParticularList.size()>0) {
							Map<String, Object> LendParticularsResult = lendParticularsDataAccessService.fillPushLendParticularsInvTransInfo(lendParticularList);
							log.info("补推投资明细，响应信息：{}", JSON.toJSONString(LendParticularsResult));
						}
						break;
					default:
						log.info("该批次数据接口未开发，请联系程序猿小哥哥 ...");
						break;
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTakeBackPrincipalZ <br>
	 * description: 增量-出借人收回本息-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月28日 上午10:07:31
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvTakeBackPrincipalZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTakeBackPrincipalZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan cutd = new WloanTermUserPlan();
					cutd.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
					cutd.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
					Page<WloanTermUserPlan> cutdPage = wloanTermUserPlanService.findUserPlanListZ(page, cutd);
					System.out.println("投资明细收回本金利息当前页：" + pageNo);
					System.out.println("投资明细收回本金利息最后页：" + cutdPage.getLast());
					List<WloanTermUserPlan> cutdList = cutdPage.getList();
					System.out.println("投资明细收本金回利息集合大小：" + cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = lendParticularsDataAccessService.pushLendParticularsInvTakeBackPrincipal(cutdList, currentTimeMillis);
						log.info("接口响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTakeBackPrincipalC <br>
	 * description: 存量-出借人还本付息-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月17日 上午9:55:34
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvTakeBackPrincipalC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTakeBackPrincipalC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan cutd = new WloanTermUserPlan();
					cutd.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
					cutd.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
					Page<WloanTermUserPlan> cutdPage = wloanTermUserPlanService.findUserPlanList(page, cutd);
					System.out.println("投资明细收回本金利息当前页：" + pageNo);
					System.out.println("投资明细收回本金利息最后页：" + cutdPage.getLast());
					List<WloanTermUserPlan> cutdList = cutdPage.getList();
					System.out.println("投资明细收本金回利息集合大小：" + cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = lendParticularsDataAccessService.pushLendParticularsInvTakeBackPrincipal(cutdList, currentTimeMillis);
						log.info("接口响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTakeBackInterestZ <br>
	 * description: 增量-出借人收回利息-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月28日 上午10:04:54
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvTakeBackInterestZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTakeBackInterestZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan wtup = new WloanTermUserPlan();
					wtup.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2); // 还款类型-2-收回利息.
					wtup.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3); // 还款状态-3-成功还款.
					Page<WloanTermUserPlan> wtupPage = wloanTermUserPlanService.findUserPlanListZ(page, wtup);
					System.out.println("增量-出借人收回利息-当前页码：" + pageNo);
					System.out.println("增量-出借人收回利息-最后页码：" + wtupPage.getLast());
					List<WloanTermUserPlan> wtupList = wtupPage.getList();
					System.out.println("增量-出借人收回利息-列表集合大小：" + wtupList.size());
					if (wtupList != null && wtupList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = lendParticularsDataAccessService.pushLendParticularsInvTakeBackInterest(wtupList, currentTimeMillis);
						log.info("接口响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > wtupPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTakeBackInterestC <br>
	 * description: 存量-出借人收回利息-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月17日 上午9:52:19
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvTakeBackInterestC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTakeBackInterestC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan wtup = new WloanTermUserPlan();
					wtup.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2); // 还款类型-2-收回利息.
					wtup.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3); // 还款状态-3-成功还款.
					Page<WloanTermUserPlan> wtupPage = wloanTermUserPlanService.findUserPlanList(page, wtup);
					System.out.println("存量-出借人收回利息-当前页码：" + pageNo);
					System.out.println("存量-出借人收回利息-最后页码：" + wtupPage.getLast());
					List<WloanTermUserPlan> wtupList = wtupPage.getList();
					System.out.println("存量-出借人收回利息-列表集合大小：" + wtupList.size());
					if (wtupList != null && wtupList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = lendParticularsDataAccessService.pushLendParticularsInvTakeBackInterest(wtupList, currentTimeMillis);
						log.info("接口响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > wtupPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvCashBackZ <br>
	 * description: 增量-出借人返现-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月28日 上午10:02:38
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvCashBackZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvCashBackZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 抵用券.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_1); // 处理中.
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					cutd.setEndTransDate(new Date());
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactInvestUserInfoPageZ(page, cutd);
					System.out.println("投资明细-出借返现-当前页：" + pageNo);
					System.out.println("投资明细-出借返现-最后页：" + cutdPage.getLast());
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					System.out.println("投资明细-出借返现-集合大小：" + cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("接口响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvCashBackC <br>
	 * description: 存量-出借人出借返现-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月17日 上午9:50:07
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvCashBackC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvCashBackC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_10); // 抵用券.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_1); // 处理中.
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					cutd.setEndTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00"));
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactInvestUserInfoPage(page, cutd);
					System.out.println("投资明细-出借返现-当前页：" + pageNo);
					System.out.println("投资明细-出借返现-最后页：" + cutdPage.getLast());
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					System.out.println("投资明细-出借返现-集合大小：" + cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("接口响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTransInfoZ <br>
	 * description: 增量-出借人出借-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月28日 上午9:58:36
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvTransInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTransInfoZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				List<WloanTermInvest> invests = new ArrayList<WloanTermInvest>();
				for (String projectId : projectIdList) {
					WloanTermProject pro = wloanTermProjectDao.get(projectId);
					if (pro != null) {
						List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(projectId);
						for (WloanTermInvest invest : investList) {
							invests.add(invest);
						}
					}
				}
				int endpoint = 3000; // 批次大小.
				int num = invests.size(); // 数据总记录.
				log.info("该批次总记录数：{}", num);
				// 取商.
				int batchNum = Math.floorDiv(num, endpoint);
				// 取模.
				int modNum = Math.floorMod(num, endpoint);
				if (modNum == 0) {
					log.info("该批次数据分：{}次进行推送！", batchNum);
				} else {
					batchNum = batchNum + 1;
					log.info("该批次数据分：{}次进行推送！", batchNum);
				}
				// 批次数据.
				List<WloanTermInvest> batchList = new ArrayList<WloanTermInvest>();
				// 系统当前时间毫秒值.
				long currentTimeMillis = System.currentTimeMillis();
				for (int i = 0; i < batchNum; i++) {
					int x = i * endpoint;
					if ((i + 1) == batchNum) {
						int y = x + modNum;
						// 清空.
						batchList.clear();
						for (int j = x; j < y; j++) {
							batchList.add(invests.get(j));
						}
					} else {
						int y = (i + 1) * endpoint;
						// 清空.
						batchList.clear();
						for (int j = x; j < y; j++) {
							batchList.add(invests.get(j));
						}
					}
					log.info("当前批次数据：{}", batchList.size());
					currentTimeMillis = currentTimeMillis + 1;
					log.info("批次号：{}", ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis);
					result = lendParticularsDataAccessService.pushLendParticularsInvTransInfo(batchList, currentTimeMillis);
					log.info("接口响应信息：{}", JSON.toJSONString(result));
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvTransInfoC <br>
	 * description: 存量-出借人出借-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月17日 上午9:44:39
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvTransInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvTransInfoC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				List<WloanTermInvest> invests = new ArrayList<WloanTermInvest>();
				for (String projectId : projectIdList) {
					WloanTermProject pro = wloanTermProjectDao.get(projectId);
					if (pro != null) {
						List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(projectId);
						for (WloanTermInvest invest : investList) {
							invests.add(invest);
						}
					}
				}
				int endpoint = 3000; // 批次大小.
				int num = invests.size(); // 数据总记录.
				log.info("该批次总记录数：{}", num);
				// 取商.
				int batchNum = Math.floorDiv(num, endpoint);
				// 取模.
				int modNum = Math.floorMod(num, endpoint);
				if (modNum == 0) {
					log.info("该批次数据分：{}次进行推送！", batchNum);
				} else {
					batchNum = batchNum + 1;
					log.info("该批次数据分：{}次进行推送！", batchNum);
				}
				// 批次数据.
				List<WloanTermInvest> batchList = new ArrayList<WloanTermInvest>();
				// 系统当前时间毫秒值.
				long currentTimeMillis = System.currentTimeMillis();
				for (int i = 0; i < batchNum; i++) {
					int x = i * endpoint;
					if ((i + 1) == batchNum) {
						int y = x + modNum;
						// 清空.
						batchList.clear();
						for (int j = x; j < y; j++) {
							batchList.add(invests.get(j));
						}
					} else {
						int y = (i + 1) * endpoint;
						// 清空.
						batchList.clear();
						for (int j = x; j < y; j++) {
							batchList.add(invests.get(j));
						}
					}
					log.info("当前批次数据：{}", batchList.size());
					currentTimeMillis = currentTimeMillis + 1;
					log.info("批次号：{}", ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis);
					result = lendParticularsDataAccessService.pushLendParticularsInvTransInfo(batchList, currentTimeMillis);
					log.info("接口响应信息：{}", JSON.toJSONString(result));
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvCashZ <br>
	 * description: 增量-出借人提现-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月28日 上午9:50:54
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvCashZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvCashZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1); // 提现.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					cutd.setEndTransDate(new Date());
					Page<CgbUserTransDetail> transCashDetailPage = cgbUserTransDetailService.findTransactInvestUserInfoPageZ(page, cutd);
					log.info("投资明细-出借人提现-当前页 = {}", pageNo);
					log.info("投资明细-出借人提现-最后页 = {}", transCashDetailPage.getLast());
					List<CgbUserTransDetail> cutdList = transCashDetailPage.getList();
					log.info("投资明细-出借人提现-集合大小= {}", cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						Map<String, Object> batchResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("当前批次，result = {}", JSON.toJSONString(batchResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > transCashDetailPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvCashC <br>
	 * description: 存量-出借人提现-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月14日 下午3:32:50
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvCashC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvCashC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1); // 提现.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					cutd.setEndTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00"));
					Page<CgbUserTransDetail> transCashDetailPage = cgbUserTransDetailService.findTransactInvestUserInfoPage(page, cutd);
					log.info("投资明细-出借人提现-当前页 = {}", pageNo);
					log.info("投资明细-出借人提现-最后页 = {}", transCashDetailPage.getLast());
					List<CgbUserTransDetail> cutdList = transCashDetailPage.getList();
					log.info("投资明细-出借人提现-集合大小= {}", cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						Map<String, Object> batchResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("当前批次，result = {}", JSON.toJSONString(batchResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > transCashDetailPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvRechargeZ <br>
	 * description: 增量-出借人充值-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月28日 上午9:22:42
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvRechargeZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvRechargeZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0); // 充值.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					cutd.setEndTransDate(new Date());
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactInvestUserInfoPageZ(page, cutd);
					log.info("投资明细-出借人充值-当前页 = {}", pageNo);
					log.info("投资明细-出借人充值-最后页 = {}", cutdPage.getLast());
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("投资明细-出借人充值-集合大小 = {}", cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						Map<String, Object> batchResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("当前批次，result = {}", JSON.toJSONString(batchResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendParticularsInvRechargeC <br>
	 * description: 存量-出借人充值-投资明细. <br>
	 * author: Roy <br>
	 * date: 2019年6月14日 下午3:27:40
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendParticularsInvRechargeC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendParticularsInvRechargeC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_88.getValue().equals(InfType)) { // 是否是投资明细接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0); // 充值.
					List<Integer> states = new ArrayList<Integer>();
					states.add(CgbUserTransDetailService.TRUST_STATE_2); // 成功.
					cutd.setStates(states);
					cutd.setEndTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00")); // 存量时间节点.
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactInvestUserInfoPage(page, cutd);
					log.info("投资明细-出借人充值-当前页 = {}", pageNo);
					log.info("投资明细-出借人充值-最后页 = {}", cutdPage.getLast());
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("投资明细-出借人充值-集合大小 = {}", cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						Map<String, Object> batchResult = lendParticularsDataAccessService.pushLendParticulars(cutdList, currentTimeMillis);
						log.info("当前批次，result = {}", JSON.toJSONString(batchResult));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendProductConfigInfoZ <br>
	 * description: 增量-推送产品配置信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月5日 下午2:49:05
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendProductConfigInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendProductConfigInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_87.getValue().equals(InfType)) { // 是否是产品配置接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = lendProductConfigDataAccessService.pushLendProductConfigInfo(projectIdList);
				log.info("推送产品配置信息增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendProductConfigInfoC <br>
	 * description: 存量-推送产品配置信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月5日 上午10:26:44
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendProductConfigInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendProductConfigInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_87.getValue().equals(InfType)) { // 是否是产品配置接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = lendProductConfigDataAccessService.pushLendProductConfigInfo(projectIdList);
				log.info("推送产品配置信息存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendProductInfoZ <br>
	 * description: 增量-推送产品信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月5日 下午2:47:00
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendProductInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendProductInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_86.getValue().equals(InfType)) { // 是否是产品信息接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = lendProductDataAccessService.pushLendProduct(projectIdList);
				log.info("推送产品信息增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushLendProductInfoC <br>
	 * description: 存量-推送产品信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月5日 上午9:19:18
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushLendProductInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushLendProductInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_86.getValue().equals(InfType)) { // 是否是产品信息接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = lendProductDataAccessService.pushLendProduct(projectIdList);
				log.info("推送产品信息存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactInvTakeBackPrincipalZ <br>
	 * description: 增量-出借人收回本息-8还本9付息-4交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月29日 下午4:19:04
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactInvTakeBackPrincipalZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvTakeBackPrincipalZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();

				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan cutd = new WloanTermUserPlan();
					cutd.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
					cutd.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
					Page<WloanTermUserPlan> cutdPage = wloanTermUserPlanService.findUserPlanListZ(page, cutd);
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					List<WloanTermUserPlan> cutdList = cutdPage.getList();
					log.info("增量-出借人收回本息-列表集合大小：{}", cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactInvTakeBackPrincipal(cutdList, currentTimeMillis);
						log.info("增量-出借人收回本息-交易流水，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactInvTakeBackPrincipalC <br>
	 * description: 存量-出借人-收回本息，8还本9付息-4交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月29日 下午4:14:20
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactInvTakeBackPrincipalC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvTakeBackPrincipalC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();

				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan cutd = new WloanTermUserPlan();
					cutd.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
					cutd.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
					Page<WloanTermUserPlan> cutdPage = wloanTermUserPlanService.findUserPlanList(page, cutd);
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					List<WloanTermUserPlan> cutdList = cutdPage.getList();
					log.info("存量-出借人收回本息-列表集合大小：{}", cutdList.size());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactInvTakeBackPrincipal(cutdList, currentTimeMillis);
						log.info("存量-出借人收回本息-交易流水，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactInvTakeBackInterestZ <br>
	 * description: 增量-出借人-9收回利息-4交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月29日 下午4:05:47
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactInvTakeBackInterestZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvTakeBackInterestZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan wtup = new WloanTermUserPlan();
					wtup.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2); // 还款类型-2-收回利息.
					wtup.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3); // 还款状态-3-成功还款.
					Page<WloanTermUserPlan> wtupPage = wloanTermUserPlanService.findUserPlanListZ(page, wtup);
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", wtupPage.getLast());
					List<WloanTermUserPlan> wtupList = wtupPage.getList();
					log.info("增量-出借人收回利息-列表集合大小：{}", wtupList.size());
					if (wtupList != null && wtupList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactInvTakeBackInterest(wtupList, currentTimeMillis);
						log.info("增量-出借人收回利息-交易流水，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > wtupPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactInvTakeBackInterestC <br>
	 * description: 存量-出借人-9收回利息-4交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月29日 下午4:05:47
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactInvTakeBackInterestC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvTakeBackInterestC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermUserPlan> page = new Page<WloanTermUserPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermUserPlan wtup = new WloanTermUserPlan();
					wtup.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2); // 还款类型-2-收回利息.
					wtup.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3); // 还款状态-3-成功还款.
					Page<WloanTermUserPlan> wtupPage = wloanTermUserPlanService.findUserPlanList(page, wtup);
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", wtupPage.getLast());
					List<WloanTermUserPlan> wtupList = wtupPage.getList();
					log.info("存量-出借人收回利息-列表集合大小：{}", wtupList.size());
					if (wtupList != null && wtupList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactInvTakeBackInterest(wtupList, currentTimeMillis);
						log.info("存量-出借人收回利息-交易流水，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > wtupPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCrePayPrincipalInfoZ <br>
	 * description: 增量-推送借款人还本付息交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:23:44
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCrePayPrincipalInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCrePayPrincipalInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermProjectPlan> page = new Page<WloanTermProjectPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermProjectPlan proPlan = new WloanTermProjectPlan();
					proPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1); // 还本付息.
					proPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2); // 成功.
					Page<WloanTermProjectPlan> proPlanPage = wloanTermProjectPlanService.findCrePayPrincipalAndInterestPageZ(page, proPlan);
					List<WloanTermProjectPlan> proPlanList = proPlanPage.getList();
					log.info("借款人还本付息交易流水增量数量：{}", proPlanList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", proPlanPage.getLast());
					if (proPlanList != null && proPlanList.size() > 0) { // 数据推送.
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCrePayPrincipalInfo(proPlanList, currentTimeMillis);
						log.info("推送借款人还本付息交易流水增量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > proPlanPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCrePayPrincipalInfoC <br>
	 * description: 存量-推送借款人还本付息交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午4:15:08
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCrePayPrincipalInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCrePayPrincipalInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermProjectPlan> page = new Page<WloanTermProjectPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermProjectPlan proPlan = new WloanTermProjectPlan();
					proPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_1); // 还本付息.
					proPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2); // 成功.
					Page<WloanTermProjectPlan> proPlanPage = wloanTermProjectPlanService.findCrePayPrincipalAndInterestPage(page, proPlan);
					List<WloanTermProjectPlan> proPlanList = proPlanPage.getList();
					log.info("借款人还本付息交易流水存量数量：{}", proPlanList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", proPlanPage.getLast());
					if (proPlanList != null && proPlanList.size() > 0) { // 数据推送.
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCrePayPrincipalInfo(proPlanList, currentTimeMillis);
						log.info("推送借款人还本付息交易流水存量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > proPlanPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCrePayInterestInfoZ <br>
	 * description: 增量-推送借款人付息交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:22:03
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCrePayInterestInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCrePayInterestInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermProjectPlan> page = new Page<WloanTermProjectPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermProjectPlan proPlan = new WloanTermProjectPlan();
					proPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0); // 付息.
					proPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2); // 成功.
					Page<WloanTermProjectPlan> proPlanPage = wloanTermProjectPlanService.findCrePayPrincipalAndInterestPageZ(page, proPlan);
					List<WloanTermProjectPlan> proPlanList = proPlanPage.getList();
					log.info("借款人付息交易流水增量数量：{}", proPlanList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", proPlanPage.getLast());
					if (proPlanList != null && proPlanList.size() > 0) { // 数据推送.
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCrePayInterestInfo(proPlanList, currentTimeMillis);
						log.info("推送借款人付息交易流水增量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > proPlanPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCrePayInterestInfoC <br>
	 * description: 存量-推送借款人付息交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午4:01:35
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCrePayInterestInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCrePayInterestInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<WloanTermProjectPlan> page = new Page<WloanTermProjectPlan>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					WloanTermProjectPlan proPlan = new WloanTermProjectPlan();
					proPlan.setPrincipal(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_PRINCIPAL_0); // 付息.
					proPlan.setState(WloanTermProjectPlanService.WLOAN_TERM_PROJECT_PLAN_STATE_2); // 成功.
					Page<WloanTermProjectPlan> proPlanPage = wloanTermProjectPlanService.findCrePayPrincipalAndInterestPage(page, proPlan);
					List<WloanTermProjectPlan> proPlanList = proPlanPage.getList();
					log.info("借款人付息交易流水存量数量：{}", proPlanList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", proPlanPage.getLast());
					if (proPlanList != null && proPlanList.size() > 0) { // 数据推送.
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCrePayInterestInfo(proPlanList, currentTimeMillis);
						log.info("推送借款人付息交易流水存量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > proPlanPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCreGrantInfoZ <br>
	 * description: 增量-推送借款人放款交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:20:59
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCreGrantInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreGrantInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_11); // 放款
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactCreUserGrantInfoPageZ(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人放款交易流水增量数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCreGrantInfo(cutdList, currentTimeMillis);
						log.info("推送借款人放款交易流水增量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCreGrantInfoC <br>
	 * description: 存量-推送借款人放款交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午3:48:27
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCreGrantInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreGrantInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_11); // 放款
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactCreUserGrantInfoPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人放款交易流水存量数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCreGrantInfo(cutdList, currentTimeMillis);
						log.info("推送借款人放款交易流水存量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCreRechargeInfoZ <br>
	 * description: 增量-推送借款人充值交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:19:57
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCreRechargeInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreRechargeInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0);
					cutd.setBeginTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00"));
					cutd.setEndTransDate(new Date()); // 实际推送时间.
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactCreUserRechargeWithdrawPageZ(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人充值交易流水增量数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCreRechargeInfo(cutdList, currentTimeMillis);
						log.info("推送借款人充值交易流水增量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCreRechargeInfoC <br>
	 * description: 存量-推送借款人充值交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午3:38:10
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCreRechargeInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreRechargeInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_0);
					cutd.setEndTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00"));
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactCreUserRechargeWithdrawPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人充值交易流水存量数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCreRechargeInfo(cutdList, currentTimeMillis);
						log.info("推送借款人充值交易流水存量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCreWithdrawInfoZ <br>
	 * description: 增量-推送借款人提现交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:18:18
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCreWithdrawInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreWithdrawInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1);
					cutd.setBeginTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00"));
					cutd.setEndTransDate(new Date()); // 实际推送时间.
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactCreUserRechargeWithdrawPageZ(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人提现交易流水增量数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCreWithdrawInfo(cutdList, currentTimeMillis);
						log.info("推送借款人提现交易流水增量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactCreWithdrawInfoC <br>
	 * description: 存量-推送借款人提现交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午3:22:13
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactCreWithdrawInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactCreWithdrawInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				boolean flag = true;
				int pageNo = 1;
				int pageSize = 3000;
				long currentTimeMillis = System.currentTimeMillis();
				while (flag) {
					Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
					page.setPageNo(pageNo);
					page.setPageSize(pageSize);
					CgbUserTransDetail cutd = new CgbUserTransDetail();
					cutd.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1);
					cutd.setEndTransDate(DateUtils.getDateOfString("2019-03-01 00:00:00")); // 存量出借人提现流水，时间节点<=2019-03-01 00:00:00.
					Page<CgbUserTransDetail> cutdPage = cgbUserTransDetailService.findTransactCreUserRechargeWithdrawPage(page, cutd);
					List<CgbUserTransDetail> cutdList = cutdPage.getList();
					log.info("借款人提现交易流水存量数量：{}", cutdList.size());
					log.info("当前页码：{}", pageNo);
					log.info("最后页码：{}", cutdPage.getLast());
					if (cutdList != null && cutdList.size() > 0) {
						currentTimeMillis = currentTimeMillis + 1;
						result = transactDataAccessService.pushTransactCreWithdrawInfo(cutdList, currentTimeMillis);
						log.info("推送借款人提现交易流水存量，响应信息：{}", JSON.toJSONString(result));
						pageNo = pageNo + 1; // next page.
						if (pageNo > cutdPage.getLast()) {
							flag = false;
						}
					} else {
						flag = false;
					}
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactInvInfoZ <br>
	 * description: 增量-推送出借人购买散标产生的初始债权交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:16:39
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactInvInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = transactDataAccessService.pushTransactInvInfo(projectIdList);
				log.info("推送初始债权交易流水增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushTransactInvInfoC <br>
	 * description: 存量-推送出借人购买散标产生的初始债权交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午3:03:33
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushTransactInvInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushTransactInvInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_4.getValue().equals(InfType)) { // 是否是交易流水接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = transactDataAccessService.pushTransactInvInfo(projectIdList);
				log.info("推送初始债权交易流水存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushCreditorInfoZ <br>
	 * description: 增量-推送初始债权. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:15:35
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushCreditorInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushCreditorInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_82.getValue().equals(InfType)) { // 是否是初始债权接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = creditorDataAccessService.pushCreditorInfo(projectIdList);
				log.info("推送初始债权增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushCreditorInfoC <br>
	 * description: 存量-推送初始债权. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午2:27:18
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushCreditorInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushCreditorInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_82.getValue().equals(InfType)) { // 是否是初始债权接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = creditorDataAccessService.pushCreditorInfo(projectIdList);
				log.info("推送初始债权存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushRepayPlanInfoZ <br>
	 * description: 增量-推送还款计划. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 下午2:12:50
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushRepayPlanInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushRepayPlanInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_81.getValue().equals(InfType)) { // 是否是还款计划接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = repayPlanDataAccessService.pushRepayPlanInfo(projectIdList);
				log.info("推送还款计划增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushRepayPlanInfoC <br>
	 * description: 存量-推送还款计划. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 下午1:47:53
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushRepayPlanInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushRepayPlanInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_81.getValue().equals(InfType)) { // 是否是还款计划接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = repayPlanDataAccessService.pushRepayPlanInfo(projectIdList);
				log.info("推送还款计划存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushScatterInvestStatusInfoZ <br>
	 * description: 增量-推送散标状态信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 上午10:35:27
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushScatterInvestStatusInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestStatusInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_6.getValue().equals(InfType)) { // 是否是散标状态接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = scatterInvestStatusDataAccessService.pushScatterInvestStatusInfo(projectIdList);
				log.info("推送散标状态信息增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushScatterInvestStatusInfoC <br>
	 * description: 存量-推送散标状态. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 上午11:41:35
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushScatterInvestStatusInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestStatusInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_6.getValue().equals(InfType)) { // 是否是散标状态接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = scatterInvestStatusDataAccessService.pushScatterInvestStatusInfo(projectIdList);
				log.info("推送散标状态信息存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushScatterInvestInfoZ <br>
	 * description: 增量-推送散标信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 上午10:34:42
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushScatterInvestInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_2.getValue().equals(InfType)) { // 是否是散标接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestListZ();
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = scatterInvestDataAccessService.pushScatterInvestInfo(projectIdList);
				log.info("推送散标信息增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushScatterInvestInfoC <br>
	 * description: 存量-推送散标信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月1日 上午11:36:10
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushScatterInvestInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_2.getValue().equals(InfType)) { // 是否是散标接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvestList();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = scatterInvestDataAccessService.pushScatterInvestInfo(projectIdList);
				log.info("推送散标信息存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushCreUserInfoZ <br>
	 * description: 增量-推送借款人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 上午10:33:24
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushCreUserInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushCreUserInfoZ(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_1.getValue().equals(InfType)) { // 是否是用户接口
				List<String> subIdList = new ArrayList<String>();
				subIdList = wloanTermProjectDao.findSubjectListZ();
				HashSet<String> set = new HashSet<String>(subIdList);
				subIdList.clear();
				subIdList.addAll(set);
				log.info("借款人数量：{}", subIdList.size());
				result = ifcertUserInfoDataAccessService.pushCreUserInfoC(subIdList);
				log.info("推送借款人用户增量，响应信息：{}", result);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushCreUserInfoC <br>
	 * description: 存量-推送借款人用户. <br>
	 * author: Roy <br>
	 * date: 2019年5月31日 下午4:49:46
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushCreUserInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushCreUserInfoC(@FormParam("InfType") String InfType) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_1.getValue().equals(InfType)) { // 是否是用户接口
				List<String> subIdList = new ArrayList<String>();
				// 围绕散标-存量借款人用户信息，时间节点：2019-03-01 00:00:00.
				subIdList = wloanTermProjectDao.findSubjectListC();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(subIdList);
				subIdList.clear();
				subIdList.addAll(set);
				log.info("借款人数量：{}", subIdList.size());
				result = ifcertUserInfoDataAccessService.pushCreUserInfoC(subIdList);
				log.info("推送借款人用户存量，响应信息：{}", result);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushInvUserInfoZ <br>
	 * description: 增量-推送出借人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年6月4日 上午10:30:54
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushInvUserInfoZ")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushInvUserInfoZ(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_1.getValue().equals(InfType)) { // 是否是用户接口
				List<String> userIdList = new ArrayList<String>();
				userIdList = wloanTermInvestDao.findIfCertUserInfoListZ();
				HashSet<String> set = new HashSet<String>(userIdList);
				userIdList.clear();
				userIdList.addAll(set);
				log.info("出借人数量：{}", userIdList.size());
				result = ifcertUserInfoDataAccessService.pushInvestUserInfo(userIdList);
				log.info("推送出借人用户增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: pushInvUserInfoC <br>
	 * description: 存量-推送出借人用户. <br>
	 * author: Roy <br>
	 * date: 2019年5月31日 下午4:40:34
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/pushInvUserInfoC")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushInvUserInfoC(@FormParam("InfType") String InfType) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_1.getValue().equals(InfType)) { // 是否是用户接口
				// 用户接口.
				List<String> userIdList = new ArrayList<String>();
				// 围绕散标-存量出借人用户信息，时间节点：2019-03-01 00:00:00.
				userIdList = wloanTermInvestDao.findUserInfoList();
				// 去重复数据.
				HashSet<String> set = new HashSet<String>(userIdList);
				userIdList.clear();
				userIdList.addAll(set);
				log.info("出借人数量：{}", userIdList.size());
				result = ifcertUserInfoDataAccessService.pushInvestUserInfo(userIdList);
				log.info("推送出借人用户存量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: fillPushUserPlanList <br>
	 * description: 采用标的id补推还本付息流水. <br>
	 * author: Roy <br>
	 * date: 2019年5月31日 下午4:40:34
	 * 
	 * @param InfType
	 * @return
	 */
	@POST
	@Path("/fillPushUserPlanList")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> fillPushUserPlanList(@FormParam("projectId") String projectId) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(projectId)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else { // 是否是用户接口
				long currentTimeMillis = System.currentTimeMillis();
				WloanTermUserPlan cutd = new WloanTermUserPlan();
				cutd.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1);
				cutd.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_3);
				cutd.setProjectId(projectId);
				List<WloanTermUserPlan> cutdList = wloanTermUserPlanService.fillPushUserPlanList(cutd);
				if (cutdList != null && cutdList.size() > 0) {
					currentTimeMillis = currentTimeMillis + 1;
					result = transactDataAccessService.pushTransactInvTakeBackPrincipal(cutdList, currentTimeMillis);
					log.info("补推还本付息流水"+JSON.toJSONString(result));
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * methods: pushScatterInvest <br>
	 * description: 推送散标信息.(根据时间区间，获取需要推送标的的散标信息) <br>
	 * author: Yangzf <br>
	 * date: 2019年11月5日 上午15:34:42
	 * 
	 * @param InfType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@POST
	@Path("/pushScatterInvest")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvest(@FormParam("InfType") String InfType, @FormParam("startTime") String startTime, @FormParam("endTime") String endTime) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(InfType) || StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (InfTypeEnum.INF_TYPE_2.getValue().equals(InfType)) { // 是否是散标接口.
				List<String> projectIdList = new ArrayList<String>();
				projectIdList = wloanTermProjectDao.findScatterInvest(startTime,endTime);
				HashSet<String> set = new HashSet<String>(projectIdList);
				projectIdList.clear();
				projectIdList.addAll(set);
				log.info("散标数量：{}", projectIdList.size());
				result = scatterInvestDataAccessService.pushScatterInvestInfo(projectIdList);
				log.info("推送散标信息增量，响应信息：{}", JSON.toJSONString(result));
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Resource
	private BatchNumCheckDao batchNumCheckDao;
	
	@POST
	@Path("/checkReconciliation")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void checkReconciliation() {
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
						log.info("code:"+code+"=====message:"+message);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
