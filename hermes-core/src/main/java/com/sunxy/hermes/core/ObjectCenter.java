package com.sunxy.hermes.core;

import java.util.concurrent.ConcurrentHashMap;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class ObjectCenter {

    private static volatile ObjectCenter sInstance = null;

    private final ConcurrentHashMap<String, Object> mObjects;

    private ObjectCenter() {
        mObjects = new ConcurrentHashMap<>();
    }

    public static ObjectCenter getInstance() {
        if (sInstance == null) {
            synchronized (ObjectCenter.class) {
                if (sInstance == null) {
                    sInstance = new ObjectCenter();
                }
            }
        }
        return sInstance;
    }

    public Object getObject(String name) {
        return mObjects.get(name);
    }

    public void putObject(String name, Object object) {
        mObjects.put(name, object);
    }

}
