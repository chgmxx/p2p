package com.power.platform.credit.service;

import java.io.File;
import java.util.ArrayList;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.tsign.ching.eSign.SignHelper;

import com.power.platform.cache.Cache;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.FileUploadUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.collateral.CreditCollateralInfoDao;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.basicinfo.CreditBasicInfo;
import com.power.platform.credit.entity.carinfo.CreditCarInfo;
import com.power.platform.credit.entity.coinsuranceinfo.CreditCoinsuranceInfo;
import com.power.platform.credit.entity.collateral.CreditCollateralInfo;
import com.power.platform.credit.entity.companyInfo.CreditCompanyInfo;
import com.power.platform.credit.entity.electronic.ElectronicSign;
import com.power.platform.credit.entity.electronic.ElectronicSignTranstail;
import com.power.platform.credit.entity.familyinfo.CreditFamilyInfo;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.basicinfo.CreditBasicInfoService;
import com.power.platform.credit.service.carinfo.CreditCarInfoService;
import com.power.platform.credit.service.coinsuranceinfo.CreditCoinsuranceInfoService;
import com.power.platform.credit.service.companyInfo.CreditCompanyInfoService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.credit.service.electronic.ElectronicSignTranstailService;
import com.power.platform.credit.service.familyinfo.CreditFamilyInfoService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.utils.CreateSupplyChainPdfContract;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

/**
 * 
 * 类: CreditImageRestService <br>
 * 描述: 个人信贷信息服务. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年3月22日 下午1:49:51
 */
@Component
@Path("/credit/info")
@Service("creditInfoRestService")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CreditInfoRestService {

	private static final Logger log = LoggerFactory.getLogger(CreditInfoRestService.class);
	private static final String FILE_PATH = Global.getConfig("upload_file_path");
	private static final String VIEW_FILE_PATH = Global.getConfig("credit_file_path");

	/**
	 * 借款企业附件类型：授权书.
	 */
	public static final String CREDIT_ANNEX_FILE_TYPE_11 = "11";
	@Autowired
	private CreditCoinsuranceInfoService creditCoinsuranceInfoService;
	@Autowired
	private CreditCompanyInfoService creditCompanyInfoService;
	@Autowired
	private CreditCarInfoService creditCarInfoService;
	@Autowired
	private CreditFamilyInfoService creditFamilyInfoService;
	@Autowired
	private CreditBasicInfoService creditBasicInfoService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CreditCollateralInfoDao creditCollateralInfoDao;
	@Resource
	private WloanSubjectService wloanSubjectService;
	@Resource
	private ElectronicSignService electronicSignService;
	@Resource
	private ElectronicSignDao electronicSignDao;
	@Resource
	private ElectronicSignTranstailService electronicSignTranstailService;
	@Resource
	private ElectronicSignTranstailDao electronicSignTranstailDao;

	/**
	 * 
	 * 方法: queryCreditUserApplyById <br>
	 * 描述: 查询借款详情. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月21日 上午9:09:29
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/queryCreditUserApplyById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryCreditUserApplyById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:queryCreditUserApplyById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditUserApply entity = creditUserApplyDao.queryCreditUserApplyById(id);
					if (null == entity) {
						log.info("fn:queryCreditUserApplyById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						data.put("entity", entity);
					}
				} else {
					log.info("fn:queryCreditUserApplyById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:queryCreditUserApplyById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:queryCreditUserApplyById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findCreditCollateralInfo <br>
	 * 描述: 查找借款人抵押物列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月15日 上午11:10:55
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findCreditCollateralInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findCreditCollateralInfo(@FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token)) {
			log.info("fn:findCreditCollateralInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					String creditUserId = principal.getCreditUserInfo().getId();
					CreditCollateralInfo entity = new CreditCollateralInfo();
					entity.setCreditUserId(creditUserId);
					List<CreditCollateralInfo> list = creditCollateralInfoDao.findList(entity);
					data.put("list", list);
				} else {
					log.info("fn:findCreditCollateralInfo,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:findCreditCollateralInfo,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findCreditUserApplyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveCreditUserApplyInfo <br>
	 * 描述: 借款申请. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月15日 下午3:47:06
	 * 
	 * @param token
	 * @param amount
	 *            借款金额
	 * @param span
	 *            借款期限(天)
	 * @param replaceUserId
	 *            核心企业
	 * @param projectDataId
	 *            供应商
	 * @return
	 */
	@POST
	@Path("/saveCreditUserApplyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveCreditUserApplyInfo(@FormParam("creditUserId") String creditUserId, @FormParam("amount") String amount, @FormParam("span") String span, @FormParam("projectDataId") String projectDataId, @FormParam("rate") String rate, @FormParam("creditSupplyId") String creditSupplyId, @FormParam("creditApplyName") String creditApplyName) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		String srcPdfFile;
		// 判断参数是否传递.
		if (StringUtils.isBlank(amount) || StringUtils.isBlank(span) || StringUtils.isBlank(projectDataId)) {
			log.info("fn:saveCreditUserApplyInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			CreditUserApply creditUserApply = new CreditUserApply();
			// 主键ID.
			creditUserApply.setId(IdGen.uuid());
			creditUserApply.setAmount(amount);
			creditUserApply.setSpan(span);
			creditUserApply.setLenderRate(rate);
			creditUserApply.setReplaceUserId(creditUserId);
			creditUserApply.setProjectDataId(projectDataId);
			creditUserApply.setCreditApplyName(creditApplyName);
			creditUserApply.setCreditSupplyId(creditSupplyId);
			creditUserApply.setState(CreditUserApplyService.CREDIT_USER_APPLY_STATE_1);
			// 待签署的PDF文件路径
			String pdfStr = CreateSupplyChainPdfContract.CreateApplicationBookPdf(creditUserApply);
			creditUserApply.setBorrPurpose(pdfStr);

			int flag = creditUserApplyService.insertCreditUserApply(creditUserApply);

			if (flag == 1) {
				log.info("fn:saveCreditUserApplyInfo,{借款申请成功.}");
			} else {
				log.info("fn:saveCreditUserApplyInfo,{借款申请失败.}");
			}

			// 生成电子签章
			srcPdfFile = pdfStr;
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

			// 创建企业客户账号(供应商)
			String userOrganizeAccountId1;
			ElectronicSign electronicSign1 = new ElectronicSign();
			electronicSign1.setUserId(creditSupplyId);
			List<ElectronicSign> electronicSignsList1 = electronicSignService.findList(electronicSign1);

			WloanSubject wloanSubject1 = new WloanSubject();
			wloanSubject1.setLoanApplyId(creditSupplyId);
			List<WloanSubject> wloanSubjectsList1 = wloanSubjectService.findList(wloanSubject1);
			wloanSubject1 = wloanSubjectsList1.get(0);

			if (electronicSignsList1 != null && electronicSignsList1.size() > 0) {
				userOrganizeAccountId1 = electronicSignsList1.get(0).getSignId();
			} else {

				userOrganizeAccountId1 = SignHelper.addOrganizeAccountZtmg(wloanSubject1);
				electronicSign1.setId(IdGen.uuid());
				electronicSign1.setSignId(userOrganizeAccountId1);
				electronicSign1.setCreateDate(new Date());
				electronicSignDao.insert(electronicSign1);
			}

			// 创建企业客户账号(核心企业)
			String userOrganizeAccountId2;
			ElectronicSign electronicSign2 = new ElectronicSign();
			electronicSign2.setUserId(creditUserId);
			List<ElectronicSign> electronicSignsList2 = electronicSignService.findList(electronicSign2);

			WloanSubject wloanSubject2 = new WloanSubject();
			wloanSubject2.setLoanApplyId(creditUserId);
			List<WloanSubject> wloanSubjectsList2 = wloanSubjectService.findList(wloanSubject2);
			wloanSubject2 = wloanSubjectsList2.get(0);

			if (electronicSignsList2 != null && electronicSignsList2.size() > 0) {
				userOrganizeAccountId2 = electronicSignsList2.get(0).getSignId();
			} else {

				userOrganizeAccountId2 = SignHelper.addOrganizeAccountZtmg(wloanSubject2);
				electronicSign2.setId(IdGen.uuid());
				electronicSign2.setSignId(userOrganizeAccountId2);
				electronicSign2.setCreateDate(new Date());
				electronicSignDao.insert(electronicSign2);
			}

			// 创建企业印章1
			AddSealResult userOrganizeSealData1 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId1, wloanSubject1);

			// 创建企业印章2
			AddSealResult userOrganizeSealData2 = SignHelper.addOrganizeTemplateSealZTMG(userOrganizeAccountId2, wloanSubject2);

			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
			FileDigestSignResult userOrganizeSignResult1 = SignHelper.userOrganizeSignByFile(srcPdfFile, userOrganizeAccountId1, userOrganizeSealData1.getSealData());
			String serviceId1 = userOrganizeSignResult1.getSignServiceId();
			// 企业客户签署,坐标定位,以文件流的方式传递pdf文档
			FileDigestSignResult userOrganizeSignResult2 = SignHelper.userOrganizeSignByStream2(userOrganizeSignResult1.getStream(), userOrganizeAccountId2, userOrganizeSealData2.getSealData());
			String serviceId2 = userOrganizeSignResult2.getSignServiceId();
			// 所有签署完成,将最终签署后的文件流保存到本地
			if (0 == userOrganizeSignResult2.getErrCode()) {
				SignHelper.saveSignedByStream(userOrganizeSignResult2.getStream(), signedFolder, signedFileName);
			}
			ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
			electronicSignTranstail.setId(IdGen.uuid());
			electronicSignTranstail.setCoreId(creditUserId);
			electronicSignTranstail.setSupplyId(creditSupplyId);
			electronicSignTranstail.setSignServiceIdSupply(serviceId1);
			electronicSignTranstail.setSignServiceIdCore(serviceId2);
			electronicSignTranstail.setCreateDate(new Date());
			electronicSignTranstailDao.insert(electronicSignTranstail);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveCreditUserApplyInfo,{" + e.getMessage() + "}");
			return result;
		}
		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		result.put("srcPdfFile", srcPdfFile);
		return result;
	}

	@POST
	@Path("/deleteQualificationInfoByType")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteQualificationInfoByType(@FormParam("collateralId") String collateralId, @FormParam("type") String type) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(collateralId) || StringUtils.isBlank(type)) {
			log.info("fn:deleteQualificationInfoByType,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 附件列表.
			CreditAnnexFile entity = new CreditAnnexFile();
			entity.setOtherId(collateralId);
			entity.setType(type);
			List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
			for (CreditAnnexFile creditAnnexFile : list) {
				// 表数据删除.
				int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
				if (flag == 1) {
					log.info("CreditAnnexFile delete success.");
				} else {
					log.info("CreditAnnexFile delete failure.");
				}
				// 文件删除.
				File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
				if (file.delete()) {
					log.info("File delete success.");
				} else {
					log.info("File delete failure.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:deleteQualificationInfoByType,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findQualificationInfo <br>
	 * 描述: 查找资质附件列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月10日 下午6:57:18
	 * 
	 * @param token
	 * @param type
	 * @return
	 */
	@POST
	@Path("/findQualificationInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findQualificationInfo(@FormParam("collateralId") String collateralId, @FormParam("type") String type) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(collateralId) || StringUtils.isBlank(type)) {
			log.info("fn:findQualificationInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 附件列表.
			CreditAnnexFile entity = new CreditAnnexFile();
			entity.setOtherId(collateralId);
			entity.setType(type);
			List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
			for (CreditAnnexFile creditAnnexFile : list) {
				creditAnnexFile.setUrl(VIEW_FILE_PATH + creditAnnexFile.getUrl());
			}
			data.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findQualificationInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: modifyQualificationInfo <br>
	 * 描述: 修改资质信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月10日 下午6:18:21
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyQualificationInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyQualificationInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("collateralId") && !item.getFieldName().equals("type")) {
						log.info("FORM DATA:{基本信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditAnnexFileService.IS_TEXT_FORM_FIELD_2) {
						log.info("FORM DATA:{资质审核信息参数不足" + CreditAnnexFileService.IS_TEXT_FORM_FIELD_2 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 资质类型.
					String type = map.get("type");
					// 抵押物ID.
					String collateralId = map.get("collateralId");
					if (isFileFormField == 1) {

						// 获取附件列表(物理删除).
						CreditAnnexFile entity = new CreditAnnexFile();
						entity.setOtherId(collateralId); // 抵押物ID.
						entity.setType(type);
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
						for (CreditAnnexFile creditAnnexFile : list) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
					}

					/**
					 * 上限.
					 */
					if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18) || type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_21)) {
						if (isFileFormField == 3) {
							result.put("state", "6");
							result.put("message", "最多只能上传两张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
						// 查库上限处理.
						CreditAnnexFile entity = new CreditAnnexFile();
						entity.setOtherId(collateralId); // 抵押物ID.
						entity.setType(type); // 附件类型.
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
						if (list.size() == 2) {
							result.put("state", "6");
							result.put("message", "最多只能上传两张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
					} else {
						if (isFileFormField == 2) {
							result.put("state", "6");
							result.put("message", "最多只能上传一张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
						// 查库上限处理.
						CreditAnnexFile entity = new CreditAnnexFile();
						entity.setOtherId(collateralId); // 抵押物ID.
						entity.setType(type); // 附件类型.
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
						if (list.size() == 1) {
							result.put("state", "6");
							result.put("message", "最多只能上传一张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存资质附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(collateralId); // 抵押物ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(type); // 类型：基本信息.
						creditAnnexFile.setState(CreditAnnexFileService.CREDIT_ANNEX_FILE_STATE_1); // 审核中.
						if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_9)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_9); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_10)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_10); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_11)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_11); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_12)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_12); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_13)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_13); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_14)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_14); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_15)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_15); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_16)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_16); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_17)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_17); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_18); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_19)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_19); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_20)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_20); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_21)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_21); // 备注.
						}
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditAnnexFileService.IS_TEXT_FORM_FIELD_2) {
				log.info("FORM DATA:{资质审核信息参数不足" + CreditAnnexFileService.IS_TEXT_FORM_FIELD_2 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				result.put("state", "5");
				result.put("message", "未上传资质附件，请选择资质附件重新请求.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:modifyQualificationInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveQualificationInfo <br>
	 * 描述: 保存资质信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年5月10日 上午11:39:07
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveQualificationInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveQualificationInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("collateralId") && !item.getFieldName().equals("type")) {
						log.info("FORM DATA:{基本信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditAnnexFileService.IS_TEXT_FORM_FIELD_2) {
						log.info("FORM DATA:{资质审核信息参数不足" + CreditAnnexFileService.IS_TEXT_FORM_FIELD_2 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;

					/**
					 * 上限.
					 */
					// 附件类型.
					String type = map.get("type");
					// 抵押物ID.
					String collateralId = map.get("collateralId");
					if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18) || type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_21)) {
						if (isFileFormField == 3) {
							result.put("state", "6");
							result.put("message", "最多只能上传两张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
						// 查库上限处理.
						CreditAnnexFile entity = new CreditAnnexFile();
						entity.setOtherId(collateralId); // 抵押物ID.
						entity.setType(type); // 附件类型.
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
						if (list.size() == 2) {
							result.put("state", "6");
							result.put("message", "最多只能上传两张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
					} else {
						if (isFileFormField == 2) {
							result.put("state", "6");
							result.put("message", "最多只能上传一张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
						// 查库上限处理.
						CreditAnnexFile entity = new CreditAnnexFile();
						entity.setOtherId(collateralId); // 抵押物ID.
						entity.setType(type); // 附件类型.
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(entity);
						if (list.size() == 1) {
							result.put("state", "6");
							result.put("message", "最多只能上传一张附件照，上传上限.");
							result.put("data", data);
							return result;
						}
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存资质附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(collateralId); // 抵押物ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(type); // 类型.
						creditAnnexFile.setState(CreditAnnexFileService.CREDIT_ANNEX_FILE_STATE_1); // 审核中.
						if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_9)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_9); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_10)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_10); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_11)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_11); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_12)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_12); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_13)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_13); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_14)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_14); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_15)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_15); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_16)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_16); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_17)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_17); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_18); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_19)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_19); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_20)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_20); // 备注.
						} else if (type.equals(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_21)) {
							creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_21); // 备注.
						}
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditAnnexFileService.IS_TEXT_FORM_FIELD_2) {
				log.info("FORM DATA:{资质审核信息参数不足" + CreditAnnexFileService.IS_TEXT_FORM_FIELD_2 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				result.put("state", "5");
				result.put("message", "未上传资质附件，请选择资质附件重新请求.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveQualificationInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: deleteCoinsuranceInfoById <br>
	 * 描述: 删除联保信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 下午12:22:07
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/deleteCoinsuranceInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteCoinsuranceInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:deleteCoinsuranceInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditCoinsuranceInfo coinsuranceInfo = creditCoinsuranceInfoService.get(id);
					if (null == coinsuranceInfo) {
						log.info("fn:deleteCoinsuranceInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						// 获取附件列表(物理删除).
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(id);
						for (CreditAnnexFile creditAnnexFile : list) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
						// 删除联保信息.
						int deleteFlag = creditCoinsuranceInfoService.deleteCoinsuranceInfoById(id);
						if (deleteFlag == 1) {
							log.info("CreditCoinsuranceInfo delete success.");
						} else {
							log.info("CreditCoinsuranceInfo delete failure.");
						}
					}
				} else {
					log.info("fn:deleteCoinsuranceInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:deleteCoinsuranceInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:deleteCoinsuranceInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: deleteCompanyInfoById <br>
	 * 描述: 删除公司信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 下午12:15:11
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/deleteCompanyInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteCompanyInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:deleteCompanyInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditCompanyInfo companyInfo = creditCompanyInfoService.get(id);
					if (null == companyInfo) {
						log.info("fn:deleteCompanyInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						// 获取附件列表(物理删除).
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(id);
						for (CreditAnnexFile creditAnnexFile : list) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
						// 删除公司信息.
						int deleteFlag = creditCompanyInfoService.deleteCompanyInfoById(id);
						if (deleteFlag == 1) {
							log.info("CreditCompanyInfo delete success.");
						} else {
							log.info("CreditCompanyInfo delete failure.");
						}
					}
				} else {
					log.info("fn:deleteCompanyInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:deleteCompanyInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:deleteCompanyInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: deleteCarInfoById <br>
	 * 描述: 删除车产信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午11:41:12
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/deleteCarInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteCarInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:deleteCarInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditCarInfo carInfo = creditCarInfoService.get(id);
					if (null == carInfo) {
						log.info("fn:deleteCarInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						// 获取附件列表(物理删除).
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(id);
						for (CreditAnnexFile creditAnnexFile : list) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl().substring(VIEW_FILE_PATH.length()));
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
						// 车产信息物理删除.
						int deleteFlag = creditCarInfoService.deleteCarInfoById(id);
						if (deleteFlag == 1) {
							log.info("CreditCarInfo delete success.");
						} else {
							log.info("CreditCarInfo delete failure.");
						}
					}
				} else {
					log.info("fn:deleteCarInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:deleteCarInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:deleteCarInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: deleteFamilyInfoById <br>
	 * 描述: 删除家庭信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午11:24:41
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/deleteFamilyInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteFamilyInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:deleteFamilyInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditFamilyInfo familyInfo = creditFamilyInfoService.get(id);
					if (null == familyInfo) {
						log.info("fn:deleteFamilyInfoById,{查无次信息.}");
						result.put("state", "5");
						result.put("message", "查无次信息.");
						result.put("data", data);
						return result;
					} else {
						// 获取附件列表(物理删除).
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(id);
						for (CreditAnnexFile creditAnnexFile : list) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
						// 家庭信息物理删除.
						int deleteFlag = creditFamilyInfoService.deleteFamilyInfoById(id);
						if (deleteFlag == 1) {
							log.info("CreditFamilyInfo delete success.");
						} else {
							log.info("CreditFamilyInfo delete failure.");
						}
					}
				} else {
					log.info("fn:deleteFamilyInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:deleteFamilyInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:deleteFamilyInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: deleteBasicInfoById <br>
	 * 描述: 删除基本信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午11:19:17
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/deleteBasicInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> deleteBasicInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:deleteBasicInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditBasicInfo basicInfo = creditBasicInfoService.get(id);
					if (null == basicInfo) {
						log.info("fn:deleteBasicInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						// 获取附件列表(物理删除).
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(id);
						for (CreditAnnexFile creditAnnexFile : list) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
						// 基本信息物理删除.
						int deleteFlag = creditBasicInfoService.deleteBasicInfoById(id);
						if (deleteFlag == 1) {
							log.info("CreditBasicInfo delete success.");
						} else {
							log.info("CreditBasicInfo delete failure.");
						}
					}
				} else {
					log.info("fn:deleteBasicInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:deleteBasicInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:deleteBasicInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: modifyCoinsuranceInfo <br>
	 * 描述: 修改联保信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午10:46:01
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyCoinsuranceInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyCoinsuranceInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 联保信息.
		 */
		CreditCoinsuranceInfo creditCoinsuranceInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("coinsuranceType") && !item.getFieldName().equals("companyName") && !item.getFieldName().equals("name") && !item.getFieldName().equals("phone") && !item.getFieldName().equals("idCard") && !item.getFieldName().equals("id")) {
						log.info("fn:modifyCoinsuranceInfo,{联保信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("fn:modifyCoinsuranceInfo,{联保信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_6) {
						log.info("fn:modifyCoinsuranceInfo,{联保信息参数不足" + CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_6 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {
								// 获取联保信息.
								creditCoinsuranceInfo = creditCoinsuranceInfoService.get(map.get("id"));
								if (null == creditCoinsuranceInfo) {
									log.info("fn:modifyCoinsuranceInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("id"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										log.info("CreditAnnexFile delete success.");
									} else {
										log.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										log.info("File delete success.");
									} else {
										log.info("File delete failure.");
									}
								}
							}
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存联保信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditCoinsuranceInfo.getId()); // 公司信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_8); // 类型：公司信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_8); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_6) {
				log.info("fn:modifyCoinsuranceInfo,{联保信息参数不足" + CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_6 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取联保信息.
						creditCoinsuranceInfo = creditCoinsuranceInfoService.get(map.get("id"));
						if (null == creditCoinsuranceInfo) {
							log.info("fn:modifyCoinsuranceInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 联保信息修改.
			int coinsuranceInfoFlag = creditCoinsuranceInfoService.updateCreditCoinsuranceInfo(creditCoinsuranceInfo, map);
			if (coinsuranceInfoFlag == 1) {
				log.info("update success.");
			} else {
				log.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: modifyCompanyInfo <br>
	 * 描述: 修改公司信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午10:32:33
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyCompanyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyCompanyInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 公司信息.
		 */
		CreditCompanyInfo creditCompanyInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("companyName") && !item.getFieldName().equals("bankAccountNo") && !item.getFieldName().equals("bankName") && !item.getFieldName().equals("id")) {
						log.info("fn:modifyCompanyInfo,{公司信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("fn:modifyCompanyInfo,{公司信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCompanyInfoService.IS_TEXT_FORM_FIELD_5) {
						log.info("fn:modifyCompanyInfo,{公司信息参数不足" + CreditCompanyInfoService.IS_TEXT_FORM_FIELD_5 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {
								// 获取公司信息.
								creditCompanyInfo = creditCompanyInfoService.get(map.get("id"));
								if (null == creditCompanyInfo) {
									log.info("fn:modifyCompanyInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("id"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										log.info("CreditAnnexFile delete success.");
									} else {
										log.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										log.info("File delete success.");
									} else {
										log.info("File delete failure.");
									}
								}
							}
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存公司信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditCompanyInfo.getId()); // 公司信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_7); // 类型：公司信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_7); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCompanyInfoService.IS_TEXT_FORM_FIELD_5) {
				log.info("fn:modifyCompanyInfo,{公司信息参数不足" + CreditCompanyInfoService.IS_TEXT_FORM_FIELD_5 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取公司信息.
						creditCompanyInfo = creditCompanyInfoService.get(map.get("id"));
						if (null == creditCompanyInfo) {
							log.info("fn:modifyCompanyInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 公司信息修改.
			int companyInfoFlag = creditCompanyInfoService.updateCreditCompanyInfo(creditCompanyInfo, map);
			if (companyInfoFlag == 1) {
				log.info("update success.");
			} else {
				log.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: modifyCarInfo <br>
	 * 描述: 修改车产信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月6日 上午10:31:58
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyCarInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyCarInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 车产信息.
		 */
		CreditCarInfo creditCarInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("plateNumber") && !item.getFieldName().equals("id")) {
						log.info("fn:modifyCarInfo,{车产信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("fn:modifyCarInfo,{车产信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCarInfoService.IS_TEXT_FORM_FIELD_3) {
						log.info("fn:modifyCarInfo,{车产信息参数不足" + CreditCarInfoService.IS_TEXT_FORM_FIELD_3 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {// 删除
								// 获取车产信息.
								creditCarInfo = creditCarInfoService.get(map.get("id"));
								if (null == creditCarInfo) {
									log.info("fn:modifyCarInfo,{查无此信息}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("id"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										log.info("CreditAnnexFile delete success.");
									} else {
										log.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										log.info("File delete success.");
									} else {
										log.info("File delete failure.");
									}
								}
							}
						}
					} else {
						log.info("fn:modifyCarInfo,{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存车产信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditCarInfo.getId()); // 车产信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_6); // 类型：车产信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_6); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCarInfoService.IS_TEXT_FORM_FIELD_3) {
				log.info("fn:modifyCarInfo,{车产信息参数不足" + CreditCarInfoService.IS_TEXT_FORM_FIELD_3 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取车产信息.
						creditCarInfo = creditCarInfoService.get(map.get("id"));
						if (null == creditCarInfo) {
							log.info("fn:modifyCarInfo,{查无此信息}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 车产信息更新.
			int carInfoFlag = creditCarInfoService.updateCreditCarInfo(creditCarInfo, map);
			if (carInfoFlag == 1) {
				log.info("update success.");
			} else {
				log.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: modifyFamilyInfo <br>
	 * 描述: 修改家庭信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 下午5:08:36
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyFamilyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyFamilyInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 家庭信息.
		 */
		CreditFamilyInfo creditFamilyInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("relationType") && !item.getFieldName().equals("name") && !item.getFieldName().equals("phone") && !item.getFieldName().equals("idCard") && !item.getFieldName().equals("id")) {
						log.info("fn:modifyFamilyInfo,{家庭信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("fn:modifyFamilyInfo,{家庭信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditFamilyInfoService.IS_TEXT_FORM_FIELD_6) {
						log.info("fn:modifyFamilyInfo,{家庭信息参数不足" + CreditFamilyInfoService.IS_TEXT_FORM_FIELD_6 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							if (isFileFormField == 1) {
								// 获取家庭信息.
								creditFamilyInfo = creditFamilyInfoService.get(map.get("id"));
								if (null == creditFamilyInfo) {
									log.info("fn:modifyFamilyInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("id"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										log.info("CreditAnnexFile delete success.");
									} else {
										log.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										log.info("File delete success.");
									} else {
										log.info("File delete failure.");
									}
								}
							}
						} else {
							log.info("fn:modifyFamilyInfo,{系统超时.}");
							result.put("state", "4");
							result.put("message", "系统超时.");
							result.put("data", data);
							return result;
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存家庭信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditFamilyInfo.getId()); // 家庭信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_2); // 类型：家庭信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_2); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditFamilyInfoService.IS_TEXT_FORM_FIELD_6) {
				log.info("FORM DATA:{家庭信息参数不足" + CreditFamilyInfoService.IS_TEXT_FORM_FIELD_6 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						// 获取家庭信息.
						creditFamilyInfo = creditFamilyInfoService.get(map.get("id"));
						if (null == creditFamilyInfo) {
							log.info("fn:modifyFamilyInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					} else {
						log.info("fn:modifyFamilyInfo,{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 家庭信息更新.
			int familyInfoFlag = creditFamilyInfoService.updateCreditFamilyInfo(creditFamilyInfo, map);
			if (familyInfoFlag == 1) {
				log.info("update success.");
			} else {
				log.info("update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: modifyBasicInfo <br>
	 * 描述: 修改基本信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午11:28:56
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/modifyBasicInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> modifyBasicInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 基本信息.
		 */
		CreditBasicInfo creditBasicInfo = null;

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("name") && !item.getFieldName().equals("age") && !item.getFieldName().equals("maritalStatus") && !item.getFieldName().equals("educationStatus") && !item.getFieldName().equals("idCard") && !item.getFieldName().equals("id")) {
						log.info("fn:modifyBasicInfo,{基本信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("fn:modifyBasicInfo,{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditBasicInfoService.IS_TEXT_FORM_FIELD_7) {
						log.info("fn:modifyBasicInfo,{基本信息参数不足" + CreditBasicInfoService.IS_TEXT_FORM_FIELD_7 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					/**
					 * 更新用户表并删除附件表.
					 */
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							if (isFileFormField == 1) {// 有且只更新删除一次.
								// 获取基本信息.
								creditBasicInfo = creditBasicInfoService.get(map.get("id"));
								if (null == creditBasicInfo) {
									log.info("fn:modifyBasicInfo,{查无此信息.}");
									result.put("state", "5");
									result.put("message", "查无此信息.");
									result.put("data", data);
									return result;
								}
								// 更新用户账户姓名.
								CreditUserInfo creditUserInfo = creditUserInfoDao.get(creditUserId);
								creditUserInfo.setName(map.get("name"));
								int creditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
								if (creditUserInfoFlag == 1) {
									log.info("CreditUserInfo update success.");
								} else {
									log.info("CreditUserInfo update failure.");
								}
								// 获取附件列表(物理删除).
								List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(map.get("id"));
								for (CreditAnnexFile creditAnnexFile : list) {
									// 表数据删除.
									int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
									if (flag == 1) {
										log.info("CreditAnnexFile delete success.");
									} else {
										log.info("CreditAnnexFile delete failure.");
									}
									// 文件删除.
									File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
									if (file.delete()) {
										log.info("File delete success.");
									} else {
										log.info("File delete failure.");
									}
								}
							}
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存基本信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditBasicInfo.getId());
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_1); // 类型：基本信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_1); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditBasicInfoService.IS_TEXT_FORM_FIELD_7) {
				log.info("fn:modifyBasicInfo,{基本信息参数不足" + CreditBasicInfoService.IS_TEXT_FORM_FIELD_7 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				/**
				 * 更新用户表.
				 */
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						// 更新用户账户姓名.
						CreditUserInfo creditUserInfo = creditUserInfoDao.get(creditUserId);
						creditUserInfo.setName(map.get("name"));
						int creditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
						if (creditUserInfoFlag == 1) {
							log.info("CreditUserInfo update success.");
						} else {
							log.info("CreditUserInfo update failure.");
						}
						// 获取基本信息.
						creditBasicInfo = creditBasicInfoService.get(map.get("id"));
						if (null == creditBasicInfo) {
							log.info("fn:modifyBasicInfo,{查无此信息.}");
							result.put("state", "5");
							result.put("message", "查无此信息.");
							result.put("data", data);
							return result;
						}
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 基本信息更新.
			int basicInfoFlag = creditBasicInfoService.updateCreditBasicInfo(creditBasicInfo, map);
			if (basicInfoFlag == 1) {
				log.info("CreditBasicInfo update success.");
			} else {
				log.info("CreditBasicInfo update failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveBasicInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: queryCoinsuranceInfoById <br>
	 * 描述: 查询联保信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午10:07:15
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/queryCoinsuranceInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryCoinsuranceInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:queryCoinsuranceInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditCoinsuranceInfo coinsuranceInfo = creditCoinsuranceInfoService.get(id);
					if (null == coinsuranceInfo) {
						log.info("fn:queryCoinsuranceInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(coinsuranceInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(creditAnnexFile.getUrl());
						}
						coinsuranceInfo.setImgList(tempList);
						data.put("coinsuranceInfo", coinsuranceInfo);
					}
				} else {
					log.info("fn:queryCoinsuranceInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:queryCoinsuranceInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:queryCoinsuranceInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: queryCompanyInfoById <br>
	 * 描述: 查询公司信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午10:03:57
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/queryCompanyInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryCompanyInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:queryCompanyInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditCompanyInfo companyInfo = creditCompanyInfoService.get(id);
					if (null == companyInfo) {
						log.info("fn:queryCompanyInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(companyInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(creditAnnexFile.getUrl());
							companyInfo.setImgList(tempList);
						}
						data.put("companyInfo", companyInfo);
					}
				} else {
					log.info("fn:queryCompanyInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:queryCompanyInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:queryCompanyInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: queryCarInfoById <br>
	 * 描述: 查询车产信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午9:55:32
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/queryCarInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryCarInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:queryCarInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditCarInfo carInfo = creditCarInfoService.get(id);
					if (null == carInfo) {
						log.info("fn:queryCarInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(carInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(creditAnnexFile.getUrl());
						}
						carInfo.setImgList(tempList);
						data.put("carInfo", carInfo);
					}
				} else {
					log.info("fn:queryCarInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:queryCarInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:queryCarInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: queryFamilyInfoById <br>
	 * 描述: 查询家庭信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午9:49:50
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/queryFamilyInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryFamilyInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:queryFamilyInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditFamilyInfo familyInfo = creditFamilyInfoService.get(id);
					if (null == familyInfo) {
						log.info("fn:queryFamilyInfoById,{查无次信息.}");
						result.put("state", "5");
						result.put("message", "查无次信息.");
						result.put("data", data);
						return result;
					} else {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(familyInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(creditAnnexFile.getUrl());
						}
						familyInfo.setImgList(tempList);
						data.put("familyInfo", familyInfo);
					}
				} else {
					log.info("fn:queryFamilyInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:queryFamilyInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:queryFamilyInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: queryBasicInfoById <br>
	 * 描述: 查询基本信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月5日 上午9:40:47
	 * 
	 * @param token
	 * @param id
	 * @return
	 */
	@POST
	@Path("/queryBasicInfoById")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> queryBasicInfoById(@FormParam("token") String token, @FormParam("id") String id) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
			log.info("fn:queryBasicInfoById,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					CreditBasicInfo basicInfo = creditBasicInfoService.get(id);
					if (null == basicInfo) {
						log.info("fn:queryBasicInfoById,{查无此信息.}");
						result.put("state", "5");
						result.put("message", "查无此信息.");
						result.put("data", data);
						return result;
					} else {
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(basicInfo.getId());
						List<String> tempList = new ArrayList<String>();
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(creditAnnexFile.getUrl());
						}
						basicInfo.setImgList(tempList);
						data.put("basicInfo", basicInfo);
					}
				} else {
					log.info("fn:queryBasicInfoById,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:queryBasicInfoById,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:queryBasicInfoById,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findCoinsuranceInfo <br>
	 * 描述: 查找联保信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月1日 下午1:58:59
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findCoinsuranceInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findCoinsuranceInfo(@FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token)) {
			log.info("fn:findCoinsuranceInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					String creditUserId = principal.getCreditUserInfo().getId();
					List<CreditCoinsuranceInfo> coinsuranceInfoList = creditCoinsuranceInfoService.getCreditCoinsuranceInfoList(creditUserId);
					for (CreditCoinsuranceInfo creditCoinsuranceInfo : coinsuranceInfoList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(creditCoinsuranceInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(VIEW_FILE_PATH + creditAnnexFile.getUrl());
						}
						creditCoinsuranceInfo.setImgList(tempList);
					}
					data.put("coinsuranceInfoList", coinsuranceInfoList);
				} else {
					log.info("fn:findCoinsuranceInfo,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:findCoinsuranceInfo,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findCoinsuranceInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findCompanyInfo <br>
	 * 描述: 查找公司信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月1日 下午1:52:00
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findCompanyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findCompanyInfo(@FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token)) {
			log.info("fn:findCompanyInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					String creditUserId = principal.getCreditUserInfo().getId();
					List<CreditCompanyInfo> companyInfoList = creditCompanyInfoService.getCreditCompanyInfoList(creditUserId);
					for (CreditCompanyInfo creditCompanyInfo : companyInfoList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(creditCompanyInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(VIEW_FILE_PATH + creditAnnexFile.getUrl());
						}
						creditCompanyInfo.setImgList(tempList);
					}
					data.put("companyInfoList", companyInfoList);
				} else {
					log.info("fn:findCompanyInfo,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:findCompanyInfo,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findCompanyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findCarInfo <br>
	 * 描述: 查找车产信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月1日 上午10:58:54
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findCarInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findCarInfo(@FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token)) {
			log.info("fn:findCarInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					String creditUserId = principal.getCreditUserInfo().getId();
					List<CreditCarInfo> carInfoList = creditCarInfoService.getCreditCarInfoList(creditUserId);
					for (CreditCarInfo creditCarInfo : carInfoList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(creditCarInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(VIEW_FILE_PATH + creditAnnexFile.getUrl());
						}
						creditCarInfo.setImgList(tempList);
					}
					data.put("carInfoList", carInfoList);
				} else {
					log.info("fn:findCarInfo,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:findCarInfo,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findFamilyInfo <br>
	 * 描述: 查询家庭信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月1日 上午10:36:47
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findFamilyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findFamilyInfo(@FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token)) {
			log.info("fn:findFamilyInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					String creditUserId = principal.getCreditUserInfo().getId();
					List<CreditFamilyInfo> familyInfoList = creditFamilyInfoService.getCreditFamilyInfoList(creditUserId);
					for (CreditFamilyInfo creditFamilyInfo : familyInfoList) {
						List<String> tempList = new ArrayList<String>();
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(creditFamilyInfo.getId());
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(VIEW_FILE_PATH + creditAnnexFile.getUrl());
						}
						creditFamilyInfo.setImgList(tempList);
					}
					data.put("familyInfoList", familyInfoList);
				} else {
					log.info("fn:findFamilyInfo,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:findFamilyInfo,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: findBasicInfo <br>
	 * 描述: 查询基本信息列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年4月1日 上午9:52:23
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findBasicInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> findBasicInfo(@FormParam("token") String token) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断参数是否传递.
		if (StringUtils.isBlank(token)) {
			log.info("fn:findBasicInfo,缺少必要参数.");
			result.put("state", "2");
			result.put("message", "缺少必要参数.");
			result.put("data", data);
			return result;
		}

		try {
			// 客户ID.
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal) {
				if (null != principal.getCreditUserInfo()) {
					String creditUserId = principal.getCreditUserInfo().getId();
					List<CreditBasicInfo> basicInfoList = creditBasicInfoService.getCreditBasicInfo(creditUserId);
					for (CreditBasicInfo creditBasicInfo : basicInfoList) {
						List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(creditBasicInfo.getId());
						List<String> tempList = new ArrayList<String>();
						for (CreditAnnexFile creditAnnexFile : list) {
							tempList.add(VIEW_FILE_PATH + creditAnnexFile.getUrl());
						}
						creditBasicInfo.setImgList(tempList);
					}
					data.put("basicInfoList", basicInfoList);
				} else {
					log.info("fn:findBasicInfo,{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			} else {
				log.info("fn:findBasicInfo,{系统超时.}");
				result.put("state", "4");
				result.put("message", "系统超时.");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:findBasicInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveCoinsuranceInfo <br>
	 * 描述: 保存联保信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 下午5:55:29
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveCoinsuranceInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveCoinsuranceInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存联保信息.
		 */
		CreditCoinsuranceInfo creditCoinsuranceInfo = new CreditCoinsuranceInfo();
		// 车产信息主键.
		String uuid = IdGen.uuid();
		creditCoinsuranceInfo.setId(uuid); // 公司信息ID.

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("coinsuranceType") && !item.getFieldName().equals("companyName") && !item.getFieldName().equals("name") && !item.getFieldName().equals("phone") && !item.getFieldName().equals("idCard")) {
						log.info("FORM DATA:{联保信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{联保信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_5) {
						log.info("FORM DATA:{联保信息参数不足" + CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_5 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							List<CreditCoinsuranceInfo> list = creditCoinsuranceInfoService.getCreditCoinsuranceInfoList(creditUserId);
							if (list.size() == CreditCoinsuranceInfoService.CEILING_6) {
								log.info("FORM DATA:{联保信息增加上限，最多只能添加" + CreditCoinsuranceInfoService.CEILING_6 + "条.}");
								result.put("state", "5");
								result.put("message", "联保信息增加上限，最多只能添加" + CreditCoinsuranceInfoService.CEILING_6 + "条.");
								result.put("data", data);
								return result;
							}
							creditCoinsuranceInfo.setCreditUserId(creditUserId);
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存联保信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 联保信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_8); // 类型：联保信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_8); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_5) {
				log.info("FORM DATA:{联保信息参数不足" + CreditCoinsuranceInfoService.IS_TEXT_FORM_FIELD_5 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						List<CreditCoinsuranceInfo> list = creditCoinsuranceInfoService.getCreditCoinsuranceInfoList(creditUserId);
						if (list.size() == CreditCoinsuranceInfoService.CEILING_6) {
							log.info("FORM DATA:{联保信息增加上限，最多只能添加" + CreditCoinsuranceInfoService.CEILING_6 + "条.}");
							result.put("state", "5");
							result.put("message", "联保信息增加上限，最多只能添加" + CreditCoinsuranceInfoService.CEILING_6 + "条.");
							result.put("data", data);
							return result;
						}
						creditCoinsuranceInfo.setCreditUserId(creditUserId);
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 联保信息保存.
			int coinsuranceInfoFlag = creditCoinsuranceInfoService.insertCreditCoinsuranceInfo(creditCoinsuranceInfo, map);
			if (coinsuranceInfoFlag == 1) {
				log.info("save success.");
			} else {
				log.info("save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveCompanyInfo <br>
	 * 描述: 保存公司信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 下午5:07:28
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveCompanyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveCompanyInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存公司信息.
		 */
		CreditCompanyInfo creditCompanyInfo = new CreditCompanyInfo();
		// 车产信息主键.
		String uuid = IdGen.uuid();
		creditCompanyInfo.setId(uuid); // 公司信息ID.

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("companyName") && !item.getFieldName().equals("bankAccountNo") && !item.getFieldName().equals("bankName")) {
						log.info("FORM DATA:{公司信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{公司信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCompanyInfoService.IS_TEXT_FORM_FIELD_4) {
						log.info("FORM DATA:{公司信息参数不足" + CreditCompanyInfoService.IS_TEXT_FORM_FIELD_4 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							List<CreditCompanyInfo> list = creditCompanyInfoService.getCreditCompanyInfoList(creditUserId);
							if (list.size() == CreditCompanyInfoService.CEILING_1) {
								log.info("FORM DATA:{公司信息增加上限，最多只能添加" + CreditCompanyInfoService.CEILING_1 + "条.}");
								result.put("state", "5");
								result.put("message", "公司信息增加上限，最多只能添加" + CreditCompanyInfoService.CEILING_1 + "条.");
								result.put("data", data);
								return result;
							}
							creditCompanyInfo.setCreditUserId(creditUserId);
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存公司信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 公司信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_7); // 类型：公司信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_7); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCompanyInfoService.IS_TEXT_FORM_FIELD_4) {
				log.info("FORM DATA:{公司信息参数不足" + CreditCompanyInfoService.IS_TEXT_FORM_FIELD_4 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						List<CreditCompanyInfo> list = creditCompanyInfoService.getCreditCompanyInfoList(creditUserId);
						if (list.size() == CreditCompanyInfoService.CEILING_1) {
							log.info("FORM DATA:{公司信息增加上限，最多只能添加" + CreditCompanyInfoService.CEILING_1 + "条.}");
							result.put("state", "5");
							result.put("message", "公司信息增加上限，最多只能添加" + CreditCompanyInfoService.CEILING_1 + "条.");
							result.put("data", data);
							return result;
						}
						creditCompanyInfo.setCreditUserId(creditUserId);
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 公司信息保存.
			int companyInfoFlag = creditCompanyInfoService.insertCreditCompanyInfo(creditCompanyInfo, map);
			if (companyInfoFlag == 1) {
				log.info("save success.");
			} else {
				log.info("save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveCarInfo <br>
	 * 描述: 保存车产信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月30日 下午4:49:03
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveCarInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveCarInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存车产信息.
		 */
		CreditCarInfo creditCarInfo = new CreditCarInfo();
		// 车产信息主键.
		String uuid = IdGen.uuid();
		creditCarInfo.setId(uuid); // 车产信息ID.

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("plateNumber")) {
						log.info("FORM DATA:{车产信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{车产信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditCarInfoService.IS_TEXT_FORM_FIELD_2) {
						log.info("FORM DATA:{车产信息参数不足" + CreditCarInfoService.IS_TEXT_FORM_FIELD_2 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							List<CreditCarInfo> list = creditCarInfoService.getCreditCarInfoList(creditUserId);
							if (list.size() == CreditCarInfoService.CEILING_3) {
								log.info("FORM DATA:{车产信息增加上限，最多只能添加" + CreditCarInfoService.CEILING_3 + "条.}");
								result.put("state", "5");
								result.put("message", "车产信息增加上限，最多只能添" + CreditCarInfoService.CEILING_3 + "条.");
								result.put("data", data);
								return result;
							}
							creditCarInfo.setCreditUserId(creditUserId);
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存车产信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 车产信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_6); // 类型：车产信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_6); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditCarInfoService.IS_TEXT_FORM_FIELD_2) {
				log.info("FORM DATA:{车产信息参数不足" + CreditCarInfoService.IS_TEXT_FORM_FIELD_2 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						List<CreditCarInfo> list = creditCarInfoService.getCreditCarInfoList(creditUserId);
						if (list.size() == CreditCarInfoService.CEILING_3) {
							log.info("FORM DATA:{车产信息增加上限，最多只能添加" + CreditCarInfoService.CEILING_3 + "条.}");
							result.put("state", "5");
							result.put("message", "车产信息增加上限，最多只能添" + CreditCarInfoService.CEILING_3 + "条.");
							result.put("data", data);
							return result;
						}
						creditCarInfo.setCreditUserId(creditUserId);
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 车产信息保存.
			int carInfoFlag = creditCarInfoService.insertCreditCarInfo(creditCarInfo, map);
			if (carInfoFlag == 1) {
				log.info("save success.");
			} else {
				log.info("save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveFamilyInfo <br>
	 * 描述: 保存家庭信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月28日 上午10:11:01
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveFamilyInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveFamilyInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();
		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存家庭信息.
		 */
		CreditFamilyInfo creditFamilyInfo = new CreditFamilyInfo();
		// 家庭信息主键.
		String uuid = IdGen.uuid();
		creditFamilyInfo.setId(uuid); // 家庭信息ID.

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("relationType") && !item.getFieldName().equals("name") && !item.getFieldName().equals("phone") && !item.getFieldName().equals("idCard")) {
						log.info("FORM DATA:{家庭信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{家庭信息参数为null或空串.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditFamilyInfoService.IS_TEXT_FORM_FIELD_5) {
						log.info("FORM DATA:{家庭信息参数不足" + CreditFamilyInfoService.IS_TEXT_FORM_FIELD_5 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							List<CreditFamilyInfo> list = creditFamilyInfoService.getCreditFamilyInfoList(creditUserId);
							if (list.size() == CreditFamilyInfoService.CEILING_6) {
								log.info("FORM DATA:{家庭信息增加上限，最多只能添加" + CreditFamilyInfoService.CEILING_6 + "条.}");
								result.put("state", "5");
								result.put("message", "家庭信息增加上限，最多只能添加" + CreditFamilyInfoService.CEILING_6 + "条.");
								result.put("data", data);
								return result;
							}
							creditFamilyInfo.setCreditUserId(creditUserId);
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存家庭信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 家庭信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_2); // 类型：家庭信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_2); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}

				}
			}

			log.info("MULTIPART FORM DATA IS TEXT FORM FIELD:{" + isTextFormField + "}");
			log.info("MULTIPART FORM DATA IS FILE FORM FIELD:{" + isFileFormField + "}");

			// 文本表单字段个数.
			if (isTextFormField != CreditFamilyInfoService.IS_TEXT_FORM_FIELD_5) {
				log.info("FORM DATA:{家庭信息参数不足" + CreditFamilyInfoService.IS_TEXT_FORM_FIELD_5 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				// 客户ID.
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						List<CreditFamilyInfo> list = creditFamilyInfoService.getCreditFamilyInfoList(creditUserId);
						if (list.size() == CreditFamilyInfoService.CEILING_6) {
							log.info("FORM DATA:{家庭信息增加上限，最多只能添加" + CreditFamilyInfoService.CEILING_6 + "条.}");
							result.put("state", "5");
							result.put("message", "家庭信息增加上限，最多只能添加" + CreditFamilyInfoService.CEILING_6 + "条.");
							result.put("data", data);
							return result;
						}
						creditFamilyInfo.setCreditUserId(creditUserId);
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}

			// 家庭信息保存.
			int familyInfoFlag = creditFamilyInfoService.insertCreditFamilyInfo(creditFamilyInfo, map);
			if (familyInfoFlag == 1) {
				log.info("save success.");
			} else {
				log.info("save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveFamilyInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	/**
	 * 
	 * 方法: saveBasicInfo <br>
	 * 描述: 保存基本信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月28日 上午9:02:06
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/saveBasicInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> saveBasicInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存基本信息.
		 */
		CreditBasicInfo creditBasicInfo = new CreditBasicInfo();
		// 基本信息主键.
		String uuid = IdGen.uuid();
		creditBasicInfo.setId(uuid); // 基本信息ID.

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("name") && !item.getFieldName().equals("age") && !item.getFieldName().equals("maritalStatus") && !item.getFieldName().equals("educationStatus") && !item.getFieldName().equals("idCard")) {
						log.info("FORM DATA:{基本信息参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{缺少必要参数.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 文本表单字段个数.
					if (isTextFormField != CreditBasicInfoService.IS_TEXT_FORM_FIELD_6) {
						log.info("FORM DATA:{基本信息参数不足" + CreditBasicInfoService.IS_TEXT_FORM_FIELD_6 + "个.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;
					/**
					 * 避免重复添加.
					 */
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {
							String creditUserId = principal.getCreditUserInfo().getId();
							List<CreditBasicInfo> basicInfoList = creditBasicInfoService.getCreditBasicInfo(creditUserId);
							if (basicInfoList.size() > 0) {
								result.put("state", "5");
								result.put("message", "已经添加过基本信息，重复添加.");
								result.put("data", data);
								return result;
							} else {
								creditBasicInfo.setCreditUserId(creditUserId);
								// 更新用户账户姓名.
								CreditUserInfo creditUserInfo = creditUserInfoDao.get(creditUserId);
								creditUserInfo.setName(map.get("name"));
								int creditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
								if (creditUserInfoFlag == 1) {
									log.info("CreditUserInfo update success.");
								} else {
									log.info("CreditUserInfo update failure.");
								}
							}
						}
					} else {
						log.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("data", data);
						return result;
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存基本信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(uuid); // 基本信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_1); // 类型：基本信息.
						creditAnnexFile.setRemark(CreditAnnexFileService.CREDIT_ANNEX_FILE_REMARK_1); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}

			// 文本表单字段个数.
			if (isTextFormField != CreditBasicInfoService.IS_TEXT_FORM_FIELD_6) {
				log.info("FORM DATA:{基本信息参数不足" + CreditBasicInfoService.IS_TEXT_FORM_FIELD_6 + "个.}");
				result.put("state", "2");
				result.put("message", "缺少必要参数.");
				result.put("data", data);
				return result;
			}
			// 文件表单字段个数为零.
			if (isFileFormField == 0) {
				/**
				 * 避免重复添加.
				 */
				Cache cache = MemCachedUtis.getMemCached();
				Principal principal = cache.get(map.get("token"));
				if (null != principal) {
					if (null != principal.getCreditUserInfo()) {
						String creditUserId = principal.getCreditUserInfo().getId();
						List<CreditBasicInfo> basicInfoList = creditBasicInfoService.getCreditBasicInfo(creditUserId);
						if (basicInfoList.size() > 0) {
							result.put("state", "5");
							result.put("message", "已经添加过基本信息，重复添加.");
							result.put("data", data);
							return result;
						} else {
							creditBasicInfo.setCreditUserId(creditUserId);
							// 更新用户账户姓名.
							CreditUserInfo creditUserInfo = creditUserInfoDao.get(creditUserId);
							creditUserInfo.setName(map.get("name"));
							int creditUserInfoFlag = creditUserInfoDao.update(creditUserInfo);
							if (creditUserInfoFlag == 1) {
								log.info("CreditUserInfo update success.");
							} else {
								log.info("CreditUserInfo update failure.");
							}
						}
					}
				} else {
					log.info("FORM DATA:{系统超时.}");
					result.put("state", "4");
					result.put("message", "系统超时.");
					result.put("data", data);
					return result;
				}
			}
			// 基本信息保存.
			int basicInfoFlag = creditBasicInfoService.insertCreditBasicInfo(creditBasicInfo, map);
			if (basicInfoFlag == 1) {
				log.info("CreditBasicInfo save success.");
			} else {
				log.info("CreditBasicInfo save failure.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			result.put("data", data);
			log.error("fn:saveBasicInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		result.put("data", data);
		return result;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/adjustAuthorizationBookInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> adjustAuthorizationBookInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 文件表单字段.
		int isFileFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		log.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			log.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			log.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 保存基本信息.
		 */
		CreditBasicInfo creditBasicInfo = new CreditBasicInfo();
		// 基本信息主键.
		String uuid = IdGen.uuid();
		creditBasicInfo.setId(uuid); // 基本信息ID.

		try {
			// 文件处理工厂.
			FileItemFactory factory = new DiskFileItemFactory();
			// 文件处理器.
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 支持中文文件名.
			upload.setHeaderEncoding("utf-8");
			// 储存普通文本数据和文件数据.
			List<FileItem> items = new ArrayList<FileItem>();
			// 解析HTTP请求出来.
			items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem item = (FileItem) items.get(i);
				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					log.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("creditUserId")) {
						log.info("FORM DATA:{参数名错误.}");
						result.put("state", "2");
						result.put("message", "参数名错误，缺少必要参数。");
						return result;
					}
					if (StringUtils.isBlank(item.getString("UTF-8"))) {
						log.info("FORM DATA:{必要参数值不能为空串或null.}");
						result.put("state", "2");
						result.put("message", "必要参数值不能为空串或null.");
						return result;
					}
				} else { // 文件数据.
					// 记录文件表单字段个数.
					isFileFormField = isFileFormField + 1;

					String creditUserId = map.get("creditUserId");
					if (isFileFormField == 1) { // 上传第一个文件.
						// 借款企业授权书查询.
						CreditAnnexFile entity = new CreditAnnexFile();
						entity.setOtherId(creditUserId);
						entity.setType(CREDIT_ANNEX_FILE_TYPE_11);
						List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findList(entity);
						for (CreditAnnexFile creditAnnexFile : creditAnnexFileList) {
							// 表数据删除.
							int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
							if (flag == 1) {
								log.info("CreditAnnexFile delete success.");
							} else {
								log.info("CreditAnnexFile delete failure.");
							}
							// 文件物理删除.
							File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
							if (file.delete()) {
								log.info("File delete success.");
							} else {
								log.info("File delete failure.");
							}
						}
					}

					// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					log.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					log.info("PATH:" + path);
					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					if (flag) {
						log.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存授权书附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(creditUserId); // 借款人ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(CREDIT_ANNEX_FILE_TYPE_11); // 类型：基本信息.
						creditAnnexFile.setRemark("授权书"); // 备注.
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, IdGen.uuid());
						if (tag == 1) {
							log.info("PATH:" + path + ", save success.");
						} else {
							log.info("PATH:" + path + ", save failure.");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "1");
			result.put("message", "系统异常.");
			log.error("fn:adjustAuthorizationBookInfo,{" + e.getMessage() + "}");
			return result;
		}

		result.put("state", "0");
		result.put("message", "接口请求成功.");
		return result;
	}

}