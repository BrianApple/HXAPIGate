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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

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
        final String json = JSON.toJSONString(obj, false);;
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
		 
		response.content().writeBytes(JSON.toJSONBytes(new RetMessage().error(status, msg), SerializerFeature.EMPTY));
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
