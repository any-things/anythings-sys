package xyz.anythings.sys.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import xyz.elidom.orm.IQueryManager;

/**
 * Anythings 기본 최상위 쿼리 서비스
 *  
 * @author shortstop
 */
public class AbstractQueryService {
	/**
	 * Logger
	 */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * Query Manager
	 */
	@Autowired
	protected IQueryManager queryManager;
}
