package IOTGateConsole.controller.apigate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import IOTGateConsole.controller.apigate.bean.AuthResource;
import IOTGateConsole.controller.apigate.bean.AuthRole;
import IOTGateConsole.databridge.ReqWebData;
import IOTGateConsole.databridge.RetData;

/**
 * 授权
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年8月16日
 * @version 1.0
 */
@RequestMapping("/role")
@RestController
public class AuthController {
	@Value("${server.paramUrl}")
	private String paramUrl;

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
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/" + index + "/" + size, headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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

			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/api/" + roleId + "/" + index + "/" + size,
					headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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

			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/api/-/" + roleId + "/" + index + "/" + size,
					headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/user/" + roleId + "/" + index + "/" + size,
					headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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

			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/user/-/" + roleId + "/" + index + "/" + size,
					headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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

			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/menu/" + roleId + "/" + index + "/" + size,
					headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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

			HttpClientResult ret = HttpClientUtil.doGet(paramUrl + "/role/menu/-/" + roleId + "/" + index + "/" + size,
					headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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

			HttpClientResult ret = HttpClientUtil.doPostJson(paramUrl + "/role", headers, jsonStr, 60000);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 根据id删除对应的角色
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

			HttpClientResult ret = HttpClientUtil.doDelete(paramUrl + "/role/" + roleId, headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 根据id修改对应的角色
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
			String jsonStr = JSON.toJSONString(role);

			HttpClientResult ret = HttpClientUtil.doPutJson(paramUrl + "/role", headers, jsonStr);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 根据roleid获取对应的角色信息
	 * 
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
			String url = paramUrl + "/role/" + roleId;
			HttpClientResult ret = HttpClientUtil.doGet(url, map, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
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
			Map role=new HashMap<>();
			role.put("roleId", roleId);
			role.put("resourceId", resourceId);
			String jsonStr = JSON.toJSONString(role);
			  HttpClientResult ret =HttpClientUtil.doPostJson(paramUrl+"/role/authority/resource",headers,jsonStr,60000); 
			  if(ret.getCode() == 200) { return new
			  RetData(200,JSON.parse(ret.getContent())); }
			 
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
			HttpClientResult ret = HttpClientUtil
					.doDelete(paramUrl + "/role/authority/resource/" + roleId + "/" + resourceId, headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}
	
	/**
	 * 给对应角色授权资源（用户）
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
			Map role=new HashMap<>();
			role.put("roleId", roleId);
			role.put("resourceId", resourceId);
			String jsonStr = JSON.toJSONString(role);
			  HttpClientResult ret =HttpClientUtil.doPostJson(paramUrl+"/role/authority/user",headers,jsonStr,60000); 
			  if(ret.getCode() == 200) { return new
			  RetData(200,JSON.parse(ret.getContent())); }
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

	/**
	 * 对应角色删除授权资源（用户）
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
			HttpClientResult ret = HttpClientUtil
					.doDelete(paramUrl + "/role/authority/user/" + roleId + "/" + resourceId, headers, null);
			if (ret.getCode() == 200) {
				return new RetData(200, JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}

}
