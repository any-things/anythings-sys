package xyz.anythings.sys.event.model;

/**
 * 프로세스 이벤트
 * 
 * @author shortstop
 */
public class ProcessEvent extends SysEvent {
	
	/**
	 * 프로세스 이벤트 유형
	 */
	public static final String PROCESS_EVENT_TYPE = "PROCESS_EVENT_TYPE";
	
	/**
	 * 액션 유형
	 */
	protected String actionType;

	/**
	 * 생성자
	 * 
	 * @param domainId
	 * @param actionType
	 * @param payload
	 */
	public ProcessEvent(Long domainId, String actionType, Object payload) {
		super(domainId, PROCESS_EVENT_TYPE, payload);
		this.actionType = actionType;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

}
