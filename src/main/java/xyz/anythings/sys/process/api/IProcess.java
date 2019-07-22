package xyz.anythings.sys.process.api;

import java.util.List;
import java.util.Map;

import xyz.anythings.sys.event.model.ProcessEvent;

/**
 * 여러 개의 태스크를 조합하여 하나의 서브 프로세스를 정의하고 실행 
 * 
 * @author shortstop
 */
public interface IProcess {
	
	/**
	 * 프로세스 Id
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * 프로세스 명
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 프로세스 설명
	 * 
	 * @return
	 */
	public String getDescription();
	
	/**
	 * taskConfig로 부터 태스크 구성
	 * taskConfig는 설정으로 부터 읽어들인다. 
	 * 
	 * @param taskConfig
	 */
	public void configureTasks(Object taskConfig);

	/**
	 * 실행될 태스크 ID - 태스크 순서 정보 쌍을 리턴 
	 * 
	 * @return
	 */
	//public Map<String, TaskSeq> taskMap();
		
	/**
	 * 프로세스 실행을 위한 변수 설정 
	 * 
	 * @param variables
	 */
	public void setVariables(Map<String, Object> variables);
	
	/**
	 * 프로세스 실행을 위한 변수 리턴 
	 * 
	 * @return
	 */
	public Map<String, Object> getVariables();
	
	/**
	 * 프로세스 초기화
	 * 
	 * @param context
	 */
	public void initializeProcess(IExecutionContext context);
	
	/**
	 * 프로세스 종료
	 * 
	 * @param context
	 */
	public void finalizeProcess(IExecutionContext context);
		
	/**
	 * 이벤트가 이 프로세스를 실행할 이벤트가 맞는지 체크  
	 * 
	 * @param event
	 * @return
	 */
	public boolean isMatchEvent(ProcessEvent event);
	
	/**
	 * 이벤트로 부터 처리할 Task를 찾아 리턴. 못 찾으면 null 리턴  
	 * 
	 * @param event
	 * @return
	 */
	public ITask routeTask(ProcessEvent event);
	
	/**
	 * 첫번째 태스크를 리턴 
	 * 
	 * @return
	 */
	public ITask firstTask();
	
	/**
	 * task의 다음 실행할 태스크 리스트를 찾아서 리턴  
	 * 
	 * @param task
	 * @return
	 */
	public List<ITask> nextTasks(ITask task);
	
	/**
	 * 프로세스가 실행 가능한 상태인지 체크 
	 * 
	 * @param context
	 * @return
	 */
	public boolean isExecutable(IExecutionContext context);
	
	/**
	 * 프로세스 실행 
	 * 
	 * @param context
	 * @return
	 */
	public IExecutionContext execute(IExecutionContext context);
	
	/**
	 * 예외 처리
	 * 
	 * @param context
	 */
	public void handleException(IExecutionContext context);
	
}
