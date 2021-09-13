package com.ct.myim.im.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "im_socket_file" )
public class SocketFile {

    private String id;

    private String fileName;

    private String path;

    private long size;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
