package xyz.elidom.dbist.annotation;

/**
 * 데이터 베이스 링크 타입
 * 엔티티에서 관리 되는 linkType 속성과 맵핑 됨 
 * @author yang
 *
 */
public class DataSourceLinkType {
	/**
	 * 기본 datasource 사용 
	 */
	public static final String SELF = "_self_";

	/**
	 * db-link 사용 
	 * annotation linkName 과 동시 사용 
	 */
	public static final String DB_LINK = "db-link";
	
	/**
	 * 시스템에서 관리 하는 별도의 datasource 사용   
	 * annotation linkName 과 동시 사용 
	 */
	public static final String DATASOURCE = "mng-source";
}
