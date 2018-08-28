package com.sunxy.hermes.core.responce;

import com.google.gson.Gson;
import com.sunxy.hermes.core.ObjectCenter;
import com.sunxy.hermes.core.TypeCenter;
import com.sunxy.hermes.core.request.RequestBean;
import com.sunxy.hermes.core.request.RequestParameter;
import com.sunxy.hermes.core.service.Request;
import com.sunxy.hermes.core.service.Responce;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public abstract class ResponceMake {

    Class<?> resultClass;

    Object[] mParameters;

    Gson gson = new Gson();

    TypeCenter typeCenter = TypeCenter.getInstance();

    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();

    protected abstract Object invokeMethod();

    protected abstract void setMethod(RequestBean requestBean);

    public Responce makeRespnce(Request request){
        RequestBean requestBean = gson.fromJson(request.getData(), RequestBean.class);
        resultClass = typeCenter.getClassType(requestBean.getResultClassName());
        RequestParameter[] requestParameters = requestBean.getRequestParameters();
        if (requestParameters != null && requestParameters.length > 0){
            mParameters = new Object[requestParameters.length];
            for (int i = 0; i < requestParameters.length; i++) {
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                mParameters[i] = gson.fromJson(requestParameter.getParameterValue(), clazz);
            }
        }else{
            mParameters = new Object[0];
        }

        setMethod(requestBean);

        Object resultObj = invokeMethod();
        ResponceBean responceBean = new ResponceBean(resultObj);
        return new Responce(gson.toJson(responceBean));
    }



}
