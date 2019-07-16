/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.base.rest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.elidom.base.BaseConfigConstants;
import xyz.elidom.base.BaseConstants;
import xyz.elidom.base.entity.Menu;
import xyz.elidom.base.entity.MenuButton;
import xyz.elidom.base.entity.MenuColumn;
import xyz.elidom.base.entity.MenuDetail;
import xyz.elidom.base.entity.MenuParam;
import xyz.elidom.base.entity.Resource;
import xyz.elidom.base.entity.ResourceColumn;
import xyz.elidom.base.util.ResourceUtil;
import xyz.elidom.core.entity.Code;
import xyz.elidom.core.entity.CodeDetail;
import xyz.elidom.core.rest.CodeController;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sec.rest.PermissionController;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.entity.User;
import xyz.elidom.sys.system.engine.ITemplateEngine;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.AssertUtil;
import xyz.elidom.sys.util.MessageUtil;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/menus")
@ServiceDesc(description="Menu Service API")
public class MenuController extends AbstractRestService {
	
	@Autowired
	@Qualifier("basic")
	private ITemplateEngine templateEngine;
	
	/**
	 * Menu Meta Buttons - ["import", "export", "add", "delete", "save"]
	 */
	private static final String[] MENU_META_BUTTONS = new String[] { "import", "export", "add", "delete", "save" };
	/**
	 * Menu Meta Buttons - ["create", "show", "create", "delete", "update"]
	 */
	private static final String[] MENU_BUTTON_AUTHS = new String[] { "create", "show", "create", "delete", "update" };	
	
	/**
	 * Auth Query
	 */
	private String AUTH_QUERY = new StringBuffer("")
	   .append(" SELECT")
	   .append("		m.id, m.name, m.routing_type, m.routing, m.category, m.parent_id, m.template, m.menu_type, m.detail_form_id, m.resource_type, m.resource_name, m.resource_url, m.grid_save_url, m.id_field, m.title_field, m.pagination, m.items_prop, m.total_prop, m.fixed_columns, m.detail_layout, m.icon_path, m.rank, p.action_name auth") 
	   .append(" FROM")
	   .append("		menus m, permissions p")
	   .append(" WHERE")
	   .append("		m.domain_id = :domainId and m.id = p.resource_id and m.hidden_flag != :hiddenFlag and m.category = :category")
	   .append("		and p.resource_type = 'Menu'")
	   .append("		and p.role_id in (select role_id from users_roles where user_id = :userId)")
	   .append(" ORDER BY")
	   .append("		m.rank, m.id, p.action_name").toString();
	
	/**
	 * Auth Query Parameters - 'domainId,userId,category,hiddenFlag'
	 */
	private static final String AUTH_QUERY_PARAMS = "domainId,userId,category,hiddenFlag";	
			
	/**
	 * Index - Default Select Fields - 'id,name,routing_type,routing,category,parent_id,menu_type,rank,template,detail_form_id,resource_type,resource_name,resource_url,grid_save_url,id_field,title_field,pagination,items_prop,total_prop,fixed_columns,detail_layout,icon_path'
	 */
	private static final String INDEX_DEFAULT_SELECT_FIELDS = "id,name,routing_type,routing,category,parent_id,menu_type,rank,template,detail_form_id,resource_type,resource_name,resource_url,grid_save_url,id_field,title_field,pagination,items_prop,total_prop,fixed_columns,detail_layout,icon_path";
	
	/**
	 * Index - Default Query - '[{"name" : "hidden_flag", "operator": "is_not_true"}]'
	 */
	private static final String INDEX_DEFAULT_QUERY = "[{\"name\" : \"hidden_flag\", \"operator\": \"is_not_true\"}]";
	
	/**
	 * Index - Default Sort - '[{"field" : "parentId", "ascending": true}, {"field" : "rank", "ascending": true}]'
	 */
	private static final String INDEX_DEFAULT_SORT = "[{\"field\" : \"parentId\", \"ascending\": true}, {\"field\" : \"rank\", \"ascending\": true}]";	
	
	@Override
	protected Class<?> entityClass() {
		return Menu.class;
	}
	
	public void setAuthQuery(String sql) {
		AUTH_QUERY = sql;
	}
	
	public String getAuthQuery() {
		return AUTH_QUERY;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search Menu (Pagination) By Search Conditions")
	public Page<?> index(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "mode", required = false) String mode, 
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		if(ValueUtil.isEmpty(sort)) {
			sort = INDEX_DEFAULT_SORT;
		}

		if(ValueUtil.isEmpty(query)) {
			query = INDEX_DEFAULT_QUERY;
		}

		if(ValueUtil.isEmpty(select)) {
			select = INDEX_DEFAULT_SELECT_FIELDS;
		}
		
		Page<?> result = this.search(Menu.class, page, limit, select, sort, query);
		List<Menu> menuList = (List<Menu>) result.getList();
		this.applyMenuTitle(SettingUtil.getUserLocale(), menuList);
		return result;
	}
	
	/**
	 * Mobile Menus 메소드 Default Query Prefix : '[{"name" : "parentId", "operator": "is_not_null"}, {"name" : "category", "value": "$category"}]'
	 */
	private static final String MOBILE_MENUS_DEFAULT_QUERY_TEMPLATE = "[{\"name\" : \"parentId\", \"operator\": \"is_not_null\"}, {\"name\" : \"category\", \"value\": \"$category\"},{\"name\" : \"hidden_flag\", \"operator\": \"is_not_true\"}]" ;
	/**
	 * 태블릿 용 메뉴 카테고리 
	 */
	private static final String CATEGORY_TABLET = "TABLET";
	/**
	 * PDA 용 메뉴 카테고리 
	 */	
	private static final String CATEGORY_PDA = "PDA";
	/**
	 * KIOSK 용 메뉴 카테고리
	 */
	private static final String CATEGORY_KIOSK = "KIOSK";
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/user_menus/{category}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search user menus by user authorization")	
	public List<Menu> userMenus(
			@PathVariable("category") String category, 
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		// 1. 사용자가 superuser이거나 해당 도메인 사용자이면서 admin이면 모든 화면 공개 
		if(User.isCurrentUserAdmin()) {
			if(ValueUtil.isEqualIgnoreCase(category, CATEGORY_TABLET) || ValueUtil.isEqualIgnoreCase(category, CATEGORY_PDA) || ValueUtil.isEqualIgnoreCase(category, CATEGORY_KIOSK)) {
				query = this.convertTemplate(MOBILE_MENUS_DEFAULT_QUERY_TEMPLATE, ValueUtil.newMap(BaseConstants.FIELD_NAME_CATEGORY, category));
			}
			
			Page<?> page = this.index(0, 0, BaseConstants.MENU_QUERY_AUTH_MODE, select, sort, query);
			return (List<Menu>)page.getList();
		}
		
		// 2. 일반 사용자라면 사용자 역할의 권한에 따라 메뉴를 리턴한다. 
		Map<String, Object> paramMap = ValueUtil.newMap(AUTH_QUERY_PARAMS, Domain.currentDomain().getId(), User.currentUser().getId(), category, true);
		String authQuery = this.getAuthQuery();
		List<Menu> items = (List<Menu>) super.queryManager.selectListBySql(authQuery, paramMap, Menu.class, 0, 0);
	    List<Menu> authItems = new ArrayList<Menu>();
	    
	    for(Menu menu : items) {
	    		Menu foundMenu = null;
	    	
	    		for(Menu m : authItems) {
				if(ValueUtil.isEqual(menu.getId(), m.getId())) {
					foundMenu = m;
					break;
				}
			}
	    	
	    		boolean isFirst = false;
	    	
		    	if(foundMenu == null) {
		    		foundMenu = menu;
		    		isFirst = true;
		    	}
	    	
		    	String newAuth = foundMenu.getAuth();
		    	String auth = isFirst ? foundMenu.getAuth() : menu.getAuth();
		    	
	    		if(isFirst) {
	    			if(!ValueUtil.isEmpty(auth)) {
		    			if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISSION_CREATE)) {
		    				newAuth = BaseConstants.MENU_PERMISSION_CREATE_VALUE;
		    			} else if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISSION_UPDATE)) {
		    				newAuth = BaseConstants.MENU_PERMISSION_UPDATE_VALUE;
		    			} else if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISSION_DELETE)) {
		    				newAuth = BaseConstants.MENU_PERMISSION_DELETE_VALUE;
		    			} else if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISION_SHOW)) {
		    				newAuth = BaseConstants.MENU_PERMISION_SHOW_VALUE;
		    			}
		    		}
	    		} else {
	    			if(!ValueUtil.isEmpty(auth)) {
	    				if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISSION_CREATE)) {
		    				newAuth += BaseConstants.MENU_PERMISSION_COMMA_CREATE_VALUE;
		    			} else if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISSION_UPDATE)) {
		    				newAuth += BaseConstants.MENU_PERMISSION_COMMA_UPDATE_VALUE;
		    			} else if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISSION_DELETE)) {
		    				newAuth += BaseConstants.MENU_PERMISSION_COMMA_DELETE_VALUE;
		    			} else if(ValueUtil.isEqual(auth, BaseConstants.MENU_PERMISION_SHOW)) {
		    				newAuth += BaseConstants.MENU_PERMISION_COMMA_SHOW_VALUE;
		    			}
	    			}
	    		}
	    			    		
	    		foundMenu.setAuth(newAuth);
	    		
	    		if(isFirst) {
	    			authItems.add(foundMenu);
	    		}
	    }
	    
	    this.applyMenuTitle(SettingUtil.getUserLocale(), authItems);
	    return authItems;
	}
	
	/**
	 * menuList에 menu title 다국어를 모두 적용 
	 * 
	 * @param locale
	 * @param menuList
	 */
	private void applyMenuTitle(String locale, List<Menu> menuList) {
		if(ValueUtil.isNotEmpty(menuList)) {
			for (Menu menu : menuList) {
				String title = MessageUtil.getLocaleTerm(locale, MessageUtil.getMenuTermKey(menu.getName()), menu.getName());
				menu.setTitle(title);
			}
		}
	}
	
	/**
	 * topMenus 메소드 Default Sort : '[{"field" : "rank", "ascending": true}]'
	 */
	private static final String TOP_MENUS_DEFAULT_SORT = "[{\"field\" : \"rank\", \"ascending\": true}]";
	/**
	 * topMenus 메소드 Default Query Prefix : '[{"name" : "parentId", "operator": "is_null"}, {"name" : "category", "value": "$category"}]'
	 */
	private static final String TOP_MENUS_DEFAULT_QUERY_TEMPLATE = "[{\"name\" : \"parentId\", \"operator\": \"is_null\"}, {\"name\" : \"category\", \"value\": \"$category\"}]" ;
		
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/all/top_menus", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search Top Menus Only")	
	public List<Menu> topMenus(@RequestParam(name = "category", required = false) String category) {
		if(ValueUtil.isEmpty(category)) {
			category = BaseConstants.MENU_CATEGORY_STANDARD;
		}
		
		String sort = TOP_MENUS_DEFAULT_SORT;
		String query = this.convertTemplate(TOP_MENUS_DEFAULT_QUERY_TEMPLATE, ValueUtil.newMap(BaseConstants.FIELD_NAME_CATEGORY, category));
		Page<?> output = this.search(this.entityClass(), 1, 10000, BaseConstants.STAR, sort, query);
		List<Menu> menuList = (List<Menu>) output.getList();
		this.applyMenuTitle(SettingUtil.getUserLocale(), menuList);
		return menuList;		
	}
	
	/**
	 * subMenus 메소드 Default Sort : '[{"field" : "rank", "ascending": true}]'
	 */
	private static final String SUB_MENUS_DEFAULT_SORT = "[{\"field\" : \"rank\", \"ascending\": true}]";
	/**
	 * subMenus 메소드 Default Query Prefix : '[{\"name\" : \"parentId\", \"value\": \"$id\"}'
	 */
	private static final String SUB_MENUS_DEFAULT_QUERY_PREFIX = "[{\"name\" : \"parentId\", \"value\": \"$id\"}";
	/**
	 * subMenus 메소드 Default Query Suffix : ', {"name" : "hiddenFlag", "operator" : "noteq", "value": true}]'
	 */
	private static final String SUB_MENUS_DEFAULT_QUERY_SUFFIX = ", {\"name\" : \"hiddenFlag\", \"operator\" : \"noteq\", \"value\": true}]";	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/{id}/sub_menus", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search Sub Menus of Selected Menu")
	public List<Menu> subMenus(@PathVariable("id") String id, @RequestParam(name = "select", required = false) String select, @RequestParam(name = "showall", required = false) Boolean showall) {
		String sort = SUB_MENUS_DEFAULT_SORT;
		String query = this.convertTemplate(SUB_MENUS_DEFAULT_QUERY_PREFIX, ValueUtil.newMap(OrmConstants.ENTITY_FIELD_ID, id));
		query += !showall ? SUB_MENUS_DEFAULT_QUERY_SUFFIX : "]";
		Page<?> output = this.search(this.entityClass(), 1, 1000, select, sort, query);
		List<Menu> menuList = (List<Menu>) output.getList();
		this.applyMenuTitle(SettingUtil.getUserLocale(), menuList);
		return menuList;
	}
	
	@SuppressWarnings({ "unchecked" })
	@Transactional(readOnly=true, propagation=Propagation.NEVER)
	@RequestMapping(value="/{id}/menu_meta", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find all meta data of the menu")
	public Map<String, Object> menuMeta(@PathVariable("id") String id, @RequestParam(name = "no_trans_term", required = false) boolean noTransTerm, @RequestParam(name = "ignore_on_save", required = false) boolean ignoreOnSave) {
		// 1. 메뉴에 대한 권한을 조회
		List<String> menuAuthList = BeanUtil.get(PermissionController.class).findMenuPermissionsByUser(User.currentUser().getId(), id);
		
		// 2. 메뉴에 대한 최소한 권한(읽기 권한)이 있다면 메뉴 메타 정보를 조회한다.
		Map<String, Object> result = this.menuMeta(id, ignoreOnSave, BaseConstants.MENU_OBJECT_MENU_NAME, BaseConstants.MENU_OBJECT_COLUMNS_NAME, BaseConstants.MENU_OBJECT_BUTTONS_NAME, BaseConstants.MENU_OBJECT_MENU_PARAMS_NAME);
		
		List<MenuButton> buttons = (List<MenuButton>)result.get(BaseConstants.MENU_OBJECT_BUTTONS_NAME);
		result.put(BaseConstants.MENU_OBJECT_BUTTONS_NAME, this.filterButtonByAuth(buttons, menuAuthList));
		List<MenuColumn> menuColumns = (List<MenuColumn>)result.get(BaseConstants.MENU_OBJECT_COLUMNS_NAME);
		Menu menu = (Menu)result.get(BaseConstants.MENU_OBJECT_MENU_NAME);
		String locale = User.currentUser().getLocale();
		menu.setTitle(MessageUtil.getTermByCategories(locale, menu.getName(), BaseConstants.MENU_OBJECT_MENU_NAME));
		
		// 3. 화면 그리기 모드인 경우(noTransTerm == false) 메뉴 컬럼이 존재하면 순회하면서 컬럼별 용어를 설정하고 코드 참조 컬럼인 경우 코드 정보를 조회하여 컬럼에 추가 정보로 설정한다. 
		if(!noTransTerm && !ValueUtil.isEmpty(menuColumns)) {
			this.translateMenuColumnNames(locale, menuColumns);
			this.fillCodeData(menuColumns);
		}
		
		return result;
	}
	
	/**
	 * menuColumns 컬럼들의 컬럼명을 번역한다.
	 * 
	 * @param locale
	 * @param menuColumns
	 */	
	private void translateMenuColumnNames(String locale, List<MenuColumn> menuColumns) {
		for (MenuColumn column : menuColumns) {
			String termKey = (column.getTerm() == null) ? SysConstants.TERM_LABELS + column.getName() : column.getTerm();
			column.setTerm(MessageUtil.getLocaleTerm(locale, termKey, termKey));
		}		
	}
	
	/**
	 * menuColumns 컬럼들 중 Grid 편집기가 CodeCombo인 경우 해당 컬럼에 코드 데이터를 추가한다. 
	 * 
	 * @param menuColumns
	 */
	private void fillCodeData(List<MenuColumn> menuColumns) {
		if(ValueUtil.toBoolean(SettingUtil.getValue(BaseConfigConstants.CODE_COMBO_DATA_FILL_AT_SERVER, SysConstants.TRUE_STRING))) {
			CodeController codeCtrl = BeanUtil.get(CodeController.class);
			ResourceController entityCtrl = BeanUtil.get(ResourceController.class);
		
			for (MenuColumn column : menuColumns) {
				if (ValueUtil.isEqual(BaseConstants.REF_TYPE_COMMON_CODE, column.getRefType()) && ValueUtil.isNotEmpty(column.getRefName()) && ValueUtil.isNotEmpty(column.getGridEditor()) && column.getGridEditor().startsWith(BaseConstants.GRID_CODE_EDITOR_PREFIX)) {
					Code code = codeCtrl.findOne(SysConstants.SHOW_BY_NAME_METHOD, column.getRefName());
					column.setCodeList(code.getItems());
					
				} else if(ValueUtil.isEqual(BaseConstants.REF_TYPE_ENTITY, column.getRefType()) && ValueUtil.isNotEmpty(column.getRefName()) && ValueUtil.isNotEmpty(column.getGridEditor()) && column.getGridEditor().equals("resource-code")) {
					List<CodeDetail> codeItems = entityCtrl.searchResourceDataAsCode(column.getRefName());
					column.setCodeList(codeItems);
				}
			}
		}
	}
	
	/**
	 * Menu Meta 정보 조회 
	 * 
	 * @param id
	 * @param ignoreOnSave
	 * @param menuName
	 * @param columnName
	 * @param buttonName
	 * @param paramName
	 * @return
	 */
	private Map<String, Object> menuMeta(String id, boolean ignoreOnSave, String menuName, String columnName, String buttonName, String paramName) {
		Map<String, Object> result = new HashMap<String, Object>();
		MenuController menuCtrl = BeanUtil.get(MenuController.class);
		Menu menu = menuCtrl.findOne(id, null);
		
		if(ignoreOnSave) {
			menu.setDomainId(null);
			menu.setCreator(null);
			menu.setUpdater(null);
			menu.setCreatorId(null);
			menu.setUpdaterId(null);
			menu.setCreatedAt(null);
			menu.setUpdatedAt(null);
		}
		
		List<MenuParam> menuParams = menuCtrl.findMenuParams(id);
		List<MenuColumn> menuColumns = menuCtrl.findMenuColumns(id);
		
		if (ignoreOnSave && ValueUtil.isNotEmpty(menuColumns)) {
			List<MenuColumn> ignoreColumns = new ArrayList<MenuColumn>();
			
			for (MenuColumn column : menuColumns) {
				if (column.getIgnoreOnSave()) {
					ignoreColumns.add(column);
				}
			}
			
			for(MenuColumn ignoreColumn : ignoreColumns) {
				menuColumns.remove(ignoreColumn);
			}
		}
		
		// 1. menu
		result.put(menuName, menu);
		// 2. menu columns
		result.put(columnName, menuColumns);
		// 3. menu buttons. 사용자의 메뉴 권한에 따라 메뉴 버튼을 필터링한다.
		result.put(buttonName, menuCtrl.findMenuButtons(id));
		// 4. menu params
		result.put(paramName, menuParams);	
		return result;
	}
	
	/**
	 * QUERY - Get Menu Id by Menu Name : 'SELECT ID FROM MENUS WHERE DOMAIN_ID = :domainId AND NAME = :name'
	 */
	private static final String QUERY_MENU_ID_BY_NAME = "SELECT ID FROM MENUS WHERE DOMAIN_ID = :domainId AND NAME = :name";
	
	@RequestMapping(value="/{name}/named_meta", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find all meta data of the menu by menuName")
	public Map<String, Object> namedMenuMeta(@PathVariable("name") String name) {
		String id = this.queryManager.selectBySql(QUERY_MENU_ID_BY_NAME, ValueUtil.newMap("domainId,name", Domain.currentDomain().getId(), name), String.class);
		return this.menuMeta(id, false, false);
	}
	
	@RequestMapping(value="/{id}/sync_menu_columns", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Synchroinze menu meta with Entity ID")
	public Map<String, Object> syncMenuMetaWithEntity(@PathVariable("id") String id) {
		MenuController menuCtrl = BeanUtil.get(MenuController.class);
		Menu menu = menuCtrl.findOne(id, null);
		int buttonCount = this.queryManager.selectSize(MenuButton.class, new MenuButton(id));
		int columnCount = this.queryManager.selectSize(MenuColumn.class, new MenuColumn(id));
		
		// 1. 버튼이 존재하지 않으면 생성 
		if(buttonCount == 0) {
			List<MenuButton> menuButtons = new ArrayList<MenuButton>();
			for(int i = 0 ; i < MENU_META_BUTTONS.length ; i++) {
				menuButtons.add(this.newMenuButton(id, MENU_META_BUTTONS[i], MENU_BUTTON_AUTHS[i], null, null));
			}
			
			menuCtrl.updateMultipleMenuButtons(id, menuButtons);
		}
		
		// 2. 엔티티에서 그대로 참조한 메뉴에 대해서만 메뉴 컬럼 동기화  
		if(ValueUtil.isEqual(menu.getResourceType(), BaseConstants.RESOURCE_TYPE_ENTITY)) {
			Resource entity = BeanUtil.get(ResourceController.class).findOne(SysConstants.SHOW_BY_NAME_METHOD, menu.getResourceName());
			
			// 1. 컬럼이 존재하지 않으면 메뉴 컬럼 생성 
			if(columnCount == 0) {
				// 1.1 엔티티 컬럼 조회 
				List<ResourceColumn> entityColumns = entity.resourceColumns();
				// 1.2 엔티티 컬럼의 모든 정보를 메뉴 컬럼에 모두 복사 
				List<MenuColumn> menuColumns = this.copyResourceColumn(id, entityColumns);
				// 1.3 메뉴 컬럼 생성 
				BeanUtil.get(MenuController.class).updateMultipleMenuColumns(id, menuColumns);
			
			// 2. 컬럼이 존재하면 메뉴 컬럼 동기화 - colType, colSize, nullable 만 동기화 
			} else {
				// 2.1 엔티티 클래스 - 엔티티 컬럼 동기화 
				BeanUtil.get(ResourceController.class).syncResourceColumnsWithEntity(entity.getId());
				// 2.2 엔티티 컬럼 - 메뉴 컬럼 동기화 
				int changeCount = ResourceUtil.syncMenuColumnsWithEntity(id);
				// 2.3 메뉴 컬럼 캐쉬 클리어 
				if(changeCount > 0) {
					BeanUtil.get(MenuController.class).clearCache();
				}
			}
		}
		
		return this.menuMeta(id, true, false);
	}
	
	/**
	 * menuButtonList를 authList로 필터링한다.
	 * 
	 * @param menuButtonList
	 * @param authList
	 * @return
	 */
	private List<MenuButton> filterButtonByAuth(List<MenuButton> menuButtonList, List<String> authList) {
		if(ValueUtil.isEmpty(authList)) {
			return new ArrayList<MenuButton>();
		}
		
		if(ValueUtil.isEqual(authList.get(0), BaseConstants.MENU_QUERY_ALL_MODE)) {
			return menuButtonList;
		}

		List<MenuButton> filteredList = new ArrayList<MenuButton>();
		for(MenuButton button : menuButtonList) {
			if(ValueUtil.isEmpty(button.getAuth()) || authList.contains(button.getAuth())) {
				filteredList.add(button);
			}
		}
		
		return filteredList;
	}	
	
	/**
	 * new menu button object
	 * 
	 * @param menuId
	 * @param text
	 * @param auth
	 * @param icon
	 * @param style
	 * @return
	 */
	private MenuButton newMenuButton(String menuId, String text, String auth, String icon, String style) {
		MenuButton button = new MenuButton(menuId);
		button.setText(text);
		button.setAuth(auth);
		button.setIcon(icon);
		button.setStyle(style);
		button.setCudFlag_(OrmConstants.CUD_FLAG_CREATE);
		return button;
	}
	
	/**
	 * Create Menu Column Objects From Resource Columns
	 *  
	 * @param menuId
	 * @param entityColumns
	 * @return
	 */
	private List<MenuColumn> copyResourceColumn(String menuId, List<ResourceColumn> entityColumns) {
		List<MenuColumn> menuColumns = new ArrayList<MenuColumn>();
		for(ResourceColumn entityColumn : entityColumns) {
			MenuColumn menuColumn = new MenuColumn(menuId);
			menuColumn = ValueUtil.populate(entityColumn, menuColumn);
			menuColumn.setId(null);
			menuColumn.setCudFlag_(OrmConstants.CUD_FLAG_CREATE);
			menuColumns.add(menuColumn);
		}
		
		return menuColumns;
	}	
		
	@RequestMapping(value="/{parent_id}/export", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Export Menus && Menus Columns")
	public Object export(HttpServletRequest request, HttpServletResponse response, @PathVariable("parent_id") String parentId) {
		Menu condition = new Menu();
		condition.setDomainId(Domain.currentDomainId());
		
		if(ValueUtil.isNotEmpty(parentId) && ValueUtil.isNotEqual(parentId, "all")) {
			condition.setParentId(parentId);
		}
		List<Menu> menuList = this.queryManager.selectList(Menu.class, condition);
		
		Menu menuMenu = this.findOne(SysConstants.SHOW_BY_NAME_METHOD, "Menu");
		MenuController menuCtrl = BeanUtil.get(MenuController.class);
		List<MenuColumn> menuColumn = menuCtrl.findMenuColumns(menuMenu.getId());

		ResourceController rscCtrl = BeanUtil.get(ResourceController.class);
		Resource menuRsc = rscCtrl.findOne(SysConstants.SHOW_BY_NAME_METHOD, "MenuColumn");
		Workbook workbook = ResourceUtil.exportMenusToExcel(menuList, menuColumn, menuRsc.resourceColumns());
		
		return this.excelDownloader.handleRequest(request, response, "menus", workbook);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find One By Menu ID")
	@Cacheable(cacheNames="Menu", condition="#name == null", key="'Menu-' + #id")
	public Menu findOne(@PathVariable("id") String id, @RequestParam(required = false) String name) {
		Menu menu = null;
		
		if(SysConstants.SHOW_BY_NAME_METHOD.equalsIgnoreCase(id)) {
			AssertUtil.assertNotEmpty(SysConstants.TERM_LABEL_NAME, name);
			menu = this.selectByCondition(true, Menu.class, new Menu(Domain.currentDomain().getId(), name));
		} else {
			menu = this.getOne(true, this.entityClass(), id);
		}
		
		menu.setTitle(MessageUtil.getTermByCategories(User.currentUser().getLocale(), menu.getName(), BaseConstants.FIELD_NAME_MENU));
		return menu;
	}
	
	@RequestMapping(value="/{id}/exist", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Check If Menu Exist By Menu ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}
	
	@RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description="Create Menu")
	public Menu create(@RequestBody Menu menu) {
		return this.createOne(menu);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Update Menu")
	@CachePut(cacheNames="Menu", key="'Menu-' + #id")
	public Menu update(@PathVariable("id") String id, @RequestBody Menu menu) {
		return this.updateOne(menu);
	}

	@RequestMapping(value="/{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Delete Menu")
	@CacheEvict(cacheNames="Menu", key="'Menu-' + #id")
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value="/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Update multiple menus at one time")
	public Boolean multipleUpdate(@RequestBody List<Menu> menuList) {
		MenuController ctrl = BeanUtil.get(MenuController.class);
		
		for (Menu menu : menuList) {
			if (ValueUtil.isEqual(menu.getCudFlag_(), OrmConstants.CUD_FLAG_DELETE)) {
				ctrl.delete(menu.getId());
			}
		}
		
		for (Menu menu : menuList) {
			if (ValueUtil.isEqual(menu.getCudFlag_(), OrmConstants.CUD_FLAG_UPDATE)) {
				ctrl.update(menu.getId(), menu);
			}			
		}
		
		for (Menu menu : menuList) {
			if (ValueUtil.isEqual(menu.getCudFlag_(), OrmConstants.CUD_FLAG_CREATE)) {
				ctrl.create(menu);
			}			
		}
		
		return true;
	}
	
	/**
	 * indexWithDetails 메소드의 Default Sort : '[{"field" : "parentId", "ascending": false}, {"field" : "rank", "ascending": true}]'
	 */
	private static final String INDEX_WITH_DETAILS_DEFAULT_SORT = "[{\"field\" : \"parentId\", \"ascending\": false}, {\"field\" : \"rank\", \"ascending\": true}]";
	
	@RequestMapping(value="/search_with_details", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Search all data with details")
	public List<Map<String, Object>> indexWithDetails(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query,
			@RequestParam(name = "include_default_fields", required = false) boolean includeDefaultFields) {

		if(ValueUtil.isEmpty(sort)) {
			sort = INDEX_WITH_DETAILS_DEFAULT_SORT;
		}
		
		Page<?> pageResult = this.search(this.entityClass(), page, limit, OrmConstants.ENTITY_FIELD_ID, sort, query);
		List<?> list = pageResult.getList();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		
		for(Object data : list) {
			String id = ((Menu)data).getId();
			results.add(this.menuMeta(id, true, BaseConstants.MENU_OBJECT_MASTER_NAME, BaseConstants.MENU_OBJECT_COLUMNS_NAME, BaseConstants.MENU_OBJECT_BUTTONS_NAME, BaseConstants.MENU_OBJECT_PARAMS_NAME));
		}
		
		return results;
	}
	
	@RequestMapping(value = "/{id}/include_details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find included all details by Menu ID")
	public Map<String, Object> findDetails(@PathVariable("id") String id, @RequestParam(name = "include_default_fields", required = false) boolean includeDefaultFields) {
		return this.findOneIncludedDetails(id, includeDefaultFields);
	}
	
	@RequestMapping(value="/{id}/menu_columns", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find Menu Columns By Menu ID")
	@Cacheable(cacheNames="MenuColumn", key="'MenuColumns-' + #id")
	public List<MenuColumn> findMenuColumns(@PathVariable("id") String id) {
		Query query = new Query();
		query.addFilter(new Filter(BaseConstants.FIELD_NAME_MENU_ID, id));
		query.addOrder(BaseConstants.FIELD_NAME_RANK, true);
		List<MenuColumn> columns = this.queryManager.selectList(MenuColumn.class, query);
		return columns;
	}
	
	@RequestMapping(value="/{id}/menu_columns/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Update Multiple Menu Columns")
	@CacheEvict(cacheNames="MenuColumn", key="'MenuColumns-' + #id")
	public Boolean updateMultipleMenuColumns(@PathVariable("id") String id, @RequestBody List<MenuColumn> menuColumnList) {
		return this.cudMultipleData(MenuColumn.class, menuColumnList);
	}	
	
	@RequestMapping(value="/{id}/menu_details", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find Menu Details By Menu ID")
	public List<MenuDetail> findMenuDetails(@PathVariable("id") String id) {
		Query query = new Query();
		query.addFilter(new Filter(BaseConstants.FIELD_NAME_MENU_ID, id));
		return this.queryManager.selectList(MenuDetail.class, query);
	}
	
	@RequestMapping(value="/{id}/menu_buttons", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find Menu Buttons By Menu ID")
	@Cacheable(cacheNames="MenuButton", key="'MenuButtons-' + #id")
	public List<MenuButton> findMenuButtons(@PathVariable("id") String id) {
		Query query = new Query();
		//add by lyonghwan park
		query.addOrder("rank", true);
		//
		query.addFilter(new Filter(BaseConstants.FIELD_NAME_MENU_ID, id));
		return this.queryManager.selectList(MenuButton.class, query);
	}
	
	@RequestMapping(value="/{id}/menu_buttons/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Update Multiple Menu Buttons")
	@CacheEvict(cacheNames="MenuButton", key="'MenuButtons-' + #id")
	public Boolean updateMultipleMenuButtons(@PathVariable("id") String id, @RequestBody List<MenuButton> menuButtonList) {
		return this.cudMultipleData(MenuButton.class, menuButtonList);
	}
	
	@RequestMapping(value="/{id}/menu_params", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Find MenuParams of Menu")
	@Cacheable(cacheNames="MenuParam", key="'MenuParams-' + #id")
	public @ResponseBody List<MenuParam> findMenuParams(@PathVariable("id") String id) {
		return (List<MenuParam>)this.queryManager.selectList(MenuParam.class, new MenuParam(id, null, null, null));
	}
	
	@RequestMapping(value="/{id}/menu_params/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Create, Update or Delete multiple MenuParams of Menu at one time")
	@CacheEvict(cacheNames="MenuParam", key="'MenuParams-' + #id")
	public @ResponseBody List<MenuParam> updateMultipleMenuParams(@PathVariable("id") String id, @RequestBody List<MenuParam> menuParamList) {
		this.cudMultipleData(MenuParam.class, menuParamList);
		return (List<MenuParam>)this.queryManager.selectList(MenuParam.class, new MenuParam(id, null, null, null));
	}	
	
	@RequestMapping(value = "/clear_menu_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(cacheNames = "Menu", allEntries = true)
	public boolean clearMenuCache() {
		return true;
	}
	
	@RequestMapping(value = "/clear_menu_column_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(cacheNames = "MenuColumn", allEntries = true)
	public boolean clearMenuColumnsCache() {
		return true;
	}
	
	@RequestMapping(value = "/clear_menu_button_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(cacheNames = "MenuButton", allEntries = true)
	public boolean clearMenuButtonsCache() {
		return true;
	}
	
	@RequestMapping(value = "/clear_menu_param_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@CacheEvict(cacheNames = "MenuParam", allEntries = true)
	public boolean clearMenuParamsCache() {
		return true;
	}
	
	@RequestMapping(value = "/clear_cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean clearCache() {
		MenuController menuCtrl = BeanUtil.get(MenuController.class);
		menuCtrl.clearMenuCache();
		menuCtrl.clearMenuColumnsCache();
		menuCtrl.clearMenuButtonsCache();
		menuCtrl.clearMenuParamsCache();
		return true;
	}
	
	/**
	 * convert template
	 * 
	 * @param template
	 * @param templateParams
	 * @return
	 */
	private String convertTemplate(String template, Map<String, Object> templateParams) {
		StringWriter writer = new StringWriter();
		this.templateEngine.processTemplate(template, writer, templateParams, null);
		return writer.toString();
	}
	
}