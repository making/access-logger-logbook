package am.ik.spring.logbook;

import jakarta.servlet.http.HttpServletRequest;
import org.zalando.logbook.HttpRequest;

public class ServletAwareAccessLoggerSink extends AccessLoggerSink {

	@Override
	protected String getUsername(HttpRequest request) {
		if (request instanceof HttpServletRequest httpServletRequest) {
			return httpServletRequest.getRemoteUser();
		}
		return null;
	}

}
