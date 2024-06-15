package am.ik.spring.logbook;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Origin;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

import org.springframework.util.StringUtils;

public class AccessLoggerSink implements Sink {

	private static final Logger log = LoggerFactory.getLogger("accesslog");

	@Override
	public boolean isActive() {
		return log.isInfoEnabled();
	}

	@Override
	public void write(Precorrelation precorrelation, HttpRequest request) throws IOException {
		// NO-OP
	}

	@Override
	public void write(Correlation correlation, HttpRequest request, HttpResponse response) throws IOException {
		Origin origin = request.getOrigin();
		String kind = switch (origin) {
			case LOCAL -> "client";
			case REMOTE -> "server";
		};
		String method = request.getMethod();
		String url = request.getRequestUri();
		String remote = request.getRemote();
		int status = response.getStatus();
		long duration = correlation.getDuration().toMillis();
		StringBuilder messageBuilder = new StringBuilder().append("kind=")
			.append(kind)
			.append(" method=")
			.append(method)
			.append(" url=\"")
			.append(url)
			.append("\"")
			.append(" status=")
			.append(status)
			.append(" duration=")
			.append(duration);
		LoggingEventBuilder loggingEventBuilder = log.atInfo()
			.addKeyValue("kind", kind)
			.addKeyValue("method", method)
			.addKeyValue("url", url)
			.addKeyValue("status", status)
			.addKeyValue("duration", duration)
			.addKeyValue("host", request.getHost())
			.addKeyValue("path", request.getPath());
		if (origin == Origin.REMOTE) {
			messageBuilder.append(" protocol=\"")
				.append(request.getProtocolVersion())
				.append("\"") //
				.append(" remote=\"")
				.append(remote)
				.append("\""); //
			loggingEventBuilder = loggingEventBuilder.addKeyValue("remote", remote)
				.addKeyValue("protocol", request.getProtocolVersion());
		}
		HttpHeaders headers = request.getHeaders();
		String userAgent = headers.getFirst("User-Agent");
		if (StringUtils.hasLength(userAgent)) {
			messageBuilder.append(" user_agent=\"").append(userAgent).append("\"");
			loggingEventBuilder = loggingEventBuilder.addKeyValue("user_agent", userAgent);
		}
		String referer = headers.getFirst("Referer");
		if (StringUtils.hasLength(referer)) {
			messageBuilder.append(" referer=\"").append(referer).append("\"");
			loggingEventBuilder = loggingEventBuilder.addKeyValue("referer", referer);
		}
		String requestBody = request.getBodyAsString();
		if (StringUtils.hasLength(requestBody)) {
			messageBuilder.append(" request_body=\"").append(escape(requestBody)).append("\"");
		}
		String responseBody = response.getBodyAsString();
		if (StringUtils.hasLength(responseBody)) {
			messageBuilder.append(" response_body=\"").append(escape(responseBody)).append("\"");
		}
		loggingEventBuilder.log(messageBuilder.toString());
	}

	private static String escape(String input) {
		if (input == null) {
			return "";
		}

		StringBuilder escapedString = new StringBuilder();
		for (char c : input.toCharArray()) {
			switch (c) {
				case '"':
					escapedString.append("\\\"");
					break;
				case '\\':
					escapedString.append("\\\\");
					break;
				case '\b':
					escapedString.append("\\b");
					break;
				case '\f':
					escapedString.append("\\f");
					break;
				case '\n':
					escapedString.append("\\n");
					break;
				case '\r':
					escapedString.append("\\r");
					break;
				case '\t':
					escapedString.append("\\t");
					break;
				default:
					if (c <= 0x1F) {
						escapedString.append(String.format("\\u%04x", (int) c));
					}
					else {
						escapedString.append(c);
					}
					break;
			}
		}
		return escapedString.toString();
	}

}