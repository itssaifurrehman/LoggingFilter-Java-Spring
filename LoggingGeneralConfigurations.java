import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import loggingservice.logutils.LogControllerConfigurationService;
import loggingservice.logutils.RequestResponseLoggingFilter;

@Configuration
public class LoggingGeneralConfigurations {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LocalLogControllerConfigurationRepository repository;

	private LogControllerConfigurationService logControllerConfigurationService;

	@PostConstruct
	private void init() {
		logControllerConfigurationService = new LogControllerConfigurationService(repository);
	}

	@Bean
	public LogControllerConfigurationService logControllerConfigurationService() {
		return logControllerConfigurationService;
	}

	@Bean
	public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
		try {
			FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
			registrationBean.setFilter(new RequestResponseLoggingFilter(logControllerConfigurationService));
			return registrationBean;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
