package com.power.platform.regular.entity;

import java.util.Date;

import com.power.platform.common.persistence.DataEntity;
import com.power.platform.common.utils.IdGen;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.entity.Dict;
import com.power.platform.sys.entity.User;

/**
 * 担保机构
 * @author lc
 *
 */
public class WGuaranteeCompany extends DataEntity<WGuaranteeCompany>{
	
	
	private static final long serialVersionUID = 1L;
	
    private String name;
    private String briefName;
    private String locus;
    private Integer industry;
    private Date registerDate;
    private String businessNo;
    private String organNo;
    private String taxCode;
    private String briefInfo;
    private String webSite;
    private String corporation;	//法人代表
    private String guaranteeScheme;
    private String guaranteeCase;
    private Integer registerAmount;
    private Integer netAssetAmount;
    private Integer lastYearCash;
    private String runCase;
    private String wguaranteeLogo;
    private String electronicSignUrl;
    private String address;		//住址	
    private String phone;		//电话
    private User user;
    private Area area;		// 注册地址
    private Dict dict;        //字典类
    
    
    public WGuaranteeCompany(){
	}
	public WGuaranteeCompany(String id){
		this.id = id;
	}
	
    public void prePersist(){
		this.id = IdGen.uuid();
		this.createDate = new Date();
	}
    
    
	public Dict getDict() {
		return dict;
	}
	public void setDict(Dict dict) {
		this.dict = dict;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBriefName() {
		return briefName;
	}
	public void setBriefName(String briefName) {
		this.briefName = briefName;
	}
	public String getLocus() {
		return locus;
	}
	public void setLocus(String locus) {
		this.locus = locus;
	}
	public Integer getIndustry() {
		return industry;
	}
	public void setIndustry(Integer industry) {
		this.industry = industry;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getOrganNo() {
		return organNo;
	}
	public void setOrganNo(String organNo) {
		this.organNo = organNo;
	}
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getBriefInfo() {
		return briefInfo;
	}
	public void setBriefInfo(String briefInfo) {
		this.briefInfo = briefInfo;
	}
	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public String getCorporation() {
		return corporation;
	}
	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}
	public String getGuaranteeScheme() {
		return guaranteeScheme;
	}
	public void setGuaranteeScheme(String guaranteeScheme) {
		this.guaranteeScheme = guaranteeScheme;
	}
	public String getGuaranteeCase() {
		return guaranteeCase;
	}
	public void setGuaranteeCase(String guaranteeCase) {
		this.guaranteeCase = guaranteeCase;
	}
	public Integer getRegisterAmount() {
		return registerAmount;
	}
	public void setRegisterAmount(Integer registerAmount) {
		this.registerAmount = registerAmount;
	}
	public Integer getNetAssetAmount() {
		return netAssetAmount;
	}
	public void setNetAssetAmount(Integer netAssetAmount) {
		this.netAssetAmount = netAssetAmount;
	}
	public Integer getLastYearCash() {
		return lastYearCash;
	}
	public void setLastYearCash(Integer lastYearCash) {
		this.lastYearCash = lastYearCash;
	}
	public String getRunCase() {
		return runCase;
	}
	public void setRunCase(String runCase) {
		this.runCase = runCase;
	}
	public String getWguaranteeLogo() {
		return wguaranteeLogo;
	}
	public void setWguaranteeLogo(String wguaranteeLogo) {
		this.wguaranteeLogo = wguaranteeLogo;
	}
	public String getElectronicSignUrl() {
		return electronicSignUrl;
	}
	public void setElectronicSignUrl(String electronicSignUrl) {
		this.electronicSignUrl = electronicSignUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
    
    
    
    
}