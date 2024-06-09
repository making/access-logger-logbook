package am.ik.spring.logbook;

import org.zalando.logbook.Logbook;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(Logbook.class)
@AutoConfigureBefore(LogbookAutoConfiguration.class)
@ConditionalOnProperty(name = "access-logger.logbook.enabled", havingValue = "true", matchIfMissing = true)
public class AccessLoggerLogbookAutoConfiguration {

	@Bean
	public AccessLoggerSink accessLoggerSink() {
		return new AccessLoggerSink();
	}

	@Bean
	public AccessLoggerRequestCondition requestCondition() {
		return new AccessLoggerRequestCondition();
	}

}
