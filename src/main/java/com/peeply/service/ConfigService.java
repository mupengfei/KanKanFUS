package com.peeply.service;

/**
 * Created by peeply on 14-5-18.
 */
public class ConfigService {

    private String fileRepository;
    private String crossServer;
    private String crossOrigins;
    private boolean deleteFinished;
    private boolean crossed;


    public boolean isCrossed() {
        return crossed;
    }

    public void setCrossed(boolean crossed) {
        this.crossed = crossed;
    }


    public boolean isDeleteFinished() {
        return deleteFinished;
    }

    public void setDeleteFinished(boolean deleteFinished) {
        this.deleteFinished = deleteFinished;
    }


    public String getFileRepository() {
        return fileRepository;
    }

    public void setFileRepository(String fileRepository) {
        this.fileRepository = fileRepository;
    }

    public String getCrossServer() {
        return crossServer;
    }

    public void setCrossServer(String crossServer) {
        this.crossServer = crossServer;
    }

    public String getCrossOrigins() {
        return crossOrigins;
    }

    public void setCrossOrigins(String crossOrigins) {
        this.crossOrigins = crossOrigins;
    }


}
