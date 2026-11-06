package com.englishapp.service.ai.safety;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

/** 读取 classpath 下的安全词表 / 话术 yml；格式不对直接启动失败，避免“空词表全放行”。 */
final class YamlResource {

    private YamlResource() {
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> load(String resource) {
        try (InputStream in = new ClassPathResource(resource).getInputStream()) {
            Object parsed = new Yaml().load(in);
            if (!(parsed instanceof Map<?, ?> map)) {
                throw new IllegalStateException(resource + " 格式错误：根节点必须是对象");
            }
            return (Map<String, Object>) map;
        } catch (IOException e) {
            throw new IllegalStateException("无法读取 " + resource, e);
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> map(Map<String, Object> parent, String key) {
        Object v = parent.get(key);
        if (!(v instanceof Map<?, ?>)) {
            throw new IllegalStateException("词表缺少对象节点：" + key);
        }
        return (Map<String, Object>) v;
    }

    /** 读取字符串列表；缺省返回空表 */
    static List<String> list(Map<String, Object> parent, String key) {
        Object v = parent.get(key);
        if (v == null) {
            return List.of();
        }
        if (!(v instanceof List<?> raw)) {
            throw new IllegalStateException("词表节点必须是列表：" + key);
        }
        return raw.stream().map(String::valueOf).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }
}
