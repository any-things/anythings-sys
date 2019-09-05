package xyz.anythings.sys.entity;

import xyz.anythings.sys.rest.ScopeSettingController;
import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@Table(name = "tb_scope_setting", idStrategy = GenerationRule.UUID, uniqueFields="domainId,scopeType,scopeCd,name", indexes = {
	@Index(name = "ix_tb_scope_setting_0", columnList = "name,scope_cd,scope_type,domain_id", unique = true)
})
public class ScopeSetting extends xyz.elidom.orm.entity.basic.ElidomStampHook implements ISettingValue {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 997923715984476312L;
	
	/**
	 * 디폴트 적용 범위 코드
	 */
	public static final String DEFAULT_SCOPE_TYPE = "_DEFAULT_";
	/**
	 * 디폴트 적용 범위 명칭 
	 */
	public static final String DEFAULT_SCOPE_NAME = "범위 설정 기본값";

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "scope_type", nullable = false, length = 32)
	private String scopeType;
	
	@Column (name = "scope_name", nullable = false, length = OrmConstants.FIELD_SIZE_NAME)
	private String scopeName;

	@Column (name = "scope_cd", nullable = false, length = 32)
	private String scopeCd;

	@Column (name = "name", nullable = false, length = OrmConstants.FIELD_SIZE_NAME)
	private String name;

	@Column (name = "value", nullable = false, length = OrmConstants.FIELD_SIZE_NAME)
	private String value;

	@Column (name = "description")
	private String description;

	@Column (name = "config", length = 4000)
	private String config;

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	public String getScopeCd() {
		return scopeCd;
	}

	public void setScopeCd(String scopeCd) {
		this.scopeCd = scopeCd;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	public void beforeCreate() {
		super.beforeCreate();

		if(ValueUtil.isEmpty(this.scopeType)) {
			this.scopeType = ScopeSetting.DEFAULT_SCOPE_TYPE;
			this.scopeName = ScopeSetting.DEFAULT_SCOPE_NAME;
		}
	}

	/**
	 * 범위 설정값 검색 
	 * @param domainId
	 * @param scopeCd
	 * @param scopeType
	 * @param name
	 * @return
	 */
	public static ScopeSetting findSetting(Long domainId, String scopeType, String scopeCd, String name) {
		ScopeSettingController scopeSettingCtrl = BeanUtil.get(ScopeSettingController.class);
		scopeType = ValueUtil.isNotEmpty(scopeType) ? scopeType : DEFAULT_SCOPE_TYPE;
		ScopeSetting setting = scopeSettingCtrl.findByName(domainId, scopeType, scopeCd, name);
		return (setting != null) ? setting : (ValueUtil.isEqualIgnoreCase(scopeType, DEFAULT_SCOPE_TYPE) ? null : scopeSettingCtrl.findByName(domainId, DEFAULT_SCOPE_TYPE, scopeCd, name));
	}

	/**
	 * 고객사 설정 값 리턴, 없다면 기본값으로 defaultValue 리턴
	 *
	 * @param setting
	 * @param defaultValue
	 * @return
	 */
	public static String settingValue(ScopeSetting setting, String defaultValue) {
		return (setting == null) ? defaultValue : setting.getValue();
	}

	/**
	 * domainId, scopeType, scopeCd 설정을 조회 후 value를 리턴, 혹시 존재하지 않으면 null 리턴
	 *
	 * @param domainId
	 * @param scopeType
	 * @param scopeCd
	 * @param name
	 * @return
	 */
	public static String settingValue(Long domainId, String scopeType, String scopeCd, String name) {
		ScopeSetting cs = findSetting(domainId, scopeType, scopeCd, name);
		return settingValue(cs, null);
	}
}
