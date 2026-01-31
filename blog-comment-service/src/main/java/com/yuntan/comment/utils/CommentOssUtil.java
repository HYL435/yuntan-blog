package com.yuntan.comment.utils;

import com.yuntan.common.utils.OssOptionUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class CommentOssUtil {

    private final OssOptionUtil ossOptionUtil;

    public CommentOssUtil(OssOptionUtil ossOptionUtil) {
        this.ossOptionUtil = ossOptionUtil;
    }

    /**
     * 处理 MultipartFile 上传
     */
    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 构建文件名：UUID + 扩展名
        String ext = getFileExtension(originalFilename);
        String fileName = folderPath + "/" + UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);

        // 使用 Common 模块的 uploadBytes 上传文件
        return ossOptionUtil.uploadBytes(file.getBytes(), fileName, file.getContentType());
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName) {
        ossOptionUtil.deleteFile(fileName);
    }
}
