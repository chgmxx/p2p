package com.power.platform.cache.format;

public enum SerializationType {
	/** 默认提供的序列化与反序列化机制 */
    PROVIDER,
    /** java默认的序列化与反序列化机制 */
    JAVA,
    /** 自定义的序列化与反序列化机制，需要自己注册 */
    CUSTOM;
}
