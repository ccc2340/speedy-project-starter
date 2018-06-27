package org.speedy.common.service;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speedy.common.exception.SpeedyCommonException;
import org.speedy.common.util.AssertUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @Description 七牛服务工具
 * @Author chenguangxue
 * @CreateDate 2018/06/26 09:35
 */
@Component
public class QiniuService implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(QiniuService.class);

    @Value("${qiniu.access.key}")
    private String accessKey;

    @Value("${qiniu.secret.key}")
    private String secretKey;

    @Value("${qiniu.bucket.name}")
    private String bucket;

    @Value("${qiniu.head.picture}")
    private String url;

    private Auth auth;

    /* 上传图片 */
    public String uploadImage(File image) {
        if (image == null) {
            throw new SpeedyCommonException("上传的图片为null");
        }
        logger.info("上传图片路径为：{}", image.getAbsolutePath());

        UploadManager uploadManager = createUploadManager();
        String uploadToken = auth.uploadToken(bucket);

        try {
            Response response = uploadManager.put(image, createKey(), uploadToken);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return url + putRet.key;
        } catch (QiniuException e) {
            throw new SpeedyCommonException(e, "七牛上传图片发生异常");
        }
    }

    private String createKey() {
        return LocalDateTime.now().toString() + Instant.now().toString();
    }

    private UploadManager createUploadManager() {
        Zone zone = Zone.autoZone();
        Configuration configuration = new Configuration(zone);
        return new UploadManager(configuration);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AssertUtils.notNull(accessKey, secretKey);
        auth = Auth.create(accessKey, secretKey);
    }
}
