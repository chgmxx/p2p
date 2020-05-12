package com.power.platform.lanmao.rw.service;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.lanmao.common.AppUtil;
import com.power.platform.lanmao.rw.pojo.NotifyVo;

/**
 *   处理资金回退充值异步通知
 * @author chenhj ant-loiter.com
 *
 */
@Service("lMBackrollRechargeNotifyService")
public class LMBackrollRechargeNotifyService {
	
	/**
	 * requestNo 	Y	S		回退充值请求流水号
		rollbackAmount	Y	A		回退到账金额（提现实际入账金额）
		rollbackCommission	N	A		回退佣金（提现佣金）
		withdrawRequestNo	Y	S		提现请求流水号（提现失败对应的提现请求流水号）
		completedTime	Y	T		回退充值完成时间
		status	Y	E	50	回退充值状态 ：SUCCESS、ONLINE_SUCCESS
		rollbackType	Y	E		资金回充类型：INTERCEPT（表示提现拦截以后系统发起的回充）、REMITFAIL（表示提现出款失败后系统发起的回充）
		failReason 	N	S	200	提现失败的原因 
	 * @param input
	 * @return  此方法暂不使用
	 */
	public String pressLMRechargeNotify(NotifyVo input) {
		// 处理充值异常， 是需要将对应的金额扣除
		JSONObject respData = JSON.parseObject(input.getRespData());
		String platformNo = input.getPlatformNo();
		String requestNo = AppUtil.CheckStringByDefault(respData.getString("requestNo"), "");
		String rollbackAmount = AppUtil.CheckStringByDefault(respData.getString("rollbackAmount"), "0");
		String rollbackCommission = AppUtil.CheckStringByDefault(respData.getString("rollbackAmount"), "0");
		String withdrawRequestNo = AppUtil.CheckStringByDefault(respData.getString("withdrawRequestNo"), "");
		String status = AppUtil.CheckStringByDefault(respData.getString("status"), "INIT");
		String completedTime = respData.getString("completedTime");
		
		// 先根据请求流水号查询出
		
		return null;
	}
}
