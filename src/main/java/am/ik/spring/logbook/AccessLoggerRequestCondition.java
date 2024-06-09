package am.ik.spring.logbook;

import java.util.function.Predicate;

import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;

public class AccessLoggerRequestCondition implements Predicate<HttpRequest> {

	@Override
	public boolean test(HttpRequest request) {
		String path = request.getPath();
		if (path.equals("/readyz")) {
			return false;
		}
		if (path.equals("/livez")) {
			return false;
		}
		if (path.startsWith("/actuator")) {
			return false;
		}
		if (path.startsWith("/cloudfoundryapplication")) {
			return false;
		}
		HttpHeaders headers = request.getHeaders();
		String userAgent = headers.getFirst("User-Agent");
		if (userAgent != null) {
			userAgent = headers.getFirst("user-agent");
		}
		return userAgent == null || !userAgent.contains("Amazon-Route53-Health-Check-Service");
	}

}
