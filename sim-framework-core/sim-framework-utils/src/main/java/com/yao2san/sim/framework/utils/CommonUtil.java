package com.yao2san.sim.framework.utils;


import org.apache.commons.lang.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author wxg
 * <p>
 * Common utils
 */
public class CommonUtil {
    /**
     * 生成指定位数的随机字符串(最大32位)
     *
     * @param length 长度
     */
    public static String getRandomString(int length) {
        if (length <= 0 || length >= 32) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        }
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }

    /**
     * 生成指定位数的随机数字
     *
     * @param len 长度
     */
    public static int getRandomInt(int len) {
        int digit = (int) Math.pow(10, len - 1);
        int rs = new Random().nextInt(digit * 10);
        if (rs < digit) {
            rs += digit;
        }
        return rs;
    }

    /**
     * Stream distinct
     *
     * @param keyExtractor key
     * @param <T>          Stream object type
     * @return A Predicate
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Distinct map
     *
     * @param list Source data list
     * @param key  Distinct key
     * @return A new list
     */
    public static List<Map<Object, Object>> distinctMapByKey(List<Map<Object, Object>> list, String key) {
        List<Map<Object, Object>> copyList = new ArrayList<>();
        Collections.copy(copyList, list);
        copyList = list.stream().filter(distinctByKey(m -> m.get(key))).collect(Collectors.toList());
        return copyList;
    }

    /**
     * Get file suffix (no .)
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        if (StringUtils.isNotEmpty(fileName)) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * Memory pagination
     *
     * @param list     data list
     * @param pageNum  page index
     * @param pageSize page size
     * @param <T>      data type
     * @return pagination list
     */
    public static <T> List<T> memoryPagination(List<T> list, Integer pageNum, Integer pageSize) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }
        if (pageNum <= 0) {
            return null;
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must greater than 0");
        }

        Integer count = list.size();
        Integer pageCount;

        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }

        int fromIndex;
        int toIndex;

        if (pageNum > pageCount) {
            pageNum = pageCount;
        }
        if (!pageNum.equals(pageCount)) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }

        return list.subList(fromIndex, toIndex);
    }

    public static String md5(String string) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder base64Encoder = Base64.getEncoder();
            return base64Encoder.encodeToString(md5.digest(string.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
