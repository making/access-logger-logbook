package am.ik.spring.logbook;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.core.HeaderFilters;

public class OpinionatedFilters {

	public static HeaderFilter headerFilter() {
		Set<String> replaceHeaders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		replaceHeaders
			.addAll(Set.of("authorization", "proxy-authenticate", "cookie", "set-cookie", "x-amz-security-token"));
		Set<String> removeHeaders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		removeHeaders.addAll(Set.of("http2-settings", "connection", "access-control-expose-headers",
				"content-security-policy", "referrer-policy", "access-control-allow-origin",
				"strict-transport-security", "access-control-allow-credentials", ":status", "date", "vary", "alt-svc"));
		return HeaderFilter.merge(HeaderFilters.replaceHeaders(replaceHeaders, "***"), HeaderFilter
			.merge(HeaderFilters.removeHeaders(removeHeaders::contains), HeaderFilters.removeHeaders(s -> {
				String headerName = s.toLowerCase(Locale.US);
				return (headerName.startsWith("x-") && !s.startsWith("x-forwarded")) || headerName.startsWith("sec-")
						|| headerName.startsWith("accept") || headerName.startsWith("upgrade")
						|| headerName.startsWith("cf-") || headerName.startsWith("openai-");
			})));
	}

}
