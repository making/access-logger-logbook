package am.ik.spring.logbook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.zalando.logbook.Sink;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class AccessLoggerLogbookAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner;

	public AccessLoggerLogbookAutoConfigurationTest() {
		contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, LogbookAutoConfiguration.class,
					AccessLoggerLogbookAutoConfiguration.class));
	}

	@Test
	void shouldSupplyBeans() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(AccessLoggerSink.class);
			assertThat(context.getBean(Sink.class)).isInstanceOf(ServletAwareAccessLoggerSink.class);
			assertThat(context.getBeansOfType(Sink.class)).hasSize(1);
			assertThat(context).hasBean("requestCondition");
			assertThat(context).hasSingleBean(AccessLoggerRequestCondition.class);
		});
	}

	@Test
	void shouldSupplyBeansWithoutServletApi() {
		this.contextRunner.withClassLoader(new FilteredClassLoader("jakarta.servlet")).run(context -> {
			assertThat(context).hasSingleBean(AccessLoggerSink.class);
			assertThat(context.getBean(Sink.class)).isNotInstanceOf(ServletAwareAccessLoggerSink.class);
			assertThat(context.getBeansOfType(Sink.class)).hasSize(1);
			assertThat(context).hasBean("requestCondition");
			assertThat(context).hasSingleBean(AccessLoggerRequestCondition.class);
		});
	}

	@Test
	void shouldNotSupplyBeansIfDisabled() {
		this.contextRunner.withPropertyValues("access-logger.logbook.enabled=false").run(context -> {
			assertThat(context).doesNotHaveBean(AccessLoggerSink.class);
			assertThat(context).doesNotHaveBean(AccessLoggerRequestCondition.class);
		});
	}

}