package hx.apigate.authorization.shiro;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import hx.apigate.authorization.shiro.databridge.JwtAccount;
import hx.apigate.authorization.shiro.databridge.JwtToken;
import hx.apigate.util.CacheUtil;
import hx.apigate.util.JsonWebTokenUtil;

import java.util.Map;
import java.util.Set;

/**
 * @author tomsun28
 * @date 18:07 2018/3/3
 */
public class JwtRealm extends AuthorizingRealm {
	/**
     * jwt签发者标志
     */
    private static final String ISSUER  = "UIOTCP_BOOTSHIRO_PRO";
    /**
     * JWT-SESSION缓存前缀
     */
    private static final String JWT_SESSION  = "JWT-SESSION:";
    
    @Override
    public Class<?> getAuthenticationTokenClass() {
        // 此realm只支持jwtToken
        return JwtToken.class;
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        Object jwtData =  principalCollection.getPrimaryPrincipal();
        // likely to be json, parse it:
        if (jwtData instanceof JwtAccount) {
        	
            Set<String> roles = JsonWebTokenUtil.split(((JwtAccount) jwtData).getRoles());
            Set<String> permissions = JsonWebTokenUtil.split(((JwtAccount) jwtData).getPerms());
            SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
            if(null!=roles&&!roles.isEmpty()) {
                info.setRoles(roles);
            }
            if(null!=permissions&&!permissions.isEmpty()) {
                info.setStringPermissions(permissions);
            }
            return info;
        }
        return null;
    }

    /**
     * 认证--解析jwt
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (!(authenticationToken instanceof JwtToken)) {
            return null;
        }
        JwtToken jwtToken = (JwtToken)authenticationToken;
        if(!CacheUtil.exist((new StringBuilder(JWT_SESSION).append(jwtToken.getUserId())).toString())) {
        	 throw new AuthenticationException("expiredJwt");
        }
        
        String jwt = (String)jwtToken.getCredentials();
        JwtAccount jwtAccount = null;
        try {
        	jwtAccount = JsonWebTokenUtil.parseJwt(jwt, JsonWebTokenUtil.SECRET_KEY);
		} catch(SignatureException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            // 令牌错误
            throw new AuthenticationException("errJwt");
        } catch(ExpiredJwtException e){
            // 令牌过期
            throw new AuthenticationException("令牌过期，请重新登录！");
        } catch(Exception e){
            throw new AuthenticationException("errJwt");
        }
        if(null == jwtAccount  || !ISSUER.equals(jwtAccount.getIssuer())){//校验签发者是否一致
            //令牌无效
            throw new AuthenticationException("errJwt");
        }
        return new SimpleAuthenticationInfo(jwtAccount,jwt,this.getName());
    }
    
    public static void main(String[] args) {
    	JwtAccount jwt = JsonWebTokenUtil.parseJwt("eyJhbGciOiJIUzUxMiIsInppcCI6IkRFRiJ9.eNqqVsoqyVSyUjIzN7Y0TEtM07U0S0vTNTFLTda1TDEz17UwMUkzBcIkMyMjJR2l4tIkoOKc5EpzQ0NDID-zuBjID_X0D3EOiHfy9w8J9vAM8o8PCPIHSSaWKFkZmppbGJgYmptb6iilVhRABUwtwQJF-TmpIANAdHxpcWqRUi0AAAD__w.PaMjlm4JYsUsNxqWHd0H3JISG66BmppRH9GhKauA9phIBiEBcMxecII6ruyN4uK6LK_fb8ZM7m_ivLNtA73Yog", JsonWebTokenUtil.SECRET_KEY);
    	System.out.println(jwt.toString());
	}
}
