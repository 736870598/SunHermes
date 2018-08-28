package com.sunxy.hermes.core.responce;

import com.sunxy.hermes.core.request.RequestBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class ObjectResponceMake extends ResponceMake{

    private Method mMethod;
    private Object mObject;

    @Override
    protected Object invokeMethod() {
        try {
            return mMethod.invoke(mObject, mParameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void setMethod(RequestBean requestBean) {
        mObject = OBJECT_CENTER.getObject(resultClass.getName());
        Method method = typeCenter.getMethod(mObject.getClass(), requestBean);
        mMethod = method;

    }
}
