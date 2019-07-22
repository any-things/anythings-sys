package xyz.anythings.sys.process.impl;

import java.util.Map;

import xyz.anythings.sys.event.model.ProcessEvent;
import xyz.anythings.sys.process.api.IExecutionContext;
import xyz.anythings.sys.process.api.ITask;
import xyz.anythings.sys.service.AbstractExecutionService;

/**
 * Task 상위 클래스
 * 
 * @author shortstop
 */
public abstract class AbstractTask extends AbstractExecutionService implements ITask {
	
	/**
	 * 태스크 Id
	 */
	protected String id;
	/**
	 * 태스크 Name
	 */
	protected String name;
	/**
	 * 태스크 설명
	 */
	protected String description;
	/**
	 * 태스크 유형 
	 */
	protected String taskType;
	/**
	 * 태스크가 동기 모드인지 여부
	 */
	protected boolean isSync;
	/**
	 * 태스크 상태
	 */
	protected String status;
	/**
	 * 태스크 파라미터 키
	 */
	protected String[] paramKeys;
	/**
	 * 태스크 결과 키
	 */
	protected String[] resultKeys;
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
	public String getTaskType() {
		return this.taskType;
	}

	@Override
	public Class<?> getRoutingEventType() {
		return ProcessEvent.class;
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
	public boolean isSync() {
		return this.isSync;
	}

	@Override
	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}
	
	@Override
	public void setParamKeys(String[] paramKeys) {
		this.paramKeys = paramKeys;
	}

	@Override
	public String[] getParamKeys() {
		return this.paramKeys;
	}

	@Override
	public void setResultKeys(String[] resultKeys) {
		this.resultKeys = resultKeys;
	}

	@Override
	public String[] getResultKeys() {
		return this.resultKeys;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	@Override
	public abstract boolean execute(IExecutionContext context);

}
