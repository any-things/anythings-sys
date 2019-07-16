/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.elidom.sys.util;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.util.StringUtils;

import xyz.elidom.msg.rest.TerminologyController;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.sys.SysConstants;
import xyz.elidom.sys.msg.I18nMsgTranslator;
import xyz.elidom.sys.msg.I18nTermTranslator;
import xyz.elidom.sys.msg.IEntityTranslator;
import xyz.elidom.sys.msg.basic.BasicEntityTranslator;
import xyz.elidom.sys.msg.basic.BasicMessageTranslator;
import xyz.elidom.sys.msg.basic.BasicTermTranslator;
import xyz.elidom.util.BeanUtil;

/**
 * 메시지 혹은 다국어 처리를 위한 Utility
 * 
 * @author shortstop
 */
public class MessageUtil {

	/**
	 * 메시지 번역기
	 */
	private static I18nMsgTranslator MSG_TRANS;

	/**
	 * 용어 번역기
	 */
	private static I18nTermTranslator TERM_TRANS;
	
	/**
	 * 엔티티 기반 번역기 
	 */
	private static IEntityTranslator ENTITY_TRANS;

	/**
	 * 메시지 번역기 설정
	 * 
	 * @param msgTrans
	 */
	public static void setMsgTranslator(I18nMsgTranslator msgTrans) {
		if ((MSG_TRANS == null) || (MSG_TRANS instanceof BasicMessageTranslator)) {
			MSG_TRANS = msgTrans;
		}
	}

	/**
	 * 용어 번역기 설정
	 * 
	 * @param termTrans
	 */
	public static void setTermTranslator(I18nTermTranslator termTrans) {
		if ((TERM_TRANS == null) || (TERM_TRANS instanceof BasicTermTranslator)) {
			TERM_TRANS = termTrans;
		}
	}
	
	/**
	 * 엔티티 번역기 설정
	 * 
	 * @param entityColTrans
	 */
	public static void setEntityTranslator(IEntityTranslator entityColTrans) {
		if ((ENTITY_TRANS == null) || (ENTITY_TRANS instanceof BasicEntityTranslator)) {
			ENTITY_TRANS = entityColTrans;
		}
	}

	/**
	 * Parameter 번역
	 * 
	 * @param locale
	 * @param paramStrs
	 * @return
	 */
	public static List<String> localeParams(String locale, String... paramStrs) {
		return TERM_TRANS.translateLocaleParams(locale, paramStrs);
	}
	
	/**
	 * Parameter 번역
	 * 
	 * @param paramStrs
	 * @return
	 */
	public static List<String> params(String... paramStrs) {
		String locale = SettingUtil.getUserLocale();
		return localeParams(locale, paramStrs);
	}	

	/**
	 * msgCode에 대한 메시지 번역 
	 * 
	 * @param msgCode
	 * @return
	 */
	public static String getMessage(String msgCode) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleMessage(locale, msgCode);
	}

	/**
	 * msgCode에 대한 메시지 번역 - 번역이 안 되면 defaultValue를 리턴 
	 * 
	 * @param msgCode
	 * @param defaultValue
	 * @return
	 */
	public static String getMessage(String msgCode, String defaultValue) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleMessage(locale, msgCode, defaultValue);
	}

	/**
	 * msgCode와 파라미터 params에 대한 메시지 번역 - 번역이 안 되면 defaultValue를 리턴 
	 * 
	 * @param msgCode
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String getMessage(String msgCode, String defaultValue, List<String> params) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleMessage(locale, msgCode, defaultValue, params);
	}

	/**
	 * 로케일 기반으로 msgCode에 대한 메시지 번역 - 번역이 안 되면 defaultValue를 리턴
	 * 
	 * @param locale
	 * @param msgCode
	 * @return
	 */
	public static String getLocaleMessage(String locale, String msgCode) {
		return MSG_TRANS.translate(locale, msgCode);
	}
	
	/**
	 * 로케일 기반으로 msgCode에 대한 메시지 번역 - 번역이 안 되면 defaultValue를 리턴
	 * 
	 * @param locale
	 * @param msgCode
	 * @param defaultValue
	 * @return
	 */
	public static String getLocaleMessage(String locale, String msgCode, String defaultValue) {
		return MSG_TRANS.translate(locale, msgCode, defaultValue);
	}
	
	/**
	 * 로케일 기반으로 msgCode에 대한 메시지 번역 - 번역이 안 되면 defaultValue를 리턴
	 * 
	 * @param locale
	 * @param msgCode
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String getLocaleMessage(String locale, String msgCode, String defaultValue, List<String> params) {
		return MSG_TRANS.translate(locale, msgCode, defaultValue, params);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 null 리턴
	 * 
	 * @param termKey
	 * @return
	 */
	public static String getTerm(String termKey) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleTerm(locale, termKey);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param termKey
	 * @param defaultValue
	 * @return
	 */
	public static String getTerm(String termKey, String defaultValue) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleTerm(locale, termKey, defaultValue);
	}
	
	/**
	 * termKey, 파라미터 params로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param termKey
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String getTerm(String termKey, String defaultValue, List<String> params) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleTerm(locale, termKey, defaultValue, params);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 null 리턴
	 * 
	 * @param locale
	 * @param termKey
	 * @return
	 */
	public static String getLocaleTerm(String locale, String termKey) {
		return TERM_TRANS.translate(locale, termKey);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param locale
	 * @param termKey
	 * @param defaultValue
	 * @return
	 */
	public static String getLocaleTerm(String locale, String termKey, String defaultValue) {
		return TERM_TRANS.translate(locale, termKey, defaultValue);
	}
	
	/**
	 * termKey, 파라미터 params로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param locale
	 * @param termKey
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String getLocaleTerm(String locale, String termKey, String defaultValue, List<String> params) {
		return TERM_TRANS.translate(locale, termKey, defaultValue, params);
	}
	
	/**
	 * Cache에서 categories와 name 조합으로 key를 찾아 리턴. 
	 * 없으면 categories를 순환하며 try. 없으면 categories 첫번째 값과 name을 조합하여 리턴 
	 * 
	 * @param locale
	 * @param name
	 * @param categories
	 * @return
	 */
	public static String getTermByCategories(String locale, String name, String... categories) {
		if(categories == null || categories.length == 0) {
			return null;
		}
		
		locale = SettingUtil.getLocale(locale);
		for(String category : categories) {
			String term = getLocaleTerm(locale, category + "." + name);
			if(term != null) {
				return term;
			}
		}
		
		return categories[0] + "." + name;
	}
	
	/**
	 * 전달받은 key로 번역을 위한 메뉴 키를 리턴한다. 'terms.menu.' + key
	 *  
	 * @param key
	 * @return
	 */
	public static String getMenuTermKey(String key) {
		return TERM_TRANS.getMenuTermKey(key);
	}
	
	/**
	 * 전달받은 key로 번역을 위한 버튼 키를 리턴한다. 'terms.button.' + key
	 *  
	 * @param key
	 * @return
	 */
	public static String getButtonTermKey(String key) {
		return TERM_TRANS.getButtonTermKey(key);
	}
	
	/**
	 * 전달받은 key로 번역을 위한 라벨 키를 리턴한다. 'terms.label.' + key
	 *  
	 * @param key
	 * @return
	 */
	public static String getLabelTermKey(String key) {
		return TERM_TRANS.getLabelTermKey(key);
	}
	
	/**
	 * 전달받은 key로 번역을 위한 설정 키를 리턴한다. 'terms.setting.' + key
	 *  
	 * @param key
	 * @return
	 */
	public static String getSettingKey(String key) {
		return TERM_TRANS.getSettingKey(key);
	}

	/**
	 * 이름이 entityName인 엔티티에 대한 필드명 (colName) 다국어 번역
	 * 
	 * @param entityName
	 * @param colName
	 * @return
	 */
	public static String getTermByEntity(String entityName, String colName) {
		return ENTITY_TRANS.getTermByEntity(entityName, colName);
	}
	
	/**
	 * 이름이 entityName인 엔티티에 대한 필드명 (colNameList) 다국어 번역
	 * 
	 * @param entityName
	 * @param colNameList
	 * @return
	 */
	public static String getTermByEntity(String entityName, List<String> colNameList) {
		return ENTITY_TRANS.getTermByEntity(entityName, colNameList);
	}
	
	/*******************************************************
	 * 						추가 API
	 *******************************************************/
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 null 리턴
	 * 
	 * @param domainId
	 * @param termKey
	 * @return
	 */
	public static String getTerm(Long domainId, String termKey) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleTerm(domainId, locale, termKey);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param domainId
	 * @param termKey
	 * @param defaultValue
	 * @return
	 */
	public static String getTerm(Long domainId, String termKey, String defaultValue) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleTerm(domainId, locale, termKey, defaultValue);
	}
	
	/**
	 * termKey, 파라미터 params로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param domainId
	 * @param termKey
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String getTerm(Long domainId, String termKey, String defaultValue, List<String> params) {
		String locale = SettingUtil.getUserLocale();
		return getLocaleTerm(domainId, locale, termKey, defaultValue, params);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 null 리턴
	 * 
	 * @param domainId
	 * @param locale
	 * @param termKey
	 * @return
	 */
	public static String getLocaleTerm(Long domainId, String locale, String termKey) {
		return translate(domainId, locale, termKey);
	}
	
	/**
	 * termKey로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param domainId
	 * @param locale
	 * @param termKey
	 * @param defaultValue
	 * @return
	 */
	public static String getLocaleTerm(Long domainId, String locale, String termKey, String defaultValue) {
		String termVal = getLocaleTerm(domainId, locale, termKey);
		return ValueUtil.checkValue(termVal, defaultValue);
	}
	
	/**
	 * termKey, 파라미터 params로 용어를 찾아 리턴. 없으면 defaultValue 리턴
	 * 
	 * @param locale
	 * @param termKey
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String getLocaleTerm(Long domainId, String locale, String termKey, String defaultValue, List<String> params) {
		return translate(domainId, locale, termKey, defaultValue, params);
	}
	
	/**
	 * domainId, locale, termKey를 기반으로 용어를 조회 
	 * 
	 * @param domainId
	 * @param locale
	 * @param termKey
	 * @return
	 */
	public static String translate(Long domainId, String locale, String termKey) {
		// 1. terms.label.로 시작 : terms.label.name --> 'terms.' 제외하고 label.name
		if (termKey.startsWith(SysConstants.TERM_LABEL_PREFIX)) { 
			String terms = termKey.substring(termKey.indexOf(OrmConstants.DOT) + 1, termKey.length());
			String label = BeanUtil.get(TerminologyController.class).findBy(domainId, locale, terms);
			return (label != null) ? label : termKey;
			
		// 2. label.으로 시작 : label.name --> 그대로 'label.name'
		} else if (termKey.startsWith(SysConstants.TERM_LABELS)) {
			String label = BeanUtil.get(TerminologyController.class).findBy(domainId, locale, termKey);
			return (label != null) ? label : termKey;
		
		// 3. terms. 으로 시작 
		// 	case-1) terms.menu.name 즉 '.'이 두 개 이상인 경우 --> 'terms.' 제외하고 'menu.name'
		// 	case-2) terms.name --> 'terms.' 제외하고 label.name
		} else if (termKey.startsWith(SysConstants.TERM_TERMS)) {
			int dotOccurance = StringUtils.countOccurrencesOf(termKey, SysConstants.DOT);
			String terms = (dotOccurance > 1) ? termKey.substring(termKey.indexOf(SysConstants.DOT) + 1, termKey.length()) : 
				SysConstants.TERM_LABEL_PREFIX + termKey.substring(termKey.indexOf(SysConstants.DOT) + 1, termKey.length());
			String label = BeanUtil.get(TerminologyController.class).findBy(domainId, locale, terms);
			return (label != null) ? label : termKey;
						
		} else {
			return termKey;
		}		
	}
	
	/**
	 * domainId, locale, termKey를 기반으로 용어를 조회 
	 * 
	 * @param domainId
	 * @param locale
	 * @param termKey
	 * @param defaultValue
	 * @param params
	 * @return
	 */
	public static String translate(Long domainId, String locale, String termKey, String defaultValue, List<String> params) {
		// 1. 로케일 체크 
		locale = SettingUtil.getLocale(locale);
		// 2. 메시지 번역 
		String termValue = MessageUtil.translate(domainId, locale, termKey);
		// 3. 찾는 값이 없다면 defaultValue를 리턴, defaultValue가 없다면 msgCode를 리턴
		termValue = ValueUtil.checkValue(termValue, ValueUtil.checkValue(defaultValue, termKey));
		
		// 4. 파라미터가 없다면 값 리턴 
		if(ValueUtil.isEmpty(params)) {
			return termValue;
		
		// 5. 파라미터가 있다면 번역해서 값 리턴 
		} else {
			Object[] paramStrs = StringUtils.toStringArray(params);
			return MessageFormat.format(termValue, paramStrs);
		}
	}
}