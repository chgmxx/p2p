package com.power.platform.credit.service.apply;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.Page;
import com.power.platform.common.service.CrudService;
import com.power.platform.credit.dao.apply.CreditUserApplyDao;
import com.power.platform.credit.dao.info.CreditInfoDao;
import com.power.platform.credit.entity.apply.CreditUserApply;
import com.power.platform.credit.entity.info.CreditInfo;

/**
 * 
 * 类: CreditUserApplyService <br>
 * 描述: 借款人，借款申请. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年6月15日 上午10:17:32
 * @param <T>
 */
@Service("creditUserApplyService")
public class CreditUserApplyService extends CrudService<CreditUserApply> {

	/**
	 * 草稿.
	 */
	public static final String CREDIT_USER_APPLY_STATE_0 = "0";
	/**
	 * 申请中（审核中）.
	 */
	public static final String CREDIT_USER_APPLY_STATE_1 = "1";
	/**
	 * 申请通过（审核通过）.
	 */
	public static final String CREDIT_USER_APPLY_STATE_2 = "2";
	/**
	 * 申请驳回（审核驳回）.
	 */
	public static final String CREDIT_USER_APPLY_STATE_3 = "3";
	/**
	 * 融资中.
	 */
	public static final String CREDIT_USER_APPLY_STATE_4 = "4";
	/**
	 * 还款中.
	 */
	public static final String CREDIT_USER_APPLY_STATE_5 = "5";
	/**
	 * 申请结束.
	 */
	public static final String CREDIT_USER_APPLY_STATE_6 = "6";
	
	//订单融资申请步骤
	//第一步完成（融资类型）
	public static final String CREDIT_USER_APPLY_STEP_1 = "1";
	//第二步完成（选择采购方）
	public static final String CREDIT_USER_APPLY_STEP_2 = "2";
	//第三步完成（基础交易）
	public static final String CREDIT_USER_APPLY_STEP_3 = "3";
	//第四步完成（上传资料）
	public static final String CREDIT_USER_APPLY_STEP_4 = "4";
	//第五步完成（融资申请）
	public static final String CREDIT_USER_APPLY_STEP_5 = "5";
	//第六步完成（担保函）
	public static final String CREDIT_USER_APPLY_STEP_6 = "6";
	//第七步完成（签订协议）
	public static final String CREDIT_USER_APPLY_STEP_7 = "7";
	
	//发票状态
	public static final String VOUCHER_STATE1 = "1";//申请中
	public static final String VOUCHER_STATE2 = "2";//审核通过
	
	//申请类型
	public static final String CREDIT_FINANCING_TYPE_1 = "1";//应收账款张让
	public static final String CREDIT_FINANCING_TYPE_2 = "2";//订单融资

	@Resource
	private CreditUserApplyDao creditUserApplyDao;
	@Resource
	private CreditInfoDao creditInfoDao;

	@Override
	protected CrudDao<CreditUserApply> getEntityDao() {

		return creditUserApplyDao;
	}

	/**
	 * 
	 * 方法: insertCreditUserApply <br>
	 * 描述: 新增借款人借款申请. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年6月15日 上午10:27:08
	 * 
	 * @param creditUserApply
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertCreditUserApply(CreditUserApply creditUserApply) {

		// 创建日期.
		creditUserApply.setCreateDate(new Date());
		// 更新日期.
		creditUserApply.setUpdateDate(new Date());
		// 备注.
		creditUserApply.setRemarks("核心企业【借款申请】");
		// 状态.
		creditUserApply.setState(CREDIT_USER_APPLY_STATE_1);

		int flag = 0;
		try {
			flag = creditUserApplyDao.insert(creditUserApply);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("fn:insertCreditUserApply,{" + e.getMessage() + "}");
		}
		return flag;
	}

	/**
	 * 借款端ERP申请详情
	 * @param userApplyId
	 * @return
	 */
	public CreditUserApply findApplyById(String userApplyId) {
		// TODO Auto-generated method stub
		return creditUserApplyDao.findApplyById(userApplyId);
	}

	/**
	 * 更新借款申请编号
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateCreditApplyName() {
		// TODO Auto-generated method stub
		CreditUserApply userApply = new CreditUserApply();
		List<CreditUserApply> list = creditUserApplyDao.findList(userApply);
		if(list!=null && list.size()>0){
			for (CreditUserApply creditUserApply : list) {
				String projectDataId = creditUserApply.getProjectDataId();
				CreditInfo creditInfo = creditInfoDao.get(projectDataId);
				if(creditInfo.getName()!=null){
					creditUserApply.setCreditSupplyId(creditInfo.getCreditUserId());
					creditUserApply.setCreditApplyName(creditInfo.getName());
					creditUserApplyDao.update(creditUserApply);
				}
			}
		}
	}
	/**
	 * 查询申请开票列表
	 */
//	@Transactional(readOnly = false, rollbackFor = Exception.class)
//	public List<CreditUserApply> findVoucherApplyList(String voucherState) {
//		// TODO Auto-generated method stub
//		return creditUserApplyDao.findVoucherApplyList(voucherState);
//	}
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Page<CreditUserApply> findVoucherApplyPage(Page<CreditUserApply> page, CreditUserApply entity) {
		// TODO Auto-generated method stub
		entity.setPage(page);
		page.setList(creditUserApplyDao.findVoucherApplyList(entity));
		return page;
	}
	
	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public List<CreditUserApply> findListForAgreement(String supplyId) {
		// TODO Auto-generated method stub
		List<CreditUserApply> list = creditUserApplyDao.findListForAgreement(supplyId);
		return list;
	}

}