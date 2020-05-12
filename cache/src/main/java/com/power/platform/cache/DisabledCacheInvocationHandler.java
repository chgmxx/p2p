package com.power.platform.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DisabledCacheInvocationHandler implements InvocationHandler {

    private final String cacheName;

    public DisabledCacheInvocationHandler(String cacheName) {
        this.cacheName = cacheName;
    }

     
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String methodName = method.getName();

        if ("getName".equals(methodName)) {
            return cacheName;
        } else if ("isEnabled".equals(methodName)) {
            return false;
        } else if ("shutdown".equals(methodName)) {
            return null;
        }

        throw new IllegalStateException(String.format("缓存 %s 已禁用", cacheName));
    }

}
