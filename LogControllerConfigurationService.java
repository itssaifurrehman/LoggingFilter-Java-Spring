import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LogControllerConfigurationService {

	private Map<String, LogControllerConfiguration> cachedConfigurations = new HashMap<String, LogControllerConfiguration>();
	private Map<String, LogControllerConfiguration> requestConfigurations = new HashMap<String, LogControllerConfiguration>();

	private LogControllerConfiguration configurationTemp;
	private LogControllerConfiguration responseConfig;
	private ScheduledExecutorService cleanCacheWorker = Executors.newSingleThreadScheduledExecutor();

	private LogControllerConfigurationRepository repository;

	private Object obj = new Object();

	public LogControllerConfigurationService(LogControllerConfigurationRepository repository) {
		this.repository = repository;
		cleanCacheWorker.scheduleAtFixedRate(() -> {
			cleanCache();
		}, 0, 30, TimeUnit.MINUTES);
	}

	public LogControllerConfigurationRepository getRepository() {
		return repository;
	}

	public void setRepository(LogControllerConfigurationRepository repository) {
		this.repository = repository;
	}

	@Cacheable("cacheService")
	public LogControllerConfiguration findByMethodAndUrl(String method, String url) {

		synchronized (obj) {
			try {
				if (cachedConfigurations == null || cachedConfigurations.isEmpty()) {
					List<LogControllerConfiguration> configurations = repository.findAll();
					for (LogControllerConfiguration configuration : configurations) {
						//TO get all the Requests where their parameters
						cachedConfigurations.put(configuration.getUrlName(), configuration);
						cachedConfigurations.put(configuration.getMethodName(), configuration);
						requestConfigurations.put(configuration.getUrlName(), configuration);
						requestConfigurations.put(configuration.getMethodName(), configuration);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return matchSearch(method, url);
	}

	public LogControllerConfiguration matchSearch(String method, String url) {
		try {
			for (LogControllerConfiguration logConfig : cachedConfigurations.values()) {

				Pattern urlPattern = Pattern.compile(logConfig.getUrlName());
				Matcher urlmatch = urlPattern.matcher(url);

				Pattern methodPattern = Pattern.compile(logConfig.getMethodName());
				Matcher methodmatch = methodPattern.matcher(method);

				if (urlmatch.matches() && methodmatch.matches())
					return logConfig;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public LogControllerConfiguration findRequiredParameters(LogControllerConfiguration configurationTemp,
			JSONObject jsonObject, String method) {
		this.configurationTemp = configurationTemp;
		try {
			for (LogControllerConfiguration logConfig : requestConfigurations.values()) {
				if (logConfig.getMethodName().equals(method)) {
					for (String param : this.configurationTemp.getRequestBodyList())
						getStringFromJsonObject(param, param, jsonObject);
					return this.configurationTemp;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	private void getStringFromJsonObject(String initialKey, String latterKey, JSONObject jsonObject) {
		String[] tempJSONBody = latterKey.split("\\.");
		if (tempJSONBody.length == 1) {
			if (jsonObject.has(latterKey)) {
				try {
					this.configurationTemp.addRequestBodyParameter(initialKey,
							jsonObject.getJSONObject(latterKey).toString());
				} catch (Exception e) {
					this.configurationTemp.addRequestBodyParameter(initialKey, jsonObject.get(latterKey).toString());
				}
			}
		} else if (tempJSONBody.length > 1 && jsonObject.has(tempJSONBody[0])) {
			try {
				String newTempJSONBody = getLatterKeyData(tempJSONBody);
				getStringFromJsonObject(initialKey, newTempJSONBody, jsonObject.getJSONObject(tempJSONBody[0]));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private String getLatterKeyData(String[] tempJSONBody) {
		try {
			StringBuilder latterData = new StringBuilder("");
			for (int i = 1; i < tempJSONBody.length; i++) {
				latterData.append(tempJSONBody[i]);
			}
			return latterData.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public LogControllerConfiguration findResponseParameters(LogControllerConfiguration responseConfig,
			JSONObject jsonObject) {
		this.responseConfig = responseConfig;
		try {
			for (String param : this.responseConfig.getResponseBodyList())
				responseGetStringFromJsonObject(param, param, jsonObject);
			return this.responseConfig;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	private void responseGetStringFromJsonObject(String initialKey, String latterKey, JSONObject jsonObject) {
		String[] tempJSONBody = latterKey.split("\\.");
		if (tempJSONBody.length == 1) {
			if (jsonObject.has(latterKey)) {
				try {
					this.responseConfig.addResponseBodyParameter(initialKey,
							jsonObject.getJSONObject(latterKey).toString());
				} catch (Exception e) {
					this.responseConfig.addResponseBodyParameter(initialKey, jsonObject.get(latterKey).toString());
				}
			}
		} else if (tempJSONBody.length > 1 && jsonObject.has(tempJSONBody[0])) {
			try {
				String newTempJSONBody = getLatterKeyData(tempJSONBody);
				responseGetStringFromJsonObject(initialKey, newTempJSONBody, jsonObject.getJSONObject(tempJSONBody[0]));
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@CacheEvict(value = "cacheService", allEntries = true)
	private void cleanCache() {
		synchronized (obj) {
			try {
				cachedConfigurations = new HashMap<String, LogControllerConfiguration>();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}