package com.yao2san.sim.framework.utils;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 图片工具
 *
 * @author wxg
 */
@Slf4j
public class ImageUtil {

    /**
     * 图片文件转base64
     *
     * @param file
     * @return
     */
    @SuppressWarnings("all")
    public static String Image2Bse64(File file) {
        long size = file.length();
        byte[] imageByte = new byte[(int) size];
        FileInputStream fs = null;
        BufferedInputStream bis = null;
        try {
            fs = new FileInputStream(file);
            bis = new BufferedInputStream(fs);
            bis.read(imageByte);
        } catch (IOException e) {
            log.error("error", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("error", e);
                }
            }
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    log.error("error", e);

                }
            }
        }
        return (new BASE64Encoder()).encode(imageByte);
    }
}
