package xyz.anythings.sys.event.model;

import java.lang.reflect.Field;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.exception.ElidomException;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.entity.ErrorLog;
import xyz.elidom.sys.entity.User;
import xyz.elidom.sys.util.ExceptionUtil;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.ClassUtil;
import xyz.elidom.util.DateUtil;

/**
 * 시스템 에러 이벤트 
 * 
 * @author shortstop
 */
public class ErrorEvent extends SysEvent {

	/**
	 * 파일 로깅 여부
	 */
	private boolean fileLoggingFlag;
	/**
	 * DB 로깅 여부
	 */
	private boolean dbLoggingFlag;
	/**
	 * 예외
	 */
	private ElidomException exception;
	
	public ErrorEvent() {
		this(Domain.currentDomainId());
	}
	
	public ErrorEvent(Long domainId) {
		this.setDomainId(domainId);
		this.payload = new ErrorLog();
	}
	
	public ErrorEvent(Long domainId, boolean fileLoggingFlag, boolean dbLoggingFlag, Exception exception, String errorType, String parameters, String errorContent) {
		this(domainId);
		this.setFileLoggingFlag(fileLoggingFlag);
		this.setDbLoggingFlag(dbLoggingFlag);
		this.setException(exception);
		this.payload = this.wrapError(this.exception, errorType, parameters, null);
	}
	
	public boolean isFileLoggingFlag() {
		return fileLoggingFlag;
	}

	public void setFileLoggingFlag(boolean fileLoggingFlag) {
		this.fileLoggingFlag = fileLoggingFlag;
	}

	public boolean isDbLoggingFlag() {
		return dbLoggingFlag;
	}

	public void setDbLoggingFlag(boolean dbLoggingFlag) {
		this.dbLoggingFlag = dbLoggingFlag;
	}
	
	public ElidomException getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception == null ? null : ExceptionUtil.wrapElidomException(exception);
	}

	public void setErrorLog(ErrorLog errorLog) {
		this.payload = errorLog;
	}
	
	public ErrorLog getErrorLog() {
		return (ErrorLog)this.payload;
	}
	
	/**
	 * ErrorLog 객체를 생성
	 * 
	 * @param exception
	 * @param errorType
	 * @param parameters
	 * @param errorContent
	 * @return
	 */
	protected ErrorLog wrapError(ElidomException exception, String errorType, String parameters, String errorContent) {				
		ErrorLog errLog = new ErrorLog();
		String code = exception.getCode();
		
		if(ValueUtil.isNotEmpty(code)) {
			Field field = ClassUtil.getField(ErrorLog.class, "code");
			
			if (ValueUtil.isNotEmpty(field)) {
				int codeSize = field.getAnnotation(Column.class).length();
				if(code.length() > codeSize) {
					errLog.setHeader(code);
					code = null;
				}
			}
		}
		
		errLog.setDomainId(this.domainId);
		errLog.setCode(code);
		errLog.setStatus(String.valueOf(exception.getStatus()));
		errLog.setErrorType(errorType == null ? exception.getTitle() : errorType);
		errLog.setParams(parameters);
		errLog.setMessage(exception.getMessage());
		errLog.setIssueDate(DateUtil.currentTimeStr());
		String traceStr = ValueUtil.isEmpty(errorContent) ? ExceptionUtil.getErrorStackTraceToString(exception.getCause() != null ? exception.getCause() : exception) : errorContent;
		errLog.setStackTrace(traceStr);
		String userId = User.currentUser() != null ? User.currentUser().getId() : null;
		errLog.setCreatorId(userId);
		errLog.setUpdaterId(userId);
		return errLog;
	}
}
