package com.power.platform.zdw.service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.zdw.utils.FileUtil;
import com.power.platform.zdw.utils.HttpUtils;
import com.power.platform.zdw.utils.JedisClient;

@Service("zdwSaveService")
public class ZdwSaveService {

	private static final Logger logger = LoggerFactory.getLogger(ZdwSaveService.class);

	public String addUserInfo(JSONObject j) {

		try {
			System.out.println(j);
			String key = j.getString("userName") + "|" + j.getString("password");
			CookieStore cookieStore = HttpUtils.getCookies(key);
			logger.info(cookieStore.toString());
			CloseableHttpClient httpClient = HttpUtils.getHttpClient(true, cookieStore);
			String url = "https://www.zhongdengwang.org.cn/rs/register/initreg.do";
			HttpGet get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/main.do");
			String result = HttpUtils.executeGetWithResult(httpClient, get);
			if (result.contains("用户连接超时，请重新") || result.contains("/sessiontimeout.jsp") || result.contains("同名用户已在别处登录")) {
				return "请重新登录";
			}
			url = "https://www.zhongdengwang.org.cn/rs/initRegBusinessTypeInfo.do";
			Map<String, Object> params = new HashMap<>();
			params.put("btype", "A00100");
			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/register/initreg.do");
			String html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("html5:" + html);

			url = "https://www.zhongdengwang.org.cn/rs/initPledgeAgree.do";
			get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegBusinessTypeInfo.do");
			HttpUtils.executeGetWithResult(httpClient, get);

			url = "https://www.zhongdengwang.org.cn/rs/initRegBusinessTypeInfo.do";
			params.clear();
			params.put("btype", "A00104");
			post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initPledgeAgree.do");
			html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("html5:" + html);

			url = "https://www.zhongdengwang.org.cn/rs/initRegBaseInfo.do";
			params.clear();
			params.put("timelimit", j.getString("timelimit"));
			params.put("title", j.getString("title"));
			post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegBusinessTypeInfo.do");
			html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("html5:" + html);
			JedisClient.set(key, cookieStore, 6 * 10000);
			if (html.contains("预&nbsp;&nbsp;览")) {
				logger.info("登记填表人/基本信息成功");
				return addPledgeeInfo(httpClient);
			} else {
				return "登记填表人/基本信息失败";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "登记填报人和质权人信息失败";
		}
	}

	public String addPledgerInfo(JSONObject json) {

		try {
			String key = json.getString("userName") + "|" + json.getString("password");
			CookieStore cookieStore = HttpUtils.getCookies(key);
			logger.info(cookieStore.toString());
			CloseableHttpClient httpClient = HttpUtils.getHttpClient(true, cookieStore);
			String url = "https://www.zhongdengwang.org.cn/rs/debtorinfo.do";
			HttpGet get = new HttpGet(url);
			String result = HttpUtils.executeGetWithResult(httpClient, get);
			logger.info("result:" + result);

			if (result.contains("用户连接超时，请重新") || result.contains("/sessiontimeout.jsp") || result.contains("同名用户已在别处登录")) {
				return "请重新登录";
			}
			url = "https://www.zhongdengwang.org.cn/rs/debtorTransfer.do";
			get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/debtorinfo.do");
			result = HttpUtils.executeGetWithResult(httpClient, get);
			String token = result.split("html.TOKEN\" value=\"")[1].split("\">")[0];
			logger.info("token:" + token);
			url = "https://www.zhongdengwang.org.cn/rs/dict/getDict.do";
			Map<String, Object> params = new HashMap<>();
			params.put("method", "area");
			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/debtorTransfer.do");
			String html = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("area:" + html);
			JSONArray array = (JSONArray) JSONArray.parse(html);
			logger.info("array:" + array);
			String provinceCode = "";
			String cityCode = "";
			for (int i = 0; i < array.size(); i++) {
				String pro = array.getJSONObject(i).getString("name");
				JSONArray citys = array.getJSONObject(i).getJSONArray("sub");
				System.out.println(pro);
				if (json.getString("province").equals(pro)) {
					provinceCode = array.getJSONObject(i).getString("code");
					for (int j = 0; j < citys.size(); j++) {
						if (json.getString("city").equals(citys.getJSONObject(j).getString("name"))) {
							cityCode = citys.getJSONObject(j).getString("code");
							break;
						}
					}
				}
				;
			}
			System.out.println(provinceCode + "|" + cityCode);

			String scale = "";
			if ("大型企业".equals(json.getString("scale"))) {
				scale = "10";
			} else if ("中型企业".equals(json.getString("scale"))) {
				scale = "20";
			} else if ("小型企业".equals(json.getString("scale"))) {
				scale = "30";
			} else if ("微型企业".equals(json.getString("scale"))) {
				scale = "40";
			}

			url = "https://www.zhongdengwang.org.cn/rs/initRegDebtorAdd.do";
			params.clear();
			params.put("org.apache.struts.taglib.html.TOKEN", token);
			params.put("debtorId", "");
			params.put("debtorType", "02");
			params.put("debtorName", json.getString("debtorName"));
			params.put("orgCode", json.getString("orgCode"));
			params.put("orgCodeNoVerify", "");
			params.put("financeCode", "");
			params.put("businessCode", json.getString("businessCode"));
			params.put("legalCertificateNo", "");
			params.put("registrationCertificateNo", "");
			params.put("lei", json.getString("lei"));
			params.put("responsiblePerson", json.getString("responsiblePerson"));
			params.put("tradeName", "");
			params.put("idType", "");
			params.put("idCode", "");
			params.put("passportCountry", "");
			params.put("passportCode", "");
			params.put("passportDate", "");
			params.put("industryCode", "9999");
			params.put("scale", scale);
			params.put("country", "CHN");
			params.put("province", provinceCode);
			params.put("city", cityCode);
			params.put("address", json.getString("address"));
			params.put("countryNoVerify", "");
			params.put("provinceNoVerify", "");
			params.put("cityNoVerify", "");
			params.put("addressNoVerify", "");
			post = HttpUtils.post(url, params);
			post.addHeader("Cache-Control", "max-age=0");
			post.addHeader("Upgrade-Insecure-Requests", "1");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/debtorTransfer.do");
			String text = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("text:" + text);
			if (text.contains("系统繁忙")) {
				return "请按接口规范进行登记";
			}
			if (text.contains("预&nbsp;&nbsp;览") || text.contains("保&nbsp;&nbsp;存")) {
				JedisClient.set(key, cookieStore, 6 * 1000);
				return "登记出质人信息成功";
			} else {
				return "登记出质人信息失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "登记出质人信息异常";
		}
	}

	public String addPledgeeInfo(CloseableHttpClient httpClient) {

		try {
			String url = "https://www.zhongdengwang.org.cn/rs/spinfo.do";
			HttpGet get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegBaseInfo.do");
			HttpUtils.executeGetWithResult(httpClient, get);

			url = "https://www.zhongdengwang.org.cn/rs/addSpAuto.do?randomNum=" + Math.random();
			get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/spinfo.do");
			String detail = HttpUtils.executeGetWithResult(httpClient, get);
			String spId = detail.split("modifySp\\('")[1].split("'\\);")[0];
			System.out.println(spId);
			String url1 = "https://www.zhongdengwang.org.cn/rs/modifySp.do?spId=" + spId;
			get = new HttpGet(url1);
			get.addHeader("Referer", url);
			String html = HttpUtils.executeGetWithResult(httpClient, get);
			System.out.println(html);
			String token = html.split("html.TOKEN\" value=\"")[1].split("\">")[0];
			String debtorType = html.split("id=\"debtorType\" value=\"")[1].split("\">")[0];
			String country = html.split("#country\"\\).val\\(\"")[1].split("\"\\)")[0];
			String provinceCode = html.split("#province\"\\).val\\(\"")[1].split("\"\\)")[0];
			String cityCode = html.split("city\"\\).val\\(\"")[1].split("\"\\)")[0];
			logger.info("token:" + token);
			Document d = Jsoup.parse(html);
			String debtorName = d.select("#debtorName").attr("value");
			System.out.println(debtorName);
			if (debtorName.contains("代")) {
				debtorName = debtorName;
			} else {
				debtorName = debtorName + " (代)";
			}
			String orgCode = d.select("#orgCode").attr("value");
			String orgCodeNoVerify = d.select("#orgCodeNoVerify").attr("value");
			String financeCode = d.select("#financeCode").attr("value");
			String businessCode = d.select("#businessCode").attr("value");
			String legalCertificateNo = d.select("#legalCertificateNo").attr("value");
			String registrationCertificateNo = d.select("#registrationCertificateNo").attr("value");
			String responsiblePerson = d.select("#responsiblePerson").attr("value");
			String idCode = d.select("#idCode").attr("value");
			String passportCode = d.select("#passportCode").attr("value");
			String address = d.select("#address").attr("value");
			String addressNoVerify = d.select("#addressNoVerify").attr("value");
			url = "https://www.zhongdengwang.org.cn/rs/initRegSpAdd.do";
			Map<String, Object> params = new HashMap<>();
			params.put("org.apache.struts.taglib.html.TOKEN", token);
			params.put("spId", spId);
			params.put("debtorType", debtorType);
			params.put("debtorName", debtorName);
			params.put("orgCode", orgCode);
			params.put("orgCodeNoVerify", orgCodeNoVerify);
			params.put("financeCode", financeCode);
			params.put("businessCode", businessCode);
			params.put("legalCertificateNo", legalCertificateNo);
			params.put("registrationCertificateNo", registrationCertificateNo);
			params.put("lei", "");
			params.put("responsiblePerson", responsiblePerson);
			params.put("tradeName", "");
			params.put("idType", "");
			params.put("idCode", idCode);
			params.put("passportCountry", "");
			params.put("passportCode", passportCode);
			params.put("passportDate", "");
			params.put("country", country);
			params.put("province", provinceCode);
			params.put("city", cityCode);
			params.put("address", address);
			params.put("countryNoVerify", "");
			params.put("provinceNoVerify", "");
			params.put("cityNoVerify", "");
			params.put("addressNoVerify", addressNoVerify);
			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Cache-Control", "max-age=0");
			post.addHeader("Upgrade-Insecure-Requests", "1");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			post.addHeader("Referer", url1);
			String text = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("text:" + text);
			if (text.contains("预&nbsp;&nbsp;览")) {
				return "登记填报人和质权人信息成功";
			} else {
				return "登记填报人和质权人信息失败";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "登记填报人和质权人信息异常";
		}

	}

	public String addPledgedPropertyInfo(JSONObject json) {

		try {
			long times = System.currentTimeMillis();
			String key = json.getString("userName") + "|" + json.getString("password");
			CookieStore cookieStore = HttpUtils.getCookies(key);
			logger.info(cookieStore.toString());
			CloseableHttpClient httpClient = HttpUtils.getHttpClient(true, cookieStore);
			String url = "https://www.zhongdengwang.org.cn/rs/describeinfo.do";
			HttpGet get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegDebtorAdd.do");
			String result = HttpUtils.executeGetWithResult(httpClient, get);
			logger.info("result:" + result);
			if (result.contains("用户连接超时，请重新") || result.contains("/sessiontimeout.jsp") || result.contains("同名用户已在别处登录")) {
				return "请重新登录";
			}
			if (result.contains("系统繁忙")) {
				return "请按接口规范进行登记";
			}
			String moneyType = "";
			String moneyType2 = "";
			if ("人民币".equals(json.getString("maincontractcurrency"))) {
				moneyType = "CNY";
			} else if ("美元".equals(json.getString("maincontractcurrency"))) {
				moneyType = "USD";
			} else if ("欧元".equals(json.getString("maincontractcurrency"))) {
				moneyType = "EUR";
			} else if ("英镑".equals(json.getString("maincontractcurrency"))) {
				moneyType = "GBP";
			} else if ("日元".equals(json.getString("maincontractcurrency"))) {
				moneyType = "JPY";
			} else if ("港币".equals(json.getString("maincontractcurrency"))) {
				moneyType = "HKY";
			} else {
				moneyType = "OTH";
			}

			if ("人民币".equals(json.getString("contractcurrency"))) {
				moneyType2 = "CNY";
			} else if ("美元".equals(json.getString("contractcurrency"))) {
				moneyType2 = "USD";
			} else if ("欧元".equals(json.getString("contractcurrency"))) {
				moneyType2 = "EUR";
			} else if ("英镑".equals(json.getString("contractcurrency"))) {
				moneyType2 = "GBP";
			} else if ("日元".equals(json.getString("contractcurrency"))) {
				moneyType2 = "JPY";
			} else if ("港币".equals(json.getString("contractcurrency"))) {
				moneyType2 = "HKY";
			} else {
				moneyType2 = "OTH";
			}
			String token = result.split("html.TOKEN\" value=\"")[1].split("\">")[0];
			url = "https://www.zhongdengwang.org.cn/rs/initRegDescribeInfo.do";
			Map<String, Object> params = new HashMap<>();
			params.put("org.apache.struts.taglib.html.TOKEN", token);
			params.put("descno", "");
			params.put("bondname", "");
			params.put("publishername", "");
			params.put("publishlimitbegin", "");
			params.put("publishlimitend", "");
			params.put("publishcurrency", "");
			params.put("bondlimitbegin", "");
			params.put("bondlimitend", "");
			params.put("publishsum", "");
			params.put("couponrate", "");
			params.put("maincontractno", json.getString("maincontractno"));
			params.put("maincontractcurrency", moneyType);
			params.put("maincontractsum", json.getString("maincontractsum"));
			params.put("loanbegindate", json.getString("loanbegindate"));
			params.put("loanenddate", json.getString("loanenddate"));
			params.put("contractno", json.getString("contractno"));
			params.put("contractcurrency", moneyType2);
			params.put("collateralsum", json.getString("collateralsum"));
			params.put("description", json.getString("description"));

			HttpPost post = HttpUtils.post(url, params);
			post.addHeader("Cache-Control", "max-age=0");
			post.addHeader("Upgrade-Insecure-Requests", "1");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/describeinfo.do");
			String text = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("text:" + text);

			url = "https://www.zhongdengwang.org.cn/rs/initRegPreview.do";
			get = new HttpGet(url);
			get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegDescribeInfo.do");
			result = HttpUtils.executeGetWithResult(httpClient, get);
			logger.info("result:" + result);
			token = result.split("html.TOKEN\" value=\"")[1].split("\">")[0];
			url = "https://www.zhongdengwang.org.cn/rs/initRegPreSubmit.do";
			params.clear();
			params.put("org.apache.struts.taglib.html.TOKEN", token);
			post = HttpUtils.post(url, params);
			post.addHeader("Cache-Control", "max-age=0");
			post.addHeader("Upgrade-Insecure-Requests", "1");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegPreview.do");
			text = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("text:" + text);
			if (text.contains("出质人必须存在")) {
				return "出质人必须存在";
			}
			token = text.split("html.TOKEN\" value=\"")[1].split("\">")[0];
			url = "https://www.zhongdengwang.org.cn/rs/initRegSubmit.do";
			params.clear();
			params.put("org.apache.struts.taglib.html.TOKEN", token);
			post = HttpUtils.post(url, params);
			post.addHeader("Cache-Control", "max-age=0");
			post.addHeader("Upgrade-Insecure-Requests", "1");
			post.addHeader("Host", "www.zhongdengwang.org.cn");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Origin", "https://www.zhongdengwang.org.cn");
			post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			post.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegPreSubmit.do");
			text = HttpUtils.executePostWithResult(httpClient, post, "UTF-8");
			logger.info("text:" + text);
			if (text.contains("本次登记修改码")) {
				String registration_number = text.split("初始登记：")[1].split("\\（点击")[0];
				String modification_code = text.split("记修改码: </span>")[1].split("<br")[0];
				url = "https://www.zhongdengwang.org.cn/rs/download.do?method=getDownload&type=00&id=" + registration_number;
				get = new HttpGet(url);
				get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/initRegSubmit.do");
				result = HttpUtils.executeGetWithResult(httpClient, get);
				logger.info("result:" + result);

				url = "https://www.zhongdengwang.org.cn/rs/download.do?method=getDownload&type=01&id=" + registration_number;
				get = new HttpGet(url);
				get.addHeader("Referer", "https://www.zhongdengwang.org.cn/rs/download.do?method=getDownload&type=00&id=" + registration_number);
				CloseableHttpResponse response = httpClient.execute(get);
				InputStream in = response.getEntity().getContent();
				// 地址需要修改
				String path = "D:\\files" + "/" + key.replace("|", "#") + "/" + registration_number + "#" + times + "_flow.pdf";
				File xlsFile = FileUtil.createFile(path);
				// 保存excel文件
				FileUtils.copyInputStreamToFile(in, xlsFile);
				response.close();
				System.out.println(registration_number);
				System.out.println(modification_code);
				System.out.println(path);
				return "登记质押财产信息成功";
			} else {
				return "登记质押财产信息失败";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "登记质押财产信息异常";
		}
	}

	public static void main(String args[]) {

		// 二。出质人信息
		// SaveService z =new SaveService();
		// JSONObject obj = new JSONObject();
		// obj.put("userName", "cicmorgan456");
		// obj.put("password", "cicmorgan456");
		// obj.put("province", "江西省");
		// obj.put("city", "萍乡市");
		// obj.put("debtorName", "江西金宝城食品工业有限公司");
		// obj.put("orgCode", "91360323065380921K");
		// obj.put("businessCode", "91360323065380921K");
		// obj.put("lei", "");
		// obj.put("scale", "小型企业");
		// obj.put("responsiblePerson", "宋文弟");
		// obj.put("address", "芦溪县宣风生物产业园A区");
		// String result = z.addPledgerInfo(obj);
		//
		// 一。填表人/基本信息
		// SaveService z =new SaveService();
		// JSONObject obj = new JSONObject();
		// obj.put("userName", "cicmorgan456");
		// obj.put("password", "cicmorgan456");
		// obj.put("timelimit", "0.5");//半年：0.5，1年：1.0
		// obj.put("title", "ztmg-2019070703");
		// String result = z.addUserInfo(obj);

		// 三。质押财产信息
		ZdwSaveService z = new ZdwSaveService();
		JSONObject obj = new JSONObject();
		obj.put("userName", "cicmorgan456");
		obj.put("password", "cicmorgan456");
		obj.put("maincontractno", "");
		obj.put("maincontractcurrency", "人民币");
		obj.put("maincontractsum", "188062.26");
		obj.put("loanbegindate", "");
		obj.put("loanenddate", "");
		obj.put("contractno", "");
		obj.put("contractcurrency", "人民币");
		obj.put("collateralsum", "235848.44");
		obj.put("description", "本次拟质押应收帐款为原债权人四川广乐食品有限公司与原债务人山西美特好连锁超市股份有限公司旗下的清徐县美特好农产品配送物流有限公司签署的编号6732为《商品采购合同》项下的新增应收帐款,共计：189,496.66 元。原发票号为：No.01492921，No.08897420，剩余履约期限为3个月。原债权人拟通过质押上述应收帐款的全部或部分价值，在本平台融资 152,500.00元，到期后由原债务人支付融资本金\n本次拟质押应收帐款为原债权人江西金宝城食品工业有限公司与债务人宁波熙耘科技有限公司签署的编号为 SP-19-066 的《商品采销购销合同》项下的新增应收帐款。共计：46,351.78元。原发票号为：No.03304275，剩余履约期限为3个月。原债权人拟通过质押上述应收帐款的全部或部分价值，在本平台融资35,562.26元，到期后由原债务人支付融资本金。");
		String result = z.addPledgedPropertyInfo(obj);
		//
		System.out.println(result);
	}

}
