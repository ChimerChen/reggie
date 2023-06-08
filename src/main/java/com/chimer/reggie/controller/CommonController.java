package com.chimer.reggie.controller;


import com.chimer.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid重新生成文件名，防止因文件名重复造成文件覆盖
        //生成随机三十位的文件名
        String fileName = UUID.randomUUID().toString()+suffix;


        //创建一个目录对象
        File dir = new File(basePath);
        //判断目录是否存在
        if(!dir.exists())
            dir.mkdirs();
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    @GetMapping("download")
    public void download(String name, HttpServletResponse response) {

        try {
            //输入流，读文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //输出流，写文件内容
            ServletOutputStream outputStream = response.getOutputStream();

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len=fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


