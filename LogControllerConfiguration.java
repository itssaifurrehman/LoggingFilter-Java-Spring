import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "log-controller-configuration")
public class LogControllerConfiguration {

	@Id
	private String _id;
	private String methodName;
	private String urlName;
	private boolean requestBody;
	private boolean requestParameters;
	private boolean responseBody;
	private List<String> requestBodyList = new ArrayList<String>();
	private Map<String, String> requestBodyListParam = new HashMap<String, String>();
	private List<String> responseBodyList = new ArrayList<String>();
	private Map<String, String> responseBodyListParam = new HashMap<String, String>();

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public boolean isRequestBody() {
		return requestBody;
	}

	public void setRequestBody(boolean requestBody) {
		this.requestBody = requestBody;
	}

	public boolean isRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(boolean requestParameters) {
		this.requestParameters = requestParameters;
	}

	public boolean isResponseBody() {
		return responseBody;
	}

	public void setResponseBody(boolean responseBody) {
		this.responseBody = responseBody;
	}

	public List<String> getRequestBodyList() {
		return requestBodyList;
	}

	public void setRequestBodyList(List<String> requestBodyList) {
		this.requestBodyList = requestBodyList;
	}

	public Map<String, String> getRequestBodyListParam() {
		return requestBodyListParam;
	}

	public void addRequestBodyParameter(String param, String value) {
		requestBodyListParam.put(param, value);
	}
	

	public List<String> getResponseBodyList() {
		return responseBodyList;
	}

	public void setResponseBodyList(List<String> responseBodyList) {
		this.responseBodyList = responseBodyList;
	}

	public Map<String, String> getResponseBodyListParam() {
		return responseBodyListParam;
	}

	public void setResponseBodyListParam(Map<String, String> responseBodyListParam) {
		this.responseBodyListParam = responseBodyListParam;
	}
	public void addResponseBodyParameter(String param, String value) {
		responseBodyListParam.put(param, value);
	}

	@Override
	public String toString() {
		return String.format(
				"LogControllerConfiguration [_id=%s, methodName=%s, urlName=%s, requestBody=%s, requestParameters=%s, responseBody=%s, requestBodyList=%s, requestBodyListParam=%s, responseBodyList=%s, responseBodyListParam=%s]",
				_id, methodName, urlName, requestBody, requestParameters, responseBody, requestBodyList,
				requestBodyListParam, responseBodyList, responseBodyListParam);
	}

//	@Override
//	public String toString() {
//		return String.format(
//				"LogControllerConfiguration [_id=%s, methodName=%s, urlName=%s, requestBody=%s, requestParameters=%s, responseBody=%s, requestBodyListParam=%s]",
//				_id, methodName, urlName, requestBody, requestParameters, responseBody, requestBodyListParam);
//	}
	

}
