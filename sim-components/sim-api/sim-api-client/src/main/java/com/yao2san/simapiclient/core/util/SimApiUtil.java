package com.yao2san.simapiclient.core.util;

import org.springframework.web.util.UriComponentsBuilder;
import sun.net.util.IPAddressUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;

public class SimApiUtil {
    /*public static String getLocalHost(){
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }*/

    public static String getLocalHost(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":")==-1){
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String buildUrl(String url, Map<String, Object> param) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        param.entrySet().forEach(o -> builder.queryParam(o.getKey(), o.getValue()));
        return builder.build().encode().toString();
    }
}
