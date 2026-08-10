package com.usthe.bootshiro.controller;

import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.redis.ApiAuthCacheService;
import com.usthe.bootshiro.redis.RedisConstance;
import com.usthe.bootshiro.redis.RouteCacheService;
import com.usthe.bootshiro.shiro.provider.ShiroFilterRulesProvider;
import com.usthe.bootshiro.shiro.rule.RolePermRule;
import hx.apigate.databridge.xmlBean.RouteAll;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 仅限redis缓存调试接口，不鉴权，对应路由请不要添加到数据库中
 * @Author yangcheng
 * @Date 2022/8/19
 */
@RestController
@RequestMapping("/cache")
public class CacheController {
    @Autowired
    private RouteCacheService routeCacheService;
    @Autowired
    private ApiAuthCacheService apiAuthCacheService;
    @Autowired
    private ShiroFilterRulesProvider shiroFilterRulesProvider;

    @Operation(summary = "获取路由缓存信息", method = "GET")
    @RequestMapping("/getAllRouteCache")
    public Message getAllRouteacheInfo() {
        Map<String, RouteAll> map = routeCacheService.getAllRouteCache();

        return new Message().ok(200, "success").addData("data",map);
    }
    @Operation(summary = "获取api鉴权缓存信息", method = "GET")
    @RequestMapping("/getAPIAuthCache")
    public Message getAuthCacheInfo() {
        List<RolePermRule> rolePermRules = this.shiroFilterRulesProvider.loadRolePermRules();
        Map map = new HashMap();
        rolePermRules.forEach(rule -> {
            map.put(RedisConstance.API_RESOURCE_ROLE+rule.getUrl(),apiAuthCacheService.get(RedisConstance.API_RESOURCE_ROLE+rule.getUrl()));
        });

        return new Message().ok(200, "success").addData("data",map);
    }
}
