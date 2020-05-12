package com.power.platform.cache.transcoder;

import java.io.UnsupportedEncodingException;

import com.power.platform.cache.providers.CacheTranscoder;
import com.power.platform.cache.providers.CachedObject;
import com.power.platform.cache.providers.CachedObjectImpl;

/**
 * 
 * 将长整型用简单的字符串字节表示. 主要在计数器方面的使用.
 * 
 * @author wulp
 * 
 */
public class LongToStringTranscoder implements CacheTranscoder {

     
    public Object decode(final CachedObject data) {
        byte[] value = data.getData();
        if (value == null || value.length == 0) {
            return null;
        }

        try {
            return Long.parseLong(new String(value, "UTF-8").trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

     
    public CachedObject encode(final Object o) {
        if (!(o instanceof Long)) {
            throw new IllegalArgumentException("Only Long objects are supported by this transcoder");
        }

        try {
            return new CachedObjectImpl(0, String.valueOf(o).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

	 
	public String toString() {
		return String.format(
				"LongToStringTranscoder [getClass()=%s, hashCode()=%s]",
				getClass(), hashCode());
	}
	
	 
	public int hashCode() {
		return super.hashCode();
	}
    
     
    public boolean equals(Object obj) {
    	return super.equals(obj);
    }

}