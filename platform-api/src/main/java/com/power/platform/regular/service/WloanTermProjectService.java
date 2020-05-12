package com.power.platform.regular.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.entity.WloanTermProjectDto;
import com.power.platform.sys.utils.HttpUtil;

/**
 * 定期项目信息Service
 * 
 * @author jiajunfeng
 * @version 2015-12-28
 */
@Service("wloanTermProjectService")
@Transactional(readOnly = true)
public class WloanTermProjectService extends CrudService<WloanTermProject> {

	/**
	 * 安心投标的-其它类项目.
	 */
	public static final String PROJECT_TYPE_NORMAL = "0";
	/**
	 * 新手标的-其它类项目.
	 */
	public static final String PROJECT_TYPE_NEWPERSON = "1";
	/**
	 * 供应链标的-债权转让类项目.
	 */
	public static final String PROJECT_TYPE_SUPPLY = "2";
	/**
	 * 推荐标的-其它类项目.
	 */
	public static final String PROJECT_TYPE_RECOMMEND = "3";

	/**
	 * 还款类型，1：一次性还本付息.
	 */
	public static final String REPAY_TYPE_1 = "1";
	/**
	 * 还款类型，2：按月付息到期还本.
	 */
	public static final String REPAY_TYPE_2 = "2";

	/**
	 * 受托支付提现标识，0：否.
	 */
	public static final String IS_ENTRUSTED_WITHDRAW_0 = "0";
	/**
	 * 受托支付提现标识，1：是.
	 */
	public static final String IS_ENTRUSTED_WITHDRAW_1 = "1";

	/**
	 * 项目偿还计划类型，0：旧版.
	 */
	public static final String PROJECT_REPAY_PLAN_TYPE_0 = "0";
	/**
	 * 项目偿还计划类型，1：新版.
	 */
	public static final String PROJECT_REPAY_PLAN_TYPE_1 = "1";

	/**
	 * span-30.
	 */
	public static final String SPAN_30 = "30";
	/**
	 * span-90.
	 */
	public static final String SPAN_90 = "90";
	/**
	 * span-180.
	 */
	public static final String SPAN_180 = "180";
	/**
	 * span-360.
	 */
	public static final String SPAN_360 = "360";

	/**
	 * 草稿.
	 */
	public static final String CANCLE = "0"; // 草稿
	/**
	 * 草稿.
	 */
	public static final String DRAFT = "1"; // 草稿
	/**
	 * 审核.
	 */
	public static final String CHECK = "2"; // 审核
	/**
	 * 发布.
	 */
	public static final String PUBLISH = "3"; // 发布
	/**
	 * 上线.
	 */
	public static final String ONLINE = "4"; // 上线
	/**
	 * 满标.
	 */
	public static final String FULL = "5"; // 满标
	/**
	 * 还款中.
	 */
	public static final String REPAYMENT = "6"; // 还款中
	/**
	 * 已完成.
	 */
	public static final String FINISH = "7"; // 已完成
	/**
	 * 流标.
	 */
	public static final String P2P_TRADE_BID_CANCEL = "8";

	public static final String NODELETE = "0"; // 未删除（默认）
	public static final String DELETEED = "1"; // 已删除

	/**
	 * 标的类型，1：其它.
	 */
	public static final String PROJECT_TYPE_1 = "1";
	/**
	 * 标的类型，2：新手标的.
	 */
	public static final String PROJECT_TYPE_2 = "2";
	/**
	 * 标的类型，3：推荐标的.
	 */
	public static final String PROJECT_TYPE_3 = "3";

	/**
	 * 标的产品类型，1：安心投类.
	 */
	public static final String PROJECT_PRODUCT_TYPE_1 = "1";
	/**
	 * 标的产品类型，2：供应链类.
	 */
	public static final String PROJECT_PRODUCT_TYPE_2 = "2";

	public static final String ISCANUSE_COUPON_YES = "0"; // 是否可用抵用券（是）
	public static final String ISCANUSE_COUPON_NO = "1"; // 是否可用抵用券（否）

	public static final String ISCANUSE_PLUSCOUPON_YES = "0"; // 是否可用加息券（是）
	public static final String ISCANUSE_PLUSCOUPON_NO = "1"; // 是否可用加息券（否）

	// 商户号
	private static final String merchantId = Global.getConfig("merchantId");
	// 存管系统为你分配的，用来对AES私钥加密，同时生成签名
	private static final String merchantRsaPublicKey = Global.getConfig("merchantRsaPublicKey");

	// 商户自己的RSA私钥
	private static final String merchantRsaPrivateKey = Global.getConfig("merchantRsaPrivateKey");

	@Resource
	private WloanTermProjectDao wloanTermProjectDao;

	@Override
	protected CrudDao<WloanTermProject> getEntityDao() {

		return wloanTermProjectDao;
	}

	public Page<WloanTermProject> findExcelReportPage(Page<WloanTermProject> page, WloanTermProject entity) {

		entity.setPage(page);
		page.setList(wloanTermProjectDao.findExcelReportList(entity));
		return page;
	}

	/**
	 * 
	 * 方法: updateUserAccountInfo <br>
	 * 描述: 更新定期项目. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月6日 下午2:34:40
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateWloanTermProject(WloanTermProject entity) {

		int flag = 0;
		try {
			flag = wloanTermProjectDao.update(entity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("fn:updateWloanTermProject,{异常：" + e.getMessage() + "}");
		}

		return flag;
	}

	/**
	 * 定期投资项目上线
	 * 供定时任务调用
	 * 
	 * @param oldState
	 *            查询状态
	 * @param newState
	 *            要修改的状态
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void wloanTermProjectOnLineTask(String oldState, String state) {

		// 查询返回所有符合上线条件的集合
		List<WloanTermProject> wloanTermProjectLit = wloanTermProjectDao.findListByStateAndDate(oldState);
		if (wloanTermProjectLit != null) {
			for (WloanTermProject project : wloanTermProjectLit) {
				wloanTermProjectDao.updateWloanTermProjectState(project.getId(), state);
				System.out.println("时间：" + new Date() + "，定时上线定期项目：" + project.getName() + ",成功");
			}
		}

	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateProState(WloanTermProject wloanTermProject) {

		wloanTermProjectDao.updateProState(wloanTermProject);
	}

	public List<WloanTermProject> findListByCompanyId(String companyId) {

		return wloanTermProjectDao.findListByCompanyId(companyId);
	}

	/**
	 * 标的信息迁移
	 * 
	 * @param project
	 * @return
	 * @throws Exception
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> importBidInfo(WloanTermProject project) throws Exception {

		// TODO Auto-generated method stub
		String orderId = UUID.randomUUID().toString().replace("-", "");
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("orderId", orderId);
		params.put("bidId", project.getId());// 标的id
		params.put("name", project.getName());// 标的名称
		BigDecimal amount = new BigDecimal(NumberUtils.scaleDouble(project.getAmount() * 100));
		params.put("amount", amount.toString());// 标的金额
		params.put("userId", project.getWloanSubject().getLoanApplyId());// 借款人，网贷平台唯一的用户编码
		Double annualRate = project.getAnnualRate() / 100;
		params.put("bidRate", annualRate.toString());// 标的年利率
		params.put("bidType", "99");// 标的类型01- 信用02- 抵押03- 债权转让04- 99-其他
		params.put("cycle", project.getSpan().toString());// 借款周期
		if (project.getRepayType().equals(REPAY_TYPE_1)) {
			params.put("repaymentType", "01");// 还款方式 01 一次还本付息02-等额本金
												// 03-等额本息04-按期付息到期还本 99-其他
		}
		if (project.getRepayType().equals(REPAY_TYPE_2)) {
			params.put("repaymentType", "04");// 还款方式 01 一次还本付息02-等额本金
												// 03-等额本息04-按期付息到期还本 99-其他
		}
		params.put("borrPurpose", project.getPurpose());// 借款用途
		params.put("productType", "99");// 标的产品类型
										// 01-房贷类02-车贷类03-收益权转让类04-信用贷款类05-股票配资类06-银行承兑汇票07-商业承兑汇票08-消费贷款类09-供应链类99-其他
		params.put("borrUserType", project.getWloanSubject().getType());// 借款人用户类型1-
																		// 个人2-
																		// 企业
		if (project.getWloanSubject().getType().equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_1)) { // 个人.
			// 借款方证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照，BLC-营业执照，USCC-统一社会信用代码.
			params.put("borrCertType", "IDC");
			// 借款方证件号码，社会信用证或营业执照号（借款方类型为企业时）.
			params.put("borrCertNo", project.getWloanSubject().getLoanIdCard());
			// 借款方名称.
			params.put("borrName", project.getWloanSubject().getCashierUser());
		} else if (project.getWloanSubject().getType().equals(WloanSubjectService.WLOAN_SUBJECT_TYPE_2)) { // 企业.
			// 借款方证件类型，IDC-身份证，GAT-港澳台身份证，MILIARY-军官证，PASS_PORT-护照，BLC-营业执照，USCC-统一社会信用代码.
			params.put("borrCertType", project.getWloanSubject().getBusinessLicenseType());
			// 借款方证件号码，社会信用证或营业执照号（借款方类型为企业时）.
			params.put("borrCertNo", project.getWloanSubject().getBusinessNo());
			// 借款方名称.
			params.put("borrName", project.getWloanSubject().getCompanyName());
		}

		// 公共请求参数
		params.put("service", "p2p.trade.bid.import");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("标的信息迁移[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		String url = ServerURLConfig.CGB_URL;

		String result = HttpUtil.sendPost(url, encryptRet);
		System.out.println("返回结果报文" + result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		System.out.println("解密结果:" + maps);

		return maps;
	}

	/**
	 * 投资单信息迁移API
	 * 
	 * @param wloanTermInvest
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public Map<String, String> orderImport(WloanTermInvest wloanTermInvest) throws IOException, Exception {

		// TODO Auto-generated method stub
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();

		params.put("userId", wloanTermInvest.getUserId());
		params.put("orderId", wloanTermInvest.getId());
		params.put("bidId", wloanTermInvest.getProjectId());
		BigDecimal amount = new BigDecimal(NumberUtils.scaleDouble(wloanTermInvest.getAmount() * 100));
		params.put("amount", amount.toString());
		params.put("orgAmount", amount.toString());

		params.put("service", "p2p.trade.investOrder.import");
		params.put("method", "RSA");
		params.put("merchantId", merchantId);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(merchantRsaPrivateKey, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("投资单信息迁移[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, merchantRsaPublicKey);
		encryptRet.put("merchantId", merchantId);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(ServerURLConfig.CGB_URL, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, merchantRsaPrivateKey);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);
		return maps;
	}

	/**
	 * @Description:jbxt-供应链标的列表
	 */
	public List<WloanTermProjectDto> findProjectDtoList(WloanTermProjectDto wloanTermProject) {
		return wloanTermProjectDao.findProjectDtoList(wloanTermProject);
	}

	/**
	 * @Description:数据中心：利用订单Id查询标的
	 */
	public WloanTermProject getWloanTermProject(String orderId) {
		
		return wloanTermProjectDao.getWloanTermProject(orderId);
	}

	/**
	 * @Description:数据统计：利用企业Id查询统计的数据
	 */
	public WloanTermProject searchData(String creditUserId, String entTimeStr) {
		
		return wloanTermProjectDao.searchData(creditUserId,entTimeStr);
	}
	/**
	 * @Description:数据统计
	 */
	public WloanTermProject searchAxtData(String entTimeStr) {
		return wloanTermProjectDao.searchAxtData(entTimeStr);
	}

	public WloanTermProject searchIntervalAxtData(String startTimeStr, String entTimeStr) {
		return wloanTermProjectDao.searchIntervalAxtData(startTimeStr,entTimeStr);
	}

	public WloanTermProject searchIntervalData(String creditUserId, String startTimeStr, String entTimeStr) {
		return wloanTermProjectDao.searchIntervalData(creditUserId,startTimeStr,entTimeStr);
	}
	
	/**
	 * @Description:统计平均数据
	 */
	public WloanTermProject searchAverageData(String entTimeStr) {
		return wloanTermProjectDao.searchAverageData(entTimeStr);
	}

	public List<WloanTermProject> searchDistinct(String entTimeStr) {
		return wloanTermProjectDao.searchDistinct(entTimeStr);
	}

	
	public List<WloanTermProject> searchBySubjectId(String subjectId, String entTimeStr) {
		return wloanTermProjectDao.searchBySubjectId(subjectId,entTimeStr);
	}

}