package com.power.platform.lanmao.search.utils;

import java.util.List;

import com.timevale.tgtext.text.pdf.parser.v;

/**
 * 
 * @author pm
 *
 */ 
public class ResultVOUtil {
	
	public static OneTransactionResult success(Object object) {
		OneTransactionResult result = new OneTransactionResult();
        result.setT((List) object);
        result.setErrorCode("0");
        result.setCode(0);
        result.setStatus("INIT");
        result.setErrorMessage("成功");
        return result;
    }
	public static OneTransactionResult fail(Object object) {
		OneTransactionResult result = new OneTransactionResult();
		result.setErrorCode("1");
        result.setCode(1);
        result.setT((List) object);
        result.setStatus("INIT");
        result.setErrorMessage("失败");
        return result;
	}
	public static String code(String code) {
		String code1 ="";
		if(code.equals("0")) {
			code1="调用成功";
		}else if(code.equals("1")) {
			code1="调用失败";
		}
        return code1;
	}
	public static String status(String status) {
		String status1 ="";
		if(status.equals("SUCCESS")) {
			status1="处理成功";
		}else if(status.equals("INIT")) {
			status1="处理失败";
		}
        return status1;
	}
	public static String getErrorCode(String errorCode) {
		String errorCode1 ="";
		if(errorCode.equals("100001")) {
			errorCode1="【100001】系统错误";
		}else if(errorCode.equals("100002")) {
			errorCode1="【100002】json 参数格式错误";
		}else if(errorCode.equals("100003")) {
			errorCode1="【100003】签名验证失败";
		}else if(errorCode.equals("100004")) {
			errorCode1="【100004】平台编号不存在";
		}else if(errorCode.equals("100005")) {
			errorCode1="【100005】平台状态异常";
		}else if(errorCode.equals("100006")) {
			errorCode1="【100006】业务未开通";
		}else if(errorCode.equals("100007")) {
			errorCode1="【100007】查询对象不存在";
		}else if(errorCode.equals("100008")) {
			errorCode1="【100008】业务受理失败";
		}else if(errorCode.equals("100009")) {
			errorCode1="【100009】用户不存在";
		}else if(errorCode.equals("100010")) {
			errorCode1="【100010】用户账户不可用";
		}else if(errorCode.equals("100011")) {
			errorCode1="【100011】该用户无此操作权限";
		}else if(errorCode.equals("100012")) {
			errorCode1="【100012】非常抱歉,暂不支持此银行";
		}else if(errorCode.equals("100013")) {
			errorCode1="【100013】请求流水号重复";
		}else if(errorCode.equals("100014")) {
			errorCode1="【100014】余额不足";
		}else if(errorCode.equals("100015")) {
			errorCode1="【100015】标的状态与业务不匹配";
		}else if(errorCode.equals("100016")) {
			errorCode1="【100016】平台垫资账户可用余额不足,请联系平台处理";
		}else if(errorCode.equals("100017")) {
			errorCode1="【100017】平台未启用垫资账户,请联系平台处理";
		}else if(errorCode.equals("100018")) {
			errorCode1="【100018】抱歉,您的提现申请方式不在受理时间范围内";
		}else if(errorCode.equals("100019")) {
			errorCode1="【100019】标的类型与业务不匹配";
		}else if(errorCode.equals("100020")) {
			errorCode1="【100020】交易处理中,请勿重试";
		}else if(errorCode.equals("100021")) {
			errorCode1="【100021】抱歉,节假日单笔提现额度超限";
		}else if(errorCode.equals("100022")) {
			errorCode1="【100022】D0 提现不支持提现拦截操作";
		}
        return errorCode1;
	}
}
