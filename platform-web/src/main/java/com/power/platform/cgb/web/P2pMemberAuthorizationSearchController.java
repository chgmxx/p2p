package com.power.platform.cgb.web;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.pojo.Grant;
import com.power.platform.common.config.Global;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.userinfo.CreditUserAccountDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.service.apply.CreditUserApplyService;
import com.power.platform.credit.service.basicinfo.CreditBasicInfoService;
import com.power.platform.credit.service.userinfo.CreditUserInfoService;
import com.power.platform.plan.dao.WloanTermProjectPlanDao;
import com.power.platform.plan.dao.WloanTermUserPlanDao;
import com.power.platform.plan.service.WloanTermProjectPlanService;
import com.power.platform.regular.dao.WloanTermInvestDao;
import com.power.platform.regular.service.WloanSubjectService;
import com.power.platform.regular.service.WloanTermInvestService;
import com.power.platform.regular.service.WloanTermProjectService;
import com.power.platform.sys.utils.HttpUtil;

/**
 * 
 * 类: P2pTradeBidController <br>
 * 描述: 网贷资金存管接口，交易类. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年5月23日 上午9:00:38
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/p2p/member/authorization")
public class P2pMemberAuthorizationSearchController extends BaseController {

	private static final Logger log = LoggerFactory.getLogger(P2pMemberAuthorizationSearchController.class);

	/**
	 * 商户自己的RSA公钥.
	 */
	private static final String MERCHANT_RSA_PUBLIC_KEY = Global.getConfigUb("UB_MERCHANT_RSA_PUBLIC_KEY");
	/**
	 * 商户自己的RSA私钥.
	 */
	private static final String MERCHANT_RSA_PRIVATE_KEY = Global.getConfigUb("UB_MERCHANT_RSA_PRIVATE_KEY");
	/**
	 * 测试环境网关地址.
	 */
	private static final String HOST = Global.getConfigUb("UB_HOST");
	/**
	 * 商户号.
	 */
	private static final String MERCHANT_ID = Global.getConfigUb("UB_MERCHANT_ID");

	@Autowired
	private WloanTermProjectService wloanTermProjectService;
	@Autowired
	private WloanTermProjectPlanService wloanTermProjectPlanService;
	@Resource
	private WloanTermProjectPlanDao wloanTermProjectPlanDao;
	@Resource
	private WloanTermUserPlanDao wloanTermUserPlanDao;
	@Resource
	private WloanTermInvestDao wloanTermInvestDao;
	@Autowired
	private WloanTermInvestService wloanTermInvestService;
	@Autowired
	private CreditUserApplyService creditUserApplyService;
	@Autowired
	private CreditUserInfoService creditUserInfoService;
	@Autowired
	private CreditBasicInfoService creditBasicInfoService;
	@Autowired
	private WloanSubjectService wloanSubjectService;
	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Resource
	private CreditUserAccountDao creditUserAccountDao;
	@Resource
	private CreditUserApplyDao creditUserApplyDao;

	/**
	 * 
	 * 方法: search <br>
	 * 描述: 授权查询. <br>
	 * 作者: Mr.li <br>
	 * 时间: 2018年11月22日 下午1:44:25
	 * 
	 * @param userId
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:memberAuthorization:view")
	@ResponseBody
	@RequestMapping(value = "search")
	@Transactional(rollbackFor = Exception.class)
	public synchronized Map<String, Grant> search(String userId, Model model, RedirectAttributes redirectAttributes) throws Exception {

		log.info("fn:search-授权查询");
		Map<String, Grant> map = new HashMap<String, Grant>();
		Grant grant = new Grant();

		// 参数列表为null或者为空字符串.
		if (StringUtils.isBlank(userId)) {
			grant.setRespCode("02");
			grant.setRespMsg("查询失败，缺少必要参数 ...");
			map.put("grant", grant);
			return map;
		}

		/**
		 * 业务请求参数封装.
		 */
		Map<String, String> params = new HashMap<String, String>();
		// 网贷平台唯一的用户编码
		params.put("userId", userId);
		/**
		 * 公共请求参数封装.
		 */
		// 接口名称：每个接口提供不同的编码.
		params.put("service", "p2p.member.authorization.search");
		// 签名算法，固定值：RSA.
		params.put("method", "RSA");
		// 由存管银行分配给网贷平台的唯一的商户编码.
		params.put("merchantId", MERCHANT_ID);
		// 请求来源1:(PC)2:(MOBILE).
		params.put("source", "1");
		// PC端无需传入，移动端需传入两位的数字：
		// 第一位表示请求发起自APP还是WAP。（1表示APP，2表示WAP），第二位表示请求来自的操作系统类型。（1表示IOS，2表示Android）注：移动端请求如果传入空，系统将默认按照12处理，可能出现页面样式不兼容.
		// params.put("mobileType", "");
		// 请求时间格式："yyyy-MM-dd HH:mm:ss".
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		// 本次请求的唯一标识，防止重复提交；可由网贷平台自主生成，也可以调用存管系统提供的JAR包中方法生成.
		params.put("reqSn", IdGen.uuid());
		// 服务版本号.
		params.put("version", "1.0.0");
		// 每次请求的业务参数的签名值，详细请参考【签名】.
		// "RSA"商户私钥加密签名.
		String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
		params.put("signature", sign);
		String paramsJsonStr = JSON.toJSONString(params);
		log.info("参数列表：" + paramsJsonStr);
		// 商户自己的RSA公钥加密.
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(paramsJsonStr, MERCHANT_RSA_PUBLIC_KEY);

		/**
		 * HTTP.
		 */
		encryptRet.put("merchantId", MERCHANT_ID);
		String requestJsonStr = JSON.toJSONString(encryptRet);
		log.info("请求：" + requestJsonStr);
		String responseStr = HttpUtil.sendPost(HOST, encryptRet);
		log.info("响应：" + responseStr);

		/**
		 * 解析响应.
		 */
		JSONObject jsonObject = JSONObject.parseObject(responseStr);
		String tm = (String) jsonObject.get("tm");
		String data = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(tm, data, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> mapParseObject = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});
		log.info("解析响应：" + JSON.toJSONString(mapParseObject));

		grant.setRespCode(mapParseObject.get("respCode"));
		grant.setRespSubCode(mapParseObject.get("respSubCode"));
		grant.setRespMsg(mapParseObject.get("respMsg"));
		// 授权列表.
		String grantListStr = mapParseObject.get("grantList");
		if (grantListStr != null) {
			grant.setGrantList(grantListStr);
			String newGrantList = "";
			String grantList = grant.getGrantList();
			String[] splitGrantList = grantList.split(",");
			for (int i = 0; i < splitGrantList.length; i++) {
				if (i >= 1) {
					newGrantList = newGrantList.concat(",");
				}
				if ("SHARE_PAYMENT".equals(splitGrantList[i])) {
					newGrantList = newGrantList.concat("免密缴费");
				}
				if ("REPAY".equals(splitGrantList[i])) {
					newGrantList = newGrantList.concat("免密还款");
				}
			}
			grant.setGrantList(newGrantList);
		} else {
			grant.setGrantList("未开通免密相关授权");
		}

		// 授权金额列表.
		String grantAmountListStr = mapParseObject.get("grantAmountList");
		if (grantAmountListStr != null) {
			grant.setGrantAmountList(grantAmountListStr);
			String newGrantAmountList = "";
			String grantAmountList = grant.getGrantAmountList();
			String[] splitGrantAmountList = grantAmountList.split(",");
			for (int i = 0; i < splitGrantAmountList.length; i++) {
				if (i >= 1) {
					newGrantAmountList = newGrantAmountList.concat(",");
				}
				if (i == 0) {
					newGrantAmountList = newGrantAmountList.concat(splitGrantAmountList[i]).concat("万元/笔");
				} else {
					newGrantAmountList = newGrantAmountList.concat(splitGrantAmountList[i]).concat("万元/笔");
				}
			}
			grant.setGrantAmountList(newGrantAmountList);
		} else {
			grant.setGrantAmountList("未开通免密金额授权");
		}
		// 授权期限.
		String grantTimeListStr = mapParseObject.get("grantTimeList");
		if (grantTimeListStr != null) {
			grant.setGrantTimeList(grantTimeListStr);
		} else {
			grant.setGrantTimeList("未开通免密期限授权");
		}
		map.put("grant", grant);
		return map;
	}

}
