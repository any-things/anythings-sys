package xyz.anythings.sys;

import xyz.elidom.base.BaseConstants;
import xyz.elidom.sys.SysConstants;

/**
 * Anythings 관련 상수 정의 
 * 
 * @author shortstop
 */
public class AnythingsSysConstants extends BaseConstants {

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
}
