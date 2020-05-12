package com.power.platform.credit.service.userinfo;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.supplierToMiddlemen.CreditSupplierToMiddlemenDao;
import com.power.platform.credit.dao.userinfo.CreditUserInfoDao;
import com.power.platform.credit.entity.supplierToMiddlemen.CreditSupplierToMiddlemen;
import com.power.platform.credit.entity.userinfo.CreditUserInfo;
import com.power.platform.credit.entity.userinfo.CreditUserInfoDto;

/**
 * 信贷用户Service
 * 
 * @author nice
 * @version 2017-03-22
 */
@Service
public class CreditUserInfoService extends CrudService<CreditUserInfo> {

	@Resource
	private CreditUserInfoDao creditUserInfoDao;
	@Autowired
	private CreditSupplierToMiddlemenDao creditSupplierToMiddlemenDao;
	
	/**
	 * 项目类型
	 */
	public static final String ACCOUNT_TYPE1 = "1"; //安心投
	public static final String ACCOUNT_TYPE2 = "2"; //供应链
	
	@Override
	protected CrudDao<CreditUserInfo> getEntityDao() {

		// TODO Auto-generated method stub
		return creditUserInfoDao;
	}
	
	public Page<CreditUserInfo> findPage(Page<CreditUserInfo> page, CreditUserInfo creditUserInfo) {
		return super.findPage(page, creditUserInfo);
	}

	public Page<CreditUserInfo> findPageByAnnexFile(Page<CreditUserInfo> page,
			CreditUserInfo creditUser) {
		creditUser.setPage(page);
		page.setList(creditUserInfoDao.findPageByAnnexFile(creditUser));
		return page;
	}

	/**
	 * 更新生产环境借款人信息字段
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateCreditUserInfo() {
		// TODO Auto-generated method stub
		List<CreditUserInfo> list = creditUserInfoDao.findAllList();
		if(list!=null){
			for (CreditUserInfo creditUserInfo : list) {
				String userId = creditUserInfo.getId();
				CreditSupplierToMiddlemen supplierToMiddlemen = new CreditSupplierToMiddlemen();
				supplierToMiddlemen.setSupplierId(userId);
				List<CreditSupplierToMiddlemen> sList = creditSupplierToMiddlemenDao.findList(supplierToMiddlemen);
				System.out.println("##############根据供应商ID["+userId+"]查询结果数"+sList.size());
				logger.info("根据供应商ID查询结果数"+sList.size());
				if(sList!=null&&sList.size()>0){
					for (CreditSupplierToMiddlemen creditSupplierToMiddlemen : sList) {
						String supplierId = creditSupplierToMiddlemen.getSupplierId();
						String middlemenId = creditSupplierToMiddlemen.getMiddlemenId();
						System.out.println("供应商ID="+supplierId+"&&核心企业ID="+middlemenId);
						if(supplierId.equals(middlemenId)){
							System.out.println("房产抵押");
							creditUserInfo.setAccountType(ACCOUNT_TYPE1);
							creditUserInfo.setOwnedCompany("房产抵押");
						}else{
							CreditUserInfo cInfo = creditUserInfoDao.get(middlemenId);
							String enterpriseFullName = "";
							if(cInfo!=null){
								enterpriseFullName = cInfo.getEnterpriseFullName();
							}
							System.out.println("供应链"+enterpriseFullName);
							creditUserInfo.setAccountType(ACCOUNT_TYPE2);
							creditUserInfo.setOwnedCompany(enterpriseFullName);
						}
						int i = creditUserInfoDao.update(creditUserInfo);
						if(i>0){
							System.out.println("供应商"+creditUserInfo.getId()+"字段更新成功");
							logger.info("供应商"+creditUserInfo.getId()+"字段更新成功");
						}
					}
				}else{
					System.out.println("核心企业"+creditUserInfo.getEnterpriseFullName());
					creditUserInfo.setAccountType(ACCOUNT_TYPE2);
					creditUserInfo.setOwnedCompany(creditUserInfo.getEnterpriseFullName());
					int i = creditUserInfoDao.update(creditUserInfo);
					if(i>0){
						System.out.println("核心企业"+creditUserInfo.getId()+"字段更新成功");
						logger.info("核心企业"+creditUserInfo.getId()+"字段更新成功");
					}
				}
			}
		}
		
	}

	/**
	 * @Description:JBXT-借款用户信息列表
	 */
	public List<CreditUserInfoDto> findCreditUserInfo(CreditUserInfoDto creditUserInfo) {
		return creditUserInfoDao.findCreditUserInfo(creditUserInfo);
	}

}