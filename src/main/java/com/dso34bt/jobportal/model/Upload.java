package com.dso34bt.jobportal.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class Upload implements Serializable {
    private MultipartFile file;
    private String title;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
