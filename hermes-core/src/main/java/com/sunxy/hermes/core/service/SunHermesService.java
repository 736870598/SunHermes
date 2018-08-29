package com.sunxy.hermes.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sunxy.hermes.core.SunHermes;
import com.sunxy.hermes.core.response.InstanceResponseMake;
import com.sunxy.hermes.core.response.ResponseMake;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class SunHermesService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private SunService.Stub mBinder = new SunService.Stub() {
        @Override
        public Response send(Request request)  {
            ResponseMake responseMake = null;
            switch (request.getType()){
                case SunHermes.TYPE_GET:
                    //获取单例
                    responseMake = new InstanceResponseMake();
                    break;
                case SunHermes.TYPE_NEW:
//                    responseMake = new ObjectResponseMake();
                    break;
            }
            if (responseMake != null){
                return responseMake.makeResponse(request);
            }
            return null;
        }
    };


    public static class SunHermesService0 extends SunHermesService{}
    public static class SunHermesService1 extends SunHermesService{}
    public static class SunHermesService2 extends SunHermesService{}
    public static class SunHermesService3 extends SunHermesService{}
    public static class SunHermesService4 extends SunHermesService{}
    public static class SunHermesService5 extends SunHermesService{}
    public static class SunHermesService6 extends SunHermesService{}
    public static class SunHermesService7 extends SunHermesService{}
    public static class SunHermesService8 extends SunHermesService{}
    public static class SunHermesService9 extends SunHermesService{}


}
