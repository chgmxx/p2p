package com.power.platform.cache;

/**
 * 异常包装类
 * @author wulp
 *
 */
public class CacheException extends Exception {

    private static final long serialVersionUID = -8079095237716455457L;

    public CacheException(final Exception cause) {
        super(cause);
    }

}
