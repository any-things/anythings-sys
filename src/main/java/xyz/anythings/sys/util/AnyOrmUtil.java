package xyz.anythings.sys.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import xyz.anythings.sys.AnyConstants;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.util.BeanUtil;

/**
 * ORM 관련 유틸리티
 * 
 * @author shortstop
 */
public class AnyOrmUtil {
	
	/**
	 * 데이터베이스 현재 시간
	 * 
	 * @return
	 */
	public static Date currentDbTime() {
		IQueryManager queryMgr = BeanUtil.get(IQueryManager.class);
		return queryMgr.selectBySql("select sysdate from dual", new HashMap<String, Object>(1), Date.class);
	}
	
	/**
	 * batchCount건수 별로 배치 생성 
	 * 
	 * @param insertList
	 * @param batchCount
	 */
	public static void insertBatch(List<?> insertList, int batchCount) {
		IQueryManager queryManager = BeanUtil.get(IQueryManager.class);
		List<Object> batchList = new ArrayList<Object>(batchCount);
		
		int count = 0;
		
		for(Object obj : insertList) {
			count++;
			
			batchList.add(obj);
			
			if(count == batchCount) {
				queryManager.insertBatch(batchList);
				count = 0;
			}
			
			if(count == 0) batchList.clear();
		}
		
		if(!batchList.isEmpty()) {
			queryManager.insertBatch(batchList);
		}
	}
	
	/**
	 * batchCount 건수 별로 업데이트 
	 * 
	 * @param updateList
	 * @param batchCount
	 * @param fields
	 */
	public static void updateBatch(List<?> updateList, int batchCount, String... fields) {
		IQueryManager queryManager = BeanUtil.get(IQueryManager.class);
		List<Object> batchList = new ArrayList<Object>(batchCount);
		int count = 0;
		
		for(Object obj : updateList) {
			if(count == 0) {
				batchList.clear();
			}
			
			count++;
			
			batchList.add(obj);
			
			if(count == batchCount) {
				queryManager.updateBatch(batchList, fields);
				count = 0;
			}
		}
		
		if(!batchList.isEmpty()) {
			queryManager.updateBatch(batchList, fields);
		}
	}
	
	/**
	 * 실행을 위한 기본 컨디션을 리턴 
	 * 
	 * @return
	 */
	public static Query newConditionForExecution() {
		Query condition = new Query();
		condition.addUnselect(AnyConstants.DEFAULT_UNSELECT_QUERY_FIELDS);
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
