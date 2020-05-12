package com.power.platform.weixin.resp;
/**
 * 上传文件返回信息
 * @author liuxiaolei
 * 下午2:13:52
 * v1.0
 */
public class Media extends BaseResult{

	private String type;
	
	private String thumb_media_id;
	
	private Integer created_at;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String geThumb_media_id() {
		return thumb_media_id;
	}

	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}

	public Integer getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Integer createdAt) {
		created_at = createdAt;
	}
	
	
}
