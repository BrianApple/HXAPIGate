package com.usthe.bootshiro.controller.apigateinner;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.domain.bo.AuthRole;
import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.ReqWebData;
import com.usthe.bootshiro.domain.vo.RetData;
import com.usthe.bootshiro.service.ResourceService;
import com.usthe.bootshiro.service.RoleService;
import com.usthe.bootshiro.service.UserService;
import com.usthe.bootshiro.shiro.filter.ShiroFilterChainManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权相关
 * 
 * @Description:
 * @author yangcheng
 * @date: 2020年4月23日
 */
@RequestMapping("/inner/role")
@RestController
public class InnerAuthController {
	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ShiroFilterChainManager shiroFilterChainManager;

	/**
	 * 初始化角色列表
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initRole")
	@ResponseBody
	public RetData deleteApiResource(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String apiId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
//			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/" + index + "/" + size, headers, null);
			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthRole> roles = roleService.getRoleList();
			PageInfo pageInfo = new PageInfo(roles);
			//兼容性处理
			return new RetData(200, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 获取角色(roleId)所被授权的API资源
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/getRestApiExtendByRoleId")
	@ResponseBody
	public RetData getRestApiExtendByRoleId(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();

			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthResource> authResources = resourceService.getAuthorityApisByRoleId(Integer.parseInt(roleId));
			PageInfo pageInfo = new PageInfo(authResources);
			//兼容性处理
			return new RetData(200, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 获取角色(roleId)未被授权的API资源
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/getRestApiByRoleId")
	@ResponseBody
	public RetData getRestApiByRoleId(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();


			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthResource> authResources = resourceService.getNotAuthorityApisByRoleId( Integer.valueOf(roleId));
			PageInfo pageInfo = new PageInfo(authResources);

			return new RetData(200, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 获取角色已关联的用户列表
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/getUserListByRoleId")
	@ResponseBody
	public RetData getUserListByRoleId(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();


			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthUser> users = userService.getUserListByRoleId( Integer.valueOf(roleId));
			users.forEach(user->user.setPassword(null));
			PageInfo pageInfo = new PageInfo(users);
			return new RetData(200, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 获取角色未关联的用户列表
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/getUserListExtendByRoleId")
	@ResponseBody
	public RetData getUserListExtendByRoleId(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();


			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthUser> users = userService.getNotAuthorityUserListByRoleId(Integer.valueOf(roleId));
			users.forEach(user -> user.setPassword(null));
			PageInfo pageInfo = new PageInfo(users);
			return new RetData(200, pageInfo);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 获取角色(roleId)所被授权的menu资源
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/getMenusByRoleId")
	@ResponseBody
	public RetData getMenusByRoleId(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();

			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthResource> authResources = resourceService.getAuthorityMenusByRoleId(Integer.valueOf(roleId));
			PageInfo pageInfo = new PageInfo(authResources);
			return new RetData(200, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 获取角色(roleId)未被授权的menu资源
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/getMenusExtendByRoleId")
	@ResponseBody
	public RetData getMenusExtendByRoleId(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();


			PageHelper.startPage(Integer.valueOf(index), Integer.valueOf(size));
			List<AuthResource> authResources = resourceService.getNotAuthorityMenusByRoleId(Integer.valueOf(roleId));
			PageInfo pageInfo = new PageInfo(authResources);
			return new RetData(200, pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 添加角色
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/addRole")
	@ResponseBody
	public RetData addRole(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数

			Map<String, String> data = reqArgs.getData();
			AuthRole role = new AuthRole();
			// 基本信息
			role.setName(data.remove("role_name"));
			role.setCode(data.remove("role_code"));
			role.setStatus(Short.parseShort(data.remove("role_state")));

			String jsonStr = JSON.toJSONString(role);

			boolean flag = roleService.addRole(role);
			if (flag) {
				return new RetData(200,"add role success");
			} else {
				return new RetData(500,"add role fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 根据角色id删除对应的角色
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/deleteRoleById")
	@ResponseBody
	public RetData deleteRoleById(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			String roleId = reqArgs.getStr();
			boolean flag = roleService.deleteRoleByRoleId(Integer.valueOf(roleId));
			if (flag) {
				return new RetData(200, "delete success");
			} else {
				return new RetData(200, "delete fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 根据角色id修改对应的角色
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/updateRoleById")
	@ResponseBody
	public RetData updateRoleById(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			Map<String, String> data = reqArgs.getData();
			AuthRole role = new AuthRole();
			// 基本信息
			role.setId(Integer.parseInt(reqArgs.getStr()));
			role.setName(data.remove("role_name"));
			role.setCode(data.remove("role_code"));
			role.setStatus(Short.parseShort(data.remove("role_state")));
			role.setUpdateTime(new Date());
//			String jsonStr = JSON.toJSONString(role);

			boolean flag = roleService.updateRole(role);
			if (flag) {
				return new RetData(200, "update success");
			} else {
				return new RetData(200, "update fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 根据roleid获取对应的角色信息
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initRoleByroleId")
	@ResponseBody
	public RetData initRoleByroleId(ReqWebData reqArgs) {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", "admin");
			map.put("authorization", reqArgs.getJwt());
			String roleId = reqArgs.getStr();

			AuthRole roleInfo=roleService.getRoleInfoById(Integer.valueOf(roleId));
//			Message  message = new Message().ok(200,"return apis success").addData("apiData",roleInfo);
			return new RetData(200, roleInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 给对应角色授权资源（菜单和api）
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/authorityRoleResource")
	@ResponseBody
	public RetData authorityRoleResource(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数

			Map<String, String> data = reqArgs.getData();
			String roleId = data.remove("roleId");
			String resourceId = data.remove("resourceId");
//			Map role=new HashMap<>();
//			role.put("roleId", roleId);
//			role.put("resourceId", resourceId);
//			String jsonStr = JSON.toJSONString(role);

			boolean flag = roleService.authorityRoleResource(Integer.valueOf(roleId),Integer.valueOf(resourceId));
			shiroFilterChainManager.reloadFilterChain(Integer.valueOf(resourceId));
//			Message  message =  flag ? new Message().ok(200,"authority success")
//						: new Message().error(500,"authority error");
			if(flag){
				return new RetData(200,"authority success");
			}else {
				return new RetData(500,"authority error");
			}

			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 对应角色删除授权资源（菜单和api）
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/deleteAuthorityRoleResource")
	@ResponseBody
	public RetData deleteAuthorityRoleResource(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			Map<String, String> data = reqArgs.getData();
			// 基本信息
			String roleId = data.remove("roleId");
			String resourceId = data.remove("resourceId");

			boolean flag = roleService.deleteAuthorityRoleResource(Integer.valueOf(roleId),Integer.valueOf(resourceId));
			shiroFilterChainManager.reloadFilterChain(Integer.valueOf(resourceId));
			if(flag){
				return new RetData(200,"delete success");
			}else {
				return new RetData(500,"delete error");
			}
//			HttpClientResult ret = HttpClientUtil
//					.doDelete(paramUrl + "/role/authority/resource/" + roleId + "/" + resourceId, headers, null);
//			if (ret.getCode() == 200) {
//				return new RetData(200, JSON.parse(ret.getContent()));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}
	
	/**
	 * 给对应角色授权用户
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/authorityRoleUser")
	@ResponseBody
	public RetData authorityRoleUser(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数

			Map<String, String> data = reqArgs.getData();
			String roleId = data.remove("roleId");
			String resourceId = data.remove("resourceId");
//			Map role=new HashMap<>();
//			role.put("roleId", roleId);
//			role.put("resourceId", resourceId);
//			String jsonStr = JSON.toJSONString(role);


			boolean flag = roleService.authorityRoleUser(Integer.valueOf(roleId),resourceId);
			if(flag){
				return new RetData(200,"authority success");
			}else {
				return new RetData(500,"authority error");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 删除角色的授权用户
	 * 
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/deleteAuthorityRoleUser")
	@ResponseBody
	public RetData deleteAuthorityRoleUser(ReqWebData reqArgs) {
		try {
			// header，固定格式
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			// 传送到bootshiro的参数--需要map 因此多转一步
			Map<String, String> data = reqArgs.getData();
			// 基本信息
			String roleId = data.remove("roleId");
			String resourceId = data.remove("resourceId");

			boolean flag = roleService.deleteAuthorityRoleUser(Integer.valueOf(roleId),resourceId);
			if(flag){
				return new RetData(200,"delete success");
			}else {
				return new RetData(500,"delete error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

}
