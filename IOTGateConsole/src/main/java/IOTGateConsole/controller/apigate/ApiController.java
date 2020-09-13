package IOTGateConsole.controller.apigate;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import IOTGateConsole.controller.apigate.bean.AuthResource;
import IOTGateConsole.databridge.ReqWebData;
import IOTGateConsole.databridge.RetData;

@Controller
@RequestMapping("/api")
/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年8月13日
 * @version 1.0
 */
public class ApiController {
	@Value("${server.paramUrl}")
	private String paramUrl;
	
	/**
	 * 获取所有的api类型
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/initApiType")
	@ResponseBody
    public RetData getApiType(ReqWebData reqArgs) {
		try {
			Map<String,String> map = new HashMap<String, String>();
			map.put("userId", "admin");
			map.put("authorization", reqArgs.getJwt());
			
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/resource/api/-1/1/60",map,null);
			if(ret.getCode() == 200) {
				return new RetData(200,JSON.parse(ret.getContent()));
			}
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
    public RetData getApiList(ReqWebData reqArgs) {
		try {
			
			Map<String,String> map = new HashMap<String, String>();
			map.put("userId", "admin");
			map.put("authorization", reqArgs.getJwt());
			
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/resource/api/0/"+index+"/"+size,map,null);
			if(ret.getCode() == 200) {
				return new RetData(200,JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	/**
	 * 根据Itemid获取对应的所有api
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
			
			String index = reqArgs.getPageIndex();
			String size = reqArgs.getPageSize();
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/resource/api/"+reqArgs.getStr()+"/"+index+"/"+size,map,null);
			if(ret.getCode() == 200) {
				return new RetData(200,JSON.parse(ret.getContent()));
			}
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
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/resource/api/"+reqArgs.getStr(),map,null);
			if(ret.getCode() == 200) {
				Map retData = (Map) JSON.parse(ret.getContent());
				Map restMap = (Map) retData.get("data");
				Map temData = (Map) restMap.get("apiData");
				if(temData.containsKey("routeInfo")) {
					//如果存在路由信息
					String routInfoStr = (String) temData.remove("routeInfo");
					if(routInfoStr != null && !"".equals(routInfoStr)) {
						Map routInfoMap = (Map) JSON.parse(routInfoStr);
						if("http".equals(routInfoMap.get("pType"))) {
							int routeNum = (routInfoMap.size() - 6) /5 ;
							routInfoMap.put("routeNum", routeNum);
						}else if("dubbo".equals(routInfoMap.get("pType"))) {
							int routeNum = (routInfoMap.size() - 6 ) /2 ;
							routInfoMap.put("routeNum", routeNum);
						}
						
						
						
						temData.put("routeInfo", routInfoMap);
					}
				}
				return new RetData(200,temData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	/**
	 * 增加api
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/addApiResource")
	@ResponseBody
	public RetData addApiResource(ReqWebData reqArgs) {
		try {
			//header，固定格式
			Map<String,String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			//传送到bootshiro的参数
			
			Map<String,String> data = reqArgs.getData();
			AuthResource resource = new AuthResource();
			//基本信息
			resource.setName(data.remove("interface_name"));
			resource.setUri(data.remove("url_val"));
			resource.setParentId(Integer.parseInt(data.remove("api_Type")));
			resource.setType(Short.parseShort(data.remove("sour_Type")));
			resource.setMethod(data.remove("request_method"));
			resource.setStatus(Short.parseShort(data.remove("state")));
			//路由信息
			resource.setNeedAuth(Integer.parseInt(data.get("isAuth")));
			String routeInfo =  JSON.toJSONString(data);
			resource.setRouteInfo(routeInfo);//路由信息全部保存在当前json中
			
			
			
			String jsonStr = JSON.toJSONString(resource);
			
			HttpClientResult ret = HttpClientUtil.doPostJson(paramUrl+"/resource/api",headers,jsonStr,60000);
			if(ret.getCode() == 200) {
				return new RetData(200,JSON.parse(ret.getContent()));
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
			Map<String,String> data = reqArgs.getData();
			AuthResource resource = new AuthResource();
			//基本信息
			resource.setId(Integer.parseInt(reqArgs.getStr()));
			resource.setName(data.remove("interface_name"));
			resource.setUri(data.remove("url_val"));//接口名称
			resource.setParentId(Integer.parseInt(data.remove("api_Type")));
			resource.setType(Short.parseShort(data.remove("sour_Type")));
			resource.setMethod(data.remove("request_method"));
			resource.setStatus(Short.parseShort(data.remove("state")));
			//路由信息
			resource.setNeedAuth(Integer.parseInt(data.get("isAuth")));
			String routeInfo =  JSON.toJSONString(data);
			resource.setRouteInfo(routeInfo);//路由信息全部保存在当前json中
			
			resource.setUpdateTime(new Date());
			String jsonStr = JSON.toJSONString(resource);
			
			HttpClientResult ret = HttpClientUtil.doPutJson(paramUrl+"/resource/api",headers,jsonStr);
			if(ret.getCode() == 200) {
				return new RetData(200,JSON.parse(ret.getContent()));
			}
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
			//header，固定格式
			Map<String,String> headers = new HashMap<String, String>();
			headers.put("userId", "admin");
			headers.put("authorization", reqArgs.getJwt());
			String apiId = reqArgs.getStr();
			
			HttpClientResult ret = HttpClientUtil.doDelete(paramUrl+"/resource/api/"+apiId,headers,null);
			if(ret.getCode() == 200) {
				return new RetData(200,JSON.parse(ret.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
	
	
	
}
