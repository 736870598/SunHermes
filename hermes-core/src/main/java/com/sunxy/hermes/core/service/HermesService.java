package com.sunxy.hermes.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunxy.hermes.core.SunHermes;
import com.sunxy.hermes.core.responce.InstanceResponceMake;
import com.sunxy.hermes.core.responce.ObjectResponceMake;
import com.sunxy.hermes.core.responce.ResponceMake;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/28 0028.
 */
public class HermesService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private SunHermeService.Stub mBinder = new SunHermeService.Stub() {
        @Override
        public Responce send(Request request) throws RemoteException {
            ResponceMake responceMake = null;
            switch (request.getType()){
                case SunHermes.TYPE_GET:
                    //获取单例
                    responceMake = new InstanceResponceMake();
                    break;
                case SunHermes.TYPE_NEW:
                    responceMake = new ObjectResponceMake();
                    break;
            }
            Log.e("sunxy11", "SunHermeService.Stub ");
            return responceMake.makeRespnce(request);
        }
    };


    public static class SunHermesService0 extends HermesService {}
    public static class SunHermesService1 extends HermesService {}
    public static class SunHermesService2 extends HermesService {}
    public static class SunHermesService3 extends HermesService {}
    public static class SunHermesService4 extends HermesService {}
    public static class SunHermesService5 extends HermesService {}
    public static class SunHermesService6 extends HermesService {}
    public static class SunHermesService7 extends HermesService {}
    public static class SunHermesService8 extends HermesService {}
    public static class SunHermesService9 extends HermesService {}
}
