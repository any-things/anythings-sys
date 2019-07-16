package xyz.anythings.sys.rest;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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

import xyz.anythings.sys.entity.CompanySetting;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.ValueUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/company_setting")
@ServiceDesc(description = "CompanySetting Service API")
public class CompanySettingController extends AbstractRestService {

	@Override
	protected Class<?> entityClass() {
		return CompanySetting.class;
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

	/**
	 * 고객사 별 설정 화면에서 조회시 호출되는 서비스
	 * 
	 * @param page
	 * @param limit
	 * @param select
	 * @param sort
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/override_default_setting", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search by overriding default setting")
	public Page<?> overrideDefaultSetting(
			@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "select", required = false) String select,
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		Filter[] filters = ValueUtil.isEmpty(query) ? null : this.jsonParser.parse(query, Filter[].class);
		String comCd = CompanySetting.DEFAULT_COMPANY_CODE;
		
		if(filters != null && filters.length > 0) {
			for(Filter filter : filters) {
				if(ValueUtil.isEqualIgnoreCase("com_cd", filter.getName())) {
					Object value = filter.getValue();
					comCd = (value == null) ? CompanySetting.DEFAULT_COMPANY_CODE : ValueUtil.toString(value);
					break;
				}
			}
		}
		
		StringJoiner sql = new StringJoiner(SysConstants.LINE_SEPARATOR);
		sql.add("select")
		   .add("	coalesce(y.id, x.id) as id, coalesce(y.com_cd, x.com_cd) as com_cd, x.name, x.description,")
		   .add("	coalesce(y.value, x.VALUE) as value, x.category, x.domain_id, x.config")
		   .add("from tb_company_setting x")
		   .add("	left join (")
		   .add("   		select *")
		   .add("		from tb_company_setting")
		   .add("		where domain_id = :domainId")
		   .add("		and com_cd = :comCd")
		   .add("	) y")
		   .add("	on x.name = y.name")
		   .add("where")
		   .add("	x.domain_id = :domainId and x.com_cd = :defaultComCd")
		   .add("order by x.name");
		
		Map<String, Object> params = ValueUtil.newMap("domainId,defaultComCd,comCd", Domain.currentDomainId(), CompanySetting.DEFAULT_COMPANY_CODE, comCd);
		return this.queryManager.selectPageBySql(sql.toString(), params, this.entityClass(), 1, 100000);
	}
	
	/**
	 * 현재 로그인 도메인에 기본 고객사 별 설정을 생성
	 * 
	 * @return
	 */
	@RequestMapping(value = "/set_default_setting", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Set default company settings of current domain")
	public Boolean setDefaultSetting() {
		return this.setDefaultSetting(Domain.currentDomainId());
	}

	/**
	 * 설정한 도메인에 기본 고객사 별 설정을 생성
	 * 
	 * @param domainId
	 * @return
	 */
	@RequestMapping(value = "/set_default_setting/{domain_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Set default company settings of domain")
	public Boolean setDefaultSetting(@PathVariable("domain_id") Long domainId) {
		CompanySetting condition = new CompanySetting();
		condition.setDomainId(Domain.systemDomain().getId());
		condition.setComCd(CompanySetting.DEFAULT_COMPANY_CODE);
		List<CompanySetting> optionList = this.queryManager.selectList(CompanySetting.class, condition);
		Map<String, Object> params = ValueUtil.newMap("domainId", domainId);
		
		for(CompanySetting option : optionList) {
			params.put("comCd", option.getComCd());
			params.put("name", option.getName());
			CompanySetting oldOne = this.queryManager.selectByCondition(CompanySetting.class, params);
			
			if(ValueUtil.isEmpty(oldOne)) {
				option.setId(null);
				option.setDomainId(domainId);
				option.setUpdaterId(null);
				option.setUpdatedAt(null);
				option.setCreatorId(null);
				option.setCreatedAt(null);
				option.setCudFlag_(OrmConstants.CUD_FLAG_CREATE);
			}
		}
		
		return this.cudMultipleData(CompanySetting.class, optionList);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find one by ID")
	public CompanySetting findOne(@PathVariable("id") String id) {
		return this.getOne(this.entityClass(), id);
	}
	
	@RequestMapping(value = "/{com_cd}/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find One Company and Name")
	@Cacheable(cacheNames="CompanySetting", keyGenerator="companySettingFindApiKeyGenerator")
	public CompanySetting findBy(@PathVariable("com_cd") String comCd, @PathVariable("name") String name) {
		return this.findCompanySetting(Domain.currentDomainId(), comCd, name);
	}

	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check exists By ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create")
	@CachePut(cacheNames="CompanySetting", keyGenerator="companySettingUpdateApiKeyGenerator")
	public CompanySetting create(@RequestBody CompanySetting input) {
		return this.createOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update")
	@CachePut(cacheNames="CompanySetting", keyGenerator="companySettingUpdateApiKeyGenerator")
	public CompanySetting update(@PathVariable("id") String id, @RequestBody CompanySetting input) {
		return this.updateOne(input);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete")
	@CacheEvict(cacheNames="CompanySetting", allEntries=true)
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete multiple at one time")
	@CacheEvict(cacheNames="CompanySetting", allEntries=true)
	public Boolean multipleUpdate(@RequestBody List<CompanySetting> list) {
		return this.cudMultipleData(this.entityClass(), list);
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Clear Settings Cache")	
	@CacheEvict(cacheNames = "CompanySetting", allEntries = true)
	public boolean clearCache() {
		return true;
	}

	@ApiDesc(description = "Find One Company and Name")
	@Cacheable(cacheNames="CompanySetting", key="#domainId + '-' + #comCd + '-' + #name")
	public CompanySetting findByName(Long domainId, String comCd, String name) {
		return this.findCompanySetting(domainId, comCd, name);
	}
	
	private CompanySetting findCompanySetting(Long domainId, String comCd, String name) {
		Query condition = new Query();
		condition.addFilter("comCd", comCd);
		condition.addFilter("name", name);
		condition.addSelect("id", "comCd", "name", "value");
		return this.queryManager.selectByCondition(CompanySetting.class, condition);
	}
}