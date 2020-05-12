package com.power.platform.cgb.pojo;

/**
 * 
 * 类: Grant <br>
 * 描述: 用户授权信息. <br>
 * 作者: Mr.li <br>
 * 时间: 2018年11月22日 下午1:55:00
 */
public class Grant {

	// 00-成功01-处理中02-失败
	public String respCode;
	// 六位编码，详见“返回码说明”
	public String respSubCode;
	// 返回详细信息，详见“返回码说明”
	public String respMsg;
	// 用户授权列表.
	public String grantList;
	// 用户授权金额列表.
	public String grantAmountList;
	// 用户授权期限列表.
	public String grantTimeList;

	public String getRespCode() {

		return respCode;
	}

	public void setRespCode(String respCode) {

		this.respCode = respCode;
	}

	public String getRespSubCode() {

		return respSubCode;
	}

	public void setRespSubCode(String respSubCode) {

		this.respSubCode = respSubCode;
	}

	public String getRespMsg() {

		return respMsg;
	}

	public void setRespMsg(String respMsg) {

		this.respMsg = respMsg;
	}

	public String getGrantList() {

		return grantList;
	}

	public void setGrantList(String grantList) {

		this.grantList = grantList;
	}

	public String getGrantAmountList() {

		return grantAmountList;
	}

	public void setGrantAmountList(String grantAmountList) {

		this.grantAmountList = grantAmountList;
	}

	public String getGrantTimeList() {

		return grantTimeList;
	}

	public void setGrantTimeList(String grantTimeList) {

		this.grantTimeList = grantTimeList;
	}

}
