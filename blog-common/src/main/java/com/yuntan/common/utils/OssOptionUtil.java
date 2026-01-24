package com.yuntan.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.yuntan.common.config.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OssOptionUtil {

    private final OssProperties ossProperties;
    private final OSS ossClient;

    /**
     * 上传 MultipartFile 到 OSS
     *
     * @param file       上传的文件
     * @param folderPath 文件夹路径，如 "avatar/user"，开头结尾无需斜杠
     * @return 文件的公共访问 URL
     */
    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 1. 构建文件夹路径 (处理斜杠)
        String path = "";
        if (StringUtils.hasText(folderPath)) {
            path = folderPath;
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }

        // 2. 构建唯一文件名：folder/uuid.ext
        String ext = getFileExtension(originalFilename);
        String fileName = path + UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);

        return uploadInputStream(file.getInputStream(), fileName, file.getContentType());
    }

    /**
     * 上传字节数组
     */
    public String uploadBytes(byte[] bytes, String fileName, String contentType) {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            return uploadInputStream(inputStream, fileName, contentType);
        } catch (IOException e) {
            log.error("上传字节数组失败", e);
            throw new RuntimeException("上传失败", e);
        }
    }

    /**
     * 通用上传方法（核心）
     */
    private String uploadInputStream(InputStream inputStream, String fileName, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            // 默认设置公共读权限，防止浏览器访问直接下载而不是预览（可选）
             metadata.setObjectAcl(CannedAccessControlList.PublicRead);

            PutObjectRequest putRequest = new PutObjectRequest(
                    ossProperties.getBucketName(), // 使用 standard getter
                    fileName,
                    inputStream,
                    metadata
            );

            ossClient.putObject(putRequest);
            log.info("文件上传成功: {}", fileName);

            return getPublicUrl(fileName);
        } catch (Exception e) {
            log.error("OSS 上传异常，文件名: {}", fileName, e);
            throw new RuntimeException("OSS 上传失败", e);
        }
    }

    /**
     * 获取公共访问 URL (默认)
     * 格式：https://bucket-name.endpoint/filename
     */
    public String getPublicUrl(String fileName) {
        String endpoint = ossProperties.getEndpoint();

        // 处理 Endpoint 中的 http:// 或 https:// 前缀，防止拼接出错
        if (endpoint.startsWith("http://")) {
            endpoint = endpoint.substring(7);
        } else if (endpoint.startsWith("https://")) {
            endpoint = endpoint.substring(8);
        }

        return "https://" + ossProperties.getBucketName() + "." + endpoint + "/" + fileName;
    }

    /**
     * 获取私有链接（带签名，有效期默认为1小时）
     * 如果你的 Bucket 权限设置为私有，请调用此方法获取链接
     */
    public String getPrivateUrl(String fileName) {
        // 设置URL过期时间为1小时
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        return ossClient.generatePresignedUrl(ossProperties.getBucketName(), fileName, expiration).toString();
    }

    /**
     * 下载文件为字节数组
     */
    public byte[] downloadAsBytes(String fileName) {
        try (InputStream inputStream = ossClient.getObject(ossProperties.getBucketName(), fileName).getObjectContent()) {
            // Java 9+ 方法
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("下载文件失败: {}", fileName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean doesObjectExist(String fileName) {
        return ossClient.doesObjectExist(ossProperties.getBucketName(), fileName);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) {
        // 如果是完整URL，尝试提取文件名
        if (fileName.startsWith("http")) {
            fileName = getFileNameFromUrl(fileName);
        }

        if (doesObjectExist(fileName)) {
            ossClient.deleteObject(ossProperties.getBucketName(), fileName);
            log.info("文件已删除: {}", fileName);
        }
    }

    /**
     * 从完整 URL 中提取文件名 (bucket key)
     */
    private String getFileNameFromUrl(String url) {
        try {
            // 假设 URL 格式为 https://bucket.endpoint/folder/file.ext
            // 这里的逻辑比较简单，如果使用 CDN 可能需要调整
            int lastSlash = url.lastIndexOf(ossProperties.getEndpoint());
            if (lastSlash != -1) {
                // + endpoint长度 + 1 (斜杠)
                return url.substring(url.indexOf("/", lastSlash));
            }
            // 兜底：直接取最后一个斜杠后的内容？不，这可能是 folder/file.ext
            // 简单处理：去掉协议头和域名
            URL netUrl = new URL(url);
            String path = netUrl.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            return url;
        }
    }

    // --- 工具方法 ---

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }
}