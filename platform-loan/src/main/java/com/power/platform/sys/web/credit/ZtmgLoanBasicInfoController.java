package com.power.platform.sys.web.credit;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cn.tsign.ching.eSign.SignHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.electronic.ElectronicSignDao;
import com.power.platform.credit.dao.electronic.ElectronicSignTranstailDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfoDao;
import com.power.platform.credit.entity.annexfile.CreditAnnexFile;
import com.power.platform.credit.entity.electronic.ElectronicSign;
import com.power.platform.credit.entity.electronic.ElectronicSignTranstail;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanShareholdersInfo;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.pojo.ShareholdersInfoPojo;
import com.power.platform.credit.service.annexfile.CreditAnnexFileService;
import com.power.platform.credit.service.electronic.ElectronicSignService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.credit.service.ztmgLoanBasicInfo.ZtmgLoanBasicInfoService;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.utils.LoanPdfContractUtil;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;

/**
 * 
 * 类: ZtmgLoanBasicInfoController <br>
 * 描述: 借款人基本信息Controller. <br>
 * 作者: Mr.Roy <br>
 * 时间: 2018年4月29日 上午11:56:55
 */
@Controller
@RequestMapping(value = "${adminPath}/loan/basicinfo/ztmgLoanBasicInfo")
public class ZtmgLoanBasicInfoController extends BaseController {

	@Autowired
	private ZtmgLoanBasicInfoService ztmgLoanBasicInfoService;
	@Resource
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;
	@Resource
	private ZtmgLoanShareholdersInfoDao ztmgLoanShareholdersInfoDao;
	@Autowired
	private CreditAnnexFileService creditAnnexFileService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Autowired
	private ElectronicSignService electronicSignService;
	@Resource
	private ElectronicSignDao electronicSignDao;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private ElectronicSignTranstailDao electronicSignTranstailDao;

	@ModelAttribute
	public ZtmgLoanBasicInfo get(@RequestParam(required = false) String id) {

		ZtmgLoanBasicInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = ztmgLoanBasicInfoService.get(id);
		}
		if (entity == null) {
			entity = new ZtmgLoanBasicInfo();
		}
		return entity;
	}

	@RequiresPermissions("loan:basicinfo:ztmgLoanBasicInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ZtmgLoanBasicInfo ztmgLoanBasicInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<ZtmgLoanBasicInfo> page = ztmgLoanBasicInfoService.findPage(new Page<ZtmgLoanBasicInfo>(request, response), ztmgLoanBasicInfo);
		model.addAttribute("page", page);
		return "modules/loan/basicinfo/ztmgLoanBasicInfoList";
	}

	@RequiresPermissions("loan:basicinfo:ztmgLoanBasicInfo:view")
	@RequestMapping(value = "form")
	public String form(ZtmgLoanBasicInfo ztmgLoanBasicInfo, Model model) {

		model.addAttribute("ztmgLoanBasicInfo", ztmgLoanBasicInfo);

		return "modules/ztmgLoanBasicInfo/ztmgLoanBasicInfoForm";
	}

	/**
	 * 
	 * 方法: ztmgLoanBasicInfoForm <br>
	 * 描述: 借款人基本信息表单页. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年4月29日 下午4:45:36
	 * 
	 * @param ztmgLoanBasicInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "ztmgLoanBasicInfoForm")
	public String ztmgLoanBasicInfoForm(ZtmgLoanBasicInfo ztmgLoanBasicInfo, Model model) {

		// 征信报告.
		CreditAnnexFile annexFile = new CreditAnnexFile();
		annexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18);
		annexFile.setOtherId(ztmgLoanBasicInfo.getCreditUserId());
		List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFile);
		model.addAttribute("creditAnnexFileList", creditAnnexFileList);
		// 借款人帐号信息.
		CreditUserInfo creditUserInfo = creditUserInfoService.get(ztmgLoanBasicInfo.getCreditUserId());
		model.addAttribute("creditUserInfo", creditUserInfo);
		// 根据借款人查询借款人基本信息.
		ZtmgLoanBasicInfo ztmgLoanBasicInfoEntity = ztmgLoanBasicInfoDao.findByCreditUserId(ztmgLoanBasicInfo);
		if (null != ztmgLoanBasicInfoEntity) {
			// 根据借款人基本信息查询股东信息.
			ZtmgLoanShareholdersInfo entity = new ZtmgLoanShareholdersInfo();
			entity.setLoanBasicId(ztmgLoanBasicInfoEntity.getId());
			List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = ztmgLoanShareholdersInfoDao.findListByLoanBasicInfoId(entity);
			List<ShareholdersInfoPojo> shareholdersInfoPojos = new ArrayList<ShareholdersInfoPojo>();
			for (ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo : ztmgLoanShareholdersInfos) {
				ShareholdersInfoPojo shareholdersInfoPojo = new ShareholdersInfoPojo();
				shareholdersInfoPojo.setShareholdersType(ztmgLoanShareholdersInfo.getShareholdersType()); // 股东类型.
				shareholdersInfoPojo.setShareholdersCertType(ztmgLoanShareholdersInfo.getShareholdersCertType()); // 股东证件类型.
				shareholdersInfoPojo.setShareholdersName(ztmgLoanShareholdersInfo.getShareholdersName()); // 股东名称.
				shareholdersInfoPojos.add(shareholdersInfoPojo);
			}
			String shareholdersInfoPojoJsons = JSON.toJSONString(shareholdersInfoPojos);
			// 征信信息.
			if (StringUtils.isBlank(ztmgLoanBasicInfoEntity.getCreditInformation())) {
				ztmgLoanBasicInfoEntity.setCreditInformation("无逾期记录");
			}
			// 信用承诺书.
			String decFilePath = ztmgLoanBasicInfoEntity.getDeclarationFilePath();
			if (null != decFilePath) {
				ztmgLoanBasicInfoEntity.setDeclarationFilePath(decFilePath.split("data")[1]);
			}
			// 借款人基本信息.
			model.addAttribute("ztmgLoanBasicInfo", ztmgLoanBasicInfoEntity);
			// 借款人股东集合.
			model.addAttribute("shareholdersInfoPojoJsons", shareholdersInfoPojoJsons);
		} else {
			// 默认征信信息.
			ztmgLoanBasicInfo.setCreditInformation("无逾期记录");
			// 借款人基本信息.
			model.addAttribute("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
			// 借款人股东集合.
			model.addAttribute("shareholdersInfoPojoJsons", "[]");
		}

		return "modules/ztmgLoanBasicInfo/ztmgLoanBasicInfoForm";
	}

	/**
	 * 
	 * 方法: declarationFileSign <br>
	 * 描述: 声明文件签章. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月27日 下午1:27:38
	 * 
	 * @param ztmgLoanBasicInfo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "declarationFileSign")
	public Map<String, Object> declarationFileSign(ZtmgLoanBasicInfo ztmgLoanBasicInfo) {

		Map<String, Object> params = new HashMap<String, Object>();

		// 声明文件模版路径.
		String declarationFilePath = "";

		// 初始化项目，做全局使用，只初始化一次即可.
		SignHelper.initProject();

		try {
			// 开户时创建基本信息，签章时，永远做修改操作.
			if (StringUtils.isBlank(ztmgLoanBasicInfo.getId())) { // 新增.
			} else { // 修改.
				// 获取借款人基本信息.
				ztmgLoanBasicInfo = ztmgLoanBasicInfoService.get(ztmgLoanBasicInfo.getId());

				String declarationFilePathOld = ztmgLoanBasicInfo.getDeclarationFilePath();
				if (StringUtils.isBlank(declarationFilePathOld)) {
				} else { // 如果存在信用承诺书.
					// 文件删除.
					File file = new File(declarationFilePathOld);
					if (file.delete()) {
						logger.info("File delete success.");
					} else {
						logger.info("File delete failure.");
					}
				}

				// 融资主体.
				WloanSubject wloanSubject = new WloanSubject();
				wloanSubject.setLoanApplyId(ztmgLoanBasicInfo.getCreditUserId());
				List<WloanSubject> wloanSubjects = wloanSubjectService.findList(wloanSubject);
				if (wloanSubjects != null && wloanSubjects.size() > 0) {
					wloanSubject = wloanSubjects.get(0);
					if (null != wloanSubject) { // 融资主体非NULL判断.

						// PDF文件.
						declarationFilePath = LoanPdfContractUtil.createCreditPledgePdf(wloanSubject);

						int lastF = declarationFilePath.lastIndexOf("\\");
						if (lastF == -1) {
							lastF = declarationFilePath.lastIndexOf("//");
						}
						// 最终签署后的PDF文件路径
						String signedFolder = declarationFilePath.substring(0, lastF + 1);
						// 最终签署后PDF文件名称
						String signedFileName = declarationFilePath.substring(lastF + 1, declarationFilePath.length());

						/**
						 * 个人章.
						 */
						// 查询章的身份铭牌.
						String userOrganizeAccountId;
						ElectronicSign userElectronicSign = new ElectronicSign();
						userElectronicSign.setUserId(ztmgLoanBasicInfo.getCreditUserId());
						userElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_1);
						List<ElectronicSign> userElectronicSigns = electronicSignService.findList(userElectronicSign);
						if (userElectronicSigns != null && userElectronicSigns.size() > 0) {
							ElectronicSign userElectronicSignOld = userElectronicSigns.get(0);
							if (null != userElectronicSignOld) {
								userOrganizeAccountId = userElectronicSignOld.getSignId();
							} else {
								// CreditUserInfo userInfo = creditUserInfoDao.get(ztmgLoanBasicInfo.getCreditUserId());
								userOrganizeAccountId = SignHelper.addPersonAccountZTMGLoan(wloanSubject);
								userElectronicSign.setId(IdGen.uuid());
								userElectronicSign.setSignId(userOrganizeAccountId);
								userElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_1);
								userElectronicSign.setCreateDate(new Date());
								userElectronicSign.setUpdateDate(new Date());
								int insertFlag = electronicSignDao.insert(userElectronicSign);
								if (insertFlag == 1) {
									logger.info(this.getClass() + "-创建签章身份铭牌成功");
								} else {
									logger.info(this.getClass() + "-创建签章身份铭牌失败");
								}
							}
						} else {
							// CreditUserInfo userInfo = creditUserInfoDao.get(ztmgLoanBasicInfo.getCreditUserId());
							userOrganizeAccountId = SignHelper.addPersonAccountZTMGLoan(wloanSubject);
							userElectronicSign.setId(IdGen.uuid());
							userElectronicSign.setSignId(userOrganizeAccountId);
							userElectronicSign.setType(ElectronicSign.ELECTRONIC_SIGN_TYPE_1);
							userElectronicSign.setCreateDate(new Date());
							userElectronicSign.setUpdateDate(new Date());
							int insertFlag = electronicSignDao.insert(userElectronicSign);
							if (insertFlag == 1) {
								logger.info(this.getClass() + "-创建签章身份铭牌成功");
							} else {
								logger.info(this.getClass() + "-创建签章身份铭牌失败");
							}
						}
						// 个人电子签章（增加密封效果）.
						AddSealResult addSealResult = SignHelper.addPersonTemplateSeal(userOrganizeAccountId);
						// 类库JAR包不兼容.
						System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
						// 签章
						FileDigestSignResult userPersonSignResult = SignHelper.operNameSignByStreamCreditPledge(declarationFilePath, userOrganizeAccountId, addSealResult.getSealData());
						String serviceIdUser = userPersonSignResult.getSignServiceId();
						/**
						 * 公司章.
						 */
						String supplyOrganizeAccountId;
						ElectronicSign supplyUserElectronicSign = new ElectronicSign();
						supplyUserElectronicSign.setUserId(ztmgLoanBasicInfo.getCreditUserId());
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
									logger.info(this.getClass() + "-创建签章身份铭牌成功");
								} else {
									logger.info(this.getClass() + "-创建签章身份铭牌失败");
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
								logger.info(this.getClass() + "-创建签章身份铭牌成功");
							} else {
								logger.info(this.getClass() + "-创建签章身份铭牌失败");
							}
						}

						// 企业印章.
						AddSealResult supplyAddSealResult = SignHelper.addOrganizeTemplateSealZTMG(supplyOrganizeAccountId, wloanSubject);
						// 企业印章签署.
						FileDigestSignResult supplyFileDigestSignResult = SignHelper.companyNameSignByStreamCreditPledge(userPersonSignResult.getStream(), supplyOrganizeAccountId, supplyAddSealResult.getSealData());
						String serviceIdSupplyUser = supplyFileDigestSignResult.getSignServiceId();

						// 所有签署完成,将最终签署后的文件保存.
						if (0 == userPersonSignResult.getErrCode()) {
							if (null == serviceIdSupplyUser) { // 签章失败，保存未签章的PDF文件.
								// PDF文件.
								declarationFilePath = LoanPdfContractUtil.createCreditPledgePdf(wloanSubject);
								// 在签章之前，保存PDF文件.
								ztmgLoanBasicInfo.setDeclarationFilePath(declarationFilePath);
								ztmgLoanBasicInfo.setUpdateDate(new Date());
								int updateFlag_1 = ztmgLoanBasicInfoDao.update(ztmgLoanBasicInfo);
								if (updateFlag_1 == 1) {
									logger.info(this.getClass() + "-基本信息-保存信用承诺书路径成功");
								} else {
									logger.info(this.getClass() + "-基本信息-保存信用承诺书路径失败");
								}
							} else {
								SignHelper.saveSignedByStream(supplyFileDigestSignResult.getStream(), signedFolder, signedFileName);
								ElectronicSignTranstail electronicSignTranstail = new ElectronicSignTranstail();
								electronicSignTranstail.setId(IdGen.uuid());
								electronicSignTranstail.setSupplyId(ztmgLoanBasicInfo.getCreditUserId());
								electronicSignTranstail.setSignServiceIdUser(serviceIdUser);
								electronicSignTranstail.setSignServiceIdCore(serviceIdSupplyUser);
								electronicSignTranstail.setCreateDate(new Date());
								int insertFlag = electronicSignTranstailDao.insert(electronicSignTranstail);
								if (insertFlag == 1) {
									logger.info(this.getClass() + "-签章流水保存成功");
									ztmgLoanBasicInfo.setDeclarationFilePath(declarationFilePath);
									ztmgLoanBasicInfo.setUpdateDate(new Date());
									int updateFlag_2 = ztmgLoanBasicInfoDao.update(ztmgLoanBasicInfo);
									if (updateFlag_2 == 1) {
										logger.info(this.getClass() + "-基本信息-保存信用承诺书路径成功");
									} else {
										logger.info(this.getClass() + "-基本信息-保存信用承诺书路径失败");
									}
								} else {
									logger.info(this.getClass() + "-签章流水保存失败");
								}
							}
						}
					}
				}

				// 信用承诺书.
				String decFilePath = ztmgLoanBasicInfo.getDeclarationFilePath();
				if (null != decFilePath) {
					ztmgLoanBasicInfo.setDeclarationFilePath(decFilePath.split("data")[1]);
				}
				// 借款人基本信息.
				params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
				// 信用承诺书路径.
				params.put("declarationFilePath", declarationFilePath);
				// 消息提醒.
				params.put("message", "信用承诺书创建成功，可点击查看");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return params;
	}

	/**
	 * 
	 * 方法: ztmgLoanBasicInfoSave <br>
	 * 描述: 保存借款人基本信息. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年4月30日 下午4:12:16
	 * 
	 * @param ztmgLoanBasicInfo
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "ztmgLoanBasicInfoSave")
	public Map<String, Object> ztmgLoanBasicInfoSave(ZtmgLoanBasicInfo ztmgLoanBasicInfo) {

		Map<String, Object> params = new HashMap<String, Object>();

		try {

			// 保存借款人基本信息.
			if (StringUtils.isBlank(ztmgLoanBasicInfo.getId())) { // 新增.

				// 更新借款人帐号信息，是否完善基本信息字段.
				CreditUserInfo creditUserInfo = creditUserInfoService.get(ztmgLoanBasicInfo.getCreditUserId());
				if (creditUserInfo != null) {
					if (creditUserInfo.getCreditUserType() != null) {
						if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_11)) {
							// 核心企业，是否上传征信报告，不做判断.
						} else {
							// 在新增/修改-之前判断是否上上传征信报告.
//							CreditAnnexFile annexFile = new CreditAnnexFile();
//							annexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18);
//							annexFile.setOtherId(ztmgLoanBasicInfo.getCreditUserId());
//							List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFile);
//							if (null == creditAnnexFileList) {
//								params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
//								params.put("message", "请上传征信报告");
//								return params;
//							} else {
//								if (creditAnnexFileList.size() == 0) {
//									params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
//									params.put("message", "请上传征信报告");
//									return params;
//								}
//							}
						}
					}
				}

				String ztmgLoanBasicInfoId = IdGen.uuid();
				ztmgLoanBasicInfo.setId(ztmgLoanBasicInfoId);
				ztmgLoanBasicInfo.setCreateDate(new Date());
				ztmgLoanBasicInfo.setUpdateDate(new Date());
				ztmgLoanBasicInfo.setRemark("借款人基本信息");
				int insertFlag_1 = ztmgLoanBasicInfoDao.insert(ztmgLoanBasicInfo);
				if (insertFlag_1 == 1) {
					// logger.info(this.getClass() + "-新增借款人基本信息成功");
					// 股东信息集合.
					List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = new ArrayList<ZtmgLoanShareholdersInfo>();
					String shareholdersJsonArrayStr = ztmgLoanBasicInfo.getShareholdersJsonArrayStr();
					// logger.info("shareholdersJsonArrayStr = " +
					// shareholdersJsonArrayStr);
					String unescapeHtml4 = StringEscapeUtils.unescapeHtml4(shareholdersJsonArrayStr);
					// logger.info("unescapeHtml4 = " + unescapeHtml4);
					JSONArray array = JSONArray.parseArray(unescapeHtml4);
					// logger.info(this.getClass() + "-JSON数组大小 = " +
					// array.size());
					// 遍历JSON数组.
					int a = 0;
					for (int i = 0; i < array.size(); i++) {
						JSONObject jsonObject = array.getJSONObject(i);
						ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo = JSON.toJavaObject(jsonObject, ZtmgLoanShareholdersInfo.class);
						ztmgLoanShareholdersInfo.setId(IdGen.uuid());
						ztmgLoanShareholdersInfo.setLoanBasicId(ztmgLoanBasicInfoId);
						ztmgLoanShareholdersInfo.setCreateDate(new Date(System.currentTimeMillis() + 1000 * ++a));
						ztmgLoanShareholdersInfo.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * ++a));
						ztmgLoanShareholdersInfo.setRemark("借款人股东信息");
						int insertFlag_2 = ztmgLoanShareholdersInfoDao.insert(ztmgLoanShareholdersInfo);
						if (insertFlag_2 == 1) {
							logger.info(this.getClass() + "-新增借款人股东信息成功");
							ztmgLoanShareholdersInfos.add(ztmgLoanShareholdersInfo);
						} else {
							logger.info(this.getClass() + "-新增借款人股东信息失败");
						}
						// logger.info(this.getClass() + "-股东信息类-" + ztmgLoanShareholdersInfo.toString());
					}
					// 回显股东信息集合.
					ztmgLoanBasicInfo.setZtmgLoanShareholdersInfos(ztmgLoanShareholdersInfos);

					/**
					 * 更新借款人帐号信息-是否完善借款人基本信息字段.
					 */
					creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_1);
					int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
					if (creditUserInfoUpdateFlag == 1) {
						logger.info(this.getClass() + "fn:ztmgLoanBasicInfoSave-更新借款人帐号信息-是否完善借款人基本信息字段-成功");
					} else {
						logger.info(this.getClass() + "fn:ztmgLoanBasicInfoSave-更新借款人帐号信息-是否完善借款人基本信息字段-失败");
					}

					// 信用承诺书.
					String decFilePath = ztmgLoanBasicInfo.getDeclarationFilePath();
					if (null != decFilePath) {
						ztmgLoanBasicInfo.setDeclarationFilePath(decFilePath.split("data")[1]);
					}
					// 借款人基本信息.
					params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
					// 消息提醒.
					params.put("message", "提交新增基本信息成功");
				} else {
					logger.info(this.getClass() + "-新增借款人基本信息失败");
				}
			} else { // 修改.

				/**
				 * 校验基本信息中提交失败的字段.
				 */
				if (null != ztmgLoanBasicInfo) {
					if (StringUtils.isBlank(ztmgLoanBasicInfo.getCompanyName())) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请填写公司名称信息");
						return params;
					} else if (StringUtils.isBlank(ztmgLoanBasicInfo.getOperName())) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请填写公司法人代表信息");
						return params;
					} else if (StringUtils.isBlank(ztmgLoanBasicInfo.getRegisteredAddress())) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请填写注册地址信息");
						return params;
					} else if (StringUtils.isBlank(DateUtils.formatDate(ztmgLoanBasicInfo.getSetUpTime(), "yyyy-MM-dd HH:mm:ss"))) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请填写成立时间信息");
						return params;
					} else if (StringUtils.isBlank(ztmgLoanBasicInfo.getRegisteredCapital())) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请填写注册资本信息");
						return params;
					} else if (StringUtils.isBlank(ztmgLoanBasicInfo.getScope())) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请填写经营区域信息");
						return params;
					}
					/**
					else if (StringUtils.isBlank(ztmgLoanBasicInfo.getDeclarationFilePath())) {
						// 借款人基本信息.
						params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
						// 消息提醒.
						params.put("message", "请阅读信用承诺书，并同意授权生成信用承诺书");
						return params;
					}
					*/
				}

				// 更新借款人帐号信息，是否完善基本信息字段.
				CreditUserInfo creditUserInfo = creditUserInfoService.get(ztmgLoanBasicInfo.getCreditUserId());
				if (creditUserInfo != null) {
					if (creditUserInfo.getCreditUserType() != null) {
						if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_11)) {
							// 核心企业，是否上传征信报告，不做判断.
						} else {
							// 在新增/修改-之前判断是否上上传征信报告.
//							CreditAnnexFile annexFile = new CreditAnnexFile();
//							annexFile.setType(CreditAnnexFileService.CREDIT_ANNEX_FILE_TYPE_18);
//							annexFile.setOtherId(ztmgLoanBasicInfo.getCreditUserId());
//							List<CreditAnnexFile> creditAnnexFileList = creditAnnexFileService.findCreditAnnexFileList(annexFile);
//							if (null == creditAnnexFileList) {
//								params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
//								params.put("message", "请上传征信报告");
//								return params;
//							} else {
//								if (creditAnnexFileList.size() == 0) {
//									params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
//									params.put("message", "请上传征信报告");
//									return params;
//								}
//							}
						}
					}
				}

				ztmgLoanBasicInfo.setUpdateDate(new Date());
				logger.info(this.getClass() + "fn:ztmgLoanBasicInfoSave-更新借款人基本信息-" + ztmgLoanBasicInfo.toString());
				int updateFlag_1 = ztmgLoanBasicInfoDao.ztmgLoanBasicInfoUpdate(ztmgLoanBasicInfo);
				if (updateFlag_1 == 1) { // 修改成功.
					// logger.info(this.getClass() + "-修改借款人基本信息成功");
					// 根据借款人基本信息查询股东信息.
					ZtmgLoanShareholdersInfo entity = new ZtmgLoanShareholdersInfo();
					entity.setLoanBasicId(ztmgLoanBasicInfo.getId());
					List<ZtmgLoanShareholdersInfo> list = ztmgLoanShareholdersInfoDao.findListByLoanBasicInfoId(entity);
					// 遍历股东信息，将其删除.
					for (ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo : list) {
						int deleteFlag = ztmgLoanShareholdersInfoDao.delete(ztmgLoanShareholdersInfo);
						if (deleteFlag == 1) {
							// logger.info(this.getClass() + "-删除借款人股东信息成功");
						} else {
							// logger.info(this.getClass() + "-删除借款人股东信息失败");
						}
					}
					// 新增股东信息-股东信息集合.
					List<ZtmgLoanShareholdersInfo> ztmgLoanShareholdersInfos = new ArrayList<ZtmgLoanShareholdersInfo>();
					String shareholdersJsonArrayStr = ztmgLoanBasicInfo.getShareholdersJsonArrayStr();
					// logger.info("shareholdersJsonArrayStr = " +
					// shareholdersJsonArrayStr);
					String unescapeHtml4 = StringEscapeUtils.unescapeHtml4(shareholdersJsonArrayStr);
					// logger.info("unescapeHtml4 = " + unescapeHtml4);
					JSONArray array = JSONArray.parseArray(unescapeHtml4);
					// logger.info(this.getClass() + "-JSON数组大小 = " +
					// array.size());
					// 遍历JSON数组.
					int a = 0;
					for (int i = 0; i < array.size(); i++) {
						JSONObject jsonObject = array.getJSONObject(i);
						ZtmgLoanShareholdersInfo ztmgLoanShareholdersInfo = JSON.toJavaObject(jsonObject, ZtmgLoanShareholdersInfo.class);
						ztmgLoanShareholdersInfo.setId(IdGen.uuid());
						ztmgLoanShareholdersInfo.setLoanBasicId(ztmgLoanBasicInfo.getId());
						ztmgLoanShareholdersInfo.setCreateDate(new Date(System.currentTimeMillis() + 1000 * ++a));
						ztmgLoanShareholdersInfo.setUpdateDate(new Date(System.currentTimeMillis() + 1000 * ++a));
						ztmgLoanShareholdersInfo.setRemark("借款人股东信息");
						int insertFlag_2 = ztmgLoanShareholdersInfoDao.insert(ztmgLoanShareholdersInfo);
						if (insertFlag_2 == 1) {
							// logger.info(this.getClass() + "-新增借款人股东信息成功");
							ztmgLoanShareholdersInfos.add(ztmgLoanShareholdersInfo);
						} else {
							// logger.info(this.getClass() + "-新增借款人股东信息失败");
						}
						// logger.info(this.getClass() + "-股东信息类-" +
						// ztmgLoanShareholdersInfo.toString());
					}
					// 回显股东信息集合.
					ztmgLoanBasicInfo.setZtmgLoanShareholdersInfos(ztmgLoanShareholdersInfos);

					/**
					 * 更新借款人帐号信息-是否完善借款人基本信息字段.
					 */
					creditUserInfo.setIsCreateBasicInfo(CreditUserInfo.IS_CREATE_BASIC_INFO_1);
					int creditUserInfoUpdateFlag = creditUserInfoDao.update(creditUserInfo);
					if (creditUserInfoUpdateFlag == 1) {
						logger.info(this.getClass() + "fn:ztmgLoanBasicInfoSave-更新借款人帐号信息-是否完善借款人基本信息字段-成功");
					} else {
						logger.info(this.getClass() + "fn:ztmgLoanBasicInfoSave-更新借款人帐号信息-是否完善借款人基本信息字段-失败");
					}

					// 信用承诺书.
					String decFilePath = ztmgLoanBasicInfo.getDeclarationFilePath();
					if (null != decFilePath) {
						ztmgLoanBasicInfo.setDeclarationFilePath(decFilePath.split("data")[1]);
					}
					// 借款人基本信息.
					params.put("ztmgLoanBasicInfo", ztmgLoanBasicInfo);
					// 消息提醒.
					params.put("message", "提交修改基本信息成功");
				} else {
					// logger.info(this.getClass() + "-修改借款人基本信息失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	@RequiresPermissions("loan:basicinfo:ztmgLoanBasicInfo:edit")
	@RequestMapping(value = "save")
	public String save(ZtmgLoanBasicInfo ztmgLoanBasicInfo, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, ztmgLoanBasicInfo)) {
			return form(ztmgLoanBasicInfo, model);
		}
		ztmgLoanBasicInfoService.save(ztmgLoanBasicInfo);
		addMessage(redirectAttributes, "保存借款人基本信息成功");
		return "redirect:" + Global.getAdminPath() + "/loan/basicinfo/ztmgLoanBasicInfo/?repage";
	}

	@RequiresPermissions("loan:basicinfo:ztmgLoanBasicInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(ZtmgLoanBasicInfo ztmgLoanBasicInfo, RedirectAttributes redirectAttributes) {

		ztmgLoanBasicInfoService.delete(ztmgLoanBasicInfo);
		addMessage(redirectAttributes, "删除借款人基本信息成功");
		return "redirect:" + Global.getAdminPath() + "/loan/basicinfo/ztmgLoanBasicInfo/?repage";
	}

	public JSONArray ZtmgLoanShareholdersInfoListToJson(List<ZtmgLoanShareholdersInfo> list) {

		JSONArray json = new JSONArray();
		for (ZtmgLoanShareholdersInfo zlsi : list) {
			JSONObject jo = new JSONObject();
			jo.put("id", zlsi.getId());
			jo.put("shareholdersType", zlsi.getShareholdersType());
			jo.put("shareholdersCertType", zlsi.getShareholdersCertType());
			jo.put("shareholdersName", zlsi.getShareholdersName());
		}
		return json;
	}

}