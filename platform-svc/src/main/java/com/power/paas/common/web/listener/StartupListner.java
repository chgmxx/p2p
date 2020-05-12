package com.power.paas.common.web.listener;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
 

/**
 * Spring启动监听器。<br/>
 * 用于注入servletContext和applicationContext。
 * <pre>
 * 在webxml配置如下：
 * &lt;listener>
 *       &lt;listener-class>com.harmazing.paas.common.web.listener.StartupListner&lt;/listener-class>
 *   &lt;/listener>
 *  <pre>
 * @author wulp
 *
 */
public class StartupListner extends ContextLoaderListener {
	 
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
//		AppUtil.init(event.getServletContext()); 
	}
	
	 
	public void contextDestroyed(ServletContextEvent event) {
		immolate();
	}
	
	public Integer immolate() {
        int count = 0;
        
        try {
            final Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            final Field inheritableThreadLocalsField = Thread.class.getDeclaredField("inheritableThreadLocals");
            inheritableThreadLocalsField.setAccessible(true);
            for (final Thread thread : Thread.getAllStackTraces().keySet()) {
                    count += clear(threadLocalsField.get(thread));
                    count += clear(inheritableThreadLocalsField.get(thread));
            }
         } catch (Exception e) {
            throw new Error("ThreadLocalImmolater.immolate()", e);
        }
        return count;
    }

    @SuppressWarnings("rawtypes")
	private int clear(final Object threadLocalMap) throws Exception {
        if (threadLocalMap == null)
                return 0;
        int count = 0;
        final Field tableField = threadLocalMap.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
        final Object table = tableField.get(threadLocalMap);
        for (int i = 0, length = Array.getLength(table); i < length; ++i) {
            final Object entry = Array.get(table, i);
            if (entry != null) {
                final Object threadLocal = ((WeakReference)entry).get();
                if (threadLocal != null) {
                    Array.set(table, i, null);
                    ++count;
                }
            }
        }
        return count;
    }
}
