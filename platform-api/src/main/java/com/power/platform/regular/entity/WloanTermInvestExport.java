package com.power.platform.regular.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 导出专用
 */
public class WloanTermInvestExport extends DataEntity<WloanTermInvestExport> {

	private static final long serialVersionUID = 1L;
	private String sn; // 流水号
	private String freezeSn; // 冻结流水号
	private String unfreezeSn; // 解冻流水号
	private WloanTermProject wloanTermProject; // 定期项目.
	private UserInfo userInfo; // 客户信息.
	private WloanSubject wloanSubject; // 融资主体
	private Double amount; // 金额
	private Double interest; // 利息
	private Double feeAmount; // 手续费
	private Date beginDate; // 开始日期
	private Date endDate; // 结束日期
	private String ip; // 投标IP
	private String state; // 状态
	private String bidState; // 投标状态
	private Double voucherAmount; // 抵用券金额.
	private String contractPdfPath; // 合同PDF存储路径.
	private Date beginBeginDate; // 开始 开始日期
	private Date endBeginDate; // 结束 开始日期
	private Date beginEndDate; // 开始 结束日期
	private Date endEndDate; // 结束 结束日期
	private Integer userFlag; // 新老用户标记.
	private String userId; // 客户id.
	private String beginInvestDate; // 开始投资日期.
	private String endInvestDate; // 结束投资日期.

	private List<String> stateItem;
	
	private ZtmgPartnerPlatform partnerForm;

	public WloanTermInvestExport() {

		super();
	}

	public WloanTermInvestExport(String id) {

		super(id);
	}

	/**
	 * 
	 * 方法: getMobilePhone <br>
	 * 描述: 投资人手机. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月19日 上午10:55:49
	 * 
	 * @return
	 */
	@ExcelField(title = "投资人手机", align = 2, sort = 10)
	public String getMobilePhone() {

		return userInfo.getName();
	}

	/**
	 * 
	 * 方法: getRealName <br>
	 * 描述: 投资人名称. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月19日 上午11:09:58
	 * 
	 * @return
	 */
	@ExcelField(title = "投资人名称", align = 2, sort = 20)
	public String getRealName() {

		return userInfo.getRealName();
	}

	/**
	 * 
	 * 方法: getProjectName <br>
	 * 描述: 项目名称. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年9月19日 上午11:10:56
	 * 
	 * @return
	 */
	@ExcelField(title = "项目名称", align = 2, sort = 50)
	public String getProjectName() {

		return wloanTermProject.getName();
	}


	/**
	 * 
	 * 方法: getSpan <br>
	 * 描述: 融资期限. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:55:10
	 * 
	 * @return
	 */
	@ExcelField(title = "投资期限(天)", align = 2, sort = 90)
	public String getSpan() {

		return wloanTermProject.getSpan().toString();
	}

	/**
	 * 
	 * 方法: getBeginDate <br>
	 * 描述: 投资日期. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:57:50
	 * 
	 * @return
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title = "投资日期", align = 2, sort = 110)
	public Date getBeginDate() {

		return beginDate;
	}
	

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * 
	 * 方法: getAmountStr <br>
	 * 描述: 投资金额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:59:51
	 * 
	 * @return
	 */
	@ExcelField(title = "投资金额", align = 2, sort = 130)
	public String getAmountStr() {

		return String.valueOf(amount);
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	@ExcelField(title = "注册日期", align = 2, sort = 110)
    public Date getUserRegistDate(){
		
    	return userInfo.getRegisterDate();
    } 
	
	@ExcelField(title = "渠道来源", align = 2, sort = 20)
	public String getPartnerName(){
		
		return partnerForm.getPlatformName();
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public ZtmgPartnerPlatform getPartnerForm() {
		return partnerForm;
	}

	public void setPartnerForm(ZtmgPartnerPlatform partnerForm) {
		this.partnerForm = partnerForm;
	}

	public WloanTermProject getWloanTermProject() {
		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {
		this.wloanTermProject = wloanTermProject;
	}
	
	
}