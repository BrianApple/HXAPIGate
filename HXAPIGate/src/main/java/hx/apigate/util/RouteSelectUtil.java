package hx.apigate.util;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import hx.apigate.circuitBreaker.CBManager;
import hx.apigate.databridge.CircleBreakException;
import org.apache.ignite.IgniteSemaphore;
import org.apache.ignite.binary.BinaryObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hx.apigate.databridge.NodeInfo;
import hx.apigate.databridge.SemphareException;
import hx.apigate.databridge.xmlBean.Route;
import hx.apigate.databridge.xmlBean.RouteAll;
import hx.apigate.databridge.xmlBean.RouteNode;
import hx.apigate.socket.handlers.GatewayServerHandler;
import io.netty.handler.codec.http.HttpMethod;

/**
 * <p>Description: 路由选择工具类</p>
　 * <p>Copyright: Copyright (c) 2019</p>
　 * <p>Company: www.uiotcp.com</p>
　 * @author yangcheng
　 * @date 2019年10月29日
　 * @version 1.0
 */
public class RouteSelectUtil {
    private static Logger logger = LoggerFactory.getLogger(RouteSelectUtil.class);
    public static String HTTP  = "http";
    public static String DUBBO  = "dubbo";


    public static String CIRCLE  = "circle";
    public static String WEIGHT  = "weight";

    public static String pathSeparator  = "/";
    /**
     * 根据url以及轮寻策略 选择路由的Node
     * @param httpMethod
     * @param sourceUrl 路径名称
     * @return
     * @throws Exception
     */
    public static Object[] selectOneNode(String sourceUrl, HttpMethod httpMethod) throws SemphareException, CircleBreakException {
        //获取与当前url匹配的route url
        String sourceUrlTemp = sourceUrl.contains("?") ? sourceUrl.split("\\?")[0] :  sourceUrl;
        String pattern = getMatchedPattern(sourceUrlTemp,httpMethod);
        if(pattern == null) {
            return null;
        }
        Object[] ret =new  Object[2];
        ret[0] = pattern;
        NodeInfo info = getRouteByPattern(sourceUrl,sourceUrlTemp,pattern);
        if(info != null) {
            ret[1] = info;
        }
        return ret;
    }
    /**
     * 通过uri获取路由（不是节点node）
     * @param patternUri
     * @return
     */
    public static IgniteSemaphore selectRouteByUri(String patternUri,String version){
        //获取与当前url匹配的route url
        Map<String, RouteAll>  map = IgniteUtil.getAPIRouteCache().get("ALL_ROUTE");
        RouteAll routeAll = map.get(patternUri);
        int len = routeAll.getRoutes().size();
        for(int i = 0 ; i < len ; i ++) {
            if(version.equals(routeAll.getRoutes().get(i).getVersion())) {
                return routeAll.getRoutes().get(i).getTps();
            }
        }
        return null;
    }
    /**
     * 根据获取到的url的pattern 获取规约类型并返回路由信息
     * @param sourceUrl  请求的url(全路径，包含get请求的参数)
     * @param sourceUrlTemp  将get请求的url中的参数截取掉，剩下简单的路径<br>
     * @param pattern    网关配置的url模版 包含请求方式
     * @return
     */
    public static NodeInfo getRouteByPattern(String sourceUrl,String sourceUrlTemp,String pattern) throws SemphareException,CircleBreakException{
        //根据负载策略选择微服务routeNode
        RouteAll routeAll = getRouteAll4lvs(pattern) ;//IgniteUtil.getAPIRouteCache().get("ALL_ROUTE").get(pattern);

        Route route = routeAll.nextRoute();
        //获取路由信号量
        if(!route.getTps().removed() && route.getTps().tryAcquire()) {
            if(HTTP.equals(route.getProtocal())) {

                int routeNum = route.getRouteNodes().size();
                for(int i = 0 ; i < routeNum ; i ++) {
                    if(RouteSelectUtil.WEIGHT.equals(route.getStratege()) || route.getStratege() == null){
                        RouteNode node = route.nextNodeByWeight();
                        if(!route.getTps().removed() && node.getTps().tryAcquire()) {
                            String circleBreakKey = new StringBuilder(route.getMatchUrl()).append(route.getVersion())
                                    .append(node.getIp()).append(node.getPort()).toString();
                            CBManager manager = null;
                            if(!IgniteUtil.getCircleBreakCache().containsKey(circleBreakKey)) {
                                manager = new CBManager(circleBreakKey,node.getIntTps() < 2 ? 1 : (node.getIntTps() > 100 ? 50 : node.getIntTps() >> 1),
                                        node.getIntTps() < 5? 1 : (node.getIntTps() > 100 ? 20 : node.getIntTps() >> 2), 1000);
                                IgniteUtil.getCircleBreakCache().putIfAbsentAsync(circleBreakKey, manager);
                            }else {
                                manager = IgniteUtil.getCircleBreakCache().get(circleBreakKey);
                            }
                            try {
                                manager.getState().preMethodExecute();
                            } catch (CircleBreakException e) {
                                node.getTps().release();
                                route.getTps().release();
                                throw e;
                            }
                            return new NodeInfo(route.getVersion(),route.getProtocal(), node,route.isNeedAuth(),circleBreakKey);
                        }
                    }else{
                        if(routeNum > 0){
                            int nextIndex = route.getIndex().addAndGet(1) % routeNum;
                            route.getIndex().set(nextIndex);
                            RouteNode node = route.getRouteNodes().get(nextIndex);
                            System.out.println(sourceUrl+"的node信号量为"+node.getTps().availablePermits() + "; host = " + node.getIp() +":"+ node.getPort());
                            if(!route.getTps().removed() && node.getTps().tryAcquire()) {
                                String circleBreakKey = new StringBuilder(route.getMatchUrl()).append(route.getVersion())
                                        .append(node.getIp()).append(node.getPort()).toString();
                                CBManager manager = null;
                                if(!IgniteUtil.getCircleBreakCache().containsKey(circleBreakKey)) {
                                    manager = new CBManager(circleBreakKey,node.getIntTps() < 2 ? 1 : (node.getIntTps() > 100 ? 50 : node.getIntTps() >> 1),
                                            node.getIntTps() < 5? 1 : (node.getIntTps() > 100 ? 20 : node.getIntTps() >> 2), 1000);
                                    IgniteUtil.getCircleBreakCache().putIfAbsentAsync(circleBreakKey, manager);
                                }else {
                                    manager = IgniteUtil.getCircleBreakCache().get(circleBreakKey);
                                }
                                try {
                                    manager.getState().preMethodExecute();
                                } catch (CircleBreakException e) {
                                    node.getTps().release();
                                    route.getTps().release();
                                    throw e;
                                }
                                return new NodeInfo(route.getVersion(),route.getProtocal(), node,route.isNeedAuth(),circleBreakKey);
                            }
                        }
                    }
                }
            }else if(DUBBO.equals(route.getProtocal())) {
                String[] urls = sourceUrlTemp.substring(1).split("\\/");
                int size = route.getRouteNodes().size();
                if(size>1) {
                    for(int i = 0 ; i < size ; i ++) {
                        int nextIndex = route.getIndex().addAndGet(1) % size;
                        route.getIndex().set(nextIndex);
                        RouteNode node = route.getRouteNodes().get(nextIndex);
                        if(!route.getTps().removed() && node.getTps().tryAcquire()) {
                            String circleBreakKey = new StringBuilder(route.getMatchUrl()).append(route.getVersion())
                                    .append(node.getInterfaceName()).toString();
                            CBManager manager = null;
                            if(!IgniteUtil.getCircleBreakCache().containsKey(circleBreakKey)) {
                                manager = new CBManager(circleBreakKey,node.getIntTps() < 2 ? 1 : (node.getIntTps() > 100 ? 50 : node.getIntTps() >> 1),
                                        node.getIntTps() < 5? 1 : (node.getIntTps() > 100 ? 20 : node.getIntTps() >> 2), 1000);
                                IgniteUtil.getCircleBreakCache().putIfAbsentAsync(circleBreakKey, manager);
                            }else {
                                manager = IgniteUtil.getCircleBreakCache().get(circleBreakKey);
                            }
                            try {
                                manager.getState().preMethodExecute();
                            } catch (CircleBreakException e) {
                                node.getTps().release();
                                route.getTps().release();
                                throw e;
                            }
                            return new NodeInfo(route.getVersion(),route.getProtocal(),urls[1], node,sourceUrl,route.isNeedAuth(),circleBreakKey);
                        }
                    }
                }else {
                    RouteNode node = route.getRouteNodes().get(0);
                    System.out.println(sourceUrl+"的node信号量为"+node.getTps().availablePermits());
                    if(!route.getTps().removed() && node.getTps().tryAcquire()) {
                        String circleBreakKey = new StringBuilder(route.getMatchUrl()).append(route.getVersion())
                                .append(node.getInterfaceName()).toString();
                        CBManager manager = null;
                        if(!IgniteUtil.getCircleBreakCache().containsKey(circleBreakKey)) {
                            manager = new CBManager(circleBreakKey,node.getIntTps() < 2 ? 1 : (node.getIntTps() > 100 ? 50 : node.getIntTps() >> 1),
                                    node.getIntTps() < 5? 1 : (node.getIntTps() > 100 ? 20 : node.getIntTps() >> 2), 1000);
                            IgniteUtil.getCircleBreakCache().putIfAbsentAsync(circleBreakKey, manager);
                        }else {
                            manager = IgniteUtil.getCircleBreakCache().get(circleBreakKey);
                        }
                        try {
                            manager.getState().preMethodExecute();
                        } catch (CircleBreakException e) {
                            node.getTps().release();
                            route.getTps().release();
                            throw e;
                        }
                        return new NodeInfo(route.getVersion(),route.getProtocal(),urls[1],node,sourceUrl,route.isNeedAuth(),circleBreakKey);
                    }
                }
            }
            route.getTps().release();
            throw new SemphareException(false,"Access to current Node is limited. Please try again later !");
        }

        throw new SemphareException(true,"Access to current Route is limited. Please try again later !");
    }
    /**
     * 获取RouteAll用于lvs
     * @return
     */
    public static RouteAll getRouteAll4lvs(String pattern) {
        Map<String, RouteAll>  map = IgniteUtil.getAPIRouteCache().get("ALL_ROUTE");
        return map.get(pattern);
    }

    /**
     * 根据url获取route cache中与之匹配的url并返回，存在“*”或“?”,为: uri + "==" +httpMethod
     * @param sourceUrl
     * @param httpMethod
     * @return
     */
    private static String getMatchedPattern(String sourceUrl, HttpMethod httpMethod){
        Iterator<String> it = null;
        try {
            it = IgniteUtil.getAPIRouteCache().get("ALL_ROUTE").keySet().iterator();
        }catch (BinaryObjectException e) {
            logger.error(e.getMessage());
        }
        if(it != null) {
            String pattern = null;
            String temp = null;
            while(it.hasNext()){
                temp = it.next();
                String[] uriInfo = temp.split("==");
                if(uriInfo[1].equals(httpMethod.toString()) && doMatch(uriInfo[0], sourceUrl ,true)){
                    pattern = temp;
                    break;
                }
            }
            return pattern;
        }
        return null;
    }




    /**
     * 选择路由
     * @param pattern
     * @param path 获取到的web端url
     * @param fullMatch 是否需要完全模式匹配
     * @return
     */
    private static  boolean doMatch(String pattern, String path, boolean fullMatch) {
        if (path.startsWith(RouteSelectUtil.pathSeparator) != pattern.startsWith(RouteSelectUtil.pathSeparator)) {
            return false;
        }

        String[] pattDirs = StringUtils.tokenizeToStringArray(pattern, RouteSelectUtil.pathSeparator);
        String[] pathDirs = StringUtils.tokenizeToStringArray(path, RouteSelectUtil.pathSeparator);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxStart];
            if ("**".equals(patDir)) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxStart])) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(RouteSelectUtil.pathSeparator) ?
                        path.endsWith(RouteSelectUtil.pathSeparator) : !path.endsWith(RouteSelectUtil.pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") &&
                    path.endsWith(RouteSelectUtil.pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String patDir = pattDirs[pattIdxEnd];
            if (patDir.equals("**")) {
                break;
            }
            if (!matchStrings(patDir, pathDirs[pathIdxEnd])) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = (String) pattDirs[pattIdxStart + j + 1];
                    String subStr = (String) pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }

        return true;
    }


    /**
     * Tests whether or not a string matches against a pattern.
     * The pattern may contain two special characters:<br>
     * '*' means zero or more characters<br>
     * '?' means one and only one character
     *
     * @param pattern pattern to match against.
     *                Must not be <code>null</code>.
     * @param str     string which must be matched against the pattern.
     *                Must not be <code>null</code>.
     * @return <code>true</code> if the string matches against the
     *         pattern, or <code>false</code> otherwise.
     */
    private static boolean matchStrings(String pattern, String str) {
        char[] patArr = pattern.toCharArray();
        char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        char ch;

        boolean containsStar = false;
        for (char aPatArr : patArr) {
            if (aPatArr == '*') {
                containsStar = true;
                break;
            }
        }

        if (!containsStar) {
            // No '*'s, so we make a shortcut
            if (patIdxEnd != strIdxEnd) {
                return false; // Pattern and string do not have the same size
            }
            for (int i = 0; i <= patIdxEnd; i++) {
                ch = patArr[i];
                if (ch != '?') {
                    if (ch != strArr[i]) {
                        return false;// Character mismatch
                    }
                }
            }
            return true; // String matches against pattern
        }


        if (patIdxEnd == 0) {
            return true; // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?') {
                if (ch != strArr[strIdxStart]) {
                    return false;// Character mismatch
                }
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }

        // Process characters after last star
        while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?') {
                if (ch != strArr[strIdxEnd]) {
                    return false;// Character mismatch
                }
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (int i = patIdxStart; i <= patIdxEnd; i++) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }

        // process pattern between stars. padIdxStart and patIdxEnd point
        // always to a '*'.
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int i = patIdxStart + 1; i <= patIdxEnd; i++) {
                if (patArr[i] == '*') {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // Two stars next to each other, skip the first one.
                patIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - patIdxStart - 1);
            int strLength = (strIdxEnd - strIdxStart + 1);
            int foundIdx = -1;
            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    ch = patArr[patIdxStart + j + 1];
                    if (ch != '?') {
                        if (ch != strArr[strIdxStart + i + j]) {
                            continue strLoop;
                        }
                    }
                }

                foundIdx = strIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            patIdxStart = patIdxTmp;
            strIdxStart = foundIdx + patLength;
        }

        // All characters in the string are used. Check if only '*'s are left
        // in the pattern. If so, we succeeded. Otherwise failure.
        for (int i = patIdxStart; i <= patIdxEnd; i++) {
            if (patArr[i] != '*') {
                return false;
            }
        }

        return true;
    }
}
