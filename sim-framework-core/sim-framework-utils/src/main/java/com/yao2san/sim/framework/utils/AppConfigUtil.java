package com.yao2san.sim.framework.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Get application config from *.yml or *.properties
 *
 * @author wxg
 */
@Component
public class AppConfigUtil {
    private static Environment env;

    @Autowired
    private void setEnv(Environment env) {
        AppConfigUtil.env = env;
    }

    public static String getValue(String key) {
        return env.getProperty(key);
    }

    public static String getValue(String key, String defaultValue) {
        return env.getProperty(key) == null ? defaultValue : env.getProperty(key);
    }
}
