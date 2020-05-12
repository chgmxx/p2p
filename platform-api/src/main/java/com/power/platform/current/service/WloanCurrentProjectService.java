package com.power.platform.current.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.common.utils.DateUtils;
import com.power.platform.common.utils.IdGen;
import com.power.platform.current.dao.WloanCurrentPoolDao;
import com.power.platform.current.dao.WloanCurrentProjectDao;
import com.power.platform.current.dao.WloanCurrentUserInvestDao;
import com.power.platform.current.dao.invest.WloanCurrentProjectInvestDao;
import com.power.platform.current.dao.moment.WloanCurrentMomentInvestDao;
import com.power.platform.current.entity.WloanCurrentPool;
import com.power.platform.current.entity.WloanCurrentProject;
import com.power.platform.current.entity.WloanCurrentUserInvest;
import com.power.platform.current.entity.invest.WloanCurrentProjectInvest;
import com.power.platform.current.entity.moment.WloanCurrentMomentInvest;
import com.power.platform.current.service.invest.WloanCurrentUserInvestService;
import com.power.platform.current.service.moment.WloanCurrentMomentInvestService;
import com.power.platform.userinfo.service.UserInfoService;

/**
 * 活期融资项目Service
 * @author Mr.Jia
 * @version 2016-01-12
 */
@Service("wloanCurrentProjectService")
@Transactional(readOnly=true)
public class WloanCurrentProjectService extends CrudService<WloanCurrentProject> {
	
	//项目状态常量
	public static final String DRAFT 		= "1"; 		// 草稿
	public static final String CHECKED 		= "2"; 		// 审核
	public static final String ONLINE 		= "3"; 		// 上线
	public static final String PAUSE 		= "4"; 		// 暂停融资
	public static final String FINISH 		= "5"; 		// 结束
	
	@Resource
	private WloanCurrentProjectDao wloanCurrentProjectDao;
	@Resource
	private WloanCurrentPoolDao wloanCurrentPoolDao;
	@Resource
	private WloanCurrentUserInvestDao wloanCurrentUserInvestDao;
	@Resource
	private WloanCurrentMomentInvestDao wloanCurrentMomentInvestDao;
	@Resource
	private WloanCurrentProjectInvestDao wloanCurrentProjectInvestDao;
	@Resource
	private UserInfoService userInfoService;
	
	@Override
	protected CrudDao<WloanCurrentProject> getEntityDao() {
		return wloanCurrentProjectDao;
	}
	
	/**
	 * 更改项目状态（项目审核、上线用）
	 * @param wloanCurrentProject
	 */
	@Transactional(readOnly = false)
	public void updateState(WloanCurrentProject wloanCurrentProject) {
		wloanCurrentProjectDao.update(wloanCurrentProject);
		// 如果是上线项目，将项目融资金额添加进资金池
		if (wloanCurrentProject.getState().equals(WloanCurrentProjectService.ONLINE)) {
			WloanCurrentPool wloanCurrentPool = new WloanCurrentPool();
			List<WloanCurrentPool> list = wloanCurrentPoolDao.findList(wloanCurrentPool);
			if (list != null && list.size() > 0) {
				wloanCurrentPool = list.get(0);
				Double project_amount = wloanCurrentProject.getAmount();
				Double pool_amount = (wloanCurrentPool.getAmount() == null ? 0d : wloanCurrentPool.getAmount());
				Double surPlusAmountDouble = (wloanCurrentPool.getSurplusAmount() == null ? 0d : wloanCurrentPool.getSurplusAmount());
				// 总金额 + 项目金额
				wloanCurrentPool.setAmount( project_amount + pool_amount );
				// 剩余金额 + 项目金额
				wloanCurrentPool.setSurplusAmount( ( surPlusAmountDouble == null ? 0.00 : surPlusAmountDouble ) + project_amount );
				wloanCurrentPoolDao.update(wloanCurrentPool);
			}
		}
	}


	/**
	 * 放款方法
	 * @param wloanCurrentProject
	 * @param repatAmountDouble
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<String> repay(WloanCurrentProject wloanCurrentProject,
			Double repayAmountDouble) throws Exception {
		
		/** 定义两个变量，用来存储中间表（用户投资）拆分时 */
		Double momentInvestAmount = 0d;
		Double userInvestAmount = 0d;
		Double repayAmount = repayAmountDouble;
		
		// 定义一个List集合，用来记录投资记录的ID
		List<String> realInvestID = new ArrayList<String>();
		/**
		 * 放款
		 * 1、 中间表是否有待投资的（有，没有）
		 * 2、投资表查记录
		 * 		金额不足放款金额--> 插入真实投资
		 * 		满足 	--> 切分-- > 插入中间表
		 */
		/* 第一步： 先查找中间表是否有待投资的记录 */
		WloanCurrentMomentInvest wloanCurrentMomentInvest = new WloanCurrentMomentInvest();
		wloanCurrentMomentInvest.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_WAIT);
		List<WloanCurrentMomentInvest> momentInvestList = wloanCurrentMomentInvestDao.findList(wloanCurrentMomentInvest);
		List<String> momentInvestIDs = new ArrayList<String>();
		if ( momentInvestList != null && momentInvestList.size() > 0 ) {
			moment:for (int i = 0; i < momentInvestList.size(); i++) {
				wloanCurrentMomentInvest = momentInvestList.get(i);
				if ( wloanCurrentMomentInvest.getAmount() != null ) {
					/* 判断放款金额是否大于中间表待投资项金额（如果大于取下一条记录，并保存该记录ID） */
					if ( repayAmountDouble >= wloanCurrentMomentInvest.getAmount() ) {
						repayAmountDouble = repayAmountDouble - wloanCurrentMomentInvest.getAmount();
						momentInvestIDs.add(wloanCurrentMomentInvest.getId());
					} else if(repayAmountDouble > 0 && repayAmountDouble < wloanCurrentMomentInvest.getAmount() ) {
						/* 判断放款金额是否大于中间表待投资项金额（如果小于，获得该记录ID，拆分，并插入一条记录，金额为： 该记录金额 - 剩余放款金额） */
						momentInvestIDs.add(wloanCurrentMomentInvest.getId());
						momentInvestAmount = repayAmountDouble;
						/* 将中间表拆分的记录插入中间表  */
						WloanCurrentMomentInvest momentInvestAgain = new WloanCurrentMomentInvest();
						momentInvestAgain.setId(IdGen.uuid());
						momentInvestAgain.setAmount( wloanCurrentMomentInvest.getAmount() - repayAmountDouble );
						momentInvestAgain.setUserid(wloanCurrentMomentInvest.getUserid());
						momentInvestAgain.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_WAIT);
						momentInvestAgain.setUserInvest(wloanCurrentMomentInvest.getUserInvest());
						momentInvestAgain.setVoucherAmount(wloanCurrentMomentInvest.getVoucherAmount());
						momentInvestAgain.setCreateDate(new Date());
						momentInvestAgain.setUpdateDate(new Date());
						wloanCurrentMomentInvestDao.insert(momentInvestAgain);
						repayAmountDouble = 0d;
						i = momentInvestList.size();
					} else {
						break moment;
					}
				}
			}
		}
		
		/* 第二步：中间表记录不够放款金额，从用户投资记录发放放款    */
		List<String> userInvestIDs = new ArrayList<String>();
		WloanCurrentUserInvest wloanCurrentUserInvest = new WloanCurrentUserInvest();
		if ( repayAmountDouble > 0d ) {
			wloanCurrentUserInvest.setState(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_1);
			List<WloanCurrentUserInvest> userInvestList = wloanCurrentUserInvestDao.findList(wloanCurrentUserInvest);
			if ( userInvestList != null && userInvestList.size() > 0 ) {
				userIn:for (int i = 0; i < userInvestList.size(); i++) {
					wloanCurrentUserInvest = userInvestList.get(i);
					if ( wloanCurrentUserInvest.getOnLineAmount() != null ) {
						/* 判断放款金额是否大于用户投资投资项金额（如果大于取下一条记录，并保存该记录ID） */
						if ( repayAmountDouble >= wloanCurrentUserInvest.getOnLineAmount() ) {
							repayAmountDouble = repayAmountDouble - wloanCurrentUserInvest.getOnLineAmount();
							userInvestIDs.add(wloanCurrentUserInvest.getId());
						} else if(repayAmountDouble > 0 && repayAmountDouble < wloanCurrentUserInvest.getOnLineAmount()) {
							/**
							 * 判断放款金额是否大于用户投资投资项金额（如果小于，获得该记录ID，拆分，
							 * 1、并在中间表插入一条记录，金额为： 该记录金额 - 剩余放款金额）
							 * 2、在投资表插入一条记录，金额为剩余放款金额 
							 */
							userInvestIDs.add(wloanCurrentUserInvest.getId());
							userInvestAmount = repayAmountDouble;
							/* 将用户投资记录拆分的记录插入中间表  */
							WloanCurrentMomentInvest momentInvestAgain = new WloanCurrentMomentInvest();
							momentInvestAgain.setId(IdGen.uuid());
							momentInvestAgain.setAmount( wloanCurrentUserInvest.getOnLineAmount() - repayAmountDouble );
							momentInvestAgain.setUserid(wloanCurrentUserInvest.getUserInfo().getId());
							momentInvestAgain.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_WAIT);
							momentInvestAgain.setUserInvest(wloanCurrentUserInvest.getId());
							momentInvestAgain.setVoucherAmount(wloanCurrentUserInvest.getVoucherAmount());
							momentInvestAgain.setCreateDate(new Date());
							momentInvestAgain.setUpdateDate(new Date());
							wloanCurrentMomentInvestDao.insert(momentInvestAgain);
							repayAmountDouble = 0d;
							i = userInvestList.size();
						} else {
							break userIn;
						}
					}
				}
			}
		}
		
		/* 第三步：放款切分完成，进行投资表插入记录，删除中间表已选投资记录 及 用户投资表已选记录   
		 * 	1、如果中间表存在数据、删除中间表数据，并插入投资记录
		 * 	2、如果用户投资表有数据、更改用户投资记录状态，并插入投资记录
		 */
		WloanCurrentProjectInvest wloanCurrentProjectInvest = new WloanCurrentProjectInvest();
		if ( momentInvestIDs != null && momentInvestIDs.size() > 0 ) {
			wloanCurrentMomentInvest = new WloanCurrentMomentInvest();
			for (int i = 0; i < momentInvestIDs.size(); i++) {
				wloanCurrentMomentInvest.setId(momentInvestIDs.get(i));
				wloanCurrentMomentInvest = wloanCurrentMomentInvestDao.get(wloanCurrentMomentInvest);
				/* 对项目投资记录进行赋值操作  */
				wloanCurrentProjectInvest.setId(IdGen.uuid());
				wloanCurrentProjectInvest.setProjectId(wloanCurrentProject.getId());
				wloanCurrentProjectInvest.setUserid(wloanCurrentMomentInvest.getUserid());
				wloanCurrentProjectInvest.setBidDate(new Date());
				wloanCurrentProjectInvest.setVouvherAmount(wloanCurrentMomentInvest.getVoucherAmount());
				wloanCurrentProjectInvest.setUserInvest(wloanCurrentMomentInvest.getUserInvest());
				/* 最后一位进行删除，插入的投资记录金额为：momentInvestAmount */
				if ( momentInvestAmount > 0d && i == momentInvestIDs.size() - 1 ) {
					wloanCurrentProjectInvest.setAmount( momentInvestAmount );
				} else {
					wloanCurrentProjectInvest.setAmount( wloanCurrentMomentInvest.getAmount() );
				}
				wloanCurrentProjectInvestDao.insert(wloanCurrentProjectInvest);
				
				realInvestID.add(wloanCurrentProjectInvest.getId());
				
				wloanCurrentMomentInvest.setState(WloanCurrentMomentInvestService.WLOAN_CURRENT_MOMENT_INVEST_STATE_FINISH);
				wloanCurrentMomentInvestDao.delete(wloanCurrentMomentInvest);
			}
		}
		
		if ( userInvestIDs != null && userInvestIDs.size() > 0 ) {
			wloanCurrentUserInvest = new WloanCurrentUserInvest();
			for (int i = 0; i < userInvestIDs.size(); i++) {
				wloanCurrentUserInvest.setId(userInvestIDs.get(i));
				wloanCurrentUserInvest = wloanCurrentUserInvestDao.get(wloanCurrentUserInvest);
				/* 对项目投资记录进行赋值操作*/
				wloanCurrentProjectInvest.setId(IdGen.uuid());
				wloanCurrentProjectInvest.setProjectId(wloanCurrentProject.getId());
				wloanCurrentProjectInvest.setUserid(wloanCurrentUserInvest.getUserInfo().getId());
				wloanCurrentProjectInvest.setBidDate(new Date());
				wloanCurrentProjectInvest.setVouvherAmount(wloanCurrentUserInvest.getVoucherAmount());
				wloanCurrentProjectInvest.setUserInvest(wloanCurrentUserInvest.getId());
				/* 如果是最后一位，且userInvestAmount > 0 */
				if ( userInvestAmount > 0d && i == userInvestIDs.size() - 1 ) {
					wloanCurrentProjectInvest.setAmount( userInvestAmount );
				} else {
					wloanCurrentProjectInvest.setAmount( wloanCurrentUserInvest.getOnLineAmount() );
				}
				/* 插入投资记录  */
				wloanCurrentProjectInvestDao.insert(wloanCurrentProjectInvest);
				
				realInvestID.add(wloanCurrentProjectInvest.getId());
				
				/* 更改用户投资记录状态  */
				wloanCurrentUserInvest.setState(WloanCurrentUserInvestService.WLOAN_CURRENT_USER_INVEST_STATE_2);
				wloanCurrentUserInvestDao.update(wloanCurrentUserInvest);
			}
		}
		
		// 放款成功之后将项目融资进度 + 放款金额
		wloanCurrentProject.setCurrentRealAmount((wloanCurrentProject.getCurrentRealAmount() == null ? 0d : wloanCurrentProject.getCurrentRealAmount()) + repayAmount );
		wloanCurrentProject.setAlreadyFeerateAmount((wloanCurrentProject.getAlreadyFeerateAmount() == null ? 0d : wloanCurrentProject.getAlreadyFeerateAmount()) + 
					repayAmount * wloanCurrentProject.getFeeRate() /100 );
		wloanCurrentProject.setIsForwardMarginPer((wloanCurrentProject.getIsForwardMarginPer() == null ? 0d : wloanCurrentProject.getIsForwardMarginPer()) +
					repayAmount * wloanCurrentProject.getMarginPercentage() /100 );
		wloanCurrentProjectDao.update(wloanCurrentProject);
		return realInvestID;
	}

	
	/**
	 * 暂停融资
	 * @param wloanCurrentProject
	 * @return
	 */
	@Transactional(readOnly = false)
	public int pauseCurrentInvest(WloanCurrentProject wloanCurrentProject) {
		return wloanCurrentProjectDao.update(wloanCurrentProject);
	}

	
	/**
	 * 债权转入方法
	 * @param wloanCurrentProject {要转给的项目}
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<String> makeOver(WloanCurrentProject wloanCurrentProject) throws Exception{
		
		Double realRepayDouble = 0d;
		// 获取暂停融资项目可转入份额
		WloanCurrentProject wloanCurrentProjectTo = new WloanCurrentProject();
		wloanCurrentProjectTo.setState(WloanCurrentProjectService.PAUSE);
		List<WloanCurrentProject> waitList = wloanCurrentProjectDao.findList(wloanCurrentProjectTo);
		Double waitAmountDouble = 0d;
		if ( waitList != null && waitList.size() > 0 ) {
			for (int i = 0; i < waitList.size(); i++) {
				waitAmountDouble += waitList.get(i).getCurrentRealAmount();
			}
		}
		// 获取该上线项目可转入份额
		Double ableReceiveAmountDouble = wloanCurrentProject.getAmount() - (wloanCurrentProject.getCurrentRealAmount() == null ? 0d : wloanCurrentProject.getCurrentRealAmount());
		
		
		if ( waitAmountDouble >= ableReceiveAmountDouble) {
			realRepayDouble = ableReceiveAmountDouble;
		} else {
			realRepayDouble = waitAmountDouble;
		}
		
		List<String> idsInsert = new ArrayList<String>();
		// 获取暂停融资项目真实投资记录
		if ( waitList != null && waitList.size() > 0 ) {
			WloanCurrentProjectInvest wloanCurrentProjectInvest = new WloanCurrentProjectInvest();
			// 可转让的项目集合
			out:for (int i = 0; i < waitList.size(); i++) {
				wloanCurrentProjectInvest.setProjectId(waitList.get(i).getId());
				List<WloanCurrentProjectInvest> proInvestList =  wloanCurrentProjectInvestDao.findList(wloanCurrentProjectInvest);
				
				if ( proInvestList != null && proInvestList.size() > 0 ) {
					WloanCurrentProject currentProjectOld = new WloanCurrentProject();
					// 遍历可转让项目投资历史记录
					for (int j = 0; j < proInvestList.size(); j++) {
						wloanCurrentProjectInvest = proInvestList.get(j);
						// 转让金额 > 投资记录金额（单条）
						if (realRepayDouble >= wloanCurrentProjectInvest.getAmount() && realRepayDouble > 0) {
							realRepayDouble -= wloanCurrentProjectInvest.getAmount();
							// 删除该条记录，并且插入一条记录
							WloanCurrentProjectInvest wloanCurrentProjectInvestInsert = new WloanCurrentProjectInvest();
							wloanCurrentProjectInvestInsert.setId(IdGen.uuid());
							wloanCurrentProjectInvestInsert.setAmount(wloanCurrentProjectInvest.getAmount());
							wloanCurrentProjectInvestInsert.setProjectId(wloanCurrentProject.getId());
							wloanCurrentProjectInvestInsert.setUserid(wloanCurrentProjectInvest.getUserid());
							wloanCurrentProjectInvestInsert.setVouvherAmount(wloanCurrentProjectInvest.getVouvherAmount());
							// 转入项目结束日期的后一天
							wloanCurrentProjectInvestInsert.setBidDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedDayAfter(DateUtils.formatDate(waitList.get(i).getEndDate(), "yyyy-MM-dd"))));
							wloanCurrentProjectInvestInsert.setUserInvest(wloanCurrentProjectInvest.getUserInvest());
							// 拆入该条转入记录
							wloanCurrentProjectInvestDao.insert(wloanCurrentProjectInvestInsert);
							wloanCurrentProjectInvestDao.delete(wloanCurrentProjectInvest);
							
							// 将金额添加进项目投资记录（新的项目）
							wloanCurrentProject.setCurrentRealAmount( (wloanCurrentProject.getCurrentRealAmount() == null ? 0d : wloanCurrentProject.getCurrentRealAmount()) + wloanCurrentProjectInvestInsert.getAmount() );
							wloanCurrentProject.setUpdateDate(new Date());
							// 扣除保证金、 手续费
							wloanCurrentProject.setAlreadyFeerateAmount(wloanCurrentProject.getAlreadyFeerateAmount() + 
									wloanCurrentProjectInvestInsert.getAmount() * wloanCurrentProject.getFeeRate() / 100);
							wloanCurrentProject.setIsForwardMarginPer(wloanCurrentProject.getIsForwardMarginPer() +
									wloanCurrentProjectInvestInsert.getAmount() * wloanCurrentProject.getMarginPercentage() /100 );
							wloanCurrentProjectDao.update(wloanCurrentProject);
							
							
							// 删除老项目投资进度
							currentProjectOld = wloanCurrentProjectDao.get(wloanCurrentProjectInvest.getProjectId());
							currentProjectOld.setCurrentRealAmount(currentProjectOld.getCurrentRealAmount() - wloanCurrentProjectInvestInsert.getAmount());
							currentProjectOld.setUpdateDate(new Date());
							wloanCurrentProjectDao.update(currentProjectOld);
							
							idsInsert.add(wloanCurrentProjectInvestInsert.getId());
						}
						
						// 转让金额 < 投资记录金额（单条）
						else if ( realRepayDouble < wloanCurrentProjectInvest.getAmount() && realRepayDouble > 0 ) {
							// 删除该条记录，并且插入一条记录
							WloanCurrentProjectInvest wloanCurrentProjectInvestInsert = new WloanCurrentProjectInvest();
							wloanCurrentProjectInvestInsert.setId(IdGen.uuid());
							wloanCurrentProjectInvestInsert.setAmount(realRepayDouble);		// 金额等于剩余转入金额
							wloanCurrentProjectInvestInsert.setProjectId(wloanCurrentProject.getId());
							wloanCurrentProjectInvestInsert.setUserid(wloanCurrentProjectInvest.getUserid());
							wloanCurrentProjectInvestInsert.setVouvherAmount(wloanCurrentProjectInvest.getVouvherAmount());
							// 转入项目结束日期的后一天
							wloanCurrentProjectInvestInsert.setBidDate(DateUtils.getShortDateOfString(DateUtils.getSpecifiedDayAfter(DateUtils.formatDate(waitList.get(i).getEndDate(), "yyyy-MM-dd"))));
							wloanCurrentProjectInvestInsert.setUserInvest(wloanCurrentProjectInvest.getUserInvest());
							wloanCurrentProjectInvestDao.insert(wloanCurrentProjectInvestInsert);
							
							// 改变原投资记录金额为 （原投资金额 - 剩余转让金额）
							wloanCurrentProjectInvest.setAmount(wloanCurrentProjectInvest.getAmount() - realRepayDouble);
							wloanCurrentProjectInvestDao.update(wloanCurrentProjectInvest);
							
							// 将金额添加进项目投资记录（新的项目）
							wloanCurrentProject.setCurrentRealAmount( (wloanCurrentProject.getCurrentRealAmount() == null ? 0d : wloanCurrentProject.getCurrentRealAmount()) + wloanCurrentProjectInvestInsert.getAmount() );
							wloanCurrentProject.setUpdateDate(new Date());
							// 扣除保证金、 手续费
							wloanCurrentProject.setAlreadyFeerateAmount(wloanCurrentProject.getAlreadyFeerateAmount() + 
									wloanCurrentProjectInvestInsert.getAmount() * wloanCurrentProject.getFeeRate() / 100);
							wloanCurrentProject.setIsForwardMarginPer(wloanCurrentProject.getIsForwardMarginPer() +
									wloanCurrentProjectInvestInsert.getAmount() * wloanCurrentProject.getMarginPercentage() /100 );
							wloanCurrentProjectDao.update(wloanCurrentProject);
							
							
							// 删除老项目投资进度
							currentProjectOld = wloanCurrentProjectDao.get(wloanCurrentProjectInvest.getProjectId());
							currentProjectOld.setCurrentRealAmount(currentProjectOld.getCurrentRealAmount() - wloanCurrentProjectInvestInsert.getAmount());
							currentProjectOld.setUpdateDate(new Date());
							wloanCurrentProjectDao.update(currentProjectOld);
							
							idsInsert.add(wloanCurrentProjectInvestInsert.getId());
						}
						
						// realRepayDouble == 0 返回存储ID的集合
						else{
							// 终止循环;执行其他动作
							break out;
						}
					}
				} else {
					throw new Exception("暂停融资的项目没有可转让的份额");
				}
			}
			
			// 如果暂停项目的currentAmount == 0,则结束项目
			WloanCurrentPool currentPool = new WloanCurrentPool();
			List<WloanCurrentPool> poolList = wloanCurrentPoolDao.findList(currentPool);
			
			for (int j = 0; j < waitList.size(); j++) {
				wloanCurrentProjectTo = wloanCurrentProjectDao.get(waitList.get(j).getId());
				if (wloanCurrentProjectTo.getCurrentRealAmount() == 0) {
					/**
					 * 结束项目
					 * 1、将项目状态改为结束
					 * 2、将资金池总额、剩余金额删除该项目金额
					 */
					wloanCurrentProjectTo.setState(WloanCurrentProjectService.FINISH);
					wloanCurrentProjectDao.update(wloanCurrentProjectTo);
					
					// 改变资金池总金额、剩余金额
					currentPool = poolList.get(0);
					currentPool.setAmount(currentPool.getAmount() - wloanCurrentProjectTo.getAmount());
					currentPool.setSurplusAmount(currentPool.getSurplusAmount() - wloanCurrentProjectTo.getAmount());
					wloanCurrentPoolDao.update(currentPool);
				}
			}
		}
		
		if ( idsInsert != null && idsInsert.size() > 0 ) {
			return idsInsert;
		} else {
			return null;		
		}
		
	}
	
}