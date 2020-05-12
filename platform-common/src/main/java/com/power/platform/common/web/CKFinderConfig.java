/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.power.platform.common.web;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import com.ckfinder.connector.configuration.Configuration;
import com.ckfinder.connector.data.AccessControlLevel;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.power.platform.common.config.Global;
import com.power.platform.common.security.shiro.SystemAuthorizingRealm.Principal;
import com.power.platform.common.utils.FileUtils;

/**
 * CKFinder配置
 * @author ThinkGem
 * @version 2014-06-25
 */
public class CKFinderConfig extends Configuration {

	public CKFinderConfig(ServletConfig servletConfig) {
        super(servletConfig);  
    }
	
	 
    protected Configuration createConfigurationInstance() {
		Principal principal = (Principal) SecurityUtils.getSubject().getPrincipal();
		if (principal == null){
			return new CKFinderConfig(this.servletConf);
		}
		boolean isView = true;//UserUtils.getSubject().isPermitted("cms:ckfinder:view");
		boolean isUpload = true;//UserUtils.getSubject().isPermitted("cms:ckfinder:upload");
		boolean isEdit = true;//UserUtils.getSubject().isPermitted("cms:ckfinder:edit");
		AccessControlLevel alc = this.getAccessConrolLevels().get(0);
		alc.setFolderView(isView);
		alc.setFolderCreate(isEdit);
		alc.setFolderRename(isEdit);
		alc.setFolderDelete(isEdit);
		alc.setFileView(isView);
		alc.setFileUpload(isUpload);
		alc.setFileRename(isEdit);
		alc.setFileDelete(isEdit);
//		for (AccessControlLevel a : this.getAccessConrolLevels()){
//			System.out.println(a.getRole()+", "+a.getResourceType()+", "+a.getFolder()
//					+", "+a.isFolderView()+", "+a.isFolderCreate()+", "+a.isFolderRename()+", "+a.isFolderDelete()
//					+", "+a.isFileView()+", "+a.isFileUpload()+", "+a.isFileRename()+", "+a.isFileDelete());
//		}
		AccessControlUtil.getInstance(this).loadACLConfig();
		try {
//			Principal principal = (Principal)SecurityUtils.getSubject().getPrincipal();
//			this.baseURL = ServletContextFactory.getServletContext().getContextPath()+"/userfiles/"+principal+"/";
//			this.baseURL = FileUtils.path(Servlets.getRequest().getContextPath() + Global.USERFILES_BASE_URL + principal + "/");
//			this.baseDir = FileUtils.path(Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + principal + "/");
			this.baseURL = FileUtils.path(Servlets.getRequest().getContextPath() + Global.USERFILES_BASE_URL + "sharedFiles" + "/");
			// System.out.println("共享目录-baseURL：" + FileUtils.path(Servlets.getRequest().getContextPath() + Global.USERFILES_BASE_URL + "sharedFiles" + "/"));
			this.baseDir = FileUtils.path(Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + "sharedFiles" + "/");
			// System.out.println("共享目录-baseDir：" + FileUtils.path(Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + "sharedFiles" + "/"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new CKFinderConfig(this.servletConf);
    }

       
    public boolean checkAuthentication(final HttpServletRequest request) {
        return SecurityUtils.getSubject().getPrincipal()!=null;
    }

}
