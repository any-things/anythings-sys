package xyz.anythings.sys;

/**
 * 설정 관련 키 상수 정의
 *
 * @author shortstop
 */
public class ConfigConstants {
	
	/**********************************************************************
	 * 								1. 공통 설정 
	 **********************************************************************/
	/**
	 * Job 스케줄링 활성화 여부
	 */
	public static final String JOB_SCHEDULER_ENABLED = "job.scheduler.enable";
	/**
	 * Validation Error를 로깅할 지 여부
	 */
	public static final String VALIDATION_ERROR_LOGGING_ENABLED = "logis.log.validation.error.enabled";
	/**
	 * 바코드 최대 길이 - 상품 스캔시 최대 입력 길이
	 */
	public static final String SKU_BARCODE_MAX_LENGTH = "logis.job.common.sku.barcode.max.length";
	/**
	 * SKU 조회를 위한 코드 필드명 리스트
	 */
	public static final String SKU_FIELDS_TO_SEARCH = "logis.job.common.sku.search.code.fields";
	/**
	 * SKU 조회를 위한 조회 필드명 리스트
	 */
	public static final String SKU_SELECT_FIELDS_TO_SEARCH = "logis.job.common.sku.search.select.fields";

}
