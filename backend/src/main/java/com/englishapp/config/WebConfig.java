package com.englishapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Web 配置:静态资源映射(Vue history 模式) + 开发期 CORS
 * @author englishapp
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置 CORS:开发期放行 Vite dev server
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * 让 Vue Router history 模式生效:非 /api 请求转发到 index.html
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    /**
     * 静态资源处理:让 Vue 路由的任意路径都能返回 index.html
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);
                        // API 请求或存在的资源直接返回;否则返回 index.html 给 Vue Router 处理
                        if (resourcePath.startsWith("api/") || requested.isReadable()) {
                            return requested;
                        }
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
