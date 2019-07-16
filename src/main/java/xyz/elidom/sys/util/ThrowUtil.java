/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.sys.util;

import java.util.List;

import xyz.anythings.sys.AnythingsSysConstants;
import xyz.anythings.sys.ConfigConstants;
import xyz.elidom.exception.client.ElidomBadRequestException;
import xyz.elidom.exception.client.ElidomInvalidParamsException;
import xyz.elidom.exception.client.ElidomRecordNotFoundException;
import xyz.elidom.exception.client.ElidomServiceNotFoundException;
import xyz.elidom.exception.client.ElidomUnauthorizedException;
import xyz.elidom.exception.server.ElidomScriptRuntimeException;
import xyz.elidom.exception.server.ElidomServiceException;
import xyz.elidom.exception.server.ElidomValidationException;
import xyz.elidom.sys.SysMessageConstants;

/**
 * 메시지가 적용된 Exception 생성을 위한 공통 유틸리티 
 * 
 * @author shortstop
 */
public class ThrowUtil {

	/**
	 * key가 존재하지 않는 경우 발생하는 예외	
	 * 	- 키 파라미터에서 빈 키를 발견했습니다. 
	 * 	- Empty key found at key parameters
	 * 
	 * @return
	 */
	public static ElidomInvalidParamsException newNotFoundKey() {
		return new ElidomInvalidParamsException(SysMessageConstants.NOT_FOUND_KEYS, "Empty key found at key parameters");
	}
	
	/**
	 * key가 유효하지 않은 경우 발생하는 예외 
	 * 	- 엔티티의 키가 올바르지 않습니다.
	 * 	- Invalid keys of entity!
	 * 
	 * @param keys
	 * @return
	 */
	public static ElidomInvalidParamsException newInvalidKey(Object... keys) {
		String[] keyStrs = new String[keys.length];
		for(int i = 0 ; i < keys.length ; i++) {
			keyStrs[i] = keys[i].toString();
		}
		
		return new ElidomInvalidParamsException(SysMessageConstants.INVALID_KEYS, "Invalid keys of entity!", MessageUtil.params(keyStrs));
	}
	
	/**
	 * 파일이 유효하지 않은 경우 발생하는 예외 
	 * 	- [{0}] (은)는 유효한 파일이 아닙니다.
	 * 	- Invalid file [{0}]
	 * 
	 * @param filename
	 * @param th
	 * @return
	 */
	public static ElidomInvalidParamsException newInvalidFile(String filename, Throwable th) {
		return new ElidomInvalidParamsException(SysMessageConstants.INVALID_FILE, "Invalid file [{0}]", MessageUtil.params(filename), th);
	}
	
	/**
	 * 엔티티에 cudFlag 메소드가 없는 경우 발생하는 예외 
	 * 	- Entity[{0}] 중 'cudFlag가 존재하지 않습니다.
	 * 	- There is no 'cudFlag' in the entity class [{0}]
	 * 
	 * @param className
	 * @return
	 */
	public static ElidomInvalidParamsException newNotFoundCudFlagMethod(String className) {
		return new ElidomInvalidParamsException(SysMessageConstants.NOT_FOUND_CUD_FLAG, "There is no 'cudFlag' in the entity class [{0}]", MessageUtil.params(className));
	}
	
	/**
	 * 엔티티에 cudFlag 값이 없는 경우 발생하는 예외 
	 * 	- cudFlag의 값을 가져오는데 실패하였습니다.
	 * 	- Failed to get cudFlag value
	 * 
	 * @return
	 */
	public static ElidomInvalidParamsException newNotFoundCudFlagValue() {
		return new ElidomInvalidParamsException(SysMessageConstants.EMPTY_CUD_FLAG_VALUE, "Failed to get cudFlag value");
	}
	
	/**
	 * ClassName이 존재하지 않는 경우 발생하는 예외 
	 * 	- 클래스 ({1}) 을(를) 찾을수 없습니다.
	 * 	- Class ({1}) not found.
	 * 
	 * @param className
	 * @return
	 */
	public static ElidomValidationException newNotFoundClass(String className) {
		return new ElidomValidationException(SysMessageConstants.NOT_FOUND, "{0} ({1}) not found.", MessageUtil.params("terms.label.class", className));
	}
	
	/**
	 * Class를 instantiate 할 때 실패한 경우 발생하는 예외 
	 * 	- {0}은(는) 인스턴스화 할수 없습니다. ({1})
	 * 	- {0} could not be instantiated ({1})
	 * 
	 * @param className
	 * @return
	 */
	public static ElidomValidationException newNotInstantiated(String className, Throwable th) {
		return new ElidomValidationException(SysMessageConstants.COULD_NOT_INSTANTIATE, "{0} could not be instantiated ({1})", MessageUtil.params("terms.label.class", className), th);
	}
	
	/**
	 * class가 mustBeClassName이 아닌 경우 발생하는 예외 
	 * 	- 유효하지 않은 {0} 클래스 ({1}) - 클래스가 {2}의 구현체가 아닙니다.
	 * 	- Invalid {0} Class ({1}) - Class is not instance of {2}.
	 * 
	 * @param className
	 * @param mustBeClassName
	 * @return
	 */
	public static ElidomValidationException newHandlerIsNotAInstanceOf(String className, String mustBeClassName) {
		return new ElidomValidationException(SysMessageConstants.IS_NOT_INSTANCE_OF, "Invalid {0} Class ({1}) - Class is not a instance of {2}.", MessageUtil.params("terms.label.handler", className, mustBeClassName));
	}
	
	/**
	 * setFieldValue 메소드 호출 실패시 발생하는 예외
	 * 	- 클래스 ({0})의 필드({1})에 값 설정 실패
	 * 	- Failed to assign value to field({1}) of class ({0})
	 * 
	 * @param className
	 * @param fieldName
	 * @return
	 */
	public static ElidomServiceException newFailToSetFieldValue(String className, String fieldName) {
		return new ElidomServiceException(SysMessageConstants.FAIL_TO_REFLECT_SET, "Failed to assign value to field({1}) of class ({0})", MessageUtil.params(className, fieldName));
	}
	
	/**
	 * getFieldValue 메소드 호출 실패시 발생하는 예외
	 * 	- 클래스 ({0})의 필드({1})에 값 추출 실패
	 * 	- Failed to get value from field({1}) of class ({0})
	 * 
	 * @param className
	 * @param fieldName
	 * @return
	 */
	public static ElidomServiceException newFailToGetFieldValue(String className, String fieldName) {
		return new ElidomServiceException(SysMessageConstants.FAIL_TO_REFLECT_GET, "Failed to assign value from field({1}) of class ({0})", MessageUtil.params(className, fieldName));
	}
	
	/**
	 * 엔티티의 ID Type이 유효하지 않은 경우 발생하는 예외 
	 * 	- 엔티티 ({0})의 ID Type ({1})이 유효하지 않습니다.
	 * 	- Invalid ID Type ({1}) of entity ({0})
	 * 
	 * @param entityName
	 * @param idType
	 * @return
	 */
	public static ElidomValidationException newInvalidEntityIdType(String entityName, String idType) {
		return new ElidomValidationException(SysMessageConstants.INVALID_ENTITY_ID_TYPE, "Invalid ID Type ({1}) of entity ({0})", MessageUtil.params(entityName, idType));
	}
	
	/**
	 * 엔티티의 ID Type의 데이터 타입이 유효하지 않은 경우 발생하는 예외 
	 * 	- 엔티티 ({0})의 ID Type ({1})의 데이터 타입은 반드시 ({2}) 이어야 합니다.
	 * 	- Data type must be ({2}) of ID Type ({1}) of entity ({0})
	 * 
	 * @param entityName
	 * @param idType
	 * @param dataType
	 * @return
	 */
	public static ElidomValidationException newInvalidDataTypeOfEntityIdType(String entityName, String idType, String dataType) {
		return new ElidomValidationException(SysMessageConstants.INVALID_DATA_TYPE_OF_ENTITY_ID_TYPE, "Data type must be ({2}) of ID Type ({1}) of entity ({0})", MessageUtil.params(entityName, idType, dataType));
	}
	
	/**
	 * 엔티티에 유니크 필드 정보가 존재하지 않은 경우 발생하는 예외 
	 * 	- 엔티티 ({0})에 uniqueFields 정보가 존재하지 않습니다.
	 * 	- Not found uniqueFields in entity ({0})
	 * 
	 * @param entityName
	 * @return
	 */
	public static ElidomValidationException newNotFoundUniqueFieldInEntity(String entityName) {
		return new ElidomValidationException(SysMessageConstants.NOT_FOUND_UNIQUE_FIELDS, "Not found uniqueFields in entity ({0})", MessageUtil.params(entityName));
	}
	
	/**
	 * 엔티티에 Detail Object가 존재하여 삭제할 수가 없는 경우 발생하는 예외 
	 * 	- 엔티티 ({0})에 Detail Data가 존재합니다. Detail 데이터 삭제 후 다시 시도해주세요.
	 * 	- There are detail data in entity ({0}), Please re-try after deleting detail data.
	 * 
	 * @param entityName
	 * @return
	 */
	public static ElidomServiceException newCannotDeleteCauseDetailExist(String entityName) {
		return new ElidomServiceException(SysMessageConstants.HAS_DETAIL_DATA, "There are detail data in entity ({0}), Please re-try after deleting detail data.", MessageUtil.params(entityName));
	}
	
	/**
	 * Detail data 삭제시에 발생하는 예외 
	 * 
	 * @param detailEntityName
	 * @param e
	 * @return
	 */
	public static ElidomServiceException newFailToDeleteDetailEntityData(String detailEntityName, Exception e) {
		return new ElidomServiceException("Failed to delete detail entity [" + detailEntityName + "] - " + e.getMessage());
	}
	
	/**
	 * 비활성화된 계정을 가진 사용자가 로그인 시도를 했을 때의 예외 
	 * 	- 비활성화된 계정입니다.
	 * 	- Deactivated account.
	 * 
	 * @return
	 */
	public static ElidomValidationException newDeactivatedAccount() {
		return new ElidomValidationException(SysMessageConstants.USER_INACTIVATED_ACCOUNT, "Inactive account.");
	}
	
	/**
	 * 요청한 서버 정보로 부터 도메인 정보를 찾을 수 없는 경우 예외 
	 * 	- 요청한 서버 정보 ({0})로 Domain 정보를 찾을 수 없습니다.
	 * 	- Cannot find Domain information by request server ({0})
	 * 
	 * @param requestServerName
	 * @return
	 */
	public static ElidomValidationException newDomainNotExist(String requestServerName) {
		return new ElidomValidationException(SysMessageConstants.DOMAIN_NOT_EXIST, MessageUtil.params(requestServerName));
	}
	
	/**
	 * 시스템 도메인 삭제 시도시 예외 발생 
	 * 	- 도메인 ({0})는 시스템 도메인이므로 삭제할 수 없습니다.
	 * 	- Domain ({0}) is System Domain, so it's not possible to delete!
	 * 
	 * @param domainName
	 * @return
	 */
	public static ElidomValidationException newCannotDeleteSystemDomain(String domainName) {
		return new ElidomValidationException(SysMessageConstants.SYSTEM_DOMAIN_CANNOT_BE_DELETED, MessageUtil.params(domainName));
	}
	
	/**
	 * 개발 모드가 아닌 경우에 위험한 실행 요청을 받은 경우 예외   
	 * 	- 개발 모드가 아닌 경우 이 요청은 실행될 수 없습니다. 
	 * 	- This request is allowed only Development Mode.
	 * 
	 * @return
	 */
	public static ElidomValidationException newNotSupportFunctionOnlyDevMode() {
		return new ElidomValidationException(SysMessageConstants.DEV_MODE_FUNCTION_NOT_SUPPORTED);
	}
	
	/**
	 * 중복된 데이터가 존재하는 경우 예외 
	 *  - ({0}) 데이터 ({1})은 이미 존재 합니다.
	 *  - ({0)) Data ({1}) already exist.
	 * 
	 * @return
	 */
	public static ElidomValidationException newDataDuplicated(String type, String data) {
		List<String> params = ThrowUtil.toTypeDataParams(type, data);
		return new ElidomValidationException(SysMessageConstants.DATA_DUPLICATED, params);
	}	
	
	/**
	 * 데이터가 존재하지 않는 경우 예외 
	 * 	- {0}({1}) 을(를) 찾을수 없습니다.
	 * 	- {0}({1}) not found.
	 * 
	 * @param type
	 * @param data
	 * @return
	 */
	public static ElidomRecordNotFoundException newNotFoundRecord(String type, String data) {
		List<String> params = ThrowUtil.toTypeDataParams(type, data);
		return new ElidomRecordNotFoundException(SysMessageConstants.NOT_FOUND, params);
	}	
	
	/**
	 * 첨부파일 Root Path가 존재하지 않는 경우 예외 
	 * 	- 파라미터 {0}은 빈 값을 허용하지 않습니다. 
	 * 	- Empty {0} is not allowed!
	 * 
	 * @return
	 */
	public static ElidomServiceException newEmptyRootPath() {
		return new ElidomServiceException(SysMessageConstants.EMPTY_PARAM, MessageUtil.params("terms.label.storage_root"));
	}
	
	/**
	 * 빈 {info} 값은 허용하지 않습니다. 
	 * 	- 파라미터 {info}은 빈 값을 허용하지 않습니다. 
	 * 	- Empty {info} is not allowed!
	 * 
	 * @return
	 */
	public static ElidomServiceException newNotAllowedEmptyInfo(String info) {
		return new ElidomServiceException(SysMessageConstants.EMPTY_PARAM, MessageUtil.params(info));
	}
	
	/**
	 * 첨부파일 Root Path가 올바르지 않는 경우 예외 
	 * 	- Storage Root의 데이터({rootPath})가 유효하지 않습니다.
	 * 	- Data ({rootPath}) of Storage Root Path is invalid.
	 * 
	 * @param rootPath
	 * @return
	 */
	public static ElidomServiceException newInvalidRootPath(String rootPath) {
		return new ElidomServiceException(SysMessageConstants.INVALID_DATA, MessageUtil.params("terms.label.storage_root", rootPath));
	}
	
	/**
	 * Map 생성시 Key와 Value쌍의 개수가 맞지 않은 경우 예외 
	 * 	- keys count and values count mismatch!
	 * 	- 키 카운트와 벨류 카운트가 일치하지 않습니다.
	 * 
	 * @return
	 */
	public static ElidomServiceException newMismatchMapKeyValue() {
		return new ElidomServiceException(SysMessageConstants.KEY_AND_VALUE_MISMATCH, "keys count and values count mismatch!");
	}
	
	/**
	 * Data 복사에 실패한 경우 예외
	 * 	- Data 복사 하는 중 에러가 발생했습니다.
	 * 	- Failed to clone data.
	 * 
	 * @return
	 */
	public static ElidomServiceException newFailToCloneData(){
		return new ElidomServiceException(SysMessageConstants.FAIL_TO_CLONE_DATA, "Failed to clone data.");
	}
	
	/**
	 * 파일 읽기 도중 에러가 발생하는 경우 예외 
	 * 	- File {filename} 읽기 중 에러가 발생하였습니다.
	 * 	- Failed to read file {filename}.
	 * 
	 * @param filename
	 * @return
	 */
	public static ElidomServiceException newFailToReadFileContent(String filename) {
		return new ElidomServiceException(SysMessageConstants.READ_FILE_ERROR, MessageUtil.params(filename));
	}
	
	/**
	 * 파일에 존재하지 않는 경우 예외 
	 * 	- 파일({filename})을 찾을수가 없습니다.
	 * 	- File({filename}) Not Found.
	 * 
	 * @param filename
	 * @return
	 */	
	public static ElidomServiceException newNotFoundFile(String filename) {
		return new ElidomServiceException(SysMessageConstants.FILE_NOT_FOUND, MessageUtil.params(filename));
	}
	
	/**
	 * 파일 생성 중 발생하는 예외 
	 * 	- File 생성 중 에러가 발생하였습니다.
	 * 	- Error occured during file creating.
	 * 
	 * @return
	 */
	public static ElidomServiceException newFailToCreateFile() {
		return new ElidomServiceException(SysMessageConstants.CREATE_FILE_ERROR, "File 생성 중 에러가 발생하였습니다.");
	}
	
	/**
	 * 파일 복사 중 발생하는 예외 
	 * 	- 파일 복사에 실패하였습니다.
	 * 	- Failed to copy file!
	 * 
	 * @return
	 */
	public static ElidomServiceException newFailToCopyFile() {
		return new ElidomServiceException(SysMessageConstants.FILE_COPY_FAILED, "Failed to copy file!");
	}
	
	/**
	 * 데이터소스 이름으로 데이터소스를 찾지 못한 경우 예외   
	 * 	- Datasource ({datasourceName}) 을(를) 찾을수 없습니다.
	 * 	- Datasource ({datasourceName}) not found.
	 * 
	 * @return
	 */
	public static ElidomValidationException newNotFoundDatasource(String datasourceName) {
		return new ElidomValidationException(SysMessageConstants.NOT_FOUND, MessageUtil.params("terms.label.datasource", datasourceName));
	}
	
	/**
	 * 서비스 URL이 유효하지 못한 경우 예외 
	 * 	- Restful Service URL({serviceUrl}) 이 올바르지 않습니다.
	 * 	- Invalid Restful Service URL({serviceUrl}).
	 * 
	 * @param serviceUrl
	 * @return
	 */
	public static ElidomServiceNotFoundException newInvalidServiceUrl(String serviceUrl) {
		return new ElidomServiceNotFoundException(SysMessageConstants.INVALID_SERVICE_URL, "Invalid Restful Service URL({0}).", MessageUtil.params(serviceUrl));
	}
	
	/**
	 * 서비스를 찾지 못한 경우 예외 
	 * 	- URL({serviceUrl})에서 서비스를 찾을 수 없습니다.
	 * 	- Service Not Found By URL({serviceUrl}).
	 * 
	 * @param serviceUrl
	 * @return
	 */
	public static ElidomServiceNotFoundException newNotFoundService(String serviceUrl) {
		return new ElidomServiceNotFoundException(SysMessageConstants.NOT_FOUND_URL, "Service Not Found By URL({0}).", MessageUtil.params(serviceUrl));
	}
	
	/**
	 * 현재 지원하지 않은 메소드인 경우 예외 
	 * 	- 지원되지 않는 method입니다.
	 * 	- Not support method
	 * 
	 * @return
	 */
	public static ElidomServiceNotFoundException newNotSupportedMethodYet() {
		return new ElidomServiceNotFoundException(SysMessageConstants.NOT_SUPPORTED_METHOD, "Not supported method");
	}
	
	/**
	 * 템플릿 실행 중 발생하는 예외
	 * 	- Failed to Process {templateName} Template
	 * 	- {templateName} 템플릿 프로세스를 실패하였습니다.
	 * 
	 * @return
	 */	
	public static ElidomServiceException newFailToProcessTemplate(String templateName, Throwable th) {
		return new ElidomServiceException(SysMessageConstants.FAIL_TO_PROCESS_TEMPLATE, "Failed to Process {0} Template.", MessageUtil.params(templateName), th);
	}
	
	/**
	 * 코드 컴파일 중 발생하는 예외
	 * 	- Failed to Compile {code} Code
	 * 	- {code} 코드 컴파일 중 에러가 발생했습니다.
	 * 
	 * @return
	 */	
	public static ElidomServiceException newFailToCompileCode(String code, Throwable th) {
		return new ElidomServiceException(SysMessageConstants.FAIL_TO_COMPILE, "Failed to Compile {0} Code.", MessageUtil.params(code), th);
	}
	
	/**
	 * 데이터 파싱 중 발생하는 예외
	 * 	- Failed to parse {code} code
	 * 	- {code} 데이터 파싱 중 에러가 발생했습니다.
	 * 
	 * @return
	 */	
	public static ElidomServiceException newFailToParseCode(String code, Throwable th) {
		return new ElidomServiceException(SysMessageConstants.FAIL_TO_PARSE, "Failed to parse {0} data.", MessageUtil.params(code), th);
	}
	
	/**
	 * 뭔가 하는 도중에 어떤 예외가 발생하는 경우  
	 * 	- {errorType} Error occurred when doing {doSomething}
	 * 	- {doSomething} 하던 도중에 {errorType} 에러가 발생했습니다.
	 * 
	 * @return
	 */	
	public static ElidomServiceException newErrorWhenDoingSomething(String errorType, String doSomething, Throwable th) {
		return new ElidomServiceException(SysMessageConstants.ERROR_WHEN_DO_SOMETHING, "{0} Error occurred when doing {1}", MessageUtil.params(errorType, doSomething), th);
	}
	
	/**
	 * 스크립트 엔진 실행시 발생하는 예외  
	 * 	- Script Error occurred when doing Logic
	 * 	- Logic 실행 도중에 Script 에러가 발생했습니다.
	 * 
	 * @return
	 */	
	public static ElidomScriptRuntimeException newFailToRunScript(Throwable th) {
		return new ElidomScriptRuntimeException(SysMessageConstants.ERROR_WHEN_DO_SOMETHING, "{0} Error occurred when doing {1}", MessageUtil.params("terms.label.logic", "Script"), th);
	}
	
	/**
	 * 상태가 유효하지 않은 경우 발생하는 예외  
	 * 	- {type} ({data})은(는) [{status}] 상태가 아닙니다.
	 * 	- The {0} ({1}) is not [{2}] Status.
	 * 
	 * @param type
	 * @param data
	 * @param status
	 * @return
	 */
	public static ElidomValidationException newInvalidStatus(String type, String data, String status) {
		return new ElidomValidationException(SysMessageConstants.MISMATCH_STATUS, "The {0} ({1}) is not [{2}] Status.", MessageUtil.params(type, data, status));
	}
	
	/**
	 * a와 b가 일치하지 않는 경우 발생하는 예외 
	 * 	- {0}와(과) {1}이(가) 맞지 않습니다.
	 * 	- {0} is not equal {1}.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static ElidomValidationException newAIsNotEqualB(String a, String b) {
		return new ElidomValidationException(SysMessageConstants.A_IS_NOT_EQUAL_B, "{0} is not equal {1}.", MessageUtil.params(a, b));
	}
	
	/**
	 * 상태가 진행 중 이 아닌 경우 발생하는 예외 
	 *  - 진행 중인 {type}이(가) 아닙니다.
	 *  - {type} is not on the process.
	 * 
	 * @param type
	 * @return
	 */
	public static ElidomValidationException newStatusIsNotIng(String type) {
		return new ElidomValidationException(SysMessageConstants.DOES_NOT_PROCEED, "{0} is not on the process.", MessageUtil.params(type));
	}
	
	/**
	 * 요청 접수가 이미 되어 있는데 다시 요청하는 경우 발생하는 예외 
	 * 	- 이미 접수된 요청입니다.
	 * 	- This request already has been received.
	 * 
	 * @return
	 */
	public static ElidomBadRequestException newAlreadyReceivedRequest() {
		return new ElidomBadRequestException(SysMessageConstants.REQUEST_ALREADY_RECEIVED, "This request already has been received.");
	}
	
	/**
	 * 인증되지 않은 사용자입니다.
	 * 
	 * @return
	 */
	public static ElidomUnauthorizedException unAuthorizedAccount() {
		return new ElidomUnauthorizedException(SysMessageConstants.NOT_AUTHORIZED_USER, "Unauthorized User.");
	}
	
	/**
	 * 계정 또는 비밀번호가 올바르지 않습니다.
	 * 
	 * @return
	 */
	public static ElidomUnauthorizedException invalidIdOrPass() {
		return new ElidomUnauthorizedException(SysMessageConstants.USER_INVALID_ID_OR_PASS, "ID or Password is incorrect.");
	}
	
	/**
	 * 권한이 이미 있는데 다시 요청하는 경우 발생하는 예외  
	 * 	- 요청하신 권한은 이미 신청자님께서 가지고 있습니다.
	 * 	- You already have the authorization. 
	 * 
	 * @return
	 */
	public static ElidomBadRequestException newAlreadyHaveAuthroization() {
		return new ElidomBadRequestException(SysMessageConstants.ALREADY_HAVE_AUTHROIZATION, "You already have the authorization.");
	}
	
	/**
	 * 요청이 이미 처리되었는데 다시 처리하려고 할 때 발생하는 예외.
	 * 	- {request}은 이미 처리되었습니다.
	 * 	- {request} has been already finished.
	 * 
	 * @param request
	 * @return
	 */
	public static ElidomBadRequestException newAlreadyProcessedRequest(String request) {
		return new ElidomBadRequestException(SysMessageConstants.ALREADY_FINISHED, "{0} has already finished.", MessageUtil.params(request));
	}

	/**
	 * 이미 활성화되어 있는 사용자입니다.
	 * 	- 이미 활성화되어 있는 사용자입니다.
	 * 	- Already activated account.
	 * 
	 * @return
	 */
	public static ElidomBadRequestException newAlreadyActivatedAccount() {
		return new ElidomBadRequestException(SysMessageConstants.USER_ALREADY_ACTIVATED, "Already activated account.");
	}
	
	/**
	 * 이미 비활성화되어 있는 사용자입니다.
	 * 	- 비활성화된 계정입니다.
	 * 	- Deactivated account.
	 * 
	 * @return
	 */
	public static ElidomBadRequestException newAlreadyDeactivatedAccount() {
		return new ElidomBadRequestException(SysMessageConstants.USER_INACTIVATED_ACCOUNT, "Already deactivated account.");
	}
	
	/**
	 * 사용자께서는 {0}-{1}에 대한 접근 권한이 없습니다.
	 * 	- 사용자께서는 {0}-{1}에 대한 접근 권한이 없습니다.
	 * 	- You have no authroity to access {0}-{1}
	 * 
	 * @return
	 */
	public static ElidomBadRequestException newHasNoAuthority(String resourceType, String resourceName) {
		return new ElidomBadRequestException(SysMessageConstants.HAS_NO_AUTHORITY, "You have no authroity to access {0}-{1}", MessageUtil.params(resourceType, resourceName));
	}
	
	/**
	 * 에러 로깅을 하지 않고 ValidationException을 생성
	 * 
	 * @param errorMsg
	 * @return
	 */
	public static ElidomValidationException newValidationErrorWithNoLog(String errorMsg) {
		// 에러 로깅을 하지 않게 Exception 생성 - Setting에서 설정할 수 있게 변경
		boolean withLogging = ValueUtil.toBoolean(SettingUtil.getValue(ConfigConstants.VALIDATION_ERROR_LOGGING_ENABLED, AnythingsSysConstants.TRUE_STRING));
		ElidomValidationException eve = new ElidomValidationException(errorMsg);
		eve.setWritable(withLogging);
		throw eve;
	}
	
	/**
	 * type, data로 타입과 데이터 파라미터를 생성
	 * 
	 * @param type
	 * @param data
	 * @return
	 */
	private static List<String> toTypeDataParams(String type, String data) {
		List<String> params = MessageUtil.params(type);
		params.add(data);
		return params;
	}
}
