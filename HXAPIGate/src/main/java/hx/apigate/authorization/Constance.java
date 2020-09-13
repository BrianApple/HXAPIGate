package hx.apigate.authorization;
/**
 * <p>Description: </p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotp.com</p>
　 * @author yangcheng
　 * @date 2019年10月31日
　 * @version 1.0
 */
public class Constance {
	/**
	 * 授权所需要的http中header的key值
	 */
	public static final String USERID = "userId";
	public static final String AUTHORIZATION = "authorization";
	public static final String DEVICEINFO = "deviceInfo";
	public static final String LOGINREGISTER = "account";
	/**
     * JWT-SESSION缓存前缀
     */
    public static final String JWT_SESSION_PREFIX_KEY  = "JWT-SESSION:";
	
	public static final String API_RESOURCE_ROLE = "API:";
	
	public static final String API_RESOURCE_ROLE_SPLIT = "==";
}
