package com.power.platform.activity.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.regular.entity.WloanTermProject;
import com.power.platform.userinfo.entity.UserInfo;

public class UserRateCouponHistory extends DataEntity<UserRateCouponHistory> {

	private static final long serialVersionUID = 1L;
	private String awardId; // 抵用券ID
	private String userId; // 客户账号ID
	private Date overdueDate; // 逾期日期
	private String state; // 状态
	private String type; // 类型
	private String bidId; // 投资ID
	private String value; // value
	private ARateCouponDic rateCouponDic; // 加息券字典数据.
	private List<ARateCouponDic> rateCouponDics; // 加息券全部字典数据.
	private UserInfo userInfo; // 客户账号信息.
	private WloanTermProject wloanTermProject; // 投资项目信息.

	public UserRateCouponHistory() {

		super();
	}

	public UserRateCouponHistory(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "抵用券ID长度必须介于 0 和 64 之间")
	public String getAwardId() {

		return awardId;
	}

	public void setAwardId(String awardId) {

		this.awardId = awardId;
	}

	@Length(min = 0, max = 64, message = "客户账号ID长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

	@Length(min = 0, max = 1, message = "类型长度必须介于 0 和 1 之间")
	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	@Length(min = 0, max = 64, message = "投资ID长度必须介于 0 和 64 之间")
	public String getBidId() {

		return bidId;
	}

	public void setBidId(String bidId) {

		this.bidId = bidId;
	}

	@Length(min = 0, max = 64, message = "value长度必须介于 0 和 64 之间")
	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public ARateCouponDic getRateCouponDic() {

		return rateCouponDic;
	}

	public void setRateCouponDic(ARateCouponDic rateCouponDic) {

		this.rateCouponDic = rateCouponDic;
	}

	public List<ARateCouponDic> getRateCouponDics() {

		return rateCouponDics;
	}

	public void setRateCouponDics(List<ARateCouponDic> rateCouponDics) {

		this.rateCouponDics = rateCouponDics;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

}