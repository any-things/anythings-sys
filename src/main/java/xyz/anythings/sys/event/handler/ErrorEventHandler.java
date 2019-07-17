package xyz.anythings.sys.event.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import xyz.anythings.sys.event.model.ErrorEvent;

/**
 * 에러 이벤트 핸들러
 * 
 * @author shortstop
 */
@Component
public class ErrorEventHandler extends AnyEventHandler {

	/**
	 * 에러 이벤트를 처리
	 * 
	 * @param errorEvent
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION, classes = ErrorEvent.class)
	public void handleErrorEvent(ErrorEvent errorEvent) {
		this.errorHandler.handleException(errorEvent);
	}
}
