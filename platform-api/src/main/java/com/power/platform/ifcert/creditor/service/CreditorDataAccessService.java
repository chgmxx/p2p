package com.power.platform.ifcert.creditor.service;

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
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.ifcert.dao.BatchNumDao;
import com.power.platform.ifcert.dao.CreditorDao;
import com.power.platform.ifcert.entity.BatchNum;
import com.power.platform.ifcert.entity.Creditor;
import com.power.platform.ifcert.type.DataTypeEnum;
import com.power.platform.ifcert.type.InfTypeEnum;
import com.power.platform.ifcert.type.LockTimeTypeEnum;
import com.power.platform.ifcert.type.ResponseEnum;
import com.power.platform.ifcert.utils.http.HttpsUtilSendPost;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermInvest;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.regular.service.WloanTermProjectService;

/**
 * 
 * class: CreditorDataAccessService <br>
 * description: 初始债权. <br>
 * 说明：（1）初始债权是针对出借人而言，指出借人投资一个原始散标而生成的对该借款人的债权，这里的初始债权必须是在出借债权。<br>
 * （2）对于存量数据而言，存量数据不需要上报历史转让信息，即所有未完结债权均视为初始债权，无需追溯其历史转让，历史承接债权均视为完结债权，不用上报。<br>
 * （3）上报初始债权数据触发时间：初始债权对应的散标放款成功之后上报该数据。<br>
 * (4)针对表里的“锁定期”，有一种场景需要注意：如果多个散标对应一个产品（出借人购买），其中某个散标在该产品锁定期内到期，那该散标则传实际到期时间。
 * 例：产品A 锁定期为2019年1月1日-2019年3月30日，产品A对应10个散标，10个散标中有一个叫b散标，b 散标在2019年2月1日到期，
 * 则b散标对应的初始债权锁定截止日期传2019年2月1日。为保持产品A正常运转，平台会将撮合一个新的散标c以替换b散标，c散标到期日期大于产品A锁定期，
 * 该c 散标对应的初始债权锁定截止日期传2019年3月30日。<br>
 * author: Roy <br>
 * date: 2019年5月14日 下午3:51:09
 */
@Service("creditorDataAccessService")
public class CreditorDataAccessService {

	private static final Logger log = LoggerFactory.getLogger(CreditorDataAccessService.class);

	@Resource
	private WloanTermProjectDao wloanTermProjectDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Resource
	private BatchNumDao batchNumDao;
	@Resource
	private CreditorDao creditorDao;
	/**
	 * 国家应急中心工具包.
	 */
	public static CertToolV1 tool = new CertToolV1();

	/**
	 * 
	 * methods: pushCreditorInfo <br>
	 * description: 补推初始债权. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月14日 下午3:49:11
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> filePushCreditorInfo(List<Creditor> creditorList) {

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
			for (Creditor creditor : creditorList) {
				// 数据中心初始债权表.
				Creditor c = new Creditor();
				/**
				 * 初始债权封装.
				 */
				Map<String, Object> param = new LinkedHashMap<String, Object>();
				param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
				c.setVersion(ServerURLConfig.VERSION);
				param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
				c.setSourceCode(ServerURLConfig.SOURCE_CODE);
				param.put("finClaimId",creditor.getFinClaimid()); // 初始债权编号.
				c.setFinClaimid(creditor.getFinClaimid());
				param.put("sourceProductCode",creditor.getSourceProductCode()); // 散标信息编号.
				c.setSourceProductCode(creditor.getSourceProductCode());
				param.put("userIdcardHash",creditor.getUserIdcardHash()); // 出借用户证件号hash值.
				c.setUserIdcardHash(creditor.getUserIdcardHash());
				param.put("invAmount",creditor.getInvAmount()); // 出借金额（元）保留小数位后2位.
				c.setInvAmount(creditor.getInvAmount());
				param.put("invRate",creditor.getInvRate()); // 出借预期年化利率.
				c.setInvRate(creditor.getInvRate());
				param.put("invTime",creditor.getInvTime()); // 出借计息时间.
				c.setInvTime(creditor.getInvTime());
				param.put("redpackage", "0.00"); // 出借红包（满减）元.
				c.setRedpackage("0.00");
				param.put("lockTime", LockTimeTypeEnum.LOCK_TIME_TYPE_NEGATIVE_1.getValue()); // 不允许债权转让的锁定截至时间.
				c.setLockTime(LockTimeTypeEnum.LOCK_TIME_TYPE_NEGATIVE_1.getValue());
				c.setBatchNum(batchNum);
				c.setSendTime(sentTime);
				int insert = creditorDao.insert(c);
				if (insert == 1) {
					log.info("初始债权信息插入成功！");
				} else {
					log.info("初始债权信息插入失败！");
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_82.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_CREDITOR, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
	 * methods: pushCreditorInfo <br>
	 * description: 推送初始债权. <br>
	 * 注意：当前接口部署正式上线需要修改两个地方，1）dataType（接口数据类型，0:调试数据，1：正式数据），2）接口地址需要修改，后缀需要去掉test.<br>
	 * author: Roy <br>
	 * date: 2019年5月14日 下午3:49:11
	 * 
	 * @param projectIdList
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> pushCreditorInfo(List<String> projectIdList) {

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
				WloanTermProject pro = wloanTermProjectDao.get(projectId);
				if (pro != null) {
					if (WloanTermProjectService.REPAYMENT.equals(pro.getState()) || WloanTermProjectService.FINISH.equals(pro.getState())) { // 放款操作推送，还款中.
						List<WloanTermInvest> investList = wloanTermInvestDao.findListByProjectId(projectId);
						for (WloanTermInvest invest : investList) {
							// 数据中心初始债权表.
							Creditor c = new Creditor();
							/**
							 * 初始债权封装.
							 */
							Map<String, Object> param = new LinkedHashMap<String, Object>();
							param.put("version", ServerURLConfig.VERSION); // 国家应急中心数据接入，接口版本号.
							c.setVersion(ServerURLConfig.VERSION);
							param.put("sourceCode", ServerURLConfig.SOURCE_CODE); // 国家应急中心数据接入，平台编码.
							c.setSourceCode(ServerURLConfig.SOURCE_CODE);
							param.put("finClaimId", invest.getId()); // 初始债权编号.
							c.setFinClaimid(invest.getId());
							param.put("sourceProductCode", invest.getWloanTermProject().getSn()); // 散标信息编号.
							c.setSourceProductCode(invest.getWloanTermProject().getSn());
							param.put("userIdcardHash", tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo()))); // 出借用户证件号hash值.
							c.setUserIdcardHash(tool.idCardHash(StringUtils.replaceBlanK(invest.getUserInfo().getCertificateNo())));
							param.put("invAmount", NumberUtils.scaleDoubleStr(invest.getAmount())); // 出借金额（元）保留小数位后2位.
							c.setInvAmount(NumberUtils.scaleDoubleStr(invest.getAmount()));
							param.put("invRate", NumberUtils.scaleSixStr(NumberUtils.divide(invest.getWloanTermProject().getAnnualRate(), 100D))); // 出借预期年化利率.
							c.setInvRate(NumberUtils.scaleSixStr(NumberUtils.divide(invest.getWloanTermProject().getAnnualRate(), 100D)));
							param.put("invTime", DateUtils.formatDate(invest.getWloanTermProject().getFullDate(), "yyyy-MM-dd HH:mm:ss")); // 出借计息时间.
							c.setInvTime(DateUtils.formatDate(invest.getWloanTermProject().getFullDate(), "yyyy-MM-dd HH:mm:ss"));
							param.put("redpackage", "0.00"); // 出借红包（满减）元.
							c.setRedpackage("0.00");
							param.put("lockTime", LockTimeTypeEnum.LOCK_TIME_TYPE_NEGATIVE_1.getValue()); // 不允许债权转让的锁定截至时间.
							c.setLockTime(LockTimeTypeEnum.LOCK_TIME_TYPE_NEGATIVE_1.getValue());
							c.setBatchNum(batchNum);
							c.setSendTime(sentTime);
							int insert = creditorDao.insert(c);
							if (insert == 1) {
								log.info("初始债权信息插入成功！");
							} else {
								log.info("初始债权信息插入失败！");
							}
							list.add(param);
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
			json.accumulate("infType", InfTypeEnum.INF_TYPE_82.getValue());
			json.accumulate("dataType", Global.getConfig("DATA_TYPE")); // 接口数据类型，0：调试数据，1：正式数据.
			json.accumulate("timestamp", currentTimeMillis + "");
			json.accumulate("nonce", nonce);
			json.accumulate("dataList", list);
			log.info("存量推送数据：" + json);
			String responseStr = HttpsUtilSendPost.sendPost(json, currentTimeMillis, nonce, ServerURLConfig.ENDPOINT_CREDITOR, "utf-8");
			log.info("存量响应数据：" + responseStr);
			com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				String code = (String) jsonObject.get("code");
				if (ResponseEnum.RESPONSE_CODE_MSG_0000.getValue().equals(code)) { // 数据已成功上报，正在等待处理，请定期(1~2 小时)使用对账接口查看数据状态.
					BatchNum bn = new BatchNum();
					bn.setId(IdGen.uuid());
					bn.setBatchNum(batchNum);
					bn.setSendTime(sentTime);
					bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
					bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
				bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
			bn.setInfType(InfTypeEnum.INF_TYPE_82.getValue());
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
