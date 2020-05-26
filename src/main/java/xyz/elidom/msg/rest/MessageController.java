/* Copyright © HatioLab Inc. All rights reserved. */
/**
 * 
 */
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
import xyz.elidom.msg.entity.Message;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.rest.DomainController;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/messages")
@ServiceDesc(description = "Message Service API")
public class MessageController extends AbstractRestService {
	
	/**
	 * 로케일 쿼리 
	 */
	private static final String LOCALE_QUERY = "SELECT distinct(LOCALE) locale FROM MESSAGES";	
	/**
	 * MESSAGE QUERY
	 */
	private static final String MSG_QUERY = "SELECT NAME, DISPLAY FROM MESSAGES WHERE DOMAIN_ID = :domainId AND LOCALE = :locale";

	@Override
	protected Class<?> entityClass() {
		return Message.class;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find all messages")
	public Map<String, Object> all() {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		List<String> locales = this.queryManager.selectListBySql(LOCALE_QUERY, null, String.class, 0, 0);
		MessageController ctrl = BeanUtil.get(MessageController.class);

		// locale별 message 추출
		for (String locale : locales) {
			messageMap.put(locale, ctrl.allByLocale(locale));
		}
		
		return messageMap;
	}

	@RequestMapping(value = "/all/{locale}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find All Messages By Locale")
	@Cacheable(cacheNames = "Message", key = "#locale")
	public Map<String, String> allByLocale(@PathVariable("locale") String locale) {
		Map<String, String> messageMap = new HashMap<String, String>();
		Map<String, Object> params = ValueUtil.newMap("domainId,locale", Domain.systemDomain().getId(), locale);
		List<Message> list = this.queryManager.selectListBySql(MSG_QUERY, params, Message.class, 0, 0);

		for (Message msg : list) {
			messageMap.put(msg.getName(), msg.getDisplay());
		}
		
		return messageMap;
	}

	@RequestMapping(value = "/{locale}/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find message by locale and name")
	@Cacheable(cacheNames = "Message", key = "#locale + #name")
	public String findBy(@PathVariable("locale") String locale, @PathVariable("name") String name) {
		Map<String, String> messageMap = BeanUtil.get(MessageController.class).allByLocale(locale);
		return (messageMap.containsKey(name)) ? messageMap.get(name) : null;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search Messages (Pagination) By Search Conditions")
	public Page<?> index(@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "select", required = false) String select,
			@RequestParam(name = "sort", required = false) String sort, 
			@RequestParam(name = "query", required = false) String query) {
		return this.search(this.entityClass(), page, limit, select, sort, query);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "find one Message by ID")
	public Message findOne(@PathVariable("id") String id) {
		return this.getOne(true, this.entityClass(), id);
	}	

	@RequestMapping(value = "/{id}/exist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check Message is exist by ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}
	
	@RequestMapping(value = "/check_import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check Before Import")
	public List<Message> checkImport(@RequestBody List<Message> list) {
		for (Message item : list) {
			this.checkForImport(Message.class, item);
		}
		
		return list;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create Message")
	public Message create(@RequestBody Message Message) {
		return this.createOne(Message);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update Message")
	public Message update(@PathVariable("id") String id, @RequestBody Message Message) {
		return this.updateOne(Message);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete Message by ID")
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete multiple Message by one time")
	public Boolean multipleUpdate(@RequestBody List<Message> MessageList) {
		Boolean result = this.cudMultipleData(this.entityClass(), MessageList);
		
		if(result) {
			BeanUtil.get(MessageController.class).clearCache();
		}
		
		return result;
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean messageClearCache() {
		return BeanUtil.get(DomainController.class).requestClearCache("messages");
	}


	@CacheEvict(cacheNames = "Message", allEntries = true)
	public boolean clearCache() {
		BeanUtil.get(MessageController.class).all();
		return true;
	}
}