package com.power.platform.task.zdw;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.credit.dao.ztmgLoanBasicInfo.ZtmgLoanBasicInfoDao;
import com.power.platform.credit.entity.voucher.CreditVoucher;
import com.power.platform.credit.entity.ztmgLoanBasicInfo.ZtmgLoanBasicInfo;
import com.power.platform.credit.service.voucher.CreditVoucherService;
import com.power.platform.regular.dao.WloanTermProjectDao;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.zdw.dao.ZdwProOrderInfoDao;
import com.power.platform.zdw.entity.ZdwProOrderInfo;
import com.power.platform.zdw.pojo.ZdPledgor;
import com.power.platform.zdw.pojo.ZdPreparer;
import com.power.platform.zdw.pojo.ZdProperty;
import com.power.platform.zdw.pojo.ZdRegister;
import com.power.platform.zdw.type.CurrencyEnum;
import com.power.platform.zdw.type.EnterpriseScaleEnum;
import com.power.platform.zdw.type.LeiEnum;
import com.power.platform.zdw.type.PledgorTypeEnum;
import com.power.platform.zdw.type.ProOrderStatusEnum;

/**
 * 
 * 类: AutoSendZdRegisterInfoTask <br>
 * 描述: 自动登记信息轮询脚本 <br>
 * 作者: Roy <br>
 * 时间: 2019年11月1日 下午5:28:57
 */
@Service
@Lazy(false)
public class AutoSendZdRegisterInfoTask {

	private static final Logger logger = LoggerFactory.getLogger(AutoSendZdRegisterInfoTask.class);

	private static final String SERVICE_URL = "http://182.92.114.130:8002/api/zdwRegisterInsert/zdwRegisterInsert";

	@Autowired
	private ZdwProOrderInfoDao zdwProOrderInfoDao;
	@Autowired
	private WloanTermProjectDao wloanTermProjectDao;
	@Autowired
	private CreditVoucherService creditVoucherService;
	@Autowired
	private ZtmgLoanBasicInfoDao ztmgLoanBasicInfoDao;

	/**
	 * 
	 * 方法: autoSendZdRegisterInfo <br>
	 * 描述: 每天早上09:15触发，发送待登记信息 <br>
	 * 作者: Roy <br>
	 * 时间: 2019年11月1日 下午5:16:21
	 */
	@Scheduled(cron = "0 30 14 * * ?")
	public void autoSendZdRegisterInfo() {

		logger.info("中登等待登记列表...start...");
		try {

			// 满标落单，等待登记
			ZdwProOrderInfo queryEntity = new ZdwProOrderInfo();
			queryEntity.setStatus(ProOrderStatusEnum.PRO_ORDER_STATUS_01.getValue());
			List<ZdwProOrderInfo> zdwProOrderList = zdwProOrderInfoDao.findList(queryEntity);
			WloanTermProject project = null;
			long currentTimeMillis = System.currentTimeMillis();
			ZtmgLoanBasicInfo entity = null;
			for (ZdwProOrderInfo zpoi : zdwProOrderList) {
				Map<String, Object> params = new HashMap<String, Object>();
				ZdRegister ZdwRegisterParam = new ZdRegister();
				logger.info("proId:{},proNo:{}", zpoi.getProId(), zpoi.getProNo());
				currentTimeMillis = currentTimeMillis + 1000;
				StringBuffer titleBuffer = new StringBuffer(); // 填表人归档号
				titleBuffer.append("ZTMG-").append(currentTimeMillis);

				logger.info("填表人归档号:{}", titleBuffer.toString());
				project = wloanTermProjectDao.get(zpoi.getProId());
				Integer span = 0;
				if (null != project) {
					span = project.getSpan();
					int monthI = span / 30;
					ZdwRegisterParam.setSn(project.getSn()); // 标的编号
					ZdwRegisterParam.setRegisterSpan(String.valueOf(monthI)); // 1~360正整数，单位：月

					// 填表人信息
					ZdPreparer zdPreparer = new ZdPreparer();
					zdPreparer.setTimeLimit(String.valueOf(monthI)); // 1~360正整数，单位：月
					zdPreparer.setTitle(titleBuffer.toString()); // 填表人归档号
					ZdwRegisterParam.setZdPreparer(zdPreparer);

					// 出质人信息
					ZdPledgor zdPledgor = new ZdPledgor();
					zdPledgor.setPledgorType(PledgorTypeEnum.PLEDGOR_TYPE_02.getValue()); // 出质人类型
					if (null != project.getWloanSubject()) {
						zdPledgor.setPledgorName(project.getWloanSubject().getCompanyName()); // 出质人名称
						zdPledgor.setRegisterCode(""); // 工商注册号
						zdPledgor.setOrganizationCode(project.getWloanSubject().getBusinessNo()); // 组织机构代码/统一社会信用代码，若已发放统一社会信用代码可在此处填写
						zdPledgor.setUscc(project.getWloanSubject().getBusinessNo()); // 组织机构代码/统一社会信用代码，若已发放统一社会信用代码可在此处填写
						zdPledgor.setResponsiblePerson(project.getWloanSubject().getLoanUser()); // 法定代表人/负责人
						zdPledgor.setLei(LeiEnum.LEI_9999.getValue()); // 其它
						zdPledgor.setScale(EnterpriseScaleEnum.ENTERPRISE_SCALE_30.getValue()); // 企业规模
						zdPledgor.setCountry(""); // 国家
						entity = new ZtmgLoanBasicInfo();
						entity.setCreditUserId(project.getWloanSubject().getLoanApplyId());
						List<ZtmgLoanBasicInfo> zlbiList = ztmgLoanBasicInfoDao.findList(entity);
						if (null != zlbiList) {
							if (zlbiList.size() > 0) {
								ZtmgLoanBasicInfo ztmgLoanBasicInfo = zlbiList.get(0);
								zdPledgor.setProvince(ztmgLoanBasicInfo.getProvince()); // 省/直辖市
								zdPledgor.setCity(ztmgLoanBasicInfo.getCity()); // 市/地级市
								zdPledgor.setAddress(ztmgLoanBasicInfo.getStreet());
							}
						}
					}
					ZdwRegisterParam.setZdPledgor(zdPledgor);

					// 质押财产
					ZdProperty zdProperty = new ZdProperty();
					zdProperty.setMainCurrencyCategory(CurrencyEnum.CURRENCY_CNY.getValue()); // 人名币
					zdProperty.setMainPrice(NumberUtils.scaleDoubleStr(project.getAmount())); // 主合同金额、
					zdProperty.setPledgeCurrencyCategory(CurrencyEnum.CURRENCY_CNY.getValue()); // 人名币
					// 发票
					List<CreditVoucher> list = creditVoucherService.findCreditVoucher(project.getId());
					Double pledgePrice = 0D;

					// 项目情况
					String projectCase = project.getProjectCase();
					StringBuffer projectCaseBuffer = new StringBuffer();
					projectCaseBuffer.append(projectCase);
					if (null != list && list.size() > 0) {
						projectCaseBuffer.append(" 附：原发票号为，");
					}
					for (CreditVoucher creditVoucher : list) {
						pledgePrice = NumberUtils.add(pledgePrice, Double.parseDouble(creditVoucher.getMoney()));
						projectCaseBuffer.append("No.").append(creditVoucher.getNo()).append(",");
					}
					if (null != list && list.size() > 0) {
						projectCaseBuffer.append("剩余履约期限为").append(span / 30).append("个月。");
					}
					zdProperty.setPledgePrice(NumberUtils.scaleDoubleStr(pledgePrice));
					zdProperty.setPledgePropertyDetails(projectCaseBuffer.toString()); // 项目情况
					ZdwRegisterParam.setZdProperty(zdProperty);
				}

				params.put("ZdwRegisterParam", ZdwRegisterParam);
				logger.info("登记数据:{}", JSONObject.toJSON(params).toString());

				String resp = HttpPostWithJson(SERVICE_URL, JSONObject.toJSON(params).toString());
				logger.info("response:{}", resp);

				// 解析响应
				JSONObject respJSONObject = JSONObject.parseObject(resp);
				if ("000000".equals(String.valueOf(respJSONObject.get("code")))) { // 登记成功
					logger.info("标的登记信息推送成功");
					zpoi.setStatus(ProOrderStatusEnum.PRO_ORDER_STATUS_00.getValue());
					zpoi.setUpdateDate(new Date(currentTimeMillis));
					int zdwProOrderInfoFlag = zdwProOrderInfoDao.update(zpoi);
					logger.info("标的登记信息状态更新:{}", zdwProOrderInfoFlag == 1 ? "成功" : "失败");
				} else { // 登记失败
					logger.info("标的中登登记信息推送失败");
					zpoi.setUpdateDate(new Date(currentTimeMillis));
					zpoi.setRemarks(String.valueOf(respJSONObject.get("msg")));
					int zdwProOrderInfoFlag = zdwProOrderInfoDao.update(zpoi);
					logger.info("标的登记信息状态更新:{}", zdwProOrderInfoFlag == 1 ? "成功" : "失败");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("中登等待登记列表...end...");
	}

	public static String HttpPostWithJson(String url, String json) {

		String returnValue = "这是默认返回值，接口调用失败";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			// 第一步：创建HttpClient对象
			httpClient = HttpClients.createDefault();

			// 第二步：创建httpPost对象
			HttpPost httpPost = new HttpPost(url);

			// 第三步：给httpPost设置JSON格式的参数
			StringEntity requestEntity = new StringEntity(json, "utf-8");
			requestEntity.setContentEncoding("UTF-8");
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setEntity(requestEntity);

			// 第四步：发送HttpPost请求，获取返回值
			returnValue = httpClient.execute(httpPost, responseHandler); // 调接口获取返回值时，必须用此方法

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 第五步：处理返回值
		return returnValue;
	}

}
