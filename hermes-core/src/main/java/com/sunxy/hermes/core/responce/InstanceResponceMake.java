package com.sunxy.hermes.core.responce;

import android.util.Log;

import com.sunxy.hermes.core.request.RequestBean;
import com.sunxy.hermes.core.request.RequestParameter;
import com.sunxy.hermes.core.utils.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class InstanceResponceMake extends ResponceMake {

    private Method mMethod;

    @Override
    protected Object invokeMethod() {
        Log.e("sunxy11", "invokeMethod --");
        try {
            Object object = mMethod.invoke(null, mParameters);
            OBJECT_CENTER.putObject(object.getClass().getName(), object);
            return object;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void setMethod(RequestBean requestBean) {
        //解析参数 去找 getInstance()      ----UserManager
        RequestParameter[] requestParameters = requestBean.getRequestParameters();


        /**
         * {
         * "parameterClassName":"java.lang.String"
         * parameterValue:"lisi"
         * }
         */
        Class<?>[] parameterTypes = null;
        if (requestParameters != null && requestParameters.length > 0) {
            parameterTypes = new Class<?>[requestParameters.length];
            for (int i = 0; i < requestParameters.length; ++i) {
                parameterTypes[i] = typeCenter.getClassType(requestParameters[i].getParameterClassName());
            }
        }
        String methodName = requestBean.getMethodName(); //可能出现重载
        Method method = TypeUtils.getMethodForGettingInstance(resultClass, methodName, parameterTypes);
        mMethod = method;
    }
}
