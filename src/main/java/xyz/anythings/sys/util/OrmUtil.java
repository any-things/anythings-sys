package xyz.anythings.sys.util;

import xyz.anythings.sys.AnythingsSysConstants;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.sys.SysConstants;

/**
 * ORM 관련 유틸리티
 * 
 * @author shortstop
 */
public class OrmUtil {
	
	/**
	 * 실행을 위한 기본 컨디션을 리턴 
	 * 
	 * @return
	 */
	public static Query newConditionForExecution() {
		Query condition = new Query();
		condition.addUnselect(AnythingsSysConstants.DEFAULT_UNSELECT_QUERY_FIELDS);
		return condition;
	}
	
	/**
	 * domainId 필터가 포함된 검색을 위한 기본 컨디션을 리턴 
	 * 
	 * @param domainId
	 * @return
	 */
	public static Query newConditionForExecution(Long domainId) {
		Query condition = newConditionForExecution();
		condition.addFilter(SysConstants.ENTITY_FIELD_DOMAIN_ID, domainId);
		return condition;
	}
	
	/**
	 * domainId 필터, 페이지네이션 정보가 포함된 검색을 위한 기본 컨디션을 리턴 
	 * 
	 * @param domainId
	 * @param page
	 * @param limit
	 * @return
	 */
	public static Query newConditionForExecution(int page, int limit) {
		Query condition = newConditionForExecution();
		condition.setPageIndex(page);
		condition.setPageSize(limit);
		return condition;
	}
	
	/**
	 * 페이지네이션 정보와 조회 필드가 포함된 검색을 위한 기본 컨디션을 리턴 
	 * 
	 * @param page
	 * @param limit
	 * @param selectFields
	 * @return
	 */
	public static Query newConditionForExecution(int page, int limit, String... selectFields) {
		Query condition = newConditionForExecution();
		condition.addSelect(selectFields);
		condition.setPageIndex(page);
		condition.setPageSize(limit);
		return condition;
	}
	
	/**
	 * domainId 필터, 페이지네이션 정보가 포함된 검색을 위한 기본 컨디션을 리턴 
	 * 
	 * @param domainId
	 * @param page
	 * @param limit
	 * @return
	 */
	public static Query newConditionForExecution(Long domainId, int page, int limit) {
		Query condition = newConditionForExecution();
		condition.addFilter(SysConstants.ENTITY_FIELD_DOMAIN_ID, domainId);
		condition.setPageIndex(page);
		condition.setPageSize(limit);
		return condition;
	}
	
	/**
	 * selectFields 필드로 검색을 위한 기본 컨디션을 리턴 
	 * 
	 * @param selectFields
	 * @return
	 */
	public static Query newConditionForExecution(String... selectFields) {
		Query condition = new Query();
		condition.addSelect(selectFields);
		return condition;
	}
	
	/**
	 * domainId 필터, 페이지네이션 조건, 검색 필드가 포함된 검색을 위한 기본 컨디션을 리턴 
	 * 
	 * @param domainId
	 * @param page
	 * @param limit
	 * @param selectFields
	 * @return
	 */
	public static Query newConditionForExecution(Long domainId, int page, int limit, String... selectFields) {
		Query condition = new Query();
		condition.addSelect(selectFields);
		condition.addFilter(SysConstants.ENTITY_FIELD_DOMAIN_ID, domainId);
		condition.setPageIndex(page);
		condition.setPageSize(limit);
		return condition;
	}
	
}
