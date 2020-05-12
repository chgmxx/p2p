package com.power.platform.ifcert.lendproduct.service;

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

import com.power.platform.cgb.dao.CgbUserBankCardDao;
import com.power.platform.cgbpay.config.ServerURLConfig;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.IfCertUserInfoDao;
import com.power.platform.ifcert.dao.LendProductDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.LendProduct;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermProject;

/**
 * 
 * class: UserInfoDataAccess <br>
 * description: 用户信息数据访问，是指出借人、借款人、第三方担保公司、平台自身、
 * 受托支付方的相关属性信息。需要注意的是，借款人用户姓名需要推送明文. <br>
 * author: Roy <br>
 * date: 2019年4月22日 上午10:52:25
 */
@Service("lendProductDataAccessService")
public class LendProductDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(LendProductDataAccessService.class);

	@Resource
	private LendProductDao lendProductDao;
	@Resource
	private IfCertUserInfoDao userinfoDao;
	@Resource
	private CgbUserBankCardDao cgbUserBankCardDao;
	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private BatchNumDao batchNumDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: fillPushLendProduct <br>
	 * description: 补推产品信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月7日 下午4:00:24
	 * 
	 * @param userIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> fillPushLendProduct(List<LendProduct> lendProductList) {

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
			for (LendProduct lp : lendProductList) {
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				// 产品信息.
				LendProduct lendProduct = new LendProduct();
				// 接口版本号.
				param.put("version", ServerURLConfig.VERSION);
				lendProduct.setVersion(ServerURLConfig.VERSION);
				// 网贷机构平台在应急中心系统的唯一编号，网贷机构在应急中心系统注册实名之后自动生成.
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
				lendProduct.setSourceCode(ServerURLConfig.SOURCE_CODE);
				// 产品信息编号.
				param.put("sourceFinancingCode", lp.getSourceFinancingCode());
				lendProduct.setSourceFinancingCode(lp.getSourceFinancingCode());
				// 发布时间.
				param.put("financingStartTime", lp.getFinancingStartTime());
				lendProduct.setFinancingStartTime(lp.getFinancingStartTime());
				// 产品名称.
				param.put("productName", lp.getProductName());
				lendProduct.setProductName(lp.getProductName());
				// 预期年化利率.
				param.put("rate", lp.getRate());
				lendProduct.setRate(lp.getRate());
				// 最小预期年化利率.
				param.put("minRate", "-1");
				lendProduct.setMinRate("-1");
				// 最大预期年化利率.
				param.put("maxRate", "-1");
				lendProduct.setMaxRate("-1");
				// 产品期限（服务期限）天.
				param.put("term", lp.getTerm());
				lendProduct.setTerm(lp.getTerm());
				lendProduct.setBatchNum(batchNum); // 批次号.
				lendProduct.setSendTime(sentTime); // 发送时间.
				int insert = lendProductDao.insert(lendProduct);
				if (insert == 1) {
					log.info("产品信息插入成功！");
				} else {
					log.info("产品信息插入失败！");
				}
				list.add(param);
			}
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION);
			json.accumulate("batchNum", batchNum);
			json.accumulate("checkCode", tool.checkCode(list.toString()));
			json.accumulate("totalNum", list.size() + "");
			json.accumulate("sentTime", sentTime);
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE);
			json.accumulate("infType", InfTypeEnum.INF_TYPE_86.getValue());// 产品配置接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("产品信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PRODUCT, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("产品信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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
	 * methods: pushInvestUserInfo <br>
	 * description: 推送产品信息. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月7日 下午4:00:24
	 * 
	 * @param userIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushLendProduct(List<String> productIdList) {

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
			WloanTermProject product = null;
			for (String productId : productIdList) {
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				product = wloanTermProjectDao.get(productId);
				if (null != product) {
					// 产品信息.
					LendProduct lendProduct = new LendProduct();
					// 接口版本号.
					param.put("version", ServerURLConfig.VERSION);
					lendProduct.setVersion(ServerURLConfig.VERSION);
					// 网贷机构平台在应急中心系统的唯一编号，网贷机构在应急中心系统注册实名之后自动生成.
					param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
					lendProduct.setSourceCode(ServerURLConfig.SOURCE_CODE);
					// 产品信息编号.
					param.put("sourceFinancingCode", product.getSn());
					lendProduct.setSourceFinancingCode(product.getSn());
					// 发布时间.
					param.put("financingStartTime", DateUtils.formatDate(product.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
					lendProduct.setFinancingStartTime(DateUtils.formatDate(product.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
					// 产品名称.
					param.put("productName", product.getName());
					lendProduct.setProductName(product.getName());
					// 预期年化利率.
					param.put("rate", NumberUtils.scaleSixStr(NumberUtils.divide(product.getAnnualRate(), 100D)));
					lendProduct.setRate(NumberUtils.scaleSixStr(NumberUtils.divide(product.getAnnualRate(), 100D)));
					// 最小预期年化利率.
					param.put("minRate", "-1");
					lendProduct.setMinRate("-1");
					// 最大预期年化利率.
					param.put("maxRate", "-1");
					lendProduct.setMaxRate("-1");
					// 产品期限（服务期限）天.
					param.put("term", product.getSpan().toString());
					lendProduct.setTerm(product.getSpan().toString());
					lendProduct.setBatchNum(batchNum); // 批次号.
					lendProduct.setSendTime(sentTime); // 发送时间.
					int insert = lendProductDao.insert(lendProduct);
					if (insert == 1) {
						log.info("产品信息插入成功！");
					} else {
						log.info("产品信息插入失败！");
					}
					list.add(param);
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_86.getValue());// 产品配置接口
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("产品信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PRODUCT, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("产品信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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
	 * methods: pushSingletonInvestUserInfo <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，去掉后缀test.<br>
	 * description: 单个出借人用户信息. <br>
	 * author: Roy <br>
	 * date: 2019年5月9日 下午4:10:20
	 * 
	 * @param user
	 * @return
	 */
	public Map<String, Object> pushSingletonLendProduct(WloanTermProject product) {

		// 系统当前时间毫秒值.
		long currentTimeMillis = System.currentTimeMillis();
		// 当前推送数据批次号.
		String batchNum = ServerURLConfig.SOURCE_CODE + "_" + DateUtils.getDate("yyyyMMdd") + "_" + currentTimeMillis;
		// 发送时间.
		String sentTime = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		// 结果集.
		Map<String, Object> result = new HashMap<String, Object>();
		// 该批次数据集合.
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		/**
		 * 出借人信息封装.
		 */
		WloanTermProject product1 = wloanTermProjectDao.get(product.getId());
		Map<String, Object> param = new LinkedHashMap<String, Object>();
		try {

			// 国家应急中心用户信息.
			LendProduct lendProduct = new LendProduct();
			/**
			 * 接口版本号.
			 */
			param.put("version", ServerURLConfig.VERSION);
			lendProduct.setVersion(ServerURLConfig.VERSION);
			/**
			 * 网贷机构平台在应急中心系统的唯一编号，
			 * 网贷机构在应急中心系统注册实名之后自动生成.
			 */
			param.put("sourceCode", ServerURLConfig.SOURCE_CODE);
			lendProduct.setSourceCode(ServerURLConfig.SOURCE_CODE);
			// 产品信息
			if (product1 != null) {
				/**
				 * 产品信息编号.
				 */
				param.put("sourceFinancingCode", product1.getSn());
				lendProduct.setSourceFinancingCode(product1.getSn());
				/**
				 * 发布时间
				 */
				param.put("financingStartTime", DateUtils.formatDate(product1.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
				lendProduct.setFinancingStartTime(DateUtils.formatDate(product1.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
				/**
				 * // 产品名称.
				 */
				param.put("productName", product1.getName());
				lendProduct.setProductName(product1.getName());
				/**
				 * 预期年化利率
				 */
				param.put("rate", NumberUtils.scaleSixStr(NumberUtils.divide(product1.getAnnualRate(), 100D)));
				lendProduct.setRate(NumberUtils.scaleSixStr(NumberUtils.divide(product1.getAnnualRate(), 100D)));
				/**
				 * 最小预期年化利率
				 */
				param.put("minRate", "-1");
				lendProduct.setMinRate("-1");
				/**
				 * 最大预期年化利率
				 */
				param.put("maxRate", "-1");
				lendProduct.setMaxRate("-1");
				/**
				 * 产品期限（服务期限）天
				 */
				param.put("term", product1.getSpan().toString());
				lendProduct.setTerm(product1.getSpan().toString());
				lendProduct.setBatchNum(batchNum); // 批次号.
				lendProduct.setSendTime(sentTime); // 发送时间.
				int insert = lendProductDao.insert(lendProduct);
				if (insert == 1) {
					log.info("产品信息插入成功！");
				} else {
					log.info("产品信息插入失败！");
				}
			}
			list.add(param);
			JSONObject json = new JSONObject();
			String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("version", ServerURLConfig.VERSION); // 接口版本号.
			json.accumulate("batchNum", batchNum); // 批次号.
			json.accumulate("checkCode", tool.checkCode(list.toString())); // 工具包checkCode方法生成.
			json.accumulate("totalNum", list.size() + "");// 本批次发送的总数据条数，一个批次最多传3000 条数据.
			json.accumulate("sentTime", sentTime); // 发送时间.
			json.accumulate("sourceCode", ServerURLConfig.SOURCE_CODE); // 平台编码（网贷机构平台在应急中心系统的唯一编号，网贷机构在应急中心系统注册实名之后自动生成）.
			json.accumulate("infType", InfTypeEnum.INF_TYPE_86.getValue());// 用户接口，传值样例：1
			json.accumulate("dataType", Global.getConfig("DATA_TYPE"));// 接口数据类型；0：调试数据；1 正式数据（接口联调阶段传0，正式推数据阶段传1）
			json.accumulate("timestamp", currentTimeMillis + "");// 获取当前系统时间戳 long timestamp = System.currentTimeMillis();
			json.accumulate("nonce", nonce); // 随机数String nonce = Integer.toHexString(new Random().nextInt());
			json.accumulate("dataList", list); // 接口数据列表.

			// System.out.println("出借人用户信息接口推送数据：" + json);
			log.info("单个出借人用户信息接口推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_LEND_PRODUCT, "utf-8");
			// System.out.println("出借人用户信息接口推送响应：" + responseStr);
			log.info("单个出借人用户信息接口推送响应：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals((String) jsonObject.get("code"))) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_01.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
					bn.setTotalNum(list.size() + "");
					bn.setCode(code);
					bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
				bn.setTotalNum(list.size() + "");
				bn.setCode("resp null");
				bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_02.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_86.getValue());
			bn.setTotalNum(list.size() + "");
			bn.setCode("exception");
			bn.setStatus(ResponseEnum.RESPONSE_CODE_MSG_04.getValue());
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
