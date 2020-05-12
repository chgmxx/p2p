package com.power.platform.cache;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.power.platform.cache.format.SerializationType;

public interface Cache {
	/**
     * 得到可用的缓存服务器.
     * 
     * @return 缓存服务器的集合
     */
    Collection<SocketAddress> getAvailableServers();
    
    /**
     * 获取缓存的名称.
     * 
     * @return 缓存名称
     */
    String getName();
    
    /**
     * 增加缓存对象,如果缓存中已经存在相同的key,则添加失败.
     * @param key
     * 			缓存key
     * @param value
     * 			缓存值
     * @return
     * 			是否添加成功
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean add(final String key, final Object value) throws TimeoutException,
    	CacheException;
    
    /**
     * 增加缓存对象,如果缓存中已经存在相同的key,则添加失败.
     * @param key
     * 			缓存key
     * @param exp
     * 			过期时间
     * @param value
     * 			缓存值
     * @return
     * 			是否添加成功
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean add(final String key, final int exp, final Object value) throws TimeoutException,
    	CacheException;

    /**
     * 增加缓存对象,如果缓存中已经存在相同的key,则添加失败.
     * 
     * @param key
     * 			缓存key
     * @param exp
     * 			过期时间
     * @param value
     * 			缓存值
     * @param serializationType
     * 						序列化类型
     * @return  true 成功
     * 			false 失败
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean add(final String key, final int exp, final Object value, final SerializationType serializationType) throws TimeoutException,
            CacheException;

    /**
     * 减少缓存数据的值
     * @param key
     * 			缓存key
     * @param by
     * 			需要减少的值
     * @return	更新后的数据值
     * @throws TimeoutException
     * @throws CacheException
     */
    long decr(final String key, final int by) throws TimeoutException, CacheException;

    /**
     * 删除.
     * 
     * @param key
     *            缓存key
     * @throws TimeoutException
     * @throws CacheException
     */
    boolean delete(final String key) throws TimeoutException, CacheException;

    /**
     * 批量删除.
     * 
     * @param keys
     * 			缓存所代表的key
     * @throws TimeoutException
     * @throws CacheException
     */
    void delete(final Collection<String> keys) throws TimeoutException, CacheException;

    /**
     * 清空所有数据.
     * 
     * @throws TimeoutException
     * @throws CacheException
     */
    void flush() throws TimeoutException, CacheException;
    
    /**
     * 获取缓存数据
     * @param key 
     * 			缓存key 
     * @return
     * 			缓存数据
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> T get(final String key) throws TimeoutException, CacheException;

    /**
     * 根据key获取缓存中的值.
     * 
     * @param <T>
     * @param key
     *            缓存key
     * @param serializationType
     *            缓存时使用的序列化方式 
     * @return 与缓存key相关的值或null
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> T get(final String key, final SerializationType serializationType) throws TimeoutException, CacheException;
    
    /**
     * 批量获取缓存数据
     * @param keys
     * 			缓存key集合
     * @return
     * 			所有缓存数据
     * @throws TimeoutException
     * @throws CacheException
     */
    Map<String,Object> getBulk(final Collection<String> keys) throws TimeoutException,CacheException;

    /**
     * 批量获取key集合的值
     * @param keys
     * 				缓存key值集合
     * @param serializationType
     *				缓存时使用的序列化方式 
     * @return	所有缓存key与key相关的值组成的键值对
     * @throws TimeoutException
     * @throws CacheException
     */
    Map<String, Object> getBulk(final Collection<String> keys, final SerializationType serializationType) throws TimeoutException,
            CacheException;

    /**
     * 根据缓存key,增加其对应的值
     * @param key
     * 			缓存key
     * @param by
     * 			需加增加的值
     * @param def
     * 			当指定的key不存在时初始化key的值
     * @return 增加后的数据值
     * @throws TimeoutException
     * @throws CacheException
     */
    long incr(final String key, final int by, final long def) throws TimeoutException, CacheException;

    /**
     * 根据缓存key,增加其对应的值
     * @param key
     * 			缓存key
     * @param by
     * 			需加增加的值
     * @param def
     * 			当指定的key不存在时初始化key的值
     * @param exp
     * 			过期时间
     * @return 增加后的数据值
     * @throws TimeoutException
     * @throws CacheException
     */
    long incr(final String key, final int by, final long def, final int exp) throws TimeoutException, CacheException;

    /**
     * 存储健值项到缓存.
     * 
     * @param <T>
     * @param key
     *            存储的key
     * @param value
     *            存储的值
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> void set(final String key, final Object value) throws TimeoutException,CacheException;
    
    /**
     * 存储健值项到memcached.
     * 
     * @param <T>
     * @param key
     *            存储的key
     * @param exp
     *            失效时间
     * @param value
     *            存储的值
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> void set(final String key, final int exp, final Object value) throws TimeoutException,CacheException;
    
    /**
     * 存储健值项到memcached.
     * 
     * @param <T>
     * @param key
     *            存储的key
     * @param exp
     *            失效时间
     * @param value
     *            存储的值
     * @param serializationType
     *            使用的序列化方式
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> void set(final String key, final int exp, final Object value, final SerializationType serializationType) throws TimeoutException,
            CacheException;
    
    /**
     * 替换缓存数据.
     * 
     * @param <T>
     * @param key
     *            存储的key
     * @param value
     *            存储的值
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean replace(final String key, final Object value) throws TimeoutException,
    		CacheException;
    
    /**
     * 替换缓存数据.
     * 
     * @param <T>
     * @param key
     *            存储的key
     * @param exp
     *            失效时间
     * @param value
     *            存储的值
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean replace(final String key, final int exp, final Object value) throws TimeoutException,
    		CacheException;
    
    /**
     * 替换缓存数据.
     * 
     * @param <T>
     * @param key
     *            存储的key
     * @param exp
     *            失效时间
     * @param value
     *            存储的值
     * @param serializationType
     *            使用的序列化方式
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean replace(final String key, final int exp, final Object value, final SerializationType serializationType) throws TimeoutException,
			CacheException;

    /**
     * 获取递增计数器的值.
     * 
     * @param cacheKey
     * @return 计数器的值
     * @throws CacheException
     * @throws TimeoutException
     */
    Long getCounter(final String cacheKey) throws TimeoutException, CacheException;

    /**
     * 设置计数器的初始值.
     * 
     * @param cacheKey
     * @param expiration
     * @param value
     * @throws CacheException
     * @throws TimeoutException
     */
    void setCounter(final String cacheKey, final int expiration, final long value) throws TimeoutException, CacheException;
    
    /**
     * 缓存是否可用
     * @return
     */
    boolean isEnabled();
    
    /**
     * 关闭缓存客户端
     */
    void shutdown();
}
