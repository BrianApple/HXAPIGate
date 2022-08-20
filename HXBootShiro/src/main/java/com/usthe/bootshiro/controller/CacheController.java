package com.usthe.bootshiro.controller;

import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.ignite.Constance;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.shiro.provider.ShiroFilterRulesProvider;
import com.usthe.bootshiro.shiro.rule.RolePermRule;
import hx.apigate.databridge.xmlBean.RouteAll;
import io.swagger.annotations.ApiOperation;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 仅限ignite缓存调试接口，不鉴权，对应路由请不要添加到数据库中
 * @Author yangcheng
 * @Date 2022/8/19
 */
@RestController
@RequestMapping("/cache")
public class CacheController {
    @Autowired
    private IgniteAutoConfig igniteAutoConfig;
    @Autowired
    private ShiroFilterRulesProvider shiroFilterRulesProvider;

    @ApiOperation(value = "获取路由缓存信息", httpMethod = "GET")
    @RequestMapping("/getAllRouteCache")
    public Message getAllRouteacheInfo() {
        Map<String, RouteAll> map = igniteAutoConfig.getAllRouteCache();

        return new Message().ok(200, "success").addData("data",map);
    }
    @ApiOperation(value = "获取api鉴权缓存信息", httpMethod = "GET")
    @RequestMapping("/getAPIAuthCache")
    public Message getAuthCacheInfo() {
        List<RolePermRule> rolePermRules = this.shiroFilterRulesProvider.loadRolePermRules();
        IgniteCache<String, String> apiAuthCache= igniteAutoConfig.getApiAuthCache();
        Map map = new HashMap();
        rolePermRules.forEach(rule -> {
            map.put(Constance.API_RESOURCE_ROLE+rule.getUrl(),apiAuthCache.get(Constance.API_RESOURCE_ROLE+rule.getUrl()));
        });

        return new Message().ok(200, "success").addData("data",map);
    }
}
