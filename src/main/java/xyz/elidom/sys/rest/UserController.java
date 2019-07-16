/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.sys.rest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.anythings.sys.AnythingsSysConstants;
import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Order;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.exception.client.ElidomBadRequestException;
import xyz.elidom.exception.client.ElidomInvalidParamsException;
import xyz.elidom.exception.client.ElidomRecordNotFoundException;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.sys.SysConfigConstants;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.SysMessageConstants;
import xyz.elidom.sys.entity.PasswordHistory;
import xyz.elidom.sys.entity.User;
import xyz.elidom.sys.system.auth.AuthProviderFactory;
import xyz.elidom.sys.system.auth.IAuthProvider;
import xyz.elidom.sys.system.auth.model.CheckPassword;
import xyz.elidom.sys.system.engine.ITemplateEngine;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.system.transport.sender.MailSender;
import xyz.elidom.sys.util.FileUtil;
import xyz.elidom.sys.util.MessageUtil;
import xyz.elidom.sys.util.SessionUtil;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.DateUtil;

/**
 * UserController
 * 
 * @author shortstop
 */
@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/users")
@ServiceDesc(description = "User Service API")
public class UserController extends AbstractRestService {
	
	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	@Qualifier("basic")
    private ITemplateEngine templateEngine;
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private AuthProviderFactory authProviderFactory;
	
	@Override
	protected Class<?> entityClass() {
		return User.class;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search User (Pagination) By Search Conditions")
	public Page<?> index(
			@RequestParam(name = "page", required = false) Integer page, 
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "select", required = false) String select, 
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		Query queryObj = this.parseQuery(this.entityClass(), page, limit, select, sort, query);
		queryObj.addFilter(new Filter("accountType", "noteq", SysConstants.ACCOUNT_TYPE_TOKEN));
		return this.search(this.entityClass(), queryObj);
	}
	
	@Override
	protected boolean isDomainBased(Class<?> clazz) {
		return false;
	}
	
	@Override
	public Page<?> search(Class<?> entityClass, Integer page, Integer limit, String select, String sort, String query) {
		Query input = new Query();
		input.setPageIndex(page == null ? 1 : page.intValue());
		limit = (limit == null) ? 50 : limit.intValue();
		input.setPageSize(limit);
		String[] selectFields = ValueUtil.isEmpty(select) ? null : select.split(AnythingsSysConstants.COMMA);

		if (!ValueUtil.isEmpty(selectFields)) {
			List<String> selectColumns = new ArrayList<String>();
			for (int i = 0; i < selectFields.length; i++) {
				selectColumns.add(selectFields[i]);
			}
			
			input.setSelect(selectColumns);
		}

		if (ValueUtil.isNotEmpty(sort)) {
			Order[] orders = this.jsonParser.parse(sort, Order[].class);
			input.addOrder(orders);
		}

		if (ValueUtil.isNotEmpty(query)) {
			Filter[] filters = this.jsonParser.parse(query, Filter[].class);
			input.addFilter(filters);
		}

		return this.search(entityClass, input);
	}

	@RequestMapping(value = "/{id:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Find User By ID")
	public User findOne(@PathVariable("id") String id) {
		return this.getOne(true, this.entityClass(), id);
	}

	@RequestMapping(value = "/exist/{id:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check if Users exists By ID")
	public Boolean isExist(@PathVariable("id") String id) {
		return this.isExistOne(this.entityClass(), id);
	}
	
	@RequestMapping(value = "/check_import", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Check Before Import")
	public List<User> checkImport(@RequestBody List<User> list) {
		for (User item : list) {
			this.checkForImport(User.class, item);
		}
		
		return list;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiDesc(description = "Create User")
	public User create(@RequestBody User user) {
		return this.createOne(user);
	}

	@RequestMapping(value = "/{id:.+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Update User")
	public User update(@PathVariable("id") String id, @RequestBody User user) {
		return this.updateOne(user);
	}

	@RequestMapping(value = "/{id:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Delete User By ID")
	public void delete(@PathVariable("id") String id) {
		this.deleteOne(this.entityClass(), id);
	}

	@RequestMapping(value = "/update_multiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Create, Update or Delete multiple Users at one time")
	public Boolean multipleUpdate(@RequestBody List<User> userList) {
		return this.cudMultipleData(this.entityClass(), userList);
	}
	
	@RequestMapping(value = "/change_pass/{id:.+}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Change Password.")
	public boolean changePass(@PathVariable("id") String id, @RequestBody CheckPassword checkPass) {
		User user = this.findOne(id);
		if (ValueUtil.isEmpty(user)) {
			throw new ElidomRecordNotFoundException(SysMessageConstants.USER_NOT_EXIST, "User does not exist");
		}

		IAuthProvider authProvider = this.authProviderFactory.getAuthProvider();
		boolean result = authProvider.isPasswordValid(user.getEncryptedPassword(), checkPass.getCurrentPass());
		if (!result) {
			throw new ElidomInvalidParamsException(SysMessageConstants.USER_INVALID_ID_OR_PASS, "ID or Password is not correct");
		}

		/**
		 * 최근 사용한 비밀번호 여부와 변경 횟수 검사.
		 */
		PasswordHistoryController passwordHistoryController = BeanUtil.get(PasswordHistoryController.class);
		passwordHistoryController.validationCheck(id, checkPass.getNewPass());

		/**
		 * 비밀번호 변경 시, Expire Date를 설정한 기간만큼 연장.
		 */
		String period = SettingUtil.getValue(SysConfigConstants.USER_PASSWORD_CHANGE_PERIOD_DAY, "90");
		String parseDate = DateUtil.addDateToStr(new Date(), ValueUtil.toInteger(period));
		SessionUtil.removeAttribute(SysConstants.ACCOUNT_STATUS);

		user.setPasswordExpireDate(parseDate);
		user.setEncryptedPassword(checkPass.getNewPass());
		user.setResetPasswordToken("");
		user.setName(checkPass.getName());

		this.updateOne(user);

		/**
		 * 비밀번호 변경 이력 생성.
		 */
		passwordHistoryController.create(new PasswordHistory(checkPass.getNewPass(), id));

		return true;
	}
	
	@RequestMapping(value = "/change_pass_later/{id:.+}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Change Password Later.")
	public boolean changePassLater(@PathVariable("id") String id) {
		String laterDay = SettingUtil.getValue(SysConfigConstants.USER_PASSWORD_CHANGE_LATER_DAY, "30");
		String parseDate = DateUtil.addDateToStr(new Date(), ValueUtil.toInteger(laterDay));

		User user = this.findOne(id);
		user.setPasswordExpireDate(parseDate);
		this.updateOne(user);

		SessionUtil.removeAttribute(SysConstants.ACCOUNT_STATUS);
		return true;
	}
	
	@RequestMapping(value = "/active/{id:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Activate account")
	public String activeAccount(@PathVariable("id") String id) {
		User account = this.getOne(true, this.entityClass(), id);
		
		if (account.getActiveFlag()) {
			throw new ElidomBadRequestException(SysMessageConstants.USER_ALREADY_ACTIVATED, "Already activated account");
		}
		
		account.setActiveFlag(true);
		this.updateOne(account);
		
		String title = MessageUtil.getMessage(SysMessageConstants.USER_COMPLETE_ACTIVE_ACCOUNT, "Your account is activated!");
		String loginLink = SettingUtil.getValue(SysConfigConstants.CLIENT_CONTEXT_PATH, "http://factory.hatiolab.com");
		Map<String, Object> templateParams = ValueUtil.newMap("loginLink,title,systemName,domain,email,userId,userName", loginLink, title, account.getDomain().getBrandName(), account.getDomain().getBrandName(), account.getEmail(), account.getId(), account.getName());
		UserController ctrl = BeanUtil.get(UserController.class);
		ctrl.sendMailToRequester(SysConfigConstants.MAIL_TEMPLATE_ACCOUNT_ACTIVATION_APPROVED, templateParams);
		return MessageUtil.getMessage(SysMessageConstants.RESULT_SENT_TO_REQUESTER, "Results of processing your request has been sent.");
	}
	
	@RequestMapping(value = "/inactive/{id:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Deactivate account")
	public String inactiveAccount(@PathVariable("id") String id) {
		User account = this.getOne(true, this.entityClass(), id);
		
		if(!account.getActiveFlag()) {
			throw new ElidomBadRequestException(SysMessageConstants.USER_INACTIVATED_ACCOUNT, "Deactivated account.");
		}
				
		account.setActiveFlag(false);
		this.updateOne(account);
		
		String title = MessageUtil.getMessage(SysMessageConstants.USER_INACTIVE_ACCOUNT, "Your account is deactivated");
		String loginLink = SettingUtil.getValue(SysConfigConstants.CLIENT_CONTEXT_PATH, "http://factory.hatiolab.com");
		Map<String, Object> templateParams = ValueUtil.newMap("loginLink,title,systemName,domain,email,userId,userName", loginLink, title, account.getDomain().getBrandName(), account.getDomain().getBrandName(), account.getEmail(), account.getId(), account.getName());
		UserController ctrl = BeanUtil.get(UserController.class);
		ctrl.sendMailToRequester(SysConfigConstants.MAIL_TEMPLATE_ACCOUNT_INACTIVATION_RESULT, templateParams);
		return MessageUtil.getMessage(SysMessageConstants.RESULT_SENT_TO_REQUESTER, "Results of processing your request has been sent.");
	}
	
	/**
	 * Send mail to requester
	 * 
	 * @param templatePath
	 * @param templateParams
	 */
	@Async
	public void sendMailToRequester(String templatePath, Map<String, Object> templateParams) {
		templateParams.put("processedAt", DateUtil.currentTimeStr());
		String title = (String)templateParams.get("title");
		String to = (String)templateParams.get("email");
		String content = this.convertTemplate(templatePath, templateParams);
		this.logger.info(content);
		this.mailSender.send(title, null, to, content, templateParams, ValueUtil.newMap("mimeType", "text/html"));
	}
	
	/**
	 * translate template
	 * 
	 * @param templatePath
	 * @param templateParams
	 * @return
	 */
	private String convertTemplate(String templatePath, Map<String, Object> templateParams) {
		templatePath = this.makeTemplatePath(templatePath);
		String template = FileUtil.readClassPathResource(templatePath);
		StringWriter writer = new StringWriter();
		this.templateEngine.processTemplate(template, writer, templateParams, null);
		return writer.toString();
	}
	
	/**
	 * 메일 templatePath를 완성하여 리턴 
	 * 
	 * @param templatePath
	 * @return
	 */
	private String makeTemplatePath(String templatePath) {
		templatePath = SysConstants.MAIL_TEMPLATE_PATH_PREFIX + templatePath; 
		templatePath = templatePath.replace(OrmConstants.DOT, OrmConstants.SLASH);
		templatePath += SysConstants.MAIL_TEMPLATE_PATH_SUFFIX;
		return templatePath;
	}
}