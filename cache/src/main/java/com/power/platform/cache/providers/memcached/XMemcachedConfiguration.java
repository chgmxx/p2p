package com.power.platform.cache.providers.memcached;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import net.rubyeye.xmemcached.MemcachedClientStateListener;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.transcoders.Transcoder;

import com.google.code.yanf4j.config.Configuration;
import com.google.code.yanf4j.core.SocketOption;
import com.power.platform.cache.providers.CacheConfiguration;

public class XMemcachedConfiguration extends CacheConfiguration {

    /**
     * 缓存服务器掉线超过指定时间后会重连
     */
    private Integer maxAwayTime;

    /**
     * 初始化连接池大小
     */
    private Integer connectionPoolSize;

    /**
     * 设置memcache客户端的网络参数
     */
    private Configuration configuration;

    /**
     * 是否启用失败恢复模式
     */
    private Boolean failureMode;

    /**
     * 设置socket选项
     */
    private Map<SocketOption<?>, Object> socketOptions;

    /**
     * 重连时间设置
     */
    private Long healSessionInterval;

    /**
     * 如果你对响应时间比较在意，那么可以将合并的因子减小
     * 如果你对吞吐量更在意，那么也可将合并因子调大，默认是150
     */
    private Integer mergeFactor;

    /**
     * 启用/禁用合并多个get命令到一个multi-get命令
     */
    private Boolean optimizeGet;

    /**
     * 启用/禁用将连续的请求合并成socket发送缓冲区大小的buffer发送
     */
    private Boolean optimizeMergeBuffer;

    /**
     * 是否启用心路
     */
    private Boolean enableHeartBeat;

    /**
     * 设置是否将所有原始类型存储为字符串
     */
    private Boolean primitiveAsString;

    /**
     * 是否对所有的缓存key进行URLEncoding
     */
    private Boolean sanitizeKeys;

    /**
     * 设置默认数据转换器
     */
    private Transcoder<?> defaultTranscoder;

    /**
     * 连接超时设置，单位毫秒
     */
    private Long connectionTimeout;

    /**
     * 设置没有响应的最大排除操作数
     */
    private Integer maxQueuedNoReplyOperations;

    /**
     * 用于启用或者禁止连接修复（当连接意外断开的时候，默认仍然是启用）
     */
    private Boolean enableHealSession;

    /**
     * 身份认证信息
     */
    private Map<InetSocketAddress, AuthInfo> authInfoMap;

    /**
     * 设置状态监听器
     */
    private List<MemcachedClientStateListener> stateListeners;

    /**
     * 设置服务器权重
     */
    private int[] weights;

	public Integer getMaxAwayTime() {
		return maxAwayTime;
	}

	public void setMaxAwayTime(Integer maxAwayTime) {
		this.maxAwayTime = maxAwayTime;
	}

	public Integer getConnectionPoolSize() {
		return connectionPoolSize;
	}

	public void setConnectionPoolSize(Integer connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Boolean getFailureMode() {
		return failureMode;
	}

	public void setFailureMode(Boolean failureMode) {
		this.failureMode = failureMode;
	}

	public Map<SocketOption<?>, Object> getSocketOptions() {
		return socketOptions;
	}

	public void setSocketOptions(Map<SocketOption<?>, Object> socketOptions) {
		this.socketOptions = socketOptions;
	}

	public Long getHealSessionInterval() {
		return healSessionInterval;
	}

	public void setHealSessionInterval(Long healSessionInterval) {
		this.healSessionInterval = healSessionInterval;
	}

	public Integer getMergeFactor() {
		return mergeFactor;
	}

	public void setMergeFactor(Integer mergeFactor) {
		this.mergeFactor = mergeFactor;
	}

	public Boolean getOptimizeGet() {
		return optimizeGet;
	}

	public void setOptimizeGet(Boolean optimizeGet) {
		this.optimizeGet = optimizeGet;
	}

	public Boolean getOptimizeMergeBuffer() {
		return optimizeMergeBuffer;
	}

	public void setOptimizeMergeBuffer(Boolean optimizeMergeBuffer) {
		this.optimizeMergeBuffer = optimizeMergeBuffer;
	}

	public Boolean getEnableHeartBeat() {
		return enableHeartBeat;
	}

	public void setEnableHeartBeat(Boolean enableHeartBeat) {
		this.enableHeartBeat = enableHeartBeat;
	}

	public Boolean getPrimitiveAsString() {
		return primitiveAsString;
	}

	public void setPrimitiveAsString(Boolean primitiveAsString) {
		this.primitiveAsString = primitiveAsString;
	}

	public Boolean getSanitizeKeys() {
		return sanitizeKeys;
	}

	public void setSanitizeKeys(Boolean sanitizeKeys) {
		this.sanitizeKeys = sanitizeKeys;
	}

	public Transcoder<?> getDefaultTranscoder() {
		return defaultTranscoder;
	}

	public void setDefaultTranscoder(Transcoder<?> defaultTranscoder) {
		this.defaultTranscoder = defaultTranscoder;
	}

	public Long getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Integer getMaxQueuedNoReplyOperations() {
		return maxQueuedNoReplyOperations;
	}

	public void setMaxQueuedNoReplyOperations(Integer maxQueuedNoReplyOperations) {
		this.maxQueuedNoReplyOperations = maxQueuedNoReplyOperations;
	}

	public Boolean getEnableHealSession() {
		return enableHealSession;
	}

	public void setEnableHealSession(Boolean enableHealSession) {
		this.enableHealSession = enableHealSession;
	}

	public Map<InetSocketAddress, AuthInfo> getAuthInfoMap() {
		return authInfoMap;
	}

	public void setAuthInfoMap(Map<InetSocketAddress, AuthInfo> authInfoMap) {
		this.authInfoMap = authInfoMap;
	}

	public List<MemcachedClientStateListener> getStateListeners() {
		return stateListeners;
	}

	public void setStateListeners(List<MemcachedClientStateListener> stateListeners) {
		this.stateListeners = stateListeners;
	}

	public int[] getWeights() {
		return weights;
	}

	public void setWeights(int[] weights) {
		this.weights = weights;
	}
}
