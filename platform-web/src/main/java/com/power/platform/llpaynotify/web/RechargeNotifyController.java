package com.power.platform.llpaynotify.web;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.power.platform.common.web.BaseController;
import com.power.platform.pay.config.PartnerConfig;
import com.power.platform.pay.recharge.dao.UserRechargeDao;
import com.power.platform.pay.recharge.entity.UserRecharge;
import com.power.platform.pay.service.LLPayService;
import com.power.platform.pay.utils.LLPayUtil;
import com.power.platform.pay.vo.PayDataBean;
import com.power.platform.pay.vo.RetBean;


/**
 * 充值回调通知Controller
 * @author 曹智
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "/llpay/rechargenotify")
public class RechargeNotifyController extends BaseController {

	@Autowired
	private LLPayService llPayService;
	@Resource
	private UserRechargeDao userRechargeDao;
	
	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public synchronized void notify(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.info("进入绑卡异步通知数据接收处理");
        RetBean retBean = new RetBean();
        String reqStr = LLPayUtil.readReqStr(request);
		if (LLPayUtil.isnull(reqStr)){
            retBean.setRet_code("9999");
            retBean.setRet_msg("交易失败");
            response.getWriter().write(JSON.toJSONString(retBean));
            response.getWriter().flush();
            return;
        }
        logger.info("接收绑卡异步通知数据：【" + reqStr + "】");
        try{
            if (!LLPayUtil.checkSign(reqStr, PartnerConfig.YT_PUB_KEY,
                    PartnerConfig.MD5_KEY)){
                retBean.setRet_code("9999");
                retBean.setRet_msg("交易失败");
                response.getWriter().write(JSON.toJSONString(retBean));
                response.getWriter().flush();
                logger.error("绑卡异步通知验签失败");
                return;
            }
        } catch (Exception e){
        	logger.error("异步通知报文解析异常：" + e);
            retBean.setRet_code("9999");
            retBean.setRet_msg("交易失败");
            response.getWriter().write(JSON.toJSONString(retBean));
            response.getWriter().flush();
            return;
        }
        retBean.setRet_code("0000");
        retBean.setRet_msg("交易成功");
        response.getWriter().write(JSON.toJSONString(retBean));
        response.getWriter().flush();
        logger.info("绑卡异步通知数据接收处理成功     "+reqStr);
        PayDataBean payDataBean = JSON.parseObject(reqStr, PayDataBean.class);
        // TODO:更新订单，发货等后续处理
        String sn = payDataBean.getNo_order();
		UserRecharge userRecharge = new UserRecharge();
		userRecharge.setSn(sn);
		List<UserRecharge> userRecharges = userRechargeDao.findList(userRecharge);
		if (userRecharges !=null && userRecharges.size() > 0) {
			userRecharge = userRecharges.get(0);
		}
		if (userRecharge.getState() == UserRecharge.RECHARGE_SUCCESS) {
			return;
		}
        llPayService.completeRecharge(payDataBean);
	}
	
	
	@RequestMapping(value = "/cashBgRetCallback.html", method = RequestMethod.POST)
	public void netSaveBgRetCallback(HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("提现放款异步回调");
		try {
				String ret_code =request.getParameter("ret_code");
				String ret_msg =request.getParameter("ret_msg");
				 logger.info("提现放款异步回调结果编码"+ret_code+","+ret_msg);
		}  catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

}
