package com.power.platform.lanmao.trade.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.lanmao.dao.CreditUserAuditInfoDao;
import com.power.platform.lanmao.dao.LmTransactionDao;
import com.power.platform.lanmao.entity.CreditUserAuditInfo;
import com.power.platform.lanmao.entity.LmTransaction;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.type.AuditStatusEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.lanmao.type.UserRoleEnum;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;

@Service("enterpriseInformationUpdateNotifyService")
public class EnterpriseInformationUpdateNotifyService {

	private final static Logger logger = LoggerFactory.getLogger(EnterpriseInformationUpdateNotifyService.class);

	@Autowired
	private LmTransactionDao lmTransactionDao;
	@Autowired
	private CreditUserAuditInfoDao creditUserAuditInfoDao;
	@Autowired
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private WloanSubjectDao wloanSubjectDao;

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public boolean enterpriseInformationUpdateNotify(NotifyVo input) {

		boolean flag = false;
		try {
			// 业务数据报文，JSON格式，具体见各接口定义
			JSONObject jsonObject = JSONObject.parseObject(input.getRespData());
			String requestNo = jsonObject.getString("requestNo"); // 平台用户编号
			String platformUserNo = jsonObject.getString("platformUserNo"); // 平台用户编号
			String code = jsonObject.getString("code"); // 调用状态（0为调用成功、1为失败，返回1时请看【调用失败错误码】及错误码描述
			String status = jsonObject.getString("status"); // 业务处理状态（处理失败INIT；处理成功SUCCESS），平台可根据非SUCCESS状态做相应处理，处理失败时可参考错误码及描述
			String errorCode = "";
			String errorMessage = "";
			LmTransaction lmTransaction = null;
			String reviewStatus = "";
			String reviewDescription = "";
			String qualificationModify = "";
			String enterpriseName = ""; // 变更后企业名称
			String bankLicense = ""; // 变更后开户银行许可证号
			String orgNo = ""; // 变更后组织机构代码
			String businessLicense = ""; // 变更后营业执照编号
			String taxNo = ""; // 变更后税务登记号
			String unifiedCode = ""; // 变更后统一社会信用代码（可替代三证）
			String legal = ""; // 变更后法人姓名
			String legalIdCardNo = ""; // 变更后法人身份证号
			String contact = ""; // 变更后企业联系人
			String contactPhone = ""; // 变更后联系人手机号
			long currentTimeMillis = System.currentTimeMillis();
			CreditUserAuditInfo creditUserAuditInfo = null; // 审核信息
			CreditUserInfo creditUserInfo = null;
			if ("SUCCESS".equals(status)) {
				// 主动异步通知，企业信息参数
				reviewStatus = jsonObject.getString("reviewStatus"); // 修改后审核状态:PASSED,表示审核通过，REFUSED:表示审核拒绝
				reviewDescription = jsonObject.getString("reviewDescription"); // 审核描述信息审核拒绝会返回拒绝原因
				qualificationModify = jsonObject.getString("qualificationModify"); // 企业资质编码是否发生变更，YES:变更， NO:未变更

				// --
				enterpriseName = jsonObject.getString("enterpriseName");
				bankLicense = jsonObject.getString("bankLicense");
				orgNo = jsonObject.getString("orgNo");
				businessLicense = jsonObject.getString("businessLicense");
				taxNo = jsonObject.getString("taxNo");
				unifiedCode = jsonObject.getString("unifiedCode");
				legal = jsonObject.getString("legal");
				legalIdCardNo = jsonObject.getString("legalIdCardNo");
				contact = jsonObject.getString("contact");
				contactPhone = jsonObject.getString("contactPhone");
				// --

				if (AuditStatusEnum.PASSED.getValue().equals(reviewStatus)) { // 审核通过
					/**
					 * 借款企业融资主体变更/新增
					 */
					WloanSubject entity = new WloanSubject();
					entity.setLoanApplyId(platformUserNo);
					List<WloanSubject> subjects = wloanSubjectDao.findList(entity);
					if (subjects != null) {
						if (subjects.size() > 0) {
							WloanSubject subject = subjects.get(0);
							if (subject != null) { // 融资主体已存在
								// 变更后的企业名称
								if (!StringUtils.isBlank(enterpriseName)) {
									subject.setCompanyName(enterpriseName);
								}
								// 变更后开户银行许可证号
								if (!StringUtils.isBlank(bankLicense)) {
									subject.setBankPermitCertNo(bankLicense);
								}
								// 变更后组织机构代码
								if (!StringUtils.isBlank(orgNo)) {
									subject.setOrganNo(orgNo);
								}
								// 变更后营业执照编号
								if (!StringUtils.isBlank(businessLicense)) {
									subject.setBusinessNo(businessLicense);
									subject.setBusinessLicenseType("BLC"); // 营业执照
								}
								// 变更后税务登记号
								if (!StringUtils.isBlank(taxNo)) {
									subject.setTaxCode(taxNo);
								}
								// 变更后统一社会信用代码（可替代三证）
								if (!StringUtils.isBlank(unifiedCode)) {
									subject.setBusinessNo(unifiedCode);
									subject.setBusinessLicenseType("USCC"); // 统一社会信用代码
								}
								// 变更后法人姓名
								if (!StringUtils.isBlank(legal)) {
									subject.setLoanUser(legal);
								}
								// 变更后法人身份证号
								if (!StringUtils.isBlank(legalIdCardNo)) {
									subject.setCorporationCertNo(legalIdCardNo);
								}
								// 变更后企业联系人
								if (!StringUtils.isBlank(contact)) {
									subject.setAgentPersonName(contact);
								}
								// 变更后联系人手机号
								if (!StringUtils.isBlank(contactPhone)) {
									subject.setAgentPersonPhone(contactPhone);
								}
								subject.setUpdateDate(new Date(currentTimeMillis)); // 更新时间
								int subjectUpdateFlag = wloanSubjectDao.update(subject);
								logger.info("借款企业融资主体更新:{}", subjectUpdateFlag == 1 ? "成功" : "失败");
							}
						}
					}
					// 借款人信息
					creditUserInfo = creditUserInfoDao.get(platformUserNo);
					if (null != creditUserInfo) {
						creditUserInfo.setAutoState(reviewStatus);
						// 变更后的企业名称
						if (!StringUtils.isBlank(enterpriseName)) {
							creditUserInfo.setEnterpriseFullName(enterpriseName);
						}
						creditUserInfo.setUpdateDate(new Date(currentTimeMillis));
						int updateCreditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
						logger.info("借款人用户信息变更:{}", updateCreditUserInfoFlag == 1 ? "成功" : "失败");
					}
					// 企业审核信息
					creditUserAuditInfo = creditUserAuditInfoDao.get(platformUserNo);
					if (null != creditUserAuditInfo) {
						creditUserAuditInfo.setAuditStatus(AuditStatusEnum.PASSED.getValue());
						creditUserAuditInfo.setCode(code);
						creditUserAuditInfo.setStatus(status);
						creditUserAuditInfo.setRemark(qualificationModify + "," + reviewDescription);// 资质编码变更标志+审核描述信息
						creditUserAuditInfo.setUpdateDate(new Date(currentTimeMillis));
						int updateCreditUserAuditInfoFlag = creditUserAuditInfoDao.update(creditUserAuditInfo);
						logger.info("企业信息修改，审核信息更新:{}", updateCreditUserAuditInfoFlag == 1 ? "成功" : "失败");
					} else {
						/**
						 * 开户审核信息留存
						 */
						creditUserAuditInfo = new CreditUserAuditInfo();
						creditUserAuditInfo.setId(platformUserNo);
						creditUserAuditInfo.setPlatformUserNo(platformUserNo);
						creditUserAuditInfo.setAuditStatus(reviewStatus);
						creditUserAuditInfo.setUserRole(UserRoleEnum.BORROWERS.getValue());
						creditUserAuditInfo.setBankcardNo(null);
						creditUserAuditInfo.setBankcode(null);
						creditUserAuditInfo.setRemark(qualificationModify + "," + reviewDescription);// 资质编码变更标志+审核描述信息
						creditUserAuditInfo.setCode(code);
						creditUserAuditInfo.setStatus(status);
						creditUserAuditInfo.setErrorCode(errorCode);
						creditUserAuditInfo.setErrorMessage(errorMessage);
						creditUserAuditInfo.setCreateDate(new Date(currentTimeMillis));
						creditUserAuditInfo.setUpdateDate(new Date(currentTimeMillis));
						int insertCreditUserAuditFlag = creditUserAuditInfoDao.insert(creditUserAuditInfo);
						logger.info("企业信息修改，企业审核信息新增:{}", insertCreditUserAuditFlag == 1 ? "成功" : "失败");
					}
				} else if (AuditStatusEnum.REFUSED.getValue().equals(reviewStatus)) { // 审核拒绝
					// 借款人信息
					creditUserInfo = creditUserInfoDao.get(platformUserNo);
					if (null != creditUserInfo) {
						creditUserInfo.setAutoState(reviewStatus);
						// 变更后的企业名称
						if (!StringUtils.isBlank(enterpriseName)) {
							creditUserInfo.setEnterpriseFullName(enterpriseName);
						}
						creditUserInfo.setUpdateDate(new Date(currentTimeMillis));
						int updateCreditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
						logger.info("借款人用户信息变更:{}", updateCreditUserInfoFlag == 1 ? "成功" : "失败");
					}
					// 企业审核信息
					creditUserAuditInfo = creditUserAuditInfoDao.get(platformUserNo);
					if (null != creditUserAuditInfo) {
						creditUserAuditInfo.setAuditStatus(AuditStatusEnum.REFUSED.getValue());
						creditUserAuditInfo.setCode(code);
						creditUserAuditInfo.setStatus(status);
						creditUserAuditInfo.setRemark(qualificationModify + "," + reviewDescription);// 资质编码变更标志+审核描述信息
						creditUserAuditInfo.setUpdateDate(new Date(currentTimeMillis));
						int updateCreditUserAuditInfoFlag = creditUserAuditInfoDao.update(creditUserAuditInfo);
						logger.info("企业信息修改，审核信息更新:{}", updateCreditUserAuditInfoFlag == 1 ? "成功" : "失败");
					} else {
						/**
						 * 开户审核信息留存
						 */
						creditUserAuditInfo = new CreditUserAuditInfo();
						creditUserAuditInfo.setId(platformUserNo);
						creditUserAuditInfo.setPlatformUserNo(platformUserNo);
						creditUserAuditInfo.setAuditStatus(reviewStatus);
						creditUserAuditInfo.setUserRole(UserRoleEnum.BORROWERS.getValue());
						creditUserAuditInfo.setBankcardNo(null);
						creditUserAuditInfo.setBankcode(null);
						creditUserAuditInfo.setRemark(qualificationModify + "," + reviewDescription);// 资质编码变更标志+审核描述信息
						creditUserAuditInfo.setCode(code);
						creditUserAuditInfo.setStatus(status);
						creditUserAuditInfo.setErrorCode(errorCode);
						creditUserAuditInfo.setErrorMessage(errorMessage);
						creditUserAuditInfo.setCreateDate(new Date(currentTimeMillis));
						creditUserAuditInfo.setUpdateDate(new Date(currentTimeMillis));
						int insertCreditUserAuditFlag = creditUserAuditInfoDao.insert(creditUserAuditInfo);
						logger.info("企业信息修改，企业审核信息新增:{}", insertCreditUserAuditFlag == 1 ? "成功" : "失败");
					}
				}

				// 懒猫交易留存
				lmTransaction = new LmTransaction();
				lmTransaction.setId(IdGen.uuid());
				lmTransaction.setRequestNo(requestNo);
				lmTransaction.setServiceName(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue());
				lmTransaction.setPlatformUserNo(platformUserNo);
				lmTransaction.setCreateDate(new Date(currentTimeMillis));
				lmTransaction.setUpdateDate(new Date(currentTimeMillis));
				lmTransaction.setCode(code);
				lmTransaction.setStatus(status);
				lmTransaction.setReviewStatus(reviewStatus);
				int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
				logger.info("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
				// 程序执行成功...
				flag = true;
			} else {
				errorCode = jsonObject.getString("errorCode"); // 错误码
				errorMessage = jsonObject.getString("errorMessage"); // 错误码描述
				// 懒猫交易留存
				lmTransaction = new LmTransaction();
				lmTransaction.setId(IdGen.uuid());
				lmTransaction.setRequestNo(requestNo);
				lmTransaction.setServiceName(ServiceNameEnum.ENTERPRISE_INFORMATION_UPDATE.getValue());
				lmTransaction.setPlatformUserNo(platformUserNo);
				lmTransaction.setCreateDate(new Date(currentTimeMillis));
				lmTransaction.setUpdateDate(new Date(currentTimeMillis));
				lmTransaction.setCode(code);
				lmTransaction.setStatus(status);
				lmTransaction.setErrorCode(errorCode);
				lmTransaction.setErrorMessage(errorMessage);
				int lmTransactionFlag = lmTransactionDao.insert(lmTransaction);
				logger.info("懒猫交易留存，插入:{}", lmTransactionFlag == 1 ? "成功" : "失败");
				// 程序执行成功...
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	}

}
