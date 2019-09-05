package xyz.anythings.sys.rest;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

import xyz.anythings.sys.entity.ScopeSetting;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/scope_setting")
@ServiceDesc(description = "ScopeSetting Service API")
public class ScopeSettingController extends AbstractRestService {

	@Override
	protected Class<?> entityClass() {
		return ScopeSetting.class;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search (Pagination) By Search Conditions")
	public Page<?> index(@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "select", required = false) String select,
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		return this.search(this.entityClass(), page, limit, select, sort, query);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find one by ID")
	public ScopeSetting findOne(@PathVariable("id") String id) {
		return this.getOne(this.entityClass(), id);
	}
	
	@RequestMapping(value = "/{scope_type}/{scope_cd}/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find One SCope and Name")
	@Cacheable(cacheNames="CompanySetting", keyGenerator="companySettingFindApiKeyGenerator")
	public ScopeSetting findBy(@PathVariable("scope_type") String scopeType
								, @PathVariable("scope_cd") String scopeCd
								, @PathVariable("name") String name) {
		return this.findScopeSetting(Domain.currentDomainId(), scopeType, scopeCd, name);
	}

	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check exists By ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create")
	@CachePut(cacheNames="ScopeSetting", keyGenerator="scopeSettingUpdateApiKeyGenerator")
	public ScopeSetting create(@RequestBody ScopeSetting input) {
		return this.createOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update")
	@CachePut(cacheNames="ScopeSetting", keyGenerator="scopeSettingUpdateApiKeyGenerator")
	public ScopeSetting update(@PathVariable("id") String id, @RequestBody ScopeSetting input) {
		return this.updateOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete")
	@CacheEvict(cacheNames="ScopeSetting", allEntries=true)
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete multiple at one time")
	@CacheEvict(cacheNames="ScopeSetting", allEntries=true)
	public Boolean multipleUpdate(@RequestBody List<ScopeSetting> list) {
		return this.cudMultipleData(this.entityClass(), list);
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Clear Settings Cache")	
	@CacheEvict(cacheNames = "CompanySetting", allEntries = true)
	public boolean clearCache() {
		return true;
	}

	@ApiDesc(description = "Find One Company and Name")
	@Cacheable(cacheNames="ScopeSetting", key="#domainId + '-' + #scopeType + '-' + #scopeCd + '-' + #name")
	public ScopeSetting findByName(Long domainId, String scopeType, String scopeCd,String name) {
		return this.findScopeSetting(domainId, scopeType, scopeCd, name);
	}
	
	private ScopeSetting findScopeSetting(Long domainId, String scopeType, String scopeCd,String name) {
		Query condition = new Query();
		condition.addFilter("scopeType", scopeType);
		condition.addFilter("scopeCd", scopeCd);
		condition.addFilter("name", name);
		condition.addSelect("id", "scopeType", "scopeCd", "name", "value");
		return this.queryManager.selectByCondition(ScopeSetting.class, condition);
	}
}