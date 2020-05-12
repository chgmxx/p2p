package com.power.platform.zdw.utils;

import java.util.Collection;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory.SecurityLevel;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class BrowserCompatSpecExFactory implements CookieSpecFactory, CookieSpecProvider {

	// public enum SecurityLevel {
	// SECURITYLEVEL_DEFAULT, SECURITYLEVEL_IE_MEDIUM
	// }

	private final String[] datepatterns;
	private final SecurityLevel securityLevel;

	public BrowserCompatSpecExFactory(final String[] datepatterns, final SecurityLevel securityLevel) {

		super();
		this.datepatterns = datepatterns;
		this.securityLevel = securityLevel;
	}

	public BrowserCompatSpecExFactory(final String[] datepatterns) {

		this(datepatterns, SecurityLevel.SECURITYLEVEL_DEFAULT);
	}

	public BrowserCompatSpecExFactory() {

		this(BrowserCompatSpecEx.DEFAULT_DATE_PATTERNS, SecurityLevel.SECURITYLEVEL_DEFAULT);
	}

	public CookieSpec newInstance(final HttpParams params) {

		if (params != null) {

			String[] patterns = null;
			final Collection<?> param = (Collection<?>) params.getParameter(CookieSpecPNames.DATE_PATTERNS);
			if (param != null) {
				patterns = new String[param.size()];
				patterns = param.toArray(patterns);
			}
			return null;
		} else {
			return null;
		}
	}

	public CookieSpec create(final HttpContext context) {

		return new BrowserCompatSpecEx(this.datepatterns);
	}

}