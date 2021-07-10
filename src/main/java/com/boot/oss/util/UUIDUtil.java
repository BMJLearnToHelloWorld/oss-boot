package com.boot.oss.util;

import java.util.UUID;

/**
 * @author bmj
 */
public class UUIDUtil {
    /**
     * 获得4个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get4Uuid() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[1];
    }

    /**
     * 获得8个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get8Uuid() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0];
    }

    /**
     * 获得12个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get12Uuid() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1];
    }

    /**
     * 获得16个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get16Uuid() {

        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2];
    }

    /**
     * 获得20个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get20Uuid() {

        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3];
    }

    /**
     * 获得24个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get24Uuid() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[4];
    }

    /**
     * 获得32个长度的十六进制的UUID
     *
     * @return UUID
     */
    public static String get32Uuid() {
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        return idd[0] + idd[1] + idd[2] + idd[3] + idd[4];
    }

    public static String convertUuidToPath(String fileUuid) throws Exception {
        StringBuffer destPath = new StringBuffer("");
        for (int i = 0; i < fileUuid.length(); i += 2) {
            String path = fileUuid.substring(i, i + 2);
            destPath.append(path + "/");
        }
        return destPath.toString();
    }

}
