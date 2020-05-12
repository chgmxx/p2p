package com.power.platform.current.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.regular.entity.WGuaranteeCompany;
import com.power.platform.regular.entity.WloanSubject;
import com.power.platform.regular.entity.WloanTermDoc;

/**
 * 活期融资项目Entity
 * @author Mr.Jia
 * @version 2016-01-12
 */
public class WloanCurrentProject extends DataEntity<WloanCurrentProject> {
	
	private static final long serialVersionUID = 1L;
	private String 				sn;							// 编号
	private String 				name;						// 项目名称
	private String 				subjectId;					// 融资主体ID
	private String 				guaranteeId;				// 担保机构ID
	private String 				docId;						// 融资档案ID
	private Double 				amount;						// 融资金额
	private Double 				ammualRate;					// 年化收益
	private Date 				onlineDate;					// 上线时间
	private Date 				endDate;					// 结束日期
	private Integer 			span;						// 期限
	private Double 				feeRate;					// 手续费（%）
	private Double 				currentRealAmount;			// 融资进度
	private String 				purpose;					// 资金用途
	private String 				state;						// 状态（1、草稿，2、审核，3、上线，4、暂定融资，5、结束）
	private String 				guaranteeSn;				// 担保函编号
	private Double 				marginPercentage;			// 保证金
	private String 				guaranteeScheme;			// 担保方案
	private String 				remark;						// 备注
	private String				contractUrl;				// 项目合同路径
	private Double				isForwardMarginPer;			// 是否收取保证金
	private Double				alreadyFeerateAmount;		// 已经收取手续费金额
	
	private WGuaranteeCompany 	wgCompany;	   				// 担保公司
	private WloanSubject 		wloanSubject;		   		// 融资主体
	private WloanTermDoc 		wloanTermDoc;		  		// 融资档案
	
	// 查询条件
	private List<String> 		stateItem;					// 根据多状态查找传参数
	
	public WloanCurrentProject() {
		super();
	}

	public WloanCurrentProject(String id){
		super(id);
	}

	@Length(min=0, max=255, message="编号长度必须介于 0 和 255 之间")
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@Length(min=0, max=255, message="项目名称长度必须介于 0 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=255, message="融资主体ID长度必须介于 0 和 255 之间")
	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	@Length(min=0, max=255, message="担保机构ID长度必须介于 0 和 255 之间")
	public String getGuaranteeId() {
		return guaranteeId;
	}

	public void setGuaranteeId(String guaranteeId) {
		this.guaranteeId = guaranteeId;
	}
	
	@Length(min=0, max=255, message="融资档案ID长度必须介于 0 和 255 之间")
	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public Double getAmmualRate() {
		return ammualRate;
	}

	public void setAmmualRate(Double ammualRate) {
		this.ammualRate = ammualRate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOnlineDate() {
		return onlineDate;
	}

	public void setOnlineDate(Date onlineDate) {
		this.onlineDate = onlineDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Integer getSpan() {
		return span;
	}

	public void setSpan(Integer span) {
		this.span = span;
	}
	
	public Double getFeeRate() {
		return feeRate;
	}

	public void setFeeRate(Double feeRate) {
		this.feeRate = feeRate;
	}
	
	public Double getCurrentRealAmount() {
		return currentRealAmount;
	}

	public void setCurrentRealAmount(Double currentRealAmount) {
		this.currentRealAmount = currentRealAmount;
	}
	
	@Length(min=0, max=255, message="资金用途长度必须介于 0 和 255 之间")
	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	@Length(min=0, max=2, message="状态（1、草稿，2、审核，3、上线，4、暂定融资，5、结束）长度必须介于 0 和 2 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=0, max=32, message="担保函编号长度必须介于 0 和 32 之间")
	public String getGuaranteeSn() {
		return guaranteeSn;
	}

	public void setGuaranteeSn(String guaranteeSn) {
		this.guaranteeSn = guaranteeSn;
	}
	
	public Double getMarginPercentage() {
		return marginPercentage;
	}

	public void setMarginPercentage(Double marginPercentage) {
		this.marginPercentage = marginPercentage;
	}
	
	@Length(min=0, max=255, message="担保方案长度必须介于 0 和 255 之间")
	public String getGuaranteeScheme() {
		return guaranteeScheme;
	}

	public void setGuaranteeScheme(String guaranteeScheme) {
		this.guaranteeScheme = guaranteeScheme;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public WGuaranteeCompany getWgCompany() {
		return wgCompany;
	}

	public void setWgCompany(WGuaranteeCompany wgCompany) {
		this.wgCompany = wgCompany;
	}

	public WloanSubject getWloanSubject() {
		return wloanSubject;
	}

	public void setWloanSubject(WloanSubject wloanSubject) {
		this.wloanSubject = wloanSubject;
	}

	public WloanTermDoc getWloanTermDoc() {
		return wloanTermDoc;
	}

	public void setWloanTermDoc(WloanTermDoc wloanTermDoc) {
		this.wloanTermDoc = wloanTermDoc;
	}

	public String getContractUrl() {
		return contractUrl;
	}

	public void setContractUrl(String contractUrl) {
		this.contractUrl = contractUrl;
	}

	public List<String> getStateItem() {
		return stateItem;
	}

	public void setStateItem(List<String> stateItem) {
		this.stateItem = stateItem;
	}

	public Double getIsForwardMarginPer() {
		return isForwardMarginPer;
	}

	public void setIsForwardMarginPer(Double isForwardMarginPer) {
		this.isForwardMarginPer = isForwardMarginPer;
	}

	public Double getAlreadyFeerateAmount() {
		return alreadyFeerateAmount;
	}

	public void setAlreadyFeerateAmount(Double alreadyFeerateAmount) {
		this.alreadyFeerateAmount = alreadyFeerateAmount;
	}

}