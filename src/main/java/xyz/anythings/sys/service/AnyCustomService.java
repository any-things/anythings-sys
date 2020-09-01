package xyz.anythings.sys.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.anythings.sys.AnyConstants;
import xyz.anythings.sys.util.AnyOrmUtil;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.dev.entity.DiyService;
import xyz.elidom.exception.ElidomException;
import xyz.elidom.exception.server.ElidomRuntimeException;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sys.system.engine.IScriptEngine;
import xyz.elidom.sys.util.ValueUtil;

/**
 * 동적 서비스 호출 구현
 * 
 * @author shortstop
 */
@Component
public class AnyCustomService implements ICustomService {

	/**
	 * 쿼리 매니저
	 */
	@Autowired
	private IQueryManager queryManager;
	/**
	 * 스크립트 엔진
	 */
	@Autowired
	private IScriptEngine scriptEngine;
	
	@Override
	public Object doCustomService(Long domainId, String diyServiceName, Map<String, Object> parameters) {
		
		Query condition = AnyOrmUtil.newConditionForExecution(domainId);
		condition.addFilter(AnyConstants.ENTITY_FIELD_NAME, diyServiceName);
		DiyService diyService = this.queryManager.selectByCondition(DiyService.class, condition);
		Object retVal = null;
		
		if(diyService != null && ValueUtil.isNotEmpty(diyService.getServiceLogic())) {
			try {
				retVal = this.scriptEngine.runScript(diyService.getLangType(), diyService.getServiceLogic(), parameters);

			} catch(ElidomException ee) {
				throw ee;
			
			} catch(Exception e) {
				Throwable th = e.getCause();
				
				if(th != null) {
					if(th instanceof ElidomException) {
						throw (ElidomException)th;
					}
				}
				
				throw new ElidomRuntimeException(th == null ? e : th);
			}
		}
		
		return retVal;
	}

}
