package xyz.anythings.sys.process.impl;

import java.util.List;
import java.util.Map;

import xyz.anythings.sys.event.model.ProcessEvent;
import xyz.anythings.sys.process.api.IExecutionContext;
import xyz.anythings.sys.process.api.IProcess;
import xyz.anythings.sys.process.api.ITask;
import xyz.anythings.sys.service.AbstractExecutionService;

/**
 * 프로세스 상위 클래스
 * 
 * @author shortstop
 */
public abstract class AbstractProcess extends AbstractExecutionService implements IProcess {

	/**
	 * 프로세스 Id
	 */
	protected String id;
	/**
	 * 프로세스 Name
	 */
	protected String name;
	/**
	 * 프로세스 설명
	 */
	protected String description;
	/**
	 * 프로세스 유형 
	 */
	protected String taskType;
	/**
	 * 프로세스 상태
	 */
	protected String status;
	/**
	 * 태스크 구성 환경
	 */
	protected Object taskConfig;
	/**
	 * 태스크 변수
	 */
	protected Map<String, Object> variables;
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void configureTasks(Object taskConfig) {
		this.taskConfig = taskConfig;
	}

	@Override
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@Override
	public Map<String, Object> getVariables() {
		return this.variables;
	}
	
	@Override
	public ITask firstTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ITask> nextTasks(ITask task) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public abstract boolean isMatchEvent(ProcessEvent event);

	@Override
	public abstract boolean isExecutable(IExecutionContext context);
	
	@Override
	public abstract ITask routeTask(ProcessEvent event);

	@Override
	public abstract void initializeProcess(IExecutionContext context);

	@Override
	public abstract void finalizeProcess(IExecutionContext context);

	@Override
	public abstract IExecutionContext execute(IExecutionContext context);

	@Override
	public abstract void handleException(IExecutionContext context);

}
