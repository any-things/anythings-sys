package xyz.anythings.sys.process.api;

import xyz.anythings.sys.event.model.ProcessEvent;

/**
 * ProcessEvent를 어디로 처리할 것 인지 결정하여 처리하는 라우터 
 * 
 * @author shortstop
 */
public interface IEventRouter {

	/**
	 * 이벤트를 처리할 대상을 찾아서 이벤트 처리 
	 * 
	 * @param event
	 * @return
	 */
	public boolean route(ProcessEvent event);
	
	/**
	 * 처리할 event를 process에 넘겨주어 바로 실행 
	 * 
	 * @param process
	 * @param event
	 * @return
	 */
	public IExecutionContext execute(IProcess process, ProcessEvent event);
	
}
