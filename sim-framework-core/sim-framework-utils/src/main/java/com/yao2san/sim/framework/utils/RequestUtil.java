package com.yao2san.sim.framework.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 测试用，实际不需要，使用feign调用
 */
@Component
public class RequestUtil {
    public static final RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public static <T> T postForObject(String url, Object object, Class<T> clazz) {
        return postForObject(url, object, MediaType.APPLICATION_JSON_UTF8, clazz);
    }

    public static <T> T postForObject(String url, Object object, MediaType mediaType, Class<T> clazz) {
        return postForEntity(url, object, mediaType, clazz).getBody();
    }

    public static <T> ResponseEntity<T> postForEntity(String url, Object object, MediaType mediaType, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        String requestJson = null;
        try {
            requestJson = objectMapper.writeValueAsString(object);

        } catch (Exception e) {

            e.printStackTrace();

        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        HttpEntity<Object> httpEntity = new HttpEntity<>(requestJson, headers);

        return restTemplate.postForEntity(url, httpEntity, clazz);
    }

    public static <T> T getForObject(String url, Class<T> clazz) {
        return getForEntity(url, null, clazz).getBody();
    }

    public static <T> T getForObject(String url, Map<String, Object> urlParams, Class<T> clazz) {
        return getForEntity(url, urlParams, clazz).getBody();
    }

    public static <T> ResponseEntity<T> getForEntity(String url, Map<String, Object> urlParams, Class<T> clazz) {
        url = builderUrl(url, urlParams);
        return restTemplate.getForEntity(url, clazz);
    }

    private static String builderUrl(String url, Map<String, Object> params) {
        if (params != null) {
            url = url.endsWith("?")?url:url+"?";
            StringBuilder urlBuilder = new StringBuilder(url);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                urlBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url = urlBuilder.toString();
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

}
