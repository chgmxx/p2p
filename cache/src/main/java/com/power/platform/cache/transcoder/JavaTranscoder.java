package com.power.platform.cache.transcoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.power.platform.cache.providers.CacheTranscoder;
import com.power.platform.cache.providers.CachedObject;
import com.power.platform.cache.providers.CachedObjectImpl;

/**
 * 
 * 使用默认的java代码转换器负责解码和编码对象序列化/反序列化. 
 * 
 * @author wulp
 * 
 */
public class JavaTranscoder implements CacheTranscoder {

    /**
     * 默认的压缩阈值.
     */
    public static final int DEFAULT_COMPRESSION_THRESHOLD = 16384;

    /**
     * 默认字符编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaTranscoder.class);

    private static final int SERIALIZED = 1;	//序列化
    private static final int COMPRESSED = 2;	//压缩

    private int compressionThreshold = DEFAULT_COMPRESSION_THRESHOLD;

    public int getCompressionThreshold() {
		return compressionThreshold;
	}

	public void setCompressionThreshold(int compressionThreshold) {
		this.compressionThreshold = compressionThreshold;
	}

	 
    public Object decode(final CachedObject d) {
        byte[] data = d.getData();

        if ((d.getFlags() & COMPRESSED) != 0) {
            data = decompress(d.getData());
        }

        if ((d.getFlags() & SERIALIZED) != 0 && data != null) {
            return deserialize(data);
        } else {
            LOGGER.warn("不能使用java转换器解码缓存数据{}", data);
            throw new RuntimeException("不能使用java转换器解码缓存数据");
        }
    }

     
    public CachedObject encode(final Object o) {
        byte[] data = serialize(o);
        int flags = SERIALIZED;

        if (data.length > getCompressionThreshold()) {
            byte[] compressed = compress(data);
            if (compressed.length < data.length) {
                LOGGER.debug("压缩 {} 从 {} 到 {}", new Object[] { o.getClass().getName(), data.length, compressed.length });
                data = compressed;
                flags |= COMPRESSED;
            } else {
                LOGGER.info("{}压缩后大小从{} 增加到 {}", new Object[] { o.getClass().getName(), data.length,
                        compressed.length });
            }
        }
        return new CachedObjectImpl(flags, data);
    }

    /**
     *  使用java的序列化方式来序列化对象.
     * 
     * @param o
     *            需要序列化的对象
     * @return 序列化后的字节数组
     */
    protected byte[] serialize(final Object o) {
        if (o == null) {
            throw new NullPointerException("空对象不能进行序列化");
        }
        byte[] data = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;

        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(o);
            os.close();
            bos.close();
            data = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("对象不能序列化", e);
        } finally {
            close(os);
            close(bos);
        }

        return data;
    }

    /**
     * 使用java默认的反序列化方式反序列化字节数组.
     * 
     * @param in
     *            需要反序列化的字节数组
     * @return 返序列化后的对象
     */
    protected Object deserialize(final byte[] in) {
        Object o = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;

        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                o = is.readObject();
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            LOGGER.warn(String.format("解码%d字节数据时捕获IO异常", in.length), e);
        } catch (ClassNotFoundException e) {
            LOGGER.warn(String.format("解码%d字节数据时捕获CNFE异常", in.length), e);
        } finally {
            close(is);
            close(bis);
        }

        return o;
    }

    /**
     * 压缩字节数组.
     * 
     * @param in
     *            需要压缩的字节数据
     */
    protected byte[] compress(final byte[] in) {
        if (in == null) {
            throw new NullPointerException("不能压缩空对象");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gz = null;

        try {
            gz = new GZIPOutputStream(bos);
            gz.write(in);
        } catch (IOException e) {
            throw new RuntimeException("压缩对象时出现IO异常", e);
        } finally {
            close(gz);
            close(bos);
        }

        return bos.toByteArray();
    }

    /**
     * 解压字节数组.
     * 
     * @param in
     *            需要解压的数据
     * @return 解压完成的数据
     */
    protected byte[] decompress(final byte[] in) {
        if (in == null) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayInputStream bis = new ByteArrayInputStream(in);
        GZIPInputStream gis = null;

        try {
            gis = new GZIPInputStream(bis);

            byte[] buf = new byte[8192];
            int r = -1;
            while ((r = gis.read(buf)) > 0) {
                bos.write(buf, 0, r);
            }

            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("解压数据时出现异常", e);
        } finally {
            close(gis);
            close(bis);
            close(bos);
        }
    }
   
    protected void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.info(String.format("不能关闭 %s", closeable), e);
            }
        }
    }

}
