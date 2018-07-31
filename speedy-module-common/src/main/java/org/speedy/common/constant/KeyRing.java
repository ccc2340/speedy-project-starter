package org.speedy.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 安全第一
 * @Author chenguangxue
 * @CreateDate 2018/06/28 10:16
 */
public class KeyRing {

    public static String findKeys(String channel) {
        return KEYS.getOrDefault(channel, "");
    }

    private static final Map<String, String> KEYS = new HashMap<>();

    // 西部商店
    private static final String WESTAIR_SHOP = "westair_shop";
    private static final String WESTAIR_FLYPLUS = "westair_flyplus";

    static {
        KEYS.put(WESTAIR_SHOP, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXcboYiskSyQx+jLy23" +
                "G72+CS6tc3d800YBMQmssgSACv/LxZ5mwCQ8dNyvZ/7mbZD1d3Tfltewa+O+dId4h1By+7i5Th" +
                "RIEMrh24shbJptOmFsOzSSL7pkwhzrmJ8AQiHXJdh592gw04f7exRSaskZOvjyyNlLERV8JJXN" +
                "CYmdwIDAQAB");
        KEYS.put(WESTAIR_FLYPLUS, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXcboYiskSyQx+jLy23" +
                "G72+CS6tc3d800YBMQmssgSACv/LxZ5mwCQ8dNyvZ/7mbZD1d3Tfltewa+O+dId4h1By+7i5Th" +
                "RIEMrh24shbJptOmFsOzSSL7pkwhzrmJ8AQiHXJdh592gw04f7exRSaskZOvjyyNlLERV8JJXN" +
                "CYmdwIDAQAB");
    }
}
