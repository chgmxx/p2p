package com.power.platform.weixin.entity;

import com.power.platform.common.persistence.DataEntity;

/**
 * 文本消息
 * 
 *
 */
public class MsgText extends DataEntity<MsgText>{

	private String content;//消息内容
	private MsgBase msgBase;
	
	public MsgBase getMsgBase() {
		return msgBase;
	}
	public void setMsgBase(MsgBase msgBase) {
		this.msgBase = msgBase;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}