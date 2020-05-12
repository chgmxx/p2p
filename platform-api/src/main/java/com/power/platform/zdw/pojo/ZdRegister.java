package com.power.platform.zdw.pojo;

/**
 * 
 * 类: ZdRegister <br>
 * 描述: 中登登记信息 <br>
 * 作者: Roy <br>
 * 时间: 2019年10月31日 下午8:07:25
 */
public class ZdRegister {

	private String registerSpan; // 登记期限，0.5：半年，1.0：1年
	private String sn; // 编号，标的编号，唯一标识
	private ZdPreparer zdPreparer; // 登记填表人信息明细
	private ZdPledgor zdPledgor; // 登记出质人信息明细
	private ZdProperty zdProperty; // 登记质押财产信息明细

	public String getRegisterSpan() {

		return registerSpan;
	}

	public void setRegisterSpan(String registerSpan) {

		this.registerSpan = registerSpan;
	}

	public String getSn() {

		return sn;
	}

	public void setSn(String sn) {

		this.sn = sn;
	}

	public ZdPreparer getZdPreparer() {

		return zdPreparer;
	}

	public void setZdPreparer(ZdPreparer zdPreparer) {

		this.zdPreparer = zdPreparer;
	}

	public ZdPledgor getZdPledgor() {

		return zdPledgor;
	}

	public void setZdPledgor(ZdPledgor zdPledgor) {

		this.zdPledgor = zdPledgor;
	}

	public ZdProperty getZdProperty() {

		return zdProperty;
	}

	public void setZdProperty(ZdProperty zdProperty) {

		this.zdProperty = zdProperty;
	}

}
