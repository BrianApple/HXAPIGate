package com.usthe.bootshiro.controller.apigateinner;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthUser;
import com.usthe.bootshiro.domain.vo.ReqWebData;
import com.usthe.bootshiro.domain.vo.RetData;
import com.usthe.bootshiro.service.UserService;
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
 * 用户管理（管理平台内部接口）：用户 CRUD + 角色分配，与角色管理打通。
 * 用户登录后签发 JWT（携带角色编码），调用网关 API 时由网关 JwtRealm 校验。
 * 注意：前缀用 /inner/sysuser 而非 /inner/user* —— ShiroFilterChainManager 动态规则用
 * startsWith("/inner/user") 排除登录接口，/inner/usermgr 恰好以该前缀开头会被误排除，
 * 导致管理接口落入 /**=anon 无鉴权。必须使用与 /inner/user 完全不同的路径。
 */
@Controller
@RequestMapping("/inner/sysuser")
public class InnerUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InnerUserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/initUserList")
    @ResponseBody
    public RetData initUserList(ReqWebData reqArgs) {
        try {
            int pageIndex = parsePage(reqArgs.getPageIndex(), 1);
            int pageSize = parsePage(reqArgs.getPageSize(), 10);
            PageHelper.startPage(pageIndex, pageSize);
            List<AuthUser> users = userService.getUserList();
            // 附加角色名（列表展示）
            for (AuthUser user : users) {
                user.setPassword(null);
                user.setSalt(null);
                user.setRoleNames(userService.loadAccountRoleNames(user.getUid()));
            }
            PageInfo<AuthUser> pageInfo = new PageInfo<>(users);
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageInfo.getList());
            result.put("total", pageInfo.getTotal());
            return new RetData(200, result);
        } catch (Exception e) {
            LOGGER.error("用户列表查询失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/addUser")
    @ResponseBody
    public RetData addUser(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            AuthUser user = new AuthUser();
            user.setUid(data.get("uid"));
            user.setUsername(data.get("username"));
            user.setPassword(data.get("password"));
            user.setRealName(data.get("realName"));
            user.setPhone(data.get("phone"));
            user.setEmail(data.get("email"));
            if (data.get("sex") != null && !data.get("sex").isEmpty()) {
                user.setSex(Byte.valueOf(data.get("sex")));
            }
            user.setStatus(parseStatus(data.get("status")));
            // 新增：账号/手机号/邮箱均不可重复
            if (isDuplicate(user, true)) {
                return new RetData(500, "账号/手机号/邮箱已存在");
            }
            boolean flag = userService.addUser(user);
            if (!flag) {
                return new RetData(500, "add error");
            }
            // 分配角色（覆盖式）
            userService.authorityUserRoles(user.getUid(), parseRoleIds(data.get("roleIds")));
            return new RetData(200, "add success");
        } catch (Exception e) {
            LOGGER.error("新增用户失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public RetData updateUser(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            AuthUser user = new AuthUser();
            user.setUid(data.get("uid"));
            user.setUsername(data.get("username"));
            user.setRealName(data.get("realName"));
            user.setPhone(data.get("phone"));
            user.setEmail(data.get("email"));
            if (data.get("sex") != null && !data.get("sex").isEmpty()) {
                user.setSex(Byte.valueOf(data.get("sex")));
            }
            user.setStatus(parseStatus(data.get("status")));
            // 编辑：仅手机号/邮箱不可与他人重复（排除自身；账号不可修改）
            if (isDuplicate(user, false)) {
                return new RetData(500, "手机号/邮箱已存在");
            }
            boolean flag = userService.updateUser(user);
            if (!flag) {
                return new RetData(500, "update error");
            }
            // 覆盖式重分配角色
            userService.authorityUserRoles(user.getUid(), parseRoleIds(data.get("roleIds")));
            return new RetData(200, "update success");
        } catch (Exception e) {
            LOGGER.error("编辑用户失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/deleteUser")
    @ResponseBody
    public RetData deleteUser(ReqWebData reqArgs) {
        try {
            String uid = reqArgs.getStr();
            if (StringUtils.isEmpty(uid) && reqArgs.getData() != null && reqArgs.getData().get("uid") != null) {
                uid = reqArgs.getData().get("uid").toString();
            }
            if (StringUtils.isEmpty(uid)) {
                return new RetData(500, "uid is empty");
            }
            if ("admin".equals(uid)) {
                return new RetData(500, "内置管理员不允许删除");
            }
            boolean flag = userService.deleteUser(uid);
            if (flag) {
                return new RetData(200, "delete success");
            }
            return new RetData(500, "delete error");
        } catch (Exception e) {
            LOGGER.error("删除用户失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/resetPassword")
    @ResponseBody
    public RetData resetPassword(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            String uid = data.get("uid");
            String password = data.get("password");
            if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(password)) {
                return new RetData(500, "参数缺失");
            }
            boolean flag = userService.resetPassword(uid, password);
            if (flag) {
                return new RetData(200, "reset success");
            }
            return new RetData(500, "reset error");
        } catch (Exception e) {
            LOGGER.error("重置密码失败", e);
        }
        return new RetData(500);
    }

    @PostMapping("/initUserByUid")
    @ResponseBody
    public RetData initUserByUid(ReqWebData reqArgs) {
        try {
            String uid = reqArgs.getStr();
            if (StringUtils.isEmpty(uid)) {
                return new RetData(500, "uid is empty");
            }
            AuthUser user = userService.getUserByAppId(uid);
            if (user != null) {
                user.setPassword(null);
                user.setSalt(null);
            }
            List<Integer> roleIds = userService.getUserRoleIds(uid);
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("roleIds", roleIds);
            return new RetData(200, result);
        } catch (Exception e) {
            LOGGER.error("用户详情查询失败", e);
        }
        return new RetData(500);
    }

    /** 覆盖式分配用户角色 */
    @PostMapping("/authorityUserRoles")
    @ResponseBody
    public RetData authorityUserRoles(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            String uid = data.get("uid");
            List<Integer> roleIds = parseRoleIds(data.get("roleIds"));
            boolean flag = userService.authorityUserRoles(uid, roleIds);
            if (flag) {
                return new RetData(200, "authority success");
            }
            return new RetData(500, "authority error");
        } catch (Exception e) {
            LOGGER.error("用户角色分配失败", e);
        }
        return new RetData(500);
    }

    /**
     * 账号/手机号/邮箱重复性校验
     * @param user 待校验用户
     * @param checkUid 是否检查 uid 重复（新增 true；编辑 false，账号不可修改）
     */
    private boolean isDuplicate(AuthUser user, boolean checkUid) {
        try {
            for (AuthUser u : userService.getUserList()) {
                if (user.getUid() != null && user.getUid().equals(u.getUid())) {
                    continue; // 排除自身
                }
                if (user.getPhone() != null && !user.getPhone().isEmpty() && user.getPhone().equals(u.getPhone())) {
                    return true;
                }
                if (user.getEmail() != null && !user.getEmail().isEmpty() && user.getEmail().equals(u.getEmail())) {
                    return true;
                }
            }
            // 新增场景：uid 已存在则重复
            if (checkUid && userService.getUserByAppId(user.getUid()) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.warn("重复性校验异常: {}", e.getMessage());
            return false;
        }
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
