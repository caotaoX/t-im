package com.ct.myim.im.controller;

import cn.hutool.core.lang.Dict;
import com.ct.myim.common.constant.Constants;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.im.service.FileService;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("add")
    public AjaxResult addFile(@RequestParam("file") MultipartFile file){
        return fileService.addFile(file);
    }

    @GetMapping("getFile")
    public void getFile(HttpServletResponse response,String id){
        if(Constants.AVATAR_ID.equals(id) || Constants.GROUP_AVATAR_ID.equals(id) || Constants.FRIEND_AVATAR_ID.equals(id) || Constants.SYSTEM_ID.equals(id) ){
            fileService.getDefaultAvatar(response,id);
        }else{
            fileService.getFile(response,id);
        }
    }

}
