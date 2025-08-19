package am.ik.spring.logbook;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.ClassUtils;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

@AutoConfiguration
@ConditionalOnClass(Logbook.class)
@AutoConfigureBefore(LogbookAutoConfiguration.class)
@ConditionalOnProperty(name = "access-logger.logbook.enabled", havingValue = "true", matchIfMissing = true)
@ImportRuntimeHints(AccessLoggerLogbookAutoConfiguration.AccessLoggerHints.class)
public class AccessLoggerLogbookAutoConfiguration {

	public static final String JAKARTA_SERVLET_HTTP_HTTP_SERVLET_REQUEST = "jakarta.servlet.http.HttpServletRequest";

	@Bean
	@ConditionalOnMissingClass(JAKARTA_SERVLET_HTTP_HTTP_SERVLET_REQUEST)
	public AccessLoggerSink accessLoggerSink() {
		return new AccessLoggerSink();
	}

	@Bean
	@ConditionalOnClass(name = JAKARTA_SERVLET_HTTP_HTTP_SERVLET_REQUEST)
	public ServletAwareAccessLoggerSink servletAwareAccessLoggerSink() {
		return new ServletAwareAccessLoggerSink();
	}

	@Bean
	public AccessLoggerRequestCondition requestCondition() {
		return new AccessLoggerRequestCondition();
	}

	static class AccessLoggerHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			if (ClassUtils.isPresent(JAKARTA_SERVLET_HTTP_HTTP_SERVLET_REQUEST, classLoader)) {
				hints.reflection()
					.registerType(TypeReference.of(JAKARTA_SERVLET_HTTP_HTTP_SERVLET_REQUEST),
							builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
			}
		}

	}

}
