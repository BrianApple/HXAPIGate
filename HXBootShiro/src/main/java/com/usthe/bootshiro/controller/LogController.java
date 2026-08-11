package com.usthe.bootshiro.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.bo.AuthAccountLog;
import com.usthe.bootshiro.domain.bo.AuthOperationLog;
import com.usthe.bootshiro.domain.vo.Message;
import com.usthe.bootshiro.service.AccountLogService;
import com.usthe.bootshiro.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 日志查询（第三方对外接口，契约：POST /log/accountLog/**、POST /log/operationLog/**）
 * @author tomsun28
 * @date 12:20 2018/4/22
 */
@RestController
@RequestMapping("/log")
public class LogController extends BaseAction {

    @Autowired
    AccountLogService accountLogService;

    @Autowired
    OperationLogService operationLogService;
    @SuppressWarnings("unchecked")
    @Operation(summary = "获取日志记录", method = "POST")
    @PostMapping("/accountLog/{currentPage}/{pageSize}")
    public Message getAccountLogList(@PathVariable Integer currentPage, @PathVariable Integer pageSize ) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthAccountLog> accountLogs = accountLogService.getAccountLogList();
        PageInfo pageInfo = new PageInfo(accountLogs);
        return new Message().ok(200, "return accountLogs success").addData("data",pageInfo);
    }

    @SuppressWarnings("unchecked")
    @Operation(summary = "获取用户操作api日志列表", method = "POST")
    @PostMapping("/operationLog/{currentPage}/{pageSize}")
    public Message getOperationLogList(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AuthOperationLog> authOperationLogs = operationLogService.getOperationList();
        PageInfo pageInfo = new PageInfo(authOperationLogs);
        return new Message().ok(200, "return operationLogList success").addData("data", pageInfo);
    }
}
