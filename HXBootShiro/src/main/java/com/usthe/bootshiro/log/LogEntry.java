package com.usthe.bootshiro.log;

/**
 * 日志条目（解析自日志文件的单行记录）
 */
public class LogEntry {

    private String time;      // 时间戳 yyyy-MM-dd HH:mm:ss.SSS
    private String thread;    // 线程名
    private String level;     // TRACE/DEBUG/INFO/WARN/ERROR
    private String traceId;   // 请求溯源 ID（无则空）
    private String proto;     // 协议标识 http/mcp/tcp/websocket/dubbo（无则空）
    private String frameId;   // 帧号（长连接消息级标识 <traceId>-F<序号>；无则空）
    private String logger;    // logger 名称
    private String message;   // 日志消息
    private String source;    // 来源：gateway / admin

    public LogEntry() {
    }

    public LogEntry(String time, String thread, String level, String traceId, String proto, String frameId,
                    String logger, String message, String source) {
        this.time = time;
        this.thread = thread;
        this.level = level;
        this.traceId = traceId;
        this.proto = proto;
        this.frameId = frameId;
        this.logger = logger;
        this.message = message;
        this.source = source;
    }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getThread() { return thread; }
    public void setThread(String thread) { this.thread = thread; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getProto() { return proto; }
    public void setProto(String proto) { this.proto = proto; }
    public String getFrameId() { return frameId; }
    public void setFrameId(String frameId) { this.frameId = frameId; }
    public String getLogger() { return logger; }
    public void setLogger(String logger) { this.logger = logger; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
