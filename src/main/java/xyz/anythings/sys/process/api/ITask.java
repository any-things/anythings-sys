package xyz.anythings.sys.process.api;

import java.util.Map;

/**
 * 기본 단위 태스크 정의
 * 
 * @author shortstop
 */
public interface ITask {

	/**
	 * 태스크 ID를 리턴 
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * 태스크 이름을 리턴
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 태스크 설명을 리턴
	 * 
	 * @return
	 */
	public String getDescription();
		
	/**
	 * 태스크 타입  
	 * 
	 * @return
	 */
	public String getTaskType();
	
	/**
	 * 실행 프로세스를 찾기 위한 이벤트 타입 
	 * 
	 * @return
	 */
	public Class<?> getRoutingEventType();
		
	/**
	 * 태스크 실행을 위한 변수 설정 
	 * 
	 * @param variables
	 */
	public void setVariables(Map<String, Object> variables);
	
	/**
	 * 태스크 실행을 위한 변수 리턴  
	 * 
	 * @return
	 */
	public Map<String, Object> getVariables();
	
	/**
	 * sync 모드인 지 여부 
	 * 
	 * @return
	 */
	public boolean isSync();
	
	/**
	 * sync 모드인 지 여부 설정 
	 * 
	 * @param isSync
	 */
	public void setSync(boolean isSync);
	
	/**
	 * 실행시 컨텍스트(IExecutionContext)에서 꺼내서 사용할 파라미터 키 설정
	 * 
	 * @param paramKeys
	 */
	public void setParamKeys(String[] paramKeys);
	
	/**
	 * 실행시 컨텍스트(IExecutionContext)에서 꺼내서 사용할 파라미터 키 리턴
	 * 
	 * @return
	 */
	public String[] getParamKeys();
	
	/**
	 * 실행 컨텍스트(IExecutionContext)에 결과 추가시 키로 사용할 이름을 설정 
	 * 
	 * @param resultKey
	 */
	public void setResultKeys(String[] resultKeys);
	
	/**
	 * 실행 컨텍스트(IExecutionContext)에 결과 추가시 키로 사용할 이름을 리턴 
	 * 
	 * @return
	 */
	public String[] getResultKeys();
	
	/**
	 * 태스크 실행 상태 
	 * 
	 * @return
	 */
	public String getStatus();
	
	/**
	 * 태스크 실행
	 * 
	 * @param context
	 * @return
	 */
	public boolean execute(IExecutionContext context);
	
}
