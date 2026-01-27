package com.yuntan.common.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.yuntan.common.config.OssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class OssOptionUtil {

    private final OssProperties ossProperties;
    private final OSS ossClient;

    /**
     * 上传字节数组到 OSS
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
     * 通用上传方法（核心），上传输入流
     */
    private String uploadInputStream(InputStream inputStream, String fileName, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            // 默认设置公共读权限
            metadata.setObjectAcl(CannedAccessControlList.PublicRead);

            PutObjectRequest putRequest = new PutObjectRequest(
                    ossProperties.getBucketName(),
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
     */
    public String getPrivateUrl(String fileName) {
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        return ossClient.generatePresignedUrl(ossProperties.getBucketName(), fileName, expiration).toString();
    }

    /**
     * 下载文件为字节数组（通用）
     */
    public byte[] downloadAsBytes(String fileName) {
        try (InputStream inputStream = ossClient.getObject(ossProperties.getBucketName(), fileName).getObjectContent()) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("下载文件失败: {}", fileName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 检查文件是否存在（通用）
     */
    public boolean doesObjectExist(String fileName) {
        return ossClient.doesObjectExist(ossProperties.getBucketName(), fileName);
    }

    /**
     * 删除文件（通用）
     */
    public void deleteFile(String fileName) {
        if (fileName.startsWith("http")) {
            fileName = getFileNameFromUrl(fileName);
        }
        if (doesObjectExist(fileName)) {
            ossClient.deleteObject(ossProperties.getBucketName(), fileName);
            log.info("文件已删除: {}", fileName);
        }
    }

    /**
     * 从URL提取文件名（通用）
     */
    /**
     * 从URL提取文件名（通用，替换弃用的URL类，适配Java 17+）
     * 支持的URL格式：https://bucket.endpoint/folder/file.ext 或 http://bucket.endpoint/file.ext
     */
    private String getFileNameFromUrl(String url) {
        // 空值/非URL直接返回
        if (url == null || !url.startsWith("http")) {
            return url;
        }

        try {
            // 1. 先用endpoint匹配（优先逻辑，最准确）
            String endpoint = ossProperties.getEndpoint();
            // 清理endpoint的协议前缀（如https://），避免匹配失败
            String cleanEndpoint = endpoint.replaceAll("^https?://", "");
            int endpointIndex = url.indexOf(cleanEndpoint);
            if (endpointIndex != -1) {
                // 找到endpoint后，截取其后的第一个斜杠到末尾（即文件名部分）
                int fileNameStart = url.indexOf("/", endpointIndex + cleanEndpoint.length());
                if (fileNameStart != -1) {
                    return url.substring(fileNameStart + 1); // 去掉开头的/
                }
            }

            // 2. 备用逻辑：用URI解析路径（替代弃用的URL）
            URI uri = URI.create(url);
            String path = uri.getPath();
            if (path == null || path.isEmpty()) {
                return url;
            }
            // 去掉路径开头的/，返回纯文件名（如folder/file.ext）
            return path.startsWith("/") ? path.substring(1) : path;

        } catch (Exception e) {
            log.warn("从URL提取文件名失败，URL: {}", url, e);
            return url; // 解析失败时返回原URL，保证后续逻辑不报错
        }
    }
}
