package com.usthe.bootshiro.controller.apigateinner;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthResource;
import com.usthe.bootshiro.domain.vo.ReqWebData;
import com.usthe.bootshiro.domain.vo.RetData;
import com.usthe.bootshiro.ignite.Constance;
import com.usthe.bootshiro.ignite.IgniteAutoConfig;
import com.usthe.bootshiro.service.ResourceService;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/inner/api")
public class InnerApiController {
	@Autowired
	private ResourceService resourceService;
	@Autowired
	IgniteAutoConfig igniteAutoConfig;

	/**
	 * 校验jwt是否已过期--放在这里的目的是为了走shiro鉴权，user默认走password
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/islogin")
	@ResponseBody
	public RetData islogin(ReqWebData reqArgs) {
		Map<String,String> map = new HashMap<String, String>();
		if(reqArgs.getJwt() != null && !"".equals(reqArgs.getJwt())){

			return new RetData(200);
		}else {
			return new RetData(500);
		}
	}
	/**
	 * 获取所有的api类型
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initApiType")
	@ResponseBody
    public RetData getApiType(ReqWebData reqArgs) {
		try {
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			
			//根据teamId判断,-1->获取api分类,0->获取全部api,id->获取对应分类id下的api
//			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/resource/api/-1/1/60",map,null);
			int parentId = "true".equals(reqArgs.getStr()) ? -1  : 0;
			AuthResource resource=new AuthResource();
			resource.setParentId(parentId);
			List<AuthResource> resources  = resourceService.getApiTeamList(resource);
			return new RetData(200,resources);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	
	
	/**
	 * 获取所有的api
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initApiList")
	@ResponseBody
    public RetData getApiList( ReqWebData reqArgs) {
		try {

			String currentPage = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			PageHelper.startPage(Integer.valueOf(currentPage), Integer.valueOf(size));
			List<AuthResource> resources  = resourceService.getApiList();
			PageInfo pageInfo = new PageInfo(resources);
			return new RetData(200,pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	/**
	 * 根据Itemid(APi分类)获取分类下的所有api
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initApiByItemId")
	@ResponseBody
	public RetData getApiListByItemId(ReqWebData reqArgs) {
		try {
			Map<String,String> map = new HashMap<String, String>();
			map.put("userId", "admin");
			map.put("authorization", reqArgs.getJwt());
			int teamId = Integer.valueOf(reqArgs.getStr());//接口分类id
			String currentPage = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			PageHelper.startPage(Integer.valueOf(currentPage), Integer.valueOf(size));
			List<AuthResource> resources  = null;
			// 其他查询teamId 对应分类下的apis
			if(-1 == teamId){
				//获取所有API
				resources = resourceService.getApiList();
			}else {
				resources = resourceService.getApiListByTeamId(teamId);
			}
			PageInfo pageInfo = new PageInfo(resources);
			return new RetData(200,pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}

	/**
	 * 根据Itemid(APi分类)获取分类下未被角色关联过的api集合
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initApiByItemIdAndRID")
	@ResponseBody
	public RetData getApiListByItemIdNotRelatedByRID(ReqWebData reqArgs) {
		try {
			Map<String,String> map = new HashMap<String, String>();
			map.put("userId", "admin");
			map.put("authorization", reqArgs.getJwt());
			String id = reqArgs.getStr();//接口分类id
			String ids[] = id.split("=");
			int itmId = Integer.valueOf(ids[0]);
			int RId = Integer.valueOf(ids[1]);
			String currentPage = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			PageHelper.startPage(Integer.valueOf(currentPage), Integer.valueOf(size));
			List<AuthResource> resources  = null;
			// 其他查询teamId 对应分类下的apis
			if(-1 == itmId){
				//获取所有API
				resources = resourceService.getApiListNotRelatedByRID(RId);
			}else {
				resources = resourceService.getApiListByTeamIdAndRID(itmId,RId);
			}
			PageInfo pageInfo = new PageInfo(resources);
			return new RetData(200,pageInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new RetData(500);
	}
	
	/**
	 * 根据APIid获取对应的api
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initApiByApiId")
	@ResponseBody
	public RetData initApiByApiId(ReqWebData reqArgs) {
		try {
			Map<String,String> map = new HashMap<String, String>();
			map.put("userId", "admin");
			map.put("authorization", reqArgs.getJwt());
			int ApiID = Integer.valueOf(reqArgs.getStr());
			AuthResource resource = null;
			// 其他查询teamId 对应分类下的apis
			resource = resourceService.getApiInfoByapiId(ApiID);
//			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/resource/api/"+reqArgs.getStr(),map,null);
			Map temData = JSON.parseObject(JSON.toJSONString(resource));//按照前端页面需求改造
			if(temData.containsKey("routeInfo")) {
				//如果存在路由信息
				String routInfoStr = (String) temData.remove("routeInfo");
				if(routInfoStr != null && !"".equals(routInfoStr)) {
					Map routInfoMap = (Map) JSON.parse(routInfoStr);
					if("http".equals(routInfoMap.get("pType"))) {
						int routeNum = (routInfoMap.size() - 6) /5 ;
						routInfoMap.put("routeNum", routeNum);//后台辅助计算route的数量
					}else if("dubbo".equals(routInfoMap.get("pType"))) {
						int routeNum = (routInfoMap.size() - 6 ) /2 ;
						routInfoMap.put("routeNum", routeNum);
					}



					temData.put("routeInfo", routInfoMap);
				}
			}
				return new RetData(200,temData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	/**
	 * 增加资源，添加资源类型 和具体资源时，都是通过该结构执行的
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/addApiResource")
	@ResponseBody
	public RetData addApiResource(ReqWebData reqArgs) {
		try {
			//传送到bootshiro的参数
			
			Map<String,String> data = reqArgs.getData();
			AuthResource resource = new AuthResource();
			//基本信息
			resource.setName(data.remove("interface_name"));
			resource.setUri(data.remove("url_val"));
			resource.setVersion(data.get("api_version"));//API版本在路由信息中也保存一份
			int parentID = Integer.parseInt(data.remove("api_Type") );
			resource.setParentId(parentID);

			short sourceType = Short.parseShort((data.get("sour_Type")) == null ? "0" :data.remove("sour_Type"));
			resource.setType(sourceType);
			resource.setMethod(data.remove("request_method"));
			resource.setStatus(Short.parseShort(data.remove("state")));
			//路由信息
			resource.setNeedAuth(Integer.parseInt(data.get("isAuth") == null ? "0" : data.get("isAuth") ));//是否鉴权不能移除，因为json中也需要
			String routeInfo =  JSON.toJSONString(data);
			resource.setRouteInfo(routeInfo);//路由信息全部保存在当前json中
			
			
			
			String jsonStr = JSON.toJSONString(resource);

			String uri = resource.getUri();
			String routeInfoJson = resource.getRouteInfo();
			Boolean flag = resourceService.addMenu(resource);//添加api信息
			if (flag) {
				if(3 != sourceType && 0 != sourceType){
					//只缓存APi信息，API分类不缓存
					igniteAutoConfig.addApiInfo(true,uri,resource.getMethod() ,routeInfoJson);
				}
				return new RetData(200,"add api success");
			} else {
				return new RetData(500,"add api fail");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	/**
	 * 根据id修改对应的api
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/updateApiResource")
	@ResponseBody
	public RetData updateApiResource(ReqWebData reqArgs) {
		try {
			//header，固定格式
			Map<String,String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			//传送到bootshiro的参数--需要map 因此多转一步
			Map<String,String> data = reqArgs.getData();
			AuthResource resource = new AuthResource();
			resource.setId(Integer.parseInt(reqArgs.getStr()));
			resource.setName(data.remove("interface_name"));
			resource.setUri(data.remove("url_val"));
			resource.setVersion(data.get("api_version"));//API版本在路由信息中也保存一份
			int parentID = Integer.parseInt(data.remove("api_Type") );
			resource.setParentId(parentID);

			short sourceType = Short.parseShort((data.get("sour_Type")) == null ? "0" :data.remove("sour_Type"));
			resource.setType(sourceType);
			resource.setMethod(data.remove("request_method"));
			resource.setStatus(Short.parseShort(data.remove("state")));
			//路由信息
			resource.setNeedAuth(Integer.parseInt(data.get("isAuth") == null ? "0" : data.get("isAuth") ));//是否鉴权不能移除，因为json中也需要

			String routeInfo =  JSON.toJSONString(data);
			resource.setRouteInfo(routeInfo);//路由信息全部保存在当前json中
			
			resource.setUpdateTime(new Date());
			String jsonStr = JSON.toJSONString(resource);

			String uri = resource.getUri();
			String routeInfoJson = resource.getRouteInfo();
			Boolean flag = resourceService.modifyMenu(resource);
			if (flag) {
				if(3 != sourceType && 0 != sourceType){
					igniteAutoConfig.addApiInfo(false,uri,resource.getMethod(), routeInfoJson);
				}

				return new RetData(200,"update api success");
			} else {
				return new RetData(500,"update api fail");
			}

//			HttpClientResult ret = HttpClientUtil.doPutJson(paramUrl+"/resource/api",headers,jsonStr);
//			if(ret.getCode() == 200) {
//				return new RetData(200,JSON.parse(ret.getContent()));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	/**
	 * 根据id删除对应的api
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/deleteApiResource")
	@ResponseBody
	public RetData deleteApiResource(ReqWebData reqArgs) {
		try {
			//传送到bootshiro的参数--需要map 因此多转一步
			Integer apiId = Integer.valueOf(reqArgs.getStr());


			AuthResource resource = resourceService.getApiInfoByapiId(apiId);//通过apiId查询到其对应的api的uri名称，用于删除缓存
			if(3 == resource.getType() ){
				List<AuthResource>  authResources= resourceService.getApiListByTeamId(apiId);
				if(authResources.size()>0){
					return new RetData(500,"has low level Apis");
				}
			}
			String uri = resource.getUri();
			String httpMethod = resource.getMethod();
			Map routeInfo = (Map) JSON.parse(resource.getRouteInfo());
			Boolean flag = resourceService.deleteMenuByMenuId(apiId);
			if (flag) {

				//shiroFilterChainManager.reloadFilterChain(resource.getId());0
				IgniteCache<String,String> apiAuthCache = igniteAutoConfig.getApiAuthCache();
				apiAuthCache.remove(Constance.API_RESOURCE_ROLE+uri);
				if(3 != resource.getType()  && 0 != resource.getType()){
					igniteAutoConfig.removeApiInfo(uri,httpMethod,
							routeInfo.get("api_version") == null ? resource.getVersion() : (String)routeInfo.get("api_version"));
				}
				return new RetData(200,"delete api success");
			} else {
				return new RetData(500,"delete api fail");
			}

//			HttpClientResult ret = HttpClientUtil.doDelete(paramUrl+"/resource/api/"+apiId,headers,null);
//			if(ret.getCode() == 200) {
//				return new RetData(200,JSON.parse(ret.getContent()));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	
	
	
}
