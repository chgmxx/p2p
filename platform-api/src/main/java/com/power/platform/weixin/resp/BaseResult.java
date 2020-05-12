package com.power.platform.weixin.resp;

/**
 * 微信请求状态数据（返回错误代码跟错误信息）
 * @author liuxiaolei
 * 下午2:16:23
 * v1.0
 */
public class BaseResult {

	private String errcode;
	private String errmsg;

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

}
