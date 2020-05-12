package com.power.platform.proapproval.entity;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 放款项目审批状态
 * @author Jia
 *
 */
public class ProjectApprovalState {

	public static final Map<Integer, String> dict = new LinkedHashMap<Integer, String>();

	public static final int APPROVAL_RCER 		= 1;
	public static final int APPROVAL_RCLERK 	= 2;
	public static final int APPROVAL_FINANCE 	= 3;
	public static final int APPROVAL_ADMIN 		= 4;

	static {
		dict.put(APPROVAL_RCER, "提交到风控专员");
		dict.put(APPROVAL_RCLERK, "提交到风控文员");
		dict.put(APPROVAL_FINANCE, "提交到财务");
		dict.put(APPROVAL_ADMIN, "提交到总经理");
	}
}
