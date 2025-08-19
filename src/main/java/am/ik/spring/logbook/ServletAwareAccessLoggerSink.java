package am.ik.spring.logbook;

import jakarta.servlet.http.HttpServletRequest;
import org.zalando.logbook.ForwardingHttpRequest;
import org.zalando.logbook.HttpRequest;

public class ServletAwareAccessLoggerSink extends AccessLoggerSink {

	@Override
	protected String getUsername(HttpRequest request) {
		if (request instanceof ForwardingHttpRequest forwardingHttpRequest) {
			return getUsername(forwardingHttpRequest.delegate());
		}
		else if (request instanceof HttpServletRequest httpServletRequest) {
			return httpServletRequest.getRemoteUser();
		}
		return null;
	}

}
