package IOTGateConsole.controller.apigate;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import IOTGateConsole.databridge.ReqWebData;
import IOTGateConsole.databridge.RetData;


/**
 * 
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: www.uiotcp.com</p>
 * @author hejuanjuan
 * @date 2020年9月01日
 * @version 1.0
 */
@Controller
@RequestMapping("/user")
public class AccountController {
	@Value("${server.paramUrl}")
	private String paramUrl;
	/**
	 * 校验jwt是否已过期
	 * @param reqArgs
	 * @return
	 */
	@PostMapping("/islogin")
	@ResponseBody
    public RetData islogin(ReqWebData reqArgs) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("userId", "admin");
		map.put("authorization", reqArgs.getJwt());
		HttpClientResult ret2;
		try {
			ret2 = HttpClientUtil.doPost(paramUrl+"/resource/isjwtRight", map, null);
			if(ret2.getCode() == 200) {
				return new RetData(200,JSON.parse(ret2.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new RetData(500);
	}
	@PostMapping("/login")
	@ResponseBody
	public RetData accountLogin(ReqWebData reqArgs) {
		try {
			HttpClientResult ret = HttpClientUtil.doGet(paramUrl+"/account/register?tokenKey=get");
			if(ret.getCode() == 200) {
				Map map = (Map) JSON.parse(ret.getContent());
				Map data = (Map) map.get("data");
				Map meta = (Map) map.get("meta");
				String token = (String) data.get("tokenKey");
//				String s1 =(String) paramMap.get("appId");
//				String s = (String) paramMap.get("password");
				//加密密码
				String password = AesUtil.aesEncode((String)reqArgs.getData().get("password"), token);
				
				//调用登录接口
				Map<String, String> header = new HashMap<>();
				header.put("content-type", "text/html");
//				header.put("enctype", "multipart/form-data");
				
				Map<String, String> arg = new HashMap<>();
				arg.put("appId", (String) reqArgs.getData().get("username"));
				arg.put("password", password);
				arg.put("methodName", "login");
				arg.put("userKey", (String) data.get("userKey"));
				arg.put("timestamp", String.valueOf(meta.get("timestamp")));
				
				
				HttpClientResult ret2 = HttpClientUtil.doPostJson(paramUrl+"/account/login", JSON.toJSONString(arg),30000);
				if(ret2.getCode() == 200) {
					System.out.println(ret2.getContent());
					return new RetData(200,JSON.parse(ret2.getContent()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RetData(500);
	}
}
