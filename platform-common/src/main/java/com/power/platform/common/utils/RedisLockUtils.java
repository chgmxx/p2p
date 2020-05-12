package com.power.platform.common.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.common.config.Global;
import com.power.platform.common.exception.DistributionLockException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class RedisLockUtils {

	/**
	 * LOG.
	 */
	private static Logger log = LoggerFactory.getLogger(RedisLockUtils.class);

	public static final Long SETNX_0 = 0L;
	public static final Long SETNX_1 = 1L;

	/**
	 * REDIS POOL.
	 */
	private static JedisPool jedisPool = SpringContextHolder.getBean(JedisPool.class);
	/**
	 * REDIS KEY PREFIX.
	 */
	public static final String KEY_PREFIX = Global.getConfig("redis.keyPrefix");
	/**
	 * SIMPLE LOCK尝试获取锁的次数.
	 */
	private static int retryCount = 3;

	/**
	 * 每次尝试获取锁的重试间隔毫秒数
	 */
	private static int waitIntervalInMS = 1000;

	public static Jedis getResource() throws JedisException {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		} catch (JedisException e) {
			log.warn("getResource JedisException {}", e);
			jedisDestroyPool();
			throw e;
		}
		return jedis;
	}

	public void closeJedisPool(final Jedis jedis, int flag) {

		if (flag == 0) {
			jedis.close();
			if (jedis.isConnected()) { // Client如果连接.
				try {
					System.out.println("退出" + jedis.toString() + ":" + jedis.quit());
					jedis.disconnect(); // 断开连接.
				} catch (Exception e) {
					System.out.println("退出失败");
					e.printStackTrace();
				}
			}
			jedis.close();
		}
	}

	/**
	 * 
	 * methods: returnBrokenResource <br>
	 * description: JEDIS对象异常出现时，回收JEDIS对象资源. <br>
	 * author: Roy <br>
	 * date: 2019年7月19日 上午11:00:19
	 * 
	 * @param jedis
	 */
	private static void jedisDestroyPool() {

		jedisPool.destroy();
		log.info("获取Redis资源异常:{关闭内部池} ...");
	}

	public static String lock(final String redisKey, final int expireInSecond, final int timeoutSecond) throws DistributionLockException {

		String lockValue = IdGen.uuid();
		if (StringUtils.isEmpty(redisKey)) {
			throw new DistributionLockException("key is empty!");
		}
		if (expireInSecond <= 0) {
			throw new DistributionLockException("expireInSecond must be greater than 0");
		}
		if (timeoutSecond <= 0) {
			throw new DistributionLockException("timeoutSecond must be greater than 0");
		}
		if (timeoutSecond >= expireInSecond) {
			throw new DistributionLockException("timeoutSecond must be less than expireInSecond");
		}

		try {

		} catch (DistributionLockException be) {
			log.warn("get redis lock error, exception: " + be.getMessage());
		} catch (Exception e) {
			log.warn("get redis lock error, exception: " + e.getMessage());
		}

		return lockValue;
	}

	/**
	 * 
	 * methods: simpleLock <br>
	 * description: 简单锁的实现，未获取锁的情况下，允许丢失. <br>
	 * author: Roy <br>
	 * date: 2019年7月18日 下午5:25:54
	 * 
	 * @param redisKey
	 *            锁的key值
	 * @param expireInSecond
	 *            锁的自动释放时间（秒）
	 * @return true:获取锁成功，false：无法获取锁.
	 * @throws DistributionLockException
	 */
	public static String simpleLock(final String redisKey, final int expireInSecond) throws DistributionLockException {

		Jedis conn = null;

		String lockValue = IdGen.uuid();

		boolean flag = false;

		try {

			if (StringUtils.isEmpty(redisKey)) { // REDIS KEY IS EMPTY.
				throw new DistributionLockException("key is empty.");
			}
			if (expireInSecond <= 0) {
				throw new DistributionLockException("expireInSecond must be bigger than 0");
			}

			conn = getResource(); // 获取内部连接池.
			for (int i = 0; i < retryCount; i++) { // 重试机制，获取锁3次.
				int n = i + 1;
				// 1）分布式锁，当指定的key存在时，重新SETNX返回0，当指定的key不存在时，SETNX返回1.
				Long setnx = conn.setnx(redisKey, lockValue);
				if (SETNX_1.equals(setnx)) { // 返回1，获取锁成功.
					log.info("第{}次获取，简单锁获取成功 ...", n);
					flag = true;

					// 设置当前key的过期时间，在REDIS中带有生存时间的key被称为[易失的]（volatile）.
					Long expire = conn.expire(redisKey, expireInSecond);
					if (expire.equals(1L)) {
						log.info("生存时间-当前'key':'{}'，生存时间为{}秒", redisKey, expireInSecond);
					} else {
						log.info("生存时间-当前'key':'{}'，生存时间设置失败 ...", redisKey);
					}

					return lockValue;
				} else if (SETNX_0.equals(setnx)) { // 返回0，获取锁失败.
					log.info("第{}次获取，简单锁获取失败 ...", n);
					lockValue = "";
				}

				try {
					log.info("第{}次睡眠前时间:{}", n, System.currentTimeMillis());
					TimeUnit.MILLISECONDS.sleep(waitIntervalInMS); // 睡眠100ms.
					log.info("第{}次睡眠后时间:{}", n, System.currentTimeMillis());
				} catch (Exception ignore) {
					log.warn("redis lock fail: " + ignore.getMessage());
				}
			}
			if (!flag) {
				throw new DistributionLockException(Thread.currentThread().getName() + " cannot acquire lock now ...");
			}
		} catch (DistributionLockException be) {
			log.warn("DistributionLockException: " + be.getDesc());
			return null;
		} catch (Exception e) {
			log.warn("get redis lock error, exception: " + e.getMessage());
			return null;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return lockValue;
	}

	public static Boolean unlock(final String redisKey, final String lockValue) {

		Jedis conn = null;
		boolean flag = false;
		if (StringUtils.isEmpty(redisKey)) {
			return flag;
		}
		if (StringUtils.isEmpty(lockValue)) {
			return flag;
		}

		try {
			conn = jedisPool.getResource();
			String currLockVal = conn.get(redisKey);
			if (currLockVal != null && currLockVal.equals(lockValue)) {
				Long delLongValue = conn.del(redisKey);
				if (delLongValue.equals(1L)) {
					log.info(Thread.currentThread().getName() + " unlock redis lock:" + redisKey + " successfully!");
					flag = true;
				} else {
					log.warn(Thread.currentThread().getName() + " unlock redis lock fail");
				}
			}
		} catch (Exception e) {
			log.warn("get redis lock error, exception:");
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		return flag;
	}

}
