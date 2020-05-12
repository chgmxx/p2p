package com.power.platform.weixin.resp;

/**
 * 音乐model（公众平台回复用户）
 * @author liuxiaolei
 * 下午2:17:58
 * v1.0
 */
public class MusicMessage extends BaseMessage {
	// 音乐
	private Music Music;

	public Music getMusic() {
		return Music;
	}

	public void setMusic(Music music) {
		Music = music;
	}
}