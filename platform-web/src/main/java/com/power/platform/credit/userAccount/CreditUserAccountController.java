package com.power.platform.credit.userAccount;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.CommonStringUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.web.BaseController;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.service.userinfo.CreditUserAccountService;
import com.power.platform.sys.utils.HttpUtil;

/**
 * 
 * 类: CreditUserAccountController <br>
 * 描述: 借款人账户. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2018年2月7日 下午5:22:02
 */
@Controller
@RequestMapping(value = "${adminPath}/credit/userAccount")
public class CreditUserAccountController extends BaseController {

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
	private CreditUserAccountService creditUserAccountService;
	@Resource
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;

	@ModelAttribute
	public CreditUserAccount get(@RequestParam(required = false) String id) {

		CreditUserAccount entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = creditUserAccountService.get(id);
		}
		if (entity == null) {
			entity = new CreditUserAccount();
		}
		return entity;
	}

	@RequiresPermissions("credit:userAccount:view")
	@RequestMapping(value = { "list", "" })
	public String list(CreditUserAccount creditUserAccount, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CreditUserAccount> page = creditUserAccountService.findPage(new Page<CreditUserAccount>(request, response), creditUserAccount);

		List<CreditUserAccount> list = page.getList();
		for (CreditUserAccount entity : list) { // 遍历一些注销的帐号，但依然保留着账户(对此类数据进行处理).
			CreditUserInfo creditUserInfo = entity.getCreditUserInfo();
			if (null == creditUserInfo) {
				CreditUserInfo u = new CreditUserInfo();
				u.setPhone("<b style='color: red;'>注销的帐号</b>");
				u.setName("<b style='color: red;'>查无此数据</b>");
				entity.setCreditUserInfo(u);
			} else {
				if (null == creditUserInfo.getName() || "".equals(creditUserInfo.getName())) {
					creditUserInfo.setName("<b style='color: #605757;'>未开户</b>");
				}
				creditUserInfo.setPhone(CommonStringUtils.mobileEncrypt(creditUserInfo.getPhone()));
				if (null == creditUserInfo.getCreditUserType() || "".equals(creditUserInfo.getCreditUserType())) {
					creditUserInfo.setCreditUserType("<b style='color: #605757;'>未开户</b>");
				} else {
					if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_01)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>投资户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_02)) {
						/**
						 * 判断是供应商/抵押业务.
						 */
						// 封装借款户查询.
						CreditSupplierToMiddlemen supplier = new CreditSupplierToMiddlemen();
						supplier.setSupplierId(creditUserInfo.getId());
						List<CreditSupplierToMiddlemen> suppliers = creditSupplierToMiddlemenDao.findCreditSupplierToMiddlemens(supplier);
						for (CreditSupplierToMiddlemen creditSupplierToMiddlemen : suppliers) {
							if (creditSupplierToMiddlemen.getSupplierId().equals(creditSupplierToMiddlemen.getMiddlemenId())) {
								creditUserInfo.setCreditUserType("<b style='color: #605757;'>借款户（抵押业务）</b>");
							} else {
								creditUserInfo.setCreditUserType("<b style='color: #605757;'>借款户（供应商）</b>");
							}
						}
						if (suppliers.size() == 0) {
							creditUserInfo.setCreditUserType("<b style='color: #605757;'>借款户</b>");
						}
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_03)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>担保户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_04)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>咨询户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_05)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>P2P平台户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_08)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>营销户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_10)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>收费户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_11)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>核心企业</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_12)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>第三方营销账户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserAccountService.BIZ_TYPE_13)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>垫资账户</b>");
					} else if (creditUserInfo.getCreditUserType().equals(CreditUserInfo.CREDIT_USER_TYPE_15)) {
						creditUserInfo.setCreditUserType("<b style='color: #605757;'>借款户（抵押业务）</b>");
					}
				}
				if (null == creditUserInfo.getEnterpriseFullName() || "".equals(creditUserInfo.getEnterpriseFullName())) {
					creditUserInfo.setEnterpriseFullName("<b style='color: #605757;'>未开户</b>");
				}
			}
			// 账户总额.
			entity.setTotalAmountStr(new DecimalFormat("0.00").format(entity.getTotalAmount()));
			// 可用余额.
			entity.setAvailableAmountStr(new DecimalFormat("0.00").format(entity.getAvailableAmount()));
			// 冻结金额.
			entity.setFreezeAmountStr(new DecimalFormat("0.00").format(entity.getFreezeAmount()));
			// 充值金额.
			entity.setRechargeAmountStr(new DecimalFormat("0.00").format(entity.getRechargeAmount()));
			// 提现金额.
			entity.setWithdrawAmountStr(new DecimalFormat("0.00").format(entity.getWithdrawAmount()));
			// 待还金额.
			entity.setSurplusAmountStr(new DecimalFormat("0.00").format(entity.getSurplusAmount()));
			// 已还金额.
			entity.setRepayAmountStr(new DecimalFormat("0.00").format(entity.getRepayAmount()));
		}
		model.addAttribute("page", page);
		return "modules/credit/userAccount/userAccountList";
	}

	@RequiresPermissions("credit:userAccount:edit")
	@RequestMapping(value = "checkAmount")
	public String checkAmount(CgbUserAccount cgbUserAccount, Model model, RedirectAttributes redirectAttributes) throws Exception {

		// 根据用户ID查询 用户余额
		/*
		 * 构造请求参数
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", cgbUserAccount.getUserId());
		params.put("service", "p2p.trade.balance.search");
		params.put("method", "RSA");
		params.put("merchantId", MERCHANT_ID);
		params.put("source", "1");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String requestTime = sdf.format(new Date());
		params.put("requestTime", requestTime);
		params.put("version", "1.0.0");
		params.put("reqSn", UUID.randomUUID().toString().replace("-", ""));
		// 生成签名
		String sign = APIUtils.createSign(MERCHANT_RSA_PRIVATE_KEY, params, "RSA");
		params.put("signature", sign);
		String jsonString = JSON.toJSONString(params);
		System.out.println("用户余额[请求参数]" + jsonString);
		// 加密
		Map<String, String> encryptRet = APIUtils.encryptDataBySSL(jsonString, MERCHANT_RSA_PUBLIC_KEY);
		encryptRet.put("merchantId", MERCHANT_ID);
		// 返回订单信息
		System.out.println(encryptRet);

		// 发送请求

		String respo = HttpUtil.sendPost(HOST, encryptRet);
		System.out.println("返回结果报文" + respo);
		JSONObject jsonObject = JSONObject.parseObject(respo);
		String respTm = (String) jsonObject.get("tm");
		String respData = (String) jsonObject.getString("data");
		String jsonRet = APIUtils.decryptDataBySSL(respTm, respData, MERCHANT_RSA_PRIVATE_KEY);
		Map<String, String> maps = JSON.parseObject(jsonRet, new TypeReference<Map<String, String>>() {
		});

		System.out.println("解密结果:" + maps);

		if (maps.get("respSubCode").equals("000000")) {
			addMessage(redirectAttributes, "用户可用余额为" + Double.valueOf(maps.get("availableBalance")) / 100 + "元,冻结金额为" + Double.valueOf(maps.get("freezeBalance")) / 100 + "元");
		} else {
			addMessage(redirectAttributes, "用户尚未开户");
		}

		return "redirect:" + Global.getAdminPath() + "/credit/userAccount/?repage";
	}

}