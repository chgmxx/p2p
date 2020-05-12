/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 用户兑换奖品Entity
 * 
 * @author yb
 * @version 2016-12-13
 */
public class UserAward extends DataEntity<UserAward> {

	private static final long serialVersionUID = 1L;
	private String userId; // 用户ID
	private Date createTime; // 创建时间
	private Date updateTime; // 更新时间
	private String awardId; // 奖品ID
	private String state; // 状态:0-下单,1-发货,2-结束,3-已兑换未领取
	private String expressNo; // 快递单号
	private String expressName; // 快递名称
	private UserInfo userInfo; // 用户信息
	private AwardInfo awardInfo; // 奖品信息
	private UserConsigneeAddress userConsigneeAddress; // 用户收货地址
	private String addressId; // 用户地址
	private Integer needAmount;// 所需积分
	private Date beginDate; // 开始日期.
	private Date endDate; // 结束日期.
	private String awardGetType;// 兑奖类型,
	private Date deadline;// 失效时间
	private String voucherId;//虚拟商品用户抵用券ID

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getBeginDate() {

		return beginDate;
	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	public UserAward() {

		super();
	}

	public UserAward(String id) {

		super(id);
	}

	/**
	 * 
	 * 方法: getUserRealName <br>
	 * 描述: 用户. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:21:29
	 * 
	 * @return
	 */
	@Length(min = 0, max = 32, message = "姓名长度必须介于 0 和 32 之间")
	@ExcelField(title = "用户", align = 2, sort = 10)
	public String getUserRealName() {

		return userInfo.getRealName();
	}

	/**
	 * 
	 * 方法: getName <br>
	 * 描述: 手机号. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:21:17
	 * 
	 * @return
	 */
	@Length(min = 1, max = 32, message = "手机号码长度必须介于 1 和 32 之间")
	@ExcelField(title = "手机号", align = 2, sort = 20)
	public String getName() {

		return userInfo.getName();
	}

	@Length(min = 0, max = 64, message = "用户ID长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	/**
	 * 
	 * 方法: getCreateTime <br>
	 * 描述: 创建时间. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:21:07
	 * 
	 * @return
	 */
	@ExcelField(title = "创建时间", align = 2, sort = 30)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCreateTime() {

		return createTime;
	}

	public void setCreateTime(Date createTime) {

		this.createTime = createTime;
	}

	/**
	 * 
	 * 方法: getUpdateTime <br>
	 * 描述: 更新时间. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:20:58
	 * 
	 * @return
	 */
	@ExcelField(title = "更新时间", align = 2, sort = 35)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getUpdateTime() {

		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {

		this.updateTime = updateTime;
	}

	@Length(min = 0, max = 64, message = "奖品ID长度必须介于 0 和 64 之间")
	public String getAwardId() {

		return awardId;
	}

	/**
	 * 
	 * 方法: getAwardName <br>
	 * 描述: 奖品. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:20:48
	 * 
	 * @return
	 */
	@ExcelField(title = "奖品", align = 2, sort = 40)
	public String getAwardName() {

		return awardInfo.getName();
	}

	public void setAwardId(String awardId) {

		this.awardId = awardId;
	}

	@Length(min = 0, max = 1, message = "状态:0-下单,1-发货,2-结束长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	/**
	 * 
	 * 方法: getTypeName <br>
	 * 描述: 类型. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:20:34
	 * 
	 * @return
	 */
	@ExcelField(title = "类型", align = 2, sort = 45)
	public String getTypeName() {

		String typeName = "";
		if (awardInfo.getIsTrue().equals("0")) {
			typeName = "实体奖品";
		} else if (awardInfo.getIsTrue().equals("1")) {
			typeName = "虚拟奖品";
		}
		return typeName;
	}

	/**
	 * 
	 * 方法: getStateName <br>
	 * 描述: 状态. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:20:24
	 * 
	 * @return
	 */
	@ExcelField(title = "状态", align = 2, sort = 50)
	public String getStateName() {

		String stateName = "";
		if (state.equals("0")) {
			stateName = "下单";
		} else if (state.equals("1")) {
			stateName = "发货";
		} else if (state.equals("2")) {
			stateName = "结束";
		} else if (state.equals("3")) {
			stateName = "已兑换";
		}
		return stateName;
	}

	public void setState(String state) {

		this.state = state;
	}

	/**
	 * 
	 * 方法: getExpressNo <br>
	 * 描述: 快递单号. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:20:08
	 * 
	 * @return
	 */
	@ExcelField(title = "快递单号", align = 2, sort = 55)
	@Length(min = 0, max = 64, message = "快递单号长度必须介于 0 和 64 之间")
	public String getExpressNo() {

		return expressNo;
	}

	public void setExpressNo(String expressNo) {

		this.expressNo = expressNo;
	}

	/**
	 * 
	 * 方法: getExpressName <br>
	 * 描述: 快递名称. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:30:09
	 * 
	 * @return
	 */
	@ExcelField(title = "快递名称", align = 2, sort = 58)
	@Length(min = 0, max = 64, message = "快递名称长度必须介于 0 和 64 之间")
	public String getExpressName() {

		return expressName;
	}

	public void setExpressName(String expressName) {

		this.expressName = expressName;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public AwardInfo getAwardInfo() {

		return awardInfo;
	}

	public void setAwardInfo(AwardInfo awardInfo) {

		this.awardInfo = awardInfo;
	}

	public UserConsigneeAddress getUserConsigneeAddress() {

		return userConsigneeAddress;
	}

	public void setUserConsigneeAddress(UserConsigneeAddress userConsigneeAddress) {

		this.userConsigneeAddress = userConsigneeAddress;
	}

	public String getAddressId() {

		return addressId;
	}

	public void setAddressId(String addressId) {

		this.addressId = addressId;
	}

	public Integer getneedAmount() {

		return needAmount;
	}

	public void setneedAmount(Integer needAmount) {

		this.needAmount = needAmount;
	}

	/**
	 * 
	 * 方法: getConsigneeName <br>
	 * 描述: 收件人. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:28:21
	 * 
	 * @return
	 */
	@ExcelField(title = "收件人", align = 2, sort = 60)
	public String getConsigneeName() {

		return userConsigneeAddress.getUsername();
	}

	/**
	 * 
	 * 方法: getConsigneeAddress <br>
	 * 描述: 收件地址. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:28:34
	 * 
	 * @return
	 */
	@ExcelField(title = "收件地址", align = 2, sort = 65)
	public String getConsigneeAddress() {

		return userConsigneeAddress.getAddress();
	}

	/**
	 * 
	 * 方法: getConsigneeMobile <br>
	 * 描述: 收件人手机. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年3月13日 下午1:28:41
	 * 
	 * @return
	 */
	@ExcelField(title = "收件人手机", align = 2, sort = 70)
	public String getConsigneeMobile() {

		return userConsigneeAddress.getMobile();
	}

	public String getAwardGetType() {
		return awardGetType;
	}

	public void setAwardGetType(String awardGetType) {
		this.awardGetType = awardGetType;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}
	
}