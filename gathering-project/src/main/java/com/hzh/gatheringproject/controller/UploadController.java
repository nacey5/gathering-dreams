package com.hzh.gatheringproject.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.StringUtils;
import com.hzh.gatheringproject.dto.Result;
import com.hzh.gatheringproject.generics.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author DAHUANG
 * @date 2022/7/6
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @PostMapping("/icon")
    public Result uploadImage(@RequestParam("file") MultipartFile image){
        try{
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            // 生成新文件名
            String fileName = createNewFileName(originalFilename);
            // 保存文件
            image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName));
            // 返回结果
            log.debug("文件上传成功，{}", fileName);
            return Result.ok(fileName);
        }catch (IOException e){
            throw new RuntimeException("文件上传失败",e);
        }
    }

    private String createNewFileName(String originFileName){
        //获取后缀
        String suffix= StrUtil.subAfter(originFileName,",",true);
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("/icon/{}/{}", d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format("/icon/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
