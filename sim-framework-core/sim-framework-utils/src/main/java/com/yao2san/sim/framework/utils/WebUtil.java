package com.yao2san.sim.framework.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class WebUtil {
    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    public static boolean mathUrl(String urlPattern, String url) {
        return PATH_MATCHER.match(urlPattern, url);
    }

    public static boolean mathUrl(String[] urlPatterns, String url) {
        if (urlPatterns == null) {
            throw new NullPointerException("urlPatterns can't be null");
        }
        for (String urlPattern : urlPatterns) {
            if (PATH_MATCHER.match(urlPattern, url)) {
                return true;
            }
        }
        return false;
    }

    public static boolean mathUrl(List<String> urlPatterns, String url) {
        if (urlPatterns == null) {
            throw new NullPointerException("urlPatterns can't be null");
        }
        for (String urlPattern : urlPatterns) {
            if (PATH_MATCHER.match(urlPattern, url)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 下载文件
     *
     * @param file 文件
     */
    public static void download(File file) {
        download(file, false);
    }

    /**
     * 下载文件
     *
     * @param file     文件
     * @param isDelete 下载完成后是否删除原文件
     */
    public static void download(File file, boolean isDelete) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();

        if (response == null) {
            return;
        }
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(FileUtil.getName(file), "utf-8"));
            int len;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            if (file.isFile() && isDelete) {
                FileUtils.deleteQuietly(file);
            }
        } catch (IOException e) {
            log.error("", e);

            response.addHeader("error-msg", URLEncoder.encode("系统异常:" + e.getMessage()));
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }
}
