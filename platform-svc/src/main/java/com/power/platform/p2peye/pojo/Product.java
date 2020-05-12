package com.power.platform.p2peye.pojo;

import java.io.Serializable;

/**
 * 
 * 类: Product <br>
 * 描述: 产品. <br>
 * 作者: Mr.云.李 <br>
 * 时间: 2017年9月8日 下午4:40:32
 */
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	// 标的唯一编号(不为空,很重要).
	private String id;
	// 借款标的URL链接.
	private String url;
	// 平台中文名称.
	private String platform_name;
	// 标的标题信息.
	private String title;
	// 借款人(发标人)的用户名称.如果没有借款人用户名,一定要返回下面的 userId，用户名不能为空.
	private String username;
	// 0,正在投标中的借款标;1,已完成(包括还款中和已完成的借款标).
	private String status;
	// 发标人的用户编号/ID.
	private String userid;
	// 借款类型.
	// 0 代表信用标;
	// 1 担保标;
	// 2 抵押,质押标;
	// 3 秒标;
	// 4 债权转让标(流转标,二级市场标的);
	// 5 理财计划(宝类业务_活期);
	// 6 其它;
	// 7 净值标;
	// 8 活动标(体验标);
	// 9 理财计划(宝类业务_定期);
	// 3 4 5
	// 标类型不参与贷款余额计算;请注意5【理财计划(宝类业务_活期)】和9【理财计划(宝类业务_定期)】的区分;4债权转让标指的是不会产生新待还的转让，如果会产生新待还，请返回其他标类型.
	private String c_type;
	// 借款金额以元为单位,精度2位(1000.00),如万元请转换为元,请过滤掉借款金额小于50块的标.
	private String amount;
	// 借款年利率如果为月利率或天利率,统一转换为年利率并使用小数表示;精度4位,如:0.0910.
	private String rate;
	// 借款期限的数字。如3月这里只返回3若借款标的为流转标,对应的要有流转期限.
	private String period;
	// 期限类型0 代表天,1 代表月.
	private String p_type;
	// 还款方式.
	// 0 代表其他;
	// 1 按月等额本息还款;
	// 2 按月付息,到期还本;
	// 3 按天计息,一次性还本付息;
	// 4 按月计息,一次性还本付息;
	// 5 按季分期还款;
	// 6 为等额本金,按月还本金;
	// 7 先息期本;
	// 8 按季付息,到期还本;
	// 9 按半年付息,到期还本;
	// 10 按年付息，到期还本.
	private String pay_way;
	// 完成百分比转换成小数表示(保留1位小数).
	private String process;
	// 投标奖励如奖励为百分比,转换成小数表示(保留2位小数)No.
	private String reward;
	// 担保奖励如奖励为百分比,转换成小数表示(保留2位小数)No.
	private String guarantee;
	// 标的创建时间时间格式要求如:2013-08-10 14:24:01(24小时制).
	private String start_time;
	// 满标时间(标的放款时间，标的起息时间)时间格式要求如:2013-08-10 13:10:00;
	// 标的放款时间，标的起息时间，如果没有起息时间，请提供投资记录最后一笔的投资时间，请不要理解为标最后的的还款完成日期.
	// status 0:No 1:Yes.
	private String end_time;
	// 投资次数这笔借款标有多少个投标记录.
	private String invest_num;
	// 续投奖励继续投标的奖励No.
	private String c_reward;

	// 1.注意字段名称数据类型对照，且不可写错（区分大小）;
	// 2.所有浮点数只保留原始值,百分比的都转小数表示如：12.45% 转 0.1245 表示;
	// 3.若某个字段信息并不存在或者没有，字符串或对象类型请返还 null，数值类型返回 0;
	// 4.没有标时返回:{"result_code":"-1","result_msg":"未授权的访问!","page_count":"0","page_index":"0","loans":null};
	// 5.几种还款方式说明：
	// 0 代表其他 ;
	// 1 按月等额本息还款：每个月会还部分本金和利息;
	// 2 按月付息，到期还本：每个月会还部分利息，到期之后还本金和剩下的利息;
	// 3 按天计息，一次性还本付息：天标的一次性还款;
	// 4 按月计息，一次性还本付息：月标的一次性还款，但每个月不用还利息;
	// 5 按季分期还款：三个月内的每月还利息，季度的时候还本金和部分利息;
	// 6 等额本金，按月还本金;
	// 7 先息期本;
	// 8 按季付息,到期还本;
	// 9 按半年付息,到期还本;
	// 10 按年付息，到期还本;
	// 注意：部分平台若存在其他还款方式，我们未列出的，请和我们的技术联系，帮助完善;

	public String getId() {

		return id;
	}

	/**
	 * 
	 * 方法: setId <br>
	 * 描述: 标的唯一编号(不为空,很重要). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午9:21:08
	 * 
	 * @param id
	 */
	public void setId(String id) {

		this.id = id;
	}

	public String getUrl() {

		return url;
	}

	/**
	 * 
	 * 方法: setUrl <br>
	 * 描述: 借款标的URL链接. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午10:02:10
	 * 
	 * @param url
	 */
	public void setUrl(String url) {

		this.url = url;
	}

	public String getPlatform_name() {

		return platform_name;
	}

	/**
	 * 
	 * 方法: setPlatform_name <br>
	 * 描述: 平台中文名称. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午10:31:05
	 * 
	 * @param platform_name
	 */
	public void setPlatform_name(String platform_name) {

		this.platform_name = platform_name;
	}

	public String getTitle() {

		return title;
	}

	/**
	 * 
	 * 方法: setTitle <br>
	 * 描述: 标的标题信息. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午11:25:51
	 * 
	 * @param title
	 */
	public void setTitle(String title) {

		this.title = title;
	}

	public String getUsername() {

		return username;
	}

	/**
	 * 
	 * 方法: setUsername <br>
	 * 描述: 借款人(发标人)的用户名称.如果没有借款人用户名,一定要返回下面的 userId，用户名不能为空. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午11:35:28
	 * 
	 * @param username
	 */
	public void setUsername(String username) {

		this.username = username;
	}

	public String getStatus() {

		return status;
	}

	/**
	 * 
	 * 方法: setStatus <br>
	 * 描述: 0,正在投标中的借款标;1,已完成(包括还款中和已完成的借款标). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午11:37:11
	 * 
	 * @param status
	 */
	public void setStatus(String status) {

		this.status = status;
	}

	public String getUserid() {

		return userid;
	}

	/**
	 * 
	 * 方法: setUserid <br>
	 * 描述: 发标人的用户编号/ID. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 上午11:48:54
	 * 
	 * @param userid
	 */
	public void setUserid(String userid) {

		this.userid = userid;
	}

	public String getC_type() {

		return c_type;
	}

	/**
	 * 
	 * 方法: setC_type <br>
	 * 描述: 2:抵押质押标. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午12:01:27
	 * 
	 * @param c_type
	 */
	public void setC_type(String c_type) {

		this.c_type = c_type;
	}

	public String getAmount() {

		return amount;
	}

	/**
	 * 
	 * 方法: setAmount <br>
	 * 描述: 借款金额以元为单位,精度2位(1000.00),如万元请转换为元,请过滤掉借款金额小于50块的标. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午2:08:45
	 * 
	 * @param amount
	 */
	public void setAmount(String amount) {

		this.amount = amount;
	}

	public String getRate() {

		return rate;
	}

	/**
	 * 
	 * 方法: setRate <br>
	 * 描述: 借款年利率如果为月利率或天利率,统一转换为年利率并使用小数表示;精度4位,如:0.0910. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午3:58:03
	 * 
	 * @param rate
	 */
	public void setRate(String rate) {

		this.rate = rate;
	}

	public String getPeriod() {

		return period;
	}

	/**
	 * 
	 * 方法: setPeriod <br>
	 * 描述: 借款期限的数字。如3月这里只返回3若借款标的为流转标,对应的要有流转期限. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午4:22:38
	 * 
	 * @param period
	 */
	public void setPeriod(String period) {

		this.period = period;
	}

	public String getP_type() {

		return p_type;
	}

	/**
	 * 
	 * 方法: setP_type <br>
	 * 描述: 期限类型0 代表天,1 代表月 <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午4:29:02
	 * 
	 * @param p_type
	 */
	public void setP_type(String p_type) {

		this.p_type = p_type;
	}

	public String getPay_way() {

		return pay_way;
	}

	/**
	 * 
	 * 方法: setPay_way <br>
	 * 描述: 还款方式. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午4:36:15
	 * 
	 * @param pay_way
	 */
	public void setPay_way(String pay_way) {

		this.pay_way = pay_way;
	}

	public String getProcess() {

		return process;
	}

	/**
	 * 
	 * 方法: setProcess <br>
	 * 描述: 完成百分比转换成小数表示(保留1位小数). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午5:09:38
	 * 
	 * @param process
	 */
	public void setProcess(String process) {

		this.process = process;
	}

	public String getReward() {

		return reward;
	}

	/**
	 * 
	 * 方法: setReward <br>
	 * 描述: 投标奖励如奖励为百分比,转换成小数表示(保留2位小数)No. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午4:55:50
	 * 
	 * @param reward
	 */
	public void setReward(String reward) {

		this.reward = reward;
	}

	public String getGuarantee() {

		return guarantee;
	}

	/**
	 * 
	 * 方法: setGuarantee <br>
	 * 描述: 担保奖励如奖励为百分比,转换成小数表示(保留2位小数)No. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午4:56:12
	 * 
	 * @param guarantee
	 */
	public void setGuarantee(String guarantee) {

		this.guarantee = guarantee;
	}

	public String getStart_time() {

		return start_time;
	}

	/**
	 * 
	 * 方法: setStart_time <br>
	 * 描述: 标的创建时间时间格式要求如:2013-08-10 14:24:01(24小时制). <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午5:26:22
	 * 
	 * @param start_time
	 */
	public void setStart_time(String start_time) {

		this.start_time = start_time;
	}

	public String getEnd_time() {

		return end_time;
	}

	/**
	 * 
	 * 方法: setEnd_time <br>
	 * 描述: 满标时间(标的放款时间，标的起息时间)时间格式要求如:2013-08-10 13:10:00. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午5:26:50
	 * 
	 * @param end_time
	 */
	public void setEnd_time(String end_time) {

		this.end_time = end_time;
	}

	public String getInvest_num() {

		return invest_num;
	}

	/**
	 * 
	 * 方法: setInvest_num <br>
	 * 描述: 投资次数这笔借款标有多少个投标记录. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午5:27:52
	 * 
	 * @param invest_num
	 */
	public void setInvest_num(String invest_num) {

		this.invest_num = invest_num;
	}

	public String getC_reward() {

		return c_reward;
	}

	/**
	 * 
	 * 方法: setC_reward <br>
	 * 描述: 续投奖励继续投标的奖励No. <br>
	 * 作者: Mr.云.李 <br>
	 * 时间: 2017年9月12日 下午5:20:05
	 * 
	 * @param c_reward
	 */
	public void setC_reward(String c_reward) {

		this.c_reward = c_reward;
	}

}
