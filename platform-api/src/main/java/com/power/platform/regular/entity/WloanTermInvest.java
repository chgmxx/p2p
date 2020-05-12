package com.power.platform.regular.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.activity.entity.ZtmgPartnerPlatform;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.excel.annotation.ExcelField;
import com.power.platform.data.entity.LenderStatistics;
import com.power.platform.userinfo.entity.UserInfo;

/**
 * 
 * 类: WloanTermInvest <br>
 * 描述: 定期融资投资表Entity. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2016年1月4日 下午7:00:54
 */
public class WloanTermInvest extends DataEntity<WloanTermInvest> {

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
	private String projectId;//
	private List<String> stateItem;
	
	private ZtmgPartnerPlatform partnerForm;

	private LenderStatistics lenderStatistics; //出借人信息统计对象
	
	public WloanTermInvest() {

		super();
	}

	public WloanTermInvest(String id) {

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
	 * 方法: getFullDate <br>
	 * 描述: 满标日期. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:32:43
	 * 
	 * @return
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "满标日期", align = 2, sort = 30)
	public Date getFullDate() {

		return wloanTermProject.getFullDate();
	}

	/**
	 * 
	 * 方法: getProjectSn <br>
	 * 描述: 项目编号. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:33:44
	 * 
	 * @return
	 */
	@ExcelField(title = "项目编号", align = 2, sort = 40)
	public String getProjectSn() {

		return wloanTermProject.getSn();
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
	 * 方法: getProjectState <br>
	 * 描述: 项目状态. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:34:46
	 * 
	 * @return
	 */
	@ExcelField(title = "项目状态", align = 2, sort = 60)
	public String getProjectState() {

		String content = "";
		Integer flag = Integer.valueOf(wloanTermProject.getState());
		if (flag == 4 || flag == 5) {
			content = "投资中";
		} else if (flag == 6) {
			content = "还款中";
		} else {
			content = "已结束";
		}
		return content;
	}

	/**
	 * 
	 * 方法: getSubject <br>
	 * 描述: 融资主体. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:36:29
	 * 
	 * @return
	 */
	@ExcelField(title = "融资主体", align = 2, sort = 70)
	public String getSubject() {

		return wloanSubject.getCompanyName();
	}

	/**
	 * 
	 * 方法: getAnnualRate <br>
	 * 描述: 年化收益率. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:37:23
	 * 
	 * @return
	 */
	@ExcelField(title = "年化收益率", align = 2, sort = 80)
	public String getAnnualRate() {

		return wloanTermProject.getAnnualRate().toString();
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
	@ExcelField(title = "融资期限", align = 2, sort = 90)
	public String getSpan() {

		return wloanTermProject.getSpan().toString();
	}

	/**
	 * 
	 * 方法: getDueToDate <br>
	 * 描述: 到期日期. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:56:18
	 * 
	 * @return
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "到期日期", align = 2, sort = 100)
	public Date getDueToDate() {

		return wloanTermProject.getEndDate();
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title = "投资日期", align = 2, sort = 110)
	public Date getBeginDate() {

		return beginDate;
	}

	/**
	 * 
	 * 方法: getFinancingAmountStr <br>
	 * 描述: 融资金额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午10:59:38
	 * 
	 * @return
	 */
	@ExcelField(title = "融资金额", align = 2, sort = 120)
	public String getFinancingAmountStr() {

		return String.valueOf(wloanTermProject.getAmount());
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

	/**
	 * 
	 * 方法: getPrincipalStr <br>
	 * 描述: 应付本金. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午11:00:57
	 * 
	 * @return
	 */
	@ExcelField(title = "应付本金", align = 2, sort = 140)
	public String getPrincipalStr() {

		return String.valueOf(amount);
	}

	/**
	 * 
	 * 方法: getInterestStr <br>
	 * 描述: 应付利息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午11:01:40
	 * 
	 * @return
	 */
	@ExcelField(title = "应付利息", align = 2, sort = 150)
	public String getInterestStr() {

		return String.valueOf(interest);
	}

	/**
	 * 
	 * 方法: getStateFlag <br>
	 * 描述: 投资状态. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午11:07:14
	 * 
	 * @return
	 */
	@ExcelField(title = "投资状态", align = 2, sort = 160)
	public String getStateFlag() {

		String content = "投标成功";
		return content;
	}

	/**
	 * 
	 * 方法: getVoucherAmountStr <br>
	 * 描述: 抵用券金额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年10月13日 上午11:08:04
	 * 
	 * @return
	 */
	@ExcelField(title = "抵用券金额", align = 2, sort = 170)
	public String getVoucherAmountStr() {

		if (voucherAmount == 0) {
			return "0";
		}
		return String.valueOf(voucherAmount);
	}

	@Length(min = 0, max = 64, message = "流水号长度必须介于 0 和 64 之间")
	public String getSn() {

		return sn;
	}

	public void setSn(String sn) {

		this.sn = sn;
	}

	@Length(min = 0, max = 64, message = "冻结流水号长度必须介于 0 和 64 之间")
	public String getFreezeSn() {

		return freezeSn;
	}

	public void setFreezeSn(String freezeSn) {

		this.freezeSn = freezeSn;
	}

	@Length(min = 0, max = 64, message = "解冻流水号长度必须介于 0 和 64 之间")
	public String getUnfreezeSn() {

		return unfreezeSn;
	}

	public void setUnfreezeSn(String unfreezeSn) {

		this.unfreezeSn = unfreezeSn;
	}

	public WloanTermProject getWloanTermProject() {

		return wloanTermProject;
	}

	public void setWloanTermProject(WloanTermProject wloanTermProject) {

		this.wloanTermProject = wloanTermProject;
	}

	public UserInfo getUserInfo() {

		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {

		this.userInfo = userInfo;
	}

	public WloanSubject getWloanSubject() {

		return wloanSubject;
	}

	public void setWloanSubject(WloanSubject wloanSubject) {

		this.wloanSubject = wloanSubject;
	}

	@Length(min = 0, max = 64, message = "金额长度必须介于 0 和 64 之间")
	public Double getAmount() {

		return amount;
	}

	public void setAmount(Double amount) {

		this.amount = amount;
	}

	@Length(min = 0, max = 64, message = "利息长度必须介于 0 和 64 之间")
	public Double getInterest() {

		return interest;
	}

	public void setInterest(Double interest) {

		this.interest = interest;
	}

	@Length(min = 0, max = 64, message = "手续费长度必须介于 0 和 64 之间")
	public Double getFeeAmount() {

		return feeAmount;
	}

	public void setFeeAmount(Double feeAmount) {

		this.feeAmount = feeAmount;
	}

	public void setBeginDate(Date beginDate) {

		this.beginDate = beginDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {

		return endDate;
	}

	public void setEndDate(Date endDate) {

		this.endDate = endDate;
	}

	@Length(min = 0, max = 255, message = "投标IP长度必须介于 0 和 255 之间")
	public String getIp() {

		return ip;
	}

	public void setIp(String ip) {

		this.ip = ip;
	}

	@Length(min = 0, max = 1, message = "状态长度必须介于 0 和 1 之间")
	public String getState() {

		return state;
	}

	public void setState(String state) {

		this.state = state;
	}

	@Length(min = 0, max = 1, message = "投标状态长度必须介于 0 和 1 之间")
	public String getBidState() {

		return bidState;
	}

	public void setBidState(String bidState) {

		this.bidState = bidState;
	}

	public String getContractPdfPath() {

		return contractPdfPath;
	}

	public void setContractPdfPath(String contractPdfPath) {

		this.contractPdfPath = contractPdfPath;
	}

	public Double getVoucherAmount() {

		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {

		this.voucherAmount = voucherAmount;
	}

	public Date getBeginBeginDate() {

		return beginBeginDate;
	}

	public void setBeginBeginDate(Date beginBeginDate) {

		this.beginBeginDate = beginBeginDate;
	}

	public Date getEndBeginDate() {

		return endBeginDate;
	}

	public void setEndBeginDate(Date endBeginDate) {

		this.endBeginDate = endBeginDate;
	}

	public Date getBeginEndDate() {

		return beginEndDate;
	}

	public void setBeginEndDate(Date beginEndDate) {

		this.beginEndDate = beginEndDate;
	}

	public Date getEndEndDate() {

		return endEndDate;
	}

	public void setEndEndDate(Date endEndDate) {

		this.endEndDate = endEndDate;
	}

	public List<String> getStateItem() {

		return stateItem;
	}

	public void setStateItem(List<String> stateItem) {

		this.stateItem = stateItem;
	}

	public Integer getUserFlag() {

		return userFlag;
	}

	public void setUserFlag(Integer userFlag) {

		this.userFlag = userFlag;
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	public String getBeginInvestDate() {

		return beginInvestDate;
	}

	public void setBeginInvestDate(String beginInvestDate) {

		this.beginInvestDate = beginInvestDate;
	}

	public String getEndInvestDate() {

		return endInvestDate;
	}

	public void setEndInvestDate(String endInvestDate) {

		this.endInvestDate = endInvestDate;
	}

	public ZtmgPartnerPlatform getPartnerForm() {
		return partnerForm;
	}

	public void setPartnerForm(ZtmgPartnerPlatform partnerForm) {
		this.partnerForm = partnerForm;
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public LenderStatistics getLenderStatistics() {
		return lenderStatistics;
	}

	public void setLenderStatistics(LenderStatistics lenderStatistics) {
		this.lenderStatistics = lenderStatistics;
	}

}