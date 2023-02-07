package com.usthe.bootshiro.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;

/**
 * 高频方法工具类
 * @author tomsun28
 * @date 14:08 2018/3/12
 */
public class CommonUtil {


    /**
     * description 获取指定位数的随机数
     *
     * @param length 1
     * @return java.lang.String
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static ArrayList<String> getLocalIpAddr()
    {
        ArrayList<String> ipList = new ArrayList<String>();
        ipList.add("127.0.0.1");
        ipList.add("localhost");
        InetAddress[] addrList;
        try
        {
            Enumeration interfaces= NetworkInterface.getNetworkInterfaces();
            while(interfaces.hasMoreElements())
            {
                NetworkInterface ni=(NetworkInterface)interfaces.nextElement();
                Enumeration ipAddrEnum = ni.getInetAddresses();
                while(ipAddrEnum.hasMoreElements())
                {
                    InetAddress addr = (InetAddress)ipAddrEnum.nextElement();
                    if (addr.isLoopbackAddress() == true)
                    {
                        continue;
                    }

                    String ip = addr.getHostAddress();
                    if (ip.indexOf(":") != -1)
                    {
                        //skip the IPv6 addr
                        continue;
                    }

                    ipList.add(ip);
                }
            }

            Collections.sort(ipList);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to get local ip list");
        }

        return ipList;
    }
}
