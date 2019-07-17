package xyz.anythings.sys.event.model;

/**
 * Anythings 시스템 최상위 이벤트
 * 
 * @author shortstop
 */
public class SysEvent {
	/**
	 * 도메인 ID
	 */
	protected Long domainId;
	/**
	 * 이벤트 유형
	 */
	protected String eventType;
	/**
	 * 이벤트 페이로드
	 */
	protected Object payload;
	
	protected SysEvent() {
	}
	
	public Long getDomainId() {
		return domainId;
	}
	
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
}
