package hx.apigate.util;



import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.RetMessage;
import hx.apigate.databridge.xmlBean.RouteNode;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.AttributeKey;
/**
 * 
 * @Description: 
 * @author  yangcheng
 * @date:   2019年3月30日
 */
public class MixAll {
	public static final  String LOG_INFO_PRIFEX = "[HXAPIGate Info :] ";
	public static final AttributeKey<String> WEB_SESSION_ID = AttributeKey.valueOf("web_seesion_id");//web（第三方客户端）http对应的唯一标识
	public static final AttributeKey<NodeInfo> ATTRIBUTEKEY_ROUTE_NODE = AttributeKey.valueOf("node");//handler之间传递微服务Node节点信息
	public static final AttributeKey<String> ATTRIBUTEKEY_URL = AttributeKey.valueOf("requestUrl");
	/** 请求溯源 ID（traceId）：HTTP 请求在 GatewayServerHandler 入口生成，随 channel 传递到转发/日志环节 */
	public static final AttributeKey<String> ATTRIBUTEKEY_TRACE_ID = AttributeKey.valueOf("traceId");
	/** 连接内帧计数器（AtomicLong）：长连接（websocket/tcp/dubbo）每帧消息递增，生成帧号 frameId（<traceId>-F<序号>） */
	public static final AttributeKey<AtomicLong> ATTRIBUTEKEY_FRAME_SEQ = AttributeKey.valueOf("frameSeq");
	/** 最近一次请求帧号（String）：后端响应帧沿用该帧号，实现请求-响应对同帧号（一帧=一次业务往返） */
	public static final AttributeKey<String> ATTRIBUTEKEY_LAST_FRAME_ID = AttributeKey.valueOf("lastFrameId");

	/** 获取连接内帧计数器（不存在则创建并绑定到 channel attr） */
	public static AtomicLong getOrCreateFrameSeq(io.netty.channel.Channel ch) {
		AtomicLong seq = ch.attr(ATTRIBUTEKEY_FRAME_SEQ).get();
		if (seq == null) {
			seq = new AtomicLong(0);
			ch.attr(ATTRIBUTEKEY_FRAME_SEQ).set(seq);
		}
		return seq;
	}

	/** 帧内容描述：文本帧显示 UTF-8 文本（超 200 字符截断），二进制帧显示 hex 预览（超 64 字节截断），控制帧仅显示类型 */
	public static String describeFrame(io.netty.handler.codec.http.websocketx.WebSocketFrame frame) {
		if (frame == null) {
			return "";
		}
		if (frame instanceof io.netty.handler.codec.http.websocketx.CloseWebSocketFrame) {
			return "<close>";
		}
		if (frame instanceof io.netty.handler.codec.http.websocketx.PingWebSocketFrame) {
			return "<ping>";
		}
		if (frame instanceof io.netty.handler.codec.http.websocketx.PongWebSocketFrame) {
			return "<pong>";
		}
		io.netty.buffer.ByteBuf buf = frame.content();
		int len = buf.readableBytes();
		if (len == 0) {
			return "<empty>";
		}
		if (frame instanceof io.netty.handler.codec.http.websocketx.TextWebSocketFrame) {
			String text = buf.toString(io.netty.util.CharsetUtil.UTF_8);
			return len > 200 ? text.substring(0, 200) + "...(" + len + "B)" : text + " (" + len + "B)";
		}
		// 二进制帧：hex 预览
		int show = Math.min(len, 64);
		byte[] bytes = new byte[show];
		buf.getBytes(buf.readerIndex(), bytes);
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString().trim() + (len > 64 ? "...(" + len + "B)" : " (" + len + "B)");
	}

	/** ByteBuf 内容描述：UTF-8 文本（超 200 字符截断）或 hex 预览（超 64 字节截断），用于 TCP 帧日志 */
	public static String describeBytes(io.netty.buffer.ByteBuf buf) {
		if (buf == null) {
			return "";
		}
		int len = buf.readableBytes();
		if (len == 0) {
			return "<empty>";
		}
		byte[] bytes = new byte[len];
		buf.getBytes(buf.readerIndex(), bytes);
		// 尝试按 UTF-8 文本显示（可打印字符占比高时）
		int printable = 0;
		for (int i = 0; i < Math.min(len, 64); i++) {
			byte b = bytes[i];
			if ((b >= 0x20 && b < 0x7F) || b == '\n' || b == '\r' || b == '\t') {
				printable++;
			}
		}
		if (printable * 10 >= Math.min(len, 64) * 8) {
			String text = new String(bytes, io.netty.util.CharsetUtil.UTF_8);
			return len > 200 ? text.substring(0, 200) + "...(" + len + "B)" : text + " (" + len + "B)";
		}
		// 二进制：hex 预览
		int show = Math.min(len, 64);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < show; i++) {
			sb.append(String.format("%02X ", bytes[i]));
		}
		return sb.toString().trim() + (len > 64 ? "...(" + len + "B)" : " (" + len + "B)");
	}
	/** 透传模式标记（true=原始响应透传，不包装 RetMessage；由 Accept: text/event-stream 或 X-HXAPI-Transparent: true 触发，用于 MCP/SSE 流式代理） */
	public static final AttributeKey<Boolean> ATTRIBUTEKEY_TRANSPARENT = AttributeKey.valueOf("transparent");
	private static final String CLASS_SUFFIX = ".class";
	private static AsciiString contentType = HttpHeaderValues.APPLICATION_JSON;
	private static final String CLASS_FILE_PREFIX = File.separator + "classes"  + File.separator;
	private static final String PACKAGE_SEPARATOR = ".";
	public static AtomicInteger counter = new AtomicInteger(0);//测试计数用
	
	private MixAll(){
		throw new AssertionError();
	}
	public static List<String> inetAddressList ;
	static{
		inetAddressList = getLocalInetAddress();
	}
	
	public static List<String> getLocalInetAddress() {
        List<String> inetAddressList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    inetAddressList.add(addrs.nextElement().getHostAddress());
                }
            }
        }
        catch (SocketException e) {
            throw new RuntimeException("get local inet address fail", e);
        }

        return inetAddressList;
    }
	/**
	 * 获取程序进程号
	 * @return
	 */
	public static long getPID() {
	    String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	    if (processName != null && processName.length() > 0) {
	        try {
	            return Long.parseLong(processName.split("@")[0]);
	        }
	        catch (Exception e) {
	            return 0;
	        }
	    }
	
	    return 0;
	}
	/**
	 * 获取本机的ip--linux平台上获取的ip会存在问题，拿到的是127.0.0.1
	 * @return
	 */
	public static String localhostName() {
	    try {
	        return InetAddress.getLocalHost().getHostAddress();
	    }
	    catch (Throwable e) {
	        throw new RuntimeException(
	            "InetAddress java.net.InetAddress.getLocalHost() throws UnknownHostException"
	        		, e);
	    }
	}
	/**
	 * 内网IP
	 * @return
	 * @throws SocketException
	 */
	
	@SuppressWarnings("rawtypes")
	public static String linuxLocalIP() throws SocketException{
		Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		String localHostIP = null;
		List<InetAddress> cache = new ArrayList<>();
		while (allNetInterfaces.hasMoreElements()){
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
			Enumeration addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()){
				ip = (InetAddress) addresses.nextElement();
//				System.out.println("：：：：：：：：：：："+ip.getHostAddress());
				if (ip != null && ip instanceof Inet4Address){
					if(!ip.isLoopbackAddress() && ip.isSiteLocalAddress()){
						localHostIP = ip.getHostAddress();
						System.out.println("本机的IPV4 = " + ip.getHostAddress());
					}else{
						cache.add(ip);
					}
					
				} else if(ip instanceof Inet6Address){
					if(!ip.isLoopbackAddress() && ip.isSiteLocalAddress()){
						localHostIP = ip.getHostAddress();
//						System.out.println("本机的IPV6 = " + ip.getHostAddress());
					}else{
						cache.add(ip);
					}
				}
			}
		}
		
		if(localHostIP != null){
			return localHostIP;
		}else{
			for (InetAddress inetAddress : cache) {
				if(inetAddress.isSiteLocalAddress()){
					System.out.println("去本地地址ip="+inetAddress.getHostAddress());
					return inetAddress.getHostAddress();
				}
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取指定包下所有的class名称
	 * reference from  
	 * @param packageName
	 * @param showChildPackageFlag
	 * @return
	 */
	public static List<String> getClazzName(String packageName, boolean showChildPackageFlag ) {
	    List<String> result = new ArrayList<>();

	    String suffixPath = packageName.replaceAll("\\.", "\\/");
	    System.out.println("suffixPath="+suffixPath);
	   

	    try {
	    	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	        Enumeration<URL> urls = loader.getResources(suffixPath);

	        while(urls.hasMoreElements()) {

	            URL url = urls.nextElement();
	            System.out.println("url=="+url.getPath());
	            if(url != null) {

	                String protocol = url.getProtocol();

	                if("file".equals(protocol)) {

	                    String path = url.getPath();

	                    System.out.println(path);

	                    result.addAll(getAllClassNameByFile(new File(path), showChildPackageFlag));

	                }

	            }

	        }

	    } catch (IOException e) {

	        e.printStackTrace();

	    }

	    
	    return result;

	}
	
	/**
	 * 发布jar包中的rpc
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static List<String> getClazzNameFromJar() throws IOException{
		
		List<String> result = new ArrayList<>();
		
		String path = MixAll.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("path: "+path); //"/opt/myprograms/Gate-0.0.1-SNAPSHOT.jar"
        JarFile localJarFile = new JarFile(new File(path));

        Enumeration<JarEntry> entries = localJarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String innerPath = jarEntry.getName();
//            System.out.println("innerPath=="+innerPath);//  "gate/rpc/rpcService/"
            if(innerPath.startsWith("gate/rpc/rpcService/") && innerPath.endsWith("class")){
            		if(-1 == innerPath.indexOf("$")) {
            			innerPath = innerPath.replace(CLASS_SUFFIX, "");
            			innerPath = innerPath.replace(File.separator, PACKAGE_SEPARATOR);
                		if(innerPath.endsWith("Impl")){
                			System.out.println("找到Impl类=="+innerPath);
                			result.add(innerPath);
                		}

    	            }
            }
        }
        return result;
	}

	/**
	 * 递归获取所有class文件的名字
	 * @param file 
	 * @param flag  是否需要迭代遍历
	 * @return List
	 */
	private static List<String> getAllClassNameByFile(File file, boolean flag) {
//		System.out.println("类名称=="+file.getPath());
	    List<String> result =  new ArrayList<>();

	    if(!file.exists()) {

	        return result;

	    }

	    if(file.isFile()) {

	        String path = file.getPath();

	        if(path.endsWith(CLASS_SUFFIX)) {

	            path = path.replace(CLASS_SUFFIX, "");

	            String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())

	                    .replace(File.separator, PACKAGE_SEPARATOR);

	            if(-1 == clazzName.indexOf("$")) {

	                result.add(clazzName);

	            }

	        }

	        return result;

	        

	    } else {

	        File[] listFiles = file.listFiles();

	        if(listFiles != null && listFiles.length > 0) {

	            for (File f : listFiles) {

	                if(flag) {

	                    result.addAll(getAllClassNameByFile(f, flag));

	                } else {

	                    if(f.isFile()){

	                        String path = f.getPath();

	                        if(path.endsWith(CLASS_SUFFIX)) {

	                            path = path.replace(CLASS_SUFFIX, "");

	                            // 从"/classes/"后面开始截取

	                            String clazzName = path.substring(path.indexOf(CLASS_FILE_PREFIX) + CLASS_FILE_PREFIX.length())

	                                    .replace(File.separator, PACKAGE_SEPARATOR);

	                            if(-1 == clazzName.indexOf("$")) {

	                                result.add(clazzName);

	                            }

	                        }

	                    }

	                }

	            }

	        } 

	        return result;

	    }

	}
	/**
	 * 反序列化指定对象
	 * @param data
	 * @param classOfT
	 * @return
	 */
	public static <T> T decode(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, Charset.forName("UTF-8"));
        try {
        	 T  t= JSON.parseObject(json, classOfT);
        	 return t;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        return null;
    }
	
	/**
	 * 序列化指定对象
	 * @param obj
	 * @return
	 */
	public static byte[] encode(final Object obj) {
        final String json = JSON.toJSONString(obj);
        if (json != null) {
            return json.getBytes(Charset.forName("UTF-8"));
        }
        return null;
    }
	
	/**
	 * 驼峰原则
	 * @param str
	 * @return
	 */
	public static String formatString(String str){
		String temp = str.substring(1, str.length());
		str = str.substring(0, 1).toLowerCase();
		return str += temp;
	}
	
	 public static String jstack() {
	        StringBuilder result = new StringBuilder();
	        try {
	            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
	            Iterator<Map.Entry<Thread, StackTraceElement[]>> ite = map.entrySet().iterator();
	            while (ite.hasNext()) {
	                Map.Entry<Thread, StackTraceElement[]> entry = ite.next();
	                StackTraceElement[] elements = entry.getValue();
	                Thread thread = entry.getKey();
	                if (elements != null && elements.length > 0) {
	                    String threadName = entry.getKey().getName();
	                    result.append(String.format("%-40sTID: %d STATE: %s\n", threadName, thread.getId(),
	                        thread.getState()));
	                    for (StackTraceElement el : elements) {
	                        result.append(String.format("%-40s%s\n", threadName, el.toString()));
	                    }
	                    result.append("\n");
	                }
	            }
	        }
	        catch (Throwable e) {
//	            result.append(RemotingHelper.exceptionSimpleDesc(e));
	        }

	        return result.toString();
	    }
	 
	 public static DefaultFullHttpResponse getDefaultFullHttpResponse4Error(int status , String msg) {//"The path you accessed does not exist !"
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		 
		response.content().writeBytes(JSON.toJSONBytes(new RetMessage().error(status, msg)));
		HttpHeaders heads = response.headers();
		// 返回内容的MIME类型
		heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
		// 响应体的长度
		heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		// 表示是否需要持久连接
		heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		//允许跨域访问
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"*");//允许headers自定义
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST, PUT,DELETE");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
    	return response;
	 }
	
}
