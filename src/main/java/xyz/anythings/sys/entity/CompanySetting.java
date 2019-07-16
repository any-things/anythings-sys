package xyz.anythings.sys.entity;

import xyz.anythings.sys.rest.CompanySettingController;
import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Table;
import xyz.elidom.sys.util.ValueUtil;
import xyz.elidom.util.BeanUtil;

@Table(name = "tb_company_setting", idStrategy = GenerationRule.UUID, uniqueFields="domainId,comCd,name", indexes = {
	@Index(name = "ix_tb_company_setting_0", columnList = "name,com_cd,domain_id", unique = true)
})
public class CompanySetting extends xyz.elidom.orm.entity.basic.ElidomStampHook implements ISettingValue {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 997923715984476312L;
	/**
	 * 디폴트 고객사 코드
	 */
	public static final String DEFAULT_COMPANY_CODE = "_DEFAULT_";

	@PrimaryKey
	@Column (name = "id", nullable = false, length = 40)
	private String id;

	@Column (name = "com_cd", nullable = false, length = 32)
	private String comCd;

	@Column (name = "name", nullable = false, length = 100)
	private String name;

	@Column (name = "description")
	private String description;

	@Column (name = "value", length = 1000)
	private String value;

	@Column (name = "category", length = 20)
	private String category;

	@Column (name = "config", length = 4000)
	private String config;

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComCd() {
		return comCd;
	}

	public void setComCd(String comCd) {
		this.comCd = comCd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public void beforeCreate() {
		super.beforeCreate();

		if(ValueUtil.isEmpty(this.comCd)) {
			this.comCd = CompanySetting.DEFAULT_COMPANY_CODE;
		}
	}

	/**
	 * 고객사 코드와 설정명으로 설정값 조회 - 고객사 코드로 찾지 못하면 기본 설정으로 조회
	 *
	 * @param domainId
	 * @param comCd
	 * @param name
	 * @return
	 */
	public static CompanySetting findSetting(Long domainId, String comCd, String name) {
		CompanySettingController companySettingCtrl = BeanUtil.get(CompanySettingController.class);
		comCd = ValueUtil.isNotEmpty(comCd) ? comCd : DEFAULT_COMPANY_CODE;
		CompanySetting setting = companySettingCtrl.findByName(domainId, comCd, name);
		return (setting != null) ? setting : (ValueUtil.isEqualIgnoreCase(comCd, DEFAULT_COMPANY_CODE) ? null : companySettingCtrl.findByName(domainId, DEFAULT_COMPANY_CODE, name));
	}

	/**
	 * 고객사 설정 값 리턴, 없다면 기본값으로 defaultValue 리턴
	 *
	 * @param setting
	 * @param defaultValue
	 * @return
	 */
	public static String settingValue(CompanySetting setting, String defaultValue) {
		return (setting == null) ? defaultValue : setting.getValue();
	}

	/**
	 * domainId, comCd, name으로 고객사 설정을 조회 후 value를 리턴, 혹시 존재하지 않으면 null 리턴
	 *
	 * @param domainId
	 * @param comCd
	 * @param name
	 * @return
	 */
	public static String settingValue(Long domainId, String comCd, String name) {
		CompanySetting cs = findSetting(domainId, comCd, name);
		return settingValue(cs, null);
	}

}
