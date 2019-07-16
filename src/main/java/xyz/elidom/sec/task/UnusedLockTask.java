package xyz.elidom.sec.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import xyz.anythings.sys.AnythingsSysConstants;
import xyz.anythings.sys.ConfigConstants;
import xyz.elidom.dbist.dml.Query;
import xyz.elidom.orm.IQueryManager;
import xyz.elidom.sec.SecConfigConstants;
import xyz.elidom.sys.entity.Domain;
import xyz.elidom.sys.entity.User;
import xyz.elidom.sys.system.context.DomainContext;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.sys.util.ValueUtil;

/**
 * 미사용 계정 잠금 배치.
 * 
 * @author Minu.Kim
 */
@Component
public class UnusedLockTask {
	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(UnusedLockTask.class);
	/**
	 * Query Manager
	 */
	@Autowired
	private IQueryManager queryManager;
	/**
	 * 이중화 서버의 양쪽에서 모두 처리되지 않게 한 쪽 서버에서 실행되도록 설정으로 처리하기 위함
	 * application.properties 설정 - mps.job.scheduler.enable=true/false 설정 필요 (이중화 서버 한 대는 true, 나머지 서버는 false로 설정, 한 대만 운영시 true로 설정)
	 */
	@Autowired
	private Environment env;	
	
	/**
	 * 서버의 Job Scheduler가 활성화 되었는지 여부
	 * 
	 * @return
	 */
	private boolean isJobEnabeld() {
		return ValueUtil.toBoolean(this.env.getProperty(ConfigConstants.JOB_SCHEDULER_ENABLED, AnythingsSysConstants.FALSE_STRING)); 
	}

	/**
	 * 매일 밤 자정에 사용하지 않는 계정 잠금 배치
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void executeTask() {
		// 수정1. Job 스케줄러 활성화 여부 체크
		if(!this.isJobEnabeld()) {
			return;
		}
		
		// 수정1. 시스템 도메인으로 명시적으로 설정값 조회
		Domain systemDomain = Domain.systemDomain();
		DomainContext.setCurrentDomain(systemDomain);
		
		try {
			this.lockUnusedAccount(systemDomain);
		} catch (Exception e) {
			this.logger.error("Failed to lock unused account!", e);
		} finally {
			DomainContext.unsetAll();
		}
	}
	
	/**
	 * 장기 미 사용 계정에 대해서 잠금 실행
	 *  
	 * @param domain
	 */
	public void lockUnusedAccount(Domain domain) {
		int unusedDay = ValueUtil.toInteger(SettingUtil.getValue(domain.getId(), SecConfigConstants.USER_ACCOUNT_LOCK_UNUSED_DAY), 0);
		if (unusedDay < 1) {
			return;
		}

		Query condition = new Query();
		condition.addFilter("activeFlag", true);
		List<User> list = this.queryManager.selectList(User.class, condition);
		
		if (ValueUtil.isEmpty(list)) {
			return;
		}

		List<User> lockUserList = new ArrayList<User>();
		for (User user : list) {
			// 로그인 시간이 존재하지 않을 경우, 계정 생성 시간으로 확인.
			Date lastSignInAt = ValueUtil.checkValue(user.getLastSignInAt(), user.getCreatedAt());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastSignInAt);
			calendar.add(Calendar.DAY_OF_MONTH, unusedDay);

			boolean isLocked = new Date().compareTo(calendar.getTime()) > 0;
			if (isLocked) {
				user.setActiveFlag(false);
				lockUserList.add(user);
			}
		}

		if (ValueUtil.isNotEmpty(lockUserList)) {
			this.queryManager.updateBatch(lockUserList);
		}
	}
}