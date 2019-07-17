package xyz.anythings.sys.event.model;

import java.util.Map;

/**
 * Anythings 공통 이벤트 모델
 * 각 모듈에서는 이 이벤트를 확장하여 사용한다.
 * 
 * @author shortstop
 */
public class AnyEvent extends SysEvent {

	/**
	 * 이벤트 유형
	 */
	protected String eventType;
	/**
	 * 이벤트 목적지
	 */
	protected String eventTarget;
	/**
	 * 액션 유형
	 */
	protected String actionType;
	/**
	 * 이벤트 프로퍼티 
	 */
	protected Map<String, ?> properties;
	/**
	 * 이벤트 페이로드
	 */
	protected Object payload;
	
	protected AnyEvent() {
	}

	public String getEventTarget() {
		return eventTarget;
	}

	public void setEventTarget(String eventTarget) {
		this.eventTarget = eventTarget;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Map<String, ?> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, ?> properties) {
		this.properties = properties;
	}
	
}
