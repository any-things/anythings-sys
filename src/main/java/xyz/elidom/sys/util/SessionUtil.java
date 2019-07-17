/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.sys.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import xyz.anythings.sys.util.AnyValueUtil;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.ValueUtil;

/**
 * Session 핸들링을 위한 유틸리티 클래스 
 * 
 * @author Minu.Kim
 */
public class SessionUtil {

	/**
	 * session 정보로 부터 attribute 값을 가져 오기 위한 method
	 * 
	 * @param name
	 * @return
	 */
	public static Object getAttribute(String name) {
		try {
			if(ValueUtil.isEqual(name, SysConstants.DOMAIN_ID)) {
				Domain domain = (Domain)SessionUtil.getAttribute(SysConstants.CURRENT_DOMAIN);
				return domain.getId();
			}
			
			if(ValueUtil.isEqual(name, SysConstants.CURRENT_DOMAIN)) name = SessionUtil.getCurrentReqeustDomainClass();
			return RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
			
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * session 정보로 부터 attribute 설정 method
	 * 
	 * @param name
	 * @param object
	 */
	public static void setAttribute(String name, Object object) {
		if(ValueUtil.isEqual(name, SysConstants.CURRENT_DOMAIN)) 
			name = SessionUtil.getCurrentReqeustDomainClass();
		RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
	}

	/**
	 * session 정보에 설정한 attribute 삭제
	 * 
	 * @param name
	 */
	public static void removeAttribute(String name) {
		if(ValueUtil.isEqual(name, SysConstants.CURRENT_DOMAIN)) name = SessionUtil.getCurrentReqeustDomainClass();
		RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
	}

	/**
	 * session id 리턴 
	 *
	 * @param void
	 * @return String SessionId 값
	 */
	public static String getSessionId() {
		return RequestContextHolder.getRequestAttributes().getSessionId();
	}
	
	/**
	 * 최초 클라이언트 도메인 정보 생성
	 *  
	 * @param currentDomain
	 */
	public static Domain setCurrentDomain() {
		Domain currentDomain = (Domain)SessionUtil.getAttribute(SysConstants.CURRENT_DOMAIN);
		boolean createNewDomain = false;
		
		// 신규 도메인 셋팅 1
		if(currentDomain == null ) createNewDomain = true;
		else {
			if(SessionUtil.getAttribute("CLIENT_TYPE") != null ) {
				if(SessionUtil.getAttribute("CLIENT_TYPE").equals("MANAGER")) {
					HttpServletRequest request = SessionUtil.getCurrentHttpRequest();
					String subDomain = AnyValueUtil.getClientRequestSubDomain(request);
					
					if(ValueUtil.isNotEqual(currentDomain.getSubdomain() , subDomain)) {
						createNewDomain = true;
					}
				}
			}
		}
		
		if(createNewDomain) {
			currentDomain = SessionUtil.getCurrentDomain();
			RequestContextHolder.getRequestAttributes().setAttribute(SessionUtil.getCurrentReqeustDomainClass(), currentDomain, RequestAttributes.SCOPE_SESSION);
		}
		
		return currentDomain;
	}
	
	/**
	 * 현재 요청에 대한 도메인 정보 추출
	 * 
	 * @return
	 */
	public static String getCurrentReqeustDomainClass() {
		HttpServletRequest request = SessionUtil.getCurrentHttpRequest();
		return AnyValueUtil.getClientRequestSubDomain(request);
	}
	
	/**
	 * Domain 정보 검색
	 * 
	 * @param request
	 * @return
	 */
	public static Domain getCurrentDomain() {
		try{
			HttpServletRequest request = SessionUtil.getCurrentHttpRequest();
			if(ValueUtil.isEmpty(request)) {
				return null;
			} else {
				return SessionUtil.findBySiteClassifier(request);
			}
		} catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * 사이트 구분자로 도메인 조회
	 * 
	 * @param request
	 * @return
	 */
	public static Domain findBySiteClassifier(HttpServletRequest request) {
		IQueryManager queryManager = BeanUtil.get(IQueryManager.class);
		String subDomain = AnyValueUtil.getClientRequestSubDomain(request);
		
		// subdomain이 없는 상태 (ex: http://111.111.111.111)로 접속하면 시스템 도메인으로 판단함.
		if(ValueUtil.isEqual(subDomain, "_ROOT_")) {
			return queryManager.selectBySql("select * from domains where system_flag = :systemFlag" , ValueUtil.newMap("systemFlag", true), Domain.class);
		} else {
			return queryManager.selectBySql("select * from domains where subdomain = :subDomain " , ValueUtil.newMap("subDomain", subDomain), Domain.class);
		}
	}
	
	/**
	 * Http를 이용한 요청의 경우 ServletRequst 리턴
	 * 
	 * @return
	 */
	private static HttpServletRequest getCurrentHttpRequest(){
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    if (requestAttributes instanceof ServletRequestAttributes) {
	        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	        return request;
	    }
	    
	    return null;
	}

}