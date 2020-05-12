package com.power.platform.zdw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 * @Description:读取配置文件工具类
 * @time:2016年8月31日 上午11:48:50
 */
public class PropertiesUtil {

	public static final String PRE_PROPS = "/com/puhui/crawler/mail/props/";
	private static final ConcurrentHashMap<String, Properties> PROPS_HOLDER = new ConcurrentHashMap<String, Properties>();
	private static final String SUF_PROPS = ".properties";
	private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

	private static Properties BANK_PROPS = null;
	private static Properties CREDIT_PROPS = null;
	private static Properties DEFAULT_PROPS = null;

	private PropertiesUtil() {

	}

	/**
	 * 获取默认配置
	 *
	 * @param key
	 * @return
	 * @author wangyepeng
	 */
	public static String getProps(String key) {

		return getProps(key, null);
	}

	/**
	 * @param key
	 * @param defVal
	 * @return
	 */
	public static String getProps(String key, String defVal) {

		if (DEFAULT_PROPS == null) {
			synchronized (PropertiesUtil.class) {
				if (DEFAULT_PROPS == null) {
					DEFAULT_PROPS = readPropertiesFile("/client_spconf.properties");
				}
			}
		}
		return DEFAULT_PROPS.getProperty(key);
	}

	/**
	 * 设置client_spconf.properties里的参数
	 *
	 * @param key
	 * @param value
	 * @author
	 */
	public static void setProps(String key, String value) {

		if (DEFAULT_PROPS == null) {
			synchronized (PropertiesUtil.class) {
				if (DEFAULT_PROPS == null) {
					DEFAULT_PROPS = readPropertiesFile("/client_spconf.properties");
				}
			}
		}
		DEFAULT_PROPS.setProperty(key, value);
	}

	/**
	 * 设置client_spconf.properties里的参数
	 *
	 * @param key
	 * @param value
	 * @author wangyepeng
	 */
	public static void reloadProps() {

		synchronized (PropertiesUtil.class) {
			DEFAULT_PROPS = readPropertiesFile("/client_spconf.properties");
		}
	}

	/**
	 * @return
	 */
	public static Set<Object> defaultPropKeys() {

		if (DEFAULT_PROPS == null) {
			synchronized (PropertiesUtil.class) {
				DEFAULT_PROPS = readPropertiesFile("/client_spconf.properties");
			}
		}
		return DEFAULT_PROPS.keySet();
	}

	/**
	 * 设置bank_spconf.properties里的参数
	 *
	 * @param key
	 * @param value
	 * @author
	 */
	public static void reloadtBankProps() {

		synchronized (PropertiesUtil.class) {
			BANK_PROPS = readPropertiesFile("/bank_spconf.properties");
		}
	}

	/**
	 * 获取银行借记卡配置文件
	 *
	 * @param key
	 * @return
	 * @author wangyepeng
	 */
	public static int getBankProps4Int(String key) {

		if (BANK_PROPS == null) {
			synchronized (PropertiesUtil.class) {
				if (BANK_PROPS == null) {
					BANK_PROPS = readPropertiesFile("/bank_spconf.properties");
				}
			}
		}
		return Integer.parseInt(BANK_PROPS.getProperty(key));
	}

	/**
	 * 获取银行信用卡配置文件
	 *
	 * @param key
	 * @return
	 * @author
	 */
	public static String getCreditProps(String key) {

		if (CREDIT_PROPS == null) {
			synchronized (PropertiesUtil.class) {
				if (CREDIT_PROPS == null) {
					CREDIT_PROPS = readPropertiesFile("/bank_ccconf.properties");
				}
			}
		}
		return CREDIT_PROPS.getProperty(key);
	}

	/**
	 * 设置bank_ccconf.properties里的参数
	 *
	 * @param key
	 * @param value
	 * @author
	 */
	public static void reloadtCreditProps() {

		synchronized (PropertiesUtil.class) {
			CREDIT_PROPS = readPropertiesFile("/bank_ccconf.properties");
		}
	}

	/**
	 * 设置所有配置里的参数
	 *
	 * @param key
	 * @param value
	 * @author
	 */
	public static void reloadAllProps() {

		reloadProps();
		reloadtBankProps();
		reloadtCreditProps();
	}

	/**
	 * 获取银行信用卡配置文件
	 *
	 * @param key
	 * @return
	 * @author
	 */
	public static int getCreditProps4Int(String key) {

		if (CREDIT_PROPS == null) {
			synchronized (PropertiesUtil.class) {
				if (CREDIT_PROPS == null) {
					CREDIT_PROPS = readPropertiesFile("/bank_ccconf.properties");
				}
			}
		}
		return Integer.parseInt(CREDIT_PROPS.getProperty(key));
	}

	/**
	 * @param file
	 * @return
	 */
	public static Properties readPropertiesFile(String file) {

		InputStream in = PropertiesUtil.class.getResourceAsStream(file);
		Properties prop = new Properties();
		try {
			prop.load(in);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return prop;
	}

}
