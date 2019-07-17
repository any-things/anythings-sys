package xyz.anythings.sys.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 이벤트 Publisher
 * 
 * @author shortstop
 */
@Component
public class EventPublisher {

	/**
	 * Event Publisher
	 */
	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	/**
	 * 이벤트 Publish
	 * 
	 * @param event
	 */
	public void publishEvent(Object event) {
		this.eventPublisher.publishEvent(event);
	}
}
