package com.power.platform.activity.entity;

import org.hibernate.validator.constraints.Length;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.entity.Dict;
import com.power.platform.trandetail.entity.UserTransDetail;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: ZtmgPartnerPlatform <br>
 * 描述: ZTMG合作方信息Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年6月23日 下午2:58:29
 */
public class ZtmgPartnerPlatform extends DataEntity<ZtmgPartnerPlatform> {

	private static final long serialVersionUID = 1L;
	private String platformName; // 平台名称
	private String platformCode; // 平台编码
	private String platformType; // 平台类型，'1'：个人电脑，'2'：移动设备
	private String phone; // 联系人电话
	private String name; // 联系人姓名
	private String email; // 电子邮箱
	private String locus; // 合作方所在地

	private Double rate;//返利利率
	private Double money;//每个有效用户返利金额
	
	private Integer registUser;//注册人数
	private Integer investUser;//投资人数
	private Double sumMoney;//总金额
	
	public UserTransDetail userTransDetail;//交易流水
	Double amount;//推荐人投资金额
	public String transDate;//交易日期
	public UserInfo userInfo;//用户信息表
	public Double moneyToOne;//单笔投资返利金额
	public String userInfoName;//被推荐人姓名
	private LevelDistribution levelDistribution;//三级关系表
	
	private Area area; // 注册地.
	private Dict dict; // 字典类.

	public ZtmgPartnerPlatform() {

		super();
	}

	public ZtmgPartnerPlatform(String id) {

		super(id);
	}

	
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getUserInfoName() {
		return userInfoName;
	}

	public void setUserInfoName(String userInfoName) {
		this.userInfoName = userInfoName;
	}

	public Double getMoneyToOne() {
		return moneyToOne;
	}

	public void setMoneyToOne(Double moneyToOne) {
		this.moneyToOne = moneyToOne;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserTransDetail getUserTransDetail() {
		return userTransDetail;
	}

	public void setUserTransDetail(UserTransDetail userTransDetail) {
		this.userTransDetail = userTransDetail;
	}

	public LevelDistribution getLevelDistribution() {
		return levelDistribution;
	}

	public void setLevelDistribution(LevelDistribution levelDistribution) {
		this.levelDistribution = levelDistribution;
	}

	public Integer getRegistUser() {
		return registUser;
	}

	public void setRegistUser(Integer registUser) {
		this.registUser = registUser;
	}

	public Integer getInvestUser() {
		return investUser;
	}

	public void setInvestUser(Integer investUser) {
		this.investUser = investUser;
	}

	public Double getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(Double sumMoney) {
		this.sumMoney = sumMoney;
	}

	@Length(min = 0, max = 10, message = "返利利率，两位小数")
	public Double getRate() {

		return rate;
	}

	public void setRate(Double rate) {

		this.rate = rate;
	}
	
	@Length(min = 0, max = 10, message = "返利金额")
	public Double getMoney() {

		return money;
	}

	public void setMoney(Double money) {

		this.money = money;
	}
	
	@Length(min = 1, max = 64, message = "平台名称长度必须介于 1 和 64 之间")
	public String getPlatformName() {

		return platformName;
	}

	public void setPlatformName(String platformName) {

		this.platformName = platformName;
	}

	@Length(min = 1, max = 64, message = "平台编码长度必须介于 1 和 64 之间")
	public String getPlatformCode() {

		return platformCode;
	}

	public void setPlatformCode(String platformCode) {

		this.platformCode = platformCode;
	}

	@Length(min = 1, max = 1, message = "平台类型，'1'：个人电脑，'2'：移动设备长度必须介于 1 和 1 之间")
	public String getPlatformType() {

		return platformType;
	}

	public void setPlatformType(String platformType) {

		this.platformType = platformType;
	}

	@Length(min = 0, max = 64, message = "联系人电话长度必须介于 0 和 64 之间")
	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	@Length(min = 0, max = 64, message = "联系人姓名长度必须介于 0 和 64 之间")
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Length(min = 0, max = 64, message = "电子邮箱长度必须介于 0 和 64 之间")
	public String getEmail() {

		return email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	@Length(min = 0, max = 64, message = "合作方所在地长度必须介于 0 和 64 之间")
	public String getLocus() {

		return locus;
	}

	public void setLocus(String locus) {

		this.locus = locus;
	}

	public Area getArea() {

		return area;
	}

	public void setArea(Area area) {

		this.area = area;
	}

	public Dict getDict() {

		return dict;
	}

	public void setDict(Dict dict) {

		this.dict = dict;
	}

}