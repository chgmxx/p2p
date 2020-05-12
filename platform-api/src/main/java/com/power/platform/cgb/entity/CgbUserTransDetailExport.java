/**
 * 银行托管-流水-Entity.
 */
package com.power.platform.cgb.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.NumberUtils;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.userinfo.entity.UserInfo;

import javax.validation.constraints.NotNull;

/**
 * 导出专用
 * 
 * @author lance
 * @version 2017-10-26
 */
public class CgbUserTransDetailExport extends DataEntity<CgbUserTransDetailExport> {

	private static final long serialVersionUID = 1L;
	public String transId; // trans_id
	private String accountId; // account_id
	private String userId; // user_id
	private Date transDate; // trans_date
	private Integer trustType; // trust_type
	private Double amount; // amount
	private String amountStr; // 金额.
	private Double avaliableAmount; // avaliable_amount
	private String avaliableAmountStr; // 可用余额.
	private Integer inOutType; // in_out_type
	private Integer state; // state
	private Date beginTransDate; // 开始 trans_date
	private Date endTransDate; // 结束 trans_date
	private String trustTypeStr; //
	private String stateStr; // 状态字符
	private List<Integer> transtypes; // 多个类型查找
	private String trustTypeName;

	private UserInfo userInfo;
	private CgbUserAccount userAccountInfo;
	private CreditUserInfo creditUserInfo;

	/**
	 * 交易流水单选按钮事件类型，1：出借人，2：借款人.
	 */
	private String transDetailRadioType;

	/**
	 * 1：出借人.
	 */
	public static final String TRANS_DETAIL_RADIO_TYPE_1 = "1";
	/**
	 * 2：借款人.
	 */
	public static final String TRANS_DETAIL_RADIO_TYPE_2 = "2";

	public CgbUserTransDetailExport() {

		super();
	}

	public CgbUserTransDetailExport(String id) {

		super(id);
	}
	
	@ExcelField(title = "出借人帐号", align = 2, sort = 10)
	private String getUserName() {

		if (userInfo != null) {
			return userInfo.getName();
		}
		return "";
	}

	@ExcelField(title = "出借人姓名", align = 2, sort = 11)
	private String getUserRealName() {

		if (userInfo != null) {
			return userInfo.getRealName();
		}
		return "";
	}


	@Length(min = 0, max = 64, message = "交易id长度必须介于 0 和 64 之间")
	@ExcelField(title = "订单号", align = 2, sort = 5)
	public String getTransId() {

		return transId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message = "交易日期不能为空")
	@ExcelField(title = "交易日期", align = 2, sort = 20)
	public Date getTransDate() {

		return transDate;
	}

	@ExcelField(title = "金额", align = 2, sort = 10)
	public String getAmount() {

		return  amount.toString();
	}

	@ExcelField(title = "可用余额", align = 2, sort = 10)
	public String getAvaliableAmount() {

		return avaliableAmount.toString();
	}

	@ExcelField(title = "交易类型", align = 2, sort = 25)
	public String getTrustTypeStr() {

		return trustTypeStr;
	}

	@ExcelField(title = "收支类型", align = 2, sort = 30)
	public String getInOutTypeStr() {

		if (inOutType.equals(1)) {
			return "收入";
		} else if (inOutType.equals(2)) {
			return "支出";
		}
		return "";
	}

	@ExcelField(title = "状态", align = 2, sort = 45)
	public String getStateStr() {

		return stateStr;
	}
	
	@ExcelField(title = "备注", align = 2, sort = 50)
	public String getRmarksStr() {
		
		return remarks;
	}

}