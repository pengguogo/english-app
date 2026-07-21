package com.englishapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 单词配图配置属性
 * <p>
 * 支持通过环境变量 WORD_IMAGE_BASE_URL 切换图片访问基址,
 * 便于未来迁移至云对象存储(CDN),无需改动代码和数据库。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "app.word-image")
public class WordImageProperties {

    /** 图片访问基址,开发期为后端服务地址,生产期可改为 CDN 域名 */
    private String baseUrl;

    /** 静态资源路径前缀,对应 Spring Boot 静态资源目录下的子路径 */
    private String pathPrefix;

    /** 图片文件扩展名(含点号,如 .jpg) */
    private String extension;

    /**
     * 获取图片访问基址
     * @return 基址 URL,如 http://localhost:8080
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置图片访问基址
     * @param baseUrl 基址 URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 获取路径前缀
     * @return 路径前缀,如 /images/words
     */
    public String getPathPrefix() {
        return pathPrefix;
    }

    /**
     * 设置路径前缀
     * @param pathPrefix 路径前缀
     */
    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    /**
     * 获取图片扩展名
     * @return 扩展名,如 .jpg
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 设置图片扩展名
     * @param extension 扩展名
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }
}
