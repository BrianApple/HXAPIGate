package com.usthe.bootshiro.shiro.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

/**
 * @author tomsun28
 * @date 18:00 2018/3/3
 */
@Component
public class PasswordMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {

        return authenticationToken.getPrincipal().toString().equals(authenticationInfo.getPrincipals().getPrimaryPrincipal().toString())
                && authenticationToken.getCredentials().toString().equals(authenticationInfo.getCredentials().toString());
    }
}
