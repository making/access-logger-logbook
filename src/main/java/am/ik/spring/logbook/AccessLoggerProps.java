package am.ik.spring.logbook;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "access-logger")
public record AccessLoggerProps() {
}
