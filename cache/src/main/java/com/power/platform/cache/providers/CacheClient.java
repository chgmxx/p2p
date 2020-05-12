package com.power.platform.cache.providers;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.power.platform.cache.CacheException;

/**
 * 缓存客户端
 * @author wulp
 *
 */
public interface CacheClient {
	/**
	 * 增加缓存数据
	 * @param key
	 * 				缓存key
	 * @param exp
	 * 				过期时间
	 * @param value
	 * 				缓存值
	 * @return
	 * 				缓存是否成功
	 * @throws TimeoutException
	 * @throws CacheException
	 */
	boolean add(final String key, final int exp, final Object value) throws TimeoutException, CacheException;
	
	/**
	 * 增加缓存数据
	 * @param key
	 * 				缓存key
	 * @param exp
	 * 				缓存数据过期时间
	 * @param value
	 * 				缓存值
	 * @param transcoder
	 * 				数据转换器
	 * @return
	 * 				缓存是否成功
	 * @throws TimeoutException
	 * @throws CacheException
	 */
    <T> boolean add(final String key, final int exp, final T value, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException;

    /**
     * 减少key中存储的数值
     * @param key
     * 				缓存key
     * @param by
     * 				减少的值
     * @return
     * 				减少后的值
     * @throws TimeoutException
     * @throws CacheException
     */
    long decr(final String key, final int by) throws TimeoutException, CacheException;

    /**
     * 减少key中存储的数值
     * @param key
     * 				缓存key
     * @param by
     * 				需要减少的值
     * @param def
     * 				如果key不存在，则用此值重新定义一个
     * @return
     * 				缓存中最新的值
     * @throws TimeoutException
     * @throws CacheException
     */
    long decr(final String key, final int by, final long def) throws TimeoutException, CacheException;

    /**
     * 删除key定义的缓存数值.
     * 
     * @param key
     *            缓存key
     * @throws TimeoutException
     * @throws CacheException
     */
    boolean delete(final String key) throws TimeoutException, CacheException;

    /**
     * 批量删除缓存中的数据.
     * 
     * @param keys
     * 				缓存key集合
     * @throws TimeoutException
     * @throws CacheException
     */
    void delete(final Collection<String> keys) throws TimeoutException, CacheException;

    /**
     * 清空缓存中的数据.
     * 
     * @throws TimeoutException
     * @throws CacheException
     */
    void flush() throws TimeoutException, CacheException;

    /**
     * 获取缓存数据.
     * 
     * @param key
     *            缓存健值
     * @return 
				     获取的缓存值，如果不存在则返回null
     * @throws TimeoutException
     * @throws CacheException
     */
    Object get(final String key) throws TimeoutException, CacheException;

    /**
     * 获取缓存数据.
     * 
     * @param <T>
     * @param key
     *            缓存健值
     * @param transcoder
     *            数据转换器
     * @return 缓存数据，如果不存在则返回null
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> T get(final String key, final CacheTranscoder transcoder) throws TimeoutException, CacheException;

    /**
     * 获取缓存数据
     * 
     * @param <T>
     * @param key
     *            缓存键值
     * @param transcoder
     *            数据转换器
     * @param timeout
     *            超时时间，在指这的时间内如果还没有返回数据则抛出timeoutException
     * @return 缓存数据，如果不存在则返回null
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> T get(final String key, final CacheTranscoder transcoder, final long timeout) throws TimeoutException, CacheException;

    /**
     * 得到可用的缓存服务器.
     * 
     * @return 缓存服务器的集合
     */
    Collection<SocketAddress> getAvailableServers();

    /**
     * 批量获取缓存数据
     * @param keys
     * 			缓存健值集合
     * @return
     * 			缓存数据
     * @throws TimeoutException
     * @throws CacheException
     */
    Map<String, Object> getBulk(final Collection<String> keys) throws TimeoutException, CacheException;

    /**
     * 批量获取缓存数据
     * @param keys
     * 			缓存健值集合
     * @param transcoder
     * 			数据转换器
     * @return
     * 			缓存数据
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> Map<String, T> getBulk(final Collection<String> keys, final CacheTranscoder transcoder) throws TimeoutException, CacheException;

    /**
     * 获取默认的数据转换器.
     * 
     * @return 默认的数据转换器
     */
    CacheTranscoder getTranscoder();

    /**
     * 增加key中存储的值
     * @param key
     * 			缓存键值
     * @param by
     * 			增加的值
     * @return
     * 			增加后的值
     * @throws TimeoutException
     * @throws CacheException
     */
    long incr(final String key, final int by) throws TimeoutException, CacheException;

    /**
     * 增加key中存储的值
     * @param key
     * 			缓存键值
     * @param by
     * 			增加的值
     * @param def
     * 			当key不存在时，初始化key的值
     * @return
     * 			增加后的值
     * @throws TimeoutException
     * @throws CacheException
     */
    long incr(final String key, final int by, final long def) throws TimeoutException, CacheException;

    /**
     * 增加key中存储的值
     * @param key
     * 			缓存的键值
     * @param by
     * 			增加的值
     * @param def
     * 			当key不存在时，初始化key的值
     * @param exp
     * 			过期时间
     * @return
     * 			增加后的值
     * @throws TimeoutException
     * @throws CacheException
     */
    long incr(final String key, final int by, final long def, final int exp) throws TimeoutException, CacheException;

    /**
     * 替换缓存数据
     * @param key
     * 			缓存的key
     * @param exp
     * 			过期时间
     * @param value
     * 			缓存数据
     * @return
     * 			替换是否成功
     * @throws TimeoutException
     * @throws CacheException
     */
    boolean replace(final String key, final int exp, final Object value) throws TimeoutException, CacheException;

    /**
     * 替换缓存数据
     * 
     * @param <T>
     * @param key
     *            缓存的key
     * @param exp
     *            过期时间
     * @param value
     *            缓存数据
     * @param transcoder
     *            数据转换器
     * @return 
				   替换是否成功
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean replace(final String key, final int exp, final T value, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException;
    
    /**
     * 存储键值对到缓存
     * @param key
     * 			缓存的key
     * @param exp
     * 			过期时间
     * @param value
     * 			缓存数据
     * @return
     * 			缓存是否成功
     * @throws TimeoutException
     * @throws CacheException
     */
    boolean set(final String key, final int exp, final Object value) throws TimeoutException, CacheException;

    /**
     * 存储键值对到缓存
     * 
     * @param <T>
     * @param key
     *            缓存的key
     * @param exp
     *            过期时间
     * @param value
     *            缓存数据
     * @param transcoder
     *            数据转换器
     * @return 
				   缓存是否成功
     * @throws TimeoutException
     * @throws CacheException
     */
    <T> boolean set(final String key, final int exp, final T value, final CacheTranscoder transcoder) throws TimeoutException,
            CacheException;

    /**
     * 关闭.
     */
    void shutdown();

    /**
     * 当前使用的缓存客户端
     */
    Object getNativeClient();
}
