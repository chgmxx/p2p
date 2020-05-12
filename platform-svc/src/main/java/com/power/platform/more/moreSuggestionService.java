package com.power.platform.more;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tsign.ching.eSign.SignHelper;

import com.power.platform.activity.dao.ActivityContactAddressDao;
import com.power.platform.activity.entity.ActivityContactAddress;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.IdcardUtils;
import com.power.platform.common.utils.InterestUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.electronic.ElectronicSign;
import com.power.platform.credit.entity.electronic.ElectronicSignTranstail;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.more.suggestion.entity.Suggestion;
import com.power.platform.more.suggestion.service.SuggestionService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;
import com.power.platform.userinfo.service.UserInfoService;
import com.power.platform.utils.AiQinPdfContract;
import com.power.platform.utils.CreateSupplyChainPdfContract;
import com.power.platform.utils.LoanAgreementPdfUtil;
import com.power.platform.weixin.service.WeixinSendTempMsgService;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

/**
 * 用户投资建议接口
 * 
 * @author Mr.Jia
 */

@Path("/more")
@Service("moreSuggestionService")
@Produces(MediaType.APPLICATION_JSON)
public class moreSuggestionService {

	private static final Logger log = LoggerFactory.getLogger(moreSuggestionService.class);
	/**
	 * 融资类型，1：应收账款.
	 */
	private static final String FINANCING_TYPE_1 = "1";
	/**
	 * 融资类型，2：订单融资.
	 */
	private static final String FINANCING_TYPE_2 = "2";

	/**
	 * 还款每30天为一期.
	 */
	private static final Integer SPAN_30 = 30;

	@Autowired
	private SuggestionService suggestionService;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermUserPlanService wloanTermUserPlanService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Autowired
	private ElectronicSignService electronicSignService;
	@Resource
	private ElectronicSignDao electronicSignDao;
	@Resource
	private ElectronicSignTranstailDao electronicSignTranstailDao;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private WeixinSendTempMsgService weixinSendTempMsgService;
	@Autowired
	private CgbUserTransDetailDao cgbUserTransDetailDao;
	@Autowired
	private ActivityContactAddressDao activityContactAddressDao;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 
	 * 方法: auditUserBirthDay <br>
	 * 描述: 调整出借人生日 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月7日 下午3:40:43
	 * 
	 * @param from
	 * @param request
	 * @return
	 */
	@POST
	@Path("/auditUserBirthDay")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> auditUserBirthDay(@FormParam("from") String from, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();
		log.info("调整出借人生日...start...");
		try {
			boolean flag = true;
			int pageNo = 1;
			int pageSize = 3000;

			while (flag) {
				Page<UserInfo> page = new Page<UserInfo>();
				page.setPageNo(pageNo);
				page.setPageSize(pageSize);
				UserInfo userInfo = new UserInfo();
				Page<UserInfo> userInfoPage = userInfoService.findPageByBirthDay(page, userInfo);
				List<UserInfo> userInfoList = userInfoPage.getList();
				log.info("当前批次客户列表大小:{}条......", null != userInfoList ? userInfoList.size() : 0);
				log.info("当前页码：{}页", pageNo);
				log.info("最后页码：{}页", userInfoPage.getLast());
				if (userInfoList != null && userInfoList.size() > 0) {
					pageNo = pageNo + 1; // next page.
					if (pageNo > userInfoPage.getLast()) {
						flag = false;
					}
					for (UserInfo uInfo : userInfoList) {
						String certificateNo = uInfo.getCertificateNo();
						if (!StringUtils.isBlank(certificateNo)) {
							String birthday = IdcardUtils.getBirthByIdCard(certificateNo);
							uInfo.setBirthday(birthday.substring(4, birthday.length()));
							int updateFlag = userInfoDao.update(uInfo);
							log.info("客户生日更新:{}", updateFlag == 1 ? "成功" : "失败");
						}
					}
				} else {
					flag = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("调整出借人生日...end...");
		result.put("state", "0");
		result.put("message", "出借人生日调整成功");
		return result;
	}

	@POST
	@Path("/submitUserContactAddress")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> submitUserContactAddress(@FormParam("street") String street, @FormParam("postcode") String postcode, @FormParam("name") String name, @FormParam("mobilePhone") String mobilePhone, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			if (StringUtils.isBlank(street) || StringUtils.isBlank(name) || StringUtils.isBlank(mobilePhone)) {
				log.info("fn:submitUserContactAddress,缺少必要参数...");
				result.put("response_code", ResponseEnum.RESPONSE_CODE_MSG_03.getValue());
				result.put("response_message", ResponseEnum.RESPONSE_CODE_MSG_03.getText());
				return result;
			}

			// 限制参与活动用户重复提交.
			ActivityContactAddress entity = new ActivityContactAddress();
			entity.setMobilePhone(StringUtils.replaceBlanK(mobilePhone));
			List<ActivityContactAddress> list = activityContactAddressDao.findList(entity);
			if (list != null && list.size() > 0) {
				log.info("fn:submitUserContactAddress,已参与活动,切勿重复提交...");
				ActivityContactAddress aca = list.get(0);
				// 判断邮政编号是否填写.
				if (!StringUtils.isBlank(postcode)) {
					street = street.concat("-" + postcode);
				}
				aca.setStreet(StringUtils.replaceBlanK(street));
				aca.setName(StringUtils.replaceBlanK(name));
				aca.setMobilePhone(StringUtils.replaceBlanK(mobilePhone));
				aca.setUpdateDate(new Date());
				int flag = activityContactAddressDao.update(aca);
				if (flag == 1) {
					log.info("参与活动用户，更新联系方式成功！");
					result.put("response_code", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
					result.put("response_message", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
					return result;
				} else {
					log.info("参与活动用户，更新联系方式失败！");
					result.put("response_code", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					result.put("response_message", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
					return result;
				}
			}

			// 判断邮政编号是否填写.
			if (!StringUtils.isBlank(postcode)) {
				street = street.concat("-" + postcode);
			}
			ActivityContactAddress aca = new ActivityContactAddress();
			aca.setId(IdGen.uuid());
			aca.setStreet(StringUtils.replaceBlanK(street));
			aca.setName(StringUtils.replaceBlanK(name));
			aca.setMobilePhone(StringUtils.replaceBlanK(mobilePhone));
			aca.setCreateDate(new Date());
			aca.setUpdateDate(new Date());
			int flag = activityContactAddressDao.insert(aca);
			if (flag == 1) {
				log.info("参与活动用户，联系方式收集成功！");
				result.put("response_code", ResponseEnum.RESPONSE_CODE_MSG_00.getValue());
				result.put("response_message", ResponseEnum.RESPONSE_CODE_MSG_00.getText());
			} else {
				log.info("参与活动用户，联系方式收集失败！");
				result.put("response_code", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("response_message", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * methods: adjustInvUserTransDetail <br>
	 * description: 调整出借用户交易流水. <br>
	 * author: Roy <br>
	 * date: 2019年8月1日 上午10:21:08
	 * 
	 * @param from
	 * @param invUserId
	 * @param request
	 * @return
	 */
	@POST
	@Path("/adjustInvUserTransDetail")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> adjustInvUserTransDetail(@FormParam("from") String from, @FormParam("invUserId") String invUserId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(invUserId)) {
			log.info("fn:adjustInvUserTransDetail，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}

		try {
			CgbUserTransDetail cutd = new CgbUserTransDetail();
			cutd.setUserId(invUserId);
			Page<CgbUserTransDetail> page = new Page<CgbUserTransDetail>();
			page.setOrderBy(" a.trans_date ASC");
			cutd.setPage(page);
			List<CgbUserTransDetail> cutdList = cgbUserTransDetailDao.findInvTransDetailList(cutd);
			log.info("出借用户流水集合大小:{}", cutdList.size());

			Double available = 0D;
			for (int i = 0; i < cutdList.size(); i++) {
				CgbUserTransDetail c = cutdList.get(i);
				switch (c.getTrustType()) {
					case 0:
						if (CgbUserTransDetailService.TRUST_STATE_2.equals(c.getState())) { // 成功.
							log.info("充值：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							available = NumberUtils.scaleDouble(NumberUtils.add(available, c.getAmount()));
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次充值成功，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次充值成功，可用余额更新失败！", (i + 1));
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_3.equals(c.getState())) {
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次充值失败，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次充值失败，可用余额更新失败！", (i + 1));
							}
						}
						break;
					case 1:
						if (CgbUserTransDetailService.TRUST_STATE_2.equals(c.getState())) { // 成功.
							log.info("提现：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							available = NumberUtils.scaleDouble(NumberUtils.subtract(available, c.getAmount()));
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次提现成功，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次提现成功，可用余额更新失败！", (i + 1));
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_3.equals(c.getState())) {
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次提现失败，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次提现失败，可用余额更新失败！", (i + 1));
							}
						}
						break;
					case 2:
						log.info("活期出借：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 3:
						if (CgbUserTransDetailService.TRUST_STATE_2.equals(c.getState())) { // 成功.
							log.info("定期出借：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							if (CgbUserTransDetailService.OUT_TYPE_2.equals(c.getInOutType())) { // 支出.
								available = NumberUtils.scaleDouble(NumberUtils.subtract(available, c.getAmount()));
								/**
								 * 调整流水中的可用余额.
								 */
								c.setAvaliableAmount(available);
								int upFlag = cgbUserTransDetailDao.update(c);
								if (upFlag == 1) { // 更新成功
									log.info("第{}次出借成功支出，可用余额更新成功！", (i + 1));
								} else {
									log.info("第{}次出借成功支出，可用余额更新失败！", (i + 1));
								}
							} else if (CgbUserTransDetailService.IN_TYPE_1.equals(c.getInOutType())) {
								available = NumberUtils.scaleDouble(NumberUtils.add(available, c.getAmount()));
								/**
								 * 调整流水中的可用余额.
								 */
								c.setAvaliableAmount(available);
								int upFlag = cgbUserTransDetailDao.update(c);
								if (upFlag == 1) { // 更新成功
									log.info("第{}次出借成功流标收入，可用余额更新成功！", (i + 1));
								} else {
									log.info("第{}次出借成功流标收入，可用余额更新失败！", (i + 1));
								}
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_3.equals(c.getState())) {
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次出借失败，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次出借失败，可用余额更新失败！", (i + 1));
							}
						}
						break;
					case 4:
						if (CgbUserTransDetailService.TRUST_STATE_2.equals(c.getState())) {
							log.info("收回利息：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							available = NumberUtils.scaleDouble(NumberUtils.add(available, c.getAmount()));
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次收回利息成功，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次收回利息成功，可用余额更新失败！", (i + 1));
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_3.equals(c.getState())) {
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次收回利息失败，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次收回利息失败，可用余额更新失败！", (i + 1));
							}
						}
						break;
					case 5:
						if (CgbUserTransDetailService.TRUST_STATE_2.equals(c.getState())) {
							log.info("收回本金：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							available = NumberUtils.scaleDouble(NumberUtils.add(available, c.getAmount()));
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次收回本金额成功，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次收回本金成功，可用余额更新失败！", (i + 1));
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_3.equals(c.getState())) {
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次收回本金失败，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次收回本金失败，可用余额更新失败！", (i + 1));
							}
						}
						break;
					case 6:
						log.info("活期赎回：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 7:
						log.info("活动返现：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 8:
						log.info("活期收益：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 9:
						log.info("佣金：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 10:
						if (CgbUserTransDetailService.TRUST_STATE_1.equals(c.getState())) {
							log.info("抵用券：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							available = NumberUtils.scaleDouble(NumberUtils.add(available, c.getAmount()));
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次抵用券处理中，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次抵用券处理中，可用余额更新失败！", (i + 1));
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_2.equals(c.getState())) {
							log.info("抵用券：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
							available = NumberUtils.scaleDouble(NumberUtils.add(available, c.getAmount()));
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次抵用券成功，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次抵用券成功，可用余额更新失败！", (i + 1));
							}
						} else if (CgbUserTransDetailService.TRUST_STATE_3.equals(c.getState())) {
							/**
							 * 调整流水中的可用余额.
							 */
							c.setAvaliableAmount(available);
							int upFlag = cgbUserTransDetailDao.update(c);
							if (upFlag == 1) { // 更新成功
								log.info("第{}次抵用券失败，可用余额更新成功！", (i + 1));
							} else {
								log.info("第{}次抵用券失败，可用余额更新失败！", (i + 1));
							}
						}
						break;
					case 11:
						log.info("放款：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 12:
						log.info("受托支付提现：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
					case 13:
						log.info("代偿还款：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						available = NumberUtils.scaleDouble(NumberUtils.subtract(available, c.getAmount()));
						/**
						 * 调整流水中的可用余额.
						 */
						c.setAvaliableAmount(available);
						int upFlag = cgbUserTransDetailDao.update(c);
						if (upFlag == 1) { // 更新成功
							log.info("第{}次代偿后，可用余额更新成功！", (i + 1));
						} else {
							log.info("第{}次代偿后，可用余额更新失败！", (i + 1));
						}
						break;
					default:
						log.info("未知交易类型：{}", NumberUtils.scaleDoubleStr(c.getAmount()));
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.put("state", "0");
		result.put("message", "出借人用户流水调整成功！");
		return result;
	}

	/**
	 * 
	 * methods: invInterestAdjust <br>
	 * description: 出借收益调整. <br>
	 * author: Roy <br>
	 * date: 2019年7月30日 下午3:23:29
	 * 
	 * @param from
	 * @param invId
	 * @param request
	 * @return
	 */
	@POST
	@Path("/invInterestAdjust")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> invInterestAdjust(@FormParam("from") String from, @FormParam("invId") String invId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(invId)) {
			log.info("fn:investAdjust，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}

		try {
			WloanTermInvest invest = wloanTermInvestDao.get(invId);
			if (invest != null) {
				Double annualRate = 0D;
				Integer span = 0;
				if (null != invest.getWloanTermProject()) {
					annualRate = invest.getWloanTermProject().getAnnualRate();
					span = invest.getWloanTermProject().getSpan();
				}
				Double interest = InterestUtils.getInvInterest(invest.getAmount(), annualRate, span);
				invest.setInterest(interest);
				int updateFlag = wloanTermInvestDao.update(invest);
				if (updateFlag == 1) {
					log.info("出借记录更新成功！");
				} else {
					log.info("出借记录更新失败！");
				}
				result.put("state", "0");
				result.put("message", "接口请求，调整成功！");
				return result;
			} else {
				result.put("state", "0");
				result.put("message", "查无此出借记录！");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", e.getMessage());
			return result;
		}
	}

	/**
	 * 
	 * methods: projectAdjust <br>
	 * description: 项目调整. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月25日 上午11:28:16
	 * 
	 * @param from
	 * @param proId
	 * @param request
	 * @return
	 */
	@POST
	@Path("/projectAdjust")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> projectAdjust(@FormParam("from") String from, @FormParam("proId") String proId, @Context HttpServletRequest request) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (StringUtils.isBlank(from) || StringUtils.isBlank(proId)) {
			log.info("fn:projectAdjust，缺少必要参数！");
			result.put("state", "2");
			result.put("message", "缺少必要参数！");
			return result;
		}

		try {
			// 1）查询项目.
			WloanTermProject project = wloanTermProjectDao.get(proId);
			if (project != null) {
				// 2）删除项目还款计划.
				wloanTermProjectPlanDao.deleteByProjectId(project.getId());
				log.info("fn:projectAdjust，删除-项目还款计划成功！");
				// 3）重新生成项目还款计划.
				if ("SUCCESS".equals(wloanTermProjectPlanService.newInitWloanTermProjectPlan(project))) {
					log.info("fn:projectAdjust，生成-项目还款计划成功！");
				}
				// 4）删除用户还款计划.
				wloanTermUserPlanDao.deleteByProjectId(project.getId());
				// 5）查询当前项目出借成功的所有记录.
				List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(project.getId());
				log.info("fn:projectAdjust，项目编号：" + project.getSn() + "\t项目出借成功笔数：" + investList.size());
				for (int i = 0; i < investList.size(); i++) {
					WloanTermInvest invest = investList.get(i);
					/**
					 * 5.1）以用户出借详情生成用户的还款计划.
					 */
					if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_0)) { // 用户还款计划-旧.
						// String wloanTermUserPlanFlag = newInitWloanTermUserPlan(invest);
						String wloanTermUserPlanFlag = initInvUserPlan(invest); // 最新利息计算逻辑，30天为一期进行计算.
						if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
							log.info("fn:projectAdjust，生成-用户还款计划成功！");
						} else {
							log.info("fn:projectAdjust，生成-用户还款计划失败！");
						}
					} else if (project.getProjectRepayPlanType().equals(WloanTermProjectService.PROJECT_REPAY_PLAN_TYPE_1)) { // 用户还款计划-新.
						String wloanTermUserPlanFlag = wloanTermUserPlanService.newInitCgbWloanTermUserPlan(invest);
						if ("SUCCESS".equals(wloanTermUserPlanFlag)) {
							log.info("fn:projectAdjust，生成-用户还款计划成功！");
						} else {
							log.info("fn:projectAdjust，生成-用户还款计划失败！");
						}
					}
					/**
					 * 5.2）以用户出借详情生成用户的出借协议.
					 */
					UserInfo user = userInfoDao.getCgb(invest.getUserId());
					if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_2.equals(project.getProjectProductType())) { // 供应链.
						CreditUserApply creditUserApply = creditUserApplyService.get(project.getCreditUserApplyId()); // 借款申请.
						if (creditUserApply != null) {
							String financingType = creditUserApply.getFinancingType(); // 融资类型.
							if (FINANCING_TYPE_1.equals(financingType)) { // 应收账款质押.
								log.info("fn:projectAdjust，应收账款质押，用户ID：" + invest.getUserId());
								// 借款协议（应收账款质押）.
								String pdfPath = LoanAgreementPdfUtil.createLoanAgreement(user, project, invest);
								log.info("fn:projectAdjust， 借款协议（应收账款质押），生成成功！");

								// 生成电子签章
								try {
									System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
									createElectronicSign(pdfPath, user, project.getCreditUserApplyId(), project.getProjectProductType());
								} catch (Exception e) {
									e.printStackTrace();
									log.info(e.getMessage());
									invest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(invest);
								}
								invest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(invest);
							} else if (FINANCING_TYPE_2.equals(financingType)) {// 订单融资.
								log.info("fn:projectAdjust，订单融资，用户ID：" + invest.getUserId());
								String pdfPath = AiQinPdfContract.createOrderFinancingPdf(user, project, invest);
								log.info("fn:projectAdjust， 借款协议（订单融资），生成成功！");

								// 生成电子签章
								try {
									System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
									createElectronicSign(pdfPath, user, project.getCreditUserApplyId(), project.getProjectProductType());
								} catch (Exception e) {
									e.printStackTrace();
									log.info(e.getMessage());
									invest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(invest);
								}
								invest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(invest);
							} else {// 应收账款让
								log.info("fn:projectAdjust，应收账款质押，用户ID：" + invest.getUserId());
								// 借款协议（应收账款质押）.
								String pdfPath = LoanAgreementPdfUtil.createLoanAgreement(user, project, invest);
								log.info("fn:projectAdjust， 借款协议（应收账款质押），生成成功！");

								// 生成电子签章
								try {
									System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
									createElectronicSign(pdfPath, user, project.getCreditUserApplyId(), project.getProjectProductType());
								} catch (Exception e) {
									e.printStackTrace();
									log.info(e.getMessage());
									invest.setContractPdfPath(pdfPath.split("data")[1]);
									wloanTermInvestDao.update(invest);
								}
								invest.setContractPdfPath(pdfPath.split("data")[1]);
								wloanTermInvestDao.update(invest);
							}
						}
					} else if (WloanTermProjectService.PROJECT_PRODUCT_TYPE_1.equals(project.getProjectProductType())) { // 安心投.
						log.info("fn:projectAdjust，安心投四方合同，用户ID：" + invest.getUserId());
						String contractPdfPath = CreateSupplyChainPdfContract.CreateRelievedPdf(user, project, invest);
						log.info("fn:projectAdjust， 借款协议（安心投四方合同），生成成功！");
						// 生成电子签章
						try {
							System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
							createElectronicSign(contractPdfPath, user, project.getWloanSubject().getLoanApplyId(), project.getProjectProductType());
						} catch (Exception e) {
							e.printStackTrace();
							log.info(e.getMessage());
							invest.setContractPdfPath(contractPdfPath.split("data")[1]);
							wloanTermInvestDao.update(invest);
						}
						invest.setContractPdfPath(contractPdfPath.split("data")[1]);
						wloanTermInvestDao.update(invest);
					}
					log.info("fn:projectAdjust， 用户ID：" + invest.getUserId() + "，生成项目还款计划及用户还款计划和用户出借协议成功！");
				}
				// 6）用户每期还款总额 == 项目每期还款总额.
				List<WloanTermProjectPlan> proPlans = wloanTermProjectPlanDao.findProPlansByProId(project.getId());
				for (int i = 0; i < proPlans.size(); i++) {
					WloanTermProjectPlan projectPlan = proPlans.get(i);
					WloanTermUserPlan wloanTermUserPlan = new WloanTermUserPlan();
					wloanTermUserPlan.setProjectId(project.getId());
					wloanTermUserPlan.setRepaymentDate(projectPlan.getRepaymentDate());
					List<WloanTermUserPlan> userPlans = wloanTermUserPlanDao.findUserRepayPlans(wloanTermUserPlan);
					Double sumInterest = 0.00D;
					for (int j = 0; j < userPlans.size(); j++) {
						WloanTermUserPlan userPlan = userPlans.get(j);
						sumInterest = NumberUtils.add(sumInterest, userPlan.getInterest() == null ? 0.00D : userPlan.getInterest());
					}
					projectPlan.setInterest(sumInterest);
					int flag = wloanTermProjectPlanDao.update(projectPlan);
					if (flag == 1) {
						log.info("fn:projectAdjust， 项目还款计划更新成功！");
					} else {
						log.info("fn:projectAdjust， 项目还款计划更新失败！");
					}
				}
			}

			log.info("fn:projectAdjust，项目调整成功！");
			result.put("state", "0");
			result.put("message", "接口调用成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("fn:projectAdjust，系统错误！");
			result.put("state", "1");
			result.put("message", "系统错误！");
			return result;
		}

		return result;
	}

	/**
	 * 
	 * methods: initInvUserPlan <br>
	 * description: 20190726-新版利息计算逻辑. <br>
	 * author: Roy <br>
	 * date: 2019年7月30日 下午3:16:47
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public String initInvUserPlan(WloanTermInvest wloanTermInvest) {

		try {
			WloanTermProject project = wloanTermProjectDao.get(wloanTermInvest.getWloanTermProject().getId());
			if (project != null) {
				if (project.getSpan() >= SPAN_30) { // 散标期限大于等于30天.
					if (project.getSpan() % SPAN_30 == 0) { // 散标每期周期为30天.
						Integer num = project.getSpan() / SPAN_30;
						WloanTermUserPlan userPlan = null;
						for (int i = 1; i <= num; i++) {
							userPlan = new WloanTermUserPlan();
							userPlan.setId(IdGen.uuid()); // 主键ID.
							userPlan.setWloanTermProject(project); // 项目信息.
							userPlan.setUserInfo(wloanTermInvest.getUserInfo()); // 用户信息.
							// userPlan.setProjectId(project.getId()); // 散标ID.
							userPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(project.getFullDate()), i))); // 还款日.
							if (i == num) {
								// 更新标的结束日期.
								project.setEndDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(project.getFullDate()), i)));
								wloanTermProjectDao.updateProState(project); // --.
								userPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1); // 还本付息.
								userPlan.setInterest(NumberUtils.add(wloanTermInvest.getAmount(), InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), project.getAnnualRate()))); // 月利息保留两位小数.
								userPlan.setInterestTrue(NumberUtils.add(wloanTermInvest.getAmount(), InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), project.getAnnualRate())));
							} else {
								userPlan.setPrincipal(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2); // 付息.
								userPlan.setInterest(InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), project.getAnnualRate())); // 月利息保留两位小数.
								userPlan.setInterestTrue(InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), project.getAnnualRate())); // 四舍五入之前月利息.
							}
							userPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2); // 正在还款.
							// userPlan.setWloanTermInvestId(wloanTermInvest.getId()); // 出借记录ID.
							userPlan.setWloanTermInvest(wloanTermInvest); // 出借记录信息.
							int insertFlag = wloanTermUserPlanDao.insert(userPlan);
							if (insertFlag == 1) {
								log.info("出借人出借还款计划插入成功！");
							} else {
								log.warn("出借人出借还款计划插入失败！");
							}
						}
					} else {
						return "ERROR";
					}
				} else {
					return "ERROR";
				}
			} else {
				return "ERROR";
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	/**
	 * 
	 * methods: newInitWloanTermUserPlan <br>
	 * description: 生成用户还款计划. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月28日 下午3:37:55
	 * 
	 * @param wloanTermInvest
	 * @return
	 */
	public String newInitWloanTermUserPlan(WloanTermInvest wloanTermInvest) {

		try {
			WloanTermProject wloanTermProject = wloanTermProjectDao.get(wloanTermInvest.getWloanTermProject().getId());
			// 投资总利息
			Double wloanTermInerest = wloanTermInvest.getInterest();
			// 计算还款期数
			Integer sum = wloanTermProject.getSpan() / 30;
			// 计算每期还款（30天一期）利息
			Double spanMoney = InterestUtils.getMonthInterestFormat(wloanTermInvest.getAmount(), wloanTermProject.getAnnualRate());
			Double spanMoneyTrue = InterestUtils.getMonthInterest(wloanTermInvest.getAmount(), wloanTermProject.getAnnualRate());// 未四舍五入
			WloanTermUserPlan wloanTermUserPlan = null;

			// 保存每期还款计划
			for (int i = 1; i <= sum; i++) {
				wloanTermUserPlan = new WloanTermUserPlan();
				wloanTermUserPlan.setWloanTermProject(wloanTermProject);
				wloanTermUserPlan.setUserInfo(wloanTermInvest.getUserInfo());
				wloanTermUserPlan.setId(IdGen.uuid());
				if (wloanTermProject != null) {
					wloanTermUserPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(wloanTermProject.getFullDate()), i)));
				} else {
					wloanTermUserPlan.setRepaymentDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(new Date()), i)));
				}
				wloanTermUserPlan.setState(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_STATE_2);
				wloanTermUserPlan.setWloanTermInvest(wloanTermInvest);

				// 如果只有一期
				if (1 == sum) {
					wloanTermUserPlan.setInterest(wloanTermInvest.getAmount() + wloanTermInvest.getInterest());
					wloanTermUserPlan.setInterestTrue(wloanTermInvest.getAmount() + wloanTermInvest.getInterest());
					wloanTermUserPlan.setPrincipal("1");
				} else {
					// 最后一期的还款金额为 本金 +[投资总利息-每期利息*(还款期数-1)]
					if (i == sum) {
						Double lastSpanMoney = wloanTermInvest.getAmount() + (wloanTermInerest - spanMoney * (sum - 1));
						wloanTermUserPlan.setInterest(lastSpanMoney);
						wloanTermUserPlan.setInterestTrue(lastSpanMoney);
						wloanTermUserPlan.setPrincipal("1");
					} else {
						wloanTermUserPlan.setInterest(spanMoney);
						wloanTermUserPlan.setInterestTrue(spanMoneyTrue);
						wloanTermUserPlan.setPrincipal("2");
					}
				}

				wloanTermUserPlanDao.insert(wloanTermUserPlan);
			}
			return "SUCCESS";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	/**
	 * 
	 * methods: createElectronicSign <br>
	 * description: 出借协议创建电子签章. <br>
	 * author: Mr.Roy <br>
	 * date: 2019年1月28日 下午3:40:45
	 * 
	 * @param srcPdfFile
	 * @param userInfo
	 * @param creditUserApplyId
	 * @param projectType
	 */
	public void createElectronicSign(String srcPdfFile, UserInfo userInfo, String creditUserApplyId, String projectType) {

		int lastF = srcPdfFile.lastIndexOf("\\");
		if (lastF == -1) {
			lastF = srcPdfFile.lastIndexOf("//");
		}
		// 最终签署后的PDF文件路径
		String signedFolder = srcPdfFile.substring(0, lastF + 1);
		// 最终签署后PDF文件名称
		String signedFileName = srcPdfFile.substring(lastF + 1, srcPdfFile.length());
		System.out.println("----<场景演示：使用标准的模板印章签署，签署人之间用文件二进制流传递>----");
		// 初始化项目，做全局使用，只初始化一次即可
		SignHelper.initProject();
		// 创建投资客户签章账户
		String userSignId;// 客户签章id
		ElectronicSign electronicSignUser = new ElectronicSign();
		electronicSignUser.setUserId(userInfo.getId());
		List<ElectronicSign> electronicSignsList = electronicSignService.findList(electronicSignUser);
		if (electronicSignsList != null && electronicSignsList.size() > 0) {
			userSignId = electronicSignsList.get(0).getSignId();
		} else {
			userSignId = SignHelper.addPersonAccountZTMG(userInfo);
			electronicSignUser.setId(IdGen.uuid());
			electronicSignUser.setSignId(userSignId);
			electronicSignUser.setCreateDate(new Date());
			electronicSignDao.insert(electronicSignUser);
		}

		// 创建投资客户印章（甲方）
		AddSealResult userSealData = SignHelper.addPersonTemplateSeal(userSignId);

		if ("1".equals(projectType)) {// 安心投
			String loanUserId = creditUserApplyId;// 借款人id
			CreditUserInfo loanUserInfo = creditUserInfoDao.get(loanUserId);
			String loanUserSignId;// 借款人签章id
			ElectronicSign electronicSignLoanUser = new ElectronicSign();
			electronicSignLoanUser.setUserId(loanUserId);
			List<ElectronicSign> electronicSignsListLoan = electronicSignService.findList(electronicSignLoanUser);
			if (electronicSignsListLoan != null && electronicSignsListLoan.size() > 0) {
				loanUserSignId = electronicSignsListLoan.get(0).getSignId();
			} else {

				loanUserSignId = SignHelper.addPersonAccountZTMGLoan(loanUserInfo);
				electronicSignLoanUser.setId(IdGen.uuid());
				electronicSignLoanUser.setSignId(loanUserSignId);
				electronicSignLoanUser.setCreateDate(new Date());
				electronicSignDao.insert(electronicSignLoanUser);
			}
			// 创建借款客户印章（乙方）
			AddSealResult loanUserSealData = SignHelper.addPersonTemplateSeal(loanUserSignId);

			// 签署
			// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
			FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvestAXT(srcPdfFile);
			// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
			FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvestAXT(platformSignResult.getStream(), userSignId, userSealData.getSealData());
			String serviceIdUser = userPersonSignResult.getSignServiceId();
			// 借款客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（乙方）
			FileDigestSignResult loanUserPersonSignResult = SignHelper.loanUserPersonSignByStreamInvestAXT(userPersonSignResult.getStream(), loanUserSignId, loanUserSealData.getSealData());
			String serviceIdLoanUser = loanUserPersonSignResult.getSignServiceId();

			// 所有签署完成,将最终签署后的文件流保存到本地
			if (0 == loanUserPersonSignResult.getErrCode()) {
				SignHelper.saveSignedByStream(loanUserPersonSignResult.getStream(), signedFolder, signedFileName);
			}
			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
			electronicSignTranstail.setId(IdGen.uuid());
			electronicSignTranstail.setInvestUserId(userInfo.getId());
			electronicSignTranstail.setSupplyId(loanUserId);// 借款人id
			electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
			electronicSignTranstail.setSignServiceIdSupply(serviceIdLoanUser);// 借款人签署后服务id
			electronicSignTranstail.setCreateDate(new Date());
			electronicSignTranstailDao.insert(electronicSignTranstail);
			// SignHelper.userPersonSignByFileInvest(srcPdfFile, signedPdf,
			// accountId, sealData)

		} else {// 供应链
				// 查询借款申请
			CreditUserApply creditUserApply = creditUserApplyService.get(creditUserApplyId);
			if (creditUserApply != null) {
				// 查询供应商签章账户
				String supplyOrganizeAccountId;
				ElectronicSign electronicSignSupply = new ElectronicSign();
				electronicSignSupply.setUserId(creditUserApply.getCreditSupplyId());
				electronicSignSupply.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				List<ElectronicSign> electronicSignsListSupply = electronicSignService.findList(electronicSignSupply);
				if (electronicSignsListSupply.size() > 0) {
					supplyOrganizeAccountId = electronicSignsListSupply.get(0).getSignId();
				} else {
					supplyOrganizeAccountId = null;
					log.info("获取供应商签章账户失败");
				}

				WloanSubject wloanSubjectSupply = new WloanSubject();
				wloanSubjectSupply.setLoanApplyId(creditUserApply.getCreditSupplyId());
				List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubjectSupply);
				wloanSubjectSupply = wloanSubjectsList1.get(0);

				// 查询核心企业签章账户
				String creditOrganizeAccountId;
				ElectronicSign electronicSignCredit = new ElectronicSign();
				electronicSignCredit.setUserId(creditUserApply.getReplaceUserId());
				electronicSignCredit.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
				List<ElectronicSign> electronicSignsListCredit = electronicSignService.findList(electronicSignCredit);
				if (electronicSignsListCredit.size() > 0) {
					creditOrganizeAccountId = electronicSignsListCredit.get(0).getSignId();
				} else {
					creditOrganizeAccountId = null;
					log.info("获取企业签章账户失败");
				}

				WloanSubject wloanSubjectCredit = new WloanSubject();
				wloanSubjectCredit.setLoanApplyId(creditUserApply.getReplaceUserId());
				List<WloanSubject> wloanSubjectsListCredit = wloanSubjectService.findList(wloanSubjectCredit);
				wloanSubjectCredit = wloanSubjectsListCredit.get(0);

				// 创建供应商印章（乙方）
				AddSealResult userOrganizeSealDataSupply = null;
				if (supplyOrganizeAccountId != null) {
					userOrganizeSealDataSupply = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId, wloanSubjectSupply);
				} else {
					log.info("获取供应商签章账户失败，无法生成电子签章");
				}

				// 创建核心企业印章（丁方）
				AddSealResult userOrganizeSealDataCredit = null;
				if (creditOrganizeAccountId != null) {
					userOrganizeSealDataCredit = SignHelper.addOrganizeTemplateSealZTMG(creditOrganizeAccountId, wloanSubjectCredit);
					log.info("核心企业签章:" + userOrganizeSealDataCredit);
				} else {
					log.info("获取核心企业签章账户失败，无法生成电子签章");
				}

				// 签署
				// 贵公司签署，签署方式：关键字定位,以文件流的方式传递pdf文档（丙方）
				FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammInvest(srcPdfFile);
				// 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档（甲方）
				FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamInvest(platformSignResult.getStream(), userSignId, userSealData.getSealData());
				String serviceIdUser = userPersonSignResult.getSignServiceId();
				// 供应商客户签署,坐标定位,以文件流的方式传递pdf文档
				FileDigestSignResult userOrganizeSignResultSupply = SignHelper.userOrganizeSignByStreamSupplyInvest(userPersonSignResult.getStream(), supplyOrganizeAccountId, userOrganizeSealDataSupply.getSealData());
				String serviceIdSupply = userOrganizeSignResultSupply.getSignServiceId();
				// 核心企业客户签署,坐标定位,以文件流的方式传递pdf文档
				if (userOrganizeSealDataCredit != null) {
					FileDigestSignResult userOrganizeSignResultCredit = SignHelper.userOrganizeSignByStreamCreditInvest(userOrganizeSignResultSupply.getStream(), creditOrganizeAccountId, userOrganizeSealDataCredit.getSealData());
					String serviceIdCredit = userOrganizeSignResultCredit.getSignServiceId();
					// 所有签署完成,将最终签署后的文件流保存到本地
					if (0 == userOrganizeSignResultCredit.getErrCode()) {
						SignHelper.saveSignedByStream(userOrganizeSignResultCredit.getStream(), signedFolder, signedFileName);
					}
					ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
					electronicSignTranstail.setId(IdGen.uuid());
					electronicSignTranstail.setInvestUserId(userInfo.getId());
					electronicSignTranstail.setCoreId(creditUserApply.getReplaceUserId());
					electronicSignTranstail.setSupplyId(creditUserApply.getCreditSupplyId());
					electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
					electronicSignTranstail.setSignServiceIdSupply(serviceIdSupply);
					electronicSignTranstail.setSignServiceIdCore(serviceIdCredit);
					electronicSignTranstail.setCreateDate(new Date());
					electronicSignTranstailDao.insert(electronicSignTranstail);
				} else {
					log.info("核心企业签章userOrganizeSealDataCredit为空！");
				}
			} else {
				log.info("查询借款申请失败！");
			}
		}
	}

	@POST
	@Path("/saveSuggestion")
	public Map<String, Object> saveSuggestion(@FormParam("from") String from, @FormParam("remarks") String remarks, @FormParam("name") String name) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 判断必要参数是否为空
		if (StringUtils.isBlank(from) || StringUtils.isBlank(remarks) || StringUtils.isBlank(name)) {
			result.put("state", "1");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			Suggestion suggestion = new Suggestion();
			suggestion.setId(IdGen.uuid());
			suggestion.setRemarks(remarks);
			suggestion.setName(name);
			suggestion.setCreateDate(new Date());
			int flag = suggestionService.insertSuggestion(suggestion);
			if (flag > 0) {
				result.put("state", "0");
				result.put("message", "意见提交成功");
			}
		} catch (Exception e) {
			result.put("state", "2");
			result.put("message", "系统异常");
		}
		return result;
	}

}
