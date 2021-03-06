package com.sunxy.hermes.core;

import android.content.Context;

import com.google.gson.Gson;
import com.sunxy.hermes.core.annotion.ClassId;
import com.sunxy.hermes.core.request.RequestBean;
import com.sunxy.hermes.core.request.RequestParameter;
import com.sunxy.hermes.core.service.SunHermesService;
import com.sunxy.hermes.core.service.Request;
import com.sunxy.hermes.core.service.Response;
import com.sunxy.hermes.core.utils.ServiceConnectionManager;
import com.sunxy.hermes.core.utils.SunHermesInvocationHandler;
import com.sunxy.hermes.core.utils.TypeCenter;
import com.sunxy.hermes.core.utils.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class SunHermes {

    //得到对象
    public static final int TYPE_NEW = 0;
    //得到单例
    public static final int TYPE_GET = 1;

    private Gson gson;
    private TypeCenter typeCenter;
    private ServiceConnectionManager serviceConnectionManager;

    private static final SunHermes ourInstance = new SunHermes();

    public static SunHermes getDefault() {
        return ourInstance;
    }

    private SunHermes(){
        gson = new Gson();
        serviceConnectionManager = ServiceConnectionManager.getInstance();
        typeCenter = TypeCenter.getInstance();
    }


    //--------------------------A进程-----------------------------------------
    public void register(Class<?> clazz) {
        typeCenter.register(clazz);
    }



    //--------------------------B进行-----------------------------------------
    public void connect(Context context, Class<? extends SunHermesService> service){
        connectApp(context, null, service);
    }

    private void connectApp(Context context, String packageName, Class<? extends SunHermesService> service){
        serviceConnectionManager.bind(context.getApplicationContext(), packageName, service);
    }

    public <T> T getInstance(Class<T> clazz){
        return getProxy(SunHermesService.class, clazz);
    }

    private <T> T getProxy(Class<? extends SunHermesService> service, Class clazz){
        ClassLoader classLoader = service.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class<?>[]{clazz},
                new SunHermesInvocationHandler(service, clazz));
    }


    public <T> Response sendObjectRequest(Class<? extends SunHermesService> hermesServiceClass, Class<T> clazz, Method method, Object[] parameters) {
        RequestBean requestBean = new RequestBean();
        //获取class name
        ClassId classId = clazz.getAnnotation(ClassId.class);
        if (classId == null){
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        }else{
            requestBean.setClassName(classId.value());
            requestBean.setResultClassName(classId.value());
        }

        //设置方法名
        if (method != null){
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        //设置参数信息，将参数都json化
        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0){
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = gson.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }
        if (requestParameters != null){
            requestBean.setRequestParameters(requestParameters);
        }

        //封装为Request对象
        Request request = new Request(gson.toJson(requestBean), TYPE_GET);
        return serviceConnectionManager.request(hermesServiceClass, request);
    }
}
