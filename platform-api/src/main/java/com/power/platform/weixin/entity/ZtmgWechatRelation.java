package com.power.platform.weixin.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;

/**
 * 
 * 类: ZtmgWechatRelation <br>
 * 描述: 中投摩根，客户资料与微信资料关系建立Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月7日 上午10:48:06
 */
public class ZtmgWechatRelation extends DataEntity<ZtmgWechatRelation> {

	private static final long serialVersionUID = 1L;
	/**
	 * WeChat唯一标识，open_id.
	 */
	private String openId;
	/**
	 * WeChat昵称.
	 */
	private String nickname;
	/**
	 * 微信头像URL.
	 */
	private String headPortraitUrl;
	/**
	 * 客户账号，id.
	 */
	private String userId;
	/**
	 * 客户账户，id.
	 */
	private String accountId;
	/**
	 * 状态，1：关注成功，2：取消关注.
	 */
	private String state;
	/**
	 * 绑定日期.
	 */
	private Date bindDate;

	public ZtmgWechatRelation() {

		super();
	}

	public ZtmgWechatRelation(String id) {

		super(id);
	}

	@Length(min = 0, max = 64, message = "wechat唯一标识，open_id长度必须介于 0 和 64 之间")
	public String getOpenId() {

		return openId;
	}

	public void setOpenId(String openId) {

		this.openId = openId;
	}

	@Length(min = 0, max = 64, message = "wechat昵称长度必须介于 0 和 64 之间")
	public String getNickname() {

		return nickname;
	}

	public void setNickname(String nickname) {

		this.nickname = nickname;
	}

	@Length(min = 0, max = 255, message = "微信头像url长度必须介于 0 和 255 之间")
	public String getHeadPortraitUrl() {

		return headPortraitUrl;
	}

	public void setHeadPortraitUrl(String headPortraitUrl) {

		this.headPortraitUrl = headPortraitUrl;
	}

	@Length(min = 0, max = 64, message = "客户账号，id长度必须介于 0 和 64 之间")
	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Length(min = 0, max = 64, message = "客户账户，id长度必须介于 0 和 64 之间")
	public String getAccountId() {

		return accountId;
	}

	public void setAccountId(String accountId) {

		this.accountId = accountId;
	}

	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getBindDate() {

		return bindDate;
	}

	public void setBindDate(Date bindDate) {

		this.bindDate = bindDate;
	}

}