package xyz.anythings.sys;

import xyz.elidom.base.BaseConstants;
import xyz.elidom.sys.SysConstants;

/**
 * Anythings 관련 상수 정의 
 * 
 * @author shortstop
 */
public class AnyConstants extends BaseConstants {

	/**
	 * 기본 unselect fields
	 */
	public static final String[] DEFAULT_UNSELECT_QUERY_FIELDS = {
		SysConstants.ENTITY_FIELD_CREATOR, 
		SysConstants.ENTITY_FIELD_UPDATER, 
		SysConstants.ENTITY_FIELD_CREATOR_ID, 
		SysConstants.ENTITY_FIELD_UPDATER_ID, 
		SysConstants.ENTITY_FIELD_CREATED_AT, 
		SysConstants.ENTITY_FIELD_UPDATED_AT
	};
	
	/**
	 * Asterisk
	 */
	public static final String ASTERISK = "*";
	/**
	 * NG 
	 */
	public static final String NG_STRING = "ng";
	/**
	 * N
	 */
	public static final String N_CAP_STRING = "N";
	/**
	 * Y
	 */
	public static final String Y_CAP_STRING = "Y";
	/**
	 * NULL
	 */
	public static final String NULL_CAP_STRING = "NULL";
	/**
	 * N
	 */
	public static final String N_STRING = "n";
	/**
	 * Y
	 */
	public static final String Y_STRING = "y";
	/**
	 * null
	 */
	public static final String NULL_STRING = "null";
	/**
	 * 해당 없음 상수
	 */
	public static final String NOT_AVAILABLE = "_na_";
	/**
	 * all
	 */
	public static final String ALL_STR = "all";
	/**
	 * ALL
	 */
	public static final String ALL_CAPITAL_STR = "ALL";
	
	/**
	 * 공통 대기 상태 : Waiting
	 */
	public static final String COMMON_STATUS_WAIT = "W";
	/**
	 * 공통 완료 상태 : Completed
	 */
	public static final String COMMON_STATUS_FINISHED = "F";
	/**
	 * 공통 진행 상태 : Running
	 */
	public static final String COMMON_STATUS_RUNNING = "R";
	/**
	 * 공통 에러 상태 : Error
	 */
	public static final String COMMON_STATUS_ERROR = "E";
	/**
	 * 공통 취소 상태 : Canceled
	 */
	public static final String COMMON_STATUS_CANCEL = "C";
}
