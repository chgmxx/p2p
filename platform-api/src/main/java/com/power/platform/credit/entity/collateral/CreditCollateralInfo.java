package com.power.platform.credit.entity.collateral;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;

/**
 * 
 * 类: CreditCollateralInfo <br>
 * 描述: 抵押物信息Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年6月20日 下午2:26:56
 */
public class CreditCollateralInfo extends DataEntity<CreditCollateralInfo> {

	private static final long serialVersionUID = 1L;
	// 用户ID.
	private String creditUserId;
	// 车牌号码.
	private String plateNumber;
	// 型号.
	private String modelNumber;
	// 购买价格.
	private String buyPrice;
	// 购买日期.
	private String buyDate;
	// 状态，1：审核中，2：已通过，3：已拒绝.
	private String state;
	// 行驶里程.
	private String mileage;
	// 抵押物估价.
	private String collateralPrice;
	// 发动机号.
	private String engineNumber;
	// 备注.
	private String remark;

	private CreditUserInfo userInfo;

	public CreditCollateralInfo() {

		super();
	}

	public CreditCollateralInfo(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getCreditUserId() {

		return creditUserId;
	}

	public void setCreditUserId(String creditUserId) {

		this.creditUserId = creditUserId;
	}

	@Length(min = 0, max = 64, message = "车牌号码长度必须介于 0 和 64 之间")
	public String getPlateNumber() {

		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {

		this.plateNumber = plateNumber;
	}

	@Length(min = 0, max = 64, message = "型号长度必须介于 0 和 64 之间")
	public String getModelNumber() {

		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {

		this.modelNumber = modelNumber;
	}

	@Length(min = 0, max = 11, message = "购买价格长度必须介于 0 和 11 之间")
	public String getBuyPrice() {

		return buyPrice;
	}

	public void setBuyPrice(String buyPrice) {

		this.buyPrice = buyPrice;
	}

	public String getBuyDate() {

		return buyDate;
	}

	public void setBuyDate(String buyDate) {

		this.buyDate = buyDate;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 11, message = "行驶里程长度必须介于 0 和 11 之间")
	public String getMileage() {

		return mileage;
	}

	public void setMileage(String mileage) {

		this.mileage = mileage;
	}

	@Length(min = 0, max = 11, message = "抵押物估价长度必须介于 0 和 11 之间")
	public String getCollateralPrice() {

		return collateralPrice;
	}

	public void setCollateralPrice(String collateralPrice) {

		this.collateralPrice = collateralPrice;
	}

	@Length(min = 0, max = 64, message = "发动机号长度必须介于 0 和 64 之间")
	public String getEngineNumber() {

		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {

		this.engineNumber = engineNumber;
	}

	@Length(min = 0, max = 255, message = "备注长度必须介于 0 和 255 之间")
	public String getRemark() {

		return remark;
	}

	public void setRemark(String remark) {

		this.remark = remark;
	}

	public CreditUserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(CreditUserInfo userInfo) {

		this.userInfo = userInfo;
	}

}