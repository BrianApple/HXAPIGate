package com.usthe.bootshiro.controller.apigateinner;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthApp;
import com.usthe.bootshiro.domain.vo.ReqWebData;
import com.usthe.bootshiro.domain.vo.RetData;
import com.usthe.bootshiro.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用管理（管理平台内部接口）：应用 CRUD + 生成 API 访问 JWT license。
 * 调用网关 API 时携带请求头：userId=APP_ID、Authorization=license(JWT)
 */
@Controller
@RequestMapping("/inner/app")
public class InnerAppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InnerAppController.class);

    @Autowired
    private AppService appService;

    @PostMapping("/initAppList")
    @ResponseBody
    public RetData initAppList(ReqWebData reqArgs) {
        try {
            int pageIndex = parsePage(reqArgs.getPageIndex(), 1);
            int pageSize = parsePage(reqArgs.getPageSize(), 10);
            PageHelper.startPage(pageIndex, pageSize);
            List<AuthApp> apps = appService.getAppList();
            PageInfo<AuthApp> pageInfo = new PageInfo<>(apps);
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageInfo.getList());
            result.put("total", pageInfo.getTotal());
            return new RetData(200, result);
        } catch (Exception e) {
            LOGGER.error("应用列表查询失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/addApp")
    @ResponseBody
    public RetData addApp(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            AuthApp app = new AuthApp();
            app.setAppName(data.get("appName"));
            app.setDescription(data.get("description"));
            app.setStatus(parseStatus(data.get("status")));
            List<Integer> roleIds = parseRoleIds(data.get("roleIds"));
            boolean flag = appService.addApp(app, roleIds);
            if (flag) {
                return new RetData(200, "add success");
            }
            return new RetData(500, "add error");
        } catch (Exception e) {
            LOGGER.error("新增应用失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/updateApp")
    @ResponseBody
    public RetData updateApp(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            AuthApp app = new AuthApp();
            if (data.get("id") != null) {
                app.setId(Integer.valueOf(data.get("id")));
            }
            app.setAppName(data.get("appName"));
            app.setDescription(data.get("description"));
            app.setStatus(parseStatus(data.get("status")));
            List<Integer> roleIds = parseRoleIds(data.get("roleIds"));
            boolean flag = appService.updateApp(app, roleIds);
            if (flag) {
                return new RetData(200, "update success");
            }
            return new RetData(500, "update error");
        } catch (Exception e) {
            LOGGER.error("编辑应用失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/deleteApp")
    @ResponseBody
    public RetData deleteApp(ReqWebData reqArgs) {
        try {
            Integer id = reqArgs.getStr() != null ? Integer.valueOf(reqArgs.getStr()) : null;
            if (id == null && reqArgs.getData() != null && reqArgs.getData().get("id") != null) {
                id = Integer.valueOf(reqArgs.getData().get("id").toString());
            }
            boolean flag = appService.deleteApp(id);
            if (flag) {
                return new RetData(200, "delete success");
            }
            return new RetData(500, "delete error");
        } catch (Exception e) {
            LOGGER.error("删除应用失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/initAppByAppId")
    @ResponseBody
    public RetData initAppByAppId(ReqWebData reqArgs) {
        try {
            String appId = reqArgs.getStr();
            if (StringUtils.isEmpty(appId)) {
                return new RetData(500, "appId is empty");
            }
            AuthApp app = appService.getAppByAppId(appId);
            List<Integer> roleIds = appService.getAppRoleIds(appId);
            Map<String, Object> result = new HashMap<>();
            result.put("app", app);
            result.put("roleIds", roleIds);
            return new RetData(200, result);
        } catch (Exception e) {
            LOGGER.error("应用详情查询失败", e);
        }
        return new RetData(500);
    }

    /**
     * 生成 API 访问 JWT license
     * 入参：str=appId，data.expireDays=有效期(天，默认1)
     */
    @PostMapping("/generateLicense")
    @ResponseBody
    public RetData generateLicense(ReqWebData reqArgs) {
        try {
            String appId = reqArgs.getStr();
            if (StringUtils.isEmpty(appId)) {
                return new RetData(500, "appId is empty");
            }
            long expireSeconds = 86400L;
            if (reqArgs.getData() != null && reqArgs.getData().get("expireDays") != null) {
                expireSeconds = Long.parseLong(reqArgs.getData().get("expireDays").toString()) * 86400L;
            }
            Map<String, Object> license = appService.generateLicense(appId, expireSeconds);
            if (license == null) {
                return new RetData(500, "app not exist");
            }
            return new RetData(200, license);
        } catch (IllegalStateException e) {
            LOGGER.warn("生成 license 失败: {}", e.getMessage());
            return new RetData(500, e.getMessage());
        } catch (Exception e) {
            LOGGER.error("生成 license 失败", e);
        }
        return new RetData(500);
    }

    private int parsePage(String val, int defaultVal) {
        try {
            return val != null && !val.isEmpty() ? Integer.parseInt(val) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private Byte parseStatus(String val) {
        if (val == null || val.isEmpty()) {
            return (byte) 1;
        }
        try {
            return Byte.parseByte(val);
        } catch (NumberFormatException e) {
            return (byte) 1;
        }
    }

    private List<Integer> parseRoleIds(String val) {
        List<Integer> roleIds = new ArrayList<>();
        if (val == null || val.trim().isEmpty()) {
            return roleIds;
        }
        for (String s : val.split(",")) {
            try {
                roleIds.add(Integer.valueOf(s.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return roleIds;
    }
}
