package xyz.anythings.sys.process.api;

import java.util.Map;

import xyz.anythings.sys.event.model.ProcessEvent;

/**
 * 프로세스 실행을 위한 Context
 * 
 * @author shortstop
 */
public interface IExecutionContext {
	
	/**
	 * 프로세스를 리턴 
	 * 
	 * @return
	 */
	public IProcess getProcess();
	
	/**
	 * 현재 태스크를 리턴 
	 * 
	 * @return
	 */
	public ITask getCurrentTask();
	
	/**
	 * 현재 태스크를 설정 
	 * 
	 * @param task
	 */
	public void setCurrentTask(ITask task);
	
	/**
	 * 외부로 부터 받은 이벤트 
	 * 
	 * @return
	 */
	public ProcessEvent getReceiveEvent();
	
	/**
	 * 외부로 보낼 이벤트 
	 * 
	 * @return
	 */
	public ProcessEvent getSendEvent();
	
	/**
	 * 외부로 보낼 이벤트 설정 
	 * 
	 * @param sendEvent
	 */
	public void setSendEvent(ProcessEvent sendEvent);
	
	/**
	 * 예외를 리턴
	 * 
	 * @return
	 */
	public Throwable getException();
	
	/**
	 * 예외를 설정 
	 * @param th
	 */
	public void setException(Throwable th);
	
	/**
	 * 데이터 리턴 
	 * 
	 * @return
	 */
	public Map<String, Object> getDataMap();
	
	/**
	 * 데이터 키로 값을 찾아 리턴 
	 * 
	 * @param name
	 * @return
	 */
	public Object getData(String name);
	
	/**
	 * 데이터 설정 
	 * 
	 * @param name
	 * @param value
	 */
	public void setData(String name, Object value);
	
	/**
	 * 데이터 키-name으로 데이터가 존재하는지 체크 
	 *  
	 * @param name
	 * @return
	 */
	public boolean hasData(String name);
	
	/**
	 * 데이터 키-name으로 데이터 제거 
	 * 
	 * @param name
	 * @return
	 */
	public Object removeData(String name);
	
	/**
	 * 컨텍스트 정보 클리어 
	 */
	public void destroyContext();
}
