package com.usthe.bootshiro.shiro.provider.impl;

import com.usthe.bootshiro.dao.AuthResourceMapper;
import com.usthe.bootshiro.shiro.provider.ShiroFilterRulesProvider;
import com.usthe.bootshiro.shiro.rule.RolePermRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author tomsun28
 * @date 16:46 2018/3/7
 */
@Service("ShiroFilterRulesProvider")
public class ShiroFilterRulesProviderImpl implements ShiroFilterRulesProvider {

    @Autowired
    private AuthResourceMapper authResourceMapper;

    @Override
    public List<RolePermRule> loadRolePermRules() {
        /**
         * 从数据库中获取所有的角色和权限关系数据
         * 
         * 对应sql为
         *      select  CONCAT(re.URI, "==",UCASE(re.method)) as url,GROUP_CONCAT(rol.CODE ORDER BY re.URI) as needRoles
                from auth_resource re
                LEFT JOIN auth_role_resource ro on re.id = ro.RESOURCE_ID
                LEFT JOIN auth_role rol on ro.ROLE_ID = rol.ID
                where re.type = 2  
                GROUP BY
                re.URI,re.ID
               ---------------------------------------------------------------------------------------------------------- 
                re.type = 2 含义是：资源element(rest-api)
         */
        
        return authResourceMapper.selectRoleRules();
    }

}
