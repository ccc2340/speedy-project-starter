package org.speedy.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speedy.common.constant.KeyRing;
import org.speedy.common.exception.SpeedyCommonException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description 用于生成签名和校验签名的工具包
 * @Author chenguangxue
 * @CreateDate 2018/06/28 10:03
 */
public class SignUtils {

    private static Logger logger = LoggerFactory.getLogger(SignUtils.class);

    // 用于签名操作的字符集
    private static final String SIGN_CHARSET = StandardCharsets.UTF_8.name();

    /**
     * 生成签名
     *
     * @param parameters 待签名的参数集
     * @param channel    签名的渠道，用于区分来自的平台，根据这个值获取不同的私钥
     * @return 生成的签名值
     */
    public static String buildSign(Map<String, String> parameters, String channel) {
        logger.info("待生成签名参数为：{}", JSON.toJSONString(parameters));
        logger.info("待生成签名渠道为：{}", channel);
        try {
            return AlipaySignature.rsaSign(parameters, KeyRing.findKeys(channel), SIGN_CHARSET);
        }
        catch (AlipayApiException e) {
            throw new SpeedyCommonException(e, "生成签名时发生异常");
        }
    }

    public static String buildSign(JSONObject jsonObject, String privateKey) throws AlipayApiException {
        Map<String, String> content = new HashMap<>();
        jsonObject.forEach((s, o) -> {
            content.put(s, o.toString());
        });

        return AlipaySignature.rsaSign(content, privateKey, "utf-8");
    }

    public static boolean verifySign(Map<String, String> content, String channel) {
        logger.info("待校验签名参数为：{}", JSON.toJSONString(content));
        logger.info("待校验签名渠道为：{}", channel);
        try {
            return AlipaySignature.rsaCheckV1(content, KeyRing.findKeys(channel), SIGN_CHARSET);
        }
        catch (AlipayApiException e) {
            throw new SpeedyCommonException(e, "校验签名时发生异常");
        }
    }
}
