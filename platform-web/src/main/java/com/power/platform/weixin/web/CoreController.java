package com.power.platform.weixin.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.power.platform.weixin.service.CoreService;
import com.power.platform.weixin.utils.SignUtil;
import com.power.platform.weixin.utils.WeixinUtil;

/**
 *  微信主响应类
 * @author liuxiaolei
 * 下午1:57:39
 * v1.0
 */

@Controller
@RequestMapping(value = "corewinxin")
public class CoreController{
    @Autowired
    private CoreService coreService;

    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * 校验信息请求get
     *
     * @return
     */

    @RequestMapping(method = { RequestMethod.GET })
    public void getService(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "signature") String signature, @RequestParam(value = "timestamp") String timestamp,
            @RequestParam(value = "nonce") String nonce, @RequestParam(value = "echostr") String echostr) {
    	logger.info("【signature】:" + signature);
    	logger.info("【timestamp】:" + timestamp);
    	logger.info("【nonce】:" + nonce);
    	logger.info("【echostr】:" + echostr);
        if (SignUtil.checkSignature(WeixinUtil.WEIXIN_ACCOUNT_TOKEN, signature, timestamp, nonce)) {
            try {
                response.getWriter().print(echostr);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 主响应方法
     *
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST })
    public void postService(HttpServletResponse response, HttpServletRequest request) {

        System.out.println("【微信请求事件开始进入post请求方法】:");
    	   try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
          response.setCharacterEncoding("UTF-8");  
          logger.info("【微信请求事件开始进入service方法】:");
          System.out.println("【微信请求事件开始进入service方法】:");
        String respMessage = coreService.coreService(request);

        logger.info("【service方法请求结束回馈字符串==】:"+respMessage);
        System.out.println("【service方法请求结束回馈字符串==】:"+respMessage);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            //out.print(respMessage);
            out.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
