package com.power.platform.lanmao.type;

/**
 * 
 * class: FailedCodeEnum <br>
 * description: 调用失败错误码 <br>
 * author: Roy <br>
 * date: 2019年9月19日 下午8:34:19
 */
public enum FailedCodeEnum {

	/**
	 * 100001:系统错误
	 */
	FAILED_CODE_100001("100001", "系统错误"),
	/**
	 * 100002:json 参数格式错误
	 */
	FAILED_CODE_100002("100002", "json 参数格式错误"),
	/**
	 * 100003:签名验证失败
	 */
	FAILED_CODE_100003("100003", "签名验证失败"),
	/**
	 * 100004:平台编号不存在
	 */
	FAILED_CODE_100004("100004", "平台编号不存在"),
	/**
	 * 100005:平台状态异常
	 */
	FAILED_CODE_100005("100005", "平台状态异常"),
	/**
	 * 100006:业务未开通
	 */
	FAILED_CODE_100006("100006", "业务未开通"),
	/**
	 * 100007:查询对象不存在
	 */
	FAILED_CODE_100007("100007", "查询对象不存在"),
	/**
	 * 100008:业务受理失败
	 */
	FAILED_CODE_100008("100008", "业务受理失败"),
	/**
	 * 100009:用户不存在
	 */
	FAILED_CODE_100009("100009", "用户不存在"),
	/**
	 * 1000010:用户账户不可用
	 */
	FAILED_CODE_1000010("1000010", "用户账户不可用"),
	/**
	 * 1000011:该用户无此操作权限
	 */
	FAILED_CODE_1000011("1000011", "该用户无此操作权限"),
	/**
	 * 1000012:非常抱歉，暂不支持此银行
	 */
	FAILED_CODE_1000012("1000012", "非常抱歉，暂不支持此银行"),
	/**
	 * 1000013:请求流水号重复
	 */
	FAILED_CODE_1000013("1000013", "请求流水号重复"),
	/**
	 * 1000014:余额不足
	 */
	FAILED_CODE_1000014("1000014", "余额不足"),
	/**
	 * 1000015:标的状态与业务不匹配
	 */
	FAILED_CODE_1000015("1000015", "标的状态与业务不匹配"),
	/**
	 * 1000016:平台垫资账户可用余额不足，请联系平台处理
	 */
	FAILED_CODE_1000016("1000016", "平台垫资账户可用余额不足，请联系平台处理"),
	/**
	 * 1000017:平台未启用 垫资账户，请联系平台处理
	 */
	FAILED_CODE_1000017("1000017", "平台未启用 垫资账户，请联系平台处理"),
	/**
	 * 1000018:抱歉，您的提现申请 方式 不在 受理 时间范围内
	 */
	FAILED_CODE_1000018("1000018", "抱歉，您的提现申请 方式 不在 受理 时间范围内"),
	/**
	 * 1000019:标的类型与业务不匹配
	 */
	FAILED_CODE_1000019("1000019", "标的类型与业务不匹配"),
	/**
	 * 1000020:交易处理中，请勿重试
	 */
	FAILED_CODE_1000020("1000020", "交易处理中，请勿重试"),
	/**
	 * 1000021:抱歉，节假日 单笔提现额度超限
	 */
	FAILED_CODE_1000021("1000021", "抱歉，节假日 单笔提现额度超限"),
	/**
	 * 1000022:D0提现不支持提现拦截操作
	 */
	FAILED_CODE_1000022("1000022", "D0提现不支持提现拦截操作");

	private String value;
	private String text;

	private FailedCodeEnum(String value, String text) {

		this.value = value;
		this.text = text;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getText() {

		return text;
	}

	public void setText(String text) {

		this.text = text;
	}
	public static String getTextByValue(String value) {

		  for (FailedCodeEnum v : FailedCodeEnum.values()) {
		   if (v.getValue().equals(value)) {
		    return v.getText();
		   }
		  }
		  return "未知结果";
		 }
}
