package com.yao2san.simapiclient.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by wxg on 2019/2/1 14:04
 */
public class CacheUtil {
    private static Map<String, Object> cacheMap = new ConcurrentHashMap<>();
    private static Map<String,Long> timeMap = new ConcurrentHashMap<>();
    private static Map<String,Integer> modCountMap = new ConcurrentHashMap<>();
    private static int modCount = 0;
    public static Object put(String key, Object value, int seconds) {
        timeMap.put(key,System.nanoTime()+ TimeUnit.SECONDS.toNanos(seconds));
        modCount++;
        modCountMap.put(key,modCountMap.getOrDefault(key,0)+1);
        return cacheMap.put(key, value);
    }
    public static Object put(String key, Object value) {
        timeMap.put(key,System.nanoTime()+ TimeUnit.SECONDS.toNanos(Integer.MAX_VALUE));
        modCount++;
        modCountMap.put(key,modCountMap.getOrDefault(key,0)+1);
        return cacheMap.put(key, value);
    }
    public static <T> T get(String key) {
        T obj = (T)cacheMap.get(key);
        if(isTimeout(key)){
            remove(key);
            return null;
        }
        modCount++;
        modCountMap.put(key,modCountMap.getOrDefault(key,0)+1);
        if (modCount>=100000){
            clean(true);
        }
        return obj;
    }
    public static Integer getModCount(String key) {
        return modCountMap.get(key);
    }
    public static Integer resetModCount(String key) {
        return modCountMap.put(key,0);
    }
    /*@Deprecated
    public static <T> Map<String, T> like(String like) {
        Map<String, T> result = new HashMap<>();
        for (String k : cacheMap.keySet()) {
            String[] split = like.split("%");
            String s0 = split[0];
            String s1 = split[1];
            String s2 = split[2];
            if(k.startsWith(s0)&&k.endsWith(s2)&&("".equals(s1)||k.contains(s1))) {
                if (isTimeout(k)) {
                    cacheMap.remove(k);
                    timeMap.remove(k);
                } else {
                    result.put(k,(T)cacheMap.get(k));
                }
            }
        }
        return result;
    }*/
    public static boolean isTimeout(String key) {
        long nowTime = System.nanoTime();
        long maxTime = timeMap.get(key)==null?Long.MAX_VALUE:timeMap.get(key);
        return nowTime > maxTime;
    }

    public static Object remove(String key){
        timeMap.remove(key);
        modCount++;
        return cacheMap.remove(key);
    }
    public static void clean(boolean keep) {
        if (keep) {
            for (String key : cacheMap.keySet()) {
                if (isTimeout(key)) {
                    cacheMap.remove(key);
                    timeMap.remove(key);
                    modCountMap.remove(key);
                }
            }
        } else {
            cacheMap.clear();
            timeMap.clear();
        }
        modCount=0;
    }
    public static Integer increment(String key){
        Integer old = get(key);
        if(old==null){
            put(key,1);
        }else {
            put(key,old+1);
        }
        return old;
    }
    public static void main(String[] args) throws InterruptedException {
        CacheUtil.put("aaa",123,10);
        System.out.println((Object) CacheUtil.get("aaa"));
        for(int i=20;i>0;i--){
            System.out.println(isTimeout("aaa"));
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println((Object)CacheUtil.get("aaa"));
    }
}
