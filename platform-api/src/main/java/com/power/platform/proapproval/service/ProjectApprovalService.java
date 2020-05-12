package com.power.platform.proapproval.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.power.platform.common.persistence.CrudDao;
import com.power.platform.common.service.CrudService;
import com.power.platform.proapproval.dao.ProjectApprovalDao;
import com.power.platform.proapproval.entity.ProjectApproval;
import com.power.platform.userinfo.entity.UserInfo;

@Service("projectApprovalService")
public class ProjectApprovalService extends CrudService<ProjectApproval>  {
	
	@Resource
	private ProjectApprovalDao projectApprovalDao;
	
	protected CrudDao<ProjectApproval> getEntityDao() {
		return projectApprovalDao;
	}

	
	public ProjectApproval getByProjectId(ProjectApproval projectApproval) {
		return projectApprovalDao.getByProjectId(projectApproval);
	}

}
