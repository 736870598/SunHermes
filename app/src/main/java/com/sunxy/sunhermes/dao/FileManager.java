package com.sunxy.sunhermes.dao;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/29 0029.
 */
public class FileManager implements IFileManager{
    private static final FileManager ourInstance = new FileManager();

    public static FileManager getInstance() {
        return ourInstance;
    }

    private FileManager() {
    }

    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
