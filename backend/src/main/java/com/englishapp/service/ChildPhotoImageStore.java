package com.englishapp.service;

import com.englishapp.domain.ChildPhoto;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 儿童成长照片的磁盘图片存取组件。
 * <p>职责:原片解码重编码(去除元数据)、缩略图生成与磁盘缓存、缩略图降级读取、文件删除。
 * 只负责磁盘与图片处理,不包含数据库业务逻辑,便于独立测试。</p>
 *
 * @author TRAE Agent
 * @since 2026-09-30
 */
@Component
public class ChildPhotoImageStore {
    private static final Logger log = LoggerFactory.getLogger(ChildPhotoImageStore.class);
    private static final int MAX_BYTES = 8 * 1024 * 1024;
    private static final long MAX_PIXELS = 20_000_000L;
    /** 缩略图最长边:参照主流相册(Immich/Google Photos)缩略规格,兼顾清晰度与列表加载速度 */
    private static final int THUMB_MAX = 480;
    private final Path directory;
    private final Path thumbDirectory;

    public ChildPhotoImageStore(@Value("${app.child-photo.dir:child-photos}") String directory) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        this.thumbDirectory = this.directory.resolve("thumbs");
    }

    /**
     * 根据上传内容类型推导存储后缀。
     *
     * @param contentType MIME 类型
     * @return ".jpg" 或 ".png"
     * @throws IllegalArgumentException 不支持的类型
     */
    String extensionOf(String contentType) {
        return switch (contentType == null ? "" : contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> throw new IllegalArgumentException("仅支持 JPG 或 PNG 照片");
        };
    }

    /**
     * 解码上传照片并重编码,去除元数据并限制尺寸。
     *
     * @param image 上传文件
     * @param suffix 目标格式后缀(".jpg"/".png"),决定画布是否带透明通道
     * @return 重编码后的字节
     * @throws IllegalArgumentException 文件为空、超 8MB、解码失败或像素过大
     */
    byte[] reencode(MultipartFile image, String suffix) {
        if (image == null || image.isEmpty() || image.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("请选择不超过 8 MB 的照片");
        BufferedImage decoded;
        try { decoded = ImageIO.read(new ByteArrayInputStream(image.getBytes())); }
        catch (IOException ex) { throw new IllegalArgumentException("照片文件无效"); }
        if (decoded == null)
            throw new IllegalArgumentException("照片文件无效");
        if ((long) decoded.getWidth() * decoded.getHeight() > MAX_PIXELS)
            throw new IllegalArgumentException("照片像素过大");
        // JPG 不支持透明,JPEG 画布用 RGB 避免出现红底
        BufferedImage clean = new BufferedImage(decoded.getWidth(), decoded.getHeight(),
                ".jpg".equals(suffix) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = clean.createGraphics();
        try { graphics.drawImage(decoded, 0, 0, null); }
        finally { graphics.dispose(); }
        return toBytes(clean, suffix.substring(1));
    }

    /**
     * 写入原片文件(文件名不存在时创建)。
     *
     * @param fileName 存储文件名
     * @param bytes 图片字节
     * @throws IllegalStateException 磁盘写入失败
     */
    void write(String fileName, byte[] bytes) {
        try {
            Files.createDirectories(directory);
            Files.write(directory.resolve(fileName), bytes, StandardOpenOption.CREATE_NEW);
        } catch (IOException ex) {
            throw new IllegalStateException("照片保存失败", ex);
        }
    }

    /**
     * 数据库保存失败时回滚已落盘的文件;回滚失败仅告警,不掩盖原始异常。
     *
     * @param fileName 存储文件名
     */
    void rollback(String fileName) {
        try { Files.deleteIfExists(directory.resolve(fileName)); }
        catch (IOException ex) { log.warn("照片回滚删除失败: {}", fileName, ex); }
    }

    /**
     * 读取原片字节。
     *
     * @param fileName 存储文件名
     * @return 图片字节
     * @throws NoSuchElementException 文件不存在(映射 404)
     */
    byte[] read(String fileName) {
        try { return Files.readAllBytes(directory.resolve(fileName)); }
        catch (IOException ex) { throw new NoSuchElementException("照片文件不存在"); }
    }

    /**
     * 读取缩略图:优先命中磁盘缓存,未命中则生成并缓存;生成失败降级返回原图,保证页面可用。
     *
     * @param photo 照片实体(需含文件名)
     * @return 缩略图或原图字节(JPEG)
     */
    byte[] readThumbnail(ChildPhoto photo) {
        Path thumb = thumbDirectory.resolve(thumbNameOf(photo.getFileName()));
        try {
            if (Files.exists(thumb)) return Files.readAllBytes(thumb);
            byte[] generated = generateThumbnail(directory.resolve(photo.getFileName()));
            if (generated != null) {
                Files.createDirectories(thumbDirectory);
                // 并发生成时内容相同,直接覆盖写即可保证幂等
                Files.write(thumb, generated, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                return generated;
            }
        } catch (IOException ex) {
            log.warn("缩略图缓存失败,降级返回原图: {}", photo.getFileName(), ex);
        }
        return read(photo.getFileName());
    }

    /**
     * 删除原片与其缩略图缓存。
     *
     * @param fileName 存储文件名
     * @throws IllegalStateException 磁盘删除失败
     */
    void delete(String fileName) {
        try {
            Files.deleteIfExists(directory.resolve(fileName));
            Files.deleteIfExists(thumbDirectory.resolve(thumbNameOf(fileName)));
        } catch (IOException ex) {
            throw new IllegalStateException("照片文件删除失败", ex);
        }
    }

    /** 生成缩略图:等比缩放到最长边不超过 480,输出 JPEG;失败返回 null 交由调用方降级 */
    private byte[] generateThumbnail(Path source) {
        try {
            BufferedImage decoded = ImageIO.read(source.toFile());
            if (decoded == null) return null;
            double scale = Math.min(1.0, (double) THUMB_MAX / Math.max(decoded.getWidth(), decoded.getHeight()));
            int width = Math.max(1, (int) Math.round(decoded.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(decoded.getHeight() * scale));
            BufferedImage thumb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = thumb.createGraphics();
            try {
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics.drawImage(decoded, 0, 0, width, height, null);
            } finally { graphics.dispose(); }
            return toBytes(thumb, "jpg");
        } catch (IOException ex) {
            log.warn("缩略图生成失败: {}", source, ex);
            return null;
        }
    }

    private byte[] toBytes(BufferedImage image, String format) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            if (!ImageIO.write(image, format, output))
                throw new IllegalArgumentException("照片格式无效");
        } catch (IOException ex) {
            throw new IllegalArgumentException("照片格式无效");
        }
        return output.toByteArray();
    }

    /** 缩略图缓存文件名:去掉原后缀加 "-t.jpg",如 uuid.jpg → uuid-t.jpg */
    private String thumbNameOf(String fileName) {
        String base = fileName.endsWith(".jpg") || fileName.endsWith(".png")
                ? fileName.substring(0, fileName.length() - 4) : fileName;
        return base + "-t.jpg";
    }
}
