package com.power.platform.trandetail.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserAccountInfo;
import com.power.platform.userinfo.entity.UserInfo;

import javax.validation.constraints.NotNull;


/**
 * 客户流水记录Entity
 * @author soler
 * @version 2015-12-23
 */
public class UserTransDetail extends DataEntity<UserTransDetail> {
	
	private static final long serialVersionUID = 1L;
	public String 			transId;			// trans_id
	private String 			accountId;			// account_id
	private String 			userId;				// user_id
	private Date 			transDate;			// trans_date
	private Integer 		trustType;			// trust_type
	private Double 			amount;				// amount
	private Double 			avaliableAmount;	// avaliable_amount
	private Integer 		inOutType;			// in_out_type
	private Integer 		state;				// state
	private Date 			beginTransDate;		// 开始 trans_date
	private Date 			endTransDate;		// 结束 trans_date
	private String 			trustTypeStr;		//
	private String 			stateStr;			// 状态字符
	private List<Integer> 	transtypes;			// 多个类型查找
	private String trustTypeName;
	
	public static final Integer TRANS_TYPE_IN = 1;//收支类型 收入
	public static final Integer TRANS_TYPE_OUT = 2;//收支类型 支出
	
	//交易类型
	public static final Integer TRANS_RECHARGE = 0;// 0：充值，
	public static final Integer TRANS_CASH = 1;//1：提现
	public static final Integer TRANS_CURRENCY = 2;//2：活期投资,
	public static final Integer TRANS_TERM = 3;//	3：定期投资,
	public static final Integer TRANS_INTEREST = 4;//	4：还利息,
	public static final Integer TRANS_PRINCIPAL = 5;//	5：还本金,
	public static final Integer TRANS_REDEEM = 6;//	6：活期赎回,
	public static final Integer TRANS_ACTIVITY = 7;//	7：活动返现
	public static final Integer TRANS_CURRENCY_INTEREST = 8;//	8：活期收益
	public static final Integer TRANS_COMMESSION = 9;//	9：佣金
	public static final Integer TRANS_USERAWARDS= 10;//	10：优惠券
	public static final Integer TRANS_EXPERIENCEMONEY = 11;//体验金

	//交易状态
	public static final Integer TRANS_STATE_DOING = 1;//处理中
	public static final Integer TRANS_STATE_SUCCESS = 2;//成功
	public static final Integer TRANS_STATE_FAIL = 3;//失败
	
	private UserInfo userInfo;
	private UserAccountInfo userAccountInfo;
	
	

	public UserAccountInfo getUserAccountInfo() {
		return userAccountInfo;
	}

	public void setUserAccountInfo(UserAccountInfo userAccountInfo) {
		this.userAccountInfo = userAccountInfo;
	}

	public String getTrustTypeStr() {
		return trustTypeStr;
	}

	public List<Integer> getTranstypes() {
		return transtypes;
	}

	public void setTranstypes(List<Integer> transtypes) {
		this.transtypes = transtypes;
	}

	public void setTrustTypeStr(String trustTypeStr) {
		this.trustTypeStr = trustTypeStr;
	}

	public String getStateStr() {
		return stateStr;
	}

	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}

	public UserTransDetail() {
		super();
	}

	public UserTransDetail(String id){
		super(id);
	}

	
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Length(min=1, max=32, message="trans_id长度必须介于 1 和 32 之间")
	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}
	
	@Length(min=1, max=32, message="account_id长度必须介于 1 和 32 之间")
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	@Length(min=1, max=32, message="user_id长度必须介于 1 和 32 之间")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="trans_date不能为空")
	@ExcelField(title="交易时间", type=0, align=1, sort=90)
	public Date getTransDate() {
		return transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}
	
	@Length(min=1, max=11, message="trust_type长度必须介于 1 和 11 之间")
	public Integer getTrustType() {
		return trustType;
	}

	public void setTrustType(Integer trustType) {
		this.trustType = trustType;
	}
	
	@ExcelField(title="交易金额", align=2, sort=70)
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@ExcelField(title="当前可用金额", align=2, sort=70)
	public Double getAvaliableAmount() {
		return avaliableAmount;
	}

	public void setAvaliableAmount(Double double1) {
		this.avaliableAmount = double1;
	}
	
	@Length(min=1, max=2, message="in_out_type长度必须介于 1 和 2 之间")
	public Integer getInOutType() {
		return inOutType;
	}

	public void setInOutType(Integer inOutType) {
		this.inOutType = inOutType;
	}
	
	@Length(min=1, max=2, message="state长度必须介于 1 和 2 之间")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	public Date getBeginTransDate() {
		return beginTransDate;
	}

	public void setBeginTransDate(Date beginTransDate) {
		this.beginTransDate = beginTransDate;
	}
	
	public Date getEndTransDate() {
		return endTransDate;
	}

	public void setEndTransDate(Date endTransDate) {
		this.endTransDate = endTransDate;
	}

	@ExcelField(title="收支类型", align=2, sort=100)
	public String getInoutType() {
		return this.getInOutType()==1?"收入":"支出";
	}

	@ExcelField(title="手机号", align=2, sort=70)
	public String getName(){
		return this.userInfo.getName();
	}
	@ExcelField(title="姓名", align=2, sort=40)
	public String getRealName(){
		return this.userInfo.getRealName();
	}
	@ExcelField(title="当前代收本金", align=2, sort=40)
	public Double getRegularPricepal(){
		return this.userAccountInfo.getRegularDuePrincipal();
	}
	@ExcelField(title="交易类型",type=1, align=2, sort=110)
	public String getTrustTypeName() {
		return getTypeName(this.getTrustType());
	}
	
	@ExcelField(title="备注", align=2, sort=120)
	public String getReamark(){
		return remarks;
	}

	public void setTrustTypeName(String trustTypeName) {
		this.trustTypeName = trustTypeName;
	}

	
	private String getTypeName(Integer type){
		String name = "";
		switch(type){
		case 0 :name = "充值";break;
		case 1 :name = "提现";break;
		case 2 :name = "活期投资";break;
		case 3:name = "定期投资";break;
		case 4 :name = "还利息";break;
		case 5 :name = "还本金";break;
		case 6 :name = "活期赎回";break;
		case 7 :name = "活动返现";break;
		case 8 :name = "活期收益";break;
		case 9 :name = "佣金";break;
		case 10:name = "优惠券";break;
		case 11:name = "体验金";break;
		default:name = "其他";break;
	  }
		return name;
	}
}