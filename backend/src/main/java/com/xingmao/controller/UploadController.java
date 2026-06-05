package com.xingmao.controller;

import com.xingmao.utils.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String UPLOAD_PATH = "./uploads/";

    @PostMapping("/image")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 创建上传目录
        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

        try {
            file.transferTo(new File(UPLOAD_PATH + newFilename));
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/" + newFilename);
            result.put("filename", newFilename);
            return Result.success(result);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}