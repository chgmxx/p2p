package com.power.platform.demo.service;

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

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.power.platform.bouns.dao.UserBounsHistoryDao;
import com.power.platform.bouns.dao.UserBounsPointDao;
import com.power.platform.bouns.dao.UserSignedDao;
import com.power.platform.bouns.entity.UserBounsHistory;
import com.power.platform.bouns.entity.UserBounsPoint;
import com.power.platform.bouns.services.UserBounsHistoryService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.FileUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.MergeFileUtils;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.PdfGenerateTables;
import com.power.platform.common.utils.PdfUtils;
import com.power.platform.common.utils.Util;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.entity.WloanTermUserPlan;
import com.power.platform.plan.service.WloanTermUserPlanService;
import com.power.platform.regular.dao.WGuaranteeCompanyDao;
import com.power.platform.regular.dao.WloanSubjectDao;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.userinfo.dao.UserInfoDao;
import com.power.platform.userinfo.entity.UserInfo;

@Path("/cicmorgan/create")
@Service("createTheOldVersionLoanContractService")
@Produces(MediaType.APPLICATION_JSON)
public class CreateTheOldVersionLoanContractService {

	private static final Logger log = LoggerFactory.getLogger(CreateTheOldVersionLoanContractService.class);

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
	 * 中投摩根电子签章.
	 */
	private static final String ZTMG_ELECTRONIC_SIGNATURE_IMAGE = "ztmg_electronic_sign.png";

	/**
	 * 电子签章URL.
	 */
	private static final String ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL = Global.getConfig("ZTMG.ELECTRONIC.SIGNATURE.IMAGE");

	/**
	 * 主域名.
	 */
	private static final String DOMAIN_NAME = "https://www.cicmorgan.com";

	/**
	 * 输出流.
	 */
	private static OutputStream fos;

	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanSubjectDao wloanSubjectDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private WGuaranteeCompanyDao wGuaranteeCompanyDao;
	@Resource
	private UserSignedDao userSignedDao;
	@Resource
	private UserBounsPointDao userBounsPointDao;
	@Resource
	private UserBounsHistoryDao userBounsHistoryDao;

	public static final String BOUNDS_TYPE_0 = "出借";
	public static final String BOUNDS_TYPE_1 = "注册";
	public static final String BOUNDS_TYPE_2 = "邀请好友";
	public static final String BOUNDS_TYPE_3 = "签到";
	public static final String BOUNDS_TYPE_4 = "积分抽奖";
	public static final String BOUNDS_TYPE_5 = "积分兑换";
	public static final String BOUNDS_TYPE_6 = "好友出借";

	/**
	 * 
	 * methods: adjustUserSigned <br>
	 * description: 2019五一假期期间签到双倍积分调整. <br>
	 * author: Roy <br>
	 * date: 2019年5月5日 下午6:05:50
	 * 
	 * @param userId
	 * @return
	 */
	@POST
	@Path("/adjustUserSigned")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, Object> adjustUserSigned(@FormParam("userId") String userId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		if (StringUtils.isBlank(userId)) {
			log.info("fn:adjustUserSigned，缺少必要参数.");
			result.put("respCode", "02");
			result.put("respMsg", "缺少必要参数.");
			return result;
		}

		try {

			// CurrentAmount
			Double tempCurrentAmount = 0D;
			// 标志位重复数据处理.
			boolean isExist_51 = false;
			// 标志位重复数据处理.
			boolean isExist_52 = false;
			// 标志位重复数据处理.
			boolean isExist_53 = false;
			// 标志位重复数据处理.
			boolean isExist_54 = false;
			// 标志位重复数据处理.
			boolean isExist_55 = false;
			// 标志位重复数据处理.
			boolean isExist_56 = false;

			//
			UserBounsHistory entity = new UserBounsHistory();
			entity.setUserId(userId);
			entity.setBeginCreateDate("2019-05-01 00:00:00");
			entity.setEndCreateDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			Page<UserBounsHistory> page = new Page<UserBounsHistory>();
			page.setOrderBy("a.create_date ASC");
			entity.setPage(page);
			List<UserBounsHistory> list = userBounsHistoryDao.findList(entity);

			for (int i = 0; i < list.size(); i++) {
				UserBounsHistory model = list.get(i);
				if (i == 0) {
					tempCurrentAmount = NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount());
					// System.out.println("调整前积分总额：" + tempCurrentAmount);
					log.info("fn:adjustUserSigned，调整前积分总额：" + tempCurrentAmount);
					if (BOUNDS_TYPE_3.equals(model.getBounsType())) { // 签到积分记录调整.
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_SIGNED); // 签到.
						if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-01"))) {
							isExist_51 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-02"))) {
							isExist_52 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-03"))) {
							isExist_53 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-04"))) {
							isExist_54 = true;
							tempCurrentAmount = NumberUtils.add(NumberUtils.subtract(Double.valueOf(model.getCurrentAmount()), model.getAmount()), NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-05"))) {
							if (isExist_55) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							} else {
								isExist_55 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-06"))) {
							if (isExist_56) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							} else {
								isExist_56 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							}
						} else {
							tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
						}
					} else if (BOUNDS_TYPE_0.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST); // 出借.
					} else if (BOUNDS_TYPE_1.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST); // 注册.
					} else if (BOUNDS_TYPE_2.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REQUEST); // 邀请好友.
					} else if (BOUNDS_TYPE_4.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW); // 抽奖.
					} else if (BOUNDS_TYPE_5.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_CASH_LOTTERY); // 兑换奖品.
					} else if (BOUNDS_TYPE_6.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST); // 好友投资.
					}
					int update = userBounsHistoryDao.update(model);
					if (update == 1) {
						// System.out.println("更新客户积分流水成功 ...");
						log.info("fn:adjustUserSigned，更新客户积分流水成功 ...");
					} else {
						// System.out.println("更新客户积分流水失败 ...");
						log.info("fn:adjustUserSigned，更新客户积分流水失败 ...");
					}
					// System.out.println("积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					log.info("fn:adjustUserSigned，调整数据：\t" + "积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					if (BOUNDS_TYPE_3.equals(model.getBounsType())) { // 签到积分记录调整.
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_SIGNED); // 签到.
						if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-01"))) {
							if (isExist_51) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_51 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-02"))) {
							if (isExist_52) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_52 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-03"))) {
							if (isExist_53) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_53 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-04"))) {
							if (isExist_54) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							} else {
								isExist_54 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, NumberUtils.multiply(model.getAmount(), 2D)); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
								model.setAmount(NumberUtils.multiply(model.getAmount(), 2D)); // 假期双倍积分.
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-05"))) {
							if (isExist_55) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							} else {
								isExist_55 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							}
						} else if (DateUtils.isSameDate(model.getCreateDate(), DateUtils.parseDate("2019-05-06"))) {
							if (isExist_56) {
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							} else {
								isExist_56 = true;
								tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
								model.setCurrentAmount(String.valueOf(tempCurrentAmount));
							}
						} else {
							tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount()); // 调整后积分余额.
							model.setCurrentAmount(String.valueOf(tempCurrentAmount));
						}
					} else if (BOUNDS_TYPE_0.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_INVEST); // 出借.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_1.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REGIST); // 注册.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_2.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_REQUEST); // 邀请好友.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_4.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_LOTTERY_DRAW); // 抽奖.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_5.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_CASH_LOTTERY); // 兑换奖品.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else if (BOUNDS_TYPE_6.equals(model.getBounsType())) {
						model.setBounsType(UserBounsHistoryService.BOUNS_TYPE_FRIEND_INVEST); // 好友投资.
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					} else {
						tempCurrentAmount = NumberUtils.add(tempCurrentAmount, model.getAmount());
						model.setCurrentAmount(String.valueOf(tempCurrentAmount));
					}
					int update = userBounsHistoryDao.update(model);
					if (update == 1) {
						// System.out.println("更新客户积分流水成功 ...");
						log.info("fn:adjustUserSigned，更新客户积分流水成功 ...");
					} else {
						// System.out.println("更新客户积分流水失败 ...");
						log.info("fn:adjustUserSigned，更新客户积分流水失败 ...");
					}
					// System.out.println("积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					log.info("fn:adjustUserSigned，调整数据：\t" + "积分类型：" + model.getBounsType() + "\t获得积分：" + model.getAmount() + "\t积分余额：" + model.getCurrentAmount() + "\t创建日期：" + DateUtils.formatDate(model.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
				}
			}
			UserBounsPoint userBounsPoint = new UserBounsPoint();
			userBounsPoint.setUserId(userId);
			List<UserBounsPoint> userBounsPointList = userBounsPointDao.findList(userBounsPoint);
			if (userBounsPointList.size() > 0) {
				UserBounsPoint userBounsPointAccount = userBounsPointList.get(0);
				userBounsPointAccount.setScore(tempCurrentAmount.intValue());
				int update = userBounsPointDao.update(userBounsPointAccount);
				if (update == 1) {
					// System.out.println("更新客户积分账户成功 ...");
					log.info("fn:adjustUserSigned，更新客户积分账户成功 ...");
				} else {
					// System.out.println("更新客户积分账户失败 ...");
					log.info("fn:adjustUserSigned，更新客户积分账户失败 ...");

				}
			}

			// 接口响应.
			result.put("respCode", "00");
			result.put("respMsg", "2019年五一假期期间签到双倍积分调整完毕.");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("fn:adjustUserSigned，程序异常！");
			result.put("respCode", "01");
			result.put("respMsg", "程序异常！");
			return result;
		}

		return result;
	}

	/**
	 * 
	 * 方法: additionalInvestInfoFormByProjectId <br>
	 * 描述: 追加出借信息表单，单个项目执行。 <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年11月2日 上午9:13:20
	 * 
	 * @param projectId
	 * @return
	 */
	@POST
	@Path("/additionalInvestInfoFormByProjectId")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> additionalInvestInfoFormByProjectId(@FormParam("projectId") String projectId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();

		WloanTermProject project = wloanTermProjectDao.get(projectId);
		if (null != project) { // 项目信息.
			List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(project.getId());
			for (WloanTermInvest invest : investList) {
				if (WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1.equals(invest.getState())) { // 出借成功.
					// 客户帐号信息.
					// UserInfo userInfo = userInfoDao.get(invest.getUserId());
					// 出借记录还款计划.
					// WloanTermUserPlan userPlan = new WloanTermUserPlan();
					WloanTermInvest wloanTermInvest = wloanTermInvestDao.get(invest.getId());
					// userPlan.setWloanTermInvest(wloanTermInvest);
					// List<WloanTermUserPlan> userPlans = wloanTermUserPlanDao.findList(userPlan);
					try {
						List<WloanTermUserPlan> userPlans = null;
						// 合同路径.
						String contractPdfPath = createInvestInfoForm(null, project, invest, userPlans);

						if (null != contractPdfPath) {
							boolean contains = contractPdfPath.contains("data");
							if (contains) {
								// 更新合同路径.
								wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
							} else {
								// 更新合同路径.
								wloanTermInvest.setContractPdfPath(contractPdfPath);
							}
							int updateInvestFlag = wloanTermInvestDao.update(wloanTermInvest);
							if (updateInvestFlag == 1) {
								log.info(this.getClass() + "保存合同路径成功");
							} else {
								log.info(this.getClass() + "保存合同路径失败");
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		result.put("msg", "出借合同追加出借人出借信息，批处理成功");
		return result;

	}

	public static String createInvestInfoForm(UserInfo userInfo, WloanTermProject project, WloanTermInvest wloanTermInvest, List<WloanTermUserPlan> userPlans) throws Exception {

		// 附表《出借人出借信息表》.
		String title = "附表《出借人出借信息表》";
		// rowTitle.
		String[] rowTitle = new String[] { "出借人", "出借金额（元）", "出借期限（天）", "出借年化收益率（%）" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = new String[rowTitle.length];
		if (null != wloanTermInvest) {
			if (wloanTermInvest.getUserInfo() != null) {
				strings[0] = wloanTermInvest.getUserInfo().getRealName();
			}
			strings[1] = fmtMicrometer(NumberUtils.scaleDoubleStr(wloanTermInvest.getAmount()));
			if (wloanTermInvest.getWloanTermProject() != null) {
				strings[2] = wloanTermInvest.getWloanTermProject().getSpan().toString();
				strings[3] = NumberUtils.scaleDoubleStr(wloanTermInvest.getWloanTermProject().getAnnualRate());
			}
		}
		dataList.add(strings);

		return mergeContractByInvestInfoForm(wloanTermInvest, title, rowTitle, dataList);
	}

	private static String mergeContractByInvestInfoForm(WloanTermInvest wloanTermInvest, String title, String[] rowTitle, ArrayList<String[]> dataList) {

		// 新文件名称.
		String newFileName = IdGen.uuid() + ".pdf";
		// newFileNamePath online.
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		// newFileNamePath test.
		// String newFileNamePath = "D:" + File.separator + "pdf" + File.separator + DateUtils.getFileDate();
		if (null != wloanTermInvest.getContractPdfPath()) {
			// 已存在的合同路径.
			String oldFilePath = DOMAIN_NAME + wloanTermInvest.getContractPdfPath();

			// 创建目录.
			FileUtils.createDirectory(FileUtils.path(newFileNamePath));
			// online table path.
			String tablePath = OUT_PATH + "Invest_Info_Form_Table.pdf";
			// test table path.
			// String tablePath = "D:" + File.separator + "pdf" + File.separator + "Invest_Info_Form_Table.pdf";
			// 生成代表格的数据.
			PdfGenerateTables.generateAllParts(title, rowTitle, dataList, tablePath);
			// 安心投借款合同模版路径和表格数据路径.
			String[] files = new String[] { oldFilePath, tablePath };
			// 将带表格的数据合并到安心投借款合同中.
			if (MergeFileUtils.mergePdfFiles(files, FileUtils.path(newFileNamePath + File.separator + newFileName))) {
				log.info("PDF合并 成功！");
			} else {
				log.info("PDF合并 失败！");
			}
		} else {
			return null;
		}

		return newFileNamePath + File.separator + newFileName;
	}

	/**
	 * 
	 * 方法: exceptionLoanUserContractByProject <br>
	 * 描述: 旧版出借合同异常处理，单个项目执行。 <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年11月1日 下午3:51:05
	 * 
	 * @param projectId
	 * @return
	 */
	@POST
	@Path("/exceptionLoanUserContractByProject")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> exceptionLoanUserContractByProject(@FormParam("projectId") String projectId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();

		WloanTermProject project = wloanTermProjectDao.get(projectId);
		if (null != project) {
			List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(project.getId());
			for (WloanTermInvest invest : investList) {
				if (WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1.equals(invest.getState()) || WloanTermInvestService.WLOAN_TERM_INVEST_STATE_9.equals(invest.getState())) { // 出借成功.
					// 客户帐号信息.
					UserInfo userInfo = userInfoDao.get(invest.getUserId());
					// 项目信息.
					WloanTermProject projectEntity = wloanTermProjectDao.get(invest.getProjectId());
					// 融资主体.
					WloanSubject subject = null;
					// 担保机构.
					WGuaranteeCompany wGuaranteeCompany = null;
					if (projectEntity != null) {
						subject = wloanSubjectDao.get(projectEntity.getSubjectId());
						wGuaranteeCompany = wGuaranteeCompanyDao.get(projectEntity.getGuaranteeId());
					}
					// 出借记录还款计划.
					WloanTermUserPlan userPlan = new WloanTermUserPlan();
					WloanTermInvest wloanTermInvest = wloanTermInvestDao.get(invest.getId());
					userPlan.setWloanTermInvest(wloanTermInvest);
					List<WloanTermUserPlan> userPlans = wloanTermUserPlanDao.findList(userPlan);
					try {
						// 合同路径.
						String contractPdfPath = createTheOldVersionLoanContract(userInfo, projectEntity, subject, wGuaranteeCompany, wloanTermInvest, userPlans);

						boolean contains = contractPdfPath.contains("data");
						if (contains) {
							// 更新合同路径.
							wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
						} else {
							// 更新合同路径.
							wloanTermInvest.setContractPdfPath(contractPdfPath);
						}
						int updateInvestFlag = wloanTermInvestDao.update(wloanTermInvest);
						if (updateInvestFlag == 1) {
							log.info(this.getClass() + "保存合同路径成功");
						} else {
							log.info(this.getClass() + "保存合同路径失败");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		result.put("msg", "异常项目-出借合同处理，批处理成功");
		return result;

	}

	@POST
	@Path("/theLoanCompanyNameReplaceLoanUserByLoanContract")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> theLoanCompanyNameReplaceLoanUserContract() {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();

		WloanTermProject entity = new WloanTermProject();
		entity.setProjectProductType(WloanTermProjectService.PROJECT_PRODUCT_TYPE_1); // 产品类型.
		List<String> stateItem = new ArrayList<String>();
		stateItem.add(WloanTermProjectService.FULL);
		stateItem.add(WloanTermProjectService.REPAYMENT);
		entity.setStateItem(stateItem);
		entity.setEndTimeToOnline("2018-04-09 00:00:00");
		List<WloanTermProject> projectList = wloanTermProjectDao.findList(entity);
		for (WloanTermProject project : projectList) {
			List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(project.getId());
			for (WloanTermInvest invest : investList) {
				if (WloanTermInvestService.WLOAN_TERM_INVEST_STATE_1.equals(invest.getState())) { // 出借成功.
					// 客户帐号信息.
					UserInfo userInfo = userInfoDao.get(invest.getUserId());
					// 项目信息.
					WloanTermProject projectEntity = wloanTermProjectDao.get(invest.getProjectId());
					// 融资主体.
					WloanSubject subject = null;
					// 担保机构.
					WGuaranteeCompany wGuaranteeCompany = null;
					if (projectEntity != null) {
						subject = wloanSubjectDao.get(projectEntity.getSubjectId());
						wGuaranteeCompany = wGuaranteeCompanyDao.get(projectEntity.getGuaranteeId());
					}
					// 出借记录还款计划.
					WloanTermUserPlan userPlan = new WloanTermUserPlan();
					WloanTermInvest wloanTermInvest = wloanTermInvestDao.get(invest.getId());
					userPlan.setWloanTermInvest(wloanTermInvest);
					List<WloanTermUserPlan> userPlans = wloanTermUserPlanDao.findList(userPlan);
					try {
						// 合同路径.
						String contractPdfPath = createTheOldVersionLoanContract(userInfo, projectEntity, subject, wGuaranteeCompany, wloanTermInvest, userPlans);

						boolean contains = contractPdfPath.contains("data");
						if (contains) {
							// 更新合同路径.
							wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
						} else {
							// 更新合同路径.
							wloanTermInvest.setContractPdfPath(contractPdfPath);
						}
						int updateInvestFlag = wloanTermInvestDao.update(wloanTermInvest);
						if (updateInvestFlag == 1) {
							log.info(this.getClass() + "保存合同路径成功");
						} else {
							log.info(this.getClass() + "保存合同路径失败");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		result.put("msg", "出借合同，变更借款人为借款人企业，批处理成功");
		return result;

	}

	@POST
	@Path("/theOldVersionLoanContract")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> theOldVersionLoanContract() {

		log.info(this.getClass() + "出借合同为空串或者为NUll的数据处理");
		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();

		// 出借合同为空字符串的数据.
		List<WloanTermInvest> emptyStringList = wloanTermInvestDao.findContractPdfPathAnEmptyString();
		if (emptyStringList != null && emptyStringList.size() >= 1) {
			for (WloanTermInvest emptyStringInvest : emptyStringList) {
				// 客户帐号信息.
				UserInfo userInfo = userInfoDao.get(emptyStringInvest.getUserId());
				// 项目信息.
				WloanTermProject project = wloanTermProjectDao.get(emptyStringInvest.getProjectId());
				// 融资主体.
				WloanSubject subject = null;
				// 担保机构.
				WGuaranteeCompany wGuaranteeCompany = null;
				if (project != null) {
					subject = wloanSubjectDao.get(project.getSubjectId());
					wGuaranteeCompany = wGuaranteeCompanyDao.get(project.getGuaranteeId());
				}
				// 出借记录还款计划.
				WloanTermUserPlan entity = new WloanTermUserPlan();
				WloanTermInvest wloanTermInvest = wloanTermInvestDao.get(emptyStringInvest.getId());
				entity.setWloanTermInvest(wloanTermInvest);
				List<WloanTermUserPlan> plans = wloanTermUserPlanDao.findList(entity);

				try {
					// 合同路径.
					String contractPdfPath = createTheOldVersionLoanContract(userInfo, project, subject, wGuaranteeCompany, wloanTermInvest, plans);

					boolean contains = contractPdfPath.contains("data");
					if (contains) {
						// 更新合同路径.
						wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
					} else {
						// 更新合同路径.
						wloanTermInvest.setContractPdfPath(contractPdfPath);
					}
					int updateInvestFlag = wloanTermInvestDao.update(wloanTermInvest);
					if (updateInvestFlag == 1) {
						log.info(this.getClass() + "保存合同路径成功");
					} else {
						log.info(this.getClass() + "保存合同路径失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result.put("respMsg_emptyString", "合同路径为空字符串，数据处理成功");
		}
		// 出借合同为NULL的数据.
		List<WloanTermInvest> isNullList = wloanTermInvestDao.findContractPdfPathAnIsNull();
		if (isNullList != null && isNullList.size() >= 1) {
			for (WloanTermInvest isNullInvest : isNullList) {
				// 客户帐号信息.
				UserInfo userInfo = userInfoDao.get(isNullInvest.getUserId());
				// 项目信息.
				WloanTermProject project = wloanTermProjectDao.get(isNullInvest.getProjectId());
				// 融资主体.
				WloanSubject subject = null;
				// 担保机构.
				WGuaranteeCompany wGuaranteeCompany = null;
				if (project != null) {
					subject = wloanSubjectDao.get(project.getSubjectId());
					wGuaranteeCompany = wGuaranteeCompanyDao.get(project.getGuaranteeId());
				}
				// 出借记录还款计划.
				WloanTermUserPlan entity = new WloanTermUserPlan();
				WloanTermInvest wloanTermInvest = wloanTermInvestDao.get(isNullInvest.getId());
				entity.setWloanTermInvest(wloanTermInvest);
				List<WloanTermUserPlan> plans = wloanTermUserPlanDao.findList(entity);

				try {
					// 合同路径.
					String contractPdfPath = createTheOldVersionContract(userInfo, project, subject, wGuaranteeCompany, wloanTermInvest, plans);

					boolean contains = contractPdfPath.contains("data");
					if (contains) {
						// 更新合同路径.
						wloanTermInvest.setContractPdfPath(contractPdfPath.split("data")[1]);
					} else {
						// 更新合同路径.
						wloanTermInvest.setContractPdfPath(contractPdfPath);
					}
					int updateInvestFlag = wloanTermInvestDao.update(wloanTermInvest);
					if (updateInvestFlag == 1) {
						log.info(this.getClass() + "保存合同路径成功");
					} else {
						log.info(this.getClass() + "保存合同路径失败");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			result.put("respMsg_isNull", "合同路径为NULL，数据处理成功");
		}

		result.put("state", "0");
		result.put("message", "接口请求成功");
		return result;
	}

	/**
	 * 
	 * methods: createTheOldVersionContract <br>
	 * description: 创建PDF密文版 <br>
	 * author: Roy <br>
	 * date: 2019年10月9日 下午6:28:53
	 * 
	 * @param userInfo
	 * @param project
	 * @param subject
	 * @param wGuaranteeCompany
	 * @param wloanTermInvest
	 * @param plans
	 * @return
	 * @throws Exception
	 */
	public String createTheOldVersionContract(UserInfo userInfo, WloanTermProject project, WloanSubject subject, WGuaranteeCompany wGuaranteeCompany, WloanTermInvest wloanTermInvest, List<WloanTermUserPlan> plans) throws Exception {

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		String newFileName = "";

		// 协议创建日期.
		String pdfCreateDate = DateUtils.getDate(new Date(), "yyyyMMddHHMMss");
		// 合同编号.
		map.put("contract_no", pdfCreateDate);
		if (null != project) {
			// 借款项目名称.
			map.put("project_name", project.getName());
			// 借款项目编号.
			map.put("project_no", project.getSn());
			// 借款总额.
			Double maxAmount = project.getAmount();
			BigDecimal loanTotalAmountBd = new BigDecimal(maxAmount);
			StringBuffer loanTotalAmountSb = new StringBuffer();
			map.put("loanTotalAmount", loanTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(loanTotalAmountBd))).append("元(大写：").append(PdfUtils.change(maxAmount)).append(")").toString());
			// 借款用途.
			map.put("uses", project.getPurpose());
			// 借款日期.
			map.put("lend_date", DateUtils.formatDate(project.getLoanDate(), "yyyy-MM-dd"));
			// 借款期限.
			map.put("term_date", project.getSpan().toString());
			// 还本日期.
			map.put("back_date", DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(project.getLoanDate()), project.getSpan() / 30));
			// 年化利率.
			map.put("year_interest", NumberUtils.scaleDoubleStr(project.getAnnualRate()));
			// 利息总额.
			map.put("interest_sum", NumberUtils.scaleDoubleStr(wloanTermInvest.getInterest()));
		}
		if (null != subject) {
			// 借款人.
			map.put("name", CommonStringUtils.replaceNameX(subject.getCashierUser()));
			// 身份证号码.
			map.put("card_id", Util.hideString(subject.getLoanIdCard() == null ? "**********" : subject.getLoanIdCard(), 6, 8)); // 身份证号码
		}
		if (null != wGuaranteeCompany) {
			// 担保人（公司名称）.
			map.put("third_name", wGuaranteeCompany.getName());
			// 法定代表人.
			map.put("legal_person", wGuaranteeCompany.getCorporation());
			// 住所.
			map.put("residence", wGuaranteeCompany.getAddress());
			// 电话.
			map.put("telphone", wGuaranteeCompany.getPhone());
		}
		if (null != wloanTermInvest) {
			// 签订日期.
			map.put("sign_date", DateUtils.formatDate(wloanTermInvest.getBeginDate(), "yyyy年MM月dd日"));
		}

		// 还款计划title.
		String title = "出借人本金利息表";
		// 还款计划rowTitle.
		String[] rowTitle = new String[] { "还款日期", "类型", "本金/利息" };
		// 还款计划rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		for (WloanTermUserPlan wloanTermUserPlan : plans) {
			strings = new String[rowTitle.length];
			strings[0] = DateUtils.getDate(wloanTermUserPlan.getRepaymentDate(), "yyyy年MM月dd日");
			if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(wloanTermUserPlan.getPrincipal())) {
				strings[1] = "还本付息";
			} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(wloanTermUserPlan.getPrincipal())) {
				strings[1] = "付息";
			}
			strings[2] = wloanTermUserPlan.getInterest().toString();
			dataList.add(strings);
		}

		return createTheOldVersionLoanContractByTemplate(map, title, rowTitle, dataList, newFileName);
	}

	/**
	 * 
	 * 方法: createTheOldVersionLoanContract <br>
	 * 描述: 创建旧版本出借合同. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月27日 上午10:33:32
	 * 
	 * @param userInfo
	 * @param project
	 * @param subject
	 * @param wGuaranteeCompany
	 * @param wloanTermInvest
	 * @param plans
	 * @return
	 * @throws Exception
	 */
	public String createTheOldVersionLoanContract(UserInfo userInfo, WloanTermProject project, WloanSubject subject, WGuaranteeCompany wGuaranteeCompany, WloanTermInvest wloanTermInvest, List<WloanTermUserPlan> plans) throws Exception {

		// PDF(Key:Value).
		Map<String, String> map = new HashMap<String, String>();

		// 协议创建日期.
		String pdfCreateDate = "";
		if (null != wloanTermInvest) {
			pdfCreateDate = DateUtils.getDate(wloanTermInvest.getBeginDate(), "yyyyMMddHHMMss");
			// 合同编号.
			map.put("contract_no", pdfCreateDate);
		}
		// 标的id
		String projectId = "";
		if (null != project) {
			projectId = project.getId();
			// 借款项目名称.
			map.put("project_name", project.getName());
			// 借款项目编号.
			map.put("project_no", project.getSn());
			// 借款总额.
			Double maxAmount = project.getAmount();
			BigDecimal loanTotalAmountBd = new BigDecimal(maxAmount);
			StringBuffer loanTotalAmountSb = new StringBuffer();
			map.put("loanTotalAmount", loanTotalAmountSb.append("￥").append(fmtMicrometer(formatToString(loanTotalAmountBd))).append("元(大写：").append(PdfUtils.change(maxAmount)).append(")").toString());
			// 借款用途.
			map.put("uses", project.getPurpose());
			// 借款日期.
			map.put("lend_date", DateUtils.formatDate(project.getLoanDate(), "yyyy-MM-dd"));
			// 借款期限.
			map.put("term_date", project.getSpan().toString());
			// 还本日期.
			map.put("back_date", DateUtils.getSpecifiedMonthAfter(DateUtils.formatDateTime(project.getLoanDate()), project.getSpan() / 30));
			// 年化利率.
			map.put("year_interest", NumberUtils.scaleDoubleStr(project.getAnnualRate()));
			// 利息总额.
			map.put("interest_sum", NumberUtils.scaleDoubleStr(wloanTermInvest.getInterest()));
		}
		if (null != subject) {
			if (WloanSubjectService.WLOAN_SUBJECT_TYPE_1.equals(subject.getType())) {
				// 借款人.
				map.put("name", CommonStringUtils.replaceNameX(subject.getCashierUser())); // 借款人，受托人姓名
				// 身份证号码.
				map.put("card_id", Util.hideString(subject.getLoanIdCard() == null ? "**********" : subject.getLoanIdCard(), 6, 8)); // 借款人，受托人身份证号码
			} else if (WloanSubjectService.WLOAN_SUBJECT_TYPE_2.equals(subject.getType())) {
				// 借款人.
				map.put("name", subject.getCompanyName());
				// 身份证号码.
				map.put("card_id", subject.getBusinessNo());
			}
		}
		if (null != wGuaranteeCompany) {
			// 担保人（公司名称）.
			map.put("third_name", wGuaranteeCompany.getName());
			// 法定代表人.
			map.put("legal_person", wGuaranteeCompany.getCorporation());
			// 住所.
			map.put("residence", wGuaranteeCompany.getAddress());
			// 电话.
			map.put("telphone", wGuaranteeCompany.getPhone());
		}
		if (null != wloanTermInvest) {
			// 签订日期.
			map.put("sign_date", DateUtils.formatDate(wloanTermInvest.getBeginDate(), "yyyy年MM月dd日"));
		}
		// 出借人本金利息表.
		String title = "附表《出借人本金利息表》";
		// rowTitle.
		String[] rowTitle = new String[] { "出借人", "身份号码", "还款日", "类型", "本金/付息" };
		// rowData.
		ArrayList<String[]> dataList = new ArrayList<String[]>();
		String[] strings = null;
		if (null != plans && plans.size() >= 1) {
			for (WloanTermUserPlan plan : plans) {
				if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_1.equals(plan.getPrincipal())) {
					for (int i = 0; i < 2; i++) {
						strings = new String[rowTitle.length]; // 列头
						if (null != userInfo) {
							strings[0] = CommonStringUtils.replaceNameX(userInfo.getRealName());
							strings[1] = Util.hideString(userInfo.getCertificateNo() == null ? "**********" : subject.getLoanIdCard(), 6, 8);
							// strings[1] = userInfo.getCertificateNo().substring(0, 6) + "********" + userInfo.getCertificateNo().substring(userInfo.getCertificateNo().length() - 4);
						}
						strings[2] = DateUtils.formatDate(plan.getRepaymentDate(), "yyyy年MM月dd日");
						if (i == 0) {
							strings[3] = "付息";
							strings[4] = fmtMicrometer(NumberUtils.scaleDoubleStr(NumberUtils.subtract(plan.getInterest(), plan.getWloanTermInvest().getAmount())));
						} else if (i == 1) {
							strings[3] = "还本";
							strings[4] = fmtMicrometer(NumberUtils.scaleDoubleStr(plan.getWloanTermInvest().getAmount()));
						}
						dataList.add(strings);
					}
				} else if (WloanTermUserPlanService.WLOAN_TERM_USER_PLAN_PRINCIPAL_2.equals(plan.getPrincipal())) {
					strings = new String[rowTitle.length]; // 列头
					if (null != userInfo) {
						strings[0] = CommonStringUtils.replaceNameX(userInfo.getRealName());
						strings[1] = Util.hideString(userInfo.getCertificateNo() == null ? "**********" : subject.getLoanIdCard(), 6, 8);
						// strings[1] = userInfo.getCertificateNo().substring(0, 6) + "********" + userInfo.getCertificateNo().substring(userInfo.getCertificateNo().length() - 4);
					}
					strings[2] = DateUtils.formatDate(plan.getRepaymentDate(), "yyyy年MM月dd日");
					strings[3] = "付息";
					strings[4] = fmtMicrometer(NumberUtils.scaleDoubleStr(plan.getInterest()));
					dataList.add(strings);
				}
			}
		}
		String newFileName = projectId + "_" + pdfCreateDate + ".pdf";
		return createTheOldVersionLoanContractByTemplate(map, title, rowTitle, dataList, newFileName);
	}

	/**
	 * 
	 * 方法: createTheOldVersionLoanContractByTemplate <br>
	 * 描述: 创建旧版本出借合同关联合同模版. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年9月27日 下午3:40:07
	 * 
	 * @param map
	 * @param title
	 * @param rowTitle
	 * @param dataList
	 * @return
	 * @throws Exception
	 */
	private String createTheOldVersionLoanContractByTemplate(Map<String, String> map, String title, String[] rowTitle, ArrayList<String[]> dataList, String newFileName) throws Exception {

		// 新文件名称.
		// String newFileName = IdGen.uuid() + ".pdf";
		// online.
		String linFilePath = LIN_FILE_PATH + "ZTMGPT_Loan_HeTong_Temp.pdf";
		String templateFileNamePath = TEMPLATE_FILE_PATH + "ZTMGPT_Loan_HeTong.pdf";
		String newFileNamePath = OUT_PATH + DateUtils.getFileDate();
		// test.
		// String linFilePath = "D:" + File.separator + "pdf" + File.separator + "ZTMGPT_Loan_HeTong_Temp.pdf";
		// String templateFileNamePath = "D:" + File.separator + "pdf" + File.separator + "ZTMGPT_Loan_HeTong.pdf";
		// String newFileNamePath = "D:" + File.separator + "pdf" + File.separator + DateUtils.getFileDate();

		// 创建目录.
		FileUtils.createDirectory(FileUtils.path(newFileNamePath));
		// 创建一个PDF读入流.
		PdfReader reader = new PdfReader(templateFileNamePath);
		// 字节数组流.
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 根据一个PdfReader创建一个PdfStamper用来生成新的PDF.
		PdfStamper ps = new PdfStamper(reader, bos);

		// 签章.
		PdfContentByte pdfContentByte = ps.getOverContent(6);
		// online.
		Image partyY_Image = Image.getInstance(ZTMG_ELECTRONIC_SIGNATURE_IMAGE_URL + ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		// test.
		// Image partyY_Image = Image.getInstance("D:" + File.separator + "pdf" + File.separator + ZTMG_ELECTRONIC_SIGNATURE_IMAGE);
		partyY_Image.scaleToFit(150, 150);
		partyY_Image.setAbsolutePosition(200, 310);
		pdfContentByte.addImage(partyY_Image);

		// Key:Value.
		AcroFields fields = ps.getAcroFields();
		// 模板文件的大小不变，字体格式满足中文要求.
		fields.setFieldProperty("contract_no", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("project_name", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("project_no", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("loanTotalAmount", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("uses", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("lend_date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("term_date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("back_date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("year_interest", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("interest_sum", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("name", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("card_id", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("third_name", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("legal_person", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("residence", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("telphone", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);
		fields.setFieldProperty("sign_date", "textfont", BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED), null);

		fillData(fields, map);
		ps.setFormFlattening(true);
		ps.close();
		fos = new FileOutputStream(FileUtils.path(linFilePath));
		fos.write(bos.toByteArray());
		fos.close();

		// online table path.
		String tablePath = OUT_PATH + "ZTMGPT_Loan_HeTong_Table.pdf";
		// test table path.
		// String tablePath = "D:" + File.separator + "pdf" + File.separator + "ZTMGPT_Loan_HeTong_Table.pdf";

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
