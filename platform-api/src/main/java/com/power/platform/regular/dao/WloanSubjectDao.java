package com.power.platform.regular.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.regular.entity.WloanSubject;

/**
 * 
 * 类: WloanSubjectDao <br>
 * 描述: 融资主体DAO接口. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2015年12月29日 上午11:35:56
 */
@MyBatisDao
public interface WloanSubjectDao extends CrudDao<WloanSubject> {

	/**
	 * 
	 * 方法: findLoanUserTotalPrincipalAndInterestAmount <br>
	 * 描述: 在贷本息余额. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月24日 下午2:28:46
	 * 
	 * @return
	 */
	Double findLoanUserTotalPrincipalAndInterestAmount();
	// 根据月份查询 在贷本息余额
	Double findLoanUserTotalPrincipalAndInterestAmountByMonth(@Param("endTime") String endTime);

	/**
	 * 
	 * 方法: getByLoanApplyId <br>
	 * 描述: 根据借款人ID获取融资主体. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年5月11日 下午4:55:49
	 * 
	 * @param loanApplyId
	 * @return
	 */
	List<WloanSubject> getByLoanApplyId(@Param("loanApplyId") String loanApplyId);

	/**
	 * 
	 * 方法: findLoanUserStayStillTotalAmount <br>
	 * 描述: 借款人，待还总额. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月3日 下午2:13:50
	 * 
	 * @param wloanSubjectId
	 * @return
	 */
	Double findLoanUserStayStillTotalAmount(@Param("wloanSubjectId") String wloanSubjectId);

	/**
	 * 
	 * 方法: findLoanUserTotalAmountList <br>
	 * 描述: 融资主体为借款人，借款人借款总金额列表. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月3日 下午2:43:16
	 * 
	 * @return
	 */
	List<WloanSubject> findLoanUserTotalAmountList();

	/**
	 * 
	 * 方法: findLoanUserTotalPrincipalAmountList <br>
	 * 描述: 在贷本金列表. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年8月22日 下午3:42:36
	 * 
	 * @return
	 */
	List<WloanSubject> findLoanUserTotalPrincipalAmountList();

	/**
	 * 
	 * 方法: findNowLoanUserTotalNumbers <br>
	 * 描述: 融资主体为借款人，查询平台当前借款人数量. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月3日 下午1:20:01
	 * 
	 * @return
	 */
	List<String> findNowLoanUserTotalNumbers();

	/**
	 * 
	 * 方法: findLoanUserTotalNumbers <br>
	 * 描述: 融资主体为借款人，查询平台全部借款人数量. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2018年4月3日 下午1:15:29
	 * 
	 * @return
	 */
	List<String> findLoanUserTotalNumbers();

	/**
	 * 
	 * 方法: isExistWloanSubjectAndWloanTermProject <br>
	 * 描述: 当前融资主体是否被融资项目使用，用于删除判断. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2016年1月11日 上午11:30:31
	 * 
	 * @param entity
	 * @return
	 */
	public abstract List<WloanSubject> isExistWloanSubjectAndWloanTermProject(WloanSubject entity);

	// 供应商销户
	public void deleteWloanSubjectByUserId(String userId);
	
	
	// 融资主体为借款人，根据月份查询平台所有借款人数量.
	List<String> findLoanUserTotalNumbersByMonth(@Param("endTime") String endTime);
	
	// 中登网登记查询出质人信息
	WloanSubject getSubject(@Param("subjectId")String subjectId);


	

}