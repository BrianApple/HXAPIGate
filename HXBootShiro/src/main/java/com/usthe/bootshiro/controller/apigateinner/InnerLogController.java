package com.usthe.bootshiro.controller.apigateinner;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.usthe.bootshiro.domain.vo.ReqWebData;
import com.usthe.bootshiro.domain.vo.RetData;
import com.usthe.bootshiro.log.LogEntry;
import com.usthe.bootshiro.log.LogQueryService;

/**
 * 日志查询接口（参考主流网关管理台日志页）：
 * <ul>
 *   <li>/inner/log/search —— 条件分页搜索（traceId/协议/级别/关键词/时间范围）</li>
 *   <li>/inner/log/trace —— 按 traceId 返回请求完整链路（网关+管理端全部相关日志，时间升序）</li>
 * </ul>
 */
@RestController
@RequestMapping("/inner/log")
public class InnerLogController {

    @Autowired
    private LogQueryService logQueryService;

    /**
     * 条件搜索日志（分页）
     * 参数（ReqWebData）：
     *   data[traceId] / data[proto] / data[level] / data[keyword] / data[startTime] / data[endTime]
     *   pageIndex / pageSize
     */
    @PostMapping("/search")
    @ResponseBody
    public RetData search(ReqWebData reqArgs) {
        try {
            Map<String, String> data = reqArgs.getData();
            String traceId = val(data, "traceId");
            String proto = val(data, "proto");
            String level = val(data, "level");
            String keyword = val(data, "keyword");
            String startTime = val(data, "startTime");
            String endTime = val(data, "endTime");
            int pageIndex = parseInt(reqArgs.getPageIndex(), 1);
            int pageSize = parseInt(reqArgs.getPageSize(), 20);

            List<LogEntry> all = logQueryService.search(traceId, proto, level, keyword, startTime, endTime);
            int total = all.size();
            int from = Math.min((pageIndex - 1) * pageSize, total);
            int to = Math.min(from + pageSize, total);
            List<LogEntry> page = all.subList(from, to);

            PageInfo<LogEntry> pageInfo = new PageInfo<>(page);
            pageInfo.setTotal(total);
            return new RetData(200, pageInfo);
        } catch (Exception e) {
            return new RetData(500, "log search error: " + e.getMessage());
        }
    }

    /**
     * 按 traceId 查询完整请求链路（str=traceId）
     */
    @PostMapping("/trace")
    @ResponseBody
    public RetData trace(ReqWebData reqArgs) {
        try {
            String traceId = reqArgs.getStr();
            if (traceId == null || traceId.trim().isEmpty()) {
                Map<String, String> data = reqArgs.getData();
                traceId = val(data, "traceId");
            }
            List<LogEntry> list = logQueryService.trace(traceId);
            return new RetData(200, list);
        } catch (Exception e) {
            return new RetData(500, "log trace error: " + e.getMessage());
        }
    }

    private String val(Map<String, String> data, String key) {
        return data == null ? null : data.get(key);
    }

    private int parseInt(String s, int def) {
        if (s == null || s.trim().isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
