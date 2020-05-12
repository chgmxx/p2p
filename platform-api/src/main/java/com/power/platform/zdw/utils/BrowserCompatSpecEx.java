package com.power.platform.zdw.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.impl.cookie.BasicCommentHandler;
import org.apache.http.impl.cookie.BasicDomainHandler;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.impl.cookie.BasicMaxAgeHandler;
import org.apache.http.impl.cookie.BasicPathHandler;
import org.apache.http.impl.cookie.BasicSecureHandler;
import org.apache.http.impl.cookie.BrowserCompatVersionAttributeHandler;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.impl.cookie.NetscapeDraftHeaderParser;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class BrowserCompatSpecEx extends CookieSpecBase {

	public static String NAME = "compatibilityEx";
	static final String[] DEFAULT_DATE_PATTERNS = new String[] { DateUtils.PATTERN_RFC1123, DateUtils.PATTERN_RFC1036, DateUtils.PATTERN_ASCTIME, "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy HH:mm:ss" };

	private final String[] datepatterns;

	public enum SecurityLevel {
		SECURITYLEVEL_DEFAULT, SECURITYLEVEL_IE_MEDIUM, SECURITYLEVEL_NO
	}

	/** Default constructor */
	public BrowserCompatSpecEx(final String[] datepatterns, final SecurityLevel securityLevel) {

		super();
		if (datepatterns != null) {
			this.datepatterns = datepatterns.clone();
		} else {
			this.datepatterns = DEFAULT_DATE_PATTERNS;
		}
		switch (securityLevel) {
			case SECURITYLEVEL_DEFAULT:
				registerAttribHandler(ClientCookie.PATH_ATTR, new BasicPathHandler());
				break;
			case SECURITYLEVEL_IE_MEDIUM:
				registerAttribHandler(ClientCookie.PATH_ATTR, new BasicPathHandler() {

					@Override
					public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {

						// No validation
					}
				});
				break;
			default:
				throw new RuntimeException("Unknown security level");
		}

		registerAttribHandler(ClientCookie.DOMAIN_ATTR, new BasicDomainHandler() {

			@Override
			public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {

				// No validation
			}
		});
		// registerAttribHandler(ClientCookie.DOMAIN_ATTR, new BasicDomainHandler());
		registerAttribHandler(ClientCookie.MAX_AGE_ATTR, new BasicMaxAgeHandler());
		registerAttribHandler(ClientCookie.SECURE_ATTR, new BasicSecureHandler());
		registerAttribHandler(ClientCookie.COMMENT_ATTR, new BasicCommentHandler());
		registerAttribHandler(ClientCookie.EXPIRES_ATTR, new BasicExpiresHandler(this.datepatterns));
		registerAttribHandler(ClientCookie.VERSION_ATTR, new BrowserCompatVersionAttributeHandler());
	}

	/** Default constructor */
	public BrowserCompatSpecEx(final String[] datepatterns) {

		this(datepatterns, SecurityLevel.SECURITYLEVEL_IE_MEDIUM);
	}

	/** Default constructor */
	public BrowserCompatSpecEx() {

		this(null, SecurityLevel.SECURITYLEVEL_IE_MEDIUM);
	}

	public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {

		Args.notNull(header, "Header");
		Args.notNull(origin, "Cookie origin");
		final String headername = header.getName();
		if (!headername.equalsIgnoreCase(SM.SET_COOKIE)) {
			throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
		}
		HeaderElement[] helems = header.getElements();
		boolean versioned = false;
		boolean netscape = false;
		for (final HeaderElement helem : helems) {
			if (helem.getParameterByName("version") != null) {
				versioned = true;
			}
			if (helem.getParameterByName("expires") != null) {
				netscape = true;
			}
		}
		if (netscape || !versioned) {
			// Need to parse the header again, because Netscape style cookies do not
			// correctly
			// support multiple header elements (comma cannot be treated as an element
			// separator)
			final NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
			final CharArrayBuffer buffer;
			final ParserCursor cursor;
			if (header instanceof FormattedHeader) {
				buffer = ((FormattedHeader) header).getBuffer();
				cursor = new ParserCursor(((FormattedHeader) header).getValuePos(), buffer.length());
			} else {
				final String s = header.getValue();
				if (s == null) {
					throw new MalformedCookieException("Header value is null");
				}
				buffer = new CharArrayBuffer(s.length());
				buffer.append(s);
				cursor = new ParserCursor(0, buffer.length());
			}
			helems = new HeaderElement[] { parser.parseHeader(buffer, cursor) };
		}
		return parse(helems, origin);
	}

	private static boolean isQuoteEnclosed(final String s) {

		return s != null && s.startsWith("\"") && s.endsWith("\"");
	}

	public List<Header> formatCookies(final List<Cookie> cookies) {

		Args.notEmpty(cookies, "List of cookies");
		final CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
		buffer.append(SM.COOKIE);
		buffer.append(": ");
		for (int i = 0; i < cookies.size(); i++) {
			final Cookie cookie = cookies.get(i);
			if (i > 0) {
				buffer.append("; ");
			}
			final String cookieName = cookie.getName();
			final String cookieValue = cookie.getValue();
			if (cookie.getVersion() > 0 && !isQuoteEnclosed(cookieValue)) {
				BasicHeaderValueFormatter.INSTANCE.formatHeaderElement(buffer, new BasicHeaderElement(cookieName, cookieValue), false);
			} else {
				// Netscape style cookies do not support quoted values
				buffer.append(cookieName);
				buffer.append("=");
				if (cookieValue != null) {
					buffer.append(cookieValue);
				}
			}
		}
		final List<Header> headers = new ArrayList<Header>(1);
		headers.add(new BufferedHeader(buffer));
		return headers;
	}

	public int getVersion() {

		return 0;
	}

	public Header getVersionHeader() {

		return null;
	}

	@Override
	public String toString() {

		return "compatibilityEx";
	}

}