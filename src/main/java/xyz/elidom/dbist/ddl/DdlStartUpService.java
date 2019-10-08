package xyz.elidom.dbist.ddl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import net.sf.common.util.ValueUtils;
import xyz.elidom.dbist.ddl.Ddl;
import xyz.elidom.dbist.ddl.InitialSetup;

@Service
public class DdlStartUpService {

	@Resource
	public Environment env;

	@Autowired(required = false)
	private Ddl ddl;

	@Autowired(required = false)
	private InitialSetup initialSetup;

	@EventListener({ ContextRefreshedEvent.class })
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void start() {
		// 1. Table Space Setup
		String dataTBSpace = ValueUtils.toString(env.getProperty("dbist.ddl.tablespace.data", ""));
		String idxTBSpace = ValueUtils.toString(env.getProperty("dbist.ddl.tablespace.idx", ""));
		this.ddl.setTableSpace(dataTBSpace, idxTBSpace);

		// 2. initial data setup
		if (initialSetup != null)
			this.initialSetup.initialSetup(this.env);
	}
}