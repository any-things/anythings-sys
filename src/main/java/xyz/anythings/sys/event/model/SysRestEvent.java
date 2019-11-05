package xyz.anythings.sys.event.model;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Anythings Rest 최상위 이벤트
 * 
 * @author yang
 */
public class SysRestEvent extends SysEvent{
	/**
	 * REST 호출 PATH
	 */
	public String restPath;
	
	/**
	 * 요청 형식  
	 */
	public RequestMethod requestMethod;
	
	/**
	 * url 뒤 parameters
	 */
	public Map<String,Object> requestParams;
	/**
	 * rest Put Body
	 */
	public Map<String,Object> requestPutBody;
	/**
	 * rest Post Body
	 */
	public List<Map<String,Object>> requestPostBody;
	
	/**
	 * 기본 생성자
	 */
	public SysRestEvent(long domainId, String restPath, RequestMethod requestMethod) {
		this(domainId,restPath,requestMethod, null);
	}
	
	/**
	 * 생성자
	 * @param domainId
	 */
	public SysRestEvent(long domainId, String restPath, RequestMethod requestMethod, Map<String,Object> requestParams) {
		super(domainId,null);
		this.setRestPath(restPath);
		this.setRequestMethod(requestMethod);
		this.setRequestParams(requestParams);
	}
	

	public String getRestPath() {
		return restPath;
	}

	public void setRestPath(String restPath) {
		this.restPath = restPath;
	}
	
	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public Map<String, Object> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(Map<String, Object> requestParams) {
		this.requestParams = requestParams;
	}

	public Map<String, Object> getRequestPutBody() {
		return requestPutBody;
	}

	public void setRequestPutBody(Map<String, Object> requestPutBody) {
		this.requestPutBody = requestPutBody;
	}

	public List<Map<String, Object>> getRequestPostBody() {
		return requestPostBody;
	}

	public void setRequestPostBody(List<Map<String, Object>> requestPostBody) {
		this.requestPostBody = requestPostBody;
	}
	
	public boolean checkCondition(String restPath) {
		if(this.isExecuted()) return false;
		
		if(this.getRestPath().startsWith(restPath) == false) return false;
		
		return true;
	}

}
