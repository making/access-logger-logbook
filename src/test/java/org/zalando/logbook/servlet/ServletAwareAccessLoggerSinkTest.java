package org.zalando.logbook.servlet;

import am.ik.spring.logbook.ServletAwareAccessLoggerSink;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.ForwardingHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class ServletAwareAccessLoggerSinkTest {

	Correlation correlation = new Correlation() {
		@Override
		public Instant getEnd() {
			return null;
		}

		@Override
		public Duration getDuration() {
			return Duration.ofSeconds(1);
		}

		@Override
		public String getId() {
			return "";
		}

		@Override
		public Instant getStart() {
			return null;
		}
	};

	@Test
	void shouldUsernameContainedIfRemoteUserIsNotNull(CapturedOutput capturedOutput) throws Exception {
		ServletAwareAccessLoggerSink sink = new ServletAwareAccessLoggerSink();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRemoteUser("test@example.com");
		RemoteRequest request = new RemoteRequest(mockRequest, FormRequestMode.OFF);
		LocalResponse response = new LocalResponse(new MockHttpServletResponse(), "2.0");
		sink.write(correlation, request, response);
		assertThat(capturedOutput.toString()).contains("username=\"test@example.com\"");
	}

	@Test
	void shouldUsernameContainedIfRemoteUserIsNotNullForForwardingRequest(CapturedOutput capturedOutput)
			throws Exception {
		ServletAwareAccessLoggerSink sink = new ServletAwareAccessLoggerSink();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setRemoteUser("test@example.com");
		RemoteRequest request = new RemoteRequest(mockRequest, FormRequestMode.OFF);
		LocalResponse response = new LocalResponse(new MockHttpServletResponse(), "2.0");
		sink.write(correlation, (ForwardingHttpRequest) () -> request, response);
		assertThat(capturedOutput.toString()).contains("username=\"test@example.com\"");
	}

	@Test
	void shouldNotUsernameContainedIfRemoteUserIsNull(CapturedOutput capturedOutput) throws Exception {
		ServletAwareAccessLoggerSink sink = new ServletAwareAccessLoggerSink();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		RemoteRequest request = new RemoteRequest(mockRequest, FormRequestMode.OFF);
		LocalResponse response = new LocalResponse(new MockHttpServletResponse(), "2.0");
		sink.write(correlation, request, response);
		assertThat(capturedOutput.toString()).doesNotContain("username=");
	}

	@Test
	void shouldNotUsernameContainedIfRemoteUserIsNullForFowardingRequest(CapturedOutput capturedOutput)
			throws Exception {
		ServletAwareAccessLoggerSink sink = new ServletAwareAccessLoggerSink();
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		RemoteRequest request = new RemoteRequest(mockRequest, FormRequestMode.OFF);
		LocalResponse response = new LocalResponse(new MockHttpServletResponse(), "2.0");
		sink.write(correlation, (ForwardingHttpRequest) () -> request, response);
		assertThat(capturedOutput.toString()).doesNotContain("username=");
	}

}
