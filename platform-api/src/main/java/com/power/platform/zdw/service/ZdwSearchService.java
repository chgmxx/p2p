package com.power.platform.zdw.service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.FileUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.juhe.JuHeCodeUtil;
import com.power.platform.zdw.dao.ZdwSearchRegisterInfoDao;
import com.power.platform.zdw.entity.Search;
import com.power.platform.zdw.entity.ZdwSearchRegisterInfo;
import com.power.platform.zdw.type.CodeDiscernEnum;
import com.power.platform.zdw.type.CodeTypeEnum;
import com.power.platform.zdw.utils.FileUtil;
import com.power.platform.zdw.utils.HttpUtils;
import com.power.platform.zdw.utils.Response;

@Service("zdwSearchService")
public class ZdwSearchService {

	private static final Logger logger = LoggerFactory.getLogger(ZdwSearchService.class);
	private static String captchaCode;
	private int num = 0;
	@Autowired
	private ZdwSearchRegisterInfoDao zdwSearchRegisterInfoDao;
	/**
	 * 生产地址，文件目录前缀.
	 */
	private static final String OUT_PATH = "/data/upload/";

	/**
	 * 测试地址，文件目录前缀.
	 */
	// private static final String OUT_PATH = "D:\\files\\";

	public String getReport(Search s) {

		String key = s.getUserName() + "|" + s.getPassword();

		CookieStore cookieStore = HttpUtils.getCookies(key);
		logger.info(cookieStore.toString());
		CloseableHttpClient httpClient = HttpUtils.getHttpClient(true, cookieStore);
		Response responseResult = getCode(httpClient);
		JSONObject jsonObject = JSONObject.parseObject(responseResult.toString());
		if (jsonObject != null) {
			boolean success = (boolean) jsonObject.get("success");
			if (success) {
				logger.info("按主体查询，验证码校验:{}", success);
			} else {
				return "false";
			}
		} else {
			return "false";
		}

		return serch(httpClient, s, s.getUserName());

	}

	private String serch(CloseableHttpClient httpClient, Search s, String key) {

		try {
			String url = "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=QueryByName";
			Map<String, Object> params = new HashMap<>();
			params.put("debttype", "1000");
			params.put("name", s.getGuarantor());
			params.put("cert_no", "");
			params.put("confirm", "true");
			params.put("validateCode", captchaCode);
			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=init");
			String html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			// logger.info("html:" + html);
			if (html.contains("本次查询共查询到登记")) {
				save(html, httpClient, key);
				logger.info("查询结果：{}", "成功");
				return "成功";
			} else if (Jsoup.parse(html).select("#code").text().trim().contains("校验码错误")) {
				getReport(s);
				logger.info("查询结果：{}", "成功");
				return "成功";
			} else if (html.contains("requested has moved")) {
				// 登录失效
				logger.info("查询结果：{}", "重新登录");
				return "重新登录";
			} else {
				logger.info("查询结果：{}", "未知异常");
				return "未知异常";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "查询异常";
		}
	}

	private void save(String html, CloseableHttpClient httpClient, String key) {

		try {

			String filePath = OUT_PATH + key + File.separator + DateUtils.getFileDate();
			FileUtils.createDirectory(FileUtils.path(filePath));

			// long times = System.currentTimeMillis();
			String s = "{ar" + html.split("\\{ar")[1].split("\\[\\]}}")[0] + "[]}}";

			// System.out.print(s);

			JSONObject j = (JSONObject) JSON.parse(s);

			// System.out.print(j);

			JSONArray result = j.getJSONObject("ar").getJSONArray("d");
			JSONObject object = new JSONObject();
			Elements es = Jsoup.parse(html).select(".table3 >tbody >tr.lanhuibiao");
			String queryPerson = "";
			String guarantorCompanyName = "";
			String queryDateTime = "";
			String queryProveNo = "";
			for (Element e : es) {
				if (e.toString().contains("人：")) {
					queryPerson = e.select("td").get(1).text().split("人：")[1];
				}
				if (e.toString().contains("条件：")) {
					guarantorCompanyName = e.select("td").get(1).text().split("条件：")[1];
				}
				if (e.toString().contains("时间：")) {
					queryDateTime = e.select("td").get(1).text().split("时间：")[1];
				}
				if (e.toString().contains("编号:")) {
					queryProveNo = e.select("td:nth-child(3) > a > span").text().split("编号:")[1];
				}
			}

			// 保存查询报告.
			ZdwSearchRegisterInfo zsri = new ZdwSearchRegisterInfo();
			String queryProveFilePath = "中国人民银行征信中心动产融资统一登记公示系统查询证明，文件路径";
			object.put("queryPerson", queryPerson);
			zsri.setQueryPerson(queryPerson);
			object.put("guarantorCompanyName", guarantorCompanyName);
			zsri.setGuarantorCompanyName(guarantorCompanyName);
			object.put("queryDateTime", queryDateTime);
			zsri.setQueryDateTime(DateUtils.parseDate(queryDateTime));
			object.put("queryProveNo", queryProveNo);
			zsri.setQueryProveNo(queryProveNo);
			object.put("queryProveFilePath", queryProveFilePath);
			zsri.setQueryProveFilePath(queryProveFilePath);
			JSONArray dataList = new JSONArray();
			for (int i = 0; i < result.size(); i++) {

				zsri.setId(IdGen.uuid()); // UUID.

				JSONObject dataObject = new JSONObject();
				List<Object> d = result.getJSONArray(i);

				String no = (String) d.get(0);
				dataObject.put("no", no);
				zsri.setNo(no);

				String registerProveNo = (String) d.get(1);
				dataObject.put("registerProveNo", registerProveNo);
				zsri.setRegisterProveNo(registerProveNo);

				String registerDateTime = (String) d.get(2);
				dataObject.put("registerDateTime", registerDateTime);
				zsri.setRegisterDateTime(DateUtils.parseDate(registerDateTime));

				String registerExpireDateTime = (String) d.get(3);
				dataObject.put("registerExpireDateTime", registerExpireDateTime);
				zsri.setRegisterExpireDateTime(DateUtils.parseDate(registerExpireDateTime));

				String registerType = (String) d.get(4);
				dataObject.put("registerType", registerType);
				zsri.setRegisterType(registerType);

				String pledgeeName = (String) d.get(5);
				dataObject.put("pledgeeName", pledgeeName);
				zsri.setPledgeeName(pledgeeName);

				String url1 = "https://www.zhongdengwang.org.cn/rs/conditionquery/byid.do?method=viewfile&regno=" + d.get(1) + "&type=1";
				HttpGet get = HttpUtils.get(url1);
				get.setHeader("Host", "www.zhongdengwang.org.cn");
				get.setHeader("Referer", "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=QueryByName");
				// String result1 =
				HttpUtils.executeGetWithResult(httpClient, get);

				// System.out.println(result1);

				String url = "https://www.zhongdengwang.org.cn/rs/conditionquery/byid.do?method=downloadregfile";
				Map<String, Object> params = new HashMap<>();
				params.put("regno", d.get(1));
				params.put("type", "1");
				params.put("save_name", "");
				params.put("show_name", "");

				HttpPost post = HttpUtils.post(url, params);
				post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
				post.addHeader("Host", "www.zhongdengwang.org.cn");
				post.addHeader("Referer", url1);

				CloseableHttpResponse response = httpClient.execute(post);
				InputStream in = response.getEntity().getContent();
				// 测试环境文件地址.
				// File xlsFile = FileUtil.createFile(filePath + File.separator + d.get(1) + "_flow.pdf");
				// 生产环境文件地址.
				File xlsFile = FileUtil.createFile(filePath + File.separator + d.get(1) + "_flow.pdf");
				// 保存excel文件
				FileUtils.copyInputStreamToFile(in, xlsFile);
				response.close();
				// 测试环境文件地址.
				// dataObject.put("registerProveFilePath", filePath + File.separator + d.get(1) + "_flow.pdf");
				// 生产环境文件地址.
				dataObject.put("registerProveFilePath", filePath.split("data")[1] + File.separator + d.get(1) + "_flow.pdf");
				// 测试环境文件地址.
				// zsri.setRegisterProveFilePath(filePath + File.separator + d.get(1) + "_flow.pdf");
				// 生产环境文件地址.
				zsri.setRegisterProveFilePath(filePath.split("data")[1] + File.separator + d.get(1) + "_flow.pdf");
				int insert = zdwSearchRegisterInfoDao.insert(zsri);
				if (insert == 1) {
					logger.info("查询报告插入成功！");
				} else {
					logger.info("查询报告插入失败！");
				}
				dataList.add(dataObject);
			}
			object.put("dataList", dataList);

			// System.out.println(object);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Response getCode(CloseableHttpClient httpClient) {

		try {
			String url = "https://www.zhongdengwang.org.cn/rs/query/bysubject.do";
			HttpGet get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/main.do");
			HttpUtils.executeGetWithResult(httpClient, get);
			url = "https://www.zhongdengwang.org.cn/rs/js/validate.js";
			get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/query/bysubject.do");
			HttpUtils.executeGetWithResult(httpClient, get);
			url = "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=init";
			get = new HttpGet(url);
			HttpUtils.executeGetWithResult(httpClient, get);
			url = "https://www.zhongdengwang.org.cn/rs/include/vcodeimage4.jsp?" + Math.random();
			get = HttpUtils.get(url);
			get.addHeader("referer", "https://www.zhongdengwang.org.cn/rs/conditionquery/byname.do?method=init");
			File imageFile = HttpUtils.getCaptchaCodeImage(httpClient, get);
			// String captchaCode = OCRUtils.commitCat("6", "999", image);
			// Scanner sc = new Scanner(System.in);
			// String code = sc.next();
			String responseStr = JuHeCodeUtil.post(CodeTypeEnum.CODE_TYPE_1004.getValue(), imageFile);
			JSONObject jsonObject = JSONObject.parseObject(responseStr);
			if (jsonObject != null) {
				Integer errorCode = (Integer) jsonObject.get("error_code");
				if (CodeDiscernEnum.ERROR_CODE_0.getValue().equals(errorCode)) { // 识别成功.
					String result = (String) jsonObject.get("result");
					captchaCode = result;
				} else {
					return Response.FAILURE;
				}
			}
			logger.info("继续验证图片" + captchaCode);
			num++;
			if (num > 10) {
				return Response.SYSTEM_WRONG_TIME_CODE;
			}
			return Response.SUCCESS;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.SYSTEM_WRONG_UNKNOWN;
		}
	}

	public static void main(String args[]) {

		ZdwSearchService z = new ZdwSearchService();
		Search u = new Search("cicmorgan456", "cicmorgan456");
		u.setGuarantor("德州市久盛食品有限公司");
		String result = z.getReport(u);
		System.out.println(result);
	}
}
