package xyz.anythings.sys.event.model;

import java.util.Map;

/**
 * 인쇄 요청 이벤트
 * 
 * @author shortstop
 */
public class PrintEvent extends SysEvent {
	/**
	 * 프린터 ID
	 */
	private String printerId;
	/**
	 * 프린터 유형 : barcode (바코드 인쇄), normal (일반 프린트)
	 */
	private String printType;
	/**
	 * 인쇄 템플릿
	 */
	private String printTemplate;
	/**
	 * 인쇄 템플릿을 실행시킬 파라미터
	 */
	private Map<String, Object> templateParams;
	/**
	 * 동기 / 비동기 처리 모드 
	 */
	private boolean isSyncMode = false;
	
	/**
	 * 기본 생성자 0
	 */
	public PrintEvent() {
	}
	
	/**
	 * 생성자 1
	 * 
	 * @param domainId 도메인 ID
	 * @param printerId 프린터 ID
	 * @param printTemplate 인쇄 템플릿 (커스텀 템플릿) 명
	 * @param templateParams 템플릿 엔진에 태울 인쇄 템플릿 파라미터
	 */
	public PrintEvent(Long domainId, String printerId, String printTemplate, Map<String, Object> templateParams) {
		this.domainId = domainId;
		this.printTemplate = printTemplate;
		this.printerId = printerId;
		this.templateParams = templateParams;
	}
	
	/**
	 * 생성자 2
	 * 
	 * @param domainId 도메인 ID
	 * @param printType 인쇄 유형 
	 * @param printerId 프린터 ID
	 * @param printTemplate 인쇄 템플릿 (커스텀 템플릿) 명
	 * @param templateParams 템플릿 엔진에 태울 인쇄 템플릿 파라미터 
	 * @param isSyncMode 동기 처리할 지 비동기 처리할 지 여부
	 */
	public PrintEvent(Long domainId, String printType, String printerId, String printTemplate, Map<String, Object> templateParams, boolean isSyncMode) {
		this.domainId = domainId;
		this.printType = printType;
		this.printTemplate = printTemplate;
		this.printerId = printerId;
		this.templateParams = templateParams;
		this.isSyncMode = isSyncMode;
	}
	
	public String getPrintTemplate() {
		return printTemplate;
	}
	
	public void setPrintTemplate(String printTemplate) {
		this.printTemplate = printTemplate;
	}
	
	public String getPrintType() {
		return printType;
	}

	public void setPrintType(String printType) {
		this.printType = printType;
	}

	public String getPrinterId() {
		return printerId;
	}
	
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}
		
	public Map<String, Object> getTemplateParams() {
		return templateParams;
	}
	
	public void setTemplateParams(Map<String, Object> templateParams) {
		this.templateParams = templateParams;
	}

	public boolean isSyncMode() {
		return isSyncMode;
	}

	public void setSyncMode(boolean isSyncMode) {
		this.isSyncMode = isSyncMode;
	}

}
