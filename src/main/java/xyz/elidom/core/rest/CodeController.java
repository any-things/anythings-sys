/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.core.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import xyz.elidom.core.entity.Code;
import xyz.elidom.core.entity.CodeDetail;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.AssertUtil;
import xyz.elidom.util.BeanUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/common_codes")
@ServiceDesc(description="CommonCode Service API")
public class CodeController extends AbstractRestService {
	
	@Override
	protected Class<?> entityClass() {
		return Code.class;
	}	
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search CommonCode (Pagination) by Search Conditions")
	public Page<?> index(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		return this.search(this.entityClass(), page, limit, select, sort, query);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find one CommonCode by ID")
	@Cacheable(cacheNames="CommonCode", condition="#name != null", keyGenerator="namedFindApiKeyGenerator")
	public Code findOne(@PathVariable("id") String id, @RequestParam(required = false) String name) {
		if(SysConstants.SHOW_BY_NAME_METHOD.equalsIgnoreCase(id)) {
			AssertUtil.assertNotEmpty(SysConstants.TERM_LABEL_NAME, name);
			return Code.findByName(Domain.currentDomain().getId(), name);
		} else {
			Code code = this.getOne(true, this.entityClass(), id);
			return BeanUtil.get(CodeController.class).findOne(SysConstants.SHOW_BY_NAME_METHOD, code.getName());
		}
	}
	
	@RequestMapping(value="/{id}/exist", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Check if CommonCode exists by ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}
	
	@RequestMapping(value = "/check_import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check Before Import")
	public List<Code> checkImport(@RequestBody List<Code> list) {
		for (Code item : list) {
			this.checkForImport(Code.class, item);
		}
		
		return list;
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description="Create CommonCode")
	@CachePut(cacheNames="CommonCode", keyGenerator="namedUpdateApiKeyGenerator")
	public Code create(@RequestBody Code commonCode) {
		return this.createOne(commonCode);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Update CommonCode")
	@CachePut(cacheNames="CommonCode", keyGenerator="namedUpdateApiKeyGenerator")
	public Code update(@PathVariable("id") String id, @RequestBody Code commonCode) {
		return this.updateOne(commonCode);
	}
	
	@RequestMapping(value="/search_with_details", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search all data with details")
	public List<Map<String, Object>> indexWithDetails(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query,
			@RequestParam(name = "include_default_fields", required = false) boolean includeDefaultFields) {
		
		Page<?> pageResult = this.search(this.entityClass(), page, limit, OrmConstants.ENTITY_FIELD_ID, sort, query);
		List<?> list = pageResult.getList();
		
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for(Object data : list) {
			String id = ((Code)data).getId();
			results.add(this.findDetails(id, includeDefaultFields));
		}
		
		return results;
	}

	@RequestMapping(value="/{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Delete CommonCode by ID")
	public boolean delete(@PathVariable("id") String id) {
		Code code = this.getOne(true, this.entityClass(), id);
		return BeanUtil.get(CodeController.class).deleteCode(code);
	}
	
	@CacheEvict(cacheNames="CommonCode", keyGenerator="namedUpdateApiKeyGenerator")
	public boolean deleteCode(Code code) {
		this.queryManager.delete(code);
		return true;
	}
	
	@RequestMapping(value="/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Create, Update or Delete multiple CommonCode at one time")
	@CacheEvict(cacheNames="CommonCode", allEntries=true)
	public Boolean multipleUpdate(@RequestBody List<Code> commonCodeList) {
		return this.cudMultipleData(this.entityClass(), commonCodeList);
	}
	
	@RequestMapping(value = "/{id}/include_details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find One included all details by ID")
	public Map<String, Object> findDetails(@PathVariable("id") String id, @RequestParam(name = "include_default_fields", required = false) boolean includeDefaultFields) {
		return this.findOneIncludedDetails(id, includeDefaultFields);
	}
	
	@RequestMapping(value="/{id}/codes", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find Sub Codes By Common Code ID")
	public List<CodeDetail> findSubCodes(@PathVariable("id") String id) {
		Query query = new Query();
		query.addFilter(new Filter(OrmConstants.ENTITY_FIELD_DOMAIN_ID, Domain.currentDomainId()));
		query.addFilter(new Filter("parentId", id));
		query.addOrder("rank", true);
		return this.queryManager.selectList(CodeDetail.class, query);
	}	
	
	@RequestMapping(value="/{id}/codes/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Create, Update or Delete multiple Sub codes at one time")
	@CacheEvict(cacheNames="CommonCode", allEntries=true)
	public Boolean multipleUpdateCodes(@RequestBody List<CodeDetail> commonDetailCodeList) {
		return this.cudMultipleData(CodeDetail.class, commonDetailCodeList);
	}
	
	// TODO /{id}/codes/update_multiple로 통합 필요 --> 추후 삭제
	@RequestMapping(value="/{id}/update_multiple_codes", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Create, Update or Delete multiple Sub codes at one time")
	@CacheEvict(cacheNames="CommonCode", allEntries=true)
	public Boolean multipleUpdateSubCodes(@RequestBody List<CodeDetail> commonDetailCodeList) {
		return this.cudMultipleData(CodeDetail.class, commonDetailCodeList);
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Clear CommonCode Cache")	
	@CacheEvict(cacheNames = "CommonCode", allEntries = true)
	public boolean clearCache() {
		return true;
	}
	
	@ApiDesc(description="Find one CommonCode by Name")
	@Cacheable(cacheNames="CommonCode", key="#domainId + '-' + #name")
	public Code findByName(Long domainId, String name) {
		AssertUtil.assertNotEmpty(SysConstants.TERM_LABEL_NAME, name);
		return Code.findByName(domainId, name);
	}
	
}