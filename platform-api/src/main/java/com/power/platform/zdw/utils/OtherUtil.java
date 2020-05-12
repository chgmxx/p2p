package com.power.platform.zdw.utils;

import java.io.InputStream;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v1.io.InputStreamUtils;

public class OtherUtil {

	public static final String UTF_8 = "UTF-8";
	private static final Logger log = LoggerFactory.getLogger(OtherUtil.class);
	protected static String script1;

	static {
		try {
			InputStream is = OtherUtil.class.getResourceAsStream("/ct_js/md5.js");
			script1 = InputStreamUtils.getContentsAsString(is, UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String ctAes(String password) {

		String result = "";
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
			engine.eval(script1);
			log.info("调用加密前" + password);
			if (engine instanceof Invocable) {
				Invocable in = (Invocable) engine;
				result = (String) in.invokeFunction("strEnc", password);
				log.info("调用加密后" + result);
				return result;
			}
		} catch (Exception e) {
			log.info("加密失败");
			log.info(e.getMessage());
		}
		return result;
	}

}
