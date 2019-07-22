package xyz.anythings.sys.event.handler;

import org.springframework.beans.factory.annotation.Autowired;

import xyz.anythings.sys.service.AbstractExecutionService;
import xyz.anythings.sys.service.AsyncExceptionHandler;

/**
 * 공통 이벤트 핸들러
 * 
 * @author shortstop
 */
public class AnyEventHandler extends AbstractExecutionService {

	/**
	 * 비동기 예외 핸들러
	 */
	@Autowired
	protected AsyncExceptionHandler errorHandler;

}
