package com.example.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.StringUtils;

import java.util.Map;


@Slf4j
public class Misc {

    public static void stackTrace(Throwable e) {

        String separator = System.getProperty("line.separator");
        StringBuilder stackTrace = new StringBuilder();

        stackTrace.append(e.toString());

        for (StackTraceElement element : e.getStackTrace()) {
            stackTrace.append(separator).append("  -- ").append(element.toString());
        }

        log.error(stackTrace.toString());
    }

    public static boolean isEmptyMap(Map<byte[], byte[]> someMap) {
        return MapUtils.isEmpty(someMap);
    }

    public static boolean isEmptyString(String s) {
        return StringUtils.isEmpty(s);
    }

    public static boolean isNotEmptyString(String s) {
        return !StringUtils.isEmpty(s);
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }
}
