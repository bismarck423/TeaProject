package com.tea.trace.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    // 上传接口
    @PostMapping("/upload")
    public String upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) return "ERROR";

        // 1. 获取文件名后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. 生成新文件名，防止重名
        String newFileName = UUID.randomUUID().toString() + suffix;

        // 3. 定义保存路径 (保存到项目的 static/uploads 目录下)
        // 注意：实际开发中通常存到 OSS 或本地磁盘绝对路径，这里为了演示方便存到 classpath
        String path = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        // 4. 保存文件
        File dest = new File(path + newFileName);
        file.transferTo(dest);

        // 5. 同时拷贝一份到 target 目录，保证无需重启即可访问（热加载模拟）
        String targetPath = System.getProperty("user.dir") + "/target/classes/static/uploads/";
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) targetDir.mkdirs();
        org.springframework.util.FileCopyUtils.copy(dest, new File(targetPath + newFileName));

        // 6. 返回可访问的 URL
        return "/uploads/" + newFileName;
    }
}