package com.power.platform.activity.web;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.activity.dao.AVouchersDicDao;
import com.power.platform.activity.dao.UserVouchersHistoryDao;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.activity.entity.UserVouchersHistory;
import com.power.platform.activity.pojo.Span;
import com.power.platform.activity.service.AUserAwardsHistoryService;
import com.power.platform.activity.service.AVouchersDicService;
import com.power.platform.activity.service.UserVouchersHistoryService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.security.shiro.session.SessionUtils;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.utils.excel.ImportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

@Controller
@RequestMapping(value = "${adminPath}/activity/userVouchersHistory")
public class UserVouchersHistoryController extends BaseController {

	/**
	 * 项目期限范围，1：通用.
	 */
	public static final String SPANS_1 = "1";

	@Resource(name = "transactionManager")
	private DataSourceTransactionManager transactionManager;
	@Autowired
	private UserVouchersHistoryService userVouchersHistoryService;
	@Autowired
	private AVouchersDicService aVouchersDicService;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private UserVouchersHistoryDao userVouchersHistoryDao;
	@Resource
	private AVouchersDicDao aVouchersDicDao;

	@ModelAttribute
	public UserVouchersHistory get(@RequestParam(required = false) String id) {

		UserVouchersHistory entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = userVouchersHistoryService.get(id);
		}
		if (entity == null) {
			entity = new UserVouchersHistory();
		}
		return entity;
	}

	@RequiresPermissions("activity:userVouchersHistory:view")
	@RequestMapping(value = { "list", "" })
	public String list(UserVouchersHistory userVouchersHistory, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<UserVouchersHistory> page = userVouchersHistoryService.findVouchersPage(new Page<UserVouchersHistory>(request, response), userVouchersHistory);
		List<UserVouchersHistory> list = page.getList();
		for (int i = 0; i < list.size(); i++) {
			UserVouchersHistory entity = list.get(i);
			UserInfo userInfo = entity.getUserInfo();
			if (userInfo != null) {
				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
				userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
			}
		}
		model.addAttribute("page", page);
		return "modules/activity/vouchers/userVouchersHistoryList";
	}

	/**
	 * 
	 * 方法: exportFile <br>
	 * 描述: 用户抵用券列表，支持满足查询条件的所有数据进行导出. <br>
	 * 作者: Mr.Rath <br>
	 * 时间: 2018年11月24日 下午11:17:03
	 * 
	 * @param userVouchersHistory
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("activity:userVouchersHistory:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(UserVouchersHistory userVouchersHistory, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "vouchers" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<UserVouchersHistory> list = userVouchersHistoryDao.findVouchersList(userVouchersHistory);
			// Page<UserVouchersHistory> page = userVouchersHistoryService.findVouchersPage(new Page<UserVouchersHistory>(request, response), userVouchersHistory);
			new ExportExcel("vouchers", UserVouchersHistory.class).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出客户抵用券数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/activity/userVouchersHistory/list?repage";
	}

	@RequiresPermissions("activity:userVouchersHistory:view")
	@RequestMapping(value = "rechargeForm")
	public String rechargeForm(UserVouchersHistory userVouchersHistory, Model model) {

		if (userVouchersHistory.getIsNewRecord()) { // 新增.
			// 抵用券全部字典数据.
			List<AVouchersDic> vouchersDics = aVouchersDicService.findAllList();
			for (AVouchersDic aVouchersDic : vouchersDics) {
				aVouchersDic.setAmountStr(NumberUtils.scaleDoubleStr(aVouchersDic.getAmount()));
				aVouchersDic.setLimitAmountStr(NumberUtils.scaleDoubleStr(aVouchersDic.getLimitAmount()));
			}
			userVouchersHistory.setVouchersDics(vouchersDics);
			// 项目期限集合.
			List<Span> spans = new ArrayList<Span>();
			Span span_1 = new Span(UserVouchersHistoryService.SPAN_1, "通用");
			spans.add(span_1);
			Span span_30 = new Span(UserVouchersHistoryService.SPAN_30, "30天");
			spans.add(span_30);
			Span span_60 = new Span(UserVouchersHistoryService.SPAN_60, "60天");
			spans.add(span_60);
			Span span_90 = new Span(UserVouchersHistoryService.SPAN_90, "90天");
			spans.add(span_90);
			Span span_120 = new Span(UserVouchersHistoryService.SPAN_120, "120天");
			spans.add(span_120);
			Span span_180 = new Span(UserVouchersHistoryService.SPAN_180, "180天");
			spans.add(span_180);
			Span span_360 = new Span(UserVouchersHistoryService.SPAN_360, "360天");
			spans.add(span_360);
			model.addAttribute("userVouchersHistory", userVouchersHistory);
			model.addAttribute("spans", spans);
		} else { // 更新.
			// 抵用券全部字典数据.
			List<AVouchersDic> vouchersDics = aVouchersDicService.findAllList();
			for (AVouchersDic aVouchersDic : vouchersDics) {
				aVouchersDic.setAmountStr(NumberUtils.scaleDoubleStr(aVouchersDic.getAmount()));
				aVouchersDic.setLimitAmountStr(NumberUtils.scaleDoubleStr(aVouchersDic.getLimitAmount()));
			}
			userVouchersHistory.setVouchersDics(vouchersDics);
			// 项目期限集合.
			List<Span> spans = new ArrayList<Span>();
			Span span_1 = new Span(UserVouchersHistoryService.SPAN_1, "通用");
			spans.add(span_1);
			Span span_30 = new Span(UserVouchersHistoryService.SPAN_30, "30天");
			spans.add(span_30);
			Span span_60 = new Span(UserVouchersHistoryService.SPAN_60, "60天");
			spans.add(span_60);
			Span span_90 = new Span(UserVouchersHistoryService.SPAN_90, "90天");
			spans.add(span_90);
			Span span_120 = new Span(UserVouchersHistoryService.SPAN_120, "120天");
			spans.add(span_120);
			Span span_180 = new Span(UserVouchersHistoryService.SPAN_180, "180天");
			spans.add(span_180);
			Span span_360 = new Span(UserVouchersHistoryService.SPAN_360, "360天");
			spans.add(span_360);
			List<Span> spanList = userVouchersHistory.getSpanList();
			String spanStr = userVouchersHistory.getSpans();
			List<String> asList = Arrays.asList(spanStr.split(","));
			for (String span : asList) {
				if (span.equals(UserVouchersHistoryService.SPAN_1)) {
					Span span_list_1 = new Span(UserVouchersHistoryService.SPAN_1, "通用");
					spanList.add(span_list_1);
				} else if (span.equals(UserVouchersHistoryService.SPAN_30)) {
					Span span_list_30 = new Span(UserVouchersHistoryService.SPAN_30, "30天");
					spanList.add(span_list_30);
				} else if (span.equals(UserVouchersHistoryService.SPAN_60)) {
					Span span_list_60 = new Span(UserVouchersHistoryService.SPAN_60, "60天");
					spanList.add(span_list_60);
				} else if (span.equals(UserVouchersHistoryService.SPAN_90)) {
					Span span_list_90 = new Span(UserVouchersHistoryService.SPAN_90, "90天");
					spanList.add(span_list_90);
				} else if (span.equals(UserVouchersHistoryService.SPAN_120)) {
					Span span_list_120 = new Span(UserVouchersHistoryService.SPAN_120, "120天");
					spanList.add(span_list_120);
				} else if (span.equals(UserVouchersHistoryService.SPAN_180)) {
					Span span_list_180 = new Span(UserVouchersHistoryService.SPAN_180, "180天");
					spanList.add(span_list_180);
				} else if (span.equals(UserVouchersHistoryService.SPAN_360)) {
					Span span_list_360 = new Span(UserVouchersHistoryService.SPAN_360, "360天");
					spanList.add(span_list_360);
				}
			}
			model.addAttribute("userVouchersHistory", userVouchersHistory);
			model.addAttribute("spans", spans);
		}

		return "modules/activity/vouchers/userVouchersHistoryRechargeForm";
	}

	@RequiresPermissions("activity:userVouchersHistory:view")
	@RequestMapping(value = "viewForm")
	public String viewForm(UserVouchersHistory userVouchersHistory, Model model) {

		model.addAttribute("userVouchersHistory", userVouchersHistory);
		return "modules/activity/vouchers/userVouchersHistoryViewForm";
	}

	@RequiresPermissions("activity:userVouchersHistory:edit")
	@RequestMapping(value = "save")
	public String save(UserVouchersHistory userVouchersHistory, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, userVouchersHistory)) {
			return rechargeForm(userVouchersHistory, model);
		}

		// 根据手机号码，查询客户账号信息.
		String phone = userVouchersHistory.getUserInfo().getName();
		UserInfo userInfo = userInfoDao.getUserInfoByPhone(phone);
		if (null == userInfo) {
			addMessage(redirectAttributes, "抵用券充值失败，无效的手机，查无此人");
			return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/?repage";
		}

		// 根据抵用券ID，查询抵用券字典数据信息.
		String awardId = userVouchersHistory.getAwardId();
		AVouchersDic vouchersDic = aVouchersDicService.get(awardId);

		// 项目期限范围.
		if (userVouchersHistory.getSpans().equals("通用")) {
			userVouchersHistory.setSpans(SPANS_1);
		}
		// 客户账号ID.
		userVouchersHistory.setUserId(userInfo.getId());
		// 逾期时间.
		Date overdueDate = DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays());
		userVouchersHistory.setOverdueDate(overdueDate);
		// value.
		userVouchersHistory.setValue(vouchersDic.getAmount().toString());
		// 类型.
		userVouchersHistory.setType(UserVouchersHistoryService.USER_VOUCHERS_HISTORY_TYPE_1);
		// 创建人.
		userVouchersHistory.setCreateBy(SessionUtils.getUser());
		// 修改人.
		userVouchersHistory.setUpdateBy(SessionUtils.getUser());
		// 过期天数.
		userVouchersHistory.setOverdueDays(vouchersDic.getOverdueDays());
		// 起投金额.
		userVouchersHistory.setLimitAmount(vouchersDic.getLimitAmount());
		userVouchersHistoryService.save(userVouchersHistory);
		addMessage(redirectAttributes, "抵用券充值成功");
		return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/?repage";
	}

	/**
	 * 批量充值
	 */
	@RequiresPermissions("activity:userVouchersHistory:edit")
	@RequestMapping(value = "saveall")
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String saveAll(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		// 事物隔离级别，开启新事务，这样会比较安全些.
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态.

		try {
			ImportExcel ie = new ImportExcel(file, 1, 0);
			for (int i = 1; i < ie.getLastDataRowNum(); i++) {
				Row row = ie.getRow(i);
				StringBuffer rowDateSb = new StringBuffer();
				for (int j = 0; j < ie.getLastCellNum(); j++) {
					Object val = ie.getCellValue(row, j);
					if (val instanceof String) { // String类型不作处理.
					}
					if (val instanceof Integer) { // Integer类型处理.
						val = new DecimalFormat("0").format(val);
					}
					if (val instanceof Double) { // Double类型处理.
						val = new DecimalFormat("0").format(val);
					}
					if (val instanceof Float) { // Float类型处理.
						val = new DecimalFormat("0").format(val);
					}
					rowDateSb.append(val).append("|");
				}
				String rowDateStr = rowDateSb.toString();
				List<String> asList = Arrays.asList(rowDateStr.split("\\|"));
				//
				UserVouchersHistory entity = new UserVouchersHistory();
				entity.setId(IdGen.uuid()); // 该条数据主键ID.
				for (int x = 0; x < asList.size(); x++) {
					if (x == 0) { // 手机号码.
						String mobilePhone = asList.get(x);
						UserInfo user = userInfoDao.getUserInfoByPhone(mobilePhone);
						if (user != null) { // 出借人ID.
							entity.setUserId(user.getId());
						} else {
							// 手动回滚事物
							transactionManager.rollback(status);
							addMessage(redirectAttributes, "第" + (i + 1) + "行，" + "抵用券充值失败，无效的手机，查无此人");
							return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/rechargeAllForm";
						}
					}
					if (x == 1) { // 抵用券类型，编号，唯一标识.
						String voucherId = asList.get(x);
						AVouchersDic vouchersDic = aVouchersDicService.get(voucherId);
						if (vouchersDic != null) { // 抵用券类型.
							// 抵用券面值.
							entity.setValue(NumberUtils.scaleDoubleStr(vouchersDic.getAmount()));
							// 抵用券类型ID.
							entity.setAwardId(vouchersDic.getId());
							// 逾期时间.
							Date overdueDate = DateUtils.getSpecifiedMonthAfter(new Date(), vouchersDic.getOverdueDays());
							entity.setOverdueDate(overdueDate);
							// 项目期限范围.
							entity.setSpans(vouchersDic.getSpans());
							// 备注.
							entity.setRemark(vouchersDic.getRemarks());
							// 充值原因.
							entity.setRechargeReason(vouchersDic.getRemarks());
							// 过期天数.
							entity.setOverdueDays(vouchersDic.getOverdueDays());
							// 起投金额.
							entity.setLimitAmount(vouchersDic.getLimitAmount());
						} else {
							// 手动回滚事物
							transactionManager.rollback(status);
							addMessage(redirectAttributes, "第" + (i + 1) + "行，" + "抵用券充值失败，抵用券，编号，唯一标识无效");
							return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/rechargeAllForm";
						}
					}
				}
				// 类型.
				entity.setType(UserVouchersHistoryService.USER_VOUCHERS_HISTORY_TYPE_1);
				entity.setState(AUserAwardsHistoryService.A_USER_AWARDS_HISTORY_STATE_1);
				// 创建时间.
				entity.setCreateDate(new Date());
				// 创建人.
				entity.setCreateBy(SessionUtils.getUser());
				// 修改时间.
				entity.setUpdateDate(new Date());
				// 修改人.
				entity.setUpdateBy(SessionUtils.getUser());
				int flag = userVouchersHistoryDao.insert(entity);
				if (flag == 1) {
					logger.info(this.getClass() + "-该批次抵用券批量充值-第" + i + "条数据-充值成功");
				} else {
					logger.info(this.getClass() + "-该批次抵用券批量充值-第" + i + "条数据-充值失败");
				}
			}
			// 手动回滚事物
			transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "该批次抵用券充值异常，请联系开发人员解决");
			return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/?repage";
		}

		addMessage(redirectAttributes, "该批次抵用券充值成功");
		return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/?repage";
	}

	/**
	 * 批量充值页面
	 */
	@RequiresPermissions("activity:userVouchersHistory:view")
	@RequestMapping(value = "rechargeAllForm")
	public String rechargeAllForm(UserVouchersHistory userVouchersHistory, Model model) {

		// 获取抵用券全部字典数据.
		List<AVouchersDic> vouchersDics = aVouchersDicService.findAllList();
		userVouchersHistory.setVouchersDics(vouchersDics);

		model.addAttribute("userVouchersHistory", userVouchersHistory);
		return "modules/activity/vouchers/userVouchersHistoryRechargeAllForm";
	}

	@RequiresPermissions("activity:userVouchersHistory:edit")
	@RequestMapping(value = "delete")
	public String delete(UserVouchersHistory userVouchersHistory, RedirectAttributes redirectAttributes) {

		userVouchersHistoryService.delete(userVouchersHistory);
		addMessage(redirectAttributes, "删除抵用券成功");
		return "redirect:" + Global.getAdminPath() + "/activity/userVouchersHistory/?repage";
	}

}