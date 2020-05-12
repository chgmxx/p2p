/**
 */
package com.power.platform.riskmanagement.entity;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.platform.common.persistence.DataEntity;
import com.power.platform.sys.entity.User;


/**
 * 风控企业信息Entity
 * @author yb
 * @version 2016-10-11
 */
public class RiskManagement extends DataEntity<RiskManagement> {
	
	private static final long serialVersionUID = 1L;
	private String companyName;		// 企业名称
	private String state;		// 审批状态
	private String checkUser1;		// 审批人
	private Date checkDate1;		// 审批时间
	private String checkNote1;		// 审批意见
	private String checkUser2;		// check_user2
	private Date checkDate2;		// check_date2
	private String checkNote2;		// check_note2
	private String checkUser3;		// check_user3
	private Date checkDate3;		// check_date3
	private String checkNote3;		// check_note3
	private String checkNote;
	private String docUrl;
	
	public RiskManagement() {
		super();
	}

	public RiskManagement(String id){
		super(id);
	}

	@Length(min=0, max=255, message="企业名称长度必须介于 0 和 255 之间")
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	@Length(min=0, max=1, message="审批状态长度必须介于 0 和 1 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getCheckUser1() {
		return checkUser1;
	}

	public void setCheckUser1(String checkUser1) {
		this.checkUser1 = checkUser1;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCheckDate1() {
		return checkDate1;
	}

	public void setCheckDate1(Date checkDate1) {
		this.checkDate1 = checkDate1;
	}
	
	@Length(min=0, max=255, message="审批意见长度必须介于 0 和 255 之间")
	public String getCheckNote1() {
		return checkNote1;
	}

	public void setCheckNote1(String checkNote1) {
		this.checkNote1 = checkNote1;
	}
	
	public String getCheckUser2() {
		return checkUser2;
	}

	public void setCheckUser2(String checkUser2) {
		this.checkUser2 = checkUser2;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCheckDate2() {
		return checkDate2;
	}

	public void setCheckDate2(Date checkDate2) {
		this.checkDate2 = checkDate2;
	}
	
	@Length(min=0, max=64, message="check_note2长度必须介于 0 和 64 之间")
	public String getCheckNote2() {
		return checkNote2;
	}

	public void setCheckNote2(String checkNote2) {
		this.checkNote2 = checkNote2;
	}
	
	public String getCheckUser3() {
		return checkUser3;
	}

	public void setCheckUser3(String checkUser3) {
		this.checkUser3 = checkUser3;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCheckDate3() {
		return checkDate3;
	}

	public void setCheckDate3(Date checkDate3) {
		this.checkDate3 = checkDate3;
	}
	
	@Length(min=0, max=64, message="check_note3长度必须介于 0 和 64 之间")
	public String getCheckNote3() {
		return checkNote3;
	}

	public void setCheckNote3(String checkNote3) {
		this.checkNote3 = checkNote3;
	}

	public String getCheckNote() {
		return checkNote;
	}

	public void setCheckNote(String checkNote) {
		this.checkNote = checkNote;
	}

	public String getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	
	
	
}