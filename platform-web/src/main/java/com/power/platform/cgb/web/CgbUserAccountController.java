/**
 * 银行托管-账户-Controller.
 */
package com.power.platform.cgb.web;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.dao.CgbUserAccountDao;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.common.config.Global;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.StringUtils;
import com.power.platform.common.utils.excel.ExportExcel;
import com.power.platform.common.web.BaseController;
import com.power.platform.sys.utils.HttpUtil;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 银行托管-账户-Controller.
 * 
 * @author lance
 * @version 2017-10-26
 */
@Controller
@RequestMapping(value = "${adminPath}/cgb/cgbUserAccount")
public class CgbUserAccountController extends BaseController {

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
	private CgbUserAccountService cgbUserAccountService;
	@Autowired
	private CgbUserAccountDao cgbUserAccountDao;

	@ModelAttribute
	public CgbUserAccount get(@RequestParam(required = false) String id) {

		CgbUserAccount entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = cgbUserAccountService.get(id);
		}
		if (entity == null) {
			entity = new CgbUserAccount();
		}
		return entity;
	}

	@RequiresPermissions("cgb:cgbUserAccount:view")
	@RequestMapping(value = { "list", "" })
	public String list(CgbUserAccount cgbUserAccount, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<CgbUserAccount> page = new Page<CgbUserAccount>();
		if (cgbUserAccount.getCanUseAmount() != null && !cgbUserAccount.getCanUseAmount().equals("")) {
			if (cgbUserAccount.getCanUseAmount().equals("0")) {// 可用余额为0
				page = cgbUserAccountService.findPage0(new Page<CgbUserAccount>(request, response), cgbUserAccount);
			} else if (cgbUserAccount.getCanUseAmount().equals("1")) {// 可用余额不为0
				page = cgbUserAccountService.findPage1(new Page<CgbUserAccount>(request, response), cgbUserAccount);
			}
		} else {
			page = cgbUserAccountService.findPage(new Page<CgbUserAccount>(request, response), cgbUserAccount);
		}
		List<CgbUserAccount> list = page.getList();
		for (CgbUserAccount entity : list) { // 遍历一些注销的帐号，但依然保留着账户(对此类数据进行处理).
			UserInfo userInfo = entity.getUserInfo();
			if (null == userInfo) {
				UserInfo u = new UserInfo();
				u.setName("<b style='color: red;'>注销的帐号</b>");
				u.setRealName("<b style='color: red;'>查无此数据</b>");
				entity.setUserInfo(u);
			} else {
				if (null == userInfo.getRealName() || "".equals(userInfo.getRealName())) {
					userInfo.setRealName("<b style='color: #605757;'>未开户</b>");
				} else {
//					userInfo.setRealName(CommonStringUtils.replaceNameX(userInfo.getRealName()));
				}
//				userInfo.setName(CommonStringUtils.mobileEncrypt(userInfo.getName()));
			}
			// 账户总额.
			entity.setTotalAmountStr(new DecimalFormat("0.00").format(entity.getTotalAmount()));
			// 可用余额.
			entity.setAvailableAmountStr(new DecimalFormat("0.00").format(entity.getAvailableAmount()));
			// 提现金额.
			entity.setCashAmountStr(new DecimalFormat("0.00").format(entity.getCashAmount()));
			// 充值金额.
			entity.setRechargeAmountStr(new DecimalFormat("0.00").format(entity.getRechargeAmount()));
			// 冻结金额.
			entity.setFreezeAmountStr(new DecimalFormat("0.00").format(entity.getFreezeAmount()));
			// 总收益.
			entity.setTotalInterestStr(new DecimalFormat("0.00").format(entity.getTotalInterest()));
			// 定期投资总额.
			entity.setRegularTotalAmountStr(new DecimalFormat("0.00").format(entity.getRegularTotalAmount()));
			// 定期代收本金.
			entity.setRegularDuePrincipalStr(new DecimalFormat("0.00").format(entity.getRegularDuePrincipal()));
			// 定期代收收益.
			entity.setRegularDueInterestStr(new DecimalFormat("0.00").format(entity.getRegularDueInterest()));
			// 定期累计收益.
			entity.setRegularTotalInterestStr(new DecimalFormat("0.00").format(entity.getRegularTotalInterest()));
			// 定期昨日收益.
			entity.setReguarYesterdayInterestStr(new DecimalFormat("0.00").format(entity.getReguarYesterdayInterest()));
			// 佣金.
			if (null == entity.getCommission()) {
				entity.setCommissionStr(new DecimalFormat("0.00").format(0D));
			} else {
				entity.setCommissionStr(new DecimalFormat("0.00").format(entity.getCommission()));
			}
		}
		model.addAttribute("page", page);
		return "modules/cgb/userAccount/cgbUserAccountList";
	}

	@RequiresPermissions("cgb:cgbUserAccount:view")
	@RequestMapping(value = "form")
	public String form(CgbUserAccount cgbUserAccount, Model model) {

		model.addAttribute("cgbUserAccount", cgbUserAccount);
		return "modules/cgb/userAccount/cgbUserAccountForm";
	}

	@RequiresPermissions("cgb:cgbUserAccount:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(CgbUserAccount cgbUserAccount, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {

		try {
			String fileName = "出借人账户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			List<CgbUserAccount> findList = new ArrayList<CgbUserAccount>();
			if (cgbUserAccount.getCanUseAmount() != null && !cgbUserAccount.getCanUseAmount().equals("")) {
				if (cgbUserAccount.getCanUseAmount().equals("0")) {// 可用余额为0
					findList = cgbUserAccountDao.findAmountList0(cgbUserAccount);
				} else if (cgbUserAccount.getCanUseAmount().equals("1")) {// 可用余额不为0
					findList = cgbUserAccountDao.findAmountList1(cgbUserAccount);
				}
			} else {
				findList = cgbUserAccountDao.findList(cgbUserAccount);
			}
			new ExportExcel("出借人账户数据", CgbUserAccount.class).setDataList(findList).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出出借人账户数据数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserAccount/?repage";
	}

	/**
	 * 存管宝余额查询
	 * 
	 * @param cgbUserAccount
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("cgb:cgbUserAccount:edit")
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

		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserAccount/?repage";
	}

	@RequiresPermissions("cgb:cgbUserAccount:edit")
	@RequestMapping(value = "save")
	public String save(CgbUserAccount cgbUserAccount, Model model, RedirectAttributes redirectAttributes) {

		if (!beanValidator(model, cgbUserAccount)) {
			return form(cgbUserAccount, model);
		}
		cgbUserAccountService.save(cgbUserAccount);
		addMessage(redirectAttributes, "保存银行托管-账户成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserAccount/?repage";
	}

	@RequiresPermissions("cgb:cgbUserAccount:edit")
	@RequestMapping(value = "delete")
	public String delete(CgbUserAccount cgbUserAccount, RedirectAttributes redirectAttributes) {

		cgbUserAccountService.delete(cgbUserAccount);
		addMessage(redirectAttributes, "删除银行托管-账户成功");
		return "redirect:" + Global.getAdminPath() + "/cgb/cgbUserAccount/?repage";
	}

}