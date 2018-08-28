package com.sunxy.hermes.core;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunxy.hermes.core.responce.ResponceBean;
import com.sunxy.hermes.core.service.HermesService;
import com.sunxy.hermes.core.service.Responce;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class SunHermesInvocationHandler implements InvocationHandler {

    private Class clazz;
    private static Gson gson = new Gson();
    private Class hermesService;

    public SunHermesInvocationHandler(Class<? extends HermesService> service, Class clazz){
        this.clazz = clazz;
        this.hermesService = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Responce responce=SunHermes.getDefault().sendObjectRequest(hermesService, clazz, method, args);
        if (!TextUtils.isEmpty(responce.getData())){
            ResponceBean responceBean = gson.fromJson(responce.getData(), ResponceBean.class);
            if (responceBean.getData() != null){
                String data = gson.toJson(responceBean.getData());
                Class<?> returnType = method.getReturnType();
                return gson.fromJson(data, returnType);
            }
        }
        return null;
    }
}
