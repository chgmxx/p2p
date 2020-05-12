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
import com.power.platform.credit.dao.creditOrder.CreditOrderDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.voucher.CreditVoucherDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.creditOrder.CreditOrder;
import com.power.platform.credit.entity.middlemen.CreditMiddlemenRate;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.middlemen.CreditMiddlemenRateService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermProjectPlan;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: AiQinPdfContract <br>
 * 描述: FinancingFramework(融资框架)PDF协议合同. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年5月25日 上午9:37:05
 */
public class AiQinPdfContract {

	/**
	 * 日志.
	 */
	private static final Logger log = Logger.getLogger(AiQinPdfContract.class);

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
	 * 核心企业：美特好.
	 */
	public static final String MTH_PRIMARY_KEY = "6809349449100994498";
	/**
	 * 核心企业：妈妈宝贝.
	 */
	public static final String MMBB_PRIMARY_KEY = "8109132022784559441";
	/**
	 * 核心企业：爱亲.
	 */
	public static final String AQ_PRIMARY_KEY = "5685145015583919274";

	/**
	 * 30天，7.00%（年化）.
	 */
	public static final String MANAGEMENT_FEE_RATE_30 = "7.00";
	/**
	 * 60天，7.50%（年化）.
	 */
	public static final String MANAGEMENT_FEE_RATE_60 = "7.50";
	/**
	 * 90天，8.00%（年化）.
	 */
	public static final String MANAGEMENT_FEE_RATE_90 = "8.00";
	/**
	 * 120天，9.00%（年化）.
	 */
	public static final String MANAGEMENT_FEE_RATE_120 = "9.00";

	/**
	 * 爱亲通用，服务费率3.00%（年化）.
	 */
	public static final String AQ_SERVIC_FEE_3 = "3.00";

	/**
	 * 美特好，30天，服务费率1.75%（年化）.
	 */
	public static final String MTH_SERVIC_FEE_30 = "1.75";
	/**
	 * 美特好，90天，服务费率2.00%（年化）.
	 */
	public static final String MTH_SERVIC_FEE_90 = "2.00";
	/**
	 * 美特好，180天，服务费率2.00%（年化）.
	 */
	public static final String MTH_SERVIC_FEE_180 = "2.00";
	/**
	 * 美特好，360天，服务费率3.00%（年化）.
	 */
	public static final String MTH_SERVIC_FEE_360 = "3.00";

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
	 * 爱亲融资类型：1-应收账款转让.
	 */
	public static final String AIQIN_FINANCING_TYPE_1 = "1";
	/**
	 * 爱亲融资类型：2-订单融资.
	 */
	public static final String AIQIN_FINANCING_TYPE_2 = "2";

	/**
	 * 罚息利率：国标上限24%.
	 */
	public static final Double PENALTY_INTEREST_RATES = 24D;

	// 融资主体.
	private static WloanSubjectService wloanSubjectService = SpringContextHolder.getBean("wloanSubjectService");
	// 项目还款计划.
	private static WloanTermProjectPlanDao wloanTermProjectPlanDao = SpringContextHolder.getBean("wloanTermProjectPlanDao");
	// 客户还款计划.
	private static WloanTermUserPlanDao wloanTermUserPlanDao = SpringContextHolder.getBean("wloanTermUserPlanDao");
	// 借款端用户帐号.
	private static CreditUserInfoDao creditUserInfoDao = SpringContextHolder.getBean("creditUserInfoDao");
	// 发票.
	private static CreditVoucherDao creditVoucherDao = SpringContextHolder.getBean("creditVoucherDao");
	// 订单.
	private static CreditOrderDao creditOrderDao = SpringContextHolder.getBean("creditOrderDao");
	// 融资主体.
	private static WloanSubjectDao wloanSubjectDao = SpringContextHolder.getBean("wloanSubjectDao");
	// 项目期限和利率Service.
	private static CreditMiddlemenRateService creditMiddlemenRateService = SpringContextHolder.getBean("creditMiddlemenRateService");
	// 借款申请.
	private static CreditUserApplyDao creditUserApplyDao = SpringContextHolder.getBean("creditUserApplyDao");
	// 输出流.
	private static OutputStream fos;

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

	/**
	 * 
	 * 方法: createOrderFinancingPdf <br>
	 * 描述: 创建出借订单融资协议. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月29日 上午11:08:49
	 * 
	 * @param userInfo
	 * @param project
	 * @param invest
	 * @return
	 * @throws Exception
	 */
	public static String createOrderFinancingPdf(UserInfo userInfo, WloanTermProject project, WloanTermInvest invest) throws Exception {

		log.info("创建出借订单融资协议PDF开始=======>>>>>>>>>>");
		
		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");

		// 编号.
		map.put("serialNumber", pdfCreateDate);

		// 年.
		map.put("years", pdfCreateDate.substring(0, 4));
		// 月.
		map.put("month", pdfCreateDate.substring(4, 6));
		// 日.
		map.put("day", pdfCreateDate.substring(6, 8));

		// 甲方（出借人）.
		map.put("party_a", userInfo.getName().substring(0, 3) + "****" + userInfo.getName().substring(userInfo.getName().length() - 4)); // 出借人手机.
		// 出借人姓名.
		map.put("party_a_name", userInfo.getRealName());
		// 出借人证件号码.
		map.put("party_a_cert_no", userInfo.getCertificateNo().substring(0, 6) + "********" + userInfo.getCertificateNo().substring(userInfo.getCertificateNo().length() - 4));
		// 甲方（出借人）联系地址和联系方式.
		map.put("party_a_address", ""); // 地址.
		map.put("party_a_postcode", ""); // 邮编.
		map.put("party_a_phone", userInfo.getName()); // 电话.
		map.put("party_a_fax", ""); // 传真.
		map.put("party_a_contacts", ""); // 联系人.
		map.put("party_a_email", userInfo.getEmail()); // 电子邮箱.

		// 融资主体.
		String subjectId = project.getSubjectId();
		WloanSubject wloanSubject = wloanSubjectService.get(subjectId);
		if (wloanSubject != null) {
			// 乙方（借款人/订单卖方）.
			map.put("party_b", wloanSubject.getCompanyName());
			// 统一社会信用代码/组织机构代码.
			if (wloanSubject.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
				if (null != wloanSubject.getBusinessNo() && !"".equals(wloanSubject.getBusinessNo())) {
					map.put("party_b_cert_no", wloanSubject.getBusinessNo()); // 统一社会信用代码.
					if (null != wloanSubject.getOrganNo() && !"".equals(wloanSubject.getOrganNo())) {
						map.put("party_b_cert_no", wloanSubject.getBusinessNo() + "/" + wloanSubject.getOrganNo()); // 组织机构代码.
					}
				}
			} else {
				if (null != wloanSubject.getOrganNo() && !"".equals(wloanSubject.getOrganNo())) {
					map.put("party_b_cert_no", wloanSubject.getOrganNo()); // 组织机构代码.
				}
			}
			// 乙方（借款人/订单卖方）联系地址和联系方式.
			map.put("party_b_address", wloanSubject.getRegistAddress()); // 地址.
			map.put("party_b_postcode", ""); // 邮编.
			map.put("party_b_phone", wloanSubject.getAgentPersonPhone()); // 电话.
			map.put("party_b_fax", ""); // 传真.
			map.put("party_b_contacts", wloanSubject.getAgentPersonName()); // 联系人.
			map.put("party_b_email", wloanSubject.getEmail()); // 电子邮箱.
		}

		// 丙方（订单买方）.
		WloanSubject replaceRepayInfo = null;
		if (project.getIsReplaceRepay().equals(WloanTermProject.IS_REPLACE_REPAY_1)) { // 代偿还款.
			String replaceRepayId = project.getReplaceRepayId(); // 代偿人ID.
			WloanSubject entity = new WloanSubject();
			entity.setLoanApplyId(replaceRepayId);
			List<WloanSubject> subjects = wloanSubjectService.findList(entity);
			if (subjects != null && subjects.size() > 0) {
				replaceRepayInfo = subjects.get(0);
				if (replaceRepayInfo != null) {
					map.put("party_c", replaceRepayInfo.getCompanyName());
					// 统一社会信用代码/组织机构代码.
					if (replaceRepayInfo.getBusinessLicenseType().equals(WloanSubject.BUSINESS_LICENSE_TYPE_USCC)) {
						if (null != replaceRepayInfo.getBusinessNo() && !"".equals(replaceRepayInfo.getBusinessNo())) {
							map.put("party_c_cert_no", replaceRepayInfo.getBusinessNo()); // 统一社会信用代码.
							if (null != replaceRepayInfo.getOrganNo() && !"".equals(replaceRepayInfo.getOrganNo())) {
								map.put("party_c_cert_no", replaceRepayInfo.getBusinessNo() + "/" + replaceRepayInfo.getOrganNo()); // 组织机构代码.
							}
						}
					} else {
						if (null != replaceRepayInfo.getOrganNo() && !"".equals(replaceRepayInfo.getOrganNo())) {
							map.put("party_c_cert_no", replaceRepayInfo.getOrganNo()); // 组织机构代码.
						}
					}
					// 丙方（订单买方）联系地址和联系方式.
					map.put("party_c_address", replaceRepayInfo.getRegistAddress()); // 地址.
					map.put("party_c_postcode", ""); // 邮编.
					map.put("party_c_phone", replaceRepayInfo.getAgentPersonPhone()); // 电话.
					map.put("party_c_fax", ""); // 传真.
					map.put("party_c_contacts", replaceRepayInfo.getAgentPersonName()); // 联系人.
					map.put("party_c_email", replaceRepayInfo.getEmail()); // 电子邮箱.
				}
			}
		}

		// 丁方.
		map.put("party_d", "中投摩根信息技术（北京）有限责任公司");

		// 借款明细.
		StringBuffer loanTotalAmountSb = new StringBuffer();
		BigDecimal projectAmount = new BigDecimal(project.getAmount());
		map.put("loan_total_amount", loanTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(projectAmount))).append("元(大写：").append(PdfUtils.change(project.getAmount())).append(")").toString()); // 借款本金总金额.
		map.put("span", project.getSpan().toString() + "天"); // 借款期限.
		map.put("rate", project.getAnnualRate().toString() + "%"); // 借款年化利率.
		map.put("purpose", project.getPurpose()); // 借款用途.
		map.put("loan_date", DateUtils.getDate(project.getLoanDate(), "yyyy年MM月dd日")); // 放款日/起息日.

		List<WloanTermProjectPlan> projectPlanlist = wloanTermProjectPlanDao.findProPlansByProId(project.getId());
		WloanTermProjectPlan lastProjectPlan = projectPlanlist.get(projectPlanlist.size() - 1); // 最后一期还款.
		map.put("end_date", DateUtils.getDate(lastProjectPlan.getRepaymentDate(), "yyyy年MM月dd日")); // 到期日.
		if (project.getRepayType().equals(WloanTermProjectService.REPAY_TYPE_1)) { // 一次性还本付息.
			map.put("repay_type", "一次性还本付息"); // 还款方式.
		} else if (project.getRepayType().equals(WloanTermProjectService.REPAY_TYPE_2)) { // 按月付息，到期还本.
			map.put("repay_type", "按月付息到期还本"); // 还款方式.
		}

		// 客户还款计划.
		WloanTermUserPlan entity = new WloanTermUserPlan();
		entity.setWloanTermInvest(invest);
		List<WloanTermUserPlan> userPlans = wloanTermUserPlanDao.findList(entity);
		// 还款明细.
		String title = "附件一	还款明细\n注：上表中金额按四舍五入规则精确到0.01元，最后一期的还款金额可能与前面的略有不同。";
		// rowTitle.
		String row_title_1 = "还款日期（共[" + userPlans.size() + "]期）";
		String[] rowTitle = new String[] { row_title_1, "应还本息/人民币（元）", "应还利息/人民币（元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		for (int i = 0; i < userPlans.size(); i++) {
			WloanTermUserPlan userPlan = userPlans.get(i);
			String[] strs = new String[rowTitle.length];
			strs[0] = "第[" + (i + 1) + "]期";
			if (userPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1)) { // 还本付息.
				BigDecimal interest_B_D = new BigDecimal(userPlan.getInterest());
				StringBuffer interest_S_B = new StringBuffer();
				strs[1] = interest_S_B.append("￥").append(fmtMicrometer(formatToString(interest_B_D))).append("元(大写：").append(PdfUtils.change(userPlan.getInterest())).append(")").toString();
			} else if (userPlan.getPrincipal().equals(WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2)) { // 付息.
				BigDecimal interest_B_D = new BigDecimal(userPlan.getInterest());
				StringBuffer interest_S_B = new StringBuffer();
				strs[2] = interest_S_B.append("￥").append(fmtMicrometer(formatToString(interest_B_D))).append("元(大写：").append(PdfUtils.change(userPlan.getInterest())).append(")").toString();
			}
			dataList.add(strs);
		}
        log.info("============创建出借订单融资协议所需参数完成===========");
		return mergeOrderFinancingByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: mergeOrderFinancingByTemplate <br>
	 * 描述: 合并订单融资协议. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月29日 上午11:09:10
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String mergeOrderFinancingByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		log.info("订单融资PDF生成开始=============>>>>>");
		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 临时文件全路径.
		String linFilePath = LIN_FILE_PATH + "AiQin_DingDanRongZiXieYi_Temp.pdf";
		log.info("临时文件全路径 = " + linFilePath);
		// 模版文件全路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "AiQin_DingDanRongZiXieYi.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 生产合同全路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("生产合同全路径 = " + newFileNamePath);
		// 创建生产合同目录.
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
		fields.setFieldProperty("party_a_name", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_b", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_c", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_c_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_c_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("loan_total_amount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("purpose", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loan_date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("end_date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("repay_type", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_d", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);

		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 表格路径.
		String tablePath = OUT_PATH + "AiQin_DingDanRongZiXieYi_Table.pdf";
		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);
		// 合同临时路径和表格路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 合并临时文件和表格文件.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}
        log.info("<<<<======订单融资PDF生成结束=========="+newFileNamePath + File.separator + newFileName);
		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createFinancingFrameworkPdf <br>
	 * 描述: 创建融资框架协议. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月28日 上午10:29:43
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createFinancingFrameworkPdf(CreditUserApply creditUserApply) throws Exception {

		// 核心企业.
		CreditUserInfo creditReplaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		// 核心企业融资主体.
		WloanSubject creditReplaceSubject = new WloanSubject();
		creditReplaceSubject.setLoanApplyId(creditUserApply.getReplaceUserId());
		List<WloanSubject> listReplace = wloanSubjectDao.findList(creditReplaceSubject);
		WloanSubject creditReplaceSubject2 = listReplace.get(0);

		// 供应商.
		CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
		// 供应商融资主体.
		WloanSubject creditSupplySubject = new WloanSubject();
		creditSupplySubject.setLoanApplyId(creditUserApply.getCreditSupplyId());
		List<WloanSubject> listSupply = wloanSubjectDao.findList(creditSupplySubject);
		WloanSubject creditSupplySubject2 = listSupply.get(0);

		// 借款申请金额.
		String amount = creditUserApply.getAmount();
		BigDecimal amountBd = new BigDecimal(amount);

		// 转让融资期限.
		String span = creditUserApply.getSpan();

		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();

		// 编号.
		map.put("serialNumber", creditUserApply.getId());

		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");

		// 签订日.
		map.put("years", pdfCreateDate.substring(0, 4));
		map.put("month", pdfCreateDate.substring(4, 6));
		map.put("day", pdfCreateDate.substring(6, 8));

		// 甲方（供应商）.
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

		// 乙方（采购商）.
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

		if (null == creditSupplySubject2) { // 甲方联络及通讯方法.
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

		// title.
		String title = "";
		// rowTitle.
		String[] rowTitle = null;
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();

		if (null == creditReplaceSubject2) { // 乙方联络及通讯方法.
			log.info("该代偿户【采购商】没有创建融资主体");
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

		if (creditReplaceUserInfo != null) { // 核心企业帐号信息.
			if (creditReplaceUserInfo.getId().equals(AQ_PRIMARY_KEY)) { // 爱亲.
				if (creditUserApply != null) {
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
							// 罚息利率，penalty_interest_rates_day，penalty_interest_rates_month.
							// 超过30天，罚息利率/日.
//							Double penalty_interest_rates_month = (PENALTY_INTEREST_RATES - Double.valueOf(apply_lender_rate) - Double.parseDouble(serviceRate)) / 365;
//							String penalty_interest_rates_month_str = String.valueOf(penalty_interest_rates_month);
//							if(penalty_interest_rates_month_str.length() >= 4){
//								penalty_interest_rates_month_str = penalty_interest_rates_month_str.substring(0, 4); // 保留两位.
//							}
//							Double penalty_interest_rates_month_d = Double.valueOf(penalty_interest_rates_month_str) + 0.01D; // 进一.
//							StringBuffer penalty_interest_rates_month_SB = new StringBuffer();
//							map.put("penalty_interest_rates_month", penalty_interest_rates_month_SB.append(String.valueOf(penalty_interest_rates_month_d)).append("%日").toString());
//							// 1-30天，罚息利率/日.
//							Double penalty_interest_rates_day = penalty_interest_rates_month_d / 2D;
//							String penalty_interest_rates_day_str = String.valueOf(penalty_interest_rates_day);
//							if(penalty_interest_rates_day_str.length() >= 4){
//								penalty_interest_rates_day_str = penalty_interest_rates_day_str.substring(0, 4); // 保留两位.
//							}
//							Double penalty_interest_rates_day_d = Double.valueOf(penalty_interest_rates_day_str) + 0.01D; // 进一.
//							StringBuffer penalty_interest_rates_day_SB = new StringBuffer();
//							map.put("penalty_interest_rates_day", penalty_interest_rates_day_SB.append(String.valueOf(penalty_interest_rates_day_d)).append("%日").toString());
						}
						if (SPAN_30.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 30天.
							StringBuffer servicFee_SB_30 = new StringBuffer();
							map.put("servic_fee_30", servicFee_SB_30.append(serviceRate).append("%").toString());
						} else if (SPAN_60.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 60天.
							StringBuffer servicFee_SB_60 = new StringBuffer();
							map.put("servic_fee_60", servicFee_SB_60.append(serviceRate).append("%").toString());
						} else if (SPAN_90.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 90天.
							StringBuffer servicFee_SB_90 = new StringBuffer();
							map.put("servic_fee_90", servicFee_SB_90.append(serviceRate).append("%").toString());
						} else if (SPAN_120.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 120天.
							StringBuffer servicFee_SB_120 = new StringBuffer();
							map.put("servic_fee_120", servicFee_SB_120.append(serviceRate).append("%").toString());
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
					// 甲方承担.
					StringBuffer party_a_money_sb = new StringBuffer();
					Double party_a_money_double = NumberUtils.scaleDouble(servic_money_double * (Double.parseDouble(party_a_annual_str) / 100));
					map.put("party_b_money", party_a_money_sb.append(String.valueOf(party_a_money_double)).toString());

					// 承担比例-供应商.
					StringBuffer party_b_annual_sb = new StringBuffer();
					int party_b_annual_int = 100 - Integer.parseInt(party_a_annual_str);
					map.put("party_a_annual", party_b_annual_sb.append(String.valueOf(party_b_annual_int)).append("%").toString());
					// 供应商承担费用.
					StringBuffer party_b_money_sb = new StringBuffer();
					Double party_b_annual_double = 100D - Double.parseDouble(party_a_annual_str);
					Double party_b_money_double = NumberUtils.scaleDouble(servic_money_double * (party_b_annual_double / 100));
					map.put("party_a_money", party_b_money_sb.append(String.valueOf(party_b_money_double)).toString());

					if (creditUserApply.getFinancingType().equals(AIQIN_FINANCING_TYPE_1)) {
						// 借款申请发票应收账款转让总金额.
						Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
						if (invoiceTotalAmount != null) {
							BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
							StringBuffer invoiceTotalAmountSb = new StringBuffer();
							map.put("yszk_financingTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
						}

						// 申请转让融资金额.
						StringBuffer amountSb = new StringBuffer();
						map.put("yszk_applicationFinancingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());

						// 期限.
						StringBuffer spanSb = new StringBuffer();
						map.put("yszk_span", spanSb.append(span.toString()).append("天").toString());

						List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
						title = "附件五	本次转让的应收账款清单列表如下：";
						rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
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
					} else if (creditUserApply.getFinancingType().equals(AIQIN_FINANCING_TYPE_2)) {
						StringBuffer amountSb = new StringBuffer();
						map.put("dt_applicationFinancingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());

						StringBuffer spanSb = new StringBuffer();
						map.put("dt_span", spanSb.append(span.toString()).append("天").toString());

						// 附件六：订单融资的订单信息如下.
						title = "附件六	订单融资的订单信息如下：";
						rowTitle = new String[] { "订单号/合同编号", "订单/合同总额（元）" };
						// 合同.
						CreditPack creditPack = creditUserApply.getCreditPack();
						List<CreditOrder> creditOrders = creditOrderDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
						for (CreditOrder creditOrder : creditOrders) {
							String[] strings = new String[rowTitle.length];
							if (creditPack != null) {
								strings[0] = creditOrder.getNo() + "/" + creditPack.getNo();
								strings[1] = creditOrder.getMoney() + "/" + creditUserApply.getAmount();
							} else {
								strings[0] = creditOrder.getNo();
								strings[1] = creditOrder.getMoney();
							}
							dataList.add(strings);
						}
					}
				}
			} else {
				if (creditUserApply != null) {
					// 借款申请发票应收账款转让总金额.
					Double invoiceTotalAmount = creditVoucherDao.invoiceTotalAmount(creditUserApply.getProjectDataId());
					if (invoiceTotalAmount != null) {
						BigDecimal invoiceTotalAmountBd = new BigDecimal(invoiceTotalAmount);
						StringBuffer invoiceTotalAmountSb = new StringBuffer();
						map.put("yszk_financingTotalAmount", invoiceTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(invoiceTotalAmountBd))).append("元(大写：").append(PdfUtils.change(invoiceTotalAmount)).append(")").toString());
					}

					// 申请转让融资金额.
					StringBuffer amountSb = new StringBuffer();
					map.put("yszk_applicationFinancingAmount", amountSb.append("￥").append(fmtMicrometer(formatToString(amountBd))).append("元(大写：").append(PdfUtils.change(amountBd.doubleValue())).append(")").toString());

					// 期限.
					StringBuffer spanSb = new StringBuffer();
					map.put("yszk_span", spanSb.append(span.toString()).append("天").toString());

//					String apply_span = creditUserApply.getSpan(); // 申请期限.
//					String apply_lender_rate = creditUserApply.getLenderRate(); // 年化利率.

					// 服务费率.
					CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
					creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
					List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
					for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
//						if (creditMiddlemenRate2.getSpan().equals(apply_span)) {
//							String serviceRate = creditMiddlemenRate2.getServiceRate();
//							// 罚息利率，penalty_interest_rates_day，penalty_interest_rates_month.
//							// 超过30天，罚息利率/日.
//							Double penalty_interest_rates_month = (PENALTY_INTEREST_RATES - Double.valueOf(apply_lender_rate) - Double.parseDouble(serviceRate)) / 365;
//							String penalty_interest_rates_month_str = String.valueOf(penalty_interest_rates_month);
//							if(penalty_interest_rates_month_str.length() >= 4){
//								penalty_interest_rates_month_str = penalty_interest_rates_month_str.substring(0, 4); // 保留两位.
//							}
//							Double penalty_interest_rates_month_d = Double.valueOf(penalty_interest_rates_month_str) + 0.01D; // 进一.
//							StringBuffer penalty_interest_rates_month_SB = new StringBuffer();
//							map.put("penalty_interest_rates_month", penalty_interest_rates_month_SB.append(String.valueOf(penalty_interest_rates_month_d)).append("%/日").toString());
//							// 1-30天，罚息利率/日.
//							Double penalty_interest_rates_day = penalty_interest_rates_month_d / 2D;
//							String penalty_interest_rates_day_str = String.valueOf(penalty_interest_rates_day);
//							if(penalty_interest_rates_day_str.length() >= 4){
//								penalty_interest_rates_day_str = penalty_interest_rates_day_str.substring(0, 4); // 保留两位.
//							}
//							Double penalty_interest_rates_day_d = Double.valueOf(penalty_interest_rates_day_str) + 0.01D; // 进一.
//							StringBuffer penalty_interest_rates_day_SB = new StringBuffer();
//							map.put("penalty_interest_rates_day", penalty_interest_rates_day_SB.append(String.valueOf(penalty_interest_rates_day_d)).append("%/日").toString());
//						}
						if (SPAN_30.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 30天.
							StringBuffer servicFee_SB_30 = new StringBuffer();
							map.put("servic_fee_30", servicFee_SB_30.append(serviceRate).append("%").toString());
						} else if (SPAN_60.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 60天.
							StringBuffer servicFee_SB_60 = new StringBuffer();
							map.put("servic_fee_60", servicFee_SB_60.append(serviceRate).append("%").toString());
						} else if (SPAN_90.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 90天.
							StringBuffer servicFee_SB_90 = new StringBuffer();
							map.put("servic_fee_90", servicFee_SB_90.append(serviceRate).append("%").toString());
						} else if (SPAN_120.equals(Integer.parseInt(creditMiddlemenRate2.getSpan()))) {
							String serviceRate = creditMiddlemenRate2.getServiceRate();
							// 120天.
							StringBuffer servicFee_SB_120 = new StringBuffer();
							map.put("servic_fee_120", servicFee_SB_120.append(serviceRate).append("%").toString());
						}
					}

					List<CreditVoucher> creditVouchers = creditVoucherDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
					title = "附件五	本次转让的应收账款清单列表如下：";
					rowTitle = new String[] { "订单号/合同编号", "发票号", "发票总金额（元）" };
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
				}
			}
		} // -- .

		return mergeFinancingFrameworkByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: mergeFinancingFrameworkByTemplate <br>
	 * 描述: 合并融资框架协议. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月28日 上午10:23:59
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String mergeFinancingFrameworkByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);

		// 测试
//		String linFilePath = "D:" + File.separator + "pdf" + File.separator + "M_B_A_GongYingLianRongZiHeZuoKuangJiaXieYi_Temp.pdf";
//		String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "M_B_A_GongYingLianRongZiHeZuoKuangJiaXieYi.pdf";
//		String newFileNamePath = "D:" + File.separator + "pdf" + File.separator + DateUtils.getFileDate();
//		String tablePath = "D:" + File.separator + "pdf" + File.separator + "M_B_A_GongYingLianRongZiHeZuoKuangJiaXieYi_Table.pdf";
		// 正式
		String linFilePath = LIN_FILE_PATH + "M_B_A_GongYingLianRongZiHeZuoKuangJiaXieYi_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "M_B_A_GongYingLianRongZiHeZuoKuangJiaXieYi.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		// 表格PDF路径.
		String tablePath = OUT_PATH + "M_B_A_GongYingLianRongZiHeZuoKuangJiaXieYi_Table.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);

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
		fields.setFieldProperty("party_a", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_representative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_b", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_representative", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("yszk_financingTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("yszk_applicationFinancingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("yszk_span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("dt_applicationFinancingAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("dt_span", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_a_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_postcode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_phone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_fax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_a_email", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("party_b_address", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_postcode", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_phone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_fax", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_contacts", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("party_b_email", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fields.setFieldProperty("penalty_interest_rates_month", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("penalty_interest_rates_day", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

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
	 * 方法: createGuaranteeCulvertPdf <br>
	 * 描述: 创建担保函. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月28日 上午9:11:46
	 * 
	 * @param creditUserApply
	 * @param creditOrders
	 * @return
	 * @throws Exception
	 */
	public static String createGuaranteeCulvertPdf(CreditUserApply creditUserApply, List<CreditOrder> creditOrders) throws Exception {

		// 核心企业.
		CreditUserInfo creditReplaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		/**
		 * PDF(Key:Value).
		 */
		Map<String, String> map = new HashMap<String, String>();
		// 融资期限.
		map.put("span", creditUserApply.getSpan());

		// 核心企业名称.
		if (null == creditReplaceUserInfo) {
			map.put("theCoreEnterprise", "");
		} else {
			map.put("theCoreEnterprise", creditReplaceUserInfo.getEnterpriseFullName());
		}

		// 日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyy年MM月dd日");
		map.put("date", pdfCreateDate);

		String title = "附件	订单融资信息列表如下：";
		// rowTitle.
		String[] rowTitle = new String[] { "订单号/合同编号", "订单/合同总额（元）", "融资金额（元）" };
		// rowData.
		// 合同.
		CreditPack creditPack = creditUserApply.getCreditPack();
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		for (CreditOrder creditOrder : creditOrders) {
			String[] strings = new String[rowTitle.length];
			if (creditPack != null) {
				strings[0] = creditOrder.getNo() + "/" + creditPack.getNo();
				strings[1] = creditOrder.getMoney() + "/" + creditUserApply.getAmount();
			} else {
				strings[0] = creditOrder.getNo();
				strings[1] = creditOrder.getMoney();
			}
			if (creditUserApply != null) {
				strings[2] = creditUserApply.getAmount();
			}
			dataList.add(strings);
		}

		return mergeGuaranteeCulvertByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: mergeGuaranteeCulvertByTemplate <br>
	 * 描述: 订单列表与担保函模版的合并. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月25日 上午9:55:17
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String mergeGuaranteeCulvertByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);

		// 测试
		// 临时文件全路径.
//		String linFilePath = "D:" + File.separator + "pdf" + File.separator + "AiQin_DanBaoHan_Temp.pdf";
		// 模版文件全路径.
//		String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "AiQin_DanBaoHan.pdf";
		// 生产合同全路径.
//		String newFileNamePath = "D:" + File.separator + "pdf" + File.separator + DateUtils.getFileDate();
		// 表格路径.
//		String tablePath = "D:" + File.separator + "pdf" + File.separator + "AiQin_DanBaoHanF_Table.pdf";

		// 临时文件全路径.
		// 正式
		String linFilePath = LIN_FILE_PATH + "AiQin_DanBaoHan_Temp.pdf";
		log.info("临时文件全路径 = " + linFilePath);
		// 模版文件全路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "AiQin_DanBaoHan.pdf";
		log.info("模版文件全路径 = " + templateFileNamePath);
		// 生产合同全路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		log.info("生产合同全路径 = " + newFileNamePath);
		// 表格路径.
		String tablePath = OUT_PATH + "AiQin_DanBaoHanF_Table.pdf";

		// 创建生产合同目录.
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
		fields.setFieldProperty("theCoreEnterprise", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);

		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);
		// 合同临时路径和表格路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 合并临时文件和表格文件.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createOrderApplicationBookPdf <br>
	 * 描述: 订单融资申请书. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年7月30日 下午4:05:42
	 * 
	 * @param creditUserApply
	 * @return
	 * @throws Exception
	 */
	public static String createOrderApplicationBookPdf(CreditUserApply creditUserApply) throws Exception {

		// 借款端核心企业用户.
		CreditUserInfo creditReplaceUserInfo = creditUserInfoDao.get(creditUserApply.getReplaceUserId());
		// 借款端供应商用户.
		CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());
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
		entity.setFinancingType(CreditUserApplyService.CREDIT_FINANCING_TYPE_2);
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
			} else {
				// 承担比例-核心企业.
//				String party_a_annual_str = creditUserApply.getShareRate();
//				StringBuffer party_a_annual_sb = new StringBuffer();
//				map.put("party_b_annual", party_a_annual_sb.append(party_a_annual_str).append("%").toString());
				// 平台融资费用＝服务费＋利息＋登记服务费
				// 服务费＝ （融资金额*服务费率／365）*融资期限
				// 利息＝（融资金额*融资利率／365）*融资期限
				// 登记服务费为应收账款转让登记费用（30元或60元人民币每笔，具体以中国人民银行征信中心动产融资统一登记系统的收费标准为准）
//				String apply_amount = creditUserApply.getAmount(); // 申请金额.
				String apply_span = creditUserApply.getSpan(); // 申请期限.
//				String apply_lender_rate = creditUserApply.getLenderRate(); // 年化利率.
//				// 服务费.
//				Double service_fee_d = 0D;
				// 服务费率.
				CreditMiddlemenRate creditMiddlemenRate = new CreditMiddlemenRate();
				creditMiddlemenRate.setCreditUserId(creditUserApply.getReplaceUserId());
				List<CreditMiddlemenRate> creditMiddlemenRateList = creditMiddlemenRateService.findList(creditMiddlemenRate);
				for (CreditMiddlemenRate creditMiddlemenRate2 : creditMiddlemenRateList) {
//					if (creditMiddlemenRate2.getSpan().equals(apply_span)) {
//						String serviceRate = creditMiddlemenRate2.getServiceRate();
//						service_fee_d = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(serviceRate) / 36500) * Double.parseDouble(apply_span));
//					}
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
//				// 利息.
//				Double interest_double = NumberUtils.scaleDouble((Double.parseDouble(apply_amount) * Double.parseDouble(apply_lender_rate) / 36500) * Double.parseDouble(apply_span));
//				// 登记服务费.
//				Double registration_fee_d = 0D;
//				int apply_span_int = Integer.valueOf(apply_span);
//				if (apply_span_int > 180) { // 大于180天，60元.
//					registration_fee_d = 60D;
//				} else {
//					registration_fee_d = 30D;
//				}
//				// 平台融资费用.
//				Double servic_money_double = NumberUtils.scaleDouble(service_fee_d + interest_double + registration_fee_d);
//				// 承担费用-核心企业.
//				StringBuffer party_a_money_sb = new StringBuffer();
//				Double party_a_money_double = NumberUtils.scaleDouble(servic_money_double * (Double.parseDouble(party_a_annual_str) / 100));
//				map.put("party_b_money", party_a_money_sb.append(String.valueOf(party_a_money_double)).toString());
//				// 承担比例-供应商.
//				StringBuffer party_b_annual_sb = new StringBuffer();
//				int party_b_annual_int = 100 - Integer.parseInt(party_a_annual_str);
//				map.put("party_a_annual", party_b_annual_sb.append(String.valueOf(party_b_annual_int)).append("%").toString());
//				// 承担费用-供应商.
//				StringBuffer party_b_money_sb = new StringBuffer();
//				Double party_b_annual_double = 100D - Double.parseDouble(party_a_annual_str);
//				Double party_b_money_double = NumberUtils.scaleDouble(servic_money_double * (party_b_annual_double / 100));
//				map.put("party_a_money", party_b_money_sb.append(String.valueOf(party_b_money_double)).toString());
			}
		}
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
		 * 本次融资的订单列表如下：
		 */
		String title = "本次融资的订单列表如下：";
		// rowTitle.
		String[] rowTitle = new String[] { "订单号/合同编号", "订单/合同总额（元）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		// 合同.
		CreditPack creditPack = creditUserApply.getCreditPack();
		List<CreditOrder> creditOrders = creditOrderDao.findByCreditInfoIdList(creditUserApply.getProjectDataId());
		for (CreditOrder creditOrder : creditOrders) {
			String[] strings = new String[rowTitle.length];
			if (creditPack != null) {
				strings[0] = creditOrder.getNo() + "/" + creditPack.getNo();
				strings[1] = creditOrder.getMoney() + "/" + creditUserApply.getAmount();
			} else {
				strings[0] = creditOrder.getNo();
				strings[1] = creditOrder.getMoney();
			}
			dataList.add(strings);
		}

		return createOrderApplicationBookPdfByTemplate(map, title, rowTitle, dataList);
	}

	/**
	 * 
	 * 方法: createOrderApplicationBookPdfByTemplate <br>
	 * 描述: 模版合并创建合同. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年7月30日 下午4:03:33
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private static String createOrderApplicationBookPdfByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		log.info("新文件名称 = " + newFileName);
		// 测试
//		String linFilePath = "D:" + File.separator + "pdf" + File.separator + "DingDanRongZiShenQinShu.pdf_Temp.pdf";
//		String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "DingDanRongZiShenQinShu.pdf";
//		String newFileNamePath = "D:" + File.separator + "pdf" + File.separator + DateUtils.getFileDate();
//		String tablePath = "D:" + File.separator + "pdf" + File.separator + "DingDanRongZiShenQinShu.pdf_Table.pdf";

		// 正式
		String tablePath = OUT_PATH + "DingDanRongZiShenQinShu.pdf_Table.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		String linFilePath = LIN_FILE_PATH + "DingDanRongZiShenQinShu.pdf_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "DingDanRongZiShenQinShu.pdf";

		log.info("模版文件全路径 = " + templateFileNamePath);

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
		fields.setFieldProperty("supplyEnterpriseFullName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("replaceEnterpriseFullName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
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

		// 生成代表格的数据.
		PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);

		// 模版路径和表格数据路径.
		String[] files = new String[] { linFilePath, tablePath };
		// 将带表格的数据合并到合同中.
		if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
			log.info("PDF合并 成功！");
		} else {
			log.info("PDF合并 失败！");
		}

		return newFileNamePath + File.separator + newFileName;
	}

}
