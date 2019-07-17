package xyz.anythings.sys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import xyz.anythings.sys.event.model.ErrorEvent;
import xyz.elidom.exception.ElidomException;
import xyz.elidom.sys.entity.ErrorLog;
import xyz.elidom.sys.rest.ErrorLogController;

/**
 * 비동기 예외 처리를 위한 핸들러
 * 
 * @author shortstop
 */
@Component
public class AsyncExceptionHandler {

	/**
	 * logger
	 */
	private Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);
	/**
	 * 에러 로그 컨트롤러
	 */
	@Autowired
	private ErrorLogController errLogCtrl;
	
	/**
	 * 에러 메시지 처리
	 * 
	 * @param errorType
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW) 
	public void handleException(ErrorEvent errorEvent) {
		// 1. 예외 추출
		ErrorLog errLog = errorEvent.getErrorLog();
		ElidomException ex = errorEvent.getException();
		
		// 2. 파일 로깅
		if(errorEvent.isFileLoggingFlag()) {
			if(ex != null) {
				this.logger.error(ex.getMessage(), ex);
			} else {
				this.logger.error(errLog.getStackTrace());
			}
		}
		
		// 3. DB 로깅 여부 확인
		if(errorEvent.isDbLoggingFlag()) {
			this.errLogCtrl.create(errLog);
		}
	}
	
}
