/**
 * Copyright &copy; 2012-2014 <a
 * href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.bouns.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.activity.entity.AVouchersDic;
import com.power.platform.common.persistence.DataEntity;

/**
 * 奖品信息Entity
 * 
 * @author yb
 * @version 2016-12-13
 */
public class AwardInfo extends DataEntity<AwardInfo> {

	private static final long serialVersionUID = 1L;
	private String needAmount; // 奖品所需积分
	private String state; // 状态:0-上架,1-下架,2-删除
	private String docs; // 奖品描述
	private String imgWeb; // 电脑端图片
	private String imgWap; // 移动端图片
	private String isLottery; // 是否为抽奖奖品:0-否,1-是
	private String odds; // 中奖概率
	private Date creatTime; // 创建时间
	private Date updateTime; // 修改时间
	private String name; // 奖品名称
	private String isTrue; // 是否为虚拟奖品
    private String deadline; //过期时间
	private String awardStandard; // 奖品规格
	private String exchangeFlow; // 兑换流程
	private String exchangeDocs; // 兑换说明

	private List<String> imgWebList; // 电脑端图片列表
	private List<String> imgWapList; // 移动端图片列表

	private String vouchersId; // 抵用券类型ID.
	private List<AVouchersDic> vouchersDics; // 抵用券全部字典数据.

	public String getVouchersId() {

		return vouchersId;
	}

	public void setVouchersId(String vouchersId) {

		this.vouchersId = vouchersId;
	}

	public List<AVouchersDic> getVouchersDics() {

		return vouchersDics;
	}

	public void setVouchersDics(List<AVouchersDic> vouchersDics) {

		this.vouchersDics = vouchersDics;
	}

	public AwardInfo() {

		super();
	}

	public AwardInfo(String id) {

		super(id);
	}

	public String getNeedAmount() {

		return needAmount;
	}

	public void setNeedAmount(String needAmount) {

		this.needAmount = needAmount;
	}

	@Length(min = 0, max = 1, message = "状态:0-上架,1-下架,2-删除长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 255, message = "奖品描述长度必须介于 0 和 255 之间")
	public String getDocs() {

		return docs;
	}

	public void setDocs(String docs) {

		this.docs = docs;
	}

	@Length(min = 0, max = 255, message = "电脑端图片长度必须介于 0 和 255 之间")
	public String getImgWeb() {

		return imgWeb;
	}

	public void setImgWeb(String imgWeb) {

		this.imgWeb = imgWeb;
	}

	@Length(min = 0, max = 255, message = "移动端图片长度必须介于 0 和 255 之间")
	public String getImgWap() {

		return imgWap;
	}

	public void setImgWap(String imgWap) {

		this.imgWap = imgWap;
	}

	@Length(min = 0, max = 1, message = "是否为抽奖奖品:0-否,1-是长度必须介于 0 和 1 之间")
	public String getIsLottery() {

		return isLottery;
	}

	public void setIsLottery(String isLottery) {

		this.isLottery = isLottery;
	}

	public String getOdds() {

		return odds;
	}

	public void setOdds(String odds) {

		this.odds = odds;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getCreatTime() {

		return creatTime;
	}

	public void setCreatTime(Date creatTime) {

		this.creatTime = creatTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public Date getUpdateTime() {

		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {

		this.updateTime = updateTime;
	}

	public List<String> getImgWebList() {

		return imgWebList;
	}

	public void setImgWebList(List<String> imgWebList) {

		this.imgWebList = imgWebList;
	}

	public List<String> getImgWapList() {

		return imgWapList;
	}

	public void setImgWapList(List<String> imgWapList) {

		this.imgWapList = imgWapList;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getIsTrue() {

		return isTrue;
	}

	public void setIsTrue(String isTrue) {

		this.isTrue = isTrue;
	}

	public String getAwardStandard() {

		return awardStandard;
	}

	public void setAwardStandard(String awardStandard) {

		this.awardStandard = awardStandard;
	}

	public String getExchangeFlow() {

		return exchangeFlow;
	}

	public void setExchangeFlow(String exchangeFlow) {

		this.exchangeFlow = exchangeFlow;
	}

	public String getExchangeDocs() {

		return exchangeDocs;
	}

	public void setExchangeDocs(String exchangeDocs) {

		this.exchangeDocs = exchangeDocs;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
}