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
	 * 인쇄 템플릿
	 */
	private String printTemplate;
	/**
	 * 인쇄 템플릿을 실행시킬 파라미터
	 */
	private Map<String, Object> templateParams;
	
	/**
	 * 기본 생성자
	 */
	public PrintEvent() {
	}
	
	/**
	 * 생성자
	 * 
	 * @param domainId
	 * @param printerId
	 * @param printTemplate
	 * @param templateParams
	 */
	public PrintEvent(Long domainId, String printerId, String printTemplate, Map<String, Object> templateParams) {
		this.domainId = domainId;
		this.printTemplate = printTemplate;
		this.printerId = printerId;
		this.templateParams = templateParams;
	}
	
	public String getPrintTemplate() {
		return printTemplate;
	}
	
	public void setPrintTemplate(String printTemplate) {
		this.printTemplate = printTemplate;
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

}
