package xyz.elidom.sec.entity;

import java.util.Date;

import xyz.elidom.dbist.annotation.Column;
import xyz.elidom.dbist.annotation.ColumnType;
import xyz.elidom.dbist.annotation.GenerationRule;
import xyz.elidom.dbist.annotation.Index;
import xyz.elidom.dbist.annotation.PrimaryKey;
import xyz.elidom.dbist.annotation.Relation;
import xyz.elidom.dbist.annotation.Table;
import xyz.elidom.orm.OrmConstants;
import xyz.elidom.orm.entity.basic.ElidomStampHook;
import xyz.elidom.sys.entity.relation.DomainRef;
import xyz.elidom.sys.entity.relation.UserRef;

@Table(name = "login_histories", idStrategy = GenerationRule.UUID, uniqueFields = "id,domainId", indexes = {
		@Index(name = "ix_login_histories_0", columnList = "id,domain_id", unique = true), 
		@Index(name = "ix_login_histories_1", columnList = "access_user_id"),
		@Index(name = "ix_login_histories_2", columnList = "success"),
		@Index(name = "ix_login_histories_3", columnList = "created_at"),
		@Index(name = "ix_login_histories_4", columnList = "access_user_id, success"),
		@Index(name = "ix_login_histories_5", columnList = "access_user_id, created_at") })
public class LoginHistory extends ElidomStampHook {
	/**
	 * SerialVersion UID
	 */
	private static final long serialVersionUID = 443648104514167072L;

	@PrimaryKey
	@Column(name = "id", nullable = false, length = OrmConstants.FIELD_SIZE_UUID)
	private String id;
	
	@Relation(field = "domain_id")
	private DomainRef domain;

	@Column(name = "access_user_id", nullable = false, length = OrmConstants.FIELD_SIZE_UUID)
	private String accessUserId;

	@Relation(field = "access_user_id")
	private UserRef accessUser;

	@Column(name = "access_ip", length = OrmConstants.FIELD_SIZE_IP)
	private String accessIp;

	@Column(name = "logout_at", type = ColumnType.DATETIME)
	private Date logoutAt;

	@Column(name = "success", nullable = false)
	private Boolean success;

	@Column(name = "session_id", length = OrmConstants.FIELD_SIZE_UUID)
	private String sessionId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccessUserId() {
		return this.accessUserId;
	}

	public void setAccessUserId(String accessUserId) {
		this.accessUserId = accessUserId;
	}
	
	public DomainRef getDomain() {
		return this.domain;
	}
	
	public void setDomain(DomainRef domain) {
		this.domain = domain;
	}

	public UserRef getAccessUser() {
		return this.accessUser;
	}

	public void setAccessUser(UserRef accessUser) {
		this.accessUser = accessUser;

		if (this.accessUser != null) {
			String refId = this.accessUser.getId();
			if (refId != null)
				this.accessUserId = refId;
		}

		if (this.accessUserId == null) {
			this.accessUserId = "";
		}
	}

	public String getAccessIp() {
		return accessIp;
	}

	public void setAccessIp(String accessIp) {
		this.accessIp = accessIp;
	}

	public Date getLogoutAt() {
		return logoutAt;
	}

	public void setLogoutAt(Date logoutAt) {
		this.logoutAt = logoutAt;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
