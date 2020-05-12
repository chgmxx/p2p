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
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tsign.ching.eSign.SignHelper;
import cn.tsign.ching.utils.FileHelper;

import com.power.platform.cache.Cache;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtil;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.FileUploadUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.ImageUtils;
import com.power.platform.common.utils.MemCachedUtis;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.credit.dao.creditOrder.CreditOrderDao;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.info.CreditInfoDao;
import com.power.platform.credit.dao.pack.CreditPackDao;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.voucher.CreditVoucherDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.creditOrder.CreditOrder;
import com.power.platform.credit.entity.electronic.ElectronicSign;
import com.power.platform.credit.entity.info.CreditInfo;
import com.power.platform.credit.entity.pack.CreditPack;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.creditOrder.CreditOrderService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.credit.service.info.CreditInfoService;
import com.power.platform.credit.service.pack.CreditPackService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.lanmao.type.CreditUserOpenAccountEnum;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.userinfo.entity.Principal;
import com.power.platform.utils.AiQinPdfContract;
import com.power.platform.utils.LoanAgreementPdfUtil;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

@Path("/creditInfo")
@Service("creditToInfoService")
@Produces(MediaType.APPLICATION_JSON)
public class CreditToInfoService {

	private static final Logger LOG = LoggerFactory.getLogger(CreditToInfoService.class);

	private static final String FILE_PATH = Global.getConfig("upload_iconfile_path");

	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditInfoService creditInfoService;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditInfoDao creditInfoDao;
	@Autowired
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	@Autowired
	private CreditPackDao creditPackDao;
	@Autowired
	private CreditVoucherDao creditVoucherDao;
	@Autowired
	private CgbUserBankCardService cgbUserBankCardService;
	@Autowired
	private CreditOrderDao creditOrderDao;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private CreditOrderService creditOrderService;
	@Autowired
	private CreditPackService creditPackService;
	@Autowired
	private ElectronicSignService electronicSignService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Resource
	private ElectronicSignDao electronicSignDao;

	/**
	 * 借款用户资料新增
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/saveCreditInfo")
	public Map<String, Object> saveCreditInfo(@FormParam("creditUserId") String creditUserId, @FormParam("coreName") String coreName, @FormParam("loanName") String loanName, @FormParam("packName") String packName, @FormParam("packNo") String packNo, @FormParam("packMoney") String packMoney, @FormParam("packType") String packType, @FormParam("packUsedDate") String packUsedDate, @FormParam("packOnDate") String packOnDate) {

		Map<String, Object> result = new HashMap<String, Object>();
		String creditInfoName;

		// N2.新增借款用户资料
		try {

			CreditUserInfo creditUserInfo = creditUserInfoService.get(creditUserId);
			if (creditUserInfo != null) {
				String id = IdGen.uuid();
				CreditInfo creditInfo = new CreditInfo();
				creditInfo.setId(id);
				creditInfo.setCreditUserId(creditUserId);
				creditInfo.setName(creditUserInfo.getEnterpriseFullName() + DateUtils.getDateStr());// 资料名称为姓名+时间戳
				creditInfo.setCreateDate(new Date());
				creditInfo.setUpdateDate(new Date());
				int i = creditInfoDao.insert(creditInfo);
				if (i > 0) {
					LOG.info("借款用户资料新增成功" + "[" + creditInfo.getName() + "]");
				}
				creditInfoName = creditInfo.getName();
				// 新增合同信息
				String packId = IdGen.uuid();
				CreditPack creditPack = new CreditPack();
				creditPack.setId(packId);
				creditPack.setCreditInfoId(id);
				creditPack.setCoreName(coreName);
				creditPack.setLoanName(loanName);
				creditPack.setName(packName);
				creditPack.setNo(packNo);
				creditPack.setMoney(packMoney);
				creditPack.setType(packType);
				creditPack.setUserdDate(DateUtil.getTextDate(packUsedDate, "yyyy-MM-dd"));
				creditPack.setSignDate(DateUtil.getTextDate(packOnDate, "yyyy-MM-dd"));
				int j = creditPackDao.insert(creditPack);
				if (j > 0) {
					LOG.info("合同资料新增成功" + "[" + creditInfo.getName() + "]");
				}
				String creditScore = creditUserInfo.getCreditScore();
				String creditInfoName2;
				if (creditScore == null && "".equals(creditScore)) {
					creditInfoName2 = "0" + DateUtils.getDateStr();
				} else {
					creditInfoName2 = creditUserInfo.getCreditScore() + DateUtils.getDateStr();
				}

				result.put("state", "0");
				result.put("message", "借款用户资料，合同资料新增成功");
				result.put("creditInfoId", id);
				result.put("creditInfoName", creditInfoName2);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 借款用户资料信息列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getCreditInfoList")
	public Map<String, Object> getCreditInfoList(@FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("token") String token, @FormParam("creditUserId") String creditUserId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token) || StringUtils.isBlank(creditUserId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {

				CreditInfo creditInfo = new CreditInfo();
				creditInfo.setCreditUserId(creditUserId);
				Page<CreditInfo> page = new Page<CreditInfo>();
				page.setPageNo(pageNo);
				page.setPageSize(pageSize);
				// page.setOrderBy("create_date DESC");
				Page<CreditInfo> creditInfoPage = creditInfoService.findPage(page, creditInfo);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				List<CreditInfo> creditInfoList = creditInfoPage.getList();
				if (creditInfoList != null && creditInfoList.size() > 0) {
					for (int i = 0; i < creditInfoList.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						creditInfo = creditInfoList.get(i);
						map.put("creditInfoId", creditInfo.getId());
						map.put("creditInfoName", creditInfo.getName() == null ? "" : creditInfo.getName());
						list.add(map);
					}
					data.put("creditInfoList", list);
					data.put("pageNo", creditInfoPage.getPageNo());
					data.put("pageSize", creditInfoPage.getPageSize());
					data.put("totalCount", creditInfoPage.getCount());
					data.put("last", creditInfoPage.getLast());
					data.put("pageCount", creditInfoPage.getLast());
					result.put("state", "0");
					result.put("message", "查询借款资料信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("creditInfoList", list);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 借款端资料图片上传
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/uploadCreditInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> uploadCreditInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";
		// Z资料ID
		String id = IdGen.uuid();

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 资料信息上传.
		 */
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
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("voucherCode") && !item.getFieldName().equals("voucherIssueDate") && !item.getFieldName().equals("type") && !item.getFieldName().equals("creditInfoId") && !item.getFieldName().equals("voucherNo") && !item.getFieldName().equals("voucherMoney") && !item.getFieldName().equals("packNo") && !item.getFieldName().equals("orderNo") && !item.getFieldName().equals("orderMoney") && !item.getFieldName().equals("creditApplyId")) {
						LOG.info("FORM DATA:{资料信息上传参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
							// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
					String name = item.getName();
					// 只获取文件名.
					fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
					// 文件扩展名
					fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
					// 文件上传路径.
					String path = FileUploadUtils.createFilePath(fileFormat);
					LOG.info("PATH:" + path);

					// 新的文件名.
					newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
					boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
					// 图片增加水印
					if (fileFormat.equals("jpg") || fileFormat.equals("png")) {
						ImageUtils.markImageByIcon(Global.getConfig("ZTMG.ICON.IMAGE"), FILE_PATH + path, FILE_PATH + path);
					}

					if (flag) {
						LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
						/**
						 * 保存资料信息附件.
						 */
						CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
						creditAnnexFile.setOtherId(map.get("creditInfoId")); // 资料信息ID.
						creditAnnexFile.setUrl(path); // 图片保存路径.
						creditAnnexFile.setType(map.get("type")); // 类型
						String remark = map.get("type").equals("1") ? "交易合同" : (map.get("type").equals("2") ? "订单" : (map.get("type").equals("3") ? "发货单" : (map.get("type").equals("4") ? "验收单" : (map.get("type").equals("5") ? "对账单" : (map.get("type").equals("6") ? "发票" : (map.get("type").equals("7") ? "承诺函" : "中等网动产登记查询"))))));
						creditAnnexFile.setRemark(remark); // 备注.
						LOG.info("资料信息ID" + id);
						int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, id);
						if (tag == 1) {
							LOG.info("PATH:" + path + ", save success.");
						} else {
							LOG.info("PATH:" + path + ", save failure.");
						}
						String voucherType = map.get("type");
						if (voucherType.equals("6")) {
							String voucherNo = map.get("voucherNo");
							String voucherMoney = map.get("voucherMoney");
							String packNo = map.get("packNo");
							String voucherCode = map.get("voucherCode"); // 发票代码.
							String voucherIssueDate = map.get("voucherIssueDate"); // 开票日期.
							if (voucherNo != null && !"".equals(voucherNo)) {// 应收账款转让
								CreditVoucher creditVoucher = new CreditVoucher();
								String creditVoucherId = IdGen.uuid();
								creditVoucher.setId(creditVoucherId);
								creditVoucher.setAnnexId(id);
								creditVoucher.setCreditInfoId(map.get("creditInfoId"));
								creditVoucher.setPackNo(packNo);
								creditVoucher.setNo(StringUtils.replaceBlanK(voucherNo)); // 发票编号.
								creditVoucher.setMoney(StringUtils.replaceBlanK(voucherMoney)); // 发票金额.
								creditVoucher.setCode(StringUtils.replaceBlanK(voucherCode)); // 发票代码.
								creditVoucher.setIssueDate(DateUtils.parseDate(voucherIssueDate, "yyyy-MM-dd")); // 开票日期.
								creditVoucher.setCreateDate(new Date());
								int j = creditVoucherDao.insert(creditVoucher);
								if (j == 1) {
									LOG.info("PATH:" + path + ", save creditVoucher success.");
								} else {
									LOG.info("PATH:" + path + ", save creditVoucher failure.");
								}
								result.put("creditVoucherId", creditVoucherId);
							} else {// 订单融资，不做处理

							}
						}
						// 保存定单信息
						if (voucherType.equals("2")) {
							// 保存订单信息
							String orderNo = map.get("orderNo");
							if (orderNo != null) {
								String orderMoney = map.get("orderMoney");
								String packNo = map.get("packNo");
								CreditOrder creditOrder = new CreditOrder();
								String creditOrderId = IdGen.uuid();
								creditOrder.setId(creditOrderId);
								creditOrder.setAnnexId(id);
								creditOrder.setCreditInfoId(map.get("creditInfoId"));
								creditOrder.setPackNo(packNo);
								creditOrder.setNo(orderNo);
								creditOrder.setMoney(orderMoney);
								creditOrder.setCreateDate(new Date());
								creditOrder.setUpdateDate(new Date());
								int j = creditOrderDao.insert(creditOrder);
								if (j == 1) {
									LOG.info("PATH:" + path + ", save creditVoucher success.");
								} else {
									LOG.info("PATH:" + path + ", save creditVoucher failure.");
								}
								result.put("creditOrderId", creditOrderId);
							}

						}
						// 上传承诺函
						if (voucherType.equals("7")) {
							// 保存订单信息
							String orderNo = map.get("orderNo");
							if (orderNo != null) {
								String creditApplyId = map.get("creditApplyId");
								if (creditApplyId != null && !"".equals(creditApplyId)) {
									CreditUserApply creditUserApply = creditUserApplyService.get(creditApplyId);
									creditUserApply.setFinancingStep(creditUserApplyService.CREDIT_USER_APPLY_STEP_6);
									creditUserApplyService.save(creditUserApply);
									Log.info("申请第六步完成");
								}
							}
						}
						result.put("path", FILE_PATH + path);

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常.");
			result.put("annexFileId", "");
			LOG.error("fn:saveHouseInfo,{" + e.getMessage() + "}");
			return result;
		}
		result.put("state", "0");
		result.put("message", "借款资料图片上传成功.");
		result.put("annexFileId", id);
		return result;
	}

	/**
	 * 借款端资料图片上传
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/createTemplate")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> createTemplate(@FormParam("creditInfoId") String creditInfoId, @FormParam("supplyUserId") String supplyUserId, @FormParam("creditApplyId") String creditApplyId, @Context HttpServletRequest request) throws Exception {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		List<CreditVoucher> list = creditVoucherDao.findByCreditInfoIdList(creditInfoId);
		String pdfStr = null;
		if (creditApplyId != null && !"".equals(creditApplyId)) {
			CreditUserApply creditUserApply = creditUserApplyService.get(creditApplyId);
			if (CreditUserApplyService.CREDIT_FINANCING_TYPE_1.equals(creditUserApply.getFinancingType())) {// 应收账款
				if (list != null && list.size() > 0) {
					if (CreditUserInfo.XIYUN_ID.equals(creditUserApply.getReplaceUserId())) { // 熙耘承诺函.
						pdfStr = LoanAgreementPdfUtil.createXiYunPromiseToPayBook(creditUserApply, list);
						/**
						 * 骑缝签章.
						 */
						int lastF = pdfStr.lastIndexOf("\\");
						if (lastF == -1) {
							lastF = pdfStr.lastIndexOf("//");
						}
						// 最终签署后的PDF文件路径
						String signedFolder = pdfStr.substring(0, lastF + 1);
						// 最终签署后PDF文件名称
						String signedFileName = pdfStr.substring(lastF + 1, pdfStr.length());
						/**
						 * 公司章.
						 */
						// 初始化项目，做全局使用，只初始化一次即可.
						SignHelper.initProject();
						// 融资主体.
						WloanSubject wloanSubject = new WloanSubject();
						wloanSubject.setLoanApplyId(CreditUserInfo.AIQIN_ID); // 爱亲.
						List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
						if (wloanSubjects != null && wloanSubjects.size() > 0) {
							wloanSubject = wloanSubjects.get(0);
							if (null != wloanSubject) { // 融资主体非NULL判断.
								String supplyOrganizeAccountId;
								ElectronicSign supplyUserElectronicSign = new ElectronicSign();
								supplyUserElectronicSign.setUserId(CreditUserInfo.AIQIN_ID);
								supplyUserElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
								List<ElectronicSign> supplyUserElectronicSigns = electronicSignService.findList(supplyUserElectronicSign);
								if (supplyUserElectronicSigns != null && supplyUserElectronicSigns.size() > 0) {
									ElectronicSign supplyUserElectronicSignOld = supplyUserElectronicSigns.get(0);
									if (null != supplyUserElectronicSignOld) {
										supplyOrganizeAccountId = supplyUserElectronicSignOld.getSignId();
									} else {
										supplyOrganizeAccountId = SignHelper.addOrganizeAccountZtmg(wloanSubject);
										supplyUserElectronicSign.setId(IdGen.uuid());
										supplyUserElectronicSign.setSignId(supplyOrganizeAccountId);
										supplyUserElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
										supplyUserElectronicSign.setCreateDate(new Date());
										supplyUserElectronicSign.setUpdateDate(new Date());
										int insertFlag = electronicSignDao.insert(supplyUserElectronicSign);
										if (insertFlag == 1) {
											LOG.info(this.getClass() + "-创建签章身份铭牌成功");
										} else {
											LOG.info(this.getClass() + "-创建签章身份铭牌失败");
										}
									}
								} else {
									supplyOrganizeAccountId = SignHelper.addOrganizeAccountZtmg(wloanSubject);
									supplyUserElectronicSign.setId(IdGen.uuid());
									supplyUserElectronicSign.setSignId(supplyOrganizeAccountId);
									supplyUserElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
									supplyUserElectronicSign.setCreateDate(new Date());
									supplyUserElectronicSign.setUpdateDate(new Date());
									int insertFlag = electronicSignDao.insert(supplyUserElectronicSign);
									if (insertFlag == 1) {
										LOG.info(this.getClass() + "-创建签章身份铭牌成功");
									} else {
										LOG.info(this.getClass() + "-创建签章身份铭牌失败");
									}
								}

								// 企业印章.
								AddSealResult supplyAddSealResult = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId, wloanSubject);
								// 企业关键字印章签署.
								FileDigestSignResult companyNameSignResult = SignHelper.companyNameSignByStreamCreditPledge(FileHelper.getBytes(pdfStr), supplyOrganizeAccountId, supplyAddSealResult.getSealData());
								if (0 == companyNameSignResult.getErrCode()) {
									LOG.info(this.getClass() + "：企业印章签署成功.");
									SignHelper.saveSignedByStream(companyNameSignResult.getStream(), signedFolder, signedFileName);
								}

								// 企业骑缝印章签署.
								FileDigestSignResult supplyFileDigestSignResult = SignHelper.companyNameSignByStreamCreditPledgeEdges(FileHelper.getBytes(pdfStr), supplyOrganizeAccountId, supplyAddSealResult.getSealData());
								// String serviceIdSupplyUser = supplyFileDigestSignResult.getSignServiceId();
								// 所有签署完成,将最终签署后的文件保存.
								if (0 == supplyFileDigestSignResult.getErrCode()) {
									LOG.info(this.getClass() + "：企业骑缝印章签署成功.");
									SignHelper.saveSignedByStream(supplyFileDigestSignResult.getStream(), signedFolder, signedFileName);

									// 获取附件列表(物理删除).
									CreditAnnexFile annexFiles = new CreditAnnexFile();
									annexFiles.setOtherId(creditUserApply.getProjectDataId());
									annexFiles.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7);
									List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFiles);
									for (CreditAnnexFile creditAnnexFile : creditAnnexFileList) {
										// 表数据删除.
										int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
										if (flag == 1) {
											LOG.info("CreditAnnexFile delete success.");
										} else {
											LOG.info("CreditAnnexFile delete failure.");
										}
										// 文件删除.
										File file = new File("/data" + File.separator + creditAnnexFile.getUrl());
										LOG.info("file path = " + file.getPath());
										if (file.delete()) {
											LOG.info("File delete success.");
										} else {
											LOG.info("File delete failure.");
										}
									}

									/**
									 * 保存资料信息附件.
									 */
									CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
									creditAnnexFile.setOtherId(creditUserApply.getProjectDataId()); // 资料信息ID.
									creditAnnexFile.setUrl(pdfStr.split("data")[1]); // 付款承诺书路径.
									creditAnnexFile.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7); // 付款承诺书.
									creditAnnexFile.setRemark("付款承诺书"); // 备注.
									int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, 1, IdGen.uuid());
									if (tag == 1) {
										LOG.info("PATH:" + pdfStr.split("data")[1] + ", save success.");
									} else {
										LOG.info("PATH:" + pdfStr.split("data")[1] + ", save failure.");
									}
								}
							}
						}
					} else {
						pdfStr = LoanAgreementPdfUtil.createPromiseToPayBook(creditUserApply, list);
						/**
						 * 骑缝签章.
						 */
						int lastF = pdfStr.lastIndexOf("\\");
						if (lastF == -1) {
							lastF = pdfStr.lastIndexOf("//");
						}
						// 最终签署后的PDF文件路径
						String signedFolder = pdfStr.substring(0, lastF + 1);
						// 最终签署后PDF文件名称
						String signedFileName = pdfStr.substring(lastF + 1, pdfStr.length());
						/**
						 * 公司章.
						 */
						// 初始化项目，做全局使用，只初始化一次即可.
						SignHelper.initProject();
						// 融资主体.
						WloanSubject wloanSubject = new WloanSubject();
						wloanSubject.setLoanApplyId(creditUserApply.getReplaceUserId());
						List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
						if (wloanSubjects != null && wloanSubjects.size() > 0) {
							wloanSubject = wloanSubjects.get(0);
							if (null != wloanSubject) { // 融资主体非NULL判断.
								String supplyOrganizeAccountId;
								ElectronicSign supplyUserElectronicSign = new ElectronicSign();
								supplyUserElectronicSign.setUserId(creditUserApply.getReplaceUserId());
								supplyUserElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
								List<ElectronicSign> supplyUserElectronicSigns = electronicSignService.findList(supplyUserElectronicSign);
								if (supplyUserElectronicSigns != null && supplyUserElectronicSigns.size() > 0) {
									ElectronicSign supplyUserElectronicSignOld = supplyUserElectronicSigns.get(0);
									if (null != supplyUserElectronicSignOld) {
										supplyOrganizeAccountId = supplyUserElectronicSignOld.getSignId();
									} else {
										supplyOrganizeAccountId = SignHelper.addOrganizeAccountZtmg(wloanSubject);
										supplyUserElectronicSign.setId(IdGen.uuid());
										supplyUserElectronicSign.setSignId(supplyOrganizeAccountId);
										supplyUserElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
										supplyUserElectronicSign.setCreateDate(new Date());
										supplyUserElectronicSign.setUpdateDate(new Date());
										int insertFlag = electronicSignDao.insert(supplyUserElectronicSign);
										if (insertFlag == 1) {
											LOG.info(this.getClass() + "-创建签章身份铭牌成功");
										} else {
											LOG.info(this.getClass() + "-创建签章身份铭牌失败");
										}
									}
								} else {
									supplyOrganizeAccountId = SignHelper.addOrganizeAccountZtmg(wloanSubject);
									supplyUserElectronicSign.setId(IdGen.uuid());
									supplyUserElectronicSign.setSignId(supplyOrganizeAccountId);
									supplyUserElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_2);
									supplyUserElectronicSign.setCreateDate(new Date());
									supplyUserElectronicSign.setUpdateDate(new Date());
									int insertFlag = electronicSignDao.insert(supplyUserElectronicSign);
									if (insertFlag == 1) {
										LOG.info(this.getClass() + "-创建签章身份铭牌成功");
									} else {
										LOG.info(this.getClass() + "-创建签章身份铭牌失败");
									}
								}

								// 企业印章.
								AddSealResult supplyAddSealResult = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId, wloanSubject);
								// 企业关键字印章签署.
								FileDigestSignResult companyNameSignResult = SignHelper.companyNameSignByStreamCreditPledge(FileHelper.getBytes(pdfStr), supplyOrganizeAccountId, supplyAddSealResult.getSealData());
								if (0 == companyNameSignResult.getErrCode()) {
									LOG.info(this.getClass() + "：企业印章签署成功.");
									SignHelper.saveSignedByStream(companyNameSignResult.getStream(), signedFolder, signedFileName);
								}

								// 企业骑缝印章签署.
								FileDigestSignResult supplyFileDigestSignResult = SignHelper.companyNameSignByStreamCreditPledgeEdges(FileHelper.getBytes(pdfStr), supplyOrganizeAccountId, supplyAddSealResult.getSealData());
								// String serviceIdSupplyUser = supplyFileDigestSignResult.getSignServiceId();
								// 所有签署完成,将最终签署后的文件保存.
								if (0 == supplyFileDigestSignResult.getErrCode()) {
									LOG.info(this.getClass() + "：企业骑缝印章签署成功.");
									SignHelper.saveSignedByStream(supplyFileDigestSignResult.getStream(), signedFolder, signedFileName);

									// 获取附件列表(物理删除).
									CreditAnnexFile annexFiles = new CreditAnnexFile();
									annexFiles.setOtherId(creditUserApply.getProjectDataId());
									annexFiles.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7);
									List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFiles);
									for (CreditAnnexFile creditAnnexFile : creditAnnexFileList) {
										// 表数据删除.
										int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
										if (flag == 1) {
											LOG.info("CreditAnnexFile delete success.");
										} else {
											LOG.info("CreditAnnexFile delete failure.");
										}
										// 文件删除.
										File file = new File("/data" + File.separator + creditAnnexFile.getUrl());
										LOG.info("file path = " + file.getPath());
										if (file.delete()) {
											LOG.info("File delete success.");
										} else {
											LOG.info("File delete failure.");
										}
									}

									/**
									 * 保存资料信息附件.
									 */
									CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
									creditAnnexFile.setOtherId(creditUserApply.getProjectDataId()); // 资料信息ID.
									creditAnnexFile.setUrl(pdfStr.split("data")[1]); // 付款承诺书路径.
									creditAnnexFile.setType(CreditAnnexFileService.CREDIT_PROJECT_DATA_TYPE_7); // 付款承诺书.
									creditAnnexFile.setRemark("付款承诺书"); // 备注.
									int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, 1, IdGen.uuid());
									if (tag == 1) {
										LOG.info("PATH:" + pdfStr.split("data")[1] + ", save success.");
									} else {
										LOG.info("PATH:" + pdfStr.split("data")[1] + ", save failure.");
									}
								}
							}
						}
					}
				} else {
					result.put("state", "2");
					result.put("message", "请先上传发票！");
					result.put("pdfStr", "#");
				}
			} else if (CreditUserApplyService.CREDIT_FINANCING_TYPE_2.equals(creditUserApply.getFinancingType())) {// 订单融资
				// 合同
				CreditPack creditPack = new CreditPack();
				creditPack.setCreditInfoId(creditUserApply.getProjectDataId());
				List<CreditPack> creditPacks = creditPackService.findList(creditPack);
				creditPack = creditPacks.get(0);
				creditUserApply.setCreditPack(creditPack);

				CreditOrder creditOrder = new CreditOrder();
				creditOrder.setCreditInfoId(creditUserApply.getProjectDataId());
				List<CreditOrder> creditOrders = creditOrderService.findList(creditOrder);
				pdfStr = AiQinPdfContract.createGuaranteeCulvertPdf(creditUserApply, creditOrders);// 爱亲订单融资
			}
		}
		if (pdfStr != null) {
			result.put("state", "0");
			result.put("message", "创建付款承诺书成功");
			result.put("pdfStr", pdfStr.split("data")[1]);
		} else {
			result.put("state", "1");
			result.put("message", "创建付款承诺书失败");
			result.put("pdfStr", "#");
		}
		return result;
	}

	/**
	 * 获取资料图片
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getCreditInfo")
	public Map<String, Object> getCreditInfo(@FormParam("token") String token, @FormParam("type") String type, @FormParam("creditInfoId") String creditInfoId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isBlank(token) || StringUtils.isBlank(type) || StringUtils.isBlank(creditInfoId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				CreditAnnexFile annexFile = new CreditAnnexFile();
				annexFile.setOtherId(creditInfoId);
				annexFile.setType(type);
				List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(annexFile);
				List<String> urlList = null;
				for (CreditAnnexFile creditAnnexFile : list) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("annexFileId", creditAnnexFile.getId());
					if (creditAnnexFile.getType().equals("7")) {
						String urlStr = creditAnnexFile.getUrl();
						urlList = new ArrayList<String>();
						String urlArr[] = urlStr.split("\\|");
						for (int i = 1; i < urlArr.length; i++) {
							urlList.add(urlArr[i]);
						}
						map.put("imgUrl", urlList.get(0));
					} else {
						// map.put("imgUrl",
						// "https://www.cicmorgan.com/upload/image/"+creditAnnexFile.getUrl());
						map.put("imgUrl", ServerURLConfig.CREDITANNEXFILEURL + creditAnnexFile.getUrl());

					}
					returnList.add(map);
				}
				data.put("imgList", returnList);
				result.put("state", "0");
				result.put("message", "查询资料信息成功");
				result.put("data", data);
				return result;

			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 借款端资料图片更新
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/updateCreditInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> updateCreditInfo(@Context HttpServletRequest request) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 数据域.
		Map<String, Object> data = new HashMap<String, Object>();
		// 键值对存储的Map接口，以HashMap(HashMap非线程安全，效率比较高)实现.
		Map<String, String> map = new HashMap<String, String>();

		// 普通文本表单字段.
		int isTextFormField = 0;
		// 上传的文件名.
		String fileName = "";
		// 新命名的文件名.
		String newFileName = "";
		// 文件格式.
		String fileFormat = "";
		// Z资料ID
		String id = IdGen.uuid();

		// 在解析请求之前先判断请求类型是否为文件上传类型.
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		LOG.info("isMultipart:" + isMultipart + ".");
		// 是否为文件上传类型.
		if (isMultipart) {
			LOG.info("isMultipart:" + isMultipart + ",the file upload type.");
		} else {
			LOG.info("isMultipart:" + isMultipart + ",not the file upload type.");
		}

		/**
		 * 资料信息上传.
		 */
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

				if (i == 4) {// 第一次循环删除所有老数据 之后不用删除
					// 获取附件列表(物理删除).
					CreditAnnexFile annexFiles = new CreditAnnexFile();
					LOG.info("=========" + map.get("creditInfoId") + "**************" + map.get("type") + "###############" + map.get("annexFileId"));
					annexFiles.setOtherId(map.get("creditInfoId"));
					annexFiles.setType(map.get("type"));
					annexFiles.setId(map.get("annexFileId"));
					List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileList(annexFiles);
					LOG.info("查出个数" + list.size());
					for (CreditAnnexFile creditAnnexFile : list) {
						LOG.info("删除图片ID为" + creditAnnexFile.getId());
						// 表数据删除.
						int flag = creditAnnexFileService.deleteCreditAnnexFileById(creditAnnexFile.getId());
						if (flag == 1) {
							LOG.info("CreditAnnexFile delete success.");
						} else {
							LOG.info("CreditAnnexFile delete failure.");
						}
						// 文件删除.
						File file = new File(FILE_PATH + File.separator + creditAnnexFile.getUrl());
						if (file.delete()) {
							LOG.info("File delete success.");
						} else {
							LOG.info("File delete failure.");
						}
					}
				}

				// 普通文本数据.
				if (item.isFormField()) {
					// 记录普通文本表单字段个数.
					isTextFormField = isTextFormField + 1;
					// 将普通文本数据存入Map.
					map.put(item.getFieldName(), item.getString("UTF-8"));
					LOG.info("FORM DATA:{" + item.getFieldName() + " = " + item.getString("UTF-8") + "}");
					/**
					 * 判断参数是否传递.
					 */
					if (!item.getFieldName().equals("token") && !item.getFieldName().equals("type") && !item.getFieldName().equals("creditInfoId") && !item.getFieldName().equals("annexFileId")) {
						LOG.info("FORM DATA:{资料信息上传参数名错误.}");
						result.put("state", "2");
						result.put("message", "缺少必要参数.");
						result.put("data", data);
						return result;
					}
				} else { // 文件数据.
					// 客户ID.
					Cache cache = MemCachedUtis.getMemCached();
					Principal principal = cache.get(map.get("token"));
					if (null != principal) {
						if (null != principal.getCreditUserInfo()) {

							// 上传的文件名(IE上是文件全路径，火狐等浏览器仅文件名).
							String name = item.getName();
							// 只获取文件名.
							fileName = name.substring(name.lastIndexOf(File.separator) + 1, name.length());
							// 文件扩展名
							fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
							LOG.info("MULTIPART FORM DATA:{" + item.getFieldName() + " = " + fileName + "},文件格式:" + fileFormat);
							// 文件上传路径.
							String path = FileUploadUtils.createFilePath(fileFormat);
							LOG.info("PATH:" + path);
							// 新的文件名.
							newFileName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());
							boolean flag = FileUploadUtils.uploadStream(newFileName, FILE_PATH, item.getInputStream());
							if (flag) {
								LOG.info("{原文件名:" + fileName + ",新文件名:" + newFileName + ",上传成功.}");
								/**
								 * 保存资料信息附件.
								 */
								CreditAnnexFile creditAnnexFile = new CreditAnnexFile();
								creditAnnexFile.setOtherId(map.get("creditInfoId")); // 资料信息ID.
								creditAnnexFile.setUrl(path); // 图片保存路径.
								creditAnnexFile.setType(map.get("type")); // 类型
								String remark = map.get("type").equals("1") ? "交易合同" : (map.get("type").equals("2") ? "订单" : (map.get("type").equals("3") ? "发货单" : (map.get("type").equals("4") ? "入库单" : (map.get("type").equals("5") ? "发票" : (map.get("type").equals("6") ? "付款单据证明" : "中等网动产登记查询")))));
								creditAnnexFile.setRemark(remark); // 备注.
								int tag = creditAnnexFileService.insertCreditAnnexFile(creditAnnexFile, i, id);
								if (tag == 1) {
									LOG.info("PATH:" + path + ", save success.");
								} else {
									LOG.info("PATH:" + path + ", save failure.");
								}
							}
						}
					} else {
						LOG.info("FORM DATA:{系统超时.}");
						result.put("state", "4");
						result.put("message", "系统超时.");
						result.put("annexFileId", "");
						return result;
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常.");
			result.put("annexFileId", "");
			LOG.error("fn:saveHouseInfo,{" + e.getMessage() + "}");
			return result;
		}
		result.put("state", "0");
		result.put("message", "借款资料图片修改成功.");
		result.put("annexFileId", id);
		return result;
	}

	/**
	 * 投资端获取项目信息
	 * 
	 * @param type
	 *            1-贸易背景 2-项目资质 3-风控资质
	 * @param creditInfoId
	 * @return
	 */
	@POST
	@Path("/getInventory")
	public Map<String, Object> getInventory(@FormParam("type") String type, @FormParam("creditInfoId") String creditInfoId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(type) || StringUtils.isBlank(creditInfoId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			CreditAnnexFile annexFile = new CreditAnnexFile();
			List<String> typeList = new ArrayList<String>();
			List<String> imgList = new ArrayList<String>();
			if (type.equals("1")) {// 贸易背景
				typeList.add("1");
				typeList.add("2");
				typeList.add("3");
				typeList.add("4");
				typeList.add("6");
			} else if (type.equals("2")) {// 项目资质
				typeList.add("5");
			} else if (type.equals("3")) {// 风控资质
				typeList.add("7");
			}
			annexFile.setTypeList(typeList);
			annexFile.setOtherId(creditInfoId);
			List<CreditAnnexFile> list = creditAnnexFileService.findCreditAnnexFileToInvestment(annexFile);
			if (list != null) {
				for (CreditAnnexFile creditAnnexFile : list) {
					imgList.add(Global.getConfig("credit_file_path") + creditAnnexFile.getUrl());
					// if(creditAnnexFile.getType().equals("7")){
					// String urlStr = creditAnnexFile.getUrl();
					// // urlList = new ArrayList<String>();
					// // String urlArr[] = urlStr.split("\\|");
					// // for (int i = 1; i < urlArr.length; i++) {
					// // urlList.add(urlArr[i]);
					// // }
					// imgList.add(urlStr);
					// }else{
					// imgList.add(Global.getConfig("credit_file_path")+creditAnnexFile.getUrl());
					// }
				}
				data.put("imgList", imgList);
				result.put("state", "0");
				result.put("message", "查询资料信息成功");
				result.put("data", data);
				return result;
			} else {
				result.put("state", "1");
				result.put("message", "未查询到资料信息");
				result.put("data", data);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 删除资料图片信息
	 * 
	 * @param token
	 * @param annexFileId
	 * @return
	 */
	@POST
	@Path("/deleteCredit")
	public Map<String, Object> deleteCredit(@FormParam("id") String id, @FormParam("annexFileId") String annexFileId) {

		Map<String, Object> result = new HashMap<String, Object>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(annexFileId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}

		try {
			CreditUserInfo userInfo = creditUserInfoService.get(id);
			if (userInfo != null) {
				// N3.获取附件列表(物理删除).
				CreditAnnexFile annexFile = creditAnnexFileService.get(annexFileId);
				if (annexFile != null) {
					LOG.info("资料图片ID为" + annexFile.getId());
					// 表数据删除.
					int flag = creditAnnexFileService.deleteCreditAnnexFileById(annexFile.getId());
					if (flag == 1) {
						LOG.info("CreditAnnexFile delete success.");
					} else {
						LOG.info("CreditAnnexFile delete failure.");
					}
					// 文件删除.
					File file = new File(FILE_PATH + File.separator + annexFile.getUrl());
					if (file.delete()) {
						LOG.info("File delete success.");
					} else {
						LOG.info("File delete failure.");
					}
					CreditVoucher creditVoucher = new CreditVoucher();
					creditVoucher.setAnnexId(annexFile.getId());
					List<CreditVoucher> list = creditVoucherDao.findList(creditVoucher);
					if (list != null && list.size() > 0) {
						CreditVoucher creditVoucher2 = list.get(0);
						int j = creditVoucherDao.delete(creditVoucher2);
						if (j == 1) {
							LOG.info("CreditVoucher delete success.");
						} else {
							LOG.info("CreditVoucher delete failure.");
						}

					}

					result.put("state", "0");
					result.put("message", "图片删除成功");
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("state", "5");
			result.put("message", "系统异常");
		}
		return result;
	}

	/**
	 * 查询代偿户对应借款户
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findCreditUserInfo")
	public Map<String, Object> findCreditUserInfo(@FormParam("id") String id) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		CreditUserInfo userInfo = new CreditUserInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(id)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			CreditSupplierToMiddlemen entity = new CreditSupplierToMiddlemen();
			entity.setMiddlemenId(id);
			List<CreditSupplierToMiddlemen> list = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemensList(entity);
			List<CreditSupplierToMiddlemen> newList = new ArrayList<CreditSupplierToMiddlemen>();
			for (CreditSupplierToMiddlemen creditSupplierToMiddlemen : list) {
				CreditUserInfo supplierUser = creditSupplierToMiddlemen.getSupplierUser();
				if (supplierUser != null) {
					String isCreateBasicInfo = supplierUser.getIsCreateBasicInfo();
					String openAccountState = supplierUser.getOpenAccountState();
					if (null != openAccountState && isCreateBasicInfo != null) {
						if (CreditUserOpenAccountEnum.OPEN_ACCOUNT_STATE_1.getValue().equals(openAccountState) && isCreateBasicInfo.equals(CreditUserInfo.IS_CREATE_BASIC_INFO_1)) {
							newList.add(creditSupplierToMiddlemen);
						}
					}
				}
			}
			data.put("creditUserList", newList);
			result.put("state", "0");
			result.put("message", "查询对应供应商账户成功");
			result.put("data", data);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 查询所有借款账户
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findAllCreditUserInfo")
	public Map<String, Object> findAllCreditUserInfo(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		CreditUserInfo userInfo = new CreditUserInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				userInfo.setCreditUserType("02");
				List<CreditUserInfo> list = creditUserInfoService.findList(userInfo);
				if (list != null) {
					for (CreditUserInfo creditUserInfo : list) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("creditUserId", creditUserInfo.getId());
						map.put("creditName", creditUserInfo.getEnterpriseFullName());
						returnList.add(map);
					}
				}
				data.put("creditUserInfoList", returnList);
				result.put("state", "0");
				result.put("message", "查询所有供应商账户成功");
				result.put("data", data);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 查询所有代偿账户
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findAllMiddlemenInfo")
	public Map<String, Object> findAllMiddlemenInfo(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		CreditUserInfo userInfo = new CreditUserInfo();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				userInfo.setCreditUserType("11");
				List<CreditUserInfo> list = creditUserInfoService.findList(userInfo);
				if (list != null) {
					for (CreditUserInfo creditUserInfo : list) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("middlemenId", creditUserInfo.getId());
						map.put("middlemenName", creditUserInfo.getEnterpriseFullName());
						returnList.add(map);
					}
				}
				data.put("middlemenList", returnList);
				result.put("state", "0");
				result.put("message", "查询所有代偿户账户成功");
				result.put("data", data);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 查询所有资料---下拉框
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findAllCreditInfo")
	public Map<String, Object> findAllCreditInfo(@FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		// N1.判断必要参数是否为空
		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {
			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {
				List<CreditInfo> list = creditInfoDao.findAllList();
				if (list != null) {
					for (CreditInfo creditInfo : list) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", creditInfo.getId());
						map.put("name", creditInfo.getName());
						returnList.add(map);
					}
				}
				data.put("creditInfoList", returnList);
				result.put("state", "0");
				result.put("message", "查询所有资料成功");
				result.put("data", data);
				return result;
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}
	}

	/**
	 * 借款用户资料信息列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @return
	 */
	@POST
	@Path("/findAllCreditInfoList")
	public Map<String, Object> findAllCreditInfoList(@FormParam("pageNo") Integer pageNo, @FormParam("pageSize") Integer pageSize, @FormParam("token") String token) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		if (StringUtils.isBlank(token)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {

				CreditInfo creditInfo = new CreditInfo();
				Page<CreditInfo> page = new Page<CreditInfo>();
				page.setPageNo(pageNo);
				page.setPageSize(pageSize);
				// page.setOrderBy("create_date DESC");
				Page<CreditInfo> creditInfoPage = creditInfoService.findPage(page, creditInfo);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				List<CreditInfo> creditInfoList = creditInfoPage.getList();
				if (creditInfoList != null && creditInfoList.size() > 0) {
					for (int i = 0; i < creditInfoList.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						creditInfo = creditInfoList.get(i);
						map.put("creditInfoId", creditInfo.getId());
						map.put("creditInfoName", creditInfo.getName() == null ? "" : creditInfo.getName());
						map.put("createDate", DateUtils.formatDateTime(creditInfo.getCreateDate()));
						list.add(map);
					}
					data.put("creditInfoList", list);
					data.put("pageNo", creditInfoPage.getPageNo());
					data.put("pageSize", creditInfoPage.getPageSize());
					data.put("totalCount", creditInfoPage.getCount());
					data.put("last", creditInfoPage.getLast());
					data.put("pageCount", creditInfoPage.getLast());
					result.put("state", "0");
					result.put("message", "查询借款资料信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("creditInfoList", list);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}

	/**
	 * 借款用户资料信息列表---下拉列表
	 * 
	 * @param from
	 * @param pageNo
	 * @param pageSize
	 * @param token
	 * @return
	 */
	@POST
	@Path("/getAllCreditInfo")
	public Map<String, Object> getAllCreditInfo(@FormParam("token") String token, @FormParam("creditUserId") String creditUserId) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();

		if (StringUtils.isBlank(token) || StringUtils.isBlank(creditUserId)) {
			result.put("state", "2");
			result.put("message", "缺少必要参数");
			return result;
		}
		try {

			Cache cache = MemCachedUtis.getMemCached();
			Principal principal = cache.get(token);
			if (null != principal && null != principal.getCreditUserInfo()) {

				CreditInfo creditInfo = new CreditInfo();
				creditInfo.setCreditUserId(creditUserId);
				List<CreditInfo> creditInfoList = creditInfoService.findList(creditInfo);
				if (creditInfoList != null && creditInfoList.size() > 0) {
					for (int i = 0; i < creditInfoList.size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						creditInfo = creditInfoList.get(i);
						map.put("creditInfoId", creditInfo.getId());
						map.put("creditInfoName", creditInfo.getName() == null ? "" : creditInfo.getName());
						returnList.add(map);
					}
					data.put("creditInfoList", returnList);
					result.put("state", "0");
					result.put("message", "查询借款资料下拉框信息成功");
					result.put("data", data);
					return result;
				} else {
					data.put("creditInfoList", returnList);
					result.put("state", "0");
					result.put("message", "查无数据");
					result.put("data", data);
					return result;
				}
			} else {
				result.put("state", "4");
				result.put("message", "系统超时");
				result.put("data", "");
				return result;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "系统异常");
			result.put("state", "3");
			result.put("data", null);
			return result;
		}

	}
}
