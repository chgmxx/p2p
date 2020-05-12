package com.power.platform.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
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
import com.power.platform.common.utils.SpringContextHolder;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.regular.entity.WloanSubject;

public class LoanPdfContractUtil {

	/**
	 * 日志.
	 */
	private static final Logger log = Logger.getLogger(LoanPdfContractUtil.class);

	/**
	 * PDF文件模版路径.
	 */
	private static final String TEMPLATE_FILE_PATH = Global.getConfig("pdf.template.name");

	/**
	 * PDF文件输出路径.
	 */
	private static final String OUT_PATH = Global.getConfig("pdf.out.path");

	// 输出流.
	private static OutputStream fos;

	// 借款端用户帐号.
	private static CreditUserInfoDao creditUserInfoDao = SpringContextHolder.getBean("creditUserInfoDao");

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
	 * 方法: createPromptBookPdf <br>
	 * 描述: 创建风险禁止性行为提示书. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月10日 下午3:49:51
	 * @param creditUserApply
	 * @param servicesRate
	 * @return
	 * @throws Exception
	 */
	public static String createPromptBookPdf(CreditUserApply creditUserApply, String servicesRate) throws Exception {

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		if (creditUserApply != null) {
			// 供应商.
			CreditUserInfo creditSupplyUserInfo = creditUserInfoDao.get(creditUserApply.getCreditSupplyId());

			if (creditSupplyUserInfo != null) {
				// 公司名称.
				map.put("companyName", creditSupplyUserInfo.getEnterpriseFullName().concat("："));
			} else {
				// 公司名称.
				map.put("companyName", "");
			}
			// 借款申请编号.
			map.put("creditUserApplyId", creditUserApply.getId());
			// 年化利率.
			map.put("rate", creditUserApply.getLenderRate().concat("%"));
			// 服务费率.
			map.put("servicesRate", servicesRate.concat("%"));
		}

		return mergePromptBookByTemplate(map);
	}

	/**
	 * 
	 * 方法: mergePromptBookByTemplate <br>
	 * 描述: 合并风险禁止性行为提示书. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月10日 下午3:49:24
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private static String mergePromptBookByTemplate(Map<String, String> map) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		// 模版文件全路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "Loan_FengXianJinZhiXingXingWeiTiShiShu.pdf";
		// 生产合同全路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
//		String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "Loan_FengXianJinZhiXingXingWeiTiShiShu.pdf";
//		String newFileNamePath = "D:" + File.separator + "pdf" +  File.separator + DateUtils.getFileDate();
		log.info("模版文件全路径 = " + templateFileNamePath);
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
		fields.setFieldProperty("companyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("creditUserApplyId", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("rate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("servicesRate", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);

		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(FileUtils.path(newFileNamePath + File.separator + newFileName)));
		fos.write(bos.toByteArray());
		fos.close();

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: createCreditPledgePdf <br>
	 * 描述: 创建信用承诺书PDF文件. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月28日 下午2:11:07
	 * 
	 * @param wloanSubject
	 * @return
	 * @throws Exception
	 */
	public static String createCreditPledgePdf(WloanSubject wloanSubject) throws Exception {

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		if (wloanSubject != null) {
			// 公司名称.
			map.put("companyName", wloanSubject.getCompanyName());
			// 法定代表人.
			map.put("operName", wloanSubject.getLoanUser());
		} else {
			// 公司名称.
			map.put("companyName", "");
			// 法定代表人.
			map.put("operName", "");
		}
		// 承诺书创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyy年MM月dd日");
		// 当前日期.
		map.put("nowDateTime", pdfCreateDate);

		return mergeOrderFinancingByTemplate(map);
	}

	/**
	 * 
	 * 方法: mergeOrderFinancingByTemplate <br>
	 * 描述: 合并信用承诺协议. <br>
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
	private static String mergeOrderFinancingByTemplate(Map<String, String> map) throws Exception {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		// 模版文件全路径.
		String templateFileNamePath = TEMPLATE_FILE_PATH + "Loan_XinYongChengNuoShu.pdf";
		// 生产合同全路径.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
//		String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "Loan_XinYongChengNuoShu.pdf";
//		String newFileNamePath = "D:" + File.separator + "pdf" +  File.separator + DateUtils.getFileDate();
		log.info("模版文件全路径 = " + templateFileNamePath);
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
		fields.setFieldProperty("companyName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("operName", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("nowDateTime", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);

		ps.setFormFlattening(true);
		ps.close();

		fos = new FileOutputStream(FileUtils.path(FileUtils.path(newFileNamePath + File.separator + newFileName)));
		fos.write(bos.toByteArray());
		fos.close();

		return newFileNamePath + File.separator + newFileName;
	}

}
