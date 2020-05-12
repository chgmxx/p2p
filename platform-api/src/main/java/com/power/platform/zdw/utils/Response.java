package com.power.platform.zdw.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wangyepeng
 * @Description: 各种返回码
 * @time:2016年9月1日 下午4:11:51
 */
public class Response {

	/**
	 * 聚合数据，验证码识别失败.
	 */
	public static final Integer JUHE_CODE_DISCERN_FAILURE = -1;
	/**
	 * 请求成功
	 */
	public static final Integer ERROR_CODE_SUCCESS = 0;
	// 再次输入短信验证码
	public static final Integer MOBILE_MSG_CODE = 10001;
	// 输入短信验证码
	public static final Integer MOBILE_NEED_MSG_CODE = 10002;
	// 密码错误
	public static final Integer MOBILE_PASSWORD_ERR_CODE = 10003;
	// 短信验证码错误
	public static final Integer MOBILE_MSG_CODE_ERR_CODE = 10004;
	// 短信验证码失效系统已自动重新下发
	public static final Integer MOBILE_MISS_MSG_CODE_ERR_CODE = 10006;
	// 简单密码或初始密码无法登录
	public static final Integer MOBILE_EASY_PASSWORD_ERR_CODE = 10007;
	// 登陸成功
	public static final Integer STATUS_CODE_LOGIN_OK = 10008;
	// 请用本机发送CXXD至10001获取查询详单的验证码
	// 短信码失效请用本机发送CXXD至10001获取查询详单的验证码
	// 输入查询密码
	public static final Integer STATUS_NEED_QUERYPWD = 10022;
	// 查询密码错误
	public static final Integer STATUS_QUERYPWD_ERROR = 10023;
	// 需要2次短信
	public static final Integer MOBILE_NEED_MSG_TWO_CODE = 10024;
	/**
	 * 图片验证码超出10次异常
	 */
	public static final Integer MOBILE_NO_SUPPORT = 20014;
	/**
	 * 聚信立返回的登陆失败
	 */
	public static final Integer MOBILE_JXL_LOGIN_NG = 30000;
	/**
	 * 提交参数问题
	 */
	public static final Integer ERROR_CODE_PARAMETER_ERROR = 90001;
	/**
	 * 聚信立调用异常
	 */
	public static final Integer ERROR_CODE_JXL_UNKNOWN = 90000;

	/**
	 * 调用异常
	 */
	public static final Integer ERROR_CODE_UNKNOWN = 90000;

	/**
	 * 请求成功
	 */
	public static final Response SUCCESS = new Response(true, ERROR_CODE_SUCCESS);
	public static final Response FAILURE = new Response(false, JUHE_CODE_DISCERN_FAILURE);

	/**
	 * 抓取成功
	 */
	public static final Integer STATUS_CODE_CRAWLER_OK = 80001;
	/**
	 * 抓取流程中的失败
	 */
	public static final Integer STATUS_CODE_CRAWLER_PROGRESS_NG = 80003;
	/**
	 * 抓取失败
	 */
	public static final Integer STATUS_CODE_CRAWLER_NG = 80002;

	/**
	 * 请求失败 系统错误
	 */
	public static final Response MOBILE_STATUS_CODE_LOGIN_OK = new Response(true, STATUS_CODE_LOGIN_OK, "登陆成功"), MOBILE_STATUS_CODE_CRAWLER_OK = new Response(true, STATUS_CODE_CRAWLER_OK, "抓取成功"), MOBILE_STATUS_CODE_CRAWLER_NG = new Response(true, STATUS_CODE_CRAWLER_NG, "抓取失败"), MOBILE_STATUS_CODE_NEED_MSG_CODE = new Response(true, MOBILE_NEED_MSG_CODE, "需要短信密码"), MOBILE_STATUS_CODE_NEED_MSG_TWO_CODE = new Response(true, MOBILE_MSG_CODE, "需要2次短信密码"), MOBILE_STATUS_MSG_CODE_ERR_CODE = new Response(true, MOBILE_MSG_CODE_ERR_CODE, " 短信验证码错误"), MOBILE_STATUS_CODE_NEED_CUSTOMER_CODE = new Response(true, STATUS_NEED_QUERYPWD, "需要查询密码/客服密码"), MOBILE_STATUS_CODE_ERROR_PWD_CODE = new Response(true, MOBILE_PASSWORD_ERR_CODE, "查询密码/客服密码错误"), MOBILE_STATUS_PARAMETER_ERROR = new Response(true, ERROR_CODE_PARAMETER_ERROR, "提交参数非空或者格式异常"), MOBILE_STATUS_RESUBMIT_ERROR = new Response(true,
			ERROR_CODE_PARAMETER_ERROR, "重复提交请求了,上一个请求还没结束"), SYSTEM_WRONG_JXL_UNKNOWN = new Response(true, ERROR_CODE_JXL_UNKNOWN, "中登系统异常"), SYSTEM_WRONG_UNKNOWN = new Response(true, ERROR_CODE_UNKNOWN, "系统异常"), SYSTEM_WRONG_TIME_CODE = new Response(true, MOBILE_NO_SUPPORT, "图片验证码连续访问超出10次异常");// 系统异常
	private static final SerializerFeature sfs[] = new SerializerFeature[] { SerializerFeature.WriteMapNullValue };
	/**
	 * 响应状态
	 *
	 * @author wangyepeng
	 */
	private boolean success = false;

	/**
	 * 下一步需要的参数
	 */
	private JSONObject data;

	public Response(String message) {

		this(true, ERROR_CODE_SUCCESS, message);
	}

	public Response(boolean success, Integer code) {

		this(success, code, null);
	}

	public Response(boolean success, int code, String message) {

		this.success = success;
		JSONObject job = new JSONObject();
		job.put("code", code);
		job.put("message", message);
		this.data = job;
	}

	public Response(boolean success, int code, String message, String token) {

		this.success = success;
		JSONObject job = new JSONObject();
		job.put("code", code);
		job.put("message", message);
		job.put("token", token);
		this.data = job;
	}

	public Response(boolean success, int code, String message, String token, String imgBase64) {

		this.success = success;
		JSONObject job = new JSONObject();
		job.put("code", code);
		job.put("message", message);
		job.put("token", token);
		job.put("imgBase64", imgBase64);
		this.data = job;
	}

	public static Response getErrorResponse(Integer code) {

		return new Response(true, code);
	}

	public boolean isSuccess() {

		return success;
	}

	public Response setToken(String token) {

		if (this.data != null) {
			this.data.put("token", token);
		}
		return this;
	}

	public Response setMsg(String msg) {

		if (this.data != null) {
			this.data.put("msg", msg);
		}
		return this;
	}

	public void setSuccess(boolean success) {

		this.success = success;
	}

	public JSONObject getData() {

		return data;
	}

	@Override
	public String toString() {

		return JSON.toJSONString(this, sfs);
	}

}
