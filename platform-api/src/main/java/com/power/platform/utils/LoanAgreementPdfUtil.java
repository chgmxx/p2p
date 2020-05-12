package com.power.platform.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.FileUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MergeFileUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfGenerateTables;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.SpringContextHolder;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.pack.CreditPackDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.voucher.CreditVoucherDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.middlemen.CreditMiddlemenRateService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: LoanAgreementPdfUtil <br>
 * 描述: 借款协议（应收账款质押）. <br>
 * 作者: Mr.li <br>
 * 时间: 2018年11月20日 下午4:16:48
 */
public class LoanAgreementPdfUtil {

	/**
	 * 日志.
	 */
	private static final Logger log = Logger.getLogger(CreateSupplyChainPdfContract.class);

	/**
	 * PDF文件模版路径.
	 */
	private static final String TEMPLATE_FILE_PATH = Global.getConfig("pdf.template.name");

	/**
	 * PDF文件输出路径.
	 */
	private static final String OUT_PATH = Global.getConfig("pdf.out.path");

	/**
	 * PDF临时路径.
	 */
	private static final String LIN_FILE_PATH = Global.getConfig("pdf.lin.path");

	/**
	 * 核心企业：爱亲.
	 */
	public static final String AQ_PRIMARY_KEY = "5685145015583919274";

	/**
	 * 融资类型，1：应收账款转让.
	 */
	public static final String FINANCING_TYPE_1 = "1";
	/**
	 * 融资类型，2：订单融资.
	 */
	public static final String FINANCING_TYPE_2 = "2";

	// 融资主体Service.
	private static WloanSubjectService wloanSubjectService = SpringContextHolder.getBean("wloanSubjectService");
	// 项目还款计划.
	private static WloanTermProjectPlanDao wloanTermProjectPlanDao = SpringContextHolder.getBean("wloanTermProjectPlanDao");
	// 借款端用户信息.
	private static CreditUserInfoDao creditUserInfoDao = SpringContextHolder.getBean("creditUserInfoDao");
	// 借款发票清单信息.
	private static CreditVoucherDao creditVoucherDao = SpringContextHolder.getBean("creditVoucherDao");
	// 融资主体DAO.
	private static WloanSubjectDao wloanSubjectDao = SpringContextHolder.getBean("wloanSubjectDao");
	// 核心企业设置的项目期限和利率Service.
	private static CreditMiddlemenRateService creditMiddlemenRateService = SpringContextHolder.getBean("creditMiddlemenRateService");
	// 借款申请.
	private static CreditUserApplyDao creditUserApplyDao = SpringContextHolder.getBean("creditUserApplyDao");
	// 借款申请合同.
	private static CreditPackDao creditPackDao = SpringContextHolder.getBean("creditPackDao");
	// 基本信息.
	private static ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao = SpringContextHolder.getBean("ztmgLoanBasicInfoDao");
	// 输出流.
	private static OutputStream fos;

	/**
	 * 
	 * 方法: createPromiseToPayBook <br>
	 * 描述: 创建付款承诺书. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:15:49
	 * 
	 * @param creditUserApply
	 * @param creditVouchers
	 * @return
	 * @throws Exception
	 */
	public static String createPromiseToPayBook(CreditUserApply creditUserApply, List<CreditVoucher> creditVouchers) throws Exception {

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 供应商帐号信息.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		if (supplierUserInfo != null) {
			// 供应商公司名称.
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}
		// 核心企业帐号信息.
		CreditUserInfo replaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		if (replaceUserInfo != null) {
			// 核心企业公司名称.
			map.put("commitmentToPeople", replaceUserInfo.getEnterpriseFullName());
		}
		// 签署日期.
		map.put("signedDate", DateUtils.getDate(new Date(), "yyyy年MM月dd日"));
		// 融资申请合同信息.
		String contractName = ""; // 合同名称.
		String contractNumber = ""; // 合同编号.
		Double contractMoney = 0D; // 合同金额.
		CreditPack creditPack = new CreditPack();
		creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
		List<CreditPack> creditPacks = creditPackDao.findList(creditPack);
		if (creditPacks != null) {
			if (creditPacks.get(0) != null) {
				contractName = creditPacks.get(0).getName();
				contractNumber = creditPacks.get(0).getNo();
				if (creditPacks.get(0).getMoney() != null) {
					contractMoney = Double.valueOf(creditPacks.get(0).getMoney());
				}
			}
		}

		// 发票总额.
		Double voucherTotalAmount = 0D;
		for (int i = 0; i < creditVouchers.size(); i++) {
			CreditVoucher creditVoucher = creditVouchers.get(i);
			String moneyStr = creditVoucher.getMoney();
			voucherTotalAmount = voucherTotalAmount + Double.valueOf(moneyStr);
		}

		// 融资金额比例（融资总额/发票总额）.
		Double financingAmountProportion = NumberUtils.scaleDouble(contractMoney / voucherTotalAmount);

		/**
		 * 我司应付账款:
		 */
		// title.
		String title = "附表一\t我司应付账款：";
		// rowTitle.
		String[] rowTitle = new String[] { "合同名称", "合同编号", "应收账款（￥/万元）", "发票号", "发票金额（￥/万元）", "融资金额（￥/万元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();

		// (n-1) 融资总额（万元）.
		Double financingTotalAmountN_1 = 0D;
		for (int i = 0; i < creditVouchers.size(); i++) {
			CreditVoucher creditVoucher = creditVouchers.get(i);
			String[] strings = new String[rowTitle.length];
			// 合同名称.
			strings[0] = contractName;
			// 合同编号.
			strings[1] = contractNumber;
			// 应收账款（万元）.
			Double voucherMoneyDouble = Double.valueOf(creditVoucher.getMoney());
			BigDecimal voucherMoneyDoubleBd = new BigDecimal(voucherMoneyDouble / 10000);
			strings[2] = fmtMicrometer(formatToString(voucherMoneyDoubleBd));
			// 发票号.
			strings[3] = creditVoucher.getNo();
			// 发票金额（万元）.
			strings[4] = fmtMicrometer(formatToString(voucherMoneyDoubleBd));
			if (i == (creditVouchers.size() - 1)) {
				// 融资金额（万元）.
				BigDecimal financingAmountBd = new BigDecimal((NumberUtils.scaleDouble(contractMoney / 10000) - financingTotalAmountN_1));
				strings[5] = fmtMicrometer(formatToString(financingAmountBd));
			} else {
				// 融资金额（万元）.
				BigDecimal financingAmountBd = new BigDecimal(NumberUtils.scaleDouble(financingAmountProportion * voucherMoneyDouble / 10000));
				financingTotalAmountN_1 = financingTotalAmountN_1 + financingAmountBd.doubleValue();
				strings[5] = fmtMicrometer(formatToString(financingAmountBd));
			}
			dataList.add(strings);
		}

		return createPromiseToPayBookByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createPromiseToPayBookByTemplate <br>
	 * 描述: 根据模版创建付款承诺书. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:14:19
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createPromiseToPayBookByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称：\t" + newFileName);
		// 正式环境
		String linFilePath = LIN_FILE_PATH + "JieKuanXieYi_FKCNS_Temp.pdf"; // 临时文件路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "JieKuanXieYi_FKCNS.pdf"; // 模版文件路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate(); // 生成文件输出路径.
		String tablePath = OUT_PATH + "JieKuanXieYi_FKCNS_Table.pdf"; // 表格路径.
		log.info("临时文件路径：\t" + linFilePath);
		log.info("模版文件路径：\t" + templateFileNamePath);
		log.info("生成文件输出路径：\t" + newFileNamePath);
		log.info("表格路径：\t" + tablePath);
		// 新文件的输出目录.
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));
		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);
		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("commitmentToPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("signedDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 临时PDF路径与表格PDF路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 临时PDF文件与表格PDF文件进行合并.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createShouQuanHan <br>
	 * 描述: 创建授权函（供应商） <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月22日 下午4:55:36
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createShouQuanHan(CreditUserApply creditUserApply) throws Exception {

		// 供应商用户帐号.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 编号
		map.put("serialNumber", creditUserApply.getId());
		// 签署日
		map.put("signDate", DateUtils.getDate(new Date(), "yyyy年MM月dd日"));

		if (supplierUserInfo != null) {
			// 供应商企业名称
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}

		return buildShouQuanHan(map);
	}

	/**
	 * 
	 * 方法: buildShouQuanHan <br>
	 * 描述: 构建授权函（供应商） <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月22日 下午4:55:22
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private static String buildShouQuanHan(Map<String, String> map) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		String templateFileNamePath = TEMPLATE_FILE_PATH + "ShouQuanHan.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("serialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("signDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(newFileNamePath + File.separator + newFileName));
		fos.write(bos.toByteArray());
		fos.close();

		log.info("构建《授权函》完成......");

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createYingShouZhangKuanDengJiXiYi <br>
	 * 描述: 创建应收账款登记协议 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月21日 下午2:44:43
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createYingShouZhangKuanDengJiXiYi(CreditUserApply creditUserApply) throws Exception {

		// 供应商用户帐号.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 供应商融资主体信息.
		WloanSubject supplierSubjectEntity = new WloanSubject();
		supplierSubjectEntity.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> supplierSubjects = wloanSubjectDao.findList(supplierSubjectEntity);
		WloanSubject supplierSubject = null;
		if (supplierSubjects != null && supplierSubjects.size() > 0) {
			supplierSubject = supplierSubjects.get(0);
		}

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 编号
		map.put("serialNumber", creditUserApply.getId());
		// 签署日
		map.put("signDate", DateUtils.getDate(new Date(), "yyyy年MM月dd日"));

		if (supplierUserInfo != null) {
			// 供应商企业名称
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}
		if (supplierSubject != null) {
			// 供应商企业统一社会信用代码
			map.put("supplierOrganizationCode", supplierSubject.getBusinessNo());
		}

		return buildYingShouZhangKuanDengJiXiYi(map);
	}

	/**
	 * 
	 * 方法: buildYingShouZhangKuanDengJiXiYi <br>
	 * 描述: 构建应收账款登记协议 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月21日 下午2:57:19
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private static String buildYingShouZhangKuanDengJiXiYi(Map<String, String> map) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		String templateFileNamePath = TEMPLATE_FILE_PATH + "YingShouZhangKuanZhiYaDengJiXieYi.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("serialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("signDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(newFileNamePath + File.separator + newFileName));
		fos.write(bos.toByteArray());
		fos.close();

		log.info("构建《应收账款质押登记协议》完成......");

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createFinancingApplicationBook <br>
	 * 描述: 创建融资申请书（应收账款质押）. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:13:03
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createFinancingApplicationBook(CreditUserApply creditUserApply) throws Exception {

		// 采购商用户帐号.
		CreditUserInfo purchaserUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		// 乙方（采购商）核心企业融资主体信息.
		WloanSubject purchaserSubjectEntity = new WloanSubject();
		purchaserSubjectEntity.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> purchaserSubjects = wloanSubjectDao.findList(purchaserSubjectEntity);
		WloanSubject purchaserSubject = null;
		if (purchaserSubjects != null) {
			purchaserSubject = purchaserSubjects.get(0);
		}
		// 供应商用户帐号.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 供应商融资主体信息.
		WloanSubject supplierSubjectEntity = new WloanSubject();
		supplierSubjectEntity.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> supplierSubjects = wloanSubjectDao.findList(supplierSubjectEntity);
		WloanSubject supplierSubject = null;
		if (supplierSubjects != null) {
			supplierSubject = supplierSubjects.get(0);
		}

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 第号.
		map.put("serialNumber", creditUserApply.getId());

		if (purchaserUserInfo != null) { // 采购商帐号信息.
			// 采购商企业名称.
			map.put("purchaserCompanyName", purchaserUserInfo.getEnterpriseFullName());
		}
		if (purchaserSubject != null) { // 采购商融资主体信息.
			// 法定代表人.
			map.put("purchaserLegalRepresentative", purchaserSubject.getLoanUser());
		}
		if (supplierUserInfo != null) { // 供应商帐号信息.
			// 供应商企业名称.
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}
		if (supplierSubject != null) { // 供应商融资主体信息.
			// 法定代表人.
			map.put("supplierLegalRepresentative", supplierSubject.getLoanUser());
		}

		// 借款端申请列表.
		CreditUserApply entity = new CreditUserApply();
		entity.setCreditSupplyId(creditUserApply.getCreditSupplyId());
		entity.setFinancingType(CreditUserApplyService.CREDIT_FINANCING_TYPE_1);
		entity.setBeginCreateDate(DateUtils.getDateOfString("2018-09-20"));
		List<CreditUserApply> creditUserApplyList = creditUserApplyDao.findListByFinancingType(entity);
		if (creditUserApplyList != null) {
			if (creditUserApplyList.get(0) != null) {
				// 供应链融资合作框架协议ID.
				map.put("financingFrameworkAgreementId", creditUserApplyList.get(0).getId());
			}
		}

		// 借款申请发票应收账款转让总金额.
		Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
		BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
		// 申请质押的应收账款总金额.
		StringBuffer invoiceTotalAmountSb = new StringBuffer();
		map.put("supplierTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
		// 借款申请融资金额.
		String amount = creditUserApply.getAmount();
		BigDecimal amountBd = new BigDecimal(amount);
		StringBuffer amountSb = new StringBuffer();
		map.put("supplierFinancingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());
		// 转让融资期限.
		map.put("span", creditUserApply.getSpan().toString());
		// 融资利率.
		map.put("financingRate", creditUserApply.getLenderRate());

		// 融资申请的服务费率.
		creditMiddlemenRateByServiceRate(map, creditUserApply);

		// 平台融资服务费分摊比例.
		platformFinancingServiceRateProportion(map, supplierUserInfo, purchaserUserInfo, creditUserApply);

		// 签署日期.
		String pdfSignDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 年.
		map.put("year", pdfSignDate.substring(0, 4));
		// 月.
		map.put("month", pdfSignDate.substring(4, 6));
		// 日.
		map.put("day", pdfSignDate.substring(6, 8));
		/**
		 * 本次转让的应收账款清单列表如下：
		 */
		// title.
		String title = "附表一\t本次转让的应收账款清单列表如下：";
		// rowTitle.
		String[] rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
		for (CreditVoucher creditVoucher : creditVouchers) {
			String[] strings = new String[rowTitle.length];
			strings[0] = creditVoucher.getPackNo();
			strings[1] = creditVoucher.getNo();
			strings[2] = creditVoucher.getMoney();
			dataList.add(strings);
		}

		return createFinancingApplicationBookByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createFinancingApplicationBookByTemplate <br>
	 * 描述: 根据模版创建融资申请书. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:13:27
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createFinancingApplicationBookByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称：\t" + newFileName);
		// 正式环境
		String linFilePath = LIN_FILE_PATH + "JieKuanXieYi_RZSQS_Temp.pdf"; // 临时文件路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "JieKuanXieYi_RZSQS.pdf"; // 模版文件路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate(); // 生成文件输出路径.
		String tablePath = OUT_PATH + "JieKuanXieYi_RZSQS_Table.pdf"; // 表格路径.
		log.info("临时文件路径：\t" + linFilePath);
		log.info("模版文件路径：\t" + templateFileNamePath);
		log.info("生成文件输出路径：\t" + newFileNamePath);
		log.info("表格路径：\t" + tablePath);
		// 新文件的输出目录.
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));
		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);
		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("serialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("financingFrameworkAgreementId", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierFinancingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("financingRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("serviceRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 平台融资服务费分摊比例.
		fields.setFieldProperty("supplierCompanyName_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierProportion", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCompanyName_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserProportion", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 甲方（供应商）.
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierLegalRepresentative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 乙方（采购商）.
		fields.setFieldProperty("purchaserCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserLegalRepresentative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 年月日.
		fields.setFieldProperty("year", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("month", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("day", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 临时PDF路径与表格PDF路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 临时PDF文件与表格PDF文件进行合并.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: creditMiddlemenRateByServiceRate <br>
	 * 描述: 根据融资申请期限确定融资服务费率. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:17:13
	 * 
	 * @param map
	 * @param creditUserApply
	 */
	private static void creditMiddlemenRateByServiceRate(Map<String, String> map, CreditUserApply creditUserApply) {

		if (creditUserApply != null) { // 融资申请.
			// 服务费率.
			CreditMiddlemenRate creditMiddlemenRateEntity = new CreditMiddlemenRate();
			creditMiddlemenRateEntity.setCreditUserId(creditUserApply.getReplaceUserId());
			List<CreditMiddlemenRate> creditMiddlemenRates = creditMiddlemenRateService.findList(creditMiddlemenRateEntity);
			for (CreditMiddlemenRate creditMiddlemenRate : creditMiddlemenRates) {
				if (creditUserApply.getSpan() != null) {
					if (creditMiddlemenRate.getSpan() != null) {
						if (creditUserApply.getSpan().equals(creditMiddlemenRate.getSpan())) {
							// 服务费率.
							map.put("serviceRate", creditMiddlemenRate.getServiceRate());
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * 方法: createFinancingFrameworkAgreement <br>
	 * 描述: 创建融资框架协议. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:17:26
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createFinancingFrameworkAgreement(CreditUserApply creditUserApply) throws Exception {

		// 甲方（供应商）供应商帐号信息.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 甲方（供应商）供应商融资主体信息.
		WloanSubject supplierSubjectEntity = new WloanSubject();
		supplierSubjectEntity.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> supplierSubjects = wloanSubjectDao.findList(supplierSubjectEntity);
		WloanSubject supplierSubject = null;
		if (supplierSubjects != null) {
			supplierSubject = supplierSubjects.get(0);
		}
		// 甲方（供应商）供应商基本信息.
		ZtmgLoanBasicInfo supplierBasicInfoEntity = new ZtmgLoanBasicInfo();
		supplierBasicInfoEntity.setCreditUserId(creditUserApply.getCreditSupplyId());
		ZtmgLoanBasicInfo supplierBasicInfo = ztmgLoanBasicInfoDao.findByCreditUserId(supplierBasicInfoEntity);

		// 乙方（采购商）核心企业帐号信息.
		CreditUserInfo purchaserUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		// 乙方（采购商）核心企业融资主体信息.
		WloanSubject purchaserSubjectEntity = new WloanSubject();
		purchaserSubjectEntity.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> purchaserSubjects = wloanSubjectDao.findList(purchaserSubjectEntity);
		WloanSubject purchaserSubject = null;
		if (purchaserSubjects != null) {
			purchaserSubject = purchaserSubjects.get(0);
		}
		// 乙方（采购商）核心企业基本信息.
		ZtmgLoanBasicInfo purchaserBasicInfoEntity = new ZtmgLoanBasicInfo();
		purchaserBasicInfoEntity.setCreditUserId(creditUserApply.getReplaceUserId());
		ZtmgLoanBasicInfo purchaserBasicInfo = ztmgLoanBasicInfoDao.findByCreditUserId(purchaserBasicInfoEntity);

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();

		// 编号.
		map.put("serialNumber", creditUserApply.getId());
		// 协议创建日期.
		String signDate = DateUtils.getDate(new Date(), "yyyy年MM月dd日");
		// 编号.
		map.put("signDate", signDate);

		// 甲方（供应商）供应商帐号信息.
		if (supplierUserInfo != null) {
			// 甲方（供应商）公司名称.
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}
		// 甲方（供应商）供应商融资主体信息.
		if (supplierSubject != null) {
			// 甲方（供应商）法定代表人.
			map.put("supplierLegalRepresentative", supplierSubject.getLoanUser());
		}
		// 甲方（供应商）供应商融资主体信息.
		if (supplierBasicInfo != null) {
			// 甲方（供应商）地址.
			map.put("supplierAddress", supplierBasicInfo.getRegisteredAddress());
		}
		// 乙方（采购商）核心企业帐号信息.
		if (purchaserUserInfo != null) {
			// 乙方（采购商）公司名称.
			map.put("purchaserCompanyName", purchaserUserInfo.getEnterpriseFullName());
		}
		// 乙方（采购商）核心企业融资主体信息.
		if (purchaserSubject != null) {
			// 乙方（采购商）法定代表人.
			map.put("purchaserLegalRepresentative", purchaserSubject.getLoanUser());
		}
		// 乙方（采购商）核心企业基本信息.
		if (purchaserBasicInfo != null) {
			// 乙方（采购商）地址.
			map.put("purchaserAddress", purchaserBasicInfo.getRegisteredAddress());
		}

		// 申请融资金额.
		BigDecimal amountBd = new BigDecimal(creditUserApply.getAmount());
		StringBuffer amountSb = new StringBuffer();
		// 甲方（供应商）申请融资金额.
		map.put("supplierFinancingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());
		// 甲方（供应商）申请融资期限.
		map.put("span", creditUserApply.getSpan());
		// 借款申请发票应收账款转让总金额.
		Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
		if (invoiceTotalAmount != null) {
			BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
			StringBuffer invoiceTotalAmountSb = new StringBuffer();
			// 甲方（供应商）首次拟以总金额.
			map.put("supplierTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
		}

		// 甲方供应链融资的融资利率及服务费率表.
		creditMiddlemenRateSort(map, creditUserApply.getReplaceUserId());

		// 平台融资服务费比例分摊.
		platformFinancingServiceRateProportion(map, supplierUserInfo, purchaserUserInfo, creditUserApply);

		// 逾期罚息表.
		overdueDaysPenaltyInterest(map, creditUserApply);

		// 各方的联络通讯及方法.
		communicationInfo(map, supplierSubject, purchaserSubject);

		// title.
		String title = "";
		// rowTitle.
		String[] rowTitle = null;
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		if (creditUserApply.getFinancingType().equals(FINANCING_TYPE_1)) {
			// title.
			title = "附表一\t应收账款清单列表如下：";
			// rowTitle.
			rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
			List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
			// rowData.
			// 合同.
			CreditPack creditPack = creditUserApply.getCreditPack();
			for (CreditVoucher creditVoucher : creditVouchers) {
				String[] strings = new String[rowTitle.length];
				if (creditPack != null) {
					strings[0] = creditPack.getNo();
				} else {
					strings[0] = creditVoucher.getPackNo();
				}
				strings[1] = creditVoucher.getNo();
				strings[2] = creditVoucher.getMoney();
				dataList.add(strings);
			}
		} else if (creditUserApply.getFinancingType().equals(FINANCING_TYPE_2)) {
		}

		return createFinancingFrameworkAgreementByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createFinancingFrameworkAgreementByTemplate <br>
	 * 描述: 根据模版创建，融资框架协议. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:17:42
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createFinancingFrameworkAgreementByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称：\t" + newFileName);
		// 正式环境
		String linFilePath = LIN_FILE_PATH + "JieKuanXieYi_GYLRZHZKJXY_Temp.pdf"; // 临时文件路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "JieKuanXieYi_GYLRZHZKJXY.pdf"; // 模版文件路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate(); // 生成文件输出路径.
		String tablePath = OUT_PATH + "JieKuanXieYi_GYLRZHZKJXY_Table.pdf"; // 表格路径.
		log.info("临时文件路径：\t" + linFilePath);
		log.info("模版文件路径：\t" + templateFileNamePath);
		log.info("生成文件输出路径：\t" + newFileNamePath);
		log.info("表格路径：\t" + tablePath);
		// 新文件的输出目录.
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));
		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);
		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("serialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("signDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierLegalRepresentative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserLegalRepresentative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierFinancingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 平台融资服务费分摊比例.
		fields.setFieldProperty("supplierCompanyName_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierProportion", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCompanyName_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserProportion", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 逾期罚息表.
		fields.setFieldProperty("overdueDays_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penaltyInterestRate_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("overdueDays_2", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penaltyInterestRate_2", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 各方的联络及通讯方法.
		fields.setFieldProperty("supplierCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierFax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserFax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 临时PDF路径与表格PDF路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 临时PDF文件与表格PDF文件进行合并.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: communicationInfo <br>
	 * 描述: 各方同意，各方的联络通讯及方法. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:17:55
	 * 
	 * @param map
	 * @param supplierSubject
	 * @param purchaserSubject
	 */
	private static void communicationInfo(Map<String, String> map, WloanSubject supplierSubject, WloanSubject purchaserSubject) {

		if (supplierSubject != null) { // 供应商.
			// 甲方（供应商）通讯地址.
			map.put("supplierCommunicationAddress", supplierSubject.getRegistAddress());
			// 甲方（供应商）邮编.
			map.put("supplierPostCode", "");
			// 甲方（供应商）电话.
			map.put("supplierPhone", supplierSubject.getAgentPersonPhone());
			// 甲方（供应商）传真.
			map.put("supplierFax", "");
			// 甲方（供应商）联系人.
			map.put("supplierTheContact", supplierSubject.getAgentPersonName());
			// 甲方（供应商）电子邮箱.
			map.put("supplierEmail", supplierSubject.getEmail());
		}
		if (purchaserSubject != null) { // 采购商.
			// 乙方（采购商）通讯地址.
			map.put("purchaserCommunicationAddress", purchaserSubject.getRegistAddress());
			// 乙方（采购商）邮编.
			map.put("purchaserPostCode", "");
			// 乙方（采购商）电话.
			map.put("purchaserPhone", purchaserSubject.getAgentPersonPhone());
			// 乙方（采购商）传真.
			map.put("purchaserFax", "");
			// 乙方（采购商）联系人.
			map.put("purchaserTheContact", purchaserSubject.getAgentPersonName());
			// 乙方（采购商）电子邮箱.
			map.put("purchaserEmail", purchaserSubject.getEmail());
		}
	}

	/**
	 * 
	 * 方法: overdueDaysPenaltyInterest <br>
	 * 描述: 逾期罚息. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:18:09
	 * 
	 * @param map
	 * @param creditUserApply
	 */
	private static void overdueDaysPenaltyInterest(Map<String, String> map, CreditUserApply creditUserApply) {

		for (int i = 1; i <= 2; i++) { // 逾期罚息表，两行.
			if (i == 1) {
				// 逾期天数Key.
				String overdueDaysKey = "overdueDays_";
				overdueDaysKey = overdueDaysKey.concat(String.valueOf(i));
				String overdueDaysValue = "1-30日（含30日）";
				map.put(overdueDaysKey, overdueDaysValue);
				// 罚息利率Key.
				String penaltyInterestRateKey = "penaltyInterestRate_";
				penaltyInterestRateKey = penaltyInterestRateKey.concat(String.valueOf(i));
				String penaltyInterestRateValue = "0.02%/日";
				map.put(penaltyInterestRateKey, penaltyInterestRateValue);
			}
			if (i == 2) {
				// 逾期天数Key.
				String overdueDaysKey = "overdueDays_";
				overdueDaysKey = overdueDaysKey.concat(String.valueOf(i));
				String overdueDaysValue = "超过30日";
				map.put(overdueDaysKey, overdueDaysValue);
				// 罚息利率Key.
				String penaltyInterestRateKey = "penaltyInterestRate_";
				penaltyInterestRateKey = penaltyInterestRateKey.concat(String.valueOf(i));
				String penaltyInterestRateValue = "0.03%/日";
				map.put(penaltyInterestRateKey, penaltyInterestRateValue);
			}
		}
	}

	/**
	 * 
	 * 方法: platformFinancingServiceRateProportion <br>
	 * 描述: 平台融资服务费分摊比例. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:18:21
	 * 
	 * @param map
	 * @param supplierUserInfo
	 * @param purchaserUserInfo
	 * @param creditUserApply
	 */
	private static void platformFinancingServiceRateProportion(Map<String, String> map, CreditUserInfo supplierUserInfo, CreditUserInfo purchaserUserInfo, CreditUserApply creditUserApply) {

		if (supplierUserInfo != null) { // 供应商.
			// 供应商企业名称.
			map.put("supplierCompanyName_1", supplierUserInfo.getEnterpriseFullName());
		}
		if (purchaserUserInfo != null) { // 采购商.
			// 采购商企业名称.
			map.put("purchaserCompanyName_1", purchaserUserInfo.getEnterpriseFullName());
			if (AQ_PRIMARY_KEY.equals(purchaserUserInfo.getId())) {
				if (creditUserApply != null) {
					// 采购商承担比例.
					String purchaserProportionStr = creditUserApply.getShareRate();
					StringBuffer purchaserProportionSb = new StringBuffer();
					map.put("purchaserProportion", purchaserProportionSb.append(purchaserProportionStr).append("%").toString());
					// 平台融资费用 ＝ 服务费 ＋ 利息 ＋ 登记服务费
					// 服务费＝ （融资金额*服务费率／365）*融资期限
					// 利息＝（融资金额*融资利率／365）*融资期限
					// 登记服务费为应收账款转让登记费用（30元或60元人民币每笔，具体以中国人民银行征信中心动产融资统一登记系统的收费标准为准）
					String apply_amount = creditUserApply.getAmount(); // 申请金额.
					String apply_span = creditUserApply.getSpan(); // 申请期限.
					String apply_lender_rate = creditUserApply.getLenderRate(); // 年化利率.
					// 服务费.
					Double service_fee_d = 0D;
					// 甲方供应链融资的融资利率及服务费率表.
					CreditMiddlemenRate creditMiddlemenRateEntity = new CreditMiddlemenRate();
					creditMiddlemenRateEntity.setCreditUserId(creditUserApply.getReplaceUserId());
					List<CreditMiddlemenRate> creditMiddlemenRates = creditMiddlemenRateService.findList(creditMiddlemenRateEntity);
					for (CreditMiddlemenRate creditMiddlemenRate : creditMiddlemenRates) {
						if (creditMiddlemenRate.getSpan() != null) {
							if (creditMiddlemenRate.getSpan().equals(apply_span)) { // 获取融资期限所对应的服务费率.
								String serviceRate = creditMiddlemenRate.getServiceRate();
								service_fee_d = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(serviceRate) / 36500) * Double.parseDouble(apply_span));
							}
						}
					}
					// 利息.
					Double interest_double = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(apply_lender_rate) / 36500) * Double.parseDouble(apply_span));
					// 登记服务费.
					Double registration_fee_d = 0D;
					int apply_span_int = Integer.valueOf(apply_span);
					if (apply_span_int > 180) { // 大于180天，60元.
						registration_fee_d = 60D;
					} else {
						registration_fee_d = 30D;
					}
					// 平台融资费用.
					Double servic_money_double = NumberUtils.scaleDouble(service_fee_d + interest_double + registration_fee_d);
					// 采购商承担费用.
					StringBuffer party_a_money_sb = new StringBuffer();
					Double party_a_money_double = NumberUtils.scaleDouble(servic_money_double * (Double.parseDouble(purchaserProportionStr) / 100));
					map.put("purchaserAmount", party_a_money_sb.append(String.valueOf(party_a_money_double)).toString());
					// 供应商承担比例.
					StringBuffer party_b_annual_sb = new StringBuffer();
					int party_b_annual_int = 100 - Integer.parseInt(purchaserProportionStr);
					map.put("supplierProportion", party_b_annual_sb.append(String.valueOf(party_b_annual_int)).append("%").toString());
					// 供应商承担费用.
					StringBuffer party_b_money_sb = new StringBuffer();
					Double party_b_annual_double = 100D - Double.parseDouble(purchaserProportionStr);
					Double party_b_money_double = NumberUtils.scaleDouble(servic_money_double * (party_b_annual_double / 100));
					map.put("supplierAmount", party_b_money_sb.append(String.valueOf(party_b_money_double)).toString());
				}
			} else { // 非爱亲的核心企业，服务费率的分摊比例全归该核心企业所承担.
				// 平台融资费用 ＝ 服务费 ＋ 利息 ＋ 登记服务费
				// 服务费＝ （融资金额*服务费率／365）*融资期限
				// 利息＝（融资金额*融资利率／365）*融资期限
				// 登记服务费为应收账款转让登记费用（30元或60元人民币每笔，具体以中国人民银行征信中心动产融资统一登记系统的收费标准为准）
				String apply_amount = creditUserApply.getAmount(); // 申请金额.
				String apply_span = creditUserApply.getSpan(); // 申请期限.
				String apply_lender_rate = creditUserApply.getLenderRate(); // 年化利率.
				// 服务费.
				Double service_fee_d = 0D;
				// 甲方供应链融资的融资利率及服务费率表.
				CreditMiddlemenRate creditMiddlemenRateEntity = new CreditMiddlemenRate();
				creditMiddlemenRateEntity.setCreditUserId(creditUserApply.getReplaceUserId());
				List<CreditMiddlemenRate> creditMiddlemenRates = creditMiddlemenRateService.findList(creditMiddlemenRateEntity);
				for (CreditMiddlemenRate creditMiddlemenRate : creditMiddlemenRates) {
					if (creditMiddlemenRate.getSpan() != null) {
						if (creditMiddlemenRate.getSpan().equals(apply_span)) { // 获取融资期限所对应的服务费率.
							String serviceRate = creditMiddlemenRate.getServiceRate();
							service_fee_d = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(serviceRate) / 36500) * Double.parseDouble(apply_span));
						}
					}
				}
				// 利息.
				Double interest_double = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(apply_lender_rate) / 36500) * Double.parseDouble(apply_span));
				// 登记服务费.
				Double registration_fee_d = 0D;
				int apply_span_int = Integer.valueOf(apply_span);
				if (apply_span_int > 180) { // 大于180天，60元.
					registration_fee_d = 60D;
				} else {
					registration_fee_d = 30D;
				}
				// 平台融资费用.
				Double servic_money_double = NumberUtils.scaleDouble(service_fee_d + interest_double + registration_fee_d);
				// 采购商承担费用.
				StringBuffer party_a_money_sb = new StringBuffer();
				Double party_a_money_double = NumberUtils.scaleDouble(servic_money_double * (100 / 100));
				map.put("purchaserAmount", party_a_money_sb.append(String.valueOf(party_a_money_double)).toString());
				// 采购商承担比例.
				map.put("purchaserProportion", "100%");
				// 供应商承担比例.
				map.put("supplierProportion", "0%");
				// 供应商承担费用.
				map.put("supplierAmount", "0.00");
			}
		}
	}

	/**
	 * 
	 * 方法: creditMiddlemenRateSort <br>
	 * 描述: 甲方（供应商）融资期限所对应的融资利率和融资服务费. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:18:35
	 * 
	 * @param map
	 * @param replaceUserId
	 */
	private static void creditMiddlemenRateSort(Map<String, String> map, String replaceUserId) {

		CreditMiddlemenRate creditMiddlemenRateEntity = new CreditMiddlemenRate();
		creditMiddlemenRateEntity.setCreditUserId(replaceUserId);
		List<CreditMiddlemenRate> creditMiddlemenRates = creditMiddlemenRateService.findList(creditMiddlemenRateEntity);
		List<String> spans = new ArrayList<String>();
		for (CreditMiddlemenRate creditMiddlemenRate : creditMiddlemenRates) {
			spans.add(creditMiddlemenRate.getSpan());
		}
		Collections.sort(spans, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {

				if (o1 == null || o2 == null) {
					return -1;
				}
				if (o1.length() > o2.length()) {
					return 1;
				}
				if (o1.length() < o2.length()) {
					return -1;
				}
				if (o1.compareTo(o2) > 0) {
					return 1;
				}
				if (o1.compareTo(o2) < 0) {
					return -1;
				}
				if (o1.compareTo(o2) == 0) {
					return 0;
				}
				return 0;
			}
		});

		for (int i = 0; i < spans.size(); i++) {
			String span = spans.get(i);
			for (CreditMiddlemenRate creditMiddlemenRate : creditMiddlemenRates) {
				if (span != null) {
					if (span.equals(creditMiddlemenRate.getSpan())) {
						String financingSpanKey = "financingSpan_";
						financingSpanKey = financingSpanKey.concat(String.valueOf(i + 1));
						map.put(financingSpanKey, creditMiddlemenRate.getSpan()); // 期限.
						String financingRateKey = "financingRate_";
						financingRateKey = financingRateKey.concat(String.valueOf(i + 1));
						map.put(financingRateKey, creditMiddlemenRate.getRate()); // 利率.
						String financingServiceRateKey = "financingServiceRate_";
						financingServiceRateKey = financingServiceRateKey.concat(String.valueOf(i + 1));
						map.put(financingServiceRateKey, creditMiddlemenRate.getServiceRate()); // 服务费率.
					}
				}
			}
		}
	}

	/**
	 * 
	 * 方法: createLoanAgreement <br>
	 * 描述: 创建借款协议，应收账款质押（出借人签署协议）. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:18:50
	 * 
	 * @param userInfo
	 * @param project
	 * @param invest
	 * @return
	 * @throws Exception
	 */
	public static String createLoanAgreement(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest) throws Exception {

		log.info("创建借款协议（应收账款质押） ...start...");

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		// 甲方（出借人）.
		if (null != userInfo) {
			map.put("investPeople", userInfo.getRealName());
			map.put("investPeopleIdCard", userInfo.getCertificateNo());
			map.put("investPeopleMobilePhone", userInfo.getName());
		}

		// 出借人出借信息.
		if (null != invest) {
			// 出借金额.
			BigDecimal investmentAmount = new BigDecimal(invest.getAmount());
			map.put("loanAmount", fmtMicrometer(formatToString(investmentAmount)));
			// 出借日期.
			map.put("loanDate", DateUtils.getDate(invest.getBeginDate(), "yyyy/MM/dd HH:mm:ss"));
			// 出借日期.
			String investmentDate = DateUtils.getDate(invest.getBeginDate(), "yyyyMMddHHMMss");
			// 年.
			map.put("year", investmentDate.substring(0, 4));
			// 月.
			map.put("month", investmentDate.substring(4, 6));
			// 日.
			map.put("day", investmentDate.substring(6, 8));
		}

		// 项目信息.
		if (null != project) {
			// 出借期限（日）.
			map.put("loanSpan", project.getSpan().toString());
			// 出借年化收益率（%）.
			map.put("annualInterestRate", NumberUtils.scaleDoubleStr(project.getAnnualRate()));
			// 起息日期（满标日期）.
			map.put("payoutDate", DateUtils.getDate(project.getFullDate(), "yyyy年MM月dd日"));
			/**
			 * 融资主体.
			 */
			String subjectId = project.getSubjectId();// 融资主体ID.
			WloanSubject subject = wloanSubjectService.get(subjectId);
			if (null != subject) {
				// 乙方（借款人）.
				map.put("loanPeople", subject.getCompanyName());
				// 统一社会信用代码/组织机构代码.
				if (subject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
					if (null != subject.getBusinessNo() && !"".equals(subject.getBusinessNo())) {
						map.put("loanPeopleOrganizationCode", subject.getBusinessNo()); // 统一社会信用代码.
						if (null != subject.getOrganNo() && !"".equals(subject.getOrganNo())) {
							map.put("loanPeopleOrganizationCode", subject.getBusinessNo() + "/" + subject.getOrganNo()); // 组织机构代码.
						}
					}
				} else {
					if (null != subject.getOrganNo() && !"".equals(subject.getOrganNo())) {
						map.put("loanPeopleOrganizationCode", subject.getOrganNo()); // 组织机构代码.
					}
				}
			}
			// 借款方住所.
			map.put("loanPeopleResidence", project.getBorrowerResidence());
			// 丁方（次债务人）.
			WloanSubject repaySubject = null;
			if (project.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 代偿还款.
				String replaceRepayId = project.getReplaceRepayId(); // 代偿人ID.
				WloanSubject entity = new WloanSubject();
				entity.setLoanApplyId(replaceRepayId);
				List<WloanSubject> subjects = wloanSubjectService.findList(entity);
				if (subjects != null && subjects.size() > 0) {
					repaySubject = subjects.get(0);
					if (null == repaySubject) {
						map.put("compensatoryPeople", "");
						map.put("compensatoryPeopleOrganizationCode", "");
						map.put("compensatoryPeopleResidence", "");
					} else {
						// 丁方（次债务人）.
						map.put("compensatoryPeople", repaySubject.getCompanyName());
						// 统一社会信用代码/组织机构代码.
						if (repaySubject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
							if (null != repaySubject.getBusinessNo() && !"".equals(repaySubject.getBusinessNo())) {
								map.put("compensatoryPeopleOrganizationCode", repaySubject.getBusinessNo()); // 统一社会信用代码.
								if (null != repaySubject.getOrganNo() && !"".equals(repaySubject.getOrganNo())) {
									map.put("compensatoryPeopleOrganizationCode", repaySubject.getBusinessNo() + "/" + repaySubject.getOrganNo()); // 组织机构代码.
								}
							}
						} else {
							if (null != repaySubject.getOrganNo() && !"".equals(repaySubject.getOrganNo())) {
								map.put("compensatoryPeopleOrganizationCode", repaySubject.getOrganNo()); // 组织机构代码.
							}
						}
						// 核心企业住所.
						map.put("compensatoryPeopleResidence", project.getReplaceRepayResidence());
					}
				} else {
					map.put("compensatoryPeople", "");
					map.put("compensatoryPeopleOrganizationCode", "");
					map.put("compensatoryPeopleResidence", "");
				}
			}
			// 借款申请信息.
			CreditUserApply creditUserApply = creditUserApplyDao.get(project.getCreditUserApplyId());
			if (null != creditUserApply) {
				CreditPack creditPack = new CreditPack();
				creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
				List<CreditPack> creditPackList = creditPackDao.findList(creditPack);
				if (creditPackList != null && creditPackList.size() > 0) {
					// 借款申请合同.
					if (creditPackList.get(0) != null) {
						// 合同签署日期.
						map.put("contractSignDate", DateUtils.getDate(creditPackList.get(0).getSignDate(), "yyyy年MM月dd日"));
						// 合同名称.
						map.put("contractName", creditPackList.get(0).getName());
						// 合同编号.
						map.put("contractNumber", creditPackList.get(0).getNo());
					}
				}
				// 查询申请列表.
				CreditUserApply applyEntity = new CreditUserApply();
				applyEntity.setCreditSupplyId(creditUserApply.getCreditSupplyId());
				applyEntity.setFinancingType(CreditUserApplyService.CREDIT_FINANCING_TYPE_1);
				List<String> stateItem = new ArrayList<String>();
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_2);
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_4);
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_5);
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_6);
				applyEntity.setStateItem(stateItem);
				applyEntity.setBeginCreateDate(DateUtils.getDateOfString("2018-09-20"));
				List<CreditUserApply> creditUserApplyList = creditUserApplyDao.findListByFinancingType(applyEntity);
				if (creditUserApplyList != null && creditUserApplyList.size() > 0) {
					if (creditUserApplyList.get(0) != null) {
						// 主合同及编号（第一次签署的供应链融资框架协议）.
						map.put("mainContractNumber", creditUserApplyList.get(0).getId());
					}
				}
				List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
				Double voucherAmount = 0D;
				for (CreditVoucher creditVoucher : creditVouchers) {
					voucherAmount = voucherAmount + Double.valueOf(creditVoucher.getMoney());
				}
				// 出质金额.
				map.put("invoiceTotalAmount", fmtMicrometer(NumberUtils.scaleDoubleStr(voucherAmount)));
			}
			// 项目还款计划列表.
			List<WloanTermProjectPlan> list = wloanTermProjectPlanDao.findProPlansByProId(project.getId());
			log.info("还款计划期数：" + list.size());
			WloanTermProjectPlan entity = list.get(list.size() - 1); // 最后一期还款.
			// 最后一期还款截至日期.
			map.put("lastRepayDate", DateUtils.getDate(entity.getRepaymentDate(), "yyyy年MM月dd日"));
			// 利率计算标准.
			map.put("interestRate", NumberUtils.scaleDoubleStr(project.getAnnualRate()).concat("%"));
		}
		// 中投摩根信息技术（北京）有限责任公司.
		map.put("ztmgPeople", "中投摩根信息技术（北京）有限责任公司");

		return createLoanAgreementByTemplate(map, project);
	}

	/**
	 * 
	 * 方法: createLoanAgreementByTemplate <br>
	 * 描述: 根据模版创建，借款协议（应收账款质押）. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:19:06
	 * 
	 * @param map
	 * @param project
	 * @return
	 * @throws Exception
	 */
	private static String createLoanAgreementByTemplate(Map<String, String> map, WloanTermProject project) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		String templateFileNamePath = TEMPLATE_FILE_PATH + "JieKuanXieYi_YingShouZhangKuanZhiYa.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("investPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("investPeopleIdCard", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("investPeopleMobilePhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("annualInterestRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanSpan", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("payoutDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanPeopleOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanPeopleResidence", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("compensatoryPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("compensatoryPeopleOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("compensatoryPeopleResidence", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("contractSignDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("contractName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("contractNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("lastRepayDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("interestRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("mainContractNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("invoiceTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ztmgPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("year", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("month", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("day", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(newFileNamePath + File.separator + newFileName));
		fos.write(bos.toByteArray());
		fos.close();

		log.info("创建借款协议（应收账款质押） ...end...");

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: fmtMicrometer <br>
	 * 描述: 格式化数字为千分位显示. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:19:21
	 * 
	 * @param text
	 * @return
	 */
	private static String fmtMicrometer(String text) {

		DecimalFormat df = null;
		if (text.indexOf('.') > 0) {
			if (text.length() - text.indexOf('.') - 1 == 0) {
				df = new DecimalFormat("###,##0.");
			} else if (text.length() - text.indexOf('.') - 1 == 1) {
				df = new DecimalFormat("###,##0.0");
			} else {
				df = new DecimalFormat("###,##0.00");
			}
		} else {
			df = new DecimalFormat("###,##0.00");
		}

		double number = 0.0;

		try {
			number = Double.parseDouble(text);
		} catch (Exception e) {
			number = 0.0;
		}

		return df.format(number);
	}

	/**
	 * 
	 * 方法: formatToString <br>
	 * 描述: BigDecimal转换为String类型，并进行去零操作. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:19:32
	 * 
	 * @param result
	 * @return
	 */
	private static String formatToString(BigDecimal result) {

		if (result == null) {
			return "";
		} else {
			return result.stripTrailingZeros().toPlainString();
		}
	}

	/**
	 * 
	 * 方法: fillData <br>
	 * 描述: 为PDF文件表单域赋值. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月20日 下午4:19:44
	 * 
	 * @param fields
	 * @param data
	 * @throws Exception
	 */
	private static void fillData(AcroFields fields, Map<String, String> data) throws Exception {

		for (String key : data.keySet()) {
			String value = data.get(key);
			fields.setField(key, value);
		}
	}

	/**
	 * 
	 * methods: xiYunOverdueDaysPenaltyInterest <br>
	 * description: 熙耘借款协议（应收账款质押），逾期罚息表. <br>
	 * author: Roy <br>
	 * date: 2019年4月19日 下午5:28:31
	 * 
	 * @param map
	 */
	public static void xiYunOverdueDaysPenaltyInterest(Map<String, String> map) {

		for (int i = 1; i <= 2; i++) { // 逾期罚息表，两行.
			if (i == 1) {
				// 逾期天数Key.
				String overdueDaysKey = "overdueDays_";
				overdueDaysKey = overdueDaysKey.concat(String.valueOf(i));
				String overdueDaysValue = "1-30日（含30日）";
				map.put(overdueDaysKey, overdueDaysValue);
				// 罚息利率Key.
				String penaltyInterestRateKey = "penaltyInterestRate_";
				penaltyInterestRateKey = penaltyInterestRateKey.concat(String.valueOf(i));
				String penaltyInterestRateValue = "0.03%/日";
				map.put(penaltyInterestRateKey, penaltyInterestRateValue);
			}
			if (i == 2) {
				// 逾期天数Key.
				String overdueDaysKey = "overdueDays_";
				overdueDaysKey = overdueDaysKey.concat(String.valueOf(i));
				String overdueDaysValue = "超过30日";
				map.put(overdueDaysKey, overdueDaysValue);
				// 罚息利率Key.
				String penaltyInterestRateKey = "penaltyInterestRate_";
				penaltyInterestRateKey = penaltyInterestRateKey.concat(String.valueOf(i));
				String penaltyInterestRateValue = "0.05%/日";
				map.put(penaltyInterestRateKey, penaltyInterestRateValue);
			}
		}
	}

	/**
	 * 
	 * methods: xiYunCommunicationInfo <br>
	 * description: 熙耘借款协议（应收账款质押），各方联系方式. <br>
	 * author: Roy <br>
	 * date: 2019年4月19日 下午5:33:37
	 * 
	 * @param map
	 * @param userInfo
	 *            甲方
	 * @param bPartySubject
	 *            乙方
	 * @param dPartySubject
	 *            丁方
	 * @param ePartySubject
	 *            戊方
	 */
	public static void xiYunCommunicationInfo(Map<String, String> map, UserInfo userInfo, WloanSubject bPartySubject, WloanSubject dPartySubject, WloanSubject ePartySubject) {

		if (userInfo != null) { // 甲方
			if (!StringUtils.isBlank(userInfo.getAddress())) {
				map.put("aPartyCommunicationAddress", userInfo.getAddress());
			} else {
				map.put("aPartyCommunicationAddress", "---");
			}
			map.put("aPartyPostCode", "---");
			if (!StringUtils.isBlank(userInfo.getName())) {
				map.put("aPartyPhone", CommonStringUtils.mobileEncrypt(userInfo.getName()));
			} else {
				map.put("aPartyPhone", "---");
			}
			map.put("aPartyFax", "---");
			map.put("aPartyTheContact", "---");
			if (!StringUtils.isBlank(userInfo.getEmail())) {
				map.put("aPartyEmail", userInfo.getEmail());
			} else {
				map.put("aPartyEmail", "---");
			}
		}

		if (bPartySubject != null) { // 乙方.
			if (!StringUtils.isBlank(bPartySubject.getRegistAddress())) {
				map.put("bPartyCommunicationAddress", bPartySubject.getRegistAddress());
			} else {
				map.put("bPartyCommunicationAddress", "---");
			}
			map.put("bPartyPostCode", "---");
			if (!StringUtils.isBlank(bPartySubject.getAgentPersonPhone())) {
				map.put("bPartyPhone", bPartySubject.getAgentPersonPhone());
			} else {
				map.put("bPartyPhone", "---");
			}
			map.put("bPartyFax", "---");
			if (!StringUtils.isBlank(bPartySubject.getAgentPersonName())) {
				map.put("bPartyTheContact", bPartySubject.getAgentPersonName());
			} else {
				map.put("bPartyTheContact", "---");
			}
			if (!StringUtils.isBlank(bPartySubject.getEmail())) {
				map.put("bPartyEmail", bPartySubject.getEmail());
			} else {
				map.put("bPartyEmail", "---");
			}
		}

		// 丙方.
		map.put("cPartyCommunicationAddress", "北京市海淀区莲花池东路39号6层601-1");
		map.put("cPartyPostCode", "100036");
		map.put("cPartyTheContact", "李元哲");
		map.put("cPartyPhone", "010-66525610/13903150121");
		map.put("cPartyEmail", "liyuanzhe@cicmorgan.com");

		if (dPartySubject != null) { // 丁方.
			if (!StringUtils.isBlank(dPartySubject.getRegistAddress())) {
				map.put("dPartyCommunicationAddress", dPartySubject.getRegistAddress());
			} else {
				map.put("dPartyCommunicationAddress", "---");
			}
			map.put("dPartyPostCode", "---");
			if (!StringUtils.isBlank(dPartySubject.getAgentPersonPhone())) {
				map.put("dPartyPhone", dPartySubject.getAgentPersonPhone());
			} else {
				map.put("dPartyPhone", "---");
			}
			if (!StringUtils.isBlank(dPartySubject.getAgentPersonName())) {
				map.put("dPartyTheContact", dPartySubject.getAgentPersonName());
			} else {
				map.put("dPartyTheContact", "---");
			}
			if (!StringUtils.isBlank(dPartySubject.getEmail())) {
				map.put("dPartyEmail", dPartySubject.getEmail());
			} else {
				map.put("dPartyEmail", "---");
			}
		}

		if (ePartySubject != null) { // 戊方.
			if (!StringUtils.isBlank(ePartySubject.getRegistAddress())) {
				map.put("ePartyCommunicationAddress", ePartySubject.getRegistAddress());
			} else {
				map.put("ePartyCommunicationAddress", "---");
			}
			map.put("ePartyPostCode", "---");
			if (!StringUtils.isBlank(ePartySubject.getAgentPersonPhone())) {
				map.put("ePartyPhone", ePartySubject.getAgentPersonPhone());
			} else {
				map.put("ePartyPhone", "---");
			}
			if (!StringUtils.isBlank(ePartySubject.getAgentPersonName())) {
				map.put("ePartyTheContact", ePartySubject.getAgentPersonName());
			} else {
				map.put("ePartyTheContact", "---");
			}
			if (!StringUtils.isBlank(ePartySubject.getEmail())) {
				map.put("ePartyEmail", ePartySubject.getEmail());
			} else {
				map.put("ePartyEmail", "---");
			}
		}
	}

	/**
	 * 
	 * methods: createXiYunLoanAgreement <br>
	 * description: 创建熙云借款协议（应收账款质押）. <br>
	 * author: Roy <br>
	 * date: 2019年4月19日 下午3:52:22
	 * 
	 * @param user
	 * @param projectc
	 * @param wloanTermInvest
	 * @return
	 */
	public static String createXiYunLoanAgreement(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest) throws Exception {

		log.info("创建【熙耘】借款协议（应收账款质押） ...start...");

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		// 甲方（出借人）.
		if (null != userInfo) {
			map.put("investPeople", userInfo.getRealName());
			map.put("investPeopleIdCard", userInfo.getCertificateNo());
			map.put("investPeopleMobilePhone", userInfo.getName());
		}

		// 出借人出借信息.
		if (null != invest) {
			// 编号.
			map.put("sn", invest.getId());
			// 出借金额.
			BigDecimal investmentAmount = new BigDecimal(invest.getAmount());
			map.put("loanAmount", fmtMicrometer(formatToString(investmentAmount)));
			// 出借日期.
			map.put("loanDate", DateUtils.getDate(invest.getBeginDate(), "yyyy/MM/dd HH:mm:ss"));
		}

		// 项目信息.
		if (null != project) {
			// 出借期限（日）.
			map.put("loanSpan", project.getSpan().toString());
			// 出借年化收益率（%）.
			map.put("annualInterestRate", NumberUtils.scaleDoubleStr(project.getAnnualRate()));
			// 起息日期（满标日期）.
			map.put("payoutDate", DateUtils.getDate(project.getFullDate(), "yyyy年MM月dd日"));
			/**
			 * 融资主体.
			 */
			String subjectId = project.getSubjectId();// 融资主体ID.
			WloanSubject subject = wloanSubjectService.get(subjectId);
			if (null != subject) {
				// 乙方（借款人）.
				map.put("loanPeople", subject.getCompanyName());
				// 统一社会信用代码/组织机构代码.
				if (subject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
					if (null != subject.getBusinessNo() && !"".equals(subject.getBusinessNo())) {
						map.put("loanPeopleOrganizationCode", subject.getBusinessNo()); // 统一社会信用代码.
						if (null != subject.getOrganNo() && !"".equals(subject.getOrganNo())) {
							map.put("loanPeopleOrganizationCode", subject.getBusinessNo() + "/" + subject.getOrganNo()); // 组织机构代码.
						}
					}
				} else {
					if (null != subject.getOrganNo() && !"".equals(subject.getOrganNo())) {
						map.put("loanPeopleOrganizationCode", subject.getOrganNo()); // 组织机构代码.
					}
				}
			}
			// 借款方住所.
			map.put("loanPeopleResidence", project.getBorrowerResidence());
			// 丁方（基础合同付款人、担保人）.
			WloanSubject repaySubject = null;
			if (project.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 代偿还款.
				String replaceRepayId = project.getReplaceRepayId(); // 代偿人ID.
				WloanSubject entity = new WloanSubject();
				entity.setLoanApplyId(replaceRepayId);
				List<WloanSubject> subjects = wloanSubjectService.findList(entity);
				if (subjects != null && subjects.size() > 0) {
					repaySubject = subjects.get(0);
					if (null == repaySubject) {
						map.put("compensatoryPeople", "");
						map.put("compensatoryPeopleOrganizationCode", "");
						map.put("compensatoryPeopleResidence", "");
					} else {
						// 丁方（次债务人）.
						map.put("compensatoryPeople", repaySubject.getCompanyName());
						// 统一社会信用代码/组织机构代码.
						if (repaySubject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
							if (null != repaySubject.getBusinessNo() && !"".equals(repaySubject.getBusinessNo())) {
								map.put("compensatoryPeopleOrganizationCode", repaySubject.getBusinessNo()); // 统一社会信用代码.
								if (null != repaySubject.getOrganNo() && !"".equals(repaySubject.getOrganNo())) {
									map.put("compensatoryPeopleOrganizationCode", repaySubject.getBusinessNo() + "/" + repaySubject.getOrganNo()); // 组织机构代码.
								}
							}
						} else {
							if (null != repaySubject.getOrganNo() && !"".equals(repaySubject.getOrganNo())) {
								map.put("compensatoryPeopleOrganizationCode", repaySubject.getOrganNo()); // 组织机构代码.
							}
						}
						// 核心企业住所.
						map.put("compensatoryPeopleResidence", project.getReplaceRepayResidence());
					}
				} else {
					map.put("compensatoryPeople", "");
					map.put("compensatoryPeopleOrganizationCode", "");
					map.put("compensatoryPeopleResidence", "");
				}
			}
			// 戊方（担保人）.
			WloanSubject aiqinWloanSubject = wloanSubjectService.get(WloanSubjectService.AIQIN_SUBJECT_ID);
			if (null == aiqinWloanSubject) {
				map.put("ePartyPeople", "");
				map.put("ePartyPeopleOrganizationCode", "");
				map.put("ePartyPeopleResidence", "");
			} else {
				// 戊方（担保人）.
				map.put("ePartyPeople", aiqinWloanSubject.getCompanyName());
				// 统一社会信用代码/组织机构代码.
				if (aiqinWloanSubject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
					if (null != aiqinWloanSubject.getBusinessNo() && !"".equals(aiqinWloanSubject.getBusinessNo())) {
						map.put("ePartyPeopleOrganizationCode", aiqinWloanSubject.getBusinessNo()); // 统一社会信用代码.
						if (null != aiqinWloanSubject.getOrganNo() && !"".equals(aiqinWloanSubject.getOrganNo())) {
							map.put("ePartyPeopleOrganizationCode", aiqinWloanSubject.getBusinessNo() + "/" + aiqinWloanSubject.getOrganNo()); // 组织机构代码.
						}
					}
				} else {
					if (null != aiqinWloanSubject.getOrganNo() && !"".equals(aiqinWloanSubject.getOrganNo())) {
						map.put("ePartyPeopleOrganizationCode", aiqinWloanSubject.getOrganNo()); // 组织机构代码.
					}
				}
				// 住所.
				map.put("ePartyPeopleResidence", aiqinWloanSubject.getRegistAddress());
			}

			// 逾期天数与罚息.
			xiYunOverdueDaysPenaltyInterest(map);

			// 各方通选方式.
			xiYunCommunicationInfo(map, userInfo, subject, repaySubject, aiqinWloanSubject);

			// 借款申请信息.
			CreditUserApply creditUserApply = creditUserApplyDao.get(project.getCreditUserApplyId());
			if (null != creditUserApply) {
				CreditPack creditPack = new CreditPack();
				creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
				List<CreditPack> creditPackList = creditPackDao.findList(creditPack);
				if (creditPackList != null && creditPackList.size() > 0) {
					// 借款申请合同.
					if (creditPackList.get(0) != null) {
						// 合同签署日期.
						map.put("contractSignDate", DateUtils.getDate(creditPackList.get(0).getSignDate(), "yyyy年MM月dd日"));
						// 合同名称.
						map.put("contractName", creditPackList.get(0).getName());
						// 合同编号.
						map.put("contractNumber", creditPackList.get(0).getNo());
					}
				}
				// 查询申请列表.
				CreditUserApply applyEntity = new CreditUserApply();
				applyEntity.setCreditSupplyId(creditUserApply.getCreditSupplyId());
				applyEntity.setFinancingType(CreditUserApplyService.CREDIT_FINANCING_TYPE_1);
				List<String> stateItem = new ArrayList<String>();
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_2);
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_4);
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_5);
				stateItem.add(CreditUserApplyService.CREDIT_USER_APPLY_STATE_6);
				applyEntity.setStateItem(stateItem);
				applyEntity.setBeginCreateDate(DateUtils.getDateOfString("2018-09-20"));
				List<CreditUserApply> creditUserApplyList = creditUserApplyDao.findListByFinancingType(applyEntity);
				if (creditUserApplyList != null && creditUserApplyList.size() > 0) {
					if (creditUserApplyList.get(0) != null) {
						// 主合同及编号（第一次签署的供应链融资框架协议）.
						map.put("mainContractNumber", creditUserApplyList.get(0).getId());
					}
				}
				List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
				Double voucherAmount = 0D;
				for (CreditVoucher creditVoucher : creditVouchers) {
					voucherAmount = voucherAmount + Double.valueOf(creditVoucher.getMoney());
				}
				// 出质金额.
				map.put("invoiceTotalAmount", fmtMicrometer(NumberUtils.scaleDoubleStr(voucherAmount)));
			}
			// 项目还款计划列表.
			List<WloanTermProjectPlan> list = wloanTermProjectPlanDao.findProPlansByProId(project.getId());
			log.info("还款计划期数：" + list.size());
			WloanTermProjectPlan entity = list.get(list.size() - 1); // 最后一期还款.
			// 最后一期还款截至日期.
			map.put("lastRepayDate", DateUtils.getDate(entity.getRepaymentDate(), "yyyy年MM月dd日"));
			// 利率计算标准.
			map.put("interestRate", NumberUtils.scaleDoubleStr(project.getAnnualRate()).concat("%"));
		}

		// 丙方签章-中投摩根信息技术（北京）有限责任公司.
		map.put("cPartyPeople", "中投摩根信息技术（北京）有限责任公司");

		/**
		 * 委托协议信息.
		 */
		// 甲方（委托人）.
		if (null != userInfo) {
			map.put("entrustName", userInfo.getRealName());
			map.put("entrustCardType", "身份证");
			map.put("entrustIdCard", userInfo.getCertificateNo());
		}

		// 受托人.
		map.put("commissionedPeople", "中金昌盛商业保理（深圳）有限公司");
		map.put("commissionedOrganizationCode", "91440300MA5EFAU171");

		return createXiYunLoanAgreementByTemplate(map, project);
	}

	/**
	 * 
	 * methods: createXiYunLoanAgreementByTemplate <br>
	 * description: 根据模版创建，熙耘借款协议（应收账款质押）. <br>
	 * author: Roy <br>
	 * date: 2019年4月21日 上午9:11:03
	 * 
	 * @param map
	 * @param project
	 * @return
	 * @throws Exception
	 */
	private static String createXiYunLoanAgreementByTemplate(Map<String, String> map, WloanTermProject project) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		String templateFileNamePath = TEMPLATE_FILE_PATH + "XY_JieKuanXieYi_YingShouZhangKuanZhiYa.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		// 出借人信息.
		fields.setFieldProperty("investPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("investPeopleIdCard", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("investPeopleMobilePhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("sn", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanSpan", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("annualInterestRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("payoutDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 融资主体乙方.
		fields.setFieldProperty("loanPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanPeopleOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanPeopleResidence", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 融资主体丁方.
		fields.setFieldProperty("compensatoryPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("compensatoryPeopleOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("compensatoryPeopleResidence", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 融资主体戊方.
		fields.setFieldProperty("ePartyPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ePartyPeopleOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ePartyPeopleResidence", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 逾期罚息.
		fields.setFieldProperty("overdueDays_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penaltyInterestRate_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("overdueDays_2", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penaltyInterestRate_2", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 通讯方式.
		// 甲方.
		fields.setFieldProperty("aPartyCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("aPartyPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("aPartyPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("aPartyFax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("aPartyTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("aPartyEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 乙方.
		fields.setFieldProperty("bPartyCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("bPartyPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("bPartyPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("bPartyFax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("bPartyTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("bPartyEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 丙方.
		fields.setFieldProperty("cPartyCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("cPartyPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("cPartyTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("cPartyPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("cPartyEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 丁方.
		fields.setFieldProperty("dPartyCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("dPartyPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("dPartyPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("dPartyTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("dPartyEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 戊方.
		fields.setFieldProperty("ePartyCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ePartyPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ePartyPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ePartyTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("ePartyEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 借款合同.
		fields.setFieldProperty("contractSignDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("contractName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("contractNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 主合同及编号（供应链融资框架协议）.
		fields.setFieldProperty("mainContractNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 出质金额.
		fields.setFieldProperty("invoiceTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 最后一期还款日.
		fields.setFieldProperty("lastRepayDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 利息计算标准.
		fields.setFieldProperty("interestRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 丙方签字.
		fields.setFieldProperty("cPartyPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 委托人.
		fields.setFieldProperty("entrustName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("entrustCardType", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("entrustIdCard", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 受托人.
		fields.setFieldProperty("commissionedPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("commissionedOrganizationCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(newFileNamePath + File.separator + newFileName));
		fos.write(bos.toByteArray());
		fos.close();

		log.info("创建【熙耘】借款协议（应收账款质押） ...end...");

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * methods: createXiYunFinancingFrameworkAgreement <br>
	 * description: 熙耘（供应链融资合作框架协议）. <br>
	 * author: Roy <br>
	 * date: 2019年4月21日 上午11:08:30
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createXiYunFinancingFrameworkAgreement(CreditUserApply creditUserApply) throws Exception {

		// 甲方（供应商）供应商帐号信息.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 甲方（供应商）供应商融资主体信息.
		WloanSubject supplierSubjectEntity = new WloanSubject();
		supplierSubjectEntity.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> supplierSubjects = wloanSubjectDao.findList(supplierSubjectEntity);
		WloanSubject supplierSubject = null;
		if (supplierSubjects != null) {
			supplierSubject = supplierSubjects.get(0);
		}
		// 甲方（供应商）供应商基本信息.
		ZtmgLoanBasicInfo supplierBasicInfoEntity = new ZtmgLoanBasicInfo();
		supplierBasicInfoEntity.setCreditUserId(creditUserApply.getCreditSupplyId());
		ZtmgLoanBasicInfo supplierBasicInfo = ztmgLoanBasicInfoDao.findByCreditUserId(supplierBasicInfoEntity);

		// 乙方（采购商）核心企业帐号信息.
		CreditUserInfo purchaserUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		// 乙方（采购商）核心企业融资主体信息.
		WloanSubject purchaserSubjectEntity = new WloanSubject();
		purchaserSubjectEntity.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> purchaserSubjects = wloanSubjectDao.findList(purchaserSubjectEntity);
		WloanSubject purchaserSubject = null;
		if (purchaserSubjects != null) {
			purchaserSubject = purchaserSubjects.get(0);
		}
		// 乙方（采购商）核心企业基本信息.
		ZtmgLoanBasicInfo purchaserBasicInfoEntity = new ZtmgLoanBasicInfo();
		purchaserBasicInfoEntity.setCreditUserId(creditUserApply.getReplaceUserId());
		ZtmgLoanBasicInfo purchaserBasicInfo = ztmgLoanBasicInfoDao.findByCreditUserId(purchaserBasicInfoEntity);

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();

		// 编号.
		map.put("serialNumber", creditUserApply.getId());
		// 协议创建日期.
		String signDate = DateUtils.getDate(new Date(), "yyyy年MM月dd日");
		// 编号.
		map.put("signDate", signDate);

		// 甲方（供应商）供应商帐号信息.
		if (supplierUserInfo != null) {
			// 甲方（供应商）公司名称.
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}
		// 甲方（供应商）供应商融资主体信息.
		if (supplierSubject != null) {
			// 甲方（供应商）法定代表人.
			map.put("supplierLegalRepresentative", supplierSubject.getLoanUser());
		}
		// 甲方（供应商）供应商融资主体信息.
		if (supplierBasicInfo != null) {
			// 甲方（供应商）地址.
			map.put("supplierAddress", supplierBasicInfo.getRegisteredAddress());
		}
		// 乙方（采购商）核心企业帐号信息.
		if (purchaserUserInfo != null) {
			// 乙方（采购商）公司名称.
			map.put("purchaserCompanyName", purchaserUserInfo.getEnterpriseFullName());
		}
		// 乙方（采购商）核心企业融资主体信息.
		if (purchaserSubject != null) {
			// 乙方（采购商）法定代表人.
			map.put("purchaserLegalRepresentative", purchaserSubject.getLoanUser());
		}
		// 乙方（采购商）核心企业基本信息.
		if (purchaserBasicInfo != null) {
			// 乙方（采购商）地址.
			map.put("purchaserAddress", purchaserBasicInfo.getRegisteredAddress());
		}
		// 核心企业爱亲.
		map.put("ePartyCompanyName", "北京爱亲科技股份有限公司");

		// 申请融资金额.
		BigDecimal amountBd = new BigDecimal(creditUserApply.getAmount());
		StringBuffer amountSb = new StringBuffer();
		// 甲方（供应商）申请融资金额.
		map.put("supplierFinancingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());
		// 甲方（供应商）申请融资期限.
		map.put("span", creditUserApply.getSpan());
		// 借款申请发票应收账款转让总金额.
		Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
		if (invoiceTotalAmount != null) {
			BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
			StringBuffer invoiceTotalAmountSb = new StringBuffer();
			// 甲方（供应商）首次拟以总金额.
			map.put("supplierTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
		}

		// 甲方供应链融资的融资利率及服务费率表.
		creditMiddlemenRateSort(map, creditUserApply.getReplaceUserId());

		// 平台融资服务费比例分摊.
		platformFinancingServiceRateProportion(map, supplierUserInfo, purchaserUserInfo, creditUserApply);

		// 逾期罚息表.
		xiYunOverdueDaysPenaltyInterest(map);

		// 各方的联络通讯及方法.
		communicationInfo(map, supplierSubject, purchaserSubject);

		// title.
		String title = "";
		// rowTitle.
		String[] rowTitle = null;
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		if (creditUserApply.getFinancingType().equals(FINANCING_TYPE_1)) {
			// title.
			title = "附表一\t应收账款清单列表如下：";
			// rowTitle.
			rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
			List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
			// rowData.
			// 合同.
			CreditPack creditPack = creditUserApply.getCreditPack();
			for (CreditVoucher creditVoucher : creditVouchers) {
				String[] strings = new String[rowTitle.length];
				if (creditPack != null) {
					strings[0] = creditPack.getNo();
				} else {
					strings[0] = creditVoucher.getPackNo();
				}
				strings[1] = creditVoucher.getNo();
				strings[2] = creditVoucher.getMoney();
				dataList.add(strings);
			}
		} else if (creditUserApply.getFinancingType().equals(FINANCING_TYPE_2)) {
		}

		return createXiYunFinancingFrameworkAgreementByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * methods: createXiYunFinancingFrameworkAgreementByTemplate <br>
	 * description: 熙耘（供应链融资合作框架协议）模版. <br>
	 * author: Roy <br>
	 * date: 2019年4月21日 上午11:10:45
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createXiYunFinancingFrameworkAgreementByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称：\t" + newFileName);
		// 正式环境
		String linFilePath = LIN_FILE_PATH + "XY_GYLRZHZKJXY_Temp.pdf"; // 临时文件路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "XY_GYLRZHZKJXY.pdf"; // 模版文件路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate(); // 生成文件输出路径.
		String tablePath = OUT_PATH + "XY_GYLRZHZKJXY_Table.pdf"; // 表格路径.
		log.info("临时文件路径：\t" + linFilePath);
		log.info("模版文件路径：\t" + templateFileNamePath);
		log.info("生成文件输出路径：\t" + newFileNamePath);
		log.info("表格路径：\t" + tablePath);
		// 新文件的输出目录.
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));
		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);
		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("serialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("signDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierLegalRepresentative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserLegalRepresentative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierFinancingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 核心企业爱亲.
		fields.setFieldProperty("ePartyCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 平台融资服务费分摊比例.
		fields.setFieldProperty("supplierCompanyName_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierProportion", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCompanyName_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserProportion", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 逾期罚息表.
		fields.setFieldProperty("overdueDays_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penaltyInterestRate_1", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("overdueDays_2", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penaltyInterestRate_2", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		// 各方的联络及通讯方法.
		fields.setFieldProperty("supplierCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierFax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("supplierEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserCommunicationAddress", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserPostCode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserPhone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserFax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserTheContact", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purchaserEmail", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 临时PDF路径与表格PDF路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 临时PDF文件与表格PDF文件进行合并.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * methods: createXiYunPromiseToPayBook <br>
	 * description: 熙耘承诺函. <br>
	 * author: Roy <br>
	 * date: 2019年4月21日 下午1:24:39
	 * 
	 * @param creditUserApply
	 * @param creditVouchers
	 * @return
	 * @throws Exception
	 */
	public static String createXiYunPromiseToPayBook(CreditUserApply creditUserApply, List<CreditVoucher> creditVouchers) throws Exception {

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 供应商帐号信息.
		CreditUserInfo supplierUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		if (supplierUserInfo != null) {
			// 供应商公司名称.
			map.put("supplierCompanyName", supplierUserInfo.getEnterpriseFullName());
		}
		// 核心企业帐号信息.
		CreditUserInfo replaceUserInfo = creditUserInfoDao.get(CreditUserInfo.AIQIN_ID);
		if (replaceUserInfo != null) {
			// 核心企业公司名称.
			map.put("commitmentToPeople", replaceUserInfo.getEnterpriseFullName());
		}
		// 签署日期.
		map.put("signedDate", DateUtils.getDate(new Date(), "yyyy年MM月dd日"));
		// 融资申请合同信息.
		String contractName = ""; // 合同名称.
		String contractNumber = ""; // 合同编号.
		Double contractMoney = 0D; // 合同金额.
		CreditPack creditPack = new CreditPack();
		creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
		List<CreditPack> creditPacks = creditPackDao.findList(creditPack);
		if (creditPacks != null) {
			if (creditPacks.get(0) != null) {
				contractName = creditPacks.get(0).getName();
				contractNumber = creditPacks.get(0).getNo();
				if (creditPacks.get(0).getMoney() != null) {
					contractMoney = Double.valueOf(creditPacks.get(0).getMoney());
				}
			}
		}

		// 发票总额.
		Double voucherTotalAmount = 0D;
		for (int i = 0; i < creditVouchers.size(); i++) {
			CreditVoucher creditVoucher = creditVouchers.get(i);
			String moneyStr = creditVoucher.getMoney();
			voucherTotalAmount = voucherTotalAmount + Double.valueOf(moneyStr);
		}

		// 融资金额比例（融资总额/发票总额）.
		Double financingAmountProportion = NumberUtils.scaleDouble(contractMoney / voucherTotalAmount);

		/**
		 * 我司应付账款:
		 */
		// title.
		String title = "附表一\t我司应付账款：";
		// rowTitle.
		String[] rowTitle = new String[] { "合同名称", "合同编号", "应收账款（￥/万元）", "发票号", "发票金额（￥/万元）", "融资金额（￥/万元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();

		// (n-1) 融资总额（万元）.
		Double financingTotalAmountN_1 = 0D;
		for (int i = 0; i < creditVouchers.size(); i++) {
			CreditVoucher creditVoucher = creditVouchers.get(i);
			String[] strings = new String[rowTitle.length];
			// 合同名称.
			strings[0] = contractName;
			// 合同编号.
			strings[1] = contractNumber;
			// 应收账款（万元）.
			Double voucherMoneyDouble = Double.valueOf(creditVoucher.getMoney());
			BigDecimal voucherMoneyDoubleBd = new BigDecimal(voucherMoneyDouble / 10000);
			strings[2] = fmtMicrometer(formatToString(voucherMoneyDoubleBd));
			// 发票号.
			strings[3] = creditVoucher.getNo();
			// 发票金额（万元）.
			strings[4] = fmtMicrometer(formatToString(voucherMoneyDoubleBd));
			if (i == (creditVouchers.size() - 1)) {
				// 融资金额（万元）.
				BigDecimal financingAmountBd = new BigDecimal((NumberUtils.scaleDouble(contractMoney / 10000) - financingTotalAmountN_1));
				strings[5] = fmtMicrometer(formatToString(financingAmountBd));
			} else {
				// 融资金额（万元）.
				BigDecimal financingAmountBd = new BigDecimal(NumberUtils.scaleDouble(financingAmountProportion * voucherMoneyDouble / 10000));
				financingTotalAmountN_1 = financingTotalAmountN_1 + financingAmountBd.doubleValue();
				strings[5] = fmtMicrometer(formatToString(financingAmountBd));
			}
			dataList.add(strings);
		}

		return createXiYunPromiseToPayBookByTemplate(map, title, rowTitle, dataList);
	}

	private static String createXiYunPromiseToPayBookByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称：\t" + newFileName);
		// 正式环境
		String linFilePath = LIN_FILE_PATH + "XY_CNH_Temp.pdf"; // 临时文件路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "XY_CNH.pdf"; // 模版文件路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate(); // 生成文件输出路径.
		String tablePath = OUT_PATH + "XY_CNH_Table.pdf"; // 表格路径.
		log.info("临时文件路径：\t" + linFilePath);
		log.info("模版文件路径：\t" + templateFileNamePath);
		log.info("生成文件输出路径：\t" + newFileNamePath);
		log.info("表格路径：\t" + tablePath);
		// 新文件的输出目录.
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));
		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);
		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("supplierCompanyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("commitmentToPeople", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("signedDate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 临时PDF路径与表格PDF路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 临时PDF文件与表格PDF文件进行合并.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

}
