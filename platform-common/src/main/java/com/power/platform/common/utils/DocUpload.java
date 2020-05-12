package com.power.platform.common.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.power.platform.common.exception.WinException;

public class DocUpload {

	public static void upload(String url, String docPath, InputStream docFile) throws ClientProtocolException, IOException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		InputStream is = null;
		try {
			HttpPost httppost = new HttpPost(url);

			InputStreamBody docFileBody = new InputStreamBody(docFile, ContentType.MULTIPART_FORM_DATA);
			StringBody docPathBody = new StringBody(docPath, ContentType.TEXT_PLAIN);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("docFile", docFileBody);
			builder.addPart("docPath", docPathBody);
			HttpEntity reqEntity = builder.build();

			httppost.setEntity(reqEntity);

			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					if (resEntity.getContentLength() > 0) {
						byte[] buf = new byte[(int) resEntity.getContentLength()];
						is = resEntity.getContent();
						is.read(buf);
						String str = new String(buf);
						throw new WinException(str);
					}
					EntityUtils.consume(resEntity);

				}
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
			if (is != null)
				is.close();
		}
	}

}
