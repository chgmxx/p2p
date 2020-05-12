package com.power.platform.common.utils.properties;

import org.springframework.beans.factory.annotation.Value;

public class WebSiteProperties {

	/**
	 * 文件上传地址.
	 */
	@Value("#{webSitePropertiesFiles['doc_upload_url']}")
	private String docUploadUrl;

	/**
	 * 文件下载地址.
	 */
	@Value("#{webSitePropertiesFiles['doc_download_url']}")
	private String docDownloadUrl;

	public String getDocUploadUrl() {

		return docUploadUrl;
	}

	public void setDocUploadUrl(String docUploadUrl) {

		this.docUploadUrl = docUploadUrl;
	}

	public String getDocDownloadUrl() {

		return docDownloadUrl;
	}

	public void setDocDownloadUrl(String docDownloadUrl) {

		this.docDownloadUrl = docDownloadUrl;
	}

}
