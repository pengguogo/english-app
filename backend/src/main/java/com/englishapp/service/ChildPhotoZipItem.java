package com.englishapp.service;

/**
 * 照片打包下载条目:控制器与 Service 之间传递的内部契约。
 * <p>预检阶段先解析出条目名,流式写入阶段仅按文件名读盘,
 * 保证非法 id 在响应流开始前抛出 404,而不是输出损坏的 zip 流。</p>
 *
 * @param entryName zip 内的条目名(已按文件名规则清洗)
 * @param fileName 磁盘上的存储文件名
 * @author TRAE Agent
 * @since 2026-09-30
 */
public record ChildPhotoZipItem(String entryName, String fileName) { }
