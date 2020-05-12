package com.power.platform.ifcert.status.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.cert.open.CertToolV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.StatusDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.Status;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.ProductStatusEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermProject;

/**
 * 
 * class: ScatterInvestStatusDataAccessService <br>
 * description: 散标状态信息。 <br>
 * 说明：（1）散标状态分为最初状态“初始公布”,状态更新包括“满标”、“流标”或“募标中”、“还款中”、“还款结束”、"逾期"、“坏账”、"放款"等状态。状态每一次变化，都需要上传一条散标状态更新信息；
 * 状态没有发生变化，不需要推送数据（如持续在“6-筹标中”、“5-还款中”、“4-逾期”、“10-线下销账”等状态时，不需要重复推送）。<br>
 * （2）如果状态是“4-逾期”，逾期之后又还款了，再重新推送一条“5-还款中”的状态数据；如全部还完，推送“3-还款结束”。<br>
 * （3）这里需要注意的状态类型是“10-线下销账”，场景1，平台通过第三方机构或个人，线上回购散标对应的全部债权（线上债权回购数据正常推送），在由第三方机构或个人线下与借款方协商销账的，网贷机构线上散标推送“10-线下销账”状态；
 * 场景2，借款人通过线下方式，支付给网贷机构或者直接支付给出借人的过程，网贷机构线上散标推送“10-线下销账”状态。<br>
 * （4）上报散标状态数据触发时间：散标状态信息产生之后，上报该业务数据。<br>
 * author: Roy <br>
 * date: 2019年5月10日 下午3:59:56
 */
@Service("scatterInvestStatusDataAccessService")
public class ScatterInvestStatusDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(ScatterInvestStatusDataAccessService.class);

	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private StatusDao statusDao;
	@Resource
	private BatchNumDao batchNumDao;
	
	
	/**
	 * 
	 * methods: fillPushScatterInvestStatusInfo <br>
	 * description: 补推标的状态列表. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月13日 上午9:57:48
	 * 
	 * @param staList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushScatterInvestStatusInfo(List<Status> staList) {
		
		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {
			for (Status status : staList) {
				
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				// 本地留存散标状态信息.
				Status sta = new Status();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				sta.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				sta.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("sourceProductCode", status.getSourceProductCode()); // 散标信息编号.
				sta.setSourceProductCode(status.getSourceProductCode());
				param.put("productStatus", status.getProductStatus()); // 散标状态编码.
				sta.setProductStatus(status.getProductStatus());
				param.put("productDate", status.getProductDate()); // 散标状态更新时间.
				sta.setProductDate(status.getProductDate());
				list.add(param);
				sta.setBatchNum(batchNum);
				sta.setSendTime(sentTime);
				int insert = statusDao.insert(sta);
				if (insert == 1) {
					log.info("散标状态信息插入成功！");
				} else {
					log.info("散标状态信息插入失败！");
				}
					
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_6.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量散标信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_SCATTER_INVEST_STATUS, "utf-8");
			log.info("存量散标信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}

	/**
	 * 发布.
	 */
	public static final String PRO_STATUS_3 = "3";
	/**
	 * 上线.
	 */
	public static final String PRO_STATUS_4 = "4";
	/**
	 * 满标.
	 */
	public static final String PRO_STATUS_5 = "5";
	/**
	 * 还款.
	 */
	public static final String PRO_STATUS_6 = "6";
	/**
	 * 结束.
	 */
	public static final String PRO_STATUS_7 = "7";
	/**
	 * 流标.
	 */
	public static final String PRO_STATUS_8 = "8";

	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	public class ProStatus {

		// 状态更新时间.
		private String updateDate;
		// 标的状态.
		private String status;

		public String getUpdateDate() {

			return updateDate;
		}

		public void setUpdateDate(String updateDate) {

			this.updateDate = updateDate;
		}

		public String getStatus() {

			return status;
		}

		public void setStatus(String status) {

			this.status = status;
		}

	}

	/**
	 * 
	 * methods: proStatusList <br>
	 * description: 散标状态列表. <br>
	 * author: Roy <br>
	 * date: 2019年5月10日 下午6:03:31
	 * 
	 * @param proId
	 * @return
	 */
	public List<ProStatus> proStatusList(WloanTermProject project) {

		List<ProStatus> list = new ArrayList<ProStatus>();
		if (project != null) {
			if (PRO_STATUS_3.equals(project.getState())) { // 发布.
				for (int i = 0; i < 1; i++) {
					ProStatus ps = new ProStatus();
					ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
					ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
					list.add(ps);
				}
			} else if (PRO_STATUS_4.equals(project.getState())) { // 上线.
				for (int i = 0; i < 2; i++) {
					if (i == 0) { // 初始发布.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 1) { // 筹标中.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_6.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
				}
			} else if (PRO_STATUS_5.equals(project.getState())) { // 满标.
				for (int i = 0; i < 3; i++) {
					if (i == 0) { // 初始发布.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 1) { // 筹标中.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_6.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 2) { // 满标.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_1.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
				}
			} else if (PRO_STATUS_6.equals(project.getState())) { // 还款.
				for (int i = 0; i < 5; i++) {
					if (i == 0) { // 初始发布.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 1) { // 筹标中.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_6.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 2) { // 满标.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_1.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 3) { // 放款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_9.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 4) { // 还款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_5.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
				}
			} else if (PRO_STATUS_7.equals(project.getState())) { // 结束.
				for (int i = 0; i < 6; i++) {
					if (i == 0) { // 初始发布.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 1) { // 筹标中.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_6.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 2) { // 满标.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_1.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 3) { // 放款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_9.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 4) { // 还款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_5.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 5) { // 结束.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_3.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getEndDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
				}
			} else if (PRO_STATUS_8.equals(project.getState())) { // 流标.
				for (int i = 0; i < 7; i++) {
					if (i == 0) { // 初始发布.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 1) { // 筹标中.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_6.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 2) { // 满标.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_1.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 3) { // 放款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_9.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 4) { // 还款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_5.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 5) { // 结束.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_3.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getEndDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 6) { // 流标.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_2.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
				}
			}
		}
		log.info("proId：{}，proStatusList：{}", project.getId(), list.size());
		return list;
	}
	
	
	

	/**
	 * 
	 * methods: pushScatterInvestStatusInfo <br>
	 * description: 推送标的状态列表. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月13日 上午9:57:48
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestStatusInfo(List<String> projectIdList) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {

			for (String projectId : projectIdList) {
				WloanTermProject project = wloanTermProjectDao.get(projectId);
				if (project != null) {
					List<ProStatus> proStatusList = proStatusList(project);
					for (ProStatus proStatus : proStatusList) {
						/**
						 * 散标信息封装.
						 */
						Map<String, Object> param = new LinkedHashMap<String, Object>();
						// 本地留存散标状态信息.
						Status sta = new Status();
						param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
						sta.setVersion(ServerURLConfig.VERSION);
						param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
						sta.setSourceCode(ServerURLConfig.SOURCE_CODE);
						param.put("sourceProductCode", project.getSn()); // 散标信息编号.
						sta.setSourceProductCode(project.getSn());
						param.put("productStatus", proStatus.getStatus()); // 散标状态编码.
						sta.setProductStatus(proStatus.getStatus());
						param.put("productDate", proStatus.getUpdateDate()); // 散标状态更新时间.
						sta.setProductDate(proStatus.getUpdateDate());
						list.add(param);
						sta.setBatchNum(batchNum);
						sta.setSendTime(sentTime);
						int insert = statusDao.insert(sta);
						if (insert == 1) {
							log.info("散标状态信息插入成功！");
						} else {
							log.info("散标状态信息插入失败！");
						}
					}
				}
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_6.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量散标信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_SCATTER_INVEST_STATUS, "utf-8");
			log.info("存量散标信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}
	
	
	
	/**
	 * 
	 * methods: proStatus <br>
	 * description: 时时的散标状态. <br>
	 * author: Roy <br>
	 * date: 2019年5月10日 下午6:03:31
	 * 
	 * @param project
	 * @return
	 */
	public List<ProStatus> proStatus(WloanTermProject project) {
		List<ProStatus> list = new ArrayList<ProStatus>();
		if (project != null) {
			if (PRO_STATUS_3.equals(project.getState())) { // 发布.
				ProStatus ps = new ProStatus();
				ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_0.getValue());
				ps.setUpdateDate(DateUtils.formatDate(project.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
				list.add(ps);
			} else if (PRO_STATUS_4.equals(project.getState())) { // 上线.
				ProStatus ps = new ProStatus();
				ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_6.getValue());
				ps.setUpdateDate(DateUtils.formatDate(project.getOnlineDate(), "yyyy-MM-dd HH:mm:ss"));
				list.add(ps);
			} else if (PRO_STATUS_6.equals(project.getState())) { // 还款.
				for (int i = 0; i < 3; i++) {
					if (i == 0) { // 满标.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_1.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getFullDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 1) { // 放款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_9.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
					if (i == 2) { // 还款.
						ProStatus ps = new ProStatus();
						ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_5.getValue());
						ps.setUpdateDate(DateUtils.formatDate(project.getRealLoanDate(), "yyyy-MM-dd HH:mm:ss"));
						list.add(ps);
					}
				}
			} else if (PRO_STATUS_7.equals(project.getState())) { // 结束.
				ProStatus ps = new ProStatus();
				ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_3.getValue());
				ps.setUpdateDate(DateUtils.formatDate(project.getEndDate(), "yyyy-MM-dd HH:mm:ss"));
				list.add(ps);
			} else if (PRO_STATUS_8.equals(project.getState())) { // 流标.
				ProStatus ps = new ProStatus();
				ps.setStatus(ProductStatusEnum.PRODUCT_STATUS_2.getValue());
				ps.setUpdateDate(DateUtils.formatDate(project.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
				list.add(ps);
			}
		}
		log.info("proId：{}，proStatusList：{}", project.getId(), list.size());
		return list;
	}
	
	/**
	 * 
	 * methods: pushScatterInvestStatusInfo <br>
	 * description: 时时推送标的状态. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月13日 上午9:57:48
	 * 
	 * @param projectId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushScatterInvestStatus(String projectId) {

		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 接口数据列表.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");

		try {
			WloanTermProject project = wloanTermProjectDao.get(projectId);
			if (project != null) {
				List<ProStatus> proStatusList = proStatus(project);
				for (ProStatus proStatus : proStatusList) {
					/**
					 * 散标信息封装.
					 */
					Map<String, Object> param = new LinkedHashMap<String, Object>();
					// 本地留存散标状态信息.
					Status sta = new Status();
					param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
					sta.setVersion(ServerURLConfig.VERSION);
					param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
					sta.setSourceCode(ServerURLConfig.SOURCE_CODE);
					param.put("sourceProductCode", project.getSn()); // 散标信息编号.
					sta.setSourceProductCode(project.getSn());
					param.put("productStatus", proStatus.getStatus()); // 散标状态编码.
					sta.setProductStatus(proStatus.getStatus());
					param.put("productDate", proStatus.getUpdateDate()); // 散标状态更新时间.
					sta.setProductDate(proStatus.getUpdateDate());
					list.add(param);
					sta.setBatchNum(batchNum);
					sta.setSendTime(sentTime);
					int insert = statusDao.insert(sta);
					if (insert == 1) {
						log.info("散标状态信息插入成功！");
					} else {
						log.info("散标状态信息插入失败！");
					}
				}
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_6.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量散标信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_SCATTER_INVEST_STATUS, "utf-8");
			log.info("存量散标信息接口响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_0000.getValue());
					result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_0000.getText());
				} else { // 该批次数据推送失败.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
					bn.setCode(code);
					int insert = batchNumDao.insert(bn);
					if (insert == 1) {
						log.info("该批次数据状态信息插入成功！");
					} else {
						log.info("该批次数据状态信息插入失败！");
					}
					result.put("respCode", code);
					result.put("respMsg", "参考数据接入手册，错误码对照表");
				}
			} else { // 该批次数据推送失败.
				BatchNum bn = new BatchNum();
				bn.setId(IdGen.uuid());
				bn.setBatchNum(batchNum);
				bn.setSendTime(sentTime);
				bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				bn.setCode("resp null");
				int insert = batchNumDao.insert(bn);
				if (insert == 1) {
					log.info("该批次数据状态信息插入成功！");
				} else {
					log.info("该批次数据状态信息插入失败！");
				}
				result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
				result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_02.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			BatchNum bn = new BatchNum();
			bn.setId(IdGen.uuid());
			bn.setBatchNum(batchNum);
			bn.setSendTime(sentTime);
			bn.setInfType(InfTypeEnum.INF_TYPE_6.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			bn.setCode("exception");
			int insert = batchNumDao.insert(bn);
			if (insert == 1) {
				log.info("该批次数据状态信息插入成功！");
			} else {
				log.info("该批次数据状态信息插入失败！");
			}
			result.put("respCode", ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
			result.put("respMsg", ResponseEnum.RESPONSE_CODE_MSG_04.getText());
		}

		return result;
	}
}
