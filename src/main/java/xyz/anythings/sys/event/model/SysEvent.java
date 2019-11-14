package xyz.anythings.sys.event.model;

import xyz.elidom.sys.entity.Domain;

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
	 *  이벤트 처리 결과 
	 */
	protected Object result;
	
	/**
	 * 확장을 위한 Event 파라미터
	 */
	protected Object[] payload;
	
	/**
	 * 이벤트 처리 완료 flag
	 * 이벤트 처리가 오버라이드 된 경우 기본 이벤트를 skip하기 위한 flag
	 */
	protected boolean isExecuted;
	
	/**
	 * 기본 생성자
	 */
	public SysEvent() {
		this(Domain.currentDomainId());
	}
	
	/**
	 * 생성자 1
	 *  
	 * @param domainId
	 */
	public SysEvent(Long domainId) {
		this(domainId, null);
	}
	
	/**
	 * 생성자 2
	 * 
	 * @param domainId
	 * @param payload
	 */
	public SysEvent(Long domainId, Object[] payload) {
		this.domainId = domainId;
		this.payload = payload;
		this.isExecuted = false;
	}
	
	public Long getDomainId() {
		return domainId;
	}
	
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}
	
	public Object[] getPayload() {
		return this.payload;
	}
	
	public void setPayLoad(Object[] payload) {
		this.payload = payload;
	}
	
}
