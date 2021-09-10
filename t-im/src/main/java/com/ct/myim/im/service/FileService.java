package com.ct.myim.im.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.ct.myim.common.constant.Constants;
import com.ct.myim.common.utils.IdUtils;
import com.ct.myim.framework.web.entity.AjaxResult;
import com.ct.myim.im.config.Config;
import com.ct.myim.im.entity.SocketFile;
import com.ct.myim.im.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FileService {

    @Value("${file.upload.abspath.prefix}")
    private String FileUploadPath;

    @Resource
    private MongoTemplate mongoTemplate;

    public AjaxResult addFile(MultipartFile file) {
        SocketFile socketFile = new SocketFile();
        socketFile.setId(IdUtils.fastSimpleUUID());
        socketFile.setFileName(file.getOriginalFilename());
        socketFile.setSize(file.getSize());
        String path = socketFile.getId() + "/" + file.getOriginalFilename();
        try {
            FileUtil.writeFromStream(file.getInputStream(), FileUploadPath + path);
            socketFile.setPath(path);
            mongoTemplate.insert(socketFile);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("文件上传失败！");
        }
        return AjaxResult.success(socketFile.getId());
    }


    public void getDefaultAvatar(HttpServletResponse response, String id) {
        ServletOutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            if (Constants.SYSTEM_ID.equals(id)) {
                response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode("system.png", "utf-8") + "\"");
                response.setContentType("application/octet-stream;charset=UTF-8");
                outputStream = response.getOutputStream();
                inputStream = new FileInputStream(new File(FileUploadPath + "default/system/system.png"));
                byte[] buff = new byte[2048];
                int size = 0;
                while (inputStream != null && (size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
            }
            if (Constants.AVATAR_ID.equals(id)) {
                response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode("user.png", "utf-8") + "\"");
                response.setContentType("application/octet-stream;charset=UTF-8");
                outputStream = response.getOutputStream();
                inputStream = new FileInputStream(new File(FileUploadPath + "default/user/user.png"));
                byte[] buff = new byte[2048];
                int size = 0;
                while (inputStream != null && (size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
            }
            if (Constants.GROUP_AVATAR_ID.equals(id)) {
                response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode("group.png", "utf-8") + "\"");
                response.setContentType("application/octet-stream;charset=UTF-8");
                outputStream = response.getOutputStream();
                inputStream = new FileInputStream(new File(FileUploadPath + "default/group/group.png"));
                byte[] buff = new byte[2048];
                int size = 0;
                while (inputStream != null && (size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
            }
            if (Constants.FRIEND_AVATAR_ID.equals(id)) {
                response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode("friend.png", "utf-8") + "\"");
                response.setContentType("application/octet-stream;charset=UTF-8");
                outputStream = response.getOutputStream();
                inputStream = new FileInputStream(new File(FileUploadPath + "default/friend/friend.png"));
                byte[] buff = new byte[2048];
                int size = 0;
                while (inputStream != null && (size = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0, size);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getFile(HttpServletResponse response, String id) {
        FileInputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            SocketFile file = mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), SocketFile.class);
            inputStream = new FileInputStream(new File(FileUploadPath + file.getPath()));
            response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(file.getFileName(), "utf-8") + "\"");
            response.setContentType("application/octet-stream;charset=UTF-8");
            outputStream = response.getOutputStream();
            byte[] buff = new byte[2048];
            int size = 0;
            while (inputStream != null && (size = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
