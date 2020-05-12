package com.power.platform.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.FileUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MergeFileUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfGenerateTables;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.SpringContextHolder;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.voucher.CreditVoucherDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucher;
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
 * 类: CreateSupplyChainPdfContract <br>
 * 描述: 创建供应链PDF协议（应收账款转让协议）. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年9月5日 下午1:44:37
 */
public class CreateSupplyChainPdfContract {

	/**
	 * 核心企业：爱亲.
	 */
	public static final String AQ_PRIMARY_KEY = "5685145015583919274";

	/**
	 * 爱亲不同期限的不同年化利率.
	 */
	public static final String AI_QIN_MANAGEMENT_FEE_RATE_30 = "7.00";
	public static final String AI_QIN_MANAGEMENT_FEE_RATE_60 = "7.50";
	public static final String AI_QIN_MANAGEMENT_FEE_RATE_90 = "8.00";
	public static final String AI_QIN_MANAGEMENT_FEE_RATE_120 = "9.00";
	/**
	 * 爱亲服务费率3.00%（年化）.
	 */
	public static final String AI_QIN_SERVIC_FEE_3 = "3.00";
	/**
	 * 爱亲项目的期限.
	 */
	public static final Integer AI_QIN_SPAN_30 = 30;
	public static final Integer AI_QIN_SPAN_60 = 60;
	public static final Integer AI_QIN_SPAN_90 = 90;
	public static final Integer AI_QIN_SPAN_120 = 120;
	/**
	 * 项目期限，30天.
	 */
	public static final Integer SPAN_30 = 30;
	/**
	 * 项目期限，60天.
	 */
	public static final Integer SPAN_60 = 60;
	/**
	 * 项目期限，90天.
	 */
	public static final Integer SPAN_90 = 90;
	/**
	 * 项目期限，120天.
	 */
	public static final Integer SPAN_120 = 120;
	/**
	 * 项目期限，180天.
	 */
	public static final Integer SPAN_180 = 180;
	/**
	 * 项目期限，360天.
	 */
	public static final Integer SPAN_360 = 360;

	/**
	 * 年化利率：30天，7.00%/年.
	 */
	public static final String MANAGEMENT_FEE_RATE_30 = "7.00";

	/**
	 * 转让服务费：30天，1.75%/年.
	 */
	public static final String SERVIC_FEE_30 = "1.75";

	/**
	 * 年化利率：90天，8.00%/年.
	 */
	public static final String MANAGEMENT_FEE_RATE_90 = "8.00";

	/**
	 * 转让服务费：90天，2.00%/年.
	 */
	public static final String SERVIC_FEE_90 = "2.00";

	/**
	 * 年化利率：180天，9.50%/年.
	 */
	public static final String MANAGEMENT_FEE_RATE_180 = "9.50";

	/**
	 * 转让服务费：180天，2.00%/年.
	 */
	public static final String SERVIC_FEE_180 = "2.00";

	/**
	 * 年化利率：360天，13.00%/年.
	 */
	public static final String MANAGEMENT_FEE_RATE_360 = "13.00";

	/**
	 * 转让服务费：360天，3.00%/年.
	 */
	public static final String SERVIC_FEE_360 = "3.00";

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
	 * 电子签章URL.
	 */
	// private static final String ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL =
	// Global.getConfig("ZTMG.ELECTRONIC.SIGNATURE.IMAGE");

	/**
	 * 中投摩根电子签章.
	 */
	// private static final String ZTMG_ELECTRONIC_SIGNATURE_IMAGE =
	// "ztmg_electronic_sign.png";

	/**
	 * 电子签章（签名）前缀URL.
	 */
	// private static final String ELECTRONIC_SIGNATURE_URL =
	// Global.getConfig("img_new_path");

	// 融资主体.
	private static WloanSubjectService wloanSubjectService = SpringContextHolder.getBean("wloanSubjectService");
	// 项目还款计划.
	private static WloanTermProjectPlanDao wloanTermProjectPlanDao = SpringContextHolder.getBean("wloanTermProjectPlanDao");
	// 借款端用户信息.
	private static CreditUserInfoDao creditUserInfoDao = SpringContextHolder.getBean("creditUserInfoDao");
	// 借款端借款申请资料清单信息.
	private static CreditVoucherDao creditVoucherDao = SpringContextHolder.getBean("creditVoucherDao");
	// 借款方融资主体.
	private static WloanSubjectDao wloanSubjectDao = SpringContextHolder.getBean("wloanSubjectDao");
	// 项目期限和利率Service.
	private static CreditMiddlemenRateService creditMiddlemenRateService = SpringContextHolder.getBean("creditMiddlemenRateService");
	// 借款申请.
	private static CreditUserApplyDao creditUserApplyDao = SpringContextHolder.getBean("creditUserApplyDao");
	// 输出流.
	private static OutputStream fos;

	/**
	 * 
	 * 方法: createAiQinCooperationFrameworkPdf <br>
	 * 描述: 创建爱亲合作框架协议PDF. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月4日 下午5:32:43
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createAiQinCooperationFrameworkPdf(CreditUserApply creditUserApply) throws Exception {

		// 借款端核心企业用户.
		CreditUserInfo creditReplaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		WloanSubject creditReplaceSubject = new WloanSubject();
		creditReplaceSubject.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> listReplace = wloanSubjectDao.findList(creditReplaceSubject);
		WloanSubject creditReplaceSubject2 = listReplace.get(0);
		// 借款端供应商用户.
		CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		WloanSubject creditSupplySubject = new WloanSubject();
		creditSupplySubject.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> listSupply = wloanSubjectDao.findList(creditSupplySubject);
		WloanSubject creditSupplySubject2 = listSupply.get(0);
		// 借款申请发票应收账款转让总金额.
		Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
		BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
		// 借款申请金额.
		String amount = creditUserApply.getAmount();
		BigDecimal amountBd = new BigDecimal(amount);

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 编号.
		map.put("serialNumber", creditUserApply.getId());
		// 签订日.
		map.put("years", pdfCreateDate.substring(0, 4));
		map.put("month", pdfCreateDate.substring(4, 6));
		map.put("day", pdfCreateDate.substring(6, 8));
		// 甲方（债权人）.
		if (null == creditSupplyUserInfo) {
			map.put("party_a", "");
			map.put("party_a_address", "");
			map.put("party_a_representative", "");
		} else {
			map.put("party_a", creditSupplyUserInfo.getEnterpriseFullName());
			map.put("party_a_address", "");
			if (creditSupplySubject2 != null) {
				map.put("party_a_representative", creditSupplySubject2.getLoanUser());
			}

		}
		// 乙方（债务人）.
		if (null == creditReplaceUserInfo) {
			map.put("party_b", "");
			map.put("party_b_address", "");
			map.put("party_b_representative", "");
		} else {
			map.put("party_b", creditReplaceUserInfo.getEnterpriseFullName());
			map.put("party_b_address", "");
			if (creditReplaceSubject2 != null) {
				map.put("party_b_representative", creditReplaceSubject2.getLoanUser());
			}

		}

		// 申请首次拟转让的应收账款总金额.
		StringBuffer invoiceTotalAmountSb = new StringBuffer();
		map.put("accountsReceivablerTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
		// 申请转让融资金额.
		StringBuffer amountSb = new StringBuffer();
		map.put("financingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());
		// 转让融资期限.
		String span = creditUserApply.getSpan();
		StringBuffer spanSb = new StringBuffer();
		map.put("span", spanSb.append(span.toString()).append("天").toString());
		Integer spanI = Integer.valueOf(span);
		if (AI_QIN_SPAN_30.equals(spanI)) {
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(AI_QIN_MANAGEMENT_FEE_RATE_30).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(AI_QIN_SERVIC_FEE_3).append("%/年").toString());
		} else if (AI_QIN_SPAN_60.equals(spanI)) {
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(AI_QIN_MANAGEMENT_FEE_RATE_60).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(AI_QIN_SERVIC_FEE_3).append("%/年").toString());
		} else if (AI_QIN_SPAN_90.equals(spanI)) {
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(AI_QIN_MANAGEMENT_FEE_RATE_90).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(AI_QIN_SERVIC_FEE_3).append("%/年").toString());
		} else if (AI_QIN_SPAN_120.equals(spanI)) {
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(AI_QIN_MANAGEMENT_FEE_RATE_120).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(AI_QIN_SERVIC_FEE_3).append("%/年").toString());
		}

		// 甲方联络及通讯方法.
		if (null == creditSupplySubject2) {
			log.info("该借款户【供应商】没有创建融资主体");
			// 地址.
			map.put("party_a_address", "");
			// 邮编.
			map.put("party_a_postcode", "");
			// 电话.
			map.put("party_a_phone", "");
			// 传真.
			map.put("party_a_fax", "");
			// 联系人.
			map.put("party_a_contacts", "");
			// 电子邮箱.
			map.put("party_a_email", "");
		} else {
			// 地址.
			map.put("party_a_address", creditSupplySubject2.getRegistAddress());
			// 邮编.
			map.put("party_a_postcode", "");
			// 电话.
			map.put("party_a_phone", creditSupplySubject2.getAgentPersonPhone());
			// 传真.
			map.put("party_a_fax", "");
			// 联系人.
			map.put("party_a_contacts", creditSupplySubject2.getAgentPersonName());
			// 电子邮箱.
			map.put("party_a_email", creditSupplySubject2.getEmail());
			// }
		}

		// 乙方联络及通讯方法.
		if (null == creditReplaceSubject2) {
			log.info("该核心企业没有创建融资主体");
			// 地址.
			map.put("party_b_address", "");
			// 邮编.
			map.put("party_b_postcode", "");
			// 电话.
			map.put("party_b_phone", "");
			// 传真.
			map.put("party_b_fax", "");
			// 联系人.
			map.put("party_b_contacts", "");
			// 电子邮箱.
			map.put("party_b_email", "");
		} else {
			// 地址.
			map.put("party_b_address", creditReplaceSubject2.getRegistAddress());
			// 邮编.
			map.put("party_b_postcode", "");
			// 电话.
			map.put("party_b_phone", creditReplaceSubject2.getAgentPersonPhone());
			// 传真.
			map.put("party_b_fax", "");
			// 联系人.
			map.put("party_b_contacts", creditReplaceSubject2.getAgentPersonName());
			// 电子邮箱.
			map.put("party_b_email", creditReplaceSubject2.getEmail());
		}

		/**
		 * 本次转让的应收账款清单列表如下：
		 */
		String title = "附件三	本次转让的应收账款清单列表如下：";
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

		return createAiQinCooperationFrameworkPdfByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createAiQinCooperationFrameworkPdfByTemplate <br>
	 * 描述: 爱亲合作框架协议与发票列表合并. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月4日 下午5:44:26
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createAiQinCooperationFrameworkPdfByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 模版文件全路径.
		// String templateFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanZhuanRangXieYi.pdf";

		// 测试
		// String linFilePath = "D:" + File.separator + "pdf"+ File.separator +
		// "AiQin_YingShouZhangKuanHeZuoKuangJiaXieYi_Temp.pdf";
		// String templateFileNamePath = "D:" + File.separator + "pdf"+
		// File.separator + "AiQin_YingShouZhangKuanHeZuoKuangJiaXieYi.pdf";
		// String newFileNamePath = "D:" + File.separator + "pdf"+
		// File.separator + DateUtils.getFileDate();
		// String tablePath = "D:" + File.separator + "pdf"+ File.separator +
		// "AiQin_YingShouZhangKuanHeZuoKuangJiaXieYi_Table.pdf";
		// 正式
		String linFilePath = LIN_FILE_PATH + "AiQin_YingShouZhangKuanHeZuoKuangJiaXieYi_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "AiQin_YingShouZhangKuanHeZuoKuangJiaXieYi.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		// 表格PDF路径.
		String tablePath = OUT_PATH + "AiQin_YingShouZhangKuanHeZuoKuangJiaXieYi_Table.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 新文件全路径.
		// String newFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();

		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// 电子签章.
		// PdfContentByte pdfContentByte = ps.getOverContent(6);
		// 甲方scaleToFit(150, 80)setAbsolutePosition(200, 340).
		// 乙方.
		// if (null == project.getBorrowerElectronicSignUrl()) { // 电子签章为Null.
		// } else if (project.getBorrowerElectronicSignUrl().equals("")) { //
		// 电子签章为空串.
		// } else {
		// Image partyX_Image = Image.getInstance(ELECTRONIC_SIGNATURE_URL +
		// project.getBorrowerElectronicSignUrl());
		// partyX_Image.scaleToFit(150, 80);
		// partyX_Image.setAbsolutePosition(200, 410);
		// pdfContentByte.addImage(partyX_Image);
		// }

		// 丙方.
		// Image partyY_Image =
		// Image.getInstance(ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL +
		// ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		// partyY_Image.scaleToFit(150, 80);
		// partyY_Image.setAbsolutePosition(200, 310);
		// pdfContentByte.addImage(partyY_Image);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("party_a", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_representative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_representative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("accountsReceivablerTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("financingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("managementFeeRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("servic_fee", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_a_postcode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_phone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_fax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_email", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_postcode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_phone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_fax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_email", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 安心投借款合同模版路径和表格数据路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 将带表格的数据合并到安心投借款合同中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createAiQinPromiseToPayBookPdf <br>
	 * 描述: 创建爱亲付款承诺书. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月4日 下午4:08:54
	 * 
	 * @param creditVouchers
	 * @return
	 * @throws Exception
	 */
	public static String createAiQinPromiseToPayBookPdf(CreditUserApply creditUserApply, List<CreditVoucher> creditVouchers) throws Exception {

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 借款端供应商用户.
		CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 供应商企业名称.
		if (null == creditSupplyUserInfo) {
			map.put("supplyEnterpriseFullName", "");
		} else {
			map.put("supplyEnterpriseFullName", creditSupplyUserInfo.getEnterpriseFullName());
		}
		/**
		 * 本次转让的应收账款清单列表如下：
		 */
		String title = "附件	本次转让的应收账款清单列表如下：";
		// rowTitle.
		String[] rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		for (CreditVoucher creditVoucher : creditVouchers) {
			String[] strings = new String[rowTitle.length];
			strings[0] = creditVoucher.getPackNo();
			strings[1] = creditVoucher.getNo();
			strings[2] = creditVoucher.getMoney();
			dataList.add(strings);
		}

		return createAiQinPromiseToPayBookPdfByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createAiQinPromiseToPayBookPdfByTemplate <br>
	 * 描述: 爱亲付款承诺书与发票列表合并. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月4日 下午4:02:44
	 * 
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createAiQinPromiseToPayBookPdfByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 测试
		// String tablePath = "D:" + File.separator + "pdf" + File.separator + "AiQinFuKuanChengNuoShu_Table.pdf";
		// String linFilePath = "D:" + File.separator + "pdf" + File.separator + "AiQinFuKuanChengNuoShu_Temp.pdf";
		// String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "AiQinFuKuanChengNuoShu.pdf";
		// String newFileNamePath = "D:" + File.separator + "pdf" + File.separator + DateUtils.getFileDate();

		// 正式
		String tablePath = OUT_PATH + "AiQinFuKuanChengNuoShu_Table.pdf";
		String linFilePath = LIN_FILE_PATH + "AiQinFuKuanChengNuoShu_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "AiQinFuKuanChengNuoShu.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("附加表格全路径 = " + tablePath);
		log.info("临时文件全路径 = " + linFilePath);
		log.info("模版文件全路径 = " + templateFileNamePath);
		log.info("生成文件全路径 = " + newFileNamePath);

		// 创建目录.
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
		fields.setFieldProperty("supplyEnterpriseFullName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);
		// 模版与表格路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 将带表格的数据合并到模版中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createPromiseToPayBookPdf <br>
	 * 描述: 创建付款承诺书PDF. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月29日 下午3:29:36
	 * 
	 * @param creditVouchers
	 * @return
	 * @throws Exception
	 */
	public static String createPromiseToPayBookPdf(List<CreditVoucher> creditVouchers) throws Exception {

		/**
		 * 本次转让的应收账款清单列表如下：
		 */
		String title = "附件	本次转让的应收账款清单列表如下：";
		// rowTitle.
		String[] rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		for (CreditVoucher creditVoucher : creditVouchers) {
			String[] strings = new String[rowTitle.length];
			strings[0] = creditVoucher.getPackNo();
			strings[1] = creditVoucher.getNo();
			strings[2] = creditVoucher.getMoney();
			dataList.add(strings);
		}

		return createPromiseToPayBookPdfByTemplate(title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createPromiseToPayBookPdfByTemplate <br>
	 * 描述: 根据付款承诺书模版创建PDF. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月29日 下午3:30:01
	 * 
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createPromiseToPayBookPdfByTemplate(String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 模版文件全路径.
		// String templateFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanZhuanRangXieYi.pdf";
		// String linFilePath = LIN_FILE_PATH + "FuKuanChengNuoShu_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "FuKuanChengNuoShu.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 新文件全路径.
		// String newFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 表格PDF路径.
		String tablePath = OUT_PATH + "FuKuanChengNuoShu_Table.pdf";

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 模版与表格路径.
		String[] files = new String[] { templateFileNamePath, tablePath };
		// 将带表格的数据合并到模版中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createCooperationFrameworkPdf <br>
	 * 描述: 创建合作框架协议PDF. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月28日 下午6:11:32
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createCooperationFrameworkPdf(CreditUserApply creditUserApply) throws Exception {

		// 借款端核心企业用户.
		CreditUserInfo creditReplaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		WloanSubject creditReplaceSubject = new WloanSubject();
		creditReplaceSubject.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> listReplace = wloanSubjectDao.findList(creditReplaceSubject);
		WloanSubject creditReplaceSubject2 = listReplace.get(0);
		// 借款端供应商用户.
		CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		WloanSubject creditSupplySubject = new WloanSubject();
		creditSupplySubject.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> listSupply = wloanSubjectDao.findList(creditSupplySubject);
		WloanSubject creditSupplySubject2 = listSupply.get(0);
		// 借款申请发票应收账款转让总金额.
		Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
		BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
		// 借款申请金额.
		String amount = creditUserApply.getAmount();
		BigDecimal amountBd = new BigDecimal(amount);

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 编号.
		map.put("serialNumber", creditUserApply.getId());
		// 签订日.
		map.put("years", pdfCreateDate.substring(0, 4));
		map.put("month", pdfCreateDate.substring(4, 6));
		map.put("day", pdfCreateDate.substring(6, 8));
		// 甲方（债权人）.
		if (null == creditSupplyUserInfo) {
			map.put("party_a", "");
			map.put("party_a_address", "");
			map.put("party_a_representative", "");
		} else {
			map.put("party_a", creditSupplyUserInfo.getEnterpriseFullName());
			map.put("party_a_address", "");
			if (creditSupplySubject2 != null) {
				map.put("party_a_representative", creditSupplySubject2.getLoanUser());
			}

		}
		// 乙方（债务人）.
		if (null == creditReplaceUserInfo) {
			map.put("party_b", "");
			map.put("party_b_address", "");
			map.put("party_b_representative", "");
		} else {
			map.put("party_b", creditReplaceUserInfo.getEnterpriseFullName());
			map.put("party_b_address", "");
			if (creditReplaceSubject2 != null) {
				map.put("party_b_representative", creditReplaceSubject2.getLoanUser());
			}

		}

		// 申请首次拟转让的应收账款总金额.
		StringBuffer invoiceTotalAmountSb = new StringBuffer();
		map.put("accountsReceivablerTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
		// 申请转让融资金额.
		StringBuffer amountSb = new StringBuffer();
		map.put("financingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());
		// 转让融资期限.
		String span = creditUserApply.getSpan();
		StringBuffer spanSb = new StringBuffer();
		map.put("span", spanSb.append(span.toString()).append("天").toString());
		Integer spanI = Integer.valueOf(span);
		if (SPAN_30.equals(spanI)) { // 30天，转让服务费：1.75%/年.
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(MANAGEMENT_FEE_RATE_30).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(SERVIC_FEE_30).append("%/年").toString());
		} else if (SPAN_90.equals(spanI)) { // 90天，转让服务费：2.00%/年.
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(MANAGEMENT_FEE_RATE_90).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(SERVIC_FEE_90).append("%/年").toString());
		} else if (SPAN_180.equals(spanI)) { // 180天，转让服务费：2.00%/年.
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(MANAGEMENT_FEE_RATE_180).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(SERVIC_FEE_180).append("%/年").toString());
		} else if (SPAN_360.equals(spanI)) { // 360天，转让服务费：3.00%/年.
			// 转让利率.
			StringBuffer managementFeeRateSb = new StringBuffer();
			map.put("managementFeeRate", managementFeeRateSb.append(MANAGEMENT_FEE_RATE_360).append("%/年").toString());
			// 转让服务费.
			StringBuffer servicFeeSb = new StringBuffer();
			map.put("servic_fee", servicFeeSb.append(SERVIC_FEE_360).append("%/年").toString());
		}

		// 甲方联络及通讯方法.
		if (null == creditSupplySubject2) {
			log.info("该借款户【供应商】没有创建融资主体");
			// 地址.
			map.put("party_a_address", "");
			// 邮编.
			map.put("party_a_postcode", "");
			// 电话.
			map.put("party_a_phone", "");
			// 传真.
			map.put("party_a_fax", "");
			// 联系人.
			map.put("party_a_contacts", "");
			// 电子邮箱.
			map.put("party_a_email", "");
		} else {
			// 地址.
			map.put("party_a_address", creditSupplySubject2.getRegistAddress());
			// 邮编.
			map.put("party_a_postcode", "");
			// 电话.
			map.put("party_a_phone", creditSupplySubject2.getAgentPersonPhone());
			// 传真.
			map.put("party_a_fax", "");
			// 联系人.
			map.put("party_a_contacts", creditSupplySubject2.getAgentPersonName());
			// 电子邮箱.
			map.put("party_a_email", creditSupplySubject2.getEmail());
			// }
		}

		// 乙方联络及通讯方法.
		if (null == creditReplaceSubject2) {
			log.info("该核心企业没有创建融资主体");
			// 地址.
			map.put("party_b_address", "");
			// 邮编.
			map.put("party_b_postcode", "");
			// 电话.
			map.put("party_b_phone", "");
			// 传真.
			map.put("party_b_fax", "");
			// 联系人.
			map.put("party_b_contacts", "");
			// 电子邮箱.
			map.put("party_b_email", "");
		} else {
			// 地址.
			map.put("party_b_address", creditReplaceSubject2.getRegistAddress());
			// 邮编.
			map.put("party_b_postcode", "");
			// 电话.
			map.put("party_b_phone", creditReplaceSubject2.getAgentPersonPhone());
			// 传真.
			map.put("party_b_fax", "");
			// 联系人.
			map.put("party_b_contacts", creditReplaceSubject2.getAgentPersonName());
			// 电子邮箱.
			map.put("party_b_email", creditReplaceSubject2.getEmail());
		}

		/**
		 * 本次转让的应收账款清单列表如下：
		 */
		String title = "附件三	本次转让的应收账款清单列表如下：";
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

		return createCooperationFrameworkPdfByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createCooperationFrameworkPdfByTemplate <br>
	 * 描述: 根据合作框架协议创建PDF. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月29日 上午10:15:29
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createCooperationFrameworkPdfByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 模版文件全路径.
		// String templateFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanZhuanRangXieYi.pdf";
		// 测试
		// String linFilePath = "D:" + File.separator + "pdf" + File.separator +
		// "YingShouZhangKuanHeZuoKuangJiaXieYi.pdf";
		// String templateFileNamePath = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanHeZuoKuangJiaXieYi.pdf";
		// String newFileNamePath = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();
		// String tablePath = "D:" + File.separator + "pdf" + File.separator +
		// "YingShouZhangKuanHeZuoKuangJiaXieYi_Table.pdf";
		// 正式
		String linFilePath = LIN_FILE_PATH + "YingShouZhangKuanHeZuoKuangJiaXieYi_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "YingShouZhangKuanHeZuoKuangJiaXieYi.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		// 表格PDF路径.
		String tablePath = OUT_PATH + "YingShouZhangKuanHeZuoKuangJiaXieYi_Table.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 新文件全路径.
		// String newFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();

		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// 电子签章.
		// PdfContentByte pdfContentByte = ps.getOverContent(6);
		// 甲方scaleToFit(150, 80)setAbsolutePosition(200, 340).
		// 乙方.
		// if (null == project.getBorrowerElectronicSignUrl()) { // 电子签章为Null.
		// } else if (project.getBorrowerElectronicSignUrl().equals("")) { //
		// 电子签章为空串.
		// } else {
		// Image partyX_Image = Image.getInstance(ELECTRONIC_SIGNATURE_URL +
		// project.getBorrowerElectronicSignUrl());
		// partyX_Image.scaleToFit(150, 80);
		// partyX_Image.setAbsolutePosition(200, 410);
		// pdfContentByte.addImage(partyX_Image);
		// }

		// 丙方.
		// Image partyY_Image =
		// Image.getInstance(ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL +
		// ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		// partyY_Image.scaleToFit(150, 80);
		// partyY_Image.setAbsolutePosition(200, 310);
		// pdfContentByte.addImage(partyY_Image);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("party_a", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_representative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_representative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("accountsReceivablerTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("financingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("managementFeeRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("servic_fee", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_a_postcode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_phone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_fax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_email", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_postcode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_phone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_fax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_email", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 安心投借款合同模版路径和表格数据路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 将带表格的数据合并到安心投借款合同中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: CreateApplicationBookPdf <br>
	 * 描述: 应收账款融资申请书. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月19日 上午9:55:24
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String CreateApplicationBookPdf(CreditUserApply creditUserApply) throws Exception {

		// 借款端核心企业用户.
		CreditUserInfo creditReplaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		// 借款端供应商用户.
		CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 借款申请发票应收账款转让总金额.
		Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
		BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
		// 借款申请金额.
		String amount = creditUserApply.getAmount();
		BigDecimal amountBd = new BigDecimal(amount);

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 第号.
		map.put("serialNumber", creditUserApply.getId());
		// 查询申请列表.
		CreditUserApply entity = new CreditUserApply();
		entity.setCreditSupplyId(creditUserApply.getCreditSupplyId());
		entity.setFinancingType(CreditUserApplyService.CREDIT_FINANCING_TYPE_1);
		entity.setBeginCreateDate(DateUtils.getDateOfString("2018-09-20"));
		List<CreditUserApply> creditUserApplyList = creditUserApplyDao.findListByFinancingType(entity);
		if (creditUserApplyList != null && creditUserApplyList.size() > 1) {
			if (creditUserApplyList.get(0) != null) {
				// 供应链融资合作框架协议编号.
				map.put("gylrzhzkjxy_id", creditUserApplyList.get(0).getId());
			}
		}
		// 供应商企业名称.
		if (null == creditSupplyUserInfo) {
			map.put("supplyEnterpriseFullName", "");
		} else {
			map.put("supplyEnterpriseFullName", creditSupplyUserInfo.getEnterpriseFullName());
			// 法定代表人或授权代表.
			map.put("supplyEnterpriseLegalPerson", creditSupplyUserInfo.getName());
		}
		// 核心企业名称.
		if (null == creditReplaceUserInfo) {
			map.put("replaceEnterpriseFullName", "");
		} else {
			map.put("replaceEnterpriseFullName", creditReplaceUserInfo.getEnterpriseFullName());
			// 法定代表人或授权代表.
			map.put("replaceEnterpriseLegalPerson", creditReplaceUserInfo.getName());
			if (creditReplaceUserInfo.getId().equals(AQ_PRIMARY_KEY)) { // 爱亲.
				// 承担比例-核心企业.
				String party_a_annual_str = creditUserApply.getShareRate();
				StringBuffer party_a_annual_sb = new StringBuffer();
				map.put("party_b_annual", party_a_annual_sb.append(party_a_annual_str).append("%").toString());
				// 平台融资费用＝服务费＋利息＋登记服务费
				// 服务费＝ （融资金额*服务费率／365）*融资期限
				// 利息＝（融资金额*融资利率／365）*融资期限
				// 登记服务费为应收账款转让登记费用（30元或60元人民币每笔，具体以中国人民银行征信中心动产融资统一登记系统的收费标准为准）
				String apply_amount = creditUserApply.getAmount(); // 申请金额.
				String apply_span = creditUserApply.getSpan(); // 申请期限.
				String apply_lender_rate = creditUserApply.getLenderRate(); // 年化利率.
				// 服务费.
				Double service_fee_d = 0D;
				// 服务费率.
				CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
				creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
				List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
				for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
					if (creditMiddlemenRate2.getSpan().equals(apply_span)) {
						String serviceRate = creditMiddlemenRate2.getServiceRate();
						service_fee_d = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(serviceRate) / 36500) * Double.parseDouble(apply_span));
					}
					if (SPAN_30.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_30.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 30天.
							StringBuffer servicFee_SB_30 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_30.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_60.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_60.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 60天.
							StringBuffer servicFee_SB_60 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_60.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_90.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_90.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 90天.
							StringBuffer servicFee_SB_90 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_90.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_120.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_120.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 120天.
							StringBuffer servicFee_SB_120 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_120.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_180.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_180.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 180天.
							StringBuffer servicFee_SB_180 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_180.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_360.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_360.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 360天.
							StringBuffer servicFee_SB_360 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_360.append(serviceRate).append("%").toString());
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
				// 承担费用-核心企业.
				StringBuffer party_a_money_sb = new StringBuffer();
				Double party_a_money_double = NumberUtils.scaleDouble(servic_money_double * (Double.parseDouble(party_a_annual_str) / 100));
				map.put("party_b_money", party_a_money_sb.append(String.valueOf(party_a_money_double)).toString());
				// 承担比例-供应商.
				StringBuffer party_b_annual_sb = new StringBuffer();
				int party_b_annual_int = 100 - Integer.parseInt(party_a_annual_str);
				map.put("party_a_annual", party_b_annual_sb.append(String.valueOf(party_b_annual_int)).append("%").toString());
				// 承担费用-供应商.
				StringBuffer party_b_money_sb = new StringBuffer();
				Double party_b_annual_double = 100D - Double.parseDouble(party_a_annual_str);
				Double party_b_money_double = NumberUtils.scaleDouble(servic_money_double * (party_b_annual_double / 100));
				map.put("party_a_money", party_b_money_sb.append(String.valueOf(party_b_money_double)).toString());
			} else { // 其它核心企业.
				// // 承担比例-核心企业.
				// String party_a_annual_str = creditUserApply.getShareRate();
				// StringBuffer party_a_annual_sb = new StringBuffer();
				// map.put("party_b_annual", party_a_annual_sb.append(party_a_annual_str).append("%").toString());
				// // 平台融资费用＝服务费＋利息＋登记服务费
				// // 服务费＝ （融资金额*服务费率／365）*融资期限
				// // 利息＝（融资金额*融资利率／365）*融资期限
				// // 登记服务费为应收账款转让登记费用（30元或60元人民币每笔，具体以中国人民银行征信中心动产融资统一登记系统的收费标准为准）
				// String apply_amount = creditUserApply.getAmount(); // 申请金额.
				String apply_span = creditUserApply.getSpan(); // 申请期限.
				// String apply_lender_rate = creditUserApply.getLenderRate(); // 年化利率.
				// 服务费.
				// Double service_fee_d = 0D;
				// 服务费率.
				CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
				creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
				List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
				for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
					// if (creditMiddlemenRate2.getSpan().equals(apply_span)) {
					// String serviceRate = creditMiddlemenRate2.getServiceRate();
					// service_fee_d = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(serviceRate) / 36500) * Double.parseDouble(apply_span));
					// }
					if (SPAN_30.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_30.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 30天.
							StringBuffer servicFee_SB_30 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_30.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_60.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_60.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 60天.
							StringBuffer servicFee_SB_60 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_60.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_90.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_90.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 90天.
							StringBuffer servicFee_SB_90 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_90.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_120.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_120.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 120天.
							StringBuffer servicFee_SB_120 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_120.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_180.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_180.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 180天.
							StringBuffer servicFee_SB_180 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_180.append(serviceRate).append("%").toString());
						}
					} else if (SPAN_360.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
						if (SPAN_360.equals(Integer.parseInt(apply_span))) { // 借款申请期限判断.
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 360天.
							StringBuffer servicFee_SB_360 = new StringBuffer();
							map.put("managementServiceFeeRate", servicFee_SB_360.append(serviceRate).append("%").toString());
						}
					}
				}
				// // 利息.
				// Double interest_double = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(apply_lender_rate) / 36500) * Double.parseDouble(apply_span));
				// // 登记服务费.
				// Double registration_fee_d = 0D;
				// int apply_span_int = Integer.valueOf(apply_span);
				// if (apply_span_int > 180) { // 大于180天，60元.
				// registration_fee_d = 60D;
				// } else {
				// registration_fee_d = 30D;
				// }
				// // 平台融资费用.
				// Double servic_money_double = NumberUtils.scaleDouble(service_fee_d + interest_double + registration_fee_d);
				// // 承担费用-核心企业.
				// StringBuffer party_a_money_sb = new StringBuffer();
				// Double party_a_money_double = NumberUtils.scaleDouble(servic_money_double * (Double.parseDouble(party_a_annual_str) / 100));
				// map.put("party_b_money", party_a_money_sb.append(String.valueOf(party_a_money_double)).toString());
				// // 承担比例-供应商.
				// StringBuffer party_b_annual_sb = new StringBuffer();
				// int party_b_annual_int = 100 - Integer.parseInt(party_a_annual_str);
				// map.put("party_a_annual", party_b_annual_sb.append(String.valueOf(party_b_annual_int)).append("%").toString());
				// // 承担费用-供应商.
				// StringBuffer party_b_money_sb = new StringBuffer();
				// Double party_b_annual_double = 100D - Double.parseDouble(party_a_annual_str);
				// Double party_b_money_double = NumberUtils.scaleDouble(servic_money_double * (party_b_annual_double / 100));
				// map.put("party_a_money", party_b_money_sb.append(String.valueOf(party_b_money_double)).toString());
			}
		}
		// 编号.
		// map.put("agreementSerialNumber", creditUserApply.getId());
		// 申请转让的应收账款总金额(借款申请发票单据列表).
		StringBuffer invoiceTotalAmountSb = new StringBuffer();
		map.put("accountsReceivablerTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
		// 申请转让融资金额.
		StringBuffer amountSb = new StringBuffer();
		map.put("financingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());
		// 转让融资期限.
		StringBuffer spanSb = new StringBuffer();
		map.put("span", spanSb.append(creditUserApply.getSpan().toString()).append("天").toString());
		// 转让利率.
		StringBuffer managementFeeRateSb = new StringBuffer();
		map.put("managementFeeRate", managementFeeRateSb.append(creditUserApply.getLenderRate()).append("%/年").toString());
		// 签订日.
		map.put("years", pdfCreateDate.substring(0, 4));
		map.put("month", pdfCreateDate.substring(4, 6));
		map.put("day", pdfCreateDate.substring(6, 8));

		/**
		 * 本次转让的应收账款清单列表如下：
		 */
		String title = "本次转让的应收账款清单列表如下：";
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

		return createApplicationBookPdfByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createApplicationBookPdfByTemplate <br>
	 * 描述: 根据借款申请书模版创建PDF文件. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月19日 上午9:54:53
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createApplicationBookPdfByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 模版文件全路径.
		// String templateFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanZhuanRangXieYi.pdf";
		// 测试
		// String linFilePath ="D:" + File.separator + "pdf" + File.separator +
		// "YingShouZhangKuanRongZiShenQinShu_Temp.pdf";
		// String templateFileNamePath = "D:" + File.separator + "pdf"+
		// File.separator + "YingShouZhangKuanRongZiShenQinShu.pdf";
		// String newFileNamePath = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();
		// String tablePath = "D:" + File.separator + "pdf" + File.separator +
		// "YingShouZhangKuanRongZiShenQinShu_Table.pdf";

		// 正式
		String tablePath = OUT_PATH + "YingShouZhangKuanRongZiShenQinShu_Table.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		String linFilePath = LIN_FILE_PATH + "YingShouZhangKuanRongZiShenQinShu_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "YingShouZhangKuanRongZiShenQinShu.pdf";

		log.info("模版文件全路径 = " + templateFileNamePath);
		// 新文件全路径.
		// String newFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();

		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// 电子签章.
		// PdfContentByte pdfContentByte = ps.getOverContent(6);
		// 甲方scaleToFit(150, 80)setAbsolutePosition(200, 340).
		// 乙方.
		// if (null == project.getBorrowerElectronicSignUrl()) { // 电子签章为Null.
		// } else if (project.getBorrowerElectronicSignUrl().equals("")) { //
		// 电子签章为空串.
		// } else {
		// Image partyX_Image = Image.getInstance(ELECTRONIC_SIGNATURE_URL +
		// project.getBorrowerElectronicSignUrl());
		// partyX_Image.scaleToFit(150, 80);
		// partyX_Image.setAbsolutePosition(200, 410);
		// pdfContentByte.addImage(partyX_Image);
		// }

		// 丙方.
		// Image partyY_Image =
		// Image.getInstance(ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL +
		// ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		// partyY_Image.scaleToFit(150, 80);
		// partyY_Image.setAbsolutePosition(200, 310);
		// pdfContentByte.addImage(partyY_Image);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("supplyEnterpriseFullName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("replaceEnterpriseFullName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("accountsReceivablerTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("financingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("managementFeeRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("managementServiceFeeRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_annual", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_money", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_annual", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_money", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 表格PDF路径.

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 安心投借款合同模版路径和表格数据路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 将带表格的数据合并到安心投借款合同中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: CreateRelievedPdf <br>
	 * 描述: 创建安心投借款合同PDF. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月12日 下午2:36:22
	 * 
	 * @param userInfo
	 * @param project
	 * @param invest
	 * @return
	 * @throws Exception
	 */
	public static String CreateRelievedPdf(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest) throws Exception {

		/**
		 * 融资主体.
		 */
		String subjectId = project.getSubjectId();// 融资主体ID.
		WloanSubject wloanSubject = wloanSubjectService.get(subjectId);

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 编号.
		map.put("serialNumber", pdfCreateDate);

		// 甲方.
		String partyA = "";
		String idCardNumber = "";
		String mobilePhone = "";
		if (userInfo != null) {
			partyA = userInfo.getRealName();
			idCardNumber = userInfo.getCertificateNo().substring(0, 6) + "********" + userInfo.getCertificateNo().substring(userInfo.getCertificateNo().length() - 4);
			mobilePhone = userInfo.getName().substring(0, 3) + "****" + userInfo.getName().substring(userInfo.getName().length() - 4);
		}

		map.put("partyA", partyA);
		map.put("idCardNumber", idCardNumber);
		map.put("mobilePhone", mobilePhone);

		// 乙方.
		map.put("partyB", wloanSubject.getCompanyName());
		// 统一社会信用代码/组织机构代码.
		if (wloanSubject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
			if (null != wloanSubject.getBusinessNo() && !"".equals(wloanSubject.getBusinessNo())) {
				map.put("organizationCodeB", wloanSubject.getBusinessNo()); // 统一社会信用代码.
				if (null != wloanSubject.getOrganNo() && !"".equals(wloanSubject.getOrganNo())) {
					map.put("organizationCodeB", wloanSubject.getBusinessNo() + "/" + wloanSubject.getOrganNo()); // 组织机构代码.
				}
			}
		} else {
			if (null != wloanSubject.getOrganNo() && !"".equals(wloanSubject.getOrganNo())) {
				map.put("organizationCodeB", wloanSubject.getOrganNo()); // 组织机构代码.
			}
		}
		map.put("registeredAddressB", project.getBorrowerResidence()); // 借款方住所.
		map.put("mobilePhoneB", wloanSubject.getLoanPhone().substring(0, 3) + "****" + userInfo.getName().substring(userInfo.getName().length() - 4)); // 电话.

		// 投资信息.
		BigDecimal investmentAmount = new BigDecimal(invest.getAmount()); // 出借金额.
		BigDecimal investmentInterest = new BigDecimal(invest.getInterest()); // 出借利息.

		// 到期日期.
		List<WloanTermProjectPlan> list = wloanTermProjectPlanDao.findProPlansByProId(project.getId());
		log.info("还款计划期数：" + list.size());
		WloanTermProjectPlan payInterest = list.get(0); // 第一期付息.
		WloanTermProjectPlan repaymentOfPrincipal = list.get(list.size() - 1); // 最后一期还款.

		// 借款条件.
		map.put("loanProjectName", project.getName()); // 借款项目名称.
		map.put("loanProjectSerialNumber", project.getSn()); // 借款项目编号.
		map.put("loanContractSerialNumber", pdfCreateDate); // 《借款服务合同》编号.
		map.put("letterOfGuaranteeSerialNumber", project.getGuaranteeSn()); // 《连带责任保证担保涵》编号.

		StringBuffer loanTotalAmountSb = new StringBuffer();
		map.put("loanTotalAmount", loanTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(investmentAmount))).append("元(大写：").append(PdfUtils.change(invest.getAmount())).append(")").toString()); // 借款总额元，￥×××元，（大写：元）.
		map.put("currency", "人民币"); // 币种.
		map.put("loanPurpose", project.getPurpose()); // 借款用途.
		map.put("loanDateTime", DateUtils.getDate(project.getLoanDate(), "yyyy年MM月dd日")); // 借款日期.

		StringBuffer loanSpanSb = new StringBuffer();
		map.put("loanSpan", loanSpanSb.append(project.getSpan().toString()).append("天").toString()); // 借款期限.
		map.put("repaymentOfPrincipalDateTime", DateUtils.getDate(repaymentOfPrincipal.getRepaymentDate(), "yyyy年MM月dd日")); // 还本日期.
		map.put("payInterestDateTime", DateUtils.getDate(payInterest.getRepaymentDate(), "yyyy年MM月dd日")); // 付息日期.

		StringBuffer annualizedRatesSb = new StringBuffer();
		map.put("annualizedRates", annualizedRatesSb.append(project.getAnnualRate().toString()).append("%(365天/年)").toString()); // 年利率.
		map.put("interestTotalAmount", fmtMicrometer(formatToString(investmentInterest))); // 利息总额.
		map.put("repaymentOfPrincipalAndInterestType", "放款计息，按月付息，到期还本"); // 利息总额.

		// 签订日.
		String investmentDate = DateUtils.getDate(invest.getBeginDate(), "yyyyMMddHHMMss");
		map.put("yearsW", investmentDate.substring(0, 4));
		map.put("monthW", investmentDate.substring(4, 6));
		map.put("dayW", investmentDate.substring(6, 8));

		// 甲方（出借人）.
		map.put("partyW", partyA);

		// 乙方（借款人）.
		map.put("partyX", wloanSubject.getCompanyName());

		// 出借人本金利息表.
		String title = "出借人本金利息表";
		// rowTitle.
		String[] rowTitle = new String[] { "编号", "出借人", "身份证号", "出借金额", "利息总额" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = new String[rowTitle.length];
		strings[0] = pdfCreateDate;
		strings[1] = partyA;
		strings[2] = userInfo.getCertificateNo().substring(0, 6) + "********" + userInfo.getCertificateNo().substring(userInfo.getCertificateNo().length() - 4);
		strings[3] = fmtMicrometer(formatToString(investmentAmount));
		strings[4] = fmtMicrometer(formatToString(investmentInterest));
		dataList.add(strings);

		return createRelievedPdfByTemplate(map, project, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createRelievedPdfByTemplate <br>
	 * 描述: 创建安心投合同根据模版输出PDF文件路径. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年3月12日 下午2:38:11
	 * 
	 * @param map
	 * @param project
	 * @param dataList
	 *            出借人本金利息表数据
	 * @param title
	 *            出借人本金利息表
	 * @param rowTitle
	 *            表格列名称
	 * @return
	 * @throws Exception
	 */
	public static String createRelievedPdfByTemplate(Map<String, String> map, WloanTermProject project, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 模版文件全路径.
		// String templateFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanZhuanRangXieYi.pdf";

		// 测试
		// String linFilePath = "D:" + File.separator + "pdf" + File.separator +
		// "ZhongTouMoGenJieKuanHeTong_Temp.pdf";
		// String templateFileNamePath = "D:" + File.separator + "pdf" +
		// File.separator + "ZhongTouMoGenJieKuanHeTong_ZhongGao.pdf";

		// 正式
		String linFilePath = LIN_FILE_PATH + "ZhongTouMoGenJieKuanHeTong_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "ZhongTouMoGenJieKuanHeTong_ZhongGao.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 新文件全路径.
		// String newFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();
		// String newFileNamePath = "D:" + File.separator + "pdf"+
		// File.separator + DateUtils.getFileDate();
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// 电子签章.
		// PdfContentByte pdfContentByte = ps.getOverContent(8);
		// // 甲方scaleToFit(150, 80)setAbsolutePosition(200, 340).
		// // 乙方.
		// if (null == project.getBorrowerElectronicSignUrl()) { // 电子签章为Null.
		// } else if (project.getBorrowerElectronicSignUrl().equals("")) { //
		// 电子签章为空串.
		// } else {
		// Image partyX_Image = Image.getInstance(ELECTRONIC_SIGNATURE_URL +
		// project.getBorrowerElectronicSignUrl());
		// partyX_Image.scaleToFit(150, 80);
		// partyX_Image.setAbsolutePosition(200, 185);
		// pdfContentByte.addImage(partyX_Image);
		// }
		//
		// // 丙方.
		// Image partyY_Image =
		// Image.getInstance(ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL +
		// ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		// partyY_Image.scaleToFit(150, 80);
		// partyY_Image.setAbsolutePosition(200, 85);
		// pdfContentByte.addImage(partyY_Image);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();

		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("loanProjectName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanProjectSerialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanContractSerialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("letterOfGuaranteeSerialNumber", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("currency", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanPurpose", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanDateTime", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanSpan", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("repaymentOfPrincipalDateTime", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("payInterestDateTime", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("annualizedRates", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("interestTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("repaymentOfPrincipalAndInterestType", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 表格PDF路径.
		// String tablePath = "D:" + File.separator + "pdf"+ File.separator +
		// "ZhongTouMoGenJieKuanHeTong_Table.pdf";
		String tablePath = OUT_PATH + "ZhongTouMoGenJieKuanHeTong_Table.pdf";

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 安心投借款合同模版路径和表格数据路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 将带表格的数据合并到安心投借款合同中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: CreateSupplyChainPdf <br>
	 * 描述: 创建供应链应收账款转让协议PDF. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年1月1日 下午3:38:36
	 * 
	 * @param userInfo
	 * @param project
	 * @param invest
	 * @return
	 * @throws Exception
	 */
	public static String CreateSupplyChainPdf(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest) throws Exception {

		log.info("======创建供应链应收账款转让协议PDF===========");
		/**
		 * 融资主体.
		 */
		String subjectId = project.getSubjectId();// 融资主体ID.
		WloanSubject wloanSubject = wloanSubjectService.get(subjectId);

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();
		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 编号.
		map.put("serialNumber", pdfCreateDate);

		// 甲方.
		map.put("partyA", userInfo.getRealName());
		map.put("idCardNumber", userInfo.getCertificateNo());
		map.put("mobilePhone", userInfo.getName());

		// 投资信息.
		BigDecimal investmentAmount = new BigDecimal(invest.getAmount());
		map.put("investmentAmount", fmtMicrometer(formatToString(investmentAmount)));
		map.put("investmentHorizon", project.getSpan().toString());
		map.put("investmentDate", DateUtils.getDate(invest.getBeginDate(), "yyyy/MM/dd HH:mm:ss"));

		// 乙方.
		map.put("partyB", wloanSubject.getCompanyName());
		// 统一社会信用代码/组织机构代码.
		if (wloanSubject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
			if (null != wloanSubject.getBusinessNo() && !"".equals(wloanSubject.getBusinessNo())) {
				map.put("organizationCodeB", wloanSubject.getBusinessNo()); // 统一社会信用代码.
				if (null != wloanSubject.getOrganNo() && !"".equals(wloanSubject.getOrganNo())) {
					map.put("organizationCodeB", wloanSubject.getBusinessNo() + "/" + wloanSubject.getOrganNo()); // 组织机构代码.
				}
			}
		} else {
			if (null != wloanSubject.getOrganNo() && !"".equals(wloanSubject.getOrganNo())) {
				map.put("organizationCodeB", wloanSubject.getOrganNo()); // 组织机构代码.
			}
		}
		map.put("registeredAddressB", project.getBorrowerResidence()); // 借款方住所.

		// 丁方.
		WloanSubject replaceRepayInfo = null;
		if (project.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 代偿还款.
			String replaceRepayId = project.getReplaceRepayId(); // 代偿人ID.
			WloanSubject entity = new WloanSubject();
			entity.setLoanApplyId(replaceRepayId);
			List<WloanSubject> subjects = wloanSubjectService.findList(entity);
			if (subjects != null && subjects.size() > 0) {
				replaceRepayInfo = subjects.get(0);
				if (null == replaceRepayInfo) {
					map.put("partyC", "");
					map.put("organizationCodeC", "");
					map.put("registeredAddressC", "");
				} else {
					map.put("partyC", replaceRepayInfo.getCompanyName());
					// 统一社会信用代码/组织机构代码.
					if (replaceRepayInfo.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
						if (null != replaceRepayInfo.getBusinessNo() && !"".equals(replaceRepayInfo.getBusinessNo())) {
							map.put("organizationCodeC", replaceRepayInfo.getBusinessNo()); // 统一社会信用代码.
							if (null != replaceRepayInfo.getOrganNo() && !"".equals(replaceRepayInfo.getOrganNo())) {
								map.put("organizationCodeC", replaceRepayInfo.getBusinessNo() + "/" + replaceRepayInfo.getOrganNo()); // 组织机构代码.
							}
						}
					} else {
						if (null != replaceRepayInfo.getOrganNo() && !"".equals(replaceRepayInfo.getOrganNo())) {
							map.put("organizationCodeC", replaceRepayInfo.getOrganNo()); // 组织机构代码.
						}
					}
					map.put("registeredAddressC", project.getReplaceRepayResidence()); // 代偿方住所.
				}
			} else {
				map.put("partyC", "");
				map.put("organizationCodeC", "");
				map.put("registeredAddressC", "");
			}
		}

		// 转让份额/数额.
		map.put("transferAmountA", fmtMicrometer(formatToString(investmentAmount)));
		map.put("transferAmountB", fmtMicrometer(formatToString(investmentAmount)));

		// 转让日期.
		String investmentDate = DateUtils.getDate(invest.getBeginDate(), "yyyyMMddHHMMss");
		map.put("yearsA", investmentDate.substring(0, 4));
		map.put("monthA", investmentDate.substring(4, 6));
		map.put("dayA", investmentDate.substring(6, 8));

		// 到期日期.
		List<WloanTermProjectPlan> list = wloanTermProjectPlanDao.findProPlansByProId(project.getId());
		log.info("还款计划期数：" + list.size());
		WloanTermProjectPlan entity = list.get(list.size() - 1); // 最后一期还款.
		String repaymentDate = DateUtils.getDate(entity.getRepaymentDate(), "yyyyMMddHHMMss"); // 还款截至日期.
		map.put("yearsB", repaymentDate.substring(0, 4));
		map.put("monthB", repaymentDate.substring(4, 6));
		map.put("dayB", repaymentDate.substring(6, 8));

		// 投资期限.
		map.put("span", project.getSpan().toString());

		// 甲方（个人）签字.
		map.put("partyW", userInfo.getRealName());
		map.put("yearsW", investmentDate.substring(0, 4));
		map.put("monthW", investmentDate.substring(4, 6));
		map.put("dayW", investmentDate.substring(6, 8));

		// 乙方（企业）签字.
		map.put("partyX", wloanSubject.getCompanyName());
		map.put("yearsX", investmentDate.substring(0, 4));
		map.put("monthX", investmentDate.substring(4, 6));
		map.put("dayX", investmentDate.substring(6, 8));

		// 丙方（企业）签字.
		map.put("partyY", "中投摩根信息技术（北京）有限责任公司");
		map.put("yearsY", investmentDate.substring(0, 4));
		map.put("monthY", investmentDate.substring(4, 6));
		map.put("dayY", investmentDate.substring(6, 8));

		// 丁方（企业）签字.
		if (null != replaceRepayInfo) { // 非代偿还款.
			map.put("partyZ", replaceRepayInfo.getCompanyName());
		} else {
			map.put("partyZ", null);
		}
		map.put("yearsZ", investmentDate.substring(0, 4));
		map.put("monthZ", investmentDate.substring(4, 6));
		map.put("dayZ", investmentDate.substring(6, 8));
		log.info("创建供应链应收账款转让协议PDF====参数封装完成====>>>>");
		return createPdfByTemplate(map, project);
	}

	public static String createPdfByTemplate(Map<String, String> map, WloanTermProject project) throws Exception {

		log.info("PDF生成中........");
		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 模版文件全路径.
		// String templateFileNamePath = "D:" + File.separator + "pdf" +
		// File.separator + "YingShouZhangKuanZhuanRangXieYi_ZhongGao.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "YingShouZhangKuanZhuanRangXieYi_ZhongGao.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 新文件全路径.
		// String newFileNamePathLocal = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();
		// String newFileNamePath = "D:" + File.separator + "pdf" +
		// File.separator + DateUtils.getFileDate();
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("新文件全路径 = " + newFileNamePath);
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));

		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// // 电子签章.
		// PdfContentByte pdfContentByte = ps.getOverContent(8);
		// // 甲方scaleToFit(150, 80)setAbsolutePosition(200, 340).
		// // 乙方.
		// if (null == project.getBorrowerElectronicSignUrl()) { //
		// 出让方，电子签章为Null.
		// } else if (project.getBorrowerElectronicSignUrl().equals("")) { //
		// 出让方，电子签章为空串.
		// } else {
		// Image partyX_Image = Image.getInstance(ELECTRONIC_SIGNATURE_URL +
		// project.getBorrowerElectronicSignUrl());
		// partyX_Image.scaleToFit(150, 80);
		// partyX_Image.setAbsolutePosition(200, 260);
		// pdfContentByte.addImage(partyX_Image);
		// }
		//
		// // 丙方.
		// Image partyY_Image =
		// Image.getInstance(ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL +
		// ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		// partyY_Image.scaleToFit(150, 80);
		// partyY_Image.setAbsolutePosition(200, 180);
		// pdfContentByte.addImage(partyY_Image);
		// // 丁方.
		// if (null == project.getReplaceRepayElectronicSignUrl()) { //
		// 债务方，电子签章为Null.
		// } else if (project.getReplaceRepayElectronicSignUrl().equals("")) {
		// // 债务方，电子签章为空串.
		// } else {
		// Image partyZ_Image = Image.getInstance(ELECTRONIC_SIGNATURE_URL +
		// project.getReplaceRepayElectronicSignUrl());
		// partyZ_Image.scaleToFit(150, 80);
		// partyZ_Image.setAbsolutePosition(200, 100);
		// pdfContentByte.addImage(partyZ_Image);
		// }

		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(newFileNamePath + File.separator + newFileName));
		fos.write(bos.toByteArray());
		fos.close();
		log.info("<<<====PDF生成结束" + newFileNamePath + File.separator + newFileName);
		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: fillData <br>
	 * 描述: 为PDF文件表单域赋值. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月5日 下午4:48:40
	 * 
	 * @param fields
	 * @param data
	 * @throws Exception
	 */
	public static void fillData(AcroFields fields, Map<String, String> data) throws Exception {

		for (String key : data.keySet()) {
			String value = data.get(key);
			fields.setField(key, value);
		}
	}

	/**
	 * 
	 * 方法: formatToString <br>
	 * 描述: BigDecimal转换为String类型，并进行去零操作. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月5日 下午2:28:36
	 * 
	 * @param result
	 * @return
	 */
	public static String formatToString(BigDecimal result) {

		if (result == null) {
			return "";
		} else {
			return result.stripTrailingZeros().toPlainString();
		}
	}

	/**
	 * 
	 * 方法: fmtMicrometer <br>
	 * 描述: 格式化数字为千分位显示. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月5日 下午2:26:43
	 * 
	 * @param text
	 * @return
	 */
	public static String fmtMicrometer(String text) {

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

}
