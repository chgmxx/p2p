package com.power.platform.lanmao.asyncmsg.rechain.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cunguanbao.api.utils.APIUtils;
import com.power.platform.cgb.entity.CgbUserAccount;
import com.power.platform.cgb.entity.CgbUserBankCard;
import com.power.platform.cgb.entity.CgbUserTransDetail;
import com.power.platform.cgb.service.CgbUserAccountService;
import com.power.platform.cgb.service.CgbUserBankCardService;
import com.power.platform.cgb.service.callback.CallbackRechargeService;
import com.power.platform.common.utils.IdGen;
import com.power.platform.common.utils.JedisUtils;
import com.power.platform.credit.entity.userinfo.CreditUserAccount;
import com.power.platform.lanmao.rw.pojo.NotifyException;
import com.power.platform.lanmao.rw.pojo.NotifyVo;
import com.power.platform.lanmao.asyncmsg.rechain.*;
import com.power.platform.lanmao.rw.service.LMRechargeNotifyService;
import com.power.platform.lanmao.type.ServiceNameEnum;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.recharge.service.UserRechargeService;
import com.power.platform.trandetail.entity.UserTransDetail;

/**  
 * 充值 
 * 
 */   
public class Recharge implements IResponsibility{
	private static final Logger LOG = LoggerFactory.getLogger(Recharge.class);
	private LMRechargeNotifyService lMRechargeNotifyService;
	public Recharge(LMRechargeNotifyService lMRechargeNotifyService) {
		this.lMRechargeNotifyService = lMRechargeNotifyService;
	} 
	@Override
	public void doSomething(NotifyVo input, IResponsibility responsibility) {
		if (input != null && ServiceNameEnum.RECHARGE.getValue().equals(input.getServiceName().toUpperCase())) {
			try{
				// TODO do something
				System.out.println("充值， todo ..... ,   adsfasdfasdf");
				System.out.println(">>>>>lMRechargeNotifyService " + lMRechargeNotifyService);
				String result = lMRechargeNotifyService.pressLMRechargeNotify(input);
				System.out.println("充值， todo .....complete vvvv");
				return ;
			}catch(Exception e){
				throw new NotifyException("处理充值通知异常");
			}
		}
		//当前没法处理，回调回去，让下一个去处理
		responsibility.doSomething(input, responsibility);
	}
} 
