package com.power.platform.jbsys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.annexfile.CreditAnnexFileDao;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.pack.CreditPackDao;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.jbsys.type.FromEnum;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 
 * class: LendCompanyService <br>
 * description: 尖兵系统-贷款企业服务接口. <br>
 * author: Roy <br>
 * date: 2019年6月9日 上午11:08:44
 */
@Component
@Path("/lendCompany")
@Service("lendCompanyService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class LendCompanyService {

	private static final Logger log = LoggerFactory.getLogger(LendCompanyService.class);

	// 接口数据开关.
	private static final boolean ON_OFF = true;

	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private CreditPackDao creditPackDao;
	@Resource
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	@Resource
	private WloanSubjectService wloanSubjectService;
	@Resource
	private CreditAnnexFileDao creditAnnexFileDao;

	/**
	 * 
	 * methods: applyForLoanSearch <br>
	 * description: 融资申请，融资合同，融资资料. <br>
	 * author: Roy <br>
	 * date: 2019年6月11日 下午2:11:53
	 * 
	 * @param from
	 * @return
	 */
	@POST
	@Path("/applyForLoanSearch")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> applyForLoanSearch(@FormParam("from") String from, @FormParam("creditUserApplyId") String creditUserApplyId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		if (!ON_OFF) { // 接口数据开关，防止暴力数据获取.
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			return result;
		}
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			if (StringUtils.isBlank(from)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (FromEnum.FROM_ENUM_JB.getValue().equals(from)) { // JB-尖兵系统.
				CreditUserApply entity = new CreditUserApply();
				entity.setId(creditUserApplyId);
				List<CreditUserApply> cuaList = creditUserApplyDao.findList(entity);
				for (CreditUserApply cua : cuaList) { // 遍历融资申请.
					Map<String, Object> param = new LinkedHashMap<String, Object>();
					// 借款申请主键ID.
					param.put("creditUserApplyId", cua.getId());
					// 项目编号/申请资料名称.
					if (cua.getProjectDataInfo() != null) {
						param.put("proSn", cua.getProjectDataInfo().getName());
					} else {
						param.put("proSn", "");
					}
					CreditUserInfo middlemenCre = creditUserInfoDao.get(cua.getReplaceUserId()); // 核心企业.
					if (null != middlemenCre) {
						param.put("middlemenCompanyName", middlemenCre.getEnterpriseFullName());
					} else {
						param.put("middlemenCompanyName", "");
					}
					CreditUserInfo supplierCre = creditUserInfoDao.get(cua.getCreditSupplyId()); // 供应商.
					if (null != supplierCre) {
						param.put("supplierCompanyName", supplierCre.getEnterpriseFullName());
					} else {
						param.put("supplierCompanyName", "");
					}
					// 借款金额.
					param.put("loanAmount", cua.getAmount() == null ? "0.00" : cua.getAmount());
					// 借款期限.
					param.put("loanSpan", cua.getSpan());
					// 申请时间.
					param.put("applyDateTime", DateUtils.formatDate(cua.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					// 服务费分摊比例.
					if (!StringUtils.isBlank(cua.getShareRate())) {
						Integer middlemenProportion = Integer.valueOf(cua.getShareRate());
						StringBuffer spBuffer = new StringBuffer();
						spBuffer.append(middlemenProportion.toString()).append("%").append("/");
						spBuffer.append((100 - middlemenProportion)).append("%");
						param.put("shareProportion", spBuffer.toString());
					} else {
						param.put("shareProportion", "0%/0%");
					}
					// 申请状态，0-草稿，1-申请中，2-申请通过，3-申请驳回，4-融资中，5-还款中，6-申请结束.
					param.put("applyStatus", cua.getState());
					// 融资框架协议/融资申请书.
					if (!StringUtils.isBlank(cua.getBorrPurpose())) {
						param.put("agreementPath", "https://www.cicmorgan.com" + cua.getBorrPurpose().split("data")[1]);
					} else {
						param.put("agreementPath", "");
					}
					// 借款人网络借贷风险、禁止性行为及有关事项提示书
					if (!StringUtils.isBlank(cua.getDeclarationFilePath())) {
						param.put("declarationFilePath", "https://www.cicmorgan.com" + cua.getDeclarationFilePath().split("data")[1]);
					} else {
						param.put("declarationFilePath", "");
					}
					// 合同信息.
					CreditPack crePack = new CreditPack();
					crePack.setCreditInfoId(cua.getProjectDataId());
					List<CreditPack> crePacks = creditPackDao.findList(crePack);
					if (null != crePacks && crePacks.size() > 0) {
						CreditPack creP = crePacks.get(0);
						Map<String, Object> contractParam = new LinkedHashMap<String, Object>();
						contractParam.put("contractName", creP.getName()); // 合同名称.
						contractParam.put("contractType", creP.getType()); // 合同类型，1-贸易合同，2-联营合同，3-购销合同.
						contractParam.put("contractNo", creP.getNo()); // 合同编号.
						contractParam.put("contractMoney", NumberUtils.scaleDoubleStr(Double.valueOf(creP.getMoney()))); // 合同金额.
						contractParam.put("contractEffectiveDateTime", DateUtils.formatDate(creP.getUserdDate(), "yyyy-MM-dd HH:mm:ss")); // 合同有效期.
						contractParam.put("contractSignDateTime", DateUtils.formatDate(creP.getSignDate(), "yyyy-MM-dd HH:mm:ss")); // 合同签订日期.
						param.put("contractInfo", contractParam);
					} else {
						Map<String, Object> contractParam = new LinkedHashMap<String, Object>();
						contractParam.put("contractName", ""); // 合同名称.
						contractParam.put("contractType", ""); // 合同类型.
						contractParam.put("contractNo", ""); // 合同编号.
						contractParam.put("contractMoney", ""); // 合同金额.
						contractParam.put("contractEffectiveDateTime", ""); // 合同有效期.
						contractParam.put("contractSignDateTime", ""); // 合同签订日期.
						param.put("contractInfo", contractParam);
					}
					// 资料清单.
					CreditAnnexFile creAnnexFile = new CreditAnnexFile();
					creAnnexFile.setOtherId(cua.getProjectDataId());
					List<CreditAnnexFile> creAnnexFiles = creditAnnexFileDao.findList(creAnnexFile);
					List<Map<String, Object>> cafList = new ArrayList<Map<String, Object>>();
					for (CreditAnnexFile caf : creAnnexFiles) {
						Map<String, Object> cafParam = new LinkedHashMap<String, Object>();
						cafParam.put("type", caf.getType()); // 类型.
						if (CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7.equals(caf.getType())) { // 付款承诺书.
							// 付款承诺书线上签订20190227，判断是否是此时间之后签署.
							if (!DateUtils.compare_date(DateUtils.formatDateTime(caf.getCreateDate()), "2019-02-27 00:00:00")) {
								if (caf.getUrl().contains("data")) {
									cafParam.put("url", "https://www.cicmorgan.com" + caf.getUrl().split("data")[1]); // 地址.
								} else {
									cafParam.put("url", "https://www.cicmorgan.com" + caf.getUrl());
								}
							} else {
								if (caf.getUrl().contains("data")) {
									cafParam.put("url", "https://www.cicmorgan.com/upload/image/" + caf.getUrl().split("data")[1]);
								} else {
									cafParam.put("url", "https://www.cicmorgan.com/upload/image/" + caf.getUrl());
								}
							}
						} else {
							if (caf.getUrl().contains("data")) {
								cafParam.put("url", "https://www.cicmorgan.com/upload/image/" + caf.getUrl().split("data")[1]);
							} else {
								cafParam.put("url", "https://www.cicmorgan.com/upload/image/" + caf.getUrl());
							}
						}
						cafParam.put("createDate", DateUtils.formatDate(caf.getCreateDate(), "yyyy-MM-dd HH:mm:ss")); // 上传日期.
						cafList.add(cafParam);
					}
					param.put("creditAnnexFileList", cafList);
					list.add(param);
				}
				result.put("dataList", list);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
				log.info("respCode:{},respMsg:{}", ResponseEnum.RESPONSE_CODE_MSG_00.getValue(), ResponseEnum.RESPONSE_CODE_MSG_00.getText());
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
	 * methods: middlemenSearch <br>
	 * description: 核心企业列表. <br>
	 * author: Roy <br>
	 * date: 2019年6月9日 上午11:52:49
	 * 
	 * @param from
	 *            请求来源，JB-尖兵系统. <br>
	 * @return
	 */
	@POST
	@Path("/middlemenSearch")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> middlemenSearch(@FormParam("from") String from) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		if (!ON_OFF) { // 接口数据开关，防止暴力数据获取.
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			return result;
		}
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 总融资额度初始化.
		double totalFinancingQuotaD = 80000000.00D;
		try {
			if (StringUtils.isBlank(from)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (FromEnum.FROM_ENUM_JB.getValue().equals(from)) { // JB-尖兵系统.
				CreditUserInfo entity = new CreditUserInfo();
				entity.setCreditUserType(CreditUserInfo.CREDIT_USER_TYPE_11); // 业务类型，11-代偿户（核心企业）.
				// 分页参数.
				Page<CreditUserInfo> page = new Page<CreditUserInfo>();
				page.setOrderBy("a.register_date ASC"); // 升序，企业注册时间.
				entity.setPage(page);
				List<CreditUserInfo> findList = creditUserInfoDao.findList(entity);
				for (CreditUserInfo cre : findList) {
					Map<String, Object> param = new LinkedHashMap<String, Object>();
					// 核心企业用户ID.
					param.put("middlemenUserId", cre.getId());
					// 核心企业名称
					param.put("companyName", cre.getEnterpriseFullName());
					// 下属供应商数量
					CreditSupplierToMiddlemen cstm = new CreditSupplierToMiddlemen();
					cstm.setMiddlemenId(cre.getId()); // 核心企业.
					List<CreditSupplierToMiddlemen> cstms = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(cstm);
					if (cstms != null) {
						param.put("supplierNumber", cstms.size());
					} else {
						param.put("supplierNumber", "0");
					}
					// 总融资额度
					param.put("totalFinancingQuota", NumberUtils.scaleDoubleStr(totalFinancingQuotaD));
					// 放款后还款中的标的，即为企业的在贷余额.
					WloanTermProject pro = new WloanTermProject();
					pro.setReplaceRepayId(cre.getId());
					List<String> stateItem = new ArrayList<String>();
					stateItem.add(WloanTermProjectService.REPAYMENT);
					pro.setStateItem(stateItem);
					List<WloanTermProject> pros = wloanTermProjectDao.findList(pro);
					double currentFinancingQuotaD = 0.00D;
					for (WloanTermProject p : pros) {
						currentFinancingQuotaD = NumberUtils.add(currentFinancingQuotaD, p.getCurrentAmount());
					}
					// 当前使用额度
					param.put("currentFinancingQuota", NumberUtils.scaleDoubleStr(currentFinancingQuotaD));
					// 剩余额度
					param.put("surplusFinancingQuota", NumberUtils.scaleDoubleStr(NumberUtils.subtract(totalFinancingQuotaD, currentFinancingQuotaD)));
					// 还款中/已完成的标的，即为企业的累计融资金额.
					pro.setStateItem(stateItem);
					stateItem.add(WloanTermProjectService.FINISH);
					List<WloanTermProject> proList = wloanTermProjectDao.findList(pro);
					double addUpFinancingQuotaD = 0.00D;
					for (WloanTermProject proj : proList) {
						addUpFinancingQuotaD = NumberUtils.add(addUpFinancingQuotaD, proj.getCurrentAmount());
					}
					// 累计融资金额
					param.put("addUpFinancingQuota", NumberUtils.scaleDoubleStr(addUpFinancingQuotaD));
					// 待还本金
					param.put("surplusPrincipal", NumberUtils.scaleDoubleStr(currentFinancingQuotaD));
					// 待还利息
					param.put("surplusInterest", "0.00");
					list.add(param);
				}
				result.put("dataList", list);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
				log.info("respCode:{},respMsg:{}", ResponseEnum.RESPONSE_CODE_MSG_00.getValue(), ResponseEnum.RESPONSE_CODE_MSG_00.getText());
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
	 * methods: supplierSearch <br>
	 * description: 供应商列表. <br>
	 * author: Roy <br>
	 * date: 2019年6月9日 上午11:55:39
	 * 
	 * @param from
	 *            请求来源，JB-尖兵系统. <br>
	 * @param middlemenId
	 *            核心企业id. <br>
	 * @return
	 */
	@POST
	@Path("/supplierSearch")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> supplierSearch(@FormParam("from") String from, @FormParam("middlemenId") String middlemenId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		if (!ON_OFF) { // 接口数据开关，防止暴力数据获取.
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			return result;
		}
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 总融资额度初始化.
		double totalFinancingQuotaD = 1000000.00D;
		try {
			if (StringUtils.isBlank(from) || StringUtils.isBlank(middlemenId)) {
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			} else if (FromEnum.FROM_ENUM_JB.getValue().equals(from)) { // JB-尖兵系统.
				// 下属供应商数量
				CreditSupplierToMiddlemen cstm = new CreditSupplierToMiddlemen();
				cstm.setMiddlemenId(middlemenId); // 核心企业.
				List<CreditSupplierToMiddlemen> cstms = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(cstm);
				for (CreditSupplierToMiddlemen c : cstms) {
					Map<String, Object> param = new LinkedHashMap<String, Object>();
					// 供应商融资主体.
					WloanSubject entity = new WloanSubject();
					entity.setLoanApplyId(c.getSupplierId());
					List<WloanSubject> subjects = wloanSubjectService.findList(entity);
					WloanSubject subject = null;
					if (subjects != null && subjects.size() > 0) {
						subject = subjects.get(0);
					}
					if (subject != null) {
						// 供应商企业名称
						param.put("companyName", subject.getCompanyName());
						// 累计借款笔数，还款中&&已结束的标的.
						WloanTermProject pro = new WloanTermProject();
						pro.setSubjectId(subject.getId());
						List<String> stateItem = new ArrayList<String>();
						stateItem.add(WloanTermProjectService.REPAYMENT);
						pro.setStateItem(stateItem);
						List<WloanTermProject> currentPros = wloanTermProjectDao.findList(pro);
						// 当前借款笔数
						param.put("currentLoanNumber", currentPros.size());
						double surplusPrincipalD = 0.00D;
						for (WloanTermProject cp : currentPros) {
							surplusPrincipalD = NumberUtils.add(surplusPrincipalD, cp.getCurrentAmount());
						}
						// 待还本金
						param.put("surplusPrincipal", NumberUtils.scaleDoubleStr(surplusPrincipalD));
						// 待还利息
						param.put("surplusInterest", "0.00");
						// 总融资额度.
						param.put("totalFinancingQuota", NumberUtils.scaleDoubleStr(totalFinancingQuotaD));
						// 剩余融资额度
						param.put("surplusFinancingQuota", NumberUtils.scaleDoubleStr(NumberUtils.subtract(totalFinancingQuotaD, surplusPrincipalD)));
						// 累计借款笔数
						stateItem.add(WloanTermProjectService.FINISH);
						List<WloanTermProject> addUpPros = wloanTermProjectDao.findList(pro);
						param.put("addUpLoanNumber", addUpPros.size());
						double addUpFinancingQuotaD = 0.00D;
						for (WloanTermProject aup : addUpPros) {
							addUpFinancingQuotaD = NumberUtils.add(addUpFinancingQuotaD, aup.getCurrentAmount());
						}
						// 累计融资金额
						param.put("addUpFinancingQuota", NumberUtils.scaleDoubleStr(addUpFinancingQuotaD));
					}
					list.add(param);
				}
				result.put("dataList", list);
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
				log.info("respCode:{},respMsg:{}", ResponseEnum.RESPONSE_CODE_MSG_00.getValue(), ResponseEnum.RESPONSE_CODE_MSG_00.getText());
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

}
