package com.power.platform.cache.providers;


public class CacheConfiguration {
	/**
	 * 一致性哈希
	 */
	private boolean consistentHashing;

	/**
	 * 使用二进制协议
	 */
    private boolean useBinaryProtocol;

    /**
     * 超时时间设置
     */
    private Integer operationTimeout;


	public boolean isConsistentHashing() {
		return consistentHashing;
	}


	public void setConsistentHashing(boolean consistentHashing) {
		this.consistentHashing = consistentHashing;
	}


	public boolean isUseBinaryProtocol() {
		return useBinaryProtocol;
	}


	public void setUseBinaryProtocol(boolean useBinaryProtocol) {
		this.useBinaryProtocol = useBinaryProtocol;
	}


	public Integer getOperationTimeout() {
		return operationTimeout;
	}


	public void setOperationTimeout(Integer operationTimeout) {
		this.operationTimeout = operationTimeout;
	}

}
