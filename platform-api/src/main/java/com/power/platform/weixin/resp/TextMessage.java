package com.power.platform.weixin.resp;

/**
 * 文本消息
 * @author liuxiaolei
 * 下午2:17:58
 */
public class TextMessage extends BaseMessage {
	// 回复的消息内容
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}
}