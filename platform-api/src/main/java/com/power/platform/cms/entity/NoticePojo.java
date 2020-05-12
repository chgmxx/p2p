package com.power.platform.cms.entity;

import java.util.Date;

import com.power.platform.common.utils.DateUtils;

public class NoticePojo {

	/**
	 * 还本付息.
	 */
	private static final String REPAY_TYPE_1 = "1";

	/**
	 * 付息.
	 */
	private static final String REPAY_TYPE_2 = "2";

	/**
	 * 
	 * 方法: createNoticeContent <br>
	 * 描述: 创建还款公告内容. <br>
	 * 作者: Mr.Roy <br>
	 * 时间: 2018年6月19日 上午9:57:16
	 * 
	 * @param type
	 * @param projectName
	 * @param projectSn
	 * @param repayAmount
	 * @return
	 */
	public static String createNoticeContent(String type, String projectName, String projectSn, String repayAmount) {

		StringBuffer notice_sb = new StringBuffer();

		String text_a_str = "&lt;p&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
		String text_b_str = "尊敬的出借人，您好：";
		String text_c_str = "&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
		String text_d_str = "项目名称：&lt;/span&gt;";
		String text_e_str = projectName;
		String text_f_str = "&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;（项目编号：&lt;/span&gt;&lt;span style=&quot;color: rgb(60, 60, 60);&quot;&gt;";
		String text_g_str = projectSn;
		notice_sb.append(text_a_str);
		notice_sb.append(text_b_str);
		notice_sb.append(text_c_str);
		notice_sb.append(text_d_str);
		notice_sb.append(text_e_str);
		notice_sb.append(text_f_str);
		notice_sb.append(text_g_str);
		if (type.equals(REPAY_TYPE_1)) {
			String text_h_str = "&lt;/span&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;）&amp;nbsp;已成功按时还本付息。&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			notice_sb.append(text_h_str);
			String text_i_str = "今日借款人向出借人还本付息总计人民币&lt;/span&gt;";
			String text_j_str = repayAmount;
			String text_k_str = "&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;元，请您登录账户查看明细。&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_l_str = "对本次付息金额有疑问和未收到短信通知的出借用户，欢迎致电详询：400-666-9068。&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_m_str = "祝您生活愉快！&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 400px;&quot;&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_n_str = "中投摩根&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 400px;&quot;&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_o_str = DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
			String text_p_str = "&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;";
			notice_sb.append(text_i_str);
			notice_sb.append(text_j_str);
			notice_sb.append(text_k_str);
			notice_sb.append(text_l_str);
			notice_sb.append(text_m_str);
			notice_sb.append(text_n_str);
			notice_sb.append(text_o_str);
			notice_sb.append(text_p_str);
		} else if (type.equals(REPAY_TYPE_2)) {
			String text_h_str = "&lt;/span&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;）&amp;nbsp;已成功按时付息。&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			notice_sb.append(text_h_str);
			String text_i_str = "今日借款人向出借人付息总计人民币&lt;/span&gt;";
			String text_j_str = repayAmount;
			String text_k_str = "&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;元，请您登录账户查看明细。&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_l_str = "对本次还本金额有疑问和未收到短信通知的出借用户，欢迎致电详询：400-666-9068。&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 40px;&quot;&gt;\r\n&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_m_str = "祝您生活愉快！&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 400px;&quot;&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_n_str = "中投摩根&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;\r\n&lt;p style=&quot;margin-left: 400px;&quot;&gt;\r\n	&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;font-size: 13.97px; text-indent: 20pt; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;white-space: normal; font-size: 13.97px; text-indent: 2em; color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;&lt;span style=&quot;color: rgb(0, 0, 0);&quot;&gt;";
			String text_o_str = DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
			String text_p_str = "&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/span&gt;&lt;/p&gt;";
			notice_sb.append(text_i_str);
			notice_sb.append(text_j_str);
			notice_sb.append(text_k_str);
			notice_sb.append(text_l_str);
			notice_sb.append(text_m_str);
			notice_sb.append(text_n_str);
			notice_sb.append(text_o_str);
			notice_sb.append(text_p_str);
		}

		return notice_sb.toString();
	}

}
