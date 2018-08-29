package com.sunxy.sunhermes.dao;

import com.sunxy.hermes.core.annotion.ClassId;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/29 0029.
 */
@ClassId("com.sunxy.sunhermes.dao.FileManager")
public interface IFileManager {

    public String getPath();

    public void setPath(String path);
}
