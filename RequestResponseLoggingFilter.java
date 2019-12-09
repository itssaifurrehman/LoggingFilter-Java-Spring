import java.io.IOException;
import java.util.logging.LogRecord;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestResponseLoggingFilter implements Filter {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private LogControllerConfigurationService logService;
	private LogControllerConfiguration configuration;
	private String requestBody = null;
	private String responseBody = null;
	JSONObject jsonObject = null;
	JSONObject jsonObjectResponse = null;

	public RequestResponseLoggingFilter(LogControllerConfigurationService logService) {
		this.logService = logService;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String logRequestTemplate = "[ STARTED ][ #METHOD# REQUEST ][ URL: #URL# ][ REQUEST BODY: #REQUEST_BODY# ][URL PARAMETERS: #URL_PARAMETERS# ]";
		String logResponseTemplate = "[ ENDED ][ #METHOD# REQUEST ][ URL: #URL# ][ RESPONSE BODY: #RESPONSE_BODY# ][EXECUTION TIME: #TIME# miliseconds ]";

		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletCacheWrapper myRequestWrapper = new HttpServletCacheWrapper(req);

		try {
			configuration = logService.findByMethodAndUrl(req.getMethod(), req.getRequestURI());
			if (configuration != null) {
				logRequestTemplate = logRequestTemplate.replace("#METHOD#", configuration.getMethodName());
				logRequestTemplate = logRequestTemplate.replace("#URL#", req.getRequestURL());

				if (configuration.isRequestBody()) {
					requestBody = IOUtils.toString(myRequestWrapper.getInputStream(), req.getCharacterEncoding());
					if (!requestBody.isEmpty() && requestBody != null) {
						try {
							jsonObject = new JSONObject(requestBody);
							configuration = logService.findRequiredParameters(configuration, jsonObject,
									req.getMethod());
							requestBody.isEmpty();
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
					if (!requestBody.isEmpty())
						logRequestTemplate = logRequestTemplate.replace("#REQUEST_BODY#",
								"" + configuration.getRequestBodyListParam());
				}

				if (configuration.isRequestParameters())
					if (req.getQueryString() != null)
						logRequestTemplate = logRequestTemplate.replace("#URL_PARAMETERS#", req.getQueryString());

				logger.info(logRequestTemplate);
				chain.doFilter(myRequestWrapper, res);

				if (configuration.isResponseBody()) {
					response.setCharacterEncoding("UTF-8");
					HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) res);
					chain.doFilter(req, responseCopier);
					responseCopier.flushBuffer();
					byte[] responseDataCopier = responseCopier.getCopy();
					responseBody = IOUtils.toString(responseDataCopier, res.getCharacterEncoding());
					try {
						jsonObjectResponse = new JSONObject(responseBody);
						configuration = logService.findResponseParameters(configuration, jsonObjectResponse);
						responseBody.isEmpty();
						logResponseTemplate = logResponseTemplate.replace("#RESPONSE_BODY#",
								"" + configuration.getResponseBodyListParam());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				long meanTime = (long) request.getAttribute("startTime");
				request.removeAttribute("meanTime");
				long endTime = System.currentTimeMillis() - meanTime;

				logResponseTemplate = logResponseTemplate.replace("#METHOD#", configuration.getMethodName());
				logResponseTemplate = logResponseTemplate.replace("#URL#", req.getRequestURL());
				logResponseTemplate = logResponseTemplate.replace("#TIME#", Long.toString(endTime));
				logger.info(logResponseTemplate);
			} else
				logger.info("[Error] [Request is not in database] [Check database]");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void destroy() {

	}

	public boolean isLoggable(LogRecord record) {
		return false;
	}
}