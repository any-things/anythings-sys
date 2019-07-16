/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.sys.rest;

import java.util.List;

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

import xyz.elidom.dbist.dml.Order;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.entity.User;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.AssertUtil;
import xyz.elidom.sys.util.ThrowUtil;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.ValueUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/domains")
@ServiceDesc(description="Domain Service API")
public class DomainController extends AbstractRestService {
	
	/**
	 * 기본 소팅 조건 - '[{\"field\": \"name\", \"ascending\": true}]'
	 */
	private static final String DEFAULT_SORT_COND = "[{\"field\": \"name\", \"ascending\": true}]";

	@Override
	protected Class<?> entityClass() {
		return Domain.class;
	}
	
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search Domains (Pagination) By Search Conditions")
	public Page<?> index(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		if(ValueUtil.isEmpty(sort)) {
			sort = DEFAULT_SORT_COND;
		}
		
		return this.search(this.entityClass(), page, limit, select, sort, query);
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search Domains (Pagination) By Authorization")
	public Page<?> searchByAuth(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		if(ValueUtil.isNotEmpty(User.currentUser()) && ValueUtil.isEqual(User.currentUser().getSuperUser(), true)) {
			return this.search(this.entityClass(), page, limit, select, sort, query);
			
		} else {
			Page<Domain> result = new Page<Domain>();
			result.setIndex(1);
			result.setTotalSize(1);
			result.setList(ValueUtil.toList(Domain.currentDomain()));
			return result;
		}
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find one Domain by ID")
	@Cacheable(cacheNames="Domain", condition="#name == null", key="#id")
	public Domain findOne(@PathVariable("id") Object id, @RequestParam(required = false) String name) {
		if(SysConstants.SHOW_BY_NAME_METHOD.equalsIgnoreCase(id.toString())) {
			return this.findByName(name); 
		} else {
			return this.getOne(true, this.entityClass(), ValueUtil.toLong(id));
		}
	}
	
	@RequestMapping(value="/current_domain", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find current domain")
	public Domain findCurrentDomain() {
		return BeanUtil.get(DomainController.class).findOne(Domain.currentDomain().getId(), null);
	}	
	
	@RequestMapping(value="/show_by_name/{name}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find one Domain by name")
	@Cacheable(cacheNames="Domain", key="#name")
	public Domain findByName(String name) {
		AssertUtil.assertNotEmpty(SysConstants.TERM_LABEL_NAME, name);
		return this.selectByCondition(Domain.class, new Domain(name));
	}
	
	@RequestMapping(value="/show_by_url/{name}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find one Domain by name")
	@Cacheable(cacheNames="Domain", key="#subdomain")
	public Domain findBySubdomain(String subdomain) {
		AssertUtil.assertNotEmpty("terms.label.subdomain", subdomain);
		Domain cond = new Domain();
		cond.setSubdomain(subdomain);
		return this.selectByCondition(Domain.class, cond);
	}
	
	@RequestMapping(value="/{id}/exist", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Check if Domain exists By ID")
	public Boolean isExist(@PathVariable("id") Long id) {
		return this.isExistOne(this.entityClass(), id);
	}
		
	@RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description="Create Domain")
	@CacheEvict(cacheNames="Domain", allEntries=true)
	public Domain create(@RequestBody Domain domain) {
		return this.createOne(domain);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Update Domain")
	@CacheEvict(cacheNames="Domain", allEntries=true)
	public Domain update(@PathVariable("id") Long id, @RequestBody Domain domain) {
		domain = this.updateOne(domain);
		
		if(domain.getSystemFlag()) {
			Domain.resetSystemDomain();
		}
		
		return domain;
	}

	@RequestMapping(value="/{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Delete Domain By ID")
	@CacheEvict(cacheNames="Domain", allEntries=true)
	public boolean delete(@PathVariable("id") Long id) {
		ThrowUtil.newNotSupportedMethodYet();
		return false;
	}
	
	@RequestMapping(value="/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Create, Update or Delete multiple domains at one time")
	@CacheEvict(cacheNames="Domain", allEntries=true)
	public Boolean multipleUpdate(@RequestBody List<Domain> domainList) {		
		DomainController ctrl = BeanUtil.get(DomainController.class);
		
		for (Domain d : domainList) {
			if (ValueUtil.isEqual(d.getCudFlag_(), SysConstants.CUD_FLAG_UPDATE)) {
				ctrl.update(d.getId(), d);
			}			
		}
		
		return true;		
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Domain List")
	@Cacheable(cacheNames="Domain")
	public List<Domain> domainList() {
		Query query = new Query();
		query.setSelect(ValueUtil.newStringList(OrmConstants.ENTITY_FIELD_ID, OrmConstants.ENTITY_FIELD_NAME, OrmConstants.ENTITY_FIELD_DESCRIPTION));
		query.addOrder(new Order(OrmConstants.ENTITY_FIELD_ID, true));
		return queryManager.selectList(Domain.class, query);
	}
	
	@RequestMapping(value="/show_by_port/{port}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find one Domain by port")
	@Cacheable(cacheNames="Domain", key="#port")
	public Domain findByPort(Integer port) {
		AssertUtil.assertNotEmpty("terms.label.site_port", port);
		Domain cond = new Domain();
		cond.setSitePort(port);
		return this.selectByCondition(Domain.class, cond);
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(cacheNames = "Domain", allEntries = true)
	public boolean clearCache() {
		return true;
	}
	
}