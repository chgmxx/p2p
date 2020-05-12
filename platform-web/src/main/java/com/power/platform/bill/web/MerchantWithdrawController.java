package com.power.platform.bill.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.power.platform.bill.dao.MerchantWithdrawDao;
import com.power.platform.bill.entity.MerchantWithdraw;
import com.power.platform.bill.service.MerchantWithdrawService;
import com.power.platform.cgb.dao.CgbUserTransDetailDao;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserTransDetailService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;

/**
 * 平台商户对账，提现文件Controller
 * 
 * @author lance
 * @version 2018-03-08
 */
@Controller
@RequestMapping(value = "${adminPath}/bill/merchantWithdraw")
public class MerchantWithdrawController extends BaseController {

	/**
	 * 对账文件，字段分隔符.
	 */
	public static final String FIELD_LIMIT_CHAR = "\\|";

	/**
	 * 文件名，字段分隔符.
	 */
	public static final String FILENAME_LIMIT_CHAR = "_";

	/**
	 * 交易类型，2001：提现.
	 */
	public static final String TRADING_TYPE_2001 = "2001";

	/**
	 * 交易类型，2002：提现收费.
	 */
	public static final String TRADING_TYPE_2002 = "2002";

	/**
	 * 对账文件，字段总数.
	 */
	public static final int FIELD_ALL_COUNT = 8;

	// 记录合法条数.
	private int count;

	@Autowired
	private MerchantWithdrawService merchantWithdrawService;
	@Resource
	private MerchantWithdrawDao merchantWithdrawDao;
	@Resource
	private CgbUserTransDetailDao cgbUserTransDetailDao;

	@ModelAttribute
	public MerchantWithdraw get(@RequestParam(required = false) String id) {

		MerchantWithdraw entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = merchantWithdrawService.get(id);
		}
		if (entity == null) {
			entity = new MerchantWithdraw();
		}
		return entity;
	}

	@RequiresPermissions("bill:merchantWithdraw:view")
	@RequestMapping(value = { "list", "" })
	public String list(MerchantWithdraw merchantWithdraw, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<MerchantWithdraw> page = new Page<MerchantWithdraw>(request, response);
		page.setOrderBy("a.completion_time DESC");
		Page<MerchantWithdraw> pages = merchantWithdrawService.findPage(page, merchantWithdraw);
		model.addAttribute("page", pages);
		return "modules/bill/merchantWithdrawList";
	}

	@RequiresPermissions("bill:merchantWithdraw:view")
	@RequestMapping(value = "form")
	public String form(MerchantWithdraw merchantWithdraw, Model model) {

		model.addAttribute("merchantWithdraw", merchantWithdraw);
		return "modules/bill/merchant/merchantWithdrawForm";
	}

	@RequiresPermissions("bill:merchantWithdraw:edit")
	@RequestMapping(value = "save")
	public String save(MerchantWithdraw merchantWithdraw, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, merchantWithdraw)) {
			return form(merchantWithdraw, model);
		}
		merchantWithdrawService.save(merchantWithdraw);
		addMessage(redirectAttributes, "保存平台商户对账，提现文件成功");
		return "redirect:" + Global.getAdminPath() + "/bill/merchantWithdraw/?repage";
	}

	@RequiresPermissions("bill:merchantWithdraw:edit")
	@RequestMapping(value = "delete")
	public String delete(MerchantWithdraw merchantWithdraw, RedirectAttributes redirectAttributes) {

		merchantWithdrawService.delete(merchantWithdraw);
		addMessage(redirectAttributes, "删除平台商户对账，提现文件成功");
		return "redirect:" + Global.getAdminPath() + "/bill/merchantWithdraw/?repage";
	}

	/**
	 * 
	 * 方法: importFile <br>
	 * 描述: 文件导入. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月8日 下午1:21:33
	 * 
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("bill:merchantWithdraw:edit")
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {

		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/bill/merchantWithdraw/?repage";
		}
		try {

			// 文件名拆分，区别处理.
			String[] filenames = file.getOriginalFilename().split(FILENAME_LIMIT_CHAR);
			if (StringUtils.isBlank(file.getOriginalFilename())) {
				throw new RuntimeException("导入文档为空!");
			} else if (file.getOriginalFilename().toLowerCase().endsWith("txt")) {
				logger.info("文本文件！");
			} else {
				throw new RuntimeException("文件格式不正确!");
			}

			if (filenames.length != 3) { // 文件格式不正确.
				throw new RuntimeException("文件格式不正确!");
			}

			if (filenames[2].equals("WITHDRAW.txt")) { // 提现文件.
				logger.info("提现文件！");
			} else if (filenames[2].equals("RECHARGE.txt")) { // 充值文件.
				logger.info("充值文件！");
				throw new RuntimeException("不属于提现文件!");
			} else if (filenames[2].equals("TRANSACTION.txt")) { // 交易类对账文件.
				logger.info("交易类对账文件！");
				throw new RuntimeException("不属于提现文件!");
			} else if (filenames[2].equals("BALANCECHANGE.txt")) { // 账户余额变动文件.
				logger.info("账户余额变动文件！");
				throw new RuntimeException("不属于提现文件!");
			} else if (filenames[2].equals("CHARGING.txt")) { // 商户费用账单.
				logger.info("商户费用账单！");
				throw new RuntimeException("不属于提现文件!");
			} else if (filenames[2].equals("AUTH.txt")) { // 鉴权明细对账单.
				logger.info("鉴权明细对账单！");
				throw new RuntimeException("不属于提现文件!");
			}

			// 建立一个输入流对象reader.
			InputStreamReader reader = new InputStreamReader(file.getInputStream());
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言.
			String line = "";
			line = br.readLine();
			while (line != null) {
				// 解析每一条记录.
				parseRecord(line);
				// 一次读入一行数据.
				line = br.readLine();
			}
			logger.info("共有合法记录：(" + count + ")条.");
		} catch (Exception e) {
			// e.printStackTrace();
			addMessage(redirectAttributes, "导入用户失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/bill/merchantWithdraw/?repage";
	}

	/**
	 * 
	 * 方法: parseRecord <br>
	 * 描述: 解析文件数据. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月8日 下午1:21:08
	 * 
	 * @param line_record
	 * @throws Exception
	 */
	private void parseRecord(String line_record) throws Exception {

		MerchantWithdraw entity = new MerchantWithdraw();

		// 拆分记录
		String[] fields = line_record.split(FIELD_LIMIT_CHAR);
		logger.debug("拆分字段总记录：" + fields.length);
		if (fields.length == FIELD_ALL_COUNT) {
			entity.setId(fields[0]);
			entity.setCgbOrderId(fields[1]);
			entity.setTradingType(fields[2]);
			entity.setTradingAmount(fields[3]);
			entity.setTradingStatus(fields[4]);
			entity.setCompletionTime(DateUtils.parseDate(fields[5]));
			entity.setPayCode(fields[6]);
			entity.setPlatformUserId(fields[7]);
			entity.setRemarks("存管提现数据");
			int flag = merchantWithdrawDao.insert(entity);
			if (flag == 1) {
				count++;
				logger.info("对账提现数据导入数据库成功，记录：" + count);
			}
		}
	}

	/**
	 * 
	 * 方法: check_t_1_file <br>
	 * 描述: t-1日对账. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月8日 下午1:26:16
	 * 
	 * @param merchantWithdraw
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("bill:merchantWithdraw:edit")
	@RequestMapping(value = "check_t_1_file")
	public String check_t_1_file(MerchantWithdraw merchantWithdraw, RedirectAttributes redirectAttributes) {

		// t-1：昨天的时间区间.
		String dateBefore = DateUtils.getDateBefore();
		String beginCompletionTime = dateBefore.concat(" 00:00:00");
		Date beginCompletionDate = DateUtils.parseDate(beginCompletionTime);
		String endCompletionTime = dateBefore.concat(" 23:59:59");
		Date endCompletionDate = DateUtils.parseDate(endCompletionTime);

		logger.info("beginCompletionTime：" + beginCompletionTime + "，endCompletionTime：" + endCompletionTime);

		merchantWithdraw.setBeginCompletionTime(beginCompletionDate);
		merchantWithdraw.setEndCompletionTime(endCompletionDate);
		// 充值文件流水.
		List<MerchantWithdraw> merchantWithdraws = merchantWithdrawDao.findList(merchantWithdraw);

		// 商户出借人提现流水.
		CgbUserTransDetail cgbUserTransDetail = new CgbUserTransDetail();
		cgbUserTransDetail.setBeginTransDate(beginCompletionDate);
		cgbUserTransDetail.setEndTransDate(endCompletionDate);
		cgbUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1);
		List<CgbUserTransDetail> cgbUserTransDetails = cgbUserTransDetailDao.findList(cgbUserTransDetail);
		// 商户借款人提现流水.
		List<CgbUserTransDetail> cgbCreditUserTransDetails = cgbUserTransDetailDao.findCreditList(cgbUserTransDetail);
		for (CgbUserTransDetail cgbCreditUserTransDetail : cgbCreditUserTransDetails) {
			cgbUserTransDetails.add(cgbCreditUserTransDetail);
		}
		// 存管单号.
		List<String> merchantOrders = new ArrayList<String>();
		// 以提现文件流水为基准，核对提现流水.
		if (merchantWithdraws != null && merchantWithdraws.size() > 0) {
			for (MerchantWithdraw mrEntity : merchantWithdraws) {
				String mrId = mrEntity.getId(); // 商户订单号.
				// 单号是否存在标志位，0：不存在.
				int flag = 0;
				if (cgbUserTransDetails != null && cgbUserTransDetails.size() > 0) {
					for (CgbUserTransDetail cutdEntity : cgbUserTransDetails) {
						if (mrId.equals(cutdEntity.getTransId())) { // 判断订单号是否存在.
							// 单号是否存在标志位，1：存在.
							flag = 1;
							break; // 结束本次循环.
						}
					}
				}
				if (flag == 0) {
					if (mrEntity.getTradingType().equals(MerchantWithdrawController.TRADING_TYPE_2002)) { // 判断是否是提现收费（提现收取手续费）.
						// 不计入核对单号（由于摩根平台，没有提现收费的流水，而只是在提现表的提现记录的某个字段中体现）.
					} else {
						merchantOrders.add(mrId);
					}
				}
			}
		}

		// 商户单号.
		List<String> cgbOrders = new ArrayList<String>();
		// 以充值流水为基准，核对充值文件流水.
		if (cgbUserTransDetails != null && cgbUserTransDetails.size() > 0) {
			for (CgbUserTransDetail cutdEntity : cgbUserTransDetails) {
				String transId = cutdEntity.getTransId(); // 商户订单号.
				// 单号是否存在标志位，0：不存在.
				int flag = 0;
				if (merchantWithdraws != null && merchantWithdraws.size() > 0) {
					for (MerchantWithdraw mrEntity : merchantWithdraws) {
						if (transId.equals(mrEntity.getId())) { // 判断订单号是否存在.
							// 单号是否存在标志位，1：存在.
							flag = 1;
							break; // 结束本次循环.
						}
					}
				}
				if (flag == 0) {
					cgbOrders.add(transId);
				}
			}
		}

		addMessage(redirectAttributes, "【完成时间】：" + DateUtils.formatDate(merchantWithdraw.getBeginCompletionTime(), "yyyy-MM-dd HH:mm:ss") + " - " + DateUtils.formatDate(merchantWithdraw.getEndCompletionTime(), "yyyy-MM-dd HH:mm:ss") + "<br /> 【存管平台】差异订单号：" + merchantOrders + "<br /> 【商户平台】差异订单号：" + cgbOrders);

		return "redirect:" + adminPath + "/bill/merchantWithdraw/?repage";
	}

	/**
	 * 
	 * 方法: check_t_t_file <br>
	 * 描述: 时间区间对账. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月8日 下午1:31:15
	 * 
	 * @param merchantWithdraw
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("bill:merchantWithdraw:edit")
	@RequestMapping(value = "check_t_t_file")
	public String check_t_t_file(MerchantWithdraw merchantWithdraw, RedirectAttributes redirectAttributes) {

		if (merchantWithdraw.getBeginCompletionTime() == null) { // 开始 完成时间.
			addMessage(redirectAttributes, "警告：请选择开始时间！");
			return "redirect:" + adminPath + "/bill/merchantWithdraw/?repage";
		}

		if (merchantWithdraw.getEndCompletionTime() == null) { // 结束 完成时间.
			addMessage(redirectAttributes, "警告：请选择结束时间！");
			return "redirect:" + adminPath + "/bill/merchantWithdraw/?repage";
		}

		// 充值文件流水.
		List<MerchantWithdraw> merchantWithdraws = merchantWithdrawDao.findList(merchantWithdraw);

		// 商户出借人提现流水.
		CgbUserTransDetail cgbUserTransDetail = new CgbUserTransDetail();
		cgbUserTransDetail.setBeginTransDate(merchantWithdraw.getBeginCompletionTime());
		cgbUserTransDetail.setEndTransDate(merchantWithdraw.getEndCompletionTime());
		cgbUserTransDetail.setTrustType(CgbUserTransDetailService.TRUST_TYPE_1);
		List<CgbUserTransDetail> cgbUserTransDetails = cgbUserTransDetailDao.findList(cgbUserTransDetail);
		// 商户借款人提现流水.
		List<CgbUserTransDetail> cgbCreditUserTransDetails = cgbUserTransDetailDao.findCreditList(cgbUserTransDetail);
		for (CgbUserTransDetail cgbCreditUserTransDetail : cgbCreditUserTransDetails) {
			cgbUserTransDetails.add(cgbCreditUserTransDetail);
		}

		// 存管单号.
		List<String> merchantOrders = new ArrayList<String>();
		// 以提现文件流水为基准，核对提现流水.
		if (merchantWithdraws != null && merchantWithdraws.size() > 0) {
			for (MerchantWithdraw mrEntity : merchantWithdraws) {
				String mrId = mrEntity.getId(); // 商户订单号.
				// 单号是否存在标志位，0：不存在.
				int flag = 0;
				if (cgbUserTransDetails != null && cgbUserTransDetails.size() > 0) {
					for (CgbUserTransDetail cutdEntity : cgbUserTransDetails) {
						if (mrId.equals(cutdEntity.getTransId())) { // 判断订单号是否存在.
							// 单号是否存在标志位，1：存在.
							flag = 1;
							break; // 结束本次循环.
						}
					}
				}
				if (flag == 0) {
					if (mrEntity.getTradingType().equals(MerchantWithdrawController.TRADING_TYPE_2002)) { // 判断是否是提现收费（提现收取手续费）.
						// 不计入核对单号（由于摩根平台，没有提现收费的流水，而只是在提现表的提现记录的某个字段中体现）.
					} else {
						merchantOrders.add(mrId);
					}
				}
			}
		}

		// 商户单号.
		List<String> cgbOrders = new ArrayList<String>();
		// 以提现流水为基准，核对提现文件流水.
		if (cgbUserTransDetails != null && cgbUserTransDetails.size() > 0) {
			for (CgbUserTransDetail cutdEntity : cgbUserTransDetails) {
				String transId = cutdEntity.getTransId(); // 商户订单号.
				// 单号是否存在标志位，0：不存在.
				int flag = 0;
				if (merchantWithdraws != null && merchantWithdraws.size() > 0) {
					for (MerchantWithdraw mrEntity : merchantWithdraws) {
						if (transId.equals(mrEntity.getId())) { // 判断订单号是否存在.
							// 单号是否存在标志位，1：存在.
							flag = 1;
							break; // 结束本次循环.
						}
					}
				}
				if (flag == 0) {
					cgbOrders.add(transId);
				}
			}
		}

		addMessage(redirectAttributes, "【完成时间】：" + DateUtils.formatDate(merchantWithdraw.getBeginCompletionTime(), "yyyy-MM-dd HH:mm:ss") + " - " + DateUtils.formatDate(merchantWithdraw.getEndCompletionTime(), "yyyy-MM-dd HH:mm:ss") + "<br /> 【存管平台】差异订单号：" + merchantOrders + "<br /> 【商户平台】差异订单号：" + cgbOrders);

		return "redirect:" + adminPath + "/bill/merchantWithdraw/?repage";
	}

}