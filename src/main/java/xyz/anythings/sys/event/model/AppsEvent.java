package xyz.anythings.sys.event.model;

/**
 * 어플리케이션 상태 관련 Event 
 * use case : 1. communication 모델에 init event 전달 
 *               ( 현재는 rabbitmq 만 .. )  
 * @author yang
 *
 */
public class AppsEvent extends AnyEvent {
	
	/**
	 *  어플리케이션 상태 
	 *  1. started
	 */
	protected String appsStatus;

	public String getAppsStatus() {
		return appsStatus;
	}

	public void setAppsStatus(String appsStatus) {
		this.appsStatus = appsStatus;
	}
}
