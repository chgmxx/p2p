package com.power.platform.lanmao.trade.service;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.type.CheckTypeEnum;
import com.power.platform.lanmao.type.ServiceNameEnum;

/**
 * 
 * class: ActivateStockedUserService <br>
 * description: 会员激活 <br>
 * author: Roy <br>
 * date: 2019年10月23日 上午11:04:20
 */
@Service("activateStockedUserService")
public class ActivateStockedUserService {

	private final static Logger logger = LoggerFactory.getLogger(ActivateStockedUserService.class);

	/**
	 * 
	 * methods: activateStockedUser <br>
	 * description: 会员激活 <br>
	 * author: Roy <br>
	 * date: 2019年10月23日 上午11:11:01
	 * 
	 * @param platformUserNo
	 *            平台用户编号
	 * @param authList
	 *            见【用户授权列表】；此处可传多个值，传多个值时用“,”英文半角逗号分隔
	 * @param redirectUrl
	 *            页面回跳URL
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Map<String, String> activateStockedUser(String platformUserNo, String authList, String redirectUrl) {

		Map<String, String> result = new LinkedHashMap<String, String>();
		try {
			// 定义reqData参数集合
			Map<String, Object> reqData = new HashMap<String, Object>();
			String requestNo = IdGen.uuid();
			reqData.put("requestNo", requestNo);
			reqData.put("redirectUrl", redirectUrl);
			reqData.put("platformUserNo", platformUserNo);
			reqData.put("authList", authList);
			reqData.put("amount", "1000000.00"); // 授权还款金额1000000.00万每笔
			reqData.put("failTime", DateUtils.formatDate(DateUtils.yearAddNum(new Date(), 3), "yyyyMMdd")); // 授权截至时间，默认三年
			reqData.put("checkType", CheckTypeEnum.LIMIT.getValue());
			// 所有的reqData的JSON内都须包含time stamp时间参数，并传入当前时间如果此时间和存管系统时间相差过大（10 分钟），则拒绝执行操作。
			reqData.put("timestamp", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
			result = AppUtil.lmGeneratePostParam(ServiceNameEnum.ACTIVATE_STOCKED_USER.getValue(), reqData);
			logger.debug("request:{}", JSON.toJSONString(result));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
