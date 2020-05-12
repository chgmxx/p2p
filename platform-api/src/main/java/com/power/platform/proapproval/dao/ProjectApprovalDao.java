package com.power.platform.proapproval.dao;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.persistence.annotation.MyBatisDao;
import com.power.platform.proapproval.entity.ProjectApproval;

@MyBatisDao
public interface ProjectApprovalDao extends CrudDao<ProjectApproval> {

	/**
	 * 根据项目id查找放款申请信息
	 * @param projectApproval
	 * @return
	 */
	public ProjectApproval getByProjectId(ProjectApproval projectApproval);

}
