package xyz.anythings.sys.process.api;

/**
 * 최소 기능 단위를 정의
 *  
 * @author shortstop
 */
public interface IFunc {
	
	/**
	 * 함수 이름 조회
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 함수 이름을 리턴
	 * 
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * 함수 설명을 리턴
	 * 
	 * @return
	 */
	public String getDescription();
	
	/**
	 * 함수 설명을 설정
	 * 
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * 함수 유형 - 자바, 쿼리, 프로시져, 메시징 ... 
	 * 
	 * @return
	 */
	public String getFuncType();
	
	/**
	 * 함수 유형을 설정
	 * 
	 * @param funcType
	 * @return
	 */
	public String setFuncType(String funcType);
	
	/**
	 * 실행시 컨텍스트(IExecutionContext)에서 꺼내서 사용할 파라미터 키 설정
	 * 
	 * @param paramKeys
	 */
	public void setParamKeys(String[] paramKeys);
	
	/**
	 * 실행시 컨텍스트(IExecutionContext)에서 꺼내서 사용할 파라미터 키 리턴
	 * 
	 * @return
	 */
	public String[] getParamKeys();
	
	/**
	 * 실행 컨텍스트(IExecutionContext)에 결과 추가시 키로 사용할 이름을 설정 
	 * 
	 * @param resultKey
	 */
	public void setResultKeys(String[] resultKeys);
	
	/**
	 * 실행 컨텍스트(IExecutionContext)에 결과 추가시 키로 사용할 이름을 리턴 
	 * 
	 * @return
	 */
	public String[] getResultKeys();

	/**
	 * 단위 기능 실행
	 * 
	 * @param context
	 */
	public void execute(IExecutionContext context);
}
