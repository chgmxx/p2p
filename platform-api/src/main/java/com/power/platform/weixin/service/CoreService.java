package com.power.platform.weixin.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.power.platform.common.utils.IdGen;
import com.power.platform.weixin.dao.ZtmgWechatRelationDao;
import com.power.platform.weixin.entity.ZtmgWechatRelation;
import com.power.platform.weixin.resp.BaseTemplete;
import com.power.platform.weixin.resp.TempleteMsg;
import com.power.platform.weixin.resp.TextMessage;
import com.power.platform.weixin.utils.MessageUtil;
import com.power.platform.weixin.utils.WeixinUtil;
/**
 * 回复用户点击/请求的service
 * @author liuxiaolei
 * 下午2:21:29
 * v1.0
 */
@Service("CoreService")
public class CoreService {
	
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Resource
	private ZtmgWechatRelationDao ztmgWechatRelationDao;

    public String coreService(HttpServletRequest request) {
        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";
            Map<String, String> requestMap = MessageUtil.parseXml(request);

            // 日志
            String fromUserName = requestMap.get("FromUserName"); // 发送方帐号（open_id）
            String toUserName = requestMap.get("ToUserName"); // 公众帐号
            String msgType = requestMap.get("MsgType"); // 消息类型
            String content = requestMap.get("Content"); // 消息内容
            logger.info("----客户端发送---- fromUserName:" + fromUserName + "  |  msgType:" + msgType + "  |  content:" + content);
            System.out.println("----客户端发送---- fromUserName:" + fromUserName + "  |  msgType:" + msgType + "  |  content:" + content);
            // 默认回复此文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.MORE_KEFU);
           // textMessage.setContent("感谢您对赢多多的关注,我们会努力给大家提供更好的服务。");
            // 将文本消息对象转换成xml字符串
            respMessage = MessageUtil.textMessageToXml(textMessage);

            // 【微信触发类型】文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
               // respMessage = doTextResponse(content, toUserName, textMessage, sys_accountId, respMessage, fromUserName, request, msgId, msgType);
            }
            // 【微信触发类型】事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                
                System.out.println(eventType+"___________________________________________________");
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    textMessage.setContent("您好！感谢您的关注！https://www.cicmorgan.com注册并开通第三方，将有2元现金红包获得，开通以后，请在此回复您的手机号码，2元红包马上到您的账号！");
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                    getUserInfo(fromUserName);
                    // sendTempMessageForToRecommendUser("oximouFYT74rqlxF0vVUC-vsAyug", 12345d);
                }
                // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    cancleUserInfo(fromUserName);
                }
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                	//textMessage.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_TEXT);
                    //respMessage = doMyMenuEvent(requestMap, textMessage, respMessage, toUserName, fromUserName, respContent, request);
                }
            }
            // 【微信触发类型】图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                logger.info(MessageUtil.REQ_MESSAGE_TYPE_IMAGE);
                respContent = "更多精彩的功能正在紧锣密鼓的开发中！";
            }
            // 【微信触发类型】地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                logger.info(MessageUtil.REQ_MESSAGE_TYPE_LOCATION);
                respContent = "更多精彩的功能正在紧锣密鼓的开发中！";
            }
            // 【微信触发类型】链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                logger.info(MessageUtil.REQ_MESSAGE_TYPE_LINK);
                respContent = "更多精彩的功能正在紧锣密鼓的开发中！";
            }
            // 【微信触发类型】音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                logger.info(MessageUtil.REQ_MESSAGE_TYPE_VOICE);
                respContent = "更多精彩的功能正在紧锣密鼓的开发中！";
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return respMessage;
    }

    
    
    /**
     * 用户关注进行的操作
     * @param fromUserName
     */
	private void getUserInfo(String fromUserName) {
		//拉取用户基本信息
		try {
			String url = WeixinUtil.user_info_url.replace("ACCESS_TOKEN", WeixinUtil.getAccessToken()).replace("OPENID", fromUserName);
			JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
			if (jsonObject != null) {
			    String openId = jsonObject.getString("openid");
			    String nickname = WeixinUtil.dofilter(jsonObject.getString("nickname"));
			    String headImgUrl = jsonObject.getString("headimgurl");
			    if(StringUtils.isNotBlank(openId)){
		    		ZtmgWechatRelation weixinUser = ztmgWechatRelationDao.findByOpenId(openId);
		    		if ( weixinUser != null ) {	
		    			weixinUser.setNickname(nickname);
		    			weixinUser.setHeadPortraitUrl(headImgUrl);
		    			weixinUser.setUpdateDate(new Date());
		    			weixinUser.setState(ZtmgWechatRelationService.FOCUS_STATE_1);
		    			ztmgWechatRelationDao.update(weixinUser);
			        } else {
			        	weixinUser = new ZtmgWechatRelation();
			        	weixinUser.setId(IdGen.uuid());
			        	weixinUser.setOpenId(openId);
			        	weixinUser.setNickname(nickname);
		    			weixinUser.setHeadPortraitUrl(headImgUrl);
		    			weixinUser.setUpdateDate(new Date());
		    			weixinUser.setState(ZtmgWechatRelationService.FOCUS_STATE_1);
		    			ztmgWechatRelationDao.insert(weixinUser);
			        }
			    }
			}
		} catch (Exception e) {
			logger.info("【同步微信用户到数据库出现异常】");
			e.printStackTrace();
		}
	}

   
	/**
     * 用户取消关注进行的操作
     * @param fromUserName
     */
    private void cancleUserInfo(String fromUserName){
    	System.out.println( fromUserName + "用户取消关注_____________________");
    	ZtmgWechatRelation weixinUser = ztmgWechatRelationDao.findByOpenId(fromUserName);
    	weixinUser.setState(ZtmgWechatRelationService.FOCUS_STATE_2);
    	weixinUser.setUpdateDate(new Date());
    	ztmgWechatRelationDao.update(weixinUser);
    	System.out.println("openid为"+fromUserName+"的用户已取消关注，账号已为锁定状态");
    }
    
    
    /**
     * 针对文本消息
     * 
     */
    String doTextResponse(String content, String toUserName, TextMessage textMessage, String sys_accountId, String respMessage, String fromUserName, HttpServletRequest request, String msgId,
            String msgType) throws Exception {
        // TODO 未来可以集成用户信息和查询余额
        return respMessage;
    }
    
    
}
