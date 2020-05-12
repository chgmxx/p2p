package com.power.platform.activity.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: AUserAwardsHistory <br>
 * 描述: 活动客户奖励历史. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年4月20日 上午10:32:35
 */
public class AUserAwardsHistory extends DataEntity<AUserAwardsHistory> {

	private static final long serialVersionUID = 1L;
	/**
	 * 奖励ID(活动字典数据，查询客户奖励).
	 */
	private String awardId;
	/**
	 * 抵用券字典数据.
	 */
	private AVouchersDic aVouchersDic;
	/**
	 * 加息券字典数据.
	 */
	private ARateCouponDic aRateCouponDic;
	/**
	 * 客户ID.
	 */
	private String userId;
	/**
	 * 客户账号信息.
	 */
	private UserInfo userInfo;
	/**
	 * 逾期日期(客户该类型奖励到期时间).
	 */
	private Date overdueDate;
	/**
	 * 状态(客户奖励状态)：1：可用，未使用，2：已使用，3：逾期的、过期的，4：使用中，5：未知状态.
	 */
	private String state;
	/**
	 * 奖励类型：1：抵用券/代金券，2：加息券.
	 */
	private String type;
	/**
	 * 奖励内容：可以是抵用券/加息券等.
	 */
	private String value;
	/**
	 * 投资ID(标识客户投资使用该类型奖励情况).
	 */
	private String bidId;
	
	/**
	 * 可使用的期限
	 */
	private String spans;
	
	//备注，活动区分
	private String remark;

	public AUserAwardsHistory() {

		super();
	}

	public AUserAwardsHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "奖励ID长度必须介于 0 和 64 之间")
	public String getAwardId() {

		return awardId;
	}

	public void setAwardId(String awardId) {

		this.awardId = awardId;
	}

	public AVouchersDic getaVouchersDic() {

		return aVouchersDic;
	}

	public void setaVouchersDic(AVouchersDic aVouchersDic) {

		this.aVouchersDic = aVouchersDic;
	}

	public ARateCouponDic getaRateCouponDic() {

		return aRateCouponDic;
	}

	public void setaRateCouponDic(ARateCouponDic aRateCouponDic) {

		this.aRateCouponDic = aRateCouponDic;
	}

	@Length(min = 0, max = 64, message = "客户ID长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getOverdueDate() {

		return overdueDate;
	}

	public void setOverdueDate(Date overdueDate) {

		this.overdueDate = overdueDate;
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	@Length(min = 0, max = 64, message = "长度必须介于 0 和 64 之间")
	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	@Length(min = 0, max = 64, message = "投资ID长度必须介于 0 和 64 之间")
	public String getBidId() {

		return bidId;
	}

	public void setBidId(String bidId) {

		this.bidId = bidId;
	}

	public String getSpans() {
		return spans;
	}

	public void setSpans(String spans) {
		this.spans = spans;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}