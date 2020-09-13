package com.usthe.bootshiro.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.domain.vo.MenuTreeNode;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.ignite.Constance;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.service.ResourceService;
import com.usthe.bootshiro.shiro.filter.ShiroFilterChainManager;
import com.usthe.bootshiro.util.TreeUtil;
import io.swagger.annotations.ApiOperation;

import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *   资源URL管理
 * @author tomsun28
 * @date 21:36 2018/3/17
 */
@RestController
@RequestMapping("/resource")
public class ResourceController extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private ShiroFilterChainManager shiroFilterChainManager;
    
    @Autowired
    IgniteAutoConfig igniteAutoConfig;

    @ApiOperation(value = "获取用户被授权菜单",notes = "通过uid获取对应用户被授权的菜单列表,获取完整菜单树形结构")
    @GetMapping("authorityMenu")
    public Message getAuthorityMenu(HttpServletRequest request) {
        String uid = request.getHeader("userId");
        List<MenuTreeNode> treeNodes = new ArrayList<>();
        List<AuthResource> resources = resourceService.getAuthorityMenusByUid(uid);

        for (AuthResource resource : resources) {
            MenuTreeNode node = new MenuTreeNode();
            BeanUtils.copyProperties(resource,node);
            treeNodes.add(node);
        }
        List<MenuTreeNode> menuTreeNodes = TreeUtil.buildTreeBy2Loop(treeNodes,-1);
        return new Message().ok(6666,"return menu list success").addData("menuTree",menuTreeNodes);
    }

    @ApiOperation(value = "获取全部菜单列", httpMethod = "GET")
    @GetMapping("menus")
    public Message getMenus() {

        List<MenuTreeNode> treeNodes = new ArrayList<>();
        List<AuthResource> resources = resourceService.getMenus();

        for (AuthResource resource: resources) {
            MenuTreeNode node = new MenuTreeNode();
            BeanUtils.copyProperties(resource,node);
            treeNodes.add(node);
        }
        List<MenuTreeNode> menuTreeNodes = TreeUtil.buildTreeBy2Loop(treeNodes,-1);
        return new Message().ok(6666,"return menus success").addData("menuTree",menuTreeNodes);
    }

    @ApiOperation(value = "增加菜单",httpMethod = "POST")
    @PostMapping("menu")
    public Message addMenu(@RequestBody AuthResource menu ) {

        Boolean flag = resourceService.addMenu(menu);
        if (flag) {
            return new Message().ok(6666,"add menu success");
        } else {
            return new Message().error(1111,"add menu fail");
        }
    }

    @ApiOperation(value = "修改菜单",httpMethod = "PUT")
    @PutMapping("menu")
    public Message updateMenu(@RequestBody AuthResource menu) {

        Boolean flag = resourceService.modifyMenu(menu);
        if (flag) {
            return new Message().ok(6666,"update menu success");
        } else {
            return new Message().error(1111, "update menu fail");
        }
    }

    @ApiOperation(value = "删除菜单", notes = "根据菜单ID删除菜单", httpMethod = "DELETE")
    @DeleteMapping("menu/{menuId}")
    public Message deleteMenuByMenuId(@PathVariable Integer menuId) {

        Boolean flag = resourceService.deleteMenuByMenuId(menuId);
        if (flag) {
            return new Message().ok(6666, "delete menu success");
        } else {
            return new Message().error(1111, "delete menu fail");
        }
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "获取API list", notes = "需要分页,根据teamId判断,-1->获取api分类,0->获取全部api,id->获取对应分类id下的api",httpMethod = "GET")
    @GetMapping("api/{teamId}/{currentPage}/{pageSize}")
    public Message getApiList(@PathVariable Integer teamId, @PathVariable Integer currentPage, @PathVariable Integer pageSize) {

        List<AuthResource> resources = null;
        if (teamId == -1) {
            // -1 为获取api分类
            resources = resourceService.getApiTeamList();
            return new Message().ok(6666,"return apis success").addData("data",resources);
        }
        PageHelper.startPage(currentPage, pageSize);
        if (teamId == 0) {
            // 0 为获取全部api
            resources = resourceService.getApiList();
        } else {
            // 其他查询teamId 对应分类下的apis
            resources = resourceService.getApiListByTeamId(teamId);
        }
        PageInfo pageInfo = new PageInfo(resources);
        return new Message().ok(6666,"return apis success").addData("data",pageInfo);
    }
    /**
     * 根据id获取对应的api信息
     * @param teamId
     * @return
     */
    @SuppressWarnings("unchecked")
    @ApiOperation(value = "获取API list", notes = "需要分页,根据teamId判断,-1->获取api分类,0->获取全部api,id->获取对应分类id下的api",httpMethod = "GET")
    @GetMapping("api/{apiId}")
    public Message getApiList(@PathVariable Integer apiId) {
    	
    	AuthResource resource = null;
		// 其他查询teamId 对应分类下的apis
		resource = resourceService.getApiInfoByapiId(apiId);
    	
    	return new Message().ok(6666,"return apis success").addData("apiData",resource);
    }

    @ApiOperation(value = "增加API",httpMethod = "POST")
    @PostMapping("api")
    public Message addApi(@RequestBody AuthResource api ) {
    	
    	String uri = api.getUri();
    	String routeInfo = api.getRouteInfo();
        Boolean flag = resourceService.addMenu(api);
        if (flag) {
        	igniteAutoConfig.addApiInfo(true,uri,api.getMethod() ,routeInfo);
            return new Message().ok(6666,"add api success");
        } else {
            return new Message().error(1111,"add api fail");
        }
    }
    /**
     * 修改和删除API时，同样需要重新加载过授权滤器链，即执行shiroFilterChainManager.reloadFilterChain();---yangcheng
     * @param api
     * @return
     */
    @ApiOperation(value = "修改API",httpMethod = "PUT")
    @PutMapping("api")
    public Message updateApi(@RequestBody AuthResource api) {
    	String uri = api.getUri();
    	String routeInfo = api.getRouteInfo();
        Boolean flag = resourceService.modifyMenu(api);
        if (flag) {
        	igniteAutoConfig.addApiInfo(false,uri,api.getMethod(), routeInfo);
            return new Message().ok(6666,"update api success");
        } else {
            return new Message().error(1111, "update api fail");
        }
    }

    @ApiOperation(value = "删除API", notes = "根据API_ID删除API", httpMethod = "DELETE")
    @DeleteMapping("api/{apiId}")
    public Message deleteApiByApiId(@PathVariable Integer apiId) {

    	AuthResource resource = resourceService.getApiInfoByapiId(apiId);//通过apiId查询到其对应的api的uri名称，用于删除缓存
    	String uri = resource.getUri();
    	String httpMethod = resource.getMethod();
    	Map routeInfo = (Map) JSON.parse(resource.getRouteInfo());
        Boolean flag = resourceService.deleteMenuByMenuId(apiId);
        if (flag) {
        	
        	//shiroFilterChainManager.reloadFilterChain(resource.getId());0
        	IgniteCache<String,String> apiAuthCache = igniteAutoConfig.getApiAuthCache();
    		apiAuthCache.remove(Constance.API_RESOURCE_ROLE+uri);
        	igniteAutoConfig.removeApiInfo(uri,httpMethod,(String)routeInfo.get("api_version"));
            return new Message().ok(6666, "delete api success");
        } else {
            return new Message().error(1111, "delete api fail");
        }
    }
    @ApiOperation(value = "判断jwt是否过期", notes = "能访问到当前接口并获取到返回值标识jwt合法，反之jwt不合法", httpMethod = "POST")
    @PostMapping("isjwtRight")
    public Message isjwtRight(HttpServletRequest request) {
    	return new Message().ok(200, "jwt is right");
    }

}
