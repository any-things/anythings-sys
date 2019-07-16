/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.anythings.sys.web.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import xyz.anythings.sys.config.ModuleProperties;
import xyz.elidom.sys.config.ModuleConfigSet;
import xyz.elidom.sys.system.service.api.IEntityFieldCache;
import xyz.elidom.sys.system.service.api.IServiceFinder;

/**
 * Anythings Sys 모듈 Startup시 Framework 초기화 클래스
 * 
 * @author shortstop
 */
@Component
public class AnythingsSysInitializer {

	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(AnythingsSysInitializer.class);

	@Autowired
	@Qualifier("rest")
	private IServiceFinder restFinder;

	@Autowired
	private IEntityFieldCache entityFieldCache;

	@Autowired
	private ModuleProperties module;

	@Autowired
	private ModuleConfigSet configSet;
	
	@EventListener({ ContextRefreshedEvent.class })
	public void ready(ContextRefreshedEvent event) {
		this.logger.info("Anythings Sys module initializing ready...");
		this.configSet.addConfig(this.module.getName(), this.module);
		this.configSet.setApplicationModule(this.module.getName());
		this.scanServices();
	}

	@EventListener({ ApplicationReadyEvent.class })
	void contextRefreshedEvent(ApplicationReadyEvent event) {
		this.logger.info("Anythings Sys module initializing started...");
		this.logger.info("Anythings Sys initializing finished");
	}

	/**
	 * 모듈 서비스 스캔
	 */
	private void scanServices() {
		this.entityFieldCache.scanEntityFieldsByBasePackage(this.module.getBasePackage());
		this.restFinder.scanServicesByPackage(this.module.getName(), this.module.getBasePackage());
	}
}