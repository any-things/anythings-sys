/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.msg.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.elidom.dbist.dml.Page;
import xyz.elidom.msg.entity.Terminology;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/terminologies")
@ServiceDesc(description = "Terminology Service API")
public class TerminologyController extends AbstractRestService {
	
	/**
	 * 로케일 쿼리 
	 */
	private static final String LOCALE_QUERY = "SELECT distinct(LOCALE) locale FROM TERMINOLOGIES";
	
	/**
	 * 용어 조회 쿼리 
	 */
	private static final String TERM_QUERY = "SELECT CATEGORY, NAME, DISPLAY FROM TERMINOLOGIES WHERE DOMAIN_ID = :domainId AND LOCALE = :locale";

	@Override
	protected Class<?> entityClass() {
		return Terminology.class;
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Load all terminologies")
	public void all() {
		List<String> locales = this.queryManager.selectListBySql(LOCALE_QUERY, null, String.class, 0, 0);
		TerminologyController ctrl = BeanUtil.get(TerminologyController.class);

		// locale별 terminologies 추출
		for (String locale : locales) {
			ctrl.resource(locale);
		}		
	}
	
	@RequestMapping(value = "/resource/{locale}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Resource List")
	@Cacheable(cacheNames = "Terminology", key = "T(xyz.elidom.sys.entity.Domain).currentDomain().getId() + #locale")
	public Map<String, Object> resource(@PathVariable("locale") String locale) {
		Map<String, Object> termsMap = new HashMap<String, Object>();
		List<Terminology> terms = this.queryManager.selectListBySql(TERM_QUERY, ValueUtil.newMap("domainId,locale", Domain.currentDomain().getId(), locale), Terminology.class, 0, 0);

		for (Terminology term : terms) {
			termsMap.put(term.getCategory() + "." + term.getName(), term.getDisplay());
		}

		return ValueUtil.newMap(locale, termsMap);
	}
		
	@SuppressWarnings("unchecked")
	@ApiDesc(description="Find terminology by locale and category and name")
	@Cacheable(cacheNames="Terminology", key="T(xyz.elidom.sys.entity.Domain).currentDomain().getId() + #locale + #key")
	public String findBy(String locale, String key) {
		TerminologyController ctrl = BeanUtil.get(TerminologyController.class);
		Map<String, Object> termsMap = (Map<String, Object>)ctrl.resource(locale).get(locale);
		return (termsMap.containsKey(key)) ? (String)termsMap.get(key) : null;
	}

	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search Dynamic Templates (Pagination) By Search Conditions")
	public Page<?> index(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		return this.search(this.entityClass(), page, limit, select, sort, query);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find one Terminology by ID")
	public Terminology findOne(@PathVariable("id") String id) {
		return this.getOne(true, this.entityClass(), id);
	}

	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check if Terminology exists By ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}
	
	@RequestMapping(value = "/check_import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check Before Import")
	public List<Terminology> checkImport(@RequestBody List<Terminology> list) {
		for (Terminology item : list) {
			this.checkForImport(Terminology.class, item);
		}
		
		return list;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create Terminology")
	public Terminology create(@RequestBody Terminology terminology) {
		return this.createOne(terminology);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update Terminology")
	public Terminology update(@PathVariable("id") String id, @RequestBody Terminology terminology) {
		return this.updateOne(terminology);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete Terminology")
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete Multiple Terminologies at one time")
	public Boolean multipleUpdate(@RequestBody List<Terminology> terminologyList) {
		Boolean result = this.cudMultipleData(this.entityClass(), terminologyList);
		
		if(result) {
			BeanUtil.get(TerminologyController.class).clearCache();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(cacheNames = "Terminology", allEntries = true)
	public boolean clearCache() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@ApiDesc(description="Find terminology by locale and category and name")
	@Cacheable(cacheNames="Terminology", key="#domainId + #locale + #key")
	public String findBy(Long domainId, String locale, String key) {
		TerminologyController ctrl = BeanUtil.get(TerminologyController.class);
		Map<String, Object> termsMap = (Map<String, Object>)ctrl.resource(domainId, locale).get(locale);
		return (termsMap.containsKey(key)) ? (String)termsMap.get(key) : null;
	}
	
	@ApiDesc(description = "Resource List")
	@Cacheable(cacheNames = "Terminology", key = "#domainId + #locale")
	public Map<String, Object> resource(Long domainId, String locale) {
		Map<String, Object> termsMap = new HashMap<String, Object>();
		List<Terminology> terms = this.queryManager.selectListBySql(TERM_QUERY, ValueUtil.newMap("domainId,locale", domainId, locale), Terminology.class, 0, 0);

		for (Terminology term : terms) {
			termsMap.put(term.getCategory() + "." + term.getName(), term.getDisplay());
		}

		return ValueUtil.newMap(locale, termsMap);
	}
}